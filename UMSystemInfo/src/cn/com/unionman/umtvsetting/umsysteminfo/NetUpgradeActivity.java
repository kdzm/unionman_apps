package cn.com.unionman.umtvsetting.umsysteminfo;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.um.huanauth.data.HuanClientAuth;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusFactory;

public class NetUpgradeActivity extends Activity {
	private static final String TAG = "NetUpgradeActivity";
	private Button mButton;
	private Switch mSwitch;
	private TextView mAppid;
	private TextView mSoftVersion;
	private TextView mDeviceTyte;
	private TextView mDeviceId;
	private TextView mDnum;
	private TextView mProjectId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.net_upgrade);
        mSwitch = (Switch)findViewById(R.id.auto_switch);
        mButton = (Button)findViewById(R.id.manual_button);
        mAppid = (TextView)findViewById(R.id.app_id);
        mProjectId = (TextView)findViewById(R.id.project_id);
        mSoftVersion = (TextView)findViewById(R.id.software_number_version);
        mDeviceTyte = (TextView)findViewById(R.id.device_type);
        mDeviceId = (TextView)findViewById(R.id.device_id);
        mDnum = (TextView)findViewById(R.id.dnum);
        initData();
        init();
	}
	
	private void initData(){
		String softVersion = "";
		String appId = "";
		String deviceType = "";
		String deviceId = "";
		String dnum = "";
		String projectId = "";
		
		softVersion = SystemProperties.get("ro.umtv.sw.version","");
		appId = softVersion.substring(0, 14);
		
		HuanClientAuth huanClientAuth = new HuanClientAuth(this);
		deviceType = huanClientAuth.getDevicemode();
		deviceId = huanClientAuth.getDeviceid();
		dnum = huanClientAuth.getDnum();
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		projectId = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);
		if (projectId == null){
			projectId = "";
		}
		mAppid.setText(appId);
		mProjectId.setText(projectId);
		mSoftVersion.setText(softVersion);
		mDeviceTyte.setText(deviceType);
		mDeviceId.setText(deviceId);
		mDnum.setText(dnum);
		
	}
	
	private void init(){
		 boolean checked = true;
		 
		 checked = getAutoCheckFlag();
		 mSwitch.setChecked(checked);
		 mSwitch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					delay();
				}
			});
	        
	        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					delay();
					saveAutoCheckFlag(arg1);
				}
			});
	        
	        mButton.setOnFocusChangeListener(new View.OnFocusChangeListener(){
	        	@Override
	        	public void onFocusChange(View arg0, boolean arg1) {
	        		delay();
	        	}
	        });
	        
	        mButton.setOnClickListener(new View.OnClickListener(){
	        	@Override
	        	public void onClick(View arg0) {
	        		delay();
	        		Intent service = new Intent("com.um.huanauth.UpgradeService.ACTION");
	        		startService(service);
	        	}
	        });
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i(TAG, "onWindowFocusChanged hasFocus=" + hasFocus);
		if (hasFocus) {
			delay();
		} else {
			finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
		}
		super.onWindowFocusChanged(hasFocus);

	}

	/**
	 * handler of finish activity
	 */
	private Handler finishHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.ACTIVITY_FINISH)
				finish();
		}
	};

	/**
	 * set delay time to finish activity
	 */
	public void delay() {
		Log.i(TAG, "delay() is calling");
		finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
		Message message = new Message();
		message.what = Constant.ACTIVITY_FINISH;
		finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
	}
	
	private void saveAutoCheckFlag(boolean autoCheck){
		 Editor sharedata = getSharedPreferences("AutoCheck", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS).edit();  
	      sharedata.putBoolean("AutoCheckFlag",autoCheck); 
	      sharedata.commit(); 
	}
	
	private boolean getAutoCheckFlag(){
        SharedPreferences sp = getSharedPreferences(
                "AutoCheck", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS);
        boolean autoCheck = sp.getBoolean("AutoCheckFlag", true);
        
        return autoCheck;
	}
}
