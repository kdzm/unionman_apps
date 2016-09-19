package com.um.huanauth;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.um.huanauth.UpgradeService.DownloadChangeObserver;
import com.um.huanauth.data.HuanClientAuth;
import com.um.huanauth.net.DownloadManagerPro;
import com.um.huanauth.net.UpgradeBiz;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusFactory;

public class NetUpgradeActivity extends Activity {
	private static final String TAG = "NetUpgradeActivity";
	public static final int ACTIVITY_FINISH = 1;
	private static final int DOWNLOAD_STATUS = 0;
	public static final long DISPEAR_TIME_30s = 30000;
	private Button mButton;
	private Switch mSwitch;
	private TextView mAppid;
	private TextView mSoftVersion;
	private TextView mDeviceTyte;
	private TextView mDeviceId;
	private TextView mDnum;
	private TextView mProjectId;
    private TextView mProgressText;
    private TextView mStatusText;
	private MyHandler handler;
    private DownloadManager mDownloadManager;
    private DownloadManagerPro mDownloadManagerPro;
    private DownloadChangeObserver downloadObserver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.net_upgrade);
        mSwitch = (Switch)findViewById(R.id.auto_switch);
        mButton = (Button)findViewById(R.id.manual_button);
        mAppid = (TextView)findViewById(R.id.app_id);
        mProjectId = (TextView)findViewById(R.id.project_id);
        mSoftVersion = (TextView)findViewById(R.id.software_number_version);
        mDeviceTyte = (TextView)findViewById(R.id.device_type);
        mDeviceId = (TextView)findViewById(R.id.device_id);
        mDnum = (TextView)findViewById(R.id.dnum);
        mProgressText = (TextView)findViewById(R.id.tv_progress);
        mStatusText = (TextView)findViewById(R.id.tv_status);
        
        handler = new MyHandler(); //����UI��ʾ
        downloadObserver = new DownloadChangeObserver();
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
        mDownloadManagerPro = new DownloadManagerPro(new DownloadManager(getContentResolver(), getPackageName()));
        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
        initData();
        init();
	}
	
	private void initData(){
		String softVersion = "";
		String appId = "";
		String deviceType = "";
		String deviceId = "";
		String dnum = "";
		String projectId = "";
		
		softVersion = SystemProperties.get("ro.umtv.sw.version","");
		if (softVersion.length() > 15)
		{
			appId = softVersion.substring(0, 15);
		}
		
		HuanClientAuth huanClientAuth = new HuanClientAuth(this);
		deviceType = huanClientAuth.getDevicemode();
		deviceId = huanClientAuth.getDeviceid();
		dnum = huanClientAuth.getDnum();
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		projectId = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);
		if (projectId == null){
			projectId = "";
		}
		mAppid.setText(appId);
		mProjectId.setText(projectId);
		mSoftVersion.setText(softVersion);
		mDeviceTyte.setText(deviceType);
		mDeviceId.setText(deviceId);
		mDnum.setText(dnum);
		
		updateView();
	}
	
	private void init(){
		 boolean checked = true;
		 
		 checked = getAutoCheckFlag();
		 mSwitch.setChecked(checked);
		 mSwitch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					delay();
				}
			});
	        
	        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					delay();
					saveAutoCheckFlag(arg1);
				}
			});
	        
	        mButton.setOnFocusChangeListener(new View.OnFocusChangeListener(){
	        	@Override
	        	public void onFocusChange(View arg0, boolean arg1) {
	        		delay();
	        	}
	        });
	        
	        mButton.setOnClickListener(new View.OnClickListener(){
	        	@Override
	        	public void onClick(View arg0) {
	        		delay();
	        		Intent service = new Intent("com.um.huanauth.UpgradeService.ACTION");
	        		startService(service);
	        	}
	        });
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i(TAG, "onWindowFocusChanged hasFocus=" + hasFocus);
		if (hasFocus) {
			delay();
		} else {
			handler.removeMessages(ACTIVITY_FINISH);
		}
		super.onWindowFocusChanged(hasFocus);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		getContentResolver().unregisterContentObserver(downloadObserver);
		downloadObserver = null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode){
			case KeyEvent.KEYCODE_0:
				//mDownloadManager.remove(UpgradeBiz.getDownLoadId());
			break;
		}
		return super.onKeyDown(keyCode, event);
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
    	Log.d(TAG,"updateView mUpgradeBiz.getCurDownLoadId():"+UpgradeBiz.getDownLoadId());
        
    	if (UpgradeBiz.getDownLoadId() == -1){
    		return;
    	}
    	
    	try{
    		int[] bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(UpgradeBiz.getDownLoadId());
            handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
            
            Log.d(TAG,"updateView bytesAndStatus[0]:"+bytesAndStatus[0]+";bytesAndStatus[1]:"+bytesAndStatus[1]+";bytesAndStatus[2]:"+bytesAndStatus[2]);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DOWNLOAD_STATUS:
                    int status = (Integer)msg.obj;
                    Log.d(TAG,"handleMessage status:"+status);
                    if (status != 0) {
                        String str = getResources().getString(getStatusStringId(status));
                        mStatusText.setText(str);
                    }

                    if (isDownloading(status)) {
                        int cur = msg.arg1;
                        int total = msg.arg2;
                        mProgressText.setText(getNotiPercent(cur, total));
                        Log.d(TAG, "download status:" + status + ";cur:" + cur + ";total:" + total);
                    }else if (status == DownloadManager.STATUS_SUCCESSFUL){
                    	mProgressText.setText(getNotiPercent(100, 100));
                    }
                    break;
                case ACTIVITY_FINISH:
                	finish();
                	break;
                default:
                	break;
            }
        }
    }
    
    public static String getNotiPercent(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int)((double)progress / max * 100);
        }
        return new StringBuilder(16).append(rate).append("%").toString();
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
    
	/**
	 * set delay time to finish activity
	 */
	public void delay() {
		Log.i(TAG, "delay() is calling");
		handler.removeMessages(ACTIVITY_FINISH);
		Message message = new Message();
		message.what = ACTIVITY_FINISH;
		handler.sendMessageDelayed(message, DISPEAR_TIME_30s);
	}
	
	private void saveAutoCheckFlag(boolean autoCheck){
		 Editor sharedata = getSharedPreferences("AutoCheck", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS).edit();  
	      sharedata.putBoolean("AutoCheckFlag",autoCheck); 
	      sharedata.commit(); 
	}
	
	private boolean getAutoCheckFlag(){
        SharedPreferences sp = getSharedPreferences(
                "AutoCheck", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS);
        boolean autoCheck = sp.getBoolean("AutoCheckFlag", true);
        
        return autoCheck;
	}
}
