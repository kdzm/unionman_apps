package com.cvte.tv.at.job;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cvte.tv.at.R;
import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.api.SysProp;
import com.cvte.tv.at.util.CVTEBURN;
import com.cvte.tv.at.util.CvteKey;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.UART_DEBUG;

/**
 * @author Leajen_Ren
 * @version V1.1 2015-06-29 新结构，去除掉AgingMode和WB
 * @Package com.cvte.tv.at.at.job
 * @Description AT核心显示部件，在这里会创建VideoView或则是Surfaceview，另外AgingMode也在这里实现
 * 这里有部分按键识别行为例如用遥控器输入数字键切换频道之类
 * ，另外这里还有死亡列表，可以在AT启动的时候干掉一些不想让之出现的第三方APK，防止在测试中突然弹个框什么的。
 * 当然死亡列表是否使用还看需求。
 */
public class ATActivity extends Activity {

    private final int FACTORY_TEST_CHECK_TVAPI = 0;
    private final int FACTORY_TEST_AT_ACTION = 1;
    private final int FACTORY_TEST_RESET_DOING = 2;

    // When AT Start Lock IR Key
    private final int WAIT_TVAPI_TIME = 0;
    private final int CHECK_TVAPI_TIME = 300;

    private static boolean skeylock = false;
    private static CvteFacAPI sFacapi;
    private static Context sContext;

    public Context getsContext() {
        return sContext;
    }

    public void setsContext(Context context) {
        ATActivity.sContext = context;
    }

    private static TextView ResetView;
    private static TextView at_title;
    private final int CheckApiTimes = 30;
    private static int timer = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case FACTORY_TEST_CHECK_TVAPI:
//                    Utils.LOG("Enter AgingMode Source = " + sFacapi.TvApi());
                    if ((!sFacapi.ApiInitStatus()) && (timer < CheckApiTimes)) {
                        sFacapi.GetTime("InitTvApiTimes=" + timer);
                        timer++;
                        mHandler.sendEmptyMessageDelayed(FACTORY_TEST_CHECK_TVAPI, CHECK_TVAPI_TIME);
                    } else {
                        timer = 0;
                        ATAction_Do();
                    }

                    if (timer > CheckApiTimes) {
                        Utils.LOG("Link Tv-Api-Service Error Exit AT APK");
                        endself();
                    }
                    break;
                case FACTORY_TEST_AT_ACTION:
                    Utils.LOG("Enter CVTE UART Mode");
                    initserial(UART_DEBUG.UART_CVTE);
                    at_title.setText(R.string.at_title);
                    at_title.setVisibility(View.VISIBLE);
                    sFacapi.ImportAllProgramTable();
                    skeylock = false;
                    break;
                case FACTORY_TEST_RESET_DOING:
                    sFacapi.ATResetFunction();
                    ResetView.setText(R.string.reset_info);
                    ResetView.setBackgroundColor(Color.WHITE);
                    ResetView.setScaleX(1);
                    Utils.LOG("<RTD> Reset End");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        skeylock = true;
        super.onCreate(savedInstanceState);
        setsContext(getApplicationContext());

        sFacapi = CvteFacAPI.getInstance(getsContext());
        CVTEBURN.delete();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.LOG("<ATScreen> onStart");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        View v = View.inflate(this, R.layout.cvteatroot, null);
        setContentView(v, params);

        ResetView = (TextView) findViewById(R.id.reset_flag);
        ResetView.setScaleX(0);
        at_title = (TextView) findViewById(R.id.at_title);
    }

    @Override
    protected void onRestart() {
        Utils.LOG("ATActivity onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.LOG("ATActivity onResume");
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //in here add a timer for Start Tv-APi Detect
        mHandler.sendEmptyMessage(FACTORY_TEST_CHECK_TVAPI);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        sFacapi.Finalize();
//        System.gc();
        Utils.LOG("<ATScreen> onPause-exit");
        sFacapi.UARTDebugEnable(Utils.UART_E.Final);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Utils.LOG("<ATScreen> onDestroy");
        System.exit(0);
        initserial(UART_DEBUG.UART_FINISH);
    }


    private void ATAction_Do() {
        mHandler.sendEmptyMessageDelayed(FACTORY_TEST_AT_ACTION, WAIT_TVAPI_TIME);
        Startsurfaceview();//Renlijia.20150126 add for Amlogic project need create Suferview
        StartScreen();
        FacKeyReceiver();
    }

    //Renlijia.20150126 modify for Amlogic project need create a suferview
    private void Startsurfaceview() {
        sFacapi.createSurfaceView(this);//MT5507 no need this solution
    }

    private void StartScreen() {
        sFacapi.setATScreen(true);
    }

    private void finialScreen() {
        sFacapi.setATScreen(false);
    }

    private void endself() {
        finialScreen();
        this.finish();
    }

    public void KeyDebugLog(KeyEvent event) {
        Utils.LOG("deviceName.name = " + InputDevice.getDevice(event.getDeviceId()).getName());
        Utils.LOG("deviceName.ID = " + event.getDeviceId());
        Utils.LOG("event.getKeyCode= " + event.getKeyCode());
        Utils.LOG("ScanCode = 0x" + Integer.toHexString(event.getScanCode()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (skeylock)
            return true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Utils.LOG("<RTD> End AT");
                endself();
                return true;
            default:
//                Utils.LOG("<CVTE-AT> onKeyDown keyCode=" + keyCode);
                KeyDebugLog(event);
                if (GetKeypadPressKey(event))
                    return true;
                if ((event.getScanCode() == CvteKey.KEYCODE_KEY_CVT_FAC_F1) ||
                        (event.getScanCode() == CvteKey.KEYCODE_KEY_CVT_FAC_F1_UM)) {
                    Utils.LOG("<RTD> DO Reset");
                    mHandler.sendEmptyMessage(FACTORY_TEST_RESET_DOING);
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private static final int SCANCODE0 = 0x2f1;
    private static final int SCANCODE1 = 0x2f6;
    private static final int SCANCODE2 = 0x2f2;
    private static final int SCANCODE3 = 0x2f3;
    private static final int SCANCODE4 = 0x2f4;
    private static final int SCANCODE5 = 0x2f5;
    private static final int SCANCODE6 = 0x2f0;//0x1e is rel power key

    public boolean GetKeypadPressKey(KeyEvent event) {
        final int ScanCode = event.getScanCode();//all scan code is 0
        final int KeyCode = event.getKeyCode();//all scan code is 0
        final boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
        String deviceName = "";
        try {
            deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.LOG("GetKeypadPressKey KeyCode:" + KeyCode + " down:" + down);
        Utils.LOG("deviceName.equals(Utils.DeviceName_Keypad):" + deviceName.equals(Utils.DeviceName_Keypad));
        if (down) {
            if (deviceName.equals(Utils.DeviceName_Keypad)) {
                int keyvalue = 7;
                switch (ScanCode) {
                    case SCANCODE0:
                        keyvalue = 0;
                        break;
                    case SCANCODE1:
                        keyvalue = 1;
                        break;
                    case SCANCODE2:
                        keyvalue = 2;
                        break;
                    case SCANCODE3:
                        keyvalue = 3;
                        break;
                    case SCANCODE4:
                        keyvalue = 4;
                        break;
                    case SCANCODE5:
                        keyvalue = 5;
                        break;
                    case SCANCODE6:
                        keyvalue = 6;
                        break;
                    default:
                        return false;
                }
                Utils.LOG("SetKeypadPressKey keyvalue:" + keyvalue);
                SysProp.set(Utils.CVTE_PRESS_KEY, "" + keyvalue);
                return true;
            }
        }
        return false;
    }

    private void initserial(UART_DEBUG type) {
        sFacapi.SetUART_DEBUG(type);
    }

    private void FacKeyReceiver() {
        IntentFilter filter = new IntentFilter(Utils.CVTE_BROADCAST_RESETKEY);
        getsContext().getApplicationContext().registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                Utils.LOG("AT BroadcastReceiver action:" + action);
                if (action.equals(Utils.CVTE_BROADCAST_RESETKEY)) {
                    Utils.LOG("BroadcastReceiver = KEYCODE_KEY_CVT_FAC_FACTORY_RESET");
                    mHandler.sendEmptyMessage(FACTORY_TEST_RESET_DOING);
                }
            }
        }, filter);
    }


}
