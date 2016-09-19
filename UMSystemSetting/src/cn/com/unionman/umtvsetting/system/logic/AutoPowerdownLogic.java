package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * SR
 *
 * @author wangchuanjian
 *
 */
public class AutoPowerdownLogic implements InterfaceLogic {

    private Context mContext;
    private String TAG = "AutoPowerdownLogic";
    // private WidgetType mDemoSR;// support SR
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSRValue = InterfaceValueMaps.demo_SR;

    public AutoPowerdownLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // SR
        WidgetType mAutoPowerdown = new WidgetType();
        // set name for SR
        mAutoPowerdown.setName(res.getStringArray(R.array.power_setting)[2]);
        // set type for SR
        mAutoPowerdown.setType(WidgetType.TYPE_SELECTOR);
        mAutoPowerdown.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set current SR mode :" + i);
                }
                return -1;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "get current SR mode :");
                }
                return 0;
            }
        });
        // set data for SR
        mAutoPowerdown.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.Auto_Powerdown));
        mWidgetList.add(mAutoPowerdown);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
