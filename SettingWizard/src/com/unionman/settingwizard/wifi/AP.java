package com.unionman.settingwizard.wifi;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WpsResult;

/**
 * Created by Administrator on 13-7-29.
 */
public class AP {
    private String TAG = "SettingWizard.AP";
    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    public static final int INVALID_NETWORK_ID = -1;

    public enum PskType {
        UNKNOWN,
        WPA,
        WPA2,
        WPA_WPA2
    }

    ;

    private Context mContext;
    public String ssid;
    public String bssid;
    private int security;
    public int networkId = INVALID_NETWORK_ID;
    private boolean wpsAvailable = false;

    private int mRssi = Integer.MAX_VALUE;
    private WifiInfo mInfo;
    private NetworkInfo.DetailedState mState;
    private WifiConfiguration mConfig;
    private WifiInfo mLastInfo;
    private NetworkInfo.DetailedState mLastState;
    private APCallback mAPCallback = null;
    private ScanResult mScanResult = null;
    private PskType pskType = PskType.UNKNOWN;

    public AP(Context context, WifiConfiguration config, APCallback cb) {
        mContext = context;
        mAPCallback = cb;
        mConfig = config;
        loadConfig(config);
    }

    public String toString() {
        return "SSID=" + ssid + " BSSID=" + bssid + " SECURITY=" + security + " NETWORKID=" + networkId + " WPS=" + wpsAvailable + " Levle=" + getLevel();
    }

    public boolean isExist() {
        return mRssi != Integer.MAX_VALUE;
    }

    public AP(Context context, ScanResult resul, APCallback cb) {
        mContext = context;
        mAPCallback = cb;

        loadResult(resul);
    }

    public int getSecurity() {
        return security;
    }

    public void loadResult(ScanResult result) {
        ssid = result.SSID;
        bssid = result.BSSID;
        security = getSecurity(result);
        wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
        if (security == SECURITY_PSK)
            pskType = getPskType(result);
        networkId = -1;
        mRssi = result.level;
        mScanResult = result;
    }

    public void update(WifiInfo info, NetworkInfo.DetailedState state) {
        boolean reorder = false;
        if (info != null && networkId != WifiConfiguration.INVALID_NETWORK_ID
                && networkId == info.getNetworkId()) {
            reorder = (mInfo == null);
            mRssi = info.getRssi();
            mInfo = info;
            mState = state;
            mAPCallback.notifyChanged(state, mConfig);
        } else if (mInfo != null) {
            reorder = true;
            mInfo = null;
            mState = null;
            mAPCallback.notifyChanged(state, mConfig);
        }
        if (reorder) {
            //notifyHierarchyChanged();
        }
    }

    public interface APCallback {
        void notifyChanged(int level, WifiConfiguration config);

        void notifyChanged(DetailedState state, WifiConfiguration config);
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    public boolean update(ScanResult result) {
        Log.e(TAG, "update result " + " ossid " + result.SSID + "ssid " + ssid
                + " comlevel " + WifiManager.compareSignalLevel(result.level, mRssi)
                + " mRssi " + mRssi + " level " + result.level);

        if (ssid.equals(result.SSID) && security == getSecurity(result)) {
//            if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
            Log.e(TAG, "update level ssid " + ssid);
            mRssi = result.level;
            bssid = result.BSSID;
//            }
            // This flag only comes from scans, is not easily saved in config
            if (security == SECURITY_PSK) {
                pskType = getPskType(result);
            }
            mAPCallback.notifyChanged(getLevel(), mConfig);
            return true;
        }
        return false;
    }

    private PskType getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    public int getLevel() {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, 4);
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    private int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private void loadConfig(WifiConfiguration config) {
        ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
        bssid = config.BSSID;
        security = getSecurity(config);
        networkId = config.networkId;
        mRssi = Integer.MAX_VALUE;
        mConfig = config;
    }

    public String removeDoubleQuotes(String string) {
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    /**
     * Generate and save a default wifiConfiguration with common values.
     * Can only be called for unsecured networks.
     *
     * @hide
     */
    protected void generateOpenNetworkConfig() {
        if (security != SECURITY_NONE)
            throw new IllegalStateException();
        if (mConfig != null)
            return;
        mConfig = new WifiConfiguration();
        mConfig.SSID = convertToQuotedString(ssid);
        mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
    }
}
