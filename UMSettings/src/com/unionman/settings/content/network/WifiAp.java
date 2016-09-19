package com.unionman.settings.content;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;

import java.lang.reflect.Method;


public class WifiAp extends RightWindowBase {
	public WifiAp(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
	}

	private WifiManager wifiManager;
	private Button btn_save;
	private Button btn_cancel;
	private EditText et_Ssid;
	private EditText et_Password;
	private Spinner sp_Security;
	private Spinner sp_Channel;
	public static final int OPEN_INDEX = 0;
	public static final int WPA_INDEX = 1;
	public static final int WPA2_INDEX = 2;
	private static final String TAG="com.unionman.settings.content.network--WifiAp--";

	public void initData() {
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration apConfig =wifiManager.getWifiApConfiguration();
		if(apConfig!=null){
			et_Ssid.setText(apConfig.SSID);
			et_Password.setText(apConfig.preSharedKey);
			sp_Channel.setSelection(apConfig.channel-1);
			sp_Security.setSelection(getSecurityTypeIndex(apConfig));
		}
	}


	public static int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		Logger.i(TAG,"getSecurityTypeIndex()--");
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return WPA_INDEX;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return WPA2_INDEX;
		}
		return OPEN_INDEX;
	}

	public void setId() {
		this.frameId = ConstantList.FRAME_WIFI_AP;
		this.levelId = 1002;
		Logger.i(TAG, "setId()--");
	}

	public void sleep_1s(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.wifi_ap, this);
		this.btn_save=(Button)findViewById(R.id.btn_ok);
		this.btn_cancel=(Button)findViewById(R.id.btn_cancel);
		this.et_Ssid = ((EditText) findViewById(R.id.et_ssid));
		this.et_Ssid.setFocusable(true);
		this.et_Ssid.requestFocus();
		this.et_Password = ((EditText) findViewById(R.id.et_pwd));
		this.sp_Security=((Spinner) findViewById(R.id.sp_security));
		this.sp_Channel=((Spinner) findViewById(R.id.sp_channel));

		String[] security = getResources().getStringArray(R.array.security);
		String[] channel = getResources().getStringArray(R.array.channel);
		ArrayAdapter securityAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_item, security);
		ArrayAdapter channelAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_item, channel);
		this.sp_Security.setAdapter(securityAdapter);
		this.sp_Channel.setAdapter(channelAdapter);
		this.btn_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View paramAnonymousView) {
				// TODO Auto-generated method stub

				WifiConfiguration wificonfig = new WifiConfiguration();
				//热点的名称
				wificonfig.SSID = et_Ssid.getText().toString().trim();
				//热点的密码
				wificonfig.preSharedKey=et_Password.getText().toString().trim();
				//信道
				wificonfig.channel=Integer.valueOf(sp_Channel.getSelectedItem().toString());
				//安全性
				switch (sp_Security.getSelectedItemPosition()) {
					case OPEN_INDEX:
						wificonfig.allowedKeyManagement.set(KeyMgmt.NONE);
						break;

					case WPA_INDEX:
						wificonfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
						wificonfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
						if (et_Password.length() != 0) {
							String password = et_Password.getText().toString();
							wificonfig.preSharedKey = password;
						}
						break;

					case WPA2_INDEX:
						wificonfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
						wificonfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
						if (et_Password.length() != 0) {
							String password = et_Password.getText().toString();
							wificonfig.preSharedKey = password;
						}
						break;

				}
				wifiManager.setWifiApEnabled(wificonfig, false);
				if(et_Password.getText().toString().length() < 8){
					ToastUtil.showToast(context, WifiAp.this.getResources().getString(R.string.ap_password_input_tips));
					wifiManager.setWifiApEnabled(wificonfig, false);
				}else {
					wificonfig.preSharedKey=et_Password.getText().toString().trim();
					wifiManager.setWifiApEnabled(wificonfig, true);
					ToastUtil.showToast(context,  WifiAp.this.getResources().getString(R.string.ap_saving));
					sleep_1s();
					ToastUtil.showToast(context, WifiAp.this.getResources().getString(R.string.ap_save_sucess));

				}

			}
		});

		this.btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				WifiAp.this.layoutManager.backShowView();
			}
		});
	}


}
