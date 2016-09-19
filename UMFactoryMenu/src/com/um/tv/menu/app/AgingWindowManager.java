package com.um.tv.menu.app;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.UmtvManager;
import com.um.tv.menu.R;
import com.um.tv.menu.model.CombinatedModel;
import com.um.tv.menu.ui.AgingWindow;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.os.SystemProperties;

public class AgingWindowManager {
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "AgingWindowManager";
	private static final String AGING_ACTIVITY_FINISH_ACTION = "com.android.um.agingactivity_finish";
    private static AgingWindowManager mManager = null;
    private static Context mContext = null;   
    private static WindowManager mWindowManager = null;
    private static WindowManager.LayoutParams mAgingLayoutParams = null;
    private static AgingWindow mAgingWindow = null;
    private static CusFactory mFactory = UmtvManager.getInstance().getFactory();
    
    private AgingWindowManager() {
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        mAgingLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mAgingLayoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);

        mAgingLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mAgingLayoutParams.type = LayoutParams.TYPE_PHONE;
        //      mAgingLayoutParams.width = LayoutParams.MATCH_PARENT;
        //      mAgingLayoutParams.height = LayoutParams.MATCH_PARENT;
        initViews();

    }
    
    public static AgingWindowManager from(Context context) {
        mContext = context;
        if (mManager == null) {
            mManager = new AgingWindowManager();
        }
        return mManager;
    }
    
    private void initViews() {
    	mAgingWindow = new AgingWindow(mContext);
    }
    
    public void changeFactoryMenuStatus() {
        if (mAgingWindow.isShowing()) {
            dismissAging();
        } else {
            showAging();
        }
    }

    public boolean isShowAging(){
    	return mAgingWindow.isShowing();
    }
    
    private void showAging() {
    	Log.d(TAG,SUBTAG+"showAging()--->");
    	
        mWindowManager.addView(mAgingWindow, mAgingLayoutParams);
        mAgingWindow.setShowing(true);
        mFactory.enableAgingMode(true);
       // mFactory.enableAgingModeFlag(true);
        Intent intent = new Intent("android.intent.action.AGING_ACTIVITY");
        intent.putExtra("start_mode", "start");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        
        if(mFactory.getPoweronMode()!=0)
        {
            mFactory.setPoweronMode(0);
        }
        
    	/*
        Intent actintent = new Intent();
        actintent.setClassName("com.um.tv.menu", "com.um.tv.menu.app.AgingTimerActivity");
        actintent.putExtra("start_mode", "start");
        actintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(actintent);
        */
        Log.d(TAG,SUBTAG+"showAging()--->AgingModeFlag:"+mFactory.isAgingModeEnable());
    }

    private void dismissAging() {
    	Log.d(TAG,SUBTAG+"dismissAging()--->");
        mWindowManager.removeView(mAgingWindow);
        mAgingWindow.setShowing(false);
        mFactory.enableAgingMode(false);
       // mFactory.enableAgingModeFlag(false);
        
    	int powerMode = Integer.parseInt(SystemProperties.get("persist.sys.powerMode", "1"));
    	if(mFactory.getPoweronMode()!=powerMode)
        {
        	mFactory.setPoweronMode(powerMode);
        }
		 
        Intent intent = new Intent(AGING_ACTIVITY_FINISH_ACTION);
        mContext.sendBroadcast(intent);
        Log.d(TAG,SUBTAG+"dismissAging()--->AgingModeFlag:"+mFactory.isAgingModeEnable());
    }
    
    public static void destroyAging(){
    	if (mAgingWindow.isShowing()){
    		mWindowManager.removeView(mAgingWindow);
            mAgingWindow.setShowing(false);
            mFactory.enableAgingMode(false);
    	}
    }
}
