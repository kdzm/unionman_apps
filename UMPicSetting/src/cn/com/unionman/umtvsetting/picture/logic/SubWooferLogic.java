package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Util;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * SubWooferLogic
 *
 * @author wangchuanjian
 *
 */
public class SubWooferLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mEnableSubWoofer;// SubWoofer
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mEnableSubWooferValue = InterfaceValueMaps.on_off;

    public SubWooferLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // SubWoofer
        WidgetType mEnableSubWoofer = new WidgetType();
        // set name for SubWoofer
        mEnableSubWoofer.setName(res.getStringArray(R.array.voice_setting)[9]);
        // set type for SubWoofer
        mEnableSubWoofer.setType(WidgetType.TYPE_SELECTOR);
        mEnableSubWoofer
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        boolean onoff = true;
                        // 0:OFF 1:ON
                        if (i == 0) {
                            onoff = false;
                        } else {
                            onoff = true;
                        }
                        return AudioInterface.enableSubWoofer(onoff);
                    }

                    @Override
                    public int getSysValue() {
                        int m = 0;
                        boolean flag = AudioInterface.isSubWooferEnable();
                        if (flag) {
                            m = 1;
                        } else {
                            m = 0;
                        }
                        return m;
                    }
                });
        // set data for SubWoofer
        mEnableSubWoofer.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mEnableSubWoofer);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    private static final String TAG = "PictureModeLogic";
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
