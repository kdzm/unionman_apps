package com.unionman.settings.content;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.UMSettings;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;
import com.unionman.settings.wifi.WifiTools_API17;
import com.unionman.settings.wifi.AccessPoint;
import com.unionman.settings.wifi.UMWifiManager;
import com.unionman.settings.wifi.WifiAdapter;
import com.unionman.settings.wifi.WifiConnectActivity;

public class WifiSettings extends RightWindowBase {
	private ListView mListView;
	private AtomicBoolean mConnected = new AtomicBoolean(false);
	private IntentFilter mFilter;
	private WifiInfo mLastInfo;
	private NetworkInfo.DetailedState mLastState;
	public List<AccessPoint> mList;
	private BroadcastReceiver mReceiver;
	private Scanner mScanner;
	public WifiAdapter mWifiAdapter;
	private WifiManager mWifiManager;
	private static final String TAG="com.unionman.settings.content.network--WifiSettings--";

	public WifiSettings(Context paramContext) {
		super(paramContext);
	}

	private List<AccessPoint> constructAccessPoints() {
		ArrayList<AccessPoint> localArrayList = new ArrayList<AccessPoint>();
		Multimap<String, AccessPoint> localMultimap = new Multimap<String, AccessPoint>();
		List<WifiConfiguration> localList1 = mWifiManager
				.getConfiguredNetworks();
		List<ScanResult> localScanResult = mWifiManager.getScanResults();
		if (localList1 != null) {
			Logger.i(TAG, "constructAccessPoints getConfiguredNetworks.size=" + localList1.size());
			for (int i = 0; i < localList1.size(); i++) {
				AccessPoint localAccessPoint2 = new AccessPoint(this.mContext,
						localList1.get(i));
				localAccessPoint2.update(mLastInfo, mLastState);
				localArrayList.add(localAccessPoint2);
				localMultimap.put(localAccessPoint2.getSsid(),
						localAccessPoint2);
			}
		}

		if (localScanResult != null) {
			Logger.i(TAG, "constructAccessPoints getScanResults.size=" + localScanResult.size());
			for (int i = 0; i < localScanResult.size(); i++) {
				ScanResult sr = localScanResult.get(i);
				if ((sr.SSID == null) || (sr.SSID.length() == 0)
						|| (sr.capabilities.contains("[IBSS]"))) {
					continue;
				}
				boolean found = false;
				for (AccessPoint accessPoint : localMultimap.getAll(sr.SSID)) {
					if (accessPoint.update(sr)) {
						found = true;
					}
				}
				if (!found) {
					AccessPoint accessPoint = new AccessPoint(this.mContext, sr);
					localArrayList.add(accessPoint);
					localMultimap.put(accessPoint.getSsid(), accessPoint);
				}
			}
		}
		Collections.sort(localArrayList);
		Logger.i(TAG, "localArrayList size=" + localArrayList.size());
		return localArrayList;

	}

	private void handleEvent(Intent paramIntent) {
		Logger.i(TAG,"handleEvent()--");
		String str = paramIntent.getAction();
		Logger.i(TAG, "handleEvent Action:" + str);
		if (str.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
			updateWifiState(paramIntent.getIntExtra("wifi_state", 4));
		} else if (str.equals("android.net.wifi.SCAN_RESULTS")
				|| str.equals("android.net.wifi.CONFIGURED_NETWORKS_CHANGE")
				|| str.equals("android.net.wifi.LINK_CONFIGURATION_CHANGED"))
			updateAccessPoints();
		else if (str.equals("android.net.wifi.supplicant.STATE_CHANGE")) {
			SupplicantState state = (SupplicantState) paramIntent
					.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
				updateConnectionState(WifiInfo.getDetailedStateOf(state));
			}
		} else if (str.equals("android.net.wifi.supplicant.RSSI_CHANGED")) {
			updateConnectionState(null);
		} else if (str.equals("android.net.wifi.STATE_CHANGE")) {
			NetworkInfo localNetworkInfo = (NetworkInfo) paramIntent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mConnected.set(localNetworkInfo.isConnected());
			updateAccessPoints();
			updateConnectionState(localNetworkInfo.getDetailedState());
		}
	}

	private void startConnectWifi(AccessPoint paramAccessPoint) {
		Logger.i(TAG,"startConnectWifi()--");
		Logger.i(TAG, "paramAccessPoint=" + paramAccessPoint);
		if (paramAccessPoint == null) {
			return;
		}
		Intent localIntent = new Intent(this.context, WifiConnectActivity.class);
		((UMSettings) this.context.getApplicationContext()).ap = paramAccessPoint;
		UMDebug.umdebug_trace();
		((Activity) this.context).startActivityForResult(localIntent, 0);
	}

	private void updateAccessPoints() {
		Logger.i(TAG,"updateAccessPoints()--");
		int state = mWifiManager.getWifiState();
		Logger.i(TAG, "updateAccessPoints wifistate=" + state);
		switch (state) {
		case 0:
		case 1:
			mList.clear();
			break;
		case 2:
		case 3:
			List localList = constructAccessPoints();
			mList.clear();
			mList.addAll(localList);
			break;
		default:
			break;
		}
		mWifiAdapter.notifyDataSetChanged();
		return;
	}

	private void updateConnectionInfo(boolean paramBoolean, int paramInt) {
		Logger.i(TAG,"updateConnectionInfo()--");
		Logger.i(TAG, "updateConnectionInfo hasError=" + paramBoolean + ",error=" + paramInt);
		WifiInfo localWifiInfo;
		AccessPoint localAccessPoint = null;
		if (paramBoolean) {
			String str = "";
			if (paramInt == 1) {
				str = this.context.getString(R.string.wifi_disabled_password_failure);
				if (!str.equals("")) {
					Message localMessage = new Message();
					localMessage.what = 2;
					localMessage.obj = str;
					UMWifiManager.getInstance(this.mContext).sendMsg(localMessage);
				}
			}

			mWifiManager.removeNetwork(UMWifiManager.getInstance(this.context).getmCurrentNetworkId());

			switch (Build.VERSION.SDK_INT) {
			default:
				WifiTools_API17.forget(this.mWifiManager, UMWifiManager.getInstance(this.context).getmCurrentNetworkId());
				mWifiManager.saveConfiguration();
				localWifiInfo = mWifiManager.getConnectionInfo();
				if (localWifiInfo.getSSID() != null) {
					break;
				} else {
					List<ScanResult> localList = this.mWifiManager.getScanResults();
					for (int i = 0; i < localList.size(); i++) {
						ScanResult localScanResult = localList.get(i);
						if ((localScanResult.SSID == null)
								|| (localScanResult.SSID.length() == 0)
								|| (localScanResult.capabilities
										.contains("[IBSS]")))
							continue;
						else {
							if (!localScanResult.BSSID
									.equals(AccessPoint
											.removeDoubleQuotes(localWifiInfo
													.getSSID())))
								continue;
							localAccessPoint = new AccessPoint(this.context,
									localScanResult);
							break;
						}
					}
				}
			}

		}

		if (localAccessPoint != null) {
			startConnectWifi(localAccessPoint);
			return;
		}

	}

	private void updateConnectionState(
			NetworkInfo.DetailedState paramDetailedState) {
		Logger.i(TAG,"updateConnectionState()--");
		Logger.i(TAG, "updateConnectionState state="+ paramDetailedState);
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}
		if (paramDetailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}
		mLastInfo = mWifiManager.getConnectionInfo();
		if (paramDetailedState != null) {
			mLastState = paramDetailedState;
		}

		Logger.i(TAG, "this.mList.size() = " + this.mList.size());
		for (int i = mList.size() - 1; i >= 0; i--) {
			Logger.i(TAG, "mLastState=" + this.mLastState);
			((AccessPoint) this.mList.get(i)).update(mLastInfo, mLastState);
		}
		Collections.sort(mList);
		mWifiAdapter.notifyDataSetChanged();
		mScanner.resume();
	}

	private void updateWifiState(int paramInt) {
		Logger.i(TAG,"updateWifiState()--");
		Logger.i(TAG, "updateWifiState--state=" + paramInt);
		if (paramInt == 3) {
			mScanner.resume();
			updateAccessPoints();
		} else {
			mScanner.pause();
			mList.clear();
			mWifiAdapter.notifyDataSetChanged();
			mListView.requestFocus();
		}
	}

	public AccessPoint getItem(List<AccessPoint> paramList,AccessPoint paramAccessPoint) {
		Logger.i(TAG,"getItem()--");
		if (paramList == null)
			return null;
		AccessPoint localAccessPoint = null;
		for (int i = 0; i < paramList.size(); i++) {
			localAccessPoint = paramList.get(i);
			if ((localAccessPoint.getSsid() == null)|| (paramAccessPoint.getBssid() == null))
				continue;
			if (!localAccessPoint.getSsid().equals(paramAccessPoint.getSsid()))
				break;
		}
		return localAccessPoint;
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		mList = new ArrayList();
		mWifiAdapter = new WifiAdapter(context, mList);
		mListView.setAdapter(mWifiAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
					View paramAnonymousView, int paramAnonymousInt,
					long paramAnonymousLong) {
				WifiSettings.this.startConnectWifi((AccessPoint) WifiSettings.this.mList.get(paramAnonymousInt));
			}
		});
	}

	public void onInvisible() {
		context.unregisterReceiver(this.mReceiver);
		mScanner.pause();
	}

	public void onResume() {
		context.registerReceiver(mReceiver, mFilter);
		updateAccessPoints();
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		frameId = ConstantList.FRAME_NETWORK_WIFI;
		levelId = 1002;
		mScanner = new Scanner();
		mWifiManager = ((WifiManager) context.getSystemService("wifi"));
		switch (Build.VERSION.SDK_INT) {
		default:
			WifiTools_API17.connect(mWifiManager,UMWifiManager.getInstance(this.context).getmCurrentNetworkId());
			break;
		}
		mFilter = new IntentFilter();
		mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		mFilter.addAction("android.net.wifi.SCAN_RESULTS");
		mFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
		mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
		mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
		mFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
		mFilter.addAction("android.net.wifi.STATE_CHANGE");
		mFilter.addAction("android.net.wifi.RSSI_CHANGED");
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramAnonymousContext,Intent paramAnonymousIntent) {
				WifiSettings.this.handleEvent(paramAnonymousIntent);
				WifiSettings.this.mListView.requestFocus();
			}
		};
		Logger.i(TAG, "mWifiManager.asyncConnect()");
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		layoutInflater.inflate(R.layout.wifi_settings, this);
		mListView = ((ListView) findViewById(R.id.wifi_listView));
		mListView.setEmptyView(findViewById(R.id.progressBar2));
	}

	private class Multimap<K, V> {
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		List<V> getAll(K paramK) {
			List<V> values = store.get(paramK);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}

	public class Scanner extends Handler {
		private int mRetry = 0;

		public void handleMessage(Message paramMessage) {
			if (mWifiManager.startScan()) {
				mRetry = 0;
				if (mRetry++ < 3) {
					sendEmptyMessageDelayed(0, 6000L);
				} else {
					mRetry = 0;
					ToastUtil.showLongToast(WifiSettings.this.context, R.string.wifi_fail_to_scan);
				}
			}
			return;
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}
	}
}
