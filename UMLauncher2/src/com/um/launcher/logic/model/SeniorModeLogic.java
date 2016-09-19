
package com.um.launcher.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;


import com.hisilicon.android.tvapi.CusSystemSetting;
import com.hisilicon.android.tvapi.UmtvManager;
import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.AudioInterface;
import com.um.launcher.interfaces.InterfaceValueMaps;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;
import com.um.launcher.model.WidgetType.AccessSysValueInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.UtilLauncher;
/**
 * SeniorMode
 *
 * @author wangchuanjian
 */
public class SeniorModeLogic implements InterfaceLogic {

    private static final String TAG = "SeniorModeLogic";
    private MainActivity mContext;

    public SeniorModeLogic(Context mContext) {
        super();
        this.mContext = (MainActivity) mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // SeniorMode
        WidgetType mSeniorMode = new WidgetType();
        // set name for SeniorMode
        mSeniorMode.setName(res.getStringArray(R.array.senior_mode_string)[0]);
        // set type for SeniorMode
        mSeniorMode.setType(WidgetType.TYPE_SELECTOR);
        mSeniorMode.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                return setLanguage(i);
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = getLanguage();
                return UtilLauncher.getIndexFromArray(mode,
                        InterfaceValueMaps.language_change);
            }
        });
        // set data for SeniorMode
        mSeniorMode.setData(UtilLauncher
                .createArrayOfParameters(InterfaceValueMaps.language_change));
        mWidgetList.add(mSeniorMode);

        // SoundMode
        WidgetType mSoundMode = new WidgetType();
        // set name for SoundMode
        mSoundMode.setName(res.getStringArray(R.array.senior_mode_string)[1]);
        // set type for SoundMode
        mSoundMode.setType(WidgetType.TYPE_SELECTOR);
        mSoundMode.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                int ret = AudioInterface
                        .setSoundMode(InterfaceValueMaps.system_sleep[i][0]);
                return ret;

            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = AudioInterface.getSoundMode();
                return UtilLauncher.getIndexFromArray(mode,
                        InterfaceValueMaps.system_sleep);
            }
        });
        // set data for SoundMode
        mSoundMode.setData(UtilLauncher
                .createArrayOfParameters(InterfaceValueMaps.system_sleep));
        mWidgetList.add(mSoundMode);

        //key sound
        WidgetType mKeySound = new WidgetType();
        mKeySound.setName(res.getStringArray(R.array.senior_mode_string)[2]);
        mKeySound.setType(WidgetType.TYPE_SELECTOR);
        mKeySound.setmAccessSysValueInterface(new AccessSysValueInterface() {
            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                boolean b = (InterfaceValueMaps.key_sound[i][0] == 0) ? false : true;
                int ret = UmtvManager.getInstance().getSystemSetting().enableKeypadSound(b);
                return ret;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = UmtvManager.getInstance().getSystemSetting().isKeypadSoundEnable() ? 1 : 0;
                return UtilLauncher.getIndexFromArray(mode, InterfaceValueMaps.key_sound);
            }
        });
        mKeySound.setData(UtilLauncher.createArrayOfParameters(InterfaceValueMaps.key_sound));
        mWidgetList.add(mKeySound);

        //power on mode
        WidgetType mPowerOnMode = new WidgetType();
        mPowerOnMode.setName(res.getStringArray(R.array.senior_mode_string)[3]);
        mPowerOnMode.setType(WidgetType.TYPE_SELECTOR);
        mPowerOnMode.setmAccessSysValueInterface(new AccessSysValueInterface() {
            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                boolean b = (InterfaceValueMaps.power_on_mode[i][0] == 0) ? false : true;
                int ret = UmtvManager.getInstance().getSystemSetting().enablePowerOnLuncher(b);
                return ret;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = UmtvManager.getInstance().getSystemSetting().isPowerOnLuncherEnable() ? 1 : 0;
                return UtilLauncher.getIndexFromArray(mode, InterfaceValueMaps.power_on_mode);
            }
        });
        mPowerOnMode.setData(UtilLauncher.createArrayOfParameters(InterfaceValueMaps.power_on_mode));
        mWidgetList.add(mPowerOnMode);

        //blue screen
        WidgetType mBlueScreen = new WidgetType();
        mBlueScreen.setName(res.getStringArray(R.array.senior_mode_string)[4]);
        mBlueScreen.setType(WidgetType.TYPE_SELECTOR);
        mBlueScreen.setmAccessSysValueInterface(new AccessSysValueInterface() {
            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                boolean b = (InterfaceValueMaps.blue_screen[i][0] == 0) ? false : true;
                int ret = UmtvManager.getInstance().getSystemSetting().enableScreenBlue(b);
                return ret;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = UmtvManager.getInstance().getSystemSetting().isScreenBlueEnable() ? 1 : 0;
                return UtilLauncher.getIndexFromArray(mode, InterfaceValueMaps.blue_screen);
            }
        });
        mBlueScreen.setData(UtilLauncher.createArrayOfParameters(InterfaceValueMaps.blue_screen));
        mWidgetList.add(mBlueScreen);

        return mWidgetList;
    }

    /**
     * return 0：Simplified Chinese (default) 1：English
     */
    protected int getLanguage() {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            if (Constant.LOG_TAG) {
                Log.d(TAG, "SeniorModeLogic getLanguage config.locale = "
                        + config.locale);
            }
            if (config.locale.equals(Locale.SIMPLIFIED_CHINESE)) {
                return 0;
            } else if (config.locale.equals(Locale.US)) {
                return 1;
            } else {
                setLanguage(0);
                return 0;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            setLanguage(0);
            return 0;
        }
    }

    /**
     * i:0 Simplified Chinese 1 English
     */
    protected int setLanguage(int i) {
        try {
            if (Constant.LOG_TAG) {
                Log.i(TAG,
                        "setLanguage---->" + i + "time --->"
                                + System.currentTimeMillis());
            }
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            switch (i) {
                case 0:
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                    break;
                case 1:
                    config.locale = Locale.US;
                    break;
                default:
                    break;
            }
            am.updateConfiguration(config);
            Log.i(TAG,
                    "setLanguage---->over" + i + "time --->"
                            + System.currentTimeMillis());
            SharedPreferences preferences = mContext.getSharedPreferences(
                    Constant.SET_LACOLE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(Constant.RESET_LACOLE, System.currentTimeMillis());
            editor.commit();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public void setHandler(Handler handler) {
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
    }

}
