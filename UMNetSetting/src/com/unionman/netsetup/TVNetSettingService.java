package com.unionman.netsetup;

import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.unionman.netsetup.view.setting.NetSettingDialog;

public class TVNetSettingService extends Service{

	private static String TAG = "TVNetSettingService";
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    private NetSettingDialog mNetSettingDialog = null;
    private void createNetDialog() {
        mNetSettingDialog = new NetSettingDialog(TVNetSettingService.this,
                NetSettingDialog.FLAG_NET);
        mNetSettingDialog.setCanceledOnTouchOutside(false);
        mNetSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	mNetSettingDialog = null;
            }
        });
        Window window = mNetSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = 600;
        lp.width = 800;
        window.setAttributes(lp);
        mNetSettingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mNetSettingDialog.show();
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mNetSettingDialog==null) {
			Log.i(TAG, "before createNetDialog");
			createNetDialog();
			Log.i(TAG, "after createNetDialog");
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
}
