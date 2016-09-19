package cn.com.unionman.umtvsetting.system;


import cn.com.unionman.umtvsetting.system.util.Constant;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;



public class SysSettingService extends Service{
	private static final String SYS_SET_FINISH_ACTION = "cn.com.unionman.systemsetting.finish";
	private static String TAG = "SysSettingService";
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				mSysSettingDialog.dismiss();
				break;
			}

        }
	};
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    private SysSettingDialog mSysSettingDialog;
    private void createSysDialog() {
    	mSysSettingDialog = new SysSettingDialog(SysSettingService.this,handler);
    	mSysSettingDialog.setCanceledOnTouchOutside(false);
    	mSysSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	mSysSettingDialog = null;
            }
        });
        Window window = mSysSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        mSysSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mSysSettingDialog.show();
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mSysSettingDialog==null) {
			createSysDialog();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
}
