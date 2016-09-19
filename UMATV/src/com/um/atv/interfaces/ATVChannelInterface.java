package com.um.atv.interfaces;

import java.util.ArrayList;

import android.util.Log;

import com.hisilicon.android.tvapi.CusAtvChannel;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.util.Constant;

/**
 * interface of AtvChannel
 *
 * @author huyq
 *
 */
public class ATVChannelInterface {
    public static final String TAG = "AtvChannelInterface";

    /**
     * get instance of AtvChannel
     *
     * @return
     */
    public static CusAtvChannel getAtvChannelManager() {
        return UmtvManager.getInstance().getAtvChannel();
    }

    /**
     * Automatic channel searching (non blocking)
     */
    public static int autoScan() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoScan() begin");
        }

        int value = getAtvChannelManager().autoScan();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoScan() end value = " + value);
        }
        return value;
    }

    /**
     * Automatic channel searching (non blocking)
     */
    public static int autoScan(long startFreq, long endFreq) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoScan(long startFreq = " + startFreq
                    + ", long endFreq = " + endFreq + ") begin");
        }

        int value = getAtvChannelManager().autoScan(startFreq, endFreq);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "autoScan(long startFreq = " + startFreq
                    + ", long endFreq = " + endFreq + ") end value = " + value);
        }
        return value;

    }

    /**
     * Delete all channel
     */
    public static int clearAll() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "clearAll() begin");
        }

        int value = getAtvChannelManager().clearAll();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "clearAll() end value = " + value);
        }
        return value;
    }

    /**
     * Delete the specified channel
     */
    public static int delete(int number) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "delete() begin");
        }

        int value = getAtvChannelManager().delete(number);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "delete() end value = " + value);
        }
        return value;
    }

    /**
     * Setting the AFT status
     */
    public static int enableAFT(boolean bEnable) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAFT(boolean bEnable = " + bEnable + ") begin");
        }

        int value = getAtvChannelManager().enableAFT(bEnable);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAFT(boolean bEnable = " + bEnable
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Change Mode
     */
    public static int enableChangeMode(boolean freezeMode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableChangeMode(boolean freezeMode= " + freezeMode
                    + ") begin");
        }

        int value = getAtvChannelManager().enableChangeMode(freezeMode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableChangeMode(boolean freezeMode= " + freezeMode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * end manual scan
     *
     * @return
     */
    public static int endManualScan() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "endManualScan() begin");
        }

        int value = getAtvChannelManager().endManualScan();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "endManualScan() end value = " + value);
        }
        return value;
    }

    /**
     * exit scan
     *
     * @return
     */
    public static int exitScan() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "exitScan() begin");
        }

        int value = getAtvChannelManager().exitScan();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "exitScan() end value = " + value);
        }
        return value;
    }

    /**
     * Channel Manager
     */
    public static int favorite(int number, boolean bFlag) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "favorite(int number =" + number + ", boolean bFlag = "
                    + bFlag + ") begin");
        }

        int value = getAtvChannelManager().favorite(number, bFlag);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "favorite(int number =" + number + ", boolean bFlag = "
                    + bFlag + ") end value = " + value);
        }
        return value;
    }

    /**
     * Frequency tuning
     */
    public static int fineTune(long freq) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "fineTune(long freq = " + freq + ") begin");
        }

        int value = getAtvChannelManager().fineTune(freq);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "fineTune(long freq = " + freq + ") end value = "
                    + value);
        }
        return value;
    }
    
    /**
     * Gets the sound system
     */
    public static int getAudioSystem() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAudioSystem() begin");
        }

        int value = getAtvChannelManager().getAudioSystem();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAudioSystem() end value = " + value);
        }
        return value;
    }

    /**
     * Get the number of programs
     */
    public static int getAvailProgCount() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailProgCount() begin");
        }

        int value = getAtvChannelManager().getAvailProgCount();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailProgCount() end value = " + value);
        }
        return value;
    }

    /**
     * Gets the color standard
     */
    public static int getColorSystem() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorSystem() begin");
        }

        int value = getAtvChannelManager().getColorSystem();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorSystem() end value = " + value);
        }
        return value;
    }

    /**
     * Gets a Tuner band
     */
    public static int getCurBand(long freq) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurBand(long freq = " + freq + ") begin");
        }

        int value = getAtvChannelManager().getCurBand(freq);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurBand(long freq = " + freq + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * Gets the current channel number
     */
    public static int getCurProgNumber() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurProgNumber() begin");
        }

        int value = getAtvChannelManager().getCurProgNumber();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurProgNumber() end value = " + value);
        }
        return value;
    }

    public static int setCurProgNumber(long progNumber) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setCurProgNumber() begin");
        }

        int value = getAtvChannelManager().setCurProgNumber(progNumber);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setCurProgNumber() end value = " + value);
        }
        return value;
    }
    
    /**
     * get current frequency
     *
     * @return
     */
    public static int getCurrentFrequency() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurrentFrequency() begin");
        }

        int value = getAtvChannelManager().getCurrentFrequency();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurrentFrequency() end value = " + value);
        }
        return value;
    }

    /**
     * Gets the Tuner frequency range
     */
    public static int getMaxTuneFreq() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMaxTuneFreq() begin");
        }

        int value = getAtvChannelManager().getMaxTuneFreq();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMaxTuneFreq() end value = " + value);
        }
        return value;
    }

    /**
     * Gets the full band frequency range
     */
    public static int getMinTuneFreq() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMinTuneFreq() begin");
        }

        int value = getAtvChannelManager().getMinTuneFreq();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMinTuneFreq() end value = " + value);
        }
        return value;
    }

    /**
     * Access to the specified channel information
     */
    public static TvProgram getProgInfo(int number) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getProgInfo(int number = " + number + ") begin");
        }

        TvProgram value = getAtvChannelManager().getProgInfo(number);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getProgInfo(int number = " + number + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * set current color system
     *
     * @param colorsystem
     * @return
     */
    public static int saveCurrentColorSystem(int colorsystem) {
        int currpro = getAtvChannelManager().getCurProgNumber();
        TvProgram proinfo = getAtvChannelManager().getProgInfo(currpro);
        TvChannelAttr channelAttr = proinfo.getStChannelAttr();
        channelAttr.setiColorSysOriginal(colorsystem);
        return 0;

    }

    /**
     * get current color system
     *
     * @return
     */
    public static int getCurrentColorSystem() {
        int currpro = getAtvChannelManager().getCurProgNumber();
        TvProgram proinfo = getAtvChannelManager().getProgInfo(currpro);
        TvChannelAttr channelAttr = proinfo.getStChannelAttr();
        return channelAttr.getiColorSysOriginal();

    }

    /**
     * set current audio system
     *
     * @param audiosystem
     * @return
     */
    public static int saveCurrentAudioSystem(int audiosystem) {
        int currpro = getAtvChannelManager().getCurProgNumber();
        TvProgram proinfo = getAtvChannelManager().getProgInfo(currpro);
        TvChannelAttr channelAttr = proinfo.getStChannelAttr();
        channelAttr.setiAudioSys(audiosystem);
        return 0;

    }

    /**
     * get current audio system
     *
     * @return
     */
    public static int getCurrentAudioSystem() {
        int currpro = getAtvChannelManager().getCurProgNumber();
        TvProgram proinfo = getAtvChannelManager().getProgInfo(currpro);
        TvChannelAttr channelAttr = proinfo.getStChannelAttr();
        return channelAttr.getiAudioSys();

    }

    /**
     * To obtain the channel list information
     */
    public static ArrayList<TvProgram> getProgList() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getProgList() begin");
        }

        ArrayList<TvProgram> value = getAtvChannelManager().getProgList();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getProgList()) end value = " + value);
        }
        return value;
    }

    /**
     * Access to the specified channel volume compensation value
     */
    public static int getVolOffset(int programNo) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVolOffset(int programNo = " + programNo + ") begin");
        }

        int value = getAtvChannelManager().getVolOffset(programNo);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVolOffset(int programNo = " + programNo
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Return to AFT
     */
    public static boolean isAFTEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAFTEnable() begin");
        }

        boolean value = getAtvChannelManager().isAFTEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAFTEnable() end value = " + value);
        }
        return value;
    }

    /**
     * Gets the change mode
     */
    public static boolean isChangeModeEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isChangeModeEnable() begin");
        }

        boolean value = getAtvChannelManager().isChangeModeEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isChangeModeEnable() end value = " + value);
        }
        return value;
    }

    /**
     * Search manually
     */
    public static int manualScan(boolean bForward) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "manualScan(boolean bForward  = " + bForward + ") begin");
        }

        int value = getAtvChannelManager().manualScan(bForward);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "manualScan(boolean bForward  = " + bForward
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Save search manually program
     */
    public static int saveManualScanProgram(long progNumber) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "saveManualScanProgram(long progNumber  = " + progNumber + ") begin");
        }

        int value = getAtvChannelManager().saveManualScanProgram(progNumber);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "saveManualScanProgram(long progNumber  = " + progNumber
                    + ") end value = " + value);
        }

        return value;
    }
    
    /**
     * Save search manually program
     */
    public static int exitManualScanProgram() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "exitManualScanProgram(long progNumber  = begin");
        }

        int value = getAtvChannelManager().exitManualScanProgram();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "exitManualScanProgram");
        }

        return value;
    }
    
    /**
     * Clear search manually program data
     */
    public static int clearManualScanProgram() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "clearManualScanProgram() begin");
        }

        int value = getAtvChannelManager().clearManualScanProgram();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "clearManualScanProgram() end value = " + value);
        }
        return value;
    }
    /**
     * Search manually
     */
    public static int manualScan(long startFreq, long endFreq, boolean bForward) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "manualScan(long startFreq = " + startFreq
                    + ", long endFreq = " + endFreq + ", boolean bForward ="
                    + bForward + ") begin");
        }

        int value = getAtvChannelManager().manualScan(startFreq, endFreq,
                bForward);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "manualScan(long startFreq = " + startFreq
                    + ", long endFreq = " + endFreq + ", boolean bForward ="
                    + bForward + ") end value = " + value);
        }
        return value;
    }

    /**
     * Reduction in channel
     */
    public static int progDown() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "progDown() begin");
        }

        int value = getAtvChannelManager().progDown();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "progDown() end value = " + value);
        }
        return value;
    }

    /**
     * Channel look back
     */
    public static int progReturn() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "progReturn() begin");
        }

        int value = getAtvChannelManager().progReturn();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "progReturn() end value = " + value);
        }
        return value;
    }

    /**
     * progUp
     */
    public static int progUp() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "progUp() begin");
        }

        int value = getAtvChannelManager().progUp();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "progUp() end value = " + value);
        }
        return value;
    }

    /**
     * Channel rename
     */
    public static int rename(int number, String name) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "rename(int number = " + number + ", String name = "
                    + name + ") begin");
        }

        int value = getAtvChannelManager().rename(number, name);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "rename(int number = " + number + ", String name = "
                    + name + ") end value = " + value);
        }
        return value;
    }

    /**
     * selectProg
     */
    public static int selectProg() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectProg() begin");
        }

        int value = getAtvChannelManager().selectProg();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectProg() end value = " + value);
        }
        return value;
    }

    /**
     * The specified channel number for channel
     */
    public static int selectProg(int number) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectProg(int number = " + number + ") begin");
        }

        int value = getAtvChannelManager().selectProg(number);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectProg(int number = " + number + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setAudioSystem
     */
    public static int setAudioSystem(int audioSystem) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAudioSystem(int audioSystem = " + audioSystem
                    + ") begin");
        }

        int value = getAtvChannelManager().setAudioSystem(audioSystem);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAudioSystem(int audioSystem = " + audioSystem
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setColorSystem
     */
    public static int setColorSystem(int colorSystem) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorSystem(int colorSystem = " + colorSystem
                    + ") begin");
        }

        int value = getAtvChannelManager().setColorSystem(colorSystem);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorSystem(int colorSystem = " + colorSystem
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Sets the specified channel and volume compensation
     */
    public static int setVolOffset(int programNo, int offset) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVolOffset(int programNo = " + programNo
                    + ", int offset=" + offset + ") begin");
        }

        int value = getAtvChannelManager().setVolOffset(programNo, offset);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVolOffset(int programNo = " + programNo
                    + ", int offset=" + offset + ") end value = " + value);
        }
        return value;
    }

    /**
     * set skip
     */
    public static int skip(int number, boolean bFlag) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "skip(int number = " + number + ", boolean bFlag="
                    + bFlag + ") begin");
        }

        int value = getAtvChannelManager().skip(number, bFlag);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "skip(int number = " + number + ", boolean bFlag="
                    + bFlag + ") end value = " + value);
        }
        return value;
    }

    /**
     * set swap
     */
    public static int swap(int number1, int number2) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "swap(int number1=" + number1 + ", int number2="
                    + number2 + ")  begin");
        }

        int value = getAtvChannelManager().swap(number1, number2);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "swap(int number1=" + number1 + ", int number2="
                    + number2 + ")  end value = " + value);
        }
        return value;
    }
}
