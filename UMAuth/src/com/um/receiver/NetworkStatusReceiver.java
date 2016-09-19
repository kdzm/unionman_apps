package com.um.receiver;

import com.um.auth.MainService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 网络状态接收器，网络发生变化时，这里将收到广播
 * 
 * @author Eniso
 */
public class NetworkStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.v("UMAuthHW-NetworkStatusReceiver", "Network Status Changed!");
			//context.startService(new Intent(context, MainService.class));
		}
	}
}
