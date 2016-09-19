package com.um.auth;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

public class TokenReceiver extends BroadcastReceiver {
	
	static final String ACTION = "action.GET.TOKEN.HW";
	private static final String TAG = "com.um.auth-----TokenReceiver---";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "onReceive()--------");
		String strAction = intent.getAction();
		Log.i(TAG,"action ==== "+strAction);
		if (strAction.equals(ACTION)) {
			Log.i(TAG, "get the token Broadcast and start token service----");
			//context.startService(new Intent(context, GetTokenService.class));
			Intent intent2 = new Intent(context, MainService.class);
			context.startServiceAsUser(intent2, UserHandle.OWNER);
		}
		

	}

}
