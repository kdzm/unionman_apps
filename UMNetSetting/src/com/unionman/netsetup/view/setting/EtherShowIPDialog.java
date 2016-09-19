
package com.unionman.netsetup.view.setting;

import java.net.InetAddress;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unionman.netsetup.R;
import com.unionman.netsetup.util.Constant;

public class EtherShowIPDialog extends Dialog implements
        android.view.View.OnClickListener {

    private final static String TAG = "EtherShowIPDialog";
    private Context mContext;
    // EthernetManager object
    private EthernetManager mEthManager;

    // text of IP
    private TextView mIPText;
    // text of subnet
    private TextView mSubnetText;
    // text of DefaultGateway
    private TextView mDefaultGatewayText;
    // text of DNS
    private TextView mDnsText;
    // button of modify
    private Button mModifyBtn;

    public EtherShowIPDialog(Context context, EthernetManager ethManager) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        mEthManager = ethManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ether_setip_dialog);
        initView();
    }

    /**
     * The initialization of view
     */
    private void initView() {
        mIPText = (TextView) findViewById(R.id.dialog_ip_input);
        mIPText.setEnabled(false);
        mIPText.setBackground(null);
        mIPText.setFocusable(false);
        mIPText.setFocusableInTouchMode(false);
        mIPText.setTextColor(mContext.getResources().getColor(R.color.white));
        mSubnetText = (TextView) findViewById(R.id.dialog_subnet_input);
        mSubnetText.setEnabled(false);
        mSubnetText.setBackground(null);
        mSubnetText.setFocusable(false);
        mSubnetText.setFocusableInTouchMode(false);
        mSubnetText.setTextColor(mContext.getResources()
                .getColor(R.color.white));
        mDefaultGatewayText = (TextView) findViewById(R.id.dialog_defaultGateway_input);
        mDefaultGatewayText.setEnabled(false);
        mDefaultGatewayText.setBackground(null);
        mDefaultGatewayText.setFocusable(false);
        mDefaultGatewayText.setFocusableInTouchMode(false);
        mDefaultGatewayText.setTextColor(mContext.getResources().getColor(
                R.color.white));
        mDnsText = (TextView) findViewById(R.id.dialog_dns_input);
        mDnsText.setEnabled(false);
        mDnsText.setBackground(null);
        mDnsText.setFocusable(false);
        mDnsText.setFocusableInTouchMode(false);
        mDnsText.setTextColor(mContext.getResources().getColor(R.color.white));
        mModifyBtn = (Button) findViewById(R.id.modify_btn);
        mModifyBtn.setText(R.string.ok);
        mModifyBtn.setOnClickListener(this);
        mModifyBtn.requestFocus();

        // Read the IP connection information
        /*
         * DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo(); String IP =
         * NetworkUtils.intToInetAddress(dhcpInfo.ipAddress) .getHostAddress();
         * ip.setText(IP); String NETMASK =
         * NetworkUtils.intToInetAddress(dhcpInfo.netmask) .getHostAddress();
         * subnet.setText(NETMASK); String GATEWAY =
         * NetworkUtils.intToInetAddress(dhcpInfo.gateway) .getHostAddress();
         * defaultGateway.setText(GATEWAY); String DNS =
         * NetworkUtils.intToInetAddress(dhcpInfo.dns1) .getHostAddress();
         * dns.setText(DNS);
         */

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses()
                .iterator();
        if (!addrs.hasNext()) {
            if (Constant.LOG_TAG) {
                Log.e(TAG, "showDhcpIP:can not get LinkAddress!!");
            }
            return;
        }
        LinkAddress linkAddress = addrs.next();
        int prefixLength = linkAddress.getNetworkPrefixLength();
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String NETMASK = Netmask.getHostAddress();
        mSubnetText.setText(NETMASK);
        try {
            String IP = linkAddress.getAddress().getHostAddress();
            mIPText.setText(IP);
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    String GATEWAY = route.getGateway().getHostAddress();
                    mDefaultGatewayText.setText(GATEWAY);
                    break;
                }
            }
            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
            if (!dnses.hasNext()) {
                if (Constant.LOG_TAG) {
                    Log.e(TAG, "showDhcpIP:empty dns!!");
                }
            } else {
                String DNS = dnses.next().getHostAddress();
                if (null != DNS) {
                    mDnsText.setText(DNS);
                }
            }
        } catch (NullPointerException e) {
            if (Constant.LOG_TAG) {
                Log.w(TAG, "can not get IP" + e);
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        dismiss();
    }
}
