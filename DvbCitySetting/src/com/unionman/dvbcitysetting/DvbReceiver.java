package com.unionman.dvbcitysetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.unionman.dvbcitysetting.util.CitySettingHelper;
import com.unionman.dvbcitysetting.util.DvbUtils;
import com.unionman.dvbcitysetting.util.PreferencesUtils;
import com.unionman.dvbcitysetting.util.PropertyUtils;

/**
 * Created by hjian on 2015/3/2.
 */
public class DvbReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DvbReceiver", "getAction: " + intent.getAction());
        boolean dvbInstalledFinish = PreferencesUtils.getBoolean(context, "dvb_installed_finish", false);
        Log.d("DvbReceiver", "dvbInstalledFinish: " + dvbInstalledFinish);
        if ("com.unionman.dvb.ACTION_DVB_NATIVE_PROVIDER_READY".equals(intent.getAction()) && dvbInstalledFinish) {
/*            int mainFreq = PropertyUtils.getInt(CitySettingHelper.KEY_MAIN_FREQ, 227000);
            Log.d("DvbReceiver", "mainFreq: " + mainFreq);
            DvbUtils.setMainFreq(DvbUtils.UM_TRANS_SYS_TYPE_CAB, mainFreq);
            DvbUtils.setMainFreq(DvbUtils.UM_TRANS_SYS_TYPE_TER, mainFreq);

            PreferencesUtils.putBoolean(context, "dvb_installed_finish", true);*/
        }
    }
}
