package com.um.networkstatus;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.net.ethernet.EthernetManager;
import android.os.SystemProperties;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class NetworkStatusRecevier extends BroadcastReceiver {
	
	private static String TAG = "NetworkStatusRecevier";
	public static final int EVENT_DHCP_CONNECT_FAILED           = 1;
	private static Handler mHandler;
	private static ImageView mImageView;
	private static int mNetStatus = -1;
	private String action;
	private Runnable netCheck;
	private Context context;
	private String strTypeCode;
	private String strAction;

	static {
		mHandler = new Handler() {
			public void handleMessage(Message paramMessage) {
				switch (paramMessage.what) {
				default:
					return;
				case 0:
					Log.d(TAG, "setImageResource(0);");
					NetworkStatusRecevier.mImageView.setImageResource(0);
				}
			}
		};
	}

	private void changeStatus(boolean paramBoolean, boolean isSwitchToMediaSource) {
		Log.i(TAG, "mNetStatus="+mNetStatus+" paramBoolean="+paramBoolean);
		if(!isSwitchToMediaSource){
			if (((mNetStatus == 1) && (paramBoolean))
					|| ((mNetStatus == 0) && (!paramBoolean)))
				return;
		}
		mHandler.removeMessages(0);
		if (paramBoolean) {
			mImageView.setImageResource(R.drawable.connect);
			mHandler.sendEmptyMessageDelayed(0, 5000L);
			mNetStatus = 1;
			return;
		}
		mImageView.setImageResource(R.drawable.discon);
		mNetStatus = 0;
	}


	private String getSSID(Context paramContext) {
		String str = ((WifiManager) paramContext.getSystemService("wifi"))
				.getConnectionInfo().getSSID();
		Log.d(TAG, "-----------ssid-------------" + str);
		return str;
	}

	private void initWindow(Context paramContext) {
		Log.d(TAG, "-----initWindow-----");
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.toast, null);
		mImageView = (ImageView) localView.findViewById(R.id.image);
		WindowManager localWindowManager = (WindowManager) paramContext
				.getApplicationContext().getSystemService("window");
		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = 2002;
		localLayoutParams.format = 1;
		localLayoutParams.format = 1;
		localLayoutParams.flags = 56;
		localLayoutParams.gravity = 85;
		localLayoutParams.x = 0;
		localLayoutParams.y = 0;
		localLayoutParams.width = 200;
		localLayoutParams.height = 150;
		localWindowManager.addView(localView, localLayoutParams);
	}

	public void onReceive(Context paramContext, Intent paramIntent) {
		this.action = paramIntent.getAction();
		Log.d(TAG, "action=" + this.action);
		if (mImageView == null){
			Log.d(TAG, "Going to initialize window.");
			initWindow(paramContext);
		}
		context=paramContext;
		
		if(action.equals("com.um.sourcechanged")){
			switchSource(paramIntent.getIntExtra("source_id",
					EnumSourceIndex.SOURCE_MEDIA));
			return;
		}
		
		isConnected(false);
		if(action.equals("android.intent.action.BOOT_COMPLETED")){
			Log.i(TAG, "BOOT_COMPLETED check network loop");
			netCheck = new Runnable() {
	            @Override
	            public void run() {
	                if (!isConnected(false)) {
	                	mHandler.postDelayed(netCheck, 1000);
	                }
	            }
	        };

	        mHandler.post(netCheck);
		}else {
			if(action.equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)){ 
				Log.i(TAG, "brocast---ETHERNET_STATE_CHANGED_ACTION");
				int state = paramIntent.getExtras().getInt(EthernetManager.EXTRA_ETHERNET_STATE);
//				if (state == EthernetManager.EVENT_DHCP_CONNECT_FAILED) {
             	if (state == EVENT_DHCP_CONNECT_FAILED) {               
					strTypeCode = "10010";
					strAction=DataManager.REPORT_ACTION;
					Log.i(TAG, "brocast---10010");
				} else{
					 if (!isConnected(false)) {
							strTypeCode = "10000";
							strAction=DataManager.REPORT_ACTION;
							Log.i(TAG, "brocast---10000");
						} else{
							strTypeCode =null;
							strAction=DataManager.CLOSE_ACTION;
						}
				}
			}else {
				if(!isConnected(false)){
					Log.i(TAG, "else brocast");
					strTypeCode="10000";
					strAction=DataManager.REPORT_ACTION;
				}else{
					strTypeCode =null;
					strAction=DataManager.CLOSE_ACTION;
				}
			}
			sendBroadcast(strTypeCode,strAction);
		}

	}

	private void sendBroadcast(String code_type,String strAction) {
		Intent intent = new Intent();
		if(!TextUtils.isEmpty(code_type)&&null!=code_type){
			intent.putExtra("code_type", code_type);
		}
		intent.setAction(strAction);
		context.sendBroadcast(intent);
	}	
	
	private void switchSource(int id){
		if(id == EnumSourceIndex.SOURCE_MEDIA){
    		Log.d(TAG, "current is media_source.");
    		netStatusIconShowOrHide(true);
    	}else {
    		Log.d(TAG, "current is not media_source.");
    		netStatusIconShowOrHide(false);
    	}
	}

	
	private void netStatusIconShowOrHide(boolean isShow){
		if(mImageView == null){
			Log.d(TAG, "Initialize view in iconshoworhide method.");
			initWindow(context);
		}
		
		if(isShow){
			isConnected(true);
			Log.d(TAG, "icon shown");
		}else {
			mHandler.sendEmptyMessage(0);
			Log.d(TAG, "Icon hidden.");
		}
	}// netStatusIconShowOrHide  --  end.
	
	private boolean isConnected(boolean isSwitchToMedia){
		try {
			ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
			NetworkInfo localNetworkInfo = localConnectivityManager
					.getActiveNetworkInfo();
			Log.d(TAG, "action=" + this.action + ",ConnectivityManager="
					+ localConnectivityManager.getClass().getName()
					+ ",networkInfo=" + localNetworkInfo);
			if ((localNetworkInfo != null) && (localNetworkInfo.isConnected())
					&& (localNetworkInfo.isAvailable())) {
				Log.i(TAG, "changeStatus(true)");
				changeStatus(true, isSwitchToMedia);
				return true;
			}
			Log.i(TAG, "changeStatus(false)");
			changeStatus(false, isSwitchToMedia);
			return false;
		} catch (Exception localException) {
			localException.printStackTrace();
			return false;
		}
	}
}
