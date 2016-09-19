package com.um.upgrade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.ExitAppUtil;
import com.um.upgrade.base.MyApp;
import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.util.UpgradeUtil;

public class NetworkForceUpgradePromptActivity extends BaseActivity{
    private Context mContext = null;

    private final int DURATION = 1000;
    private final int COUNT = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_force_upgrade_prompt);

        mContext = this;
		TextView desTextView = (TextView) findViewById(R.id.network_force_fix_text_view);
		TextView countTextView = (TextView) findViewById(R.id.count_text_view);
		TextView curVerTextView = (TextView) findViewById(R.id.tv_current_version);
		TextView upVerTextView = (TextView) findViewById(R.id.tv_upgrade_version);
        TextView detailsTextView = (TextView) findViewById(R.id.network_force_details_text_view);
		desTextView.requestFocus();

		Intent intent = getIntent();
        final String fileType = intent.getStringExtra("fileType");
		String description = intent.getStringExtra("description");
		String currentVersion = intent.getStringExtra("verBeforeUpgrade");
		String upgradeVersion = intent.getStringExtra("verAfterUpgrade");

        String filePath = null;
        String[] filePaths = null;
        String[] details = null;

        if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
            filePath = intent.getStringExtra("filePath");
        } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
            filePaths = intent.getStringArrayExtra("filePaths");
            details = intent.getStringArrayExtra("details");
        }

		if(description != null)
		{
			desTextView.setText(description);
		}
		if(currentVersion != null)
		{
			curVerTextView.setText(currentVersion);
		}
		if(upgradeVersion != null)
		{
			upVerTextView.setText(upgradeVersion);
		}

        if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
            String detailsStr = null;
            for (String detail : details) {
                detailsStr = detail+"\n";
                detailsTextView.append(detailsStr);
            }
        }

        final TextView finalCountTextView = countTextView;

        final String finalFilePath = filePath != null ? filePath : null;
        final String[] finalFilePaths = filePaths != null ? filePaths : null;

        new Counter(DURATION, COUNT, new OnCountListener() {
            @Override
            public void onCounting(int dutation, int count) {
                if (count <= 0) {
                    if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
                        UpgradeUtil.systemRecovery(mContext, finalFilePath);
                    } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)){
                        for (int i = 0; i < finalFilePaths.length; i++) {
                            UpgradeUtil.apkInstall(mContext, finalFilePaths[i]);
                        }
                        finalCountTextView.setText(getString(R.string.installing));
                        waitUpgradeCompleted(true, finalFilePaths.length);
                    }
                } else {
                    finalCountTextView.setText(String.valueOf(count));
                }
            }
        }).start();
	}

	@Override
    protected void onResume() {
    	super.onResume();
        MyApp.setKeyDisable();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MyApp.setKeyEnable();
        finish();
    }

    private void waitUpgradeCompleted(final boolean isSuccess, int toInstallApksNum) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    Toast.makeText(mContext, getString(R.string.upgrade_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.upgrade_fail), Toast.LENGTH_LONG).show();
                }
                MyApp.setKeyEnable();
                ExitAppUtil.getInstance().exit();
            }
        };

        handler.postDelayed(runnable, 1000*10*toInstallApksNum);
    }

    private class Counter {
        private Handler handler = null;
        private OnCountListener listener = null;
        private CountRunnale countRunnale = null;

        private int duration = 0;
        private int count = 0;

        public Counter(int duration, int count, OnCountListener listener) {
            this.duration = duration;
            this.count = count;
            this.handler = new Handler();
            this.listener = listener;
        }

        public void start() {
            countRunnale = new CountRunnale();
            handler.postDelayed(countRunnale, duration);
        }

        private class CountRunnale implements Runnable {
            @Override
            public void run() {
                handler.removeCallbacks(countRunnale);
                listener.onCounting(duration, --count);
                if (count > 0) {
                    handler.postDelayed(countRunnale, duration);
                }
            }
        }
    }

    private interface OnCountListener {
        void onCounting(int dutation, int count);
    }

}
