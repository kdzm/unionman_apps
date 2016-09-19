package com.unionman.umlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by hjian on 2015/7/6.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("hjian---", "==========[" + intent.getAction() + "]");
    }
}
