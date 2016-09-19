package com.um.launcher.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.um.launcher.MyMultiScreenActivity;
import com.um.launcher.R;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

public class MultiScreenLayout extends RelativeLayout implements View.OnClickListener, OnFocusChangeListener, OnKeyListener{

	private static final String TAG = "MyMultiScreenActivity";
    private static final int DIRECT_LEFT = 1;
    private static final int DIRECT_RIGHT = 2;
    private static final int DIRECT_UP = 3;
    private static final int DIRECT_DOWN = 4;
	private View[] mImgView;
	private View[] mImgFocusView;
	private TextView[] mTextView;
    private Context mContext;
    private ViewGroup root;
    private View  mViewFocuseMove;
    private View  mImgViewFocuseMove;
    private View  mViewNextFocuse;
    private View parent;
    private Scroller mScroller;
    
    private String[] firstPkg = new String[] {
    		"com.hisilicon.dlna.dmr",//multiscreen
    		"com.hisilicon.miracast",//source
            "com.hisilicon.multiscreen.server",//news
            "com.abc.airplay.player",//housekeeper

    };

    // The main Activity in the first interface
    private String[] firstCls = new String[] {
    		"com.hisilicon.dlna.dmr.DLNASettingActivity",//multiscreen 
    		"com.hisilicon.miracast.activity.WelcomeActivity", //source
            "com.hisilicon.multiscreen.server.MultiScreenServerActivity",//news
            "com.abc.airplay.player.setting.SkyPlaySettingActivity",//housekeeper
            
            
    };
    
    public MultiScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        parent = inflater.inflate(R.layout.multiscreen_layout, this);
        initView();
        mScroller = new Scroller(mContext);
    }

    public MultiScreenLayout(Context context) {
        super(context);
    }
    
	private void initView() {
        
	       mImgView = new View[] {
	    		   parent.findViewById(R.id.app_item_dlna),
	    		   parent.findViewById(R.id.app_item_miracast),
	    		   parent.findViewById(R.id.app_item_multiscreen),
	    		   parent.findViewById(R.id.app_item_skyplay),
	       };
	       mImgFocusView = new View[] {
	    		   parent.findViewById(R.id.view_dlna_focus),
	    		   parent.findViewById(R.id.view_miracast_focus),
	    		   parent.findViewById(R.id.view_multiscreen_focus),
	    		   parent.findViewById(R.id.view_skyplay_focus)
	       };
	       mTextView = new TextView[] {
	               (TextView)parent.findViewById(R.id.app_dlna_txt),
	               (TextView)parent.findViewById(R.id.app_miracast_txt),
	               (TextView)parent.findViewById(R.id.app_multiscreen_txt),
	               (TextView)parent.findViewById(R.id.app_skyplay_txt),

	       };
	       
	       for (int i = 0; i < mImgView.length; i++) {
	    	   mImgView[i].setOnKeyListener(this);
	           mImgView[i].setOnClickListener(this);
	           mImgFocusView[i].getBackground().setAlpha(0);
	           mImgView[i].setOnFocusChangeListener(this);
	       }
	        for (int i = 0; i < mTextView.length; i++)
	        {
	        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
	        }	       
	       root = (ViewGroup)parent.findViewById(R.id.app_multiscreen);
	       mViewFocuseMove = parent.findViewById(R.id.focuse_item);
	       mImgViewFocuseMove = parent.findViewById(R.id.focuse_img);
	       
	    }
	
	 @Override  
	    public boolean onKey(View v, int keyCode, KeyEvent event) {
	    	
	    	Log.d(TAG,"leon---- keyCode:"+keyCode+",v.getId():"+v.getId()+",event.getAction:"+event.getAction());
	    	
	        if (event.getAction() == KeyEvent.ACTION_DOWN) {
	        	if (keyCode == KeyEvent.KEYCODE_MENU
	            		|| keyCode == KeyEvent.KEYCODE_TVSETUP
	            		||keyCode == KeyEvent.KEYCODE_BACK){
	        		Log.d(TAG," keyCode:"+keyCode+",v.getId():"+v.getId());
	        		doExit();
	            	return true;
	            }else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
	            	//foucesStartMove(DIRECT_LEFT);
	            }else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
	            	//foucesStartMove(DIRECT_RIGHT);
	            }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
	            	//foucesStartMove(DIRECT_UP);
	            }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
	            	//foucesStartMove(DIRECT_DOWN);
	            }
	        } 
	        else if (event.getAction() == KeyEvent.ACTION_UP){
	        }
	        return false;  
	    } 
	    
	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {

	    	Log.d(TAG,"leon---- hasFocus:"+hasFocus+",v.getId():"+v.getId());
	        if (hasFocus) {
				 v.bringToFront();	
				if(v.equals(mImgView[0])){
					mTextView[0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					mImgFocusView[0].getBackground().setAlpha(255);
				}else if(v.equals(mImgView[1])){
					mTextView[1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					mImgFocusView[1].getBackground().setAlpha(255);
				}else if(v.equals(mImgView[2])){
					mTextView[2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					mImgFocusView[2].getBackground().setAlpha(255);
				}else if(v.equals(mImgView[3])){
					mTextView[3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					mImgFocusView[3].getBackground().setAlpha(255);
				}
				v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start();
	        } else {
	        	if(v.equals(mImgView[0])){
	        		mTextView[0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));	        		
					mImgFocusView[0].getBackground().setAlpha(0);
				}else if(v.equals(mImgView[1])){
	        		mTextView[1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));					
					mImgFocusView[1].getBackground().setAlpha(0);
				}else if(v.equals(mImgView[2])){
	        		mTextView[2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));					
					mImgFocusView[2].getBackground().setAlpha(0);
				}else if(v.equals(mImgView[3])){
	        		mTextView[3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));					
					mImgFocusView[3].getBackground().setAlpha(0);
				}
	        	v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
	        }
	    }
	    
	    @Override
	    public void onClick(View v) {
	        for (int i = 0; i < mImgView.length; i++) {
	        	if (mImgView[i] == v) {
	                try {
	                    String pkg = firstPkg[i].trim();
	                    String cls = firstCls[i].trim();
	                    ComponentName componentName = new ComponentName(pkg, cls);
	                    Intent mIntent = new Intent();
	                    mIntent.setComponent(componentName);
	                    mContext.startActivity(mIntent);
	                } catch (Exception e) {
	                    Util.appNoSupportPrompt(mContext, R.string.app_no_support);
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	    
	    @Override
		public void computeScroll() {
			super.computeScroll();
			
	        if (mScroller.computeScrollOffset()) {
	        	mViewFocuseMove.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
	        	mViewFocuseMove.bringToFront();
	            postInvalidate();
	            Log.d(TAG,"leon: come to computeScroll:move");
	        }else{
	        	mImgViewFocuseMove.setVisibility(View.INVISIBLE);
	        	if (mViewNextFocuse != null)
	        		mViewNextFocuse.getBackground().setAlpha(255);
	        }
		}
	    
	    private void foucesStartMove(int direct){
			int forwardid = 0;
			View focuseView = root.getFocusedChild();
			//focuseView.setVisibility(View.INVISIBLE);
			Log.d(TAG,"leon: come to foucesStartMove");
			if (focuseView != null)
			{
				Log.d(TAG,"leon: come to foucesStartMove:find focus child");
				switch (direct)
				{
					case DIRECT_LEFT:
						forwardid = focuseView.getNextFocusLeftId();
						break;
					case DIRECT_RIGHT:
						forwardid = focuseView.getNextFocusRightId();
						break;
					case DIRECT_UP:
						forwardid = focuseView.getNextFocusUpId();
						break;
					case DIRECT_DOWN:
						forwardid = focuseView.getNextFocusDownId();
						break;
					default:
						break;
				}
				Log.d(TAG,"leon: come to foucesStartMove:find forwardid:"+forwardid);
				View nextView = findViewById(forwardid);
				if (nextView != null)
				{
					mViewNextFocuse = nextView;
					int startx = focuseView.getLeft();
					int starty = focuseView.getTop();
					int dx = startx - nextView.getLeft();
					int dy = starty - nextView.getTop();
					RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mImgViewFocuseMove.getLayoutParams();
					relativeParams.height = nextView.getHeight();//(focuseView.getHeight() > nextView.getHeight()) ? nextView.getHeight(): focuseView.getHeight();
					relativeParams.width = nextView.getWidth();//(focuseView.getWidth() > nextView.getWidth()) ? nextView.getWidth(): focuseView.getWidth();
					relativeParams.leftMargin = startx;
					relativeParams.topMargin = starty;
					mImgViewFocuseMove.setLayoutParams(relativeParams);
					mImgViewFocuseMove.setVisibility(View.VISIBLE);
					Log.d(TAG,"leon: [startx:"+startx+"][starty:"+starty+"][dx:"+dx+"][dy:"+dy+"]");
					mScroller.startScroll(0, 0, dx, dy, 200);
					Log.d(TAG,"leon: startScroll");
				}
			}
		}
	    
	    public void doExit()
	    {
	    	((MyMultiScreenActivity) mContext).doExit();
	    }
	    
}
