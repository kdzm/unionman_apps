package com.um.launcher.data;

/**
 * Created by hjian on 2015/4/3.
 */
public class PosterInfo {
    private String imageUrl;
    private String appKey;
    private String type;
    private String name;

    public PosterInfo(String imageUrl, String appKey, String type, String name) {
        this.imageUrl = imageUrl;
        this.appKey = appKey;
        this.type = type;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
