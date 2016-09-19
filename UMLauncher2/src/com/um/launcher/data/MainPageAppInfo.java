package com.um.launcher.data;

/**
 * Created by hjian on 2015/3/28.
 */
public class MainPageAppInfo{
    private long installTime;
    private String packageName;

    public MainPageAppInfo() {
    }

    public MainPageAppInfo(long installTime, String packageName) {
        this.installTime = installTime;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }
}
