package com.um.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.um.controller.ParamSave;


public class ProgUpdateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		if(intent.getAction().equals("com.umionman.ACTION_BOOT_COMPLETED"))
//		{
//			int state = ParamSave.getProgSyncStatus(context);
//			if(state==0)
//			{
//				Intent serviceIntent = new Intent(context, ProgUpdateActivity.class);
//				serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	            context.startActivity(serviceIntent);
//			}
//		}
	}

}
