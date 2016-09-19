package com.um.dvb_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.unionman.jazzlib.*;
import android.os.IBinder;

import com.um.dvbstack.DVB;

public class UmBroadcastReceiver extends BroadcastReceiver {
	final private String TAG = "UmBroadcastReceiver";
	
	private boolean isDvbstackAlive() {
		IBinder binder = ServiceManager.checkService("dvbstack");
		return (binder != null);
	}
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String action = arg1.getAction();
		Log.i(TAG, "onReceive: " + action);
		if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(action)) {
			boolean isAlive = DVB.isServerAlive();
			if (isAlive) {
				arg0.startService(new Intent(arg0, UmService.class));
				// 生命一号版本，去掉ca管控功能
				arg0.startService(new Intent(arg0, UmCaService.class));
			}
			Intent intent = new Intent("com.unionman.dvb.ACTION_DVB_SERVER_STATUS_CHANGE");
    		intent.putExtra("status", isAlive ? 1 : 0);
    		arg0.sendStickyBroadcast(intent);
		} else if ("com.unionman.dvb.ACTION_DVB_SERVER_READY".equalsIgnoreCase(action)) {
			if (DVB.isServerAlive()) {
				Log.v(TAG, "ready to start service.");
				arg0.startService(new Intent(arg0, UmService.class));
				// 生命一号版本，去掉ca管控功能
				arg0.startService(new Intent(arg0, UmCaService.class));
			}
    		Intent intent = new Intent("com.unionman.dvb.ACTION_DVB_SERVER_STATUS_CHANGE");
    		intent.putExtra("status", 1);
    		arg0.sendStickyBroadcast(intent);
		} else if ("com.unionman.dvb.ACTION_DVB_FACTORY_RESET".equalsIgnoreCase(action)) {
			boolean isAlive = DVB.isServerAlive();
			if (isAlive) {
				Intent it = new Intent(arg0, UmService.class);
				it.putExtra("reset", true);
				it.putExtra("trans_type", arg1.getIntExtra("trans_type", -1));
				arg0.startService(it);
			}
		}
	}
}
