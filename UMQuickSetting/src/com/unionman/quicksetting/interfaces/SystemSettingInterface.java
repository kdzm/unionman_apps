package com.unionman.quicksetting.interfaces;

import android.util.Log;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSystemSetting;
import com.unionman.quicksetting.util.Constant;

/**
 * The interface of system settings
 * 
 * @author huyq
 * 
 */
public class SystemSettingInterface {

    private static final String TAG = "SystemSettingInterface";

    /**
     * get instance of System Setting
     * 
     * @return
     */
    public static CusSystemSetting getSystemSettingManager() {
        return UmtvManager.getInstance().getSystemSetting();
    }

    /**
     * enableScreenBlue
     * 
     * @param onOff
     * @return
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
     * get ScreenBlueEnable
     * 
     * @return
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
     * 
     * @return
     */
    public static int restoreDefault() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "restoreDefault() begin");
        }

        int value = getSystemSettingManager().restoreDefault(4);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "restoreDefault() end value = " + value);
        }
        return value;
    }
}
