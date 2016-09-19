package com.portplayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


import android.os.RemoteException;
import android.util.Xml;
import android.widget.Toast;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSoundChannel;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.impl.AtvChannelImpl;
import com.hisilicon.android.tvapi.impl.AudioImpl;
import com.hisilicon.android.tvapi.impl.SourceManagerImpl;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.portplayer.R;
import com.portplayer.interfaces.AudioInterface;
import com.portplayer.interfaces.InterfaceValueMaps;
import com.portplayer.interfaces.PictureInterface;
import com.portplayer.interfaces.SourceManagerInterface;
import com.portplayer.util.Constant;
import com.portplayer.util.Util;
import com.portplayer.widget.SignalShow;


/**
 * The entrance of ATV
 *
 * @author wangchuanjian
 *
 */
public class PortMainActivity extends Activity {

    public static final String TAG = "PortMainActivity";
    private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
    private static final int SIGNAL_CHECK = 1008;
    private static final int NOSIGNAL_POWERDOWN = 1009;
    private static final int NOSIGNAL_CHECK_MAXCNT = 5;
    private static final int SIGNAL_NOSUPPORT_CHECK_MAXCNT = 3;
    // private Context mContext = this;
    private SurfaceView mSurfaceView;
    // view to show signal
    private SignalShow mShowTiming;
    // text of no signal
    private TextView noSignalTxt;
    private TextView signalNoSupportTxt;
    private ImageView screenSaverImg;
    // get instance of SourceManager
    private SourceManagerImpl mSourceManager = SourceManagerImpl.getInstance();

    // private boolean isChangingChanel;
    private boolean isWindOK = false;
    // source id
    private SurfaceHolder mHolder = null;

    private int showTimingFlag = 0;
    private Context mContext = this;

    private Timer checkPortPlayerSignalStat = null;
    private TimerTask timerTask = null;

    private Timer blackScreenTimer = null;
    private TimerTask blackScreenTimerTask = null;
    
    private int mCurrentSourceIdx = 0;
    private int mDestSourceIdx = 0;
    private AudioManager mAudioManager = null;
    
    private static int screenSaverMode=0;
    private static int mNoSignalPD = 1;
    private int mNoSignalCnt = 0;
    private int mSignalNoSupport = 0;
    private static final int MAX_PROG_NUM = 1000;
   
    private static final int PIC_DIALOG_DISMISS_BYTIME = 1;
    private static final int SOUND_DIALOG_DISMISS_BYTIME = 2;
    private static final int ZOOM_DIALOG_DISMISS_BYTIME = 3;
    private static final int TRACK_DIALOG_DISMISS_BYTIME = 4;
    private  AlertDialog picAlertdialog;
    private  AlertDialog soundAlertdialog;
    private  AlertDialog zoomAlertdialog;
    private  AlertDialog trackAlertdialog;
    private  LinearLayout picdialog;
    private  LinearLayout sounddialog;
    private  LinearLayout zoomdialog;
    private  LinearLayout trackdialog;

    private int picModeIndex;
    private int soundModeIndex;
    private int zoomModeIndex;
    private int trackModeIndex;
    
    boolean isPicDialogDismiss =true;
    boolean isSoundDialogDismiss =true;
    boolean isZoomDialogDismiss =true;
    boolean isTrackDialogDismiss =true;
    private TextView  pic_menu_btn;
    private TextView  sound_menu_btn;
    private TextView  zoom_menu_btn;
    private TextView  track_menu_btn;
	private static boolean isNosignalPowerdownMonitorStarted = false;
	private static String mMode = "normal";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * The initialization of all views
     */
    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        initSurfaceView();
        noSignalTxt = (TextView) findViewById(R.id.tv_nosignal);
        signalNoSupportTxt = (TextView) findViewById(R.id.tv_signal_nosupport);
        screenSaverImg = (ImageView) findViewById(R.id.tv_screensaver);
        mShowTiming = (SignalShow) findViewById(R.id.SignalShow);
    }

    /**
     * The initialization of SurfaceView
     */
    private void initSurfaceView() {
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_HISI_TRANSPARENT);
        mHolder.addCallback(new Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // holder.getSurface().setSize(1920,1080);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                int width, int height) {
            // TODO Auto-generated method stub
        }
        });   
    }
    
    @Override
    protected void onStart(){
       Log.d(TAG,"-------PortPlayer onStart()--------");
       super.onStart();
    }
    
    /**
     * register all listeners
     */
    private void registerListener() {
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
              
    }

    private void unregisterListener() {
    	UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGIN, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGOUT, onPlayerListener);

        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
    }
    
    /**
     * TV play listener
     */
    OnPlayerListener onPlayerListener = new OnPlayerListener() {

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
            Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
        }

        @Override
        public void onSignalStatus(int arg0) {
        	handleSignalStat(arg0);
        }

        @Override
        public void onTimmingChanged(TimingInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
            }
            refreshTimingShow(true);
        }

        @Override
        public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugin  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
        }
		
        @Override
        public void onSelectSource(int  arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSource  arg0: " + arg0);
            }
        }
	@Override
        public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSourceComplete  arg0: " + arg0);
            }
            Log.d(TAG, "===yiyonghui=== ");
        }
		
		@Override
        public void onPlayLock(ArrayList<Integer> list) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onPlayLock  arg0: " + list);
            }
        }
    };
    
	private void registerEventMonitors(){
		Log.i(TAG,"registerEventMonitors");
		
		IntentFilter filter = new IntentFilter();   
		filter.addAction(RESET_MONITORS);  
		registerReceiver(systemEventMonitorsReceiver, filter);
	}
	
	private void unRegisterEventMonitors() {
		unregisterReceiver(systemEventMonitorsReceiver);
	}
	
    private void getscreenSaverMode(){
        try {
	        Context otherContext = createPackageContext(
	                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
	        SharedPreferences sp = otherContext.getSharedPreferences(
	                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	        
	        screenSaverMode = sp.getInt("screensaverState", 0);
	        Log.i(TAG,"screensaverState "+screenSaverMode);
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
    }
    
    private void getNoSignalPDFlag(){
		try {
			Context powerSaveAppContext;
			powerSaveAppContext = createPackageContext("cn.com.unionman.umtvsetting.powersave", Context.CONTEXT_IGNORE_SECURITY);
	       	SharedPreferences sharedata = powerSaveAppContext.getSharedPreferences("PoweritemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	       	mNoSignalPD = sharedata.getInt("autoShutdonw", 1);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
    private void getDestSourceIdx(){
        Intent intent = this.getIntent();
        if(intent == null){
            Log.d(TAG,"onResume getIntent is null");
            mDestSourceIdx = 0;
        }else{
            Bundle bu = intent.getExtras();
            if(bu == null){
                Log.d(TAG,"onResume getExtras is null");
                mDestSourceIdx = 0;
            }else{
                mDestSourceIdx = getIntent().getExtras().getInt("SourceName");
            }
        }
    }

 /*   private String getDestSourceStr(){
        Intent intent = this.getIntent();
        String SourceStr="";
        if(intent == null){
            Log.d(TAG,"onResume getIntent is null");
            //mDestSourceIdx = 0;
        }else{
            Bundle bu = intent.getExtras();
            if(bu == null){
                Log.d(TAG,"onResume getExtras is null");
                //mDestSourceIdx = 0;
            }else{
            	SourceStr = getIntent().getExtras().getString("SourceNameStr");
            }
        }
        
        return SourceStr;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        getscreenSaverMode();
        getNoSignalPDFlag();
        showTimingFlag = 1;
        registerListener();
        registerEventMonitors();
        
        String bootFlag = SystemProperties.get("persist.sys.bootup");
        mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
        getDestSourceIdx();
        if (!bootFlag.equals("1")){
            Log.d(TAG,"dest Source = "+ mDestSourceIdx + " current Source = "+ mCurrentSourceIdx);
            if(mCurrentSourceIdx != mDestSourceIdx){
                Log.d(TAG,"=============select source===================");
                //KillAppsBeforeSelectSource();
				KillBlackListApps();
				KillNonsystemApps();
                SourceManagerInterface.deselectSource(mCurrentSourceIdx, true);
                SourceManagerInterface.selectSource(mDestSourceIdx, 0);
            }
            RectInfo rect = new RectInfo();
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            SourceManagerInterface.setWindowRect(rect, 0);
        }
        
        mNoSignalCnt = 0;
        mSignalNoSupport = 0;
        if (checkPortPlayerSignalStat != null){
            checkPortPlayerSignalStat.cancel();
        }
        checkPortPlayerSignalStat = new Timer();
        //checkPortPlayerSignalStat.schedule(checkSignalStat(),500); /*handle in onWindowFocusChanged(boolean)*/
        
        refreshTimingShow(true);
        
        SystemProperties.set("persist.sys.fullScreen_Source", ""+mDestSourceIdx);       
    }

    @Override
    protected void onPause() {
    	Log.d(TAG,"onPause");
       	unregisterListener();
       	unRegisterEventMonitors();
       	checkSignalHandler.removeMessages(SIGNAL_CHECK);
       	noSignalHide();
        hideTimingShow();
        signalNoSupportHide();
        removeDialogs();
    	/*if (mMode.equals("normal")){
    		stopNoSignalPowerdownHandleMonitor();
    	}*/
    	
        if (checkPortPlayerSignalStat != null){
            checkPortPlayerSignalStat.cancel();
        }

        super.onPause();
    }

    private void handleSignalStat(int sigstat){
        Log.d(TAG,"===========sigstat======= "+ sigstat);
		
        if (!isWindOK){
        	Log.d(TAG,"===========handleSignalStat======= isWindOK:"+isWindOK);
        	return;
        }
        
        if (sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
        	Log.d(TAG, "-------->play--Strong-Signal---->");
        	
        	noSignalHide();
        	signalNoSupportHide();
        	stopNoSignalPowerdownHandleMonitor();
            if (showTimingFlag == 1){
                refreshTimingShow(true);
                showTimingFlag = 0;
            }
            mNoSignalCnt = 0;
        }else if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
        	mSignalNoSupport = 0;
            if (mNoSignalCnt >= NOSIGNAL_CHECK_MAXCNT){
            	Log.d(TAG, "-------->play--No-Signal-Layout---->");
            	signalNoSupportHide();
            	noSignalShow();
            	startNoSignalPowerdownHandleMonitor();
            	showTimingFlag = 1;            	
                mNoSignalCnt = 0;
            }else{
            	mNoSignalCnt++;
            	checkPortPlayerSignalStat.schedule(checkSignalStat(), 500);
            }	    
        }else if (sigstat == EnumSignalStat.SIGSTAT_UNSUPPORT){
        	mNoSignalCnt = 0;
        	if (mSignalNoSupport >= SIGNAL_NOSUPPORT_CHECK_MAXCNT){
        		noSignalHide();
        		signalNoSupportShow();
        	}else{
        		mSignalNoSupport++;
        		checkPortPlayerSignalStat.schedule(checkSignalStat(), 500);
        	}
        }
    }
    
    Handler checkSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SIGNAL_CHECK:
                int sigstat = SourceManagerInterface.getSignalStatus();
                handleSignalStat(sigstat);
                break;
            case NOSIGNAL_POWERDOWN:
			    Intent intent = new Intent();
			    intent.setAction("android.intent.action.NOSIGNAL_POWERDOWN_BROADCAST");
			    mContext.sendBroadcast(intent);
            	break;
            default:
                break;
            }
        }
    };

    public TimerTask checkSignalStat(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
                checkSignalHandler.sendEmptyMessage(SIGNAL_CHECK);
            }
        };
        return timerTask;
    }
    
    private void noSignalShow(){
    	if (screenSaverMode == 0){
    		startSignalTxt();
    	}else{
    		showScreenSaver();
    	}
    }
    
    private void noSignalHide(){
        if (screenSaverMode==0) {
        	stopSignalTxt();
        } else {
        	closeScreanSaver();
        }
    }
    
    private void stopSignalTxt(){
        if (noSignalTxt != null) {
        	if (noSignalTxt.getVisibility() == View.VISIBLE){
        		Log.i(TAG, "stopSignalTxt View.GONE");
                noSignalTxt.setVisibility(View.GONE);
                stopScreenTxtMoving();
        	}
        }
    }
    
    private void startSignalTxt(){
        if (noSignalTxt != null) {
        	if (noSignalTxt.getVisibility() != View.VISIBLE){
                noSignalTxt.setVisibility(View.VISIBLE);
                noSignalTxt.requestLayout();
                startScreenTxtMoving();
        	}
        }
    }
    
    private void signalNoSupportShow(){
    	 if (signalNoSupportTxt != null) {
         	if (signalNoSupportTxt.getVisibility() != View.VISIBLE){
         		signalNoSupportTxt.setVisibility(View.VISIBLE);
         	}
         }
    }
    
    private void signalNoSupportHide(){
	   	if (signalNoSupportTxt != null) {
	      	if (signalNoSupportTxt.getVisibility() == View.VISIBLE){
	      		signalNoSupportTxt.setVisibility(View.GONE);
	      	}
	    }
    }
    
    private void startNoSignalPowerdownMonitor(){
		Log.i(TAG, "startNoSignalPowerdownMonitor " + isNosignalPowerdownMonitorStarted);
		
		if (mNoSignalPD == 1 ){
			if (!isNosignalPowerdownMonitorStarted) {
				checkSignalHandler.sendEmptyMessageDelayed(NOSIGNAL_POWERDOWN, 300000);
				isNosignalPowerdownMonitorStarted = true;
			}
		}
	}
	
	private void stopNoSignalPowerdownMonitor() {
		Log.i(TAG, "stopNoSignalPowerdownMonitor " + isNosignalPowerdownMonitorStarted);

		if (mNoSignalPD == 1){
			if (isNosignalPowerdownMonitorStarted) {
				checkSignalHandler.removeMessages(NOSIGNAL_POWERDOWN);
				isNosignalPowerdownMonitorStarted = false;
				
			}
		}
	}
	
    public TimerTask blackScreenHandle(){
    	blackScreenTimerTask = new TimerTask(){
            @Override
            public void run(){
            	boolean backLightOn = UmtvManager.getInstance().getPicture().isBacklightEnable();
            	Log.d(TAG, "blackScreenHandle,backLightOn:"+backLightOn);
            	//if (backLightOn){
            		UmtvManager.getInstance().getPicture().enableBacklight(false);
            	//}
            }
        };
        return blackScreenTimerTask;
    }
    
	private void startNoSignalBlackScreenMonitor(){
		if ((mNoSignalPD == 1) && (mDestSourceIdx == EnumSourceIndex.SOURCE_VGA)){
			Log.d(TAG, "startNoSignalBlackScreenMonitor");
			if (blackScreenTimer == null){
				blackScreenTimer = new Timer();
				blackScreenTimer.schedule(blackScreenHandle(), 30 * 1000);
			}
		}
	}
	
	private void stopNoSignalBlackScreenMonitor(){
		if (mNoSignalPD == 1){
			Log.d(TAG, "stopNoSignalBlackScreenMonitor");
			if (blackScreenTimer != null){
				blackScreenTimer.cancel();
				blackScreenTimer = null;
			}
			
			boolean backLightOn = UmtvManager.getInstance().getPicture().isBacklightEnable();
			Log.d(TAG, "stopNoSignalBlackScreenMonitor,backLightOn:"+backLightOn);
			if (backLightOn == false){
				UmtvManager.getInstance().getPicture().enableBacklight(true);
			}
		}
	}
	
	private void startNoSignalPowerdownHandleMonitor(){
		Log.d(TAG, "startNoSignalPowerdownHandleMonitor");
		startNoSignalBlackScreenMonitor();
		startNoSignalPowerdownMonitor();
	}
	
	private void stopNoSignalPowerdownHandleMonitor(){
		Log.d(TAG, "stopNoSignalPowerdownHandleMonitor");
		stopNoSignalBlackScreenMonitor();
		stopNoSignalPowerdownMonitor();
	}
	
	private void noSignalPowerdownMonitorKeyHandle(){
		
	
			stopNoSignalPowerdownHandleMonitor();
			int sigstat = SourceManagerInterface.getSignalStatus();
			if (((noSignalTxt != null) && (noSignalTxt.getVisibility() == View.VISIBLE))
					||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
					|| (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL)){
				startNoSignalPowerdownHandleMonitor();
			}
		
	}
	
	private void windowFocusChangeHandle(boolean focus){
		if (focus){
			if (checkPortPlayerSignalStat != null){
				checkPortPlayerSignalStat.schedule(checkSignalStat(),500);
			}
		}else{
		
				
	    	if (mMode.equals("normal")){
	    		stopNoSignalPowerdownHandleMonitor();
	    	}
			
			
			if (screenSaverMode == 1){
				checkSignalHandler.removeMessages(SIGNAL_CHECK);
				closeScreanSaver();
			}
		}
	}
	
	private BroadcastReceiver systemEventMonitorsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d(TAG, "-------->systemEventMonitorsReceiver---->");
			if (intent.getAction().equals(RESET_MONITORS)) {
				String mode = intent.getStringExtra("mode");
				Log.d(TAG, "-------->systemEventMonitorsReceiver---->mode:"+mode);
				if ((mode != null) && (mode.equals("powerdown"))){
					mMode = "powerdown";
				}else{
					resetMonitors();
				}	
			} 
		}
	};
	
	private void resetMonitors() {
		Log.d(TAG, "-------->resetMonitors---->");
		int sigstat = SourceManagerInterface.getSignalStatus();
		if (((noSignalTxt != null) && (noSignalTxt.getVisibility() == View.VISIBLE))
				||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
				|| (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL)){
			stopNoSignalPowerdownHandleMonitor();
			startNoSignalPowerdownHandleMonitor();
		}
	}
	
    /**
     * refresh view of SignalShow by force
     *
     * @param force
     */
    public void refreshTimingShow(boolean force) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "refreshTimingShow()");
        }
        
        if (mShowTiming != null){
        	//mShowTiming.refreshPanelInfo(mSourceManager.getSelectSourceId(),getDestSourceStr(), force);
        	mShowTiming.refreshPanelInfo(mSourceManager.getSelectSourceId(), force);
        }
    }
    
    private void hideTimingShow(){
        if (mShowTiming != null && mShowTiming.getVisibility() == View.VISIBLE){
            mShowTiming.focusHide();
        }
    }
    
    @Override
   protected void onStop(){
       Log.d(TAG,"onStop");
       super.onStop();
    }
    
   @Override
   protected void onDestroy() {
	   Log.d(TAG,"onDestroy()");
       super.onDestroy();
   }
   
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onKeyUp keycode = " + keyCode + "; event = " + event);
        }

        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (isWindOK) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onKeyDown keycode = " + keyCode + "; event = "
                        + event);
            }
            
            noSignalPowerdownMonitorKeyHandle();
            screanSaverKeyHandle();
            
            switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_TVSETUP:
                Intent intent = new Intent(PortMainActivity.this,
                        SettingActivity.class);
                startActivity(intent);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_INFO:
                refreshTimingShow(true);
                break;
            case KeyEvent.KEY_SOUND:
            	Log.d(TAG,"leon... KeyEvent.KEY_SOUND");
            	break;
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                adjustStreamVolume(keyCode);
                break;  
            case KeyEvent.KEY_TRACK:
            	TrackModeQuickKeyHandle();
            	break;
            case KeyEvent.KEYCODE_PICTUREMODE:
            	pictureModeQuickKeyHandle();
            	break;
            case KeyEvent.KEYCODE_SOUNDMODE:
            	soundModeQuickKeyHandle();
            	break;
            case KeyEvent.KEYCODE_ZOOM:
            	String w = SystemProperties.get("persist.sys.reslutionWidth");
            	String h = SystemProperties.get("persist.sys.reslutionHight");
            	if((SourceManagerInterface.getSelectSourceId() != EnumSourceIndex.SOURCE_VGA)&&
            	   (!((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160"))))){
            		zoomQuickKeyHandle();
            	}
            	
            	break;               	
            default:
                break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void pictureModeQuickKeyHandle(){
    	Log.i(TAG, "zemin KeyEvent.KEY_PICTUREMODE press");    
    	Log.i(TAG,"isPicDialogDismiss="+isPicDialogDismiss);
    	
    	finishHandle.removeMessages(SOUND_DIALOG_DISMISS_BYTIME);

    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	
    	if(!isSoundDialogDismiss){
    		//soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isZoomDialogDismiss){
    		//zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
    		isZoomDialogDismiss=true;
    	}
    	
    	if(!isPicDialogDismiss){ //not dismiss 
			 delay(PIC_DIALOG_DISMISS_BYTIME); 
			 picModeIndex++;
			 if(picModeIndex>=InterfaceValueMaps.picture_mode.length){
				 picModeIndex=0;
			 }
			 Log.i(TAG,"picModeIndex="+picModeIndex);
			  PictureInterface.setPictureMode(InterfaceValueMaps.picture_mode[picModeIndex][0]);
			  pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);        		
    	}else{  //dismiss
       	 int mode = PictureInterface.getPictureMode(); 
   	     picModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.picture_mode);
   	     Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
			 Log.i(TAG,"mode="+mode);
			 Log.i(TAG,"picModeIndex="+picModeIndex);
            picdialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater factory = LayoutInflater.from(mContext);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	       pic_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
 	       pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);
/* 	       picAlertdialog = mPicBuilder.create();
	        Window window = picAlertdialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.y=350;
	        window.setAttributes(lp);
	        picAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        picAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        picAlertdialog.show();	
	        picAlertdialog.show();	
	        picAlertdialog.getWindow().setContentView(myView);	   */
	        isPicDialogDismiss=false;
            picdialog.addView(myView);
		   delay(PIC_DIALOG_DISMISS_BYTIME);         		
    	}           	
    }
    
    private void TrackModeQuickKeyHandle(){
    	if(!isSoundDialogDismiss){
    		//soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
    		isSoundDialogDismiss=true;
    	}
    	if(!isZoomDialogDismiss){
    		//zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
    		isZoomDialogDismiss=true;
    	}
    	if(!isPicDialogDismiss){
    		//picAlertdialog.dismiss();
            picdialog.removeAllViews();
    		isPicDialogDismiss=true;
    	}
    	
    	if(!isTrackDialogDismiss){ //not dismiss 
			 delay(TRACK_DIALOG_DISMISS_BYTIME); 
			 trackModeIndex++;
			 if(trackModeIndex>=4){
				trackModeIndex=0;
			 }
			 Log.i(TAG,"trackModeIndex="+trackModeIndex);
			 UmtvManager.getInstance().getAudio().setTrackMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
			  //PictureInterface.setPictureMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
			 track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);        		
    	}else{  //dismiss
       	 int mode = UmtvManager.getInstance().getAudio().getTrackMode();
       	trackModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.track_mode);
   	    Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
			
 	      LayoutInflater factory = LayoutInflater.from(mContext);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	     track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
            trackdialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	    track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);
/* 	   trackAlertdialog = mPicBuilder.create();
 	       
	        Window window = trackAlertdialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.y=350;
	        window.setAttributes(lp);
	        trackAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        trackAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        trackAlertdialog.show();	
	        trackAlertdialog.getWindow().setContentView(myView);*/
	        isTrackDialogDismiss=false;
            trackdialog.addView(myView);
		   delay(TRACK_DIALOG_DISMISS_BYTIME);         		
    	}     			         	
    }
    
    private void soundModeQuickKeyHandle(){

       	Log.i(TAG,"KeyEvent.KEY_SOUNDMODE press");
    	Log.i(TAG,"isSoundDialogDismiss="+isSoundDialogDismiss);
    	
    	finishHandle.removeMessages(PIC_DIALOG_DISMISS_BYTIME);
    	
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	if(!isPicDialogDismiss){
    		//picAlertdialog.dismiss();
            picdialog.removeAllViews();
    		isPicDialogDismiss=true;
    	}
    	if(!isZoomDialogDismiss){
    		//zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
    		isZoomDialogDismiss=true;
    	}

    	if(!isSoundDialogDismiss){ //not dismiss
			 delay(SOUND_DIALOG_DISMISS_BYTIME); 
			 soundModeIndex++;
			 if(soundModeIndex>=InterfaceValueMaps.sound_mode.length){
				 soundModeIndex=0;
			 }
			 Log.i(TAG,"soundModeIndex="+soundModeIndex);
			   AudioInterface.getAudioManager().setSoundMode(InterfaceValueMaps.sound_mode[soundModeIndex][0]);
			   sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);        		
    	}else{   // dismiss
      	  int sound_mode = AudioInterface.getAudioManager().getSoundMode();
      	  soundModeIndex= Util.getIndexFromArray(sound_mode,InterfaceValueMaps.sound_mode); 
      	  //Builder	 mSoundBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
				 Log.i(TAG,"sound_mode"+sound_mode);
				 Log.i(TAG,"soundModeIndex="+soundModeIndex);
            sounddialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater sound_factory = LayoutInflater.from(mContext);
 	      View sound_myView = sound_factory.inflate(R.layout.selector_view_dialog,null);
 	         sound_menu_btn =(TextView) sound_myView.findViewById(R.id.menu_btn);
 	      sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);
/* 	     soundAlertdialog = mSoundBuilder.create();

	        Window sound_window = soundAlertdialog.getWindow();
	        WindowManager.LayoutParams sound_lp = sound_window.getAttributes();
	        sound_lp.y=350;
	        sound_window.setAttributes(sound_lp);
	        soundAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        soundAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        soundAlertdialog.show();
	        soundAlertdialog.getWindow().setContentView(sound_myView);	*/
	        isSoundDialogDismiss =false;
            sounddialog.addView(sound_myView);
		    delay(SOUND_DIALOG_DISMISS_BYTIME);  
    	}        	 		             	  
    }
    
    private void zoomQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEY_ZOOM press");
    	Log.i(TAG,"isZoomDialogDismiss="+isZoomDialogDismiss);
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	if(!isPicDialogDismiss){
    		//picAlertdialog.dismiss();
            picdialog.removeAllViews();
    		isPicDialogDismiss=true;
    	}
    	if(!isSoundDialogDismiss){
    		//soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isZoomDialogDismiss){ //not dismiss
			 delay(ZOOM_DIALOG_DISMISS_BYTIME); 
			 zoomModeIndex++;
			 if(zoomModeIndex>=InterfaceValueMaps.picture_aspect.length){
				 zoomModeIndex=0;
			 }
			 Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
			PictureInterface.setAspect(InterfaceValueMaps.picture_aspect[zoomModeIndex][0],false);
			  zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);        		
    	}else{   // dismiss
    	  int aspect = PictureInterface.getAspect();          //display mode  4 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣浠嬫晸閿燂拷  
    	  zoomModeIndex= Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	      //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撴鎭掑劵閹风兘鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥懄椤掑﹥瀚圭化顖炴晸閿熶粙鏁撻弬銈嗗闁跨喕鍓兼潏鐐2闁鏁撶粵瀣拷闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣鏍э拷娑撴椽鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗閸婇棿璐烝SPECT_16_9
   	      if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2)||(aspect==EnumPictureAspect.ASPECT_AUTO)){
   	 	   PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
   	 	   aspect = PictureInterface.getAspect(); 
   	 	   zoomModeIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	 	   Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2  or ASPECT_AUTO ;set value = ASPECT_16_9; aspect="+aspect);
   	       }                 	
       	    Builder  mZoomBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
					 Log.i(TAG,"aspect="+aspect);
					 Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
            zoomdialog = (LinearLayout) this.findViewById(R.id.mydialog);
       	      LayoutInflater zoom_factory = LayoutInflater.from(mContext);
       	      View zoom_myView = zoom_factory.inflate(R.layout.selector_view_dialog,null);
       	       zoom_menu_btn =(TextView) zoom_myView.findViewById(R.id.menu_btn);
       	      zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
/*       	       zoomAlertdialog = mZoomBuilder.create();
		        Window zoom_window = zoomAlertdialog.getWindow();
		        WindowManager.LayoutParams zoom_lp = zoom_window.getAttributes();
		        zoom_lp.y=350;
		        zoom_window.setAttributes(zoom_lp);
		        zoomAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		        zoomAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		        zoomAlertdialog.show();
		        zoomAlertdialog.getWindow().setContentView(zoom_myView);	*/
		        isZoomDialogDismiss=false;
                zoomdialog.addView(zoom_myView);
            delay(ZOOM_DIALOG_DISMISS_BYTIME);
    	}
    }
    
    private void adjustStreamVolume(int keyCode)
    {
        if (null == mAudioManager)
        {
            mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        }
        boolean mute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        if(mute == true && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
        keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
        ? AudioManager.ADJUST_RAISE:AudioManager.ADJUST_LOWER,
                AudioManager.FX_FOCUS_NAVIGATION_UP);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	Log.d(TAG,"onWindowFocusChanged,hasFocus:"+hasFocus);
        isWindOK = hasFocus;
        super.onWindowFocusChanged(hasFocus);
        windowFocusChangeHandle(hasFocus);
    }
    
	private int imgIDs[] = {
			R.drawable.ss00,
			R.drawable.ss01,
			R.drawable.ss02,
			R.drawable.ss03,
			R.drawable.ss04,
			R.drawable.ss05,
			R.drawable.ss06,
			R.drawable.ss07,
			R.drawable.ss08,
			R.drawable.ss09,
	};
	
	private int imgIdx = 0;
	private Timer timer = new Timer(true);
	private Handler mScreenSaverHandler = new Handler(){     
        public void handleMessage(Message msg) {     
            switch (msg.what) {         
            case 1:        
            	  Log.i(TAG,"handleMessage");
            	  screenSaverImg.setImageResource(imgIDs[imgIdx++]);
		    	  if (imgIdx>=imgIDs.length) {
		    		  imgIdx=0;
		    	  }    
                break;         
            }         
            super.handleMessage(msg);     
        }     
             
    };
    
	private TimerTask screenSavertask ;
	private Timer screenSaverTimer ;
	private void showScreenSaver() {
		Log.i(TAG,"showScreenSaver");
		if (screenSaverImg != null){
			if (screenSaverImg.getVisibility() != View.VISIBLE) {
				
				screenSaverImg.setVisibility(View.VISIBLE);
		    	screenSaverTimer = new Timer(true);
		    	screenSavertask= new TimerTask(){  
			  	      public void run() { 
			              Message message = new Message();         
			              message.what = 1;         
			              mScreenSaverHandler.sendMessage(message); 
			  	   }  
			  	};
		    	screenSaverTimer.schedule(screenSavertask, 1000, 6000); 
			}
		}
	}
	
	private void closeScreanSaver() {
		Log.i(TAG,"closeScreanSaver");
		
		if (screenSaverImg != null){
			if (screenSaverImg.getVisibility() == View.VISIBLE){
				screenSaverImg.setVisibility(View.GONE);
				screenSaverTimer.cancel();
				screenSaverTimer = null;
			}
		}
	}
    
	private void screanSaverKeyHandle(){
		if (screenSaverMode == 1){
			checkSignalHandler.removeMessages(SIGNAL_CHECK);
			closeScreanSaver();			
			int sigstat = SourceManagerInterface.getSignalStatus();
			if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
				checkSignalHandler.sendEmptyMessageDelayed(SIGNAL_CHECK, 2000);
			}
		}
	}
	
    /* 
    * 闁跨喐鏋婚幏鐑芥晸閻偅甯剁涵閿嬪闁跨喐鏋婚幏鐑芥晸閼哄倻顣幏铚傜秴闁跨喐鏋婚幏绌�闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔稿疆鐠囇勫闁跨喍鑼庨幉瀣闁跨喓顏敐蹇斿 
    * XY娑撴椽鏁撻弬銈嗗闁跨喕濞囨导娆愬闁跨噦鎷�
    */ 
    public static void setLayout(View view,int x,int y) 
    { 
	    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	    layoutParams.leftMargin = x;
	    layoutParams.topMargin = y;
	    view.setLayoutParams(layoutParams); 
	    view.postInvalidate();
    }
    
    private int txtMovingDown = 1*25;
    private int txtMovingLeft = 1*25;
    private int txtMovingStep = 100;
	private Timer dymScreenTxtTimer;
	
	private Handler mdymScreenTxtHandler = new Handler(){     
	        public void handleMessage(Message msg) {     
	            switch (msg.what) {         
	            case 101:        
	            	View view = noSignalTxt;
	            	if (mSurfaceView.getHeight()==0||mSurfaceView.getWidth()==0)
	            		break;
	            	
	                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	                int currLeft = layoutParams.leftMargin;  
	                int currTop = layoutParams.topMargin;  
	            	//Log.i("haha","currLeft "+currLeft+" currTop "+currTop);
	            	if (txtMovingDown==1) {
	            		currTop += txtMovingStep;
	            		if (currTop>mSurfaceView.getHeight()-view.getHeight()) {
	            			txtMovingDown = 0;
	            			currTop = mSurfaceView.getHeight()-view.getHeight();
	            		}
	            	} else {
	            		currTop -= txtMovingStep;
	            		if (currTop<0) {
	            			txtMovingDown = 1;
	            			currTop = 0;
	            		}
	            	}
	            	
	            	if (txtMovingLeft==1) {
	            		currLeft += txtMovingStep;
	            		if (currLeft>mSurfaceView.getWidth()-view.getWidth()) {
	            			txtMovingLeft = 0;
	            			currLeft = mSurfaceView.getWidth()-view.getWidth();
	            		}
	            	} else {
	            		currLeft -= txtMovingStep;
	            		if (currLeft<0) {
	            			txtMovingLeft = 1;
	            			currLeft = 0;
	            		}
	            	}
	            	setLayout(view, currLeft,currTop);
	                break;         
	            }         
	            super.handleMessage(msg);     
	        }     
	             
	    }; 
	    
	private TimerTask dymScreenTxtTask;
	
	private void startScreenTxtMoving() {
		if (noSignalTxt !=null && noSignalTxt.getVisibility() == View.VISIBLE){
			txtMovingDown = 1*25;
		    txtMovingLeft = 1*25;
			dymScreenTxtTimer = new Timer(true);
			dymScreenTxtTask= new TimerTask(){  
		  	      public void run() { 
		              Message message = new Message();         
		              message.what = 101;         
		              mdymScreenTxtHandler.sendMessage(message); 
		  	   }  
		  	};
		  	dymScreenTxtTimer.schedule(dymScreenTxtTask, 0, 3000);
		}
	}
	
	private void stopScreenTxtMoving() {
		if (dymScreenTxtTimer!=null) {
			dymScreenTxtTimer.cancel();
			dymScreenTxtTimer = null;
		}
	}
	
    /**
     * handler of finish dialog
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	Log.d(TAG,"handleMessage msg.what:"+msg.what);
        	switch (msg.what) {
			case PIC_DIALOG_DISMISS_BYTIME:				
				//picAlertdialog.dismiss();
                picdialog.removeAllViews();
				isPicDialogDismiss=true;
				break;
			case SOUND_DIALOG_DISMISS_BYTIME:
				// soundAlertdialog.dismiss();
                 sounddialog.removeAllViews();
	             isSoundDialogDismiss=true;
				break;
			case ZOOM_DIALOG_DISMISS_BYTIME:
				//zoomAlertdialog.dismiss();
                zoomdialog.removeAllViews();
				isZoomDialogDismiss=true;
				break;
			case TRACK_DIALOG_DISMISS_BYTIME:
				//trackAlertdialog.dismiss();
                trackdialog.removeAllViews();
				isTrackDialogDismiss=true;
				break;
			default:
				break;
			}

        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay(int msg) {
        finishHandle.removeMessages(msg);
        Message message = new Message();
        message.what = msg;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
    
    private void removeDialogs(){
    	finishHandle.removeMessages(PIC_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(SOUND_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(ZOOM_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(TRACK_DIALOG_DISMISS_BYTIME);
    	
    	if(!isSoundDialogDismiss){
    		//soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	
    	if(!isPicDialogDismiss){
    		//picAlertdialog.dismiss();
            picdialog.removeAllViews();
    		isPicDialogDismiss=true;
    	}
    	
    	if(!isZoomDialogDismiss){
    		//zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
    		isZoomDialogDismiss=true;
    	}
    }
    
    private void KillAppsBeforeSelectSource()
    {
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(!actRSI.processName.equals("com.um.launcher")
				&& !actRSI.processName.equals("com.portplayer")
				&& !actRSI.processName.equals("com.android.musicfx")
				&& !actRSI.processName.equals("com.source")
				&& !actRSI.processName.equals("com.um.umreceiver")
				&& !actRSI.processName.equals("com.um.dvb")
				&& !actRSI.processName.equals("com.um.dvbsearch")
				&& !actRSI.processName.equals("com.unionman.dvbprovider")
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
		        pullParser.setInput(xml, "UTF-8"); //涓篜ull瑙ｉ噴鍣ㄨ缃瑙ｆ瀽鐨刋ML鏁版嵁        
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
}
