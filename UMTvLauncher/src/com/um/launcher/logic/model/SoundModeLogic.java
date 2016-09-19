
package com.um.launcher.logic.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.um.launcher.R;
import com.um.launcher.interfaces.AudioInterface;
import com.um.launcher.interfaces.InterfaceValueMaps;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;
import com.um.launcher.model.WidgetType.AccessProgressInterface;
import com.um.launcher.model.WidgetType.AccessSysValueInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.UtilLauncher;

/**
 * SoundModeLogic
 *
 * @author wangchuanjian
 */
public class SoundModeLogic implements InterfaceLogic {

    private static final String TAG = "SoundModeLogic";
    private Context mContext;
    private AudioManager mAudioManager;

    public SoundModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
        mAudioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // SoundMode
        WidgetType mSoundMode = new WidgetType();
        // set name for SoundMode
        mSoundMode.setName(res.getStringArray(R.array.sound_mode_string)[0]);
        // set type for SoundMode
        mSoundMode.setType(WidgetType.TYPE_SELECTOR);
        mSoundMode.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                int ret = AudioInterface
                        .setSoundMode(InterfaceValueMaps.voice_mode_logic[i][0]);
                return ret;

            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = AudioInterface.getSoundMode();
                return UtilLauncher.getIndexFromArray(mode,
                        InterfaceValueMaps.voice_mode_logic);
            }
        });
        // set data for SoundMode
        mSoundMode.setData(UtilLauncher
                .createArrayOfParameters(InterfaceValueMaps.voice_mode_logic));
        mWidgetList.add(mSoundMode);

        // InputVolume
        WidgetType mInputVolume = new WidgetType();
        // set name for InputVolume
        mInputVolume.setName(res.getStringArray(R.array.sound_mode_string)[1]);
        // set type for InputVolume
        mInputVolume.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress
        mInputVolume.setMaxProgress(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mInputVolume.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress   mAudioManager.setStreamVolume = "
                            + i);
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                return i;
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG,
                            "getProgress mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) = "
                                    + mAudioManager
                                            .getStreamVolume(AudioManager.STREAM_MUSIC));
                }
                int volume = mAudioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                return volume;
            }
        });
        mWidgetList.add(mInputVolume);

        // Balance
        WidgetType mBalance = new WidgetType();
        // set name for Balance
        mBalance.setName(res.getStringArray(R.array.sound_mode_string)[2]);
        // set type for Balance
        mBalance.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress
        mBalance.setMaxProgress(Constant.BARLENGTH);
        // set offset
        mBalance.setOffset(-50);
        mBalance.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG,
                            "AudioInterface.setBalance(i)="
                                    + AudioInterface.setBalance(i));
                }

                return AudioInterface.setBalance(i);
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG,
                            "AudioInterface.getBalance()="
                                    + AudioInterface.getBalance());
                }
                return AudioInterface.getBalance();
            }
        });
        mWidgetList.add(mBalance);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
    }

}
