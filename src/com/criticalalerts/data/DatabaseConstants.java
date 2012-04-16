package com.criticalalerts.data;

public class DatabaseConstants {
	// Database Config 
	public static final String DATABASE_NAME="PSIRT_db";
	public static final int DATABASE_VERSION=1;
	public static final String TABLE_NAME="psirt_table";
	
	// Column Names 
	public static final String COL_PSIRT_ID="psirtid"; 
	public static final String COL_EXTERNAL_URL="externalurl";
	public static final String COL_FIRST_PUBLISHED="firstpublished";
	public static final String COL_DATE_RECEIVED="datereceived";
	public static final String COL_HEADLINE="headline";
	public static final String COL_READ="read"; 
	public static final String COL_LAST_UPDATED="lastupdated";
	public static final String COL_IMPACT="impact";
	public static final String KEY_ID="_id";
	public static final String COL_STATUS="status";
	
	// Database constant values 
	public static final String READ = "Read"; 
	public static final String UNREAD = "Unread"; 
	public static final String UNRESOLVED = "Unresolved"; 
	public static final String ASSIGNED = "Assigned"; 
	public static final String RESOLVED = "Resolved"; 
}
