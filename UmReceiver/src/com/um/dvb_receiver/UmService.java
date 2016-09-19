package com.um.dvb_receiver;

import java.util.Timer;
import java.util.TimerTask;


import com.um.dvbstack.DVB;
import com.um.dvbstack.DvbStackSearch;
import com.um.dvbstack.ReceiverMsgInterface;
import com.um.dvbstack.Status;
import com.um.dvbstack.Tuner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.unionman.dvbstorage.ProgStorage;
import com.unionman.jazzlib.*;
import com.um.dvbstack.Tuner.TunerInfo;


import android.widget.Toast;
import com.um.umreceiver.R;

public class UmService extends Service implements Status.StatusListener{
	private final String TAG = "UmService";
	private Timer mUpgradeTimer = new Timer();
	private NitVersionMonitor mNitMonitor = null;
	private ProgStorage mProgStorage = null;
	private int mFrontendType = -1;
	
	private TimerTask mUpgradeTimerTask = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (SystemProperties.get("runtime.ota.cable.check.manual","0").equals("1")) {
				Log.i(TAG, "Start manual upgrade");
				UmService.this.sendBroadcast(new Intent("com.um.upgrade.CABLE_MANUAL_UPGRADE"));
			} else if (SystemProperties.get("runtime.ota.cable.check.force", "0").equals("1")){
				Log.i(TAG, "Start force upgrade");
				UmService.this.sendBroadcast(new Intent("com.um.upgrade.CABLE_FORCE_UPGRADE"));
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();

        if (!DVB.isServerAlive()) {
            Log.d(TAG, "Server not alive, exit UmService");
            stopSelf();
            return;
        }
        
        Log.i(TAG, "UmService onCreate!!");
        DVB dvb = DVB.getInstance();		
        mFrontendType = Tuner.GetInstance(dvb).GetType();
        Log.v(TAG, "mFrontendType="+mFrontendType);
		mUpgradeTimer.schedule(mUpgradeTimerTask, 0, 1000*30);
		Log.i(TAG, "UpgradeTimer is started");
		mProgStorage = new ProgStorage(getContentResolver());
		
		mNitMonitor = new NitVersionMonitor(new NitVersionMonitor.OnNitChangeListener() {
			@Override
			public void onNitChange(int newVer) {
				// TODO Auto-generated method stub
		        int progTypeId;
		        if (mFrontendType == Tuner.UM_TRANS_SYS_TYPE_TER) {
		            progTypeId = 4;
		        } else  {
		            progTypeId = 3;
		        }
		        
				boolean isEmpty = true;
				if (mProgStorage != null) {
					isEmpty = mProgStorage.getProgCount(new int[] {progTypeId}) <= 0;
				}
				if (!isEmpty) {
					showNitChangeDialog();
				} else {
					Log.v(TAG, "prog is empty, not need to show dialog");
				}
			}
		});
        mNitMonitor.start();
        checkTunerStatus();
        Status.getInstance().addStatusListener(this);
        
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		boolean reset = intent.getBooleanExtra("reset", false);
		if (reset) {
			Log.v(TAG, "clear local info");
			mNitMonitor.reset();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
    private void sendTunerStatus(boolean hasSignal) {
    	Log.v(TAG, "ready to send broadcast: hasSignal="+hasSignal);
        Intent intent = new Intent();
        intent.setAction("unionman.intent.action.STATUS_TUNER");
        if (hasSignal) {
            intent.putExtra("no_signal", false);
        } else {
            intent.putExtra("no_signal", true);
        }
        sendBroadcast(intent);

		//sendStickyBroadcast(intent);
    }
    private void checkTunerStatus () {
        boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
		DVB dvb = DVB.getInstance();
		Tuner tuner = Tuner.GetInstance(dvb);
		TunerInfo info = new TunerInfo();
		if (0 == tuner.GetInfo(0, info)) {
			Log.v(TAG, "tuner.CurrLockFlag " + info.CurrLockFlag+" flag " + flag);
			if(0 != info.CurrLockFlag)
		        sendTunerStatus(true);
			else
				sendTunerStatus(false);
		}
	}

    @Override
    public void onDestroy() {
    	Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (DVB.isServerAlive()) {
        	mNitMonitor.stop();
            DVB.getInstance().release();
        }
        Status.getInstance().removeStatusListener(this);
    }

	private void showNitChangeDialog() {
		Dialog alertDialog = new AlertDialog.Builder(getApplicationContext())
				.setTitle(R.string.altert_dialog_title)
				.setMessage(R.string.nit_version_change_tips)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startSearch();
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
	}
    
	private void startSearch() {
		int fre = ParamSave
				.GetMainFreq();
		int symbl = 6875;
		int qam = 3;
		int type = 0;

		ComponentName componentName = new ComponentName(
				"com.um.dvbsearch", "com.um.ui.Search");
		Intent it = new Intent();
		it.setComponent(componentName);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.setAction("com.um.dvbsearch.START_SEARCH");
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		bundle.putInt("fre", fre);
		bundle.putInt("sym", symbl);
		bundle.putInt("qam", qam);
		it.putExtras(bundle);

		startActivity(it);
	}
    @Override
    public void OnMessage(Message msg) {
    	if (msg.what == 1) {
    		checkTunerStatus();
    	}
    }
}
