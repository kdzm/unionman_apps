package com.um.atv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.um.atv.util.Constant;
import com.um.atv.widget.SettingLayout;

/**
 * when you press the menu button,SettingActivity will show
 *
 * @author wangchuanjian
 *
 */
public class SettingActivity extends Activity {
    // message of finish activity
    private String TAG = "SettingActivity";
    // layout of setting
    private SettingLayout mAllSettingLay;
    // layout of menu container
    private LinearLayout menuContainerlay;
    // source id
    private int mSourceId;

    /**
     * get source id
     *
     * @return
     */
    public int getSourceId() {
        return mSourceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllSettingLay = new SettingLayout(SettingActivity.this, null,finishHandle);
        setContentView(mAllSettingLay);
        initView();
    }

    /**
     * The initialization of all views
     */
    private void initView() {
//        mAllSettingLay = (SettingLayout) findViewById(R.id.settingLayout);
        menuContainerlay = (LinearLayout) (mAllSettingLay
                .findViewById(R.id.menu_containerlayout));
    }

    @Override
    protected void onPause() {
        menuContainerlay.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        doEnterList();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mAllSettingLay.onKeyDown(keyCode, event);
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
        case KeyEvent.KEYCODE_BACK:
        case KeyEvent.KEYCODE_TVSETUP:
        	doExitList();       
            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            if (Constant.LOG_TAG) {
                Log.d(TAG, "case KeyEvent.KEYCODE_DPAD_DOWN:" + keyCode);
            }
            break;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mAllSettingLay.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    /**
     * ListView fade effect
     */
    public void doEnterList() {

        Animation enterAnimation = AnimationUtils.loadAnimation(this,
                R.anim.fade_in);
        enterAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                menuContainerlay.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });
        menuContainerlay.startAnimation(enterAnimation);
    }

    /**
     * ListView fade out
     */
    public void doExitList() {
        Animation exitAnimation = AnimationUtils.loadAnimation(this,
                R.anim.fade_out);
        exitAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAllSettingLay.setVisibility(View.GONE);
                SettingActivity.this.finish();
            }
        });
        menuContainerlay.startAnimation(exitAnimation);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
        if (hasFocus) {
            delay();
        } else {
            finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * handler of finish activity
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == Constant.ACTIVITY_FINISH)
                finish();
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay() {
    	Log.i(TAG,"delay() is calling");
        finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        Message message = new Message();
        message.what = Constant.ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
    }
    
    @Override
    protected void onDestroy() {
        if (mAllSettingLay != null) {
            mAllSettingLay.onDestroy();
        }
        super.onDestroy();
    }
}
