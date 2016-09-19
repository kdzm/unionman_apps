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
 * BalanceLogic
 *
 * @author wangchuanjian
 *
 */
public class BalanceLogic implements InterfaceLogic {
    private static final String TAG = "BalanceLogic";
    private Context mContext;

    // private WidgetType mBalance;// Balance
    // private List<WidgetType> mWidgetList = null;

    public BalanceLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // Balance
        WidgetType mBalance = new WidgetType();
        // set name for Balance
        mBalance.setName(res.getStringArray(R.array.voice_setting)[2]);
        // set type for Balance
        mBalance.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress for Balance
        mBalance.setMaxProgress(100);
        // set offset
        mBalance.setOffset(-50);
        mBalance.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                return AudioInterface.setBalance(i);
            }

            @Override
            public int getProgress() {
                int i = 50;
                if (Constant.LOG_TAG) {
                    Log.d("Tag", "getProgress i = " + i);
                }
                return AudioInterface.getBalance();
            }
        });
        mWidgetList.add(mBalance);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
