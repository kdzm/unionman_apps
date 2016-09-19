
package com.unionman.netsetup;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.EthernetDataTracker;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;

import com.unionman.netsetup.R;
import com.hisilicon.android.tvapi.UmtvManager;

import com.unionman.netsetup.logic.factory.InterfaceLogic;
import com.unionman.netsetup.logic.factory.LogicFactory;
import com.unionman.netsetup.util.Constant;
import com.unionman.netsetup.util.Util;

import com.unionman.netsetup.view.setting.CustomSettingView;
import com.unionman.netsetup.view.setting.NetSettingDialog;
import android.os.SystemProperties;
  
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    // Poster focus marker
    private static final int REQUEST_FOCUS = 1;
    // Poster start marker
    private static final int CHANGE_PIC = 2;
 
    private static final int SELECT_TVSOURCE = 3;

    private static final int DELECT_TVSOURCE = 4;
    // Poster start delay
    private static final int POST_DISPLAY_DELAY = 3000;
    private static final int DELAYSET = 1000;
    // mark for control focus
    public static boolean isSnapLeftOrRight = false;
    public static boolean isNeedResetTvFocus = false;
    // Whether the locale is changed
    public static boolean isChangeLocale = false;
    // Whether the page,The main control paging process of focus,
    // Only switch to next page for focus switches
    public static boolean mFlipFlag = false;
    // Mark whether the focus is at the top
    private boolean isFocusUp = true;
    // Whether it is sliding to the left
    private boolean isSnapLeft = false;
    // The page recording focus
    private int mFocusedPage = 1;
    // Record the focus window
    private int mFocusedView = 0;

    private Timer switchSourceTimer = null;
    private TimerTask timerTask = null;

    private boolean isNewIntent = false;

    private ImageView wifiImg;
    private ImageView interImg;
    private ImageView logoImg;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private InterfaceLogic mInterfaceLogic;
    private static final String FACTORY_CALL = "factoryCall";
    private String bootFlag = null;
    private LogicFactory mLogicFactory = null;
    // language setting dialog
    private AlertDialog mLocalChangedDialog;
    private int isFactoryCall = 0;
    // private int[][] settings = InterfaceValueMaps.app_item_values;
    // array of wifi image
    
    private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };
    
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	if ((reason != null) && (reason.equals("SelectSource")||reason.equals("HomeKey"))){
            		Constant.isHomeSourClick=true;
            	}
            }
        }
    };
    
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	this.registerReceiver(systemDialogCloseReceiver, filter);
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	this.unregisterReceiver(systemDialogCloseReceiver);
    }
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.activity_main);
        initView();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int flag;
            isFactoryCall = extras.getInt("fromeFactory");
            if (isFactoryCall == 1) {
                flag = isFactoryCall;
                tellWifiSettingFacCalls();
            }
        }
        createNetDialog(isFactoryCall);
        registerSystemDialogCloseReceiver();
    }
        
    private void tellWifiSettingFacCalls(){
		Intent intent = new Intent();  
		intent.setAction(FACTORY_CALL);             
		intent.putExtra("factoryCalls", 1);
		sendBroadcast(intent);
       }
    
    /**
     * create net setting Dialog
     */
    
    private NetSettingDialog mNetSettingDialog;
    private void createNetDialog(int isFactoryCall) {
    	if(1 != isFactoryCall){
        mNetSettingDialog = new NetSettingDialog(MainActivity.this,
                NetSettingDialog.FLAG_NET);
    	}else{
    		mNetSettingDialog = new NetSettingDialog(MainActivity.this,
    				NetSettingDialog.FLAG_WIFI);
    		tellWifiSettingFacCalls();
    	}
        mNetSettingDialog.setCanceledOnTouchOutside(false);
        mNetSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	mNetSettingDialog = null;
                moveTaskToBack(true);
            	finish();
            }
        });
        Window window = mNetSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        String density =SystemProperties.get("ro.sf.lcd_density");        
        if(density.equals("240")){  
        	lp.height = dip2px(this,700); //900;
            lp.width = dip2px(this,1200);//1540;
        }else{  
            lp.height = dip2px(this,600); //900;
            lp.width = dip2px(this,1024);//1540;
        }
        
   
        window.setAttributes(lp);
        mNetSettingDialog.show();
    }



    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }


    /**
     * Initialize views
     */
    private void initView() {

//        mSettingPage = (MainPageSetting) findViewById(R.id.set_page);
//        mTagView = (TagView) findViewById(R.id.tag_view);
//        wifiImg = (ImageView) findViewById(R.id.wifi);
//        interImg = (ImageView) findViewById(R.id.interactive);
//        logoImg = (ImageView) findViewById(R.id.logo);
        mLogicFactory = new LogicFactory(this);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // If the language is changed into the main interface, directly display
        // the language change dialog
       
    }

    /**
     * create change language dialog
     *
     * @param Index
     */

    /**
     * make all views visible or gone
     *
     * @param visible
     */

    /**
     * check Locale Changed or not
     *
     * @return
     */

//    @Override
//    protected void onNewIntent(Intent intent) {
//        Log.d(TAG, "onNewIntent");
//        isNewIntent = true;
//        String action = intent.getAction();
//        if (Constant.LOG_TAG) {
//            Log.d(TAG, "onNewIntent = " + intent.getAction() + "; id "
//                    + mRootLayout.getCurScreen().getId());
//        }
//        if (null != mLocalChangedDialog && mLocalChangedDialog.isShowing()) {
//            mLocalChangedDialog.dismiss();
//        }
//        mSettingPage.dismissDialog();
//        if (action != null && action.equals(Intent.ACTION_MAIN)) {
//            mRootLayout.setToMovieScreen(mRootLayout.getCurScreen().getId());
//            mFocusedView = 0;
//        } else {
//            isNeedResetTvFocus = true;
//            mRootLayout.setToTVScreen(mRootLayout.getCurScreen().getId());
//        }
//        super.onNewIntent(intent);
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mLocalChangedDialog && mLocalChangedDialog.isShowing()) {
            mLocalChangedDialog.dismiss();
        }


    }

    @Override
    protected void onPause() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "Pause");
        }
        super.onPause();
        if(switchSourceTimer != null)
            switchSourceTimer.cancel();
        mHandler.removeMessages(CHANGE_PIC);
//        unregisterReceiver(rssiReceiver);
        if (Constant.LOG_TAG) {
            Log.d(TAG, "Unregistered");
        }
        
        if(Constant.isHomeSourClick){
	    		
	        	this.finish();
	    	}
    }
    @Override
    protected void onDestroy()
    {
//        Util.unbindPlayService(this);
    	unregisterSystemDialogCloseReceiver();
    	Constant.isHomeSourClick=false;
    	super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (Constant.LOG_TAG) {
//            Log.d(TAG, "onResume");
//        }
//        registerBroadcastReceiver();
//        if (Constant.LOG_TAG) {
//            Log.d(TAG, "Registered");
//        }
//        mFocusedPage = mRootLayout.getCurScreen().getId();
//        mTagView.setViewOnSelectChange(mFocusedPage);
        // Set the focus position
//        setFocus();
//        mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
//        TvSourceIdx = Util.getCurSourceToPrefer(MainActivity.this);
//        mTvPage.setTextValue(TvSourceIdx);
//
//        bootFlag = SystemProperties.get("persist.sys.bootup");
//        Log.d(TAG, "Frist boot up is:" + bootFlag);
//        if (!bootFlag.equals("1") && UmtvManager.getInstance().getFactory().isAgingModeEnable() == false) {
//            Log.d(TAG, "focusedPage:" + mFocusedPage + "  mCurrentSourceIdx = " + mCurrentSourceIdx);
//            if(mFocusedPage != MainPageTv.PAGENUM){
//                if(mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
//            Log.d(TAG, "change source to Media");
//            if (mCurrentSourceIdx == EnumSourceIndex.SOURCE_DVBC
//                    || mCurrentSourceIdx == EnumSourceIndex.SOURCE_DTMB) {
//                Util.notifyDTVStopPlay(MainActivity.this);
//            }
//                     setVideoWindowRect(TvSourceIdx, true);
//            SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
//            SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
//                }
//            }else{
//                Log.d(TAG, "change source to TV");
//                if(mCurrentSourceIdx == EnumSourceIndex.SOURCE_MEDIA){
//                    SourceManagerInterface.deselectSource(EnumSourceIndex.SOURCE_MEDIA, true);
//                    Log.d(TAG,"onResume----1111-TvSourceIdx==="+TvSourceIdx);
//                    SourceManagerInterface.selectSource(TvSourceIdx, 0);
//                    if(TvSourceIdx == EnumSourceIndex.SOURCE_DVBC
//                           ||TvSourceIdx == EnumSourceIndex.SOURCE_DTMB){
//                        Log.d(TAG,"Select DTV Window to play small");
//                        Util.notifyDTVStartPlay(MainActivity.this,false);
//                        mHandler.removeMessages(100);
//            		mHandler.sendEmptyMessageDelayed(100, DELAYSET);
//
//                    }else{
//                        setVideoWindowRect(TvSourceIdx, false);
//                    }
//                }else{
//                    if(TvSourceIdx == EnumSourceIndex.SOURCE_DVBC
//                           ||TvSourceIdx == EnumSourceIndex.SOURCE_DTMB){
//                        Log.d(TAG,"Select DTV Window to small rect");
//                        Util.notifyDTVSmallWindow(MainActivity.this);
//                         mHandler.removeMessages(100);
//            		mHandler.sendEmptyMessageDelayed(100, DELAYSET);
//
//                    }else{
//                        setVideoWindowRect(TvSourceIdx, false);
//                    }
//                }
//            }
//        } else {
//            Log.d(TAG, "onResum to clear frist boot flag");
//            SystemProperties.set("persist.sys.bootup", "" + 0);
//        }
    }

    /**
     * register BroadcastReceiver to monitor network status
     */
//    private void registerBroadcastReceiver() {
//        IntentFilter filter = new IntentFilter(
//                EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
//        registerReceiver(rssiReceiver, filter);
//    }

    /**
     * set focus
     */
 /*   private void setFocus() {
        if (mRootLayout.getCurScreen().getId() == MainPageTv.PAGENUM) {
            mFocusedPage = MainPageTv.PAGENUM;
            mTvPage.isShow();
        } else if (mRootLayout.getCurScreen().getId() == mFocusedPage) {
            View[] views = mRootLayout.getCurScreen().getImgViews();
            if (views[mFocusedView] != null) {
                views[mFocusedView].requestFocus();
            } else {
                mFocusedView = 0;
                isFocusUp = true;
                views[mFocusedView].requestFocus();
            }
        } else {
            mFocusedPage = mRootLayout.getCurScreen().getId();
            mFocusedView = 0;
            isFocusUp = true;
            View[] views = mRootLayout.getCurScreen().getImgViews();
            views[mFocusedView].requestFocus();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        isSnapLeftOrRight = false;
     
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                //case KeyEvent.KEYCODE_1:
                //        Log.d(TAG,"menual set  Window to play small window");
                //        RectInfo rect = new RectInfo();
                //        rect.setX(173);
                //        rect.setY(183);
                //        rect.setW(781);
                //        rect.setH(442);
                //        SourceManagerInterface.setWindowRect(rect, 0);
                //    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    isSnapLeft = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    isSnapLeft = false;
                    break;
                default:
                    break;
            }
         
        }
        return super.onKeyDown(keyCode, event);
    }

    public TimerTask selectSourceToTV(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
                mHandler.sendEmptyMessage(SELECT_TVSOURCE);
            }
        };
        return timerTask;
    }

    public TimerTask delectTVSource(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
                mHandler.sendEmptyMessage(DELECT_TVSOURCE);
            }
        };
        return timerTask;
    }

//    public void switchSource() {
//        mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
//        if(switchSourceTimer != null){
//            switchSourceTimer.cancel();
//        }
//        switchSourceTimer = new Timer();
//        if(mFocusedPage == MainPageTv.PAGENUM){
//            if(mCurrentSourceIdx == EnumSourceIndex.SOURCE_MEDIA){
//                Log.d(TAG, "change source to TV");
//                switchSourceTimer.schedule(selectSourceToTV(),450);
//            }
//        }else{
//            if(mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
//                Log.d(TAG, "change source to Media");
//                switchSourceTimer.schedule(delectTVSource(),450);
//            }
//
//        }
//
//    }


 

    public void setFocusUp(boolean focusUp) {
        this.isFocusUp = focusUp;
    }

    public boolean isFocusUp() {
        return isFocusUp;
    }

    public boolean isSnapLeft() {
        return isSnapLeft;
    }



    public ImageView getLogoImg() {
        return logoImg;
    }


    public ImageView getWifiImg() {
        return wifiImg;
    }

    public ImageView getInterImg() {
        return interImg;
    }

    public void setFocusePage(int page) {
        this.mFocusedPage = page;
    }

    public int getFocusedPage() {
        return mFocusedPage;
    }

    public int getFocusedView() {
        return mFocusedView;
    }

    public void setFocusedView(int focusedView) {
        this.mFocusedView = focusedView;
    }
}
