/*
 * This service is based off of the UpdaterService in the AndroidBootcamp 
 * book by Marakana
 */
package com.criticalalerts;

import java.io.*;
import java.net.SocketException;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cisco.pssapi.TokenClient.MySSLSocketFactory;
import com.criticalalerts.DesktopWidgetProvider;
import com.criticalalerts.data.*;
import com.testwebservice.rest.PsirtServiceClient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
public class UpdaterService extends Service {

	protected static final String TAG = "UpdaterService";
	public final int UNIQUE_NOTIFICATION_ID = 284775; 
	static final int DELAY = 3600000; // one hour 
	private boolean runFlag = false;
	private Updater updater; 
	private MyServiceBinder myServiceBinder = new MyServiceBinder();
	protected Database psirtDatabase; 
	Context context;
	protected AppWidgetManager appWidgetManager;
	int[] allWidgetIds;
	ComponentName thisWidget;
	Intent intent;
	private UpdaterServiceBroadcastReceiver broadcastReceiver; 
	private NotificationManager nm; 
	
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service onCreate was called");
		context = this;
		this.updater = new Updater();
		
		IntentFilter filter = new IntentFilter(UpdaterServiceBroadcastReceiver.UPDATEDATABASE_ACTION); 
		broadcastReceiver = new UpdaterServiceBroadcastReceiver(); 
		registerReceiver(broadcastReceiver, filter);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
		nm.cancel(UNIQUE_NOTIFICATION_ID);
	}
	
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//String clientToken = TokenClient.getToken("dmI5ZngzajM1bTJoamtrc2h5c3I0ZmI0OlhrVGtrd05ENzJ4REtzenhQNFRLZ0ZWUw==", "GES9Y2wcSY96NVUxFH5DVsrd", "https://cloudsso-test2.cisco.com/as/token.oauth2");
		this.intent = intent;
		//Setting up Desktop Widget manager 
		
		//InventoryServiceClient inventoryServiceClient = new InventoryServiceClient();
		//Log.i(TAG, "Response: " + inventoryServiceClient.getCustomersInventory(clientToken));
		
		Random rand = new Random(); 
		
		psirtDatabase = new Database(context);
		psirtDatabase.open();
		psirtDatabase.close();
		
		//Put psirt in a database
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
		Date now = new Date(); 
		
		PsirtServiceClient psc = new PsirtServiceClient("http://192.168.1.109:5968/jersey/getPsirts");
		ArrayList<PSIRT> newPsirts = null;
		try {
			newPsirts = psc.getPsirts();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
			Toast.makeText(getApplicationContext(), "Could not connect to web service to check for new alerts", Toast.LENGTH_SHORT);
		}
		// Load old PSIRTs from database to compare them 
		int num = addNewPsirts(newPsirts);
		Log.i(TAG, "Number of Psirts added: " + num);
		if(num > 0) {
			Intent i = new Intent(this, AlertListActivity.class); 
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0); 
			String body = num + " Alerts received."; 
			String title = "New Alerts"; 
			Notification n = new Notification(R.drawable.error, body, System.currentTimeMillis());
			n.setLatestEventInfo(this, title, body, pi); 
			n.defaults = Notification.DEFAULT_ALL;
			nm.notify(UNIQUE_NOTIFICATION_ID, n);
		}
		
		this.runFlag = true; 
		if(!this.updater.isAlive()) {
			this.updater.start();
		}
		
		
		Log.d(TAG,"OnStart'd");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return myServiceBinder;
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false; 
		this.updater.interrupt();
		this.updater = null;
		
		this.unregisterReceiver(broadcastReceiver);
		
		Log.d(TAG, "onDestroy'd");
	}
	
	// This part of the Service will check the PSIRT web service for new PSIRTs 
	// and will download any new PSIRTs that show up
	private class Updater extends Thread {
		
		public Updater() {
			super("UpdaterService-Updater");
		}
		
		@Override 
		public void run() {
			Log.i(TAG, "running");
			UpdaterService us = UpdaterService.this;
			
			// Get date of last PSIRT download in XML file 
			
			while(us.runFlag) {
				try {
					
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
					allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
					thisWidget = new ComponentName(getApplicationContext(), DesktopWidgetProvider.class);
					
					RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(),
							R.layout.widget_layout);
					remoteViews.setTextViewText(R.id.widget_psirtheadline, "New Psirts");
					
					// Register an onClickListener 
					Intent clickIntent = new Intent(context.getApplicationContext(), DesktopWidgetProvider.class);
					
					clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
					clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
					
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					remoteViews.setOnClickPendingIntent(R.id.widget_psirtheadline, pendingIntent);
					
					appWidgetManager.updateAppWidget(thisWidget, remoteViews);
					// Send SOAP message
					
					// Receive SOAP response message
					
					// Check dates 
					
						// If new PSIRT is found, download and add to DB 
						// send broadcast to Application 
					
						// If no new PSIRT is found do nothing
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					us.runFlag = false;
				}
			}
		}
		
		
	}
	
	/**
	 * This method compares the date of the psirts to the latest date in the database, and 
	 * add the new ones to the database. 
	 * 
	 * We have to compare the dates because the web service will receive all the psirts. 
	 * 
	 * @param receivedPsirts Psirts received from the web service
	 * @return number of psirts added 
	 */
	private int addNewPsirts(ArrayList<PSIRT> receivedPsirts) {
		int numAdded = 0; 
		// Load old Psirts 
		ArrayList<PSIRT> oldPsirts = getAllPsirts(); 
		
		// Sort oldPsirts by date 
		Collections.sort(oldPsirts, new Comparator<PSIRT>() {
			@Override
			public int compare(PSIRT p1, PSIRT p2) {
				Date p1Date = p1.getLastUpdatedDate();
				Date p2Date = p2.getLastUpdatedDate(); 
				
				return p1Date.compareTo(p2Date)*-1;
			}
		});
		
		// Get latest date
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy MMMMM d");
		Date now = new Date(); 
		Date latestDate = null; 
		if(oldPsirts.size() != 0) {
			latestDate = oldPsirts.get(0).getLastUpdatedDate();
		} else {
			try {
				latestDate = dateFormatter2.parse("1980 January 1");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i(TAG, "Latest Date: " + latestDate.toGMTString());
		// Open database and add new psirts
		psirtDatabase.open(); 
		Bundle holder = new Bundle(); 
		if(receivedPsirts == null) {
			return 0;
		}
		for(PSIRT psirt : receivedPsirts) {
			if(psirt.getLastUpdatedDate().after(latestDate)) {
				holder = new Bundle(); 
				holder.putString(DatabaseConstants.COL_EXTERNAL_URL, psirt.getExternalURL());
				holder.putString(DatabaseConstants.COL_FIRST_PUBLISHED, formatDate(psirt.getFirstPublished()));
				holder.putString(DatabaseConstants.COL_LAST_UPDATED, formatDate(psirt.getLastUpdated()));
				holder.putString(DatabaseConstants.COL_STATUS, DatabaseConstants.UNRESOLVED);
				holder.putString(DatabaseConstants.COL_HEADLINE, psirt.getHeadline());
				holder.putString(DatabaseConstants.COL_DATE_RECEIVED, dateFormatter.format(now));
				holder.putString(DatabaseConstants.COL_PSIRT_ID, psirt.getId());
				holder.putString(DatabaseConstants.COL_READ, DatabaseConstants.UNREAD);
				holder.putString(DatabaseConstants.COL_IMPACT, psirt.getImpact());
				psirtDatabase.insertPSIRT(holder);
				numAdded++;
			}
		}
		
		return numAdded; 
		
	}
	
	public class MyServiceBinder extends Binder{

		public UpdaterService getService() {
			return UpdaterService.this;
		}
	}

	public ArrayList<PSIRT> getAllPsirts() {
		psirtDatabase.open(); 
		ArrayList<PSIRT> list = new ArrayList<PSIRT>();
		Cursor c = psirtDatabase.getPSIRTs();
		if(c.moveToFirst()) {
			do {
				PSIRT p = new PSIRT(); 
				p.setExternalURL(c.getString(c.getColumnIndex(DatabaseConstants.COL_EXTERNAL_URL)));
				p.setHeadline(c.getString(c.getColumnIndex(DatabaseConstants.COL_HEADLINE)));
				Log.i(TAG, c.getString(c.getColumnIndex(DatabaseConstants.COL_FIRST_PUBLISHED)));
				p.setFirstPublished(c.getString(c.getColumnIndex(DatabaseConstants.COL_FIRST_PUBLISHED)));
				p.setRead(c.getString(c.getColumnIndex(DatabaseConstants.COL_READ)));
				p.setStatus(c.getString(c.getColumnIndex(DatabaseConstants.COL_STATUS)));
				p.setDateReceived(c.getString(c.getColumnIndex(DatabaseConstants.COL_DATE_RECEIVED)));
				p.setLastUpdated(c.getString(c.getColumnIndex(DatabaseConstants.COL_LAST_UPDATED))); 
				p.setId(c.getString(c.getColumnIndex(DatabaseConstants.COL_PSIRT_ID)));
				p.setDatabaseKeyId(c.getInt(c.getColumnIndex(DatabaseConstants.KEY_ID)));
				p.setImpact(c.getString(c.getColumnIndex(DatabaseConstants.COL_IMPACT)));
				list.add(p);
			} while(c.moveToNext());
		}
		psirtDatabase.close();
		
		return list;
	}
	
	public class UpdaterServiceBroadcastReceiver extends BroadcastReceiver{

		public static final String UPDATEDATABASE_ACTION = "com.criticalalerts.intent.action.UPDATEDATABASE_ACTION";
		public static final String ACTION_TODO = "action-todo";
		public static final String UPDATE_STATUS = "update-status"; 
		public static final String UPDATE_READ = "update-read";
		public static final String PSIRT_POSITION = "position";
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Bundle extras = intent.getExtras();
			String action = extras.getString(ACTION_TODO); 
			
			// Update Database 
			psirtDatabase.open(); 
			if(action.equals(UPDATE_READ)) {
				psirtDatabase.updatePSIRTRead(extras);
			} else if (action.equals(UPDATE_STATUS)) {
				psirtDatabase.updatePSIRTStatus(extras);
			}
			
			psirtDatabase.close();
			 
			
			Log.d(TAG, "Context received");
		}

	}
	
	/**
	 * This method turns the string date received by the web service into a formatted 
	 * string that can be parsed by the SimpleDateFormat 
	 * 
	 * @param date string of date to format 
	 * @return String that can be parsed by the SimpleDateFormat yyyy MMMMM d HH:mm z
	 */
	private String formatDate(String date) {
		int UTCloc = date.indexOf("UTC");
		String part1 = date.substring(0, UTCloc+3); 
		
		Log.i(TAG, "Old date: " + date + " New Date: " + part1);
		return part1;
	}
	
	public static HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	    	Log.e(TAG, e.toString());
	        return new DefaultHttpClient();
	    }
	}

}
