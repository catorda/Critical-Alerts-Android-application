package com.criticalalerts.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	public final String TAG = "DBHelper";
	private static final String CREATE_TABLE = "create table " +
			DatabaseConstants.TABLE_NAME + " (" + 
			DatabaseConstants.KEY_ID + " integer primary key autoincrement, " + 
			DatabaseConstants.COL_STATUS + " text not null, " +
			DatabaseConstants.COL_PSIRT_ID +" text not null, " + 
			//Constants.COL_DOCUMENT_NUMBER+" text not null, " + 
			DatabaseConstants.COL_HEADLINE+ " text not null, " + 
			DatabaseConstants.COL_IMPACT + " text not null, " +
			DatabaseConstants.COL_READ + " text not null, " + 
			DatabaseConstants.COL_FIRST_PUBLISHED + " text not null, "+
			DatabaseConstants.COL_DATE_RECEIVED + " text not null," + 
			DatabaseConstants.COL_LAST_UPDATED+ " text not null, " +
			DatabaseConstants.COL_EXTERNAL_URL + " text not null);";
			//Constants.COL_MESSAGE_DETAIL + " text not null, " + 
			//Constants.COL_MESSAGE_TYPE + " text not null);";

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_TABLE);
			Log.i(TAG, "Table created");
		} catch(SQLiteException ex) {
			Log.e("DBHelper", "Error execSQL table" + "/n" + CREATE_TABLE);
		}
	}
	
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + DatabaseConstants.TABLE_NAME);
		onCreate(db);
	}
}
