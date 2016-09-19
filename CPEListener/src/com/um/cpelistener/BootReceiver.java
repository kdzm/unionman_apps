package com.um.cpelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * boot completed,start the service of upgrade
 */
public class BootReceiver extends BroadcastReceiver {
	private final String TAG = "CpeListener--BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(TAG, "onReceiver, intent action: "+intent.getAction());
		//if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent it = new Intent(context, ListenService.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(it);
		//}
	}
}
