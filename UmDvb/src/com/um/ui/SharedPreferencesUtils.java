package com.um.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 14-11-20.
 */
public class SharedPreferencesUtils {
    public static void setPreferencesBoolean(Context context, String preferencesTag, String preferencesKey, boolean value, int mode) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesTag, mode);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(preferencesKey, value);
        editor.commit();
    }

    public static boolean getPreferencesBoolean(Context context, String preferencesTag, String preferencesKey, boolean defvaule, int mode) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesTag, mode);
        return preferences.getBoolean(preferencesKey, defvaule);
    }
}
