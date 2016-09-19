package com.unionman.settingwizard.util;

import android.util.Log;

public class Util {

    /**
     * get index of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int num = 0;

        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i][0] == mode) {
                num = i;
                return num;
            }
        }
        return num;
    }
}
