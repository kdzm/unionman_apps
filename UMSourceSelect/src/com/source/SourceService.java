package com.source;

import com.source.widget.SourceSelectLayout2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;


public class SourceService extends Service {
	private String TAG = "SourceService";
	private View view = null;
	Context mcontext=this;
	private SourceSelectLayout2 menuContainerlay = null;
	
	public SourceService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		Log.d(TAG,"lgj come to onCreate startId:"+startId);
		
		if (SourceSelectLayout2.isexist == false)
		{
			showWindow();
		}
		super.onStart(intent, startId);
	}
	
	private void showWindow() {
    	Log.d(TAG, "--------> lgj SourceService showWindow----");
    	view = LayoutInflater.from(this).inflate(R.layout.activity_source_select2, null);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ;//
        //wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;;//
        wmParams.gravity = Gravity.TOP; //
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = LayoutParams. FILL_PARENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        wmParams.format = PixelFormat.RGBA_8888;
        
        
        wmParams.windowAnimations=R.style.Animation_view;
        
    	menuContainerlay = (SourceSelectLayout2) (view
                .findViewById(R.id.sourceSelectLayout2));
    	menuContainerlay.setContextType(SourceSelectLayout2.Context_SERVICE_TYPE);
    	/*
    	menuContainerlay.setFocusable(true);
    	menuContainerlay.setFocusableInTouchMode(true);
    	
    	menuContainerlay.setOnKeyListener(new OnKeyListener() {  
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {  
                	menuContainerlay.onKeyDown(keyCode, event);
                } 
                else if (event.getAction() == KeyEvent.ACTION_UP){
                	menuContainerlay.onKeyUp(keyCode, event);
                }
                return false;  
            }  
        }); */
    	
    	wm.addView(view, wmParams);
    	view.setVisibility(View.VISIBLE);
    }
	
	private void hideWindow(){
    	Log.d(TAG, "--------> lgj SourceService hideWindow----");
    	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    	if (view != null)
    	{
    		view.setVisibility(View.INVISIBLE);
    		//menuContainerlay.setFocusable(false);
        	//menuContainerlay.setFocusableInTouchMode(false);
    		wm.removeView(view);
    		menuContainerlay = null;
    		view = null;
    	}
	}
	
	public void doExit(){
		hideWindow();
	}
}
