package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.interfaces.SystemSettingInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * SingnalListenerLogic
 *
 * @author wangchuanjian
 *
 */
public class SingleListenerLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mSignalListener;
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mEnableBacklightValue = InterfaceValueMaps.on_off;

    public SingleListenerLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // String title = res.getStringArray(R.array.senior_setting)[2];

        // SignalListener
        WidgetType mSignalListener = new WidgetType();
        // set name for SignalListener
        mSignalListener.setName(res.getStringArray(R.array.senior_setting)[2]);
        // set type for SignalListener
        mSignalListener.setType(WidgetType.TYPE_SELECTOR);
        mSignalListener
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        boolean onOff = true;
                        // 0:OFF 1:ON
                        if (i == 0) {
                            onOff = false;
                        } else {
                            onOff = true;
                        }
                        return PictureInterface.enableBacklight(onOff);
                    }

                    @Override
                    public int getSysValue() {
                        int m = 0;
                        boolean flag = PictureInterface.isBacklightEnable();
                        if (flag) {
                            m = 1;
                        } else {
                            m = 0;
                        }
                        return m;
                    }
                });
        // set data for SignalListener
        mSignalListener.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mSignalListener);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
