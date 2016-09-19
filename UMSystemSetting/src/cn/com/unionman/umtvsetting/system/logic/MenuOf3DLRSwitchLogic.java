package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.Video3DInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * MenuOf3DLRSwitchLogic
 *
 * @author wangchuanjian
 *
 */
public class MenuOf3DLRSwitchLogic implements InterfaceLogic {

    // private static final String TAG = "MenuOf3DEyeLogic";
    private Context mContext;

    // private WidgetType mMenuOf3DExchange;// MenuOf3DExchange
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mMenuOf3DEyeValue = InterfaceValueMaps.on_off;

    public MenuOf3DLRSwitchLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MenuOf3DExchange
        WidgetType mMenuOf3DExchange = new WidgetType();
        // set name for MenuOf3DExchange
        mMenuOf3DExchange
                .setName(res.getStringArray(R.array.menuof3d_setting)[1]);
        // set type for MenuOf3DExchange
        mMenuOf3DExchange.setType(WidgetType.TYPE_SELECTOR);
        mMenuOf3DExchange
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        // TODO Auto-generated method stub
                        boolean onoff = false;
                        if(i == 0){
                           onoff = false;
                        }
                        else{
                           onoff = true;
                        }
                        return Video3DInterface.switchLR(onoff);
                    }

                    @Override
                    public int getSysValue() {
                        // TODO Auto-generated method stub
                        int i = 0;
                        boolean res = Video3DInterface.getSwitchLR();
                        if(res){
                            i = 1;
                        }
                        else{
                            i = 0;
                        }
                        return i;
                    }
                });
        // set data for MenuOf3DExchange
        mMenuOf3DExchange.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mMenuOf3DExchange);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
