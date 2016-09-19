package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
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

/**
 * SR
 *
 * @author wangchuanjian
 *
 */
public class SystemLangureLogic implements InterfaceLogic {

    private Context mContext;
    private String TAG = "AutoPowerdownLogic";
    // private WidgetType mDemoSR;// support SR
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSRValue = InterfaceValueMaps.demo_SR;

    public SystemLangureLogic(Context mContext) {
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
        mAutoPowerdown.setName(res.getStringArray(R.array.system_setting)[1]);
        // set type for SR
        mAutoPowerdown.setType(WidgetType.TYPE_SELECTOR);
        mAutoPowerdown.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set current SR mode :" + i);
                }
                return setLanguage(i);
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "get current SR mode :");
                }
                return getLanguage();
            }
        });
        // set data for SR
        mAutoPowerdown.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.Langure));
        mWidgetList.add(mAutoPowerdown);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }
    /**
     * i:0 Simplified Chinese 1 English
     */
    private int setLanguage(int i) {
        try {
            if (Constant.LOG_TAG) {
                Log.i(TAG,
                        "setLanguage---->" + i + "time --->"
                                + System.currentTimeMillis());
            }
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            switch (i) {
                case 0:
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                    break;
                case 1:
                    config.locale = Locale.US;
                    break;
                default:
                    break;
            }
			
			Intent intent = new Intent(Intent.ACTION_LOCALE_CHANGED);
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            mContext.sendBroadcast(intent);
			
            am.updateConfiguration(config);
            Log.i(TAG,
                    "setLanguage---->over" + i + "time --->"
                            + System.currentTimeMillis());
            SharedPreferences preferences = mContext.getSharedPreferences(
                    Constant.SET_LACOLE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(Constant.RESET_LACOLE, System.currentTimeMillis());
            editor.commit();
            
            
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    
    /**
     * return 0：Simplified Chinese (default) 1：English
     */
    protected int getLanguage() {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            if (Constant.LOG_TAG) {
                Log.d(TAG, "SeniorModeLogic getLanguage config.locale = "
                        + config.locale);
            }
            if (config.locale.equals(Locale.SIMPLIFIED_CHINESE)) {
                return 0;
            } else if (config.locale.equals(Locale.US)) {
                return 1;
            } else {
                setLanguage(0);
                return 0;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            setLanguage(0);
            return 0;
        }
    }
}
