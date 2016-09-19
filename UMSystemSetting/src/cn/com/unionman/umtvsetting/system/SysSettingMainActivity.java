package cn.com.unionman.umtvsetting.system;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import cn.com.unionman.umtvsetting.system.util.Common;
import cn.com.unionman.umtvsetting.system.util.Constant;
 
public class SysSettingMainActivity extends Activity {
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				if(sysSettingDialog!=null){
					sysSettingDialog.dismiss();	
				}
				break;
			}

        }
	};

	
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	if ((reason != null) && (reason.equals("SelectSource")||reason.equals("HomeKey"))){
            		Common.isHomeSourClick=true;
            	}
            }
        }
    };
    
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	this.registerReceiver(systemDialogCloseReceiver, filter);
    	Log.i("hehe", "===registerReceiver");
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	this.unregisterReceiver(systemDialogCloseReceiver);
    	Log.i("hehe", "===unregisterReceiver");
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_main);
		String action = getIntent().getStringExtra(Constant.LAUNCH_ACTION);
		createNetDialog(action);
		registerSystemDialogCloseReceiver();
	}
	
    private SysSettingDialog sysSettingDialog;
    private void createNetDialog(String action) {
    	sysSettingDialog = new SysSettingDialog(SysSettingMainActivity.this,handler, action);
    	sysSettingDialog.setCanceledOnTouchOutside(false);
    	sysSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	Log.i("hehe", "activity ===onDismiss");
            	sysSettingDialog.unregester();
            	sysSettingDialog = null;
                moveTaskToBack(true);
            	finish();
            	
            }
        });
        Window window = sysSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        sysSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        sysSettingDialog.show();
    }
 
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	
    	if(Common.isHomeSourClick){
    		
        	this.finish();
    	}
    	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	unregisterSystemDialogCloseReceiver();
    	if (sysSettingDialog != null){
    		Log.i("hehe", "===sysSettingDialog");
    		sysSettingDialog.unregester();
    	}else{
    		Log.i("hehe", "===null");
    	}
    	
    	Common.isHomeSourClick=false;
    	Log.i("hehe", "===onDestroy");
    	super.onDestroy();
    }
  
    
}
