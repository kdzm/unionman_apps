package com.um.huanauth.provider;

public class HuanUpgradeInfoBean {
	
	private String mDownLoadId = "";
	private String mFileURL = "";
	private String mFileSize = "";
	private String mFileMD5 = "";
	private String mFileVersion = "";
	private String mFileStoageURL = "";
	private String mFileUUID = "";
	
	public void setDownLoadId(String downLoadId){
		mDownLoadId = downLoadId;
	}
	
	public String getDownLoadId(){
		return mDownLoadId;
	}
	
	public void setFileURL(String fileURL){
		mFileURL = fileURL;
	}
	
	public String getFileURL(){
		return mFileURL;
	}
	
	public void setFileSize(String fileSize){
		mFileSize = fileSize;
	}
	
	public String getFileSize(){
		return mFileSize;
	}
	
	public void setFileMD5(String fileMD5){
		mFileMD5 = fileMD5;
	}
	
	public String getFileMD5(){
		return mFileMD5;
	}
	
	public void setFileVersion(String fileVersion){
		mFileVersion = fileVersion;
	}
	
	public String getFileVersion(){
		return mFileVersion;
	}
	
	public void setFileStorageURL(String fileStoageURL){
		mFileStoageURL = fileStoageURL;
	}
	
	public String getFileStorageURL(){
		return mFileStoageURL;
	}
	
	public void setFileUUID(String fileUUID){
		mFileUUID = fileUUID;
	}
	
	public String getFileUUID(){
		return mFileUUID;
	}
}
