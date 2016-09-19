
package com.um.launcher;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.EthernetDataTracker;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;

import com.hisilicon.android.tvapi.UmtvManager;
import com.um.launcher.interfaces.AudioInterface;
import com.um.launcher.interfaces.InterfaceValueMaps;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.logic.factory.LogicFactory;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;
import com.um.launcher.util.UtilLauncher;
import com.um.launcher.view.MainPageApp;
import com.um.launcher.view.MainPageEducation;
import com.um.launcher.view.MainPageFirst;
import com.um.launcher.view.MainPageGame;
import com.um.launcher.view.MainPageMovie;
import com.um.launcher.view.MainPageSetting;
import com.um.launcher.view.MainPageTv;
import com.um.launcher.view.ScrollLayout;
import com.um.launcher.view.TagView;
import com.um.launcher.view.setting.CustomSettingView;
import com.um.launcher.weather.CityWeatherInfoBean;
import com.um.launcher.weather.WeatherReceiver.WeatherUpdateListener;

public class MainActivity extends Activity implements WeatherUpdateListener{
    private static final String TAG = "MainActivity";
    public static final int ENTER_ACTIVITY_BY_CLICK = 0x1;
    public static final int ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE = 0x10;
    public static final int ENTER_ACTIVITY_WITHOUT_SCALE_WINDOW = 0x100;
    public static final int ENTER_ACTIVITY_INVISIBLE_VIEW = 0x1000;
    private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
    private static final String HOME_KEY_ACTION = "cn.com.unionman.home.action";
    private static final String UMBOOT_COMPLETED_ACTION = "android.intent.action.UMBOOT_COMPLETED";
    private static final int SELECT_TVSOURCE = 1;
    private static final int DELECT_TVSOURCE = 2;
    private static final int VIEW_SHOW = 3;
    private static final int VIEW_NOSHOW = 4;
    private static final int ACTIVITY_FINISH = 0x5;
    private static final int BOOTUP_TO_FULLPLAY = 0x6;
    private static final int BOOTUP_TO_AGINGMENU = 0x7;
    private static final int SWITCH_SOURCE_FULL_MODE = 0x8;
    private static final int SWITCH_SOURCE_STOPPLAY_MODE = 0x9;
    private static final int SWITCH_SOURCE = 10;
    
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
    private int mFocusedPage = 2;
    // Record the focus window
    private int mFocusedView = 0;
    private int mEnterActivyFlag = 0;

    private boolean isNewIntent = false;
    private RelativeLayout mLauncherLayout;
    private ScrollLayout mRootLayout;
    private MainPageMovie mMoviePage;
    private MainPageEducation mEducationPage;
    private MainPageApp mAppPage;
    private MainPageGame mGamePage;
    private MainPageSetting mSettingPage;
    private MainPageTv mTvPage;
    private MainPageFirst mFirstPage;
    private TagView mTagView;
    private ImageView wifiImg;
    private ImageView interImg;
    private ImageView usbImg;
    private com.um.launcher.view.DigitalClock digtalClock;
    private com.um.launcher.weather.WeatherView weatherView;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private AudioManager mAudioManager;
    private int TvSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private int mCurrentSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private String bootFlag = null;
    private SwitchThread mSwitchThread;
    private Object mSwitchLock;
    // private int[][] settings = InterfaceValueMaps.app_item_values;
    // array of wifi image
    private int[] wifiImage = new int[] {
            R.drawable.main_wifi_signal_1,
            R.drawable.main_wifi_signal_2, R.drawable.main_wifi_signal_3,
            R.drawable.main_wifi_signal_4, R.drawable.main_wifi_signal_5
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case SELECT_TVSOURCE:
                    doSelectSourceToTv(SWITCH_SOURCE_FULL_MODE);
                    break;
                case DELECT_TVSOURCE:
                    doDelectTVSource(SWITCH_SOURCE_FULL_MODE);
                    break;
                case SWITCH_SOURCE:
                	int type = msg.arg1;
                	Log.d(TAG,"SWITCH_SOURCE, type:"+type);
                	if (type == SELECT_TVSOURCE){
                		doSelectSourceToTv(SWITCH_SOURCE_FULL_MODE);
                	}else if (type == DELECT_TVSOURCE){
                		doDelectTVSource(SWITCH_SOURCE_FULL_MODE);
                	}
                	break;
                case VIEW_SHOW:
                	showAllVisbleOrGone(true);
                	break;
                case VIEW_NOSHOW:
                	showAllVisbleOrGone(false);
                	break;
                case ACTIVITY_FINISH:
                	enterToPlay();
                	break;
                case BOOTUP_TO_FULLPLAY:
                	Log.i(TAG, "leon... BOOTUP_TO_FULLPLAY");
                	bootupToFullPlay();
                	break;
                case BOOTUP_TO_AGINGMENU:
                	bootupToAgingMenu();
                	break;
                default:
                    break;
            }
        }
    };

    /**
     * Registered receiver monitor WiFi signal changes
     */
    public BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (intent.getAction().equals(
                    EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                if (Constant.LOG_TAG) {
                    Log.i(TAG, "ETHERNET_STATE_CHANGED_ACTION");
                }
                int message = -1;
                message = intent.getIntExtra(
                        EthernetManager.EXTRA_ETHERNET_STATE, -1);
                if (Constant.LOG_TAG) {
                    Log.i(TAG, "Main message-->" + message);
                }
                switch (message) {
                // Dynamic IP connection is successful event
                    case EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED:
                    case EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED:
                    case EthernetDataTracker.EVENT_PHY_LINK_UP:
                        interImg.setBackgroundResource(R.drawable.interactive_connect);
                        break;
                    case EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED:
                    case EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED:
                    case EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED:
                    case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
                        interImg.setBackgroundResource(R.drawable.interactive_no_connect);
                        break;
                    default:
                        break;
                }
            } else if (intent.getAction().equals(
                    WifiManager.RSSI_CHANGED_ACTION)
                    || intent.getAction().equals(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                    || intent.getAction().equals(
                            WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                    || intent.getAction().equals(
                            WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                if (networkInfo != null
                        && networkInfo.isConnected()
                        && ConnectivityManager.TYPE_WIFI == networkInfo
                                .getType()) {
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    int level = 5;
                    if (info != null){
                    	level = WifiManager.calculateSignalLevel(
                                info.getRssi(), 5);
                    }
                   
                    wifiImg.setBackgroundResource(wifiImage[level]);
                } else {
                    wifiImg.setBackgroundResource(R.drawable.wifi_signal_off);
                }
            }
        }
    };

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_REMOVED)
            || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

                if (action.equals(Intent.ACTION_MEDIA_REMOVED)
                || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    if (UtilLauncher.isExtralDevicesMount()){
                    	usbImg.setVisibility(View.VISIBLE);
                    }else{
                    	usbImg.setVisibility(View.INVISIBLE);
                    }
                }
                else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                    if (UtilLauncher.isExtralDevicesMount()){
                    	usbImg.setVisibility(View.VISIBLE);
                    }else{
                    	usbImg.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    };
    
    private BroadcastReceiver localReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "localReceiver ACTION_LOCALE_CHANGED");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_LOCALE_CHANGED)){
            	Log.e(TAG, "localReceiver ACTION_LOCALE_CHANGED");
            	isChangeLocale = true;
            }
        }
    };
    
    private BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "homeKeyReceiver ACTION_CLOSE_SYSTEM_DIALOGS");
            String action = intent.getAction();
            if (action.equals(HOME_KEY_ACTION)){
            	delay();
                if (mRootLayout.isFinished()) {
                	snapToNextScreen();
                }
            }
        }
    };
    
    private boolean appInstalled(String packageName){
    	boolean exitst = false;
		try{
			File file = new File(packageName);
			if (file.exists()){
				Log.d(TAG,"leon... exists");
				exitst = true;
			}else{
				Log.d(TAG,"leon... exists!!!!");
				exitst = false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return exitst;
	}
    
    private void bootupToFullPlay(){
    	int mdbSource = SourceManagerInterface.getLastSourceId();
    	
    	if (mdbSource != EnumSourceIndex.SOURCE_MEDIA)
    	{	
    	    selectPlayActivity(mdbSource);
    	    setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE);
    	}
    }
    
    private void bootupToAgingMenu(){
    	Intent intent = new Intent();
    	intent.setClassName("com.um.tv.menu", "com.um.tv.menu.app.TvMenuWindowManagerService");
    	intent.putExtra("com.um.tv.menu.commmand", "com.um.tv.menu.commmand.aging_menu");
    	intent.putExtra("bootup", "bootup");
    	startService(intent);
    	setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE);
    }
    
    private void pollAPK(boolean isAgingMode){
        boolean isAPKExist = false;
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        
    	if (isAgingMode == false){
        	int mdbSource = SourceManagerInterface.getLastSourceId();
        	String appName = null;
        	
        	if (mdbSource == EnumSourceIndex.SOURCE_ATV){
        		appName = "/system/app/UMATV.apk";
        		isAPKExist = appInstalled(appName);
        	}else if ((mdbSource == EnumSourceIndex.SOURCE_DVBC) || (mdbSource == EnumSourceIndex.SOURCE_DTMB)){
        		String installed = null;
        		installed = SystemProperties.get("persist.sys.dvb.installed");
        		if (installed.equals("1")){
        			isAPKExist = true;
        		}else{
        			isAPKExist = false;
        		}
        	}else{
        		appName = "/system/app/UMPortPlayer.apk";
        		isAPKExist = appInstalled(appName);
        	}
        	
    	    if (mdbSource == EnumSourceIndex.SOURCE_DVBC || mdbSource == EnumSourceIndex.SOURCE_DTMB){
    	        intent.setAction(Constant.INTENT_DTV);
    	    }
    	    else if (mdbSource == EnumSourceIndex.SOURCE_ATV){
    	    	intent.setAction(Constant.INTENT_ATV);
    	    }
    	    else{
    	    	intent.setAction(Constant.INTENT_PORT);
    	    }   	    
    	}else{
    		return;
    	}
	    
	    PackageManager packageManager = this.getPackageManager();
        List<ResolveInfo> tempAppList = null;
        Log.d(TAG,"leon...packageManager.queryIntentActivities before,isAPKExist:"+isAPKExist);
        if (isAPKExist){
            while ((tempAppList == null) || (tempAppList != null && tempAppList.size() == 0)){
            	tempAppList = packageManager.queryIntentActivities(
                		intent, 0);
            	Log.d(TAG,"leon...packageManager.queryIntentActivities");
            	for (int i = 0; tempAppList != null && i < tempAppList.size();i++){
                    ResolveInfo info = tempAppList.get(i);
                    String pkg = info.activityInfo.packageName;
                    Log.d(TAG,"leon...packageManager.queryIntentActivities pkg:"+pkg);
            	}
            }
        }
    }
    
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mdbSource = SourceManagerInterface.getSelectSourceId();
        bootFlag = SystemProperties.get("persist.sys.bootup");
        Log.e(TAG, "onCreate mdbSource= " + mdbSource+",bootFlag"+bootFlag);
        if (bootFlag.equals("1") || UmtvManager.getInstance().getFactory().isAgingModeEnable() == true)
        {
        	Log.e(TAG, "Enter to this mdbSource= " + mdbSource);
        	if (UmtvManager.getInstance().getFactory().isAgingModeEnable() == false){
        		pollAPK(false);
            }else{            	
            	pollAPK(true);
            }
        }
        
        setContentView(R.layout.activity_main);
        initView();
        registerLocaleBroadcastReceiver();
        registerHomeKeyBroadcastReceiver();
        registerBroadcastReceiver();
        registerUSBroadcastReceiver();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        mSwitchLock = new Object();
        mSwitchThread = new SwitchThread();
        mSwitchThread.start();
        
    }

    /**
     * Initialize views
     */
    private void initView() {
    	mLauncherLayout = (RelativeLayout)findViewById(R.id.main);
        mRootLayout = (ScrollLayout) findViewById(R.id.root);
        //mMoviePage = (MainPageMovie) findViewById(R.id.movie_page);
        mEducationPage = (MainPageEducation) findViewById(R.id.education_page);
        mAppPage = (MainPageApp) findViewById(R.id.app_page);
        mGamePage = (MainPageGame) findViewById(R.id.game_page);
        //mSettingPage = (MainPageSetting) findViewById(R.id.set_page);
        //mTvPage = (MainPageTv) findViewById(R.id.tv_page);
        mFirstPage = (MainPageFirst) findViewById(R.id.first_page);
        mTagView = (TagView) findViewById(R.id.tag_view);
        wifiImg = (ImageView) findViewById(R.id.wifi);
        interImg = (ImageView) findViewById(R.id.interactive);
        usbImg = (ImageView) findViewById(R.id.usb);
        digtalClock = (com.um.launcher.view.DigitalClock) findViewById(R.id.digitalClock);
        weatherView = (com.um.launcher.weather.WeatherView) findViewById(R.id.weather_view);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (isChangeLocale == false){
	        isSnapLeftOrRight = true;
	        mRootLayout.setToFirstScreen(mRootLayout.getCurScreen().getId());
	        mFocusedView = MainPageFirst.PAGENUM;
        }else{
            mRootLayout.setToFirstScreen(mRootLayout.getCurScreen()
                    .getId());
            isChangeLocale = false;
        }
    }

    /**
     * make all views visible or gone
     *
     * @param visible
     */
    public void showAllVisbleOrGone(boolean visible) {
        if (visible) {
        	mLauncherLayout.setVisibility(View.VISIBLE);
            mRootLayout.setVisibility(View.VISIBLE);
            wifiImg.setVisibility(View.VISIBLE);
            interImg.setVisibility(View.VISIBLE);
            mTagView.setVisibility(View.VISIBLE);
            //usbImg.setVisibility(View.VISIBLE);
            digtalClock.setVisibility(View.VISIBLE);
            weatherView.setVisibility(View.VISIBLE);
        } else {
        	mLauncherLayout.setVisibility(View.INVISIBLE);
            mRootLayout.setVisibility(View.INVISIBLE);
            wifiImg.setVisibility(View.INVISIBLE);
            interImg.setVisibility(View.INVISIBLE);
            mTagView.setVisibility(View.INVISIBLE);
            //usbImg.setVisibility(View.INVISIBLE);
            digtalClock.setVisibility(View.INVISIBLE);
            weatherView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        isNewIntent = true;
        String action = intent.getAction();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onNewIntent = " + intent.getAction() + "; id "
                    + mRootLayout.getCurScreen().getId());
        }
        
        if (action != null && action.equals(Intent.ACTION_MAIN)) {
        }
        
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onStart");
        }
        
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onStop");
        }
        
        super.onStop();       
    }

    @Override
    protected void onPause() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "Pause");
        }
        super.onPause();
        mHandler.removeMessages(ACTIVITY_FINISH);
        switchMessageRemove(SWITCH_SOURCE);
        
        if (Constant.LOG_TAG) {
            Log.d(TAG, "Unregistered");
        }
        
        doSnapActivity(false);
    }
    
    @Override
    protected void onDestroy()
    {
    	unregisterReceiver(localReceiver);
    	unregisterReceiver(homeKeyReceiver);
        unregisterReceiver(rssiReceiver);
        unregisterReceiver(usbReceiver);
    	super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onResume");
        }
        
        mHandler.removeMessages(VIEW_NOSHOW);
        
        if (UtilLauncher.isExtralDevicesMount()){
        	usbImg.setVisibility(View.VISIBLE);
        }else{
        	usbImg.setVisibility(View.INVISIBLE);
        }
        
        if (Constant.LOG_TAG) {
            Log.d(TAG, "Registered");
        }
        mFocusedPage = mRootLayout.getCurScreen().getId();
        mTagView.setViewOnSelectChange(mFocusedPage);
        // Set the focus position
        setFocus();
        mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
        TvSourceIdx = SourceManagerInterface.getLastSourceId();//Util.getCurSourceToPrefer(MainActivity.this);
        //mTvPage.setTextValue(TvSourceIdx);
        mFirstPage.setTextValue(TvSourceIdx);
        bootFlag = SystemProperties.get("persist.sys.bootup");
        Log.d(TAG, "Frist boot up is:" + bootFlag);
        if (!bootFlag.equals("1") && UmtvManager.getInstance().getFactory().isAgingModeEnable() == false) {
            Log.d(TAG, "focusedPage:" + mFocusedPage + "  mCurrentSourceIdx = " + mCurrentSourceIdx);
            doSnapActivity(true);
        } else {
            Log.d(TAG, "onResum to clear frist boot flag");
            Log.d(TAG, "onResum AgingMode flag:"+UmtvManager.getInstance().getFactory().isAgingModeEnable());
            SystemProperties.set("persist.sys.bootup", "" + 0);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.UMBOOT_COMPLETED");
            sendBroadcast(intent);
            if (UmtvManager.getInstance().getFactory().isAgingModeEnable() == false){
            	bootupToFullPlay();
            	//mHandler.sendEmptyMessageDelayed(BOOTUP_TO_FULLPLAY, 500);
            }else{
            	bootupToAgingMenu();
            	//mHandler.sendEmptyMessageDelayed(BOOTUP_TO_AGINGMENU, 500);
            }
        }
    }

    /**
     * register BroadcastReceiver to monitor network status
     */
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(
                EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
        registerReceiver(rssiReceiver, filter);
    }

    private void registerUSBroadcastReceiver(){
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbFilter.addDataScheme("file");
        registerReceiver(usbReceiver, usbFilter);
    }
    
    private void registerLocaleBroadcastReceiver(){
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(Intent.ACTION_LOCALE_CHANGED);
    	registerReceiver(localReceiver, filter);
    }
    
    private void registerHomeKeyBroadcastReceiver(){
    	IntentFilter filter = new IntentFilter(HOME_KEY_ACTION);
    	registerReceiver(homeKeyReceiver, filter);
    }
    
    /**
     * set focus
     */
    private void setFocus() {
    	Log.d(TAG,"mRootLayout.getCurScreen().getId():"+mRootLayout.getCurScreen().getId()
    			+";mFocusedPage:"+mFocusedPage);
        if (mRootLayout.getCurScreen().getId() == MainPageFirst.PAGENUM) {
            mFocusedPage = MainPageFirst.PAGENUM;
            //mTvPage.isShow();
            mFirstPage.isShow();
            Log.d(TAG,"setFocus,1");
        }else if (mRootLayout.getCurScreen().getId() == mFocusedPage) {
            View[] views = mRootLayout.getCurScreen().getImgViews();
            if ((views.length > mFocusedView) && (views[mFocusedView] != null)) {
                views[mFocusedView].requestFocus();
                Log.d(TAG,"setFocus,2");
            } else {
                mFocusedView = 0;
                isFocusUp = true;
                views[mFocusedView].requestFocus();
                Log.d(TAG,"setFocus,3");
            }
        } else {
            mFocusedPage = mRootLayout.getCurScreen().getId();
            mFocusedView = 0;
            isFocusUp = true;
            View[] views = mRootLayout.getCurScreen().getImgViews();
            views[mFocusedView].requestFocus();
            Log.d(TAG,"setFocus,4");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        isSnapLeftOrRight = false;
        if (!mRootLayout.isFinished()) {
        	Log.d(TAG,"onKeyDown, mRootLayout do not Finished, key return");
            return true;
        }
        
        delay();
        
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
            	case KeyEvent.KEYCODE_BACK:
            		enterToPlay();
            		break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    isSnapLeft = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    isSnapLeft = false;
                    break;
                default:
                    break;
            }
            if (mRootLayout.getCurScreen().getId() == MainPageEducation.PAGENUM) {
                return mEducationPage.onKeyDown(keyCode, event);
            } else if (mRootLayout.getCurScreen().getId() == MainPageApp.PAGENUM) {
                return mAppPage.onKeyDown(keyCode, event);
            } else if (mRootLayout.getCurScreen().getId() == MainPageGame.PAGENUM) {
                return mGamePage.onKeyDown(keyCode, event);
            } else if (mRootLayout.getCurScreen().getId() == MainPageFirst.PAGENUM) {
                return mFirstPage.onKeyDown(keyCode, event);
            } else if (mRootLayout.getCurScreen().getId() == MainPageSetting.PAGENUM) {
                return mSettingPage.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void doSelectSourceToTv(int switchMode){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doSelectSourceToTv is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx);
    		
        	if (switchMode == SWITCH_SOURCE_FULL_MODE){
                if (mCurrentSourceIdx != TvSourceIdx) {
                    Log.d(TAG, "doSelectSourceToTv");
    	             if (TvSourceIdx != EnumSourceIndex.SOURCE_DVBC
    	                		&& TvSourceIdx != EnumSourceIndex.SOURCE_DTMB){
    	                   SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
    	                   Log.d(TAG,"---------TvSourceIdx==="+TvSourceIdx);
    	                   SourceManagerInterface.selectSource(TvSourceIdx, 0);
    	           	 }else{
    	                   SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
    	                   Log.d(TAG,"---------TvSourceIdx==="+TvSourceIdx);
    	                   SourceManagerInterface.selectSource(TvSourceIdx, 0);
    	                   Util.notifyDTVStartPlay(MainActivity.this, false);
    	           	 }
    	             	 
    	             setVideoWindowRect(false);
                  }else{
                	  setVideoWindowRect(false);
                  }
        	}else{
        	 	if (TvSourceIdx == EnumSourceIndex.SOURCE_DVBC
                  		|| TvSourceIdx == EnumSourceIndex.SOURCE_DTMB){
            		//Util.notifyDTVStartPlay(MainActivity.this, false);
            		setVideoWindowRect(false);
            	}
        	}
    	}
    }

    private void doDelectTVSource(int switchMode){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doDelectTVSource is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx);
    		
        	if (mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
        		Log.d(TAG, "doDelectTVSource");
            	setVideoWindowRect(true);
                if (TvSourceIdx != EnumSourceIndex.SOURCE_DVBC
                		&& TvSourceIdx != EnumSourceIndex.SOURCE_DTMB) {
                    SourceManagerInterface.deselectSource(TvSourceIdx, true);
                    SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
                }else{
                	if (switchMode == SWITCH_SOURCE_FULL_MODE){
                    	Util.notifyDTVStopPlay(MainActivity.this);
                        SourceManagerInterface.deselectSource(TvSourceIdx, true);
                        SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
                	}else{
                		Util.notifyDTVStopPlay(MainActivity.this);
                	}
                }
        	}
		}
    }

    public void switchSource(boolean bSelectTV, boolean asyn) {
		
    	switchMessageRemove(SWITCH_SOURCE);
		
		if (asyn == true){
	        if (bSelectTV == true){
	        	switchMessageSend(SWITCH_SOURCE, SELECT_TVSOURCE, SWITCH_SOURCE_FULL_MODE);
	        	Log.d(TAG,"sendMessageDelayed SELECT_TVSOURCE");
	        }else{
	        	switchMessageSend(SWITCH_SOURCE, DELECT_TVSOURCE, SWITCH_SOURCE_FULL_MODE);
	        	Log.d(TAG,"sendMessageDelayed DELECT_TVSOURCE");
	        }
		}else{
			 if (bSelectTV == true){
				 doSelectSourceToTv(SWITCH_SOURCE_FULL_MODE);
		     }else{
		    	 doDelectTVSource(SWITCH_SOURCE_FULL_MODE);
		     }
		}
    }
    
    public void setVideoWindowRect(boolean isFullScreen) {
    	int sourceIdx = SourceManagerInterface.getSelectSourceId();
        RectInfo rect = new RectInfo();
        if ((sourceIdx == EnumSourceIndex.SOURCE_MEDIA) || (sourceIdx == EnumSourceIndex.SOURCE_ATV)
                || (sourceIdx >= EnumSourceIndex.SOURCE_CVBS1 && sourceIdx <= EnumSourceIndex.SOURCE_HDMI4)) {
             if(isFullScreen){
                 rect.setX(0);
                 rect.setY(0);
                 rect.setW(1920);
                 rect.setH(1080);
                 Log.d(TAG, "Scaler TV Windows to Full");
             }else{
                 rect.setX(120);
                 rect.setY(205);
                 rect.setW(720);
                 rect.setH(410);
                 Log.d(TAG, "Scaler TV Windows to Small");
             }
            SourceManagerInterface.setWindowRect(rect, 0);
        }else if ((sourceIdx == EnumSourceIndex.SOURCE_DVBC) || (sourceIdx == EnumSourceIndex.SOURCE_DTMB)){
        	if (isFullScreen){
        		Util.notifyDTVStartPlay(this, true);
        		Log.d(TAG, "Scaler DTV Windows to Full");
        	}else{
        		Util.notifyDTVStartPlay(this, false);
        		Log.d(TAG, "Scaler DTV Windows to Small");
        	}
        }
    }
    
    private void switchSourceOnSnapScreen(boolean bSelectTV){
		mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
		TvSourceIdx = SourceManagerInterface.getLastSourceId();
		
		 if (bSelectTV == true){
			 doSelectSourceToTv(SWITCH_SOURCE_STOPPLAY_MODE);
	     }else{
	    	 doDelectTVSource(SWITCH_SOURCE_STOPPLAY_MODE);
	     }
    }
    
    //�л�����
    private void doSnapScreen(){
    	if (mFocusedPage == MainPageFirst.PAGENUM){
    		switchSource(true, true);
    		mFirstPage.resetSignalCheckStatus();
    	}else{
    		switchSource(false,true);
    	}
    }
    
    //�л�ativity
    private void doSnapActivity(boolean snapToLauncher){
    	if (snapToLauncher == true){
    		if (mFocusedPage == MainPageFirst.PAGENUM){
    			switchSource(true, true);
    		}else{
    			switchSource(false, true);
    		}

    		if ((mEnterActivyFlag & ENTER_ACTIVITY_INVISIBLE_VIEW) == ENTER_ACTIVITY_INVISIBLE_VIEW){
    			showAllVisbleOrGone(true);
    			//mHandler.sendEmptyMessageDelayed(VIEW_SHOW, 300);
    		}
    		
    		mEnterActivyFlag = 0;
    		
    	}else{
    		Log.d(TAG,"mEnterActivyFlag:"+mEnterActivyFlag);

    		if ((mEnterActivyFlag & ENTER_ACTIVITY_BY_CLICK) == ENTER_ACTIVITY_BY_CLICK){
        		
        		if ((mEnterActivyFlag & ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE) != ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE){
        			Log.d(TAG,"doSnapActivity false switchSource");
        			switchSource(false, false);
        		}else{
        			Log.d(TAG,"doSnapActivity false notifyDTVStopPlay 1");
        			//waitForSwitchLock();
        			Util.notifyDTVStopPlay(this);
        		}
        		
        		if ((mEnterActivyFlag & ENTER_ACTIVITY_INVISIBLE_VIEW) == ENTER_ACTIVITY_INVISIBLE_VIEW){
        			mHandler.sendEmptyMessageDelayed(VIEW_NOSHOW, 400);
        		}       		
    		}else{
    			Log.d(TAG,"doSnapActivity false notifyDTVStopPlay 2");
    			//waitForSwitchLock();
    			Util.notifyDTVStopPlay(this);
    		}
    	}
    }
    
    public void setEnterActivityFlag(int flag){
    	mEnterActivyFlag |= flag;
    }
    
    public void startToActivity(Intent intent){
    	switchMessageRemove(SWITCH_SOURCE);
    	waitForSwitchLock();
    	startActivity(intent);
    }
    
    /**
     * show next Screen
     */
    public void snapToNextScreen() {
        isSnapLeftOrRight = true;
        mRootLayout.snapToScreen(mRootLayout.getCurrentScreen() + 1);
        mFocusedPage = mRootLayout.getCurScreen().getId();
        doSnapScreen();
    }

    /**
     * show previous Screen
     */
    public void snapToPreScreen() {
        isSnapLeftOrRight = true;
        mRootLayout.snapToScreen(mRootLayout.getCurrentScreen() - 1);
        mFocusedPage = mRootLayout.getCurScreen().getId();
        doSnapScreen();
    }

    public void setFocusUp(boolean focusUp) {
        this.isFocusUp = focusUp;
    }

    public boolean isFocusUp() {
        return isFocusUp;
    }

    public boolean isSnapLeft() {
        return isSnapLeft;
    }

    public ScrollLayout getRoot() {
        return mRootLayout;
    }

    public TagView getTagView() {
        return mTagView;
    }

    public MainPageSetting getSettingPage() {
        return mSettingPage;
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
	
	public void enterToPlay(){
		switchMessageRemove(SWITCH_SOURCE);
		waitForSwitchLock();
	    int curId = SourceManagerInterface.getLastSourceId();
	    selectPlayActivity(curId);
	    
	    setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE | ENTER_ACTIVITY_WITHOUT_SCALE_WINDOW);
	}
	
	private void selectPlayActivity(int curId){
    	Intent intent = new Intent();
    	
        switch (curId) {
	        case EnumSourceIndex.SOURCE_DVBC:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
	            break;
	        case EnumSourceIndex.SOURCE_DTMB:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DTMB);
	            break;
	        case EnumSourceIndex.SOURCE_ATV:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_ATV);
	            break;
	        case EnumSourceIndex.SOURCE_CVBS1:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS1);
	            break;
	        case EnumSourceIndex.SOURCE_CVBS2:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS2);
	            break;
	        case EnumSourceIndex.SOURCE_YPBPR1:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_YPBPR1);
	            break;
	        case EnumSourceIndex.SOURCE_HDMI1:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI1);
	            break;
	        case EnumSourceIndex.SOURCE_HDMI2:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI2);
	            break;
	        case EnumSourceIndex.SOURCE_HDMI3:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI3);
	            break;
	        case EnumSourceIndex.SOURCE_VGA:
	            intent.putExtra("SourceName", EnumSourceIndex.SOURCE_VGA);
	            break;
	        default:
	        	intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS1);
	            break;
        	}
	    
	    if (curId == EnumSourceIndex.SOURCE_DVBC || curId == EnumSourceIndex.SOURCE_DTMB){
	
	        intent.setAction(Constant.INTENT_DTV);
	    }
	    else if (curId == EnumSourceIndex.SOURCE_ATV)
	    {
	    	intent.setAction(Constant.INTENT_ATV);
	    }
	    else
	    {
	    	intent.setAction(Constant.INTENT_PORT);
	    }
	    
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    try{
		    startActivity(intent);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus){
			delay();
		}else{
			mHandler.removeMessages(ACTIVITY_FINISH);
		}
	}
	
    private void delay() {
    	mHandler.removeMessages(ACTIVITY_FINISH);
        Message message = new Message();
        message.what = ACTIVITY_FINISH;
        mHandler.sendEmptyMessageDelayed(ACTIVITY_FINISH, 60000);
    }
    
	@Override
	public void updateWeather(CityWeatherInfoBean bean) {
		if (weatherView != null){
			weatherView.updateWeather(bean);
		}
	}
	
	private void switchMessageSend(int what, int arg1, int arg2){
		Handler handler = mSwitchThread.getHandler();
		if (handler != null){
        	Message msg = handler.obtainMessage(what, arg1, arg2);
        	handler.sendMessageDelayed(msg, 450);
		}
	}
	
	private void switchMessageRemove(int what){
		Handler handler = mSwitchThread.getHandler();
		if (handler != null){
        	handler.removeMessages(what);
		}
	}
	
	private void waitForSwitchLock(){
		Log.d(TAG,"waitForSwitchLock waiting");
		synchronized (mSwitchLock){
			Log.d(TAG,"waitForSwitchLock in");
		}
		Log.d(TAG,"waitForSwitchLock out");
	}
	
	class SwitchThread extends Thread{
		private Handler mSwitchHandler;
		
		public Handler getHandler(){
			return mSwitchHandler;
		}
		
		@Override
		public void run(){
			Looper.prepare();
			mSwitchHandler = new SwitchHandler();
			Looper.loop();
		}
	}
	
	class SwitchHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what){
				case SWITCH_SOURCE:
		           	int type = msg.arg1;
                	Log.d(TAG,"SwitchThread SWITCH_SOURCE, type:"+type);
                	if (type == SELECT_TVSOURCE){
                		doSelectSourceToTv(msg.arg2);
                	}else if (type == DELECT_TVSOURCE){
                		doDelectTVSource(msg.arg2);
                	}
					break;
				default:
					break;
			}
		}
	} 
}
