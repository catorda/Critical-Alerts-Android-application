package com.criticalalerts;

import com.criticalalerts.data.DatabaseConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PSIRTDetailActivity extends Activity implements OnClickListener {

	private ImageView statusIcon; 
	private TextView status;
	private TextView headline; 
	private TextView id; 
	private TextView firstPublished; 
	private TextView lastUpdated; 
	private TextView dateReceived;
	private TextView impact;
	private Button moreInfoButton; 
	private Button emailButton;
	private String url; 
	private int psirtDatabaseKeyId;
	static final String TAG = "PSIRTDetailActivity";
	final int EMAIL_REQUEST = 32;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.psirt_detail);
        
        Bundle extras = this.getIntent().getExtras();
        
        statusIcon = (ImageView) this.findViewById(R.id.psirtdetail_statusIcon);
        status = (TextView) this.findViewById(R.id.psirtdetail_status);
        headline = (TextView) this.findViewById(R.id.psirtdetail_headline); 
        id = (TextView) this.findViewById(R.id.psirtdetail_id);
        firstPublished = (TextView) this.findViewById(R.id.psirtdetail_firstpublished);
        lastUpdated = (TextView) this.findViewById(R.id.psirtdetail_lastupdated);
        dateReceived = (TextView) this.findViewById(R.id.psirtdetail_datereceived);
        moreInfoButton = (Button) this.findViewById(R.id.psirtdetail_externalurl_button);
        moreInfoButton.setOnClickListener(this);
        impact = (TextView) this.findViewById(R.id.psirtdetail_impact);
        
        if(((String) extras.get("psirt_status")).equalsIgnoreCase(DatabaseConstants.UNRESOLVED)) {
			statusIcon.setImageResource(R.drawable.error);
			status.setText((CharSequence) extras.get("psirt_status"));
		} else if (((String) extras.get("psirt_status")).equalsIgnoreCase(DatabaseConstants.ASSIGNED)) {
			statusIcon.setImageResource(R.drawable.warning); 
			status.setText((CharSequence) extras.get("psirt_status"));
		} else {
			statusIcon.setImageResource(R.drawable.ok);
			status.setText((CharSequence) extras.get("psirt_status"));
		}
        status.setOnClickListener(this);
        statusIcon.setOnClickListener(this);
        headline.setText((String)extras.get("psirt_headline"));
        id.setText((String)extras.get("psirt_id"));
        firstPublished.setText((String)extras.get("psirt_firstPublished"));
        lastUpdated.setText((String)extras.get("psirt_lastupdated"));
        dateReceived.setText((String)extras.getString("psirt_datereceived"));
        impact.setText((String) extras.getString("psirt_impact"));
        url = (String) extras.getString("psirt_url");
        psirtDatabaseKeyId = extras.getInt("psirt_key_id");
        Log.d(TAG, (String) extras.get("psirt_firstPublished"));
        
        emailButton = (Button) this.findViewById(R.id.psirtdetail_email_button);
        emailButton.setOnClickListener(this); 
        
        //emailButton.getBackground().setColorFilter(new LightingColorFilter((Integer) null, 0x884F94CD)); 
        
    }
	
	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.psirtdetail_email_button) {
			
			String body = ""; 
			body += headline.getText() + "\n"; 
			body += "\n"; 
			body += "Alert Id: " + id.getText() + "\n"; 
			body += "First published: " + firstPublished.getText() + "\n"; 
			body += "Last updated: " + lastUpdated.getText() + "\n"; 
			body += "\n"; 
			body += "External URL for more information: " + "\n";
			body += url;
			
			String subject = "[CriticalAlerts] " + headline.getText();
			
			Intent emailIntent = new Intent();
			emailIntent.setClass(arg0.getContext(), SendEmailActivity.class); 
			emailIntent.putExtra("subject", subject); 
			emailIntent.putExtra("body", body);

			
			this.startActivityForResult(emailIntent, EMAIL_REQUEST);
			
		} else if (arg0.getId() == R.id.psirtdetail_externalurl_button) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); startActivity(browserIntent); 
		} else if (arg0.getId() == R.id.psirtdetail_status || arg0.getId() == R.id.psirtdetail_statusIcon) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Set alert's status to Resolved?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {           
					public void onClick(DialogInterface dialog, int id) {               
						// Change STATUS to ASSIGNED
						Intent broadcastIntent = new Intent(); 
						broadcastIntent.setAction(UpdaterService.UpdaterServiceBroadcastReceiver.UPDATEDATABASE_ACTION); 
						Bundle extras = new Bundle(); 
						extras.putString(DatabaseConstants.COL_STATUS, DatabaseConstants.RESOLVED);
						extras.putInt(DatabaseConstants.KEY_ID, psirtDatabaseKeyId);
						extras.putString(UpdaterService.UpdaterServiceBroadcastReceiver.ACTION_TODO, UpdaterService.UpdaterServiceBroadcastReceiver.UPDATE_STATUS);
						broadcastIntent.putExtras(extras);
						sendBroadcast(broadcastIntent);          
						
						statusIcon.setImageResource(R.drawable.ok); 
						status.setText(DatabaseConstants.RESOLVED);
					}       
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {           
					public void onClick(DialogInterface dialog, int id) {                
						           
					}     
				});AlertDialog alert = builder.create();  
				alert.show();
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EMAIL_REQUEST) {             
			Log.i(TAG, "Request code: " + requestCode + " " + EMAIL_REQUEST);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Set alert's status to Assigned?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {           
					public void onClick(DialogInterface dialog, int id) {               
						// Change STATUS to ASSIGNED
						Intent broadcastIntent = new Intent(); 
						broadcastIntent.setAction(UpdaterService.UpdaterServiceBroadcastReceiver.UPDATEDATABASE_ACTION); 
						Bundle extras = new Bundle(); 
						extras.putString(DatabaseConstants.COL_STATUS, DatabaseConstants.ASSIGNED);
						extras.putInt(DatabaseConstants.KEY_ID, psirtDatabaseKeyId);
						extras.putString(UpdaterService.UpdaterServiceBroadcastReceiver.ACTION_TODO, UpdaterService.UpdaterServiceBroadcastReceiver.UPDATE_STATUS);
						broadcastIntent.putExtras(extras);
						sendBroadcast(broadcastIntent);          
						
						statusIcon.setImageResource(R.drawable.warning); 
						status.setText(DatabaseConstants.ASSIGNED);
					}       
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {           
					public void onClick(DialogInterface dialog, int id) {                
						           
					}     
				});AlertDialog alert = builder.create();  
				alert.show();
		}     
	}

}
