package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;

import com.hisilicon.android.tvapi.constant.EnumSoundEqBand;
import cn.com.unionman.umtvsetting.picture.R;

/**
 * set EqLogic
 *
 * @author wangchuanjian
 *
 */
public class EQLogic implements InterfaceLogic {

    private static final String TAG = "EQLogic";
    private Context mContext;

    // private WidgetType mEQBAND_120HZ, mEQBAND_500HZ, mEQBAND_1d5kHZ,
    // mEQBAND_5kHZ, mEQBAND_10kHZ;

    // private List<WidgetType> mWidgetList = null;
    // private int[][] mSoundModeValue = InterfaceValueMaps.sound_mode;
    // private Handler mHandler;

    public EQLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // mEQBAND_120HZ
        WidgetType mEQBAND_120HZ = new WidgetType();
        // set name for mEQBAND_120HZ
        mEQBAND_120HZ.setName(res.getStringArray(R.array.EQ_setting)[0]);
        // set type for mEQBAND_120HZ
        mEQBAND_120HZ.setType(WidgetType.TYPE_PROGRESS);
        mEQBAND_120HZ
                .setmAccessProgressInterface(new AccessProgressInterface() {

                    @Override
                    public int setProgress(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setProgress i = " + i);
                        }

                        AudioInterface
                                .setSoundMode(InterfaceValueMaps.sound_mode[6][0]);
                        return AudioInterface.setEQ(
                                EnumSoundEqBand.EQBAND_120HZ, i);
                    }

                    @Override
                    public int getProgress() {
                        int i = 50;
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "getProgress i = " + i);
                        }
                        return AudioInterface
                                .getEQ(EnumSoundEqBand.EQBAND_120HZ);
                    }
                });
        mWidgetList.add(mEQBAND_120HZ);

        // mEQBAND_500HZ
        WidgetType mEQBAND_500HZ = new WidgetType();
        // set name for mEQBAND_500HZ
        mEQBAND_500HZ.setName(res.getStringArray(R.array.EQ_setting)[1]);
        // set type for mEQBAND_500HZ
        mEQBAND_500HZ.setType(WidgetType.TYPE_PROGRESS);
        mEQBAND_500HZ
                .setmAccessProgressInterface(new AccessProgressInterface() {

                    @Override
                    public int setProgress(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setProgress i = " + i);
                        }
                        AudioInterface
                                .setSoundMode(InterfaceValueMaps.sound_mode[6][0]);
                        return AudioInterface.setEQ(
                                EnumSoundEqBand.EQBAND_500HZ, i);
                    }

                    @Override
                    public int getProgress() {
                        int i = 50;
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "getProgress i = " + i);
                        }
                        return AudioInterface
                                .getEQ(EnumSoundEqBand.EQBAND_500HZ);
                    }
                });
        mWidgetList.add(mEQBAND_500HZ);

        // mEQBAND_1d5kHZ
        WidgetType mEQBAND_1d5kHZ = new WidgetType();
        // set name for mEQBAND_1d5kHZ
        mEQBAND_1d5kHZ.setName(res.getStringArray(R.array.EQ_setting)[2]);
        // set type for mEQBAND_1d5kHZ
        mEQBAND_1d5kHZ.setType(WidgetType.TYPE_PROGRESS);
        mEQBAND_1d5kHZ
                .setmAccessProgressInterface(new AccessProgressInterface() {

                    @Override
                    public int setProgress(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setProgress i = " + i);
                        }
                        AudioInterface
                                .setSoundMode(InterfaceValueMaps.sound_mode[6][0]);
                        return AudioInterface.setEQ(
                                EnumSoundEqBand.EQBAND_1d5kHZ, i);
                    }

                    @Override
                    public int getProgress() {
                        int i = 50;
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "getProgress i = " + i);
                        }
                        return AudioInterface
                                .getEQ(EnumSoundEqBand.EQBAND_1d5kHZ);
                    }
                });
        mWidgetList.add(mEQBAND_1d5kHZ);

        // mEQBAND_5kHZ
        WidgetType mEQBAND_5kHZ = new WidgetType();
        // set name for mEQBAND_5kHZ
        mEQBAND_5kHZ.setName(res.getStringArray(R.array.EQ_setting)[3]);
        // set type for mEQBAND_5kHZ
        mEQBAND_5kHZ.setType(WidgetType.TYPE_PROGRESS);
        mEQBAND_5kHZ.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                AudioInterface
                        .setSoundMode(InterfaceValueMaps.sound_mode[6][0]);
                return AudioInterface.setEQ(EnumSoundEqBand.EQBAND_5kHZ, i);
            }

            @Override
            public int getProgress() {
                int i = 50;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = " + i);
                }
                return AudioInterface.getEQ(EnumSoundEqBand.EQBAND_5kHZ);
            }
        });
        mWidgetList.add(mEQBAND_5kHZ);

        // mEQBAND_10kHZ
        WidgetType mEQBAND_10kHZ = new WidgetType();
        // set name for mEQBAND_10kHZ
        mEQBAND_10kHZ.setName(res.getStringArray(R.array.EQ_setting)[4]);
        // set type for mEQBAND_10kHZ
        mEQBAND_10kHZ.setType(WidgetType.TYPE_PROGRESS);
        mEQBAND_10kHZ
                .setmAccessProgressInterface(new AccessProgressInterface() {

                    @Override
                    public int setProgress(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setProgress i = " + i);
                        }
                        AudioInterface
                                .setSoundMode(InterfaceValueMaps.sound_mode[6][0]);
                        return AudioInterface.setEQ(
                                EnumSoundEqBand.EQBAND_10kHZ, i);
                    }

                    @Override
                    public int getProgress() {
                        int i = 50;
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "getProgress i = " + i);
                        }
                        return AudioInterface
                                .getEQ(EnumSoundEqBand.EQBAND_10kHZ);
                    }
                });
        mWidgetList.add(mEQBAND_10kHZ);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
