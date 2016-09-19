package cn.com.unionman.umtvsetting.sound.interfaces;

import android.util.Log;

import cn.com.unionman.umtvsetting.sound.util.Constant;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusPCSetting;

/**
 * interface of PC setting
 *
 * @author huyq
 *
 */
public class PCSettingInterface {
    public static final String TAG = "PCSettingInterface";

    /**
     * get instance of PC setting
     *
     * @return
     */
    public static CusPCSetting getPCSettingManager() {
        return UmtvManager.getInstance().getPCSetting();
    }

    /**
     * autoAdjust
     */
    public static int autoAdjust() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoAdjust()  begin");
        }

        int value = getPCSettingManager().autoAdjust();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoAdjust() end value = " + value);
        }
        return value;
    }

    /**
     * getClock
     */
    public static int getClock() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getClock()  begin");
        }

        int value = getPCSettingManager().getClock();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getClock() end value = " + value);
        }
        return value;
    }

    /**
     * getHPosition
     */
    public static int getHPosition() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHPosition()  begin");
        }

        int value = getPCSettingManager().getHPosition();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHPosition() end value = " + value);
        }
        return value;
    }

    /**
     * getPhase
     */
    public static int getPhase() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getPhase()  begin");
        }

        int value = getPCSettingManager().getPhase();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getPhase() end value = " + value);
        }
        return value;
    }

    /**
     * getVPosition
     */
    public static int getVPosition() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVPosition()  begin");
        }

        int value = getPCSettingManager().getVPosition();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVPosition() end value = " + value);
        }
        return value;
    }

    /**
     * setClock
     */
    public static int setClock(int clock) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setClock(int clock = " + clock + ")  begin");
        }

        int value = getPCSettingManager().setClock(clock);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setClock(int clock = " + clock + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setHPosition
     */
    public static int setHPosition(int position) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHPosition(int position = " + position + ")  begin");
        }

        int value = getPCSettingManager().setHPosition(position);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHPosition(int position = " + position
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setPhase
     */
    public static int setPhase(int phase) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setPhase(int phase = " + phase + ")  begin");
        }

        int value = getPCSettingManager().setPhase(phase);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setPhase(int phase = " + phase + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setVPosition
     */
    public static int setVPosition(int position) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVPosition(int position = " + position + ")  begin");
        }

        int value = getPCSettingManager().setVPosition(position);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVPosition(int position = " + position
                    + ") end value = " + value);
        }
        return value;
    }
}
