package com.um.tv.menu.app;

import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.um.tv.menu.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AgingActivity extends Activity{
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "AgingActivity:";
	private static final String AGING_ACTIVITY_FINISH_ACTION = "com.android.um.agingactivity_finish";
	private static final int ACTIVITY_FINISH = 1;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder = null;
	private int mdbSource = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,SUBTAG+"onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aging);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_HISI_TRANSPARENT);
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG,"onResume");
		agingPlayerInit();
		registFinishBroadcast();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG,"onPause");
		unregistFinishBroadcast();
		AgingWindowManager.destroyAging();
		agingPlayerDeinit();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG,"onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEY_AGING){
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(android.content.Context arg0, Intent arg1) {
			String action = arg1.getAction();
			if (action.equals(AGING_ACTIVITY_FINISH_ACTION)){
				finish();
			}
		};
	};
	
	private void registFinishBroadcast(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AGING_ACTIVITY_FINISH_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	private void unregistFinishBroadcast(){
		unregisterReceiver(receiver);
	}
	
	private void agingPlayerInit(){
        RectInfo rect = new RectInfo();
        rect.setX(0);
        rect.setY(0);
        rect.setW(1920);
        rect.setH(1080);
        UmtvManager.getInstance().getSourceManager().setWindowRect(rect, 0);
        
        mdbSource = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
        /*
        if (mdbSource != EnumSourceIndex.SOURCE_VGA){
        	UmtvManager.getInstance().getSourceManager().deselectSource(mdbSource, true);
        	UmtvManager.getInstance().getSourceManager().selectSource(EnumSourceIndex.SOURCE_VGA, 0);
        }*/
	}
	
	private void agingPlayerDeinit(){
		/*
		if (mdbSource == EnumSourceIndex.SOURCE_VGA){
			UmtvManager.getInstance().getSourceManager().deselectSource(EnumSourceIndex.SOURCE_VGA, true);
        	UmtvManager.getInstance().getSourceManager().selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
        	
    		UmtvManager.getInstance().getSourceManager().deselectSource(EnumSourceIndex.SOURCE_MEDIA, true);
        	UmtvManager.getInstance().getSourceManager().selectSource(EnumSourceIndex.SOURCE_VGA, 0);
		}*/
	}
}
