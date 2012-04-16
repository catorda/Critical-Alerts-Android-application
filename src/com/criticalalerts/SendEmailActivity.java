package com.criticalalerts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SendEmailActivity extends Activity{

	@Override     
	protected void onCreate(Bundle savedInstanceState) {     
		super.onCreate(savedInstanceState);  
		
		Bundle extras = this.getIntent().getExtras();
		
		final Intent email = new Intent(android.content.Intent.ACTION_SEND);         
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, extras.getString("subject"));             
		email.putExtra(android.content.Intent.EXTRA_TEXT, extras.getString("body"));    
		email.setType("plain/text"); 
		this.startActivityForResult(Intent.createChooser(email, "Sending email to notify correspondant"), 2);         
	} 
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {      
			this.finish();
		}
	}
}
