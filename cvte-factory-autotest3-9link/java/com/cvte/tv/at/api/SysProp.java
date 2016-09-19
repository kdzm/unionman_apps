package com.cvte.tv.at.api;

import java.lang.reflect.Method;

/**
 * Created by User on 2015/6/30.
 */
public class SysProp {

    private static final String TAG = "MySystemProperties";

    // String SystemProperties.get(String key){}
    public static void set(String key, String val) {
        init();

        try {
            mSetMethod.invoke(mClassType, key, val);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // String SystemProperties.get(String key){}
    public static String get(String key, String def) {
        init();

        String value = null;

        try {
            value = (String) mGetMethod.invoke(mClassType, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    //int SystemProperties.get(String key, int def){}
    public static int getInt(String key, int def) {
        init();

        int value = def;
        try {
            Integer v = (Integer) mGetIntMethod.invoke(mClassType, key, def);
            value = v.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int getSdkVersion() {
        return getInt("ro.build.version.sdk", -1);
    }

    //-------------------------------------------------------------------
    private static Class<?> mClassType = null;
    private static Method mSetMethod = null;
    private static Method mGetMethod = null;
    private static Method mGetIntMethod = null;

    private static void init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");

                mSetMethod = mClassType.getDeclaredMethod("set", String.class, String.class);
                mGetMethod = mClassType.getDeclaredMethod("get", String.class, String.class);
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", String.class, int.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}