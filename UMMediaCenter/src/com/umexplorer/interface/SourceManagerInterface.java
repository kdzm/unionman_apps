package com.umexplorer.interfaces;

import java.util.ArrayList;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.vo.RectInfo;


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
     * getSignalStatus
     */
    public static int getSignalStatus() {
        if (true) {
            Log.d(TAG, "getSignalStatus() begin");
        }

        int value = getSourceManager().getSignalStatus();

        if (true) {
            Log.d(TAG, "getSignalStatus() end value = " + value);
        }
        return value;
    }

	
    public static int deselectSource(int srcId, boolean bDestroy) {
        if (true) {
            Log.d(TAG, "deselectSource(int srcId = " + srcId
                    + ", boolean bDestroy = " + bDestroy + ")  begin");
        }

        int value = getSourceManager().deselectSource(srcId, bDestroy);

        if (true) {
            Log.d(TAG, "deselectSource(int srcId = " + srcId
                    + ", boolean bDestroy =" + bDestroy + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * set enable dualdisplay
     */
    public static int enableDualDisplay(boolean enable) {
        if (true) {
            Log.d(TAG, "enableDualDisplay(boolean enable = " + enable
                    + ") begin");
        }

        int value = getSourceManager().enableDualDisplay(enable);

        if (true) {
            Log.d(TAG, "enableDualDisplay(boolean enable = " + enable
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * get avail source list
     */
    public static ArrayList<Integer> getAvailSourceList() {
        if (true) {
            Log.d(TAG, "getAvailSourceList() begin");
        }

        ArrayList<Integer> value = getSourceManager().getAvailSourceList();

        if (true) {
            Log.d(TAG, "getAvailSourceList() end value = " + value);
        }
        return value;
    }

    /**
     * get cursource id
     */
    public static int getCurSourceId() {
        if (true) {
            Log.d(TAG, "getCurSourceId() begin");
        }

        int value = getSourceManager().getCurSourceId(0);

        if (true) {
            Log.d(TAG, "getCurSourceId() end value = " + value);
        }
        return value;
    }

    /**
     * Get current SourceId saved
     */
    public static int getSelectSourceId() {
        if (true) {
            Log.d(TAG, "getSelectSourceId() begin");
        }

        int value = getSourceManager().getSelectSourceId();

        if (true) {
            Log.d(TAG, "getSelectSourceId() end value = " + value);
        }
        return value;
    }

    /**
     * getLastSourceId
     */
    public static int getLastSourceId() {
        if (true) {
            Log.d(TAG, "getLastSourceId() begin");
        }

        int value = getSourceManager().getLastSourceId();

        if (true) {
            Log.d(TAG, "getLastSourceId() end value = " + value);
        }
        return value;
    }

	
    public static int setWindowRect(RectInfo rect, int mainWindow) {

        int res = getSourceManager().setWindowRect(rect, mainWindow);

        Log.d(TAG, "SourceManager  setWindowRect ret = " + res);

        return res;

    }

    /**
     * select source
     */
    public static int selectSource(int srcId, int nMainWindow) {
        if (true) {
            Log.d(TAG, "selectSource(int srcId = " + srcId
                    + ", int nMainWindow =" + nMainWindow + ")  begin");
        }

        int value = getSourceManager().selectSource(srcId, nMainWindow);

        if (true) {
            Log.d(TAG, "selectSource(int srcId = " + srcId
                    + ", int nMainWindow = " + nMainWindow
                    + ")  end value = " + value);
        }
        return value;
    }
    
}

