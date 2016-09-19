package com.unionman.settingwizard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("times", Context.MODE_PRIVATE);
        int count = preferences.getInt("times", 0);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {/*
            if (count == 0) {
                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
            }

            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        */}
    }
}
