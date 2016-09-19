package com.unionman.dvbcitysetting.data;

import java.util.List;

/**
 * Created by Administrator on 2014/11/24.
 */
public class InstalledCityInfo {
    private List<String> installedPackages;
    private String installingConfigFilePath;
    private String installedConfigFilePath;

    public List<String> getInstalledPackages() {
        return installedPackages;
    }

    public void setInstalledPackages(List<String> installedPackages) {
        this.installedPackages = installedPackages;
    }

    public String getInstallingConfigFilePath() {
        return installingConfigFilePath;
    }

    public void setInstallingConfigFilePath(String installingConfigFilePath) {
        this.installingConfigFilePath = installingConfigFilePath;
    }

    public String getInstalledConfigFilePath() {
        return installedConfigFilePath;
    }

    public void setInstalledConfigFilePath(String installedConfigFilePath) {
        this.installedConfigFilePath = installedConfigFilePath;
    }
}
