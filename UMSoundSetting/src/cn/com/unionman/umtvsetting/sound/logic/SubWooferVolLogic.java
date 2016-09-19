package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.sound.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.sound.util.Constant;

import cn.com.unionman.umtvsetting.sound.R;

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
