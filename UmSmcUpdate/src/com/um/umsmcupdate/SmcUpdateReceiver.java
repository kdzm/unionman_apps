package com.um.umsmcupdate;

import com.um.dvbstack.DVB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SmcUpdateReceiver extends BroadcastReceiver{
    private final String TAG = SmcUpdateReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
    	if (!DVB.isServerAlive()) {
    		return;
    	}
        if (intent.getAction().equals("com.um.umsmcupdate.START_PROGRESS_RECEIVEPATCH")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Log.e("Status", "START_PROGRESS_RECEIVEPATCH 0");
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.um.umsmcupdate.START_PROGRESS_RECEIVEPATCH");
            i.putExtras(bundle);
            context.startService(i);
        } else if (intent.getAction().equals("com.um.umsmcupdate.START_PROGRESS_PATCHING")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Log.e("Status", "START_PROGRESS_PATCHING 0");
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.um.umsmcupdate.START_PROGRESS_PATCHING");
            i.putExtras(bundle);
            context.startService(i);
        } else if (intent.getAction().equals("com.um.umsmcupdate.STOP_PROGRESS_RECEIVEPATCH")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Log.e("Status", "STOP_PROGRESS_RECEIVEPATCH 0");
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.um.umsmcupdate.STOP_PROGRESS_RECEIVEPATCH");
            i.putExtras(bundle);
            context.startService(i);
        } else if (intent.getAction().equals("com.um.umsmcupdate.STOP_PROGRESS_PATCHING")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Log.e("Status", "STOP_PROGRESS_PATCHING 0");
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.um.umsmcupdate.STOP_PROGRESS_PATCHING");
            i.putExtras(bundle);
            context.startService(i);
        }
    }
}



