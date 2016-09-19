
package com.unionman.netsetup.view.setting;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.unionman.netsetup.R;
import com.unionman.netsetup.util.EthCtl;
import com.unionman.netsetup.util.IpAddrEdit;

/**
 * the dialog of set IP
 *
 * @author huyq
 */
public class EtherSetIPDialog extends Dialog implements
        android.view.View.OnClickListener {
    private static final String TAG = "EtherSetIPDialog";
	private Context mContext;
    private Handler mHandler;
    // EthernetManager object
    private EthernetManager mEthManager;

/*    // EditText of IP
    private EditText mIPEdit;
    // EditText of subnet
    private EditText mSubnetEdit;
    // EditText of DefaultGateway
    private EditText mDefaultGatewayEdit;
    // EditText of DNS
    private EditText mDnsEdit;*/
    // button of modify
    private Button mModifyBtn;
    
	String strIP;
    String strGW;
    String strDNS1;
    String strDNS2;
    String strSubnet;
    private EthCtl mNetworkCtl = null;

    IpAddrEdit ipEdit;
    IpAddrEdit subnetEdit;
    IpAddrEdit gwEdit;
    IpAddrEdit dsn1Edit;
    IpAddrEdit dsn2Edit;

    // public static ProgressDialog dialog;

    public EtherSetIPDialog(Context context, Handler handler) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        mHandler = handler;
        mEthManager = (EthernetManager) context
                .getSystemService(Context.ETHERNET_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ether_setip_dialog2);
        
/*        initView();
        checkDB();
        showIP();*/      
        mNetworkCtl = new EthCtl(mContext);
        mModifyBtn = (Button) findViewById(R.id.modify_btn);
        mModifyBtn.requestFocus();
        mModifyBtn.setOnClickListener(this);
        
        getDefault();
        setDefault();
    }

/*    *//**
     * The initialization of view
     *//*
    private void initView() {
        mIPEdit = (EditText) findViewById(R.id.ipet_ipaddr);
        mSubnetEdit = (EditText) findViewById(R.id.ipet_netmask);
        mDefaultGatewayEdit = (EditText) findViewById(R.id.ipet_gateway);
        mDnsEdit = (EditText) findViewById(R.id.ipet_dns1);
        mModifyBtn = (Button) findViewById(R.id.modify_btn);
        mModifyBtn.setOnClickListener(this);
    }


    *//**
     * check IP right or not
     *
     * @return
     *//*
    private boolean checkIP(String text) {
        if (text != null && !text.isEmpty()) {
            // The definition of regular expressions
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // To determine whether the IP address matches the regular
            // expression
            if (text.matches(regex)) {
                // Returns the judgment information
                return true;
            } else {
                // Returns the judgment information
                return false;
            }
        }
        // Returns the judgment information
        return false;
    }

    *//**
     * set default IP gateway .. to DB when dhcp failed and DB is empty
     *//*
    private void setDefIP() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses()
                .iterator();
        if (!addrs.hasNext()) {
            return;
        }
        LinkAddress linkAddress = addrs.next();
        int prefixLength = linkAddress.getNetworkPrefixLength();
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String NETMASK = Netmask.getHostAddress();
        mSubnetEdit.setText(NETMASK);
        String ipAddress = "0.0.0.0";
        String gateWay = "0.0.0.0";
        String DNS1 = "0.0.0.0";
        try {
            ipAddress = linkAddress.getAddress().getHostAddress();
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    gateWay = route.getGateway().getHostAddress();
                    break;
                }
            }
            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
            if (!dnses.hasNext()) {
                // Log.e(TAG, "showDhcpIP:empty dns!!");
            } else {
                DNS1 = dnses.next().getHostAddress();
            }
        } catch (NullPointerException e) {
            // Log.w(TAG, "can not get IP" + e);
        }
        // String ipAddress = "0.0.0.0";
        // String DNS1 = "0.0.0.0";
        // String gateWay = "0.0.0.0";
        // int prefixLength = 32;

        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress netmask = NetworkUtils.numericToInetAddress(NETMASK);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);

        DhcpInfo dhcpInfo = new DhcpInfo();
        dhcpInfo.ipAddress = NetworkUtils
                .inetAddressToInt((Inet4Address) ipaddr);
        dhcpInfo.gateway = NetworkUtils
                .inetAddressToInt((Inet4Address) getwayaddr);
        // dhcpInfo.netmask =
        // NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        dhcpInfo.netmask = NetworkUtils
                .inetAddressToInt((Inet4Address) netmask);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) idns1);
    }

    *//**
     * show IP gateway ... from pannel to DB when dhcp success
     *//*
    private void showIP() {
        if (!EtherSetting.DhcpIP.equals("")
                && !EtherSetting.DhcpGatewary.equals("")
                && !EtherSetting.DhcpMask.equals("")
                && !EtherSetting.DhcpDns.equals("")) {
            mIPEdit.setText(EtherSetting.DhcpIP);
            mDefaultGatewayEdit.setText(EtherSetting.DhcpGatewary);
            mSubnetEdit.setText(EtherSetting.DhcpMask);
            mDnsEdit.setText(EtherSetting.DhcpDns);
        } else {
            DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();

            String IP = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress)
                    .getHostAddress();
            if (IP == null) {
                setDefIP();
            }
            mIPEdit.setText(IP);
            String GATEWAY = NetworkUtils.intToInetAddress(dhcpInfo.gateway)
                    .getHostAddress();
            mDefaultGatewayEdit.setText(GATEWAY);
            String NETMASK = NetworkUtils.intToInetAddress(dhcpInfo.netmask)
                    .getHostAddress();
            mSubnetEdit.setText(NETMASK);
            String DNS = NetworkUtils.intToInetAddress(dhcpInfo.dns1)
                    .getHostAddress();
            mDnsEdit.setText(DNS);
        }
    }

    *//**
     * set IP gateway ... from pannel to DB when dhcp success
     *//*
    private void setIP() {
        String ipStr = mIPEdit.getText().toString();
        String gatewayStr = mDefaultGatewayEdit.getText().toString();
        String subnetStr = mSubnetEdit.getText().toString();
        String dnsStr = mDnsEdit.getText().toString();

        final DhcpInfo dhcpInfo = new DhcpInfo();
        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipStr);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gatewayStr);
        InetAddress inetmask = NetworkUtils.numericToInetAddress(subnetStr);
        InetAddress idns = NetworkUtils.numericToInetAddress(dnsStr);

        dhcpInfo.ipAddress = NetworkUtils
                .inetAddressToInt((Inet4Address) ipaddr);
        dhcpInfo.gateway = NetworkUtils
                .inetAddressToInt((Inet4Address) getwayaddr);
        dhcpInfo.netmask = NetworkUtils
                .inetAddressToInt((Inet4Address) inetmask);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) idns);

        mEthManager.saveEthernetIpInfo(dhcpInfo,
                EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
    }

    *//**
     * check settings DB ,if it is empty set a default value,otherwise do
     * nothing
     *
     * @return
     *//*
    private void checkDB() {
        DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();
        String ipAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress)
                .getHostAddress();
        if (ipAddress == null) {
            setDefIP();
        }
    }

    *//**
     * If you enter the ip is legal, setting this ip, otherwise modify failure
     *//*
    private void startStatic() {
        if (checkIP(mIPEdit.getText().toString())
                && checkIP(mSubnetEdit.getText().toString())
                && checkIP(mDefaultGatewayEdit.getText().toString())
                && checkIP(mDnsEdit.getText().toString())) {
            mEthManager.setEthernetEnabled(false);
            setIP();// set ip from title to DB
            mEthManager.setEthernetEnabled(true);
            dismiss();
        } else {
            Message message = mHandler.obtainMessage();
            message.what = NetStateDialog.SET_IP_FAILED;
            mHandler.sendMessage(message);
        }
    }*/
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.modify_btn) {
            Message message = mHandler.obtainMessage();
            message.what = NetStateDialog.SETTING_IP;
            mHandler.sendMessage(message);
            getCurValue();
            Log.i(TAG,"strIP="+ strIP);
            mNetworkCtl.setStaticEth(strIP, strGW, strSubnet, strDNS1, strDNS2);
            dismiss();
 //           startStatic();
        }

    }   

    private void getDefault() {
        String[] strDSN = mNetworkCtl.getEthDnsIP();
        strIP = mNetworkCtl.getEthIP();
        strGW = mNetworkCtl.getEthGatewallIP();
        strDNS1 = strDSN[0];
//        strDNS2 = strDSN[1];
        strSubnet = mNetworkCtl.getEthNetmask();
    }
    
    private void setDefault() {
        ipEdit = (IpAddrEdit) findViewById(R.id.ipet_ipaddr);
        subnetEdit = (IpAddrEdit) findViewById(R.id.ipet_netmask);
        gwEdit = (IpAddrEdit) findViewById(R.id.ipet_gateway);
        dsn1Edit = (IpAddrEdit) findViewById(R.id.ipet_dns1);
//        dsn2Edit = (IpAddrEdit) findViewById(R.id.ipet_dns2);
        
        ipEdit.setText(strIP);
        subnetEdit.setText(strSubnet);
        gwEdit.setText(strGW);
        dsn1Edit.setText(strDNS1);
//        dsn2Edit.setText(strDNS2);
    }
    private void getCurValue() {
        strIP = ipEdit.getText();
        strGW = gwEdit.getText();
        strDNS1 = dsn1Edit.getText();
//        strDNS2 = dsn2Edit.getText();
        strSubnet = subnetEdit.getText();
    }

}
