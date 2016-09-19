package com.unionman.dvbcitysetting.data;

import java.util.List;

/**
 * Created by Administrator on 2014/10/31.
 */
public class City extends CityBase{
    private List<String> packages;

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
}
