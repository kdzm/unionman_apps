package com.unionman.quicksetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.unionman.quicksetting.util.Constant;

/**
 * 
 * @author tsang_shengchang HiSi.ltd <br>
 *         receive buttons info
 */
public class KeyEventReceiver extends BroadcastReceiver {

    private static String TAG = "KeyEventReceiver";

    // public static long begintime;

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i("UIService","Broadcast onReceive()");
        Intent serviceinttent = new Intent(context, UIService.class);
        serviceinttent.putExtra(Constant.KEYEVENT_KEYCODE,
                intent.getIntExtra("hotkey", -1));
        context.startService(serviceinttent);
        Log.i("UIService","startService UIService");
    }

}
