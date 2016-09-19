package cn.com.unionman.umtvsetting.sound;

import java.util.List;

import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.logic.factory.LogicFactory;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.util.Constant;
import cn.com.unionman.umtvsetting.sound.widget.CustomSettingView;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class SoundSettingService extends Service{
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private static String TAG = "SoundSettingService";
	private static String mCloseReason = "";
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
			case Constant.DIALOG_DISMISS_NOW:
				if (mSoundSettingDialog != null){
					mSoundSettingDialog.dismiss();
				}
				break;
			}

        }
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		registerSystemDialogCloseReceiver();
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    private SoundSettingDialog mSoundSettingDialog;
    private void createPicDialog() {
    	mSoundSettingDialog = new SoundSettingDialog(SoundSettingService.this,handler);
    	mSoundSettingDialog.setCanceledOnTouchOutside(false);
    	mSoundSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	mSoundSettingDialog = null;
            	if (!mCloseReason.equals("HomeKey")){
                    Intent intent = new Intent();  
                    intent.setAction(SOUND_SET_FINISH_ACTION);  
                    sendBroadcast(intent); 
            	}
            }
        });
        Window window = mSoundSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.height = 900;
//        lp.width = 1540;
        window.setAttributes(lp);
        mSoundSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mSoundSettingDialog.show();
}
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String command = intent == null ? null : intent.getStringExtra("type");
		
		if (command != null){
			if (command.equals("sound_balance")){
				soundBalanceDialogOpen();
			}
		}else{
			if (mSoundSettingDialog==null) {
				createPicDialog();
				sendsystemDialogCloseBroadCast();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		unregisterSystemDialogCloseReceiver();
		super.onDestroy();
	}
	
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	Log.e(TAG, "systemDialogCloseReceiver UM_CLOSE_SYSTEM_DIALOG_ACTION reason:" + reason);
            	mCloseReason = reason;
            	if ((reason != null) && (!reason.equals("SoundSettingService"))){
            		if (mSoundSettingDialog != null){
                		mSoundSettingDialog.dismiss();
                		mSoundSettingDialog = null;
                		stopSelf();
            		}
            	}
            }
        }
    };
	
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	registerReceiver(systemDialogCloseReceiver, filter);
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	unregisterReceiver(systemDialogCloseReceiver);
    }
    
    private void sendsystemDialogCloseBroadCast(){
    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	intent.putExtra("reason", "SoundSettingService");
    	sendBroadcast(intent);
    }
    
    private void soundBalanceDialogOpen(){
    	  
    	AlertDialog mAlertDialog = null;
    	CustomSettingView mCustomSettingView = null;
    	LogicFactory mLogicFactory = null;
    	
    	  mLogicFactory = new LogicFactory(this);
	      InterfaceLogic logic = mLogicFactory.createLogic(
	              1, 1);
	      
	      if (null == logic) {
	          return;
	      }
	      
	      List<WidgetType> list = logic.getWidgetTypeList();
	      if (logic != null && list != null) {
	          mAlertDialog = new AlertDialog.Builder(this,
	                  R.style.Translucent_NoTitle).create();
	          mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
	          mAlertDialog.show();
	          Window window = mAlertDialog.getWindow();
	          WindowManager.LayoutParams lp = window
	                  .getAttributes();
	          lp.x = 10;
	          lp.y = 150;	                
	          mCustomSettingView = new CustomSettingView(
	                  mAlertDialog, this, getResources().getStringArray(R.array.voice_setting)[2], mLogicFactory
	                          .createLogic(1, 1));
	          window.setContentView(mCustomSettingView);
	          
	          if (logic != null && list.size() == 1) {
	              lp.y = 250;
	              lp.height = 150;
	              window.setGravity(Gravity.NO_GRAVITY);
	          }
	          mAlertDialog.setOnDismissListener(new OnDismissListener() {
	            @Override
	            public void onDismiss(DialogInterface arg0) {
	            }
	        });
	      }	      
    }
}
