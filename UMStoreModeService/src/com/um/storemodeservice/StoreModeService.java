package com.um.storemodeservice;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;
import java.util.Iterator;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.os.SystemProperties;

import com.hisilicon.android.tvapi.UmtvManager;

public class StoreModeService extends Service {

	protected static final String TAG = "StoreModeService";
	protected static final String StorePath = "/system/picture/store";
    private ImageView im_storemode;
	private int imgIdx = 0;
	private KeyReceiver mKeyReceiver = null;
	private View mView ;
	private Handler mHandler = new Handler();
	private static final int PLAY_FRAME_INTERVER = 6*1000;
	private static final int PLAY_TIME_INTERVER = 30*1000;
	private static final int CHECK_TIME_INTERVER = 1*1000;
	
	private boolean mWindowInitFlag = false;
	private int mPlayedImageFrames = 0;
	
	private Runnable mRunCheckFullScreen = new Runnable() {
		
		@Override
		public void run() {
			
			mHandler.postDelayed(this, CHECK_TIME_INTERVER);
		}
	};
	
	private Runnable mRunPlayImages = new Runnable() {
		
		@Override
		public void run() {
			Log.i(TAG,"mRunPlayImages");
			initWindow();
			File file = new File(StorePath) ;
          	if(!file.exists()){
          		Log.i(TAG, StorePath+" file no exists");
          		return;
          	}
          	int fileNum = file.list().length;
          	if(fileNum==0){
          		Log.i(TAG,StorePath + " file num is 0");
          		return;
          	}
          	Log.i(TAG, "file.list()["+imgIdx+"]="+StorePath+"/"+file.list()[imgIdx]);
          	Bitmap bitmap = BitmapFactory.decodeFile(StorePath+"/"+file.list()[imgIdx]); 
          	im_storemode.setImageBitmap(bitmap); 
          	imgIdx++;
			if (imgIdx>=fileNum) {
				imgIdx=0;
			}
			mPlayedImageFrames++;
			Log.i(TAG,"mRunPlayImages="+mPlayedImageFrames);
			if (mPlayedImageFrames > PLAY_TIME_INTERVER/PLAY_FRAME_INTERVER) {
				mHandler.postDelayed(mRunPauseImages, 0);
				return;
			}
			mHandler.postDelayed(this, PLAY_FRAME_INTERVER);
		}
	};
	
	private Runnable mRunPauseImages = new Runnable() {
		
		@Override
		public void run() {
			Log.i(TAG,"mRunPauseImages");
			mHandler.removeCallbacks(mRunPlayImages);
			deinitWindow();
			mPlayedImageFrames = 0;
			mHandler.postDelayed(mRunPlayImages, PLAY_TIME_INTERVER);
		}
	};
	
	private class KeyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.v(TAG, "receive action: " + arg1.getAction());
			int keyCode = arg1.getIntExtra("keyCode", -1);
			int action = arg1.getIntExtra("action", -1);
			Log.v(TAG, "keyCode: " + keyCode + ", action: " + action);

			stopPlayImages();
			startPlayImages(PLAY_TIME_INTERVER);

		}
	}
	
    private void startPlayImages(int delay) {
    	mPlayedImageFrames = 0;
    	mHandler.removeCallbacksAndMessages(null);
    	mHandler.postDelayed(mRunPlayImages, delay);
    }
    
    private void stopPlayImages() {
    	mHandler.removeCallbacksAndMessages(null);
    	deinitWindow();
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void initWindow() {
		if (mWindowInitFlag) {
			return ;
		}
		WindowManager windowManager = (WindowManager) getApplication().getSystemService(Activity.WINDOW_SERVICE);
		Log.i(TAG,"windowManager="+windowManager);
		
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCHABLE;
		layoutParams.gravity = Gravity.TOP;
		layoutParams.x = 0;
		layoutParams.y = 0;
		String w = SystemProperties.get("ro.sf.lcd_density");
		if(w.equals("240")){
			layoutParams.height = 180;
		}else{
			layoutParams.height = 120;
		}
		
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.format = PixelFormat.RGBA_8888;
		layoutParams.type = LayoutParams.TYPE_SYSTEM_OVERLAY; 
		windowManager.addView(mView, layoutParams);
		mWindowInitFlag = true;
	}
	
	private void deinitWindow() {
		if (mWindowInitFlag) {
			WindowManager windowManager = (WindowManager) getApplication().getSystemService(Activity.WINDOW_SERVICE);
			windowManager.removeView(mView);
			mWindowInitFlag = false;
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
	    registerKeyReceiver() ;
	    
	    mView = LayoutInflater.from(this).inflate(R.layout.storemode_view, null);
	    im_storemode = (ImageView) mView.findViewById(R.id.im_storemode);
		if(UmtvManager.getInstance().getPicture().getStoreMode()){
		    startPlayImages(0);
		}else{
			stopSelf();
		}
		super.onCreate();
	}
	
	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		unregisterKeyReceiver();
		stopPlayImages();
		super.onDestroy();
	}
	
	private void registerKeyReceiver() {
		if (mKeyReceiver == null) {
			Log.v(TAG, "regist KeyReceiver...");
			mKeyReceiver = new KeyReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.unionman.intent.ACTION_KEY_PRESSED");
			registerReceiver(mKeyReceiver, filter);
			SystemProperties.set("sys.broadcastkey.enabled", "1");
		}
	}

	private void unregisterKeyReceiver() {
		if (mKeyReceiver != null) {
			Log.v(TAG, "unregist KeyReceiver...");
			unregisterReceiver(mKeyReceiver);
			mKeyReceiver = null;
			SystemProperties.set("sys.broadcastkey.enabled", "0");
		}
	}
	
    public  int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
}
