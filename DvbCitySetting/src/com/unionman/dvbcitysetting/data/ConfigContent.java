package com.unionman.dvbcitysetting.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/11/18.
 */
public class ConfigContent {
    private int mainFreq;
    private String configFilePath = "";
    private String province = "";
    private String state = "";
    private String city = "";
    private String cityCode = "";
    private List<String> caSupport = new ArrayList<String>();
    private List<String> packageName = new ArrayList<String>();
    private List<String> packagePath = new ArrayList<String>();
    private String caSupportStr = "";
    
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

    public void setCaSupportStr(String str) {
    	this.caSupportStr = str;
    }
    
    public String getCaSupportStr() {
    	return this.caSupportStr;
    }
    
    public int getMainFreq() {
        return mainFreq;
    }

    public void setMainFreq(int mainFreq) {
        this.mainFreq = mainFreq;
    }

    public List<String> getPackageName() {
        return packageName;
    }

    public void setPackageName(List<String> packageName) {
        this.packageName = packageName;
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
        return packagePath;
    }

    public void setPackagesPath(List<String> packagePath) {
        this.packagePath = packagePath;
    }
}
