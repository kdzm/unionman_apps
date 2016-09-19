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
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;

/**
 * SR
 *
 * @author wangchuanjian
 *
 */
public class SecureModeLogic implements InterfaceLogic {

	private static int mSecureMode = 0;
    private Context mContext;
    private String TAG = "SecureModeLogic";
    // private WidgetType mDemoSR;// support SR
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSRValue = InterfaceValueMaps.demo_SR;

    public SecureModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // SR
        WidgetType mAutoPowerdown = new WidgetType();
        // set name for SR
        mAutoPowerdown.setName(res.getStringArray(R.array.system_setting)[10]);
        // set type for SR
        mAutoPowerdown.setType(WidgetType.TYPE_SELECTOR);
        mAutoPowerdown.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set current SR mode :" + i);
                }
                setSecureMode(i);
                return 1;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "get current SR mode :");
                }
                return getSecureMode();
            }
        });
        // set data for SR
        mAutoPowerdown.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mAutoPowerdown);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    
    private int getSecureMode(){
    	return (isADBEnable()||isNonMarketAppsAllowed())?0:1;
    }
    private int setSecureMode(int i){
    	if (i==0) {
    		setADBEnable(true);
    		setNonMarketAppsAllowed(true);
    	} else {
    		setADBEnable(false);
    		setNonMarketAppsAllowed(false);
    	}
    	return 1;
    } 
    private boolean isADBEnable(){
    	return Settings.Global.getInt(
    			mContext.getContentResolver(),Settings.Global.ADB_ENABLED, 0) != 0;
    }
    private void setADBEnable(boolean enabled){
    	Log.i(TAG, "persist.sys.usb.config"+System.getProperty("persist.sys.usb.config"));
    	System.setProperty("persist.sys.usb.config", enabled?"adb":"none");
        Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.ADB_ENABLED, enabled?1:0);
    }
    
    private boolean isNonMarketAppsAllowed() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                                      Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0;
    }
    private void setNonMarketAppsAllowed(boolean enabled) {
        // Change the system setting
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS,
                                enabled ? 1 : 0);
    }
}
