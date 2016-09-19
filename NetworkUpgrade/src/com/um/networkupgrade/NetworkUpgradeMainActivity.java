package com.um.networkupgrade;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.SystemProperties;
import android.os.UserHandle;

public class NetworkUpgradeMainActivity extends Activity {

	private static final String TAG = "NetworkUpgrade--NetworkUpgrade";
	private EditText mServerAddress = null;
	private TextView mStbVersionText = null;
	private TextView mStbHARDVersionText = null;
	private TextView mStbSerialno = null;
	private TextView mStbMACno = null;
	private TextView mVersion=null;
	private Button mSaveButton = null;
	private Button mDefaultButton = null;
	private Button mUpgradeButton = null;
	private String mURL = null;
	private String mStbVersion = null;
	private final String mDefaultURL = DefaultParameter.STB_DEFAULT_URL;
	private final String mDefaultVER = DefaultParameter.STB_SAFEWARE_DEFAULT_VERSION;
	private final String mDefaultHARD = DefaultParameter.STB_HARDWARE_DEFAULT_VERSION;
	private SharedPreferences mUrlPreferences = null;
	private SharedPreferences mStbPreferences = null;
	private Intent mServiceIntent = new Intent("com.um.networkupgrade.SERVICE");
	private NetworkDetector mNetwrokDetector = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_upgrade_main2);

		mNetwrokDetector = new NetworkDetector(getApplicationContext());

		mServerAddress = (EditText) findViewById(R.id.server_address);
		mStbVersionText = (TextView) findViewById(R.id.software_number_version);
		mStbHARDVersionText = (TextView) findViewById(R.id.hardware_number_version);
		mSaveButton = (Button) findViewById(R.id.save);
		mDefaultButton = (Button) findViewById(R.id.default_id);
		mStbSerialno = (TextView) findViewById(R.id.serial_number_version);
		mStbMACno = (TextView) findViewById(R.id.mac_addr_no);
		mUpgradeButton = (Button) findViewById(R.id.network_upgrade_button);

		mVersion=(TextView)findViewById(R.id.tv_version);
		showOwnVersion();
		
		mUpgradeButton.requestFocus();
		
		mStbHARDVersionText.setText(StbManager.getSystemProperties("ro.build.product", ""));

		mURL = StbManager.getSystemProperties("persist.sys.um.upgrade.url", mDefaultURL);
		Logger.d(TAG, "mURL:" + mURL);
		mServerAddress.setText(mURL);

		String serialNo = StbManager.getSystemProperties("ro.serialno", "");
		if(!("".equals(serialNo)))
		{
		//	String serialNomac =serialNo.substring(0, serialNo.length() - DefaultParameter.STB_MAC_LEN);
		//	mStbSerialno.setText(serialNomac);
			String strSerialno = SystemProperties.get("ro.serialno", "");
			if (strSerialno.length() < 32)
			{
				mStbSerialno.setText( strSerialno.substring(0, strSerialno.length()));
			}else
			{
				mStbSerialno.setText( strSerialno.substring(0, 32));
			}
			
			String macAddr = null;
			if(serialNo.length() >= 12)
			{
				macAddr = serialNo.substring(serialNo.length() - DefaultParameter.STB_MAC_LEN, serialNo.length());
				Logger.i(TAG, "macAddr:" + macAddr);
			}
			mStbMACno.setText( SystemProperties.get("ro.mac", ""));
//			if((macAddr != null) && (DefaultParameter.STB_MAC_LEN == macAddr.length()))
//			{
//				mStbMACno.setText(macAddr.substring(0, 2) + ":"
//					+ macAddr.substring(2, 4) + ":" + macAddr.substring(4, 6) + ":"
//					+ macAddr.substring(6, 8) + ":" + macAddr.substring(8, 10)
//					+ ":" + macAddr.substring(10, 12));
//			}
			
		}

		mStbVersion = StbManager.getSystemProperties("ro.build.version.incremental", "");
		mStbVersionText.setText(mStbVersion);

		mSaveButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				StbManager.setSystemProperties("persist.sys.um.upgrade.url",
						mServerAddress.getText().toString());
				ToastUtil.showToast(NetworkUpgradeMainActivity.this, R.string.save_success);
			}
		});

		mDefaultButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				StbManager.setSystemProperties("persist.sys.um.upgrade.url", mDefaultURL);
				mServerAddress.setText(mDefaultURL);
				ToastUtil.showToast(NetworkUpgradeMainActivity.this, R.string.set_default);
			}
		});

		mUpgradeButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mNetwrokDetector.NetwrokCheck() != true) {
					ToastUtil.showToast(NetworkUpgradeMainActivity.this, R.string.network_disconnect);
					return;
				} else {
					ToastUtil.showToast(NetworkUpgradeMainActivity.this, R.string.upgrade_check);
				}
				mServiceIntent.putExtra("URL", mServerAddress.getText().toString());
				mServiceIntent.putExtra("from", "fromActivity");
				startService(mServiceIntent);
			}
		});
	};

	private void showOwnVersion() {
		 try {
		        PackageManager manager = this.getPackageManager();
		        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
		        String version = info.versionName;
		        mVersion.setText("V"+version);
		    } catch (Exception e) {
		        e.printStackTrace();
		  }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
