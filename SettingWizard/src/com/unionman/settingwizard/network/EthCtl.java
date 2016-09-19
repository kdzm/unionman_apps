package com.unionman.settingwizard.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.pppoe.PppoeManager;
import android.util.Log;
import android.net.ethernet.EthernetManager;
import android.net.EthernetDataTracker;
import java.net.Inet4Address;
import android.net.LinkProperties;
/*import android.net.DhcpInfoInternal;*/

import java.net.InetAddress;
import java.util.Iterator;
import android.net.LinkAddress;

/**
 * Created by Administrator on 13-7-28.
 */
public class EthCtl {
    private String TAG = "NetworkCtl";
    private EthernetManager mEthManager = null;
    private StatusCallBack mNetworkStatusCB = null;
    private IntentFilter mIntentFilter = null;
    private int mEthernetCurrentStatus = -1;
    private Context mContext = null;

    private final BroadcastReceiver mEthSettingsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int message = -1;
            int rel = -1;

            if (intent.getAction().equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                message = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, rel);
            }

            switch (message) {
                case EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED;
                    break;
                case EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED;
                    break;
                case EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED;
                    break;
                case EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED;
                    break;
                case EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED;
                    break;
                case EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED;
                    break;
                case EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED;
                    break;
                case EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED;
                    break;
                case EthernetDataTracker.EVENT_PHY_LINK_UP:
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_PHY_LINK_UP;
                    break;
                case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
//                try {
//                    int SwitchEnable = Settings.System.getInt(resolver,
//                            Settings.Secure.ETHERNET_ON);
//                }catch (SettingNotFoundException e1) {
//                    e1.printStackTrace();
//                }
                    mNetworkStatusCB.onReceive(context, intent, message);
                    mEthernetCurrentStatus = EthernetDataTracker.EVENT_PHY_LINK_DOWN;
                    break;
                default:
                    break;
            }
        }
    };

    public EthCtl(Context context) {
        mIntentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);

        mContext = context;
        mEthManager = (EthernetManager) context.getSystemService("ethernet");
    }

    public void startDHCP(boolean changSystemSetting) {
        //需要改变系统设这，保证和系统设置一致

        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetDefaultConf();
        mEthManager.setEthernetEnabled(true);
    }

    public void stopEth() {
        mEthManager.enableEthernet(false);
        mEthManager.setEthernetEnabled(false);
    }

    public void startEth() {
        mEthManager.enableEthernet(true);
        mEthManager.setEthernetEnabled(true);
    }

    public String getEthNetmask() {

      //andorid 4.2
/*       
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
        DhcpInfoInternal mDhcpInfoInternal = new DhcpInfoInternal();
        mDhcpInfoInternal.getFromDhcpInfo(mEthManager.getSavedEthernetIpInfo());
        int prefixLength = mDhcpInfoInternal.prefixLength;
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String mNM = Netmask.toString();
        String[] arrNM = mNM.split("\\/");

        if (null != arrNM) {
            return arrNM[1];
        }
        return "";
*/
        //android 4.4
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses()
                .iterator();
        if (!addrs.hasNext()) {
            return "";
        }
        LinkAddress linkAddress = addrs.next();
        int prefixLength = linkAddress.getNetworkPrefixLength();
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);        
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String mNM = Netmask.toString();
        String[] arrNM = mNM.split("\\/");

        if (null != arrNM) {
            return arrNM[1];
        }
        return "";      
    }

    public String getEthIP() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        String mIP = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getAddresses().toString();
        String[] arrIP = mIP.split("/|\\[|\\]| ");

        if (arrIP.length != 0) {
            return arrIP[2];
        }

        return "";
    }

    public String getEthGatewallIP() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        String mGW = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getRoutes().toString();
        String[] arrGW = mGW.split("\\[|\\]| ");
         Log.i(TAG,"mGW="+mGW);
         for(int m=0;m<arrGW.length;m++){
             Log.i(TAG,"mGW["+m+"]="+arrGW[m]);
         }
        if (arrGW.length >= 6) {
            return arrGW[6];
        }

        return "";
    }

    public String[] getEthDnsIP() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        String mDns = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getDnses().toString();
        String[] arrDNS = mDns.split("/|\\[/|\\]| |,");
	        Log.i(TAG,"mDns="+mDns);
	        for(int m=0;m<arrDNS.length;m++){
	            Log.i(TAG,"arrDNS["+m+"]="+arrDNS[m]);
	        }
        if (arrDNS.length > 4) {

            return new String[]{arrDNS[1], arrDNS[4]};
        }

        return new String[]{"", ""};

    }

    public void setStaticEth(String ipAddress, String gateWay, String netMask, String DNS1, String DNS2) {
    	//android 4.2
    	/*
        android.net.DhcpInfoInternal DHCPInfoInternal = new android.net.DhcpInfoInternal();

        mEthManager.setEthernetEnabled(false);

        InetAddress iRoute = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress iNetmask = NetworkUtils.numericToInetAddress(netMask);

        try {
            int netmask = NetworkUtils.inetAddressToInt(iNetmask);
            int prefixLength = NetworkUtils.netmaskIntToPrefixLength(netmask);
            DHCPInfoInternal.prefixLength = prefixLength;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, " Network may disconnected " + e);
        }

        DHCPInfoInternal.ipAddress = ipAddress;
        DHCPInfoInternal.addRoute(new RouteInfo(iRoute));
        DHCPInfoInternal.dns1 = DNS1;
        DHCPInfoInternal.dns2 = DNS2;
        DHCPInfoInternal.serverAddress = gateWay;

        mEthManager.saveEthernetIpInfo(DHCPInfoInternal.makeDhcpInfo(),
                EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);

        mEthManager.setEthernetEnabled(true);
    */
      //android 4.4
        android.net.DhcpInfo dhcpInfo = new android.net.DhcpInfo();
        mEthManager.setEthernetEnabled(false);
        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress inetmask = NetworkUtils.numericToInetAddress(netMask);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
        InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);

        dhcpInfo.ipAddress = NetworkUtils
                .inetAddressToInt((Inet4Address) ipaddr);
        dhcpInfo.gateway = NetworkUtils
                .inetAddressToInt((Inet4Address) getwayaddr);
        dhcpInfo.netmask = NetworkUtils
                .inetAddressToInt((Inet4Address) inetmask);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) idns1);
        dhcpInfo.dns2 = NetworkUtils.inetAddressToInt((Inet4Address) idns2);

        mEthManager.saveEthernetIpInfo(dhcpInfo,EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
        mEthManager.setEthernetEnabled(true);	
    }

    public void startBroadcast(Context context, StatusCallBack cb) {
        mNetworkStatusCB = cb;
        context.registerReceiver(mEthSettingsReceiver, mIntentFilter);
    }

    public void stopBroadcast(Context context) {
        context.unregisterReceiver(mEthSettingsReceiver);
        mNetworkStatusCB = null;
    }

    public interface StatusCallBack {
        public void onReceive(Context context, Intent intent, int message);
    }
}
