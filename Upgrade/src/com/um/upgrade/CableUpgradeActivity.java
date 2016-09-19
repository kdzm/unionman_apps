package com.um.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.MyApp;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Upgrade;
import com.um.upgrade.data.DeviceInfo;
import com.um.upgrade.util.AndroidUtils;
import com.um.upgrade.util.UpgradeUtil;

public class CableUpgradeActivity extends BaseActivity{
    private final String TAG = CableUpgradeActivity.class.getSimpleName();

    private Context mContext = null;
    private TextView mStepTextView = null;
    private ProgressBar mUpgradeProgressBar = null;
    private TextView mStbSwVersion = null;
    private TextView mStbHwVersion = null;
    private TextView mStbSerialNo = null;
    private TextView mStbMacNo = null;
    private TextView mLoaderVersion = null;
    private TextView mMachineModelTextView = null;
    private Handler mUpgradeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cable_upgrade);
        mContext = this;

        mStepTextView = (TextView) findViewById(R.id.step_text_view);
        mUpgradeProgressBar = (ProgressBar) findViewById(R.id.upgrade_progress_bar);
        mStbSwVersion = (TextView) findViewById(R.id.software_number_version);
        mStbHwVersion = (TextView) findViewById(R.id.hardware_number_version);
        mStbSerialNo = (TextView) findViewById(R.id.serial_number_version);
        mStbMacNo = (TextView) findViewById(R.id.mac_addr_no);
        mLoaderVersion = (TextView)findViewById(R.id.loader_version_val);
        mMachineModelTextView = (TextView)findViewById(R.id.machine_model_val);

        DeviceInfo deviceInfo = MyApp.getDeviceInfo();
        mStbSwVersion.setText(deviceInfo.getDisplaySoftVersion());
        mStbHwVersion.setText(deviceInfo.getHardwareVersion());
        mStbSerialNo.setText(deviceInfo.getDisplaySerial());
        mStbMacNo.setText(AndroidUtils.getHwAddress("eth0"));

//        String mac = MyApp.getMac();
//		if(mac.length()==DefaultParameter.STB_MAC_LEN)
//		{
//			mStbMacNo.setText(mac.substring(0,2)+":"+mac.substring(2,4)+":"+mac.substring(4,6)+
//						":"+ mac.substring(6,8)+":"+mac.substring(8,10)+":"+mac.substring(10,12));
//		}
        /*当前系统没定义，写定该值*/
        mLoaderVersion.setText(DefaultParameter.STB_LOADER_VERSION);
        mMachineModelTextView.setText(deviceInfo.getMachineModel());
		
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.um.upgrade.CABLE_DOWNLOAD_PROGRESS");
        intentFilter.addAction("com.um.upgrade.CABLE_CRC_PROGRESS");
        intentFilter.addAction("com.um.upgrade.CABLE_DOWNLOAD_SUCCESS");
        intentFilter.addAction("com.um.upgrade.CABLE_DOWNLOAD_ERROR");
        registerReceiver(mUpgradeProgressReceiver, intentFilter);

        int upgradeType = getIntent().getExtras().getInt("upgradeType", 0);
        Log.i(TAG, "接收到的升级类型"+ upgradeType);
        
        Upgrade upgrade = new Upgrade(DVB.getInstance());

        Log.i(TAG, "new Upgrade成功");

        switch (upgradeType) {
            case 1:
                Log.i(TAG, "触发升级");
                upgrade.UpgradeProcess(upgradeType, 0, 0, 0, 0, 0);
                break;
            case 2:
                Log.i(TAG, "手动升级");
                Bundle bundle = getIntent().getExtras();
                int upgradeFreq = bundle.getInt("upgradeFreq", 0);
                int upgradeSymbol = bundle.getInt("upgradeSymbol", 0);
                int upgradePid = bundle.getInt("upgradePid", 0);
                int upgradeQam = bundle.getInt("upgradeQam", 0);
                upgrade.UpgradeProcess(upgradeType, 0, upgradeFreq, upgradeSymbol, upgradeQam, upgradePid);
                break;
            default:
                Log.i(TAG, "其他升级类型");
                break;
        }
    }

    private BroadcastReceiver mUpgradeProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.um.upgrade.CABLE_DOWNLOAD_PROGRESS")) {
            	int downloadProgress = intent.getExtras().getInt("downloadProgress", 0);
                Log.i(TAG, "下载进度："+String.valueOf(downloadProgress));
                mStepTextView.setText(getString(R.string.download_proress) + " " + String.valueOf(downloadProgress) + "%");
                mUpgradeProgressBar.setProgress(intent.getExtras().getInt("downloadProgress", 0));
            } else if (intent.getAction().equals("com.um.upgrade.CABLE_CRC_PROGRESS")) {
            	int crcProgress = intent.getExtras().getInt("crcProgress",0);
                Log.i(TAG, "校验进度："+String.valueOf(crcProgress));
                mStepTextView.setText(getString(R.string.crc_progress) + " " + String.valueOf(crcProgress) + "%");
                mUpgradeProgressBar.setProgress(crcProgress);
            } else if (intent.getAction().equals("com.um.upgrade.CABLE_DOWNLOAD_ERROR")) {
                Toast.makeText(mContext, getString(R.string.download_error), Toast.LENGTH_LONG).show();
//                mContext.finish();
                //同方测试，重启系统
                mUpgradeHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent i = new Intent(Intent.ACTION_REBOOT);
		    			i.putExtra("nowait", 1);
		    			i.putExtra("interval", 1);
		    			i.putExtra("window", 0);
		    			sendBroadcast(i);
		    			Log.i(TAG, "下载失败，重启系统（同方认证）");
					}
				}, 1000*3);
            } else if (intent.getAction().equals("com.um.upgrade.CABLE_DOWNLOAD_SUCCESS")) {
                Log.i(TAG, "下载完成，启动recovery");
                Toast.makeText(mContext, "下载完成，启动recovery", Toast.LENGTH_LONG).show();
                try {
                    String filePath = Environment.getDownloadCacheDirectory().toString()+"/"+"update.zip";
                    Log.i(TAG, "更新文件路径："+filePath);
                    UpgradeUtil.systemRecovery(mContext, filePath);
//                    new com.unionman.SystemUpgrade.Upgrade(mContext, filePath).start();
//                    RecoverySystem.installPackage(mContext,new File(filePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

	@Override
	protected void onPause() {
		super.onPause();
        MyApp.setKeyEnable();
	}

	@Override
	protected void onResume() {
		super.onResume();
        MyApp.setKeyDisable();
	}
 }
