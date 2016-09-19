package com.um.launcher.view;

import java.util.Calendar;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Like AnalogClock, but digital.  Shows seconds.
 *
 * FIXME: implement separate views for hours/minutes/seconds, so
 * proportional fonts don't shake rendering
 */

public class DigitalClock extends TextView {
	private static final String TAG = "DigitalClock";
	private static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_TIMEZONE_CHANGED;
	private Calendar mCalendar;
	private final static String mFormat = "yyyy-M-d\n\r\r\r\rk:mm";
	private FormatChangeObserver mFormatChangeObserver;
	private Runnable mTicker = new Runnable() {
		public void run() {
			//Log.v(TAG, "in ticker run.");
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			if (mCalendar.getTime().getYear() < 100){
				
			}

			setText(DateFormat.format(mFormat, mCalendar));
			//setText(localDateFormat(mCalendar));
			invalidate();
			mHandler.postDelayed(this, 1000);
		}
	};
	
	private Handler mHandler = new Handler();
	private Context mContext;
	
	public DigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initClock(context);
	}

	private void initClock(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

	}
	
	@Override
	public void onWindowFocusChanged(final boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		Log.d(TAG,"onWindowFocusChanged hasWindowFocus:"+hasWindowFocus);
		mHandler.removeCallbacks(mTicker);
		if (hasWindowFocus) {
			mHandler.post(mTicker);		
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.d(TAG,"onAttachedToWindow");
		registerTimezoneBoadcastReceiver();
		mFormatChangeObserver = new FormatChangeObserver();
		this.getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mFormatChangeObserver);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(TAG,"onDetachedFromWindow");
    	mHandler.removeCallbacks(mTicker);
		mContext.unregisterReceiver(timezoneReceiver);
		this.getContext().getContentResolver().unregisterContentObserver(mFormatChangeObserver);
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {

		}
	}
	
    private void registerTimezoneBoadcastReceiver(){
    	Log.d(TAG,"registerTimezoneBoadcastReceiver");
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    	mContext.registerReceiver(timezoneReceiver, filter);
    }
    
    private BroadcastReceiver timezoneReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "timezoneReceiver ACTION_TIMEZONE_CHANGED");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)){
            	Log.e(TAG, "timezoneReceiver ACTION_TIMEZONE_CHANGED");
            	if (mHandler != null){
            		mHandler.removeCallbacks(mTicker);
            	}
            	
            	mCalendar = null;
            	mCalendar = Calendar.getInstance();
            	if (mHandler != null){
            		mHandler.post(mTicker);
            	}
            }
        }
    };
    
    private String localDateFormat(Calendar calendar){
    	String dataTimeStr = null;
    	String dataStr = null;
    	String timeStr = null;
    	java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.getDefault());
    	java.text.DateFormat tf = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, Locale.getDefault());
    	dataStr = df.format(mCalendar.getTimeInMillis());
    	timeStr = tf.format(mCalendar.getTimeInMillis());
    	dataTimeStr = dataStr+"\n\r\r\r\r"+timeStr;
    	return dataTimeStr;
    }
}

