package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.Video3DInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;

/**
 * MenuOf3DView
 *
 * @author wangchuanjian
 *
 */
public class MenuOf3DViewLogic implements InterfaceLogic {

    private static final String TAG = "MenuOf3DViewLogic";
    private Context mContext;

    // private WidgetType mMenuOf3DView;// MenuOf3DView
    // private List<WidgetType> mWidgetList = null;

    public MenuOf3DViewLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MenuOf3DView
        WidgetType mMenuOf3DView = new WidgetType();
        // set name for MenuOf3DView
        mMenuOf3DView.setName(res.getStringArray(R.array.menuof3d_setting)[4]);
        // set type for MenuOf3DView
        mMenuOf3DView.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress for MenuOf3DView
        mMenuOf3DView.setMaxProgress(10);
        mMenuOf3DView
                .setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                return Video3DInterface.setView(i);
            }

            @Override
            public int getProgress() {
                int res = 0;
                res = Video3DInterface.getView();
                return res;
            }
        });
        mWidgetList.add(mMenuOf3DView);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {

    }

}
