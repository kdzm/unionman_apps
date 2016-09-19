package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;


import com.hisilicon.android.tvapi.constant.EnumPictureDemo;
import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * DciDemoLogic
 *
 * @author wangchuanjian
 *
 */
public class DCIDemoLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mDemoDci;// DciDemo
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoDciValue = InterfaceValueMaps.on_off;

    public DCIDemoLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // DCIDemo
        WidgetType mDemoDci = new WidgetType();
        // set name for DCIDemo
        mDemoDci.setName(res.getStringArray(R.array.demo_mode_setting)[1]);
        // set type for DCIDemo
        mDemoDci.setType(WidgetType.TYPE_SELECTOR);
        mDemoDci.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                int res = EnumPictureDemo.DEMO_DCI;
                boolean flag = true;
                if (i == 0) {
                    flag = false;
                } else {
                    flag = true;
                }
                return PictureInterface.setDemoMode(res, flag);
            }

            @Override
            public int getSysValue() {
                int res = EnumPictureDemo.DEMO_DCI;
                boolean i = PictureInterface.getDemoMode(res);
                int a = 0;
                if (i) {
                    a = 1;
                } else {
                    a = 0;
                }
                return a;
            }
        });
        // set data for DCIDemo
        mDemoDci.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mDemoDci);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {

    }

}
