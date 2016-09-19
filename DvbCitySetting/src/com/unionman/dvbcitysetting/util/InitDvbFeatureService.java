package com.unionman.dvbcitysetting.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.util.Log;

import com.unionman.dvbcitysetting.CityDvbInstaller;
import com.unionman.dvbcitysetting.data.ConfigContent;
import com.unionman.dvbcitysetting.data.InstalledCityInfo;
import com.unionman.dvbstorage.ContentSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 安装过程会操作一下3个变量
 * CitySettingHelper.INSTALLING_CITY_CONFIG_FILE
 * CitySettingHelper.INSTALLED_CITY_CONFIG_FILE
 * CitySettingHelper.LAST_INSTALLED_PACKAGE
 *
 * 开始安装时保存config.xml到INSTALLED_CITY_CONFIG_FILE，
 * 安装完成后，保存成功安装的城市名到INSTALLED_CITY_FULL_NAME，已安装的包名到LAST_INSTALLED_PACKAGE
 *
 * Created by hjian on 2014/11/4.
 */
public class InitDvbFeatureService extends Service{
    private final String TAG = "InitDvbFeatureService";
    private InstallReceiver installReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        installReceiver = new InstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(installReceiver, filter);
        super.onCreate();
    }

    private CityDvbInstaller cityDvbInstaller;
    private ConfigContent configContent;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String configFile = intent.getStringExtra(CitySettingHelper.CURRENT_CITY_CONFIG_FILE);

            if (FileUtils.isFileExist(configFile)) {
                PreferencesUtils.putString(this, CitySettingHelper.INSTALLING_CITY_CONFIG_FILE, configFile);
                try {
                    configContent = CitySettingHelper.getConfigFileContent(new File(configFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<String> apkPath = configContent.getPackagesPath();
                List<String> packageList = new ArrayList<String>();
                for (String str : apkPath) {
                    PackageInfo packageInfo = PackageUtils.getPackageInfoByFile(this, str);
                    if (packageInfo != null) {
                        packageList.add(packageInfo.packageName);
                    }
                }
                configContent.setPackageName(packageList);

                String installedConfig = PreferencesUtils.getString(this, CitySettingHelper.INSTALLED_CITY_CONFIG_FILE);
                String packageStr = PreferencesUtils.getString(this, CitySettingHelper.LAST_INSTALLED_PACKAGE, "");
                String installingConfig = PreferencesUtils.getString(this, CitySettingHelper.INSTALLING_CITY_CONFIG_FILE, "");

                InstalledCityInfo installedCityInfo = new InstalledCityInfo();
                installedCityInfo.setInstalledConfigFilePath(installedConfig);
                installedCityInfo.setInstallingConfigFilePath(installingConfig);
                installedCityInfo.setInstalledPackages(StringUtils.stringToList(packageStr, CitySettingHelper.PACKAGES_SEPARATOR));
                cityDvbInstaller = new CityDvbInstaller(this, configContent, installedCityInfo);

                if (apkPath.size() > 0) {
                    installCityDvb(apkPath);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void installCityDvb(List<String> apkPath) {
        Log.d(TAG, ", onStartCommand, apkFiles: " + apkPath.size());
        cityDvbInstaller.install(new CityDvbInstaller.OnCityDvbInstallListener() {
            @Override
            public void onInstallSuccess() {
                saveStateInfo();

                cleanProgsDb();
                
                int mainFreq = configContent.getMainFreq();
                setMainFreq(mainFreq);
            }

            @Override
            public void onInstallFailed() {
//                saveStateInfo();
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(installReceiver);
        super.onDestroy();
    }

    private void setMainFreq(int mainFreq) {
        PropertyUtils.setInt(CitySettingHelper.KEY_MAIN_FREQ, mainFreq);
        PreferencesUtils.putBoolean(this, "dvb_installed_finish", true);
//        DvbUtils.setMainFreq(DvbUtils.UM_TRANS_SYS_TYPE_CAB, mainFreq);
//        DvbUtils.setMainFreq(DvbUtils.UM_TRANS_SYS_TYPE_TER, mainFreq);
    }

    private void cleanProgsDb() {
    	Log.v(TAG, "PROGRESET: cleanProgsDb()");
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(ContentSchema.ProgsTable.CONTENT_URI, null, null);
        contentResolver.delete(ContentSchema.CategoryTable.CONTENT_URI, null, null);
        contentResolver.delete(ContentSchema.CategoryProgrmaTable.CONTENT_URI, null, null);
        
        Log.v(TAG, "PROGRESET: ready to rm dvb data...");
        FileUtils.deleteFile("/data/data/umdb.dat");
        FileUtils.deleteFile("/data/data/umdb_sysdata.dat");
        FileUtils.deleteFile("/data/data/umdb_sysdata.dat-bak");
        FileUtils.deleteFile("/data/data/umdb_sysdata_ter.dat");
        FileUtils.deleteFile("/data/data/umdb_sysdata_ter.dat-bak");
        Log.v(TAG, "PROGRESET: rm dvb data done.");
    }

    private void saveStateInfo() {
        // copy config.xml for CA
        String dstPath = "/data/data/" + CitySettingHelper.CONFIG_FILE;
        FileUtils.changeFileMod(dstPath, "777");
        FileUtils.copyFile(configContent.getConfigFilePath(), dstPath);
        FileUtils.changeFileMod(dstPath, "777");

        List<String> packages = configContent.getPackageName();
        String packageStr = StringUtils.listToString(packages, CitySettingHelper.PACKAGES_SEPARATOR);
        PreferencesUtils.putString(InitDvbFeatureService.this, CitySettingHelper.INSTALLED_CITY_CONFIG_FILE,
                configContent.getConfigFilePath());
        PreferencesUtils.putString(InitDvbFeatureService.this, CitySettingHelper.LAST_INSTALLED_PACKAGE, packageStr);

        PropertyUtils.setString(CitySettingHelper.DVB_CITY_NAME, configContent.getCityCode());
        PropertyUtils.setString(CitySettingHelper.DVB_LOCAL_CAS_TYPES, configContent.getCaSupportStr());
        PropertyUtils.setString(CitySettingHelper.DVB_CAS_TYPE, "");
        
    }

    class InstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                if ("com.unionman.cityfeature".equals(packageName)) {
                	Log.v(TAG, "PROGRESET: com.unionman.cityfeature is installed, readyt to start service: com.unionman.cityfeature.FEATURE_INIT");
                    Intent serviceIntent = new Intent("com.unionman.cityfeature.FEATURE_INIT");
                    context.startService(serviceIntent);
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {

            }
        }
    }
}
