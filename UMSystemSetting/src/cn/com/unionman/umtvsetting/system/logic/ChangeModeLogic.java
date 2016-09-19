package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * ChangeModeLogic
 *
 * @author wangchuanjian
 *
 */
public class ChangeModeLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mChangeMode;// ChangeMode
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mChangeModeEnableValue =
    // InterfaceValueMaps.change_mode_enable;

    public ChangeModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // ChangeMode
        WidgetType mChangeMode = new WidgetType();
        // set name for ChangeMode
        mChangeMode.setName(res.getStringArray(R.array.channel_setting)[7]);
        // set type for ChangeMode
        mChangeMode.setType(WidgetType.TYPE_SELECTOR);
        mChangeMode.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = false;
                if (i == 1) {
                    onOff = true;
                } else {
                    onOff = false;
                }
                return ATVChannelInterface.enableChangeMode(onOff);
            }

            @Override
            public int getSysValue() {
                int m = 0;
                boolean onoff = ATVChannelInterface.isChangeModeEnable();
                if (onoff) {
                    m = 1;
                } else {
                    m = 0;
                }
                return Util.getIndexFromArray(m,
                        InterfaceValueMaps.change_mode_enable);
            }
        });
        // set data for ChangeMode
        mChangeMode
                .setData(Util
                        .createArrayOfParameters(InterfaceValueMaps.change_mode_enable));
        mWidgetList.add(mChangeMode);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
