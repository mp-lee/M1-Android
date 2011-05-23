package com.neko68k.M1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class GameListActivity extends ListActivity{
	GameListAdapter gla;
	int isRunning = 0;
	int max_games;
	
	public void onCreate(Bundle savedInstanceState) {
		
		//gla=null;
		//String fn = new String();
		//Intent intent = getIntent();
		//fn = intent.getStringExtra("com.tutorials.hellotabwidget.FN");		
        super.onCreate(savedInstanceState);        
        final ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(lv, v,pos,id);
            }
        });                
        /*gla = new GameListAdapter(this);
        max_games = NDKBridge.getMaxGames();
        for(int i=0;i<max_games;i++){
        	GameList gl = NDKBridge.getGameTitle(i);
        	gla.addItem(gl);
        }*/
        /*if(NDKBridge.globalGLA.isEmpty()){
			// we should call the ROM audit task
			ROMAuditTask task = new ROMAuditTask(this);
            task.execute();
            if(NDKBridge.globalGLA.isEmpty()){
            	// toast the user about not having any roms then finish
            	finish();
            }
		}*/
        
        //gla = NDKBridge.globalGLA;
        //gla.sort();
        NDKBridge.globalGLA.sort();
        NDKBridge.globalGLA.setContext(this);
        this.setListAdapter(NDKBridge.globalGLA);
        //this.setListAdapter(gla);
        
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
            int selectionRowID = (int)position;
            
            NDKBridge.loadROM(NDKBridge.globalGLA.get(selectionRowID));
            //LoadROMTask loadTask = new LoadROMTask(this);
            //loadTask.execute(new Integer(selectionRowID));
            
            Intent i = new Intent();
            String title = NDKBridge.getGameList(selectionRowID);
        	i.putExtra("com.neko68k.M1.title", title);
        	//startActivity(i);
        	setResult(RESULT_OK, i);                	
        	finish();                       
    }
}
