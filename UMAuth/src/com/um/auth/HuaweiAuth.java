package com.um.auth;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;

import java.util.Random;
import com.json.helper.ParserCompleteListener;
import com.json.helper.huawei.HuaweiAccessToken;
import com.json.helper.huawei.HuaweiAccessTokenHelper;
import com.json.helper.huawei.HuaweiEncryToken;
import com.json.helper.huawei.HuaweiEncryTokenHelper;
import com.json.helper.huawei.HuaweiEpgUrl;
import com.json.helper.huawei.HuaweiEpgUrlHelper;
import com.json.helper.huawei.HuaweiRefreshToken;
import com.json.helper.huawei.HuaweiRefreshTokenHelper;
import com.um.util.EncryptUtils;
import com.um.util.Network;
import com.um.auth.MainService;
import com.um.auth.LoginActivity;



import android.util.Log;
import android.widget.Toast;

public class HuaweiAuth {

	private static final String TAG = "UMAuthHW-----HuaweiAuth---";

	private Context mContext = null;
	private Handler mHandler = new Handler();
	private HuaweiEpgUrl mEpgUrl = null;
	private HuaweiEncryToken mEncryToken = null;
	private HuaweiAccessToken mAccessToken = null;
	private HuaweiRefreshToken mRefreshToken = null;
	private Boolean	flag = true;
	private String STB_CONFIG_URL = "content://stbconfig/summary";
	public static final int EPG_ERR = 0;
	public static final int ENCRY_TOKEN_ERR = 1;
	public static final int USERTOKEN_ERR = 2;
	public static final int GET_USER_TOKEN_ERR = 3;
	public static final int UPDATE_TOKEN = 4;
	public static final int GET_USER_TOKEN_OK = 5;
	private static final String USERID_KEY= "UserID";
	private static final String PASSWORD_KEY = "UserPassword";
	private static final String USERTOKEN_KEY = "UserToken";
	private static final String EPGURL_KEY = "AuthURL";
	public String mUserID = null;
	public String mUserPasswd = null;
	private String mUserToken = null;
	private String mDataEpgUrl = null;
	private int mTokenTimeLeft = 86400;
	private Runnable tokenTimer = null;
	private Thread tokenTimerThread = null;
	private long lastTime=0;
	private boolean isUserTokenPresent = false;
	private boolean isEpgUrlPresent = false;
	private String mAction1 = "action.GET.TOKEN.CLOSE_LOGIN_ACTIVITY";
	private String mAction2 = "action.GET.TOKEN.CLOSE_LOGIN_AND_WAIT";
	private String mAction3 = "action.GET.TOKEN.SUCCESS";
	private int refreshCount=0;
	private int mHeartbit_interval=900;
	private String strRefreshToken="";
	private boolean isFromLogin=false;
	public HuaweiAuth(Context ctx) {
		this.mContext = ctx;

		initHuaweiAuth(null, null);
		
		//mUserID = "006";
		//mUserPasswd = "000000";
		
	}
	
	public void initHuaweiAuth(String userid, String userPasswd){
		if(userid == null){
			mUserID = getDataFromStbConfig(USERID_KEY);
			isFromLogin=false;
		}
		else{
			mUserID = userid;
			isFromLogin=true;
		}
		
		if(userPasswd == null)
			mUserPasswd = getDataFromStbConfig(PASSWORD_KEY);
		else
			mUserPasswd = userPasswd;
		
		mUserToken = getDataFromStbConfig(USERTOKEN_KEY);
		mDataEpgUrl = getDataFromStbConfig(EPGURL_KEY);
		String saveNtp=SystemProperties.get("persist.sys.saventp","false");
		if("false".equals(saveNtp)){
			Settings.Secure.putString(mContext.getContentResolver(),"ntp_server", "183.235.3.59");
			Settings.Secure.putString(mContext.getContentResolver(),"ntp_server2", "183.235.19.59");
			SystemProperties.set("persist.sys.saventp","true");
		}
		Log.d(TAG, "mUserID = " + mUserID + " mUserPasswd = " + mUserPasswd 
				+ " mDataEpgUrl = " + mDataEpgUrl + " mUserToken = " + mUserToken);
		
		if(mUserID == null && mUserPasswd == null){
			//flag = false;
			Intent intent1 = new Intent();		
			ComponentName componentName = new ComponentName(mContext, LoginActivity.class);
			intent1.setComponent(componentName);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivityAsUser(intent1, UserHandle.OWNER);
			//huaweiAuth();
		}
		
		if(mUserID != null && mUserPasswd != null 
				&& mUserToken != null && mDataEpgUrl != null){
			//Log.d(TAG, "isUserTokenPresent = true");
			//startPackage("cn.gd.snm.snmcm");
			//isUserTokenPresent = true;
			//isEpgUrlPresent = true;
		}
	}
	
	private void goForToTheRefreshToken(){
		tokenTimer = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
			}
		};
		
		mHandler.removeCallbacks(tokenTimer);
		mHandler.postDelayed(tokenTimer, 1000);
		
		tokenTimerThread = new Thread(tokenTimer);
		tokenTimerThread.start();
	}
	
	
	private void writeData2StbConfig(String key,String value){
		Log.i(TAG, "writeData2StbConfig()---");
		String keyString = key;
		String valueString = value;
		ContentResolver cr = mContext.getContentResolver();
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
	}
	
	private String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()----");
		String keyString = key;
		String valueString = null;
		ContentResolver cr = mContext.getContentResolver();
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
	
	private Handler tokenHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i(TAG, "handleMessage() ------- "+msg.what);
			switch (msg.what) {
			case EPG_ERR:
			case ENCRY_TOKEN_ERR:
			case GET_USER_TOKEN_ERR:
				writeData2StbConfig("UserToken", "");
				/*Intent intent2 = new Intent(mAction2);
				mContext.sendBroadcast(intent2);*/
				
				Intent intent1 = new Intent();		
				ComponentName componentName = new ComponentName(mContext, LoginActivity.class);
				intent1.setComponent(componentName);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivityAsUser(intent1, UserHandle.OWNER); 
				
				break;
			case GET_USER_TOKEN_OK:
				MyApplication.isLogin=true;
				Intent intent3 = new Intent(mAction3);
				mContext.sendBroadcast(intent3);
				Log.i(TAG, "startPackage(cn.gd.snm.snmcm)");
				startPackage("cn.gd.snm.snmcm");
				break;
			default:
				break;
			}
		}
	};
	
	private void startPackage(String pkg) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkg);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
	
	// am startservice com.um.auth/.MainService
	public void huaweiAuth() {
		if(mUserID == null && mUserPasswd == null){
			Log.d(TAG, "no userid return huawei_auth");
			return ;
		}
		if(!isUserTokenPresent){
			Log.d(TAG, "isUserTokenPresent = " + isUserTokenPresent);
			Intent intent2 = new Intent(mAction1);
			mContext.sendBroadcast(intent2);
			
			Intent intent1 = new Intent();				
			ComponentName componentName = new ComponentName(mContext, LoginAndWait.class);
			intent1.setComponent(componentName);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivityAsUser(intent1, UserHandle.OWNER);
		}
		if(flag){
			Log.d("lwn","First enter huaweiAuth init...");
			while (!Network.isNetworkAvailable(mContext)) {
				Log.e("UMAuthHW", "Network is not available!");
				try{
					Thread.sleep(1500);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			String ip = "http://183.235.3.110:8082";
			String uri;
			String params;
	
			uri = "/EDS/jsp/AuthenticationURL?";
			params = "Action=Login&UserID="+mUserID+"&return_type=1";
			getEpgUrl(ip + uri + params);
	
			uri = "/EPG/oauth/v2/authorize?";
			// params = "response_type=EncryToken&client_id=jltv&userid=hw0001";     client_id=gntv
			params = "response_type=EncryToken&client_id=jltv&userid=" + mUserID;
			getEncryToken(uri + params);
	
			uri = "/EPG/oauth/v2/token?";
			// grant_type=EncryToken&client_id=jltv&UserID=hw0001&DeviceType=EC6108V9U_pub_gdyd&DeviceVersion=1.0.0.1&authinfo=
			params = "grant_type=EncryToken&client_id=jltv&UserID=" + mUserID + "&DeviceType=UNT400B&DeviceVersion=5.3.0&authinfo=";
			getAcessToken(uri + params);
			
			// 采用POST请求
			params = "/EPG/oauth/v2/token?";
			getRefreshToken(params);
			//flag = false;
		}
	}

	/** 获取EpgUrl */
	private Thread epgurlThread = null;
	private Runnable epgurlRunnable = null;
	
	private void getEpgUrl(final String url) {
		epgurlRunnable = new Runnable() {
			HuaweiEpgUrlHelper huaweiEpgUrlHelper = new HuaweiEpgUrlHelper(url,mContext);
			ParserCompleteListener huaweiEpgUrlListener = new ParserCompleteListener() {
				@Override
				public void parserComplete(Object obj) {
					HuaweiEpgUrl epg = (HuaweiEpgUrl) obj;
					if (epg != null) {
						Log.d(TAG, "ip: " + epg.ip + ", epgurl: " + epg.epgurl);
						mEpgUrl = epg;
						
						if (encryTokenRunnable == null)
							return;
						mHandler.postDelayed(encryTokenRunnable, 100);
						isEpgUrlPresent = true;
					} else {
						Log.d("UMAuthHW", "epgurlRunnable retry! isEpgUrlPresent = " + isEpgUrlPresent);
						
						if(!isEpgUrlPresent){
							Message IDMessage = new Message();
							IDMessage.what = EPG_ERR;
							tokenHandler.sendMessage(IDMessage);
							if(!isUserTokenPresent)
								Toast.makeText(mContext, "获取EPG地址失败,请检查您的网络或账号密码" ,Toast.LENGTH_LONG ).show();
						}
						//mHandler.removeCallbacks(epgurlRunnable);
						//mHandler.postDelayed(epgurlRunnable, 5000);
						
						
						
					}
				}
			};

			@Override
			public void run() {
				Log.d(TAG, "Start get epgurl!");
				huaweiEpgUrlHelper.setUrl(url);
				huaweiEpgUrlHelper.setParserCompleteListener(huaweiEpgUrlListener);
			}
		};
		epgurlThread = new Thread(epgurlRunnable);
		epgurlThread.start();
	}

	/** 获取EncryToken */
	private Thread encryTokenThread = null;
	private Runnable encryTokenRunnable = null;

	// /EPG/oauth/v2/authorize?response_type=EncryToken&client_id=jltv&userid=hw0001
	private void getEncryToken(final String uparams) {
		encryTokenRunnable = new Runnable() {
			HuaweiEncryTokenHelper huaweiEncryTokenHelper = new HuaweiEncryTokenHelper(uparams,mContext);
			ParserCompleteListener huaweiEncryTokenListener = new ParserCompleteListener() {
				@Override
				public void parserComplete(Object obj) {
					HuaweiEncryToken encryToken = (HuaweiEncryToken) obj;
					if (encryToken != null) {
						Log.d(TAG, "EncryToken: " + encryToken.encryToken);
						mEncryToken = encryToken;
						if (accessTokenRunnable == null)
							return;
						mHandler.postDelayed(accessTokenRunnable, 100);
					} else {
						Log.d(TAG, "encryTokenRunnable retry!");
						
						//mHandler.removeCallbacks(encryTokenRunnable);
						//mHandler.postDelayed(encryTokenRunnable, 5000);
						if(!isUserTokenPresent){/*假如Usertoken过期了是不是会有问题*/
							Message IDMessage = new Message();
							IDMessage.what = ENCRY_TOKEN_ERR;
							tokenHandler.sendMessage(IDMessage);
							Toast.makeText(mContext, "获取用户令牌失败,请检查网络或用户名和密码" ,Toast.LENGTH_LONG ).show();
						}
					}
				}
			};

			@Override
			public void run() {
				Log.d(TAG, "Start get encry token!");
				String ip = null;
				if (mEpgUrl == null || (ip = mEpgUrl.ip) == null || ip.equals("")) {
					Log.d("UMAuthHW", "epgurl is null now, encryTokenRunnable suspend!");
					if(!isEpgUrlPresent){
						Message IDMessage = new Message();
						IDMessage.what = ENCRY_TOKEN_ERR;
						tokenHandler.sendMessage(IDMessage);
					}
					return;
				}
				huaweiEncryTokenHelper.setUrl("http://" + ip + uparams);
				huaweiEncryTokenHelper.setParserCompleteListener(huaweiEncryTokenListener);
			}
		};
		//encryTokenThread = new Thread(encryTokenRunnable);
		//encryTokenThread.start();
	}

	/** 获取AccessToken */
	private Thread accessTokenThread = null;
	private Runnable accessTokenRunnable = null;

	private void getAcessToken(final String uparams) {
		accessTokenRunnable = new Runnable() {
			HuaweiAccessTokenHelper huaweiAccessTokenHelper = new HuaweiAccessTokenHelper(uparams,mContext);
			ParserCompleteListener huaweiAccessTokenListener = new ParserCompleteListener() {
				@Override
				public void parserComplete(Object obj) {
					HuaweiAccessToken accessToken = (HuaweiAccessToken) obj;
					if (accessToken != null) {
						Log.d(TAG, "AccessToken: " + accessToken.access_token);
						Log.d(TAG, "heartbit_interval: " + accessToken.heartbit_interval);
						mAccessToken = accessToken;
						if(!isUserTokenPresent){
							isUserTokenPresent = true;
							Message IDMessage = new Message();
							IDMessage.what = GET_USER_TOKEN_OK;
							tokenHandler.sendMessage(IDMessage); 
						}
						writeData2StbConfig("UserID", mUserID);
						writeData2StbConfig("UserPassword", mUserPasswd);
						writeData2StbConfig("AuthURL", "http://" + mEpgUrl.ip);/*给南传APK的IP地址需要加http://*/
						writeData2StbConfig("UserToken", mAccessToken.access_token);
						
						if(isFromLogin){
							Intent mIntent=new Intent("com.unionman.action.UPDATE_ACCOUNT");
							mIntent.putExtra("userid", mUserID);
							mIntent.putExtra("userpw", mUserPasswd);
							mContext.sendBroadcast(mIntent);
							Log.i(TAG, "send com.unionman.action.UPDATE_ACCOUNT");
						}
						
						strRefreshToken=mAccessToken.refresh_token;
						Log.d(TAG, "strRefreshToken="+strRefreshToken);
						mHandler.removeCallbacks(refreshTokenRunnable);
						mHeartbit_interval=accessToken.heartbit_interval;
						Log.d(TAG, "postDelayed refreshTokenRunnable: " + mHeartbit_interval);
						mHandler.postDelayed(refreshTokenRunnable, mHeartbit_interval*1000);

					} else {
						Log.d(TAG, "accessTokenRunnable retry!");
						if(!isUserTokenPresent)
							Toast.makeText(mContext, "获取用户令牌失败,请检查网络或用户名和密码" ,Toast.LENGTH_LONG ).show();
						//mHandler.removeCallbacks(accessTokenRunnable);
						//mHandler.postDelayed(accessTokenRunnable, 5000);
						Message IDMessage = new Message();
						IDMessage.what = GET_USER_TOKEN_ERR;
						tokenHandler.sendMessage(IDMessage); 
					}
				}
			};

			@Override
			public void run() {
				Log.d(TAG, "Start get access token!");
				String ip = null;
				String encryToken = null;
				if (mEpgUrl == null || (ip = mEpgUrl.ip) == null || ip.equals("")) {
					Log.d(TAG, "epgurl is null now, accessTokenRunnable suspend!");
					if(!isEpgUrlPresent){
						Message IDMessage = new Message();
						IDMessage.what = GET_USER_TOKEN_ERR;
						tokenHandler.sendMessage(IDMessage);
					}
					return;
				}
				if (mEncryToken == null || (encryToken = mEncryToken.encryToken) == null || encryToken.equals("")) {
					Log.d(TAG, "encry token is null now, accessTokenRunnable suspend!");
					Message IDMessage = new Message();
					IDMessage.what = GET_USER_TOKEN_ERR;
					tokenHandler.sendMessage(IDMessage); 
					return;
				}
				String localIp = Network.GetIpAddress("eth0");
				if (localIp == null || localIp.equals("0.0.0.0"))
					localIp = Network.GetIpAddress("wlan0");
				//Random+"$"+EncryToken+"$"+UserID+"$"+DeviceID+"$"+IP+"$"+MAC+"$"+ Reserved+"$"+"OTT"
				String data = new Random().nextInt(99999999) + "$" + encryToken + "$" + mUserID + "$"
						+ SystemProperties.get("ro.serialno").substring(0, 31) + "$" + localIp + "$" + SystemProperties.get("ro.mac") + "$"
						+ "Reserved" + "$" + "OTT";
				String authinfo = EncryptUtils.encryptMode(data, mUserPasswd);
				huaweiAccessTokenHelper.setUrl("http://" + ip + uparams + authinfo + "&issmarthomestb=1&tvdesktopid=");
				huaweiAccessTokenHelper.setParserCompleteListener(huaweiAccessTokenListener);
			}
		};
		//accessTokenThread = new Thread(accessTokenRunnable);
		//accessTokenThread.start();
	}

	/** 获取RefreshToken */
	private Thread refreshTokenThread = null;
	private Runnable refreshTokenRunnable = null;

	private void getRefreshToken(final String uparams) {
		refreshTokenRunnable = new Runnable() {
			// false: POST请求，true: GET请求（ 默认）
			HuaweiRefreshTokenHelper huaweiRefreshTokenHelper = new HuaweiRefreshTokenHelper(uparams, false,mContext);
			ParserCompleteListener huaweiRefreshTokenListener = new ParserCompleteListener() {
				@Override
				public void parserComplete(Object obj) {
					HuaweiRefreshToken refreshToken = (HuaweiRefreshToken) obj;
					if (refreshToken != null) {
						Log.d(TAG, "RefreshToken: " + refreshToken.access_token);
						Log.d(TAG,"heartbit_interval="+refreshToken.heartbit_interval);
						mRefreshToken = refreshToken;
						
						mHandler.removeCallbacks(refreshTokenRunnable);
						Log.d(TAG, "getRefreshToken postDelayed refreshTokenRunnable: " + mHeartbit_interval);
						mHandler.postDelayed(refreshTokenRunnable, mHeartbit_interval*1000);
						Log.d(TAG,"save UserToken");
						writeData2StbConfig("UserToken", refreshToken.access_token);
						strRefreshToken=refreshToken.refresh_token;
					} else {
						Log.d(TAG, "refreshTokenRunnable retry!");
						if(refreshCount>=3){
							refreshCount=0;
							Intent intent=new Intent("com.unionman.action.REAUTH");
							mContext.sendBroadcast(intent);
							Log.d(TAG, "sendBroadcast com.unionman.action.REAUTH");
						}else{
							refreshCount+=1;
							mHandler.removeCallbacks(refreshTokenRunnable);
							mHandler.postDelayed(refreshTokenRunnable, 5000);
						}
					}
				}
			};
			private String userPasswd = mUserPasswd;

			@Override
			public void run() {
				Log.d(TAG, "Start get refresh token! params="+uparams);
				String ip = null;
				String encryToken = null;
				String refreshToken = null;
				if (mEpgUrl == null || (ip = mEpgUrl.ip) == null || ip.equals("")) {
					Log.d(TAG, "epgurl is null now, refreshTokenRunnable suspend!");
					return;
				}
				if (mEncryToken == null || (encryToken = mEncryToken.encryToken) == null || encryToken.equals("")) {
					Log.d(TAG, "encry token is null now, refreshTokenRunnable suspend!");
					return;
				}
				if (mAccessToken == null || (refreshToken = strRefreshToken) == null || refreshToken.equals("")) {
					Log.d(TAG, "access's refresh token is null now, refreshTokenRunnable suspend!");
					return;
				}
				// grant_type=refresh_token&refresh_token=之前取得的刷新令牌&client_id=jltv&client_secret=客户端密钥
				String data = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=jltv&client_secret=gmcc123";//20160418002
				Log.d(TAG, "refreshTokenRunnable data="+data);
				huaweiRefreshTokenHelper.setUrl("http://" + ip + uparams + data);
				huaweiRefreshTokenHelper.setParserCompleteListener(huaweiRefreshTokenListener);
			}
		};
		refreshTokenThread = new Thread(refreshTokenRunnable);
		refreshTokenThread.start();
	}
	

}
