package cn.com.unionman.umtvsystemserver;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class UsbEventDialog extends Dialog{
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private static String TAG = "KeyMonitorDialog";
	private TextView mTime ;
	private CountDownTimer mTimer;
	private Timer mDismissTimer;
	private TimerTask mDismissTimerTask;
	private Context mContext;
	private Button OKBtn;
	private Button CancelBtn;
	public UsbEventDialog(Context context) {
		super(context, R.style.Translucent_NoTitle);
		mContext = context;
		setContentView(R.layout.usb_dialog_layout);
		mTime = (TextView) findViewById(R.id.timeout_txt);
		mTime.setText(30+"s");
		OKBtn = (Button) findViewById(R.id.usb_ok_btn);
		CancelBtn = (Button) findViewById(R.id.usb_cancel_btn);
		CancelBtn.requestFocus();
	
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
		
		mTimer =  new CountDownTimer(30000, 1000) {//总时间， 间隔时间
            public void onTick(long millisUntilFinished) {
            	mTime.setText(millisUntilFinished/1000+"s");
            }
 
            public void onFinish() {
            	doCancel();
            }
        }; 
        mTimer.start();
	}
	
	private void doOk() {
		mTimer.cancel();
		mTimer = null;
		int currSourceIdx = SourceManagerInterface.getCurSourceId();
		if (currSourceIdx!=EnumSourceIndex.SOURCE_MEDIA) {
			SourceManagerInterface.deselectSource(currSourceIdx, true);
			SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
		}
		Intent intent = new Intent();
		intent.setClassName("com.unionman.filebrowser", "com.unionman.filebrowser.MyMediaActivity");
		intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
		dismiss();
		
		sendsystemDialogCloseBroadCast();
	}
	
	private void doCancel() {
		mTimer.cancel();
		mTimer = null;
    	dismiss();
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
    
    private void sendsystemDialogCloseBroadCast(){
    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	intent.putExtra("reason", "UsbEventDialog");
    	mContext.sendBroadcast(intent);
    }
}
