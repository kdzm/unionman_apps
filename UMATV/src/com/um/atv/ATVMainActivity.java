package com.um.atv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager.LayoutParams;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSoundChannel;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.impl.CusAtvChannelImpl;
import com.hisilicon.android.tvapi.impl.AudioImpl;
import com.hisilicon.android.tvapi.impl.SourceManagerImpl;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.OnFactoryListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.interfaces.PCSettingInterface;
import com.um.atv.interfaces.PictureInterface;
import com.um.atv.interfaces.SourceManagerInterface;
import com.um.atv.util.Constant;
import com.um.atv.util.PropertyUtils;
import com.um.atv.util.Util;
import com.um.atv.widget.ChannelEditView;
import com.um.atv.widget.NoSignalLayout;
import com.um.atv.widget.SignalShow;
import  com.um.atv.interfaces.AudioInterface;
import com.um.atv.util.SystemUtils;

/**
 * The entrance of ATV
 *
 * @author wangchuanjian
 *
 */
public class ATVMainActivity extends Activity {

    public static final String TAG = "ATVMainActivity";
    private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
    private static final int SHOW_INFO = 1008;
    private static final int NOSIGNAL_POWERDOWN = 1009;
    private static final int NOSIGNAL_CHECK_MAXCNT = 3;
    // private Context mContext = this;
    private SurfaceView mSurfaceView;
    // layout of no signal
    private NoSignalLayout mNoSignalLayout;

    private ChannelEditView mChannelEditView;
    // view to show signal
    private SignalShow mShowSignal;
    // text of no signal
    private TextView noSignalTxt;
    private ImageView screenSaverImg;
    // get instance of SourceManager
    private SourceManagerImpl mSourceManager = SourceManagerImpl.getInstance();
    // get instance of ChannelManager
    private CusAtvChannelImpl mChannelManager = CusAtvChannelImpl.getInstance();

    // number of current channel
    private int mCurrentChannel;
	// number of last channel
    private int mLastChannelNumber;
    // private boolean isChangingChanel;
    private boolean isWindOK = false;
    // source id
    private SurfaceHolder mHolder = null;

    private int showTimingFlag = 0;
    private Context mContext = this;

    private Timer checkATVSignalStat = null;
    private TimerTask timerTask = null;

    private int mCurrentSourceIdx = 0;
    private int mDestSourceIdx = 0;
    private int mNoSignalCnt = 0;
    private static int mNoSignalPD = 1;
    private boolean mPowerDownDialogOpenFlag = false;
    private static boolean isNosignalPowerdownMonitorStarted = false;
    private AudioManager mAudioManager = null;
    //VGAAdjustingDialog mSystemUpdateDialog = null;
    private static final int MAX_PROG_NUM = 1000;
    // ADJUSTING
    public final static int ADJUSTING = 0;
    // adjust failed
    public final static int ADJUST_FAILED = 1;
    // adjust success
    public final static int ADJUST_SUCCESS = 2;
    
    private static int screenSaverMode=0;
    
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
    private int trackModeIndex;
    private int soundModeIndex;
    private int zoomModeIndex;
    
    boolean isPicDialogDismiss =true;
    boolean isSoundDialogDismiss =true;
    boolean isZoomDialogDismiss =true;
    boolean isTrackDialogDismiss =true;
    
    private TextView  pic_menu_btn;
    private TextView  sound_menu_btn;
    private TextView  zoom_menu_btn;
    private TextView  track_menu_btn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentChannel = 0;
        setContentView(R.layout.activity_main);

        // isChangingChanel = false;
        initView();
    }

    /**
     * The initialization of all views
     */
    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        initSurfaceView();

        mNoSignalLayout = (NoSignalLayout) findViewById(R.id.nosignallayout);
        noSignalTxt = (TextView) findViewById(R.id.tv_nosignal);
        screenSaverImg = (ImageView) findViewById(R.id.tv_screensaver);
        mChannelEditView = (ChannelEditView) findViewById(R.id.channeleditview);
        mShowSignal = (SignalShow) findViewById(R.id.SignalShow);
        mLastChannelNumber = mShowSignal.getCurChannelNumber();
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
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_COLORTEMP_ADJ_STATUS,mFactoryListener);
        
        IntentFilter filter = new IntentFilter(Constant.ACTION_START_RF_SCAN);
        filter.addAction(Constant.ACTION_FINISH_RF_SCAN);
        registerReceiver(mRfScanReceiver, filter);
    }
    
    private void unRegisterListener() {
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
        UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_COLORTEMP_ADJ_STATUS,mFactoryListener);
        
        unregisterReceiver(mRfScanReceiver);
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
            //if (Constant.LOG_TAG) {
                Log.d(TAG, "onSignalStatus  arg0: " + arg0);
            //}
                handleSignalStat(arg0);
        }
        
        @Override
        public void onTimmingChanged(TimingInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
            }
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
        
  
        public void onSelectSource(int arg0) {
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

    private OnFactoryListener mFactoryListener = new OnFactoryListener() {

        @Override
        public void onAutoCalibrationAdjustStatus(int status) {
            // TODO Auto-generated method stub
        }

       // @Override
        public void onAutoCalibrationColorTempStatus(int status) {
            // TODO Auto-generated method stub
            Log.d(TAG,"Auto ColorTemp Result: "+status);
            if(status == 1){
                Toast.makeText(ATVMainActivity.this, "ColorTemp Auto is Success", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ATVMainActivity.this, "ColorTemp Auto is Fail", Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    /**
     * broadcast of RFScan
     */
    private BroadcastReceiver mRfScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context content, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onReceive--->action:" + action);
            }
            if (action.equals(Constant.ACTION_START_RF_SCAN)) {
                /*
                 * if(mNoSignalLayout.getVisibility() == View.VISIBLE){
                 * mNoSignalLayout.snapBackground(false); }
                 */
            	noSignalHide();
            } else if (action.equals(Constant.ACTION_FINISH_RF_SCAN)) {
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
	
    @Override
    protected void onStart(){
       Log.d(TAG,"-------ATV onStart()--------");
       super.onStart();
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

    @Override
    protected void onResume() {

        Log.d(TAG,"------ATV onResume------");
        super.onResume();
        getscreenSaverMode();
        getNoSignalPDFlag();
        registerListener();
        registerEventMonitors();
        
        showTimingFlag = 1;
        
        SystemProperties.set("persist.sys.fullScreen_Source", ""+EnumSourceIndex.SOURCE_ATV);
        
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
            int aspect = PictureInterface.getAspect();
            PictureInterface.setAspect(aspect,false);
            
        }
        
        mNoSignalCnt = 0;
        
        if (checkATVSignalStat != null){
            checkATVSignalStat.cancel();
            checkATVSignalStat = null;
        }
        
        checkATVSignalStat = new Timer();
        checkATVSignalStat.schedule(checkSignalStat(),500); /*handle in onWindowFocusChanged(boolean)*/
        
        RefreshSignalShow(true);
        
       // int tunerLNA = PropertyUtils.getInt("persist.sys.tunerLNA", 0);
        /*
        int tunerLNA = UmtvManager.getInstance().getFactory().getTunerLNA();
        int curSourceId = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
        int bitval = 1 << curSourceId;
		if((bitval & tunerLNA) != 0)
        	SystemUtils.shellExecute("echo LNA on > /proc/msp/tuner");
        else
        	SystemUtils.shellExecute("echo LNA off > /proc/msp/tuner");
		Log.d(TAG, "tunerLNA " + tunerLNA + " (1 << curSourceId) " + bitval);*///暂时关闭tunerLNA功能
    }
    
    @Override
    protected void onPause() {
    	stopNoSignalPowerdownMonitor();
    	unRegisterListener();
    	unRegisterEventMonitors();
    	hideSignalShow();
    	noSignalHide();
    	removeDialogs();
    	NumKeyHandler.removeMessages(0);
    	checkSignalHandler.removeMessages(SHOW_INFO);
        if (checkATVSignalStat != null){
            checkATVSignalStat.cancel();
            checkATVSignalStat = null;
        }

        super.onPause();
    }
    
    private void handleSignalStat(int sigstat){
        Log.d(TAG,"===========sigstat======= "+ sigstat);
        
        getPowerDownDialogFlag();
        if (!isWindOK && mPowerDownDialogOpenFlag == false){
        	Log.d(TAG,"===========handleSignalStat======= isWindOK:"+isWindOK);
        	return;
        }
        
        if (sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
        	Log.d(TAG, "-------->play--Strong-Signal---->");
        	
        	noSignalHide();
        	stopNoSignalPowerdownMonitor();
            if (showTimingFlag == 1){
            	RefreshSignalShow(true);
                showTimingFlag = 0;
            }
            mNoSignalCnt = 0;
        }else if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
            
            if (mNoSignalCnt >= NOSIGNAL_CHECK_MAXCNT){
            	Log.d(TAG, "-------->play--No-Signal-Layout---->");
            	noSignalShow();
            	startNoSignalPowerdownMonitor();
            	showTimingFlag = 1;            	
                mNoSignalCnt = 0;
            }else{
            	mNoSignalCnt++;
            	checkATVSignalStat.schedule(checkSignalStat(), 500);
            }	    
        }
    }
    
    Handler checkSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_INFO:
                int sigstat = SourceManagerInterface.getSignalStatus();
                Log.d(TAG,"===========sigstat======= "+ sigstat);
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
                checkSignalHandler.sendEmptyMessage(SHOW_INFO);
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
                noSignalTxt.setVisibility(View.INVISIBLE);
                stopScreenTxtMoving();
        	}
        }
    }
    
    private void startSignalTxt(){
        if (noSignalTxt != null) {
        	if (noSignalTxt.getVisibility() == View.INVISIBLE){
                noSignalTxt.setVisibility(View.VISIBLE);
                noSignalTxt.requestLayout();
                startScreenTxtMoving();
        	}
        }
    }
    
    private void getPowerDownDialogFlag(){
		try {
			Context systemServerAppContext;
			systemServerAppContext = createPackageContext("cn.com.unionman.umtvsystemserver", Context.CONTEXT_IGNORE_SECURITY);
	       	SharedPreferences sharedata = systemServerAppContext.getSharedPreferences("powerDownDialog", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	       	mPowerDownDialogOpenFlag = sharedata.getBoolean("powerDownDialogFlag", false);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
    private void startNoSignalPowerdownMonitor(){
		Log.i(TAG, "startNoSignalPowerdownMonitor " + isNosignalPowerdownMonitorStarted);
		
		if (mNoSignalPD == 1){
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
				
				getPowerDownDialogFlag();
				Log.i(TAG, "mPowerDownDialogOpenFlag " + mPowerDownDialogOpenFlag);
				if (mPowerDownDialogOpenFlag == true){
					Intent intent3 = new Intent();
					intent3.setAction("android.intent.action.NOSIGNAL_POWERDOWN_BROADCAST");
					intent3.putExtra("mode", "close");
					mContext.sendBroadcast(intent3);
				}
			}
		}
	}
	
	private void noSignalPowerdownMonitorKeyHandle(){
		
		int sigstat = SourceManagerInterface.getSignalStatus();
		stopNoSignalPowerdownMonitor();
		if (((noSignalTxt != null) && (noSignalTxt.getVisibility() == View.VISIBLE))
				||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
				|| (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL)){
			startNoSignalPowerdownMonitor();
		}
	}
	
	private void windowFocusChangeHandle(boolean focus){
		if (focus){
			if (checkATVSignalStat != null){
				checkATVSignalStat.schedule(checkSignalStat(),500);
			}
		}else{
			
			getPowerDownDialogFlag();
			if (mPowerDownDialogOpenFlag == false){
				
				stopNoSignalPowerdownMonitor();
			}
			noSignalHide();
			if (screenSaverMode == 1){
				checkSignalHandler.removeMessages(SHOW_INFO);
				closeScreanSaver();
			}
		}
	}
	
	private BroadcastReceiver systemEventMonitorsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d(TAG, "-------->systemEventMonitorsReceiver---->");
			if (intent.getAction().equals(RESET_MONITORS)) {
				resetMonitors();
			} 
		}
	};
	
	private void resetMonitors() {
		Log.d(TAG, "-------->resetMonitors---->");
		int sigstat = SourceManagerInterface.getSignalStatus();
		if (((noSignalTxt != null) && (noSignalTxt.getVisibility() == View.VISIBLE))
				||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
				|| (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL)){
			stopNoSignalPowerdownMonitor();
			startNoSignalPowerdownMonitor();
		}
	}
	
    /**
     * refresh view of SignalShow by force
     *
     * @param force
     */
    public void RefreshSignalShow(boolean force) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "RefreshSignalShow()");
        }
        mShowSignal.refreshPanelInfo(mSourceManager.getSelectSourceId(), force);
    }
    
    private void hideSignalShow(){
        if (mShowSignal != null && mShowSignal.getVisibility() == View.VISIBLE){
        	mShowSignal.focusHide();
        }
    }
    
    @Override
   protected void onStop() {
       super.onStop();
   }

   @Override
   protected void onDestroy() {       
       super.onDestroy();
   }
   
    @Override
    public void onBackPressed() {
    	closeScreanSaver();
    	stopScreenTxtMoving();
    	Log.d(TAG,"leon... onBackPressed");
    	super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onKeyUp keycode = " + keyCode + "; event = " + event);
        }

        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isWindOK) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onKeyDown keycode = " + keyCode + "; event = "
                        + event);
            }
            
            noSignalPowerdownMonitorKeyHandle();
            screanSaverKeyHandle();
            
			//mChannelEditView.onKeyDown(keyCode, event);
            switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_TVSETUP:
                Intent intent = new Intent(ATVMainActivity.this,
                        SettingActivity.class);
                startActivity(intent);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            	if ((mShowSignal.isPlayProgram() == true) && (mCurrentChannel != 0))
            	{
            		NumKeyHandler.removeMessages(0);
            		NumKeyHandler.sendEmptyMessage(0);
            	}
            	else
            	{
            		//Intent RecentActivityintent = new Intent(mContext, RecentActivity.class);
            		//mContext.startActivity(RecentActivityintent);
                   /*Intent ChannelActivityintent = new Intent(mContext, ChannelActivity.class);
                   mContext.startActivity(ChannelActivityintent); */
                   if(ATVChannelInterface.getProgList() != null && ATVChannelInterface.getProgList().size() > 0){
           			Intent ChannelActivityintent = new Intent(mContext, ChannelActivity.class);
                       mContext.startActivity(ChannelActivityintent); 
	           		}else{
	           			LayoutInflater inflater =LayoutInflater.from(mContext);
	           			View mView =inflater.inflate(R.layout.user_back, null);
	           			AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
	   			        final AlertDialog mAlertDialog = builder.create();
	   			        mAlertDialog.show();
	   			        mAlertDialog.getWindow().setContentView(mView);
	           			Handler handler = new Handler();  
	           	        handler.postDelayed(new Runnable() {  
	           	 
	           	            public void run() {  
	           	            	mAlertDialog.dismiss(); 
	           	            }  
	           	        }, 3000);
	           		}
                   
            	}
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    if (mChannelManager.getAvailProgCount() != 0) {
                    	mLastChannelNumber = mShowSignal.getCurChannelNumber();
                        mChannelManager.progDown();
                    }
                    RefreshSignalShow(true);
                    
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_CHANNEL_UP:
                    if (mChannelManager.getAvailProgCount() != 0) {
                    	mLastChannelNumber = mShowSignal.getCurChannelNumber();
                        mChannelManager.progUp();
                    }
                    RefreshSignalShow(true);
                break;

            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                if (mSourceManager.getSelectSourceId() == EnumSourceIndex.SOURCE_ATV) {
                	if (mCurrentChannel > 99)
                	{
                		mCurrentChannel = 0;
                	}
                    int tmpChannel = mCurrentChannel * 10 + keyCode
                            - KeyEvent.KEYCODE_0;
                    NumKeyHandler.removeMessages(0);
                   
                    if(tmpChannel <= MAX_PROG_NUM){
                        mCurrentChannel = tmpChannel;
                    }
                    NumKeyHandler.sendEmptyMessageDelayed(0, 2000);
         
                    mShowSignal.changingChannel(mCurrentChannel);
                }
                break;
            case KeyEvent.KEYCODE_INFO:
            	mShowSignal.showChannelInfo();
            	break;
			case KeyEvent.KEYCODE_SEARCH:
				finish();
				break;
            case KeyEvent.KEYCODE_BACK:
            	/*mCurrentChannel = mLastChannelNumber;
            	NumKeyHandler.removeMessages(0);
        		NumKeyHandler.sendEmptyMessage(0);*/
            	ATVChannelInterface.progReturn();
            	 RefreshSignalShow(true);
        		return true;
                //return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                adjustStreamVolume(keyCode);
                break;
            case KeyEvent.KEYCODE_PICTUREMODE :
            	pictureModeQuickKeyHandle();
            	break;
            	
            /*case KeyEvent.KEY_TRACK:
            	TrackModeQuickKeyHandle();
            	break;*/
            	
            case KeyEvent.KEYCODE_SOUNDMODE:
            	soundModeQuickKeyHandle();
            	break;
            case KeyEvent.KEYCODE_ZOOM:
            	String w = SystemProperties.get("persist.sys.reslutionWidth");
            	String h = SystemProperties.get("persist.sys.reslutionHight");
            	if(!((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160")))){
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
       	Log.i(TAG, "zemin KeyEvent.KEYCODE_PICTUREMODE press");    
    	Log.i(TAG,"isPicDialogDismiss="+isPicDialogDismiss);

    	finishHandle.removeMessages(SOUND_DIALOG_DISMISS_BYTIME);

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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
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
	        picAlertdialog.getWindow().setContentView(myView);*/
	        isPicDialogDismiss=false;
            picdialog.addView(myView);
		   delay(PIC_DIALOG_DISMISS_BYTIME);         		
    	}     			         	
    }
    
    private void soundModeQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEYCODE_SOUNDMODE press");
    	Log.i(TAG,"isSoundDialogDismiss="+isSoundDialogDismiss);

    	finishHandle.removeMessages(PIC_DIALOG_DISMISS_BYTIME);
    	
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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
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
      	  Builder	 mSoundBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
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
   	 //   Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
          trackdialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater factory = LayoutInflater.from(mContext);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	     track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
 	    	
 	    track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);
 /*	   trackAlertdialog = mPicBuilder.create();
 	       
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
    
    private void zoomQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEY_ZOOM press");
    	Log.i(TAG,"isZoomDialogDismiss="+isZoomDialogDismiss);
    	
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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
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
    	  int aspect = PictureInterface.getAspect();          //display mode  4 闁汇垹顭峰鎵焊閸濆嫷鍤�
   	     zoomModeIndex= Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	    //闁哄秷顬冨畵渚�閿熺晫婀撮柨娑樿嫰楠炴捇姊介妶鍕溄闁绘せ鏅槐婵嬪绩閹佷海1闁挎稑鏈弬浣瑰緞閿熶粙鏌呮径鎰╋拷闁靛棗鍊哥紞瀣嚔瀹勬澘绲块柛濠囨？鐠愮喐娼诲▎搴ｇ憦闁兼澘鎳忓鍌炴晬瀹�喚鍟庣紓鍐惧枛閿熻姤绋夌弧鍦玃ECT_16_9
   	    if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2||(aspect==EnumPictureAspect.ASPECT_AUTO))){
   	 	   PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
   	 	   aspect = PictureInterface.getAspect(); 
   	 	  zoomModeIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	 	   Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2 or ASPECT_AUTO;set value = ASPECT_16_9; aspect="+aspect);
   	    }                 	
       	   // Builder  mZoomBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
					 Log.i(TAG,"aspect="+aspect);
					 Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
              zoomdialog = (LinearLayout) this.findViewById(R.id.mydialog);
       	      LayoutInflater zoom_factory = LayoutInflater.from(mContext);
       	      View zoom_myView = zoom_factory.inflate(R.layout.selector_view_dialog,null);
       	       zoom_menu_btn =(TextView) zoom_myView.findViewById(R.id.menu_btn);
       	      zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
/*       	      zoomAlertdialog = mZoomBuilder.create();
       	      
		        Window zoom_window = zoomAlertdialog.getWindow();
		        WindowManager.LayoutParams zoom_lp = zoom_window.getAttributes();
		        zoom_lp.y=350;
		        zoom_window.setAttributes(zoom_lp);
		        zoomAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		        zoomAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		        zoomAlertdialog.show();
		        zoomAlertdialog.getWindow().setContentView(zoom_myView);*/
		        isZoomDialogDismiss=false;
               zoomdialog.addView(zoom_myView);
			   delay(ZOOM_DIALOG_DISMISS_BYTIME);           		
    	}
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

    /**
     * handler of refresh SignalShow
     */
    Handler NumKeyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // isChangingChanel = false;
   
            if(mChannelManager.getAvailProgCount() != 0){
                /*if (mCurrentChannel >= mChannelManager.getAvailProgCount()) {
                    mCurrentChannel = mChannelManager.getAvailProgCount() - 1;
                }*/
                if (mCurrentChannel >= Constant.MAX_PROGRAM_COUNT) {
                    mCurrentChannel = Constant.MAX_PROGRAM_COUNT-1;
                }
                if (mCurrentChannel < 0) {
                    mCurrentChannel = 0;
                }
                ArrayList<TvProgram> atvChannelList = new ArrayList<TvProgram>();
                
                atvChannelList = ATVChannelInterface.getProgList();
                for (int i=0; i<atvChannelList.size(); i++)
                {
                	TvProgram info = atvChannelList.get(i);
                	if (mCurrentChannel == info.getiId())
                	{
                		mLastChannelNumber = mShowSignal.getCurChannelNumber();
                		mChannelManager.selectProg(mCurrentChannel);
                		break;
                	}
                }
            }
            RefreshSignalShow(true);
            mCurrentChannel = 0;
            super.handleMessage(msg);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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
	private Timer screenSaverTimer ;
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
   
    private void showScreenSaver() {
		Log.i(TAG,"showScreenSaver");
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
    
    private void closeScreanSaver() {
    	Log.i(TAG,"closeScreanSaver");
    	
    	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    	if (screenSaverImg.getVisibility()==View.VISIBLE)
    	{
    		screenSaverImg.setVisibility(View.GONE);
    		screenSaverTimer.cancel();
    		screenSaverTimer = null;
    	}
    }
    
	private void screanSaverKeyHandle(){
		if (screenSaverMode == 1){
			checkSignalHandler.removeMessages(SHOW_INFO);
			closeScreanSaver();			
			int sigstat = SourceManagerInterface.getSignalStatus();
			if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
				checkSignalHandler.sendEmptyMessageDelayed(SHOW_INFO, 2000);
			}
		}
	}
	
    /* 
    * 闂備浇娉曢崰鎰板几婵犳艾绠柣鎴ｅГ閺呮悂鏌ｉ褍浜濋悽顖氬濞戠敻鏌ㄧ�顏嶄紦闂備浇娉曢崰鎰板几婵犳艾绠柣鎴ｅГ閺呮悂鏌ら崫鍕拷妞わ綆鍣ｉ獮蹇涙憥閸屾粎孝闂備浇娉曢崰鎰板几婵犳艾绠紒宀嬫嫹闂備浇娉曢崰鎰板几婵犳艾绠柣鎴ｅГ閺呮悂鏌￠崒妯猴拷閻庢艾缍婇弻銊╂偄缁嬭法鏋傞柣鐘叉厂閸曨偒浼撻梻浣芥硶閸犲秹鎳犳惔銊ョ疅閻庯綆鍋勯鍫曟⒑鐠恒劌鏋旀い蹇ｄ邯閺佹劘绠涢弬娆句紦 
    * XY婵炴垶鎸诲浠嬪极閹捐妫橀柕鍫濇椤忓爼姊虹捄銊ユ灆濠电偛娲︾�鐓庘枎閹邦剦浼撻梻浣芥硶閸ｏ箓骞忛敓锟�   */ 
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
	private Timer dymScreenTxtTimer ;
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
	            	Log.i("haha","currLeft "+currLeft+" currTop "+currTop);
	            	if (txtMovingDown==1) {
	            		Log.i("haha","txtMovingDown==1");
	            		currTop += txtMovingStep;
	            		if (currTop>mSurfaceView.getHeight()-view.getHeight()) {
	            			txtMovingDown = 0;
	            			currTop = mSurfaceView.getHeight()-view.getHeight();
	            		}
	            	} else {
	            		Log.i("haha","txtMovingDown not=1");
	            		currTop -= txtMovingStep;
	            		if (currTop<0) {
	            			txtMovingDown = 1;
	            			currTop = 0;
	            		}
	            	}
	            	
	            	if (txtMovingLeft==1) {
	            		Log.i("haha","txtMovingLeft==1");
	            		currLeft += txtMovingStep;
	            		if (currLeft>mSurfaceView.getWidth()-view.getWidth()) {
	            			txtMovingLeft = 0;
	            			currLeft = mSurfaceView.getWidth()-view.getWidth();
	            		}
	            	} else {
	            		Log.i("haha","txtMovingLeft not=1");
	            		currLeft -= txtMovingStep;
	            		if (currLeft<0) {
	            			txtMovingLeft = 1;
	            			currLeft = 0;
	            		}
	            	}
	            	Log.i("haha","after currLeft "+currLeft+" currTop "+currTop);
	            	setLayout(view, currLeft,currTop);
	                break;         
	            }         
	            super.handleMessage(msg);     
	        }     
	             
	    }; 
	    
	private TimerTask dymScreenTxtTask ;
	
	private void startScreenTxtMoving() {
		if (noSignalTxt!=null&&noSignalTxt.getVisibility() == View.VISIBLE){
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
        	switch (msg.what) {
			case PIC_DIALOG_DISMISS_BYTIME:
				//picAlertdialog.dismiss();
                picdialog.removeAllViews();
				isPicDialogDismiss=true;
				break;
			case TRACK_DIALOG_DISMISS_BYTIME:
				//trackAlertdialog.dismiss();
                trackdialog.removeAllViews();
				isTrackDialogDismiss=true;
				break;
			case SOUND_DIALOG_DISMISS_BYTIME:
				 //soundAlertdialog.dismiss();
                sounddialog.removeAllViews();
	             isSoundDialogDismiss=true;
				break;
			case ZOOM_DIALOG_DISMISS_BYTIME:
				//zoomAlertdialog.dismiss();
                zoomdialog.removeAllViews();
				isZoomDialogDismiss=true;
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
    
    private void KillAppsBeforeSelectSource()
    {
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(!actRSI.processName.equals("com.um.launcher")
				&& !actRSI.processName.equals("com.um.atv")
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
				&& !actRSI.processName.equals("com.unionman.factorytestassist")
				&& !actRSI.processName.equals("com.unionman.factorytestassist.PrepareActivity")
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
		        pullParser.setInput(xml, "UTF-8"); //濞戞挾鐦籾ll閻熸瑱缍侀崳鎾闯閵婎煈鍟庣紓鍐惧枦椤懐鎲撮敐鍡欙拷闁汇劌鍨昅L闁轰胶澧楀畵锟�      
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
		  String[] pkgNameList =  infor.pkgList;; // 闂佸吋鍎抽崲鑼鏉堛劍浜ら柟閭﹀灱閺�粙鏌涢敂鍝勫妞ゆ洘纰嶅璇测槈濮橈絾鐓犻梻浣瑰絻閻厧鈻撻幋锕�闁逞屽墴瀵灚寰勬繝鍕暔闂佹椿鏋岄崝搴ㄥ煝閸忓吋鍎熼煫鍥ㄦ尭閻︼拷
		  // 闁哄鐗婇幐鎼佸吹椤撱垹绠ラ柍褜鍓熷鍨緞婵犲嫬鐣ㄩ梺娲绘瀸閸斿酣鍩㈤崗鍏煎劅闊洦娲橀悾閬嶆煕閺嵮勫櫣闁诡噯鎷�
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
