package com.unionman.settingwizard.network;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.unionman.settingwizard.wifi.AP;

/**
 * Created by Administrator on 13-7-30.
 */
public class WIFITest implements WiFiCtl.WIFICtlCallback {
    private static final String TAG = "WIFITest";
    private final WifiManager mWifiManager;
    private Context mContext;
    private WiFiCtl mWIFICtl = null;
    private AP mAccessPoint = null;
    private boolean isConnected = false;

    public WIFITest(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnected()) {
            Log.v(TAG, "already connect");
            return true;
        } else
            return false;
    }

    public void startTest() {
        mWIFICtl = new WiFiCtl(mContext, (WiFiCtl.WIFICtlCallback) this);

        mWIFICtl.startWIFI();
        mWIFICtl.startScan();

        ArrayList<AP> accessPointList = mWIFICtl.getAccessPointList();

        for (AP accessPoint : accessPointList) {
            Log.v(TAG, accessPoint.toString());
        }

        mAccessPoint = accessPointList.get(0);
        mWIFICtl.setAccessPoint(mAccessPoint);

        mWIFICtl.submit(WiFiCtl.MANUAL, mAccessPoint, "1234567890", "TP-LINK_783414",
                mAccessPoint.getSecurity(), null, null, null, null, null, null, null, true);

        DhcpInfo ipInfo = mWIFICtl.getDHCPInfo();

        String s_dns1 = Formatter.formatIpAddress(ipInfo.dns1);
        String s_dns2 = Formatter.formatIpAddress(ipInfo.dns2);
        String s_gateway = Formatter.formatIpAddress(ipInfo.gateway);
        String s_ipAddress = Formatter.formatIpAddress(ipInfo.ipAddress);
        String s_leaseDuration = String.valueOf(ipInfo.leaseDuration);
        String s_netmask = Formatter.formatIpAddress(ipInfo.netmask);
        String s_serverAddress = Formatter.formatIpAddress(ipInfo.serverAddress);

        Log.v(TAG, " IP=" + s_ipAddress + " GateWay=" + s_gateway + " Netmask=" + s_netmask
                + " ServerAddr=" + s_serverAddress + " DNS1=" + s_dns1 + " DNS2=" + s_dns2
                + " Duration=" + s_leaseDuration);
    }

    @Override
    public void onResult(int wifiState, ArrayList<AP> list) {

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                Log.v(TAG, "WifiManager.WIFI_STATE_ENABLED");
                Log.v(TAG, "config: " + mWifiManager.getConnectionInfo());

                for (AP accessPoint : list) {
                    //Log.v(TAG, accessPoint.toString());

                    if (!isConnected && accessPoint.ssid.equals("TP-LINK_783414")) {
                        Log.v(TAG, "found ssid " + accessPoint.ssid);
                        isConnected = true;
                        mWIFICtl.setAccessPoint(accessPoint);
                        mWIFICtl.submit(WiFiCtl.MANUAL,
                                accessPoint,
                                "1234567890",
                                "TP-LINK_783414",
                                accessPoint.getSecurity(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                true);

                        DhcpInfo ipInfo = mWIFICtl.getDHCPInfo();

                        String s_dns1 = Formatter.formatIpAddress(ipInfo.dns1);
                        String s_dns2 = Formatter.formatIpAddress(ipInfo.dns2);
                        String s_gateway = Formatter.formatIpAddress(ipInfo.gateway);
                        String s_ipAddress = Formatter.formatIpAddress(ipInfo.ipAddress);
                        String s_leaseDuration = String.valueOf(ipInfo.leaseDuration);
                        String s_netmask = Formatter.formatIpAddress(ipInfo.netmask);
                        String s_serverAddress = Formatter.formatIpAddress(ipInfo.serverAddress);

                        Log.v(TAG, " IP=" + s_ipAddress +
                                " GateWay=" + s_gateway +
                                " Netmask=" + s_netmask +
                                " ServerAddr=" + s_serverAddress +
                                " DNS1=" + s_dns1 +
                                " DNS2=" + s_dns2 +
                                " Duration=" + s_leaseDuration);
                        mWIFICtl.stopScan();
                    }
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.v(TAG, "WifiManager.WIFI_STATE_ENABLING");
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                Log.v(TAG, "WifiManager.WIFI_STATE_DISABLING");
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                Log.v(TAG, "WifiManager.WIFI_STATE_DISABLED");
                break;
            default:
                break;
        }
    }

    @Override
    public void notifyChanged(int level, WifiConfiguration config) {

        if (config == null) {
            Log.v(TAG, "Level=" + level);
        } else {
            Log.v(TAG, "Level=" + level + " NetworkId=" + config.networkId + " SSID=" + config.SSID);
        }
    }

    /* 
     * 覆盖方法描述
     * @see com.unionman.settingwizard.network.WiFiCtl.WIFICtlCallback#notifyChanged(android.net.NetworkInfo.DetailedState, android.net.wifi.WifiConfiguration)
     */
    @Override
    public void notifyChanged(DetailedState status, WifiConfiguration config) {
        // TODO Auto-generated method stub

    }
}
