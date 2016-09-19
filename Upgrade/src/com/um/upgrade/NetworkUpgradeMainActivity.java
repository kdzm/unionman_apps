package com.um.upgrade;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.MyApp;
import com.um.upgrade.data.DeviceInfo;
import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.util.AndroidUtils;
import com.um.upgrade.util.CheckUtil;
import com.um.upgrade.util.DownloadManagerPro;
import com.um.upgrade.util.FileUtil;
import com.um.upgrade.util.HttpUtils;
import com.um.upgrade.util.LogUtils;
import com.um.upgrade.util.NetworkDetector;
import com.um.upgrade.util.ParseConfigUtil;
import com.um.upgrade.util.PreferencesUtils;
import com.um.upgrade.util.UpgradeUtil;
import com.um.upgrade.widget.DownloadProcessButton;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class NetworkUpgradeMainActivity extends BaseActivity {
	private final String TAG = NetworkUpgradeMainActivity.class.getSimpleName();
    private final boolean LOG_EN = true;

    private static final int UPDATA_VIEW = 1;
    private static final int UPGRADE_CHECK = 2;
    private static final int LATEST_VERSION = 3;
    private static final int NETWORK_ERROR = 4;

	private EditText mServerAddrEditText = null;
	private DownloadProcessButton mUpgradeButton = null;
    private DownloadProcessButton mCableUpgradeButton = null;
    private DownloadManager mDownloadManager;
    private DownloadManagerPro mDownloadManagerPro;
    private UpgradeInfoBean mUpgrageInfo;
    private DeviceInfo mDeviceInfo;
    private Handler mHandler = new MyHandler(this);
    private long mDownloadId = 0;
    private DownloadChangeObserver mDownloadObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_upgrade_main);

        mDownloadObserver = new DownloadChangeObserver();
        mDownloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
        mServerAddrEditText = (EditText)findViewById(R.id.server_address);
        mUpgradeButton = (DownloadProcessButton)findViewById(R.id.network_upgrade_button);
        mCableUpgradeButton = (DownloadProcessButton) findViewById(R.id.cable_upgrade_button);
        setView();

        ((TextView)findViewById(R.id.save)).setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.setServerUrl(mServerAddrEditText.getText().toString());
                Toast.makeText(NetworkUpgradeMainActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
            }
        });

        ((TextView)findViewById(R.id.default_id)).setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerAddrEditText.setText(DefaultParameter.STB_SERVER_URL);
                MyApp.setServerUrl(DefaultParameter.STB_SERVER_URL);
                Toast.makeText(NetworkUpgradeMainActivity.this, R.string.set_default, Toast.LENGTH_SHORT).show();
            }
        });

    	mUpgradeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkDetector.getInstance(NetworkUpgradeMainActivity.this).NetwrokCheck()) {
                    Toast.makeText(NetworkUpgradeMainActivity.this, R.string.network_disconnect, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (mUpgradeButton.getStatus() == DownloadProcessButton.WORKING_NONE) {
                        mUpgradeButton.setStatus(DownloadProcessButton.WORKING_CHECK_CONFIG);
                        Toast.makeText(NetworkUpgradeMainActivity.this, R.string.upgrade_check, Toast.LENGTH_SHORT).show();
                        checkUpgrade("http://" + mServerAddrEditText.getText().toString() + DefaultParameter.UPGRADE_CONFIG);
                    } else if (mUpgradeButton.getStatus() == DownloadProcessButton.WORKING_CHECK_CONFIG) {
                        Toast.makeText(NetworkUpgradeMainActivity.this, R.string.upgrade_checking, Toast.LENGTH_SHORT).show();
                    } else if (mUpgradeButton.getStatus() == DownloadProcessButton.WORKING_DOWNLOAD){
                        Toast.makeText(NetworkUpgradeMainActivity.this, R.string.download_data, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCableUpgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NetworkUpgradeMainActivity.this, RecoveryUpgradeActivity.class));
            }
        });

        /* 判断是否是具有DVB功能，如果无DVB功能则隐藏Cable升级 */
//        if (!MyApp.hasDvb() || !DVB.isServerAlive()) {
            mCableUpgradeButton.setVisibility(View.INVISIBLE);
            mCableUpgradeButton.setEnabled(false);
            if (LOG_EN) Log.d(TAG, "this product is not a dvb product, hide the cable upgrade function");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadId = PreferencesUtils.getLong(this, DefaultParameter.KEY_NAME_DOWNLOAD_ID, 0);
        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, mDownloadObserver);
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mDownloadObserver);
    }

    private void updateView() {
        if (mDownloadId != 0) {
            int[] bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(mDownloadId);
            if (bytesAndStatus[1] > 0) {
                mHandler.sendMessage(mHandler.obtainMessage(UPDATA_VIEW, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
                LogUtils.d(" " + bytesAndStatus[0] + " " + bytesAndStatus[1] + " " + bytesAndStatus[2]);

                if (isDownloading(bytesAndStatus[2])) {
                    mUpgradeButton.setStatus(DownloadProcessButton.WORKING_DOWNLOAD);
                }
            }
        }
    }

    private boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    private void checkUpgrade(final String configUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String config = HttpUtils.httpGetString(configUrl);
                    if (config != null) {
                        InputStream in = new ByteArrayInputStream(config.getBytes());

                        List<UpgradeInfoBean> upgradeInfoBeanList =  ParseConfigUtil.getUpgradeInfo(in);
                        mUpgrageInfo = CheckUtil.getUpgrageInfo(upgradeInfoBeanList, MyApp.getDeviceInfo());
                        if (mUpgrageInfo != null) {
                            Message message = new Message();
                            message.what = UPGRADE_CHECK;
                            mHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = LATEST_VERSION;
                            mHandler.sendMessage(message);
                        }
                    } else {
                        Message message = new Message();
                        message.what = NETWORK_ERROR;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private long startDownLoad(String url) {
        LogUtils.d(url);
        Uri srcUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(srcUri);
        request.setDestinationUri(Uri.parse("file:///cache/update.zip"));
        request.setTitle(DefaultParameter.DOWNLOAD_FILE_NAME);
        request.setDescription(DefaultParameter.DOWNLOAD_FILE_NAME);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/unionman.update.download.file");

        long lastId = PreferencesUtils.getLong(this, DefaultParameter.KEY_NAME_DOWNLOAD_ID, 0);
        if (lastId != 0) {
            mDownloadManager.remove(lastId);
        }
        long downLoadId = mDownloadManager.enqueue(request);
        PreferencesUtils.putLong(this, DefaultParameter.KEY_NAME_DOWNLOAD_ID, downLoadId);
        PreferencesUtils.putString(this, DefaultParameter.KEY_NAME_DOWNLOAD_VERSION, mUpgrageInfo.getVersion());
        return downLoadId;
    }

    private void setView() {
        mDeviceInfo = MyApp.getDeviceInfo();
        mUpgradeButton.requestFocus();
        mServerAddrEditText.setText(MyApp.getServerUrl());
        ((TextView)findViewById(R.id.machine_model_val)).setText(mDeviceInfo.getMachineModel());
        ((TextView)findViewById(R.id.software_number_version)).setText(mDeviceInfo.getDisplaySoftVersion());
        ((TextView)findViewById(R.id.hardware_number_version)).setText(mDeviceInfo.getHardwareVersion());
        ((TextView)findViewById(R.id.serial_number_version)).setText(mDeviceInfo.getDisplaySerial());
        ((TextView)findViewById(R.id.loader_version_val)).setText(DefaultParameter.STB_LOADER_VERSION);
        ((TextView)findViewById(R.id.mac_addr_no)).setText(AndroidUtils.getHwAddress("eth0"));
    }

    private void saveInfos() {
        PreferencesUtils.putString(this, DefaultParameter.KEY_NAME_UPGRADE_DESCRIPTION, mUpgrageInfo.getDescription());
        PreferencesUtils.putString(this, DefaultParameter.KEY_NAME_UPGRADE_FILE_TYPE, mUpgrageInfo.getPacketList().get(0).getPacketType());
        PreferencesUtils.putString(this, DefaultParameter.KEY_NAME_UPGRADE_MODE, mUpgrageInfo.getUpdateMode());
        PreferencesUtils.putString(this, DefaultParameter.KEY_NAME_UPGRADE_SOFTWARE_VERSION, mUpgrageInfo.getVersion());
    }

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(mHandler);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateView();
            LogUtils.d("hjian: onChange, selfChange: " + selfChange);
        }

    }

    static class MyHandler extends Handler {
        WeakReference<NetworkUpgradeMainActivity> mActivity;

        MyHandler(NetworkUpgradeMainActivity activity) {
            mActivity = new WeakReference<NetworkUpgradeMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NetworkUpgradeMainActivity activity = mActivity.get();
            switch (msg.what) {
                case UPDATA_VIEW:
                    int status = (Integer)msg.obj;
                    if (status == DownloadManager.STATUS_RUNNING ||
                            status == DownloadManager.STATUS_PENDING) {
                        if (msg.arg2 < 0) {
                        } else {
                            activity.mUpgradeButton.setStatus(DownloadProcessButton.WORKING_DOWNLOAD);
                            int rate = getPercent(msg.arg1, msg.arg2);
                            activity.mUpgradeButton.setProgress(rate);
                        }
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        activity.mUpgradeButton.setStatus(DownloadProcessButton.WORKING_NONE);
                    }
                    break;
                case UPGRADE_CHECK:
                    activity.saveInfos();
                    String newVersion = activity.mUpgrageInfo.getVersion();
                    String downLoadedVersion = PreferencesUtils.getString(activity, DefaultParameter.KEY_NAME_DOWNLOAD_VERSION, "");
                    if (CheckUtil.parseSoftware(newVersion) > CheckUtil.parseSoftware(downLoadedVersion)) {
                        activity.mDownloadId = activity.startDownLoad(activity.mUpgrageInfo.getPacketList().get(0).getPacketUrl());
                    } else {
                        int[] bytesAndStatus = activity.mDownloadManagerPro.getBytesAndStatus(activity.mDownloadId);
                        if (FileUtil.fileExist(DefaultParameter.DOWNLOAD_FOLDER_NAME + File.separator + DefaultParameter.DOWNLOAD_FILE_NAME)
                                && (bytesAndStatus[2] == DownloadManager.STATUS_SUCCESSFUL)) {
                            activity.mUpgradeButton.setStatus(DownloadProcessButton.WORKING_NONE);
                            UpgradeUtil.upgrade(activity.mUpgrageInfo, activity.mDeviceInfo);
                        } else {
                            activity.mDownloadId = activity.startDownLoad(activity.mUpgrageInfo.getPacketList().get(0).getPacketUrl());
                        }
                    }
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(activity, R.string.server_no_response2, Toast.LENGTH_SHORT).show();
                    activity.mUpgradeButton.setStatus(DownloadProcessButton.WORKING_NONE);
                    break;
                case LATEST_VERSION:
                    Toast.makeText(activity, R.string.cur_sys_is_newest, Toast.LENGTH_SHORT).show();
                    activity.mUpgradeButton.setStatus(DownloadProcessButton.WORKING_NONE);
                    break;
                default:
                    break;
            }
        }
    }

    public static int getPercent(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int)((double)progress / max * 100);
        }
        return rate;
    }
}
