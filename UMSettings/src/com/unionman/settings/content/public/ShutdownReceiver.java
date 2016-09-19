package com.unionman.settings.content;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.unionman.settings.content.ShutdownActivity;
import com.unionman.settings.tools.Logger;

public class ShutdownReceiver extends BroadcastReceiver {
	private static String TAG="com.unionman.settings.content.public--ShutdownReceiver--";
    @Override
    public void onReceive(Context context, Intent intent) {
            Logger.i(TAG,"onReceive()--");
            Logger.i(TAG, "action=" + intent.getAction());
            Logger.i(TAG, "jump to Dialog");
           /* Intent mIntent=new Intent(context,Dialog.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);*/
            Intent mIntent = new Intent();
            mIntent.setAction("com.unionman.SHUTDOWN_DIALOG");
            context.sendBroadcast(mIntent);

        ShutdownActivity.setAutoCloseEnable(context, false);
        PowerManager mPowerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mPowerManager.goToSleep(SystemClock.uptimeMillis());
    }
}