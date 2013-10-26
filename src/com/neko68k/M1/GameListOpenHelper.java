package com.neko68k.M1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GameListOpenHelper {
	public static final String KEY_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_YEAR = "year";
	public static final String KEY_ROMNAME = "romname";
	public static final String KEY_MFG = "mfg";
	public static final String KEY_SYS = "sys";
	public static final String KEY_CPU = "cpu";
	public static final String KEY_SOUND1 = "sound1";
	public static final String KEY_SOUND2 = "sound2";
	public static final String KEY_SOUND3 = "sound3";
	public static final String KEY_SOUND4 = "sound4";
	public static final String KEY_SOUND5 = "sound5";
	public static final String KEY_LISTAVAIL = "sound5";

	private static final int DATABASE_VERSION = 1;
    private static final String GAMELIST_TABLE_NAME = "gamelist";
    private static final String GAMELIST_TABLE_CREATE =
                "CREATE TABLE " + GAMELIST_TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_TITLE + " TEXT, " +
                KEY_YEAR + " TEXT, " +
                KEY_ROMNAME + " TEXT, " +
                KEY_MFG + " TEXT, " +
                KEY_SYS + " TEXT, " +
                KEY_CPU + " TEXT, " +
                KEY_SOUND1 + " TEXT, " +
                KEY_SOUND2 + " TEXT, " +
                KEY_SOUND3 + " TEXT, " +
                KEY_SOUND4 + " TEXT, " +
                //"sound5" + " TEXT, " +
                KEY_LISTAVAIL + " INTEGER);";
    
    private static SQLiteDatabase dbloc;

    
    public static void onCreate(SQLiteDatabase db) {
    	dbloc = db;
    	db.execSQL("DROP TABLE IF EXISTS " + GAMELIST_TABLE_NAME);
        db.execSQL(GAMELIST_TABLE_CREATE);
    }

	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + GAMELIST_TABLE_NAME);
 
        // Create tables again
        onCreate(db);
	}
	
	public static Cursor getAllTitles(){
		
		
		return(dbloc.query(GAMELIST_TABLE_NAME, null, null, null, null, null, null));
	}
	
	public static void addGame(Game game) {
	    //SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_ID, game.getIndex()); 
	    values.put(KEY_TITLE, game.getTitle()); 
	    values.put(KEY_YEAR, game.getYear()); 
	    values.put(KEY_ROMNAME, game.getRomname()); 
	    values.put(KEY_MFG, game.getMfg()); 
	    values.put(KEY_SYS, game.getSys()); 
	    values.put(KEY_CPU, game.getTitle()); 
	    values.put(KEY_SOUND1, game.getTitle()); 
	    values.put(KEY_SOUND2, game.getTitle()); 
	    values.put(KEY_SOUND3, game.getTitle()); 
	    values.put(KEY_SOUND4, game.getTitle()); 	   
	    values.put(KEY_LISTAVAIL, game.getListavail()); 
	 
	    // Inserting Row
	    //NDKBridge.m1db.insert(GAMELIST_TABLE_NAME, null, values);
	    //NDKBridge.m1db.close(); // Closing database connection
	}
}