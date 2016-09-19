package com.unionman.gettoken;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.UserHandle;
import android.util.Log;
import android.os.SystemProperties;

public class TokenReceiver extends BroadcastReceiver {
	
	
	private static final String TAG = "com.unionman.gettoken-----TokenReceiver---";
	static final String ACTION = "action.GET.TOKEN.ZTE";
//	static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
//	static final String NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "onReceive()--------");
		String strAction = intent.getAction();
		Log.i(TAG,"action ==== "+strAction);
		String strSerialno = SystemProperties.get("ro.serialno", "");
		String StbIDString = strSerialno.substring(40, 41);
	
		Log.i(TAG,"StbIDString ==== "+StbIDString);
		if ( strAction.equals(ACTION)) {
			Log.i(TAG, "get the token Broadcast ------------");			
			Intent intent2 = new Intent(context, GetTokenService.class);
				context.startServiceAsUser(intent2, UserHandle.OWNER); 

		}

	}

}
