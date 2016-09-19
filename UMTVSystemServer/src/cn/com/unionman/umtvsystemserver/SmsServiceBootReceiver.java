package cn.com.unionman.umtvsystemserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class SmsServiceBootReceiver extends BroadcastReceiver {
	private static String TAG = "SmsServiceBootReceiver";
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String UMBOOT_COMPLETED_ACTION = "android.intent.action.UMBOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
    	    Log.i(TAG, "onReceive");
    	    Log.i(TAG, "ACTION "+intent.getAction());
            if (intent.getAction().equals(UMBOOT_COMPLETED_ACTION)){
                    Intent i = new Intent(Intent.ACTION_RUN);
                    i.setClass(context, TVSystemMonitorService.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(i);
            }
    }
 
}
