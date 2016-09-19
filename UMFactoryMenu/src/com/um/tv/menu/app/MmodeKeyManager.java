package com.um.tv.menu.app;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.CusSystemSetting;
import com.hisilicon.android.tvapi.vo.ColorTempInfo;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.AgingWindow;
import com.um.tv.menu.ui.VGAAdjustingDialog;

import com.um.tv.menu.utils.DeviceInfoUtils;
import com.um.tv.menu.utils.FileUtils;
import com.um.tv.menu.utils.SocketClient;
import com.um.tv.menu.interfaces.SourceManagerInterface;
import com.um.tv.menu.interfaces.PictureInterface;
import android.os.storage.IMountService;
import android.os.ServiceManager;

import android.app.Activity;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import com.um.huanauth.data.HuanClientAuth;

import com.um.tv.menu.app.CaInfoAccessor;


import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;


public class MmodeKeyManager {
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "MmodeKeyManager:";
	private static final String INTENT_ATV = "android.intent.umaction.UMATV";
	private static final String INTENT_DTV = "com.unionman.intent.ACTION_PLAY_DVB";
	private static final String INTENT_PORT = "android.intent.PortPlayer";
	private static final String DTV_DB_PATH = "/data/dvb/";
	private static final String DTV_DAT_PATH = "/data/data/";
	private static final String ATV_DB_PATH = "/atv/db/";
	private static final String PROGRAM_PATH = "/atv/dtv/preProgramSet/";
    private final String UPGRADE_DEFAULT_MOUNTED_PATH = "/mnt/sda/sda1/";
	private final String UPGRADE_ZIP_FILE = "update.zip";
	
	private final static int UM_TRANS_SYS_TYPE_CAB = 2;
	private final static int UM_TRANS_SYS_TYPE_TER = 3;
    private static Context mContext = null;
    private static MmodeKeyManager mManager = null;
    private static boolean mMmodeShow = false;
    private CusFactory mFactory = UmtvManager.getInstance().getFactory();
	private  ArrayList<Integer> mCVBSList = null;
	private static ArrayList<Integer> mHDMIList = null;
	private int mCVBSIndex = 0;
	private static int mHDMIIndex = 0;
	private static int mBLKIndex = 0;
	private static CusSourceManager mSourceManager = null;
	private AudioManager mAudioManager = null;
    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mMmodeKeyManagerLayoutParams = null;
    private RelativeLayout mRoot = null;

	private TextView tv_software_version_val;
	private TextView tv_build_time_val;
	private TextView mSerialNO;
	private TextView mHwVer;
	private TextView mDeviceID;
	private TextView mMacAddr;
	private TextView mClientType;
	private TextView mProjectID;
	private TextView mCaCardNO;
	
	private TextView tv_software_ver;
	private TextView tv_build_time;
	private TextView tv_SerialNO;
	private TextView tv_HwVer;
	private TextView tv_DeviceID;
	private TextView tv_MacAddr;
	private TextView tv_ClientType;
	private TextView tv_ProjectID;
	private TextView tv_CaCardNO;
    
    private Object mSwitchLock;
    private int TvSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private int mCurrentSourceIdx = EnumSourceIndex.SOURCE_ATV;
    
	private int seconds = 0;
	private int day = 0;
	private int hour = 0;
	private int min = 0;
	private int sec = 0;
	private boolean isSnNeedUpdate = false;
	
	private int mVisible = View.VISIBLE;
	private static final int CHANGE_VISIBLE = 1;
	private TimerTask mModeTimerTask = null;
	private Timer mModeTimer = null;
	private CaInfoAccessor mCaAccessor = null;
	private String mCaCardNum = "";
	private Dialog mSystemUpdateDialog = null;
	private int caCardStage = 0;
	
    private WifiManager mWifiManager;
    
    private static final String FACTORY_CMDS = "com.um.tv.factorycmds";
	
	private static final String FACCMD_SWITCHHDMI = "switch_hdmi";
	private static final String FACCMD_SAVEAWB = "save_awb";
	private static final String FACCMD_RESETAWB = "reset_awb";
	private static final String FACCMD_FINISH = "finish_awb";
	private static final String FACCMD_SHOWSN = "show_sn";

	
    private MmodeKeyManager() {
    	mSourceManager = UmtvManager.getInstance().getSourceManager();
    	mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
    	
        ArrayList<Integer> sourceList = mSourceManager.getSourceList();
        mCVBSList = new  ArrayList<Integer>();
        mHDMIList = new  ArrayList<Integer>();
        caCardStage = 0;
		
        for (int i = 0;i < sourceList.size(); i++){
        	if ((sourceList.get(i) >= EnumSourceIndex.SOURCE_CVBS1)
        			&&(sourceList.get(i) <= EnumSourceIndex.SOURCE_CVBS3)){
        		mCVBSList.add(sourceList.get(i));
        	}
        	
        	if ((sourceList.get(i) >= EnumSourceIndex.SOURCE_HDMI1)
        			&&(sourceList.get(i) <= EnumSourceIndex.SOURCE_HDMI4)){
        		mHDMIList.add(sourceList.get(i));
        	}
        }
        
        if (null == mAudioManager){
            mAudioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        }
	   mSwitchLock = new Object();
	   Log.d(TAG, "mContext.registerReceiver facbc");
        IntentFilter lfilter = new IntentFilter(FACTORY_CMDS);
        mContext.registerReceiver(facbc, lfilter);
	   /*
	   IntentFilter lfilter = new IntentFilter(FACTORY_CMDS);
	   mContext.registerReceiver(facbc, lfilter);
	   */
    }
    
    public static MmodeKeyManager from(Context context) {
        mContext = context;
        if (mManager == null) {
            mManager = new MmodeKeyManager();
        }
        return mManager;
    }
    
	private Handler mHandler = new Handler(){
		 @Override
	     public void handleMessage(Message msg) {
	           switch (msg.what) {
	           case CHANGE_VISIBLE:
	        	   if(mVisible == View.INVISIBLE)
	        		   mVisible = View.VISIBLE;
	        	   else
	        		   mVisible = View.INVISIBLE;
	        	   
	        	   changeVisible(View.VISIBLE);
	        	   break;
	            default:
	                break;
	            }
	            super.handleMessage(msg);
	        }
	};
	
    private class MmodeTimerTask extends TimerTask{
    	@Override
    	public void run() {
    		seconds++;
			day = (seconds / 86400);
			hour = (seconds / 3600)%24;
			min = (seconds / 60)%60;
			sec = (seconds % 60);
    		mHandler.sendEmptyMessage(CHANGE_VISIBLE);
    	}
    };
    
    private void changeVisible(int isVisible){

		String serialNo= "";
		String hwVer = "";
		String deviceId = "";
		String macAddr = "";
		String clientType = "";
		String projectId = "";
		if(isSnNeedUpdate){
			DeviceInfoUtils.reloadDeviceInfo();
			isSnNeedUpdate = false;
		}
		
		serialNo = DeviceInfoUtils.getSn();
		hwVer = DeviceInfoUtils.getHwVersion();
		deviceId = DeviceInfoUtils.getDeviceId();
		macAddr = DeviceInfoUtils.getDevMac();
		clientType = DeviceInfoUtils.getClientType();
		projectId = DeviceInfoUtils.getProjectId();

		mSerialNO.setText(serialNo);
		mHwVer.setText(hwVer);
		mDeviceID.setText(deviceId);
		mMacAddr.setText(macAddr);
		mClientType.setText(clientType);
		mProjectID.setText(projectId);
		
    	mSerialNO.setVisibility(isVisible);
		mHwVer.setVisibility(isVisible);
		mDeviceID.setVisibility(isVisible);
		mMacAddr.setVisibility(isVisible);
		mClientType.setVisibility(isVisible);
		mProjectID.setVisibility(isVisible);
		tv_software_version_val.setVisibility(isVisible);
		tv_build_time_val.setVisibility(isVisible);
		
		tv_software_ver.setVisibility(isVisible);
		tv_build_time.setVisibility(isVisible);
		tv_SerialNO.setVisibility(isVisible);
		tv_HwVer.setVisibility(isVisible);
		tv_DeviceID.setVisibility(isVisible);
		tv_MacAddr.setVisibility(isVisible);
		tv_ClientType.setVisibility(isVisible);
		tv_ProjectID.setVisibility(isVisible);
		
		int curid = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
		if( EnumSourceIndex.SOURCE_DVBC == curid
				||EnumSourceIndex.SOURCE_DTMB == curid){
			
			boolean isCardInserted = mCaAccessor.getCardStatus();
			boolean isProtocalT0 = false;
			if(isCardInserted){
				if(0 == caCardStage){
					caCardStage = 1;
					mCaCardNO.setText(R.string.card_inserted);
					isProtocalT0 = mCaAccessor.checkCardProtocalT0();
					Log.d(TAG, "isProtocalT0 = " + isProtocalT0);
					if(isProtocalT0){
						if(caCardStage == 1){
							caCardStage = 2;
							mCaCardNO.setText(R.string.card_t0);
						}
					}else{
						caCardStage = 3;
						mCaCardNO.setText(R.string.card_unknow);
					}
				}
			}else{
				if(caCardStage == 2){
					mCaCardNO.setText(R.string.card_removed_ok);
				}else if(caCardStage == 3){
					mCaCardNO.setText(R.string.card_removed);
				}
				caCardStage = 0;
				mCaCardNum = "no Card Or not int DVBC or DTMB";
			}
		}else{
			mCaCardNum = "no Card Or not int DVBC or DTMB";
			caCardStage = 0;
			mCaCardNO.setText(R.string.ca_cardno_val);
		}
		//Log.d(TAG, "caCardStage = "+caCardStage);
		//mCaCardNO.setText(mCaCardNum);
		mCaCardNO.setVisibility(isVisible);
		tv_CaCardNO.setVisibility(isVisible);

    }
    
	private void initData(){
		String serialNo= "";
		String hwVer = "";
		String deviceId = "";
		String macAddr = "";
		String clientType = "";
		String projectId = "";
		
		HuanClientAuth huanClientAuth = new HuanClientAuth(mContext);
		/*
		deviceType = huanClientAuth.getDevicemode();
		deviceId = huanClientAuth.getDeviceid();
		dnum = huanClientAuth.getDnum();
		didtoken = huanClientAuth.getDidtoken();
		huanId = huanClientAuth.getHuanid();
		*/
		serialNo = DeviceInfoUtils.getSn();
		hwVer = DeviceInfoUtils.getHwVersion();
		deviceId = DeviceInfoUtils.getDeviceId();
		macAddr = DeviceInfoUtils.getDevMac();
		clientType = DeviceInfoUtils.getClientType();
		projectId = DeviceInfoUtils.getProjectId();

		
		if(mRoot != null){
			tv_software_version_val = (TextView) mRoot.findViewById(R.id.tv_Software_Version_val);
			tv_build_time_val = (TextView) mRoot.findViewById(R.id.tv_Build_Time_val);
			mSerialNO = (TextView) mRoot.findViewById(R.id.tv_sn_no_val);
			mHwVer = (TextView) mRoot.findViewById(R.id.tv_hw_ver_val);
			mDeviceID = (TextView) mRoot.findViewById(R.id.tv_deviceid_val);
			mMacAddr = (TextView) mRoot.findViewById(R.id.tv_mac_addr_val);
			mClientType = (TextView) mRoot.findViewById(R.id.tv_client_type_val);
			mProjectID = (TextView) mRoot.findViewById(R.id.tv_project_id_val);
			mCaCardNO = (TextView) mRoot.findViewById(R.id.tv_ca_cardno_val);
			
			tv_software_ver = (TextView) mRoot.findViewById(R.id.tv_Software_Version);
			tv_build_time = (TextView) mRoot.findViewById(R.id.tv_Build_Time);
			tv_SerialNO = (TextView) mRoot.findViewById(R.id.tv_sn_no);
			tv_HwVer = (TextView) mRoot.findViewById(R.id.tv_hw_ver);
			tv_DeviceID = (TextView) mRoot.findViewById(R.id.tv_deviceid);
			tv_MacAddr = (TextView) mRoot.findViewById(R.id.tv_mac_addr);
			tv_ClientType = (TextView) mRoot.findViewById(R.id.tv_client_type);
			tv_ProjectID = (TextView) mRoot.findViewById(R.id.tv_project_id);
			tv_CaCardNO = (TextView) mRoot.findViewById(R.id.tv_ca_cardno);
		}
		mSerialNO.setText(serialNo);
		mHwVer.setText(hwVer);
		mDeviceID.setText(deviceId);
		mMacAddr.setText(macAddr);
		mClientType.setText(clientType);
		mProjectID.setText(projectId);
		
		//mCaCardNum = mCaAccessor.getCardNO();
		mCaCardNum = "no Card Or not int DVBC or DTMB";
		Log.d(TAG, "mCaCardNum = "+ mCaCardNum);
		mCaCardNO.setVisibility(View.INVISIBLE);
		tv_software_version_val.setText(Build.UMTVSWVER);//need to do
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date buildTime = new Date(Build.TIME);
		String buildTimeStr = formatter.format(buildTime); 
		tv_build_time_val.setText(buildTimeStr);
		
		mSerialNO.setTextColor(Color.YELLOW);
		mHwVer.setTextColor(Color.YELLOW);
		mDeviceID.setTextColor(Color.YELLOW);
		mMacAddr.setTextColor(Color.YELLOW);
		mClientType.setTextColor(Color.YELLOW);
		mProjectID.setTextColor(Color.YELLOW);
		mCaCardNO.setTextColor(Color.YELLOW);
		tv_software_version_val.setTextColor(Color.YELLOW);
		tv_build_time_val.setTextColor(Color.YELLOW);
		
		tv_software_ver.setTextColor(Color.YELLOW);
		tv_build_time.setTextColor(Color.YELLOW);
		tv_SerialNO.setTextColor(Color.YELLOW);
		tv_DeviceID.setTextColor(Color.YELLOW);
		tv_MacAddr.setTextColor(Color.YELLOW);
		tv_ClientType.setTextColor(Color.YELLOW);
		tv_ProjectID.setTextColor(Color.YELLOW);
		tv_CaCardNO.setTextColor(Color.YELLOW);
		tv_HwVer.setTextColor(Color.YELLOW);
		
		mCaAccessor = new CaInfoAccessor(mContext.getContentResolver());
	}
	
	
    private void mModeShowNote(){
    	mMmodeKeyManagerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	mMmodeKeyManagerLayoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_PHONE ,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);

    	mMmodeKeyManagerLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
    	mMmodeKeyManagerLayoutParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
    	//mRoot = LayoutInflater.from(mContext).inflate(R.layout.mmode_note_window_layout, null);
    	
    	LayoutInflater inflater = LayoutInflater.from(mContext);
    	mRoot = (RelativeLayout)inflater.inflate(R.layout.mmode_note_window_layout, null);
    	if (mRoot != null){
    		mWindowManager.addView(mRoot, mMmodeKeyManagerLayoutParams);
    		mMmodeShow = true;
			//SystemProperties.set("ctl.stop", "serialserver");
	        //SystemProperties.set("ctl.start", "serialserver");
    	}

		initData();
		mModeTimerTask = new MmodeTimerTask();
		mModeTimer = new Timer();
		mModeTimer.schedule(mModeTimerTask, 1000, 1000);
    	
    	mFactory.setPoweronMode(0);
    	//startFactoryCmds();
    	/*
    	Log.d(TAG, "mContext.registerReceiver facbc");
    	IntentFilter lfilter = new IntentFilter(FACTORY_CMDS);
  	    mContext.registerReceiver(facbc, lfilter);
  	    */
    	
    }

	private String getCaCardNum(){

		int curid = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
		
    	if(EnumSourceIndex.SOURCE_ATV != curid
			|| EnumSourceIndex.SOURCE_DVBC != curid
			||EnumSourceIndex.SOURCE_DTMB != curid){
			
			return null;
		}
    	/*
		Ca ca = new Ca(DVB.GetInstance());
		Card_No card_no = new Card_No();
		ca.CaGetIcNo(0, card_no);
		String cardno = new String(card_no.cardno);
		*/
		return null;
	}
    
    public void mModeDismissNote(){
    	if (mRoot != null){
    		mWindowManager.removeView(mRoot);
    		mMmodeShow = false;
    		mRoot = null;
    		
        	int powerMode = Integer.parseInt(SystemProperties.get("persist.sys.powerMode", "1"));
    		if(mFactory.getPoweronMode()!=powerMode)
            {
                  mFactory.setPoweronMode(powerMode);
            }
    		//SystemProperties.set("ctl.stop", "serialserver");
    	}   
    	if (mModeTimerTask != null){
    		mModeTimerTask.cancel();
    		mModeTimerTask = null;
    	}
    	if(mContext != null){
    	//	mContext.unregisterReceiver(facbc);
    }
    }
    
    public static boolean ismModeNoteShow(){
    	return mMmodeShow;
    }
    
    public void mModeKeyProcess(boolean enable){
    	Log.d(TAG, SUBTAG+"enable:"+enable+";mFactory.isMModeEnable()"+mFactory.isMModeEnable());
    	if (enable != mFactory.isMModeEnable()){
        	if (mFactory.isMModeEnable()){
        		mFactory.enableMMode(false);
        		mModeDismissNote();
        		Log.d(TAG, SUBTAG+"disable MMode");
        		int curVol = Integer.parseInt(SystemProperties.get("persist.sys.volume", "30"));
        		if(null != mAudioManager){
        			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVol, AudioManager.FX_FOCUS_NAVIGATION_UP);
        		}else{
        			mAudioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVol, AudioManager.FX_FOCUS_NAVIGATION_UP);
        		}
        	}else{
        		mFactory.enableMMode(true);
        		mModeShowNote();
        		int currentVolume;
        		Log.d(TAG, SUBTAG+"enable MMode");
        		if(null != mAudioManager){
        			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 80, AudioManager.FX_FOCUS_NAVIGATION_UP);
        		}else{
        			mAudioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 80, AudioManager.FX_FOCUS_NAVIGATION_UP);
        		}
        		if(!mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)){
        			SystemProperties.set("persist.sys.volume", currentVolume+"");
        		}
        	}
    	}
    }
    
    public void mModeBootProcess(){
    	if (mFactory.isMModeEnable()){
    		mModeShowNote();
    	}
    }
    
    public void mModeNoteProcess(boolean enable){

		if (enable){
			if (mMmodeShow == false){
				mModeShowNote();
			}
		}else{
			if (mMmodeShow){
				mModeDismissNote();
			}
		}
    }
    
    private void resetDTVTestProg() {
    	FileUtils.deleteFile(ATV_DB_PATH+"atv.db");
    	FileUtils.copyFile(PROGRAM_PATH+"atv.db", ATV_DB_PATH+"atv.db");
    	FileUtils.changeFileMod(ATV_DB_PATH+"atv.db", "777");
    }

    private void createDTVTestProg() {
    	Log.i("DTV_PREPROG","createDTVTestProg");
    	FileUtils.deleteFile(DTV_DB_PATH+"dvb.db");
    	FileUtils.deleteFile(DTV_DAT_PATH+"umdb.dat");
    	FileUtils.deleteFile(ATV_DB_PATH+"atv.db");
    	FileUtils.copyFile(PROGRAM_PATH+"dvb.db", DTV_DB_PATH+"dvb.db");
    	FileUtils.copyFile(PROGRAM_PATH+"umdb.dat", DTV_DAT_PATH+"umdb.dat");
    	FileUtils.copyFile(PROGRAM_PATH+"atv.db", ATV_DB_PATH+"atv.db");
    	FileUtils.changeFileMod(DTV_DB_PATH+"dvb.db", "777");
    	FileUtils.changeFileMod(DTV_DAT_PATH+"umdb.dat", "777");
    	FileUtils.changeFileMod(ATV_DB_PATH+"atv.db", "777");
    	Log.i("DTV_PREPROG","restartDvbServer1");
    	SystemProperties.set("ctl.stop", "dvbserver");
        //SystemProperties.set("ctl.start", "dvbserver");
    	CusFactoryImpl.getInstance().loadATVProg();
    	Log.i("DTV_PREPROG","loadATVProg");
    	Log.i("DTV_PREPROG","endcreateDTVTestProg");
    }

    private void systemInitProcess(){
    	
    	Log.d(TAG, SUBTAG+"systemInitProcess");
    	
    	CusSystemSetting systemSetting = UmtvManager.getInstance().getSystemSetting();
    	systemSetting.restoreDefault(0);
    	CusFactoryImpl.getInstance().resetNVM();
    	mFactory.enableMMode(false);
    	mFactory.enableAgingMode(false);

		mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
		
        if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}

		
    	Intent it = new Intent("com.unionman.intent.ACTION_FAST_RESET");
    	it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	it.putExtra("extra", "sysInit");
        mContext.startActivity(it);
    	
        /*SocketClient socketClient = null;
        socketClient = new SocketClient();
        socketClient.writeMsg("reset");
        socketClient.readNetResponseSync();
        mContext.sendBroadcast(new Intent(
                "android.intent.action.MASTER_CLEAR"));*/
    	
    	Toast toast = Toast.makeText(mContext, "System init...", Toast.LENGTH_LONG);
    	toast.show();
        
    }
    
    private void doSystemReboot() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("startTime", 1);
        intent.putExtra("window", 0);
        mContext.sendBroadcast(intent);
    }
    
    private void factoryResetProcess(){
    	CusSystemSetting systemSetting = UmtvManager.getInstance().getSystemSetting();
    	systemSetting.restoreDefault(0);
    	
    	mFactory.enableMMode(false);
    	mFactory.enableAgingMode(false);
    	Intent it = new Intent("com.unionman.intent.ACTION_FAST_RESET");
    	it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	it.putExtra("extra", "");
        mContext.startActivity(it);

		mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
		
        if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
    	
//        SocketClient socketClient = null;
//        socketClient = new SocketClient();
//        socketClient.writeMsg("reset");
//        socketClient.readNetResponseSync();
//        mContext.sendBroadcast(new Intent(
//                "android.intent.action.MASTER_CLEAR"));
    	
    	
    }
    
    private void forceStopPackage(Context context, String packageName) {
		try {
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			Method method = Class.forName("android.app.ActivityManager")
					.getMethod("forceStopPackage", String.class);
			method.invoke(am, packageName);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
    
    private void programSetProcess(){
    	//FactoryImpl.getInstance().resetATVProg();
    	//resetDTVTestProg();
    	forceStopPackage(mContext, "com.unionman.dvbprovider");
    	createDTVTestProg();
    	forceStopPackage(mContext, "com.unionman.dvbprovider");
    }
    
    private void versionShowProcess(){
        ComponentName componentName = new ComponentName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.SysInfoActivity");
        Intent mIntent = new Intent();
        mIntent.setComponent(componentName);
        mIntent.putExtra("dialog_mode", 2);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

	    private void wifiInterfaceEntry(){
        ComponentName componentName = new ComponentName("com.unionman.netsetup", "com.unionman.netsetup.MainActivity");
        Intent mIntent = new Intent();
		Bundle bundle = new Bundle();
		int facCall = 1;
		bundle.putInt("fromeFactory", facCall);
        mIntent.setComponent(componentName);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }
    
    private void cvbsSwichProcess(){
    	
    	if (mCVBSList.size() == 0){
    		return;
    	}
    	
    	if (mCVBSIndex >= mCVBSList.size()){
    		mCVBSIndex = 0;
    	}
    	
    	switchSourceToPlay(mCVBSList.get(mCVBSIndex));
    	mCVBSIndex++;
    }
    
    public static void hdmiSwichProcess(){
    	
    	if (mHDMIList.size() == 0){
    		return;
    	}
    	
    	if (mHDMIIndex >= mHDMIList.size()){
    		mHDMIIndex = 0;
    	}
    	
    	switchSourceToPlay(mHDMIList.get(mHDMIIndex));
    	mHDMIIndex++;
    }
    
    public static void BLKSwichProcess(){
	
    	if (mBLKIndex >= 1){
    		mBLKIndex = 0;
    		Log.d(TAG,"====setBackLight=100");
    		PictureInterface.setBacklight(100);
    	}else{
    		PictureInterface.setBacklight(50);
    		Log.d(TAG,"====setBackLight=50");
    		mBLKIndex++;
    	}
    }
    
    
    public static void ypbprSwichProcess(){
    	switchSourceToPlay(EnumSourceIndex.SOURCE_YPBPR2);
    }
    
    public static void vgaSwichProcess(){
    	switchSourceToPlay(EnumSourceIndex.SOURCE_VGA);
    }
    
    public static void atvSwichProcess(){
    	switchSourceToPlay(EnumSourceIndex.SOURCE_ATV);
    }
    
    public static void dvbcSwichProcess(){
    	switchSourceToPlay(EnumSourceIndex.SOURCE_DVBC);
    }
    
    public static void dtmbSwichProcess(){
    	switchSourceToPlay(EnumSourceIndex.SOURCE_DTMB);
    }
    
    private void openMediaCenter(){
        ComponentName componentName = new ComponentName("com.umexplorer", "com.umexplorer.activity.SelectFileType");
        Intent mIntent = new Intent();
        mIntent.setComponent(componentName);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

	private void openBlueTeethSetting(){
        ComponentName componentName = new ComponentName("com.unionman.settings", "com.unionman.settings.Settings$BluetoothSettingsActivity");
        Intent mIntent = new Intent();
        mIntent.setComponent(componentName);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
	}

	private void openBlueTeethScaner(){
		Intent blueteeth = new Intent();
		//blueteeth.setAction("android.bluetooth.devicepicker.action.LAUNCH");
		blueteeth.setAction("android.settings.BLUETOOTH_SETTINGS");
		blueteeth.addCategory(Intent.CATEGORY_DEFAULT);
        blueteeth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        blueteeth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(blueteeth);
	}
	
	private void startFactoryCmds(){
		Intent facCmds = new Intent();
		facCmds.setAction("android.intent.action.FACTORY_COMMANDS");
		facCmds.addCategory(Intent.CATEGORY_DEFAULT);
		facCmds.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		facCmds.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(facCmds);
	}
    
//    private void openUsbUpgrade(){
//        ComponentName componentName = new ComponentName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.LocalUpgradeActivity");
//        Intent mIntent = new Intent();
//        mIntent.setComponent(componentName);
//        mIntent.putExtra("dialog_mode", 6);
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(mIntent);
//    }
    
	private String checkUpgrade(Context context) {
		try {
			IBinder service = ServiceManager.getService("mount");
			if (service != null) {
				IMountService mountService = IMountService.Stub
						.asInterface(service);
				List<android.os.storage.ExtraInfo> mountList = mountService
						.getAllExtraInfos();
				int deviceCount = mountList.size();
				for (int i = 0; i < deviceCount; i++) {
					if (mountList.get(i).mMountPoint.startsWith("/mnt")&&CheckUpdateFile(mountList.get(i).mMountPoint)) {
						return mountList.get(i).mMountPoint;
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean CheckUpdateFile(String path) {
		File file = new File(path + "/" + UPGRADE_ZIP_FILE);
		try {
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return false;
		}
		return false;

	}
    
    
	private void openUsbUpgrade(Context context) {
		
		String mountPoint = null;
		
		mountPoint = checkUpgrade(context);
		if(mountPoint==null)
		{
			Toast.makeText(context, R.string.upgrade_file_not_exists, Toast.LENGTH_SHORT).show();		
			return ;	
		}
		
		SocketClient socketClient = null;
        socketClient = new SocketClient();
        socketClient.writeMsg("upgrade " + mountPoint);
        socketClient.readNetResponseSync();
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
        intent.putExtra("mount_point", mountPoint);
        context.sendBroadcast(intent);
	}
    
    private void adjustStreamVolume(int keyCode)
    {   
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  
      
        boolean mute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        if (mute == true && keyCode == KeyEvent.KEY_VOLUP15){
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
        
        if (keyCode == KeyEvent.KEY_VOLUP15){
        	currentVolume = currentVolume + 15;
        }else{
        	currentVolume = currentVolume - 15;
        }
        
        if (currentVolume <= 0){
        	mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }
        
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FX_FOCUS_NAVIGATION_UP);
        
    }
    
    private void vgaAdjustKeyProcess(){
    	if (UmtvManager.getInstance().getSourceManager().getSelectSourceId() == EnumSourceIndex.SOURCE_VGA){
        	Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
        	intent_pic.putExtra("type", "vga_adjust_fac");
        	mContext.startService(intent_pic);
    	}
    }

	private void vgaPCAutoAdjust(){
		if (UmtvManager.getInstance().getSourceManager().getSelectSourceId() == EnumSourceIndex.SOURCE_VGA){
			UmtvManager.getInstance().getPCSetting().autoAdjust();
			
			if(mSystemUpdateDialog != null && mSystemUpdateDialog.isShowing())
	            mSystemUpdateDialog.dismiss();
	        mSystemUpdateDialog = new VGAAdjustingDialog( mContext, 0);
	        mSystemUpdateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
	        Window window = mSystemUpdateDialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.height = LayoutParams.WRAP_CONTENT;
	        lp.width = LayoutParams.WRAP_CONTENT;
	        window.setAttributes(lp);
	        mSystemUpdateDialog.show();
		}
	}
    private void soundBalanceKeyProcess(){
		Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
		intent_voice.putExtra("type", "sound_balance");
		mContext.startService(intent_voice);
    }
    
    public void mModeQuickKeyProcess(int key){
    	switch (key){
    	case KeyEvent.KEY_SYSINIT:
    		systemInitProcess();
    		break;
    	case KeyEvent.KEY_RESET:
    		factoryResetProcess();
    		break;
    	case KeyEvent.KEY_PROCGRAMSET:
    		programSetProcess();
    		break;    		
    	case KeyEvent.KEY_VERSION:
    		versionShowProcess();
    		break;
    	case KeyEvent.KEY_USB:
    		openMediaCenter();
    		break;
    	case KeyEvent.KEY_USBUPGRADE:
    		openUsbUpgrade(mContext);
    		break;
    	case KeyEvent.KEYCODE_CVBS:
    		cvbsSwichProcess();
    		break;
    	case KeyEvent.KEYCODE_HDMI:
    		hdmiSwichProcess();
    		break;
    	case KeyEvent.KEY_YPBPR:
    		ypbprSwichProcess();
    		break;
    	case KeyEvent.KEY_VGA:
    		vgaSwichProcess();
    		break;
    	case KeyEvent.KEY_ATV:
    		atvSwichProcess();
    		break;
    	case KeyEvent.KEY_DVBC:
    		dvbcSwichProcess();
    		break;
    	case KeyEvent.KEY_DTMB:
    		dtmbSwichProcess();
    		break;
    	case KeyEvent.KEY_VOLUP15:
    		adjustStreamVolume(KeyEvent.KEY_VOLUP15);
    		break;
    	case KeyEvent.KEY_VOLDOWN15:
    		adjustStreamVolume(KeyEvent.KEY_VOLDOWN15);
    		break;
    	case KeyEvent.KEY_PCADJUST:
    		//vgaAdjustKeyProcess();
			vgaPCAutoAdjust();
    		break;
    	case KeyEvent.KEY_SOUND:
    		soundBalanceKeyProcess();
    		break;
		case KeyEvent.KEY_WIFI:
			wifiInterfaceEntry();
			break;
		case KeyEvent.KEY_ADC:
			openBlueTeethScaner();
			break;
		case KeyEvent.KEY_BLK:
			BLKSwichProcess();
			break;
    	default:
    		break;
    	}
    }
    

	
	BroadcastReceiver facbc = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			ColorTempInfo wbInfo = null;
			int colorTempLevel = 0;
			if(FACTORY_CMDS.equals(intent.getAction())){
				Bundle tBundle = intent.getExtras();
                if(tBundle != null){
                    String facCmd = tBundle.getString("cmds");
                    Log.d(TAG,SUBTAG+" get factory cmds "+facCmd);
                    if(MmodeKeyManager.ismModeNoteShow()){
	                    if(FACCMD_SWITCHHDMI.equals(facCmd)){
							switchSourceToPlay(EnumSourceIndex.SOURCE_HDMI1);
	                    }else if(FACCMD_SAVEAWB.equals(facCmd)){
	                    	int [] awbdata = tBundle.getIntArray("awbs");
	                    	for(int j = 0; j < awbdata.length; j++){
	                    		Log.d(TAG, "awbdata get " + awbdata[j]);
	                    	}
	                    	wbInfo = CusFactoryImpl.getInstance().getColorTemp(0);
	                    	wbInfo.setrGain(awbdata[1]);
	                    	wbInfo.setgGain(awbdata[2]);
	                    	wbInfo.setbGain(awbdata[3]);
							
					     switch(awbdata[0]){
							case 0:
								colorTempLevel = 0;
								break;
							case 1:
								colorTempLevel = 1;
								break;
							case 2:
								colorTempLevel = 2;
								break;
							case 3:
								colorTempLevel = 3;
								break;
							default:
								colorTempLevel = 0;
								break;
						}
						    Log.i("====","colorTempLevel="+colorTempLevel+" rGain="+wbInfo.getrGain()+" gGain="+wbInfo.getgGain()+" bGain="+wbInfo.getbGain()
						    		+" rOffset="+wbInfo.getrOffset()+" gOffset="+wbInfo.getgOffset()+" bOffset="+wbInfo.getbOffset());
	                    	CusFactoryImpl.getInstance().setAllSourceColorTemp((int)colorTempLevel, wbInfo);
							
	                    }else if(FACCMD_RESETAWB.equals(facCmd)){
	                    	
	                    }else if(FACCMD_FINISH.equals(facCmd)){
	                    	
	                    }else if(FACCMD_SHOWSN.equals(facCmd)){
						isSnNeedUpdate = true;
					 }
                    }
                }
			}
		}
	}; 
    
    private static void switchSourceToPlay(int sourceId){
        int curId = mSourceManager.getCurSourceId(0);
        
        Intent intent = new Intent();
        int destid = 0;
        intent.putExtra("SourceName", sourceId);
        destid = sourceId;
        //KillAppsBeforeSwitchSource();
        Log.d(TAG, "selectSource start,set destid = " + destid + "curId = "+curId); 
        RectInfo rect = new RectInfo();
        if ((destid == EnumSourceIndex.SOURCE_ATV)
                || (destid >= EnumSourceIndex.SOURCE_CVBS1 && destid <= EnumSourceIndex.SOURCE_HDMI4)) {

            Log.d(TAG, "Scaler Windows to full");
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            mSourceManager.setWindowRect(rect, 0);
        }
        else if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB) {
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            mSourceManager.setWindowRect(rect, 0);
            Log.d(TAG,"tv current ID ="+curId);
            
            
            Log.d(TAG, "selectSource done,now getCurSourceId = "
                    + mSourceManager.getCurSourceId(0));
          
        }
        


        if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB){

            intent.setAction(INTENT_DTV);
        }
        else if (destid == EnumSourceIndex.SOURCE_ATV)
        {
        	intent.setAction(INTENT_ATV);
        }
        else
        {
        	intent.setAction(INTENT_PORT);
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        
    }

	
	private void doDeleteCurrentSource(int curid, int destid){
		
		synchronized (mSwitchLock) {
				Log.d(TAG, "doDelectTVSource");
				mSourceManager.deselectSource(curid, true);
			}
	}

	private void waitForSwitchLock(){
		Log.d(TAG,"waitForSwitchLock waiting");
		synchronized (mSwitchLock){
			Log.d(TAG,"waitForSwitchLock in");
		}
		Log.d(TAG,"waitForSwitchLock out");
	}

    private void KillAppsBeforeSwitchSource()
    {
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(!actRSI.processName.equals("com.um.launcher")
                && !actRSI.processName.equals("com.android.musicfx")
                && !actRSI.processName.equals("com.um.umreceiver")
                && !actRSI.processName.equals("com.um.dvb")
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
                && !actRSI.processName.equals("com.portplayer"))
    		{
    			am.forceStopPackage(actRSI.processName);
    		}
    	}
    }
	
}
