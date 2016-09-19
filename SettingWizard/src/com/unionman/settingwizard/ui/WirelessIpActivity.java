package com.unionman.settingwizard.ui;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WirelessIpActivity extends Activity {
    private TextView mIp;
    private TextView mGateway;
    private TextView mSubnet;
    private TextView mDns1;
    private TextView mDns2;
    private Button mBackButton;
    DhcpInfo d;
    WifiManager wifii;
    private String s_dns1;
    private String s_dns2;
    private String s_gateway;
    private String s_ipAddress;
    private String s_leaseDuration;
    private String s_netmask;
    private String s_serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.wireless_information);
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        mIp = (TextView) this.findViewById(R.id.tv_ip);
        mGateway = (TextView) this.findViewById(R.id.tv_gateway);
        mSubnet = (TextView) this.findViewById(R.id.tv_subnet_mask);
        mDns1 = (TextView) this.findViewById(R.id.tv_dns1);
        mDns2 = (TextView) this.findViewById(R.id.tv_dns2);
        mBackButton = (Button) this.findViewById(R.id.btn_wireless_back);

        setInfos();
    }

    private void setInfos() {
        boolean a = checkWifi(WirelessIpActivity.this);
        if (a == false) {
            Toast.makeText(WirelessIpActivity.this, getResources().getString(R.string.wifi_off), Toast.LENGTH_SHORT).show();
            s_dns1 = null;
            s_dns2 = null;
            s_gateway = null;
            s_ipAddress = null;
            s_leaseDuration = null;
            s_netmask = null;
            s_serverAddress = null;
        } else {
            wifii = (WifiManager) getSystemService(WIFI_SERVICE);
            d = wifii.getDhcpInfo();
            s_dns1 = Formatter.formatIpAddress(d.dns1);
            s_dns2 = Formatter.formatIpAddress(d.dns2);
            s_gateway = Formatter.formatIpAddress(d.gateway);
            s_ipAddress = Formatter.formatIpAddress(d.ipAddress);
            s_leaseDuration = String.valueOf(d.leaseDuration);
            s_netmask = Formatter.formatIpAddress(d.netmask);
            s_serverAddress = Formatter.formatIpAddress(d.serverAddress);
        }
        mIp.setText(s_ipAddress);
        mGateway.setText(s_gateway);
        mSubnet.setText(s_netmask);
        mDns1.setText(s_dns1);
        mDns2.setText(s_dns2);
        Log.i("hello!!!", s_dns1 + s_dns2 + s_gateway + s_ipAddress + s_leaseDuration + s_netmask
                + s_serverAddress);

        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WirelessIpActivity.this, DeviceInfoActivity.class);
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

    public static boolean checkWifi(Activity activitiy) {
        WifiManager mWifiManager = (WifiManager) activitiy
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            System.out.println("**** WIFI is on");
            return true;
        } else {
            System.out.println("**** WIFI is off");
            return false;
        }
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
