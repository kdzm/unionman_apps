package com.unionman.settings.content;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.unionman.settings.R;
import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;

public class FrameNetworkOneKey extends RightWindowBase {
	private Button btnStop;
	private ImageView ivIP;
	private ImageView ivGateway;
	private ImageView ivDns;
	private ProgressBar pbProgress;
	private TextView tvState;
	private Handler mHandler;
	private final static int IP_SUCCESS=0;
	private final static int GATEWAY_SUCCESS=1;
	private final static int DNS_SUCCESS=2;
	private final static int IP_FAIL=3;
	private final static int GATEWAY_FAIL=4;
	private final static int DNS_FAIL=5;
	
	private final static String TAG = "UMSettings--FrameNetworkOneKey";

	private WifiManager wifiManager;
	private PppoeManager mPppoeManager;
	private EthernetManager mEthernetManager;

	public FrameNetworkOneKey(Context paramContext) {
		super(paramContext);
		wifiManager = ((WifiManager) paramContext.getSystemService("wifi"));
		mPppoeManager = ((PppoeManager) paramContext.getSystemService("pppoe"));
		mEthernetManager = ((EthernetManager) paramContext.getSystemService("ethernet"));
	}


	public void initData() {
		Log.i(TAG,"initData");
	}

	public void onInvisible() {
		Log.i(TAG,"onInvisible");
	}

	public void onResume() {
		Log.i(TAG,"onResume");
	}

	public void setId() {
		Log.i(TAG,"setId");
		frameId = ConstantList.NET_CHECK;
		levelId = 1002;
	}

	public void setView() {
		Log.i(TAG,"setView");
		layoutInflater.inflate(R.layout.network_check_onekey, this);
		btnStop = ((Button) findViewById(R.id.btn_stop));
		ivIP=(ImageView)findViewById(R.id.iv_ip);
		ivGateway=(ImageView)findViewById(R.id.iv_gateway);
		ivDns=(ImageView)findViewById(R.id.iv_dns);
		pbProgress=(ProgressBar)findViewById(R.id.pb_progress);
		tvState=(TextView)findViewById(R.id.tv_state);
		pbProgress.setVisibility(View.VISIBLE);
		tvState.setText(R.string.onekey_testing);
		hideFlag();
		mHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case IP_SUCCESS:
					ivIP.setVisibility(View.VISIBLE);
					ivIP.setImageDrawable(getResources().getDrawable(R.drawable.tui_ic_checkmark));
					break;
				case IP_FAIL:
					ivIP.setVisibility(View.VISIBLE);
					ivGateway.setVisibility(View.VISIBLE);
					ivDns.setVisibility(View.VISIBLE);
					ivIP.setImageDrawable(getResources().getDrawable(R.drawable.nopass));
					ivGateway.setImageDrawable(getResources().getDrawable(R.drawable.nopass));
					ivDns.setImageDrawable(getResources().getDrawable(R.drawable.nopass));
					onReCheck();
					isDone();
					break;
				case GATEWAY_SUCCESS:
					ivGateway.setVisibility(View.VISIBLE);
					ivGateway.setImageDrawable(getResources().getDrawable(R.drawable.tui_ic_checkmark));
					break;
				case GATEWAY_FAIL:
					ivGateway.setVisibility(View.VISIBLE);
					ivGateway.setImageDrawable(getResources().getDrawable(R.drawable.nopass));
					break;
				case DNS_SUCCESS:
					ivDns.setVisibility(View.VISIBLE);
					ivDns.setImageDrawable(getResources().getDrawable(R.drawable.tui_ic_checkmark));
					onReCheck();
					isDone();
					break;
				case DNS_FAIL:
					ivDns.setVisibility(View.VISIBLE);
					ivDns.setImageDrawable(getResources().getDrawable(R.drawable.nopass));
					onReCheck();
					isDone();
					break;

				}
			};
		};
		btnStop.requestFocus();
		onStop();
		check();
	}
	
	private void check(){
		Log.i(TAG,"check");
		new Thread(){
			public void run() {
				Log.i(TAG,"run");
				if(isNetworkConnected(context)){
					mHandler.sendEmptyMessageDelayed(IP_SUCCESS, 1000);
					if(isGatewayConnectable()){
						mHandler.sendEmptyMessageDelayed(GATEWAY_SUCCESS,2000);
					}else{
						mHandler.sendEmptyMessageDelayed(GATEWAY_FAIL, 2000);
					}
					if(isDnsConnectable()){
						mHandler.sendEmptyMessageDelayed(DNS_SUCCESS, 3000);
					}else{
						mHandler.sendEmptyMessageDelayed(DNS_FAIL, 3000);
					}
					
				}else{
					mHandler.sendEmptyMessageDelayed(IP_FAIL, 1000);
				}
			};
		}.start();
	}
	
	private void isDone(){
		pbProgress.setVisibility(View.GONE);
		tvState.setText(R.string.onekey_test_success);
	}
	
	private boolean isGatewayConnectable(){
		String gateway1=null;
		String gateway2=null;
		String gateway3=null;
		boolean result=false;
		
		DhcpInfo localDhcpInfo1 = this.wifiManager.getDhcpInfo();
		DhcpInfo localDhcpInfo2 = this.mPppoeManager.getDhcpInfo();
		DhcpInfo localDhcpInfo3 = this.mEthernetManager.getDhcpInfo();
		
		if(localDhcpInfo1!=null){
			gateway1=NetworkUtils.intToInetAddress(localDhcpInfo1.gateway).getHostAddress();
			if(gateway1!=null){
				try {
					InetAddress address = InetAddress.getByName(gateway1);
					if(address.isReachable(3000)){
						result=true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		if(localDhcpInfo2!=null){
			gateway2=NetworkUtils.intToInetAddress(localDhcpInfo2.gateway).getHostAddress();
			if(gateway1!=null){
				try {
					InetAddress address = InetAddress.getByName(gateway2);
					if(address.isReachable(3000)){
						result=true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(localDhcpInfo3!=null){
			gateway3=NetworkUtils.intToInetAddress(localDhcpInfo3.gateway).getHostAddress();
			if(gateway1!=null){
				try {
					InetAddress address = InetAddress.getByName(gateway3);
					if(address.isReachable(3000)){
						result=true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
		
	}
	
	private boolean isDnsConnectable(){
		 String ip=null;
		 InetAddress  myServer=null;
		  try {
			  myServer = InetAddress.getByName("www.baidu.com");
		  } catch (UnknownHostException e) {
			  e.printStackTrace();
		  }
		  if(myServer!=null){
			 ip=myServer.getHostAddress();
		  }
		  
		  if(ip==null||ip.equals("")){
			  return false;
		  }else{
			  return true;
		  }
	}
	
	public boolean isNetworkConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
			.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null) { 
				return mNetworkInfo.isConnected(); 
			} 
		} 
		return false; 
	} 
	
	private void onStop(){
		btnStop.setText(R.string.onekey_stop);
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"onStop onClick");
				try{
					layoutManager.showLayout(6);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private void onReCheck(){
		btnStop.setText(R.string.onekey_return);
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"onReCheck onClick");
				try{
					layoutManager.showLayout(6);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void hideFlag(){
		ivIP.setVisibility(View.INVISIBLE);
		ivGateway.setVisibility(View.INVISIBLE);
		ivDns.setVisibility(View.INVISIBLE);
	}

}
