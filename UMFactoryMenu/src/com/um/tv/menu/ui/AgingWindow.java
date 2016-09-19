package com.um.tv.menu.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.um.tv.menu.R;
import com.um.tv.menu.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

public class AgingWindow extends RelativeLayout{
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "AgingWindow:";
	private boolean isShowing = false;
	private static final int AGING_NOTE_INVISIBLE = 1;
	private static final int AGING_COLOR_CHANGE = 2;
	private TextView mAgingNoteTextView = null;
	private TextView mAgingColorTextView = null;
	private Timer mAgingNoteTimer = null;
	private TimerTask mAgingNoteTimerTask = null;
	private Timer mAgingColorTimer = null;
	private TimerTask mAgingColorTimerTask = null;
	private int mColorArray[] = {Color.RED, Color.BLUE, Color.WHITE, Color.GREEN, Color.BLACK, Color.YELLOW};
	private int mColorIndex = 0;
	private TextView tvTimeTextView = null;
	private int seconds = 0;
	private int day = 0;
	private int hour = 0;
	private int min = 0;
	private int sec = 0;
	
	private Handler mHandler = new Handler(){
		 @Override
	     public void handleMessage(Message msg) {
	           switch (msg.what) {
	            case AGING_NOTE_INVISIBLE:
	            	mAgingNoteTextView.setVisibility(View.INVISIBLE);
	            	if (mAgingNoteTimer != null){
	            		mAgingNoteTimer.cancel();
	            		mAgingNoteTimer = null;
	            	}
	            	
	            	if (mAgingNoteTimerTask != null){
	            		mAgingNoteTimerTask.cancel();
	            		mAgingNoteTimerTask = null;
	            	}
	            	break;
	            case AGING_COLOR_CHANGE:
	            	Log.w(TAG, "AGING_COLOR_CHANGE "+day+" day"+hour+":"+min+":"+sec);

					if(0 == seconds%5){
						mColorIndex++;
						if (mColorIndex >= mColorArray.length){
		        			mColorIndex = 0;
		        		}
	        			mAgingColorTextView.setBackgroundColor(mColorArray[mColorIndex]);
					}
					
		    		try{
						tvTimeTextView.setText(String.format("%1$02dday %2$02d:%3$02d:%4$02d", day, hour, min, sec));
		    		} catch(Exception e) {
		    			tvTimeTextView.setText(day + ":" + hour + ":" + min + ":" + sec);
		    			e.printStackTrace();
		    			Log.e("MyTimer onCreate", "Format string error.");
		    		}
	            	break;
	            default:
	                break;
	            }
	            super.handleMessage(msg);
	        }
	};
	
	public AgingWindow(Context context) {
        this(context, null);
    }

    public AgingWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public AgingWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View root = LayoutInflater.from(context).inflate(R.layout.aging_window_layout, this);
        mAgingNoteTextView = (TextView)root.findViewById(R.id.aging_note);
        mAgingColorTextView = (TextView)root.findViewById(R.id.aging_color);
        tvTimeTextView = (TextView)root.findViewById(R.id.agingTimer);
        seconds = 0;
        
    }
    
    public boolean isShowing() {
        return isShowing;
    }
    
    public void setShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }
    
    private class AgingNoteTimerTask extends TimerTask{
    	@Override
    	public void run() {
    		mHandler.sendEmptyMessage(AGING_NOTE_INVISIBLE);
    	}
    };
    
    private class AgingColorTimerTask extends TimerTask{
    	@Override
    	public void run() {
    		seconds++;
			day = (seconds / 86400);
			hour = (seconds / 3600)%24;
			min = (seconds / 60)%60;
			sec = (seconds % 60);
    		mHandler.sendEmptyMessage(AGING_COLOR_CHANGE);
    	}
    };
    
	
    @Override
    protected void onAttachedToWindow() {
    	super.onAttachedToWindow();
    	Log.d(TAG,SUBTAG+"onAttachedToWindow");
    	mAgingNoteTextView.setVisibility(View.VISIBLE);

    	tvTimeTextView.setVisibility(View.VISIBLE);
    	mAgingNoteTextView.bringToFront();
    	tvTimeTextView.bringToFront();
		
    	if(seconds != 0){
    		seconds = 0;
    	}

    	tvTimeTextView.setText("00day 00:00:00");
		
    	mColorIndex = 0;
    	mAgingColorTextView.setBackgroundColor(mColorArray[mColorIndex]);
    	
    	if (mAgingNoteTimer != null){
    		mAgingNoteTimer.cancel();
    		mAgingNoteTimer = null;
    	}
    	
    	mAgingNoteTimerTask = new AgingNoteTimerTask();
    	mAgingNoteTimer = new Timer();
    	mAgingNoteTimer.schedule(mAgingNoteTimerTask, 3000);
    	
    	
    	if (mAgingColorTimer != null){
    		mAgingColorTimer.cancel();
    		mAgingColorTimer = null;
    	}
		

    	mAgingColorTimerTask = new AgingColorTimerTask();
    	mAgingColorTimer = new Timer();
    	mAgingColorTimer.schedule(mAgingColorTimerTask, 1000, 1000);
    	
    }
    
    @Override
    protected void onDetachedFromWindow() {
    	super.onDetachedFromWindow();
    	Log.d(TAG,SUBTAG+"onDetachedFromWindow");
    	
    	mHandler.removeMessages(AGING_NOTE_INVISIBLE);
    	mHandler.removeMessages(AGING_COLOR_CHANGE);
    	if (mAgingNoteTimer != null){
    		mAgingNoteTimer.cancel();
    		mAgingNoteTimer = null;
    	}
    	
    	if (mAgingNoteTimerTask != null){
    		mAgingNoteTimerTask.cancel();
    		mAgingNoteTimerTask = null;
    	}
    	
    	
    	if (mAgingColorTimer != null){
    		mAgingColorTimer.cancel();
    		mAgingColorTimer = null;
    	}
    	
    	if (mAgingColorTimerTask != null){
    		mAgingColorTimerTask.cancel();
    		mAgingColorTimerTask = null;
    	}
    }
    
}
