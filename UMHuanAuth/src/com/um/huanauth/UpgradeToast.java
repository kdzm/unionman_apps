package com.um.huanauth;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.CountDownTimer;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import android.os.RemoteException;  
import android.os.SystemClock;  
import android.view.InputDevice;  
import android.view.KeyCharacterMap;  
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.um.huanauth.R;

public class UpgradeToast extends Dialog{
	private static String TAG = "UpgradeToast";
	private Context mContext;
	private int timeOut;
	private TextView mInfo ;
	private CountDownTimer mTimer;
	
	public UpgradeToast(Context context, int time, String info) {
		super(context, R.style.Translucent_NoTitle);
		timeOut = time;
		mContext = context;
		setContentView(R.layout.upgrade_toast_layout);
		mInfo = (TextView) findViewById(R.id.info_txt);
		mInfo.setText(info);
		
		mTimer =  new CountDownTimer(1000*timeOut, 1000) {//总时间， 间隔时间
        	 
            public void onTick(long millisUntilFinished) {
            	
            }
 
            public void onFinish() {
            	doOk();
            }
        }; 
        mTimer.start();
	}
	
	private void doOk() {
    	mTimer.cancel();
    	dismiss();
	}
}
