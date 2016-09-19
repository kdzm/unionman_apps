package com.um.tv.menu.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class FactoryCmdsActivity extends Activity{
	
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "FactoryCmdsReceiver";
	public static final String FACTORY_CMDS = "com.um.tv.factorycmds";
	
	private static final String FACCMD_SWITCHHDMI = "switch_hdmi";
	private static final String FACCMD_SAVEAWB = "save_awb";
	private static final String FACCMD_RESETAWB = "reset_awb";
	private static final String FACCMD_FINISH = "finish_awb";
	
	BroadcastReceiver facbc = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(FACTORY_CMDS.equals(intent.getAction())){
				Bundle tBundle = intent.getExtras();
                if(tBundle != null){
                    String facCmd = tBundle.getString("cmds");
                    Log.d(TAG,SUBTAG+" get factory cmds "+facCmd);
                    if(MmodeKeyManager.ismModeNoteShow()){
	                    if(FACCMD_SWITCHHDMI.equals(facCmd)){
	                    	MmodeKeyManager.hdmiSwichProcess();
	                    }else if(FACCMD_SAVEAWB.equals(facCmd)){
	                    	
	                    }else if(FACCMD_RESETAWB.equals(facCmd)){
	                    	
	                    }else if(FACCMD_FINISH.equals(facCmd)){
	                    	
	                    }
                    }
                }
			}
		}
	}; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		IntentFilter lfilter = new IntentFilter(FACTORY_CMDS);
		FactoryCmdsActivity.this.registerReceiver(facbc, lfilter);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
