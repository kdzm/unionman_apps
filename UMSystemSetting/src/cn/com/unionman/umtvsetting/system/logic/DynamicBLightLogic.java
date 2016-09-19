package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * DynamicBLLogic
 *
 * @author wangchuanjian
 *
 */
public class DynamicBLightLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mDynamicBL;// DynamicBL
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mEnableDynamicBLValue = InterfaceValueMaps.on_off;

    public DynamicBLightLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // DynamicBL
        WidgetType mDynamicBL = new WidgetType();
        // set name for DynamicBL
        mDynamicBL.setName(res.getStringArray(R.array.senior_setting)[0]);
        // set type for DynamicBL
        mDynamicBL.setType(WidgetType.TYPE_SELECTOR);
        mDynamicBL.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = true;
                // 0:OFF 1:ON
                if (i == 0) {
                    onOff = false;
                } else {
                    onOff = true;
                }

                return PictureInterface.enableDynamicBL(onOff);
            }

            @Override
            public int getSysValue() {
                int m = 0;
                boolean flag = PictureInterface.isDynamicBLEnable();
                if (flag) {
                    m = 1;
                } else {
                    m = 0;
                }
                return m;
            }
        });
        // set data for DynamicBL
        mDynamicBL.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mDynamicBL);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
