package com.unionman.umlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		Log.i("YYYPackage", "===============[" + intent.getAction() + "]");
	}

}
