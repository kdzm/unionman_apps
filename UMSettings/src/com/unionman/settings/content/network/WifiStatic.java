package com.unionman.settings.content;

import com.unionman.settings.R;
import com.unionman.settings.custom.IpAddrEditText;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Logger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.widget.Button;

public class WifiStatic extends RightWindowBase {
	private Button btn_cancel;
	private Button btn_save;
	private IpAddrEditText et_dns;
	private IpAddrEditText et_dns2;
	private IpAddrEditText et_gateway;
	private IpAddrEditText et_ip;
	private IpAddrEditText et_mask;
	private UMLogger log;
	private ConnectivityManager mConnectivityManager;


	private static final String TAG="com.unionman.settings.content.network--WifiStatic--";
	public WifiStatic(Context paramContext) {
		super(paramContext);
	}

	private boolean checkIP(String paramString1, String paramString2,
			String paramString3, String paramString4, String paramString5) {
		return true;
	}

	private void setEditPreview() {
		Logger.i(TAG, "setEditPreview()--");
		int i;
		try {
			i = Settings.Secure.getInt(context.getContentResolver(),"default_wifi_mod");
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if (i == 1) {
			try {
				showIpInfor();
			} catch (SettingNotFoundException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void showIpInfor() throws SettingNotFoundException {
		Logger.i(TAG,"showIpInfor()--");
		String str1 = Settings.Secure.getString(context.getContentResolver(),"wifi_static_ip");
		String str2 = Settings.Secure.getString(context.getContentResolver(),"wifi_static_gateway");
		String str3 = Settings.Secure.getString(context.getContentResolver(),"wifi_static_mask");
		String str4 = Settings.Secure.getString(context.getContentResolver(),"wifi_static_dns");
		String str5 = Settings.Secure.getString(context.getContentResolver(),"wifi_static_dns2");
		IpAddrEditText localIpAddrEditText1 = et_ip;
		if (str1 == null)
			str1 = "0.0.0.0";
		localIpAddrEditText1.setText(str1);
		IpAddrEditText localIpAddrEditText2 = et_gateway;
		if (str2 == null)
			str2 = "0.0.0.0";
		localIpAddrEditText2.setText(str2);
		IpAddrEditText localIpAddrEditText3 = et_mask;
		if (str3 == null)
			str3 = "0.0.0.0";
		localIpAddrEditText3.setText(str3);
		IpAddrEditText localIpAddrEditText4 = et_dns;
		if (str4 == null)
			str4 = "0.0.0.0";
		localIpAddrEditText4.setText(str4);
		IpAddrEditText localIpAddrEditText5 = et_dns2;
		if (str5 == null)
			str5 = "0.0.0.0";
		localIpAddrEditText5.setText(str5);
	}

	private void startStatic() {

		String str1 = et_ip.getText();
		String str2 = et_gateway.getText();
		String str3 = et_mask.getText();
		String str4 = et_dns.getText();
		String str5 = et_dns2.getText();
		if (!checkIP(str1, str2, str3, str4, str5)) {
			return;
		}
		Logger.i(TAG,"startStatic()---");
		Settings.Secure.putInt(context.getContentResolver(),"default_wifi_mod", 1);
		Settings.Secure.putString(context.getContentResolver(),"wifi_static_ip", str1);
		Settings.Secure.putString(context.getContentResolver(),"wifi_static_gateway", str2);
		Settings.Secure.putString(context.getContentResolver(),"wifi_static_mask", str3);
		Settings.Secure.putString(context.getContentResolver(),"wifi_static_dns", str4);
		Settings.Secure.putString(context.getContentResolver(),"wifi_static_dns2", str5);
		Logger.i("str1===" + str1 + "str2===" + str2 + "str3===" + str3+ "str4===" + str4 + "str5===" + str5);
		layoutManager.backShowView();
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
	}

	public void onInvisible() {
		Logger.i(TAG,"onInvisible()--");
	}

	public void onResume() {
		setEditPreview();
		et_ip.requestFocus();
	}

	public void setId() {
		Logger.i(TAG,"setId()---");
		frameId = ConstantList.FRAME_NETWORK_WIFI_SET_STATIC;
		levelId = 1003;
		mConnectivityManager = ((ConnectivityManager) this.context
				.getSystemService("connectivity"));
		log = UMLogger.getLogger(getClass());
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		layoutInflater.inflate(R.layout.wifi_network_static, this);
		et_ip = ((IpAddrEditText) findViewById(R.id.iptxt_wifi_static_ip));
		et_ip.setCanUp(false);
		et_mask = ((IpAddrEditText) findViewById(R.id.iptxt_wifi_static_mask));
		et_gateway = ((IpAddrEditText) findViewById(R.id.iptxt_wifi_static_gateway));
		et_dns = ((IpAddrEditText) findViewById(R.id.iptxt_wifi_static_dns));
		et_dns2 = ((IpAddrEditText) findViewById(R.id.iptxt_wifi_static_dns2));
		btn_save = ((Button) findViewById(R.id.btn_wifi_static_ok));
		btn_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startStatic();
			}
		});
		btn_cancel = ((Button) findViewById(R.id.btn_wifi_static_cancel));
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				layoutManager.backShowView();
			}
		});
	}
}