package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * SPDIFDelayLogic
 *
 * @author wangchuanjian
 *
 */
public class SPDIFDelayLogic implements InterfaceLogic {

    private Context mContext;
    private static final String TAG = "SPDIFDelayLogic";

    // private WidgetType mDelay;
    // private List<WidgetType> mWidgetList = null;

    public SPDIFDelayLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // SPDIFDelay
        WidgetType mDelay = new WidgetType();
        // set name for SPDIFDelay
        mDelay.setName(res.getString(R.string.SPDIF_delay));
        // set type for SPDIFDelay
        mDelay.setType(WidgetType.TYPE_PROGRESS);
        // set max value of progress for SPDIFDelay
        mDelay.setMaxProgress(300);
        mDelay.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                return AudioInterface.setSPDIFOutputDelay(i);
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "getProgress i = ");
                }
                return AudioInterface.getSPDIFOutputDelay();
            }
        });
        mWidgetList.add(mDelay);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
