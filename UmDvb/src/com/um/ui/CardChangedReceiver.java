package com.um.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.SourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.um.dvbstack.DVB;
import com.unionman.dvbserver.DvbServerManager;
import com.unionman.jazzlib.SystemProperties;

public class CardChangedReceiver extends BroadcastReceiver {
	private static final String TAG = "CardChangedReceiver";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		String action = arg1.getAction();
		Log.v(TAG, "receive " + action);
		if (action.equals("com.unionman.intent.CA_CARD_CHANGED")) {
			Log.v(TAG, "card is changed.");
			if (!DVB.isServerAlive()) {
				Log.v(TAG, "WARN: service is not alive, something wrong is happened.");
				return ;
			}
			
			if (SystemProperties.getInt("sys.dvb.cas.cardchanged", 0) == 0) {
				Log.v(TAG, "WARN: sys.dvb.cas.cardchanged is 0.");
				return ;
			}
			
			if (SystemProperties.getInt("sys.dvbentry.booted", 0) ==  0) {
				Log.v(TAG, "WARN: dvb has never been launched. drop the card-changed event");
				return ;
			}
			
			if (isInDvbActivity(arg0)) {
				Log.v(TAG, "ready to launch EntryActivity...");
				Intent it = new Intent("com.unionman.intent.ACTION_PLAY_DVB");
		    	it.putExtra("cardChanged", true);
		    	it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	arg0.startActivity(it);
			}
		}
	}
	
	private boolean isDtvSource() {
		int curSourceId = UmtvManager.getInstance().getSourceManager().getCurSourceId(0);
		return curSourceId == EnumSourceIndex.SOURCE_DVBC
				|| curSourceId == EnumSourceIndex.SOURCE_DTMB;
	}

	private boolean isInDvbActivity(Context context) {
		if (!isDtvSource()) {
			return false;
		}
		
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		
		return !packageName.equals("com.unionman.settingwizard");
	}
}
