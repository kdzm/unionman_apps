package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.interfaces.Video3DInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * MenuOf3DTo2DLogic
 *
 * @author wangchuanjian
 *
 */
public class MenuOf3DTo2DLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mMenuOf3DTo2D;// MenuOf3DTo2D
    // private List<WidgetType> mWidgetList = null;
    // private boolean isOnOff = false;
    // private int[][] mMenuOf3DTo2DValue = InterfaceValueMaps.on_off;

    public MenuOf3DTo2DLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MenuOf3DTo2D
        WidgetType mMenuOf3DTo2D = new WidgetType();
        // set name for MenuOf3DTo2D
        mMenuOf3DTo2D.setName(res.getStringArray(R.array.menuof3d_setting)[2]);
        // set type for MenuOf3DTo2D
        mMenuOf3DTo2D.setType(WidgetType.TYPE_SELECTOR);
        mMenuOf3DTo2D
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        boolean isOnOff = false;
                        // 0:OFF 1:ON
                        if (i == 1) {
                            isOnOff = true;
                        } else {
                            isOnOff = false;
                        }
                        Video3DInterface.set3dto2d(isOnOff);
                        return i;
                    }

                    @Override
                    public int getSysValue() {
                        int i = 0;
                        boolean isOnOff = Video3DInterface.get3dto2d();
                        if (isOnOff)
                            i = 1;
                        else
                            i = 0;
                        return i;
                    }
                });
        // set data for MenuOf3DTo2D
        mMenuOf3DTo2D.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mMenuOf3DTo2D);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
