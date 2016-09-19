
package com.um.launcher.logic.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.um.launcher.R;
import com.um.launcher.interfaces.PictureInterface;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;
import com.um.launcher.model.WidgetType.AccessProgressInterface;
import com.um.launcher.util.Constant;

/**
 * picture model
 *
 * @author wangchuanjian
 */

public class PictureModeLogic implements InterfaceLogic {

    private static final String TAG = "PictureLogic";
    private Context mContext;
    private Handler mHandler;

    public PictureModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // Brightness
        WidgetType mBrightness = new WidgetType();
        // set name for Brightness
        mBrightness.setName(res.getStringArray(R.array.picture_mode_string)[1]);
        // set type for Brightness
        mBrightness.setType(WidgetType.TYPE_PROGRESS);
        mBrightness.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                PictureInterface.setBrightness(i);

                refreshPictureSelector();

                return i;
            }

            @Override
            public int getProgress() {
                // int i = 50;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                }
                return PictureInterface.getBrightness();
            }
        });
        mWidgetList.add(mBrightness);

        // Contrast
        WidgetType mContrast = new WidgetType();
        // set name for Contrast
        mContrast.setName(res.getStringArray(R.array.picture_mode_string)[2]);
        // set type for Contrast
        mContrast.setType(WidgetType.TYPE_PROGRESS);
        mContrast.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }

                PictureInterface.setContrast(i);
                return i;
            }

            @Override
            public int getProgress() {
                // int i = 50;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                }
                return PictureInterface.getContrast();
            }
        });
        mWidgetList.add(mContrast);

        // Saturation
        WidgetType mSaturation = new WidgetType();
        // set name for Saturation
        mSaturation.setName(res.getStringArray(R.array.picture_mode_string)[3]);
        // set type for Saturation
        mSaturation.setType(WidgetType.TYPE_PROGRESS);
        mSaturation.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                }
                PictureInterface.setHue(i);
                return i;
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                }
                return PictureInterface.getHue();
            }
        });
        mWidgetList.add(mSaturation);

        // Hue
        WidgetType mHue = new WidgetType();
        // set name for Hue
        mHue.setName(res.getStringArray(R.array.picture_mode_string)[4]);
        // set type for Hue
        mHue.setType(WidgetType.TYPE_PROGRESS);
        mHue.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                PictureInterface.setSaturation(i);
                return i;
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                }
                return PictureInterface.getSaturation();
            }
        });
        mWidgetList.add(mHue);

        // Backlight
        WidgetType mBacklight = new WidgetType();
        // set name for Backlight
        mBacklight.setName(res.getStringArray(R.array.picture_mode_string)[5]);
        // set type for Backlight
        mBacklight.setType(WidgetType.TYPE_PROGRESS);
        mBacklight.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                PictureInterface.setBacklight(i);
                return i;

            }

            @Override
            public int getProgress() {
                // int i = 50;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                    // Log.d(TAG, "getProgress i = " + i);
                }
                return PictureInterface.getBacklight();
            }
        });
        mWidgetList.add(mBacklight);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * refresh the selector of Picture
     */
    private void refreshPictureSelector() {
        Message msg = new Message();
        msg.what = Constant.SETTING_UI_REFRESH_VIEWS;
        List<String> Stringlist = new ArrayList<String>();
        Stringlist.add(mContext.getResources().getStringArray(
                R.array.picture_mode_string)[0]);
        msg.obj = Stringlist;
        mHandler.sendMessage(msg);
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
    }

}
