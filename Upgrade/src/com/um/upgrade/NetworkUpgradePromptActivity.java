package com.um.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.ExitAppUtil;
import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.util.RuntimeCmdUtil;
import com.um.upgrade.util.UpgradeUtil;

import java.io.File;


public class NetworkUpgradePromptActivity extends BaseActivity{
	private final String TAG = NetworkUpgradePromptActivity.class.getSimpleName();
    private final boolean LOG_EN = true;

    private Context mContext = null;

	private void startUpgrade(String fileType, String filePath) {

		if(fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
            UpgradeUtil.systemRecovery(mContext, filePath);
            return ;
		}
		else if(fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
			RuntimeCmdUtil.chmod("777", filePath);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            startActivity(intent);
            return ;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_upgrade_prompt);
        mContext = this;

        Intent intent = this.getIntent();
        final String fileType = intent.getStringExtra("fileType");
        String filePath = null;
        String[] filePaths = null;
        String[] details = null;

        if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
            filePath = intent.getStringExtra("filePath");
        } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
            filePaths = intent.getStringArrayExtra("filePaths");
            details = intent.getStringArrayExtra("details");
        }

        final String finalFilePath = filePath != null ? filePath : null;
        final String[] finalFilePaths = filePaths != null? filePaths : null;

        final String verBeforeUpgrade = intent.getStringExtra("verBeforeUpgrade");
        final String description = intent.getStringExtra("description");
        final String verAfterUpgrade = intent.getStringExtra("verAfterUpgrade");

        Button upgradeYesButton = (Button)findViewById(R.id.btn_network_upgrade_yes);
        Button upgradeNoButton = (Button)findViewById(R.id.btn_network_upgrade_no);
        TextView curVerTextView = (TextView) findViewById(R.id.tv_current_version);
        TextView upgradeVersion = (TextView) findViewById(R.id.tv_upgrade_version);
        TextView fixProblemTextView = (TextView) findViewById(R.id.network_manual_fix_text_view);
        TextView detailsTextView = (TextView) findViewById(R.id.network_manual_details_text_view);
        upgradeNoButton.requestFocus();

        if(description != null)
        {
        	fixProblemTextView.setText(description);
        }
        if(verAfterUpgrade != null)
        {
        	upgradeVersion.setText(" " + verAfterUpgrade);
        }
        if(verBeforeUpgrade != null)
        {
        	curVerTextView.setText(" " + verBeforeUpgrade);
        }

        if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
            String detailsStr = null;
            for (String detail : details) {
                detailsStr = detail+"\n";
                detailsTextView.append(detailsStr);
            }
        }
        
        upgradeYesButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
                    UpgradeUtil.systemRecovery(mContext, finalFilePath);
                } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
                    for (int i = 0; i < finalFilePaths.length; i++) {
                        UpgradeUtil.apkInstall(mContext, finalFilePaths[i]);
                    }
                    waitUpgradeCompleted(true, finalFilePaths.length);
                }

            }
        });
        
        upgradeNoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    private void waitUpgradeCompleted(final boolean isSuccess, int toInstallApksNum) {
        Toast.makeText(mContext, getString(R.string.installing), Toast.LENGTH_LONG).show();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    Toast.makeText(mContext, getString(R.string.upgrade_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.upgrade_fail), Toast.LENGTH_LONG).show();
                }
                ExitAppUtil.getInstance().exit();
            }
        };

        handler.postDelayed(runnable, 1000*10*toInstallApksNum);
    }

}
