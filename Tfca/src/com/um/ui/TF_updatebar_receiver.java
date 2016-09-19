 package com.um.ui;

import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Ca.Ca_Version;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TF_updatebar_receiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_SCALE_RECEIVEPATCH"))
		{
			int process;
			int flag = 1;

			Bundle bundle = intent.getExtras();
			process = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 1RECEIVEPATCH"+process);
//			if(process == 1)
//			{	
//				Intent it = new Intent(context,Tf_updatebar.class);
//				//it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//				it.putExtra("progress", String.valueOf(process));
//				it.putExtra("flag",String.valueOf(flag));
//				context.startActivity(it);
//			}

		}
		else if(intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_SCALE_PATCHING"))
		{
			int process;
			int flag = 0;
			
			Bundle bundle = intent.getExtras();
			process = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 1PATCHING"+process);
//			Intent it = new Intent(context,Tf_updatebar.class);
//			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//			it.putExtra("progress", String.valueOf(process));
//			it.putExtra("flag",String.valueOf(flag));
//			context.startActivity(it);
		}	
			
	}
	
}
