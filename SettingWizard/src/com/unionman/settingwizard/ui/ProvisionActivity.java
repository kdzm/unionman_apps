package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.unionman.settingwizard.util.CityDvbUtils;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.PropertyUtils;
import com.unionman.settingwizard.util.PackageUtils;
import com.unionman.settingwizard.util.FileUtils;
import com.unionman.settingwizard.util.SystemUtils;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;

/**
 * Created by hjian on 2014/12/26.
 */
	
public class ProvisionActivity extends Activity {
	
	private Handler mHandler = new Handler();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (isATModeForCVTE())
		{
			ATModeDo();
		}else
		{
			startActivity(new Intent(this, MainActivity.class));

			CityDvbUtils.initDvbPackages(this, false);
			PreferencesUtils.putBoolean(this, "boot_by_provision", true);

			finish();
		}

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

	private Runnable mATModeRunnable = new Runnable() {
		@Override
		public void run() {
			startATScreen();
		}
	};
	
	private void startATScreen() {
		Intent it = new Intent("cvte.factory.intent.action.ATScreenActivity");
		startActivity(it);
		finish();
	}
	
	private boolean prepareForFactoryTest() {
		boolean installSuccess = false;
		
		for (int i = 0; i < 3; i++) {
			if (PackageUtils.installPackage(this,
					"/vendor/dvb/install_packages/default_city/CityFeature.apk")) {
				installSuccess = true;
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (!installSuccess) {
			return false;
		}
		
		Intent it = new Intent("com.unionman.cityfeature.FEATURE_INIT");
		startService(it);

		FileUtils.deleteFile("/data/dvb");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/dvb.db", "/data/dvb/dvb.db");
		FileUtils.changeFolderMod("/data/dvb", "777");
		
		FileUtils.deleteFile("/data/data/umdb.dat");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/umdb.dat", "/data/data/umdb.dat");
		FileUtils.changeFileMod("/data/data/umdb.dat", "777");

		FileUtils.deleteFile("/atv/db/atv.db");
		FileUtils.copyFile("/atv/dtv/UmPreProgramSet/atv.db", "/atv/db/atv.db");
		FileUtils.changeFolderMod("/atv/db/atv.db", "777");

		CusFactoryImpl.getInstance().loadATVProg();
		
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
