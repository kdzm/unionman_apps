package com.thirdparty.dataaccess;

import com.thirdparty.dataaccess.IDataAccess.Stub;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;

public class UmAidlService extends Service{
	
	private final String TAG = "UMAidlService";

	private DataAccess dataAccess;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG,"return dataAccess : " + dataAccess);
		return dataAccess;
	}
	
	public class DataAccess extends Stub{

		@Override
		public String getSTBData(String dataName, String extData)
				throws RemoteException {
			
			Log.d(TAG,"service start getSTBData");
			//String Upstr = dataName.toUpperCase();
			Log.d("UMAidlClient","dataName:" + dataName);
				Log.d(TAG,"service start getSTBData");
				//String Upstr = dataName.toUpperCase();
				if(dataName.equalsIgnoreCase("STBID")){
					//机顶盒ID 32位				
					String serialno = SystemProperties.get("ro.serialno");
					String subserialno = serialno.substring(0,32);
					return subserialno;
				}else if(dataName.equalsIgnoreCase("STBType")){
					//机顶盒型号
					String model = SystemProperties.get("ro.product.model");
					return model;
				}else if(dataName.equalsIgnoreCase("SoftwareVersion")){
					//机顶盒软件版本号				
					String version = SystemProperties.get("ro.build.version.incremental");
					return version;
				}else if(dataName.equalsIgnoreCase("IP")){
					//机顶盒IP
					String ipaddress = SystemProperties.get("dhcp.eth0.ipaddress");
					if(ipaddress != null){
						return ipaddress;
					}else {
						String wipaddress = SystemProperties.get("dhcp.wlan0.ipaddress");
						if(wipaddress != null){
							return ipaddress;
						}else{
							return null;	
						}
					}
					
				}else if(dataName.equalsIgnoreCase("MAC")){
					//机顶盒有线MAC				
					String macold = SystemProperties.get("ro.mac");
					//String macnew = macold.replace(":", "");
					String mac = macold.toUpperCase();
					return mac;
				}else if(dataName.equalsIgnoreCase("DeviceID")){
					//中移动设备序号15位
					String serialno = SystemProperties.get("ro.serialno");
					String subserialno = serialno.substring(serialno.length()-15,serialno.length());
					return subserialno;
				}else if(dataName.equalsIgnoreCase("DeviceCode")){
					//设备播放编码 15位，广电串号，出厂为空			Radio serial number	
					String raSeNo = SystemProperties.get("persist.sys.raSeNo","");
					return raSeNo;
				}else if(dataName.equalsIgnoreCase("UserID")){
					//业务账户		
					String userid = getDataFromStbConfig(dataName);				
					return userid;
				}else if(dataName.equalsIgnoreCase("UserToken")){
					//用户token
					return getDataFromStbConfig(dataName);
				}else if(dataName.equalsIgnoreCase("EPGServerURL")){
					//业务调度的EPG地址，格式：http://ip:port/xxx
					return getDataFromStbConfig("AuthURL");
				}else if(dataName.equalsIgnoreCase("LastchannelNum")){
					//最后一次播放的频道号
					String lastchannelNum = SystemProperties.get("persist.sys.lastchannelNum","");
					return lastchannelNum;
				}else if(dataName.equalsIgnoreCase("Reserved")){
					//预留
					String serialno = SystemProperties.get("ro.serialno");
					String reserved = serialno.substring(40,41);
				//	String reserved = SystemProperties.get("persist.sys.reserved","");
					return reserved;
				}else{
					if(dataName != null)
						return "dataNameNotExist";
					else{
						return null;
					}
				}	
		}

		@Override
		public int setSTBData(String dataName, String value, String extData)
				throws RemoteException {
			
			
			return 0;
		}
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("UMAidlClient","!!!!!!!!!!!!!service onCreate");
		dataAccess = new DataAccess();
		
	}
	
	@Override
	public void onDestroy(){
		
	}
	
	
	public String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()----");
		String keyString = key;
		String valueString = null;
		ContentResolver cr = this.getContentResolver();
		Uri uri = Uri.parse("content://stbconfig/summary");
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
	
	
	
	
	

}
