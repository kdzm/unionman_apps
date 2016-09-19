package com.unionman.netsetup.view.setting;

import com.unionman.netsetup.R;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class AccessPoint implements Comparable<Object> {
    static final String TAG = "Wifi.AccessPoint";

    private static final String KEY_DETAILEDSTATE = "key_detailedstate";
    private static final String KEY_WIFIINFO = "key_wifiinfo";
    private static final String KEY_SCANRESULT = "key_scanresult";
    private static final String KEY_CONFIG = "key_config";

    private static final int[] STATE_SECURED = { R.attr.state_encrypted };
    private static final int[] STATE_NONE = {};

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    enum PskType {
        UNKNOWN, WPA, WPA2, WPA_WPA2
    }

    public String ssid;
    public String bssid;
    public int security;
    public int networkId;
    boolean wpsAvailable = false;

    PskType pskType = PskType.UNKNOWN;

    private Context mContext;
    private WifiConfiguration mConfig;
    /* package */ScanResult mScanResult;

    private int mRssi;
    private WifiInfo mInfo;
    private DetailedState mState;
    private String mAccessPointSummary = "";

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
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

    public String getSecurityString(boolean concise) {
        switch (security) {
        case SECURITY_EAP:
            return concise ? mContext
                    .getString(R.string.wifi_security_short_eap) : mContext
                    .getString(R.string.wifi_security_eap);
        case SECURITY_PSK:
            switch (pskType) {
            case WPA:
                return concise ? mContext
                        .getString(R.string.wifi_security_short_wpa) : mContext
                        .getString(R.string.wifi_security_wpa);
            case WPA2:
                return concise ? mContext
                        .getString(R.string.wifi_security_short_wpa2)
                        : mContext.getString(R.string.wifi_security_wpa2);
            case WPA_WPA2:
                return concise ? mContext
                        .getString(R.string.wifi_security_short_wpa_wpa2)
                        : mContext.getString(R.string.wifi_security_wpa_wpa2);
            case UNKNOWN:
            default:
                return concise ? mContext
                        .getString(R.string.wifi_security_short_psk_generic)
                        : mContext
                                .getString(R.string.wifi_security_psk_generic);
            }
        case SECURITY_WEP:
            return concise ? mContext
                    .getString(R.string.wifi_security_short_wep) : mContext
                    .getString(R.string.wifi_security_wep);
        case SECURITY_NONE:
        default:
            return concise ? "" : mContext
                    .getString(R.string.wifi_security_none);
        }
    }

    private static PskType getPskType(ScanResult result) {
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

    AccessPoint(Context context, WifiConfiguration config) {
        mContext = context;
        loadConfig(config);
        refresh();
    }

    AccessPoint(Context context, ScanResult result) {
        mContext = context;
        loadResult(result);
        refresh();
    }

    private void loadConfig(WifiConfiguration config) {
        ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
        bssid = config.BSSID;
        security = getSecurity(config);
        networkId = config.networkId;
        mRssi = Integer.MAX_VALUE;
        mConfig = config;
    }

    private void loadResult(ScanResult result) {
        ssid = result.SSID;
        bssid = result.BSSID;
        security = getSecurity(result);
        wpsAvailable = security != SECURITY_EAP
                && result.capabilities.contains("WPS");
        if (security == SECURITY_PSK)
            pskType = getPskType(result);
        networkId = -1;
        mRssi = result.level;
        mScanResult = result;
    }

    @Override
    public int compareTo(Object otherAP) {
        if (!(otherAP instanceof AccessPoint)) {
            return 1;
        }
        AccessPoint other = (AccessPoint) otherAP;
        // Active one goes first.
        if (mInfo != null && other.mInfo == null)
            return -1;
        if (mInfo == null && other.mInfo != null)
            return 1;

        // Reachable one goes before unreachable one.
        if (mRssi != Integer.MAX_VALUE && other.mRssi == Integer.MAX_VALUE)
            return -1;
        if (mRssi == Integer.MAX_VALUE && other.mRssi != Integer.MAX_VALUE)
            return 1;

        // Configured one goes before unconfigured one.
        if (networkId != WifiConfiguration.INVALID_NETWORK_ID
                && other.networkId == WifiConfiguration.INVALID_NETWORK_ID)
            return -1;
        if (networkId == WifiConfiguration.INVALID_NETWORK_ID
                && other.networkId != WifiConfiguration.INVALID_NETWORK_ID)
            return 1;

        // Sort by signal strength.
        int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
        if (difference != 0) {
            return difference;
        }
        // Sort by ssid.
        return ssid.compareToIgnoreCase(other.ssid);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AccessPoint))
            return false;
        return (this.compareTo((AccessPoint) other) == 0);
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (mInfo != null)
            result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * networkId;
        result += 29 * ssid.hashCode();
        return result;
    }

    boolean update(ScanResult result) {
        if (ssid.equals(result.SSID) && security == getSecurity(result)) {
            mRssi = result.level;
            // This flag only comes from scans, is not easily saved in config
            if (security == SECURITY_PSK) {
                pskType = getPskType(result);
            }
            refresh();
            return true;
        }
        return false;
    }

    void update(WifiInfo info, DetailedState state) {
        boolean reorder = false;
        if (info != null && networkId != WifiConfiguration.INVALID_NETWORK_ID
                && networkId == info.getNetworkId()) {
            reorder = (mInfo == null);
            mRssi = info.getRssi();
            mInfo = info;
            mState = state;
            refresh();
        } else if (mInfo != null) {
            reorder = true;
            mInfo = null;
            mState = null;
            refresh();
        }
    }

    public int getLevel() {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, 4);
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    public WifiInfo getInfo() {
        return mInfo;
    }

    public DetailedState getState() {
        return mState;
    }

    public static String removeDoubleQuotes(String string) {
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

    /** Updates the title and summary; may indirectly call notifyChanged() */
    private void refresh() {
        if (mConfig != null
                && mConfig.status == WifiConfiguration.Status.DISABLED) {
            switch (mConfig.disableReason) {
            case WifiConfiguration.DISABLED_AUTH_FAILURE:
                mAccessPointSummary = mContext
                        .getString(R.string.wifi_disabled_password_failure);
                break;
            case WifiConfiguration.DISABLED_DHCP_FAILURE:
            case WifiConfiguration.DISABLED_DNS_FAILURE:
                mAccessPointSummary = mContext
                        .getString(R.string.wifi_disabled_network_failure);
                break;
            case WifiConfiguration.DISABLED_UNKNOWN_REASON:
                mAccessPointSummary = mContext
                        .getString(R.string.wifi_disabled_generic);
            }
        } else if (mRssi == Integer.MAX_VALUE) { // Wifi out of range
            mAccessPointSummary = mContext
                    .getString(R.string.wifi_not_in_range);
        } else if (mState != null) { // This is the active connection
            String[] formats = mContext.getResources().getStringArray(
                    R.array.wifi_status);
            int index = mState.ordinal();
            if (index >= formats.length || formats[index].length() == 0) {
                mAccessPointSummary = null;
            } else {
                mAccessPointSummary = formats[index];
            }
        } else { // In range, not disabled.
            StringBuilder summary = new StringBuilder();
            if (mConfig != null) { // Is saved network
                summary.append(mContext.getString(R.string.wifi_remembered));
            }

            if (security != SECURITY_NONE) {
                String securityStrFormat;
                if (summary.length() == 0) {
                    securityStrFormat = mContext
                            .getString(R.string.wifi_secured_first_item);
                } else {
                    securityStrFormat = mContext
                            .getString(R.string.wifi_secured_second_item);
                }
                summary.append(String.format(securityStrFormat,
                        getSecurityString(true)));
            }

            if (mConfig == null && wpsAvailable) { // Only list WPS available
                                                   // for unsaved networks
                if (summary.length() == 0) {
                    summary.append(mContext
                            .getString(R.string.wifi_wps_available_first_item));
                } else {
                    summary.append(mContext
                            .getString(R.string.wifi_wps_available_second_item));
                }
            }
            mAccessPointSummary = summary.toString();
        }
    }

    public String getSummary() {
        return mAccessPointSummary;
    }

    /**
     * Generate and save a default wifiConfiguration with common values. Can
     * only be called for unsecured networks.
     *
     * @hide
     */
    protected void generateOpenNetworkConfig() {
        if (security != SECURITY_NONE)
            throw new IllegalStateException();
        if (mConfig != null)
            return;
        mConfig = new WifiConfiguration();
        mConfig.SSID = AccessPoint.convertToQuotedString(ssid);
        mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
    }
}
