package cn.com.unionman.umtvsetting.appmanage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import cn.com.unionman.umtvsetting.appmanage.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class UpdateHelper {
	
	public static final int UPDATA_NONEED = 1;
	public static final int UPDATA_CLIENT = 2;
	public static final int GET_UNDATAINFO_ERROR = 3;
	public static final int DOWN_ERROR = 4;
	public static final int DOWN_SUCCESS = 5;
	
	private Context mContext;
	private String mLocalVersion;
	private String mServerURLPath = "http://127.0.0.1/update";
	private UpdataInfo info;
	private ResolveInfo mRinfo;
	private PackageManager mPackManager;
	private File mUpdateFile;
	
	public UpdateHelper(Context context, ResolveInfo info){
		mContext = context;
		mRinfo = info;
		mPackManager = mContext.getPackageManager();
	}
	
	public void updateStart(){
		
		try{
			PackageInfo packageinfo = mPackManager.getPackageInfo(mRinfo.activityInfo.packageName, 0);
			mLocalVersion = packageinfo.versionName;
		}catch(Exception ex){
            ex.printStackTrace() ;  
		}
		 
		CheckVersionTask cv = new CheckVersionTask();
		new Thread(cv).start();
	}
	
	public UpdataInfo getUpdataInfo(InputStream is) throws Exception{
		XmlPullParser parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();
		while(type != XmlPullParser.END_DOCUMENT ){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					info.setVersion(parser.nextText());	
				}else if ("url".equals(parser.getName())){
					info.setUrl(parser.nextText());	
				}else if ("description".equals(parser.getName())){
					info.setDescription(parser.nextText());	
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}
	
	class UpdataInfo {
		private String version;
		private String url;
		private String description;
		private String url_server;
		
		public String getUrl_server() {
			return url_server;
		}
		public void setUrl_server(String url_server) {
			this.url_server = url_server;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
	public class CheckVersionTask implements Runnable {
		InputStream is;
		
		public void run() {
			try {
				
				URL url = new URL(mServerURLPath);
				HttpURLConnection conn = (HttpURLConnection)url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET"); 
                int responseCode = conn.getResponseCode(); 
                if (responseCode == 200) { 
                    // 从服务器获得一个输入流 
                	is = conn.getInputStream(); 
                } 
                info = getUpdataInfo(is);
				if (info.getVersion().equals(mLocalVersion)) {
					Message msg = new Message();
					msg.what = UPDATA_NONEED;
					mhandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					mhandler.sendMessage(msg);
				}
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				mhandler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	
	Handler mhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				Toast.makeText(mContext, R.string.app_no_update,
						Toast.LENGTH_SHORT).show();
			case UPDATA_CLIENT:
				 //对话框通知用户升级程序   
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				//服务器超时   
	            Toast.makeText(mContext, R.string.connect_timeout, Toast.LENGTH_SHORT).show(); 
				break;
			case DOWN_ERROR:
				//下载apk失败  
	            Toast.makeText(mContext, R.string.app_update_fail, Toast.LENGTH_SHORT).show(); 
				break;
			case DOWN_SUCCESS:
				showInstallDialog();
				break;
			}
		}
	};
	
	private void showUpdataDialog(){ 
		// 发现新版本，提示用户更新  
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);  
		alert.setTitle(R.string.app_update)  
		    .setMessage(R.string.app_update_notice)  
		    .setPositiveButton(R.string.update,  
		            new DialogInterface.OnClickListener() {  
		                public void onClick(DialogInterface dialog,  
		                        int which) {  
		                    Toast.makeText(mContext, R.string.app_update_fail, Toast.LENGTH_SHORT)  
		                    .show();
		                    
		                    DownLoadTask dl = new DownLoadTask();
		            		new Thread(dl).start();
		                }  
		            })  
		    .setNegativeButton(R.string.cancal,  
		            new DialogInterface.OnClickListener() {  
		                public void onClick(DialogInterface dialog,  
		                        int which) {  
		                    dialog.dismiss();  
		                }  
		            });
		
		alert.create().show();   
	}
	
	public class DownLoadTask implements Runnable {
		InputStream is;
		
		public void run() {
			String fileName = mRinfo.activityInfo.packageName +".apk";
			mUpdateFile = downLoadFile(info.url, fileName);
		}
		
	    private File downLoadFile(String downLoadUrl, String fileName) {
	    	int totalSize = 0, downloadCount = 0; 
	    	
	        File tmpFile = new File(Environment.getExternalStorageDirectory().getPath());  
	        if (!tmpFile.exists()) {  
	        	tmpFile.mkdir();  
	        }
	     
	        final File file = new File(Environment.getExternalStorageDirectory().getPath() + fileName);  
	        try {  
	                URL url = new URL(downLoadUrl);  
	                try {  
	                        HttpURLConnection conn = (HttpURLConnection) url  
	                                        .openConnection();
	                        // 获取下载文件的size  
	                        totalSize = conn.getContentLength();  
	                        if (conn.getResponseCode() == 404) {  
	                            Toast.makeText(mContext, R.string.connect_timeout, Toast.LENGTH_SHORT)  
	                            .show();    
	                        } 
	                        
	                        InputStream is = conn.getInputStream();  
	                        FileOutputStream fos = new FileOutputStream(file);   
	                        conn.connect();  
	                        byte buffer[] = new byte[1024];  
	                        int readsize = 0;  
	                        while ((readsize = is.read(buffer)) != -1) {  
	                            fos.write(buffer, 0, readsize);  
	                            downloadCount += readsize;// 时时获取下载到的大小  
	                        }  
	                          
	                        conn.disconnect();  
	                        fos.close();  
	                        is.close();
	        				Message msg = new Message();
	        				msg.what = DOWN_SUCCESS;
	        				mhandler.sendMessage(msg);
	                } catch (IOException e) {
						Message msg = new Message();
						msg.what = DOWN_ERROR;
						mhandler.sendMessage(msg);
	                	e.printStackTrace();  
	                }  
	        } catch (MalformedURLException e) {
				Message msg = new Message();
				msg.what = DOWN_ERROR;
				mhandler.sendMessage(msg);
	        	e.printStackTrace();  
	        }  

	        return file;  
	    } 
	}
	
	private void showInstallDialog(){ 
		Util.installApp(mContext, mUpdateFile);
	}
	
}
