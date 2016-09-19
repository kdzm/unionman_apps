package cn.com.unionman.umtvsystemserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar.LayoutParams;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.os.SystemProperties;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class TVSystemMonitorService extends  Service{
	public static int mNoSignalPD = 1;
	public static int mAutoStandby = 0;
	public static int mSleepOn = 0;
	public static int mUsbEventMonitor = 0;
	private static String TAG = "TVSystemMonitorService";
    private static final String SIGNAL_STATE_ACTION = "cn.com.unionman.umtvsystemserver.SIGNAL_STATE_CHANGED";
    private static final String NOSIGNAL_PD_SWITCH_ACTION = "cn.com.unionman.umtvsystemserver.NOSIGNAL_PD_SWITCH_ACTION";
    private static final String AUTO_STANDBY_ACTION = "cn.com.unionman.umtvsystemserver.AUTO_STANDBY_ACTION";
	private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
	private static final String SLEEP_ON_SWITCH_ACTION = "cn.com.unionman.umtvsystemserver.SLEEP_ON_SWITCH_ACTION";
	private static final int SOURCE_PLUGININ = 1;
	private static final int SLEEPON_POWNDOWN = 2;
	private static boolean isNosignalMonitorStarted = false;
	private static boolean isAutoStandbyMonitorStarted = false;
	private static boolean isSleepOnMonitorStarted = false;
	private int lastSourceId;
	private Dialog mScreenSaverDialog ;
	private PopupWindow mScreenSaverPopupWindow;
	private static SourceSelectDialog mDialog;
	private static boolean mDialogOpenFlag = false;
	private static boolean mSourceDetectEnable = false;
    private Timer SourceDetectEnableTimer = null;
    private TimerTask timerTask = null;
    private Set mPluginSourceSet = null;
    private Context mContext = this;
	private Dialog mUsbEventDialog;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what){
			case SOURCE_PLUGININ:
				if (mPluginSourceSet.size() > 0){
					Iterator iterator = mPluginSourceSet.iterator();
					Integer integer = (Integer)iterator.next();
					if (integer != null){
						srcDetectPluginHandle(integer.intValue());
					}
					
					mPluginSourceSet.clear();
				}				
				break;
			case SLEEPON_POWNDOWN:
			    Intent intent = new Intent();
			    intent.setAction("android.intent.action.SLEEP_NO_BROADCAST");
			    mContext.sendBroadcast(intent);
				break;
			default:
				break;
			}
		}
	};
	
    public boolean checkAutoStandbySwitch() {
    	Log.i(TAG, "checkAutoStandbySwitch "+mAutoStandby);
    	return (mAutoStandby!=0);
    }
    
    public boolean checkSleepOnSwitch() {
    	Log.i(TAG, "checkSleepOnSwitch "+mSleepOn);
    	return (mSleepOn!=0);
    }
	
	private void loadFlags() {
        try {
	        Context otherContext = createPackageContext(
	                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
	        SharedPreferences sp = otherContext.getSharedPreferences(
	                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	        
	        mSleepOn = sp.getInt("sleeponState", 0);
	        mUsbEventMonitor = sp.getInt("usbState", 1);
	        Log.i(TAG,"usbState "+mUsbEventMonitor);
	        Log.i(TAG,"sleeponState "+mSleepOn);
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
        
		try {
			Context powerSaveAppContext;
			powerSaveAppContext = createPackageContext("cn.com.unionman.umtvsetting.powersave", Context.CONTEXT_IGNORE_SECURITY);
	       	SharedPreferences sharedata = powerSaveAppContext.getSharedPreferences("PoweritemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	       	mAutoStandby = sharedata.getInt("waitting", 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
    public TimerTask checkSourceDetectEnable(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
            	mSourceDetectEnable = true;
            	Log.i(TAG,"set mSourceDetectEnable "+mSourceDetectEnable);
            	try{
                	SourceDetectEnableTimer.cancel();
                	SourceDetectEnableTimer = null;
                	timerTask.cancel();
                	timerTask = null;
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }
        };
        return timerTask;
    }
    
	@Override
	public void onCreate() {
		Log.i(TAG,"onCreate");
		lastSourceId = SourceManagerInterface.getSelectSourceId();
		
		loadFlags();

    	if (checkSleepOnSwitch())
    		startSleepOnMonitor();
		
		registerEventMonitors();
		registerListener();
		
		SourceDetectEnableTimer = new Timer();
		SourceDetectEnableTimer.schedule(checkSourceDetectEnable(), 20 * 1000);
		
		mPluginSourceSet = new HashSet<Integer>();
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 0;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG,"onCreate");
		stopSleepOnMonitor();
		unRegisterEventMonitors();
		unregisterListener();
		
		super.onDestroy();
	}
	
	private int timeOut[] = new int[]{
		0,
		10,
		20,
		30,
		60,
		90,
		120,
		180,
		240
	};
	
	private void startSleepOnMonitor() {
		Log.i(TAG, "startSleepOnMonitor "+isSleepOnMonitorStarted);
		
			if (!isSleepOnMonitorStarted) {
				mHandler.sendEmptyMessageDelayed(SLEEPON_POWNDOWN, timeOut[mSleepOn]*60*1000);
				isSleepOnMonitorStarted = true;
			}
	}
	
	private void stopSleepOnMonitor() {
		Log.i(TAG, "stopSleepOnMonitor "+isSleepOnMonitorStarted);
		if (isSleepOnMonitorStarted) {
			mHandler.removeMessages(SLEEPON_POWNDOWN);
		    isSleepOnMonitorStarted = false;
		}
	}

	private void registerEventMonitors(){
		Log.i(TAG,"registerEventMonitors");
		
		IntentFilter filter = new IntentFilter();  
		filter.addAction(AUTO_STANDBY_ACTION);  
		filter.addAction(RESET_MONITORS);  
		filter.addAction(SLEEP_ON_SWITCH_ACTION); 
		registerReceiver(systemEventMonitorsReceiver, filter);
				
		IntentFilter filter2 = new IntentFilter();  
		filter2.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter2.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter2.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter2.addDataScheme("file");
		registerReceiver(usbEventMonitorsReceiver, filter2);
	}
	
	private void unRegisterEventMonitors() {
		unregisterReceiver(systemEventMonitorsReceiver);
		unregisterReceiver(usbEventMonitorsReceiver);
	}
	
	private void resetMonitors() {
		Log.i(TAG, "resetMonitors");
		stopSleepOnMonitor();
		
    	if (checkSleepOnSwitch())
    		startSleepOnMonitor();
	}
	
	private BroadcastReceiver systemEventMonitorsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(RESET_MONITORS)) {
				loadFlags();
				resetMonitors();
			} else if (intent.getAction().equals(AUTO_STANDBY_ACTION)) {
				loadFlags();
            } else if (intent.getAction().equals(SLEEP_ON_SWITCH_ACTION)) {
            	loadFlags();
            	if (checkSleepOnSwitch()) {
            		stopSleepOnMonitor();
            		startSleepOnMonitor();
            	} else {
            		stopSleepOnMonitor();
            	}
            	Log.i(TAG,"SLEEP_ON_SWITCH_ACTION "+mSleepOn);
            	
            }  
		}
	};
    
	private BroadcastReceiver usbEventMonitorsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			 Log.i(TAG,"usbEventMonitorsReceiver");
			 if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)&&checkCurrentActivityToOpen()) {
	            	loadFlags();
	                Log.i(TAG,"USB_PLUGIN_ACTION"+mUsbEventMonitor);
	                if (mUsbEventMonitor!=0&&(mUsbEventDialog==null||!mUsbEventDialog.isShowing())) {
	                	mUsbEventDialog = new UsbEventDialog(TVSystemMonitorService.this);
	                	mUsbEventDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
	            			@Override
	            			public void onDismiss(DialogInterface arg0) {
	            				mUsbEventDialog = null;
	            			}
	            		});
	                	mUsbEventDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	                	mUsbEventDialog.show();
	                }
			 } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)
            		|| intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            	
            	if (mUsbEventDialog!=null&&mUsbEventDialog.isShowing()) {
            		mUsbEventDialog.dismiss();
            	}
            }
			
		}
		
	};
	
    private boolean checkCurrentActivityToOpen(){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
        
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);  
   
        RunningTaskInfo rti = runningTasks.get(0);  
        ComponentName component = rti.topActivity;
        String currPackage = component.getPackageName();
        String currActivity = component.getClassName();
        Log.d(TAG,"myleon... component.getPackageName():"+component.getPackageName()+";component.getClassName():"+component.getClassName());
        //ý�����ġ�����Ƶ��ͼƬ���� ������ʾ��
        if (currPackage.equals("com.um.gallery3d")||
        	currPackage.equals("com.um.music") ||
        	currPackage.equals("com.um.videoplayer") ||
        	currPackage.equals("com.umexplorer") ||
        	currPackage.equals("com.um.filemanager") ||
        	(currPackage.equals("com.um.atv") && currActivity.equals("com.um.atv.AutoScanActivity")) ||
        	(currPackage.equals("com.um.atv") && currActivity.equals("com.um.atv.ManualScanActivity")) || 
        	(currPackage.equals("com.um.dvbsearch") && currActivity.equals("com.um.ui.Search"))) {
        	return false;
        } else {
        	return true;
        }
    }
    
    private void registerListener() {
    	Log.d(TAG, "  registerListener ");
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
    }
    
    private void unregisterListener() {
    	Log.d(TAG, "  unregisterListener ");
        UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        
        UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
    }
    
    OnPlayerListener onPlayerListener = new OnPlayerListener() {

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
        }
        
        @Override
        public void onSignalStatus(int arg0) {
        }
        
        @Override
        public void onTimmingChanged(TimingInfo arg0) {
        }

		@Override
        public void onSelectSource(int arg0){
		}
        @Override
       public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
           
        }
        @Override
        public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
        	Log.d(TAG, "onSrcDetectPlugin");
        	int i = 0, cnt = 0;
        	
        	cnt = arg0.size();
        	if (cnt > 0){
        		for (i = 0; i < cnt; i++){
            		int sourceIndex = arg0.get(i).intValue();
            		Log.d(TAG,"onSrcDetectPlugin,sourceIndex:"+sourceIndex);
            		
            		if (!mPluginSourceSet.contains(arg0.get(i))){
                		mPluginSourceSet.add(arg0.get(i));
            		}
            		
            		if (i == 0){
            			mHandler.sendEmptyMessageDelayed(SOURCE_PLUGININ, 3000);
            		}
        		}
        	}
        	
        	//Log.d(TAG, "onSrcDetectPlugin mPluginSourceSet cnt:"+mPluginSourceSet.size());
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
        	Log.d(TAG, "onSrcDetectPlugout");
        	
        	if (arg0.size() > 0){
        		for (Integer m : arg0){
        			if (mPluginSourceSet.contains(m)){
        				mPluginSourceSet.remove(m);
        				//Log.d(TAG,"onSrcDetectPlugout m:"+m.intValue());
        			}
        		}
        	}
        	
        	//Log.d(TAG, "onSrcDetectPlugout mPluginSourceSet cnt:"+mPluginSourceSet.size());
        	if (mPluginSourceSet.size() <= 0){
        		mHandler.removeMessages(SOURCE_PLUGININ);
        	}
        }
		
		@Override
        public void onPlayLock(ArrayList<Integer> list) {
            // TODO Auto-generated method stub
			
        }
    };
    
	private boolean isTFProduct(){
		String tmp = "";
		boolean isTF = false;
		
		tmp = SystemProperties.get("ro.umtv.sw.version");
		tmp = tmp.substring(11,13);
		if (tmp.contains("TF")){
			isTF = true;
		}else{
			isTF = false;
		}
		
		return isTF;
	}
	
    private void srcDetectPluginHandle(int sourceIndex){
    	String bootFlag = "";
    	
    	int curId = SourceManagerInterface.getCurSourceId();
    	bootFlag = SystemProperties.get("persist.sys.bootup");
    	if ((mSourceDetectEnable == false) || bootFlag.equals("1")){
    		Log.d(TAG,"myleon... mSourceDetectEnable:"+mSourceDetectEnable);
    		return;
    	}
    	
    	if (sourceIndex != curId){
    		
    		if (isTFProduct()){
    			if (((curId == EnumSourceIndex.SOURCE_CVBS1) && (sourceIndex == EnumSourceIndex.SOURCE_YPBPR1))
    					|| ((curId == EnumSourceIndex.SOURCE_YPBPR1) && (sourceIndex == EnumSourceIndex.SOURCE_CVBS1))){
    				Log.d(TAG,"TFProduct return");
    				return;
    			}
    		}
    		
    		if (mDialogOpenFlag){
    			mDialog.myCancel();
    			mDialog = null;
    			mDialogOpenFlag = false;
    		}
    		
    		openSourceSelectDialogWithTimeout(30, null, sourceIndex);
    	}
    }
    
	private void openSourceSelectDialogWithTimeout(int tminsecond,Intent intent, int sourceIndex){
		Log.i(TAG,"openSourceSelectDialogWithTimeout");
		Log.i("heh", "==openSourceSelectDialogWithTimeout=");
		if (mDialogOpenFlag == false){
			mDialog = new SourceSelectDialog(this, tminsecond, sourceIndex);
			mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					mDialogOpenFlag = false;
				}
			});
			mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mDialogOpenFlag = true;
			mDialog.show();
		}	
	}
}
