package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.network.EthCtl;
import com.unionman.settingwizard.network.WiFiCtl;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.PropertyUtils;
import com.unionman.settingwizard.wifi.AccessPoint;
import com.unionman.settingwizard.wifi.WifiAdmin;
import com.unionman.settingwizard.wifi.WifiBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

public class WirelessNetworkSetup extends Activity {
    private String TAG = "WirelessNetworkSetup";
    private static final int WIFI_RESCAN_INTERVAL_MS = 20 * 1000;
    private ListView mWifiListView = null;
    private WiFiCtl mWifiCtl = null;
    private EthCtl mNetworkCtl = null;
    private Context mContext;
    private boolean editing = false;
    private Button mConnectBtn = null;
    private TextView mConnect = null;
    private ProgressBar mSearchingDialog = null;

    private Scanner mScanner;
    private WifiAdmin mWifiAdmin;
    private WifiManager mWifiManager;
    private WifiBaseAdapter mWifiBaseAdapter;

    private List<AccessPoint> mWifiList;
    private NetworkInfo.DetailedState mLastState;
    private WifiInfo mLastInfo;
    private AccessPoint mSelectedWifiItem;
    private List<WifiConfiguration> mConfigList;
    private  int onSeletPos = 0;
    private  String onSeletSSID = null;
    private int mAccessPointSecurity;
    private String mPassWord = null;

    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter = null;
    private final AtomicBoolean mConnected = new AtomicBoolean(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wireless_network_setup2);
        mContext = WirelessNetworkSetup.this;
        mScanner = new Scanner();

        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!editing) {
                    handleEvent(context, intent);
                }
            }
        };
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdmin(mWifiManager);
        mNetworkCtl = new EthCtl(this);

        initView();
        registerReceiver(mReceiver, mFilter);
        new OpenWifi().execute();

        setViewInvalid(mWifiManager.getWifiState());
        if(mWifiAdmin.isWifiOpen() &&mWifiList!=null&&mWifiList.size()>0) {
//            mWifiListView.requestFocus();
        }
        refreshList();
    }

    private class OpenWifi extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            mNetworkCtl.stopEth();
            mWifiAdmin.openWifi();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            refreshList();
        }
    }

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG,"action="+action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            setViewInvalid(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
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
            //refreshList();
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

        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
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

    private void updateConnectionState(NetworkInfo.DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
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

    void submit(int pos) {
        WifiConfiguration config = getConfig(pos);
        if (config == null) {
            if (mSelectedWifiItem != null
                    && mSelectedWifiItem.networkId != INVALID_NETWORK_ID) {
                mWifiManager.connect(mSelectedWifiItem.networkId, null);
            }
        } else {
            mWifiManager.connect(config, null);
        }
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        refreshList();
    }

    private void setViewInvalid(int wifi_state) {

        Log.d(TAG, "setViewInvalid wifi_state:"+wifi_state);
        switch (wifi_state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mSearchingDialog.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                mSearchingDialog.setVisibility(View.VISIBLE);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                mSearchingDialog.setVisibility(View.VISIBLE);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                mSearchingDialog.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                mSearchingDialog.setVisibility(View.GONE);
                break;
        }
    }

    protected void refreshList() {
        Log.i(TAG,"refreshList() is calling");
        final int wifiState = mWifiManager.getWifiState();
        setViewInvalid(wifiState);
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            mWifiList = constructAccessPoints();
            mWifiBaseAdapter = new WifiBaseAdapter(mContext, mWifiList, mConfigList, mWifiManager);
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

    private WifiConfiguration getConfig(int pos) {
        if (mSelectedWifiItem != null
                && mSelectedWifiItem.networkId != INVALID_NETWORK_ID) {
            return null;
        }

        WifiConfiguration config = new WifiConfiguration();

        if (mSelectedWifiItem == null) {
            config.SSID = AccessPoint.convertToQuotedString(onSeletSSID);
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else if (mSelectedWifiItem.networkId == INVALID_NETWORK_ID) {
            config.SSID = AccessPoint.convertToQuotedString(mSelectedWifiItem.ssid);
        } else {
            config.networkId = mSelectedWifiItem.networkId;
        }

        switch (mAccessPointSecurity) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                if (mPassWord != null && mPassWord.length() != 0) {
                    int length = mPassWord.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)
                            && mPassWord.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = mPassWord;
                    } else {
                        config.wepKeys[0] = '"' + mPassWord + '"';
                    }
                }
                break;

            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (mPassWord != null && mPassWord.length() != 0) {
                    if (mPassWord.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = mPassWord;
                    } else {
                        config.preSharedKey = '"' + mPassWord + '"';
                    }
                }
                break;
            default:
                return null;
        }
        return config;
    }

    private void initView() {
        Button nextStepBtn = (Button) findViewById(R.id.btn_next_step);
        Button lastStepBtn = (Button) findViewById(R.id.btn_last_step);
        nextStepBtn.setOnClickListener(new MyClickListener());
        lastStepBtn.setOnClickListener(new MyClickListener());

        mSearchingDialog = (ProgressBar) findViewById(R.id.pb_progress_bar);
        mSearchingDialog.setVisibility(View.GONE);
        mWifiListView = (ListView) findViewById(R.id.lv_wifi);
        mWifiListView.setOnFocusChangeListener(focusListener);
        mWifiListView.setOnItemSelectedListener(selectListener);
        mWifiListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                editing = true;
                final AccessPoint scanResult = mWifiList.get(position);
                int animId = R.anim.push_right_in;
                final Animation animation = AnimationUtils.loadAnimation(mContext,
                        animId);

                final LinearLayout layout = (LinearLayout) view;
                final LinearLayout password = (LinearLayout) ((FrameLayout) layout.getChildAt(0)).getChildAt(0);
                final LinearLayout txt = (LinearLayout) ((FrameLayout) layout.getChildAt(0)).getChildAt(1);
                password.setVisibility(View.VISIBLE);
                txt.setVisibility(View.INVISIBLE);
                view.setBackgroundResource(R.color.blue);
                mAccessPointSecurity = (scanResult == null) ? AccessPoint.SECURITY_NONE : scanResult.security;
                ((EditText)view.findViewById(R.id.et_password)).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() >= 8) {
                            mConnectBtn.setSelected(true);
                        } else {
                            mConnectBtn.setSelected(false);
                        }
                    }
                });

                mConnectBtn = (Button) view.findViewById(R.id.btn_connect);
                mConnectBtn.setSelected(false);

                mConnectBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editing = false;
                        password.setVisibility(View.INVISIBLE);
                        txt.setVisibility(View.VISIBLE);
                        mConnect = (TextView) txt.getChildAt(1);
                        mConnect.setText(R.string.connecting);
                        mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

                        EditText password = (EditText) ((LinearLayout) v.getParent()).getChildAt(1);
                        mPassWord = password.getText().toString();
                        submit(pos);

                        View myView = ((View) v.getParent().getParent().getParent());
                        myView.startAnimation(animation);
                        myView.setBackgroundDrawable(null);
                    }
                });
                mConnectBtn.setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        View myView = ((View) v.getParent().getParent().getParent());
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                editing = false;
                                password.setVisibility(View.INVISIBLE);
                                txt.setVisibility(View.VISIBLE);
                                mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                                myView.startAnimation(animation);
                                myView.setBackgroundDrawable(null);
                                return true;
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                            case KeyEvent.KEYCODE_DPAD_UP:
                                editing = false;
                                password.setVisibility(View.INVISIBLE);
                                txt.setVisibility(View.VISIBLE);
                                mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

                                myView.startAnimation(animation);
                                myView.setBackgroundDrawable(null);
                                return false;
                        }
                        return false;
                    }
                });

                mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                EditText editText = (EditText) view.findViewById(R.id.et_password);
                editText.requestFocus();
                editText.setOnKeyListener(new OnKeyListener() {
                    int backKeyTimes = 0;

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        boolean returnCode;
                        View myView = ((View) v.getParent().getParent().getParent());
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                                returnCode = false;
                                editing = true;
                                break;
                            case KeyEvent.KEYCODE_BACK:
                                if (backKeyTimes == 0) {
                                    editing = false;
                                    returnCode = true;
                                    backKeyTimes++;
                                    break;
                                } else {
                                    editing = false;
                                    returnCode = true;
                                    password.setVisibility(View.INVISIBLE);
                                    txt.setVisibility(View.VISIBLE);
                                    mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                                    myView.startAnimation(animation);
                                    myView.setBackgroundDrawable(null);
                                    backKeyTimes = 0;
                                    break;
                                }
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                            case KeyEvent.KEYCODE_DPAD_UP:
                                if (!editing) {
                                    returnCode = true;
                                    password.setVisibility(View.INVISIBLE);
                                    txt.setVisibility(View.VISIBLE);
                                    mWifiListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                                    myView.startAnimation(animation);
                                    myView.setBackgroundDrawable(null);
                                } else {
                                    returnCode = false;
                                }
                                break;
                            default:
                                returnCode = false;
                                break;
                        }
                        return returnCode;
                    }
                });
                view.startAnimation(animation);
            }
        });
    }

    private View preItemView = null;
    public OnItemSelectedListener selectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg3) {
            if (mWifiListView.isFocused()) {
                TextView tv = (TextView) view.findViewById(R.id.tv_ssid);
                ;
                tv.setTextColor(Color.WHITE);

                if (preItemView != null && view != preItemView) {
                    tv = (TextView) preItemView.findViewById(R.id.tv_ssid);
                    tv.setTextColor(Color.WHITE);
                }

                preItemView = view;
                mSelectedWifiItem = mWifiList.get(pos);
                onSeletPos = pos;
                onSeletSSID = mSelectedWifiItem.ssid;
            } else {
                TextView tv = (TextView) view.findViewById(R.id.tv_ssid);
                tv.setTextColor(Color.WHITE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public OnFocusChangeListener focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                View view = (View) mWifiListView.getSelectedView();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_ssid);
                    tv.setTextColor(Color.WHITE);
                    preItemView = view;
                }
            } else {
                TextView tv = (TextView) preItemView.findViewById(R.id.tv_ssid);
                tv.setTextColor(Color.WHITE);
            }
        }
    };

    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    boolean hasPreInstalled = PropertyUtils.getInt("persist.sys.dvb.installed", 0) == 1;
       					intent = new Intent();
       	                if (hasPreInstalled) {
       	                    intent.setClass(WirelessNetworkSetup.this, SetupFinishActivity.class);
       	                } else {
       	                    intent.putExtra("isInSettingWizard", true);
       	                    intent.setClassName("com.unionman.dvbcitysetting", "com.unionman.dvbcitysetting.CitySettingActivity");
       	                }
   			    	startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(mContext, NetworkSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {    
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
