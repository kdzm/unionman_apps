package com.unionman.dvbcitysetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unionman.dvbcitysetting.data.InstalledCityInfo;
import com.unionman.dvbcitysetting.util.CitySettingHelper;
import com.unionman.dvbcitysetting.util.PreferencesUtils;
import com.unionman.dvbcitysetting.util.StringUtils;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private boolean mLaunchWhenFirstBoot = false;               // 是否需要第一次开机时就启动
    private boolean mFixINstallWhenBootComplete = false;        // 开机是否检测dvb相关apk是否完整，不完整则提示安装
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            int count = PreferencesUtils.getInt(context, "boot_times", 0);
            if (mLaunchWhenFirstBoot && count == 0) {
                Intent mainActivityIntent = new Intent(context, CitySettingActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);

                PreferencesUtils.putInt(context, "boot_times", 1);
            } else if (mFixINstallWhenBootComplete){
                fixIfInstallNotComplete(context);
            }
        }
    }

    private void fixIfInstallNotComplete(Context context) {
        String installedConfig = PreferencesUtils.getString(context, CitySettingHelper.INSTALLED_CITY_CONFIG_FILE, "");
        String packageStr = PreferencesUtils.getString(context, CitySettingHelper.LAST_INSTALLED_PACKAGE, "");
        String installingConfig = PreferencesUtils.getString(context, CitySettingHelper.INSTALLING_CITY_CONFIG_FILE, "");

        InstalledCityInfo installedCityInfo = new InstalledCityInfo();
        installedCityInfo.setInstalledConfigFilePath(installedConfig);
        installedCityInfo.setInstallingConfigFilePath(installingConfig);
        installedCityInfo.setInstalledPackages(StringUtils.stringToList(packageStr, CitySettingHelper.PACKAGES_SEPARATOR));

        CityDvbInstaller cityDvbInstaller = new CityDvbInstaller(context, null, installedCityInfo);
        Intent intent = new Intent(context, CitySettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!cityDvbInstaller.isFinish()) {
            intent.putExtra(CitySettingHelper.KEY_LAUNCH_TYPE, CityDvbInstaller.TYPE_RE_INSTALL);
            context.startActivity(intent);
        } else if (!cityDvbInstaller.isComplete()) {
            intent.putExtra(CitySettingHelper.KEY_LAUNCH_TYPE, CityDvbInstaller.TYPE_FIX_INSTALL);
            context.startActivity(intent);
        }
    }
}
