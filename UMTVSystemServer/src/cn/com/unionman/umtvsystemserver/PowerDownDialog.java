package cn.com.unionman.umtvsystemserver;

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

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.SystemSetting;

public class PowerDownDialog extends Dialog{
	private static String TAG = "KeyMonitorDialog";
	private Context mContext;
	private int mPowerDownType = 0;
	private int timeOut;
	private TextView mTime ;
	private CountDownTimer mTimer;
	private Button OKBtn;
	private Button CancelBtn;
	private Timer mDismissTimer;
	private TimerTask mDismissTimerTask;
	private static SystemSetting  sysSetting;
	
	public PowerDownDialog(Context context, int time, int powerDownType) {
		super(context, R.style.Translucent_NoTitle);
		timeOut = time;
		mPowerDownType = powerDownType;
		mContext = context;
		setContentView(R.layout.powerdown_dialog_layout);
		mTime = (TextView) findViewById(R.id.timeout_txt);
		mTime.setText(10+"s");
		OKBtn = (Button) findViewById(R.id.ok_btn);
		CancelBtn = (Button) findViewById(R.id.cancel_btn);
		
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
		mTimer =  new CountDownTimer(1000*timeOut, 1000) {//总时间， 间隔时间
        	 
            public void onTick(long millisUntilFinished) {
            	mTime.setText(millisUntilFinished/1000+"s");
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
    	doPowerDown();
	}
	
	private void doCancel() {
    	mTimer.cancel();
    	dismiss();
	}
	
	public void myCancel() {
		doCancel();
	}
	
    private void doPowerDown() {
    	if (mPowerDownType == PowerDownEventReciver.POWERDOWN_TYPE_SLEEPON){
    		try{
 		        Context otherContext = mContext.createPackageContext(
 		                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
 		        Editor sharedata = otherContext.getSharedPreferences(
 		                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
 		                  + Context.MODE_MULTI_PROCESS).edit();
	 		    sharedata.putInt("sleeponState",0); 
	 		    sharedata.commit();
    		}catch (NameNotFoundException e) {
            	e.printStackTrace();
            }
    	}
		
	       sysSetting = UmtvManager.getInstance().getSystemSetting();
		sysSetting.suspend();
		Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
		intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
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
