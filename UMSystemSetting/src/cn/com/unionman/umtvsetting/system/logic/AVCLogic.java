package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * AVCLogic
 *
 * @author wangchuanjian
 *
 */
public class AVCLogic implements InterfaceLogic {
    private static final String TAG = "AVCLogic";
    private Context mContext;

    // private WidgetType mEnableAVC;// Avc
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mEnableAVCValue = InterfaceValueMaps.on_off;

    public AVCLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // AVC
        WidgetType mEnableAVC = new WidgetType();
        // set name for AVC
        mEnableAVC.setName(res.getStringArray(R.array.voice_setting)[4]);
        // set type for AVC
        mEnableAVC.setType(WidgetType.TYPE_SELECTOR);
        mEnableAVC.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = false;
                if (i == 1) {
                    onOff = true;
                } else {
                    onOff = false;
                }

                int res = AudioInterface.enableAVC(onOff);
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "AudioInterface.enableAVC: " + res);
                }
                return res;
            }

            @Override
            public int getSysValue() {
                // TODO Auto-generated method stub
                int m = 0;
                boolean flag = AudioInterface.isAVCEnable();
                if (flag) {
                    m = 1;
                } else {
                    m = 0;
                }
                return m;
            }
        });
        // set data for AVC
        mEnableAVC.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mEnableAVC);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
