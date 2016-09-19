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
 * SharpnessLogic
 *
 * @author wangchuanjian
 *
 */
public class SharpnessDemoLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mDemoSharppen;// support SHARPEN
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSharppenValue = InterfaceValueMaps.on_off;

    public SharpnessDemoLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // Sharpness
        WidgetType mDemoSharpness = new WidgetType();
        // set name for Sharpness
        mDemoSharpness
                .setName(res.getStringArray(R.array.demo_mode_setting)[3]);
        // set type for Sharpness
        mDemoSharpness.setType(WidgetType.TYPE_SELECTOR);
        mDemoSharpness
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        int res = EnumPictureDemo.DEMO_SHARPNESS;
                        boolean flag = true;
                        // 0:OFF 1:ON
                        if (i == 0) {
                            flag = false;
                        } else {
                            flag = true;
                        }
                        return PictureInterface.setDemoMode(res, flag);
                    }

                    @Override
                    public int getSysValue() {
                        int res = EnumPictureDemo.DEMO_SHARPNESS;
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
        // set data for Sharpness
        mDemoSharpness.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mDemoSharpness);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
