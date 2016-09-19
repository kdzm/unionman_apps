package com.um.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.um.ui.EpgTimerProc;
import com.um.dvbstack.ProgManage;

/**
 * Created by Administrator on 14-4-3.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context, EpgTimerProc.class);
            Log.v("bootreceiver","wsl####startService!!!");
            context.startService(i);
        }
    }
}
