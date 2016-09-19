package com.unionman.settings.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;
import com.unionman.settings.wifi.WifiNetworkBean;

public class WifiNetwork extends RightWindowBase implements
		View.OnClickListener {
	private TextView tv_dhcpinfo;
	private int intSelectedId;
	private RadioButton rb_dhcp;
	private RadioButton rb_static;
	private TextView tv_staticinfo;

	private ConnectivityManager mConnectivityManager;
	public static final String TAG="com.unionman.settings.content.network--WifiNetwork--";

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			String str = paramAnonymousIntent.getAction();
			Logger.i(TAG, "action=" + str);
		}
	};

	public WifiNetwork(Context paramContext) {
		super(paramContext);
	}

	private void initView() {
		Logger.i(TAG,"initView()--");
		rb_dhcp.setChecked(false);
		rb_static.setChecked(false);
		tv_dhcpinfo.setVisibility(4);
		tv_staticinfo.setVisibility(4);
		intSelectedId = Settings.Secure.getInt(this.context.getContentResolver(),
				"default_wifi_mod", 0);
		Logger.i(TAG, "get current mod=" + intSelectedId);
		if (intSelectedId == 0) {
			rb_dhcp.setChecked(true);
			rb_dhcp.requestFocus();
		}
		if (intSelectedId == 1) {
			rb_static.setChecked(true);
			rb_static.requestFocus();
		}
	}

	private void registReceiver() {
		Logger.i(TAG,"registReceiver()--");
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGE");
		localIntentFilter.addAction("ETH_STATE");
		if (Contants.SYS_SURPORT_PPPOE)
			localIntentFilter.addAction("PPPOE_STATE_CHANGED");
		context.registerReceiver(mReceiver, localIntentFilter);
	}

	private void unregistReceiver() {
		try {
			context.unregisterReceiver(this.mReceiver);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	private void updateUI() {
		Logger.i(TAG,"updateUI()--");
		WifiNetworkBean mWifiNetworkBean = new WifiNetworkBean();
		if (intSelectedId == 0) {
			tv_dhcpinfo.setText(mWifiNetworkBean.getIp());
		} else {
			tv_staticinfo.setText(mWifiNetworkBean.getIp());
		}
	}

	public void initData() {
	}

	public void onClick(View paramView) {
		Logger.i(TAG,"onClick()--");
		if (paramView == rb_dhcp) {
			Logger.i(TAG, "dhcp radio button checked");
			rb_static.setChecked(false);
			Settings.Secure.putInt(this.context.getContentResolver(),"default_wifi_mod", 0);
			ToastUtil.showToast(this.context, "设置成功");
		}
		if (paramView == rb_static) {
			try {
				layoutManager.showLayout(ConstantList.FRAME_NETWORK_WIFI_SET_STATIC);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
	}

	public void onInvisible() {
		unregistReceiver();
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		initView();
		registReceiver();
		updateUI();
	}

	public void setId() {
		frameId = ConstantList.FRAME_NETWORK_WIFI_SET;
		levelId = 1002;
		mConnectivityManager = ((ConnectivityManager) this.context.getSystemService("connectivity"));
	}

	public void setView() {
		layoutInflater.inflate(R.layout.wifi_network, this);
		rb_dhcp = ((RadioButton) findViewById(R.id.radio_wlan_dhcp));
		rb_static = ((RadioButton) findViewById(R.id.radio_wlan_static));
		tv_dhcpinfo = ((TextView) findViewById(R.id.wlan_dhcp_info));
		tv_staticinfo = ((TextView) findViewById(R.id.wlan_static_info));
		rb_dhcp.setOnClickListener(this);
		rb_static.setOnClickListener(this);
	}
}