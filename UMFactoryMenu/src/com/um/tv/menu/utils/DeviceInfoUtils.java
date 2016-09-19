package com.um.tv.menu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Hex;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusFactory;

public class DeviceInfoUtils {
	                                           
	private static final String TESTDEVICEID = "bb2b77c6d3959b5e797c38d08024ba6670f3d819";//"ca1aa782c39c6688c2d50facc8522956051ed349";
	private static final String TESTDEMODE = "UNION-CN-HISI80-UW8000-2D";
	private static final String TAG = "DeviceInfoUtils";

	public static int reloadDeviceInfo(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		int strDeviceId = mFactory.reloadUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_DEVICE_ID);
		return strDeviceId;
	}
	
	public static String getDeviceId(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		String strDeviceId = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_DEVICE_ID);
		return strDeviceId;
	}
	
	public static String getDevMode(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		String strDeviceMode = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_CLIENT_TYPE);
		if ((strDeviceMode == null) || strDeviceMode.equals("")){
			strDeviceMode = TESTDEMODE;
		}
		
		return strDeviceMode;
	}

	public static String getClientType(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		String ct = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_CLIENT_TYPE);
		
		return ct;
	}
	
	public static String getDevSerial(){
		return "";
	}
	
	public static String getDevMac(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		String mac = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_MAC_ADDRESS);
		
		return mac;
	}
	
	public static String getActiveFlag(){
		
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		
		String strUUID = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_UUID);
		Log.d(TAG,"getActiveFlag strUUID:"+strUUID);
		if ((strUUID == null) || strUUID.equals("")){
			strUUID = UUID.randomUUID().toString();
			mFactory.setUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_UUID, strUUID);
			Log.d(TAG,"getActiveFlag randomUUID strUUID:"+strUUID);
		}
		
		return strUUID;
	}
	
	public static String getSn(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		String sn = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_SERIAL_NO);
		return sn;
	}

	public static String getHwVersion(){
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		String hwv = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_HARDWARE_VER);
		return hwv;
	}
	
	public static String getSoftVersion(){
		String tmp = "";
		tmp = SystemProperties.get("ro.umtv.sw.version");
		return tmp;
	}
	
	public static String getAppId(){
		String tmp = "";
		tmp = SystemProperties.get("ro.umtv.sw.version");
		tmp = tmp.substring(0, 15);
		return tmp;
	}
	
	public static String getProjectId(){
		String tmp = "1";
		
		CusFactory mFactory = UmtvManager.getInstance().getFactory();
		tmp = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);
		if (tmp == null){
			tmp = "1";
		}
		
		return tmp;
	}
	
	/*** 
     * MD5���� ���32λmd5�� 
     */  
    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];
        
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
  
    }
    
    public static String getFileMd5(File f) {
        FileInputStream fis = null;
        MessageDigest md5 = null;
        
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return null;  
        }  
        
        try {
            fis = new FileInputStream(f);
            //100KB each time
            byte[] buffer = new byte[102400];
            int length;
            long loopCount = 0;
            while ((length = fis.read(buffer)) != -1) {
            	md5.update(buffer, 0, length);
                loopCount++;
            }
            
            Log.d(TAG,"read file to buffer loopCount:"+loopCount);

            return new String(Hex.encodeHex(md5.digest()));
        } catch (FileNotFoundException e) {
            Log.d(TAG,"md5 file " + f.getAbsolutePath() + " failed:" + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.d(TAG,"md5 file " + f.getAbsolutePath() + " failed:" + e.getMessage());
            return null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static long getSdCardAvailableSize(){
		long size = 0;
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();  
			StatFs sf = new StatFs(sdcardDir.getPath());
			size = sf.getAvailableBytes();
		}
		
		return size;
    }
    
    public static long getCacheAvailableSize(){
    	long size = 0;
    	File cacheDir = Environment.getDownloadCacheDirectory();
    	StatFs sf = new StatFs(cacheDir.getPath());
		size = sf.getAvailableBytes();
		
    	return size;
    }
    
    public static long getCacheTotalSize(){
    	long size = 0;
    	File cacheDir = Environment.getDownloadCacheDirectory();
    	StatFs sf = new StatFs(cacheDir.getPath());
		size = sf.getTotalBytes();
		
    	return size;
    }
    
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
    
    public static void cleanCache(){
    	File cacheDir = Environment.getDownloadCacheDirectory();
    	deleteFilesByDirectory(cacheDir);
    }
    
    public static boolean isExternalStorageMount() {
        String[] path = new String[64];
        int cnt = 0, count = 0;
        boolean ret = false;
        
        try {
            // support for DevType
            IBinder service = ServiceManager.getService("mount");

            if (service != null) {
                IMountService mountService = IMountService.Stub
                                             .asInterface(service);
                List<android.os.storage.ExtraInfo> mountList = mountService
                                                               .getAllExtraInfos();
                cnt = mountList.size();
                
                for (int i = 0; i < cnt; i++) {
                    path[i] = mountList.get(i).mMountPoint;

                    String typeStr = mountList.get(i).mDevType;
                    Log.d(TAG,"leon... typeStr="+typeStr);
                    if (path[i].contains("/mnt/nand")) {
                    	count++;
                    }
                    else if (typeStr.equals("SDCARD") && !(path[i].contains("/storage/emulated"))) {
                    	ret = true;
                    	count++;
                    	break;
                    }
                    else if (typeStr.equals("SATA")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB2.0")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB3.0")) {
                    	count++;
                    }
                    else if (typeStr.equals("UNKOWN")) {
                    	count++;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return ret;
        }
        
        return ret;
    }
    
    static public String getNetUpgradeUrl() {
    	String url = null;
		File file = new File("/vendor/etc/netupgradeurl.xml");
		Log.d(TAG, "getNetUpgradeUrl Enter ");
		
		if (file.exists() && file.isFile()){
	    	try {
		    	InputStream xml = new FileInputStream(file);
		    	XmlPullParser pullParser = Xml.newPullParser();
		        pullParser.setInput(xml, "UTF-8");     
		        int event = pullParser.getEventType();
		        
		        while (event != XmlPullParser.END_DOCUMENT) {		            
		            switch (event) {
		            case XmlPullParser.START_DOCUMENT:
		                break;    
		            case XmlPullParser.START_TAG:   
		            	String name = pullParser.getName();
						if ("url".equals(name)) {
							url = pullParser.getAttributeValue(0);
							Log.d(TAG, "getNetUpgradeUrl url is "+url);
	                    }
		                break;		                
		            case XmlPullParser.END_TAG:
		                break;
		            }
		            event = pullParser.next();
		        }
	    	} catch (FileNotFoundException e) {
	    		e.printStackTrace();
	    		Log.d(TAG,"ERROR: netupgradeurl.xml not found.");
	    		return null;
	    	} catch (XmlPullParserException e) {
	    		e.printStackTrace();
	    		Log.d(TAG,"ERROR: parse netupgradeurl.xml failed.");
	        	return null;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		Log.d(TAG,"ERROR: read netupgradeurl.xml failed.");
	    		return null;
	    	}
		}
		
		return url;
	}
    
    static public String getUUID(){
    	String strUUID = UUID.randomUUID().toString();
    	
    	return strUUID;
    }
}
