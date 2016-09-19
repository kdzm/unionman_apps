package com.unionman.factorytestassist;

import com.unionman.factorytestassist.utils.FileLog;
import com.unionman.factorytestassist.utils.FileUtils;
import com.unionman.factorytestassist.utils.PackageUtils;
import com.unionman.factorytestassist.utils.PropertyUtils;
import com.unionman.factorytestassist.utils.SystemUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;

public class PrepareActivity extends Activity {
	private static final String TAG = "PrepareActivity";
	private static final String ERROR_LOGCAT_FILE = "/data/fac_prepare_error.log";
	private static final String TIMEOUT_LOGCAT_FILE = "/data/fac_prepare_timeout.log";
	private static final String ONBOOT_LOGCAT_FILE = "/data/fac_onboot.log";
	private static final boolean UPGRADE_TEST = false;
	private static final int OPERATE_TIME_OUT = 20 * 1000;
	
	private TextView mTxtMsg;
	private Handler mHandler = new Handler();
	private FileLog mFileLog = FileLog.getInstance();
	
	private Runnable mFinishRunnable = new Runnable() {
		@Override
		public void run() {
			//PackageUtils.enableSettingWizard(PrepareActivity.this, false);
			PackageUtils.setProvisioned(PrepareActivity.this, true);
			disableMyself();
			Toast.makeText(PrepareActivity.this, R.string.prepare_success, 3 * 1000).show();
			if (isATModeForCVTE()){
				startATScreen();
			}else{
				startLauncher();
			}
			
		}
	};

	private Runnable mFailRunnalbe = new Runnable() {

		@Override
		public void run() {
			disableMyself();
			Toast.makeText(PrepareActivity.this, R.string.prepare_failed, 3 * 1000).show();
			//startSettingWizard();
			startLauncher();
		}
	};
	
	private Runnable mATModeRunnable = new Runnable() {
		@Override
		public void run() {
			startATScreen();
		}
	};
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		return true;
	};

	private void disableMyself() {
		ComponentName name = new ComponentName(this, PrepareActivity.class);
		PackageUtils.enableComponet(this, name, false);
	}
	
	private boolean isATModeForCVTE(){
		boolean isATMode = false;
		isATMode = (PropertyUtils.getInt("ro.boot.AT_Read", 0) == 1);
		
		return isATMode;
	}
	
	private void ATModeDo(){
		
		new Thread() {
			public void run() {
				long startTime = SystemClock.uptimeMillis();
				boolean result = prepareForFactoryTest();
				long useTime = SystemClock.uptimeMillis() - startTime;
				
				mHandler.postDelayed(mATModeRunnable, useTime > 3000 ? 0
				: useTime - 3 * 1000);
			};
		}.start();
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFileLog.print(TAG, "onCreate");
		
		if (UPGRADE_TEST) {
			SystemUtils.shellExecute("logcat -d -f " + ONBOOT_LOGCAT_FILE);
		}
		
		if (PropertyUtils.getInt("persist.sys.factorymode", 0) == 0) {
			mFileLog.print(TAG, "persist.sys.factorymode is 0");
			if (isATModeForCVTE()){
				ATModeDo();
			}else{
				disableMyself();
				//startSettingWizard();
				startLauncher();
			}

			return;
		}

		mFileLog.print(TAG, "persist.sys.factorymode is 1");

		setContentView(R.layout.activity_main);
		TextView txt = (TextView) findViewById(R.id.txt_title);
		txt.setText(R.string.preparing_for_factory_test);

		mTxtMsg = (TextView) findViewById(R.id.txt_msg);
		mTxtMsg.setText(R.string.please_wait_for_prepare);

		new Thread() {
			public void run() {
				mFileLog.print(TAG, "ready to prepare for factory testing.");
				long startTime = SystemClock.uptimeMillis();
				boolean result = prepareForFactoryTest();
				long useTime = SystemClock.uptimeMillis() - startTime;
				mFileLog.print(TAG, "after prepareForFactoryTest(). use time: "
						+ useTime + "(ms)");
				
				if (result) {
					if (UPGRADE_TEST) {
						mFileLog.print(TAG, "ready to reboot for upgrade...");
						//SystemUtils.shellExecute("echo --update_package=/storage/emulated/0/update.zip > /cache/recovery/command");
						SystemUtils.shellExecute("echo boot-recovery > /dev/block/platform/hi_mci.1/by-name/misc; reboot;");
						return ;
					}
					
					mHandler.postDelayed(mFinishRunnable, useTime > 3000 ? 0
							: useTime - 3 * 1000);
				} else {
					SystemUtils.shellExecute("logcat -d -f " + ERROR_LOGCAT_FILE);
					mHandler.postDelayed(mFailRunnalbe, useTime > 3000 ? 0
							: useTime - 3 * 1000);
				}
			};
		}.start();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mFileLog.print(TAG, "ERROR: operate timeout.");
				SystemUtils.shellExecute("logcat -d -f " + TIMEOUT_LOGCAT_FILE);
				mTxtMsg.setText(R.string.timeout_tip);
			}
		}, OPERATE_TIME_OUT);
	}

	@Override
	protected void onPause() {
		mFileLog.print(TAG, "onPuase");
		mHandler.removeCallbacksAndMessages(null);
		super.onPause();
	}

	private void startSettingWizard() {
		mFileLog.print(TAG, "ready to start setting wizard");
		Intent it = getPackageManager().getLaunchIntentForPackage(
				"com.unionman.settingwizard");
		startActivity(it);
		finish();
	}

	private void startLauncher() {
		mFileLog.print(TAG, "ready to start launcher");
		Intent it = getPackageManager().getLaunchIntentForPackage(
				"com.um.launcher");
		startActivity(it);
		finish();
	}

	private void startATScreen() {
		Intent it = new Intent("cvte.factory.intent.action.ATScreenActivity");
		startActivity(it);
		finish();
	}
	
	private boolean prepareForFactoryTest() {
		mFileLog.print(TAG, "ready to install CityFeature.apk.");
		boolean installSuccess = false;
		
		/*
		for (int i = 0; i < 3; i++) {
			if (PackageUtils.installPackage(this,
					"/vendor/dvb/install_packages/default_city/CityFeature.apk")) {
				installSuccess = true;
				break;
			} else {
				mFileLog.print(TAG, "instlal CityFeature.apk failed. try count:" + (i+1));
				try {
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		if (!installSuccess) {
			mFileLog.print(TAG, "failed in installing apk");
			return false;
		}
		*/
		
		mFileLog.print(TAG,
				"ready to start service: com.unionman.cityfeature.FEATURE_INIT");
		Intent it = new Intent("com.unionman.cityfeature.FEATURE_INIT");
		startService(it);

		mFileLog.print(TAG, "PROGRESET: ready to cp dvb db.");
		FileUtils.deleteFile("/data/dvb");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/dvb.db", "/data/dvb/dvb.db");
		FileUtils.changeFolderMod("/data/dvb", "777");
		
		FileUtils.deleteFile("/data/data/umdb.dat");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/umdb.dat", "/data/data/umdb.dat");
		FileUtils.changeFileMod("/data/data/umdb.dat", "777");
		mFileLog.print(TAG, "copy dvb db done.");

		mFileLog.print(TAG, "ready to copy atv db.");
		FileUtils.deleteFile("/atv/db/atv.db");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/atv.db", "/atv/db/atv.db");
		FileUtils.changeFolderMod("/atv/db/atv.db", "777");

		CusFactoryImpl.getInstance().loadATVProg();
		mFileLog.print(TAG, "copy atv db done.");
		
		PropertyUtils.setInt("persist.sys.dvb.installed", 1);

		int powerMode = CusFactoryImpl.getInstance().getPoweronMode();
		PropertyUtils.setInt("persist.sys.powerMode", powerMode);
		
		// sync persist properties to flash.
		FileUtils.sync();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			
		}
		
		return true;
	}

}
