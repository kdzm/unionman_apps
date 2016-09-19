
package com.unionman.netsetup.logic.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.util.Log;

import com.unionman.netsetup.R;
import com.unionman.netsetup.logic.factory.InterfaceLogic;
import com.unionman.netsetup.model.WidgetType;

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
//        mInfo = new WidgetType();
//        mInfo.setName(res.getStringArray(R.array.net_state)[1]);
//        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
//        // access to broadband dial-up state, the state setInfo
//        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[1]);
//        mInfo.setInfo(res.getString(isPppoeConnected ? R.string.netstat_connected
//                : R.string.netstat_unconnected));
//        mWidgetList.add(mInfo);

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
        
        // DNS2:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[7]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // DNS access to the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[5]);
        mInfo.setInfo(mDNS2 == null ? "" : mDNS2);
        mWidgetList.add(mInfo);
        
     // WifiMAC:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[8]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // DNS access to the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[5]);
        mInfo.setInfo(mMAC == null ? "" : mMAC);
        mWidgetList.add(mInfo);
        
     // etherMAC:
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.net_state)[9]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // DNS access to the state, the state setInfo
        // mInfo.setInfo(res.getStringArray(R.array.net_state_info)[5]);
        mInfo.setInfo(mEtherMAC == null ? "" : mEtherMAC);
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
    private String mDNS2 = null;
    private String mMAC = null;
    private String mEtherMAC = null;
    

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
        
        mMAC = ((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        mEtherMAC=getEthernetMacAddress();

        if (isEtherConnected) {
            EthernetManager mEthManager = (EthernetManager) mContext
                    .getSystemService(Context.ETHERNET_SERVICE);
            DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();
            if (dhcpInfo != null) {
                mIpAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
                mNetMask = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
                mGateway = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
                mDNS = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
                mDNS2 = NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
                
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
                mDNS2 = NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
               
            }
        }
    }
    
    
    private String getEthernetMacAddress() {  
        String mac = null;  
        BufferedReader bufferedReader = null;
        Process process = null;  
        try {  
            process = Runtime.getRuntime().exec("busybox ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            String line = null;  
            int index = -1;  
            while ((line = bufferedReader.readLine()) != null) {  
                index = line.toLowerCase().indexOf("hwaddr");// find string [hwaddr]  
                if (index >= 0) {
                    mac = line.substring(index +"hwaddr".length()+ 1).trim(); //extract mac address which trimed spaces in head and tail  
                    break;  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (bufferedReader != null) {  
                    bufferedReader.close();  
                }  
            } catch (IOException e1) {  
                e1.printStackTrace();  
            }  
            bufferedReader = null;  
            process = null;  
        }  
        return mac;
    }
 
}
