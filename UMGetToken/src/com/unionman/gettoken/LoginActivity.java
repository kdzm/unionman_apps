package com.unionman.gettoken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import com.unionman.gettoken.Network;

public class LoginActivity extends Activity{

	private static final String TAG = "com.unionman.gettoken ----- LoginActivity()---";
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
	private boolean ifGetUserID = false;
	private long lastTime = 0;
	private Context mContext;
	private UserIDObserver mUserIDObserver;
	public static final int GET_USERID = 0;
	public static final int ENCRY_TOKEN = 1;
	public static final int ENCRY_3DES = 2;
	public static final int GET_USER_TOKEN = 3;

	public String strEncryToken = "";
	private String strEncryParam = "&Action=Login&TerminalFlag=1";	
	private String strEncryURL = "http://183.235.21.100:8090/iptvepg/launcher/mobilelogin.jsp";
	private static final String CURRENT_ACTIVITY = "com.unionman.gettoken.LoginActivity";
	
	private String strUserURL = "http://183.235.21.100:8090/iptvepg/launcher/mobilegetusertoken.jsp";
	private String strAuthenParam =" ";
	private String strUserParam = "";
	private String strAuthentor = "";
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

		mUserIDObserver = new UserIDObserver(new Handler());
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOGIN_TIPS_ACTION);
		registerReceiver(tokenReceiver, filter);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()  ---");
		ifGetUserID = false;
		String userID = getDataFromStbConfig(USERID_KEY);
		String password = getDataFromStbConfig(PASSWORD_KEY);
		if (userID == null || password == null || userID.isEmpty() || password.isEmpty()) {
			Log.i(TAG, "userid or passwod is null  ---");
			if (mUserIDObserver == null) {
				mUserIDObserver = new UserIDObserver(new Handler());
			}
			Uri uri = Uri.parse(STB_CONFIG_URL);
			this.getContentResolver().registerContentObserver(uri, true, mUserIDObserver);
		}else {
			Log.i(TAG, "userid and passwod is getted and trun to get token ---");						
			Intent intent =new Intent();
			intent.setComponent(new ComponentName(LoginActivity.this, GetTokenService.class));
			LoginActivity.this.startServiceAsUser(intent, UserHandle.OWNER); 													
		}
				
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOGIN_TIPS_ACTION);
		registerReceiver(tokenReceiver, filter);
		
		this.btn_Login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				Uri uri = Uri.parse(STB_CONFIG_URL);
				LoginActivity.this.getContentResolver().registerContentObserver(uri, true, mUserIDObserver);
				
				String strUserID = et_userID.getText().toString();
				String strPassword = et_password.getText().toString();
																	
				if (strUserID == null || strPassword == null|| strUserID.isEmpty() || strPassword.isEmpty()) {
					Toast.makeText(LoginActivity.this,mContext.getResources().getString(R.string.login_input_userid_tips) ,Toast.LENGTH_LONG ).show();						
				}else {											
					LoginActivity.this.userID = strUserID;
					LoginActivity.this.password = strPassword;
					
					Message encryMessage = new Message();
					encryMessage.what = ENCRY_TOKEN;
					loginHandler.sendMessage(encryMessage);
					tv_loginError.setVisibility(View.INVISIBLE);
				}			
				
			}
		});
		
		this.btn_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				et_userID.setText(null);
				et_password.setText(null);
				tv_loginError.setVisibility(View.INVISIBLE);
			}
		});						
	}
	
	private Handler loginHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i(TAG, "handleMessage() ------- "+msg.what);
			switch (msg.what) {		
			case ENCRY_TOKEN:
				LoginActivity.this.getEncryToken();
				break;
			case ENCRY_3DES:
				if (strEncryToken == null) {
					LoginActivity.this.getEncryToken();
				}else {
					LoginActivity.this.getAuthenticator();
				}
				break;
			case GET_USER_TOKEN:
				LoginActivity.this.getUserToken();
				break;			
			default:
				break;
			}
		}
	};
	
	public void getUserToken(){
		Log.i(TAG, "getUserToken()----");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "getUserToken thread run ----");
				HttpURLConnection connection = null;
				try {					
					URL UserUrl = new URL(strUserURL + strUserParam);	
					Log.i(TAG, "UserUrl----"+UserUrl);
					Log.i(TAG, "UserToken connect https service----");
					connection = (HttpURLConnection)UserUrl.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					connection.setDoInput(true);
										
					Log.i(TAG, "http get responseCode------------  "+connection.getResponseCode());											
					if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
						Log.i(TAG, "connect service success-----");
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
						Log.i(TAG, "get message from service-----");
						String line ;
						StringBuffer sb = new StringBuffer();
						while((line = reader.readLine()) != null){								
							sb.append(line);
						}
						//get data from json
						JSONObject sbObject = new JSONObject(sb.toString());
						String strReturnCode = sbObject.getString("ReturnCode");
						Log.i(TAG, "ReturnCode is ===== "+strReturnCode);
						ErrorReport.sendErrorReport(getApplicationContext(), strReturnCode);
						if(strReturnCode.equals("0")){
							Log.i(TAG, "get UserToken success ========== ");	
							writeData2StbConfig(USERID_KEY, userID);
							writeData2StbConfig(PASSWORD_KEY, password);
							Intent mIntent=new Intent("com.unionman.action.UPDATE_ACCOUNT");
							mIntent.putExtra("userid", userID);
							mIntent.putExtra("userpw", password);
							sendBroadcast(mIntent);
							Log.i(TAG, "send com.unionman.action.UPDATE_ACCOUNT");
						}else {
							Log.i(TAG, "get UserToken error ========== ");	
							sendBroadcast(new Intent(LOGIN_TIPS_ACTION));								
						}																					
					}else{
						Log.i(TAG, "connect service fail-----");							
					}												
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					Log.i(TAG, "disconnect  service  ===== ");
	                connection.disconnect();
	                   
				}
			}
				
		
		}).start();
				
	}
	
	public void getEncryToken(){
		Log.i(TAG, "getEncryToken()----");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				Log.i(TAG, "getEncryToken thread run ----");
				HttpURLConnection connection = null;
				try {
					String userIDsString = "";
					if(LoginActivity.this.userID != null){
						Log.i(TAG, "get UserID success ====== "+LoginActivity.this.userID);
						userIDsString = LoginActivity.this.userID;							
					}
					String encryParamString = "?UserID="+userIDsString+strEncryParam;
					Log.i(TAG, "encryParamString-========== "+encryParamString);
					URL encryUrl = new URL(strEncryURL + encryParamString);
					Log.i(TAG, "EncryToken connect https service----");
					connection = (HttpURLConnection)encryUrl.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					connection.setDoInput(true);
										
					Log.i(TAG, "http get responseCode------------  "+connection.getResponseCode());											
					if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
						Log.i(TAG, "connect service success-----");
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
						Log.i(TAG, "get message from service-----");
						String line ;
						StringBuffer sb = new StringBuffer();
						while((line = reader.readLine()) != null){								
							sb.append(line);
						}
						//get data from json
						JSONObject sbObject = new JSONObject(sb.toString());
						String strReturnCode = sbObject.getString("ReturnCode");
						Log.i(TAG, "ReturnCode is ===== "+strReturnCode);
						ErrorReport.sendErrorReport(getApplicationContext(), strReturnCode);
						if(strReturnCode.equals("0")){													
							String EncryToken = sbObject.getString("EncryToken");
							Log.i(TAG, "get EncryToken OK ========== "+EncryToken);
							LoginActivity.this.strEncryToken = EncryToken;
							Message message = new Message();
							message.what = ENCRY_3DES;
							loginHandler.sendMessage(message);								
						}else {
							Log.i(TAG, "get EncryToken error ========== ");									
							sendBroadcast(new Intent(LOGIN_TIPS_ACTION));																					
						}																				
						
					}else{
						Log.i(TAG, "connect service fail-----");
						
					}												
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					Log.i(TAG, "disconnect  service  ===== ");
	                connection.disconnect();
	                   
				}
				
			}
		}).start();		
	}
	
	private BroadcastReceiver tokenReceiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
		
			tv_loginError.setVisibility(View.VISIBLE);
			ifGetUserID = false;
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
		//	 if (System.currentTimeMillis()-lastTime>2000) {	
			if (!ifGetUserID) {
				String userID = getDataFromStbConfig(USERID_KEY);
				String password = getDataFromStbConfig(PASSWORD_KEY);
				if (userID == null || password == null || userID.isEmpty() || password.isEmpty()) {
					
				}else {
					Log.i(TAG, "userid and passwod is getted  ---");
					if (!ifGetUserID) {
						Log.i(TAG, "trun to get token  ---");
						LoginActivity.this.ifGetUserID = true;
						Log.i(TAG, "ifGetUserID ====== "+ifGetUserID);
						LoginActivity.this.getContentResolver().unregisterContentObserver(mUserIDObserver);
						Intent intent =new Intent();
						intent.setComponent(new ComponentName(LoginActivity.this, GetTokenService.class));
						LoginActivity.this.startServiceAsUser(intent, UserHandle.OWNER); 
					}											
				}
		//		lastTime = System.currentTimeMillis(); 
			 }
			 	
		}
		
	}	
	
	public String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()----");
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
	
	public void writeData2StbConfig(String key,String value){
		Log.i(TAG, "writeData2StbConfig()---");
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
		
		Log.i(TAG, "query "+keyString+" === "+getDataFromStbConfig(keyString));
	}
	
public void getAuthenticator(){
		
		Log.i(TAG, "getAuthenticator()----");
				
		getAuthenParam();
		byte[] byteAuthenticator = DES3Utils.encryptMode(this.strAuthenParam.getBytes(), getPassword());			
		Log.i(TAG, "byteAuthenticator  ---- "+byteAuthenticator);
		if (byteAuthenticator == null) {
			Log.i(TAG, "get Authenticator fail -----");	
				sendBroadcast(new Intent(LOGIN_TIPS_ACTION));	
		}
		String Authenticator = byte2Hex(byteAuthenticator);
		this.strAuthentor = Authenticator;
		this.strUserParam = "?UserID="+this.userID+"&Authenticator="+Authenticator+"&TerminalFlag=1";
		Log.i(TAG, "strUserParam  ---- "+strUserParam);
		
		Message userMessage = new Message();
		userMessage.what = GET_USER_TOKEN;
		loginHandler.sendMessage(userMessage);
		
	}

	public void getAuthenParam(){
		Log.i(TAG, "getAuthenParam()----");
						
		String RamdomString = String.valueOf(((long)(0+Math.random()*(99999999+1-1))));
		String EncryTokenString = strEncryToken;
		String UserIDsString = getUserID();
		String strSerialno = SystemProperties.get("ro.serialno", "");
		String StbIDString = strSerialno.substring(0, 32);
		String IPString = Network.GetIpAddress("eth0");
		if (IPString == null || IPString.equals("0.0.0.0"))
			IPString = Network.GetIpAddress("wlan0");
		String MACString = SystemProperties.get("ro.mac", "");
		String ReservedString = "";
		
		String authensString = RamdomString + "$" +EncryTokenString + "$" +UserIDsString +
				"$" +StbIDString + "$" +IPString + "$" +MACString + "$"+ReservedString+"$CTC";
		this.strAuthenParam = authensString;
		
		Log.i(TAG, "this.strAuthenParam ===== " + this.strAuthenParam);
	}

	//byte to 16
    public static String byte2Hex(byte[] b){  
        String hs="";  
        String stmp="";  
        for(int n=0; n<b.length; n++){  
            stmp = (java.lang.Integer.toHexString(b[n]& 0XFF));  
            if(stmp.length()==1){  
                hs = hs + "0" + stmp;                 
            }else{  
                hs = hs + stmp;  
            }   
        }  
        return hs.toUpperCase();       
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
		Log.i(TAG, "onDestroy");
		if (mUserIDObserver != null) {
			this.getContentResolver().unregisterContentObserver(mUserIDObserver);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStop");
		super.onStop();
	}
}
