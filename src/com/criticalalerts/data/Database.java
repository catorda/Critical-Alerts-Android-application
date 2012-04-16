package com.criticalalerts.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

public class Database {
	private SQLiteDatabase db;
	private final Context context;
	private final DBHelper dbhelper; 
	private static final String TAG = "Database";
	// Initializes a DBHelper instance
	public Database(Context c) {
		context = c;
		dbhelper = new DBHelper(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
	}
	
	// Closes database connection
	public void close() {
		db.close();
	}
	
	// Initializes an SQLiteDatabase instance, using DBHelper
	// Opens a writeable database connection
	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch(SQLiteException ex) {
			db = dbhelper.getReadableDatabase();
		}
	}
	
	// Using SQLiteDatabase's insert() method, insert's a PSIRT into the database 
	// Returns the row ID of the newly inserted row, or -1 if an error occurred
	public long insertPSIRT(Bundle psirt) {
		try {
			ContentValues newTaskValue = new ContentValues(); 
			newTaskValue.put(DatabaseConstants.COL_PSIRT_ID, (String) psirt.get(DatabaseConstants.COL_PSIRT_ID));
			//newTaskValue.put(Constants.COL_DOCUMENT_NUMBER, (String) psirt.get(Constants.COL_DOCUMENT_NUMBER));
			newTaskValue.put(DatabaseConstants.COL_STATUS, (String) psirt.get(DatabaseConstants.COL_STATUS));
			newTaskValue.put(DatabaseConstants.COL_HEADLINE, (String) psirt.get(DatabaseConstants.COL_HEADLINE));
			newTaskValue.put(DatabaseConstants.COL_FIRST_PUBLISHED, (String) psirt.get(DatabaseConstants.COL_FIRST_PUBLISHED));
			newTaskValue.put(DatabaseConstants.COL_DATE_RECEIVED, (String) psirt.get(DatabaseConstants.COL_DATE_RECEIVED));
			newTaskValue.put(DatabaseConstants.COL_IMPACT, (String) psirt.get(DatabaseConstants.COL_IMPACT));
			newTaskValue.put(DatabaseConstants.COL_LAST_UPDATED, (String) psirt.get(DatabaseConstants.COL_LAST_UPDATED));
			newTaskValue.put(DatabaseConstants.COL_EXTERNAL_URL, (String) psirt.get(DatabaseConstants.COL_EXTERNAL_URL));
			//newTaskValue.put(Constants.COL_MESSAGE_DETAIL, (String) psirt.get(Constants.COL_MESSAGE_DETAIL));
			//newTaskValue.put(Constants.COL_MESSAGE_TYPE, (String) psirt.get(Constants.COL_MESSAGE_TYPE));
			newTaskValue.put(DatabaseConstants.COL_READ,  (String) psirt.get(DatabaseConstants.COL_READ));
			return db.insert(DatabaseConstants.TABLE_NAME, null, newTaskValue);
		} catch (SQLiteException ex) {
			Log.e("Inserst into database exception caught", ex.getMessage());
			return -1;
		}
	}
	
	public long updatePSIRTStatus(Bundle psirt) {
		ContentValues newTaskValue = new ContentValues(); 
		newTaskValue.put(DatabaseConstants.COL_STATUS, (String) psirt.get(DatabaseConstants.COL_STATUS)); 
		return db.update(DatabaseConstants.TABLE_NAME, newTaskValue, DatabaseConstants.KEY_ID + "=?", new String[]{String.valueOf(psirt.get(DatabaseConstants.KEY_ID))});
	}
	
	public long updatePSIRTRead(Bundle psirt) {
		ContentValues newTaskValue = new ContentValues(); 
		newTaskValue.put(DatabaseConstants.COL_READ, (String) psirt.get(DatabaseConstants.COL_READ)); 
		return db.update(DatabaseConstants.TABLE_NAME, newTaskValue, DatabaseConstants.KEY_ID + "=?", new String[]{String.valueOf(psirt.get(DatabaseConstants.KEY_ID))});
	}
	
	
	public Cursor getPSIRTs() {
		Cursor c = db.query(DatabaseConstants.TABLE_NAME, null, null, null, null, null, null);
		return c;
	}
}
