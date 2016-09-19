package com.unionman.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class NetworkChangeReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("LAUNCHER_MAIN", arg1.getAction().toString());
		SystemProperties.set("persist.sys.networkchange", "1");
	}

}
