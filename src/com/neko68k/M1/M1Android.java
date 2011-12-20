package com.neko68k.M1;



import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.neko68k.emu.M1Android.R;

public class M1Android extends Activity {	
	ListView trackList;
	Button nextButton;
	Button prevButton;
	Button stopButton;
	Button playButton;	
	TextView trackNum;
	TextView playTime;
	TextView board;
	TextView hardware;
	TextView mfg;
	TextView song;
	TextView title;
	Timer updateTimer;   
	
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	private Handler mHandler = new Handler();
//	public PlayerService playerService = new PlayerService();
	boolean mIsBound = false;
		
	boolean paused = false;
	boolean playing = false;
	boolean inited = false;
	int curSong = 0;
	int playtime = 0;
	TimerTask timerTask;
	int numSongs = 0;
	int maxSongs = 0;
	Map<String, ?> preferences;
	Integer defLen;	// seconds
	int skipTime = 0;
	Boolean listLen;
	Boolean normalize;
	Boolean resetNorm;
	Boolean useList;
	Integer lstLang;
	
	
	InitM1Task task;
	
	//private PlayerService playerService;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // get our widget id's
        trackList = (ListView)findViewById(R.id.listView1);
        nextButton = (Button)findViewById(R.id.next);
        prevButton = (Button)findViewById(R.id.prev);
        stopButton = (Button)findViewById(R.id.stop);
        playButton = (Button)findViewById(R.id.play);
        trackNum = (TextView)findViewById(R.id.trackNum);
        playTime = (TextView)findViewById(R.id.playTime);
        board = (TextView)findViewById(R.id.board);
        hardware = (TextView)findViewById(R.id.hardware);
        mfg = (TextView)findViewById(R.id.mfg);
        song = (TextView)findViewById(R.id.song);
        title = (TextView)findViewById(R.id.title);
        NDKBridge.setTitleView(title);  
        
        NDKBridge.ctx = this;
        if(inited==false){
		        listItems.add("No game loaded");
		        adapter=new ArrayAdapter<String>(this,
					    android.R.layout.simple_list_item_1,
					    listItems);
		        trackList.setAdapter(adapter);
		        trackList.setOnItemClickListener(mMessageClickedHandler);
		        GetPrefs();
		        task = new InitM1Task(this);
		        task.execute();
		        //GetPrefs();
		        Init();
		        //GetPrefs();
        }        
    }
   
    
    private void GetPrefs(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	preferences = prefs.getAll();
    	normalize = (Boolean) preferences.get("normPref");
    	if(normalize==null)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_NORMALIZE,  1);
    	else if(normalize)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_NORMALIZE,  1);
    	else
    		NDKBridge.SetOption(NDKBridge.M1_OPT_NORMALIZE,  0);
    	
    	resetNorm = (Boolean) preferences.get("resetNormPref");
    	if(resetNorm==null)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_RESETNORMALIZE,  1);
    	else if(resetNorm)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_RESETNORMALIZE,  1);
    	else
    		NDKBridge.SetOption(NDKBridge.M1_OPT_RESETNORMALIZE,  0);
    	
    	useList = (Boolean) preferences.get("listPref");
    	if(useList==null)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_USELIST,  1);
    	else if(useList)
    		NDKBridge.SetOption(NDKBridge.M1_OPT_USELIST,  1);
    	else
    		NDKBridge.SetOption(NDKBridge.M1_OPT_USELIST,  0);
    	
    	String tmp = (String) preferences.get("langPref");
    	if(tmp!=null){
    		lstLang = new Integer(tmp);
    		NDKBridge.SetOption(NDKBridge.M1_OPT_LANGUAGE,  lstLang);
    	}
    	
    	tmp = (String) preferences.get("defLenPref");
    	/*if(tmp!=null){
    		NDKBridge.defLen = new Integer(tmp);    		
    	}
    	else*/
    		NDKBridge.defLen = 300;
    	
    	listLen = (Boolean) preferences.get("listLenPref");
    	if(listLen!=null){
    		if(!listLen){
    			NDKBridge.songLen = NDKBridge.defLen;
    		}
    	}
    	else{
    		NDKBridge.songLen = NDKBridge.defLen;
    		listLen = false;
    	}
    		
    	
    }
    
    private void Init(){
    	 // set up the button handlers
        // NEXT
    	
    	
        nextButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	if(playing==true){
	            	if(paused==false){
	            		trackNum.setText("Command: "+(NDKBridge.next()));	
	            		NDKBridge.playerService.setNoteText();
	            		NDKBridge.playtime = 0;
	            		if(listLen)
	            			NDKBridge.getSongLen();
	            	}
            	}
            }
        });
        // PREV
        prevButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	if(playing==true){
	            	if(paused==false){
	            		trackNum.setText("Command: "+(NDKBridge.prevSong()));
	            		NDKBridge.playerService.setNoteText();
	            		NDKBridge.playtime = 0;
	            		if(listLen)
	            			NDKBridge.getSongLen();
	            	}
            	}
            }
        });
        // STOP
        stopButton.setOnClickListener(new View.OnClickListener() 
        {
        	// need to to something with this. it basically kills
        	// the game now and thats kind of unfriendly
            public void onClick(View v) {  
            	playing = false;
            	paused = false;
            	playButton.setText("Play");
            	//ad.PlayStop();
            	NDKBridge.playerService.stop();
            	doUnbindService();
            	//NDKBridge.pause();
            	//NDKBridge.stop();
            	NDKBridge.playtime = 0;
            }
        });
        // PLAY
        playButton.setOnClickListener(new View.OnClickListener() 
        {        	
            public void onClick(View v) {
            	if(playing==true)
            	{
            		if(paused==true)
	                {
	                	playButton.setText("Pause");
	                	NDKBridge.unPause();
	                	//ad.UnPause();
	                	NDKBridge.playerService.unpause();
	                	paused=false;	                	
	                }
            		else if(paused==false)
	                {
	                	NDKBridge.pause();
	                	playButton.setText("Play");
	                	//ad.PlayPause();
	                	NDKBridge.playerService.pause();
	                	paused=true;
	                }
            	}
            }
        });
    }
   
    private OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
        	if(mIsBound){
        		NDKBridge.jumpSong(position);
        		NDKBridge.playerService.setNoteText();
        		NDKBridge.getSongLen();
        	}
        }
    };
    
    private OnItemClickListener mDoNothing= new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
            //NDKBridge.jumpSong(position);
        }
    };

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
   
        
    private Runnable mUpdateTimeTask = new Runnable() {
    	   public void run() {    		   
    		   // update stuff here
    		   runOnUiThread(new Runnable() {
    			   
    		  	   public void run() {
    		  		   if(playing==true){
    		  			   if(paused==false){
    		  				   int seconds=NDKBridge.getCurTime()/60;
    		  				   if(seconds>NDKBridge.songLen){
    		  					   NDKBridge.next();
    		  					   if(listLen)
    		  						 NDKBridge.getSongLen();
    		  				   }
    		  				   int minutes=seconds/60;
    		  				   seconds -= minutes*60;
    		  				   
    		  				   trackNum.setText("Command: "+(NDKBridge.getCurrentCmd()+1));
    		  				   if(seconds<10)
    		  					 playTime.setText("Time: "+minutes+":0"+seconds);
    		  				   else
    		  					   playTime.setText("Time: "+minutes+":"+seconds);
    		  				   song.setText("Song: "+NDKBridge.getSong(NDKBridge.getCurrentCmd()));
    		  				   
    		  			   }
    		  		   }
    		   	   }    		  	   
    		   	});
    		   // retrigger task
    		   if(playing==true){
    			   mHandler.postDelayed(mUpdateTimeTask, 100);
    		   }
    	   }
    };    
    
    
    // service connection stuff
    private ServiceConnection mConnection = new ServiceConnection(){
    	public void onServiceConnected(ComponentName className, IBinder service){
    		NDKBridge.playerService = ((PlayerService.LocalBinder)service).getService();
    	}
    	
    	public void onServiceDisconnected(ComponentName className){
    		NDKBridge.playerService = null;
    	}
    };
    
    void doBindService(){
    	bindService(new Intent(M1Android.this, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
    	mIsBound = true;
    }
    
    void doUnbindService(){
    	if(mIsBound){
    		unbindService(mConnection);
    		mIsBound = false;
    	}
    }
    
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	 			
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode == 1 && resultCode == RESULT_OK){
    		
    		//playerService.//.startService(new Intent(this, PlayerService.class));
        	if(playing==true){
        		
        		//ad.PlayStop();
        		//doUnbindService();
        		playing = false;
        		paused = true;
        		NDKBridge.playerService.stop();
        		doUnbindService();
    			NDKBridge.playtime = 0;
        	}
        	int gameid = data.getIntExtra("com.neko68k.M1.position", 0);
        	NDKBridge.loadROM(NDKBridge.globalGLA.get(gameid));
    		
    		if(NDKBridge.loadError==false){
	    		NDKBridge.playtime = 0;
	    		mHandler.post(mUpdateTimeTask);
	    		board.setText("Board: "+NDKBridge.board);
				mfg.setText("Maker: "+NDKBridge.mfg);
				hardware.setText("Hardware: "+NDKBridge.hdw);
				
	    		playButton.setText("Pause");
	    		numSongs = NDKBridge.getNumSongs(NDKBridge.curGame);
	    		if(numSongs>0){
	    			
	    			listItems.clear();
	    			for(int i = 0; i<numSongs;i++){
	    				String song = NDKBridge.getSong(i);
	    				if(song!=null){
	    					listItems.add((i+1)+". "+song);
	    				}
	    			}     
	    			
	    			
	    			trackList.setOnItemClickListener(mMessageClickedHandler);	    			
	    			adapter.notifyDataSetChanged();
	    			trackList.setSelection(0);
	    			
	    		}
	    		else{
	    			listItems.clear();
	    			listItems.add("No playlist");
	    			trackList.setOnItemClickListener(mDoNothing);
	    			adapter.notifyDataSetChanged();
	    		}
	    		if(!mIsBound){
	    			doBindService();
	    		}
	    		else{
	    			if(listLen)
	    				NDKBridge.getSongLen();
	    			NDKBridge.playerService.play();

	    		}
	    		playing=true;
	    		paused=false;
    		}
    		else{
    			listItems.clear();
    			listItems.add("No game loaded");
    			trackList.setOnItemClickListener(mDoNothing);
    			adapter.notifyDataSetChanged();
    			board.setText("");
    			mfg.setText("");
    			hardware.setText("");
    			song.setText("");
    			playTime.setText("Time:");
    			trackNum.setText("Command:");
    			
    			title.setText("No game loaded");
    			playButton.setText("Play");
    			//Toast.makeText(this, NDKBridge.m1error, Toast.LENGTH_SHORT).show();
    		}
    	} else if(requestCode == 2 && resultCode == RESULT_OK){
    		// options returned
    		// stop everything and set options
    		GetPrefs();
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override 
    protected void onDestroy(){
    	super.onDestroy();
    	if(playing==true){
    		NDKBridge.playerService.stop();
    		doUnbindService();
    	}
    		//ad.PlayQuit();          		
    		NDKBridge.nativeClose();    	
    		this.finish();
    }      
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.open:

        	NDKBridge.loadError=false;
        	intent = new Intent(this, GameListActivity.class);
        	startActivityForResult(intent, 1);        	
            return true;
        case R.id.options:
        	intent = new Intent(this, Prefs.class);
        	startActivityForResult(intent, 2);
            return true;

        default:
               	        	
            return super.onOptionsItemSelected(item);
        }
    }
}
