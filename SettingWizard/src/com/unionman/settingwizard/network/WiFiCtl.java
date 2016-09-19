package com.unionman.settingwizard.network;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WpsResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.ScanResult;
import android.content.BroadcastReceiver;
import android.net.NetworkUtils;

import android.net.LinkProperties;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo;

import com.android.internal.util.AsyncChannel;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import android.security.Credentials;
import android.security.KeyStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.unionman.settingwizard.wifi.AP;

/**
 * Created by Administrator on 13-7-28.
 */
public class WiFiCtl implements AP.APCallback {
    private String TAG = "SettingWizard.WiFiCtl";


    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    public static final int INVALID_NETWORK_ID = -1;
    /* These values come from "wifi_network_setup" resource array */
    public static final int MANUAL = 0;
    public static final int WPS_PBC = 1;
    public static final int WPS_KEYPAD = 2;
    public static final int WPS_DISPLAY = 3;
    private static final String KEYSTORE_SPACE = "keystore://";
    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private Context mContext = null;
    private WifiManager mWifiManager;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private IntentFilter mFilter = null;
    private final BroadcastReceiver mReceiver;

    private int mKeyStoreNetworkId = INVALID_NETWORK_ID;
    private AP mSelectedAccessPoint = null;
    private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
    private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
    private LinkProperties mLinkProperties = new LinkProperties();
    private ArrayList<AP> mAPList = null;
    private Scanner mScanner = null;
    private DetailedState mLastState;
    private WifiInfo mLastInfo;

    public interface WIFICtlCallback {
        public void onResult(int wifiState, ArrayList<AP> list);

        public void notifyChanged(int level, WifiConfiguration config);

        public void notifyChanged(DetailedState status, WifiConfiguration config);
    }

    private WIFICtlCallback mWIFICtlCallback = null;

    public WiFiCtl(Context context, WIFICtlCallback cb) {
        mContext = context;
        mWIFICtlCallback = cb;

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
                Log.v("BroadcastReceiver", "  " + intent.getAction());
                handleEvent(context, intent);
            }
        };
        mContext.registerReceiver(mReceiver, mFilter);
        mScanner = new Scanner();

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        mWifiManager.connect(context, new WifiServiceHandler());

    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public boolean isConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        return wifiInfo.getSSID() != null;
    }

    public WifiInfo getWiFiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public String getWifiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipInt = wifiManager.getConnectionInfo().getIpAddress();
        String ip = NetworkUtils.intToInetAddress(ipInt).toString().split("\\/")[1];
        return ip;

    }

    public void startWIFI() {
        int wifiState = mWifiManager.getWifiState();

        if (!((WifiManager.WIFI_STATE_ENABLING == wifiState) ||
                (WifiManager.WIFI_STATE_DISABLING == wifiState))) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    public void stopWIFI() {
        // Disable tethering if enabling Wifi
        int wifiApState = mWifiManager.getWifiApState();
        if (((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) {
            mWifiManager.setWifiApEnabled(null, false);
        } else {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public static void stopWIFI(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // Disable tethering if enabling Wifi
        int wifiApState = wifiManager.getWifiApState();
        if (((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) {
            wifiManager.setWifiApEnabled(null, false);
        } else {
            wifiManager.setWifiEnabled(false);
        }
    }

    public void startScan() {
        //mContext.registerReceiver(mReceiver, mFilter);


        if (mKeyStoreNetworkId != INVALID_NETWORK_ID &&
                KeyStore.getInstance().state() == KeyStore.State.UNLOCKED) {
            mWifiManager.connect(mKeyStoreNetworkId, null);
        }
        mKeyStoreNetworkId = INVALID_NETWORK_ID;

        updateAPs();
    }

    public void stopScan() {
        //Log.e(TAG, mContext.toString());
        mContext.unregisterReceiver(mReceiver);
        mScanner.pause();
    }

    private boolean controlerRequireKeyStore(WifiConfiguration config) {
        if (config == null) {
            return false;
        }
/*        String values[] = {config.ca_cert.value(), config.client_cert.value(),
                config.key_id.value()};
        for (String value : values) {
            if (value != null && value.startsWith(KEYSTORE_SPACE)) {
                return true;
            }
        }*/
        return false;
    }

    private boolean requireKeyStore(WifiConfiguration config) {
        if (controlerRequireKeyStore(config) &&
                KeyStore.getInstance().state() != KeyStore.State.UNLOCKED) {
            mKeyStoreNetworkId = config.networkId;
            Credentials.getInstance().unlock(mContext);
            return true;
        }
        return false;
    }

    /**
     * A restricted multimap for use in constructAPs
     */
    private class Multimap<K, V> {
        private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

        /**
         * retrieve a non-null list of values with key K
         */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
            return values != null ? values : new ArrayList<V>();
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

    public ArrayList<AP> getAccessPointList() {
        ArrayList<AP> APs = new ArrayList<AP>();

        Multimap<String, AP> apMap = new Multimap<String, AP>();
        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AP accessPoint = new AP(mContext, config, this);
                accessPoint.update(mLastInfo, mLastState);
                APs.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 ||
                        result.capabilities.contains("[IBSS]")) {
                    continue;
                }

                boolean found = false;
                for (AP accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result))
                        found = true;
                }
                if (!found) {
                    AP accessPoint = new AP(mContext, result, (AP.APCallback) this);
                    APs.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }


        // Pre-sort APs to speed preference insertion
        mAPList = APs;

        return APs;
    }

    private void updateWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mScanner.resume();
                return; // not break, to avoid the call to pause() below
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                break;
        }

        mLastInfo = null;
        mLastState = null;
        mScanner.pause();
    }

    public void refreshAccessPoints() {
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
    }

    public int getAccessPointsCount() {
        final boolean wifiIsEnabled = mWifiManager.isWifiEnabled();
        if (wifiIsEnabled) {
            return mAPList.size();
        } else {
            return 0;
        }
    }

    /**
     * Requests wifi module to pause wifi scan. May be ignored when the module is disabled.
     */
    public void pauseWifiScan() {
        if (mWifiManager.isWifiEnabled()) {
            mScanner.pause();
        }
    }

    /**
     * Requests wifi module to resume wifi scan. May be ignored when the module is disabled.
     */
    public void resumeWifiScan() {
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
    }

    private void updateConnectionState(DetailedState state) {
        Log.v("updateConnectionState", "" + state);
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            Log.v(TAG, "" + mWifiManager.isWifiEnabled());
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            Log.v(TAG, "OBTAINING_IPADDR");
            mScanner.pause();
        } else {
            Log.v(TAG, "OBTAINING");
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            Log.v(TAG, "mLastInfo");
            mLastState = state;
        }
        ArrayList<AP> APList = getAccessPointList();
        for (AP accessPoint : APList) {
            accessPoint.update(mLastInfo, mLastState);
        }
    }

    /**
     * Shows the latest access points available with supplimental information like
     * the strength of network and the security for it.
     */
    private void updateAPs() {
        final int wifiState = mWifiManager.getWifiState();
        Log.v("updateAPs", "" + wifiState);

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                // APs are automatically sorted with TreeSet.
                final ArrayList<AP> APs = this.getAccessPointList();
                mWIFICtlCallback.onResult(wifiState, APs);
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                mWIFICtlCallback.onResult(wifiState, null);
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                mWIFICtlCallback.onResult(wifiState, null);
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                mWIFICtlCallback.onResult(wifiState, null);
                break;
        }
    }

    private void handleEvent(Context context, Intent intent) {
        Log.v("handleEvent", "" + intent);
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "WIFI_STATE_CHANGED_ACTION");
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action) ||
                WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action) ||
                WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "SCAN_RESULTS_AVAILABLE_ACTION");
            updateAPs();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "SUPPLICANT_STATE_CHANGED_ACTION");
            //Ignore supplicant state changes when network is connected
            //TODO: we should deprecate SUPPLICANT_STATE_CHANGED_ACTION and
            //introduce a broadcast that combines the supplicant and network
            //network state change events so the apps dont have to worry about
            //ignoring supplicant state change when network is connected
            //to get more fine grained information.
            if (!mConnected.get()) {
                Log.v(TAG, "SUPPLICANT_STATE_CHANGED_ACTION mConnected");
                updateConnectionState(WifiInfo.getDetailedStateOf((SupplicantState)
                        intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
            }

        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "NETWORK_STATE_CHANGED_ACTION mConnected");
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
                    WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateAPs();
            updateConnectionState(info.getDetailedState());

            Log.v(TAG, "info.isConnected() " + info.isConnected());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "RSSI_CHANGED_ACTION mConnected");
            updateConnectionState(null);
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
/*            if (mWifiManager.startScanActive()) {
                mRetry = 0;
            } else */if (++mRetry >= 3) {
                mRetry = 0;
                //Toast.makeText(mContext, "can't find AP", Toast.LENGTH_LONG).show();
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    /* These values come from "wifi_network_setup" resource array */
    public static final int NETWORK_SETUP_MANUAL = 0;
    public static final int NETWORK_SETUP_WPS_PBC = 1;
    public static final int NETWORK_SETUP_WPS_KEYPAD = 2;
    public static final int NETWORK_SETUP_WPS_DISPLAY = 3;

    WpsInfo getWpsConfig(int networkSetupMethod, String password, AP accessPoint) {
        WpsInfo config = new WpsInfo();
        switch (networkSetupMethod) {
            case WPS_PBC:
                config.setup = WpsInfo.PBC;
                break;
            case WPS_KEYPAD:
                config.setup = WpsInfo.KEYPAD;
                break;
            case WPS_DISPLAY:
                config.setup = WpsInfo.DISPLAY;
                break;
            default:
                config.setup = WpsInfo.INVALID;
                Log.e(TAG, "WPS not selected type");
                return config;
        }
        config.pin = password;
        config.BSSID = (accessPoint != null) ? accessPoint.bssid : null;

//        config.proxySettings = mProxySettings;
//        config.ipAssignment = mIpAssignment;
//        config.linkProperties = new LinkProperties(mLinkProperties);
        return config;
    }

    private WifiConfiguration getConfig(AP accessPoint,
                                        String password,
                                        String ssid,
                                        int accessPointSecurity,
                                        String eap,
                                        String phase2,
                                        String caCert,
                                        String clientCert,
                                        String privateKey,
                                        String identity,
                                        String anonymousIdentity,
                                        boolean isSave) {
        if (accessPoint != null && accessPoint.networkId != INVALID_NETWORK_ID && !isSave) {
            return null;
        }

        WifiConfiguration config = new WifiConfiguration();

        if (accessPoint == null) {
            config.SSID = AP.convertToQuotedString(ssid);
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else if (accessPoint.networkId == INVALID_NETWORK_ID) {
            config.SSID = AP.convertToQuotedString(
                    accessPoint.ssid);
        } else {
            config.networkId = accessPoint.networkId;
        }

        switch (accessPointSecurity) {
            case AP.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;

            case AP.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (password.length() != 0) {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;

            case AP.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

            case AP.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
/*                config.eap.setValue(eap);

                config.phase2.setValue((phase2 == null) ? "" : "auth=" + phase2);
                config.ca_cert.setValue((caCert == null) ? "" :
                        KEYSTORE_SPACE + Credentials.CA_CERTIFICATE + caCert);
                config.client_cert.setValue((clientCert == null) ?
                        "" : KEYSTORE_SPACE + Credentials.USER_CERTIFICATE + clientCert);
                config.key_id.setValue((privateKey == null) ?
                        "" : KEYSTORE_SPACE + Credentials.USER_PRIVATE_KEY + privateKey);
                config.identity.setValue((identity == null) ? "" : identity);
                config.anonymous_identity.setValue((anonymousIdentity == null) ? "" : anonymousIdentity);
                if (password.length() != 0) {
                    config.password.setValue(password);
                }*/
                break;

            default:
                return null;
        }

        config.proxySettings = mProxySettings;
        config.ipAssignment = mIpAssignment;
        config.linkProperties = new LinkProperties(mLinkProperties);

        return config;
    }

    public void setAccessPoint(AP accessPoint) {
        mSelectedAccessPoint = accessPoint;
    }

    public void submit(int networkSetupMethod,
                       AP accessPoint,
                       String password,
                       String ssid,
                       int accessPointSecurity,
                       String eap,
                       String phase2,
                       String caCert,
                       String clientCert,
                       String privateKey,
                       String identity,
                       String anonymousIdentity,
                       boolean isSave) {

        switch (networkSetupMethod) {
            case WPS_PBC:
            case WPS_DISPLAY:
            case WPS_KEYPAD:
                //mWifiManager.startWps(getWpsConfig(networkSetupMethod, password, accessPoint));
                break;
            case MANUAL:
                final WifiConfiguration config = getConfig(accessPoint,
                        password,
                        ssid,
                        accessPointSecurity,
                        eap,
                        phase2,
                        caCert,
                        clientCert,
                        privateKey,
                        identity,
                        anonymousIdentity,
                        isSave);
                Log.d(TAG, "networkSetupMethod = "+ networkSetupMethod +" config = "+config);
                if (config == null) {
                    if (mSelectedAccessPoint != null
                            && !requireKeyStore(mSelectedAccessPoint.getConfig())
                            && mSelectedAccessPoint.networkId != INVALID_NETWORK_ID) {
                    	Log.d(TAG, "we are going to connect ID " + mSelectedAccessPoint.networkId);
                    	new Thread() {
                            public void run() {
                            	mWifiManager.connect(mSelectedAccessPoint.networkId, null);
                            }
                        }.start();
                        //mWifiManager.connect(mSelectedAccessPoint.networkId, null);
                    }
                } else if (config.networkId != INVALID_NETWORK_ID) {
                    if (mSelectedAccessPoint != null) {
                    	Log.d(TAG, "(mSelectedAccessPoint != null we are going to connect ID " + mSelectedAccessPoint.networkId);
                    	new Thread() {
                            public void run() {
                            	mWifiManager.connect(config, null);
                            }
                        }.start();
                        //mWifiManager.connect(config, null);
                    }
                } else {
                    if (isSave || requireKeyStore(config)) {
                    	new Thread() {
                            public void run() {
                            	mWifiManager.save(config, null);
                            }
                        }.start();
                        //mWifiManager.save(config, null);
                    } else {
                    	Log.d(TAG, "isSave || requireKeyStore(config) we are going to connect ID " + mSelectedAccessPoint.networkId);
                    	new Thread() {
                            public void run() {
                            	mWifiManager.save(config, null);
                            }
                        }.start();
                        //mWifiManager.connect(config, null);
                    }
                }
                break;
        }

        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        updateAPs();
    }

    public void forget() {
        mWifiManager.forget(mSelectedAccessPoint.networkId, null);

        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        updateAPs();
    }

    public void forgetConnecting() {
        WifiInfo info = getWiFiInfo();
        if (info != null) {
            int networdId = info.getNetworkId();
            mWifiManager.forget(networdId, null);
        }
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        updateAPs();
    }

    public DhcpInfo getDHCPInfo() {
        return mWifiManager.getDhcpInfo();
    }

    private class WifiServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AsyncChannel.CMD_CHANNEL_HALF_CONNECTED:
                    if (msg.arg1 == AsyncChannel.STATUS_SUCCESSFUL) {
                        //AsyncChannel in msg.obj
                    } else {
                        //AsyncChannel set up failure, ignore
                        Log.e(TAG, "Failed to establish AsyncChannel connection");
                    }
                    break;
//                case WifiManager.CMD_WPS_COMPLETED:
//                    WpsResult result = (WpsResult) msg.obj;
//                    if (result == null) break;
//
//                    switch (result.status) {
//                        case FAILURE:
//                            Log.v(TAG, "WPS connect failure");
//                            break;
//                        case IN_PROGRESS:
//                            Log.e(TAG, "WPS connecting");
//                            break;
//                        default:
//                            if (result.pin != null) {
//                                Log.v(TAG, "WPS PNI:" + result.pin);
//                            }
//                            break;
//                    }
//                    break;
//                //TODO: more connectivity feedback
//                default:
//                    //Ignore
//                    break;
            }
        }
    }

    /* 
     * 覆盖方法描述
     * @see com.unionman.settingwizard.wifi.AP.APCallback#notifyChanged(int, android.net.wifi.WifiConfiguration)
     */
    @Override
    public void notifyChanged(int level, WifiConfiguration config) {
        mWIFICtlCallback.notifyChanged(level, config);
    }

    /* 
     * 覆盖方法描述
     * @see com.unionman.settingwizard.wifi.AP.APCallback#notifyChanged(android.net.NetworkInfo.DetailedState, android.net.wifi.WifiConfiguration)
     */
    @Override
    public void notifyChanged(DetailedState state, WifiConfiguration config) {
        mWIFICtlCallback.notifyChanged(state, config);
    }

}
