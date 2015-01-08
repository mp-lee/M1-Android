package com.neko68k.M1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
//import android.R;

// this will handle all the threading and shit
// so we can properly stop it and allow the app
// to shut down or otherwise not keep sucking
// up the battery

public class PlayerService extends Service implements MusicFocusable {
	String text;
	Notification notification = null;
	PendingIntent contentIntent;

    // ***** Remote Control Stuff ***** //
    public static final String ACTION_TOGGLE_PLAYBACK =
            "com.neko68k.M1.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "com.neko68k.M1.action.PLAY";
    public static final String ACTION_PAUSE = "com.neko68k.M1.action.PAUSE";
    public static final String ACTION_STOP = "com.neko68k.M1.action.STOP";
    public static final String ACTION_SKIP = "com.neko68k.M1.action.SKIP";
    public static final String ACTION_REWIND = "com.neko68k.M1.action.REWIND";
    public static final String ACTION_URL = "com.neko68k.M1.action.URL";

    RemoteControlClientCompat mRemoteControlClientCompat;
    ComponentName mMediaButtonReceiverComponent;
    AudioFocusHelper mAudioFocusHelper = null;
    public static final float DUCK_VOLUME = 0.1f;
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    AudioManager mAudioManager;

    enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }
    // ***** End Remote Control Stuff ***** //

	AudioDevice ad = new AudioDevice("deviceThread");

	public class LocalBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
	}

	@Override
	public void onCreate() {
        Context ctx = getApplicationContext();
		// mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mMediaButtonReceiverComponent = new ComponentName(ctx, MusicIntentReceiver.class);


        mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= 8)
            mAudioFocusHelper = new AudioFocusHelper(ctx, this);
        else
            mAudioFocus = AudioFocus.Focused; // no focus feature, so we always "have" audio focus
		play();
	}

    public void onLostAudioFocus(boolean canDuck) {
        //Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" :
        //"no duck"), Toast.LENGTH_SHORT).show();
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
        // start/restart/pause media player with new focus settings
        if (mIsBound && playing)
            configAndStartMediaPlayer();
    }

    public void onGainedAudioFocus() {
        //Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
        mAudioFocus = AudioFocus.Focused;
        // restart media player with new focus settings
        if (playing)
            configAndStartMediaPlayer();

    }

    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause, even if mState
            // is State.Playing. But we stay in the Playing state so that we know we have to resume
            // playback once we get the focus back.
            if (playing == true) {
                if (paused == false) {
                    NDKBridge.pause();
                    playButton.setImageResource(R.drawable.ic_action_play);
                    NDKBridge.playerService.pause();
                    paused = true;
                }
            }
        }
        else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            NDKBridge.playerService.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        else
            NDKBridge.playerService.setVolume(1.0f, 1.0f); // we can be loud
        if (playing == true) {
            if (paused == true) {
                playButton.setImageResource(R.drawable.ic_action_pause);
                NDKBridge.unPause();
                NDKBridge.playerService.unpause();
                paused = false;
                updateRemoteMetadata();
            }
        }
    }

    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if(action != null){
            if (action.equals(ACTION_TOGGLE_PLAYBACK)) processTogglePlaybackRequest();
            else if (action.equals(ACTION_PLAY)) processPlayRequest();
            else if (action.equals(ACTION_PAUSE)) processPauseRequest();
            else if (action.equals(ACTION_SKIP)) processSkipRequest();
                //else if (action.equals(ACTION_STOP)) processStopRequest();
            else if (action.equals(ACTION_REWIND)) processRewindRequest();
        }

        //return START_NOT_STICKY; // Means we started the service, but don't want it to
        // restart in case it's killed.
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	public void setVolume(float l, float r){
		ad.setVolume(l, r);
	}

	public void pause() {
		ad.PlayPause();
	}

	public void unpause() {
		ad.UnPause();
	}

	public void stop() {
		ad.PlayQuit();
		// mNM.cancelAll();
		stopForeground(true);
	}

	public void play() {

		if (notification == null)
			notification = new Notification(R.drawable.icon, "",
					System.currentTimeMillis());
		new Intent(this, M1AndroidFragment.class);

		setNoteText();
		ad.PlayStart();
	}

	public void setNoteText() {

		notification.flags |= Notification.FLAG_NO_CLEAR;
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.custom_notification);
		contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		//contentView.setTextViewText(R.id.title, "M1Android");

		notification.contentView = contentView;
		int cmdNum = NDKBridge
				.getInfoInt(NDKBridge.M1_IINF_TRACKCMD, (NDKBridge.getInfoInt(
						NDKBridge.M1_IINF_CURSONG, 0) << 16 | NDKBridge
						.getInfoInt(NDKBridge.M1_IINF_CURGAME, 0)));
		text = NDKBridge.getInfoStr(NDKBridge.M1_SINF_TRKNAME, cmdNum << 16
				| NDKBridge.getInfoInt(NDKBridge.M1_IINF_CURGAME, 0));
		// text = NDKBridge.getSong(NDKBridge.getCurrentCmd());
		// text=null;
		if (text != null) {
			contentView.setTextViewText(R.id.text2, text);
			contentView
					.setTextViewText(R.id.text, NDKBridge.getInfoStr(
							NDKBridge.M1_SINF_VISNAME,
							NDKBridge.getInfoInt(NDKBridge.M1_IINF_CURGAME, 0)));
		}
		if (text == null) {
			contentView.setTextViewText(R.id.text2, "No track list");
			// contentView.setTextViewText(R.id.text2,
			// NDKBridge.getGameTitle(NDKBridge.curGame).getText());
			contentView.setTextViewText(R.id.text, "FIXME");
		}

		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				M1AndroidFragment.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		Intent notificationIntent = new Intent(this, M1AndroidFragment.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.contentIntent = contentIntent;

		startForeground(1337, notification);
	}

	// @Override
	public void onDestory() {
		ad.PlayQuit();
		stopForeground(true);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

}
