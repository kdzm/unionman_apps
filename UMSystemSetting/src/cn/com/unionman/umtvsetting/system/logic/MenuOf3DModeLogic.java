package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import com.hisilicon.android.tvapi.constant.Enum3DConstant;
import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.Video3DInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * MenuOf3DTo2DLogic
 *
 * @author wangchuanjian
 *
 */
public class MenuOf3DModeLogic implements InterfaceLogic {

    public static final String TAG = "MenuOf3DModeLogic";
    private Context mContext;
    // private WidgetType mMenuOf3DModel;
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mMenuOf3DModelValue = InterfaceValueMaps.menuof_3D_mode;
    private static int[] MenuOfDModeTypeValue = { Enum3DConstant.TV_3DMODE_OFF,
            Enum3DConstant.TV_3DMODE_2DT3D, Enum3DConstant.TV_3DMODE_SBS,
            Enum3DConstant.TV_3DMODE_TAB, Enum3DConstant.TV_3DMODE_AUTO,
            Enum3DConstant.TV_3DMODE_OFF, Enum3DConstant.TV_3DMODE_OFF,
            Enum3DConstant.TV_3DMODE_OFF, Enum3DConstant.TV_3DMODE_OFF,
            Enum3DConstant.TV_3DMODE_OFF };

    public MenuOf3DModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MenuOf3DMode
        WidgetType mMenuOf3DMode = new WidgetType();
        // set name for MenuOf3DMode
        mMenuOf3DMode.setName(res.getStringArray(R.array.menuof3d_setting)[0]);
        // set type for MenuOf3DMode
        mMenuOf3DMode.setType(WidgetType.TYPE_SELECTOR);
        mMenuOf3DMode
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "set 3d mode:  " + i);
                        }
                        return Video3DInterface
                                .set3dMode(MenuOfDModeTypeValue[i]);
                    }

                    @Override
                    public int getSysValue() {
                        int res = Video3DInterface.get3dMode();
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "get 3d mode:  " + res);
                        }
                        return res;
                    }
                });
        // set data for MenuOf3DMode
        mMenuOf3DMode.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.menuof_3D_mode));
        mWidgetList.add(mMenuOf3DMode);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
