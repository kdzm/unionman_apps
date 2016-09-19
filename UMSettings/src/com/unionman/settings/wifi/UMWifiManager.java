package com.unionman.settings.wifi;

import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.DLBLog;
import com.unionman.settings.tools.EthernetsTools;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
public class UMWifiManager
{
  private static final int CONNECT_PRE_SSID = 1;
  public static final int DISABLED_AUTH_FAILURE = 3;
  public static final int DISABLED_DHCP_FAILURE = 2;
  public static final int DISABLED_DNS_FAILURE = 1;
  public static final int DISABLED_UNKNOWN_REASON = 0;
  public static final int GOTO_CONNECT_PRE_SSID = 0;
  public static final String NET_ACTION = "android.sunniwell.Wifi_STATE_CHANGED";
  public static final String NET_ACTION_MESSAGE = "message";
  private static final int REMOVE_NOTIFATION = 3;
  public static final int SEND_MSG_TO_STATUSBAR = 2;
  private static final int WIFI_RESCAN_INTERVAL_MS = 10000;
  public static final int WIFI_STATE_DISABLED = 1;
  public static final int WIFI_STATE_DISABLING = 0;
  public static final int WIFI_STATE_ENABLED = 3;
  public static final int WIFI_STATE_ENABLING = 2;
  public static final int WIFI_STATE_UNKNOWN = 4;
  private static WifiNetworkSetting mNetwork = null;
  private static WifiConfiguration mWifiConfig = null;
  private static UMWifiManager sManager = null;
  private Context ctx;
  private UMLogger log = UMLogger.getLogger(getClass());
  private AccessPoint mCurrentAccessPoint = null;
  private int mCurrentNetworkId;
  private String mCurrentSSID;
  private IntentFilter mFilter;
  private int mPreNetworkId;
  private Scanner mScanner;
  private Handler mWifiHandler;
  private WifiManager mWifiManager;
  private String mWifiPassword = null;

	private UMWifiManager(Context paramContext)
	{
		this.ctx = paramContext;
		if (this.mWifiManager == null)
			this.mWifiManager = ((WifiManager)this.ctx.getSystemService("wifi"));
		if (this.mScanner == null)
			this.mScanner = new Scanner();
		this.mPreNetworkId = -1;
		this.mWifiHandler = new Handler()
		{
			public void handleMessage(Message paramAnonymousMessage)
			{
				super.handleMessage(paramAnonymousMessage);
				NetworkInfo localNetworkInfo = ((ConnectivityManager)UMWifiManager.this.ctx.getSystemService("connectivity")).getNetworkInfo(1);
				if (paramAnonymousMessage.what == 0)
				{
					if ((UMWifiManager.this.mCurrentSSID != null) && ((localNetworkInfo == null) || (!localNetworkInfo.isConnected())) && (UMWifiManager.this.mWifiManager.isWifiEnabled()))
					{
					new StringBuilder("connect to ").append(UMWifiManager.this.mCurrentSSID).append(" failed, please try it again!").toString();
					if (hasMessages(1))
						removeMessages(1);
						sendEmptyMessageDelayed(1, 3000L);
						UMWifiManager.this.mCurrentSSID = null;
					}
				}
				else  if (paramAnonymousMessage.what == 1)
					return;
				else if (paramAnonymousMessage.what == 2)
				{
			          mCurrentSSID = ((String)paramAnonymousMessage.obj);
					return;
				}
				UMDebug.umdebug_trace();
				((NotificationManager)paramAnonymousMessage.obj).cancel(0);
			}
		};
	}

  private int[] changeToInt(String paramString)
  {
	int[] arrayOfInt = null;
	if (paramString == null)
		return null;
	String[] arrayOfString = paramString.split("\\.");
	if (arrayOfString.length != 4)
		return null;
	try
	{
		arrayOfInt = new int[4];
		for (int i = 0; i < arrayOfString.length; i++)
		  arrayOfInt[i] = Integer.parseInt(arrayOfString[i]);
	}
	catch (Exception localException)
	{
	}
	return arrayOfInt;
  }

  public static UMWifiManager getInstance(Context paramContext)
  {
    if (sManager == null)
      sManager = new UMWifiManager(paramContext);
    if (mNetwork == null)
      mNetwork = new WifiNetworkSetting();
    if (mWifiConfig == null)
      mWifiConfig = new WifiConfiguration();
    return sManager;
  }

  private int getNetmaskLength(String paramString)
  {
    int i = toInt(paramString);
    int j = 0;
    for (int k = 1; ; k++)
    {
      if (k >= 32)
        return j;
      if ((0x1 & i >> k) == 1)
        j++;
    }
  }

	private void setConfig()
	{
		AccessPoint localAccessPoint = mCurrentAccessPoint;
		UMDebug.d("SWifiManager", "setConfig==="+mCurrentAccessPoint);
		if (localAccessPoint == null)
		{
		  return;
		}
		mWifiConfig.SSID = convertToQuotedString(localAccessPoint.getSsid());
		UMDebug.d("SWifiManager", "convertToQuotedString=="+mWifiConfig.SSID);
		mWifiConfig.hiddenSSID = true;
		int i = localAccessPoint.getSecurity();
		UMDebug.d("SWifiManager", ".......getConfig()........iSecurity=" + i);
		switch (i)
		{
			case 0:
				UMDebug.d("SWifiManager", "convertToQuotedString00=="+mWifiConfig.SSID);
				mWifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);
				break;
			case 1:
				UMDebug.d("SWifiManager", "convertToQuotedString11=="+mWifiConfig.SSID);
				mWifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);
				break;
			case 2:
				UMDebug.d("SWifiManager", "convertToQuotedString22=="+mWifiConfig.SSID);
				mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
				mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                            
				mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                            
				mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);   
				mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);   
				mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
				mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				if (mWifiPassword.matches("[0-9A-Fa-f]{64}")) {
				    mWifiConfig.preSharedKey = mWifiPassword;
				} else {
				    mWifiConfig.preSharedKey = '"' + mWifiPassword + '"';
				}
				mWifiConfig.status = WifiConfiguration.Status.ENABLED;  
				mWifiConfig.hiddenSSID = true;    
				return ;
				//break;
			case 3:
				UMDebug.d("SWifiManager", "convertToQuotedString33=="+mWifiConfig.SSID);
				mWifiConfig.allowedAuthAlgorithms.set(1);
				break;
			default:
				mWifiConfig.proxySettings = WifiConfiguration.ProxySettings.NONE;
				mWifiConfig.priority = 2;
				mWifiConfig.allowedProtocols.set(0);
				mWifiConfig.allowedProtocols.set(1);
				return;
		}
		if ((mWifiPassword != null) && (!mWifiPassword.equals("")))
		{
			int j = this.mWifiPassword.length();
			if (((j == 10) || (j == 26) || (j == 58)) && (this.mWifiPassword.matches("[0-9A-Fa-f]*")))
			{  
				mWifiConfig.wepKeys[0] = mWifiPassword;
			}
			else
			{  
				mWifiConfig.wepKeys[0] = ('"' + this.mWifiPassword + '"');
				mWifiConfig.allowedKeyManagement.set(1);
				if ((this.mWifiPassword != null) && (!this.mWifiPassword.equals("")))
				{   
					DLBLog.d("....PSWD===" + this.mWifiPassword);
					if (this.mWifiPassword.matches("[0-9A-Fa-f]{64}"))
					{  
						DLBLog.d("..aaaaa..PSWD===" + this.mWifiPassword);
						mWifiConfig.preSharedKey = this.mWifiPassword;
						mWifiConfig.allowedKeyManagement.set(2);
					}
					else
					{  
						DLBLog.d("..bbbb..PSWD===" + this.mWifiPassword);
						mWifiConfig.preSharedKey = ('"' + this.mWifiPassword + '"');
						mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
					}
				}
			}
		}
		UMDebug.umdebug_trace();
	  	return ;
	}

  private int toInt(String paramString)
  {
	int[] arrayOfInt = changeToInt(paramString);
	int i = 0;
	if (arrayOfInt != null)
	{
		i = arrayOfInt[0] << 24 | arrayOfInt[1] << 16 | arrayOfInt[2] << 8 | arrayOfInt[3];
	}
	return i;
  }

  public void closeWifi()
  {
	if (this.mWifiManager.isWifiEnabled())
		this.mWifiManager.setWifiEnabled(false);
	DLBLog.d("=======closeWifi==end===");
  }

  public boolean connectWifi(String paramString)
  {  

	mCurrentNetworkId = -1;
	mCurrentSSID = paramString;
	UMDebug.d("SWifiManager", "mWifiConfig==="+mWifiConfig);
	if(mWifiConfig == null)
	{
		mWifiConfig = new WifiConfiguration();
	}	
	List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
	UMDebug.d("SWifiManager", "list.size="+ list.size());
	for(int i=0; i<list.size(); i++)
	{
		WifiConfiguration localWifiConfiguration = list.get(i);
		if (localWifiConfiguration.SSID.equals("\"" + paramString + "\""))
		{  
			UMDebug.d("SWifiManager", "localWifiConfiguration==="+localWifiConfiguration);
			mWifiConfig = localWifiConfiguration;
			mCurrentAccessPoint = null;//new AccessPoint(this.ctx, mWifiConfig);
			break;
		}
	}
	setConfig();
	if (((ConnectivityManager)this.ctx.getSystemService("connectivity")).getNetworkInfo(1).isConnected())
	{
		mPreNetworkId = mWifiManager.getConnectionInfo().getNetworkId();
		UMDebug.d("SWifiManager", "mPreNetworkId==="+mPreNetworkId);
	}
	mCurrentNetworkId = mWifiManager.addNetwork(mWifiConfig);
	DLBLog.d(".....connectWifi()..currentNetworkId==" + this.mCurrentNetworkId);
	mWifiConfig.networkId = mCurrentNetworkId;
	DLBLog.d(".....connectWifi()..mWifiConfig.networkId ==" + this.mCurrentNetworkId);
	mWifiManager.saveConfiguration();
	DLBLog.d(".....connectWifi()..saveConfiguration==" + this.mCurrentNetworkId);
	mWifiManager.enableNetwork(mCurrentNetworkId,  true);
	DLBLog.d(".....connectWifi()..enableNetwork==" + this.mCurrentNetworkId);
	mWifiManager.reconnect();	
	DLBLog.d(".....connectWifi()..reconnect==" + this.mCurrentNetworkId);
	mCurrentNetworkId = mWifiManager.updateNetwork(mWifiConfig);
//	mWifiConfig = null;
	return true;
  }

  public String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }

  public void disconnect()
  {
    this.mWifiManager.disconnect();
  }

  public void forgetNetwork(String paramString)
  {
	List localList = mWifiManager.getConfiguredNetworks();
	DLBLog.d("......forgetNetwork()...==configurations.size=" + localList.size());
	Iterator localIterator;
	if ((localList != null) && (localList.size() > 0))
	{
	  	localIterator = localList.iterator();
	}
	else
	{
		return ;
	}
	while (true)
	{     
		UMDebug.umdebug_trace();
		WifiConfiguration localWifiConfiguration = (WifiConfiguration)localIterator.next();
		if ((localWifiConfiguration.SSID != null) && (localWifiConfiguration.SSID.equals('"' + paramString + '"')))
		{
			DLBLog.d("......forgetNetwork()......==");
			mWifiManager.removeNetwork(localWifiConfiguration.networkId);
			mWifiManager.saveConfiguration();
		}
		if (!localIterator.hasNext())
			return;
		UMDebug.umdebug_trace();
	}
  }

  public int getCurrentWifiStatus()
  {
    if (mWifiConfig != null)
      return mWifiConfig.status;
    return 3;
  }

  public int getDisableReason()
  {
    if (mWifiConfig != null)
      return mWifiConfig.disableReason;
    return 0;
  }

  public String getMac()
  {
    if (this.mWifiManager != null)
    {
      DLBLog.d("SWifiManager", "");
      WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
      //DLBLog.d("SWifiManager", localWifiInfo.getMacAddress());
      return localWifiInfo.getMacAddress();
    }
    return "";
  }

  public String getMode()
  {
    String str = "dhcp";
    if ((mWifiConfig != null) && (mWifiConfig.ipAssignment.equals(WifiConfiguration.IpAssignment.STATIC)))
    {
      str = "static";
    }
    if ((mWifiConfig == null) || (!mWifiConfig.ipAssignment.equals(WifiConfiguration.IpAssignment.DHCP)))
    {
      return "dhcp";
    }
    return str;
  }

	public List<AccessPoint> getWifiList()
	{
		ArrayList localArrayList = new ArrayList();
		List localList = mWifiManager.getScanResults();
		Iterator localIterator;
		if (localList != null)
		{
			localIterator = localList.iterator();
		}
		else
		{
			return null;
		}
		while (true)
		{
			ScanResult localScanResult = (ScanResult)localIterator.next();
			localArrayList.add(new AccessPoint(this.ctx, localScanResult));
			if (!localIterator.hasNext())
				break;
			UMDebug.umdebug_trace();
		}
		return localArrayList;
	}

  public WifiNetworkBean getWifiNetWork()
  {
    WifiNetworkBean localWifiNetworkBean = new WifiNetworkBean();
    int i = this.mWifiManager.getDhcpInfo().ipAddress;
    int j = this.mWifiManager.getDhcpInfo().gateway;
    int k = this.mWifiManager.getDhcpInfo().netmask;
    int m = this.mWifiManager.getDhcpInfo().dns1;
    int n = this.mWifiManager.getDhcpInfo().dns2;
    localWifiNetworkBean.setIp(EthernetsTools.intToIp(i));
    localWifiNetworkBean.setGateway(EthernetsTools.intToIp(j));
    localWifiNetworkBean.setMask(EthernetsTools.intToIp(k));
    localWifiNetworkBean.setDns(EthernetsTools.intToIp(m));
    localWifiNetworkBean.setDns2(EthernetsTools.intToIp(n));
    WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
    String str1 = localWifiInfo.getBSSID();
    String str2 = localWifiInfo.getSSID();
    int i1 = localWifiInfo.getLinkSpeed();
    String str3 = localWifiInfo.getMacAddress();
    int i2 = localWifiInfo.getRssi();
    localWifiNetworkBean.setSsid(str2);
    localWifiNetworkBean.setBssid(str1);
    localWifiNetworkBean.setmRssi(i2);
    localWifiNetworkBean.setMACAddr(str3);
    localWifiNetworkBean.setLinkspeed(i1);
    return localWifiNetworkBean;
  }

  public int getWifiState()
  {
    try
    {
      int i = this.mWifiManager.getWifiState();
      return i;
    }
    catch (Exception localException)
    {
    }
    return 4;
  }

  public final int getmCurrentNetworkId()
  {
    return this.mCurrentNetworkId;
  }

  public List<String> haveConfiguration()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return localArrayList;
      localArrayList.add(((WifiConfiguration)localIterator.next()).SSID);
	  UMDebug.umdebug_trace();
    }
  }

  public boolean haveConfiguration(String paramString)
  {
    Iterator localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
    do
    	{
      if (!localIterator.hasNext())
        return false;
	  UMDebug.umdebug_trace();
    	}
    while (!((WifiConfiguration)localIterator.next()).SSID.equals("\"" + paramString + "\""));
    return true;
  }

  public boolean isWifiEnabled()
  {
    return getWifiState() == 3;
  }

  public void openWifi()
  {
    if (!this.mWifiManager.isWifiEnabled())
      this.mWifiManager.setWifiEnabled(true);
    DLBLog.d("=======openWifi==end===");
  }

  public IntentFilter registerBroadcast()
  {
    this.mFilter = new IntentFilter();
    this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    this.mFilter.addAction("android.net.wifi.SCAN_RESULTS");
    this.mFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
    this.mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
    this.mFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
    this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mFilter.addAction("android.net.wifi.RSSI_CHANGED");
    return this.mFilter;
  }

  public void resumeWifiScan()
  {
    if (this.mWifiManager.isWifiEnabled())
    {
      DLBLog.d(".....mScanner.resume()....");
      this.mScanner.resume();
    }
  }

  public void sendMsg(Message paramMessage)
  {
    if (this.mWifiHandler.hasMessages(paramMessage.what))
      this.mWifiHandler.removeMessages(paramMessage.what);
    this.mWifiHandler.sendMessage(paramMessage);
  }

  public void sendMsg(Message paramMessage, int paramInt)
  {
    if (this.mWifiHandler.hasMessages(paramMessage.what))
      this.mWifiHandler.removeMessages(paramMessage.what);
    this.mWifiHandler.sendMessageDelayed(paramMessage, paramInt);
  }

  public void sendMsgToStatusbar(Message paramMessage)
  {
    if (this.mWifiHandler.hasMessages(2))
      this.mWifiHandler.removeMessages(2);
    this.mWifiHandler.sendMessage(paramMessage);
  }

  public boolean setMode(String paramString)
  {
    if (mWifiConfig != null)
    {
      if ((paramString != null) && (!paramString.equals("")))
      {
        if (paramString.equals("dhcp"))
        {
        	mWifiConfig.ipAssignment = WifiConfiguration.IpAssignment.DHCP;
        }else{
        	mWifiConfig.ipAssignment = WifiConfiguration.IpAssignment.STATIC;
        	DLBLog.d("mWifiConfig.ipAssignment==="+mWifiConfig.ipAssignment);
        	return true;
        }
//        for (mWifiConfig.ipAssignment = WifiConfiguration.IpAssignment.DHCP; ; mWifiConfig.ipAssignment = WifiConfiguration.IpAssignment.STATIC)
      }
      DLBLog.d("...please input right mode....");
      return false;
    }
    DLBLog.d("...mWifiConfig..error...");
    return false;
  }

  public void setNetWork(WifiNetworkBean paramWifiNetworkBean)
  {
    if (mNetwork != null)
    {
      WifiNetworkSetting localWifiNetworkSetting = new WifiNetworkSetting();
      int i = getNetmaskLength(paramWifiNetworkBean.getMask());
      DLBLog.d("......iMaskPre=" + i);
      localWifiNetworkSetting.setIP(paramWifiNetworkBean.getIp(), i);
      localWifiNetworkSetting.setGateway(paramWifiNetworkBean.getGateway());
      localWifiNetworkSetting.setDns(paramWifiNetworkBean.getDns());
      localWifiNetworkSetting.setDns(paramWifiNetworkBean.getDns2());
      mWifiConfig.linkProperties = localWifiNetworkSetting.getmLinkProperties();
    }
  }

  public boolean setPassword(String paramString)
  {
  	if(paramString != null && !paramString.equals(""))
	{
		this.mWifiPassword = paramString;
		return true;
	}
	return false;
  }

	public boolean setSSID(String paramString)
	{
		UMDebug.d("SWifiManager", "...>>>>>>>>>>ssid=" + paramString);
		boolean bool = false;
		if (mWifiConfig != null)
		{
			UMDebug.d("SWifiManager", ">>mWifiConfig!=null");
			if ((paramString != null) && (!paramString.equals("")))
			{
				List<AccessPoint> localList = getWifiList();
				bool = false;
				if (localList != null)
				{
					for(int i=0;i<localList.size();i++)
					{
						AccessPoint localAccessPoint = (AccessPoint)localList.get(i);
						if(localAccessPoint.getSsid().equals(paramString))
						{
							mCurrentAccessPoint = localAccessPoint;
							mWifiConfig.SSID = AccessPoint.convertToQuotedString(paramString);
							DLBLog.d(">>>>>>>>>>>>ssid=" + mWifiConfig.SSID);
							bool = true;
							break;
						}
					}
				}
			}
		}
		DLBLog.d(">>...>>>>>>>>>>ssid=" + mWifiConfig.SSID);
		return bool;
	}

  public void startWifiScan()
  {
    if (this.mWifiManager.isWifiEnabled())
    {
      DLBLog.d(".....mScanner.forceScan()....");
      this.mScanner.forceScan();
    }
  }

  public void stopWifiScan()
  {
    if (this.mWifiManager.isWifiEnabled())
    {
      DLBLog.d(".....mScanner.pause()....");
      this.mScanner.pause();
    }
  }

  public void unRegisterBroadcast()
  {
    this.mFilter = null;
  }

  public boolean wifiIsConnect()
  {
    return ((ConnectivityManager)this.ctx.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
  }

  private class Scanner extends Handler
  {
    private int mRetry = 0;

    private Scanner()
    {
    }

    void forceScan()
    {
      removeMessages(0);
      sendEmptyMessage(0);
    }

    public void handleMessage(Message paramMessage)
    {
      if (UMWifiManager.this.mWifiManager.startScan())
        this.mRetry = 0;
      do
      {
        sendEmptyMessageDelayed(0, 10000L);
      }
      while (this.mRetry++ < 3);
      this.mRetry = 0;
	  return;
    }

    void pause()
    {
      this.mRetry = 0;
      removeMessages(0);
    }

    void resume()
    {
      if (!hasMessages(0))
        sendEmptyMessage(0);
    }
  }

  public static class Status
  {
    public static final int CURRENT = 0;
    public static final int DISABLED = 1;
    public static final int ENABLED = 2;
    public static final String[] strings = { "current", "disabled", "enabled" };
  }
}