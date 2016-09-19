package com.um.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{

	private static final String TAG = "UMAuthHW ----- LoginActivity()---";
	private static final String USERID_KEY= "UserID";
	private static final String PASSWORD_KEY = "UserPassword";
	private static final String STB_CONFIG_URL = "content://stbconfig/summary";
	private static final String LOGIN_TIPS_ACTION = "action.ZTE.TOKEN.TIPS";
	private String userID = null;
	private String password = null;
	private Button btn_Login = null;
	private Button btn_reset = null;
	private EditText et_userID = null;
	private EditText et_password = null;
	private TextView tv_loginError = null;
	private String mAction = "action.GET.TOKEN.CLOSE_LOGIN_ACTIVITY";
	
	private boolean isUserIDAndPasswdPresent= false;
	
	private Context mContext;
	private UserIDObserver mUserIDObserver;
	private long lastTime=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_input);
		this.mContext = getBaseContext();
		this.et_userID = (EditText)findViewById(R.id.user_id);
		this.et_password= (EditText)findViewById(R.id.user_password);
		this.btn_Login = (Button)findViewById(R.id.login_btn);
		this.btn_reset = (Button)findViewById(R.id.reset_btn);
		this.tv_loginError = (TextView)findViewById(R.id.login_error_info);
		this.tv_loginError.setVisibility(View.INVISIBLE);
//		et_userID.setText("jltest1");
//		et_password.setText("1");
		
		mUserIDObserver = new UserIDObserver(new Handler());
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOGIN_TIPS_ACTION);
		registerReceiver(tokenReceiver, filter);
		
		IntentFilter filter2 = new IntentFilter();  
        filter.addAction(mAction);  
        registerReceiver(mLoginActivityReceiver, filter2); 
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mUserIDObserver == null) {
			mUserIDObserver = new UserIDObserver(new Handler());
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOGIN_TIPS_ACTION);
		registerReceiver(tokenReceiver, filter);
		
		String userID = getDataFromStbConfig(USERID_KEY);
		String password = getDataFromStbConfig(PASSWORD_KEY);
		if (userID == null || password == null || userID.isEmpty() || password.isEmpty()) {
			Log.i(TAG, "userid = " + userID + " passwd = " + password);
			Uri uri = Uri.parse(STB_CONFIG_URL);
			this.getContentResolver().registerContentObserver(uri, true, mUserIDObserver);
		}/*else{
			LoginActivity.this.getContentResolver().unregisterContentObserver(mUserIDObserver);
			Intent intent =new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("user_id", userID);
			bundle.putString("user_passwd", password);
			intent.putExtras(bundle);
			intent.setComponent(new ComponentName(LoginActivity.this, MainService.class));
			LoginActivity.this.startServiceAsUser(intent, UserHandle.OWNER);
		}*/
		
		this.btn_Login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				Uri uri = Uri.parse(STB_CONFIG_URL);
				LoginActivity.this.getContentResolver().registerContentObserver(uri, true, mUserIDObserver);
				
				String strUserID = et_userID.getText().toString();
				String strPassword = et_password.getText().toString();
				LoginActivity.this.userID = strUserID;
				LoginActivity.this.password = strPassword;
				Log.i(TAG, "onClick userID === "+strUserID+"  password ====="+strPassword);
													
					if (strUserID == null || strPassword == null|| strUserID.isEmpty() || strPassword.isEmpty()) {
						Toast.makeText(LoginActivity.this,mContext.getResources().getString(R.string.login_input_userid_tips) ,Toast.LENGTH_LONG ).show();						
					}else {
						
//						writeData2StbConfig(USERID_KEY, "jltest2");
//						writeData2StbConfig(PASSWORD_KEY, "1");
						
						//writeData2StbConfig(USERID_KEY, strUserID);
						//writeData2StbConfig(PASSWORD_KEY, strPassword);
						
						LoginActivity.this.getContentResolver().unregisterContentObserver(mUserIDObserver);
						Intent intent =new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("user_id", strUserID);
						bundle.putString("user_passwd", strPassword);
						intent.putExtras(bundle);
						intent.setComponent(new ComponentName(LoginActivity.this, MainService.class));
						LoginActivity.this.startServiceAsUser(intent, UserHandle.OWNER);

					}			
				
			}
		});
		
		this.btn_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				et_userID.setText(null);
				et_password.setText(null);
				
//				writeData2StbConfig(USERID_KEY, null);
//				writeData2StbConfig(PASSWORD_KEY, null);
			}
		});
		
		
		
	}
	
	private BroadcastReceiver tokenReceiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			tv_loginError.setVisibility(View.VISIBLE);
//			Toast.makeText(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.login_error_tips), Toast.LENGTH_LONG).show();
		}
		
	};
	
	private class UserIDObserver extends ContentObserver{

		public UserIDObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			Log.i(TAG, "UserIDObserver -- onChange ---");
			if(System.currentTimeMillis() - lastTime > 2000){
				isUserIDAndPasswdPresent = false;
			}
			 if (true) {	
				String userID = getDataFromStbConfig(USERID_KEY);
				String password = getDataFromStbConfig(PASSWORD_KEY);
				if (userID == null || password == null || userID.isEmpty() || password.isEmpty()) {
					Log.i(TAG, "userid = " + userID + " passwd = " + password);
				}else {
					Log.i(TAG, "userid and passwod is getted  ---isUserIDAndPasswdPresent = " + isUserIDAndPasswdPresent);
					if(!isUserIDAndPasswdPresent){
						LoginActivity.this.getContentResolver().unregisterContentObserver(mUserIDObserver);
						Intent intent =new Intent();
						intent.setComponent(new ComponentName(LoginActivity.this, MainService.class));
						LoginActivity.this.startServiceAsUser(intent, UserHandle.OWNER);
						isUserIDAndPasswdPresent = true;
					}
					
					//start sm.apk
				//	startPackage("cn.gd.snm.snmcm");
				//	Intent intent =new Intent("action.GET.TOKEN.HW.RESTART");
				//	LoginActivity.this.sendBroadcast(intent);
					
				//	LoginActivity.this.finish();
				}
				
				lastTime = System.currentTimeMillis(); 
			 }
			 	
		}
		
	}
	
	 private void startPackage(String pkg) {
		 Log.i(TAG, "startPackage()--- "+pkg);
	        PackageManager packageManager = getPackageManager();
	        Intent intent = packageManager.getLaunchIntentForPackage(pkg);
	        if (intent != null) {
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
	        }
	    }
	 
	public void writeData2StbConfig(String key,String value){
		Log.i(TAG, "writeData2StbConfig()--- key = " + key + " value = " + value);
		String keyString = key;
		String valueString = value;
		ContentResolver cr = LoginActivity.this.getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(keyString, valueString);
		
		String[] arg = {"1"};
		
		int updateSu = cr.update(Uri.parse(STB_CONFIG_URL), cv, "_id=?", arg);
		Log.i(TAG, "if update success =="+cr.update(Uri.parse(STB_CONFIG_URL), cv, "_id=?", arg));
		if(updateSu == 0){
			cr.insert(Uri.parse("content://stbconfig/summary"), cv);
			cr.update(Uri.parse(STB_CONFIG_URL), cv, "_id=?", arg);
		}
		//	cr.insert(Uri.parse("content://stbconfig/summary"), cv);
		Log.i(TAG, "query "+keyString+" === "+getDataFromStbConfig(keyString));
	}
	
	public String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()---- key = " + key);
		String keyString = key;
		String valueString = null;
		ContentResolver cr = LoginActivity.this.getContentResolver();
		Uri uri = Uri.parse(STB_CONFIG_URL);
		String[] prj = {keyString};
		
		Cursor cu =  cr.query(uri, prj, null, null, null);		
		if (cu == null){
			valueString = null;
			Log.i(TAG, keyString+" value is null ======= ");
		}else  if (cu.getCount() > 0) {
			cu.moveToNext();
			valueString = cu.getString(0);
			Log.i(TAG, keyString+" value is  ======= "+valueString);
		
		}
		return valueString;
	}
	
	public String getUserID(){
		Log.i(TAG, "this.userID === "+this.userID);
		return this.userID;
	}
	
	public String getPassword() {
		Log.i(TAG, "  this.password ====="+this.password);
		return this.password;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		//	moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mUserIDObserver != null) {
			this.getContentResolver().unregisterContentObserver(mUserIDObserver);
		}
		if(mLoginActivityReceiver != null)
			LoginActivity.this.unregisterReceiver(mLoginActivityReceiver);
	}
	
	BroadcastReceiver mLoginActivityReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String strAction = intent.getAction();
			Log.d(TAG, "mLogAndWaitReceiver receive action = " + strAction);
			if(strAction.equals(mAction)){
				LoginActivity.this.finish();
			}
		}
	};
	
	
}
