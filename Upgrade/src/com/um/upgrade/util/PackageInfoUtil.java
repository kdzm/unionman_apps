package com.um.upgrade.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziliang.nong on 14-6-19.
 */
public class PackageInfoUtil {
    private final static String TAG = PackageInfoUtil.class.getSimpleName();
    private final static boolean LOG_EN = true;

    private PackageInfoUtil() {
    }

    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> result = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packagesInfo = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packagesInfo) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            String verName = packageInfo.versionName;
            int verCode = packageInfo.versionCode;
            String packageLabel = pm.getApplicationLabel(appInfo).toString();
            String packageName = packageInfo.packageName;
            result.add(new AppInfo(packageName, packageLabel, verName, verCode));
        }

        return result;
    }

    public static boolean isPackageExist(Context context, String packageName) {
        List<AppInfo> appsInfo = null;

        appsInfo = getInstalledApps(context);
        for (AppInfo appInfo : appsInfo) {
            if (appInfo.getPackageName().equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    public static AppInfo getInstalledApp(Context context, String packageName) {
        List<AppInfo> appsInfo = null;

        appsInfo = getInstalledApps(context);
        for (AppInfo appInfo : appsInfo) {
            if (appInfo.getPackageName().equals(packageName)) {
                return appInfo;
            }
        }

        return null;
    }

    public static AppInfo getUnstalledApp(Context context, String filePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
//            appInfo.sourceDir = filePath;
            appInfo.publicSourceDir = filePath;

            String packageName = packageInfo.applicationInfo.packageName;
            String packageLabel = pm.getApplicationLabel(appInfo).toString();
            String verName = packageInfo.versionName;
            int verCode = packageInfo.versionCode;
            return new AppInfo(packageName, packageLabel, verName, verCode);
        }

        return null;
    }

    public static class AppInfo{
        private String packageName;
        private String packageLabel;
        private String verName;
        private int verCode;

        public AppInfo(String packageName, String packageLabel, String verName, int verCode) {
            this.packageName = packageName;
            this.packageLabel = packageLabel;
            this.verName = verName;
            this.verCode = verCode;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageLabel() {
            return packageLabel;
        }

        public void setPackageLabel(String packageLabel) {
            this.packageLabel = packageLabel;
        }

        public String getVerName() {
            return verName;
        }

        public void setVerName(String verName) {
            this.verName = verName;
        }

        public int getVerCode() {
            return verCode;
        }

        public void setVerCode(int verCode) {
            this.verCode = verCode;
        }
    }
}
