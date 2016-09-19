package com.um.auth;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

public class MainService extends Service {

	private String TAG = "UMAuthHW--"+this.getClass().getSimpleName();
	
	private HuaweiAuth mHuaweiAuth = null; // 华为开机认证
	
	private Uri stbCfg = Uri.parse("content://stbconfig/summary");
	private ContentResolver mContentResolver;
	private ContentObserver mContentObserver;

	private String REAUTH_ACTION="com.unionman.action.REAUTH";
	private BroadcastReceiver reauthBroadcastReceiver=new BroadcastReceiver(){
		public void onReceive(android.content.Context context, Intent intent) {
			Log.d(TAG, "onReceive action="+intent.getAction());
			if(intent.getAction().equals(REAUTH_ACTION)){
				mHuaweiAuth.huaweiAuth();
			}
		};
	};
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate()");
		mContentResolver = this.getContentResolver();
		mContentObserver = new ContentObserver(new Handler());
		mContentResolver.registerContentObserver(stbCfg, true, mContentObserver);
		super.onCreate();
	}

	private class ContentObserver extends android.database.ContentObserver {
		public ContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Cursor cursor = mContentResolver.query(stbCfg, null, null, null, null);
			if (cursor.moveToFirst()) {
				String UserID = cursor.getString(cursor.getColumnIndex("UserID"));
				Log.d(TAG, "ContentProvider name = " + UserID);
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		Bundle bd =  intent.getExtras();
		String userID = null;
		String userPasswd = null;
		if(bd != null){
			userID = bd.getString("user_id");
			userPasswd = bd.getString("user_passwd");
		}
		Log.d(TAG, "MainService onStartCommand() userid = " + userID + " userpasswd = " + userPasswd);
		// 这里是真正的入口，每次网络发生改变的时候，就会调用这里
		if (mHuaweiAuth == null)
			mHuaweiAuth = new HuaweiAuth(this);
		else
			mHuaweiAuth.initHuaweiAuth(userID, userPasswd);
		
		mHuaweiAuth.huaweiAuth(); // 每次网络变化，都会去认证
		
		Log.d(TAG, "registerReauthReceiver");
		registerReauthReceiver();
		// UserToken userToken = new UserToken();
		// userToken.getUserToken();
		return START_STICKY;
	};

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		if (mContentResolver != null && mContentObserver != null) {
			mContentResolver.unregisterContentObserver(mContentObserver);
			mContentResolver = null;
			mContentObserver = null;
		}
		unregisterReceiver(reauthBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void registerReauthReceiver(){
		IntentFilter filter=new IntentFilter();
		filter.addAction(REAUTH_ACTION);
		registerReceiver(reauthBroadcastReceiver, filter);
	}
}
