package com.unionman.settings;

import com.unionman.settings.custom.Appinfo;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.wifi.AccessPoint;

import android.app.Application;
import android.os.Handler;

public class UMSettings extends Application
{
  public AccessPoint ap;
  private Appinfo mAppInfo;
  private LayoutManager mLayoutManager;
  public Handler mHandler;
  public String result = "";

  public Appinfo getAppInfo()
  {
    return this.mAppInfo;
  }

  public LayoutManager getLayoutManager()
  {
    return this.mLayoutManager;
  }

  public void onCreate()
  {
    super.onCreate();
  }

  public void onTerminate()
  {
    super.onTerminate();
  }

  public void setAppInfo(Appinfo paramAppinfo)
  {
    this.mAppInfo = paramAppinfo;
  }

  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    this.mLayoutManager = paramLayoutManager;
  }
}