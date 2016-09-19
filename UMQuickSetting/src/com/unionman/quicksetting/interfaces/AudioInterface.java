package com.unionman.quicksetting.interfaces;

import android.util.Log;

import com.hisilicon.android.tvapi.CusAudio;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSoundAdvEftParam;
import com.hisilicon.android.tvapi.constant.EnumSoundEftParam;
import com.unionman.quicksetting.util.Constant;

/**
 * The interface of Audio
 * 
 * @author huyq
 * 
 */
public class AudioInterface {

    private static final String TAG = "AudioInterface";

    /**
     * get instance of Audio
     * 
     * @return Audio
     */
    public static CusAudio getAudioManager() {
        return UmtvManager.getInstance().getAudio();
    }

    /**
     * enableAmplifierMute
     * 
     * @param bMute
     * @return
     */
    public static int enableAmplifierMute(boolean bMute) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAmplifierMute(boolean bMute = " + bMute
                    + ") begin");
        }

        int value = getAudioManager().enableAmplifierMute(bMute);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAmplifierMute(boolean bMute = " + bMute
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * isAmplifierMute
     * 
     * @return
     */
    public static boolean isAmplifierMute() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAmplifierMute() begin");
        }

        boolean value = getAudioManager().isAmplifierMute();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAmplifierMute() end value = " + value);
        }
        return value;
    }

    /**
     * enableARC
     * 
     * @param onOff
     * @return
     */
    public static int enableARC(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableARC(boolean onOff = " + onOff + ") begin");
        }

        int value = getAudioManager().enableARC(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableARC(boolean onOff = " + onOff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * isARCEnable
     * 
     * @return
     */
    public static boolean isARCEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isARCEnable() begin");
        }

        boolean value = getAudioManager().isARCEnable();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isARCEnable() end value = " + value);
        }
        return value;
    }

    /**
     * enableAVC
     * 
     * @param onOff
     * @return
     */
    public static int enableAVC(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAVC(boolean onOff = " + onOff + ") begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF, onOff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAVC(boolean onOff = " + onOff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * isAVCEnable
     * 
     * @return
     */
    public static boolean isAVCEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAVCEnable() begin");
        }

        boolean value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF) == 1 ? true
                : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAVCEnable() end value = " + value);
        }
        return value;
    }

    /**
     * EnableSRS
     * 
     * @param onOff
     * @return
     */
    public static int EnableSRS(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRS(boolean onOff = " + onOff + ") begin");
        }

        int value = getAudioManager().setAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_ONOFF, onOff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRS(boolean onOff = " + onOff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * isSRSEnable
     * 
     * @return
     */
    public static boolean isSRSEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSEnable() begin");
        }

        boolean value = getAudioManager().getAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_ONOFF) == 1 ? true : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSEnable() end value = " + value);
        }
        return value;
    }

    /**
     * enableSRSBass
     * 
     * @param onOff
     * @return
     */
    public static int enableSRSBass(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSBass(boolean onOff = " + onOff + ") begin");
        }
        int value = getAudioManager().setAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEBASS_ONOFF, onOff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSBass(boolean onOff = " + onOff
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * get SRS bass boost switch state
     * 
     * @return
     */
    public static boolean isSRSBassEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSBassEnable() begin");
        }

        boolean value = getAudioManager().getAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEBASS_ONOFF) == 1 ? true : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSBassEnable() end value = " + value);
        }
        return value;
    }

    /**
     * set SRS treble switch
     * 
     * @param onOff
     * @return
     */
    public static int enableSRSTreble(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSTreble(boolean onOff = " + onOff + ") begin");
        }

        int value = getAudioManager().setAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEDIALOG_ONOFF, onOff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSTreble(boolean onOff = " + onOff
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * get SRS treble switch state
     * 
     * @return
     */
    public static boolean isSRSTrebleEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSTrebleEnable() begin");
        }

        boolean value = getAudioManager().getAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEDIALOG_ONOFF) == 1 ? true
                : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSTrebleEnable() end value = " + value);
        }
        return value;
    }

    /**
     * Set the bass switch
     * 
     * @param onOff
     * @return
     */
    public static int enableSubWoofer(boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSubWoofer = " + onOff);
        }

        int value = getAudioManager().enableSubWoofer(onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, " return enableSubWoofer = " + value);
        }

        return value;
    }

    /**
     * isSubWooferEnable
     * 
     * @return
     */
    public static boolean isSubWooferEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSubWooferEnable() begin");
        }

        boolean value = getAudioManager().isSubWooferEnable();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSubWooferEnable() end value = " + value);
        }
        return value;
    }

    /**
     * setAVsync
     * 
     * @param ms
     * @return
     */
    public static int setAVsync(int ms) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAVsync(int ms = " + ms + ")  begin");
        }

        int value = getAudioManager().setAVsync(ms);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setAVsync(int ms = " + ms + ")  end value = " + value);
        }
        return value;
    }

    /**
     * getAVsync
     * 
     * @return
     */
    public static int getAVsync() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAVsync() begin");
        }

        int value = getAudioManager().getAVsync();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getAVsync() end value = " + value);
        }
        return value;
    }

    /**
     * setBalance
     * 
     * @param balance
     * @return
     */
    public static int setBalance(int balance) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBalance(int balance = " + balance + ")  begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BALANCE, balance);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBalance(int balance = " + balance
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * getBalance
     * 
     * @return
     */
    public static int getBalance() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBalance() begin");
        }

        int value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BALANCE);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBalance() end value = " + value);
        }
        return value;
    }

    /**
     * setBass
     * 
     * @param gain
     * @return
     */
    public static int setBass(int gain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBass(int gain = " + gain + ")  begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BASS, gain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBass(int gain = " + gain + ")  end value = " + value);
        }
        return value;
    }

    /**
     * getBass
     * 
     * @return
     */
    public static int getBass() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBass() begin");
        }

        int value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BASS);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBass() end value = " + value);
        }
        return value;
    }

    /**
     * setEQ
     * 
     * @param band
     * @param gain
     * @return
     */
    public static int setEQ(int band, int gain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setEQ(int band = " + band + ", int gain = " + gain
                    + ")  begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BAND0_LEVEL + band, gain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setEQ(int band =" + band + ", int gain = " + gain
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * getEQ
     * 
     * @param band
     * @return
     */
    public static int getEQ(int band) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getEQ(int band = " + band + ") begin");
        }

        int value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_BAND0_LEVEL + band);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getEQ(int band = " + band + ") end value = " + value);
        }
        return value;
    }

    /**
     * getHangMode
     * 
     * @return
     */
    public static int getHangMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHangMode() begin");
        }

        int value = getAudioManager().getHangMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getHangMode() end value = " + value);
        }
        return value;
    }

    /**
     * getInputVolume
     * 
     * @return
     */
    public static int getInputVolume() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getInputVolume() begin");
        }

        int value = getAudioManager().getInputVolume();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getInputVolume() end value = " + value);
        }
        return value;
    }

    /**
     * getMute
     * 
     * @param channel
     * @return
     */
    public static boolean getMute(int channel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMute(int channel = " + channel + ") begin");
        }

        boolean value = getAudioManager().getMute(channel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getMute(int channel = " + channel + ") end value = "
                    + value);
        }
        return value;
    }


    /**
     * getSoundMode
     * 
     * @return
     */
    public static int getSoundMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSoundMode() begin");
        }

        int value = getAudioManager().getSoundMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSoundMode() end value = " + value);
        }
        return value;
    }

    /**
     * getSPDIFOutput
     * 
     * @return
     */
    public static int getSPDIFOutput() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager().getSPDIFOutput();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() end value = " + value);
        }
        return value;
    }

    /**
     * getSPDIFOutputDelay
     * 
     * @return
     */
    public static int getSPDIFOutputDelay() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutputDelay() begin");
        }

        int value = getAudioManager().getSPDIFOutputDelay();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutputDelay() end value = " + value);
        }
        return value;
    }

    /**
     * getSpeakerOutput
     * 
     * @return
     */
    public static int getSpeakerOutput() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager().getSpeakerOutput();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSpeakerOutput() end value = " + value);
        }
        return value;
    }

    /**
     * getStereoMode
     * 
     * @return
     */
    public static int getStereoMode() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager().getStereoMode();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getStereoMode() end value = " + value);
        }
        return value;
    }

    /**
     * getSubWooferVolume
     * 
     * @return
     */
    public static int getSubWooferVolume() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager().getSubWooferVolume();

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSubWooferVolume() end value = " + value);
        }
        return value;
    }

    /**
     * getTreble
     * 
     * @return
     */
    public static int getTreble() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_TREBLE);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getTreble() end value = " + value);
        }
        return value;
    }

    /**
     * getVolume
     * 
     * @param channel
     * @return
     */
    public static int getVolume(int channel) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVolume(int channel= " + channel + ") begin");
        }

        int value = getAudioManager().getVolume(channel);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getVolume(int channel= " + channel + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setHangMode
     * 
     * @param hangMode
     * @return
     */
    public static int setHangMode(int hangMode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHangMode(int hangMode = " + hangMode + ")  begin");
        }

        int value = getAudioManager().setHangMode(hangMode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHangMode(int hangMode = " + hangMode
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * setInputVolume
     * 
     * @param vol
     * @return
     */
    public static int setInputVolume(int vol) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setInputVolume(int vol = " + vol + ")  begin");
        }

        int value = getAudioManager().setInputVolume(vol);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setInputVolume(int vol = " + vol + ")  end value = "
                    + value);
        }
        return value;
    }

    /**
     * setMute
     * 
     * @param channel
     * @param onOff
     * @return
     */
    public static int setMute(int channel, boolean onOff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMute(int channel = " + channel
                    + ", boolean onOff = " + onOff + ")  begin");
        }

        int value = getAudioManager().setMute(channel, onOff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMute(int channel = " + channel
                    + ", boolean onOff = " + onOff + ")  end value = " + value);
        }
        return value;
    }


    /**
     * setSoundMode
     * 
     * @param soundMode
     * @return
     */
    public static int setSoundMode(int soundMode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSoundMode(int soundMode = " + soundMode + ")  begin");
        }

        int value = getAudioManager().setSoundMode(soundMode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSoundMode(int soundMode = " + soundMode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setSPDIFOutput
     * 
     * @param spdif
     * @return
     */
    public static int setSPDIFOutput(int spdif) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSPDIFOutput(int spdif = " + spdif + ")  begin");
        }

        int value = getAudioManager().setSPDIFOutput(spdif);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSPDIFOutput(int spdif = " + spdif + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setSPDIFOutputDelay
     * 
     * @param ms
     * @return
     */
    public static int setSPDIFOutputDelay(int ms) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSPDIFOutputDelay(int ms = " + ms + ")  begin");
        }

        int value = getAudioManager().setSPDIFOutputDelay(ms);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSPDIFOutputDelay(int ms = " + ms + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setSpeakerOutput
     * 
     * @param output
     * @return
     */
    public static int setSpeakerOutput(int output) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSpeakerOutput(int output =" + output + ")   begin");
        }

        int value = getAudioManager().setSpeakerOutput(output);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSpeakerOutput(int output = " + output
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * setStereoMode
     * 
     * @param stereo
     * @return
     */
    public static int setStereoMode(int stereo) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setStereoMode(int stereo = " + stereo + ")  begin");
        }

        int value = getAudioManager().setStereoMode(stereo);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setStereoMode(int stereo = " + stereo
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * setTreble
     * 
     * @param gain
     * @return
     */
    public static int setTreble(int gain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setTreble(int gain = " + gain + ")  begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_TREBLE, gain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setTreble(int gain = " + gain + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setSubWooferVolume
     * 
     * @param vol
     * @return
     */
    public static int setSubWooferVolume(int vol) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSubWooferVolume(int vol = " + vol + ")  begin");
        }

        int value = getAudioManager().setSubWooferVolume(vol);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSubWooferVolume(int vol = " + vol + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * setVolume
     * 
     * @param channel
     * @param vol
     * @return
     */
    public static int setVolume(int channel, int vol) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVolume(int channel = " + channel + ", int vol  = "
                    + vol + ")  begin");
        }

        int value = getAudioManager().setVolume(channel, vol);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setVolume(int channel = " + channel + ", int vol = "
                    + vol + ") end value = " + value);
        }
        return value;
    }
}
