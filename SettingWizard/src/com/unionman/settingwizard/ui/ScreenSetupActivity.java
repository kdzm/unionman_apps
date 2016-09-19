package com.unionman.settingwizard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hisilicon.android.HiDisplayManager;

import android.os.ServiceManager;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.unionman.settingwizard.R;

import com.unionman.settingwizard.util.BitmapCtl;
import com.unionman.video.Percent;
import com.unionman.video.Rectangle;

public class ScreenSetupActivity extends Activity {
    ImageView mReflectedView;
    LinearLayout mContentView;
    private static final int SCALE = 2;// actual and virtual scaling
    private static int top_margin = 0;
    private static int left_margin = 0;
    private static int right_margin = 0;
    private static int bottom_margin = 0;

    private static final String TAG = "ScopePreference";

    /**
     * Display settings java interface.
     */
    private HiDisplayManager display_manager = null;

    /**
     * A Rectangle for dispose.
     */
    private Rectangle mRange;
    private Rectangle originalRange;
    private Percent percentTemp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_setup);

        if (display_manager == null) {
            display_manager = new HiDisplayManager();
        }
        mRange = new Rectangle();
        originalRange = new Rectangle();
        percentTemp = new Percent();

        Rect rect = display_manager.getOutRange();
        originalRange.left = rect.left;
        originalRange.top = rect.top;
        originalRange.width = rect.right;
        originalRange.height = rect.bottom;
        mRange.left = rect.left;
        mRange.top = rect.top;
        mRange.width = rect.right;
        mRange.height = rect.bottom;

        Percent percent = rangeToPercent(mRange);
        left_margin = percent.leftPercent;
        top_margin = percent.topPercent;
        right_margin = percent.widthPercent;
        bottom_margin = percent.heightPercent;

        Button NextStepBtn = (Button) findViewById(R.id.btn_next_step);
        Button LastStepBtn = (Button) findViewById(R.id.btn_last_step);
        NextStepBtn.requestFocus();
        NextStepBtn.setOnClickListener(new MyClickListener());
        LastStepBtn.setOnClickListener(new MyClickListener());
    }

    public Percent rangeToPercent(Rectangle range) {
        Percent percent = new Percent();
        percent.leftPercent = range.left / SCALE;
        percent.topPercent = range.top / SCALE;
        percent.widthPercent = range.width / SCALE;
        percent.heightPercent = range.height / SCALE;
        return percent;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        mContentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(mContentView, mReflectedView);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(ScreenSetupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Rectangle currentRange = new Rectangle();
        Rectangle maxRange = new Rectangle();
        Rect rect = new Rect();
        //maxRange = getMaxRangeByFmt(display_manager.getFmt());

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (left_margin > 0 && top_margin > 0 && right_margin > 0 && bottom_margin > 0) {
                    left_margin -= 2;
                    top_margin -= 2;
                    right_margin -= 2;
                    bottom_margin -= 2;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (left_margin < 100 && top_margin < 100 && right_margin < 100 && bottom_margin < 100) {
                    left_margin += 2;
                    top_margin += 2;
                    right_margin += 2;
                    bottom_margin += 2;
                }
                break;
            default:
                break;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mRange.left = left_margin * SCALE;
            mRange.top = top_margin * SCALE;
            mRange.width = right_margin * SCALE;
            mRange.height = bottom_margin * SCALE;
            display_manager.setOutRange(mRange.left, mRange.top, mRange.width, mRange.height);
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        display_manager.SaveParam();
        super.onPause();
    }

    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    intent = new Intent(ScreenSetupActivity.this, NetworkSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(ScreenSetupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
