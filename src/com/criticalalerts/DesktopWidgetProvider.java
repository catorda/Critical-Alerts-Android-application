/*
 * This class is based off of the App Widget recipe in: 
 * The Android Developers Cookbook Building Applications with the Android SDK 
 * By Jason Steele and Nelson To 
 */
package com.criticalalerts;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

public class DesktopWidgetProvider extends AppWidgetProvider {
	
	static final String TAG = "DesktopWidgetProvider";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// Loop through all widgets to display an update 
		final int N = appWidgetIds.length;
		for(int i=0; i<N; i++) {
			int appWidgetId = appWidgetIds[i];
			String titlePrefix = "Time since the widget was started: "; 
			updateAppWidget(context, appWidgetManager, appWidgetId, titlePrefix); 
		}
	}
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {

		CharSequence text = titlePrefix;
		text = "Psirt headline";
		
		//Construct the RemoteViews object. 
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		Log.i(TAG, views.toString());
		views.setTextViewText(R.id.widget_psirtheadline, text); 
		//Tell the widget manager 
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
