package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * SRS
 *
 * @author wangchuanjian
 *
 */
public class SRSLogic implements InterfaceLogic {
    private static final String TAG = "SRSLogic";
    private Context mContext;
    private WidgetType mEnableSRS, mSRSTreble, mSRSBass;
    // private List<WidgetType> mWidgetList = null;
    private Handler mHandler;

    // private int[][] mSRSEnableValue = InterfaceValueMaps.on_off;

    public SRSLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // mEnableSRS
        mEnableSRS = new WidgetType();
        // set name for EnableSRS
        mEnableSRS.setName(res.getStringArray(R.array.SRS_setting)[0]);
        // set type for EnableSRS
        mEnableSRS.setType(WidgetType.TYPE_SELECTOR);
        mEnableSRS.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = true;
                // 0:OFF 1:ON
                if (i == 0) {
                    onOff = false;
                } else {
                    onOff = true;
                }
                int ret = AudioInterface.EnableSRS(onOff);
                if (!onOff) {
                    // AudioInterface.enableSRSTreble(onoff);
                    // AudioInterface.enableSRSBass(onoff);
                    mSRSTreble.setEnable(false);
                    mSRSBass.setEnable(false);
                } else {
                    if (AudioInterface.isSRSTrebleEnable()) {
                        AudioInterface.enableSRSTreble(true);
                    }
                    if (AudioInterface.isSRSBassEnable()) {
                        AudioInterface.enableSRSBass(true);
                    }
                    mSRSTreble.setEnable(true);
                    mSRSBass.setEnable(true);
                }
                Message msg = new Message();
                msg.what = Constant.SETTING_UI_REFRESH_VIEWS;
                List<String> Stringlist = new ArrayList<String>();
                Stringlist.add(mContext.getResources().getStringArray(
                        R.array.SRS_setting)[1]);
                Stringlist.add(mContext.getResources().getStringArray(
                        R.array.SRS_setting)[2]);
                msg.obj = Stringlist;
                mHandler.sendMessage(msg);
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "EnableSRS--->setSysValue-->i:" + i + "    result:"
                            + ret);
                }
                return ret;
            }

            @Override
            public int getSysValue() {
                int m = 1;
                boolean flag = AudioInterface.isSRSEnable();
                if (flag) {
                    m = 1;

                } else {
                    m = 0;
                }
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "EnableSRS--->getSysValue-->flag:" + flag
                            + "    m:" + m);
                }
                return Util.getIndexFromArray(m, InterfaceValueMaps.on_off);
            }
        });
        // set data for EnableSRS
        mEnableSRS.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mEnableSRS);

        // mSRSTreble
        mSRSTreble = new WidgetType();
        // set name for SRSTreble
        mSRSTreble.setName(res.getStringArray(R.array.SRS_setting)[1]);
        // set type for SRSTreble
        mSRSTreble.setType(WidgetType.TYPE_SELECTOR);
        mSRSTreble.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = false;
                // 0:OFF 1:ON
                if (i == 1) {
                    onOff = true;
                } else {
                    onOff = false;
                }

                int result = AudioInterface.enableSRSTreble(onOff);
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "SRSTreable--->setSysValue-->i:" + i
                            + "    result:" + result);
                }
                return result;
            }

            @Override
            public int getSysValue() {
                int m = 0;
                boolean res = AudioInterface.isSRSTrebleEnable();
                if (res) {
                    m = 1;
                } else {
                    m = 0;
                }
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "SRSTreable--->getSysValue-->m:" + m);
                }
                return Util.getIndexFromArray(m, InterfaceValueMaps.on_off);
            }
        });
        // set data for SRSTreble
        mSRSTreble.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mSRSTreble);

        // mSRSBass
        mSRSBass = new WidgetType();
        // set name for SRSBass
        mSRSBass.setName(res.getStringArray(R.array.SRS_setting)[2]);
        // set type for SRSBass
        mSRSBass.setType(WidgetType.TYPE_SELECTOR);
        mSRSBass.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onoff = false;
                // 0:OFF 1:ON
                if (i == 1) {
                    onoff = true;
                } else {
                    onoff = false;
                }
                int result = AudioInterface.enableSRSBass(onoff);
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "SRSBass--->setSysValue-->i:" + i + "    result:"
                            + result);
                }
                return result;
            }

            @Override
            public int getSysValue() {
                int m = 0;
                boolean flag = AudioInterface.isSRSBassEnable();
                if (flag) {
                    m = 1;
                } else {
                    m = 0;
                }
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "SRSBass--->getSysValue-->flag:" + flag);
                }
                return Util.getIndexFromArray(m, InterfaceValueMaps.on_off);
            }
        });
        // set data for SRSBass
        mSRSBass.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mSRSBass);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;

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
