
package com.um.launcher.interfaces;

import android.util.Log;

import com.hisilicon.android.tvapi.CusAudio;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSoundEftParam;
import com.hisilicon.android.tvapi.constant.EnumSoundAdvEftParam;
import com.um.launcher.util.Constant;

public class AudioInterface {

    public static final String TAG = "AudioInterface";

    /**
     * set external amplifier MUTE
     */
    public static CusAudio getAudioManager() {
        return UmtvManager.getInstance().getAudio();
    }

    public static int enableAmplifierMute(boolean bmute) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAmplifierMute(boolean bmute = " + bmute
                    + ") begin");
        }

        int value = getAudioManager().enableAmplifierMute(bmute);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAmplifierMute(boolean bmute = " + bmute
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set the ARC switch
     */
    public static int enableARC(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableARC(boolean onoff = " + onoff + ") begin");
        }

        int value = getManager().enableARC(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableARC(boolean onoff = " + onoff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * set the volume switch
     */
    public static int enableAVC(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAVC(boolean onoff = " + onoff + ") begin");
        }

        int value = getAudioManager().setEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF, onoff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableAVC(boolean onoff = " + onoff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * Get AVC Enable
     */
    public static boolean isAVCEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAVCEnable() begin");
        }

        boolean value = getAudioManager().getEffectParameter(
                EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF) == 1 ? true : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isAVCEnable() end value = " + value);
        }
        return value;
    }

    /**
     * sets the SRS audio switch
     */
    public static int EnableSRS(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRS(boolean onoff = " + onoff + ") begin");
        }

        int value = getAudioManager().setAdvancedEffectParameter(EnumSoundAdvEftParam.E_SRS_ONOFF,
                onoff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRS(boolean onoff = " + onoff + ") end value = "
                    + value);
        }
        return value;
    }

    /**
     * Get SRS Enable
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
     * set the SRS curve bass switch
     */
    public static int enableSRSBass(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSBass(boolean onoff = " + onoff + ") begin");
        }

        int value = getAudioManager().setAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEBASS_ONOFF, onoff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSBass(boolean onoff = " + onoff
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Get SRS Base Enable
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
     * set the SRS curve treble switch
     */
    public static int enableSRSTreble(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSTreble(boolean onoff = " + onoff + ") begin");
        }

        int value = getAudioManager().setAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEDIALOG_ONOFF, onoff ? 1 : 0);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSRSTreble(boolean onoff = " + onoff
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * Get SRS Treble Enable
     */
    public static boolean isSRSTrebleEnable() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSTrebleEnable() begin");
        }

        boolean value = getAudioManager().getAdvancedEffectParameter(
                EnumSoundAdvEftParam.E_SRS_TRUEDIALOG_ONOFF) == 1 ? true : false;

        if (Constant.LOG_TAG) {
            Log.d(TAG, "isSRSTrebleEnable() end value = " + value);
        }
        return value;
    }

    /**
     * Enable SubWoofer
     */
    public static int enableSubWoofer(boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "enableSubWoofer = " + onoff);
        }

        int value = getAudioManager().enableSubWoofer(onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, " return enableSubWoofer = " + value);
        }

        return value;
    }

    /**
     * Get SubWoofer Enable
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
     * setting out of sync
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
     * Set Audio Balance
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
     * access channel balance
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
     * Set Bass
     */
    public static int setBass(int gain) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBass(int gain = " + gain + ")  begin");
        }

        int value = getAudioManager().setEffectParameter(EnumSoundEftParam.E_SOUND_SET_PARAM_BASS,
                gain);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setBass(int gain = " + gain + ")  end value = " + value);
        }
        return value;
    }

    /**
     * gets the bass
     */
    public static int getBass() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBass() begin");
        }

        int value = getAudioManager().getEffectParameter(EnumSoundEftParam.E_SOUND_SET_PARAM_BASS);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getBass() end value = " + value);
        }
        return value;
    }

    /**
     * Set EQ
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
     * access to the specified frequency band gain
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
     * get sound field model
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
     * gets the input volume
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
     * access to the specified channel mute switch
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
     * get sound mode
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
     * gets the SPDIF output mode
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
     * set the SPDIF output delay
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
     * access to the speaker output mode
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
     * gets the stereo mode
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
     * to obtain external amplifier bass volume
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
     * gets the treble
     */
    public static int getTreble() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getSPDIFOutput() begin");
        }

        int value = getAudioManager()
                .getEffectParameter(EnumSoundEftParam.E_SOUND_SET_PARAM_TREBLE);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "getTreble() end value = " + value);
        }
        return value;
    }

    /**
     * gets the subwoofer volume
     */
    /*
     * public static int getTruBassVol() { if (Constant.LOG_TAG) { Log.d(TAG,
     * "getSPDIFOutput() begin"); } int value =
     * getAudioManager().getTruBassVol(); if (Constant.LOG_TAG) { Log.d(TAG,
     * "getTruBassVol() end value = " + value); } return value; }
     */

    /**
     * get voice enhancement
     */
    /*
     * public static int getTruDialog() { if (Constant.LOG_TAG) { Log.d(TAG,
     * "getSPDIFOutput() begin"); } int value =
     * getAudioManager().getTruDialog(); if (Constant.LOG_TAG) { Log.d(TAG,
     * "getTruDialog() end value = " + value); } return value; }
     */

    /**
     * access to the specified channel volume
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
     * to obtain external amplifier Mute state
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
     * gets a ARC switch
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
     * set sound mode
     */
    public static int setHangMode(int hangmode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHangMode(int hangmode = " + hangmode + ")  begin");
        }

        int value = getAudioManager().setHangMode(hangmode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setHangMode(int hangmode = " + hangmode
                    + ")  end value = " + value);
        }
        return value;
    }

    /**
     * set the input volume
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
     * sets the specified channel mute switch
     */
    public static int setMute(int channel, boolean onoff) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMute(int channel = " + channel
                    + ", boolean onoff = " + onoff + ")  begin");
        }

        int value = getAudioManager().setMute(channel, onoff);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setMute(int channel = " + channel
                    + ", boolean onoff = " + onoff + ")  end value = " + value);
        }
        return value;
    }


    /**
     * set sound mode
     */
    public static int setSoundMode(int sndmode) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSoundMode(int sndmode = " + sndmode + ")  begin");
        }

        int value = getAudioManager().setSoundMode(sndmode);

        if (Constant.LOG_TAG) {
            Log.d(TAG, "setSoundMode(int sndmode = " + sndmode
                    + ") end value = " + value);
        }
        return value;
    }

    /**
     * set the SPDIF output mode
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
     * set the SPDIF output delay
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
     * setting the speaker output
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
     * treble
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
     * Set SubWoofer Volume
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
     * sets the specified channel volume
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
