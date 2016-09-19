package com.um.huanauth.data;

import com.um.huanauth.provider.ContentDefine;
import com.um.huanauth.provider.HuanAuthInfoBean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class HuanClientAuth {
	private static final String TAG = "HuanClientAuth";
	private Context mContext;
	private ContentResolver mContentResolver;
	private HuanAuthInfoBean mHuanAuthInfoBean;
	
	public HuanClientAuth(Context context) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
		mHuanAuthInfoBean = new HuanAuthInfoBean();
		getAuthinfo();
	}
	
	private void getAuthinfo() {   
        
        Uri myUri = ContentDefine.AuthTable.CONTENT_URI;  
        Cursor cursor = mContentResolver.query(myUri, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {  
                String deviceid = null;  
                String dnum = null;
                String devicemode = null;
                String activekey = null;
                String didtoken = null;
                String token = null;
                String huanid = null;
                String licensetype = null;
                String licensedata = null;
                
                deviceid = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEID));  
                //deviceid = Util.getDeviceId();
                dnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
                devicemode = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
                //devicemode = Util.getDevMode();
                activekey = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_ACTIVEKEY));
                didtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
                token = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_TOKEN));
                huanid = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_HUANID));
                licensetype = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_LICENSETYPE));
                licensedata = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_LICENSEDATA));
                
                mHuanAuthInfoBean.setDeviceid(deviceid);
                mHuanAuthInfoBean.setDnum(dnum);
                mHuanAuthInfoBean.setDevicemode(devicemode);
                mHuanAuthInfoBean.setActivekey(activekey);
                mHuanAuthInfoBean.setDidtoken(didtoken);
                mHuanAuthInfoBean.setToken(token);
                mHuanAuthInfoBean.setHuanid(huanid);
                mHuanAuthInfoBean.setLicensetype(licensetype);
                mHuanAuthInfoBean.setLicensedata(licensedata); 
            }
            
            cursor.close();
        }
    }
	
	public String getDeviceid(){
		return mHuanAuthInfoBean.getDeviceid();
	}
	
	public String getDnum(){
		return mHuanAuthInfoBean.getDnum();
	}
	
	public String getDevicemode(){
		return mHuanAuthInfoBean.getDevicemode();
	}
	
	public String getActivekey(){
		return mHuanAuthInfoBean.getActivekey();
	}
	
	public String getDidtoken(){
		return mHuanAuthInfoBean.getDidtoken();
	}
	
	public String getToken(){
		return mHuanAuthInfoBean.getToken();
	}
	
	public String getHuanid(){
		return mHuanAuthInfoBean.getHuanid();
	}
	
	public String getLicensetype(){
		return mHuanAuthInfoBean.getLicensetype();
	}
	
	public String getLicensedata(String licenseType){
		return mHuanAuthInfoBean.getLicensedata();
	}
}
