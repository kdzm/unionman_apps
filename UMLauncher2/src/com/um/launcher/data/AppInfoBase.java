package com.um.launcher.data;

/**
 * Created by hjian on 2015/3/28.
 */
public class AppInfoBase {
    private String packageName;

    public AppInfoBase(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
