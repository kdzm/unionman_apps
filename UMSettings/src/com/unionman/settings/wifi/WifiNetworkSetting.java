package com.unionman.settings.wifi;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import java.net.InetAddress;

import com.unionman.settings.tools.DLBLog;

public class WifiNetworkSetting
{
  LinkProperties mLinkProperties = new LinkProperties();
  
  public WifiNetworkSetting()
  {
    mLinkProperties.clear();
  }

  public LinkProperties getmLinkProperties()
  {
    return mLinkProperties;
  }

  public void setDns2(String paramString,int parint)
  {
    try
    {
    	if(parint>0)
    	{
      mLinkProperties.addDns(NetworkUtils.numericToInetAddress(paramString));
    	}
    }
    catch (Exception localException)
    {
    }
	return;
  }

  
  public void setDns(String paramString)
  {
    try
    { DLBLog.d("setDns===="+paramString);
      mLinkProperties.addDns(NetworkUtils.numericToInetAddress(paramString));
      
    }
    catch (Exception localException)
    {
    }
	return;
  }
  public void setGateway(String paramString)
  {
    try
    {  DLBLog.d("setGateway===="+paramString);
      mLinkProperties.addRoute(new RouteInfo(NetworkUtils.numericToInetAddress(paramString)));
      
    }
    catch (Exception localException)
    {

    }
	return;
  }

  public void setIP(String paramString, int paramInt)
  {
    try
    {   DLBLog.d("setIP===="+paramString+"paramInt=="+paramInt);
      if (paramInt >= 0 && paramInt<=32){
      	mLinkProperties.addLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(paramString), paramInt));
      }
    }
    catch (Exception localException)
    {
    	
    }
	return;
  }
}