package com.um.huanauth.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.um.huanauth.provider.ContentDefine;
import com.um.huanauth.provider.DataBaseUtil;
import com.um.huanauth.provider.HuanUpgradeInfoBean;
import com.um.huanauth.util.Util;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.RecoverySystem;
import android.util.Log;

public class UpgradeBiz {
	private static final String TAG = "UpgradeBiz";
	private static final String URL = "http://api.upgrade.platform.huan.tv/service/upmp/";//"http://118.194.161.82:8080/service/upmp/";//
	private static final String CACHEDIR = "/cache";
	private static final String SDCARDDIR = "/sdcard/recovery";
	private static final String SDCARDMOUNTPATH = "/storage/emulated/0/recovery";
	private static final String UPDATEFILENAME = "/update.zip";
	private static final String PREFIX = "file://";
	public static final int SUCCESS = 0;
	public static final int DOWN_NOW = 1;
	public static final int UPDATE_NOW = 2;
	public static final int NO_SPACE = 3;
	public static final int DOWN_COMPELETE = 4;
	public static final int DOWN_FAILURE = 5;
	private static String mDownLoadId = "";
	private String mUrl = "";
	private Context mContext;
	private String mDnum = "", mDevicemode = "", mDidtoken = "";
	private String mAppid = "", mAppver = "", mRegion = "", mProjectId = "";
	private String mFileURL = "", mFileMD5 = "", mFileVersion = "";
	private String mFileSize = "";
	private String mVersionInfo = "";
	private UpgradeInfoAccessHelper mUpgradeInfoAccessHelper;
	private DownloadManager mDownloadManager;
	
	public UpgradeBiz(Context context, DownloadManager downloadManager) {
		mContext = context;
		mUpgradeInfoAccessHelper = new UpgradeInfoAccessHelper();
		initValues();
		mUrl = Util.getNetUpgradeUrl();
		Log.d(TAG,"UpgradeBiz mUrl:"+mUrl);
		if ((mUrl == null) || mUrl.equals("")){
			mUrl = URL;
		}
        mDownloadManager = downloadManager;
	}
	
	private void initValues(){
		DataBaseUtil dataBaseUtil = new DataBaseUtil(mContext);
		dataBaseUtil.open();
		Cursor cursor = dataBaseUtil.fetchAuthInfo(1);
		if ((cursor != null) && (cursor.getCount() > 0)){
			mDnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
			//mDevicemode = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
			mDevicemode = Util.getDevMode();
			mDidtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
		}
		dataBaseUtil.close();
		
		mAppid = Util.getAppId();
		mProjectId = Util.getProjectId();
		String softVersion = Util.getSoftVersion();
		String tmp1 = softVersion.substring(0, 15);
		String tmp2 = softVersion.substring(17, 20);
		mAppver = tmp1 + tmp2;
		mRegion = "cn";
		
		Log.d(TAG, "initValues[mDnum:"+mDnum+";mDevicemode:"+mDevicemode+";mDidtoken:"+mDidtoken
				+";mAppid:"+mAppid+";mAppver:"+mAppver+"]");
	}
	
	public void doOperate(String operateType, String result){
		String url = mUrl + "operateInterface";
		String requestXml = "";
		
		requestXml = createOperateRequestXml(operateType, result);
		BasicHeader basicHeader1 = new BasicHeader("Content-Type", "application/xml");
		Header[] header = {basicHeader1};
		Log.d(TAG,"doOperate,requestXml="+requestXml);
		String resultXml = HttpUtils.getContentWithPostMethor(url, header, requestXml);
		Log.d(TAG,"doOperate,resultXml="+resultXml);
		if (resultXml != null){
			resolveOperateResultXml(resultXml);
		}
	}
	
	/*���*/
	public int doDetect(){
		String url = mUrl + "upgradeIncrInterface";
		String requestXml = "";
		int ret = -1;
		
		Log.d(TAG,"doDetect");
		
		mFileURL = "";
		mFileMD5 = "";
		mFileSize = "";
		mFileVersion = "";
		
		requestXml = createUpgradeRequestXml();
		BasicHeader basicHeader1 = new BasicHeader("Content-Type", "application/xml");
		Header[] header = {basicHeader1};
		Log.d(TAG,"doUpgrade,requestXml="+requestXml);
		String resultXml = HttpUtils.getContentWithPostMethor(url, header, requestXml);
		Log.d(TAG,"doUpgrade,resultXml="+resultXml);
		if (resultXml != null){
			ret = resolveUpgradeResultXml(resultXml);
		}
		
		return ret;
	 }
	
	 public HuanUpgradeInfoBean addUpgradeDetectInfoBean(){
		 Log.d(TAG, "addUpgradeDetectInfoBean mFileURL:"+mFileURL+";mFileMD5:"+mFileMD5+";mFileSize:"+mFileSize+";mFileVersion:"+mFileVersion);
		 HuanUpgradeInfoBean bean = new HuanUpgradeInfoBean();
		 bean.setFileURL(mFileURL);
		 bean.setFileMD5(mFileMD5);
		 bean.setFileSize(mFileSize);
		 bean.setFileVersion(mFileVersion);
		 
		 return bean;
	 }
	 
	 public void deleteUpgradeDetectInfoBean(HuanUpgradeInfoBean bean){
		 bean = null;
	 }
	 
	 //checkDownLoadCondition��downLoad��ִ���Ǵ��еĲ��Ҳ��ᱻ����
	 public int checkDownLoadCondition(HuanUpgradeInfoBean bean){
		 int ret = 0;
		 String fileVersion = "";
		 String stage = "";
		 
		 fileVersion = mUpgradeInfoAccessHelper.getFileVersion();
		 Log.d(TAG, "checkDownLoadCondition stage:"+mUpgradeInfoAccessHelper.getStage());
		 Log.d(TAG, "checkDownLoadCondition bean fileVersion:"+bean.getFileVersion());
		 Log.d(TAG, "checkDownLoadCondition fileVersion:"+fileVersion);
		 
		 if (fileVersion.equals(bean.getFileVersion())){
			 stage = mUpgradeInfoAccessHelper.getStage();
			 
			 Log.d(TAG,"checkDownLoadCondition stage:"+stage);
			 if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD)){
				 long downLoadId = 0;
				 
				 String strDownLoadId = mUpgradeInfoAccessHelper.getDownLoadId();				 
				 if ((strDownLoadId != null) && (!strDownLoadId.equals(""))){
					 downLoadId = Long.valueOf(strDownLoadId);
				 }
				 
				 DownloadManagerPro downloadManagerPro = new DownloadManagerPro(mDownloadManager);
				 int statusId = downloadManagerPro.getStatusById(downLoadId);
				 Log.d(TAG,"checkDownLoadCondition statusId:"+statusId);
				 
				 if ((statusId == DownloadManager.STATUS_RUNNING)
						 || (statusId == DownloadManager.STATUS_PAUSED)
						 || (statusId == DownloadManager.STATUS_PENDING)){
					 ret = DOWN_NOW;
				 }else if (statusId == DownloadManager.STATUS_SUCCESSFUL){
					 boolean returnret = checkDownLoadCompelete();
					 if (returnret){
						 ret = DOWN_COMPELETE;
					 }
				 }
			 }else if (stage.equals(ContentDefine.UpgradeTable.STAGE_UPDATE)){
				 ret = UPDATE_NOW;
			 }
		 }
		 
		 return ret;
	 }
	 
	 public int checkDownLoadState(){
		 int ret = 0;
		 String stage = "";
		 
		 stage = mUpgradeInfoAccessHelper.getStage();
		 if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD)){
			 long downLoadId = 0;
			 
			 String strDownLoadId = mUpgradeInfoAccessHelper.getDownLoadId();				 
			 if ((strDownLoadId != null) && (!strDownLoadId.equals(""))){
				 downLoadId = Long.valueOf(strDownLoadId);
			 }
			 
			 DownloadManagerPro downloadManagerPro = new DownloadManagerPro(mDownloadManager);
			 int statusId = downloadManagerPro.getStatusById(downLoadId);
			 Log.d(TAG,"checkDownLoadState statusId:"+statusId);
			 
			 if ((statusId == DownloadManager.STATUS_RUNNING)
					 || (statusId == DownloadManager.STATUS_PAUSED)
					 || (statusId == DownloadManager.STATUS_PENDING)){
				 ret = DOWN_NOW;
			 }else if (statusId == DownloadManager.STATUS_SUCCESSFUL){
				 boolean returnret = checkDownLoadCompelete();
				 if (returnret){
					 ret = DOWN_COMPELETE;
				 }
			 }else if (statusId == DownloadManager.STATUS_FAILED){
				 ret = DOWN_FAILURE;
			 }
		 }else if (stage.equals(ContentDefine.UpgradeTable.STAGE_UPDATE)){
			 ret = UPDATE_NOW;
		 }
		 
		 return ret;
	 }
	 
	 private boolean checkDownLoadCompelete(){
		 boolean ret = false;		 
		 String destString = "";
		 String md5 = null;
		 String strFileSize = "";
		 long fileSize = 0;
		 
		 destString = mUpgradeInfoAccessHelper.getFileStorageURL();
		 Log.d(TAG, "checkDownLoadCompelete destString:"+destString);
		 
		 if ((destString == null) || destString.equals("")){
			 return ret;
		 }
		 
		 File file = new File(destString);
		 
		 if ((file != null) && file.exists()){
			 strFileSize = mUpgradeInfoAccessHelper.getFileSize();
			 if ((strFileSize != null) && (!strFileSize.equals(""))){
				 fileSize = Long.parseLong(strFileSize);
			 }
			 Log.d(TAG,"checkDownLoadCompelete strFileSize:"+strFileSize+";file.length():"+file.length());
			 
			 if (fileSize == file.length()){
				 md5 = Util.getFileMd5(file);
				 Log.d(TAG, "checkDownLoadCompelete md5:"+md5+";getFileMD5():"+mUpgradeInfoAccessHelper.getFileMD5());
				 
				 /*��Сһ�²���md5һ�����������*/
				 if ((md5 != null) && md5.equals(mUpgradeInfoAccessHelper.getFileMD5())){
					 ret = true;
				 }
			 }
		 }
		 
		 return ret;
	 }
	 
	 private void queryAndRemoveDownLoad(){
		 DownloadManager.Query downloadsQuery = new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING);
		 Cursor downloadsCursor = mDownloadManager.query(downloadsQuery);
		 downloadsCursor.moveToFirst();
		 
		 int descriptionIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
		 int mimeTypeIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE);
		 int downIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_ID);
		 
		 if (downloadsCursor != null){
			 do{
				  String description = downloadsCursor.getString(descriptionIdx);  
				  String mimeType = downloadsCursor.getString(mimeTypeIdx);
				  int downId = downloadsCursor.getInt(downIdx);
				  
				  if (description.equals("unionman net download")
						  && mimeType.equals("application/unionman.update.download.file")){
					  mDownloadManager.remove(downId);
				  }
			 }while(downloadsCursor.moveToNext());
			 
			 downloadsCursor.close();
		 }
	 }
	 
	 private long getDownLoadIdByUUID(String uuid){
		 long downLoadId = -1;
		 
		 DownloadManager.Query downloadsQuery = new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING);
		 Cursor downloadsCursor = mDownloadManager.query(downloadsQuery);
		 downloadsCursor.moveToFirst();
		 
		 int descriptionIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
		 int mimeTypeIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE);
		 int uuidIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
		 int downIdx = downloadsCursor.getColumnIndex(DownloadManager.COLUMN_ID);
		 
		 if (downloadsCursor != null){
			 do{
				  String description = downloadsCursor.getString(descriptionIdx);  
				  String mimeType = downloadsCursor.getString(mimeTypeIdx);
				  String downUUID = downloadsCursor.getString(uuidIdx);
				  int downId = downloadsCursor.getInt(downIdx);
				  
				  if (description.equals("unionman net download")
						  && mimeType.equals("application/unionman.update.download.file")
						  && downUUID.equals(uuid)){//ͨ��uuid�����
					  downLoadId = downId;
					  break;
				  }
			 }while(downloadsCursor.moveToNext());
			 
			 downloadsCursor.close();
		 }
		 
		 return downLoadId;
	 }
	 
	 private long downLoad(String downLoadUrl, String destUri, String uuid) {
		long downloadId = -1;
		 
		try{
			Log.d(TAG,"downLoad downLoadUrl:"+downLoadUrl+";destUri:"+destUri);
			//String tmp = "http://www.umstb.com.cn/upgrade/umtv/update.zip";
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downLoadUrl));
			request.setDestinationUri(Uri.parse(destUri));
			request.setTitle(uuid);
			request.setDescription("unionman net download");
			request.setVisibleInDownloadsUi(false);
			request.setMimeType("application/unionman.update.download.file");
			downloadId = mDownloadManager.enqueue(request);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return downloadId;
	 }
	 
	 private void releaseZipSpace(){
		 String cacheDestString = "";
		 String sdcardDestString = "";
		 
		 cacheDestString = CACHEDIR+UPDATEFILENAME;
		 sdcardDestString = SDCARDMOUNTPATH+UPDATEFILENAME;
		 
		 File file = new File(cacheDestString);
		 if (file.exists()){
			 file.delete();
			 Log.d(TAG, "releaseZipSpace:"+cacheDestString);
		 }
		 
		 file = new File(sdcardDestString);
		 if (file.exists()){
			 file.delete();
			 Log.d(TAG, "releaseZipSpace:"+sdcardDestString);
		 }
	 }
	 
	 private void resetNormalState(){
		 mUpgradeInfoAccessHelper.setStage(ContentDefine.UpgradeTable.STAGE_NORMAL);
		 mUpgradeInfoAccessHelper.setDownLoadId("");
		 mUpgradeInfoAccessHelper.setFileURL("");
		 mUpgradeInfoAccessHelper.setFileSize("");
		 mUpgradeInfoAccessHelper.setFileMD5("");
		 mUpgradeInfoAccessHelper.setFileVersion("");
		 mUpgradeInfoAccessHelper.setFileStorageURL("");
		 mUpgradeInfoAccessHelper.setFileUUID("");
		 mUpgradeInfoAccessHelper.updateUpgradeinfoBean();
		 Log.d(TAG,"resetNormalState");
	 }
	 
	 private String getSpacePath(String strFileSize){
		 long fileSize = 0;
		 String destString = "";
		 
		 if ((strFileSize != null) && (!strFileSize.equals(""))){
			 fileSize = Long.valueOf(strFileSize);
		 }
		 
		 fileSize = fileSize + 1024; //Ԥ��1k�Ļ�д�ռ�
		 
		 Log.d(TAG, "getSpacePath fileSize:"+fileSize+";Util.getCacheAvailableSize():"+Util.getCacheAvailableSize());
		 if (fileSize < Util.getCacheAvailableSize()){
			 destString = CACHEDIR+UPDATEFILENAME;
		 }else{
			 if (fileSize < Util.getCacheTotalSize()){
				 destString = CACHEDIR+UPDATEFILENAME;
				 Util.cleanCache();
			 }else{
				 if (fileSize < Util.getSdCardAvailableSize()){
					 File file = new File(SDCARDMOUNTPATH);
					 if (!file.exists()){
						 file.mkdir();
					 }
					 destString = SDCARDMOUNTPATH+UPDATEFILENAME;
				 }else{
					  return "";
				 }
			 }
		 }
		 
		 return destString;
	 }
	 
	 public void downLoadFailDo(){
		 doOperate("download", "FAILURE");
		 Log.d(TAG, "downLoadFailDo");
		 resetNormalState();
	 }
	 
	 public void downLoadSuccessDo(){
		 doOperate("download","SUCCESS");
	 }
	 
	 private String saveDownLoadInfo(HuanUpgradeInfoBean bean, long downLoadId, String destString){
		 String uuid = "";
		 
		 uuid = Util.getUUID();
		 mUpgradeInfoAccessHelper.setStage(ContentDefine.UpgradeTable.STAGE_DOWNLOAD);
		 mUpgradeInfoAccessHelper.setDownLoadId(String.valueOf(downLoadId));
		 mUpgradeInfoAccessHelper.setFileURL(bean.getFileURL());
		 mUpgradeInfoAccessHelper.setFileSize(bean.getFileSize());
		 mUpgradeInfoAccessHelper.setFileMD5(bean.getFileMD5());
		 mUpgradeInfoAccessHelper.setFileVersion(bean.getFileVersion());
		 mUpgradeInfoAccessHelper.setFileStorageURL(destString);
		 mUpgradeInfoAccessHelper.setFileUUID(uuid);
		 mUpgradeInfoAccessHelper.updateUpgradeinfoBean();
		 
		 bean = null;
		 
		 return uuid;
	 }
	 
	/*����*/
	public int doDownLoad(HuanUpgradeInfoBean bean){
		String stage = "";
		String fileVersion = "";
		String destString = "";
		long downLoadId = -1;  
		
		Log.d(TAG,"doDownLoad");
		
		if (bean == null){
			Log.d(TAG,"doDownLoad bean is null");
			return -1;
		}
		
		stage = mUpgradeInfoAccessHelper.getStage();
		Log.d(TAG,"doDownLoad stage:"+stage);
		if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD)){ 
			fileVersion = mUpgradeInfoAccessHelper.getFileVersion();
			if (fileVersion.equals(bean.getFileVersion()) && (checkDownLoadState() == 0)){
				//downLoadId = downLoad(mFileURL, destString);//for test
				Log.d(TAG,"doDownLoad mFileVersion is same downLoadId:"+mUpgradeInfoAccessHelper.getDownLoadId()+";mFileVersion:"+mFileVersion);
			}else{
				//delete old download
				long oldDownLoadId = 0;
				String strOldDownLoadId = mUpgradeInfoAccessHelper.getDownLoadId();
				if ((strOldDownLoadId != null) && (!strOldDownLoadId.equals(""))){
					oldDownLoadId = Long.valueOf(strOldDownLoadId);
				}
				
				try{
					DownloadManagerPro downloadManagerPro = new DownloadManagerPro(mDownloadManager);
					int statusId = downloadManagerPro.getStatusById(oldDownLoadId);
					Log.d(TAG,"oldDownLoadId:"+oldDownLoadId+";statusId:"+statusId);
					if ((statusId != -1) && (statusId != DownloadManager.STATUS_SUCCESSFUL)){
						mDownloadManager.remove(oldDownLoadId);
						Log.d(TAG,"remove oldDownLoadId");
					}
				}catch (Exception e){
					e.printStackTrace();
					Log.d(TAG,"remove oldDownLoadId failure");
					return -1;
				}
				resetNormalState();
				
				releaseZipSpace();
				destString = getSpacePath(bean.getFileSize());
				if (destString.equals("")){
					 return NO_SPACE;
				}
				
				try {
					Thread.sleep(200);       //�����ӳٻᵼ���¿��������������ļ���ʧ
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				String uuid = saveDownLoadInfo(bean, -1, destString);     //������ԣ���ǰ��¼���ȷ���������سɹ�����Իָ���
				//�ϵ��
				
				//start new download
				downLoadId = downLoad(mFileURL, PREFIX+destString, uuid);
				if (downLoadId == -1){
					resetNormalState();
					return -1;
				}
				
				//�ϵ��
				mUpgradeInfoAccessHelper.saveDownLoadId(String.valueOf(downLoadId));
				Log.d(TAG,"doDownLoad delete old download,then start new download downLoadId:"+downLoadId);
			}
		}else{
			resetNormalState();
			
			releaseZipSpace();
			
			destString = getSpacePath(bean.getFileSize());
			if (destString.equals("")){
				 resetNormalState();
				 return NO_SPACE;
			}
			
			String uuid = saveDownLoadInfo(bean, -1, destString);  //������ԣ���ǰ��¼���ȷ���������سɹ�����Իָ���
			//�ϵ��
			
			downLoadId = downLoad(mFileURL, PREFIX+destString, uuid);
			if (downLoadId == -1){
				resetNormalState();
				return -1;
			}
			
			//�ϵ��
			mUpgradeInfoAccessHelper.saveDownLoadId(String.valueOf(downLoadId));
			
			Log.d(TAG,"doDownLoad start download first downLoadId:"+downLoadId);
		}
		
		return 0;
	}
	
	/*У��*/
	public boolean doVerify(){
		boolean ret = false;
		String md5 = null;
		String strdest = "";
		
		strdest = mUpgradeInfoAccessHelper.getFileStorageURL();
		Log.d(TAG,"doVerify strdest:"+strdest);
		
		if ((strdest == null) || (strdest.equals(""))){
			resetNormalState();
			return false;
		}
		
		File file = new File(strdest);
		if (!file.exists()){
			Log.d(TAG,"doVerify failure packagefile no exists");
			
			resetNormalState();
			return false;
		}
		
		md5 = Util.getFileMd5(file);
		if ((md5 != null) && !md5.equals(mUpgradeInfoAccessHelper.getFileMD5())){
			Log.d(TAG,"doVerify failure md5:"+md5+";mUpgradeInfoAccessHelper.getFileMD5()"+mUpgradeInfoAccessHelper.getFileMD5());
			
			resetNormalState();
			return false;
		}
		
		Log.v(TAG, "doVerify:name:" + file.getAbsolutePath());
		
		SocketClient socketClient = null;
		socketClient = new SocketClient();
		
		 socketClient.writeMsg("CaAndroidUpdateFile " + file.getAbsolutePath()
		 + " " + file.getAbsolutePath());
		 int result = socketClient.readNetResponseSync();
		 if (result == 0){
			 ret = true;
		 }else{
			 Log.d(TAG,"doVerify failure CaAndroidUpdateFile");
			 resetNormalState();
		 }
		 
		 return ret;
	}
	
	/*����*/
	public int doUpdate(boolean updateNow){
		int ret = 0;
		String destString = "";
		int pos = 0;
		
		Log.d(TAG,"doUpdate updateNow:"+updateNow);
		
		destString = mUpgradeInfoAccessHelper.getFileStorageURL();
		Log.d(TAG,"doUpdate strdest:"+destString);
		
		if ((destString == null) || (destString.equals(""))){
			resetNormalState();
			return -1;
		}
		
		String path = null;
		File packageFile = new File(destString);
		pos = destString.indexOf(UPDATEFILENAME);
		path = destString.substring(0, pos);
		
		Log.d(TAG,"doUpdate filename:"+packageFile.getAbsolutePath());
		Log.d(TAG,"doUpdate path:"+path);
		
		SocketClient socketClient = null;
        socketClient = new SocketClient();
        socketClient.writeMsg("upgrade " + path);
        socketClient.readNetResponseSync();
        
        try{
        	if (updateNow){
        		mUpgradeInfoAccessHelper.saveStage(ContentDefine.UpgradeTable.STAGE_UPDATE);
                RecoverySystem.installPackage(mContext, new File(destString));
                
                Log.d(TAG, "doUpdate after saveStage");
        	}else{
        		mUpgradeInfoAccessHelper.saveStage(ContentDefine.UpgradeTable.STAGE_UPDATE);
        		RecoverySystem.installPackage(mContext, new File(destString), false);//need to do
        	}

        }catch (Exception e){
        	e.printStackTrace();
        }
        
        return ret;
	}
	
	public boolean toWaitSdcardMount(){
		boolean ret = false;
		String stage = "";
		
		Log.d(TAG,"toWaitSdcardMount");
		stage = mUpgradeInfoAccessHelper.getStage();
		
		if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD)){
			String destString = "";
			destString = mUpgradeInfoAccessHelper.getFileStorageURL();
			if ((destString != null) && (!destString.equals(""))){
				if (destString.equals(SDCARDMOUNTPATH+UPDATEFILENAME)){
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	/*�������Ƿ���Ҫ�ϴ�������*/
	public void checkForUploadUpgradeInfo(){
		String stage = "";
		String version = "";
		
		stage = mUpgradeInfoAccessHelper.getStage();
		Log.d(TAG,"checkForUploadUpgradeInfo stage:"+stage);
		Log.d(TAG,"mUpgradeInfoAccessHelper.getDownLoadId():"+mUpgradeInfoAccessHelper.getDownLoadId());
		
		//���ؽ׶μ�¼����IDʱ�ϵ�ָ�
		if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD) && mUpgradeInfoAccessHelper.getDownLoadId().equals("-1")){
			if (!mUpgradeInfoAccessHelper.getFileUUID().equals("")){
				long downLoadId = getDownLoadIdByUUID(mUpgradeInfoAccessHelper.getFileUUID());
				Log.d(TAG,"checkForUploadUpgradeInfo getDownLoadIdByUUID downLoadId:"+downLoadId);
				if (downLoadId != -1){
					mUpgradeInfoAccessHelper.saveDownLoadId(String.valueOf(downLoadId));
				}
			}
		}
		
		if (stage.equals(ContentDefine.UpgradeTable.STAGE_UPDATE)){ //�������Ƿ�����״̬������ǲ��Ұ汾��һ������Ϊ����ʧ�ܣ����汾�Ų�һ������Ϊ���³ɹ�
			version = mUpgradeInfoAccessHelper.getVersion();
			
			if (version != null){
				if (version.equals(mAppver)){
					doOperate("upgrade","FAILURE");
				}else{
					doOperate("upgrade","SUCCESS");
					mUpgradeInfoAccessHelper.saveVersion(mAppver);
				}
			}
			
			resetNormalState();
		}else if (stage.equals(ContentDefine.UpgradeTable.STAGE_DOWNLOAD)){ //�������Ƿ�������״̬,���������״̬�������ؽ������Ǳ��ϵ��쳣������Ҫ�ָ�Ϊ��״̬
			 String destString = "";
			 String md5 = null;
			 String strFileSize = "";
			 long fileSize = 0;
			 
			 destString = mUpgradeInfoAccessHelper.getFileStorageURL();
			 Log.d(TAG, "checkForUploadUpgradeInfo destString:"+destString);
			 
			 if ((destString == null) || destString.equals("")){
				 resetNormalState();
				 Log.d(TAG, "checkForUploadUpgradeInfo destString is null");
				 return;
			 }
			 
			 File file = new File(destString);
			 
			 if ((file != null) && file.exists()){
				 
				 strFileSize = mUpgradeInfoAccessHelper.getFileSize();
				 if ((strFileSize != null) && (!strFileSize.equals(""))){
					 fileSize = Long.parseLong(strFileSize);
				 }
				 Log.d(TAG,"strFileSize:"+strFileSize+";file.length():"+file.length());
				 
				 if (fileSize == file.length()){
					 md5 = Util.getFileMd5(file);
					 Log.d(TAG, "checkForUploadUpgradeInfo md5:"+md5+";getFileMD5():"+mUpgradeInfoAccessHelper.getFileMD5());
					 
					 if ((md5 != null) && md5.equals(mUpgradeInfoAccessHelper.getFileMD5())){
						 ;
					 }else{/*��Сһ�²���md5��һ��������쳣*/
						 Log.d(TAG, "checkForUploadUpgradeInfo md5 no the same");
						 resetNormalState();
					 }
				 }else{
					 /*�ļ���С�ȼ�¼��С������Ϊ���쳣�����ȼ�¼��С����δ���*/
					 if (file.length() > fileSize){
						 Log.d(TAG, "checkForUploadUpgradeInfo file.length() > fileSize");
						 resetNormalState();
					 }
				 }
			 }else{
				 Log.d(TAG, "checkForUploadUpgradeInfo downfile is no exist");
				 String downLoadid = mUpgradeInfoAccessHelper.getDownLoadId();
				 if ((downLoadid == null) || (downLoadid.equals(""))){
					 resetNormalState();
				 }
			 }
		}else if (stage.equals(ContentDefine.UpgradeTable.STAGE_NORMAL)){
			resetNormalState();
		}else{
			resetNormalState();
		}
	}
	
	public long getCurDownLoadId(){
		String strDownLoadId = "";
		long downLoadId = 0xffffffff;
		
		strDownLoadId = mUpgradeInfoAccessHelper.getDownLoadId();
		if (strDownLoadId != null && !strDownLoadId.equals("")){
			downLoadId = Long.valueOf(strDownLoadId);
		}
		
		return downLoadId;
	}
	
	public String getVersionInfo(){
		return mVersionInfo;
	}
	
	private String createOperateRequestXml(String operateType, String operateResult){
		String xmlWriter = null;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder = factory.newDocumentBuilder();  
	        Document doc = builder.newDocument();
	        
	        Element eleRoot = doc.createElement("operateRequest");  
	        doc.appendChild(eleRoot);
	        
	        Element eleCallid = doc.createElement("callid");  
            Node nodeCallid = doc.createTextNode(mUpgradeInfoAccessHelper.getCallId());
            eleCallid.appendChild(nodeCallid);
            eleRoot.appendChild(eleCallid);
            
            Element eleClient = doc.createElement("client");
            
            Element eleDnum = doc.createElement("dnum");
            Node nodeDnum = doc.createTextNode(mUpgradeInfoAccessHelper.getDnum());
            eleDnum.appendChild(nodeDnum);
            
            Element eleDidtoken = doc.createElement("didtoken");
            Node nodeDidtoken = doc.createTextNode(mUpgradeInfoAccessHelper.getDidtoken());
            eleDidtoken.appendChild(nodeDidtoken);
            
            Element eleDevmodel = doc.createElement("devmodel");
            Node nodeDevmodel = doc.createTextNode(mUpgradeInfoAccessHelper.getDevicemode());
            eleDevmodel.appendChild(nodeDevmodel);
            
            eleClient.appendChild(eleDnum);
            eleClient.appendChild(eleDidtoken);
            eleClient.appendChild(eleDevmodel);
            eleRoot.appendChild(eleClient);
            
            Element eleOperate = doc.createElement("operate");
            
            Element eleAppid = doc.createElement("appid");
            Node nodeAppid = doc.createTextNode(mAppid);
            eleAppid.appendChild(nodeAppid);
            
            Element eleOperatetype = doc.createElement("operatetype");
            Node nodeOperatetype = doc.createTextNode(operateType);
            eleOperatetype.appendChild(nodeOperatetype);
            
            Element eleVer = doc.createElement("ver");
            Node nodeVer = doc.createTextNode(mAppver);
            eleVer.appendChild(nodeVer);
            
            Element eleResult = doc.createElement("result");
            Node nodeResult = doc.createTextNode(operateResult);
            eleResult.appendChild(nodeResult);
            
            eleOperate.appendChild(eleAppid);
            eleOperate.appendChild(eleOperatetype);
            eleOperate.appendChild(eleVer);
            eleOperate.appendChild(eleResult);
            eleRoot.appendChild(eleOperate);
            
            Element eleApiversion = doc.createElement("apiversion");
            Node nodeApiversion = doc.createTextNode("1.0");
            eleApiversion.appendChild(nodeApiversion);
            
            eleRoot.appendChild(eleApiversion);
            
            Properties properties = new Properties();  
            properties.setProperty(OutputKeys.MEDIA_TYPE, "application/xml");  
            properties.setProperty(OutputKeys.VERSION, "1.0");  
            properties.setProperty(OutputKeys.ENCODING, "utf-8");    
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();  
            Transformer transformer = transformerFactory.newTransformer();  
            transformer.setOutputProperties(properties);
            
            DOMSource domSource = new DOMSource(doc.getDocumentElement());  
            OutputStream output = new ByteArrayOutputStream();  
            StreamResult result = new StreamResult(output);  
            transformer.transform(domSource, result);  
              
            xmlWriter = output.toString();  
	        
		}catch (ParserConfigurationException e) {      //factory.newDocumentBuilder  
	        e.printStackTrace();  
	    } catch (DOMException e) {                      //doc.createElement  
	        e.printStackTrace();  
	    } catch (TransformerFactoryConfigurationError e) {      //TransformerFactory.newInstance  
	        e.printStackTrace();  
	    } catch (TransformerConfigurationException e) {     //transformerFactory.newTransformer  
	        e.printStackTrace();  
	    } catch (TransformerException e) {              //transformer.transform  
	        e.printStackTrace();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
		
		return xmlWriter;
	}
	
	private int resolveOperateResultXml(String result){
		String callId = "";
		String strState = "";
		
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes());
			Document doc = builder.parse(is);
			
			doc.getDocumentElement().normalize();  
	        NodeList nlRoot = doc.getElementsByTagName("operateResponse");
	        if ((nlRoot != null) && (nlRoot.item(0) != null)){
	        	Element eleRoot = (Element)nlRoot.item(0);
		        
		        NodeList nlCallid = eleRoot.getElementsByTagName("callid");
		        if ((nlCallid != null) && (nlCallid.item(0) != null)){
			        Element eleCallid = (Element)nlCallid.item(0);
			        callId = eleCallid.getTextContent();
		        }

		        NodeList nlState = eleRoot.getElementsByTagName("state");
		        if ((nlState != null) && (nlState.item(0) != null)){
			        Element eleState = (Element)nlState.item(0);
			        strState = eleState.getTextContent();
		        }
		        
		        NodeList nlApiversion = eleRoot.getElementsByTagName("apiversion");
		        if ((nlApiversion != null) && (nlApiversion.item(0) != null)){
			        Element eleApiversion = (Element)nlApiversion.item(0);
			        String strApiversion = eleApiversion.getTextContent();
		        }
	        }
	        
	        if (!callId.equals("")){
	        	mUpgradeInfoAccessHelper.setCallId(callId);
	        }
	        
	        if (strState.equals("0000")){
	        	return 0;
	        }
	        
	        Log.d(TAG,"strCallid:"+callId+";strState:"+strState);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
        
        return -1;
	}
	
	private String createUpgradeRequestXml(){
		String xmlWriter = null;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder = factory.newDocumentBuilder();  
	        Document doc = builder.newDocument();
	        
	        Element eleRoot = doc.createElement("upgradeIncrRequest");  
	        doc.appendChild(eleRoot);
	        
	        Element eleUpmptype = doc.createElement("upmptype");  
            Node nodeUpmptype = doc.createTextNode("3");
            eleUpmptype.appendChild(nodeUpmptype);
            eleRoot.appendChild(eleUpmptype);
            
            Element eleParameter = doc.createElement("parameter");
            
            Element eleCallid = doc.createElement("callid");
            Node nodeCallid = doc.createTextNode(mUpgradeInfoAccessHelper.getCallId());
            eleCallid.appendChild(nodeCallid);
            
            Element eleLanguage = doc.createElement("language");
            Node nodeLanguage = doc.createTextNode("zh_CN");
            eleLanguage.appendChild(nodeLanguage);
            
            Element eleTimezone = doc.createElement("timezone");
            Node nodeTimezone = doc.createTextNode("+0800");
            eleTimezone.appendChild(nodeTimezone);
            
            Element eleRegion = doc.createElement("region");
            Node nodeRegion = doc.createTextNode(mRegion);
            eleRegion.appendChild(nodeRegion);
            
            Element eleClient = doc.createElement("client");
            
            Element eleDnum = doc.createElement("dnum");
            Node nodeDnum = doc.createTextNode(mUpgradeInfoAccessHelper.getDnum());
            eleDnum.appendChild(nodeDnum);
            
            Element eleDidtoken = doc.createElement("didtoken");
            Node nodeDidtoken = doc.createTextNode(mUpgradeInfoAccessHelper.getDidtoken());
            eleDidtoken.appendChild(nodeDidtoken);
            
            Element eleDevmodel = doc.createElement("devmodel");
            Node nodeDevmodel = doc.createTextNode(mUpgradeInfoAccessHelper.getDevicemode());
            eleDevmodel.appendChild(nodeDevmodel);
            
            Element eleProjectid = doc.createElement("projectid");
            Node nodeProjectid = doc.createTextNode(mProjectId);
            eleProjectid.appendChild(nodeProjectid);
            
            Element eleRequesttype = doc.createElement("requesttype");
            Node nodeRequesttype = doc.createTextNode("0");
            eleRequesttype.appendChild(nodeRequesttype);
            
            Element eleSystemver = doc.createElement("systemver");
            Node nodeSystemver = doc.createTextNode("1");
            eleSystemver.appendChild(nodeSystemver);
            
            eleClient.appendChild(eleDnum);
            eleClient.appendChild(eleDidtoken);
            eleClient.appendChild(eleDevmodel);
            eleClient.appendChild(eleProjectid);
            eleClient.appendChild(eleRequesttype);
            eleClient.appendChild(eleSystemver);
            
            eleParameter.appendChild(eleCallid);
            eleParameter.appendChild(eleLanguage);
            eleParameter.appendChild(eleTimezone);
            eleParameter.appendChild(eleRegion);
            eleParameter.appendChild(eleClient);
            eleRoot.appendChild(eleParameter);
            
            Element eleApp = doc.createElement("app");

            Element eleAppid = doc.createElement("appid");
            Node nodeAppid = doc.createTextNode(mAppid);
            eleAppid.appendChild(nodeAppid);
            
            Element eleVer = doc.createElement("ver");
            Node nodeVer = doc.createTextNode(mAppver);
            eleVer.appendChild(nodeVer);
            
            Element eleVerid = doc.createElement("verid");
            Node nodeVerid = doc.createTextNode("1.0");
            eleVerid.appendChild(nodeVerid);
            
            eleApp.appendChild(eleAppid);
            eleApp.appendChild(eleVer);
            eleApp.appendChild(eleVerid);
            eleRoot.appendChild(eleApp);
            
            Element eleApiversion = doc.createElement("apiversion");
            Node nodeApiversion = doc.createTextNode("1.0");
            eleApiversion.appendChild(nodeApiversion);
            
            eleRoot.appendChild(eleApiversion);
            
            Properties properties = new Properties();  
            properties.setProperty(OutputKeys.MEDIA_TYPE, "application/xml");  
            properties.setProperty(OutputKeys.VERSION, "1.0");  
            properties.setProperty(OutputKeys.ENCODING, "utf-8");    
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();  
            Transformer transformer = transformerFactory.newTransformer();  
            transformer.setOutputProperties(properties);
            
            DOMSource domSource = new DOMSource(doc.getDocumentElement());  
            OutputStream output = new ByteArrayOutputStream();  
            StreamResult result = new StreamResult(output);  
            transformer.transform(domSource, result);  
              
            xmlWriter = output.toString();  
	        
		}catch (ParserConfigurationException e) {      //factory.newDocumentBuilder  
	        e.printStackTrace();  
	    } catch (DOMException e) {                      //doc.createElement  
	        e.printStackTrace();  
	    } catch (TransformerFactoryConfigurationError e) {      //TransformerFactory.newInstance  
	        e.printStackTrace();  
	    } catch (TransformerConfigurationException e) {     //transformerFactory.newTransformer  
	        e.printStackTrace();  
	    } catch (TransformerException e) {              //transformer.transform  
	        e.printStackTrace();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }
		
		return xmlWriter;
	}
	
	private int resolveUpgradeResultXml(String result){
		String callId = "";
		String strState = "";
		Element eleUpgrade = null;
		
		try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes());
			Document doc = builder.parse(is);
			
			doc.getDocumentElement().normalize();  
	        NodeList nlRoot = doc.getElementsByTagName("upgradeIncrResponse");
	        
	        Element eleRoot = (Element)nlRoot.item(0);
	        if (eleRoot.hasChildNodes()){
	        	NodeList nlServertime = eleRoot.getElementsByTagName("servertime");
	        	if ((nlServertime != null) && (nlServertime.item(0) != null)){
	        		Element eleServertime = (Element)nlServertime.item(0);
	 		        String strServertime = eleServertime.getTextContent();
	        	}
		       
		        NodeList nlState = eleRoot.getElementsByTagName("state"); 
		        if ((nlState != null) && (nlState.item(0) != null)){
			        Element eleState = (Element)nlState.item(0);
			        strState = eleState.getTextContent();
		        }

		        NodeList nlCallid = eleRoot.getElementsByTagName("callid");
		        if ((nlCallid != null) && (nlCallid.item(0) != null)){
			        Element eleCallid = (Element)nlCallid.item(0);
			        callId = eleCallid.getTextContent();
		        }

		        NodeList nlLanguage = eleRoot.getElementsByTagName("language"); 
		        if ((nlLanguage != null) && (nlLanguage.item(0) != null)){
			        Element eleLanguage = (Element)nlLanguage.item(0);
			        String strLanguage = eleLanguage.getTextContent();
		        }
		        
		        NodeList nlTimezone = eleRoot.getElementsByTagName("timezone"); 
		        if ((nlTimezone != null) && (nlTimezone.item(0) != null)){
			        Element eleTimezone = (Element)nlTimezone.item(0);
			        String strTimezone = eleTimezone.getTextContent();
		        }

		        NodeList nlRegion = eleRoot.getElementsByTagName("region");
		        if ((nlRegion != null) && (nlRegion.item(0) != null)){
			        Element eleRegion = (Element)nlRegion.item(0);
			        String strRegion = eleRegion.getTextContent();
		        }

		        NodeList nlUpgrade = eleRoot.getElementsByTagName("upgrade");
		        
		        if (nlUpgrade != null){
		        	eleUpgrade = (Element)nlUpgrade.item(0);
		 	        
		        	if (eleUpgrade != null){
		        		NodeList nlType = eleUpgrade.getElementsByTagName("type");
		        		if ((nlType != null) && (nlType.item(0) != null)){
		        			Element eleType = (Element)nlType.item(0);
				 	        String strType = eleType.getTextContent();
		        		}
			 	        
			 	        NodeList nlAppid = eleUpgrade.getElementsByTagName("appid"); 
			 	        if ((nlAppid != null) && (nlAppid.item(0) != null)){
				 	        Element eleAppid = (Element)nlAppid.item(0);
				 	        String strAppid = eleAppid.getTextContent();
			 	        }
			 	        
			 	        NodeList nlTitle = eleUpgrade.getElementsByTagName("title");
			 	        if ((nlTitle != null) && (nlTitle.item(0) != null)){
			 	            Element eleTitle = (Element)nlTitle.item(0);
				 	        String strTitle = eleTitle.getTextContent();
			 	        }
			 	        
			 	        NodeList nlApptype = eleUpgrade.getElementsByTagName("apptype");
			 	        if ((nlApptype != null) && (nlApptype.item(0) != null)){
			 	        	Element eleApptype = (Element)nlApptype.item(0);
				 	        String strApptype = eleApptype.getTextContent();
			 	        }
			 	        
			 	        NodeList nlVersion = eleUpgrade.getElementsByTagName("version");
			 	        if ((nlVersion != null) && (nlVersion.item(0) != null)){
				 	        Element eleVersion = (Element)nlVersion.item(0);
				 	        mFileVersion = eleVersion.getTextContent();
			 	        }

			 	        NodeList nlVerid = eleUpgrade.getElementsByTagName("verid");
			 	        if ((nlVerid != null) && (nlVerid.item(0) != null)){
				 	        Element eleVerid = (Element)nlVerid.item(0);
				 	        String strVerid = eleVerid.getTextContent();
			 	        }

			 	        NodeList nlDescription = eleUpgrade.getElementsByTagName("description");
			 	        if ((nlDescription != null) && (nlDescription.item(0) != null)){
				 	        Element eleDescription = (Element)nlDescription.item(0);
				 	        String strDescription = eleDescription.getTextContent();
			 	        }
			 	        
			 	        NodeList nlMinicon = eleUpgrade.getElementsByTagName("minicon");
			 	        if ((nlMinicon != null) && (nlMinicon.item(0) != null)){
				 	        Element eleMinicon = (Element)nlMinicon.item(0);
				 	        String strMinicon = eleMinicon.getTextContent();
			 	        }

			 	        NodeList nlMidicon = eleUpgrade.getElementsByTagName("midicon");
			 	        if ((nlMidicon != null) && (nlMidicon.item(0) != null)){
				 	        Element eleMidicon = (Element)nlMidicon.item(0);
				 	        String strMidicon = eleMidicon.getTextContent();
			 	        }
			 	        
			 	        NodeList nlFileurl = eleUpgrade.getElementsByTagName("fileurl");
			 	        if ((nlFileurl != null) && (nlFileurl.item(0) != null)){
				 	        Element eleFileurl = (Element)nlFileurl.item(0);
				 	        mFileURL = eleFileurl.getTextContent();
			 	        }
			 	        
			 	        NodeList nlSize = eleUpgrade.getElementsByTagName("size");
			 	        if ((nlSize != null) && (nlSize.item(0) != null)){
				 	        Element eleSize = (Element)nlSize.item(0);
				 	        mFileSize = eleSize.getTextContent();
			 	        }

			 	        NodeList nlMd5 = eleUpgrade.getElementsByTagName("md5");
			 	        if ((nlMd5 != null) && (nlMd5.item(0) != null)){
				 	        Element eleMd5 = (Element)nlMd5.item(0);
				 	        mFileMD5 = eleMd5.getTextContent();
			 	        }

			 	        NodeList nlIncrement = eleUpgrade.getElementsByTagName("increment");
			 	        if ((nlIncrement != null) && (nlIncrement.item(0) != null)){
				 	        Element eleIncrement = (Element)nlIncrement.item(0);
				 	        String strIncrement = eleIncrement.getTextContent();
			 	        }

			 	        NodeList nlAppendver = eleUpgrade.getElementsByTagName("appendver");
			 	        if ((nlAppendver != null) && (nlAppendver.item(0) != null)){
				 	        Element eleAppendver = (Element)nlAppendver.item(0);
				 	        String strAppendver = eleAppendver.getTextContent();
			 	        }
			 	        
			 	        NodeList nlNote = eleUpgrade.getElementsByTagName("note");
			 	        if ((nlNote != null) && (nlNote.item(0) != null)){
				 	        Element eleNote = (Element)nlNote.item(0);
				 	        mVersionInfo = eleNote.getTextContent();
			 	        }
		        	}
		        }
		        
		        NodeList nlApiversion = eleRoot.getElementsByTagName("apiversion");
		        if ((nlApiversion != null) && (nlApiversion.item(0) != null)){
			        Element eleApiversion = (Element)nlApiversion.item(0);
			        String strApiversion = eleApiversion.getTextContent();
		        }
	        }
	        
	        if (!callId.equals("")){
	        	mUpgradeInfoAccessHelper.setCallId(callId);
	        }
	        
	        if (strState.equals("0000") && (eleUpgrade != null)){
	        	return 0;
	        }
	        
	        Log.d(TAG,"strCallid:"+callId+";strState:"+strState);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public class UpgradeInfoAccessHelper{
		private DataBaseUtil mDataBaseUtil;
		private String mcallId = "";
		private String mVersion = "";
		private String mStage = "";
		private HuanUpgradeInfoBean mHuanUpgradeInfoBean;
		
		public UpgradeInfoAccessHelper() {
			mDataBaseUtil = new DataBaseUtil(mContext);
			mHuanUpgradeInfoBean = new HuanUpgradeInfoBean();
			
			mDataBaseUtil.open();
			Cursor cursor = mDataBaseUtil.fetchUpgradeInfo(1);
			if ((cursor == null) || (cursor.getCount() <= 0)){
				Log.d(TAG,"db table is null, insert record data");
				ContentValues initialValues;
				initialValues = new ContentValues();
				initialValues.put(ContentDefine.UpgradeTable.KEY_CALLID, "0");
				initialValues.put(ContentDefine.UpgradeTable.KEY_VERSION, mAppver);
				initialValues.put(ContentDefine.UpgradeTable.KEY_STAGE, ContentDefine.UpgradeTable.STAGE_NORMAL);
				initialValues.put(ContentDefine.UpgradeTable.KEY_DOWNLOADID, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILEURL, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILESIZE, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILEMD5, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILEVERSION, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILESTOAGEURL, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_FILEUUID, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_RESERVER1, "");
				initialValues.put(ContentDefine.UpgradeTable.KEY_RESERVER2, "");
				mDataBaseUtil.createUpgradeInfo(initialValues);
			}else{
				mcallId = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_CALLID));
				mVersion = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_VERSION));
				mStage = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_STAGE));
				String downLoadId = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_DOWNLOADID));
				String fileUrl = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILEURL)); 
				String fileSize = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILESIZE));
				String fileMd5 = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILEMD5)); 
				String fileVersion = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILEVERSION));
				String fileStoageUrl = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILESTOAGEURL));
				String fileuuid = cursor.getString(cursor.getColumnIndex(ContentDefine.UpgradeTable.KEY_FILEUUID));
				mHuanUpgradeInfoBean.setDownLoadId(downLoadId);
				mHuanUpgradeInfoBean.setFileURL(fileUrl);
				mHuanUpgradeInfoBean.setFileSize(fileSize);
				mHuanUpgradeInfoBean.setFileMD5(fileMd5);
				mHuanUpgradeInfoBean.setFileVersion(fileVersion);
				mHuanUpgradeInfoBean.setFileStorageURL(fileStoageUrl);
				mHuanUpgradeInfoBean.setFileUUID(fileuuid);
				
				mDownLoadId = downLoadId;
				
				Log.d(TAG,"db table is no null; mcallId:"+mcallId+";mVersion:"+mVersion+";mStage:"+mStage+";downLoadId:"+downLoadId+";fileUrl:"+fileUrl
						+";fileSize:"+fileSize+";fileMd5:"+fileMd5+";fileVersion:"+fileVersion+";fileStoageUrl:"+fileStoageUrl+";fileuuid:"+fileuuid);
			}
			
			mDataBaseUtil.close();
		}
		
		public void updateUpgradeinfo(NameValuePair[] pairs){
			Log.d(TAG,"updateAuthinfo pairs");
			if ((pairs != null) && (pairs.length > 0)){
				mDataBaseUtil.open();
				ContentValues newValues;
				newValues = new ContentValues();
				for (NameValuePair nameValuePair:pairs){
					newValues.put(nameValuePair.getName(), nameValuePair.getValue());
					Log.d(TAG,"updateUpgradeinfo,nameValuePair.getName():"+nameValuePair.getName()+";nameValuePair.getValue():"+nameValuePair.getValue());
				}
				
				mDataBaseUtil.updateUpgradeInfo(1, newValues);
				mDataBaseUtil.close();
			}
		}
		
		public String getCallId(){
			return mcallId;
		}
		
		public void setCallId(String callId){
			mcallId = callId;
			BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.UpgradeTable.KEY_CALLID, mcallId);
			NameValuePair[] pairs = {basicNameValuePair1};
			updateUpgradeinfo(pairs);
		}
		
		public String getVersion(){
			return mVersion;
		}
		
		public void setVersion(String version){
			mVersion = version;
		}
		
		public void saveVersion(String version){
			mVersion = version;
			BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.UpgradeTable.KEY_VERSION, mVersion);
			NameValuePair[] pairs = {basicNameValuePair1};
			updateUpgradeinfo(pairs);
		}
		
		public String getStage(){
			return mStage;
		}
		
		public void setStage(String stage){
			mStage = stage;
		}
		
		public void saveStage(String stage){
			mStage = stage;
			BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.UpgradeTable.KEY_STAGE, mStage);
			NameValuePair[] pairs = {basicNameValuePair1};
			updateUpgradeinfo(pairs);
		}
		
		public String getDownLoadId(){
			return mHuanUpgradeInfoBean.getDownLoadId();
		}
		
		public void setDownLoadId(String downLoadId){
			mHuanUpgradeInfoBean.setDownLoadId(downLoadId);
			mDownLoadId = downLoadId;
		}
		
		public void saveDownLoadId(String downLoadId){
			mDownLoadId = downLoadId;
			mHuanUpgradeInfoBean.setDownLoadId(downLoadId);
			BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair(ContentDefine.UpgradeTable.KEY_DOWNLOADID, downLoadId);
			NameValuePair[] pairs = {basicNameValuePair1};
			updateUpgradeinfo(pairs);
		}
		
		public String getFileURL(){
			return mHuanUpgradeInfoBean.getFileURL();
		}
		
		public void setFileURL(String fileURL){
			mHuanUpgradeInfoBean.setFileURL(fileURL);
		}
		
		public String getFileSize(){
			return mHuanUpgradeInfoBean.getFileSize();
		}
		
		public void setFileSize(String fileSize){
			mHuanUpgradeInfoBean.setFileSize(fileSize);
		}
		
		public String getFileMD5(){
			return mHuanUpgradeInfoBean.getFileMD5();
		}
		
		public void setFileMD5(String fileMD5){
			mHuanUpgradeInfoBean.setFileMD5(fileMD5);
		}
		
		public String getFileVersion(){
			return mHuanUpgradeInfoBean.getFileVersion();
		}
		
		public void setFileVersion(String fileVersion){
			mHuanUpgradeInfoBean.setFileVersion(fileVersion);
		}
		
		public String getFileStorageURL(){
			return mHuanUpgradeInfoBean.getFileStorageURL();
		}
		
		public void setFileStorageURL(String fileStorageURL){
			mHuanUpgradeInfoBean.setFileStorageURL(fileStorageURL);
		}
		
		public String getFileUUID(){
			return mHuanUpgradeInfoBean.getFileUUID();
		}
		
		public void setFileUUID(String fileUUID){
			mHuanUpgradeInfoBean.setFileUUID(fileUUID);
		}
		
		public void updateUpgradeinfoBean(){
			Log.d(TAG,"updateUpgradeinfoBean pairs");
			Log.d(TAG,"updateUpgradeinfoBean DownLoadId:"+mHuanUpgradeInfoBean.getDownLoadId());
			mDataBaseUtil.open();
			ContentValues newValues;
			newValues = new ContentValues();
			newValues.put(ContentDefine.UpgradeTable.KEY_DOWNLOADID, mHuanUpgradeInfoBean.getDownLoadId());
			newValues.put(ContentDefine.UpgradeTable.KEY_FILEURL, mHuanUpgradeInfoBean.getFileURL());
			newValues.put(ContentDefine.UpgradeTable.KEY_FILESIZE, mHuanUpgradeInfoBean.getFileSize());
			newValues.put(ContentDefine.UpgradeTable.KEY_FILEMD5, mHuanUpgradeInfoBean.getFileMD5());
			newValues.put(ContentDefine.UpgradeTable.KEY_FILEVERSION, mHuanUpgradeInfoBean.getFileVersion());
			newValues.put(ContentDefine.UpgradeTable.KEY_FILESTOAGEURL, mHuanUpgradeInfoBean.getFileStorageURL());
			newValues.put(ContentDefine.UpgradeTable.KEY_STAGE, mStage);
			mDataBaseUtil.updateUpgradeInfo(1, newValues);
			mDataBaseUtil.close();
			
		}
		
		public String getDnum(){
			if (mDnum.equals("")){
				DataBaseUtil dataBaseUtil = new DataBaseUtil(mContext);
				dataBaseUtil.open();
				Cursor cursor = dataBaseUtil.fetchAuthInfo(1);
				if ((cursor != null) && (cursor.getCount() > 0)){
					mDnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
					//mDevicemode = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
					mDevicemode = Util.getDevMode();
					mDidtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
				}
				dataBaseUtil.close();
			}
			
			return mDnum;
		}
		
		public String getDevicemode(){
			if (mDevicemode.equals("")){
				DataBaseUtil dataBaseUtil = new DataBaseUtil(mContext);
				dataBaseUtil.open();
				Cursor cursor = dataBaseUtil.fetchAuthInfo(1);
				if ((cursor != null) && (cursor.getCount() > 0)){
					mDnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
					//mDevicemode = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
					mDevicemode = Util.getDevMode();
					mDidtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
				}
				dataBaseUtil.close();
			}
			
			return mDevicemode;
		}
		
		public String getDidtoken(){
			if (mDidtoken.equals("")){
				DataBaseUtil dataBaseUtil = new DataBaseUtil(mContext);
				dataBaseUtil.open();
				Cursor cursor = dataBaseUtil.fetchAuthInfo(1);
				if ((cursor != null) && (cursor.getCount() > 0)){
					mDnum = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DNUM));
					//mDevicemode = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DEVICEMODE));
					mDevicemode = Util.getDevMode();
					mDidtoken = cursor.getString(cursor.getColumnIndex(ContentDefine.AuthTable.KEY_DIDTOKEN));
				}
				dataBaseUtil.close();
			}
			
			return mDidtoken;
		}
	}
	
	static public long getDownLoadId(){
		String strDownLoadId = "";
		long downLoadId = 0xffffffff;
		
		if (mDownLoadId != null && !mDownLoadId.equals("")){
			downLoadId = Long.valueOf(mDownLoadId);
		}
		
		return downLoadId;
	}
}
