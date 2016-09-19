package cn.com.unionman.umtvsystemserver;

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
     * deSelect source by srcId and bDestroy
     *
     * @param srcId
     * @param bDestroy
     * @return
     */
    public static int deselectSource(int srcId, boolean bDestroy) {
        int value = getSourceManager().deselectSource(srcId, bDestroy);
        return value;
    }

    /**
     * enableDualDisplay
     */
    public static int enableDualDisplay(boolean enable) {
        int value = getSourceManager().enableDualDisplay(enable);
        return value;
    }

    /**
     * getAvailSourceList
     */
    public static ArrayList<Integer> getAvailSourceList() {
        ArrayList<Integer> value = getSourceManager().getAvailSourceList();
        return value;
    }

    /**
     * getCurSourceId
     */
    public static int getCurSourceId() {

        int value = getSourceManager().getCurSourceId(0);
        return value;
    }

    /**
     * getSelectSourceId
     */
    public static int getSelectSourceId() {

        int value = getSourceManager().getSelectSourceId();
        return value;
    }

    /**
     * getSignalStatus
     */
    public static int getSignalStatus() {

        int value = getSourceManager().getSignalStatus();
        return value;
    }

    /**
     * getSourceList
     */
    public static ArrayList<Integer> getSourceList() {

        ArrayList<Integer> value = getSourceManager().getSourceList();
        return value;
    }

    /**
     * getTimingInfo
     */
    public static TimingInfo getTimingInfo() {

        TimingInfo value = getSourceManager().getTimingInfo();
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

        boolean value = getSourceManager().isDVIMode();
        return value;
    }

    /**
     * selectSource
     */
    public static int selectSource(int srcId, int nWindow) {

        int value = getSourceManager().selectSource(srcId, nWindow);

        return value;
    }

    /**
     * Set the window said displayed in the left or right, if the two terminal
     * display this interface must be set
     */
    public static int setDisplayOnLeft(boolean left, int nWindow) {

        int value = getSourceManager().setDisplayOnLeft(left, nWindow);

        return value;
    }

    /**
     * Set the focus on the main window or a child window
     */
    public static int setFocusWindow(int nWindow) {

        int value = getSourceManager().setFocusWindow(nWindow);

        return value;
    }
}
