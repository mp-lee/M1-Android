package com.neko68k.M1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import java.util.Map;

public class FragmentControl extends FragmentActivity implements
		GameListFrag.OnItemSelectedListener, GameListOptionsFrag.OnOptionsChanged, FileBrowser.FBCallback{

	private static boolean filtered = false;
	private static boolean sorted = false;
	private static int sortType = 0;
	private static boolean faves = false;
    boolean mIsBound = false;
    InitM1Task task;
    Map<String, ?> preferences;
    public PlayerService playerService = new PlayerService();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmentcontainer);

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			M1AndroidFragment firstFragment = new M1AndroidFragment();

			// In case this activity was started with special instructions from
			// an
	// Intent, pass the Intent's extras to the fragment as arguments
			// firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, firstFragment).commit();
		}

	}

    // service connection stuff
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            NDKBridge.playerService = ((PlayerService.LocalBinder) service)
                    .getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            NDKBridge.playerService = null;
        }
    };

    void doBindService() {
        NDKBridge.ctx.bindService(new Intent(NDKBridge.ctx, PlayerService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            NDKBridge.ctx.unbindService(mConnection);
            mIsBound = false;
        }
    }
	
	public static boolean isFaves(){
		return faves;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent;
		InitM1Task task;
		FragmentTransaction transaction;
		
		switch (item.getItemId()) {
		case R.id.open:

			NDKBridge.loadError = false; 
			//intent = new Intent(NDKBridge.ctx, GameListActivity.class);
			//startActivityForResult(intent, 1);
			
			GameListFrag glfFragment = new GameListFrag();
			transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragment_container, glfFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
			return true;
		case R.id.options:
			intent = new Intent(NDKBridge.ctx, Prefs.class);
			startActivityForResult(intent, 2);
			return true;			
		case R.id.rescan:
			SQLiteDatabase db = NDKBridge.m1db.getWritableDatabase();
			GameListOpenHelper.wipeTables(db);
			task = new InitM1Task(NDKBridge.ctx);
			task.execute();
			return true;
		case R.id.sortOptions:
			GameListOptionsFrag newFragment = new GameListOptionsFrag();
			// args.putInt(GameListOptionsActivity.ARG_POSITION, position);
			// newFragment.setArguments(args);

			transaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();

		default:

			return super.onOptionsItemSelected(item);
		}
	}
	
	public void onOptionsChanged(Bundle b){
		filtered = b.getBoolean("filtered");
		sorted = b.getBoolean("sorted");
		faves = b.getBoolean("faves");
		sortType = b.getInt("sortType");		
		return;
	}
	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	GameListOpenHelper.setAlbumFave((Integer)view.getTag(), true);
	    } else {
	    	GameListOpenHelper.setAlbumFave((Integer)view.getTag(), false);
	    }
	}
	
	public static boolean isFiltered(){
		return filtered;
	}
	public static boolean isSorted(){
		return sorted;
	}
	public static int getSortType(){
		return sortType;
	}

	public void onGameSelected(Game game) {
		// //Game game = NDKBridge.queryRom(position);

		// LoadROMTask loadTask = new LoadROMTask(this);
		// loadTask.execute(new Integer(selectionRowID));
		// game.index= position;
		
		Intent i = new Intent();
		String title = "";// NDKBridge.getGameList(selectionRowID);
		//i.putExtra("com.neko68k.M1.game", game);
		i.putExtra("com.neko68k.M1.title", title);
		i.putExtra("com.neko68k.M1.position", game.index);
		
		//i.putExtra("com.neko68k.M1.game", game);
		// startActivity(i);
		NDKBridge.game = game;
		setResult(RESULT_OK, i);
		finish();

	}
    public void selected() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NDKBridge.ctx);
        preferences = prefs.getAll();

        NDKBridge.basepath = (String) preferences.get("sysdir");
        NDKBridge.rompath = (String) preferences.get("romdir");
        NDKBridge.iconpath = (String) preferences.get("icondir");
        task = new InitM1Task(NDKBridge.ctx);
        task.execute();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstRun", false);
        //editor.putString("basepath", NDKBridge.basepath);
        editor.commit();
        //Init();

    }
}
