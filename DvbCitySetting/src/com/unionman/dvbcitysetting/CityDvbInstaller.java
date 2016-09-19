package com.unionman.dvbcitysetting;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;

import com.unionman.dvbcitysetting.data.ConfigContent;
import com.unionman.dvbcitysetting.data.InstalledCityInfo;
import com.unionman.dvbcitysetting.util.CitySettingHelper;
import com.unionman.dvbcitysetting.util.FileUtils;
import com.unionman.dvbcitysetting.util.PackageUtils;
import com.unionman.dvbcitysetting.util.PropertyUtils;
import com.unionman.dvbcitysetting.util.ServiceControl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 注意这里把apk的安装和卸载统称为城市安装，城市安装之前先卸载该城市所有apk
 * Created by hjian on 2014/11/24.
 */
public class CityDvbInstaller {
    private final String TAG = "CityDvbInstaller";

    public static final int TYPE_INSTALL_NEW = 0;       //全新安装
    public static final int TYPE_FIX_INSTALL = 1;       //对应安装某些apk被卸载的情况
    public static final int TYPE_RE_INSTALL = 2;        //对应安装没有完成的情况

    private ConfigContent configContent;
    private Context context;
    private CityDvbInstallTask cityDvbInstallTask;
    private OnCityDvbInstallListener onCityDvbInstallListener;
    private InstalledCityInfo installedCityInfo;

    private ConfigContent installedConfigContent;

    /**
     *
     * @param context
     * @param configContent 当前选择城市对应的config
     * @param installedCityInfo 上次安装时和安装后保存的信息
     */
    public CityDvbInstaller(Context context, ConfigContent configContent, InstalledCityInfo installedCityInfo) {
        this.context = context;
        this.configContent = configContent;
        this.installedCityInfo = installedCityInfo;

        if (FileUtils.isFileExist(installedCityInfo.getInstalledConfigFilePath())) {
            try {
                installedConfigContent = CitySettingHelper.getConfigFileContent(
                        new File(installedCityInfo.getInstalledConfigFilePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 安装是否完成
     * @return
     */
    public boolean isFinish() {
        String installedConfig = installedCityInfo.getInstalledConfigFilePath();
        String installingConfig = installedCityInfo.getInstallingConfigFilePath();
        return installedConfig.equals(installingConfig);
    }

    /**
     * 安装是否完整
     * @return
     */
    public boolean isComplete(){
        return getUninstallFile().isEmpty();
    }

    public List<String> getUninstallFile() {
        List<String> apkList = new ArrayList<String>();
        if (FileUtils.isFileExist(installedCityInfo.getInstalledConfigFilePath())) {
            ConfigContent config;
            try {
                config = CitySettingHelper.getConfigFileContent(new File(installedCityInfo.getInstalledConfigFilePath()));
                apkList = PackageUtils.findUninstallByFiles(context, config.getPackagesPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return apkList;
    }
    
    public List<String> getCurrentConfigApkFiles() {
        List<String> apkList = new ArrayList<String>();
        if (FileUtils.isFileExist(installedCityInfo.getInstalledConfigFilePath())) {
            ConfigContent config;
            try {
                config = CitySettingHelper.getConfigFileContent(new File(installedCityInfo.getInstalledConfigFilePath()));
                apkList = config.getPackagesPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return apkList;
    }

    public void install(final int type, OnCityDvbInstallListener onCityDvbInstallListener) {
        this.onCityDvbInstallListener = onCityDvbInstallListener;
        String selectedCityName = CitySettingHelper.getCityFullName(configContent);

        //如果选择的城市跟已安装的城市不一样，或者虽然一样，但是某些apk没有安装，则需要安装该城市
        String installedCityName = CitySettingHelper.getCityFullName(installedConfigContent);
        if (!selectedCityName.equalsIgnoreCase(installedCityName)
                || !isComplete() || (PropertyUtils.getInt(CitySettingHelper.DVB_INSTALLED, 0) == 0)) {

            sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, InstallStatus.INSTALL_PACKAGE_START);
            ServiceControl serviceControl = new ServiceControl(CitySettingHelper.DVB_PLAYER_SERVICE);
            serviceControl.stopService(new ServiceControl.OnServiceStopListener() {
                @Override
                public void onServiceStop() {
                    cityDvbInstallTask = new CityDvbInstallTask(type);
                    cityDvbInstallTask.executeOnExecutor(Executors.newSingleThreadExecutor());
                }
            });
        } else {
            sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, InstallStatus.CITY_ALREADY_INSTALLED);
        }
    }

    /**
     * 安装城市apk
     */
    public void install(OnCityDvbInstallListener onCityDvbInstallListener){
        boolean isTheSameCity = true;
        if (configContent != null) {
            isTheSameCity = configContent.getConfigFilePath().equals(installedCityInfo.getInstalledConfigFilePath());
        }

        if (isTheSameCity && !isComplete() && isFinish() 
        		&& PropertyUtils.getInt(CitySettingHelper.DVB_INSTALLED, 0) == 0) {
            install(TYPE_FIX_INSTALL, onCityDvbInstallListener);
        } else {
            install(TYPE_INSTALL_NEW, onCityDvbInstallListener);
        }
    }

    private void sendBroadcast(String action, String status) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(InstallStatus.STATUS, status);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private class CityDvbInstallTask  extends AsyncTask<String, Integer, Integer> {
        private int SUCCESS = 1;
        private int FAILED = 0;
        private List<String> apkFiles;
        private List<String> packages;
        private int type;

        private CityDvbInstallTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
        	List<String> allApkFiles = null;
            Log.d(TAG, "com in onPreExecute ");
            
            PropertyUtils.setInt(CitySettingHelper.DVB_INSTALLED, 0);
            
            if(type == TYPE_FIX_INSTALL) {
                packages = new ArrayList<String>();
                allApkFiles = getCurrentConfigApkFiles();
            } else {
//                packages = installedCityInfo.getInstalledPackages();
//                apkFiles = configContent.getPackagesPath();

                packages = new ArrayList<String>();
                allApkFiles = configContent.getPackagesPath();
            }
            
            apkFiles = PackageUtils.findUninstallByFiles(context, allApkFiles);
            
            packages.add("com.unionmcn.cityfeature");
            boolean findFlag = false;
            for (String apkFile : apkFiles) {
            	if (apkFile.contains("CityFeature.apk")) {
            		findFlag = true;
            		break;
            	}
            }
            
            if (!findFlag) {
            	for (String apkFile : allApkFiles) {
            		if (apkFile.contains("CityFeature.apk")) {
            			apkFiles.add(apkFile);
                		break;
                	}
            	}
            }
            
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... p) {
            Log.d(TAG, "com in doInBackground ");
            int ret = SUCCESS;
            
            int worksCount = packages.size() + apkFiles.size();
            for(int i = 0; i < packages.size(); i++) {
                Log.d(TAG, "====packageName" + packages.get(i));
                int result = PackageUtils.uninstall(context, packages.get(i));
                publishProgress(i * 100 / worksCount);

//                if (ret != PackageUtils.DELETE_SUCCEEDED) {
//                    ret = FAILED;
//                }
//
//                ret &= result;
            }

            for(int i = 0; i < apkFiles.size(); i++) {
                Log.d(TAG, "====apkPath" + apkFiles.get(i));
                int result = PackageUtils.install(context, apkFiles.get(i));
                publishProgress((i + packages.size()) * 100 / worksCount);

                if (ret != PackageUtils.INSTALL_SUCCEEDED) {
                    ret = FAILED;
                }

                ret &= result;
            }
            
            Log.d(TAG, "leave in doInBackground");
            return ret;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == SUCCESS) {
                onCityDvbInstallListener.onInstallSuccess();
                PropertyUtils.setInt(CitySettingHelper.DVB_INSTALLED, 1);
            } else {
                onCityDvbInstallListener.onInstallFailed();
            }

            Log.d(TAG, "=====onPostExecute, All install success: " + (integer == SUCCESS ? "true" : "false"));
            sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, InstallStatus.INSTALL_PACKAGE_SUCCESS);

            ServiceControl serviceControl = new ServiceControl(CitySettingHelper.DVB_PLAYER_SERVICE);

            if (1 == PropertyUtils.getInt(CitySettingHelper.DVB_ENABLE, 0)) {
	            serviceControl.startService(new ServiceControl.OnServiceStartListener() {
	                @Override
	                public void onServiceStart() {
	                    Log.d(TAG, "===========onServiceStart: ");
	                    sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, InstallStatus.START_DVBSERVER_SUCCESS);
	                }
	            });
            } else {
            	sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, InstallStatus.START_DVBSERVER_SUCCESS);
            }

            for (String packageName : packages) {
                PackageUtils.stopPackage(packageName, false);
            }
            
            FileUtils.sync();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "=====InstallTask, onProgressUpdate: values:" + values[0]);
            sendBroadcast(InstallStatus.ACTION_CITY_INSTALL, values[0] + "/100");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "==onCancelled");
        }
    }

    public interface OnCityDvbInstallListener{
        void onInstallSuccess();
        void onInstallFailed();
    }
}
