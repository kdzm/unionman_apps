package com.unionman.settingwizard.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Xml;

import com.unionman.settingwizard.data.CityDvbConfigContent;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.pm.IPackageManager;
import android.os.ServiceManager;
/**
 * Created by hjian on 2015/2/2.
 */
public class CityDvbUtils {
    private static final String TAG = "PackageUtils";
    private final static String NEED_RUN_DVB_CITY_SETTING = "enable_dvb_city_setting";
    public final static String DVB_CITY_SETTING_PREFERENCE_NAME = "DvbCitySetting";
    private final static String PACKAGES_SEPARATOR = ";";
    private final static String INSTALLING_CITY_CONFIG_FILE = "installing_city_config_file";
    private final static String INSTALLED_CITY_CONFIG_FILE = "installed_city_config_file";
    private final static String LAST_INSTALLED_PACKAGE = "last_installed_package";
    private final static String DEFAULT_CITY_DVB_CONFIG = "/system/vendor/dvb/install_packages/default_city/config.xml";
    public final static String DVB_ENABLE = "persist.sys.dvb.enabled";
    public final static String DVB_INSTALLED = "persist.sys.dvb.installed";
    public final static String DVB_CAS_TYPE = "persist.sys.dvb.cas.type";
    public final static String DVB_PLAYER_SERVICE = "dvbserver";
    public final static String DVB_CITY_NAME = "persist.sys.dvb.cas.area";

    private static void putInstallInfo(Context context, List<String> packages) {
        Context friendContext = null;
        try {
            friendContext = context.createPackageContext("com.unionman.dvbcitysetting", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (friendContext != null) {
            PreferencesUtils.putRemoteString(friendContext, DVB_CITY_SETTING_PREFERENCE_NAME,
                    INSTALLING_CITY_CONFIG_FILE, DEFAULT_CITY_DVB_CONFIG);
            PreferencesUtils.putRemoteString(friendContext, DVB_CITY_SETTING_PREFERENCE_NAME,
                    INSTALLED_CITY_CONFIG_FILE, DEFAULT_CITY_DVB_CONFIG);

            String packageStr = listToString(packages, PACKAGES_SEPARATOR);
            PreferencesUtils.putRemoteString(friendContext, DVB_CITY_SETTING_PREFERENCE_NAME,
                    LAST_INSTALLED_PACKAGE, packageStr);
        }
    }

    public static void initDvbPackages(Context context, boolean stop) {
        CityDvbConfigContent configContent = null;
        try {
            configContent = getConfigFileContent(new File(DEFAULT_CITY_DVB_CONFIG));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (configContent != null) {
            List<String> packages = configContent.getPackagesName();
            if (checkPackagesInstalled(context, packages)) {
                setPackagesStoppedState(context, packages, stop);

                putInstallInfo(context, packages);

                Intent serviceIntent = new Intent("com.unionman.cityfeature.FEATURE_INIT");
                context.startService(serviceIntent);

                PropertyUtils.setInt(DVB_INSTALLED, 1);
                PropertyUtils.setInt(DVB_CAS_TYPE, -1);
                PropertyUtils.setString(DVB_CITY_NAME, configContent.getCityCode());
                ServiceControl serviceControl = new ServiceControl(DVB_PLAYER_SERVICE);

                if (1 == PropertyUtils.getInt(DVB_ENABLE, 0)) {
                    serviceControl.startService(new ServiceControl.OnServiceStartListener() {
                        @Override
                        public void onServiceStart() {
                            Log.d(TAG, "===========onServiceStart: ");
                        }
                    });
                }

                PreferencesUtils.putBoolean(context, "has_pre_install", true);
            }
        }
    }

    private static boolean checkPackagesInstalled(Context context, List<String> packages) {
        boolean ret = true;
        for (String packageName : packages) {
            ret &= isPackageInstalled(context, packageName);
        }

        return ret && packages.size() > 0;
    }

    private static void setPackagesStoppedState(Context context, List<String> packages, boolean stop) {
        for (String packageName : packages) {
            if (isPackageInstalled(context, packageName)) {
                setPackageStoppedState(packageName, stop);
            }
        }
    }

    /**
     * /data/system/users/0/package-restrictions.xml 文件有一个字段用于描述一个apk 是否处于stop状态（新安装apk为stop）
     *  stop状态不能接受需要特定权限的广播，如：开机广播。
     *
     *  该方法可以将apk的状态置为非stop状态，从而可以在不用手动启动apk即可接受开机广播等
     * @param packageName 包名
     * @param stop 是否置为stop状态
     */
    private static void setPackageStoppedState(String packageName, boolean stop) {
        IPackageManager mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        try {
            mPm.setPackageStoppedState(packageName, stop, 0);
        } catch (Exception e) {
            Log.e("TAG", "Utils.setPackageStoppedState: " + "Is Package Manager running?");
        }
    }

    public static CityDvbConfigContent getConfigFileContent(File configFile) throws Exception{
        CityDvbConfigContent configContent = null;
        List<String> packages = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new FileInputStream(configFile), "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    String str = parser.getName();
                    if ("cityconfig".equals(str)) {
                        configContent = new CityDvbConfigContent();
                        packages = new ArrayList<String>();
                    } else if ("city".equals(str)) {
                        if (configContent != null) {
                            configContent.setProvince(parser.getAttributeValue(null, "province"));
                            configContent.setState(parser.getAttributeValue(null, "state"));
                            String cityName = parser.getAttributeValue(null, "city");
                            if (StringUtils.isBlank(cityName)) {
                                configContent.setCity("- - -");
                            } else {
                                configContent.setCity(cityName);
                            }
                            configContent.setCityCode(parser.getAttributeValue(null, "code"));
                        }
                    } else if("feature".equals(str)) {
                        assert configContent != null;
                        configContent.setMainFreq(Integer.parseInt(parser.getAttributeValue(null, "mainfreq")));
                        String caSupport = parser.getAttributeValue(null, "casupport");
                        String[] _caSupport = caSupport.split(";");
                        List<String> caTypes = new ArrayList<String>();
                        Collections.addAll(caTypes, _caSupport);
                        configContent.setCaSupport(caTypes);
                    }else if ("package".equals(str)) {
                        String value = parser.getAttributeValue(0);
                        if (!value.startsWith("/")) {
                            value = configFile.getParent() + File.separator + FileUtils.getFileName(value);
                        }
                        assert packages != null;
                        packages.add(value);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    String endTag = parser.getName();
                    if ("cityconfig".equals(endTag)) {
                        assert configContent != null;
                        configContent.setPackagesPath(packages);
                        configContent.setConfigFilePath(configFile.getPath());
                        return configContent;
                    }
                    break;
                default:
                    break;
            }
            event = parser.next();
        }

        return null;
    }

    private static String getFileName(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return "";
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    private static PackageInfo getPackageInfoByFile(Context context, String apkFilePath) {
        PackageManager pm = context.getPackageManager();

        return pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
    }

    private static String getPackageNameByFile(Context context, String apkFilePath) {
        PackageInfo packageInfo = getPackageInfoByFile(context, apkFilePath);
        if (packageInfo != null) {
            return getPackageInfoByFile(context, apkFilePath).packageName;
        }

        return "";
    }

    private static boolean isPackageInstalled(Context context, String packageName){
        return getPackageInfo(context, packageName) != null;
    }

    private static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        return packageInfo;
    }

    private static String listToString(List<String> stringList, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringList) {
            stringBuilder.append(str).append(separator);
        }
        return stringBuilder.toString();
    }
}
