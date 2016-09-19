package com.um.huanauth;

import com.um.huanauth.net.HttpWorkTask;
import com.um.huanauth.net.UpgradeBiz;
import com.um.huanauth.provider.HuanAuthInfoBean;
import com.um.huanauth.provider.HuanUpgradeInfoBean;
import com.um.huanauth.net.DownloadManagerPro;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

public class UpgradeService extends Service{
	private static final String TAG = "UpgradeService";
	private static final int DOWNLOAD_STATUS = 0;
	private static final int DO_DETECT = 1;
	public static final int DO_DOWNLOAD = 2;
	public static final int DO_UPDATE = 3;
	private static final int DOWNLOAD_FAILURE = 4;
	private static final int DOWNLOAD_SUCCESS = 5;
	public static final int DOWNLOAD_CANCLE = 6;
	private static final int OPEN_DOWNLOAD_DIALOG = 7;
	private static final int DETECT_FAILURE = 8;
	private static final int NO_SPACE = 9;
	private static final int VERIFY_FAILURE = 10;
	private static final int UPDATE_FAILURE = 11;
	private static final int BOOT_DETECT_MAX = 3;
	public static final int UpgradeService_BOOT = 1;
	public static final int UpgradeService_AUTODETECT = 2;
	private static final int UpgradeService_MANUALDETECT = 3;
	private static int mBootDetectCount = 0;
	private static boolean mBootDetectFinish = false;
	private ConnectivityManager mConnectivityManager;
	private Context mContext;
	private static boolean mUploadinfo = false;
	private UpgradeBiz mUpgradeBiz;
	private CompleteReceiver completeReceiver;
	private MyHandler handler;
    private DownloadManager mDownloadManager;
    private DownloadManagerPro mDownloadManagerPro;
    private DownloadChangeObserver downloadObserver;
    private OtherThread mOtherThread;
    private UpgradeDialog mUpgradeDialog = null;
    private UpdateDialog mUpdateDialog = null;
    private boolean mAutoCheck = true;
    public static int mServiceState = UpgradeService_MANUALDETECT;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG,"onCreate");
		mContext = this;
		
		mOtherThread = new OtherThread(); //用于网络等耗时操作
		mOtherThread.start();
        handler = new MyHandler(); //用于UI提示
        
        downloadObserver = new DownloadChangeObserver();
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //mDownloadManager = new DownloadManager(getContentResolver(), getPackageName());
        //mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
        mDownloadManagerPro = new DownloadManagerPro(new DownloadManager(getContentResolver(), getPackageName()));
		mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		mUpgradeBiz = new UpgradeBiz(this, mDownloadManager);
		
		registerUSBroadcastReceiver();
		completeReceiver = new CompleteReceiver();
		registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
		
		mAutoCheck = getAutoCheckFlag();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        
		Log.d(TAG,"onStartCommand mServiceState:"+mServiceState);
		
        if (mServiceState == UpgradeService_BOOT){
        	Log.d(TAG,"onStartCommand boot");
        	mServiceState = UpgradeService_MANUALDETECT;
        }else if (mServiceState == UpgradeService_AUTODETECT){
			Log.d(TAG,"onStartCommand autodetect");
			if (mAutoCheck){
				sendMessageToOtherThread(DO_DETECT);
			}
        	mServiceState = UpgradeService_MANUALDETECT;
        }else{
        	Log.d(TAG,"onStartCommand UpgradeService_MANUALDETECT");
			mBootDetectFinish = true;
			mBootDetectCount = BOOT_DETECT_MAX;
			removeMessageInOtherThread(DO_DETECT);
			sendMessageToOtherThread(DO_DETECT);
			Log.d(TAG,"onStartCommand mBootDetectFinish:"+mBootDetectFinish);
        }
        
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(completeReceiver);
		getContentResolver().unregisterContentObserver(downloadObserver);
		unregisterReceiver(usbReceiver);
		if (mOtherThread.getLooper() != null){
			mOtherThread.getLooper().quitSafely();
		}
	}
    
	private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            	
            	Log.d(TAG,"Intent.ACTION_MEDIA_MOUNTED");
            }
        }
    };
    
    private void registerUSBroadcastReceiver(){
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addDataScheme("file");
        registerReceiver(usbReceiver, usbFilter);
    }
    
    private void doUpgradeInTask(){
    	boolean ret = false;
    	
    	ret = mUpgradeBiz.toWaitSdcardMount();
    	if (ret){
        	String state = Environment.getExternalStorageState();
        	Log.d(TAG,"doUploadInfo getExternalStorageState:"+state);
        	if (!Environment.MEDIA_MOUNTED.equals(state)){
        		;
        	}
    	}
    	
    	if (mUploadinfo == false){
			doUploadInfo();
			mUploadinfo = true;
		}
    	
		doUpgrade();
    }
    
    private void doUpgrade(){
    	int ret = -1;
    	Log.d(TAG,"doUpgrade");
    	
    	mBootDetectCount++;
    	ret = mUpgradeBiz.doDetect();
    	if (ret == 0){
    		mBootDetectCount = BOOT_DETECT_MAX;
        	if ((mUpgradeDialog == null) || (mUpgradeDialog == null)){
        		HuanUpgradeInfoBean bean = mUpgradeBiz.addUpgradeDetectInfoBean();
        		Message msg = Message.obtain();
        		msg.what = OPEN_DOWNLOAD_DIALOG;
        		msg.obj = bean;
        		handler.sendMessage(msg);
        	}
    	}else{
    		if (mBootDetectCount < BOOT_DETECT_MAX){
    			Log.d(TAG,"doUpgrade mBootDetectCount:"+mBootDetectCount);
    			sendMessageToOtherThread(DO_DETECT);
    			
    		}else{
    			if (mBootDetectFinish){
    				handler.sendEmptyMessage(DETECT_FAILURE);
    			}
    		}
    	}
    	
    }
    
    private void doUploadInfo(){
    	Log.d(TAG,"doUploadInfo");
    	mUpgradeBiz.checkForUploadUpgradeInfo();
    }
    
    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
        	
        	long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        	Log.d(TAG,"download CompleteReceiver completeDownloadId:"+completeDownloadId+";mUpgradeBiz.getCurDownLoadId():"+mUpgradeBiz.getCurDownLoadId());
        	//DownloadManagerPro downloadManagerPro = new DownloadManagerPro(new DownloadManager(getContentResolver(), getPackageName()));
        	if ((mUpgradeBiz.getCurDownLoadId() == completeDownloadId) || (completeDownloadId == -1)){
        		if (mDownloadManagerPro.getStatusById(mUpgradeBiz.getCurDownLoadId()) == DownloadManager.STATUS_SUCCESSFUL){       			
        			openUpdateDialogWithTimeout(30, mOtherThread.getHandler());
        			sendMessageToOtherThread(DOWNLOAD_SUCCESS);
        		}
        	}
        }
    }
    
    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
        	Log.d(TAG,"onChange");
            updateView();
        }

    }
    
    public void updateView() {
    	Log.d(TAG,"updateView mUpgradeBiz.getCurDownLoadId():"+mUpgradeBiz.getCurDownLoadId());
    	
    	if (mUpgradeBiz.getCurDownLoadId() == 0xffffffff){
    		return;
    	}
    	
    	int[] bytesAndStatus = {0, 0, 0};
    	try{
    		bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(mUpgradeBiz.getCurDownLoadId());
            handler.sendMessage(handler.obtainMessage(DOWNLOAD_STATUS, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
            
            Log.d(TAG,"updateView bytesAndStatus[0]:"+bytesAndStatus[0]+";bytesAndStatus[1]:"+bytesAndStatus[1]+";bytesAndStatus[2]:"+bytesAndStatus[2]);
    	}catch(Exception e){
    		e.printStackTrace();
    		Log.d(TAG,"updateView getBytesAndStatus failure");
    		
        	String str = getResources().getString(R.string.download_error);
        	UpgradeToast myToast = new UpgradeToast(mContext, 2, str);
        	myToast.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        	myToast.show();
    		sendMessageToOtherThread(DOWNLOAD_FAILURE);
    	}
    }
    
    private class MyHandler extends Handler {
    	int ret = 0;
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DOWNLOAD_STATUS:
                    int status = (Integer)msg.obj;
                    Log.d(TAG,"handleMessage status:"+status);
                    if (status != 0) {
                    	if (status == DownloadManager.STATUS_FAILED){
                    		Log.d(TAG,"DownloadManager.STATUS_FAILED");
                        	String str = getResources().getString(R.string.download_error);
                        	UpgradeToast myToast = new UpgradeToast(mContext, 2, str);
                        	myToast.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                        	myToast.show();
                    		sendMessageToOtherThread(DOWNLOAD_FAILURE);
                    		
                    	}
                    }

                    //if (isDownloading(status)) {
                        int cur = msg.arg1;
                        int total = msg.arg2;
                        Log.d(TAG, "download status:" + status + ";cur:" + cur + ";total:" + total);
                   //}
                    break;
                case OPEN_DOWNLOAD_DIALOG:
                	HuanUpgradeInfoBean bean = (HuanUpgradeInfoBean)msg.obj;
                	ret = 0;
                	ret = mUpgradeBiz.checkDownLoadCondition(bean);
                	if (ret != 0){
                		mUpgradeBiz.deleteUpgradeDetectInfoBean(bean);
                	}
                	
                	if (ret == 0){
                		openUpgradeDialogWithTimeout(30, handler, bean); //打开提示框，提示用户是否需要升级
                	}else if (ret == UpgradeBiz.DOWN_COMPELETE){ //提示下载完成是否进入数据更新
                		openUpdateDialogWithTimeout(30, mOtherThread.getHandler());
                	}else if (ret == UpgradeBiz.DOWN_NOW){ //提示正在下载
                		if (mBootDetectFinish){
                        	String str2 = getResources().getString(R.string.down_now);
                        	UpgradeToast myToast2 = new UpgradeToast(mContext, 2, str2);
                        	myToast2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                        	myToast2.show();
                		}
                	}else if (ret == UpgradeBiz.UPDATE_NOW){ //提示正在更新
                		if (mBootDetectFinish){
                			openUpdateDialogWithTimeout(30, mOtherThread.getHandler());
                		}
                	}
                	break;
                case DO_DOWNLOAD:      //用户选择确定后提示用户进入升级，并发消息到非UI线程进行网络下载
                	HuanUpgradeInfoBean bean1 = (HuanUpgradeInfoBean)msg.obj;
                	String str = getResources().getString(R.string.download_warn);
                	UpgradeToast myToast = new UpgradeToast(mContext, 2, str);
                	myToast.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                	myToast.show();
        			int ret1 = mUpgradeBiz.doDownLoad(bean1);
        			if (ret1 == UpgradeBiz.NO_SPACE){
        				handler.sendEmptyMessage(NO_SPACE);
        			}
                	break;
                case DETECT_FAILURE:  //检测失败
                	ret = 0;
                	ret = mUpgradeBiz.checkDownLoadState();
                	if (ret == 0){
                    	String str1 = getResources().getString(R.string.detect_failure);
                    	UpgradeToast myToast1 = new UpgradeToast(mContext, 2, str1);
                    	myToast1.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                    	myToast1.show();
                	}else if (ret == UpgradeBiz.DOWN_COMPELETE){ //提示下载完成是否进入数据更新
                		openUpdateDialogWithTimeout(30, mOtherThread.getHandler());
                	}else if (ret == UpgradeBiz.DOWN_NOW){ //提示正在下载
                    	String str2 = getResources().getString(R.string.down_now);
                    	UpgradeToast myToast2 = new UpgradeToast(mContext, 2, str2);
                    	myToast2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                    	myToast2.show();
                		
                	}else if (ret == UpgradeBiz.UPDATE_NOW){ //提示正在更新
                		openUpdateDialogWithTimeout(30, mOtherThread.getHandler());
                	}
                	break;
                case NO_SPACE:
                	String str4 = getResources().getString(R.string.no_space);
                	UpgradeToast myToast4 = new UpgradeToast(mContext, 2, str4);
                	myToast4.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                	myToast4.show();
                	break;
                case VERIFY_FAILURE:
                	String str5 = getResources().getString(R.string.verify_failure);
                	UpgradeToast myToast5 = new UpgradeToast(mContext, 2, str5);
                	myToast5.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                	myToast5.show();
                	break;
                case UPDATE_FAILURE:
                	String str6 = getResources().getString(R.string.update_failure);
                	UpgradeToast myToast6 = new UpgradeToast(mContext, 2, str6);
                	myToast6.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
                	myToast6.show();
                	break;
                case DOWNLOAD_CANCLE:
                	sendMessageToOtherThread(DOWNLOAD_CANCLE);
                	break;
                default:
                	break;
            }
        }
    }
    
    public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    private int getStatusStringId(int status) {
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                return R.string.download_error;
            case DownloadManager.STATUS_SUCCESSFUL:
                return R.string.download_success;
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                return R.string.download_running;
            case DownloadManager.STATUS_PAUSED:
                return R.string.download_running;
        }
        
        return 0;
    }
    
    private void openUpgradeDialogWithTimeout(int tminsecond, Handler handler, HuanUpgradeInfoBean bean){
		Log.i(TAG,"openUpgradeDialogWithTimeout mUpgradeDialog:"+mUpgradeDialog);
		
		if (mUpgradeDialog == null){
			mUpgradeDialog = new UpgradeDialog(this, tminsecond, mUpgradeBiz.getVersionInfo(), handler, bean);
			mUpgradeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					mUpgradeDialog = null;
				}
			});
			mUpgradeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mUpgradeDialog.show();
		}

	}
    
    private void openUpdateDialogWithTimeout(int tminsecond, Handler handler){
		Log.i(TAG,"openUpdateDialogWithTimeout mUpdateDialog:"+mUpdateDialog);
		
		if (mUpdateDialog == null){
			mUpdateDialog = new UpdateDialog(this, tminsecond, handler);
			mUpdateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					
					mUpdateDialog = null;
				}
			});
			mUpdateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mUpdateDialog.show();
		}

	}
    
    private void sendMessageToOtherThread(int whatMessage){
    	Handler handler = mOtherThread.getHandler();
    	if (handler != null){
    		handler.sendEmptyMessage(whatMessage);
    	}
    }
    
    private void removeMessageInOtherThread(int whatMessage){
    	Handler handler = mOtherThread.getHandler();
    	if (handler != null){
    		handler.removeMessages(whatMessage);
    	}
    }
    
    private class OtherThread extends Thread{
    	private OtherHandler mHandler;
    	private Looper mLooper;
    	
    	@Override
    	public void run() {
    		Looper.prepare();   		
    		synchronized (this) {
    			mLooper = Looper.myLooper();
    			mHandler = new OtherHandler();
    			notifyAll();
			}
    		
    		Looper.loop();
    	}
    	
    	public Looper getLooper(){
    		if (!isAlive()){
    			return null;
    		}
    		
    		synchronized (this) {
				while (isAlive() && (mLooper == null)){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
    		
    		return mLooper;
    	}
    	
    	public Handler getHandler(){
    		if (!isAlive()){
    			return null;
    		}
    		
    		synchronized (this) {
				while (isAlive() && (mHandler == null)){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
    		
    		return mHandler;
    	}
    }
    
    private class OtherHandler extends Handler{
    	
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what){
    		case DO_DETECT:
    			doUpgradeInTask();
    			break;
            case DO_UPDATE:    //更新镜像操作
    			int updateNow = 1;
    			updateNow = msg.arg1;
    			Log.d(TAG,"DO_UPDATE updateNow:"+updateNow);
            	boolean ret = mUpgradeBiz.doVerify();
    			if (ret){
    				int returnret = 0;
    				if (updateNow == 1){
    					returnret = mUpgradeBiz.doUpdate(true);
    				}else{
    					returnret = mUpgradeBiz.doUpdate(false);
    				}
    				
    				if (returnret != 0){
    					handler.sendEmptyMessage(UPDATE_FAILURE);
    				}
    			}else{
    				handler.sendEmptyMessage(VERIFY_FAILURE);
    			}
            	break;
            case DOWNLOAD_SUCCESS:
            	mUpgradeBiz.downLoadSuccessDo(); //下载成功提交服务器操作
            	break;
            case DOWNLOAD_FAILURE: //下载失败提交服务器操作
            	mUpgradeBiz.downLoadFailDo();
            	break;
            case DOWNLOAD_CANCLE:  //取消下载提交服务器操作
            	mUpgradeBiz.doOperate("download", "CANCEL");
            	break;
    		default:
    			break;
    		}
    	}
    }
    
	private boolean getAutoCheckFlag(){
		boolean autoCheck = true;
		
		try{
	        SharedPreferences sp = getSharedPreferences(
	                "AutoCheck", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                + Context.MODE_MULTI_PROCESS);
	        autoCheck = sp.getBoolean("AutoCheckFlag", true);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Log.d(TAG, "getAutoCheckFlag autoCheck:"+autoCheck);
        return autoCheck;
	}
}
