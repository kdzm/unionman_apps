package com.unionman.settingwizard.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/11/18.
 */
public class CityDvbConfigContent {
    public static final String PACKAGE_NAME = "package_name";
    public static final String PACKAGE_PATH = "package_path";

    private int mainFreq;
    private String configFilePath = "";
    private String province = "";
    private String state = "";
    private String city = "";
    private String cityCode = "";
    private List<String> caSupport = new ArrayList<String>();
    private List<String> packagesName = new ArrayList<String>();
    private List<String> packagesPath = new ArrayList<String>();

    public int getMainFreq() {
        return mainFreq;
    }

    public void setMainFreq(int mainFreq) {
        this.mainFreq = mainFreq;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public List<String> getCaSupport() {
        return caSupport;
    }

    public void setCaSupport(List<String> caSupport) {
        this.caSupport = caSupport;
    }

    public List<String> getPackagesName() {
        return packagesName;
    }

    public void setPackagesName(List<String> packagesName) {
        this.packagesName = packagesName;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getPackagesPath() {
        return packagesPath;
    }

    public void setPackagesPath(List<String> packagesPath) {
        this.packagesPath = packagesPath;
    }
}
