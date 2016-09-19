package com.cvte.tv.at;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.util.CVTEBURN;
import com.cvte.tv.at.util.Utils;

import java.util.Formatter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2014/10/10.
 */
public class AgingModeActivity extends Activity {

    private Timer loop = null;
    private FrameLayout layout;
    private TextView tv_time;
    private int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.BLACK};
    private static final int MSG_REFRESH_LED = 2;
    private static final int MSG_FINISH_AGINGMODE = 3;
    private static final int MSG_INIT_OTP = 4;

    private static final int MSG_INIT_OTP_TIMER = 1000;
    private CvteFacAPI facapi = null;

    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.LOG("AgingMode onCreate");
        CVTEBURN.create();
        facapi = CvteFacAPI.getInstance(getApplicationContext());
        setContentView(R.layout.activity_agingmode);
        layout = (FrameLayout) findViewById(R.id.fl_layout);
        tv_time = (TextView) findViewById(R.id.tv_time);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        StopWYScreenprotect();
        handler.sendEmptyMessageDelayed(MSG_INIT_OTP, MSG_INIT_OTP_TIMER);
    }

    private int preX = 0, preY = 0;

    private void startRandomTranslate() {
        Random random = new Random();
        int[] location = new int[2];
        tv_time.getLocationInWindow(location);
        float x = tv_time.getX();
        float y = tv_time.getY();
        if (x == 0)
            x = width / 2;
        if (y == 0)
            y = height / 2;
        float nx = random.nextInt(width - 100);
        float ny = random.nextInt(height - 100);
        TranslateAnimation translateAnimation = new TranslateAnimation(preX, nx - x, preY, ny - y);
        translateAnimation.setDuration(3000);
        tv_time.startAnimation(translateAnimation);
        preX = (int) (nx - x);
        preY = (int) (ny - y);
    }

    boolean LEDLight = false;
    Handler handler = new Handler() {
        private int colorIndex = 0;
        private int cur = 0;

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_REFRESH_LED:
//                    startRandomTranslate();
                    if (cur >= 3) {
                        if (colorIndex == (colors.length - 1)) {
                            colorIndex = 0;
                        } else {
                            colorIndex++;
                        }

                        switch (colors[colorIndex]) {
                            case Color.RED:
                                tv_time.setTextColor(Color.GREEN);
                                break;
                            case Color.GREEN:
                                tv_time.setTextColor(Color.BLUE);
                                break;
                            case Color.BLUE:
                                tv_time.setTextColor(Color.WHITE);
                                break;
                            case Color.WHITE:
                                tv_time.setTextColor(Color.BLACK);
                                break;
                            case Color.BLACK:
                                tv_time.setTextColor(Color.WHITE);
                                break;
                        }

                        layout.setBackgroundColor(colors[colorIndex]);
                        Utils.LOG("MSG_REFRESH_LED colorIndex=" + colorIndex);
                        cur = 0;
                        tv_time.setText(getResources().getString(R.string.cvteaingtime) + GetSystemTotalTime());
                    } else {
                        cur++;
                    }

                    if (facapi != null)
                        facapi.SetLEDLight(LEDLight = !LEDLight);
                    break;
                case MSG_FINISH_AGINGMODE:
                    loop.cancel();
                    finish();
                    break;
                case MSG_INIT_OTP:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    public String GetSystemTotalTime() {
        int Systime = GetBootUpTimeMin();
        Utils.LOG("<AT> GetSystemTotalTime = " + Systime);
        long min = Systime;

        long hor = min / 60;
        long min_show = min % 60;

        long day = hor / 24;
        long hor_show = hor % 24;

        Formatter formatter = new Formatter();
        String Ltime = formatter.format("%d day %02d:%02d",
                day, hor_show, min_show).toString().toLowerCase();

        return Ltime;
    }

    private int GetBootUpTimeMin() {
        int min_show = (int) (((SystemClock.uptimeMillis() / 1000) / 60) % 60);
        Utils.LOG("<AT> GetBootUpTimeMin min_show = " + min_show);
        return min_show;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loop = new Timer();
        loop.schedule(new TimerTask() {
            @Override
            public void run() {
                if (CVTEBURN.exist()) {
                    handler.sendEmptyMessage(MSG_REFRESH_LED);
                } else {
                    handler.sendEmptyMessage(MSG_FINISH_AGINGMODE);
                }
            }
        }, 0, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_POWER:
                Utils.LOG("AgingMode KEYCODE_POWER Leave AgingMode");
                exitAgingMode();
                finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        exitAgingMode();
        super.onDestroy();
    }

    private void exitAgingMode() {
        CVTEBURN.delete();
    }

    public void StopWYScreenprotect() {
        int time_never = 2147483647;
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time_never);
    }
}
