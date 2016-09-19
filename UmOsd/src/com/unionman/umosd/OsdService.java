package com.unionman.umosd;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Status;


public class OsdService extends Service{
    private final String TAG = OsdService.class.getSimpleName();
    private final int tfCaOsd = 1;
    private final int dvtCaOsd = 2;
    private long osdShowTick;
    private int osdType = 0;
    private Timer mOsdScrollTimer = null;
    private final int MSG_TYPE_OSD_SCROLL_BY_TIME = 1;
    private final int MSG_TYPE_OSD_SCROLL_BY_COUNT = 2;
    private final int OSD_SCROLL_OFFSET = 2;
    private View mTopHorScrollView = null;
    private View mBottonHorScrollView = null;
    private LinearLayout mTopOsdContainer = null;
    private LinearLayout mBottonOsdContainer = null;
    private int mTopLastScrollX = -1;
    private int mBottonLastScrollX = -1;
    private int mTopOsdParam = -1;
    private int mBottonOsdParam = -1;
    private int mTopOsdTimeCount = 0;
    private int mBottonOsdTimeCount = 0;
    private boolean mTopOsdRunning = false;
    private boolean mBottonOsdRunning = false;


    private Handler mScrollHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        switch (msg.what){
            case MSG_TYPE_OSD_SCROLL_BY_TIME:
                if (mTopOsdRunning){
                    mTopHorScrollView.scrollTo(mTopHorScrollView.getScrollX()+ OSD_SCROLL_OFFSET, 0);
                    if (mTopOsdTimeCount >= 50 && mTopOsdParam > 0 && mTopOsdParam != -1){
                        mTopOsdParam --;
                        Log.i(TAG, "mTopOsdParam = "+String.valueOf(mTopOsdParam));
                        mTopOsdTimeCount = 0;
                    } else {
                        mTopOsdTimeCount ++;
                    }
                    if (mTopHorScrollView.getScrollX()- mTopLastScrollX == 0){
                        mTopHorScrollView.scrollTo(0,0);
                    }
                    mTopLastScrollX = mTopHorScrollView.getScrollX();
                }
                if (mTopOsdParam == 0){
                    resetTopOsd();
                }

                if (mBottonOsdRunning){
                    mBottonHorScrollView.scrollTo(mBottonHorScrollView.getScrollX()+ OSD_SCROLL_OFFSET, 0);
                    if (mBottonOsdTimeCount >= 50 && mBottonOsdParam > 0 && mBottonOsdParam != -1){
                        mBottonOsdParam --;
                        mBottonOsdTimeCount = 0;
                    } else {
                        mBottonOsdTimeCount ++;
                    }
                    if (mBottonHorScrollView.getScrollX()- mBottonLastScrollX == 0){
                        mBottonHorScrollView.scrollTo(0,0);
                    }
                    mBottonLastScrollX = mBottonHorScrollView.getScrollX();
                }
                if (mBottonOsdParam == 0){
                    resetBottonOsd();
                }

                if (!mTopOsdRunning && !mBottonOsdRunning) {
                    Log.i(TAG, "!mTopOsdRunning && !mBottonOsdRunning");
                    if (mOsdScrollTimer != null){
                        stopOsdScrollTimer();
                    }
                }
                break;
            case MSG_TYPE_OSD_SCROLL_BY_COUNT:
                if (mTopOsdRunning){
                    mTopHorScrollView.scrollTo(mTopHorScrollView.getScrollX()+ OSD_SCROLL_OFFSET, 0);
                    if (mTopHorScrollView.getScrollX()- mTopLastScrollX == 0){
                        mTopHorScrollView.scrollTo(0,0);
                        mTopOsdParam --;
                    }
                    mTopLastScrollX = mTopHorScrollView.getScrollX();
                }
                if (mTopOsdParam == 0){
                    resetTopOsd();
                }

                if (mBottonOsdRunning){
                    mBottonHorScrollView.scrollTo(mBottonHorScrollView.getScrollX()+ OSD_SCROLL_OFFSET, 0);
                    if (mBottonHorScrollView.getScrollX()- mBottonLastScrollX == 0){
                        mBottonHorScrollView.scrollTo(0,0);
                        mBottonOsdParam --;
						Log.i(TAG, "mBottonOsdParam --,mBottonOsdParam:" +mBottonOsdParam);
                    }
                    mBottonLastScrollX = mBottonHorScrollView.getScrollX();
                }
                if (mBottonOsdParam == 0){
                    resetBottonOsd();
                }
                if (!mTopOsdRunning && !mBottonOsdRunning) {
                    Log.i(TAG, "!mTopOsdRunning && !mBottonOsdRunning");
                    if (mOsdScrollTimer != null){
                        stopOsdScrollTimer();
                    }
                }
            default:
                break;
        }
        }
    };

    public class OsdServiceBinder extends Binder {
        public OsdService getService() {
            return OsdService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new OsdServiceBinder();
    }

    @Override
    public void onCreate() {
    	Log.i(TAG, "onCreate");
        createOsdContainer();
        super.onCreate();
        if (!DVB.isServerAlive()) {
        	stopSelf();
        	return;
        }
        Status.GetInstance().attachContext(this);
    }
    
    @Override
    public void onDestroy() {
    	Log.i(TAG, "onDestroy");
    	if (DVB.isServerAlive()) {
    		Status.GetInstance().detachContext();
    	}
    	super.onDestroy();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null == intent){
                Log.e(TAG, "onStartCommand,intent=null");
                return Service.START_STICKY;
		}
        if (intent.getAction().equals("com.unionman.umosd.START_TFCA_TOP_OSD")){
        	osdType = tfCaOsd;
            Log.i(TAG, "onStartCommand : 显示字幕");
            Bundle bundle = intent.getExtras();
            if(null == bundle) {
            	Log.e(TAG, "onStartCommand,bundle=null");
            	return Service.START_STICKY;
            }
            createTopOsd(bundle.getString("osdString", "null"));
            mTopOsdParam = bundle.getInt("osdParam", -1);
            startOsdScrollTimer(bundle.getInt("osdScrollType", 1));
        } else if (intent.getAction().equals("com.unionman.umosd.START_TFCA_BOTTON_OSD")){
            Log.i(TAG, "onStartCommand : 鏄剧ず瀛楀箷");
            Bundle bundle = intent.getExtras();
            if(null == bundle) {
            	Log.e(TAG, "onStartCommand,bundle=null");
            	return Service.START_STICKY;
            }
//            createTopOsd(bundle.getString("osdString", "null"));
            createBottonOsd(bundle.getString("osdString", "null"));
            mBottonOsdParam = bundle.getInt("osdParam", -1);
            startOsdScrollTimer(bundle.getInt("osdScrollType", 1));
        } else if (intent.getAction().equals("com.unionman.umosd.STOP_TFCA_TOP_OSD")){
            resetTopOsd();
        } else if (intent.getAction().equals("com.unionman.umosd.STOP_TFCA_BOTTON_OSD")){
            resetBottonOsd();
        }else if (intent.getAction().equals("com.unionman.umosd.START_DVTCA_BOTTON_OSD")){
        	osdType = dvtCaOsd;
        	Log.i(TAG, "TART_DVTCA_BOTTON_OSD,osdType:" +osdType);
        	osdShowTick = System.currentTimeMillis();
        	Log.i(TAG, "System.currentTimeMillis():" +System.currentTimeMillis());
            Log.i(TAG, "onStartCommand : 鏄剧ず瀛楀箷");
            Bundle bundle = intent.getExtras();
            createBottonOsd(bundle.getString("osdString", "null"));
            mBottonOsdParam = bundle.getInt("osdParam", 1);
			Log.i(TAG, "START_DVTCA_BOTTON_OSD,mBottonOsdParam:" +mBottonOsdParam);
            startOsdScrollTimer(bundle.getInt("osdScrollType", 2));
        }
        return Service.START_REDELIVER_INTENT;
    }

    private void createOsdContainer(){
        WindowManager windowManager = (WindowManager) OsdService.this.getSystemService(Activity.WINDOW_SERVICE);
        WindowManager.LayoutParams wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
        wmLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
        // 以屏幕左上角为原点，设置x、y初始值
        wmLayoutParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.format = PixelFormat.RGBA_8888;

        wmLayoutParams.gravity = Gravity.CENTER | Gravity.TOP; // 调整悬浮窗口至上边缘中间
        mTopOsdContainer = new LinearLayout(OsdService.this);
        windowManager.addView(mTopOsdContainer,wmLayoutParams);

        wmLayoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM; // 调整悬浮窗口至下边缘中间
        mBottonOsdContainer = new LinearLayout(OsdService.this);
        windowManager.addView(mBottonOsdContainer,wmLayoutParams);
    }

    private void createTopOsd(String osdtring){
        mTopOsdParam --;
        mTopOsdRunning = true;
        ViewGroup.LayoutParams blankLayoutParams = new ViewGroup.LayoutParams(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout blankBefore = new LinearLayout(this);
        blankBefore.setLayoutParams(blankLayoutParams);
        LinearLayout blankAfter = new LinearLayout(this);
        blankAfter.setLayoutParams(blankLayoutParams);

        mTopHorScrollView = (View) LayoutInflater.from(OsdService.this).
                inflate(R.layout.hor_scroll_view, null);
        LinearLayout topHorScrollInner = (LinearLayout) mTopHorScrollView.findViewById(R.id.scroll_inner_layout);

        topHorScrollInner.addView(blankBefore);
        TextView textView = new TextView(this);
        textView.setText(osdtring);
        textView.setTextSize(40.0f);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setSingleLine();
        topHorScrollInner.addView(textView);
        topHorScrollInner.addView(blankAfter);

        mTopOsdContainer.removeAllViews();
        mTopOsdContainer.addView(mTopHorScrollView);
    }

    private void createBottonOsd(String osdtring){
        mBottonOsdParam --;
        mBottonOsdRunning = true;
        ViewGroup.LayoutParams blankLayoutParams = new ViewGroup.LayoutParams(((WindowManager) getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout blankBefore = new LinearLayout(this);
        blankBefore.setLayoutParams(blankLayoutParams);
        LinearLayout blankAfter = new LinearLayout(this);
        blankAfter.setLayoutParams(blankLayoutParams);

        mBottonHorScrollView = (View) LayoutInflater.from(OsdService.this).
                inflate(R.layout.hor_scroll_view, null);
        LinearLayout bottonHorScrollInner = (LinearLayout) mBottonHorScrollView.findViewById(R.id.scroll_inner_layout);

        bottonHorScrollInner.addView(blankBefore);
        TextView textView = new TextView(this);
        textView.setText(osdtring);
        textView.setTextSize(40.0f);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setSingleLine();
        bottonHorScrollInner.addView(textView);
        bottonHorScrollInner.addView(blankAfter);

        mBottonOsdContainer.removeAllViews();
        mBottonOsdContainer.addView(mBottonHorScrollView);
    }

    private void resetTopOsd(){
        mTopOsdContainer.removeAllViews();
        mTopOsdRunning = false;
    }

    private void resetBottonOsd(){
        mBottonOsdContainer.removeAllViews();
        mBottonOsdRunning = false;
        
        Log.i(TAG, "resetBottonOsd,osdType:" +osdType);
        if(dvtCaOsd == osdType){
        	  Ca ca = new Ca(DVB.GetInstance());
              Log.i(TAG, "System.currentTimeMillis():" +System.currentTimeMillis());
              int dutation = (int)(System.currentTimeMillis() - osdShowTick)/1000;
              Log.i(TAG, "dutation:" +dutation);
              ca.CaOsdmessageCompleted(dutation);
        }
    }

    private void startOsdScrollTimer(final int osdScrollType){
        Log.i(TAG, "startOsdScrollTimer");
        if (mOsdScrollTimer != null){
            stopOsdScrollTimer();
        }
        Log.i(TAG, "osdScrollType:" +osdScrollType);
        mOsdScrollTimer = new Timer();
        TimerTask mOsdScrollTimerTask = new TimerTask(){
            @Override
            public void run() {
                mScrollHandler.removeMessages(osdScrollType);
                mScrollHandler.obtainMessage(osdScrollType).sendToTarget();
            }
        };
        mOsdScrollTimer.schedule(mOsdScrollTimerTask, 0, 20);
    }

    private void stopOsdScrollTimer(){
        Log.i(TAG, "stopOsdScrollTimer");
        mOsdScrollTimer.cancel();
        mOsdScrollTimer = null;
    }
}

