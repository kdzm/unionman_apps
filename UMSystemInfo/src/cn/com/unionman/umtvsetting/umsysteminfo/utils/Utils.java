package cn.com.unionman.umtvsetting.umsysteminfo.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Utils {

	private final static String SDPATH="/storage/emulated/0/recovery";
    
    private final static String CACHEDIR="/cache";
    
    private final static String UPDATEFILENAME="/update.zip";
	
	public static String getSpacePath(String strFileSize){
		Log.i("localupdate" , "getSpacePath   ---strFileSize=" + strFileSize);
		 long fileSize = 0;
		 String destString = "";
		 
		 if ((strFileSize != null) && (!strFileSize.equals(""))){
			 fileSize = Long.valueOf(strFileSize);
		 }
		 
		 fileSize = fileSize + 1024; 
		 
		 if (fileSize < getCacheAvailableSize()){
			 destString = CACHEDIR+UPDATEFILENAME;
		 }else{
			 if (fileSize < getCacheTotalSize()){
				 destString = CACHEDIR+UPDATEFILENAME;
				 cleanCache();
			 }else{
				 if (fileSize < getSdCardAvailableSize()){
					 File file = new File(SDPATH);
					 if (!file.exists()){
						 file.mkdir();
					 }
					 destString = SDPATH+UPDATEFILENAME;
				 }else{
					  return "";
				 }
			 }
		 }
		 
		 return destString;
	 }
	
	 public static long getCacheTotalSize(){
	    	long size = 0;
	    	File cacheDir = Environment.getDownloadCacheDirectory();
	    	StatFs sf = new StatFs(cacheDir.getPath());
			size = sf.getTotalBytes();
			
	    	return size;
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
   
   private  static void deleteFilesByDirectory(File directory) {
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
}
