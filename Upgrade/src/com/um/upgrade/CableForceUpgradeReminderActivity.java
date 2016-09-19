package com.um.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.um.upgrade.base.BaseActivity;
import com.um.upgrade.base.MyApp;

public class CableForceUpgradeReminderActivity extends BaseActivity {
    private final String TAG = CableForceUpgradeReminderActivity.class.getSimpleName();
    private final boolean LOG_EN = true;

    private Handler mHandler = null;
    private TextView mCountTextView = null;
    private int mCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cable_force_upgrade_reminder);
        mCountTextView = (TextView) findViewById(R.id.count_text_view);

        mCount = 30;
        mCountTextView.setText(Integer.toString(mCount));
        mHandler = new Handler();
        mHandler.postDelayed(mCountDownRunnable, 1000);
    }
    
    @Override
    protected void onResume() {
    	MyApp.setKeyDisable();
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MyApp.setKeyEnable();
        finish();
    }

    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCount <= 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("upgradeType", 1);
                Intent intent = new Intent(CableForceUpgradeReminderActivity.this, CableUpgradeActivity.class);
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
