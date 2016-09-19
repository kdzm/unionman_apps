package com.unionman.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.content.NetworkActivity;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.tools.Logger;

public class WifiEnabler implements CheckRadioButton.OnCheckedChangeListener {
	private static final String TAG="com.unionman.settings.wifi--WifiEnabler--";
	private final CheckRadioButton belong;
	private final CheckRadioButton mCheckBox;
	private final Context mContext;
	private final IntentFilter mIntentFilter;
	private final String mOriginalSummary;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,
				Intent paramAnonymousIntent) {
			String str = paramAnonymousIntent.getAction();
			Logger.i(TAG, "action="+str);
			if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str)) {
				WifiEnabler.this.handleWifiStateChanged(paramAnonymousIntent.getIntExtra("wifi_state", 4));
			} else {
				if ("android.net.wifi.supplicant.STATE_CHANGE".equals(str)) {
					WifiEnabler.this
					.handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState) paramAnonymousIntent
											.getParcelableExtra("newState")));
					return;
				} else
					WifiEnabler.this
							.handleStateChanged(((NetworkInfo) paramAnonymousIntent
									.getParcelableExtra("networkInfo"))
									.getDetailedState());
			}
		}
	};
	private final WifiManager mWifiManager;
	private CheckRadioButton apCheckBox;

	public WifiEnabler(Context paramContext,
			CheckRadioButton paramCheckRadioButton1,
			CheckRadioButton paramCheckRadioButton2,
			CheckRadioButton paramCheckRadioButton3) {
		this.mContext = paramContext;
		this.mCheckBox = paramCheckRadioButton1;
		this.belong = paramCheckRadioButton2;
		this.apCheckBox = paramCheckRadioButton3;
		this.mOriginalSummary = paramCheckRadioButton1.getText2();
		this.mWifiManager = ((WifiManager) paramContext.getSystemService("wifi"));
		this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
		this.mIntentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
		this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
	}

	private void handleStateChanged(NetworkInfo.DetailedState paramDetailedState) {
		Logger.i(TAG, "handleStateChanged paramDetailedState="+ paramDetailedState);
		if ((paramDetailedState != null) && (this.mCheckBox.isChecked())) {
			WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
			if (localWifiInfo != null) {
				this.mCheckBox.setText2(get(this.mContext,localWifiInfo.getSSID(), paramDetailedState));
				return;
			}
		}
		this.mCheckBox.setText2("");
		return;
	}

	private void handleWifiStateChanged(int paramInt) {
		Logger.i(TAG, "handleWifiStateChanged paramInt=" + paramInt);
		switch (paramInt) {
		default:
			this.mCheckBox.setCheckedState(false);
			this.belong.setViewState(false);
			this.mCheckBox.setChecked(true);
			this.mCheckBox.setText2(this.mContext.getText(R.string.wifi_error).toString());
			this.mCheckBox.setCanchecked(true);
			this.mCheckBox.setViewState(true);
			return;
		case 2:
			this.mCheckBox.setText2(this.mContext.getText(R.string.wifi_starting).toString());
			this.mCheckBox.setCanchecked(false);
			this.mCheckBox.setViewState(true);
			this.mCheckBox.setViewFalse();
			return;
		case 3:
			this.mCheckBox.setCheckedState(true);
			this.belong.setViewState(true);
			this.mCheckBox.setText2("");
			this.mCheckBox.setCanchecked(true);
			this.mCheckBox.setViewState(true);
			return;
		case 0:
			this.mCheckBox.setText2(this.mContext.getText(R.string.wifi_stopping).toString());
			this.mCheckBox.setCanchecked(false);
			this.mCheckBox.setChecked(false);
			this.mCheckBox.setViewFalse();
			return;
		case 1:
			this.mCheckBox.setCheckedState(false);
			this.belong.setViewState(false);
			this.mCheckBox.setText2(this.mOriginalSummary);
			this.mCheckBox.setCanchecked(true);
			this.mCheckBox.setViewState(true);
			return;
		}

	}

	public String get(Context paramContext,
			NetworkInfo.DetailedState paramDetailedState) {
		return get(paramContext, null, paramDetailedState);
	}

	public String get(Context paramContext, String paramString,
			NetworkInfo.DetailedState paramDetailedState) {
		Resources localResources = paramContext.getResources();
		if (paramString == null)
			;
		String[] arrayOfString;
		int j;
		for (int i = R.array.wifi_status;; i = R.array.wifi_status_with_ssid) {
			arrayOfString = localResources.getStringArray(i);
			j = paramDetailedState.ordinal();
			if ((j < arrayOfString.length) && (arrayOfString[j].length() != 0))
				break;
			return "";
		}
		return String.format(arrayOfString[j], new Object[] { paramString });
	}

	public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
			boolean paramBoolean) {
		UMDebug.umdebug_trace();
		Logger.i(TAG, "getWifiApState=" + this.mWifiManager.getWifiApState()+ ", paramBoolean=" + paramBoolean);
		this.belong.setViewState(false);
		int i = this.mWifiManager.getWifiApState();
		if ((paramBoolean) && ((i == 12) || (i == 13))){
			this.apCheckBox.setChecked(false);
			this.mWifiManager.setWifiApEnabled(null, false);
			Log.i("mylog", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
			

		if (this.mWifiManager.setWifiEnabled(paramBoolean)) {
			UMDebug.d("WifiEnabler", "setWifiEnabled=true");
			this.mCheckBox.setCanchecked(false);
			return;
		}
		this.mCheckBox.setText2(this.mContext.getText(R.string.wifi_error).toString());
	}

	public void pause() {
		if (this.mReceiver != null) {
			this.mContext.unregisterReceiver(this.mReceiver);
			this.mCheckBox.setOnCheckedChangeListener(null);
		}
	}

	public void resume() {
		this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
		this.mCheckBox.setOnCheckedChangeListener(this);
	}
}
