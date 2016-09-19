package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;

/**
 * BacklightLogic
 *
 * @author wangchuanjian
 *
 */
public class BacklightLogic implements InterfaceLogic {
    private static final String TAG = "BacklightLogic";
    private Context mContext;

    // private WidgetType mBacklight;// Backlight
    // private List<WidgetType> mWidgetList = null;

    public BacklightLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // Backlight
        WidgetType mBacklight = new WidgetType();
        // set name for Backlight
        mBacklight.setName(res.getStringArray(R.array.senior_setting)[1]);
        // set type for Backlight
        mBacklight.setType(WidgetType.TYPE_PROGRESS);
        mBacklight.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "PictureInterface.setBacklight(i) = " + i);
                }

                return PictureInterface.setBacklight(i);
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                }

                return PictureInterface.getBacklight();
            }
        });
        mWidgetList.add(mBacklight);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
