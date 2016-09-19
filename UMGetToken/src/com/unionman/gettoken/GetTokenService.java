
package com.unionman.gettoken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.renderscript.Element;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;
import android.os.SystemProperties;
import android.provider.Settings;
import com.unionman.gettoken.Network;


public class GetTokenService extends Service {
	
	private static final String TAG = "com.unionman.gettoken--------GetTokenService----";
	private static int snmcm_start_flag = 0;
	public static final int GET_USERID = 0;
	public static final int ENCRY_TOKEN = 1;
	public static final int ENCRY_3DES = 2;
	public static final int GET_USER_TOKEN = 3;
	public static final int UPDATE_TOKEN = 4;
	
	private static final String USERID_KEY= "UserID";
	private static final String PASSWORD_KEY = "UserPassword";
	private static final String STB_CONFIG_URL = "content://stbconfig/summary";
	private static final String USER_TOKEN_KEY = "UserToken";
	private static final String NEW_USER_TOKEN_KEY = "NewUserToken";
	private static final String UPDATE_TOKEN_TIME = "UserTokenExpiredTime";
	private static final String UPDATE_ACTION = "UPDATE_USERTOKEN_ACTION";
	private static final String LOGIN_TIPS_ACTION = "action.ZTE.TOKEN.TIPS";
	private static final String EPG_DOMAIN= "EPGDomain";
	private static final String EPG_URL_KEY= "AuthURL";
	private static final String CURRENT_ACTIVITY = "com.unionman.gettoken.LoginActivity";
	//encrytoken data
	private String strEncryURL = "http://183.235.21.100:8090/iptvepg/launcher/mobilelogin.jsp";
	private String strUserID = null;
	private String strPassword = null;
	private String strEncryParam = "&Action=Login&TerminalFlag=1";
	public String strEncryToken = "";

	//usertoken data
	private String strUserURL = "http://183.235.21.100:8090/iptvepg/launcher/mobilegetusertoken.jsp";
	private String strAuthenParam =" ";
	private String strUserParam = "";
	private String strAuthentor = "";
	private String strUserToken = "";
	
	//update token 
	private String strUpdateURL = "http://183.235.21.100:8090/iptvepg/launcher/updateusertoken.jsp";
	private String strUpdateTokenParam = "?SPID=&OldUserToken=";
	private String strUpdateIDParam = "&Action=UserTokenExpired&UserID=";
	
	private String actionString="";
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand()-------");
		// get encryToken from service
		actionString = intent.getAction();
		Log.i(TAG, "actionString ----- "+actionString);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!Network.isNetworkAvailable(getApplicationContext())) {
					Log.e("GetTokenService", "Network is not available!");
					try{
						Thread.sleep(1500);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if (actionString == null) {
					Log.i(TAG, "get userid ----- ");
					Message IDMessage = new Message();
					IDMessage.what = GET_USERID;
					tokenHandler.sendMessage(IDMessage);
				}else if (actionString.equals(UPDATE_ACTION)) {
					Log.i(TAG, "update usertoken !!!!! ----- ");
					Message updateMessage = new Message();
					updateMessage.what = UPDATE_TOKEN;
					tokenHandler.sendMessage(updateMessage);
				}
			}
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}
		
	
	
	private Handler tokenHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i(TAG, "handleMessage() ------- "+msg.what);
			switch (msg.what) {
			case GET_USERID:
				GetTokenService.this.getUserID();
				break;
			
			case ENCRY_TOKEN:
				GetTokenService.this.getEncryToken();
				break;
			case ENCRY_3DES:
				if (strEncryToken == null) {
					GetTokenService.this.getEncryToken();
				}else {
					GetTokenService.this.getAuthenticator();
				}
				break;
			case GET_USER_TOKEN:
				GetTokenService.this.getUserToken();
				break;
			case UPDATE_TOKEN:
				GetTokenService.this.updateUserToken();
				break;
			default:
				break;
			}
		}
	};
	
	public void updateUserToken(){
		Log.i(TAG, "updateUserToken()----");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					String updateUrlString = strUpdateURL + strUpdateTokenParam + strUserToken + strUpdateIDParam + strUserID;
					URL updateUrl  = new URL(updateUrlString);
					
					Log.i(TAG, "updateUrl----"+updateUrl);
					connection = (HttpURLConnection)updateUrl.openConnection();
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
						Log.i(TAG, "sbObject ===== "+sbObject);
						String strResult = sbObject.getString("Result");
						Log.i(TAG, "Result is ===== "+strResult);
						ErrorReport.sendErrorReport(getApplicationContext(), strResult);
						if(strResult.equals("0")){
												
							String newUserToken = sbObject.getString(NEW_USER_TOKEN_KEY);						
							Log.i(TAG, "get userToken  OK ========== "+newUserToken);
							GetTokenService.this.writeData2StbConfig(USER_TOKEN_KEY, newUserToken);
							GetTokenService.this.strUserToken = newUserToken;
							
							String updateTime = sbObject.getString("TokenExpiredTime");
							Log.i(TAG, "get new  updateTime ========== "+updateTime);
							String updateTime2 = "2016.05.04 15:14:00";
							if (updateTime == null) {
								GetTokenService.this.UpdateTokenEveryHours();
							}else {
								setExpiredTokenTime(updateTime);
							}
						//	setExpiredTokenTime(updateTime);
							
							
						}else {
							Log.i(TAG, "updateUserToken error ========== ");								
						}														
										
						
					}else{
						Log.i(TAG, "connect service fail-----");
						Message updateMessage = new Message();
						updateMessage.what = UPDATE_TOKEN;
						tokenHandler.sendMessageDelayed(updateMessage, 60*1000);
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
					//	URL UserUrl = new URL(strUserURL + "?UserID=gztest1&Authenticator=db4458b096aadc6f4e4874ba8e106735af1bcb655ffe2e4c09f75350bedcc2073e3596f08a06f4c25eaa0d5b7844f34879448ea9a251ce69442f5f01b13fb3c30ecf55f9dfeadbe47abd7d5e61bf2b68442f5f01b13fb3c3420d77e3847722c86d0127f473ed6f52442f5f01b13fb3c3be2cd57855982a1c&TerminalFlag=1");
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
							Log.i(TAG, "sbObject ===== "+sbObject);
							String strReturnCode = sbObject.getString("ReturnCode");
							Log.i(TAG, "ReturnCode is ===== "+strReturnCode);
							ErrorReport.sendErrorReport(getApplicationContext(), strReturnCode);
							if(strReturnCode.equals("0")){
													
								String strNTP = sbObject.getString("NTPDomain");
								if (strNTP != null) {
									Settings.Secure.putString(GetTokenService.this.getContentResolver(),"ntp_server", strNTP);
								}
								String userTokenString = sbObject.getString(USER_TOKEN_KEY);
								GetTokenService.this.writeData2StbConfig(USER_TOKEN_KEY, userTokenString);								
								Log.i(TAG, "get userToken  OK ========== "+userTokenString);
								GetTokenService.this.strUserToken = userTokenString;
								//get EPG url 
								String EPGDomain = sbObject.getString(EPG_DOMAIN);
								Log.i(TAG, "get EPGDomain ========== "+EPGDomain);
								GetTokenService.this.writeData2StbConfig(EPG_URL_KEY, EPGDomain);
								
						//		String updateTime = null;
								String updateTime = sbObject.getString(UPDATE_TOKEN_TIME);
								Log.i(TAG, "get updateTime ========== "+updateTime);
								String updateTime2 = "20160504121100";
								if(updateTime == null){
									UpdateTokenEveryHours();	
								}else {
									setUpdateTokenTime(updateTime);
								}
								startPackage("cn.gd.snm.snmcm");								
								
							}else {
								Log.i(TAG, "get UserToken error ========== ");
								String currentActivity = getCurrentActivityName(getBaseContext());
								Log.i(TAG, "get currentActivity ========== "+currentActivity);
								if (currentActivity.equals(CURRENT_ACTIVITY)) {
									sendBroadcast(new Intent(LOGIN_TIPS_ACTION));
								}else {
									Intent intent1 = new Intent();				
									ComponentName componentName = new ComponentName(GetTokenService.this, LoginActivity.class);
									intent1.setComponent(componentName);
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivityAsUser(intent1, UserHandle.OWNER); 
								//	sendBroadcast(new Intent(LOGIN_TIPS_ACTION));
								}
							}																					
						}else{
							Log.i(TAG, "connect service fail-----");
							Message userMessage = new Message();
							userMessage.what = GET_USER_TOKEN;
							tokenHandler.sendMessageDelayed(userMessage, 60*1000);
						}												
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, " re run getUserToken");
					tokenHandler.removeMessages(ENCRY_TOKEN);
					strUserID = getDataFromStbConfig(USERID_KEY);
					Message encryMessage = new Message();
					encryMessage.what = GET_USER_TOKEN;
					tokenHandler.sendMessage(encryMessage);
					
					}finally{
						Log.i(TAG, "disconnect  service  ===== ");
		                connection.disconnect();
					}
				}					
		}).start();
				
	}
	
	public void setUpdateTokenTime(String updateTime){
		Log.i(TAG, "setUpdateTokenTime()----");
		String updateTokenTime = updateTime;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar calendar = Calendar.getInstance();
		Log.i(TAG, "SimpleDateFormat ---- "+sdf.format(calendar.getTime()));
		Date updateDate = null;
		try {
			 updateDate = sdf.parse(updateTokenTime);
		} catch (Exception e) {
			// TODO: handle exception
		}
		calendar.setTime(updateDate);
		
		Log.i(TAG, "update time---- ---- "+sdf.format(calendar.getTime()));
		Log.i(TAG, "calendar.getTimeInMillis() ---- "+calendar.getTimeInMillis());
		
		AlarmManager aManager = (AlarmManager) GetTokenService.this.getSystemService(Context.ALARM_SERVICE);
		Intent updateIntent = new Intent(GetTokenService.this, GetTokenService.class);
		updateIntent.setAction(UPDATE_ACTION);	
		PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, updateIntent, 0);
		aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
		
		
	}
	
	public void UpdateTokenEveryHours(){
		Log.i(TAG, "UpdateTokenEveryHours()----");
		Calendar calendar = Calendar.getInstance();
		Log.i(TAG, "calendar.getTimeInMillis() ---- "+calendar.getTimeInMillis());
		AlarmManager aManager = (AlarmManager) GetTokenService.this.getSystemService(Context.ALARM_SERVICE);
		Intent updateIntent = new Intent(GetTokenService.this, GetTokenService.class);
		updateIntent.setAction(UPDATE_ACTION);	
		PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, updateIntent, 0);
		aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+3600*1000, pIntent);
				
	}
	
	public void setExpiredTokenTime(String newUpdateTime){
		Log.i(TAG, "setExpiredTokenTime()----");
		String updateTokenTime = newUpdateTime;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		Log.i(TAG, "SimpleDateFormat ---- "+sdf.format(calendar.getTime()));
		Date updateDate = null;
		try {
			 updateDate = sdf.parse(updateTokenTime);
		} catch (Exception e) {
			// TODO: handle exception
		}
		calendar.setTime(updateDate);
		
		Log.i(TAG, " new update time---- ---- "+sdf.format(calendar.getTime()));
		Log.i(TAG, "calendar.getTimeInMillis() ---- "+calendar.getTimeInMillis());
		
		AlarmManager aManager = (AlarmManager) GetTokenService.this.getSystemService(Context.ALARM_SERVICE);
		Intent updateIntent = new Intent(GetTokenService.this, GetTokenService.class);
		updateIntent.setAction(UPDATE_ACTION);	
		PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, updateIntent, 0);
		aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
		
		
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
	
	public void getAuthenParam(){
		Log.i(TAG, "getAuthenParam()----");
						
		String RamdomString = String.valueOf(((long)(0+Math.random()*(99999999+1-1))));
		String EncryTokenString = strEncryToken;
		String UserIDsString = strUserID;
		String strSerialno = SystemProperties.get("ro.serialno", "");
		String StbIDString = strSerialno.substring(0, 32);
	//	String IPString = SystemProperties.get("dhcp.eth0.ipaddress", "");
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
	
	
	public void getAuthenticator(){
		
		Log.i(TAG, "getAuthenticator()----");
				
		getAuthenParam();
		byte[] byteAuthenticator = DES3Utils.encryptMode(this.strAuthenParam.getBytes(), getPassword());			
	//	byte[] byteAuthenticator = DES3Utils.encryptMode(this.strAuthenParam.getBytes(), "2");
		Log.i(TAG, "byteAuthenticator  ---- "+byteAuthenticator);
		if (byteAuthenticator == null) {
			Log.i(TAG, "get Authenticator fail -----");
			String currentActivity = getCurrentActivityName(getBaseContext());
			Log.i(TAG, "get currentActivity ========== "+currentActivity);
			if (currentActivity.equals(CURRENT_ACTIVITY)) {
				sendBroadcast(new Intent(LOGIN_TIPS_ACTION));
			}else {
				Intent intent1 = new Intent();				
				ComponentName componentName = new ComponentName(GetTokenService.this, LoginActivity.class);
				intent1.setComponent(componentName);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityAsUser(intent1, UserHandle.OWNER); 
			}
		}
		String Authenticator = byte2Hex(byteAuthenticator);
		this.strAuthentor = Authenticator;
		this.strUserParam = "?UserID="+this.strUserID+"&Authenticator="+Authenticator+"&TerminalFlag=1";
		Log.i(TAG, "strUserParam  ---- "+strUserParam);
		
		Message userMessage = new Message();
		userMessage.what = GET_USER_TOKEN;
		tokenHandler.sendMessage(userMessage);
		
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
						if(GetTokenService.this.strUserID != null){
							Log.i(TAG, "get UserID success ====== "+GetTokenService.this.strUserID);
							userIDsString = GetTokenService.this.strUserID;
							
						}else {
							Log.i(TAG, "get UserID fail and try again----");
							GetTokenService.this.getUserID();
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
											
						Log.i(TAG, "judege if encryToken connect https service success ----");
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
							Log.i(TAG, "sbObject ===== "+sbObject);
							String strReturnCode = sbObject.getString("ReturnCode");
							Log.i(TAG, "ReturnCode is ===== "+strReturnCode);
							ErrorReport.sendErrorReport(getApplicationContext(), strReturnCode);
							if(strReturnCode.equals("0")){
													
								String EncryToken = sbObject.getString("EncryToken");
								Log.i(TAG, "get EncryToken OK ========== "+EncryToken);
								GetTokenService.this.strEncryToken = EncryToken;
					//			startPackage("cn.gd.snm.snmcm");
								
								//send message to hander
								Message message = new Message();
								message.what = ENCRY_3DES;
								tokenHandler.sendMessage(message);
								
							}else {
								Log.i(TAG, "get EncryToken error ========== ");	
								String currentActivity = getCurrentActivityName(getBaseContext());
								Log.i(TAG, "get currentActivity ========== "+currentActivity);
								if (currentActivity.equals(CURRENT_ACTIVITY)) {
									sendBroadcast(new Intent(LOGIN_TIPS_ACTION));
								}else {
									Intent intent1 = new Intent();				
									ComponentName componentName = new ComponentName(GetTokenService.this, LoginActivity.class);
									intent1.setComponent(componentName);
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivityAsUser(intent1, UserHandle.OWNER); 
								}
															
							}																				
							
						}else{
							Log.i(TAG, "connect service fail-----");
							Message encryMessage = new Message();
							encryMessage.what = ENCRY_TOKEN;
							tokenHandler.sendMessageDelayed(encryMessage, 60*1000);
						}												
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					tokenHandler.removeMessages(ENCRY_TOKEN);
					strUserID = getDataFromStbConfig(USERID_KEY);
				    Message encryMessage = new Message();
					encryMessage.what = ENCRY_TOKEN;
					tokenHandler.sendMessage(encryMessage);
					}finally{
						Log.i(TAG, "disconnect  service  ===== ");
		                connection.disconnect();
					}
				
			}
		}).start();		
	}
	
	private String getCurrentActivityName(Context context) {  
		  ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);  
		  
		  
		  // get the info from the currently running task  
		  List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);  
		  
		  
		  ComponentName componentInfo = taskInfo.get(0).topActivity;  
		  return componentInfo.getClassName();  
		}  
	
	
	public String getPassword() {
		Log.i(TAG, "getKey()----");
	//	String keyString = this.strPassword;
		String passwordString = getDataFromStbConfig(PASSWORD_KEY);
		if(passwordString == null || passwordString.isEmpty() ){
			Log.i(TAG, "get Key from txt ------- ");
			this.strPassword = readTxtFile(2);
		}else {
			Log.i(TAG, "get Key from provider ------- ");
			this.strPassword = passwordString;
		}
		Log.i(TAG, "Key------- "+passwordString);
		return passwordString;
		
	}
	public void getUserID(){
		Log.i(TAG, "getUserID()----");
		
		String userIdString = getDataFromStbConfig(USERID_KEY);
		String passwordString = getDataFromStbConfig(PASSWORD_KEY);

		if (userIdString == null || passwordString == null || userIdString.isEmpty()|| passwordString.isEmpty()) {
			Log.i(TAG, "UserID or password is null");
			
			Intent intent1 = new Intent();				
			ComponentName componentName = new ComponentName(this, LoginActivity.class);
			intent1.setComponent(componentName);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//		GetTokenService.this.startActivity(intent1);
			startActivityAsUser(intent1, UserHandle.OWNER); 
		}else {
			
			this.strUserID = userIdString;
			Message encryMessage = new Message();
			encryMessage.what = ENCRY_TOKEN;
			tokenHandler.sendMessage(encryMessage);
		}
		
		Log.i(TAG, "getUserID()--UserID = "+this.strUserID);
	}

	 public String readTxtFile(int which){
		 Log.i(TAG, "readTxtFile()--");
	        try {
	                String encoding="UTF-8";
	                File file=new File("/data/keydata/keydata.txt");
	                if(file.isFile() && file.exists()){ //
	                    InputStreamReader read = new InputStreamReader(
	                    new FileInputStream(file),encoding);//
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineTxt = null;
	                    int i = 1;
	                    while((lineTxt = bufferedReader.readLine()) != null){
	                        
	                 //       Logger.i(TAG, "line = "+ i +"  lineTxt ------ "+lineTxt);
	                        if (i == which) {
								return lineTxt;
							}else {
								i++;
							}	                       	                        
	                    }
	                    read.close();
	        }else{
	   		 	Log.i(TAG, "not found File--");
	        }
	        } catch (Exception e) {
	        	Log.i(TAG, "read file error --");
	            e.printStackTrace();
	        }
	     return null;
	    }
	
	public String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()----");
		String keyString = key;
		String valueString = null;
		ContentResolver cr = GetTokenService.this.getContentResolver();
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
		ContentResolver cr = GetTokenService.this.getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(keyString, valueString);
		
		String[] arg = {"1"};
		cr.update(Uri.parse(STB_CONFIG_URL), cv, "_id=?", arg);

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
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
        String saveNtp=SystemProperties.get("persist.sys.saventp","false");
		if("false".equals(saveNtp)){
			Settings.Secure.putString(getApplicationContext().getContentResolver(),"ntp_server", "183.235.21.140");
	        Settings.Secure.putString(getApplicationContext().getContentResolver(),"ntp_server2", "221.181.100.40");
			SystemProperties.set("persist.sys.saventp","true");
		}
        super.onCreate();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

}
