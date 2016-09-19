package com.um.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.um.controller.Play_Full_Screen;
import com.um.controller.Player;
import com.um.dvbstack.DVB;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.ProviderProgManage;

public class DvbPlayerBroadCastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        Log.e("-----DVB BC----", "receive broadcast");
        if (!DVB.isServerAlive()) {
        	return;
        }
        
		if(intent.getAction().equals("android.intent.action.DVB_FULLSCREEN_PLAY"))
		{
			DVB.getInstance();
			Player.GetInstance();
			Play_Full_Screen.GetInstance().setMainMemuFlag(false);
			Log.e("-----DVB BC----", "DvbPlayerBroadCastReceiver");
			Intent startServiceIntent = new Intent(context, Dvbplayer_Activity.class);
			ProviderProgManage.GetInstance(context).SetCurMode(ProgManage.TVPROG);
			startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startServiceIntent);
		}
        else if(intent.getAction().equals("com.um.dvb.MONITOR_NIT_VERSION_CHANGE"))
        {
            Log.d("", "ProgList isEmpty?: " + ProviderProgManage.GetInstance(context).getAllProgList().list.isEmpty());
            if(!ProviderProgManage.GetInstance(context).getAllProgList().list.isEmpty()) {
                int defver = 0;
                int nitver = intent.getIntExtra("nitver", defver);
                Log.v("DvbPlayerBroadCastReceiver", "wsl#####ACTION_NIT_VERSION_CHANGE!!!" + nitver);
                Intent it = new Intent(context, DvbMonitorAlertDialog.class);
                it.putExtra("version", nitver);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        }
	}
}
