package cn.com.unionman.umtvsetting.umsettingmenu;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainService extends Service {
	private static final String MENU_SET_FINISH_ACTION = "cn.com.unionman.umtvsetting.umsettingmenu.finish";
    private MainDialog mainDialog;
    private Handler handler = new Handler(){
    	  
        public void handleMessage(Message msg) {   
        	mainDialog.dismiss();
             super.handleMessage(msg);   
        }   
    };
    private void createPicDialog() {
    	mainDialog = new MainDialog(MainService.this, handler);
    	mainDialog.setCanceledOnTouchOutside(false);
    	mainDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	mainDialog = null;
                Intent intent = new Intent();  
                intent.setAction(MENU_SET_FINISH_ACTION);  
                sendBroadcast(intent); 

            }
        });
        Window window = mainDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        mainDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        mainDialog.show();
}    
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mainDialog==null) {
			createPicDialog();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
