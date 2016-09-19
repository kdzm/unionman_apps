package com.unionman.quicksetting.interfaces;

import java.util.ArrayList;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusPicture;
import com.hisilicon.android.tvapi.vo.ColorTempInfo;
import com.unionman.quicksetting.util.Constant;

/**
 * The interface of Picture
 * 
 * @author huyq
 * 
 */
public class PictureInterface {

    private static final String TAG = "PictureInterface";

    /**
     * get instance of Picture
     * 
     * @return Picture
     */
    public static CusPicture getPictureManager() {
        return UmtvManager.getInstance().getPicture();
    }

    /**
     * enableBacklight
     * 
     * @param onOff
     * @return
     */
    public static int enableBacklight(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBacklight(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableBacklight(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBacklight(boolean onOff = " + onOff
                    + ")   end value = " + value);
        }
        return value;
    }

    /**
     * enableBlueExtend
     * 
     * @param onOff
     * @return
     */
    public static int enableBlueExtend(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBlueExtend(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableBlueExtend(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBlueExtend(boolean onOff = " + onOff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * enableDCI
     * 
     * @param onOff
     * @return
     */
    public static int enableDCI(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDCI(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableDCI(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDCI(boolean onOff = " + onOff + ")  end value = "
                    + value);
        }
        return value;
    }

    /**
     * enableDynamicBL
     * 
     * @param onOff
     * @return
     */
    public static int enableDynamicBL(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableDynamicBL(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onoff = " + onOff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * enableFreeze
     * 
     * @param onOff
     * @return
     */
    public static int enableFreeze(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableFreeze(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableFreeze(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableFreeze(boolean onOff = " + onOff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * enableGameMode
     * 
     * @param bEnable
     * @return
     */
    public static int enableGameMode(boolean bEnable) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableGameMode(boolean bEnable = " + bEnable
                    + ") begin");
        }

        int value = getPictureManager().enableGameMode(bEnable);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableGameMode(boolean bEnable = " + bEnable
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * enableOverscan
     * 
     * @param bEnable
     * @return
     */
    public static int enableOverscan(boolean bEnable) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableOverscan(boolean bEnable = " + bEnable
                    + ") begin");
        }

        int value = getPictureManager().enableOverscan(bEnable);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableOverscan(boolean bEnable = " + bEnable
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * getAspect
     * 
     * @return
     */
    public static int getAspect() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAspect() begin");
        }

        int value = getPictureManager().getAspect();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAspect()  end value = " + value);
        }
        return value;
    }

    /**
     * getAvailAspectList
     * 
     * @return
     */
    public static ArrayList<Integer> getAvailAspectList() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailAspectList() begin");
        }

        ArrayList<Integer> value = getPictureManager().getAvailAspectList();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAvailAspectList()  end value = " + value);
        }
        return value;
    }

    /**
     * getBacklight
     * 
     * @return
     */
    public static int getBacklight() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBacklight() begin");
        }

        int value = getPictureManager().getBacklight();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBacklight()  end value = " + value);
        }
        return value;
    }

    /**
     * getBrightness
     * 
     * @return
     */
    public static int getBrightness() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBrightness() begin");
        }

        int value = getPictureManager().getBrightness();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBrightness()  end value = " + value);
        }
        return value;
    }

    /**
     * getColorGain
     * 
     * @return
     */
    public static int getColorGain() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorGain() begin");
        }

        int value = getPictureManager().getColorGain();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorGain()  end value = " + value);
        }
        return value;
    }

    /**
     * getColorTemp
     * 
     * @return
     */
    public static int getColorTemp() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorTemp() begin");
        }

        int value = getPictureManager().getColorTemp();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorTemp()  end value = " + value);
        }
        return value;
    }

    /**
     * getColorTempPara
     * 
     * @return ColorTempInfo
     */
    public static ColorTempInfo getColorTempPara() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorTempPara() begin");
        }

        ColorTempInfo value = getPictureManager().getColorTempPara();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getColorTempPara()  end value = " + value);
        }
        return value;
    }

    /**
     * getContrast
     * 
     * @return
     */
    public static int getContrast() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getContrast() begin");
        }

        int value = getPictureManager().getContrast();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getContrast()  end value = " + value);
        }
        return value;
    }

    /**
     * getDeBlocking
     * 
     * @return
     */
    public static int getDeBlocking() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDeBlocking() begin");
        }

        int value = getPictureManager().getDeBlocking();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDeBlocking()  end value = " + value);
        }
        return value;
    }

    /**
     * getDemoMode
     * 
     * @param mode
     * @return
     */
    public static boolean getDemoMode(int mode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDemoMode() begin");
        }

        boolean value = getPictureManager().getDemoMode(mode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDemoMode()  end value = " + value);
        }
        return value;
    }

    /**
     * getDeRinging
     * 
     * @return
     */
    public static int getDeRinging() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDeRinging() begin");
        }

        int value = getPictureManager().getDeRinging();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getDeRinging()  end value = " + value);
        }
        return value;
    }

    /**
     * getFilmMode
     * 
     * @return
     */
    public static int getFilmMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getFilmMode() begin");
        }

        int value = getPictureManager().getFilmMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getFilmMode()  end value = " + value);
        }
        return value;
    }

    /**
     * getFleshTone
     * 
     * @return
     */
    public static int getFleshTone() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getFleshTone() begin");
        }

        int value = getPictureManager().getFleshTone();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getFleshTone()  end value = " + value);
        }
        return value;
    }

    /**
     * getHDMIColorRange
     * 
     * @return
     */
    public static int getHDMIColorRange() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHDMIColorRange() begin");
        }

        int value = getPictureManager().getHDMIColorRange();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHDMIColorRange()  end value = " + value);
        }
        return value;
    }

    /**
     * getHue
     * 
     * @return
     */
    public static int getHue() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHue() begin");
        }

        int value = getPictureManager().getHue();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHue()  end value = " + value);
        }
        return value;
    }

    /**
     * getMEMCLevel
     * 
     * @return
     */
    public static int getMEMCLevel() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMEMCLevel() begin");
        }

        int value = getPictureManager().getMEMCLevel();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMEMCLevel()  end value = " + value);
        }
        return value;
    }

    /**
     * getNR
     * 
     * @return
     */
    public static int getNR() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getNR() begin");
        }

        int value = getPictureManager().getNR();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getNR()  end value = " + value);
        }
        return value;
    }

    /**
     * getPictureMode
     * 
     * @return
     */
    public static int getPictureMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getPictureMode() begin");
        }

        int value = getPictureManager().getPictureMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getPictureMode()  end value = " + value);
        }
        return value;
    }

    /**
     * getSaturation
     * 
     * @return
     */
    public static int getSaturation() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSaturation() begin");
        }

        int value = getPictureManager().getSaturation();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSaturation()  end value = " + value);
        }
        return value;
    }

    /**
     * getSharpness
     * 
     * @return
     */
    public static int getSharpness() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSharpness() begin");
        }

        int value = getPictureManager().getSharpness();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSharpness()  end value = " + value);
        }
        return value;
    }

    /**
     * isBacklightEnable
     * 
     * @return
     */
    public static boolean isBacklightEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isBacklightEnable() begin");
        }

        boolean value = getPictureManager().isBacklightEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isBacklightEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * isBlueExtendEnable
     * 
     * @return
     */
    public static boolean isBlueExtendEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isBlueExtendEnable() begin");
        }

        boolean value = getPictureManager().isBlueExtendEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isBlueExtendEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * isMEMCEnable
     * 
     * @return
     */
    public static boolean isMEMCEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isMEMCEnable() begin");
        }

        boolean value = getPictureManager().isMEMCEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isMEMCEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * isDCIEnable
     * 
     * @return
     */
    public static boolean isDCIEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDCIEnable() begin");
        }

        boolean value = getPictureManager().isDCIEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDCIEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * get DynamicBLEnable
     * 
     * @return
     */
    public static boolean isDynamicBLEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDynamicBLEnable() begin");
        }

        boolean value = getPictureManager().isDynamicBLEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isDynamicBLEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * get FreezeEnable
     * 
     * @return
     */
    public static boolean isFreezeEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isFreezeEnable() begin");
        }

        boolean value = getPictureManager().isFreezeEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isFreezeEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * isGameModeEnable
     * 
     * @return
     */
    public static boolean isGameModeEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isGameModeEnable() begin");
        }

        boolean value = getPictureManager().isGameModeEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isGameModeEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * isOverscanEnable
     * 
     * @return
     */
    public static boolean isOverscanEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isOverscanEnable() begin");
        }

        boolean value = getPictureManager().isOverscanEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isOverscanEnable()  end value = " + value);
        }
        return value;
    }

    /**
     * setAspect
     * 
     * @param aspect
     * @param mute
     * @return
     */
    public static int setAspect(int aspect, boolean mute) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAspect(int aspect = " + aspect + ", boolean mute ="
                    + mute + ") begin");
        }

        int value = getPictureManager().setAspect(aspect, mute);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAspect(int aspect = " + aspect + ", boolean mute ="
                    + mute + ")  end value = " + value);
        }
        return value;
    }

    /**
     * setBacklight
     * 
     * @param backlight
     * @return
     */
    public static int setBacklight(int backlight) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBacklight(int backlight = " + backlight + ") begin");
        }

        int value = getPictureManager().setBacklight(backlight);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBacklight(int backlight = " + backlight
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * setBrightness
     * 
     * @param brightness
     * @return
     */
    public static int setBrightness(int brightness) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBrightness(int brightness = " + brightness
                    + ") begin");
        }

        int value = getPictureManager().setBrightness(brightness);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBrightness(int brightness = " + brightness
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * setColorGain
     * 
     * @param colorGain
     * @return
     */
    public static int setColorGain(int colorGain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorGain(int colorGain = " + colorGain + ") begin");
        }

        int value = getPictureManager().setColorGain(colorGain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorGain(int colorGain = " + colorGain
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setColorTemp
     * 
     * @param colorTemp
     * @return
     */
    public static int setColorTemp(int colorTemp) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTemp(int colorTemp = " + colorTemp + ") begin");
        }

        int value = getPictureManager().setColorTemp(colorTemp);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTemp(int colorTemp = " + colorTemp
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set ColorTemp RGB GAIN and OFFSET range 0-100
     * 
     * @param stColorTemp
     * @return
     */
    public static int setColorTempPara(ColorTempInfo stColorTemp) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTempPara(ColorTempInfo stColorTemp = "
                    + stColorTemp + ") begin");
        }

        int value = getPictureManager().setColorTempPara(stColorTemp);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTempPara(ColorTempInfo stColorTemp = "
                    + stColorTemp + ") end value = " + value);
        }
        return value;
    }

    /**
     * setContrast
     * 
     * @param contrast
     * @return
     */
    public static int setContrast(int contrast) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setContrast(int contrast = " + contrast + ") begin");
        }

        int value = getPictureManager().setContrast(contrast);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setContrast(int contrast=" + contrast
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setDeBlocking
     * 
     * @param dbLevel
     * @return
     */
    public static int setDeBlocking(int dbLevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeBlocking(int dbLevel = " + dbLevel + ") begin");
        }

        int value = getPictureManager().setDeBlocking(dbLevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeBlocking(int dbLevel = " + dbLevel
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setDemoMode
     * 
     * @param demoMode
     * @param onOff
     * @return
     */
    public static int setDemoMode(int demoMode, boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDemoMode(int demoMode = " + demoMode + " onOff = "
                    + onOff + ")begin");
        }

        int value = getPictureManager().setDemoMode(demoMode, onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDemoMode(int demoMode = " + demoMode
                    + ") end  onOff= " + onOff + " + value=" + value);
        }
        return value;
    }

    /**
     * setDeRinging
     * 
     * @param DRLevel
     * @return
     */
    public static int setDeRinging(int DRLevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeRinging(int DRLevel =" + DRLevel + ") begin");
        }

        int value = getPictureManager().setDeRinging(DRLevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeRinging(int DRLevel =" + DRLevel
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setFilmMode
     * 
     * @param filmMode
     * @return
     */
    public static int setFilmMode(int filmMode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFilmMode(int filmMode = " + filmMode + ") begin");
        }

        int value = getPictureManager().setFilmMode(filmMode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFilmMode(int filmMode = " + filmMode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setFleshTone
     * 
     * @param fleshTone
     * @return
     */
    public static int setFleshTone(int fleshTone) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFleshTone(int fleshTone = " + fleshTone + ") begin");
        }

        int value = getPictureManager().setFleshTone(fleshTone);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFleshTone(int fleshTone = " + fleshTone
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setHDMIColorRange
     * 
     * @param val
     * @return
     */
    public static int setHDMIColorRange(int val) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHDMIColorRange(int val = " + val + ") begin");
        }

        int value = getPictureManager().setHDMIColorRange(val);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHDMIColorRange(int val = " + val + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setHue
     * 
     * @param hue
     * @return
     */
    public static int setHue(int hue) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHue(int hue = " + hue + " begin");
        }

        int value = getPictureManager().setHue(hue);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHue(int hue = " + hue + ") end value = " + value);
        }
        return value;
    }

    /**
     * setMEMCLevel
     * 
     * @param MEMCLevel
     * @return
     */
    public static int setMEMCLevel(int MEMCLevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMEMCLevel(int MEMCLevel = " + MEMCLevel + ") begin");
        }

        int value = getPictureManager().setMEMCLevel(MEMCLevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMEMCLevel(int MEMCLevel = " + MEMCLevel
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setNR
     * 
     * @param NR
     * @return
     */
    public static int setNR(int NR) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setNR(int NR = " + NR + ") begin");
        }

        int value = getPictureManager().setNR(NR);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setNR(int NR = " + NR + ") end value = " + value);
        }
        return value;
    }

    /**
     * setPictureMode
     * 
     * @param pictureMode
     * @return
     */
    public static int setPictureMode(int pictureMode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setPictureMode(int pictureMode = " + pictureMode
                    + ") begin");
        }

        int value = getPictureManager().setPictureMode(pictureMode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setPictureMode(int pictureMode = " + pictureMode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setSaturation
     * 
     * @param saturation
     * @return
     */
    public static int setSaturation(int saturation) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSaturation(int saturation = " + saturation
                    + ") begin");
        }

        int value = getPictureManager().setSaturation(saturation);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSaturation(int saturation = " + saturation
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setSharpness
     * 
     * @param sharpness
     * @return
     */
    public static int setSharpness(int sharpness) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSharpness(int sharpness = " + sharpness + ") begin");
        }

        int value = getPictureManager().setSharpness(sharpness);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSharpness(int sharpness = " + sharpness
                    + " end value = " + value);
        }
        return value;
    }

    /**
     * setSRLevel
     * 
     * @param SRLevel
     * @return
     */
    public static int setSRLevel(int SRLevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "srlevel(int SRLevel = " + SRLevel + ") begin");
        }

        int value = getPictureManager().setSRLevel(SRLevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "srlevel(int SRLevel = " + SRLevel + ") end");
        }
        return value;
    }

    /**
     * getSRLevel
     * 
     * @return
     */
    public static int getSRLevel() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSRLevel() begin");
        }

        int value = getPictureManager().getSRLevel();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSRLevel()  end value = " + value);
        }
        return value;
    }

}
