package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * BlueExtendLogic
 *
 * @author wangchuanjian
 *
 */
public class BlueExtendLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mBlueExtend;// BlueExtend
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mBlueExtendEnableValue = InterfaceValueMaps.on_off;

    public BlueExtendLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // BlueExtend
        WidgetType mBlueExtend = new WidgetType();
        // set name for BlueExtend
        mBlueExtend.setName(res.getStringArray(R.array.pic_setting)[6]);
        // set type for BlueExtend
        mBlueExtend.setType(WidgetType.TYPE_SELECTOR);
        mBlueExtend.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                boolean onOff = false;
                if (i == 1) {
                    onOff = true;
                } else {
                    onOff = false;
                }
                return PictureInterface.enableBlueExtend(onOff);
            }

            @Override
            public int getSysValue() {
                // TODO Auto-generated method stub
                int m = 0;
                boolean flag = PictureInterface.isBlueExtendEnable();
                if (flag) {
                    m = 1;
                } else {
                    m = 0;
                }
                return m;
            }
        });
        // set data for BlueExtend
        mBlueExtend.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mBlueExtend);
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
