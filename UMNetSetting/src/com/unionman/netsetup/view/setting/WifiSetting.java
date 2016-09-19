package com.unionman.netsetup.view.setting;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WpsInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.netsetup.R;
import com.unionman.netsetup.interfaces.WifiAdmin;
import com.unionman.netsetup.model.WifiBaseAdapter;
import com.unionman.netsetup.util.Constant;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wireless network settings menu three
 *
 * @author huyq
 */
public class WifiSetting extends LinearLayout implements
        View.OnFocusChangeListener, DialogInterface.OnClickListener {
    public final static String TAG = "WifiSetting";

    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 20 * 1000;

    private Context mContext;
    private WifiAdmin mWifiAdmin;
    private WifiManager mWifiManager;
    private WifiBaseAdapter mWifiBaseAdapter;

    // scan wifi connect list
    private List<AccessPoint> mWifiList;
    // Not to scan and properly configured WiFi connection list
    private List<WifiConfiguration> mConfigList;
    // The selected scan WiFi connection
    private AccessPoint mSelectedWifiItem;
    // The selected were not scanned and configured WiFi connection
    private WifiConfiguration mWifiConfiguration;
    // Control NetSettingDialog display content
    private WifiDialog mDialog;
    private NetStateDialog mNetStateDialog;

    private DetailedState mLastState;
    private WifiInfo mLastInfo;

    private LinearLayout wifi_menu;
    private TextView mEmptyText;
    // wifi switch
    private CheckBox mSwitchCb;
    private TextView wifi_refresh_txt;
    // listView of wifi
    private ListView mWifiListView;
    // text of refresh
    private TextView mRefreshText;
    // text of add
    private TextView mAddText;
    // text of direct
    private TextView mDirectText;
    private Toast mToast;
    private final IntentFilter mFilter;
    private final BroadcastReceiver mReceiver;
    private final Scanner mScanner;
    private  int onSeletPos = 0;
    private  String onSeletSSID = null;
   
    private  int freshCounter = 0;
    private static final String FACTORY_CALL = "factoryCall";
    private static boolean isFactoryCalled = false;
    
    private final AtomicBoolean mConnected = new AtomicBoolean(false);
    
    public WifiSetting(Context context, Handler handler) {
        super(context);
        this.mContext = context;
        Log.d(TAG,"WifiSetting");
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction(FACTORY_CALL);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };
        mScanner = new Scanner();

        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdmin(mWifiManager);
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.setting_wifi, this);
        initView(parent);
        mSwitchCb.setChecked(mWifiAdmin.isWifiOpen());
        setViewInvalid(mWifiManager.getWifiState());
        
        if(mWifiAdmin.isWifiOpen()==true&&mWifiList!=null&&mWifiList.size()>0)
        {
        	mWifiListView.requestFocus();	
        }
        else
        {
        	mSwitchCb.requestFocus();	
        }
        refreshList();
        
    }

    private void initView(View parent) {
    	wifi_menu =  (LinearLayout) parent.findViewById(R.id.wifi_menu);
        mEmptyText = (TextView) parent.findViewById(R.id.empty_wifi_txt);
        // wifi switch
        mSwitchCb = (CheckBox) parent.findViewById(R.id.wifi_switch_cb);
        wifi_refresh_txt = (TextView) parent.findViewById(R.id.wifi_refresh_txt);
        mSwitchCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                try {
                	if (isChecked || isFactoryCalled) {
                        mWifiAdmin.openWifi();
                        isFactoryCalled = false;
                        if (Constant.LOG_TAG) {
                        }
                        refreshList();
                        wifi_refresh_txt.requestFocus(); 
                    } else {
                        mWifiAdmin.closeWifi();
                        if (Constant.LOG_TAG) {
                        }
                        refreshList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // wifi list
        mWifiListView = (ListView) parent.findViewById(R.id.wifi_signal_list);
        mWifiListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				onSeletPos = arg2;
				onSeletSSID = getSSIDByIndex(onSeletPos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        mWifiListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                selectWifiItem(arg2);
            }
        });

        // wifi refresh
        mRefreshText = (TextView) parent.findViewById(R.id.wifi_refresh_txt);
        mRefreshText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshList();
            }
        });
       mRefreshText.setOnFocusChangeListener(this);
        // wifi add
        mAddText = (TextView) parent.findViewById(R.id.wifi_add_txt);
        mAddText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                mDialog = new WifiDialog(mContext, WifiSetting.this,
                        null, true);
                mDialog.show();
            }
        });
        mAddText.setOnFocusChangeListener(this);
        // wifi direct connect
        mDirectText = (TextView) parent.findViewById(R.id.wifi_direct_txt);
        mDirectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WpsDialog wpsDialog = new WpsDialog(mContext, WpsInfo.PBC);
                wpsDialog.show();
                // TODO Do not need to click the effect
            }
        });
        mDirectText.setOnFocusChangeListener(this);
        getContext().registerReceiver(mReceiver, mFilter);
    }
    
    public void onStop() {
        mContext.unregisterReceiver(mReceiver);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
/*        case KeyEvent.KEYCODE_DPAD_LEFT:
            if (mSwitchCb.isFocused() && mSwitchCb.isChecked()) {
                mSwitchCb.setChecked(false);
            }
            break;

        case KeyEvent.KEYCODE_DPAD_RIGHT:
            if (mSwitchCb.isFocused() && !mSwitchCb.isChecked()) {
                mSwitchCb.setChecked(true);
            }
            break;

        default:
            break;*/
        }
        return super.dispatchKeyEvent(event);
    }
    
	public void WifiSettingRequestFocus()
	{
        if(mWifiAdmin!=null&&mWifiAdmin.isWifiOpen()==true
        		&&mWifiList!=null&&mWifiList.size()>0)
        {
        	mWifiListView.requestFocus();	
        }
        else
        {
        	mSwitchCb.requestFocus();	
        }
		onSeletPos = 0;
		onSeletSSID = null;
        refreshList();
	}
	
    public void freshDelay() {
		if((++freshCounter)%2==0)
		{
			freshCounter = 0;
			refreshList(); 
		}
    }

    /**
     * connect the selected item
     */
    private void selectWifiItem(int position) {
        // TODO Auto-generated method stub
        if (position < mWifiList.size()) {
            mSelectedWifiItem = mWifiList.get(position);

            if (mSelectedWifiItem.security == AccessPoint.SECURITY_NONE
                    && mSelectedWifiItem.networkId == INVALID_NETWORK_ID) {
            	Log.i(TAG,"mSelectedWifiItem.security="+mSelectedWifiItem.security+" mSelectedWifiItem.networkId="+INVALID_NETWORK_ID);
                mSelectedWifiItem.generateOpenNetworkConfig();
                mWifiManager.connect(mSelectedWifiItem.getConfig(), null);
            } else {
            	Log.i(TAG,"new WifiDialog()");
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                mDialog = new WifiDialog(mContext, this, mSelectedWifiItem,
                        false);
                mDialog.show();
            }
        }
    }

    /**
     * refresh the ui
     *
     * @param isWifiOpen
     */
    private void setViewInvalid(int wifi_state) {

    	Log.d(TAG, "setViewInvalid wifi_state:"+wifi_state);
        switch (wifi_state) {
        case WifiManager.WIFI_STATE_ENABLED:
            mEmptyText.setVisibility(View.GONE);
            mWifiListView.setVisibility(View.VISIBLE);
/*            mRefreshText.setVisibility(View.VISIBLE);
            mAddText.setVisibility(View.VISIBLE);
            mDirectText.setVisibility(View.VISIBLE);*/
            wifi_menu.setVisibility(View.VISIBLE);
            mSwitchCb.setChecked(true);
            break;

        case WifiManager.WIFI_STATE_ENABLING:
            mEmptyText.setText(R.string.wifi_starting);
            mEmptyText.setVisibility(View.VISIBLE);
            mWifiListView.setVisibility(View.GONE);
/*            mRefreshText.setVisibility(View.GONE);
            mAddText.setVisibility(View.GONE);
            mDirectText.setVisibility(View.GONE);*/
            wifi_menu.setVisibility(View.GONE);
            mSwitchCb.setChecked(true);
            break;

        case WifiManager.WIFI_STATE_DISABLING:
            mEmptyText.setText(R.string.wifi_stoping);
            mEmptyText.setVisibility(View.VISIBLE);
            mWifiListView.setVisibility(View.GONE);
            mWifiListView.setAdapter(null);
/*            mRefreshText.setVisibility(View.GONE);
            mAddText.setVisibility(View.GONE);
            mDirectText.setVisibility(View.GONE);*/
            wifi_menu.setVisibility(View.GONE);
            mSwitchCb.setChecked(false);
            break;

        case WifiManager.WIFI_STATE_DISABLED:
            mEmptyText.setText(R.string.wifi_stoped);
            mEmptyText.setVisibility(View.VISIBLE);
            mWifiListView.setVisibility(View.GONE);
            mWifiListView.setAdapter(null);
/*            mRefreshText.setVisibility(View.GONE);
            mAddText.setVisibility(View.GONE);
            mDirectText.setVisibility(View.GONE);*/
            wifi_menu.setVisibility(View.GONE); 
            mSwitchCb.setChecked(false);
            break;
        case WifiManager.WIFI_STATE_UNKNOWN:
            mEmptyText.setVisibility(View.INVISIBLE);
            mWifiListView.setVisibility(View.GONE);
            mWifiListView.setAdapter(null);
/*            mRefreshText.setVisibility(View.GONE);
            mAddText.setVisibility(View.GONE);
            mDirectText.setVisibility(View.GONE);*/
            wifi_menu.setVisibility(View.GONE);
            mSwitchCb.setChecked(false);
            if (mToast == null) {
                mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
            }
            mToast.setText(R.string.wifi_state_unknow);
            mToast.show();
            break;
        }
    }

    /**
     * reload and refresh the wifi list
     */
    protected void refreshList() {
    	Log.i(TAG,"refreshList() is calling");
        final int wifiState = mWifiManager.getWifiState();

        setViewInvalid(wifiState);

        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            mWifiList = constructAccessPoints();
            mWifiBaseAdapter = new WifiBaseAdapter(mContext, mWifiList,
                    mConfigList, mWifiManager);
            mWifiListView.setAdapter(mWifiBaseAdapter);
            if(mWifiList!=null&&mWifiList.size()>0)
            {
            	int index = findSSIDIndex(onSeletSSID);
            	int seletIndex = ((index==-1)?onSeletPos:index);
            	mWifiListView.setSelection(mWifiList.size()-1>seletIndex?seletIndex:mWifiList.size()-1);
            }
            mWifiBaseAdapter.notifyDataSetChanged();
        }
        
    }

    /** Returns sorted list of access points */
    private List<AccessPoint> constructAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        /**
         * Lookup table to more quickly update AccessPoints by only considering
         * objects with the correct SSID. Maps SSID -> List of AccessPoints with
         * the given SSID.
         */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

        final List<WifiConfiguration> configs = mWifiManager
                .getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AccessPoint accessPoint = new AccessPoint(mContext, config);
                accessPoint.update(mLastInfo, mLastState);
                accessPoints.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result))
                        found = true;
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(mContext, result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }

        // Pre-sort accessPoints to speed preference insertion
        Collections.sort(accessPoints);
        return accessPoints;
    }

    /** A restricted multimap for use in constructAccessPoints */
    private class Multimap<K, V> {
        private final HashMap<K, List<V>> store = new HashMap<K, List<V>>();

        /** retrieve a non-null list of values with key K */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
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

    /**
     * make the child dialog dismiss
     */
    public void dismissChildDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (null != mNetStateDialog && mNetStateDialog.isShowing()) {
            mNetStateDialog.dismiss();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {/*
        Log.i(TAG, "hasFocus:" + hasFocus);
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.launcher_set_focus);
            ((TextView) v).setTextColor(mContext.getResources().getColor(
                    R.color.black));
        } else {
            v.setBackgroundResource(R.drawable.button_transparent);
            ((TextView) v).setTextColor(mContext.getResources().getColor(
                    R.color.white));
        }
    */}

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG,"action="+action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            setViewInvalid(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
                        .equals(action)
                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
        	Log.i(TAG,"1 action="+action);
    //cancle the refresh every 10s .Edit by zemin  	
        	//freshDelay();
        	refreshList();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            // Ignore supplicant state changes when network is connected
            // TODO: we should deprecate SUPPLICANT_STATE_CHANGED_ACTION and
            // introduce a broadcast that combines the supplicant and network
            // network state change events so the apps dont have to worry about
            // ignoring supplicant state change when network is connected
            // to get more fine grained information.
            SupplicantState state = (SupplicantState) intent
                    .getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
	            updateConnectionState(WifiInfo.getDetailedStateOf(state));
            } else {
                // During a connect, we may have the supplicant
                // state change affect the detailed network state.
                // Make sure a lost connection is updated as well.
            	updateConnectionState(null);
            }
            Log.i(TAG,"2 action="+action);
            //refreshList();
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
			updateConnectionState(info.getDetailedState());
            Log.i(TAG,"3 action="+action);
            refreshList();
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            Log.i(TAG,"rssi CHANGED");
			updateConnectionState(null);
            Log.i(TAG,"4 action="+action);
        }else if(FACTORY_CALL.equals(action)){
			Log.i(TAG,"FAC CALL received");
			isFactoryCalled = true;
			if(mWifiAdmin != null){                         
				Log.i(TAG,"mWifiAdmin openWifi");
				mWifiAdmin.openWifi();
				isFactoryCalled = false;
			}
            //refreshList();
		} 
    }

    private void updateConnectionState(DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            mScanner.pause();
        } else {
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }
        
        if(mWifiList!=null&&mWifiList.size()>0)
        {
	        for (int i = mWifiList.size() - 1; i >= 0; --i) {
	            final AccessPoint accessPoint = mWifiList.get(i);
	            accessPoint.update(mLastInfo, mLastState);
	            mWifiList.set(i, accessPoint);
	        }
	        mWifiBaseAdapter.notifyDataSetChanged();
        }
    }

    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                if (mContext != null) {
                    Toast.makeText(mContext, R.string.wifi_fail_to_scan,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int button) {
        if (button == WifiDialog.BUTTON_FORGET && mSelectedWifiItem != null) {
            forget();
        } else if (button == WifiDialog.BUTTON_SUBMIT) {
        	Log.i(TAG,"mDialog="+mDialog);
            if (mDialog != null) {
                submit(mDialog.getController());
            }
        }
    }

    /* package */void submit(WifiConfigController configController) {

        final WifiConfiguration config = configController.getConfig();

        if (config == null) {       	
            if (mSelectedWifiItem != null
                    && mSelectedWifiItem.networkId != INVALID_NETWORK_ID) {
                mWifiManager.connect(mSelectedWifiItem.networkId, null);
            }
        } else if (config.networkId != INVALID_NETWORK_ID) {
            if (mSelectedWifiItem != null) {
                mWifiManager.save(config, null);
            }
        } else {
            if (configController.isEdit()) {
                mWifiManager.save(config, null);
            } else {
                mWifiManager.connect(config, null);
            }
        }
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        refreshList();
    }

    /* package */void forget() {
        if (mSelectedWifiItem.networkId == INVALID_NETWORK_ID) {
            // Should not happen, but a monkey seems to triger it
            Log.e(TAG,
                    "Failed to forget invalid network "
                            + mSelectedWifiItem.getConfig());
            return;
        }

        mWifiManager.forget(mSelectedWifiItem.networkId, null);
        refreshList();
    }
    
    private int findSSIDIndex(String ssid)
    {
        if((mWifiList!=null)&&(mWifiList.size()>0)&&(ssid!=null))
        {
        	for(int i = 0;i<mWifiList.size();i++)
        	{
        		if(ssid.equals(mWifiList.get(i).ssid))
        		{
        			return i;
        		}
        	}
        }
        
        return -1;
    }
    
    
    private String getSSIDByIndex(int index)
    {
        if((mWifiList!=null)&&(mWifiList.size()>0)&&(index>0)&&(index<mWifiList.size()))
        {
        		return mWifiList.get(index).ssid;
        }
        
        return null;	
    	
    }
}
