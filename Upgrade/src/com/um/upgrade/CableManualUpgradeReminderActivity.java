package com.um.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.TextView;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.MyApp;

public class CableManualUpgradeReminderActivity extends BaseActivity implements OnClickListener{
    private final String TAG = CableManualUpgradeReminderActivity.class.getSimpleName();
    private TextView mCountTextView = null;
    private Handler mHandler = null;
    private int mCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cable_manual_upgrade_reminder);
        findViewById(R.id.yes_button).setOnClickListener(this);
        findViewById(R.id.no_button).setOnClickListener(this);
        mCountTextView = (TextView) findViewById(R.id.count_text_view);

        mCount = 30;
        mCountTextView.setText(String.valueOf(mCount));
        mHandler = new Handler();
        mHandler.postDelayed(mCountDownRunnable, 1000);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mCountDownRunnable);
        finish();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes_button:
                Bundle bundle = new Bundle();
                bundle.putInt("upgradeType", 1);
                Intent intent = new Intent(CableManualUpgradeReminderActivity.this, CableUpgradeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.no_button:
                MyApp.setCableUpgradeManual();
                finish();
                break;
            default:
                break;
        }
    }

    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCount <= 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("upgradeType", 1);
                Intent intent = new Intent(CableManualUpgradeReminderActivity.this, CableUpgradeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                mHandler.removeCallbacks(mCountDownRunnable);
            } else {
                mCount --;
                mCountTextView.setText(Integer.toString(mCount));
                mHandler.postDelayed(mCountDownRunnable, 1000);
            }
        }
    };
}
