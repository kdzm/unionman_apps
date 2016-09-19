package com.um.upgrade.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.um.upgrade.CableManualUpgradeReminderActivity;
import com.um.upgrade.CableUserManualUpgrade;
import com.um.upgrade.NetworkUpgradePromptActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 13-10-25.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ((this instanceof Activity)){
            ExitAppUtil.getInstance().addActivity(this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if ((this instanceof Activity)){
            ExitAppUtil.getInstance().delActivity(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (this instanceof NetworkUpgradePromptActivity) {
                    return super.onKeyDown(keyCode, event);
                }
                if (this instanceof CableManualUpgradeReminderActivity) {
                    return super.onKeyDown(keyCode, event);
                }
                if (this instanceof CableUserManualUpgrade) {
                    return super.onKeyDown(keyCode, event);
                }
                if(isExit == false ) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    if(!hasTask) {
                        mExitTimer.schedule(mExitTimerTask, 2000);
                    }
                    isExit = true;
                } else {
                    ExitAppUtil.getInstance().exit();
                }
                break;
            default:
                break;
        }
        return false;
    }

    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    private Timer mExitTimer = new Timer();
    private TimerTask mExitTimerTask = new TimerTask() {

        @Override
        public void run() {
            isExit = false;
            hasTask = true;
        }
    };

}
