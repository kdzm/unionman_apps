package com.unionman.factorytestassist;

import com.unionman.factorytestassist.utils.FileUtils;
import com.unionman.factorytestassist.utils.PackageUtils;
import com.unionman.factorytestassist.utils.PropertyUtils;
import com.unionman.factorytestassist.utils.SystemUtils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;

public class FastResetActivity extends Activity {
	private static final String TAG = "FastResetActivity";
	private LinearLayout ll_all;
	private TextView mTxtMsg;
	private boolean isSysInit = false;
	
	private Handler mHanlder = new Handler();
	private Runnable mErrorRunable = new Runnable() {
		@Override
		public void run() {
			if(isSysInit){
				mTxtMsg.setText(R.string.reinit_system_failed);
			}else{
				mTxtMsg.setText(R.string.reset_system_failed);
			}
			mHanlder.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					FastResetActivity.this.finish();
				}
			}, 2*1000);
			
		}
	};
	
	private Runnable mSuccessRunable = new Runnable() {
		@Override
		public void run() {
			if(isSysInit){
				mTxtMsg.setText(R.string.reinit_system_success);
			}else{
				mTxtMsg.setText(R.string.reset_system_success);
			}
			mHanlder.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					SystemUtils.suspendSystem(FastResetActivity.this);
				}
			}, 4*1000);
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		isSysInit = false;
		Intent it = getIntent();
		String ext = it.getStringExtra("extra");
		Log.v(TAG, "ext "+ext);
		
		if(ext.equals("sysInit")){
			
			isSysInit = true;
			Log.v(TAG, "sysInit now Mmode Fac Calls "+ext);
		}
		
		setContentView(R.layout.activity_main);
		ll_all = (LinearLayout)findViewById(R.id.ll_all);
		TextView txt = (TextView)findViewById(R.id.txt_title);
		if(isSysInit){
			txt.setText(R.string.reinit_system);
			mTxtMsg = (TextView)findViewById(R.id.txt_msg);
			mTxtMsg.setText(R.string.reinit_system_please_wait);
		}else{
			txt.setText(R.string.reset_system);
			mTxtMsg = (TextView)findViewById(R.id.txt_msg);
			mTxtMsg.setText(R.string.please_wait);
			ll_all.setBackgroundColor(Color.RED);			
		}
		
		SystemUtils.enableKeyDispatch(false);
		
		new Thread() {
			public void run() {
				boolean result = resetSystem();
				if (result) {
					mHanlder.post(mSuccessRunable);
				} else {
					mHanlder.post(mErrorRunable);
				}
			};
		}.start();
	}
	
	private void enablePrepare() {
		ComponentName name = new ComponentName(this, PrepareActivity.class);
		PackageUtils.enableComponet(this, name, true);
	}
	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		
		SystemUtils.enableKeyDispatch(true);
		super.onDestroy();
	}
	
	private boolean resetSystem() {
		Log.v(TAG, "ready to reset system...");
		SystemUtils.setBcbForBootRecovery(true);
		
		int powerMode = PropertyUtils.getInt("persist.sys.powerMode", 1);
		if (CusFactoryImpl.getInstance().getPoweronMode() != powerMode) {
			CusFactoryImpl.getInstance().setPoweronMode(powerMode);
		}
    	
		PackageUtils.uninstallPackage(this, "com.unionman.cityfeature");
		
		//Force stop background service
		PackageUtils.forceStopAndWipePackages(this);
		
		//Remove data
		SystemUtils.removeDirAndFile("/data/misc/wifi");
		SystemUtils.removeDirAndFile("/data/misc/bluetooth");
		SystemUtils.removeDirAndFile("/data/misc/bluetoothd");
		SystemUtils.removeDirAndFile("/data/misc/bluedroid");
		
		SystemUtils.removeDirAndFile("/data/atv/db/user.db");
		SystemUtils.removeDirAndFile("/data/atv/db/atv.db");
		
		Log.v(TAG, "PROGRESET: clear dvb data");
		SystemUtils.removeDirAndFile("/data/dvb/dvb.db");
		SystemUtils.removeDirAndFile("/data/data/umdb.dat");
		SystemUtils.removeDirAndFile("/data/data/umdb_sysdata.dat");
		SystemUtils.removeDirAndFile("/data/data/umdb_sysdata.dat-bak");
		SystemUtils.removeDirAndFile("/data/data/umdb_sysdata_ter.dat");
		SystemUtils.removeDirAndFile("/data/data/umdb_sysdata_ter.dat-bak");
		SystemUtils.removeDirAndFile("/data/data/config.xml");
		
		SystemUtils.removeDirAndFile("/data/data/com.android.providers.settings/databases/settings.db");
		PackageUtils.enableSettingWizard(this, true);
		if(isSysInit){
			Log.v(TAG, "Mmode Calls System init");
			//SystemUtils.shellExecute("cp /data/property/persist.sys.factorymode /data/dvb/");
			//isSysInit = false;
		}
		SystemUtils.removeDirAndFile("/data/property/*");
		PackageUtils.enableSettingWizard(this, true);
		if(isSysInit){
			enablePrepare();
			Log.v(TAG, "Mmode Calls System init");
			//SystemUtils.shellExecute("cp /data/dvb/persist.sys.factorymode /data/property/persist.sys.factorymode");
			//isSysInit = false;
			PropertyUtils.setInt("persist.sys.factorymode", 1);
		}

		FileUtils.sync();
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			
		}
		
		SystemUtils.setBcbForBootRecovery(false);
    	
		
		return true;
	}

}
