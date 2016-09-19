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
 * TrebleLogic
 *
 * @author wangchuanjian
 *
 */
public class TrebleLogic implements InterfaceLogic {

    private static final String TAG = "TrebleLogic";
    private Context mContext;

    // private WidgetType mTreble;// Treble
    // private List<WidgetType> mWidgetList = null;

    public TrebleLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // Treble
        WidgetType mTreble = new WidgetType();
        // set name for Treble
        mTreble.setName(res.getStringArray(R.array.voice_setting)[1]);
        // set type for Treble
        mTreble.setType(WidgetType.TYPE_PROGRESS);
        mTreble.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }

                return AudioInterface.setTreble(i);
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                }
                return AudioInterface.getTreble();
            }
        });
        mWidgetList.add(mTreble);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
