package com.neko68k.M1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

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
	public static final String KEY_ROMAVAIL = "romavail";
	public static final String KEY_SOUNDHW = "soundhw";
	public static final String KEY_FILTERED = "filtered";
	
	


	public static final String KEY_YEAR_HASH = "yearhash";
	public static final String KEY_MFG_HASH = "mfghash";
	public static final String KEY_SYS_HASH = "syshash";
	public static final String KEY_CPU_HASH = "cpuhash";
	public static final String KEY_SOUND1_HASH = "sound1hash";
	public static final String KEY_SOUND2_HASH = "sound2hash";
	public static final String KEY_SOUND3_HASH = "sound3hash";
	public static final String KEY_SOUND4_HASH = "sound4hash";
	public static final String KEY_SOUND5_HASH = "sound5hash";

	
	
	private static final String GAMELIST_TABLE_NAME = "gamelist";
	
	
	
	
	
	

	public static final int DATABASE_VERSION = 2;
	
	private static final String GAMELIST_TABLE_CREATE = "CREATE TABLE "
			+ GAMELIST_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_TITLE + " TEXT, " + KEY_YEAR_HASH + " INTEGER, " + KEY_ROMNAME
			+ " TEXT, " + KEY_MFG_HASH + " INTEGER, " + KEY_SYS_HASH + " INTEGER, " + KEY_CPU_HASH
			+ " INTEGER, " + KEY_SOUND1_HASH + " INTEGER, " + KEY_SOUND2_HASH + " INTEGER, "
			+ KEY_SOUND3_HASH + " INTEGER, " + KEY_SOUND4_HASH + " INTEGER, " +
			 KEY_SOUNDHW + " TEXT, " +
			KEY_ROMAVAIL + " INTEGER);";

	public static final String CPU_TABLE = "cputable";
	private static final String CPU_TABLE_CREATE = "CREATE TABLE " 
			+ CPU_TABLE	+ " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ KEY_CPU_HASH + " INTEGER, "
			+ KEY_CPU + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";

	public static final String SOUND1_TABLE = "sound1";
	private static final String SOUND1_TABLE_CREATE = "CREATE TABLE "
			+ SOUND1_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_SOUND1_HASH + " INTEGER , "
			+ KEY_SOUND1 + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";

	public static final String SOUND2_TABLE = "sound2";
	private static final String SOUND2_TABLE_CREATE = "CREATE TABLE "
			+ SOUND2_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_SOUND2_HASH + " INTEGER , "
			+ KEY_SOUND2 + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";

	public static final String SOUND3_TABLE = "sound3";
	private static final String SOUND3_TABLE_CREATE = "CREATE TABLE "
			+ SOUND3_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_SOUND3_HASH + " INTEGER , "
			+ KEY_SOUND3 + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";

	public static final String SOUND4_TABLE = "sound4";
	private static final String SOUND4_TABLE_CREATE = "CREATE TABLE "
			+ SOUND4_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_SOUND4_HASH + " INTEGER , "
			+ KEY_SOUND4 + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";
	
	public static final String MFG_TABLE = "mfg";
	private static final String MFG_TABLE_CREATE = "CREATE TABLE "
			+ MFG_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_MFG_HASH + " INTEGER , "
			+ KEY_MFG + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";
	
	public static final String BOARD_TABLE = "board";
	private static final String BOARD_TABLE_CREATE = "CREATE TABLE "
			+ BOARD_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_SYS_HASH + " INTEGER , "
			+ KEY_SYS + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";
	
	public static final String YEAR_TABLE = "year";
	private static final String YEAR_TABLE_CREATE = "CREATE TABLE "
			+ YEAR_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_YEAR_HASH + " INTEGER , "
			+ KEY_YEAR + " TEXT, " 
			+ KEY_FILTERED+ " INTEGER DEFAULT 0);";
	
	public static final String dispCols = YEAR_TABLE+"."+KEY_YEAR
			+ ", " + MFG_TABLE+"."+KEY_MFG
			+ ", " + BOARD_TABLE+"."+KEY_SYS 
			+ ", " + GAMELIST_TABLE_NAME+"."+KEY_ID 
			+ ", " + GAMELIST_TABLE_NAME+"."+KEY_TITLE 
			+ ", " + GAMELIST_TABLE_NAME+"."+KEY_SOUNDHW;
	

	
	public static final String dispTbls = YEAR_TABLE+","+MFG_TABLE+","+BOARD_TABLE+","+SOUND4_TABLE+","+
	SOUND3_TABLE+","+SOUND2_TABLE+","+SOUND1_TABLE+","+CPU_TABLE;
	
	
	// generates a single joined sql query
	// these must be put in a String[] and passed to buildUnionQuery(...)
	// to generate the final query
	private static String genQuery(String table, String key, String dispCol){
		/*return "SELECT C._id, C."+KEY_TITLE+", R."+ dispCol + " FROM " 
				+ table + " AS R INNER JOIN " + GAMELIST_TABLE_NAME + " AS C ON " 
				+ "C." + key + "=R._id" + " WHERE R.filtered=0";*/
		
		String query = 
				"SELECT " + dispCols + " FROM " 
				+ dispTbls + " JOIN " + GAMELIST_TABLE_NAME + " ON " 
				+ GAMELIST_TABLE_NAME+"." + key + "="+table+"."+key + " WHERE "+table+".filtered=0";
		
		
		/*query = "SELECT " + dispCols + " FROM " 
				+ dispTbls + " LEFT JOIN " + GAMELIST_TABLE_NAME +" USING ("+ key +")";*/
		
		
		//query = "SELECT DISTINCT "+GAMELIST_TABLE_NAME+" FROM (SELECT filtered from "+table+" WHERE "+GAMELIST_TABLE_NAME+"."+key+"="+table+"."+key+")";
		
		return query;
		
		/*return "SELECT "+GAMELIST_TABLE_NAME+"._id, "+GAMELIST_TABLE_NAME+"."+KEY_TITLE+", "+table+"."+ dispCol +" FROM " 
		+ table + " JOIN " + GAMELIST_TABLE_NAME + " ON " 
		+ GAMELIST_TABLE_NAME+"." + key + "="+table+"."+key + " WHERE "+table+".filtered=0";*/
		
		
		
		
		
		/*return "SELECT " +dispCols+ " FROM " + GAMELIST_TABLE_NAME + ", " 
				+ table + " WHERE " + GAMELIST_TABLE_NAME + "." + key 
				+ " = " + table + "._id AND " + table + "." + "filtered = 1;";*/
		//return "SELECT " +dispCols+ " FROM " + GAMELIST_TABLE_NAME + " NATURAL JOIN " + table;
	}


	public static void onCreate(SQLiteDatabase db) {
		//if(checkTable()==false){
		db.execSQL(GAMELIST_TABLE_CREATE);
		db.execSQL(CPU_TABLE_CREATE);
		db.execSQL(SOUND1_TABLE_CREATE);
		db.execSQL(SOUND2_TABLE_CREATE);
		db.execSQL(SOUND3_TABLE_CREATE);
		db.execSQL(SOUND4_TABLE_CREATE);
		db.execSQL(MFG_TABLE_CREATE);
		db.execSQL(BOARD_TABLE_CREATE);
		db.execSQL(YEAR_TABLE_CREATE);
		//}
	}

	public static Boolean checkTable() {
		SQLiteDatabase db = NDKBridge.m1db.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
						+ GAMELIST_TABLE_NAME + "'", null);
		if (cursor != null) {
			/*if (cursor.getCount() > 0) {
				cursor.close();
				db.close();
				return (true);
			}*/
			cursor.close();
			//db.close();
			return(true);
		}
		return (false);
	}

	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + GAMELIST_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CPU_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND1_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND2_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND3_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND4_TABLE);
		db.execSQL(MFG_TABLE_CREATE);
		db.execSQL(BOARD_TABLE_CREATE);
		db.execSQL(YEAR_TABLE_CREATE);

	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// // Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + GAMELIST_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CPU_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND1_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND2_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND3_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SOUND4_TABLE);
		db.execSQL(MFG_TABLE_CREATE);
		db.execSQL(BOARD_TABLE_CREATE);
		db.execSQL(YEAR_TABLE_CREATE);
		// Create tables again
		onCreate(db);
	}

	public static Cursor getAllTitles(SQLiteDatabase db, boolean filtered) {
		// default statement
		//if(filtered){
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			String union;
			String[] queries = new String[8];
			queries[0]=genQuery(CPU_TABLE,KEY_CPU_HASH, KEY_CPU);
			queries[1]=genQuery(SOUND1_TABLE,KEY_SOUND1_HASH, KEY_SOUND1);
			queries[2]=genQuery(SOUND2_TABLE,KEY_SOUND2_HASH, KEY_SOUND2);
			queries[3]=genQuery(SOUND3_TABLE,KEY_SOUND3_HASH, KEY_SOUND3);
			queries[4]=genQuery(SOUND4_TABLE,KEY_SOUND4_HASH, KEY_SOUND4);
			queries[5]=genQuery(BOARD_TABLE,KEY_SYS_HASH, KEY_SYS);
			queries[6]=genQuery(MFG_TABLE,KEY_MFG_HASH, KEY_MFG);
			queries[7]=genQuery(YEAR_TABLE,KEY_YEAR_HASH, KEY_YEAR);
			
			union = qb.buildUnionQuery(queries, null, null);
			union = "SELECT DISTINCT "+dispCols+" FROM "+dispTbls+" JOIN gamelist"
			+" ON cputable.cpuhash=gamelist.cpuhash AND "
			+" sound1.sound1hash=gamelist.sound1hash AND "
			+" sound2.sound2hash=gamelist.sound2hash AND "
			+" sound3.sound3hash=gamelist.sound4hash AND "
			+" sound4.sound4hash=gamelist.sound3hash AND "
			+" year.yearhash=gamelist.yearhash AND "
			+" mfg.mfghash=gamelist.mfghash AND "
			+" board.syshash=gamelist.syshash";
			return db.rawQuery(union, null);
			
						
		//}/
		//return (db.query(GAMELIST_TABLE_NAME, null, KEY_ROMAVAIL + "=1 ", null,
		//		null, null, KEY_TITLE));

	}

	
	public static Cursor getAllExtra(SQLiteDatabase db, String table, String key) {
		return (db.query(table, null, null, null, null, null, key));
	}

	public static void addExtra(String table, String key, String data, Long id){
		SQLiteDatabase db = NDKBridge.m1db.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(key+"hash", id);
		values.put(key, data);
		values.put(KEY_FILTERED,  0);
		db.insert(table, null, values);
		//db.close(); // Closing database connection
	}

	public static void addGame(Game game) {
		SQLiteDatabase db = NDKBridge.m1db.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, game.getIndex());
		values.put(KEY_TITLE, game.getTitle());
		values.put(KEY_YEAR_HASH, game.getIntyear());
		values.put(KEY_ROMNAME, game.getRomname());
		values.put(KEY_MFG_HASH, game.getIntmfg());
		values.put(KEY_SYS_HASH, game.getIntsys());
		values.put(KEY_CPU_HASH, game.getIntcpu());
		values.put(KEY_SOUND1_HASH, game.getIntsound1());
		values.put(KEY_SOUND2_HASH, game.getIntsound2());
		values.put(KEY_SOUND3_HASH, game.getIntsound3());
		values.put(KEY_SOUND4_HASH, game.getIntsound4());
		values.put(KEY_ROMAVAIL, game.getromavail());
		values.put(KEY_SOUNDHW, game.getSoundhw());

		// Inserting Row
		db.insert(GAMELIST_TABLE_NAME, null, values);
		//db.close(); // Closing database connection
	}
}