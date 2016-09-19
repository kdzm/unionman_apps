package com.um.huanauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AuthBootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "AuthBootCompleteReceiver";
	private static final String UMBOOT_COMPLETED_ACTION = "android.intent.action.UMBOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(UMBOOT_COMPLETED_ACTION)){
			Log.d(TAG,"onReceive UMBOOT_COMPLETED_ACTION");
			Intent serviceIntent = new Intent();
			serviceIntent.setClass(context, AuthService.class);
			//serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(serviceIntent);
			
			UpgradeService.mServiceState = UpgradeService.UpgradeService_BOOT;
			Intent serviceIntent1 = new Intent();
			serviceIntent1.setClass(context, UpgradeService.class);
			//serviceIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//serviceIntent1.putExtra("startmode", "boot");
			context.startService(serviceIntent1);
		}
	}
}
