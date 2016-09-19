package com.um.auth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class LoginAndWait extends Activity {
	
	private String mAction = "action.GET.TOKEN.CLOSE_LOGIN_AND_WAIT";
	private String mAction2 = "action.GET.TOKEN.SUCCESS";
	private String TAG = "UMAuthHW LoginAndWait";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("UMAuthHW LoginAndWait", "Please Wait");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_wait);
		
		//Intent intent3 = new Intent(this, MainService.class);
		//startServiceAsUser(intent3, UserHandle.OWNER);
		 IntentFilter filter = new IntentFilter();  
         filter.addAction(mAction);  
         filter.addAction(mAction2);
         registerReceiver(mLogAndWaitReceiver, filter);  
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("UMAuthHW LoginAndWait", "onResume isLogined="+MyApplication.isLogin);
		if(MyApplication.isLogin){
			startPackage("cn.gd.snm.snmcm");
		}
	}
	
	private void startPackage(String pkg) {
        PackageManager packageManager = this.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkg);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("UMAuthHW LoginAndWait", "onPause");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("UMAuthHW LoginAndWait", "onDestroy");
		if(mLogAndWaitReceiver != null)
			LoginAndWait.this.unregisterReceiver(mLogAndWaitReceiver);
	}
	
	BroadcastReceiver mLogAndWaitReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String strAction = intent.getAction();
			Log.d(TAG, "mLogAndWaitReceiver receive action = " + strAction);
			if(strAction.equals(mAction)){
				LoginAndWait.this.finish();
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		//	moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
