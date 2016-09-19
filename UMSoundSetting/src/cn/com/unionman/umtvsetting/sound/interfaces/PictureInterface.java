package cn.com.unionman.umtvsetting.sound.interfaces;

import java.util.ArrayList;

import android.util.Log;

import cn.com.unionman.umtvsetting.sound.util.Constant;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusPicture;
import com.hisilicon.android.tvapi.vo.ColorTempInfo;

/**
 * interface of Picture
 *
 * @author huyq
 *
 */
public class PictureInterface {
    public static final String TAG = "PictureInterface";

    /**
     * get instance of Picture
     *
     * @return
     */
    public static CusPicture getPictureManager() {
        return UmtvManager.getInstance().getPicture();
    }

    /**
     * enableBacklight
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
     */
    public static int enableDynamicBL(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onOff = " + onOff + ") begin");
        }

        int value = getPictureManager().enableDynamicBL(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableDynamicBL(boolean onOff = " + onOff
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * enableFreeze
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
     * Timming getAvailAspectList
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
     * get ColorTempInfo
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


   public static int setColorSystem(int colorSys){

       if (Constant.LOG_TAG) {
           Log.d(TAG,"set ColorSystem value = " + colorSys);
        }

       int res = getPictureManager().setColorSystem(colorSys);

        if (Constant.LOG_TAG) {
           Log.d(TAG,"setColorSystem return res = " + res);
        }
        return res;


   }



   public static int getColorSystem(){

       int value = getPictureManager().getRealColorSystem();

        if (Constant.LOG_TAG) {
           Log.d(TAG,"getColorSystem value = " + value);
        }

        return value;


   }

    /**
     * isBacklightEnable
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
     * isDCIEnable
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
     * isDynamicBLEnable
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
     * isFreezeEnable
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
     * setBacklight
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
     * setColorTempPara range 0-100
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
     */
    public static int setPictureMode(int pictureMode) {
        int value = getPictureManager().setPictureMode(pictureMode);
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
     * setSharpness
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
