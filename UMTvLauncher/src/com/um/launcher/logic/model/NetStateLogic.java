
package com.um.launcher.logic.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.NetworkUtils;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.um.launcher.R;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;

/**
 * net state
 *
 * @author wangchuanjian
 */
public class NetStateLogic implements InterfaceLogic {

    private Context mContext;

    public NetStateLogic(Context mContext) {
        super();
        this.mContext = mContext;
        refreshNetStat();
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        WidgetType mInfo = null;
        // local connection
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[0]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to the local connection state, the state setInfoo
        mInfo.setInfo(res.getString(isEtherConnected ? R.string.netstat_connected
                : R.string.netstat_unconnected));
        mWidgetList.add(mInfo);

        // broadband dial-up
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[1]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to broadband dial-up state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[1]);
        mInfo.setInfo(res.getString(isPppoeConnected ? R.string.netstat_connected
                : R.string.netstat_unconnected));
        mWidgetList.add(mInfo);

        // wireless connection
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[2]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to wireless connection to the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[2]);
        mInfo.setInfo(res.getString(isWifiConnected ? R.string.netstat_connected
                : R.string.netstat_unconnected));
        mWidgetList.add(mInfo);

        // Broadband:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[3]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to broadband address of the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[3]);
        mInfo.setInfo(mIpAddress == null ? "" : mIpAddress);
        mWidgetList.add(mInfo);

        // subnet mask:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[4]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to the subnet mask of the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[4]);
        mInfo.setInfo(mNetMask == null ? "" : mNetMask);
        mWidgetList.add(mInfo);

        // Gateway:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[5]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // access to the gateway of the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[4]);
        mInfo.setInfo(mGateway == null ? "" : mGateway);
        mWidgetList.add(mInfo);

        // DNS:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[6]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // DNS access to the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[5]);
        mInfo.setInfo(mDNS == null ? "" : mDNS);
        mWidgetList.add(mInfo);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
    }

    private boolean isEtherConnected = false;
    private boolean isPppoeConnected = false;
    private boolean isWifiConnected = false;
    private String mIpAddress = null;
    private String mNetMask = null;
    private String mGateway = null;
    private String mDNS = null;

    private void refreshNetStat() {
        ConnectivityManager connectivity = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            isEtherConnected = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
                    .isConnected();
            isPppoeConnected = connectivity.getNetworkInfo(ConnectivityManager.TYPE_PPPOE)
                    .isConnected();
            isWifiConnected = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnected();
            if(isEtherConnected)
                isWifiConnected = false;
        }

        if (isEtherConnected) {
            EthernetManager mEthManager = (EthernetManager) mContext
                    .getSystemService(Context.ETHERNET_SERVICE);
            DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();
            if (dhcpInfo != null) {
                mIpAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
                mNetMask = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
                mGateway = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
                mDNS = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
            }
        }

        if (isWifiConnected
                && (mIpAddress == null || mIpAddress.equals("") || mIpAddress.equals("0.0.0.0"))) {
            WifiManager mWifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
            if (dhcpInfo != null) {
                mIpAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
                mNetMask = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
                mGateway = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
                mDNS = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
            }
        }
    }
}
