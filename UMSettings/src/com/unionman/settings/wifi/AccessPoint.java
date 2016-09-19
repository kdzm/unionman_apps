package com.unionman.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import java.util.BitSet;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.UMLogger;
public class AccessPoint extends Preference
{
  private static final String KEY_CONFIG = "key_config";
  private static final String KEY_DETAILEDSTATE = "key_detailedstate";
  private static final String KEY_SCANRESULT = "key_scanresult";
  private static final String KEY_WIFIINFO = "key_wifiinfo";
  public static final int SECURITY_EAP = 3;
  public static final int SECURITY_NONE = 0;
  public static final int SECURITY_PSK = 2;
  public static final int SECURITY_WEP = 1;
  private static UMLogger log = UMLogger.getLogger(AccessPoint.class);
  String bssid;
  private WifiConfiguration mConfig;
  private WifiInfo mInfo;
  public int mRssi;
  ScanResult mScanResult;
  private NetworkInfo.DetailedState mState;
  int networkId = -1;
  PskType pskType = PskType.UNKNOWN;
  public int security;
  String ssid;
  boolean wpsAvailable = false;

  public AccessPoint(Context paramContext, ScanResult paramScanResult)
  {
    super(paramContext);
    loadResult(paramScanResult);
    refresh();
  }

  public AccessPoint(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    super(paramContext);
    loadConfig(paramWifiConfiguration);
    refresh();
  }

  public AccessPoint(Context paramContext, Bundle paramBundle)
  {
    super(paramContext);
    mConfig = ((WifiConfiguration)paramBundle.getParcelable(KEY_CONFIG));
    if (mConfig != null)
      loadConfig(mConfig);
    mScanResult = ((ScanResult)paramBundle.getParcelable(KEY_SCANRESULT));
    if (mScanResult != null)
      loadResult(mScanResult);
    mInfo = ((WifiInfo)paramBundle.getParcelable(KEY_WIFIINFO));
    if (paramBundle.containsKey(KEY_DETAILEDSTATE))
      mState = NetworkInfo.DetailedState.valueOf(paramBundle.getString(KEY_DETAILEDSTATE));
    update(mInfo, mState);
  }

  private int calculateSignalLevel(int paramInt)
  {
    if (paramInt > 0)
      return -1;
    if ((paramInt <= 0) && (paramInt > -50))
      return 3;
    if ((paramInt <= -50) && (paramInt > -60))
      return 2;
    if ((paramInt <= -60) && (paramInt > -70))
      return 1;
    return 0;
  }

  public static String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }

  private String get(Context paramContext, NetworkInfo.DetailedState paramDetailedState)
  {
    return get(paramContext, null, paramDetailedState);
  }

  private String get(Context paramContext, String paramString, NetworkInfo.DetailedState paramDetailedState)
  {
	Resources localResources = paramContext.getResources();
	String[] arrayOfString;
	int j;
	if (paramString == null)
	{
		int i = 2131034112;
		arrayOfString = localResources.getStringArray(i);
		log.d("arrayOfString======"+arrayOfString);
	   j = paramDetailedState.ordinal();
	   log.d("j1111111======"+j);
		if ((j >= arrayOfString.length) || (arrayOfString[j].length() == 0))
		{
		return "";
		}
	}else{
		int i = 2131034113;
		arrayOfString = localResources.getStringArray(i);
		log.d("arrayOfString22222======"+arrayOfString);
		   j = paramDetailedState.ordinal();
		   log.d("j22222222======"+j);
		if ((j >= arrayOfString.length) || (arrayOfString[j].length() == 0))
		{
		return "";
		}
	}
	return String.format(arrayOfString[j], new Object[] { paramString });
  }

  private static PskType getPskType(ScanResult paramScanResult)
  {
    boolean bool1 = paramScanResult.capabilities.contains("WPA-PSK");
    boolean bool2 = paramScanResult.capabilities.contains("WPA2-PSK");
    if ((bool2) && (bool1))
    {
      return PskType.WPA_WPA2;
    }
    if (bool2)
    {
      return PskType.WPA2;
    }
    if (bool1)
    {
      return PskType.WPA;
    }
    log.w("Received abnormal flag string: " + paramScanResult.capabilities);
    return PskType.UNKNOWN;
  }

  private static int getSecurity(ScanResult paramScanResult)
  {
	  if (paramScanResult.capabilities.contains("WEP")) {
          return SECURITY_WEP;
      } else if (paramScanResult.capabilities.contains("PSK")) {
          return SECURITY_PSK;
      } else if (paramScanResult.capabilities.contains("EAP")) {
          return SECURITY_EAP;
      }
      return SECURITY_NONE;
  }

  public static int getSecurity(WifiConfiguration paramWifiConfiguration)
  {
	  if (paramWifiConfiguration.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
          return SECURITY_PSK;
      }
      if (paramWifiConfiguration.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
    		  paramWifiConfiguration.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
          return SECURITY_EAP;
      }
      return (paramWifiConfiguration.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
  }

  private void loadConfig(WifiConfiguration paramWifiConfiguration)
  {
	     ssid = (paramWifiConfiguration.SSID == null ? "" : removeDoubleQuotes(paramWifiConfiguration.SSID));
		bssid = paramWifiConfiguration.BSSID;
		security = getSecurity(paramWifiConfiguration);
		networkId = paramWifiConfiguration.networkId;
		mRssi = Integer.MAX_VALUE;
		mConfig = paramWifiConfiguration;
		UMDebug.d("AccessPoint","ssid="+this.ssid+",bssid="+this.bssid+",security="+this.security+"mRssi===="+mRssi);
  }

	private void loadResult(ScanResult paramScanResult)
	{
		ssid = paramScanResult.SSID;
		bssid = paramScanResult.BSSID;
		security = getSecurity(paramScanResult);
		 wpsAvailable = security != SECURITY_EAP && paramScanResult.capabilities.contains("WPS");
	    if (security == SECURITY_PSK)
	        {
	            pskType = getPskType(paramScanResult);
	        }
		networkId = -1;
		mRssi = paramScanResult.level;
		mScanResult = paramScanResult;
		UMDebug.d("AccessPoint","ssid1="+this.ssid+",bssid2="+this.bssid+",security2="+this.security+"mRssi2===="+mRssi);
	}

  private void refresh()
  {
	setTitle(ssid);
	Context localContext = getContext();
	if (mState != null)
	{
		UMDebug.d("AccessPoint","mState="+this.mState+",text="+ get(localContext, this.mState));
		setSummary(get(localContext, mState));
	}else if (mRssi == Integer.MAX_VALUE){		
		setSummary(localContext.getString(R.string.wifi_not_in_range));
	}else if ((mConfig != null) && (mConfig.status ==5))	{
		UMDebug.d("AccessPoint","status="+ mConfig.status +", disableReason="+ mConfig.disableReason);
		switch (mConfig.disableReason)
		{
		case WifiConfiguration.DISABLED_AUTH_FAILURE:
            setSummary(localContext.getString(R.string.wifi_disabled_password_failure));
            break;
        case WifiConfiguration.DISABLED_DHCP_FAILURE:
        case WifiConfiguration.DISABLED_DNS_FAILURE:
            setSummary(localContext.getString(R.string.wifi_disabled_network_failure));
            break;
        case WifiConfiguration.DISABLED_UNKNOWN_REASON:
           setSummary(localContext.getString(R.string.wifi_disabled_generic));
		}
	}else {
		 StringBuilder summary = new StringBuilder();
         if (mConfig != null) { // Is saved network
             summary.append(localContext.getString(R.string.wifi_remembered));
         }

         if (security != SECURITY_NONE) {
             String securityStrFormat;
             if (summary.length() == 0) {
                 securityStrFormat = localContext.getString(R.string.wifi_secured_first_item);
             } else {
                 securityStrFormat = localContext.getString(R.string.wifi_secured_second_item);
             }
             summary.append(String.format(securityStrFormat, getSecurityString(true)));
         }

         if (mConfig == null && wpsAvailable) { // Only list WPS available for unsaved networks
             if (summary.length() == 0) {
                 summary.append(localContext.getString(R.string.wifi_wps_available_first_item));
             } else {
                 summary.append(localContext.getString(R.string.wifi_wps_available_second_item));
             }
         }
         setSummary(summary.toString());
	}	
  }

	public static String removeDoubleQuotes(String paramString)
	{
		int i = paramString.length();
		if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"'))
			paramString = paramString.substring(1, i - 1);
		return paramString;	
	}

  public int compareTo(Preference paramPreference)
  {
	if (!(paramPreference instanceof AccessPoint))
		return -1;
	AccessPoint localAccessPoint  = (AccessPoint)paramPreference;
	if(this.mInfo == localAccessPoint.mInfo)
		return 1;
	if(this.mRssi != 2147483647)
	{
		if ((this.mRssi ^ localAccessPoint.mRssi) >= 0)
		  return 1;
	}
	if(this.networkId != -1)
	{
		if ((this.networkId ^ localAccessPoint.networkId) >= 0)
	        return 1;
	}
	if(WifiManager.compareSignalLevel(localAccessPoint.mRssi, this.mRssi)!= 0)
		return WifiManager.compareSignalLevel(localAccessPoint.mRssi, this.mRssi);
	
	return this.ssid.compareToIgnoreCase(localAccessPoint.ssid);
  }

  protected void generateOpenNetworkConfig()
  {
    if (security != 0)
      throw new IllegalStateException();
    if (mConfig != null)
      return;
    mConfig = new WifiConfiguration();
    mConfig.SSID = convertToQuotedString(this.ssid);
    mConfig.allowedKeyManagement.set(0);
  }

  public String getBssid()
  {
    return bssid;
  }

  public WifiConfiguration getConfig()
  {
    return mConfig;
  }

  public WifiInfo getInfo()
  {
    return mInfo;
  }

  public int getLevel()
  {
    if (mRssi == Integer.MAX_VALUE)
      return -1;
    return WifiManager.calculateSignalLevel(mRssi,4);
  }

  public int getNetworkId()
  {
    return networkId;
  }

  public PskType getPskType()
  {
    return pskType;
  }

  public int getSecurity()
  {
    return security;
  }

  public String getSecurityString(boolean paramBoolean)
  {
    Context localContext = getContext();
    switch (security)
    {
     case SECURITY_EAP:
        return paramBoolean ? localContext.getString(R.string.wifi_security_short_eap) :
        	localContext.getString(R.string.wifi_security_eap);
     case SECURITY_PSK:
        switch (pskType) {
            case WPA:
                return paramBoolean ? localContext.getString(R.string.wifi_security_short_wpa) :
                	localContext.getString(R.string.wifi_security_wpa);
            case WPA2:
                return paramBoolean ? localContext.getString(R.string.wifi_security_short_wpa2) :
                	localContext.getString(R.string.wifi_security_wpa2);
            case WPA_WPA2:
                return paramBoolean ? localContext.getString(R.string.wifi_security_short_wpa_wpa2) :
                	localContext.getString(R.string.wifi_security_wpa_wpa2);
            case UNKNOWN:
            default:
                return paramBoolean ? localContext.getString(R.string.wifi_security_short_psk_generic)
                        : localContext.getString(R.string.wifi_security_psk_generic);
            }
     case SECURITY_WEP:
        return paramBoolean ? localContext.getString(R.string.wifi_security_short_wep) :
        	localContext.getString(R.string.wifi_security_wep);
      case SECURITY_NONE:
    default:
        return paramBoolean ? "" : localContext.getString(R.string.wifi_security_none);
    
    }
  }

  public String getSsid()
  {
    return ssid;
  }

  public NetworkInfo.DetailedState getState()
  {
    return mState;
  }

  public int getmRssi()
  {
    return mRssi;
  }

  public NetworkInfo.DetailedState getmState()
  {
    return mState;
  }

  public final boolean isWpsAvailable()
  {
    return wpsAvailable;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
  }

  public void saveWifiState(Bundle paramBundle)
  {
	  paramBundle.putParcelable(KEY_CONFIG, mConfig);
	  paramBundle.putParcelable(KEY_SCANRESULT, mScanResult);
	  paramBundle.putParcelable(KEY_WIFIINFO, mInfo);
      if (mState != null) {
    	  paramBundle.putString(KEY_DETAILEDSTATE, mState.toString());
      }
  }


  public void update(WifiInfo paramWifiInfo, NetworkInfo.DetailedState paramDetailedState)
  {
	  boolean reorder = false;
	UMDebug.d("AccessPoint","paramWifiInfo="+paramWifiInfo+",networkId="+ this.networkId);
	UMDebug.d("kekeke","paramDetailedState="+paramDetailedState);
	if ((paramWifiInfo != null)  &&  (networkId != -1)  &&  (networkId == paramWifiInfo.getNetworkId()))
	{
		reorder = (mInfo == null);
		mRssi = paramWifiInfo.getRssi();
		mInfo = paramWifiInfo;
		mState = paramDetailedState;
		refresh();
		UMDebug.d("AccessPoint","mState="+this.mState);
	}else if (mInfo != null) {
        reorder = true;
        mInfo = null;
        mState = null;
        refresh();
    }
	if (reorder){
        notifyHierarchyChanged();
    }
  }

  public void update(AccessPoint paramAccessPoint)
  {
	if ((paramAccessPoint.getSsid().equals(this.ssid)) && ((this.bssid == null) || (paramAccessPoint.getBssid() == null) || (this.bssid.equals(paramAccessPoint.getBssid()))))
	{
		networkId = paramAccessPoint.getNetworkId();
		bssid = paramAccessPoint.getBssid();
		mRssi = paramAccessPoint.getmRssi();
		mState = paramAccessPoint.getmState();
		mConfig = paramAccessPoint.getConfig();
		mInfo = paramAccessPoint.getInfo();
		security = paramAccessPoint.getSecurity();
		wpsAvailable = paramAccessPoint.isWpsAvailable();
		refresh();
	}
  }

  public boolean update(ScanResult paramScanResult)
  {
    if ((ssid.equals(paramScanResult.SSID)) && (security == getSecurity(paramScanResult)))
    {
      if (WifiManager.compareSignalLevel(paramScanResult.level, mRssi) > 0)
      {
        int i = getLevel();
        mRssi = paramScanResult.level;
        if (getLevel() != i)
          notifyChanged();
      }
      if (security == SECURITY_PSK)
      {
        pskType = getPskType(paramScanResult);
      }
      refresh();
      return true;
    }
    return false;
  }

  public static enum PskType
  {
  	UNKNOWN,WPA,WPA2,WPA_WPA2;
  }
}
