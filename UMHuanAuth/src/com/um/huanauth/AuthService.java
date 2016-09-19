package com.um.huanauth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.huanauth.net.AuthBiz;
import com.um.huanauth.net.HttpUtils;
import com.um.huanauth.net.HttpWorkTask;
import com.um.huanauth.net.UpgradeBiz;
import com.um.huanauth.provider.HuanAuthInfoBean;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AuthService extends Service {
	private static final String TAG = "AuthService";
	private static final int DO_AUTH = 1;
	private static final int DO_START_UPGRADE_SERVICE = 2;
	private static final int AUTH_MAX_DELAY_TIME = 1000 * 60;
	private ConnectivityManager mConnectivityManager;
	private static boolean mAuthfinish = false;
	private static int mAuthDelayTime = 100;
	private Context mContext;
	private OtherThread mOtherThread;
	private AuthBiz mAuthBiz;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		
		mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		mAuthBiz = new AuthBiz(this);
		mOtherThread = new OtherThread();
		mOtherThread.start();
		
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
        	if (networkInfo.isAvailable() && networkInfo.isConnected()) {
        		if (mAuthfinish == false){
        			Log.i(TAG, "onCreate doHuanAuth!");
        			sendMessageToOtherThread(DO_AUTH);
        		}
        	}
        }
        
		registerNetBroadcastReceiver();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//for test
		//sendMessageToOtherThread(DO_AUTH);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterNetBroadcastReceiver();
	}
	
	public BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null){
	        	if (networkInfo.isAvailable() && networkInfo.isConnected()) {
	        		if (mAuthfinish == false){
	        			Log.i(TAG, "netReceiver doHuanAuth!");
						mAuthDelayTime = 100;
						sendMessageToOtherThread(DO_AUTH);
	        		}
	        	}
            }
        }
    };
    
    private void registerNetBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(
        		ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }
    
    private void unregisterNetBroadcastReceiver(){
    	unregisterReceiver(netReceiver);
    }
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    		case DO_START_UPGRADE_SERVICE:
    			UpgradeService.mServiceState = UpgradeService.UpgradeService_AUTODETECT;
    			Intent serviceIntent = new Intent();
    			serviceIntent.setClass(mContext, UpgradeService.class);
    			mContext.startService(serviceIntent);
    			Log.d(TAG,"DO_START_UPGRADE_SERVICE");
    			break;
    		default:
    			break;
    		}
    	}
    };
    
    private void doHuanAuth(){
    	int resultDeviceActive = -1, resultDeviceLogin = -1, resultAutoLoginUser = -1;
    	
    	if (mAuthBiz.getDeviceActiveFlag() == false){
    		resultDeviceActive = mAuthBiz.deviceActive();
    	}else{
    		resultDeviceActive = 0;
    	}
    	
    	if (resultDeviceActive == 0){
    		resultDeviceLogin = mAuthBiz.deviceLogin();
    		if (resultDeviceLogin == 0){
    			resultAutoLoginUser = mAuthBiz.autoLoginUser();
    		}
    	}
    	
    	Log.d(TAG,"resultAutoLoginUser:"+resultAutoLoginUser);
    	if (resultAutoLoginUser == 0){
    		mAuthfinish = true;
    		mHandler.sendEmptyMessage(DO_START_UPGRADE_SERVICE);
    		Looper.myLooper().quitSafely();
    		mOtherThread = null;
    	}else{
    		if (mAuthDelayTime < AUTH_MAX_DELAY_TIME){
    			sendMessageDelayedToOtherThread(DO_AUTH, mAuthDelayTime);
    			mAuthDelayTime = mAuthDelayTime << 1;
    			Log.d(TAG, "mAuthDelayTime:"+mAuthDelayTime);
    		}else{
    			Log.d(TAG, "mAuthDelayTime:"+mAuthDelayTime);
    			sendMessageDelayedToOtherThread(DO_AUTH, mAuthDelayTime);
    		}
    	}
    }
	
    private void sendMessageToOtherThread(int what){
    	Handler handler = mOtherThread.getHandler();
    	if (handler != null){
    		handler.sendEmptyMessage(what);
    	}
    }
    
    private void sendMessageDelayedToOtherThread(int what, long time){
    	Handler handler = mOtherThread.getHandler();
    	if (handler != null){
    		handler.sendEmptyMessageDelayed(what, time);
    	}
    }
    
    private class OtherThread extends Thread{
    	private OtherHandler mHandler;
    	
    	@Override
    	public void run() {
    		Looper.prepare();
    		synchronized (this) {
    			mHandler = new OtherHandler();
    			notifyAll();
			}
    		
    		Looper.loop();
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
    	int ret = 0;
    	
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what){
    		case DO_AUTH:
    			if (!mAuthfinish){
        			doHuanAuth();
    			}
    			break;
    		default:
    			break;
    		}
    	}
    }
}
