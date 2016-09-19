package com.unionman.settings.content;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import android.app.ActivityManager;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;

public class AppActivity extends RightWindowBase
{
  CheckRadioButton crb_appAll;
  CheckRadioButton crb_appDown;
  CheckRadioButton crb_appRun;
  CheckRadioButton crb_availableSize;
  CheckRadioButton crb_totalSize;
  CheckRadioButton crb_StorageAva;
  CheckRadioButton crb_StorageUsed;
  private StatFs mDataFileStats;
  private static final String TAG = "com.unionman.settings.content.app--AppActivity--";
  public AppActivity(Context paramContext)
  {
    super(paramContext);
  }

  public void initData()
  {
   
  }

  public void onInvisible()
  {
  }

  public void onResume()
  {
      Logger.i(TAG,"onResume()--");
	this.crb_totalSize.setText2("1024MB");
	this.crb_availableSize.setText2(getMemAva(context)+"MB");
	this.crb_StorageAva.setText2(getStorageAva());
	this.crb_StorageUsed.setText2(getStorageUsed());
	if(crb_appDown.isChecked()){crb_appDown.setChecked(false);}
	if(crb_appAll.isChecked()){crb_appAll.setChecked(false);}
	if(crb_appRun.isChecked()){crb_appRun.setChecked(false);}
	return;
	}

  public void setId()
  {
      Logger.i(TAG,"setId()--");
		this.frameId = 10;
		this.levelId = 1001;
	  
  }

  public void setView() {
      Logger.i(TAG,"setView()--");
    this.layoutInflater.inflate(R.layout.app, this);
    this.crb_totalSize = ((CheckRadioButton)findViewById(R.id.crb_app_mem_total));
    this.crb_availableSize = ((CheckRadioButton)findViewById(R.id.crb_app_mem_ava));
    this.crb_StorageAva = ((CheckRadioButton)findViewById(R.id.crb_storage_ava));
    this.crb_StorageUsed = ((CheckRadioButton)findViewById(R.id.crb_storage_used));
    this.crb_appDown = ((CheckRadioButton)findViewById(R.id.crb_app_down));
    this.crb_appDown.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
    	  if(paramAnonymousCheckRadioButton.isChecked()){
        try
        {
      
        Logger.i(TAG, "onCheckedChanged 下载");
          AppActivity.this.layoutManager.showLayout(ConstantList.FRAME_APP_DOWNLOAD);
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
    	  }
      }
    });
    this.crb_appAll = ((CheckRadioButton)findViewById(R.id.crb_app_all));
    this.crb_appAll.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
    	  if(paramAnonymousCheckRadioButton.isChecked()){
        try
        {
          Logger.i(TAG, "onCheckedChanged 所有");
          AppActivity.this.layoutManager.showLayout(ConstantList.FRAME_APP_ALL);
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
    	  }
      }
    });
    this.crb_appRun = ((CheckRadioButton)findViewById(R.id.crb_app_run));
    this.crb_appRun.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
    	  if(paramAnonymousCheckRadioButton.isChecked()){
        try
        { 
        Logger.i(TAG, "onCheckedChanged 运行");
          AppActivity.this.layoutManager.showLayout(ConstantList.FRAME_APP_RUN);
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
    	  }
      }
    });
  }
  
  private String getStorageAva(){
      Logger.i(TAG,"getStorageAva()--");
	  long lngAva= getAva("/data")+getAva("/cache")+getAva("/system");
      Logger.i(TAG, "getStorageAva ava=" + lngAva);
	  return Formatter.formatShortFileSize(this.context, lngAva);
  }
  
  private String getStorageUsed(){
      Logger.i(TAG,"getStorageUsed()--");
	 // long lngUsed=getUsed("/data")+getUsed("/cache")+getUsed("/system")+(long)2210*1024*1024;
   	  long lngUsed = (long)8*1024*1024*1024 - getAva("/data") - getAva("/cache") - getAva("/system");
	  Logger.i(TAG, "getStorageUsed used="+lngUsed);
	  return Formatter.formatShortFileSize(this.context, lngUsed);
  }
  
  private long getAva(String where){
      Logger.i(TAG,"getAva(String where)--");
	  	this.mDataFileStats = new StatFs(where);
	  	//this.mDataFileStats.restat(where);
	  	long lngAvailable=0;
	    try{
            lngAvailable = (long)this.mDataFileStats.getAvailableBlocks()*(long)(this.mDataFileStats.getBlockSize());
	    }
	    catch (IllegalArgumentException localIllegalArgumentException){
	      localIllegalArgumentException.printStackTrace();
	    }
	    Logger.i(TAG, "getAva where="+where+",lngAvailable="+lngAvailable);
	    return lngAvailable;
  }
  
  private long getUsed(String where){
      Logger.i(TAG,"getUsed(String where)--");
	    this.mDataFileStats = new StatFs(where);
	    //this.mDataFileStats.restat(where);
	  	long lnguUsed=0;
	    try{
	    	long lngTotal = (long)this.mDataFileStats.getBlockCount() * (long)(this.mDataFileStats.getBlockSize());
	        long lngAvailable = (long)this.mDataFileStats.getAvailableBlocks()*(long)(this.mDataFileStats.getBlockSize());
            lnguUsed = lngTotal - lngAvailable;
	        Logger.i(TAG, "getAva where="+where+",totalCount="+mDataFileStats.getBlockCount()+",crb_totalSize="+mDataFileStats.getBlockSize());
	        Logger.i(TAG, "getAva where="+where+",total="+lngTotal);
	    }catch (IllegalArgumentException localIllegalArgumentException){
	    	localIllegalArgumentException.printStackTrace();
	    }
	    Logger.i(TAG, "getAva where="+where+",lnguUsed="+lnguUsed);
	    return lnguUsed;
  }
  
  
  //获得总内存
  public static long getMemTotal() {
      Logger.i(TAG,"getMemTotal()--");
      long lngTotal;
      // /proc/meminfo读出的内核信息进行解释
      String strPath = "/proc/meminfo";
      String strContent = null;
      BufferedReader mBufferedReader = null;
      try {
          mBufferedReader = new BufferedReader(new FileReader(strPath), 8);
          String strLine;
          if ((strLine = mBufferedReader.readLine()) != null) {
              strContent = strLine;
          }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          if (mBufferedReader != null) {
              try {
                  mBufferedReader.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      // beginIndex
      int intBegin = strContent.indexOf(':');
      // endIndex
      int intEnd = strContent.indexOf('k');
      // 截取字符串信息

      strContent = strContent.substring(intBegin + 1, intEnd).trim();
      lngTotal = Integer.parseInt(strContent);
      return lngTotal/1024;
  }
  
  // 获得可用的内存
  public static long getMemAva(Context mContext) {
      Logger.i(TAG,"getMemAva(Context mContext)--");
      long lngAvailable;
      ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
      ActivityManager.MemoryInfo meninfo = new ActivityManager.MemoryInfo();
      mActivityManager.getMemoryInfo(meninfo);
      lngAvailable = (meninfo.availMem/1024)/1024;
      return lngAvailable;
  }
  
}
