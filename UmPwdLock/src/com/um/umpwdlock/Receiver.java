package com.um.umpwdlock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

	private static final String TAG = "Receiver";

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i(TAG,"====onReceive()");
		 String pwd = Settings.Secure.getString(context.getContentResolver(), "UMPwdLock19");
		 if(pwd==null){
			 Log.i(TAG,"pwd==null set pwd to default ");
			 Settings.Secure.putString(context.getContentResolver(), "UMPwdLock19", "123456999999999");
		 }
	}



}
