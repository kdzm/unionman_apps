package com.um.upgrade;

import android.content.Context;
import android.os.Bundle;

import com.um.upgrade.base.BaseActivity;

/**
 * Created by ziliang.nong on 14-8-18.
 */
public class RecoveryUpgradeActivity extends BaseActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_upgrade);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
