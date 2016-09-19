package com.um.atv.interfaces;

import java.util.ArrayList;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.um.atv.util.Constant;

/**
 * interface of SourceManager
 *
 * @author huyq
 *
 */
public class SourceManagerInterface {
    public static final String TAG = "SourceManagerInterface";

    /**
     * get instance of SourceManager
     *
     * @return
     */
    public static CusSourceManager getSourceManager() {
        return UmtvManager.getInstance().getSourceManager();
    }

    /**
     * deSelect source by srcId and bDestroy
     *
     * @param srcId
     * @param bDestroy
     * @return
     */
    public static int deselectSource(int srcId, boolean bDestroy) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "deselectSource(int srcId = " + srcId
                    + ", boolean bDestroy = " + bDestroy + ")  begin");
        }

        int value = getSourceManager().deselectSource(srcId, bDestroy);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "deselectSource(int srcId = " + srcId
                    + ", boolean bDestroy =" + bDestroy + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * enableDualDisplay
     */
    public static int enableDualDisplay(boolean enable) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDualDisplay(boolean enable = " + enable
                    + ") begin");
        }

        int value = getSourceManager().enableDualDisplay(enable);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDualDisplay(boolean enable = " + enable
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * getAvailSourceList
     */
    public static ArrayList<Integer> getAvailSourceList() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailSourceList() begin");
        }

        ArrayList<Integer> value = getSourceManager().getAvailSourceList();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailSourceList() end value = " + value);
        }
        return value;
    }

    /**
     * getCurSourceId
     */
    public static int getCurSourceId() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurSourceId() begin");
        }

        int value = getSourceManager().getCurSourceId(0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurSourceId() end value = " + value);
        }
        return value;
    }

    /**
     * getSelectSourceId
     */
    public static int getSelectSourceId() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSelectSourceId() begin");
        }

        int value = getSourceManager().getSelectSourceId();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSelectSourceId() end value = " + value);
        }
        return value;
    }

    /**
     * getSignalStatus
     */
    public static int getSignalStatus() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSignalStatus() begin");
        }

        int value = getSourceManager().getSignalStatus();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSignalStatus() end value = " + value);
        }
        return value;
    }

    /**
     * getSourceList
     */
    public static ArrayList<Integer> getSourceList() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSourceList() begin");
        }

        ArrayList<Integer> value = getSourceManager().getSourceList();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSourceList() end value = " + value);
        }
        return value;
    }

    /**
     * getTimingInfo
     */
    public static TimingInfo getTimingInfo() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getTimingInfo() begin");
        }

        TimingInfo value = getSourceManager().getTimingInfo();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getTimingInfo() end value = " + value);
        }
        return value;
    }


    public static int setWindowRect(RectInfo rect, int mainWindow){

        int res = getSourceManager().setWindowRect(rect, mainWindow);

        Log.d(TAG,"SourceManager  setWindowRect ret = " + res);

        return res;


    }


    /**
     * isDVIMode
     */
    public static boolean isDVIMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDVIMode() begin");
        }

        boolean value = getSourceManager().isDVIMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDVIMode() end value = " + value);
        }
        return value;
    }

    /**
     * selectSource
     */
    public static int selectSource(int srcId, int nWindow) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectSource(int srcId = " + srcId + ", int nWindow ="
                    + nWindow + ")  begin");
        }

        int value = getSourceManager().selectSource(srcId, nWindow);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectSource(int srcId = " + srcId + ", int nWindow = "
                    + nWindow + ")  end value = " + value);
        }
        return value;
    }

    /**
     * Set the window said displayed in the left or right, if the two terminal
     * display this interface must be set
     */
    public static int setDisplayOnLeft(boolean left, int nWindow) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDisplayOnLeft(boolean left = " + left
                    + ", int nWindow = " + nWindow + ")  begin");
        }

        int value = getSourceManager().setDisplayOnLeft(left, nWindow);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDisplayOnLeft(boolean left = " + left
                    + ", int nWindow = " + nWindow + ")  end value = " + value);
        }
        return value;
    }

    /**
     * Set the focus on the main window or a child window
     */
    public static int setFocusWindow(int nWindow) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFocusWindow(int nWindow = " + nWindow + ")  begin");
        }

        int value = getSourceManager().setFocusWindow(nWindow);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFocusWindow(int nWindow = " + nWindow
                    + ")  end value = " + value);
        }
        return value;
    }
}
