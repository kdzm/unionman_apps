package com.um.atv;

import com.um.atv.R;
import com.um.atv.util.Constant;
import com.um.atv.widget.RecentPlayListLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.app.Activity;

public class RecentActivity extends Activity {
    private RecentPlayListLayout mRecentPlayListLay;
    private static final String TAG = "RecentActivity";
    private static final int ACTIVITY_FINISH = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        mRecentPlayListLay = (RecentPlayListLayout) findViewById(R.id.recentplaylistlayout);
        delay();
    }
     @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            Log.d(TAG, "onKeyDown= keyCode:" + keyCode);
            switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				finish();
				break;
			}
            mRecentPlayListLay.onKeyDown(keyCode, event);
            return super.onKeyDown(keyCode, event);
        }
     @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            Log.d(TAG, "dispatchKeyEvent= keyCode:" + event.getKeyCode());
            delay();
            if (event.getAction() == KeyEvent.ACTION_UP) {
                mRecentPlayListLay.dispatchEvent(event);
            }
            return super.dispatchKeyEvent(event);
        }
     private Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case ACTIVITY_FINISH:
                    if (Constant.LOG_TAG) {
                        Log.d("ACTIVITY_FINISH", ACTIVITY_FINISH + "");
                    }
                    finish();
                    break;
                default:
                    break;
                }
            }
        };
        public void delay() {
            mHandler.removeMessages(ACTIVITY_FINISH);
            mHandler.sendEmptyMessageDelayed(ACTIVITY_FINISH, Constant.DISPEAR_TIME_60s);
        }
}
