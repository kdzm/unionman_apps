package com.source.interfaces;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusVideo3D;
import com.source.util.Constant;

/**
 * interface of Video3D
 *
 * @author huyq
 *
 */
public class Video3DInterface {
    public static final String TAG = "Video3DInterface";

    /**
     * get instance of Video3D
     *
     * @return
     */
    public static CusVideo3D getVideo3DManager() {
        return UmtvManager.getInstance().getVideo3D();
    }

    /**
     * get3dMode
     */
    public static int get3dMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "get3dMode() begin");
        }

        int value = getVideo3DManager().get3dMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "get3dMode() end value = " + value);
        }
        return value;
    }

    /**
     * get3dto2d
     */
    public static boolean get3dto2d() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "get3dto2d() begin");
        }

        boolean value = getVideo3DManager().get3dto2d();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "get3dto2d() end value = " + value);
        }
        return value;
    }

    /**
     * getDepth
     */
    public static int getDepth() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDepth() begin");
        }

        int value = getVideo3DManager().getDepth();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDepth() end value = " + value);
        }
        return value;
    }

    /**
     * Get around eyes switch
     */
    public static boolean getSwitchLR() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSwitchLR() begin");
        }

        boolean value = getVideo3DManager().getSwitchLR();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSwitchLR() end value = " + value);
        }
        return value;
    }

    /**
     * getView
     */
    public static int getView() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getView() begin");
        }

        int value = getVideo3DManager().getView();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getView() end value = " + value);
        }
        return value;
    }

    /**
     * set3dMode
     */
    public static int set3dMode(int mode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "set3dMode(int mode = " + mode + ") begin");
        }

        int value = getVideo3DManager().set3dMode(mode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "set3dMode(int mode = " + mode + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * set3dto2d
     */
    public static int set3dto2d(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "set3dto2d(boolean onOff = " + onOff + ") begin");
        }

        int value = getVideo3DManager().set3dto2d(onOff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "set3dto2d(boolean onOff = " + onOff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setDepth
     */
    public static int setDepth(int depth) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDepth(int depth = " + depth + ") begin");
        }

        int value = getVideo3DManager().setDepth(depth);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDepth(int depth = " + depth + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setView
     */
    public static int setView(int view) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setView(int view = " + view + ") begin");
        }

        int value = getVideo3DManager().setView(view);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setView(int view = " + view + ") end value = " + value);
        }
        return value;
    }

    /**
     * Set the left and right eye switch
     */
    public static int switchLR(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "switchLR(boolean onOff = " + onOff + ") begin");
        }

        int value = getVideo3DManager().switchLR(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "switchLR(boolean onOff = " + onOff + ") end value = "
                    + value);
        }
        return value;
    }
}
