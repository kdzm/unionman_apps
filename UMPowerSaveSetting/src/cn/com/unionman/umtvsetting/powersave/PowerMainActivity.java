package cn.com.unionman.umtvsetting.powersave;

import cn.com.unionman.umtvsetting.powersave.R;
import cn.com.unionman.umtvsetting.powersave.util.Constant;
import cn.com.unionman.umtvsetting.powersave.util.Util;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
 
public class PowerMainActivity extends Activity {
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	if ((reason != null) && (reason.equals("SelectSource")||reason.equals("HomeKey"))){
            		Util.isHomeSourClick=true;
            		Log.i("hehe","BroadcastReceiver========================="+Util.isHomeSourClick);
            	}
            }
        }
    };
    
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	this.registerReceiver(systemDialogCloseReceiver, filter);
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	this.unregisterReceiver(systemDialogCloseReceiver);
    }
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				if(powerSettingDialog!=null){
				powerSettingDialog.dismiss();
				}
				break;
			}

        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_main);
		registerSystemDialogCloseReceiver();
		createNetDialog();
	}
	
    private PowerSettingDialog powerSettingDialog;
    private void createNetDialog() {
    	powerSettingDialog = new PowerSettingDialog(PowerMainActivity.this,handler);
    	powerSettingDialog.setCanceledOnTouchOutside(false);
    	powerSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	powerSettingDialog = null;
                moveTaskToBack(true);
            	finish();
            }
        });
        Window window = powerSettingDialog.getWindow();
        powerSettingDialog.show();
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	
    	super.onPause();
		if(Util.isHomeSourClick){
		    		finish();
		    	}
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	Util.isHomeSourClick=false;
    	unregisterSystemDialogCloseReceiver();
    }
}
