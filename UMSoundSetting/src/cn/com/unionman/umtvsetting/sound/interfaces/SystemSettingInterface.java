package cn.com.unionman.umtvsetting.sound.interfaces;

import android.util.Log;

import cn.com.unionman.umtvsetting.sound.util.Constant;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSystemSetting;

/**
 * interface of system setting
 *
 * @author huyq
 *
 */
public class SystemSettingInterface {
    public static final String TAG = "SystemSettingInterface";

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

        int value = getSystemSettingManager().restoreDefault(2);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "restoreDefault() end value = " + value);
        }
        return value;
    }
}
