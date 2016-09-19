
package cn.com.unionman.umtvsetting.umsysteminfo;

import android.util.Log;


import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSystemSetting;

/**
 * the interface of system setting
 *
 * @author huyq
 */
public class SystemSettingInterface {

    public static final String TAG = "SystemSettingInterface";

    /**
     * get instance of SystemSetting
     *
     * @return
     */
    public static CusSystemSetting getSystemSettingManager() {
        return UmtvManager.getInstance().getSystemSetting();
    }

    /**
     * restore default
     */
    public static int restoreDefault() {

            Log.d(TAG, "restoreDefault() begin");


        int value = getSystemSettingManager().restoreDefault(0);


            Log.d(TAG, "restoreDefault() end value = " + value);

        return value;
    }
}
