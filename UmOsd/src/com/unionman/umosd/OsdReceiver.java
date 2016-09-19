package com.unionman.umosd;

import com.um.dvbstack.DVB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OsdReceiver extends BroadcastReceiver{
    private final String TAG = OsdReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
    	if (!DVB.isServerAlive()) {
    		return;
    	}
    	
        if (intent.getAction().equals("com.unionman.umosd.START_TFCA_TOP_OSD")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.unionman.umosd.START_TFCA_TOP_OSD");
            i.putExtras(bundle);
            context.startService(i);
        } else if (intent.getAction().equals("com.unionman.umosd.START_TFCA_BOTTON_OSD")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.unionman.umosd.START_TFCA_BOTTON_OSD");
            i.putExtras(bundle);
            context.startService(i);
        } else if (intent.getAction().equals("com.unionman.umosd.STOP_TFCA_TOP_OSD")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Intent i = new Intent("com.unionman.umosd.STOP_TFCA_TOP_OSD");
            context.startService(i);
        } else if (intent.getAction().equals("com.unionman.umosd.STOP_TFCA_BOTTON_OSD")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Intent i = new Intent("com.unionman.umosd.STOP_TFCA_BOTTON_OSD");
            context.startService(i);
        }else if (intent.getAction().equals("com.unionman.umosd.START_DVTCA_BOTTON_OSD")){
            Log.i(TAG, "接收到广播:"+intent.getAction());
            Bundle bundle = intent.getExtras();
            Intent i = new Intent("com.unionman.umosd.START_DVTCA_BOTTON_OSD");
            i.putExtras(bundle);
            context.startService(i);
        }
    }
}



