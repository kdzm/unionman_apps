package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.SystemProperties;
import android.widget.Toast;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.network.EthCtl;
import com.unionman.settingwizard.util.BitmapCtl;

public class DeviceInfoActivity extends Activity {
    private final String TAG = DeviceInfoActivity.class.getSimpleName()+"--U668";
    private final boolean LOG_EN = false;
    private final String DEFAULT_SOFTWARE_VERTION = "3.0.0.3";
    private final String DEFAULT_HARDWARE_VERTION = "16509697";
    private WifiManager mWifiManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_information);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        initViews();
    }

    private void initViews() {
        setDeviceInfo();

        Button mWirelessButton = (Button) this.findViewById(R.id.btn_wireless_button);
        Button mWiredButton = (Button) this.findViewById(R.id.btn_wired_button);
        Button mBackButton = (Button) this.findViewById(R.id.btn_back);
        mWirelessButton.setFocusable(mWifiManager.getConnectionInfo() != null);
        mWiredButton.setFocusable(!new EthCtl(DeviceInfoActivity.this).getEthIP().equals(""));

        mBackButton.requestFocus();
        mWirelessButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceInfoActivity.this, WirelessIpActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mWiredButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceInfoActivity.this, WiredIpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setDeviceInfo() {
        TextView mDeviceModel = (TextView) this.findViewById(R.id.tv_device_model);
        TextView mDeviceSortNum = (TextView) this.findViewById(R.id.tv_device_sort_num);
        TextView mAndroidVersion = (TextView) this.findViewById(R.id.tv_android_version);
        TextView mSoftwareVersion = (TextView) this.findViewById(R.id.tv_software_version);
        TextView mHardwareVersion = (TextView) this.findViewById(R.id.tv_hardware_version);
        TextView macAddress = (TextView) this.findViewById(R.id.tv_mac_address);
        Context context = null;
        try {
            context = createPackageContext("com.um.networkupgrad", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        
        String tmpStbSerialno = "";
        tmpStbSerialno = SystemProperties.get("ro.serialno","").substring(0,DefaultParameter.STB_SERIAL_LEN);
        mDeviceSortNum.setText(tmpStbSerialno);
        String mac = SystemProperties.get("ro.serialno","").substring(DefaultParameter.STB_SERIAL_LEN);
        if (LOG_EN) Log.i(TAG, "SN:"+tmpStbSerialno+"  Length:"+tmpStbSerialno.length());
        if (LOG_EN) Log.i(TAG, "MAC:"+mac+"  Length:"+mac.length());
		if(mac.length()==DefaultParameter.STB_MAC_LEN)
		{
			macAddress.setText(mac.substring(0,2)+":"+mac.substring(2,4)+":"+mac.substring(4,6)+
						":"+ mac.substring(6,8)+":"+mac.substring(8,10)+":"+mac.substring(10,12));
		}
		
//        String serialno = SystemProperties.get("ro.serialno", "");  //序列号
//        String deviceSortNum = "ottonline_alios4.2";
        String androidVersion = android.os.Build.VERSION.RELEASE;// 系统版本
        String softwareVersion = SystemProperties.get("ro.cursoftware",DEFAULT_SOFTWARE_VERTION);  //软件版本号
        String hardwareVersion = SystemProperties.get("ro.hardwareversion",DEFAULT_HARDWARE_VERTION);  //硬件版本号
        String deviceSortNum = SystemProperties.get("ro.product.ummodel","");  //硬件版本号
        mDeviceModel.setText(deviceSortNum);
//        mDeviceModel.setText(deviceSortNum);
//        if (!serialno.equals("")) {
//            mDeviceSortNum.setText(serialno.substring(0, 17));
//            String mac = serialno.substring(17);
//            if (mac.length() == 12) {
//                macAddress.setText(mac.substring(0, 2) + ":" + mac.substring(2, 4) + ":" + mac.substring(4, 6) +
//                        ":" + mac.substring(6, 8) + ":" + mac.substring(8, 10) + ":" + mac.substring(10, 12));
//            }
//        }

        mAndroidVersion.setText(androidVersion);
        mSoftwareVersion.setText(softwareVersion);
        mHardwareVersion.setText(hardwareVersion);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        LinearLayout mContentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(mContentView, mReflectedView);
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(DeviceInfoActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//        super.onBackPressed();
//    }

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
