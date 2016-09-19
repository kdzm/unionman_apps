package com.um.huanauth.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.huanauth.provider.ContentDefine;
import com.um.huanauth.provider.DataBaseUtil;
import com.um.huanauth.provider.HuanAuthInfoBean;
import com.um.huanauth.util.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class AuthBiz {
	
	private static final String TAG = "AuthBiz";
	private static final int ERROR_CODE = -1;
	private static final int SUCCESS_CODE = 0;
	private static final String HTTPS_URL = "https://tvuser-tcl.cedock.com/uc/json";//"https://118.194.161.81/uc/json";
	private static final String HTTP_URL = "http://tvuser-tcl.cedock.com/uc/json";//"http://118.194.161.81:8080/uc/json";
	private Context mContext;
	private AuthInfoAccessHelper mAuthInfoAccessHelper;
	
	public AuthBiz(Context context) {
		Log.d(TAG,"AuthBiz");
		mContext = context;
		mAuthInfoAccessHelper = new AuthInfoAccessHelper();
	}
	
	public int deviceActive(){
		
		Log.d(TAG,"come to deviceActive");
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonDevice = new JSONObject();
		
		try{
			jsonDevice.put("deviceid", Util.getDeviceId());
			jsonDevice.put("devmodel", Util.getDevMode());
			jsonDevice.put("devserial", Util.getDevSerial());
			jsonDevice.put("devmac", Util.getDevMac());
			jsonDevice.put("activeflag", mAuthInfoAccessHelper.getActiveFlag());
			jsonDevice.put("sn", Util.getSn());
			
			jsonRequest.put("action", "DeviceActive");
			jsonRequest.put("locale", "zh_CN");
			jsonRequest.put("timezone", "+0800");
			jsonRequest.put("region", "cn");
			jsonRequest.put("device", jsonDevice);
			
		}catch(JSONException e){
			e.printStackTrace();
			return ERROR_CODE;
		}
		
		String json = HttpUtils.getContentWithPostMethor(HTTPS_URL, null, jsonRequest.toString());
		if (json != null){
			try{
				Log.i(TAG, "deviceActive,return json:"+json);
				
				JSONObject jsonResult = new JSONObject(json);
				JSONObject jsonError = jsonResult.getJSONObject("error");
				JSONObject jsonResultDevice = jsonResult.getJSONObject("device");
				
				int errorCode = jsonError.getInt("code");
				String errorInfo = jsonError.getString("info");
				String dnum = jsonResultDevice.getString("dnum");
				String activeKey = jsonResultDevice.getString("activekey");
				
				if (errorCode == 0){
					BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_DNUM, dnum);
					BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_ACTIVEKEY, activeKey);
					BasicNameValuePair basicNameValuePair3 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_DEVICEACTIVEENABLE, "enable");
					NameValuePair[] pairs = {basicNameValuePair1,basicNameValuePair2,basicNameValuePair3};
					mAuthInfoAccessHelper.updateAuthinfo(pairs);
					mAuthInfoAccessHelper.setDnum(dnum);
					mAuthInfoAccessHelper.setActivekey(activeKey);
					mAuthInfoAccessHelper.setDeviceActiveEable("enable");
					return SUCCESS_CODE;
				}
			}catch(JSONException e){
				e.printStackTrace();
				return ERROR_CODE;
			}
		}
		
		Log.d(TAG, "deviceActive,return null:");
		
		return ERROR_CODE;
	}
	
	public int deviceLogin(){
		Log.d(TAG,"come to deviceLogin");
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonDevice = new JSONObject();
		JSONObject jsonParam = new JSONObject();
		
		try{
			jsonDevice.put("dnum", mAuthInfoAccessHelper.getDnum());
			jsonDevice.put("didtoken", mAuthInfoAccessHelper.getDidtoken());
			jsonDevice.put("activekey", mAuthInfoAccessHelper.getActivekey());
			
			jsonParam.put("ostype", "");
			jsonParam.put("osversion", "");
			jsonParam.put("kernelversion", "");
			jsonParam.put("webinfo", "");
			jsonParam.put("javainfo", "");
			jsonParam.put("flashinfo", "");
			
			jsonRequest.put("action", "DeviceLogin");
			jsonRequest.put("device", jsonDevice);
			jsonRequest.put("param", jsonParam);
			
		}catch(JSONException e){
			e.printStackTrace();
			return ERROR_CODE;
		}
		
		String json = HttpUtils.getContentWithPostMethor(HTTP_URL, null, jsonRequest.toString());
		
		if (json != null){
			try{
				Log.i(TAG, "deviceLogin,return json:"+json);
				
				JSONObject jsonResult = new JSONObject(json);
				JSONObject jsonError = jsonResult.getJSONObject("error");
				JSONObject jsonResultDevice = jsonResult.getJSONObject("device");
				
				int errorCode = jsonError.getInt("code");
				String errorInfo = jsonError.getString("info");
				String activeKey = jsonResultDevice.getString("activekey");
				
				if (errorCode == 0){
					BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_ACTIVEKEY, activeKey);
					BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_DIDTOKEN, mAuthInfoAccessHelper.getDidtoken());
					NameValuePair[] pairs = {basicNameValuePair1,basicNameValuePair2};
					mAuthInfoAccessHelper.updateAuthinfo(pairs);
					mAuthInfoAccessHelper.setActivekey(activeKey);
					return SUCCESS_CODE;
				}
			}catch(JSONException e){
				e.printStackTrace();
				return ERROR_CODE;
			}
		}
		
		Log.d(TAG, "deviceLogin,return json:"+json);
		
		return ERROR_CODE;
	}
	
	public int autoLoginUser(){
		Log.d(TAG,"come to autoLoginUser");
		
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonDevice = new JSONObject();
		
		try{
			jsonDevice.put("dnum", mAuthInfoAccessHelper.getDnum());
			jsonDevice.put("didtoken", mAuthInfoAccessHelper.getDidtoken());
			
			jsonRequest.put("action", "AutoLoginUser");
			jsonRequest.put("device", jsonDevice);
			
		}catch(JSONException e){
			e.printStackTrace();
			return ERROR_CODE;
		}
		
		String json = HttpUtils.getContentWithPostMethor(HTTP_URL, null, jsonRequest.toString());
		
		if (json != null){
			try{
				Log.i(TAG, "autoLoginUser,return json:"+json);
				
				JSONObject jsonResult = new JSONObject(json);
				JSONObject jsonError = jsonResult.getJSONObject("error");
				JSONObject jsonResultUser = jsonResult.getJSONObject("user");
				
				int errorCode = jsonError.getInt("code");
				String errorInfo = jsonError.getString("info");
				String huanId = jsonResultUser.getString("huanid");
				String token = jsonResultUser.getString("token");
				
				if (errorCode == 0){
					BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_HUANID, huanId);
					BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair(ContentDefine.AuthTable.KEY_TOKEN, token);
					NameValuePair[] pairs = {basicNameValuePair1,basicNameValuePair2};
					mAuthInfoAccessHelper.updateAuthinfo(pairs);
					mAuthInfoAccessHelper.setHuanId(huanId);
					mAuthInfoAccessHelper.setToken(token);
					return SUCCESS_CODE;
				}
			}catch(JSONException e){
				e.printStackTrace();
				return ERROR_CODE;
			}
		}
		
		Log.d(TAG, "autoLoginUser,return json:"+json);
		
		return ERROR_CODE;
	}
	
	public class AuthInfoAccessHelper{
		private HuanAuthInfoBean mHuanAuthInfoBean;
		private DataBaseUtil mDataBaseUtil;
		private String mDeviceActiveEable = "";
		private String mDeviceActiveFlag = "";
		
		public AuthInfoAccessHelper() {
			Log.d(TAG, "AuthInfoAccessHelper");
			mHuanAuthInfoBean = new HuanAuthInfoBean();
			mDataBaseUtil = new DataBaseUtil(mContext);
			
			mDataBaseUtil.open();
			Cursor cursor = mDataBaseUtil.fetchAuthInfo(1);
			
			if ((cursor == null) || (cursor.getCount() <= 0)){
				Log.d(TAG,"db table is null, insert record data");
				ContentValues initialValues;
				initialValues = new ContentValues();
				initialValues.put(ContentDefine.AuthTable.KEY_DEVICEID, Util.getDeviceId());
				initialValues.put(ContentDefine.AuthTable.KEY_DNUM, "");
				initialValues.put(ContentDefine.AuthTable.KEY_DEVICEMODE, Util.getDevMode());
				initialValues.put(ContentDefine.AuthTable.KEY_ACTIVEKEY, "");
				initialValues.put(ContentDefine.AuthTable.KEY_DIDTOKEN, "");
				initialValues.put(ContentDefine.AuthTable.KEY_TOKEN, "");
				initialValues.put(ContentDefine.AuthTable.KEY_HUANID, "");
				initialValues.put(ContentDefine.AuthTable.KEY_LICENSETYPE, "");
				initialValues.put(ContentDefine.AuthTable.KEY_LICENSEDATA, "");
				initialValues.put(ContentDefine.AuthTable.KEY_ACTIVEFLAG, Util.getActiveFlag());
				initialValues.put(ContentDefine.AuthTable.KEY_DEVICEACTIVEENABLE, "disable");
				mDataBaseUtil.createAuthInfo(initialValues);
				
				mDeviceActiveFlag = Util.getActiveFlag();
				mDeviceActiveEable = "disable";
                mHuanAuthInfoBean.setDeviceid(Util.getDeviceId());
                mHuanAuthInfoBean.setDnum("");
                mHuanAuthInfoBean.setDevicemode(Util.getDevMode());
                mHuanAuthInfoBean.setActivekey("");
                mHuanAuthInfoBean.setDidtoken("");
                mHuanAuthInfoBean.setToken("");
                mHuanAuthInfoBean.setHuanid("");
                mHuanAuthInfoBean.setLicensetype("");
                mHuanAuthInfoBean.setLicensedata("");
               
			}else{
				String deviceidOld = null;
                String deviceid = null;  
                String dnum = null;
                String devicemode = null;
                String devicemodeOld = null;
                String activekey = null;
                String didtoken = null;
                String token = null;
                String huanid = null;
                String licensetype = null;
                String licensedata = null;
                
                deviceidOld = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEID));  
                deviceid = Util.getDeviceId();
                dnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
                devicemodeOld = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
                devicemode = Util.getDevMode();
                activekey = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_ACTIVEKEY));
                didtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
                token = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_TOKEN));
                huanid = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_HUANID));
                licensetype = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_LICENSETYPE));
                licensedata = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_LICENSEDATA));
                mDeviceActiveFlag = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_ACTIVEFLAG));
                mDeviceActiveEable = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEACTIVEENABLE));
                
                mHuanAuthInfoBean.setDeviceid(deviceid);
                mHuanAuthInfoBean.setDnum(dnum);
                mHuanAuthInfoBean.setDevicemode(devicemode);
                mHuanAuthInfoBean.setActivekey(activekey);
                mHuanAuthInfoBean.setDidtoken(didtoken);
                mHuanAuthInfoBean.setToken(token);
                mHuanAuthInfoBean.setHuanid(huanid);
                mHuanAuthInfoBean.setLicensetype(licensetype);
                mHuanAuthInfoBean.setLicensedata(licensedata);
                
                Log.d(TAG,"db fetch record data:");
                Log.d(TAG,"deviceid:"+deviceid+";dnum:"+dnum+";devicemode:"+devicemode+";activekey:"+activekey+
                		";didtoken:"+didtoken+";token:"+token+";huanid:"+huanid+
                		";licesetype:"+licensetype+";licensedata:"+licensedata+"mDeviceActiveFlag:"+mDeviceActiveFlag+";mDeviceActiveEable:"+mDeviceActiveEable);
                
                if ((deviceidOld != deviceid) || (devicemodeOld != devicemode)){
                	ContentValues newValues;
    				newValues = new ContentValues();
    				newValues.put(ContentDefine.AuthTable.KEY_DEVICEID, mHuanAuthInfoBean.getDeviceid());
    				newValues.put(ContentDefine.AuthTable.KEY_DEVICEMODE, mHuanAuthInfoBean.getDevicemode());
    				mDataBaseUtil.updateAuthInfo(1, newValues);
                }
			}
			mDataBaseUtil.close();
		}
		
		public void setDnum(String dnum){
			mHuanAuthInfoBean.setDnum(dnum);
		}
		
		public void setActivekey(String activekey){
			mHuanAuthInfoBean.setActivekey(activekey);
		}
		
		public void setDeviceActiveEable(String deviceActiveEable){
			mDeviceActiveEable = deviceActiveEable;
		}
		
		public void setHuanId(String huanId){
			mHuanAuthInfoBean.setHuanid(huanId);
		}
		
		public void setToken(String token){
			mHuanAuthInfoBean.setToken(token);
		}
		
		public String getDnum(){
			return mHuanAuthInfoBean.getDnum();
		}
		
		public String getActivekey(){
			return mHuanAuthInfoBean.getActivekey(); 
		}
		
		public String getDidtoken(){
			return Util.string2MD5(mHuanAuthInfoBean.getDeviceid() + mHuanAuthInfoBean.getActivekey());
		}
		
		public String getActiveFlag(){
			return mDeviceActiveFlag;
		}
		
		public void updateAuthinfo(){
			Log.d(TAG,"updateAuthinfo");
			mDataBaseUtil.open();
			ContentValues newValues;
			newValues = new ContentValues();
			newValues.put(ContentDefine.AuthTable.KEY_DEVICEID, mHuanAuthInfoBean.getDeviceid());
			newValues.put(ContentDefine.AuthTable.KEY_DNUM, mHuanAuthInfoBean.getDnum());
			newValues.put(ContentDefine.AuthTable.KEY_DEVICEMODE, mHuanAuthInfoBean.getDevicemode());
			newValues.put(ContentDefine.AuthTable.KEY_ACTIVEKEY, mHuanAuthInfoBean.getActivekey());
			newValues.put(ContentDefine.AuthTable.KEY_DIDTOKEN, mHuanAuthInfoBean.getDidtoken());
			newValues.put(ContentDefine.AuthTable.KEY_TOKEN, mHuanAuthInfoBean.getToken());
			newValues.put(ContentDefine.AuthTable.KEY_HUANID, mHuanAuthInfoBean.getHuanid());
			newValues.put(ContentDefine.AuthTable.KEY_LICENSETYPE, mHuanAuthInfoBean.getLicensetype());
			newValues.put(ContentDefine.AuthTable.KEY_LICENSEDATA, mHuanAuthInfoBean.getLicensedata());
			newValues.put(ContentDefine.AuthTable.KEY_DEVICEACTIVEENABLE, mDeviceActiveEable);
			mDataBaseUtil.updateAuthInfo(1, newValues);
			mDataBaseUtil.close();
		}
		
		public void updateAuthinfo(NameValuePair[] pairs){
			Log.d(TAG,"updateAuthinfo pairs");
			if ((pairs != null) && (pairs.length > 0)){
				mDataBaseUtil.open();
				ContentValues newValues;
				newValues = new ContentValues();
				for (NameValuePair nameValuePair:pairs){
					newValues.put(nameValuePair.getName(), nameValuePair.getValue());
					Log.d(TAG,"updateAuthinfo,nameValuePair.getName():"+nameValuePair.getName()+";nameValuePair.getValue():"+nameValuePair.getValue());
				}
				
				mDataBaseUtil.updateAuthInfo(1, newValues);
				mDataBaseUtil.close();
			}
		}
		
		public boolean getDeviceActiveFlag(){
			Log.d(TAG,"getDeviceActiveFlag,mDeviceActiveEable:"+mDeviceActiveEable);
			return mDeviceActiveEable.equals("enable") ? true : false;
		}
	}
	
	public boolean getDeviceActiveFlag(){
		return mAuthInfoAccessHelper.getDeviceActiveFlag();
	}
}
