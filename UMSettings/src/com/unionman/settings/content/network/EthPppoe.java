package com.unionman.settings.content;

import com.unionman.settings.R;
import com.unionman.settings.UMSettings;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.DhcpInfo;

public class EthPppoe extends RightWindowBase {
	private Button btn_cancel;
	private static final String TAG="com.unionman.settings.content.network--EthPppoe--";
	private ConnectivityManager mConnectivityManager;
	View.OnKeyListener mEditKeyListener = new View.OnKeyListener() {
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt,KeyEvent paramAnonymousKeyEvent) {
			if ((paramAnonymousKeyEvent.getAction() == 0)&& (paramAnonymousInt == 4)) {
				if (((EditText) paramAnonymousView).getText().length() <= 0)
					;
				return true;
			}
			if ((paramAnonymousKeyEvent.getAction() == 1)&& (paramAnonymousInt == 4)) {
				StringUtils.delText((EditText) paramAnonymousView);
				return true;
			}
			return false;
		}
	};
	private EthernetManager mEthernetManager;
	private PppoeManager mPppoeManager;
	private Button btn_save;
	private EditText et_pass;
	private EditText et_user;

	public EthPppoe(Context paramContext) {
		super(paramContext);
	}

	private final void sendIntent(int paramInt) {
		Logger.i(TAG,"sendIntent()--");
		Intent localIntent = new Intent("CLEARDHCP");
		localIntent.putExtra("DHCPSTATE", paramInt);
		this.context.sendStickyBroadcast(localIntent);
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		this.et_user.setOnKeyListener(this.mEditKeyListener);
		this.et_pass.setOnKeyListener(this.mEditKeyListener);
		if (Settings.Secure.getString(this.context.getContentResolver(),"pppoe_username") == null)
			Settings.Secure.putString(this.context.getContentResolver(),"pppoe_username", "");
		if (Settings.Secure.getString(this.context.getContentResolver(),"pppoe_pswd") == null)
			Settings.Secure.putString(this.context.getContentResolver(),"pppoe_pswd", "");
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		String username = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_username");
		String password = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_pswd");
		Logger.i(TAG, "username="+username+" passowrd="+password);
		this.et_user.setText(username);
		this.et_pass.setText(password);
		int i = this.mPppoeManager.getPppoeState();
		Logger.i(TAG, "state="+i);
		this.et_user.requestFocus();
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = ConstantList.FRAME_NETWORK_ETH_PPPOE;
		this.levelId = 1003;
		this.mConnectivityManager = ((ConnectivityManager) this.context.getSystemService("connectivity"));
		this.mEthernetManager = ((EthernetManager) this.context.getSystemService("ethernet"));
		this.mPppoeManager = ((PppoeManager) this.context.getSystemService("pppoe"));
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.network_eth_pppoe, this);
		this.et_user = ((EditText) findViewById(R.id.et_eth_pppoe_user));
		this.et_pass = ((EditText) findViewById(R.id.et_eth_pppoe_pswd));
		this.btn_save = ((Button) findViewById(R.id.btn_eth_pppoe_ok));
		this.btn_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				String str1 = EthPppoe.this.et_user.getText().toString().trim();
				String str2 = EthPppoe.this.et_pass.getText().toString().trim();
				if ((!str1.equals("")) && (!str2.equals(""))) {
					Settings.Secure.putInt(EthPppoe.this.context.getContentResolver(),"default_eth_mod", 2);
					Settings.Secure.putString(EthPppoe.this.context.getContentResolver(),"pppoe_username", str1);
					Settings.Secure.putString(EthPppoe.this.context.getContentResolver(),"pppoe_pswd", str2);
					// EthPppoe.this.mPppoeManager.disconnect(EthPppoe.this.mEthernetManager.getInterfaceName());
					new EthPppoe.Conn(EthPppoe.this).start();
					EthPppoe.this.layoutManager.backShowView();
				} else {
					ToastUtil.showToast(EthPppoe.this.context, "用户名密码不能为空");
				}
			}
		});
		this.btn_cancel = ((Button) findViewById(2131099772));
		this.btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				EthPppoe.this.layoutManager.backShowView();
			}
		});
	}

	class Conn extends Thread {
		private EthPppoe mepppoe;

		Conn(EthPppoe epppoe) {
			mepppoe = epppoe;
		}

		public void run() {
			((UMSettings) mepppoe.context.getApplicationContext()).mHandler.sendEmptyMessage(5);
			NetworkInfo localNetworkInfo = mepppoe.mConnectivityManager.getActiveNetworkInfo();
			if ((localNetworkInfo != null) && (localNetworkInfo.isConnected())) {
				if (localNetworkInfo.getType() != 1) {
					Logger.i(TAG, "eth0 is on");
					EthPppoe.this.sendIntent(1);
				}
				Logger.i(TAG, "wifi is on");
				EthPppoe.this.sendIntent(5);
			}
			// EthPppoe.this.mPppoeManager.setPppoeMode("dhcp", new DhcpInfo());
			/*
			 * EthPppoe.this.mPppoeManager.connect(mepppoe.user.getText().toString
			 * (), mepppoe.pass.getText().toString(),
			 * mepppoe.mEthernetManager.getInterfaceName());
			 */
			//EthPppoe.this.mPppoeManager.enablePppoe(true);
			EthPppoe.this.mPppoeManager.setPppoeMode("dhcp", new DhcpInfo());
			EthPppoe.this.mPppoeManager.connect(mepppoe.et_user.getText().toString(), mepppoe.et_pass.getText().toString(),
			mepppoe.mEthernetManager.getInterfaceName());
			((UMSettings) mepppoe.context.getApplicationContext()).mHandler.sendEmptyMessage(5);
			((UMSettings) mepppoe.context.getApplicationContext()).result = String.valueOf(mepppoe.mPppoeManager.getPppoeState());
			((UMSettings) mepppoe.context.getApplicationContext()).mHandler.sendEmptyMessage(1);
		}
	}
}
