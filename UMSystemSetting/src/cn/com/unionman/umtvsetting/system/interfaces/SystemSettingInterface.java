package cn.com.unionman.umtvsetting.system.interfaces;

import java.io.File;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.os.SystemProperties;

import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.SystemUtils;
import cn.com.unionman.umtvsetting.system.util.Util;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSystemSetting;
import com.hisilicon.android.tvapi.CusPicture;

/**
 * interface of system setting
 * 
 * @author huyq
 * 
 */
public class SystemSettingInterface {
	public static final String TAG = "SystemSettingInterface";
	private static final String SLEEP_ON_SWITCH_ACTION = "cn.com.unionman.umtvsystemserver.SLEEP_ON_SWITCH_ACTION";
	private Context context;

	/**
	 * standbyLedState value: 0 off ; 1 on
	 */
	private int standbyLedState = 0;
	/**
	 * usbState value: 0 off; 1 on
	 */
	private int usbState = 0;
	private int bluetoothState = 0;
	/**
	 * sleeponState value: 0 off;
	 */
	private int sleeponState = 0;
	/**
	 * screensaverState value: 0 animation; 1 4k picture
	 */
	private int screensaverState = 0;
	/**
	 * 卖场模式 demoMode value: 0 off; 1 on
	 */
	private int demoMode = 0;
	 /**
     * 开机信号源记忆开关
     * 0 off 中间件信号源;
     * 1 on 上次关机退出信号源
     */
    private int bootSourceState = 1;
    private int config_bootSourceState = 1;
    
    private final static int DEFAULT_VOLUME = 30;
	
	public SystemSettingInterface(Context mContext) {
		context = mContext;
		SharedPreferences sharedata = context.getSharedPreferences("itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS);
		standbyLedState = sharedata.getInt("standbyLedState", 1);
		usbState = sharedata.getInt("usbState", 1);
		bluetoothState = sharedata.getInt("bluetoothState", 0);
		sleeponState = sharedata.getInt("sleeponState", 0);
		screensaverState = sharedata.getInt("screensaverState", 0);
		demoMode = sharedata.getInt("demoMode", 0);
		config_bootSourceState = getSystemProperties("um.app.default_bootSourceState",1);
    	bootSourceState = sharedata.getInt("bootSourceState", config_bootSourceState);
	}
	
	    public int getBootSourceState() {
		return bootSourceState;
	}

	public void setBootSourceState(int bootSourceState) {
		 Editor sharedata = context.getSharedPreferences("itemVal", Activity.MODE_WORLD_WRITEABLE|Activity.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS).edit();  
	     sharedata.putInt("bootSourceState",bootSourceState); 
	     sharedata.commit();  	
		this.bootSourceState = bootSourceState;
	}

	public int getKeySoundState() {
		Log.i("1000", "getKeySoundState:isKeypadSoundEnable====="
				+ UmtvManager.getInstance().getSystemSetting()
						.isKeypadSoundEnable() + "");
		int mode = UmtvManager.getInstance().getSystemSetting()
				.isKeypadSoundEnable() ? 1 : 0;
		return Util.getIndexFromArray(mode, InterfaceValueMaps.key_sound);
	}

	public void setKeySoundState(int keySoundState) {
		boolean b = (InterfaceValueMaps.key_sound[keySoundState][0] == 0) ? false
				: true;
		Log.i("1000", "setKeySoundState:b=====" + b);
		int ret = UmtvManager.getInstance().getSystemSetting()
				.enableKeypadSound(b);
		// wkg Change the system setting
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.SOUND_EFFECTS_ENABLED, b ? 1 : 0);
	}

	public int getStandbyLedState() {
		
		return (getSystemSettingManager().isPowerLedStateEnable()==true?1:0);
	}

	public void setStandbyLedState(int standbyLedState) {
		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("standbyLedState", standbyLedState);
		sharedata.commit();
		if (standbyLedState == 1) {
			getSystemSettingManager().enablePowerLedState(true);
		} else {
			getSystemSettingManager().enablePowerLedState(false);
		}
		this.standbyLedState = standbyLedState;

	}

	public int getDemoModeState() {
		return demoMode;
	}

	public void setDemoModeState(int demoMode) {

		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("demoMode", demoMode);
		sharedata.commit();
		this.demoMode = demoMode;
	}

	public int getUsbState() {
		return usbState;
	}

	public void setUsbState(int usbState) {
		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("usbState", usbState);
		sharedata.commit();
		this.usbState = usbState;
	}

	public int getSecureState() {
		return getSecureMode();
	}

	public void setSecureState(int secureState) {
		setSecureMode(secureState);
	}

	private int getSecureMode() {
		return isNonMarketAppsAllowed() ? 0 : 1;
	}

	private int setSecureMode(int i) {
		if (i == 0) {
			setNonMarketAppsAllowed(true);
		} else {
			setNonMarketAppsAllowed(false);
		}
		return 1;
	}

	private boolean isNonMarketAppsAllowed() {
		return Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0;
	}

	private void setNonMarketAppsAllowed(boolean enabled) {
		// Change the system setting
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
	}

	public int getBluetoothState() {
		return bluetoothState;
	}

	public void setBluetoothState(int bluetoothState) {
		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("bluetoothState", bluetoothState);
		sharedata.commit();
		this.bluetoothState = bluetoothState;
	}

	public int getSleeponState() {
		return sleeponState;
	}

	public void setSleeponState(int sleeponState) {
		setSleeponState(sleeponState, true);
	}

	public void setSleeponState(int sleeponState, boolean broadcast) {
		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("sleeponState", sleeponState);
		sharedata.commit();
		this.sleeponState = sleeponState;
		if (broadcast) {
			Intent intent = new Intent(SLEEP_ON_SWITCH_ACTION);
			intent.putExtra("SleepOn", sleeponState);
			context.sendBroadcast(intent);
		}
	}

	public int getScreensaverState() {
		return screensaverState;
	}

	public void setScreensaverState(int screensaverState) {
		Editor sharedata = context.getSharedPreferences(
				"itemVal",
				Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE
						| Context.MODE_MULTI_PROCESS).edit();
		sharedata.putInt("screensaverState", screensaverState);
		sharedata.commit();
		this.screensaverState = screensaverState;
	}

	/**
	 * get instance of system setting
	 * 
	 * @return
	 */
	public static CusSystemSetting getSystemSettingManager() {
		return UmtvManager.getInstance().getSystemSetting();
	}

	/**
	 * enableScreenBlue
	 */
	public static int enableScreenBlue(boolean onOff) {
		if (Constant.LOG_TAG) {
			Log.d(TAG, "enableScreenBlue(boolean onOff = " + onOff + ") begin");
		}

		int value = getSystemSettingManager().enableScreenBlue(onOff);

		if (Constant.LOG_TAG) {
			Log.d(TAG, "enableScreenBlue(boolean onOff = " + onOff
					+ ") end value = " + value);
		}
		return value;
	}

	/**
	 * isScreenBlueEnable
	 */
	public static boolean isScreenBlueEnable() {
		if (Constant.LOG_TAG) {
			Log.d(TAG, "isScreenBlueEnable() begin");
		}

		boolean value = getSystemSettingManager().isScreenBlueEnable();

		if (Constant.LOG_TAG) {
			Log.d(TAG, "isScreenBlueEnable() end value = " + value);
		}
		return value;
	}

	/**
	 * restoreDefault
	 */
	public int restoreDefault() {
		if (Constant.LOG_TAG) {
			Log.d(TAG, "restoreDefault() begin");
		}
		resetoreLocalData();
		int value = getSystemSettingManager().restoreDefault(0);

		if (Constant.LOG_TAG) {
			Log.d(TAG, "restoreDefault() end value = " + value);
		}

		// TODO: reboot system!
		return value;
	}
	
	/**
	 * restoreDefault(flag)  for user
	 */
	public int restoreDefault(int flag) {
		if (Constant.LOG_TAG) {
			Log.d(TAG, "restoreDefault() begin");
		}
		resetoreLocalData();
		int value = getSystemSettingManager().restoreDefault(flag);

		if (Constant.LOG_TAG) {
			Log.d(TAG, "restoreDefault() end value = " + value);
		}

		// TODO: reboot system!
		return value;
	}


	private void resetoreLocalData() {
		//add for restore volume
		restoreVolume();
		//by end
		setStoreMode(0);
		setSecureState(0);
		setKeySoundState(0);

		setStandbyLedState(1);
		setUsbState(1);
		// setBluetoothState(0);
		setSleeponState(0, false);
		setScreensaverState(0);
        setBootSourceState(config_bootSourceState);
	}
/*
 * set default volume(30)
 */
	private void restoreVolume() {
		AudioManager am=(AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, DEFAULT_VOLUME , 0);
	}

	/**
	 * get instance of Picture
	 * 
	 * @return
	 */
	public CusPicture getPictureManager() {
		return UmtvManager.getInstance().getPicture();
	}

	public boolean getStoreMode() {
		boolean value = getPictureManager().getStoreMode();
		return value;
	}

	public int setStoreMode(int storeMode) {
		int value = getPictureManager().setStoreMode(
				storeMode == 1 ? true : false);
		return value;
	}
	
	public  int getSystemProperties(String key, int defauleValue) {
        String value = SystemProperties.get(key, defauleValue + "");
        if (value != null && value.trim().length() > 0) {
            return Integer.parseInt(value);
        }
        return defauleValue;
    }
}
