
package com.um.gallery3d.list;

/**
 * common class. used to get data.
 */
public class Common {
    private static String TAG = "Common";

    /* Is data ready */
    public static boolean isLoadSuccess = false;

    /* sort mode */
    public static int sortCount = 1;

    public static boolean isLoadSuccess() {
        return isLoadSuccess;
    }

    public static void setLoadSuccess(boolean isLoadSuccess) {
        Common.isLoadSuccess = isLoadSuccess;
    }

    public static final int FROMFILEM = 1;

    public static final String ACTION = "com.um.gallery3d.list.listservice";
    
    public static int imgListSize=0;
    
    public static int imgCurrPos=-1;
    public static boolean isLoadpos = false;
}
