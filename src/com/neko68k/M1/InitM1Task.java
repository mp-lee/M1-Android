package com.neko68k.M1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class InitM1Task extends AsyncTask<Void, Void, Void>{
	ProgressDialog dialog;
	
	private Context context;

    public InitM1Task (Context c)  //pass the context in the constructor
	{
	    context = c;
	}

	
	@Override
	protected void onPreExecute (){
		dialog = ProgressDialog.show(context, "", "Please wait, initializing...", true);	
	}
	
	@Override
	protected Void doInBackground(Void... unused){				
		String outpath = null;
		Game game;
		File xml = new File(NDKBridge.basepath+"/m1/m1.xml");
		if(xml.exists()==false){
			File extdir = Environment.getExternalStorageDirectory();
	    	try {
				outpath = NDKBridge.basepath + "/m1/";
				File m1dir = new File(outpath);
		    	m1dir.mkdirs();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Unzip(outpath, R.raw.m1xml20111011);
			
			try {
				outpath = NDKBridge.basepath + "/m1/lists/";
				File m1dir = new File(outpath);
		    	m1dir.mkdirs();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Unzip(outpath, R.raw.lists);
			
			try {
				outpath = NDKBridge.basepath + "/m1/roms/";
				File m1dir = new File(outpath);
		    	m1dir.mkdirs();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
				
		NDKBridge.initM1();
		int numGames = NDKBridge.getMaxGames();
		int i =0;		
		
		NDKBridge.db = new GameListOpenHelper();
		
		//List<GameList> mItems = new ArrayList<GameList>();
		/*if(NDKBridge.globalGLA.isEmpty()){
			for(i = 0; i<numGames;i++){
				NDKBridge.cur = i;
				String title = NDKBridge.auditROM(i);	
				NDKBridge.addROM(title, i);
			}
		}*/
		
		for(i = 0; i<numGames;i++){
			NDKBridge.cur = i;
			// simple zipname audit, need to add a full audit procedure...
			String title = NDKBridge.auditROM(i);	
			//NDKBridge.addROM(title, i);
			
			game = new Game(i, NDKBridge.getInfoString(NDKBridge.M1_SINF_VISNAME, i), NDKBridge.getInfoString(NDKBridge.M1_SINF_YEAR, i), 
					NDKBridge.getInfoString(NDKBridge.M1_SINF_ROMNAME, i), NDKBridge.getInfoString(NDKBridge.M1_SINF_MAKER, i),
					NDKBridge.getInfoString(NDKBridge.M1_SINF_BNAME, i), "", "", "", "", "",
					0);
			
			String sound = NDKBridge.getInfoString(NDKBridge.M1_SINF_BHARDWARE, i);
			String soundary[] = sound.split(", ");
			switch(soundary.length){
			case 5:
				game.setSound4(soundary[4]);
			case 4:
				game.setSound3(soundary[3]);
			case 3:
				game.setSound2(soundary[2]);
			case 2:
				game.setSound1(soundary[1]);
			case 1:
				game.setCpu(soundary[0]);
			case 0:
				break;
			}			
			
			NDKBridge.db.addGame(game);
		}
		
		return(null);
	}
	
	@Override
	protected void onPostExecute (Void result){
		//NDKBridge.globalGLA = NDKCallbacks.nonglobalgla;		
		dialog.dismiss();	
	}
	
	private void Unzip(String outpath, int res){
    	final int BUFFER = 2048;    	
    	InputStream xmlzip = context.getResources().openRawResource(res);
    	ZipInputStream zis = new ZipInputStream(new BufferedInputStream(xmlzip));
    	BufferedOutputStream dest = null;;
    	ZipEntry entry = null;
    	String finalpath = outpath;
    	    	
    	try {
	    	while((entry = zis.getNextEntry())!=null){				
				if(entry.isDirectory()){
					finalpath = outpath + entry.getName()+"/";
					File newDir = new File(finalpath);
					newDir.mkdirs();
				}
				else{
					int count;
					byte[] data = new byte[BUFFER];
					FileOutputStream out = new FileOutputStream(outpath + entry.getName());
					dest = new BufferedOutputStream(out, BUFFER);
					while((count = zis.read(data, 0, BUFFER)) != -1){
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
	    	}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
