package com.unionman.settings.custom;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.unionman.settings.UMSettings;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Logger;

public class AppinfoAsynctask extends AsyncTask<Integer, Integer, List<Appinfo>>
{
  public static final int FILTER_APPS_ALL = 0;
  public static final int FILTER_APPS_RUNNING = 1;
  public static final int FILTER_APPS_SDCARD = 3;
  public static final int FILTER_APPS_THIRD_PARTY = 2;
  private BaseAdapter adpter;
  private Context ctx;
  private List<Appinfo> list;
  private UMLogger log = UMLogger.getLogger(getClass());
  private ListView lv;
  private PackageManager pm;
  private RightWindowBase rwb;
  private static final String TAG = "com.unionman.settings.custom--AppinfoAsynctask";
  public AppinfoAsynctask(RightWindowBase paramRightWindowBase, Context paramContext, PackageManager paramPackageManager, List<Appinfo> paramList, BaseAdapter paramBaseAdapter, ListView paramListView)
  {
    this.ctx = paramContext;
    this.pm = paramPackageManager;
    this.list = paramList;
    this.adpter = paramBaseAdapter;
    this.lv = paramListView;
    this.rwb = paramRightWindowBase;
  }

  private Appinfo getAppinfo(ApplicationInfo paramApplicationInfo)
  {
    Appinfo localAppinfo = new Appinfo();
    String str = paramApplicationInfo.packageName;
    localAppinfo.pkgName = str;
    localAppinfo.appIcon = paramApplicationInfo.loadIcon(this.pm);
    localAppinfo.appName = paramApplicationInfo.loadLabel(this.pm);
    localAppinfo.uid = paramApplicationInfo.uid;
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    PackSize localPackSize = new PackSize(localCountDownLatch);
	
    this.pm.getPackageSizeInfo(str, localPackSize);
    
    try
    {
      localCountDownLatch.await();
      if (localPackSize.success)
      {
        localAppinfo.cacheSize = localPackSize.pstats.cacheSize;
        localAppinfo.codeSize = localPackSize.pstats.codeSize;
        localAppinfo.dataSize = localPackSize.pstats.dataSize;
        localAppinfo.sdCodeSize = localPackSize.pstats.externalCodeSize;
        localAppinfo.sdDataSize = localPackSize.pstats.externalDataSize;

      }
      return localAppinfo;
    }
    catch (InterruptedException localInterruptedException)
    {
        localInterruptedException.printStackTrace();
		return null;
    }
  }

  private List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessesList()
  {
    return ((ActivityManager)this.ctx.getSystemService("activity")).getRunningAppProcesses();
  }

  protected List<Appinfo> doInBackground(Integer[] paramArrayOfInteger)
  {
  //	return null;
  /* qi.tang
    ArrayList localArrayList = new ArrayList();
    List localList1 = this.pm.getInstalledApplications(8192);
  //  List localList1 = this.pm.getInstalledApplications(0);
  //  if (localList1 == null);
//    label163: label231: label252: 
    ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo;
    do
    {
      localArrayList = null;
      Iterator localIterator1 = localList1.iterator();
      while (!localIterator1.hasNext())
      {
        List localList2;
        do
        {
          do
          {
            while (true)
            {
              //return localArrayList;
              if (paramArrayOfInteger[0].intValue() == 0)
              {
                Iterator localIterator4 = localList1.iterator();
                while (localIterator4.hasNext())
                  localArrayList.add(getAppinfo((ApplicationInfo)localIterator4.next()));
              }
              else
              {
                if (paramArrayOfInteger[0].intValue() != 3)
                  break;
                Iterator localIterator3 = localList1.iterator();
                while (localIterator3.hasNext())
                {
                  ApplicationInfo localApplicationInfo3 = (ApplicationInfo)localIterator3.next();
                  if ((0x40000 & localApplicationInfo3.flags) != 0)
                    localArrayList.add(getAppinfo(localApplicationInfo3));
                }
              }
            }
            if (paramArrayOfInteger[0].intValue() == 2)
            {
              Iterator localIterator2 = localList1.iterator();
              while (true)
              {
                ApplicationInfo localApplicationInfo2 = (ApplicationInfo)localIterator2.next();;
                int m = 0;
                if (localIterator2.hasNext())
                {
                 // localApplicationInfo2 = (ApplicationInfo)localIterator2.next();
                  if ((0x80 & localApplicationInfo2.flags) == 0)
                   // break label231;
                	  break;
                  m = 1;
                }
                while (true)
                {
                  if (m == 0)
                  //  break label252;
                	  break;
                  Appinfo localAppinfo = getAppinfo(localApplicationInfo2);
                  localAppinfo.systemApp = false;
                  localArrayList.add(localAppinfo);
                 // break label163;
                  break;
                  int k;
                  k = (0x1 & localApplicationInfo2.flags);
                  m = 0;
                  if (k == 0)
                    m = 1;
                }
              }
            }
          }
          while (paramArrayOfInteger[0].intValue() != 1);
          localList2 = getRunningAppProcessesList();
        }
        while ((localList2 == null) || (localList2.size() == 0));
        localIterator1 = localList2.iterator();
      }
      localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)localIterator1.next();
    }
    while ((localRunningAppProcessInfo == null) || (localRunningAppProcessInfo.pkgList == null));
    int i = localRunningAppProcessInfo.pkgList.length;
    int j = 0;
    while (j < i)
      try
      {
        ApplicationInfo localApplicationInfo1 = this.pm.getApplicationInfo(localRunningAppProcessInfo.pkgList[j], 8192);
        if (localApplicationInfo1 != null)
          localArrayList.add(getAppinfo(localApplicationInfo1));
       // label385: j++;
        j++;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
       // break label385;
    	  break;
      }
      */
	  
	 
	  Logger.i(TAG, paramArrayOfInteger[0].intValue()+"");
	  
	  //下载
	  if (paramArrayOfInteger[0].intValue() == 2){		
		  Logger.i(TAG, "下载页面");
	  ArrayList<Appinfo> localArrayList = new ArrayList<Appinfo>();	   
	  List<ApplicationInfo> localList1 = this.pm.getInstalledApplications(0);  
	  
	 
	  
	  for (int i=0;i<localList1.size();i++) { 
		  ApplicationInfo applicationInfoInfo = localList1.get(i); 
		  Appinfo localAppinfo = getAppinfo(applicationInfoInfo);
		 
	          //Only display the non-system app info
	  if ((applicationInfoInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0)
	          { 
		  		localAppinfo.systemApp = false;
		  		localArrayList.add(localAppinfo);//如果非系统应用，则添加至appList
	          }
	//  if ((applicationInfoInfo.flags&ApplicationInfo.FLAG_STOPPED)==0)
    //  { 
  	//	localAppinfo.runingApp = true; 
  		
    //  }else{
    	  localAppinfo.runingApp = false;//如果apk停止，设置false
   //   }
	          
	         }
	  
	  
	  return localArrayList;
	  
	  }else if (paramArrayOfInteger[0].intValue() == 1){
		  Logger.i(TAG, "运行界面");
		  ArrayList<Appinfo> localArrayList2 = new ArrayList<Appinfo>();	   
		  List<RunningAppProcessInfo> localList2 = getRunningAppProcessesList();
		  
		  for (int i = 0; i < localList2.size(); i++ ){
			  try{
			  RunningAppProcessInfo runningAppProcessInfo = localList2.get(i);	
			  int n = runningAppProcessInfo.pkgList.length;
			  int m = 0;
			  while (m < n) {
				  ApplicationInfo applicationInfo2 =  this.pm.getApplicationInfo(runningAppProcessInfo.pkgList[m], 0);
				  m++;
				  Appinfo runAppinfo = getAppinfo(applicationInfo2);
				  
				  runAppinfo.runingApp = true;
				
				  
				  if ((applicationInfo2.flags&ApplicationInfo.FLAG_SYSTEM)==0)
		          { 
					  runAppinfo.systemApp = false;
			  		
		          }
				  
				  
				  localArrayList2.add(runAppinfo);
			}

			  }catch(PackageManager.NameNotFoundException localNameNotFoundException){
				  Logger.i(TAG, "出现异常");
			  }
		  }
/*		  ArrayList<Appinfo> localArrayList2 = new ArrayList<Appinfo>();	   
		  List<ApplicationInfo> localList2 = this.pm.getInstalledApplications(0);  
		  
		 
		  
		  for (int i=0;i<localList2.size();i++) { 
			  ApplicationInfo applicationInfoInfo2 = localList2.get(i); 
			  Appinfo runAppinfo = getAppinfo(applicationInfoInfo2);
			 	          
		          //Only display the non-system app info
		  if ((applicationInfoInfo2.flags&ApplicationInfo.FLAG_SYSTEM)==0)
		          { 
			  runAppinfo.systemApp = false;
			  		
		          }
		  if ((applicationInfoInfo2.flagsA&pplicationInfo.FLAG_STOPPED)==0)
	      { 
			  runAppinfo.runingApp = true; 
	  		localArrayList2.add(runAppinfo);
	  		
	      }else{
	    	  runAppinfo.runingApp = false;//如果apk停止，设置false
	      }
		          
		         }*/
		  return localArrayList2;
	  }else if (paramArrayOfInteger[0].intValue() == 0){
		  Logger.i(TAG, "所有界面");
		  ArrayList<Appinfo> localArrayList3 = new ArrayList<Appinfo>();	   
		  List<ApplicationInfo> localList3 = this.pm.getInstalledApplications(0);  
		  

		  
		  for (int i=0;i<localList3.size();i++) { 
			  ApplicationInfo applicationInfoInfo3 = localList3.get(i); 
			  Appinfo allAppinfo = getAppinfo(applicationInfoInfo3);
			  //判断是否apk是否停止
			//  if((applicationInfoInfo3.flags&ApplicationInfo.FLAG_STOPPED)==0)
			//  {
			//	  allAppinfo.runingApp = true;
			//  }else{
				  allAppinfo.runingApp = false;
			//  }
			  if ((applicationInfoInfo3.flags&ApplicationInfo.FLAG_SYSTEM)==0)
	          { 
				  allAppinfo.systemApp = false;
		  		
	          }
			  
			  
			  
			  localArrayList3.add(allAppinfo);
			  }
		  
		  return localArrayList3;
	  }
	  return null;
  }

  protected void onPostExecute(List<Appinfo> paramList)
  {
	  Logger.i(TAG, "nPostExecute paramList"+paramList);
    super.onPostExecute(paramList);
    this.list.clear();
   this.list.addAll(paramList);
    if (this.rwb.enable)
    {
      this.adpter.notifyDataSetChanged();
      this.lv.requestFocus();
    }
  }

  class PackSize extends android.content.pm.IPackageStatsObserver.Stub
  {
    private CountDownLatch mCount;
    public PackageStats pstats;
    public boolean success;

    public PackSize(CountDownLatch arg2)
    {
      this.mCount = arg2;
    }

    public void onGetStatsCompleted(PackageStats paramPackageStats, boolean paramBoolean)
      throws RemoteException
    {
      this.success = paramBoolean;
      this.pstats = paramPackageStats;
      this.mCount.countDown();
    }
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\seting\classes_dex2jar.jar
 * Qualified Name:     net.sunniwell.app.swsettings.chinamobile.custom.AppinfoAsynctask
 * JD-Core Version:    0.6.2
 */