package com.unionman.settings.content;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.unionman.settings.R;
import com.unionman.settings.UMSettings;
import com.unionman.settings.custom.Appinfo;
import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.UMDebug;

public class AppDetail extends RightWindowBase
{
  private static final String ATTR_PACKAGE_STATS = "PackageStats";
  private static final int CLEAR_CACHE = 3;
  private static final int CLEAR_USER_DATA = 1;
  private static final int GET_PKG_SIZE = 2;
  private static final int OP_FAILED = 2;
  private static final int OP_SUCCESSFUL = 1;
  private static final int PACKAGE_MOVE = 4;
  private final int UNINSTALL_COMPLETE = 5;
  private Appinfo app;
  private TextView tv_appName;
  private TextView tv_appSize;
  private TextView tv_appVer;
  private Button btn_Clearcache;
  private Button btn_Cleardata;
  private Button btn_Clearlaunch;
  private Button btn_Force;
  private Button btn_Uninstall;
  private TextView tv_cacheSize;
  private TextView tv_dataSize;
  private ImageView iv;
  private TextView tv_launch;
  private static final String TAG = "com.unionman.settings.content.app--AppDetail--";
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {

    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Logger.i(TAG,"mCheckKillProcessesReceiver--onReceive()--");
      if (getResultCode() != 0);
      for (boolean bool = true; ; bool = false)
      {
        if (AppDetail.this.enable)
        {
          AppDetail.this.btn_Force.setEnabled(bool);
          if (!bool)
            break;
          AppDetail.this.btn_Force.setTextColor(paramAnonymousContext.getResources().getColor(R.color.white));
        }
        return;
      }
      AppDetail.this.btn_Force.setTextColor(paramAnonymousContext.getResources().getColor(R.color.gray));
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
    	
    	Logger.i(TAG,"handleMessage");
    /*  if (!AppDetail.this.enable);
      do
      {
        do
        {
          
          switch (paramAnonymousMessage.what)
          {
          case 4:
        	 
        //  default:
            break;
          case 1:
            AppDetail.this.processClearMsg(paramAnonymousMessage);
          
            break;
          case 2:
            PackageStats localPackageStats = (PackageStats)paramAnonymousMessage.getData().getParcelable("PackageStats");
            AppDetail.this.app.cacheSize = localPackageStats.cacheSize;
            AppDetail.this.app.dataSize = localPackageStats.dataSize;
            AppDetail.this.app.codeSize = localPackageStats.codeSize;
           
            break;
          case 3:
        	
        	  break;
          case 5:
        	
        	  break;
          default:
        	  break;
          }
		  //umdebug.umdebug_trace();
        }
        while (!AppDetail.this.enable);
		AppDetail.this.initSize();
    	AppDetail.this.pm.getPackageSizeInfo(AppDetail.this.app.pkgName, new AppDetail.PkgSizeObserver(AppDetail.this));
      //  break;
      }
      while (paramAnonymousMessage.arg1 != 1);*/
     
  	Logger.i(TAG,"backShowView");
      AppDetail.this.layoutManager.backShowView();
    }
  };
  private PackageInfo packageInfo;
  private PackageManager pm;
  private UMSettings settings;
  private TextView totalSize;

  public AppDetail(Context paramContext)
  {
    super(paramContext);
  }

  private void checkForceStop()
  {
    Logger.i(TAG,"checkForceStop()--");
	this.btn_Force.setEnabled(false);
  	this.btn_Force.setTextColor(this.context.getResources().getColor(R.color.gray));
    Intent intent = new Intent(Intent.ACTION_QUERY_PACKAGE_RESTART,
            Uri.fromParts("package",AppDetail.this.app.pkgName, null));
    intent.putExtra(Intent.EXTRA_PACKAGES, new String[] { AppDetail.this.app.pkgName });
    intent.putExtra(Intent.EXTRA_UID, AppDetail.this.app.uid);
    intent.putExtra(Intent.EXTRA_USER_HANDLE, UserHandle.getUserId(AppDetail.this.app.uid));
    this.context.sendOrderedBroadcast(intent, null, mCheckKillProcessesReceiver, null,
            Activity.RESULT_CANCELED, null, null);
    
    
  }

  private void forceStopPackage(String paramString)
  { 
	 Logger.i(TAG, "forceStopPackage()--");
    ((ActivityManager)this.context.getSystemService("activity")).forceStopPackage(paramString);
     checkForceStop();
  }

  private void initiateClearUserData()
  {
    Logger.i(TAG,"initiateClearUserData()--");
    this.btn_Cleardata.setEnabled(false);
    this.btn_Cleardata.setTextColor(this.context.getResources().getColor(R.color.gray));
    String str = this.app.pkgName;
    ((ActivityManager)this.context.getSystemService("activity")).clearApplicationUserData(str, new ClearUserDataObserver());
  }

  private void processClearMsg(Message paramMessage)
  {
    Logger.i(TAG,"processClearMsg(Message paramMessage)--");
    int i = paramMessage.arg1;
    String str = this.app.pkgName;
    if (i == 1)
    {
      this.pm.getPackageSizeInfo(str, new PkgSizeObserver(this));
      return;
    }
    this.btn_Cleardata.setEnabled(true);
    this.btn_Cleardata.setTextColor(this.context.getResources().getColor(R.color.white));
  }

  private void showCleanDataConfirmDialog()
  {
    Logger.i(TAG,"showCleanDataConfirmDialog()--");
    final CustomDialog localCustomDialog = new CustomDialog(this.context, 554, 354, R.layout.dialog_clear_data, R.style.dialog);
    Button localButton1 = (Button)localCustomDialog.findViewById(R.id.clear_data_yes);
    Button localButton2 = (Button)localCustomDialog.findViewById(R.id.clear_data_no);
    localButton1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AppDetail.this.initiateClearUserData();
        localCustomDialog.dismiss();
      }
    });
    localButton2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        localCustomDialog.dismiss();
      }
    });
    localCustomDialog.setCancelable(true);
    localCustomDialog.show();
  }

  private void showUninstallConfirmDialog()
  {
    Logger.i(TAG,"showCleanDataConfirmDialog()--");
    final CustomDialog localCustomDialog = new CustomDialog(this.context, 554, 354, R.layout.dialog_clear_data, R.style.dialog);
    Button localButton1 = (Button)localCustomDialog.findViewById(R.id.clear_data_yes);
    Button localButton2 = (Button)localCustomDialog.findViewById(R.id.clear_data_no);
    localButton1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AppDetail.this.uninstall(AppDetail.this.app.pkgName);
        localCustomDialog.dismiss();
      }
    });
    localButton2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        localCustomDialog.dismiss();
      }
    });
    localCustomDialog.setCancelable(true);
    localCustomDialog.show();
  }

  private void uninstall(String paramString)
  {
    Logger.i(TAG,"uninstall(String paramString)--");
    PackageDeleteObserver localPackageDeleteObserver = new PackageDeleteObserver();
    this.pm.deletePackage(paramString, localPackageDeleteObserver, 0);
  }

  public void initClearLaunchBtn(String paramString)
  {
    Logger.i(TAG,"initClearLaunchBtn(String paramString)--");
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    this.pm.getPreferredActivities(localArrayList2, localArrayList1, paramString);
    if (localArrayList1.size() <= 0)
    {
      this.btn_Clearlaunch.setEnabled(false);
      this.btn_Clearlaunch.setTextColor(this.context.getResources().getColor(R.color.gray));
      return;
    }else{
    this.btn_Clearlaunch.setEnabled(true);
    this.btn_Clearlaunch.setTextColor(this.context.getResources().getColor(R.color.white));
    this.tv_launch.setText(2131296459);
    }
  }

  public void initData()
  {
	  
  }
//初始化btn的状态
  public void initSize()
  {
    Logger.i(TAG,"initSize()--");
    this.totalSize.setText(Formatter.formatFileSize(this.context, this.app.codeSize + this.app.dataSize));
    
    this.tv_dataSize.setText(Formatter.formatFileSize(this.context, this.app.dataSize));
    if (this.app.dataSize <= 0L)
    {
      this.btn_Cleardata.setEnabled(false);
      this.btn_Cleardata.setTextColor(this.context.getResources().getColor(R.color.gray));
      this.app.dataSize = 0L;
    }else{
    	this.btn_Cleardata.setEnabled(true);
        this.btn_Cleardata.setTextColor(this.context.getResources().getColor(R.color.white));
    }
    
    this.tv_cacheSize.setText(Formatter.formatFileSize(this.context, this.app.cacheSize));
    if (this.app.cacheSize <= 0L)
    {
      this.btn_Clearcache.setEnabled(false);
      this.btn_Clearcache.setTextColor(this.context.getResources().getColor(R.color.gray));
      this.app.dataSize = 0L;
    }else{
    	this.btn_Clearcache.setEnabled(true);
        this.btn_Clearcache.setTextColor(this.context.getResources().getColor(R.color.white));
    }
    
    this.tv_appSize.setText(Formatter.formatFileSize(this.context, this.app.codeSize));
    if (this.app.systemApp)
    {
      this.btn_Uninstall.setEnabled(false);
      this.btn_Uninstall.setTextColor(this.context.getResources().getColor(R.color.gray));
    }else{
    	this.btn_Uninstall.setEnabled(true);
        this.btn_Uninstall.setTextColor(this.context.getResources().getColor(R.color.white));
    }
    if(this.app.runingApp){
    	this.btn_Force.setEnabled(true);
    	this.btn_Force.setTextColor(this.context.getResources().getColor(R.color.white));
    }else{
    	this.btn_Force.setEnabled(false);
        this.btn_Force.setTextColor(this.context.getResources().getColor(R.color.gray));
    }
  }

  public void onInvisible()
  {
  }

  public void onResume()
  {
    Logger.i(TAG,"onResume()--");
    this.app = this.settings.getAppInfo();
    while (true)
    {
      try
      {
        this.packageInfo = this.pm.getPackageInfo(this.app.pkgName, 0);
        this.iv.setImageDrawable(this.app.appIcon);
        this.tv_appName.setText(this.app.appName);
        if ((this.packageInfo != null) && (this.packageInfo.versionName != null))
        {
          this.tv_appVer.setVisibility(0);
          TextView localTextView = this.tv_appVer;
          Resources localResources = this.context.getResources();
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = String.valueOf(this.packageInfo.versionName);
          localTextView.setText(localResources.getString(R.string.app_detail_info_version, arrayOfObject));
          initSize();
          initClearLaunchBtn(this.app.pkgName);
          this.btn_Force.requestFocus();
          return;
        }
		UMDebug.umdebug_trace();
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        localNameNotFoundException.printStackTrace();
        return;
      }
      this.tv_appVer.setVisibility(4);
    }
  }

  public void setId()
  {
    Logger.i(TAG,"setId()--");
    this.frameId = ConstantList.FRAME_APP_DETAIL;
    this.levelId = 1003;
    this.settings = ((UMSettings)this.context.getApplicationContext());
    this.pm = this.context.getPackageManager();
  }

  public void setView()
  {
	Logger.i(TAG,"setView()--");
    this.layoutInflater.inflate(R.layout.app_detail, this);
    this.iv = ((ImageView)findViewById(R.id.app_detail_icon));
    this.tv_appName = ((TextView)findViewById(R.id.app_detail_name));
    this.tv_appVer = ((TextView)findViewById(R.id.app_detail_version));
    this.tv_appSize = ((TextView)findViewById(R.id.app_detail_app));
    this.totalSize = ((TextView)findViewById(R.id.app_detail_total));
    this.tv_dataSize = ((TextView)findViewById(R.id.app_detail_data));
    this.tv_cacheSize = ((TextView)findViewById(R.id.app_detail_cache));
    this.tv_launch = ((TextView)findViewById(R.id.app_detail_launch));
    this.btn_Clearcache = ((Button)findViewById(R.id.app_detail_btn_cache));
    this.btn_Clearcache.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
    	 
    	  Logger.i(TAG,"清除缓存");
        AppDetail.this.pm.deleteApplicationCacheFiles(AppDetail.this.app.pkgName, new AppDetail.ClearCacheObserver(AppDetail.this));
      }
    });
    this.btn_Cleardata = ((Button)findViewById(R.id.app_detail_btn_data));
    this.btn_Cleardata.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
    	 
    	  Logger.i(TAG,"清除数据");
        AppDetail.this.showCleanDataConfirmDialog();
      }
    });
    this.btn_Force = ((Button)findViewById(R.id.app_detail_btn_force));
    this.btn_Force.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
    	 
    	  Log.i(TAG,"停止");
    	AppDetail.this.forceStopPackage(AppDetail.this.app.pkgName);

      }
    });
    this.btn_Uninstall = ((Button)findViewById(R.id.app_detail_btn_uninstall));
    this.btn_Uninstall.setOnClickListener(new View.OnClickListener()
    {
    	
      public void onClick(View paramAnonymousView)
      {	
    	  
    	  Logger.i(TAG,"卸载");
        AppDetail.this.showUninstallConfirmDialog();
      }
    });
    this.btn_Clearlaunch = ((Button)findViewById(R.id.app_detail_btn_launch));
    this.btn_Clearlaunch.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AppDetail.this.pm.clearPackagePreferredActivities(AppDetail.this.app.pkgName);
        AppDetail.this.tv_launch.setText(R.string.app_detail_launch_no);
        AppDetail.this.btn_Clearlaunch.setEnabled(false);
        AppDetail.this.btn_Clearlaunch.setTextColor(AppDetail.this.context.getResources().getColor(R.color.gray));
      }
    });
  }

  class ClearCacheObserver extends IPackageDataObserver.Stub
  {
	 
    private AppDetail mAppDetail;
    ClearCacheObserver(AppDetail app)
    {
    	mAppDetail = app;
    }

    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      Logger.i(TAG,"onRemoveCompleted()--");
      Message localMessage = mAppDetail.mHandler.obtainMessage(3);
      if (paramBoolean);
      for (int i = 1; ; i = 2)
      {
        localMessage.arg1 = i;
        Logger.i(TAG,localMessage+"");
        mAppDetail.mHandler.sendMessage(localMessage);

        return;
      }
    }
  }

  class ClearUserDataObserver extends IPackageDataObserver.Stub
  {
    ClearUserDataObserver()
    {
    }

    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      int i = 1;
      Message localMessage = AppDetail.this.mHandler.obtainMessage(i);
      if (paramBoolean)
      {
        localMessage.arg1 = i;
        Logger.i(TAG,localMessage+"");
        AppDetail.this.mHandler.sendMessage(localMessage);
       
        return;
      }
    }
  }

  class PackageDeleteObserver extends IPackageDeleteObserver.Stub
  {
    PackageDeleteObserver()
    {
    }

    public void packageDeleted(String paramString, int paramInt)
      throws RemoteException
    {
      Message localMessage = AppDetail.this.mHandler.obtainMessage(5);
      localMessage.arg1 = paramInt;
      Logger.i(TAG,localMessage+"");
      AppDetail.this.mHandler.sendMessage(localMessage);
      
    }
  }

  class PkgSizeObserver extends IPackageStatsObserver.Stub
  {
  	private AppDetail mAppDetail;
    PkgSizeObserver(AppDetail app)
    {
    	mAppDetail = app;
    }

    public void onGetStatsCompleted(PackageStats paramPackageStats, boolean paramBoolean)
      throws RemoteException
    {
      Message localMessage = mAppDetail.mHandler.obtainMessage(2);
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("PackageStats", paramPackageStats);
      localMessage.setData(localBundle);
      Logger.i(TAG,localMessage+"");
      mAppDetail.mHandler.sendMessage(localMessage);
      
    }
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\seting\classes_dex2jar.jar
 * Qualified Name:     net.sunniwell.app.swsettings.chinamobile.content.AppDetail
 * JD-Core Version:    0.6.2
 */