package com.um.tv.menu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class TvKeyReceiver extends BroadcastReceiver {
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "TvKeyReceiver";
    public static final int FACTORY_MEMU_KEY = 226;
    public static final int AGING_MEMU_KEY = 227;
    public static final int M_MODE_KEY = 228;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.d(TAG, SUBTAG+"onReceive:	" + action);
        Intent startService = new Intent(context, TvMenuWindowManagerService.class);
        if (action.equals("android.intent.action.UMBOOT_COMPLETED")) {
            startService.putExtra(TvMenuWindowManagerService.COMMAND, TvMenuWindowManagerService.COMMAND_START_SERVICE);
            context.startService(startService);
        } else if (action.equals("com.android.um.SendHotKey")) {
            int key = intent.getIntExtra("hotkey", 0);
            String extraString = intent.getStringExtra("extra");
            Log.d(TAG, SUBTAG+"onReceive:	hotkey:" + key);
            switch (key) {
                case KeyEvent.KEY_FACTORY:
                    startService.putExtra(TvMenuWindowManagerService.COMMAND, TvMenuWindowManagerService.COMMAND_FACTORY_MENU);
                    startService.putExtra(TvMenuWindowManagerService.EXTRA, extraString);
                    context.startService(startService);
                    break;
                case KeyEvent.KEY_AGING:
                    startService.putExtra(TvMenuWindowManagerService.COMMAND, TvMenuWindowManagerService.COMMAND_AGING_MENU);
                    context.startService(startService);
                    break;
                default:
                    startService.putExtra(TvMenuWindowManagerService.COMMAND, TvMenuWindowManagerService.COMMAND_MMODE_KEY);
                    startService.putExtra(TvMenuWindowManagerService.KEY, key);
                    startService.putExtra(TvMenuWindowManagerService.EXTRA, extraString);
                    context.startService(startService);
                	break;
            }
        } else if (action.equals("com.android.um.StartFactory")) {
            startService.putExtra(TvMenuWindowManagerService.COMMAND, TvMenuWindowManagerService.COMMAND_FACTORY_MENU);
            context.startService(startService);
        }
    }
}
