package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Util;

import com.hisilicon.android.tvapi.constant.EnumPictureDemo;
import cn.com.unionman.umtvsetting.picture.R;

/**
 * DciDemoLogic
 *
 * @author wangchuanjian
 *
 */
public class DCIDemoLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mDemoDci;// DciDemo
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoDciValue = InterfaceValueMaps.on_off;

    public DCIDemoLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // DCIDemo
        WidgetType mDemoDci = new WidgetType();
        // set name for DCIDemo
        mDemoDci.setName(res.getStringArray(R.array.demo_mode_setting)[1]);
        // set type for DCIDemo
        mDemoDci.setType(WidgetType.TYPE_SELECTOR);
        mDemoDci.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                int res = EnumPictureDemo.DEMO_DCI;
                boolean flag = true;
                if (i == 0) {
                    flag = false;
                } else {
                    flag = true;
                }
                return PictureInterface.setDemoMode(res, flag);
            }

            @Override
            public int getSysValue() {
                int res = EnumPictureDemo.DEMO_DCI;
                boolean i = PictureInterface.getDemoMode(res);
                int a = 0;
                if (i) {
                    a = 1;
                } else {
                    a = 0;
                }
                return a;
            }
        });
        // set data for DCIDemo
        mDemoDci.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mDemoDci);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {

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
