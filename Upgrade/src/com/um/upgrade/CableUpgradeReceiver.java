package com.um.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CableUpgradeReceiver extends BroadcastReceiver {
    private final String TAG = CableUpgradeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "接收到广播 : " + intent.getAction());
        if (intent.getAction().equals("com.um.upgrade.CABLE_FORCE_UPGRADE")) {
            Intent i = new Intent(context, CableForceUpgradeReminderActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("com.um.upgrade.CABLE_MANUAL_UPGRADE")) {
            Intent i = new Intent(context, CableManualUpgradeReminderActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
