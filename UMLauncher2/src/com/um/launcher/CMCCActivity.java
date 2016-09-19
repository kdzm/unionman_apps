
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

public class CMCCActivity extends Activity{
    private static final String TAG = "CMCCActivity";
    private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
    private static final String HOME_KEY_ACTION = "cn.com.unionman.home.action";
    private static final String UMBOOT_COMPLETED_ACTION = "android.intent.action.UMBOOT_COMPLETED";
    
    private final static String UPDATE_PATH = "/storage/emulated/0/recovery2/update.zip";
    private final static String CACHE_PATH = "/cache2/update.zip";
    private File updateFile;
    private File cacheFile;
    
    // mark for control focus
    public static boolean isSnapLeftOrRight = false;
    public static boolean isNeedResetTvFocus = false;
    // Whether the locale is changed
    public static boolean isChangeLocale = false;
    // Whether the page,The main control paging process of focus,
    // Only switch to next page for focus switches
    public static boolean mFlipFlag = false;
    // Mark whether the focus is at the top
    // Whether it is sliding to the left
    private String  fullScreen_Source = "";
    //是否需要发送startDTV
    private boolean isNewIntent = false;
    private AudioManager mAudioManager;
    private int TvSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private int mCurrentSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private String bootFlag = null;
    private int mSourceType = FocusedRelativeLayout.SourceChangeListener.SOURCE_LEFT;
    private int config_bootSourceState;
    
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
        Log.i(TAG,"============bootSourceState="+bootSourceState);
    	if(bootSourceState == 0){
        	startDefaultApk();    		
    	}else{
    		int mdbSource = Integer.parseInt(fullScreen_Source);
			Log.i(TAG,"============mdbSource "+mdbSource);			
        	if (mdbSource != EnumSourceIndex.SOURCE_MEDIA)
        	{	
        	    selectPlayActivity(mdbSource);
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
        
        setContentView(R.layout.activity_cmcc);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (NetWorkUtils.isNetworkAvailable(this)) {
            getPosterInfos();
        }

        if(!  isServiceWork(CMCCActivity.this,"com.android.settings.bluetooth.BlueToothAutoPairService"))	
          {
	        Log.i(TAG, "start BlueToothAutoPairService");
	        Intent autoPairIntent = new Intent("UM_BlueToothAutoPairService");
	        startService(autoPairIntent);
        }
        
        if(!isServiceWork(CMCCActivity.this,"com.um.storemodeservice.StoreModeService"))	
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
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        fullScreen_Source = SystemProperties.get("persist.sys.fullScreen_Source");        
        
        if (Constant.LOG_TAG) {
            Log.d(TAG, "Registered");
        }
        mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
        TvSourceIdx = SourceManagerInterface.getLastSourceId();//Util.getCurSourceToPrefer(CMCCActivity.this);
  
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
			startDefaultApk();
			
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
				
				if (getSystemProperties("persist.sys.factorymode", 0) == 0)
				{
					bootupToFullPlay();
				}
            	else{
					selectPlayActivity(EnumSourceIndex.SOURCE_ATV);
				}
                //finish();
            }else{
            	bootupToAgingMenu();
            }
        }
    }
    
	@Override
    protected void onDestroy()
    {
    	super.onDestroy();
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
        ActivityManager am = (ActivityManager)CMCCActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d("hjian... onKeyDown: " + keyCode);
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
            	case KeyEvent.KEYCODE_BACK:
            		//enterToPlay();
            		break;
                default:
                    break;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void startToActivity(Intent intent){
    	startActivity(intent);
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

            }
        }).start();
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
        LogUtils.d("hjian..." + hasFocus);
	}
    
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
