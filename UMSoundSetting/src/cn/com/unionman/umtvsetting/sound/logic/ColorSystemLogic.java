package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.sound.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * ColorSystemLogic
 *
 * @author wangchuanjian
 *
 */
public class ColorSystemLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mColorSystem;// ColorSystem
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mColorSystemValue = InterfaceValueMaps.color_system;

    public ColorSystemLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // ColorSystem
        WidgetType mColorSystem = new WidgetType();
        // set name for ColorSystem
        mColorSystem.setName(res.getStringArray(R.array.channel_setting)[1]);
        // set type for ColorSystem
        mColorSystem.setType(WidgetType.TYPE_SELECTOR);
        mColorSystem.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                return ATVChannelInterface
                        .setColorSystem(InterfaceValueMaps.color_system[i][0]);
            }

            @Override
            public int getSysValue() {

                int m = ATVChannelInterface.getCurrentColorSystem();
                return Util.getIndexFromArray(m,
                        InterfaceValueMaps.color_system);
            }
        });
        // set data for ColorSystem
        mColorSystem.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.color_system));
        mWidgetList.add(mColorSystem);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {

    }

}
