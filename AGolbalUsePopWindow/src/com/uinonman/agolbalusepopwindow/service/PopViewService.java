package com.uinonman.agolbalusepopwindow.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.uinonman.agolbalusepopwindow.R;

public class PopViewService extends Service implements OnItemClickListener,
												OnKeyListener {
	
	private final String TAG = "PopViewService";
	
	private final byte PIC_SETTING = 0;
	private final byte SOUND_SETTING = 1;
	private final byte DEFAULT_DELAY_TIME_LONG = 60;
	private final byte DEFAULT_DELAY_TIME_SHORT = 30;
	private final byte DELAY_STATE = -1;
	
	private byte delay;

	private View view;
	private ListView lv;
	private SimpleAdapter mSimpleAdapter;
	private String[] mainTitle;
	private String[] subTitle;
	
	private WindowManager wm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate.");
		boolean isViewNull = (view == null);
		if(isViewNull){
			Log.d(TAG, "view is null");
			init();
			showView();
		}else{
			Log.d(TAG, "view not null.");
		}
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestory.");
		handler.removeMessages(DELAY_STATE);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand.");
		
		//return super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
	private void init(){
		wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
	}

	private void showView() {
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ;//
        wmParams.gravity = Gravity.CENTER; //
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = 666;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.windowAnimations=R.style.Animation_view;
        view = LayoutInflater.from(getApplicationContext())
        		.inflate(R.layout.activity_main, null);
        initView();
        setData();
        wm.addView(view, wmParams);
        handler.sendEmptyMessage(DELAY_STATE);
	}
	
	private void setData() {
		List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;
		for(byte idx = 0; idx < mainTitle.length; idx++){
			map = new HashMap<String, Object>();
			map.put("main", mainTitle[idx]);
			map.put("sub", subTitle[idx]);
			listItem.add(map);
		}
		Log.v(TAG, "setData finished. listItem's size = "+listItem.size());
		mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.alayout, 
				new String[]{"main", "sub"}, 
				new int[]{R.id.tvMainTitle,R.id.tvSubTitle});
		lv.setAdapter(mSimpleAdapter);
		lv.setOnItemClickListener(this);
		lv.setOnKeyListener(this);
	}

	private void initView() {
		lv = (ListView)view.findViewById(R.id.lvGolbalUseDialog);
		
		mainTitle = new String[]{
				getResources().getString(R.string.pic_setting_main_title),
				getResources().getString(R.string.sound_setting_main_title)
		};
		
		subTitle = new String[]{
				getResources().getString(R.string.pic_setting_sub_title),
				getResources().getString(R.string.sound_setting_sub_title),
		};
		delay = DEFAULT_DELAY_TIME_SHORT;
	}
	
	private void finish(){
    	if (view != null){
    		Log.d(TAG, "view not null.");
    		wm.removeView(view);
    		view = null;
    		stopSelf();
    	}
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(msg.what == DELAY_STATE){
				Log.d(TAG, "delay val="+delay);
				if(delay == 0){
					finish();
				}else {
					delay--;
					handler.sendEmptyMessageDelayed(DELAY_STATE, 1000);
				}
			}
		};
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "you click item.pos = "+position);
		Intent intent = new Intent();
		switch(position){
			case PIC_SETTING:{
				intent.setAction("cn.com.unionman.umtvsetting.picture.service.ACTION");
			}break;
			case SOUND_SETTING:{
				intent.setAction("cn.com.unionman.umtvsetting.sound.service.ACTION");
			}break;
		}
		if(intent.getAction() != null){
			Log.d(TAG, "you're going to jump to the:"+intent.getAction());
			startService(intent);
			finish();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		String action;
		if(event.getAction() == KeyEvent.ACTION_DOWN)
			action = "Action Down.";
		else if(event.getAction() == KeyEvent.ACTION_UP)
			action = "Action Up.";
		else
			action = "Other action.";
		Log.d(TAG, action + ",onKey,keycode="+keyCode);
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
			Log.d(TAG, "you're going to finish this pop view.");
			finish();
			return true;
		}
		delay = DEFAULT_DELAY_TIME_SHORT;
		return false;
	}

}
