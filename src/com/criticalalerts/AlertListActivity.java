/*
 * This activity is the "TabHost" for the New and Old PSIRTs
 * tabs. 
 */
package com.criticalalerts;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.criticalalerts.data.DatabaseConstants;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class AlertListActivity extends ListActivity implements ServiceConnection {
	public final String TAG = "NewPsirtsActivity";
	UpdaterService s; 
	ArrayList<PSIRT> psirtList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getApplicationContext().bindService(new Intent(this, UpdaterService.class), this, 0)) {
			Log.i(TAG, "Service Successfully Bound");
		} else {
			Log.i(TAG, "Service not bound");
		}
		
		setContentView(R.layout.psirt_list);
		NotificationManager nm = (NotificationManager) getSystemService (NOTIFICATION_SERVICE);
		nm.cancel(284775);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if(s != null) { 
			this.updateList();
		}
		
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		s = ((UpdaterService.MyServiceBinder)service).getService();
		Toast.makeText(this, "Service successfully connected", Toast.LENGTH_LONG);
		this.updateList();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateList() {
		// Get the psirts from the service 
		ArrayList<PSIRT> psirtList = s.getAllPsirts();
		ArrayList<String> titles = new ArrayList<String>(psirtList.size());
		for (PSIRT psirt : psirtList){
    		titles.add(psirt.getHeadline());
    	}
		/*
		final ListView lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setAdapter(new MyCustomBaseAdapter(this, searchResults));
		
		ArrayAdapter<String> adapter = 
	    		new ArrayAdapter<String>(this, R.layout.row,titles);*/
	    this.setListAdapter(new PSIRTListAdapter(this, R.layout.row, psirtList));
	    
	}
	
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		PSIRT clickedPsirt = (PSIRT) listView.getItemAtPosition(position);
		
		// First update READ status 
		Intent broadcastIntent = new Intent(); 
		broadcastIntent.setAction(UpdaterService.UpdaterServiceBroadcastReceiver.UPDATEDATABASE_ACTION); 
		Bundle extras = new Bundle(); 
		extras.putString(DatabaseConstants.COL_READ, DatabaseConstants.READ);
		extras.putInt(DatabaseConstants.KEY_ID, clickedPsirt.getDatabaseKeyId());
		extras.putString(UpdaterService.UpdaterServiceBroadcastReceiver.ACTION_TODO, UpdaterService.UpdaterServiceBroadcastReceiver.UPDATE_READ);
		broadcastIntent.putExtras(extras);
		sendBroadcast(broadcastIntent);

		
		Intent i = new Intent();
		i.setClass(v.getContext(), PSIRTDetailActivity.class);
		i.putExtra("psirt_headline", clickedPsirt.getHeadline()); 
		i.putExtra("psirt_firstPublished", clickedPsirt.getFirstPublished());
		i.putExtra("psirt_lastupdated", clickedPsirt.getLastUpdated()); 
		i.putExtra("psirt_datereceived", clickedPsirt.getDateReceived());
		i.putExtra("psirt_url", clickedPsirt.getExternalURL());
		i.putExtra("psirt_status", clickedPsirt.getStatus());
		i.putExtra("psirt_id", clickedPsirt.getId());
		i.putExtra("psirt_key_id", clickedPsirt.getDatabaseKeyId());
		i.putExtra("psirt_impact", clickedPsirt.getImpact());
		startActivity(i);
		
		
	}
	
	/*
	 *  Adapter to display PSIRT items in the list
	 */
	private class PSIRTListAdapter extends BaseAdapter implements OnClickListener {
		private ArrayList<PSIRT> PSIRTArrayList;
		private LayoutInflater mInflater;
		View rowView;
		
		public PSIRTListAdapter(Context context, int layoutId, ArrayList<PSIRT> list) {
			PSIRTArrayList = list; 
			mInflater = LayoutInflater.from(context);
			this.rowView = findViewById(layoutId);
		}
		
		@Override
		public int getCount() {
			return PSIRTArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return PSIRTArrayList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			  ViewHolder holder;
			  if (convertView == null) {
			   convertView = mInflater.inflate(R.layout.row, null);
			   holder = new ViewHolder();
			   holder.headline = (TextView) convertView.findViewById(R.id.PSIRTtitle);
			   holder.date = (TextView) convertView.findViewById(R.id.PSIRTdate);
			   holder.statusImage = (ImageView) convertView.findViewById(R.id.PSIRTstatus);
			   convertView.setTag(holder);
			  } else {
			   holder = (ViewHolder) convertView.getTag();
			  }
			  if(PSIRTArrayList.get(position).getRead().equalsIgnoreCase(DatabaseConstants.UNREAD)) {
				  holder.headline.setTextColor(Color.RED);
				  holder.headline.setTypeface(null, Typeface.BOLD);
			  } 
			  
			  // Set status images 
			  if(PSIRTArrayList.get(position).getStatus().equalsIgnoreCase(DatabaseConstants.UNRESOLVED)) {
				  holder.statusImage.setImageResource(R.drawable.error); 
			  } else if (PSIRTArrayList.get(position).getStatus().equalsIgnoreCase(DatabaseConstants.ASSIGNED)) {
				  holder.statusImage.setImageResource(R.drawable.warning); 
			  } else {
				  holder.statusImage.setImageResource(R.drawable.ok);
			  }
			  
			  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
			  Date dateRec;
			try {
				dateRec = dateFormatter.parse(PSIRTArrayList.get(position).getDateReceived());
				SimpleDateFormat shortDate = new SimpleDateFormat("MMM dd");
				holder.date.setText(shortDate.format(dateRec));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				holder.date.setText("N/A");
			} 
			  holder.headline.setText(PSIRTArrayList.get(position).getHeadline());
			  
			  return convertView;
			 }
		
		class ViewHolder {
			TextView headline;
			TextView date;
			ImageView statusImage; 
		}

		@Override
		public void onClick(View view) {
			Log.i(TAG, "clicked");
		}
		
	}
	
}