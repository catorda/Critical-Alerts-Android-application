package com.testwebservice.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.net.SocketException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cisco.pssapi.RestClient;
import com.criticalalerts.PSIRT;

public class PsirtServiceClient {

	private String URL; 
	private static final String TAG = "PsirtServiceClient";
	
	public PsirtServiceClient() {
		
	}
	
	public PsirtServiceClient(String url) {
		URL = url; 
	}
	
	public ArrayList<PSIRT> getPsirts() throws SocketException {
		Log.i(TAG, "Getting psirts");
		HttpClient httpClient = RestClient.getNewHttpClient();
		HttpGet getRest = new HttpGet(URL); 
		ArrayList<PSIRT> newPsirts = new ArrayList<PSIRT>();
		// Execute post with soap message 
		String response= null;
		try {
			HttpResponse resp = httpClient.execute(getRest);
			BufferedInputStream bis = new BufferedInputStream(resp.getEntity().getContent());
			BufferedReader rd = new BufferedReader(new InputStreamReader(bis), 4096);
			String line;
			StringBuilder sb =  new StringBuilder();
			while ((line = rd.readLine()) != null) {
					sb.append(line);
			}
			rd.close();
			response = sb.toString();
		} catch (SocketException s) {
			throw s;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e(TAG, e1.toString()); 
			response = null; 
		}
		
		if(response != null) {
			try {
				Log.i(TAG, "Response: " + response.toString());
				JSONObject ps = new JSONObject(response);
				JSONArray psirts = ps.getJSONArray("psirts");
				for(int i = 0; i < psirts.length(); i++) {
					JSONObject jsonP = psirts.getJSONObject(i);
					PSIRT p = new PSIRT(); 
					p.setHeadline(jsonP.getString("headline")); 
					p.setAlertVersion(jsonP.getString("alertversion")); 
					p.setId(jsonP.getString("alertid")); 
					p.setFirstPublished(jsonP.getString("firstpublisehd")); 
					String lastUpdated; 
					if(jsonP.getString("lastupdated") == null || jsonP.getString("lastupdated").equalsIgnoreCase("null")) {
						lastUpdated = jsonP.getString("firstpublisehd"); 
					} else {
						lastUpdated = jsonP.getString("lastupdated");
					}
					p.setLastUpdated(lastUpdated); 
					p.setImpact(jsonP.getString("impact")); 
					p.setExternalURL(jsonP.getString("url")); 
					
				    newPsirts.add(p);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return newPsirts;
	}
	
	/**
	 * This method turns the string date received by the web service into a formatted 
	 * string that can be parsed by the SimpleDateFormat 
	 * 
	 * @param date string of date to format 
	 * @return String that can be parsed by the SimpleDateFormat yyyy MMMMM d HH:mm  z
	 */
	private String formatDate(String date) {
		String newDate; 
		Log.i(TAG, "Date: "+ date + " UTC Location: " + date.indexOf("UTC")); 
		int UTCloc = date.indexOf("UTC");
		String part1 = date.substring(0, UTCloc+3);
		
		Log.i(TAG, "Old date: " + date + " New Date: " + part1);
		return part1;
	}
	
}
