package com.unionman.netsetup.view.setting;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.unionman.netsetup.R;

public class CreateAPView extends FrameLayout implements OnClickListener {

	private Context context;
	private View motherView;
	private NetSettingDialog dialog;
	private WifiManager wifiManager;
	
	private Button btn_save;
	private Button btn_cancel;
	private Button btn_close;
	private EditText et_Ssid;
	private EditText et_Password;
	private Spinner sp_Security;
	private Spinner sp_Channel;
	
	public CreateAPView(Context context, NetSettingDialog n) {
		super(context);
		this.context = context;
		dialog = n;
		initViews();
		setup();
		addView(motherView);
	}

	private void initViews() {
		motherView = LayoutInflater.from(context).inflate(R.layout.wifi_ap, null);
		this.btn_save=(Button)motherView.findViewById(R.id.btn_ok);
		this.btn_cancel=(Button)motherView.findViewById(R.id.btn_cancel);
		btn_close = (Button)motherView.findViewById(R.id.btn_close);
		this.et_Ssid = ((EditText) motherView.findViewById(R.id.et_ssid));
		this.et_Ssid.setFocusable(true);
		this.et_Ssid.requestFocus();
		this.et_Password = ((EditText) motherView.findViewById(R.id.et_pwd));
		this.sp_Security=((Spinner) motherView.findViewById(R.id.sp_security));
		this.sp_Channel=((Spinner) motherView.findViewById(R.id.sp_channel));
		
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}
	
	private void setup(){
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		
		//setup spinner.
		String[] security = getResources().getStringArray(R.array.security);
		ArrayAdapter<String> securityAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_item, security);
		sp_Security.setAdapter(securityAdapter);
		
		String[] channel = getResources().getStringArray(R.array.channel);
		ArrayAdapter<String> channelAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_item, channel);
		sp_Channel.setAdapter(channelAdapter);
		
		//setup click listener.
		btn_save.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_close.setOnClickListener(this);
	}
	
	private void openAP(){
		WifiConfiguration wificonfig = new WifiConfiguration();
		//热点的名称
		wificonfig.SSID = et_Ssid.getText().toString().trim();
		//热点的密码
		wificonfig.preSharedKey=et_Password.getText().toString().trim();
		//信道
		wificonfig.channel=Integer.valueOf(sp_Channel.getSelectedItem().toString());
		//安全性
		switch (sp_Security.getSelectedItemPosition()) {
			case 0:
				wificonfig.allowedKeyManagement.set(KeyMgmt.NONE);
				break;

			case 1:
				wificonfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
				wificonfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
				if (et_Password.length() != 0) {
					String password = et_Password.getText().toString();
					wificonfig.preSharedKey = password;
				}
				break;

			case 2:
				wificonfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
				wificonfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
				if (et_Password.length() != 0) {
					String password = et_Password.getText().toString();
					wificonfig.preSharedKey = password;
				}
				break;

		}
		
		//step 1.如果开启了WIFI，则将它关闭。
		if (wifiManager.isWifiEnabled()) { 
			wifiManager.setWifiEnabled(false);
		} 
		//step 2.判断当前热点是否已打开。
		if(isAPEnable()){
			return;
		}
		
		if(et_Password.getText().toString().length() < 8){
			Toast.makeText(context, "The psw is too short.", Toast.LENGTH_SHORT).show();
			wifiManager.setWifiApEnabled(wificonfig, false);
		}else {
			wificonfig.preSharedKey=et_Password.getText().toString().trim();
			wifiManager.setWifiApEnabled(wificonfig, true);
			Toast.makeText(context, "create success.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean isAPEnable(){
		try {    
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");    
            method.setAccessible(true);    
            return (Boolean) method.invoke(wifiManager);    
        } catch (NoSuchMethodException e) {    
            e.printStackTrace();    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return false;
	}
	
	private void closeAP(){
		Log.d("yyf", "closing AP...");
		if(isAPEnable()){
			try {    
	            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");    
	            method.setAccessible(true);    
	            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);    
	            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled",
	            		WifiConfiguration.class, boolean.class);    
	            method2.invoke(wifiManager, config, false);    
	        } catch (NoSuchMethodException e) {    
	            e.printStackTrace();    
	        } catch (IllegalArgumentException e) {    
	            e.printStackTrace();    
	        } catch (IllegalAccessException e) {    
	            e.printStackTrace();    
	        } catch (InvocationTargetException e) {    
	            e.printStackTrace();    
	        }  
			Toast.makeText(context, "hotspot closed.", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "hotspot already closed.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_ok:{
				openAP();
			}break;
			case R.id.btn_close:{
				closeAP();
			}break;
			case R.id.btn_cancel:{
				dialog.onBackPressed();
			}break;
		}// switch  --  end.
	}

}
