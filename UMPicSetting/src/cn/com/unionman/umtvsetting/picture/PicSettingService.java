package cn.com.unionman.umtvsetting.picture;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.logic.factory.LogicFactory;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.widget.CustomSettingView;

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


public class PicSettingService extends Service{
	private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private static String TAG = "PicSettingService";
	private static String mCloseReason = "";
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
			case Constant.DIALOG_DISMISS_NOW:
				if (mPicSettingDialog != null){
					mPicSettingDialog.dismiss();
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
    private PicSettingDialog mPicSettingDialog;
    private void createPicDialog(int dvbMode) {
    	mPicSettingDialog = new PicSettingDialog(PicSettingService.this,handler, dvbMode);
    	mPicSettingDialog.setCanceledOnTouchOutside(false);
    	mPicSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	mPicSettingDialog = null;
            	if (!mCloseReason.equals("HomeKey")){
                    Intent intent = new Intent();  
                    intent.setAction(PIC_SET_FINISH_ACTION);  
                    sendBroadcast(intent); 
            	}
            }
        });
        Window window = mPicSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.height = 900;
//        lp.width = 1540;
        window.setAttributes(lp);
        mPicSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mPicSettingDialog.show();
}
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String command = intent == null ? null : intent.getStringExtra("type");
		int dvb_mode = intent == null ? -1 : intent.getIntExtra("dvb_cur_mode", -1);
		
		if (command != null){
			if (command.equals("vga_adjust")){
				vgaAdjustDialogOpen();
			}
		}else{
			if (mPicSettingDialog==null) {
				createPicDialog(dvb_mode);
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

    
    private void sendsystemDialogCloseBroadCast(){
    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	intent.putExtra("reason", "PicSettingService");
    	sendBroadcast(intent);
    }
    
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	mCloseReason = reason;
            	Log.e(TAG, "systemDialogCloseReceiver UM_CLOSE_SYSTEM_DIALOG_ACTION reason:"+reason);
            	if ((reason != null) ) {
            		if (!reason.equals("PicSettingService")){
            			if (mPicSettingDialog != null){
                			mPicSettingDialog.dismiss();
                			mPicSettingDialog=null;
            			}
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
    
    private void vgaAdjustDialogOpen(){
    	AlertDialog mAlertDialog = null;
    	CustomSettingView mCustomSettingView = null;
    	LogicFactory mLogicFactory = null;
    	
    	mLogicFactory = new LogicFactory(this);
    	InterfaceLogic logic = mLogicFactory.createLogic(
                0, 11);       
        if (null == logic) {
            return;
        }
        
        List<WidgetType> list = logic.getWidgetTypeList();
        
        if (logic != null && list != null){
        	mAlertDialog = new AlertDialog.Builder(this,
                    R.style.Translucent_NoTitle).create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE); 
            mAlertDialog.show();
            Window window = mAlertDialog.getWindow();
            WindowManager.LayoutParams lp = window
                    .getAttributes();

            mCustomSettingView = new CustomSettingView(
                    mAlertDialog, this, getResources().getString(R.string.VGA_adjust), mLogicFactory
                            .createLogic(0, 11));
            window.setContentView(mCustomSettingView);
            
            lp.width = 1000;
            window.setAttributes(lp);
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
