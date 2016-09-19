package com.unionman.dvbcitysetting.util;

import android.os.SystemProperties;

/**
 * Created by hjian on 2014/11/7.
 */
public class PropertyUtils {
    public static void setString(String key, String value){
        SystemProperties.set(key, value);
        FileUtils.sync();
    }

    public static String getString(String key, String defauleValue) {
        return SystemProperties.get(key, defauleValue);
    }

    public static void setInt(String key, int value){
        SystemProperties.set(key, value + "");
        FileUtils.sync();
    }

    public static int getInt(String key, int defauleValue) {
        String value = SystemProperties.get(key, defauleValue + "");
        if (!StringUtils.isBlank(value)) {
            return Integer.parseInt(value);
        }
        return defauleValue;
    }

    public static void setBoolean(String key, boolean value){
        SystemProperties.set(key, value ? "true" : "false");
        FileUtils.sync();
    }

    public static boolean getBoolean(String key, boolean defauleValue) {
        String value = SystemProperties.get(key, defauleValue + "");
        if (!StringUtils.isBlank(value)) {
            return Boolean.getBoolean(value);
        }
        return defauleValue;
    }
}
