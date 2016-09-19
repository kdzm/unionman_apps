package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.network.EthCtl;
import com.unionman.settingwizard.util.BitmapCtl;
import com.unionman.settingwizard.util.IpAddrEdit;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.PropertyUtils;

public class WiredNetworkSetup extends Activity implements EthCtl.StatusCallBack {

    private static final String TAG = "WiredNetworkSetup";
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

    ImageView mReflectedView;
    LinearLayout mContentView;
    IpAddrEdit mIpAddrView;
    IpAddrEdit mSubnetView;
    IpAddrEdit mGwView;
    IpAddrEdit mDSN1View;
    IpAddrEdit mDSN2View;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wired_network_setup2);

//        mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
//        mContentView = (LinearLayout) findViewById(R.id.content_layout);
//        new BitmapCtl().setReflection(mContentView, mReflectedView);

        //        Button connectBtn = (Button) findViewById(R.id.btn_connect);
        Button nextStepBtn = (Button) findViewById(R.id.btn_next_step);
        Button lastStepBtn = (Button) findViewById(R.id.btn_last_step);
        nextStepBtn.setOnClickListener(new MyClickListener());
        lastStepBtn.setOnClickListener(new MyClickListener());
        //        connectBtn.setOnClickListener(new MyClickListener());

        mIpAddrView = (IpAddrEdit) findViewById(R.id.ipet_ipaddr);
        mSubnetView = (IpAddrEdit) findViewById(R.id.ipet_netmask);
        mGwView = (IpAddrEdit) findViewById(R.id.ipet_gateway);
        mDSN1View = (IpAddrEdit) findViewById(R.id.ipet_dns1);
        mDSN2View = (IpAddrEdit) findViewById(R.id.ipet_dns2);
        mIpAddrView.setOnFocusChangeListener(focusListener);
        mSubnetView.setOnFocusChangeListener(focusListener);
        mGwView.setOnFocusChangeListener(focusListener);
        mDSN1View.setOnFocusChangeListener(focusListener);
        mDSN2View.setOnFocusChangeListener(focusListener);


        mNetworkCtl = new EthCtl(this);

        getDefault();
        setDefault();
    }

/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        mContentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(mContentView, mReflectedView);
    }*/

    public OnFocusChangeListener focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {/*
            Log.v(">>>>>>>>>>>>>>>>>", "" + view + hasFocus);

            LinearLayout layout = (LinearLayout) view;
            EditText ipAddr1 = (EditText) layout.getChildAt(0);
            EditText ipAddr2 = (EditText) layout.getChildAt(1);
            EditText ipAddr3 = (EditText) layout.getChildAt(2);
            EditText ipAddr4 = (EditText) layout.getChildAt(3);

            if (hasFocus) {
                ipAddr1.setTextColor(Color.WHITE);
                ipAddr2.setTextColor(Color.WHITE);
                ipAddr3.setTextColor(Color.WHITE);
                ipAddr4.setTextColor(Color.WHITE);
            } else {
                ipAddr1.setTextColor(Color.BLACK);
                ipAddr2.setTextColor(Color.BLACK);
                ipAddr3.setTextColor(Color.BLACK);
                ipAddr4.setTextColor(Color.BLACK);
            }
        */}

    };

    private void getDefault() {
        String[] strDSN = mNetworkCtl.getEthDnsIP();
        strIP = mNetworkCtl.getEthIP();
        strGW = mNetworkCtl.getEthGatewallIP();
        strDNS1 = strDSN[0];
        strDNS2 = strDSN[1];
        strSubnet = mNetworkCtl.getEthNetmask();
    }

    private void getCurValue() {
        strIP = ipEdit.getText();
        strGW = gwEdit.getText();
        strDNS1 = dsn1Edit.getText();
        strDNS2 = dsn2Edit.getText();
        strSubnet = subnetEdit.getText();
    }

    private void setDefault() {
        ipEdit = (IpAddrEdit) findViewById(R.id.ipet_ipaddr);
        subnetEdit = (IpAddrEdit) findViewById(R.id.ipet_netmask);
        gwEdit = (IpAddrEdit) findViewById(R.id.ipet_gateway);
        dsn1Edit = (IpAddrEdit) findViewById(R.id.ipet_dns1);
        dsn2Edit = (IpAddrEdit) findViewById(R.id.ipet_dns2);

        ipEdit.setText(strIP);
        subnetEdit.setText(strSubnet);
        gwEdit.setText(strGW);
        dsn1Edit.setText(strDNS1);
        dsn2Edit.setText(strDNS2);
    }

    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    getCurValue();
                    Log.v("<<<<<<<<<<<<<<curValue  ", strIP);
                    mNetworkCtl.setStaticEth(strIP, strGW, strSubnet, strDNS1, strDNS2);
                    boolean hasPreInstalled = PropertyUtils.getInt("persist.sys.dvb.installed", 0) == 1;
       					intent = new Intent();
       	                if (hasPreInstalled) {
       	                    intent.setClass(WiredNetworkSetup.this, SetupFinishActivity.class);
       	                } else {
       	                    intent.putExtra("isInSettingWizard", true);
       	                    intent.setClassName("com.unionman.dvbcitysetting", "com.unionman.dvbcitysetting.CitySettingActivity");
       	                }
       			    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(WiredNetworkSetup.this, NetworkSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        mNetworkCtl.startBroadcast(this, this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mNetworkCtl.stopBroadcast(this);
        super.onPause();
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* 
     * 覆盖方法描述
     * @see com.unionman.settingwizard.network.EthCtl.StatusCallBack#onReceive(android.content.Context, android.content.Intent, int)
     */
    @Override
    public void onReceive(Context context, Intent intent, int message) {
        // TODO Auto-generated method stub

    }
}
