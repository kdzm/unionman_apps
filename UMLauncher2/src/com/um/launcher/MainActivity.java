
package com.um.launcher;

import java.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import android.view.Gravity;
import android.widget.Toast;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.EthernetDataTracker;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.content.SharedPreferences.Editor;


import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.um.launcher.constant.PosterConstants;
import com.um.launcher.data.PosterInfo;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.LogUtils;
import com.um.launcher.util.NetWorkUtils;
import com.um.launcher.util.PosterUtils;
import com.um.launcher.util.TimeUtils;
import com.um.launcher.util.Util;
import com.um.launcher.util.UtilLauncher;
import com.um.launcher.view.PopupMenu;
import com.um.launcher.weather.CityWeatherInfoBean;
import com.um.launcher.weather.WeatherReceiver.WeatherUpdateListener;
import com.um.launcher.widget.FocusedRelativeLayout;
import android.os.SystemProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private static final int POSTER_URL = 11;
    
    private final static String UPDATE_PATH = "/storage/emulated/0/recovery2/update.zip";
    private final static String CACHE_PATH = "/cache2/update.zip";
    private File updateFile;
    private File cacheFile;
    
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
    // Record the focus window
    private int mFocusedView = 0;
    private int mEnterActivyFlag = 0;
    private String  fullScreen_Source = "";
    //是否需要发送startDTV
    private boolean isNotifyDTVStartPlay = false;
    private boolean isNewIntent = false;
    private RelativeLayout mLauncherLayout;
    private MainPageFragment mFirstPage;
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
    private int mSourceType = FocusedRelativeLayout.SourceChangeListener.SOURCE_LEFT;
    private int config_bootSourceState;
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
                case POSTER_URL:
                    mFirstPage.updatePoster(posterInfoMap);
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
                    case EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED:
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
            } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetWorkUtils.isNetworkAvailable(MainActivity.this)) {
                    getPosterInfos();
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
    	int bootSourceState = getBootSourceState();
    	Log.i(TAG,"=============bootSourceState="+bootSourceState);
    	if(bootSourceState == 0){
        	startDefaultApk();    		
    	}else{
    		int mdbSource = Integer.parseInt(fullScreen_Source); 
        	if (mdbSource != EnumSourceIndex.SOURCE_MEDIA)
        	{	
        	    selectPlayActivity(mdbSource);
        	    setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE);
        	}else{
        		startDefaultApk();   		        		
        	}
    	}
    }

	private void startDefaultApk() {
		SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
		String pkg1 = Constant.Default_Start_Apk_Package_Name;
		String cls1 = Constant.Default_Start_Apk_MainClass_Name;
		ComponentName componentName1 = new ComponentName(pkg1, cls1);
		Intent intent = new Intent();
		intent.setComponent(componentName1);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startToActivity(intent);   
		SystemProperties.set("persist.sys.fullScreen_Source", ""+EnumSourceIndex.SOURCE_MEDIA);
	}
    
    private int getBootSourceState(){
    	int bootSourceState = 0;
        try {
                Context otherContext = createPackageContext(
                        "com.unionman.settings", Context.CONTEXT_IGNORE_SECURITY);
                SharedPreferences sp = otherContext.getSharedPreferences(
                          "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                          + Context.MODE_MULTI_PROCESS);

                bootSourceState = sp.getInt("bootSourceState", 0);
                Log.i(TAG,"bootSourceState "+bootSourceState);
        } catch (NameNotFoundException e) {
                e.printStackTrace();
        }
        return bootSourceState;
    }    
    
    private void bootupToAgingMenu(){
    	Intent intent = new Intent();
    	intent.setClassName("com.um.tv.menu", "com.um.tv.menu.app.TvMenuWindowManagerService");
    	intent.putExtra("com.um.tv.menu.commmand", "com.um.tv.menu.commmand.aging_menu");
    	intent.putExtra("bootup", "bootup");
    	startService(intent);
    	setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE);
    }
	private void bootupToMMode(boolean isNeedKey){
		Log.i("hehe", "bootupToMMode");
		Intent service = new Intent();
		service.setClassName("com.um.tv.menu", "com.um.tv.menu.app.TvMenuWindowManagerService");
		if(isNeedKey){
			service.putExtra("com.um.tv.menu.commmand", "com.um.tv.menu.commmand.mmode_key");
			service.putExtra("com.um.tv.menu.key", KeyEvent.KEY_MMODE);
			service.putExtra("com.um.tv.menu.extra", "close");
		}else {
			service.putExtra("com.um.tv.menu.commmand", "com.um.tv.menu.commmand.start_service");
		}
		startService(service);
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
        startServicesOnBoot();
        
        config_bootSourceState =  getSystemProperties("um.app.default_bootSourceState",1);
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
        
        setContentView(R.layout.activity_main1);
        initView();
        registerLocaleBroadcastReceiver();
        registerHomeKeyBroadcastReceiver();
        registerBroadcastReceiver();
        registerUSBroadcastReceiver();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        
        mSwitchLock = new Object();
        mSwitchThread = new SwitchThread();
        mSwitchThread.start();

        if (NetWorkUtils.isNetworkAvailable(this)) {
            getPosterInfos();
        }

        if(!  isServiceWork(MainActivity.this,"com.android.settings.bluetooth.BlueToothAutoPairService"))	
          {
	        Log.i(TAG, "start BlueToothAutoPairService");
	        Intent autoPairIntent = new Intent("UM_BlueToothAutoPairService");
	        startService(autoPairIntent);
        }
        
        if(!  isServiceWork(MainActivity.this,"com.um.storemodeservice.StoreModeService"))	
        {
	        Log.i(TAG, "start StoreModeService");
	        Intent storeModeService = new Intent("UM_StoreModeService");
	        startService(storeModeService);
      }
        //add by unionman for release update.zip
        releaseZipSpace();
    }
    //add by unionman
    private void releaseZipSpace(){
    	updateFile = new File(UPDATE_PATH);
    	cacheFile = new File(CACHE_PATH);
    	
    	if (updateFile.exists()) {
        	updateFile.delete();
		}
    	if (cacheFile.exists()) {
			cacheFile.delete();
		}
    }
    //by end

    /**
     * Initialize views
     */
    private void initView() {
    	mLauncherLayout = (RelativeLayout)findViewById(R.id.main);
        mFirstPage = (MainPageFragment) getFragmentManager().findFragmentById(R.id.first_page);
        wifiImg = (ImageView) findViewById(R.id.wifi);
        interImg = (ImageView) findViewById(R.id.interactive);
        usbImg = (ImageView) findViewById(R.id.usb);
        digtalClock = (com.um.launcher.view.DigitalClock) findViewById(R.id.digitalClock);
        weatherView = (com.um.launcher.weather.WeatherView) findViewById(R.id.weather_view);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        interImg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new PopupMenu(MainActivity.this).showAsDropDown(interImg);
                }
            }
        });
        mFirstPage.setOnSourceChangeListener(new FocusedRelativeLayout.SourceChangeListener() {
            @Override
            public void onSourceChange(int source) {
                mSourceType = source;
                doSnapScreen();
            }
        });
        mFirstPage.setOnDirectionKeyListener(new FocusedRelativeLayout.DirectionKeyListener() {
            @Override
            public void onDirectionKeyDown(int keyCode, KeyEvent keyEvent) {
                LogUtils.d("hjian..." + keyCode);
                delay();
            }
        });
    }

    /**
     * make all views visible or gone
     *
     * @param visible
     */
    public void showAllVisbleOrGone(boolean visible) {
        if (visible) {
        	mLauncherLayout.setVisibility(View.VISIBLE);
            wifiImg.setVisibility(View.VISIBLE);
            interImg.setVisibility(View.VISIBLE);
            //usbImg.setVisibility(View.VISIBLE);
            digtalClock.setVisibility(View.VISIBLE);
            weatherView.setVisibility(View.VISIBLE);
        } else {
        	mLauncherLayout.setVisibility(View.INVISIBLE);
            wifiImg.setVisibility(View.INVISIBLE);
            interImg.setVisibility(View.INVISIBLE);
            //usbImg.setVisibility(View.INVISIBLE);
            digtalClock.setVisibility(View.INVISIBLE);
            weatherView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        isNewIntent = true;
        String action = intent.getAction();
        
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

    
    private void registerListener() {
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SELECT_SOURCE_COMPLETE, onPlayerListener1);
              
    }
    
    private void unregisterListener() {

        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SELECT_SOURCE_COMPLETE, onPlayerListener1);
    }
    
    
    /**
     * TV play listener
     */
    OnPlayerListener onPlayerListener1 = new OnPlayerListener() {
	
    	 @Override
         public void onPCAutoAdjustStatus(int arg0) {
             Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
         }

         @Override
         public void onSignalStatus(int arg0) {
             //if (Constant.LOG_TAG) {
                 Log.d(TAG, "onSignalStatus  arg0: " + arg0);
             //}
            
         }

         @Override
         public void onTimmingChanged(TimingInfo arg0) {
             if (Constant.LOG_TAG) {
                 Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
             }
         }

         @Override
         public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
             if (Constant.LOG_TAG) {
                 Log.d(TAG, "onSrcDetectPlugin  arg0: " + arg0);
             }
         }

         @Override
         public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
             if (Constant.LOG_TAG) {
                 Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
             }
         }
         
         @Override
         public void onSelectSource(int arg0) {
             if (Constant.LOG_TAG) {
                 Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
             }
         }
    	
    	
        @Override
        public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSourceComplete  arg0: " + arg0);
            }
            if(isNotifyDTVStartPlay == true){
            Util.notifyDTVStartPlay(MainActivity.this, false);
            isNotifyDTVStartPlay = false;
            Log.d(TAG, "===yiyonghui=== ");
            }
        }
		
	    @Override
        public void onPlayLock(ArrayList<Integer> list) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onPlayLock  arg0: " + list);
            }
        }
    };
    
    
    
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
        unregisterListener();
        mHandler.removeMessages(ACTIVITY_FINISH);
        switchMessageRemove(SWITCH_SOURCE);
        unregisterReceiver(mReceiver);
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
		mSwitchThread.getLooper().quitSafely();
		mSwitchThread = null;
    	super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerListener();
        fullScreen_Source = SystemProperties.get("persist.sys.fullScreen_Source");        
        mHandler.removeMessages(VIEW_NOSHOW);
        
        if (UtilLauncher.isExtralDevicesMount()){
        	usbImg.setVisibility(View.VISIBLE);
        }else{
        	usbImg.setVisibility(View.INVISIBLE);
        }
        
        if (Constant.LOG_TAG) {
            Log.d(TAG, "Registered");
        }
        mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
        TvSourceIdx = SourceManagerInterface.getLastSourceId();//Util.getCurSourceToPrefer(MainActivity.this);
        mFirstPage.setTextValue(TvSourceIdx);
        bootFlag = SystemProperties.get("persist.sys.bootup");
        Log.d(TAG, "Frist boot up is:" + bootFlag);
        if (!bootFlag.equals("1") && UmtvManager.getInstance().getFactory().isAgingModeEnable() == false) {
			        	//long timenow = System.currentTimeMillis();
        				long timenow = TimeUtils.getUptimeMillis();
        	Log.d(TAG, "start kill apps time is "+timenow);
	        //KillAppsBeforeLaunchingHome();
			KillBlackListApps();
			KillNonsystemApps();
			//long timethen = System.currentTimeMillis();
			long timethen =TimeUtils.getUptimeMillis();
			long timediff = timethen - timenow;
			Log.d(TAG, "start kill apps timediff is "+timediff);
			

            LogUtils.d("bootFlag: " + bootFlag + ", isAgingModeEnable: " + "false");

            doSnapActivity(true);
			

        } else {

        	int volume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        	//如果开机第一次volume值是零的话，显示静音图标。
            if (volume == 0) {
    			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    		}
            
            Log.d(TAG, "onResum to clear frist boot flag");
            Log.d(TAG, "onResum AgingMode flag:"+UmtvManager.getInstance().getFactory().isAgingModeEnable());
            SystemProperties.set("persist.sys.bootup", "" + 0);
		    try{
		        Context otherContext = createPackageContext(
		                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
		        Editor sharedata = otherContext.getSharedPreferences(
		                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
		                  + Context.MODE_MULTI_PROCESS).edit();
 		        sharedata.putInt("sleeponState",0); 
 		        sharedata.commit();
		    }catch (NameNotFoundException e) {
        	    e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.UMBOOT_COMPLETED");
            sendBroadcast(intent);
            LogUtils.d("isAgingModeEnable: " + UmtvManager.getInstance().getFactory().isAgingModeEnable());
            
        
            if (UmtvManager.getInstance().getFactory().isAgingModeEnable() == false){
				if(UmtvManager.getInstance().getFactory().isMModeEnable()){
					bootupToMMode(false);
				}/*else if(getSystemProperties("persist.sys.factorymode", 0) != 0){
					bootupToMMode(true);
				}*/
					Log.i("hehe", getSystemProperties("persist.sys.factorymode", 0)+"===========");
					
            	bootupToFullPlay();
                //finish();
            }else{
            	bootupToAgingMenu();
            }
        }

        registPackageReceiver();
    }
    
    /* 随开机广播启动太慢，暂时由Launcherq启动*/
    private void startServicesOnBoot() {
    	if (SystemProperties.getInt("sys.um.bootservices", 0) == 1) {
    		Log.v(TAG, "services is started already.");
    		return ;
    	}
    	SystemProperties.set("sys.um.bootservices", "1");
    	
    	Log.v(TAG, "ready to start services on boot...");
    	
    }
    
    private void KillAppsBeforeLaunchingHome()
    {
        ActivityManager am = (ActivityManager)MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(!actRSI.processName.equals("com.um.launcher")
                && !actRSI.processName.equals("com.android.musicfx")
                && !actRSI.processName.equals("com.um.umreceiver")
				&& !actRSI.processName.equals("android.process.acore")
                && !actRSI.processName.equals("com.um.dvb")
				//&& !actRSI.processName.equals("com.unionman.netsetup")
				//&& !actRSI.processName.equals("cn.com.unionman.umtvsetting.umsettingmenu")
                && !actRSI.processName.equals("com.unionman.dvbprovider")
                && !actRSI.processName.equals("com.um.dvb.entry")
                && !actRSI.processName.equals("com.um.dvbsearch")
                && !actRSI.processName.equals("com.unionman.caprovider")
                && !actRSI.processName.equals("com.um.upgrade")
                && !actRSI.processName.equals("com.unionman.dvbcitysetting")
                && !actRSI.processName.equals("com.unionman.settingwizard")
                && !actRSI.processName.equals("com.android.exchange")
                && !actRSI.processName.equals("com.android.settings")
                && !actRSI.processName.equals("android.process.media")
                && !actRSI.processName.equals("com.android.onetimeinitializer")
                && !actRSI.processName.equals("com.um.tv.menu")
                && !actRSI.processName.equals("com.android.smspush")
                && !actRSI.processName.equals("cn.com.unionman.umtvsystemserver")
                && !actRSI.processName.equals("com.hisilicon.android.hiRMService")
                && !actRSI.processName.equals("com.android.phone")
                && !actRSI.processName.equals("com.hisilicon.android.inputmethod.remote")
                && !actRSI.processName.equals("com.android.systemui")
                && !actRSI.processName.equals("system")
                && !actRSI.processName.equals("com.um.atv")
                && !actRSI.processName.equals("com.portplayer")
                && !actRSI.processName.contains("inputmethod")
			)
    		{
    			Log.v(TAG, "lanucher >>>> Kill process: " + actRSI.processName);
    			am.forceStopPackage(actRSI.processName);
    		}
    	}
    }

	  private void KillBlackListApps() {
		File file = new File("/vendor/etc/blacklist.xml");
		Log.d(TAG, "KillBlackListApps Enter ");
		if(file.exists() && file.isFile()){
	    	try {
				
		    	InputStream xml = new FileInputStream(file);
		    	ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		    	XmlPullParser pullParser = Xml.newPullParser();
		        pullParser.setInput(xml, "UTF-8"); //娑撶瘻ull鐟欙綁鍣撮崳銊啎缂冾喛顪呯憴锝嗙�閻ㄥ垕ML閺佺増宓�       
		        int event = pullParser.getEventType();
		        
		        while (event != XmlPullParser.END_DOCUMENT) {
		            
		            switch (event) {
		            
		            case XmlPullParser.START_DOCUMENT:
		                break;    
		            case XmlPullParser.START_TAG:   
		            	String name = pullParser.getName();
						if ("listname".equals(name)) {
							String value = pullParser.getAttributeValue(0);
							Log.d(TAG, "KillBlackListApps value is "+value);
	                        am.forceStopPackage(value);
	                    }
		                break;
		                
		            case XmlPullParser.END_TAG:
		                break;
		            }
		            
		            event = pullParser.next();
		        }
	    	} catch (FileNotFoundException e) {
	    		e.printStackTrace();
	    		Toast.makeText(this, "ERROR: blacklist.xml not found.", 
						Toast.LENGTH_LONG).show();
	    		return;
	    	} catch (XmlPullParserException e) {
	    		e.printStackTrace();
	    		Toast.makeText(this, "ERROR: parse blacklist.xml failed.", 
						Toast.LENGTH_LONG).show();
	        	return;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		Toast.makeText(this, "ERROR: read blacklist.xml failed.", 
						Toast.LENGTH_LONG).show();
	    		return;
	    	}
		}

	}

	  private boolean isSystemApplication(PackageManager packageManager, String packageName) {
		  if (packageManager == null || packageName == null || packageName.length() == 0) {
			  return false;
		  }
  
		  try {
			  ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
			  return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
		  } catch (NameNotFoundException e) {
			  e.printStackTrace();
		  }
		  return false;
	  }
	  
	private void KillNonsystemApps(){
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (int i=0; i<processes.size(); i++){
			ActivityManager.RunningAppProcessInfo infor = processes.get(i);
			String[] pkgNameList =  infor.pkgList;; // 閼惧嘲绶辨潻鎰攽閸︺劏顕氭潻娑氣柤闁插瞼娈戦幍鈧張澶婄安閻劎鈻兼惔蹇撳瘶 
	        // 鏉堟挸鍤幍鈧張澶婄安閻劎鈻兼惔蹇曟畱閸栧懎鎮� 
	        for (int j = 0; j < pkgNameList.length; j++) {  
	            String pkgName = pkgNameList[j];  
	            Log.i(TAG, "packageName " + pkgName + " at index " + j);
	            if(pkgName.equals("com.huan.appstore")){
	            	continue;
	            }
				if(!isSystemApplication(this.getPackageManager(), pkgName)){
					Log.i(TAG, "forceStopPackage " + pkgName + " at index " + j); 		
					am.forceStopPackage(pkgName);
				} 
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
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d("hjian... onKeyDown: " + keyCode);
        isSnapLeftOrRight = false;
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
                default:
                    break;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void doSelectSourceToTv(int switchMode){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();//getSelectSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		int sourceIdx = SourceManagerInterface.getSelectSourceId();
    		Log.d(TAG, "doSelectSourceToTv is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx+" getSelectSourceId = "+SourceManagerInterface.getSelectSourceId());
    		
        	if (switchMode == SWITCH_SOURCE_FULL_MODE){
                if (mCurrentSourceIdx != TvSourceIdx) {
                    Log.d(TAG, "doSelectSourceToTv");
    	             if (TvSourceIdx != EnumSourceIndex.SOURCE_DVBC
    	                		&& TvSourceIdx != EnumSourceIndex.SOURCE_DTMB){
    	                   SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
    	                   Log.d(TAG,"---------TvSourceIdx==="+TvSourceIdx);
    	                   SourceManagerInterface.selectSource(TvSourceIdx, 0);
    	           	 }else{
    	                   //SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
    	                   Log.d(TAG,"---------TvSourceIdx==="+TvSourceIdx);
    	                   SourceManagerInterface.selectSource(TvSourceIdx, 0);//现在v510改为异步模式，需要这步骤完成才继续下面的
    	                   isNotifyDTVStartPlay = true;
    	                   //Util.notifyDTVStartPlay(MainActivity.this, false);
    	           	 }	 
    	             setVideoWindowRect(false);
                  }else{
                	  setVideoWindowRect(false);  
                	  if ((sourceIdx == EnumSourceIndex.SOURCE_DVBC) || (sourceIdx == EnumSourceIndex.SOURCE_DTMB)){
                      		Util.notifyDTVStartPlay(this, false);
                      		Log.d(TAG, "Scaler DTV Windows to Small");
                	  }
                  }
        	}else{
        	 	if ((sourceIdx == EnumSourceIndex.SOURCE_DVBC) || (sourceIdx == EnumSourceIndex.SOURCE_DTMB)){
            		//Util.notifyDTVStartPlay(MainActivity.this, false);
            		//setVideoWindowRect(false);
        	 		Util.notifyDTVStartPlay(this, false);
            		Log.d(TAG, "Scaler DTV Windows to Small");
            	}
        	}
    	}
    }

    private void doDelectTVSource(int switchMode){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doDelectTVSource is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx+" getSelectSourceId = "+SourceManagerInterface.getSelectSourceId());
    		
        	if (mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
        		Log.d(TAG, "doDelectTVSource");
            	//setVideoWindowRect(true);
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
		
		if (asyn){
	        if (bSelectTV){
	        	switchMessageSend(SWITCH_SOURCE, SELECT_TVSOURCE, SWITCH_SOURCE_FULL_MODE);
	        	Log.d(TAG,"sendMessageDelayed SELECT_TVSOURCE");
	        }else{
	        	switchMessageSend(SWITCH_SOURCE, DELECT_TVSOURCE, SWITCH_SOURCE_FULL_MODE);
	        	Log.d(TAG,"sendMessageDelayed DELECT_TVSOURCE");
	        }
		}else{
			 if (bSelectTV){
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
                 rect.setY(235);
                 rect.setW(720);
                 rect.setH(405);
                 Log.d(TAG, "Scaler TV Windows to Small");
             }
            SourceManagerInterface.setWindowRect(rect, 0);
        }
        /*else if ((sourceIdx == EnumSourceIndex.SOURCE_DVBC) || (sourceIdx == EnumSourceIndex.SOURCE_DTMB)){
        	if (isFullScreen){
        		Util.notifyDTVStartPlay(this, true);
        		Log.d(TAG, "Scaler DTV Windows to Full");
        	}else{
        		Util.notifyDTVStartPlay(this, false);
        		Log.d(TAG, "Scaler DTV Windows to Small");
        	}
        } */
    }
    
    private void switchSourceOnSnapScreen(boolean bSelectTV){
		mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
		TvSourceIdx = SourceManagerInterface.getLastSourceId();
		
		 if (bSelectTV){
			 doSelectSourceToTv(SWITCH_SOURCE_STOPPLAY_MODE);
	     }else{
	    	 doDelectTVSource(SWITCH_SOURCE_STOPPLAY_MODE);
	     }
    }
    private void doSnapScreen(){
		KillNonsystemApps();
    	if (mSourceType == FocusedRelativeLayout.SourceChangeListener.SOURCE_LEFT){
    		switchSource(true, true);
    		mFirstPage.resetSignalCheckStatus();
    	}else{
    		switchSource(false,true);
    	}
    }
    private void doSnapActivity(boolean snapToLauncher){
    	if (snapToLauncher){
    		if (mSourceType == FocusedRelativeLayout.SourceChangeListener.SOURCE_LEFT){
    			//switchSource(true, true);
    			switchSource(false, true);
    		}else{
    			switchSource(false, true);
    		}

    		if ((mEnterActivyFlag & ENTER_ACTIVITY_INVISIBLE_VIEW) == ENTER_ACTIVITY_INVISIBLE_VIEW){
    			showAllVisbleOrGone(true);
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
        			Util.notifyDTVStopPlay(this);
        		}
        		if ((mEnterActivyFlag & ENTER_ACTIVITY_INVISIBLE_VIEW) == ENTER_ACTIVITY_INVISIBLE_VIEW){
        			mHandler.sendEmptyMessageDelayed(VIEW_NOSHOW, 400);
        		}
    		}else{
    			Log.d(TAG,"doSnapActivity false notifyDTVStopPlay 2");
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

    public void setFocusUp(boolean focusUp) {
        this.isFocusUp = focusUp;
    }

    public boolean isFocusUp() {
        return isFocusUp;
    }

    public boolean isSnapLeft() {
        return isSnapLeft;
    }

    public ImageView getWifiImg() {
        return wifiImg;
    }

    public ImageView getInterImg() {
        return interImg;
    }

    public int getFocusedView() {
        return mFocusedView;
    }

    public void setFocusedView(int focusedView) {
        this.mFocusedView = focusedView;
    }
	
	public void enterToPlay(){		
    	int sourceId = Integer.parseInt(fullScreen_Source);    	
    	if (sourceId != EnumSourceIndex.SOURCE_MEDIA)  //not cmcc.apk
    	{	
    		switchMessageRemove(SWITCH_SOURCE);
    		waitForSwitchLock();
    	    selectPlayActivity(sourceId);    
    	    setEnterActivityFlag(ENTER_ACTIVITY_BY_CLICK | ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE | ENTER_ACTIVITY_WITHOUT_SCALE_WINDOW);
    	}else{
            startDefaultApk();
    	}
		
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

    private HashMap<String, List<PosterInfo>> posterInfoMap = new HashMap<String, List<PosterInfo>>();
    public void getPosterInfos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                List<PosterInfo> gameInfos = PosterUtils.getAppPosterInfo(PosterConstants.URL_GAME);
                List<PosterInfo> eduInfos = PosterUtils.getAppPosterInfo(PosterConstants.URL_EDUCATION);
                List<PosterInfo> appStoreInfos = PosterUtils.getPosterInfo(PosterConstants.URL_APPSTORE);

/*                if (!gameInfos.isEmpty()) {
                    LogUtils.i("gameInfos: " + gameInfos.get(0).getImageUrl());
                }*/
                if (!eduInfos.isEmpty()) {
                    LogUtils.i("eduInfos: " + eduInfos.get(0).getImageUrl());
                }
                if (!appStoreInfos.isEmpty()) {
                    LogUtils.i("appStoreInfos: " + appStoreInfos.get(0).getImageUrl());
                }

//                posterInfoMap.put(PosterConstants.POSTER_GAME, gameInfos);
                posterInfoMap.put(PosterConstants.POSTER_EDUCATION, eduInfos);
                posterInfoMap.put(PosterConstants.POSTER_APP_STORE, appStoreInfos);

                mHandler.sendEmptyMessage(POSTER_URL);
            }
        }).start();
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
        LogUtils.d("hjian..." + hasFocus);
		if (hasFocus){
			delay();
            mFirstPage.updateCustomApp();
		}else{
			mHandler.removeMessages(ACTIVITY_FINISH);
		}
	}
	
    public void delay() {
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
		private Looper mLooper;
		
		public Handler getHandler(){
			if (!isAlive()){
				return null;
			}
			
			synchronized (this) {
				while (isAlive() && (mSwitchHandler == null)){
					try{
						wait();
					}catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			
			return mSwitchHandler;
		}
		
		public Looper getLooper(){
			
			if (!isAlive()){
				return null;
			}
			
			synchronized (this) {
				while (isAlive() && (mLooper == null)){
					try{
						wait();
					}catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			
			return mLooper;
		}
		
		@Override
		public void run(){
			Looper.prepare();
			synchronized (this){
				mSwitchHandler = new SwitchHandler();
				mLooper = Looper.myLooper();
				notifyAll();
			}
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

    private void registPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PackageReceiver.USER_PACKAGE_ADDED);
        intentFilter.addAction(PackageReceiver.USER_PACKAGE_REMOVED);
        registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("onReceive =intent:" + intent.getAction());
            mFirstPage.updateCustomApp();
        }
    };
    
    /**
   	 * 判断某个服务是否正在运行的方法
   	 * 
   	 * @param mContext
   	 * @param serviceName
   	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
   	 * @return true代表正在运行，false代表服务没有正在运行
   	 */
   	public boolean isServiceWork(Context mContext, String serviceName) {
   		boolean isWork = false;
   		ActivityManager myAM = (ActivityManager) mContext
   				.getSystemService(Context.ACTIVITY_SERVICE);
   		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
   		if (myList.size() <= 0) {
   			return false;
   		}
   		for (int i = 0; i < myList.size(); i++) {
   			String mName = myList.get(i).service.getClassName().toString();
   			if (mName.equals(serviceName)) {
   				isWork = true;
   				break;
   			}
   		}
   		return isWork;
   	}
   	
    public static int getSystemProperties(String key, int defauleValue) {
        String value = SystemProperties.get(key, defauleValue + "");
        if (value != null && value.trim().length() > 0) {
            return Integer.parseInt(value);
        }
        return defauleValue;
    }
}
