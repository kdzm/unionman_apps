package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.provider.Settings;


import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;
import com.unionman.settingwizard.util.Constant;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.PropertyUtils;

public class SetupFinishActivity extends Activity {
    private static final String TAG = "SetupFinishActivity";
    ImageView mReflectedView;
    LinearLayout mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_finish2);

        Button finishStepBtn = (Button) findViewById(R.id.btn_finsh);
        Button lastStepBtn = (Button) findViewById(R.id.btn_last_step);
        finishStepBtn.setOnClickListener(new MyClickListener());
        lastStepBtn.setOnClickListener(new MyClickListener());
        finishStepBtn.requestFocus();
    }
    
    @Override
	protected void onDestroy() {
    	ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        am.forceStopPackage("com.unionman.settingwizard");
		super.onDestroy();
	}

    private void startLauncher() {
    	Log.v(TAG, "ready to start launcher.");
        Intent homeIntent =  new Intent(Intent.ACTION_MAIN, null);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        homeIntent = new Intent(homeIntent);
        homeIntent.setClassName("com.um.launcher",  "com.um.launcher.MainActivity");
        startActivity(homeIntent);
    }
    
	class MyClickListener implements OnClickListener {

		@Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_finsh:
                    int prvisioned = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
                    if (prvisioned != 1) {
                        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
                        PackageManager pm = getPackageManager();
                        ComponentName name = new ComponentName(SetupFinishActivity.this, ProvisionActivity.class);
                        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                        PreferencesUtils.putBoolean(SetupFinishActivity.this, "boot_by_provision", false);
                    }
                    //init password value for UmPwdLock
        		    Settings.Secure.putString(getContentResolver(), Constant.UMDefaultPwd, Constant.UMDefaultPwdValue);
        		    Settings.Secure.putString(getContentResolver(), Constant.UMSuperPwd, Constant.UMSuperPwdValue);
        		    startLauncher();
        		    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(SetupFinishActivity.this, NetworkSetupActivity.class);
                    startActivity(intent);
//                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {    
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
