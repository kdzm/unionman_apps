package com.um.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.um.controller.FocusAnimator;
import com.um.dvbsettings.R;
import com.unionman.jazzlib.SystemProperties;
public class BootDVBActivity extends Activity {
	private String DELAY_TO_DVB = "delay_to_dvb";
	private String BOOT_TO_DVB = "boot_to_dvb";
	private Context mContext = null;
	private int sign = -1;
	private int mDelayTime = 0;
    private TextView textYes = null;
    private TextView textNo = null;
    private ImageView focusFrame;
    private final String BOOTCHECK_PROPERTY_NAME = "persist.default.boot.app";
    private LinearLayout mDelayLayout = null;
    private EditText mDelayEt = null;
    private TextView mSaveDelay = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.boot_to_dvb_setting);
        mContext = getApplicationContext();

        textYes = (TextView)findViewById(R.id.TextViewYes);
        textNo = (TextView)findViewById(R.id.TextViewNo);
        focusFrame = (ImageView)findViewById(R.id.bootsetting_focus);
        mDelayLayout = (LinearLayout)findViewById(R.id.delay_layout);
        mDelayEt = (EditText)findViewById(R.id.delay_et);
        mSaveDelay = (TextView)findViewById(R.id.save_delay);

        int defaultType = -1;
        int delayTime = 0;
        String bootParma = SystemProperties.get(BOOTCHECK_PROPERTY_NAME, "launcher");
        if(!("launcher".equalsIgnoreCase(bootParma)))
        {
        	if(bootParma.contains("dvb"))
        	{
        		defaultType = 1;
        		//默认启动的直播
        		String[] parserData = bootParma.split("&&");
        		if(parserData.length == 2)
        		{
        			String[] delayData = parserData[1].split(":");
        			if(delayData[0].contains("delay"))
        			{       			
        			  delayTime = Integer.parseInt(delayData[1]);
        			}
        		}	
        	}else
        	{
        		defaultType = 0;
            	delayTime = 0;
        	}
        }else
        {
        	defaultType = 0;
        	delayTime = 0;
        }
        
        Log.i("DVB_SETTING", "defaultType:" + defaultType + "delayTime:" + delayTime);
        
        sign = System.getInt(mContext.getContentResolver(), BOOT_TO_DVB, defaultType);
        mDelayTime = System.getInt(mContext.getContentResolver(), DELAY_TO_DVB, delayTime);
        
        if(sign == 1)
        {
            textYes.setTextColor(getResources().getColor(R.color.yellow));
            textNo.setTextColor(getResources().getColor(R.color.white));
            mDelayEt.setText((mDelayTime)+"");
            mDelayLayout.setVisibility(View.VISIBLE);
        }else
        {
            textYes.setTextColor(getResources().getColor(R.color.white));
            textNo.setTextColor(getResources().getColor(R.color.yellow));
            mDelayLayout.setVisibility(View.GONE);
        }

        textYes.setOnClickListener(onClickHandler);
        textNo.setOnClickListener(onClickHandler);
        textYes.setOnFocusChangeListener(mFocusChangeListener);
        textNo.setOnFocusChangeListener(mFocusChangeListener);
        mSaveDelay.setOnFocusChangeListener(mFocusChangeListener);
        mDelayEt.setOnFocusChangeListener(mFocusChangeListener);
        mSaveDelay.setOnClickListener(onClickHandler);
	}

    View.OnClickListener onClickHandler = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if(v.getId() == R.id.TextViewYes)
            {
                textYes.setTextColor(getResources().getColor(R.color.yellow));
                textNo.setTextColor(getResources().getColor(R.color.white));
                mDelayLayout.setVisibility(View.VISIBLE);
                System.putInt(mContext.getContentResolver(), BOOT_TO_DVB, 1);               
                
            }else if(v.getId() == R.id.TextViewNo)
            {
                textYes.setTextColor(getResources().getColor(R.color.white));
                textNo.setTextColor(getResources().getColor(R.color.yellow));
                System.putInt(mContext.getContentResolver(), BOOT_TO_DVB, 0);
                mDelayLayout.setVisibility(View.GONE);
            }else if(v.getId() == R.id.save_delay)
            {
            	 Editable editValue = mDelayEt.getText();
                 String tempTimeStr = null;
                 if(editValue != null)
                 {
                 	tempTimeStr = editValue.toString();
                 }
                 int setTime = 0;
                 if(tempTimeStr != null)
                 {               	
                 	setTime = Integer.parseInt(tempTimeStr);           	
                 }
                 Log.i("DVB_SETTING", "setTime:" + setTime);
                 System.putInt(mContext.getContentResolver(), DELAY_TO_DVB, setTime);
                 Toast.makeText(BootDVBActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            int[] location = new  int[2] ;
            if(true == hasFocus)
            {
                v.getLocationOnScreen(location);
                FocusAnimator focusAnimator = new FocusAnimator();
                focusAnimator.flyFoucsFrame(focusFrame, v.getWidth(), v.getHeight(), location[0], location[1]);
            }
        }
    };
}
