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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
/**
 * SR
 *
 * @author wangchuanjian
 *
 */
public class BlueToothLogic implements InterfaceLogic {

	private BluetoothAdapter mBluetoothAdapter = null;
    private Context mContext;
    private String TAG = "AutoPowerdownLogic";
    // private WidgetType mDemoSR;// support SR
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDemoSRValue = InterfaceValueMaps.demo_SR;

    public BlueToothLogic(Context mContext) {
        super();
        this.mContext = mContext;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // SR
        WidgetType mAutoPowerdown = new WidgetType();
        // set name for SR
        mAutoPowerdown.setName(res.getStringArray(R.array.system_setting)[11]);
        // set type for SR
        mAutoPowerdown.setType(WidgetType.TYPE_SELECTOR);
        mAutoPowerdown.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set current SR mode :" + i);
                }
                if (mBluetoothAdapter==null) return 0;
                if (i!=0&&!mBluetoothAdapter.isEnabled())
                	mBluetoothAdapter.enable();
                else 
                	mBluetoothAdapter.disable();
                return 0;
            }

            @Override
            public int getSysValue() {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "get current SR mode :");
                }
                if (mBluetoothAdapter==null) return 0;
                return mBluetoothAdapter.isEnabled()?1:0;
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
    
}
