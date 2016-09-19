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
 * DCILogic
 *
 * @author wangchuanjian
 *
 */
public class DCILogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mEnableDCI;// Dci
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDciEnableValue = InterfaceValueMaps.on_off;

    public DCILogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // DCI
        WidgetType mEnableDCI = new WidgetType();
        // set name for DCI
        mEnableDCI.setName(res.getStringArray(R.array.pic_setting)[4]);
        // set type for DCI
        mEnableDCI.setType(WidgetType.TYPE_SELECTOR);
        mEnableDCI.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = false;
                int n = InterfaceValueMaps.on_off[i][0];
                if (n == 0) {
                    onOff = false;
                } else {
                    onOff = true;
                }
                int res = PictureInterface.enableDCI(onOff);
                return res;
            }

            @Override
            public int getSysValue() {
                // TODO Auto-generated method stub
                int m = 0;
                boolean flag = PictureInterface.isDCIEnable();
                if (flag) {
                    m = 1;
                } else {
                    m = 0;
                }
                return m;
            }
        });
        // set data for DCI
        mEnableDCI.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mEnableDCI);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
