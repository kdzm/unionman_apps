package com.um.huanauth;

import com.um.huanauth.R;
import com.um.huanauth.data.HuanClientAuth;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView mDeviceId;
	private TextView mDnum;
	private TextView mDeviceMode;
	private TextView mActiveKey;
	private TextView mDidToken;
	private TextView mToken;
	private TextView mHuanId;
	private TextView mLicensetype;
	private TextView mLicensedata;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDeviceId = (TextView)findViewById(R.id.deviceid);
		mDnum = (TextView)findViewById(R.id.devicenum);
		mDeviceMode = (TextView)findViewById(R.id.devicemode);
		mActiveKey = (TextView)findViewById(R.id.activekey);
		mDidToken = (TextView)findViewById(R.id.didtoken);
		mToken = (TextView)findViewById(R.id.token);
		mHuanId = (TextView)findViewById(R.id.huanid);
		mLicensetype = (TextView)findViewById(R.id.licensetype);
		mLicensedata = (TextView)findViewById(R.id.licensedata);
		
		initvalues();
	}
	
	private void initvalues(){
		String deviceid = "";
		String dnum = "";
		String devicemode = "";
		String activekey = "";
		String didtoken = "";
		String token = "";
		String huanid = "";
		String licensetype = "";
		String licensedata = "";
		
		HuanClientAuth huanClientAuth = new HuanClientAuth(this);
    	deviceid = huanClientAuth.getDeviceid();
        dnum = huanClientAuth.getDnum();
        devicemode = huanClientAuth.getDevicemode();
        activekey = huanClientAuth.getActivekey(); 
        didtoken = huanClientAuth.getDidtoken();
        token = huanClientAuth.getToken();
        huanid = huanClientAuth.getHuanid();
        licensetype = huanClientAuth.getLicensetype();
        licensedata = huanClientAuth.getLicensedata(licensetype);
        
        mDeviceId.setText("deviceid:"+deviceid);
        mDnum.setText("dnum:"+dnum);
        mDeviceMode.setText("devicemode:"+devicemode);
        mActiveKey.setText("activekey:"+activekey);
        mDidToken.setText("didtoken:"+didtoken);
        mToken.setText("token:"+token);
        mHuanId.setText("huanid:"+huanid);
        mLicensetype.setText("licensetype:"+licensetype);
        mLicensedata.setText("licensedata:"+licensedata);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
}
