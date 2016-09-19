package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;

/**
 * SubWooferVolLogic
 *
 * @author wangchuanjian
 *
 */
public class SubWooferVolLogic implements InterfaceLogic {

    private static final String TAG = "SubWooferVolLogic";
    private Context mContext;

    // private WidgetType mSubWooferVol;// SubWooferVol
    // private List<WidgetType> mWidgetList = null;

    public SubWooferVolLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // SubWooferVol
        WidgetType mSubWooferVol = new WidgetType();
        // set name for SubWooferVol
        mSubWooferVol.setName(res.getStringArray(R.array.voice_setting)[10]);
        // set type for SubWooferVol
        mSubWooferVol.setType(WidgetType.TYPE_PROGRESS);
        mSubWooferVol
                .setmAccessProgressInterface(new AccessProgressInterface() {

                    @Override
                    public int setProgress(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setProgress i = " + i);
                        }
                        return AudioInterface.setSubWooferVolume(i);
                    }

                    @Override
                    public int getProgress() {
                        if (Constant.LOG_TAG) {
                        }
                        return AudioInterface.getSubWooferVolume();
                    }
                });
        mWidgetList.add(mSubWooferVol);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
