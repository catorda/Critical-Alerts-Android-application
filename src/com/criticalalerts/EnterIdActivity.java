package com.criticalalerts;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EnterIdActivity extends Activity implements OnClickListener{

	Button submitButton;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_layout);
        startService(new Intent(this, UpdaterService.class));
        submitButton = (Button) this.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.submitButton) {
			
			// TO DO 
			// Verify that password/username is valid
			
			Intent i = new Intent(this, AlertListActivity.class);
			startActivity(i);
		}
		
	}
}
