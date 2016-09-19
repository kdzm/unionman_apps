package cn.com.unionman.umtvsetting.powersave.interfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.WindowManager;

import cn.com.unionman.umtvsetting.powersave.util.Constant;

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
	private static final String NOSIGNAL_PD_SWITCH_ACTION = "cn.com.unionman.umtvsystemserver.NOSIGNAL_PD_SWITCH_ACTION";
	private static final String AUTO_STANDBY_ACTION = "cn.com.unionman.umtvsystemserver.AUTO_STANDBY_ACTION";
	
	private int powerDisplay;
	private int autoShutdonw;
	private int waitting;
	private Context context;
    public static final String TAG = "SystemSettingInterface";

    public SystemSettingInterface(Context mContext ){
    	context = mContext;
    	SharedPreferences sharedata = context.getSharedPreferences("PoweritemVal",Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS);
    	powerDisplay = sharedata.getInt("powerDisplay", 0);
    	autoShutdonw = sharedata.getInt("autoShutdonw", 1);
    	waitting  = sharedata.getInt("waitting", 0);
    }
    
    public int getSavingEnergy() {
		return PictureInterface.isDynamicBLEnable()?1:0;
	}

	public void setSavingEnergy(int savingEnergy) {
		PictureInterface.enableDynamicBL(savingEnergy==1);
	}

	public int getPowerDisplay() {
		return powerDisplay;
	}

	public void setPowerDisplay(int powerDisplay) {
		 Editor sharedata = context.getSharedPreferences("PoweritemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                 + Context.MODE_MULTI_PROCESS).edit();  
	      sharedata.putInt("powerDisplay",powerDisplay); 
	      sharedata.commit();  	
		this.powerDisplay = powerDisplay;
	}

	public int getAutoShutdonw() {
		return autoShutdonw;
	}

	public void setAutoShutdonw(int autoShutdonw) {
		 Editor sharedata = context.getSharedPreferences("PoweritemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                 + Context.MODE_MULTI_PROCESS).edit();  
	      sharedata.putInt("autoShutdonw",autoShutdonw); 
	      sharedata.commit();  	
		this.autoShutdonw = autoShutdonw;
		Intent intent = new Intent(NOSIGNAL_PD_SWITCH_ACTION);
		context.sendBroadcast(intent);
		Log.i(TAG,"sendBroadcast NosignalOnOff");
	}
	public int getWaitting() {
		return waitting;
	}

	public void setWaitting(int waitting) {
		 Editor sharedata = context.getSharedPreferences("PoweritemVal",Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                 + Context.MODE_MULTI_PROCESS).edit();  
	      sharedata.putInt("waitting",waitting); 
	      sharedata.commit();  	
		this.waitting = waitting;
		Intent intent = new Intent(AUTO_STANDBY_ACTION);
		context.sendBroadcast(intent);
		
		Log.i(TAG,"sendBroadcast AutoStanbyValue");
	}

	/**
     * get instance of Picture
     *
     * @return
     */
    public static CusPicture getPictureManager() {
        return UmtvManager.getInstance().getPicture();
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
    public static int restoreDefault() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "restoreDefault() begin");
        }

        int value = getSystemSettingManager().restoreDefault(0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "restoreDefault() end value = " + value);
        }
        return value;
    }
    
    /**
     * getBrightnessHistogram
     */
    public static ArrayList<Integer> getBrightnessHistogram() {
    	return getPictureManager().getBrightnessHistogram();
    }    
}
