package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;
import com.hisilicon.android.tvapi.CusSystemSetting;
import com.hisilicon.android.tvapi.UmtvManager;
/**
 * SR
 *
 * @author wangchuanjian
 *
 */
public class KeySoundLogic implements InterfaceLogic {

	private static int mScreen = 0;
    private Context mContext;
    private String TAG = "AutoPowerdownLogic";
    // private WidgetType mDemoSR;// support SR
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSRValue = InterfaceValueMaps.demo_SR;

    public KeySoundLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        //key sound
        WidgetType mKeySound = new WidgetType();
        mKeySound.setName(res.getStringArray(R.array.system_setting)[5]);
        mKeySound.setType(WidgetType.TYPE_SELECTOR);
        mKeySound.setmAccessSysValueInterface(new AccessSysValueInterface() {
            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                boolean b = (InterfaceValueMaps.key_sound[i][0] == 0) ? false : true;
                int ret = UmtvManager.getInstance().getSystemSetting().enableKeypadSound(b);
                return ret;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                }
                int mode = UmtvManager.getInstance().getSystemSetting().isKeypadSoundEnable() ? 1 : 0;
                return Util.getIndexFromArray(mode, InterfaceValueMaps.key_sound);
            }
        });
        mKeySound.setData(Util.createArrayOfParameters(InterfaceValueMaps.key_sound));
        mWidgetList.add(mKeySound);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    
}
