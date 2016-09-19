package com.unionman.quicksetting.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.Log;

/**
 * the class of util
 * 
 * @author huyq
 * 
 */
public class Util {
    private static final String TAG = "Util";

    /**
     * get index of Parameters from array
     * 
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int n = 0;

        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                n = i;
                return n;
            }
        }
        return n;
    }

    /**
     * create array of parameters
     * 
     * @param arrays
     * @return array of Parameters
     */
    public static int[] createArrayOfParameters(int[][] arrays) {
        int[] n = new int[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "createArrayOfParameters=" + i);
            }
            n[i] = arrays[i][1];
        }
        return n;
    }

    /**
     * Whether ATV or DTV in the operation of the current
     * 
     * @param activity
     * @return
     */
    public static boolean IsATVOrDTVRunning(Context activity) {
        ActivityManager am = (ActivityManager) activity
                .getSystemService(Activity.ACTIVITY_SERVICE);
        // ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        RunningTaskInfo info = am.getRunningTasks(1).get(0);
        String pkgString = info.topActivity.getPackageName();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "info.topActivity.getPackageName() = " + pkgString);
        }
        if (pkgString != null
                && (pkgString.equals(Constant.PACKAGE_ATV) || pkgString
                        .equals(Constant.PACKAGE_DTV))) {
            return true;
        }
        return false;
    }
}
