package com.um.umsmcupdate;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.spec.EllipticCurve;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Status;
import com.um.umsmcupdate.R;

public class SmcUpdateService extends Service{
    private final String TAG = SmcUpdateService.class.getSimpleName();
    private Timer mOsdScrollTimer = null;
    private final int MSG_TYPE_OSD_SCROLL = 1;
    private final int OSD_SCROLL_SCALE = 3;
    private View mTopHorScrollView = null;
    private LinearLayout mTopOsdContainer = null;
    private LinearLayout mSmcUpdateContainer = null;
    private int mTopLastScrollX = -1;
    private int mBottonLastScrollX = -1;
    private int mTopOsdParam = -1;
    private int mBottonOsdParam = -1;
    private int mTopOsdTimeCount = 0;
    private int mBottonOsdTimeCount = 0;
    private boolean mTopOsdRunning = false;
    private boolean mBottonOsdRunning = false;
	int updateflag = 1;
	int updateprocess = 0;
	View UpdateLayout;
	private TextView update_status;
	public Handler handler=new Handler();
	private ProgressBar updatebar;
	
    public class SmcUpdateServiceBinder extends Binder {
        public SmcUpdateService getService() {
            return SmcUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SmcUpdateServiceBinder();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "创建OSDContainer");
        createUpdateContainer();
        super.onCreate();
        if (!DVB.isServerAlive()) {
        	stopSelf();
        	return;
        }
        Status.GetInstance().attachContext(this);
    }
    
    @Override
    public void onDestroy() {
    	if (DVB.isServerAlive()) {
    		Status.GetInstance().detachContext();
    	}
    	super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if(null == intent){
    		Log.e(TAG, "onStartCommand,NullPointer(intent)");
    		return Service.START_STICKY;
    	}
		if (intent.getAction().equals("com.um.umsmcupdate.START_PROGRESS_RECEIVEPATCH"))
		{
			updateflag = 1;
			Bundle bundle = intent.getExtras();
			if(null == bundle){
				Log.e(TAG, "onStartCommand,NullPointer");
				return Service.START_STICKY;
			}
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 START_PROGRESS_RECEIVEPATCH"+updateprocess);
			start_updatebar();

		}
		else if(intent.getAction().equals("com.um.umsmcupdate.STOP_PROGRESS_RECEIVEPATCH"))
		{
			updateflag = 1;
			
			Bundle bundle = intent.getExtras();
			if(null == bundle){
				Log.e(TAG, "onStartCommand,NullPointer");
				return Service.START_STICKY;
			}
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 STOP_PROGRESS_RECEIVEPATCH"+updateprocess);
			stop_updatebar();
		}	
		else if(intent.getAction().equals("com.um.umsmcupdate.START_PROGRESS_PATCHING"))
		{
			updateflag = 0;
			
			Bundle bundle = intent.getExtras();
			if(null == bundle){
				Log.e(TAG, "onStartCommand,NullPointer");
				return Service.START_STICKY;
			}
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 START_PROGRESS_PATCHING"+updateprocess);
			start_updatebar();
		}
		else if(intent.getAction().equals("com.um.umsmcupdate.STOP_PROGRESS_PATCHING"))
		{
			updateflag = 0;
			
			Bundle bundle = intent.getExtras();
			if(null == bundle){
				Log.e(TAG, "onStartCommand,NullPointer");
				return Service.START_STICKY;
			}
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 STOP_PROGRESS_PATCHING"+updateprocess);
			stop_updatebar();
		}
        return Service.START_STICKY;
    }

    private void start_updatebar()
    {
        UpdateLayout = (View) LayoutInflater.from(SmcUpdateService.this).
                inflate(R.layout.tf_updatebar, null);
        mSmcUpdateContainer.removeAllViews();
        mSmcUpdateContainer.addView(UpdateLayout);
        
        update_status = (TextView)UpdateLayout.findViewById(R.id.textViewUpdating);
        update_status.setGravity(Gravity.CENTER);
		
		if(1 == updateflag)          //flag_int = 1  loaddata
		{
			update_status.setText(R.string.tf_ca_scale_receivepatch);
		}
		else                       //update
		{
			update_status.setText(R.string.tf_ca_scale_patching);
		}
		
		//2.启动计时器：
		handler.postDelayed(runnable, 100);//每两秒执行一次runnable.
		
		updatebar	  = (ProgressBar)UpdateLayout.findViewById(R.id.updateProgressBar);
		updatebar.setProgress(updateprocess);
		TextView progress = (TextView)UpdateLayout.findViewById(R.id.update_percent);
		progress.setText(updateprocess+" %");
    }
    private void stop_updatebar()
    {
    	handler.removeCallbacks(runnable);
    	mSmcUpdateContainer.removeAllViews();
    }
    
	Runnable runnable=new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		//要做的事情

			int process_value = 0;
			int mark_value = 0;
	        Ca ca = new Ca(DVB.GetInstance());
	        /*CA Version*/
	        
	        int []buff_len = {256};
	        byte []buff = new byte[buff_len[0]];
	        int ret = ca.CaGetUpdateProgress(buff, buff_len);
	        Log.i("SmcUpdateService", "ret:"+ret);
	        Log.i("SmcUpdateService", "buff_len:"+buff_len[0]);
	        if((ret == 0)&&(buff_len[0] != 0))
	        {
	        	 String jsonStr = new String(buff, 0, buff_len[0]);
				 System.out.println("jsonStr:"+jsonStr); //输出字符串
				 
				 try {  
					  	JSONObject jsonObject = new JSONObject(jsonStr);
					  	process_value = jsonObject.getInt("progress");
					  	mark_value = jsonObject.getInt("mark");
					    
					} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");// 异常处理代码  
					}  
	        }	
	       
			Log.i("TF_updatebar_receiver", "Auto-generated method stub process_value"+process_value);
			Log.i("TF_updatebar_receiver", "Auto-generated method stub mark_value"+mark_value);
			
			updatebar	  = (ProgressBar)UpdateLayout.findViewById(R.id.updateProgressBar);
			updatebar.setProgress(process_value);
			TextView progress = (TextView)UpdateLayout.findViewById(R.id.update_percent);
			progress.setText(process_value+" %");
			
			//onLoadData(process_value);	
		    handler.postDelayed(this, 100);
		}
	};
	
    
    private void createUpdateContainer(){
        WindowManager windowManager = (WindowManager) SmcUpdateService.this.getSystemService(Activity.WINDOW_SERVICE);
        WindowManager.LayoutParams wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
        wmLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
        // 以屏幕左上角为原点，设置x、y初始值
        wmLayoutParams.x = 0;
        wmLayoutParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.format = PixelFormat.RGBA_8888;

        wmLayoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM; // 调整悬浮窗口至下边缘中间
        mSmcUpdateContainer = new LinearLayout(SmcUpdateService.this);
        windowManager.addView(mSmcUpdateContainer,wmLayoutParams);
    }

}

