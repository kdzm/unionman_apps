package com.um.networkupgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpgradeReceiver extends BroadcastReceiver {
	private final String TAG = "NetworkUpgrade--UpgradeReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(TAG, "onReceiver, intent action: "+intent.getAction());
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent it = new Intent(context, UpgradeService.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(it);
		}
	}
}
