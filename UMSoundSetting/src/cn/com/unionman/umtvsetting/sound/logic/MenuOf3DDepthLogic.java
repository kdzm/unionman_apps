package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.sound.interfaces.Video3DInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.sound.util.Constant;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * MenuOf3DTo2DLogic
 *
 * @author wangchuanjian
 *
 */
public class MenuOf3DDepthLogic implements InterfaceLogic {

    private static final String TAG = "MenuOf3DDepthLogic";
    private Context mContext;

    // private WidgetType mDepth;// MenuOf3DDepth
    // private List<WidgetType> mWidgetList = null;

    public MenuOf3DDepthLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MenuOf3DDepth
        WidgetType mDepth = new WidgetType();
        // set name for MenuOf3DDepth
        mDepth.setName(res.getStringArray(R.array.menuof3d_setting)[3]);
        // set type for MenuOf3DDepth
        mDepth.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress for MenuOf3DDepth
        mDepth.setMaxProgress(10);
        mDepth.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set 3d depth = " + i);
                }
                return Video3DInterface.setDepth(i);
            }

            @Override
            public int getProgress() {
                int res = Video3DInterface.getDepth();
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "get 3d Depth = " + res);
                }
                return res;
            }
        });
        mWidgetList.add(mDepth);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {

    }

}
