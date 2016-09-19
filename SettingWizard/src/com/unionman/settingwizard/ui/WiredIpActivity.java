package com.unionman.settingwizard.ui;

import java.net.InetAddress;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.ethernet.EthernetManager;
import android.net.NetworkUtils;
/*import android.net.DhcpInfoInternal;*/
import android.net.RouteInfo;
import android.net.pppoe.PppoeManager;
import android.net.PppoeStateTracker;
import android.net.EthernetDataTracker;
import android.widget.Toast;

public class WiredIpActivity extends Activity {
    private TextView mIp, mGateway, mSubnet, mDns1, mDns2;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wired_information);

        initViews();
    }

    private void initViews() {
        mIp = (TextView) this.findViewById(R.id.tv_wired_ip);
        mGateway = (TextView) this.findViewById(R.id.tv_wired_gateway);
        mSubnet = (TextView) this.findViewById(R.id.tv_wired_subnet_mask);
        mDns1 = (TextView) this.findViewById(R.id.tv_wired_dns1);
        mDns2 = (TextView) this.findViewById(R.id.tv_wired_dns2);
        mBackButton = (Button) this.findViewById(R.id.btn_wired_back);

        setInfos();
    }

    private void setInfos() {
        EthernetManager mEthManager = (EthernetManager) this.getSystemService("ethernet");

        ConnectivityManager mConnectivityManager = (ConnectivityManager) WiredIpActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
/*      DhcpInfoInternal mDhcpInfoInternal = new DhcpInfoInternal();
        mDhcpInfoInternal.getFromDhcpInfo(mEthManager.getSavedEthernetIpInfo());
        int prefixLength = mDhcpInfoInternal.prefixLength;
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String mNM = Netmask.toString();
        String[] arrNM = mNM.split("\\/");*/
        String[] arrNM;

        String mIP = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getAddresses().toString();
        String[] arrIP = mIP.split("/|\\[|\\]| ");

        String mGW = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getRoutes().toString();
        String[] arrGW = mGW.split("\\[|\\]| ");
        String mDns = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                .getDnses().toString();
        String[] arrDNS = mDns.split("/|\\[/|\\]| |,");
        if (arrIP.length != 0) {
            if (null != mIP) {
                mIp.setText(arrIP[2]);
            }
        }
        if (arrGW.length >= 3) {
            if (null != mGW) {
                mGateway.setText(arrGW[3]);
            }
        }
        if (arrDNS.length > 4) {
            if (null != arrDNS[1]) {
                mDns1.setText(arrDNS[1]);
            }
            if (null != arrDNS[4]) {
                mDns2.setText(arrDNS[4]);
            }
        }
/*        if (null != arrNM) {
            mSubnet.setText(arrNM[1]);
        }*/

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WiredIpActivity.this, DeviceInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView reflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        LinearLayout contentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(contentView, reflectedView);
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.more_time_to_exit), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
