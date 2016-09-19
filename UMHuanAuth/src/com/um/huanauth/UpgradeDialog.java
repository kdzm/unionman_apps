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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.um.huanauth.R;
import com.um.huanauth.provider.HuanUpgradeInfoBean;

public class UpgradeDialog extends Dialog{
	private static String TAG = "UpgradeDialog";
	private Context mContext;
	private int timeOut;
	private TextView mVersionInfo ;
	private CountDownTimer mTimer;
	private Button OKBtn;
	private Button CancelBtn;
	private Timer mDismissTimer;
	private TimerTask mDismissTimerTask;
	private Handler mHandler;
	private boolean mFinish = false;
	private HuanUpgradeInfoBean mBean;
	
	public UpgradeDialog(Context context, int time, String versionInfo, Handler handler, HuanUpgradeInfoBean bean) {
		super(context, R.style.Translucent_NoTitle);
		timeOut = time;
		mContext = context;
		mHandler = handler;
		mBean = bean;
		setContentView(R.layout.upgrade_dialog_layout);
		mVersionInfo = (TextView) findViewById(R.id.upgrade_versioninfo_txt);
		OKBtn = (Button) findViewById(R.id.ok_btn);
		CancelBtn = (Button) findViewById(R.id.cancel_btn);
		
		if ((versionInfo != null) && !versionInfo.equals("")){
			mVersionInfo.setText(versionInfo);
		}else{
			mVersionInfo.setText(mContext.getResources().getString(R.string.version_info));
		}
		
    	StringBuffer str = new StringBuffer(mContext.getResources().getString(R.string.OK));
    	str.append("(");
    	str.append(time);
    	str.append("s)");
    	OKBtn.setText(str);
    	
		OKBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doOk();
			}
		});
		CancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doCancel();
			}
		});
		
		mTimer =  new CountDownTimer(1000*timeOut, 1000) {//��ʱ�䣬 ���ʱ��
        	 
            public void onTick(long millisUntilFinished) {
            	Log.d(TAG,"onTick... mFinish:"+mFinish);
            	if (mFinish){
            		Log.d(TAG,"onTick... return");
            		return;
            	}
            	//mTime.setText(millisUntilFinished/1000+"s");
            	StringBuffer str = new StringBuffer(mContext.getResources().getString(R.string.OK));
            	str.append("(");
            	str.append(millisUntilFinished/1000);
            	str.append("s)");
            	OKBtn.setText(str);
            }
 
            public void onFinish() {
            	Log.d(TAG,"onFinish...");
            	doCancel();
            }
        }; 
        mTimer.start();
	}
	
	private void doOk() {
		Log.d(TAG,"doOk...");
		mFinish = true;
    	mTimer.cancel();
    	mTimer = null;
    	dismiss();
    	Message msg =  Message.obtain();
    	msg.what = UpgradeService.DO_DOWNLOAD;
    	msg.obj = mBean;
    	mHandler.sendMessage(msg);
	}
	
	private void doCancel() {
		Log.d(TAG,"doCancel...");
		mFinish = true;
    	mTimer.cancel();
    	mTimer = null;
    	dismiss();
    	mHandler.sendEmptyMessage(UpgradeService.DOWNLOAD_CANCLE);
	}
	
	public void myCancel() {
		doCancel();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_SOURCE){
    		mDismissTimer = new Timer();
    		mDismissTimerTask = new TimerTask() {
				@Override
				public void run() {
					doCancel();
					mDismissTimerTask.cancel();
					mDismissTimer.cancel();
				}
			};
			mDismissTimer.schedule(mDismissTimerTask, 500);
    	}
    	super.onKeyDown(keyCode, event);
    	return false;
    }
}
