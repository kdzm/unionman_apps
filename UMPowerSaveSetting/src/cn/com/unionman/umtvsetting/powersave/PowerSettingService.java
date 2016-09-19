package cn.com.unionman.umtvsetting.powersave;


import cn.com.unionman.umtvsetting.powersave.util.Constant;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class PowerSettingService extends Service{
	private static final String POWER_SET_FINISH_ACTION = "cn.com.unionman.power.finish";
	private static String TAG = "PowerSettingService";
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				mPowerSettingDialog.dismiss();
				break;
			}

        }
	};
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    private PowerSettingDialog mPowerSettingDialog;
    private void createPowerDialog() {
    	mPowerSettingDialog = new PowerSettingDialog(PowerSettingService.this,handler);
    	mPowerSettingDialog.setCanceledOnTouchOutside(false);
    	mPowerSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	mPowerSettingDialog = null;
                Intent intent = new Intent();  
                intent.setAction(POWER_SET_FINISH_ACTION);  
                sendBroadcast(intent); 
            }
        });
        Window window = mPowerSettingDialog.getWindow();
        mPowerSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mPowerSettingDialog.show();
}
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mPowerSettingDialog==null) {
			createPowerDialog();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
}
