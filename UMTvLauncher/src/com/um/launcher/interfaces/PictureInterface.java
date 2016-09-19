
package com.um.launcher.interfaces;

import java.util.ArrayList;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusPicture;
import com.hisilicon.android.tvapi.vo.ColorTempInfo;
import com.um.launcher.util.Constant;

public class PictureInterface {

    public static final String TAG = "PictureInterface";

    /**
     * Set the backlight switch
     */
    public static CusPicture getPictureManager() {
        return UmtvManager.getInstance().getPicture();
    }

    public static int enableBacklight(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBacklight(boolean onoff = " + onoff + ") begin");
        }

        int value = getPictureManager().enableBacklight(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBacklight(boolean onoff = " + onoff
                    + ")   end value = " + value);
        }
        return value;
    }

    /**
     * Set the blue level expansion
     */
    public static int enableBlueExtend(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBlueExtend(boolean onoff = " + onoff + ") begin");
        }

        int value = getPictureManager().enableBlueExtend(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableBlueExtend(boolean onoff = " + onoff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * Design of dynamic contrast
     */
    public static int enableDCI(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDCI(boolean onoff = " + onoff + ") begin");
        }

        int value = getPictureManager().enableDCI(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDCI(boolean onoff = " + onoff + ")  end value = "
                    + value);
        }
        return value;
    }

    /**
     * Design of dynamic backlight
     */
    public static int enableDynamicBL(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onoff = " + onoff + ") begin");
        }

        int value = getPictureManager().enableDynamicBL(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onoff = " + onoff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * Set the static frame
     */
    public static int enableFreeze(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableFreeze(boolean onoff = " + onoff + ") begin");
        }

        int value = getPictureManager().enableFreeze(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableFreeze(boolean onoff = " + onoff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * Setting up the game model
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
     * Set the rate of recurrence
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
     * Gets the current ratio model
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
     * The Timming supports Aspect only on the main screen
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
     * Get backlight
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
     * Acquiring the brightness
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
     * Gets the color gain
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
     * Gets the color temperature
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
     * get ColorTemp
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
     * Gets the contrast
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
     * Gets the block noise reduction of De-blocking strength
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
     * Gets the store mode
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
     * Gets the mosquito noise removal de-ringing strength
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
     * Get film model
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
     * Gets the flesh tone
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
     * Gets a HDMI color range
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
     * get hue
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
     * to obtain the motion compensation
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
     * get noise reduction
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
     * get picture model
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
     * get saturation
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
     * get backlight
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
     * gets the blue level expansion
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
     * get dynamic contrast
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
     * get dynamic backlight
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
     * get static frame
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
     * get game model
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
     * get enable reproducibility rate state
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
     * set the display proportions model
     */
    public static int setAspect(int aspect, boolean mute) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAspect(int aspect = " + aspect + ", boolean mute ="
                    + mute + ") begin");
        }

        int value = getPictureManager().setAspect(aspect, mute);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAspect(int aspect =" + aspect + ", boolean mute ="
                    + mute + ")  end value = " + value);
        }
        return value;
    }

    /**
     * set backlight
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
     * set brightness
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
     * set color gain
     */
    public static int setColorGain(int colorgain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorGain(int colorgain = " + colorgain + ") begin");
        }

        int value = getPictureManager().setColorGain(colorgain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorGain(int colorgain = " + colorgain
                    + ") end value = " + value);
        }
        return value;
    }

    public static int setColorTemp(int colortemp) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTemp(int colortemp = " + colortemp + ") begin");
        }

        int value = getPictureManager().setColorTemp(colortemp);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setColorTemp(int colortemp = " + colortemp
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set ColorTemp RGB GAIN and OFFSET Offset range function factory parameter
     * 0-100
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
     * set contrast
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
     * set De-blocking
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
     * set store model
     */
    public static int setDemoMode(int demomode, boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDemoMode(int demomode = " + demomode + " onoff = "
                    + onoff + ")begin");
        }

        int value = getPictureManager().setDemoMode(demomode, onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDemoMode(int demomode = " + demomode
                    + ") end  onoff= " + onoff + " + value=" + value);
        }
        return value;
    }

    /**
     * Set the mosquito noise removal de-ringing strength
     */
    public static int setDeRinging(int drLlevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeRinging(int drLlevel =" + drLlevel + ") begin");
        }

        int value = getPictureManager().setDeRinging(drLlevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDeRinging(int drLlevel =" + drLlevel
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set film model
     */
    public static int setFilmMode(int filmmode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFilmMode(int filmmode = " + filmmode + ") begin");
        }

        int value = getPictureManager().setFilmMode(filmmode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFilmMode(int filmmode = " + filmmode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setb flesh tone
     */
    public static int setFleshTone(int fleshtone) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFleshTone(int fleshtone = " + fleshtone + ") begin");
        }

        int value = getPictureManager().setFleshTone(fleshtone);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setFleshTone(int fleshtone = " + fleshtone
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set HDMI color range
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
     * set hue
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
     * set MEMCLevel
     */
    public static int setMEMCLevel(int memclevel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMEMCLevel(int memclevel = " + memclevel + ") begin");
        }

        int value = getPictureManager().setMEMCLevel(memclevel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMEMCLevel(int memclevel = " + memclevel
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set NR
     */
    public static int setNR(int nr) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setNR(int nr = " + nr + ") begin");
        }

        int value = getPictureManager().setNR(nr);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setNR(int nr = " + nr + ") end value = " + value);
        }
        return value;
    }

    /**
     * set picture model
     */
    public static int setPictureMode(int picturemode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setPictureMode(int picturemode = " + picturemode
                    + ") begin");
        }

        int value = getPictureManager().setPictureMode(picturemode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setDemoMode(int picturemode = " + picturemode
                    + ") end value = " + value);
        }
        Log.d(TAG, "setPictureMode(int picturemode = " + picturemode
                + ") end value = " + value);
        return value;
    }

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
     * set sharpness
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
}
