package com.unionman.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("LAUNCHER_MAIN", "onReceiver, intent action: "+intent.getAction());
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			//SystemProperties.set("persist.sys.launcherstarted", "false");
		}
	}
}
