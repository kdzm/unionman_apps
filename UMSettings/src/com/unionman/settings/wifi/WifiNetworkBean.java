package com.unionman.settings.wifi;

public class WifiNetworkBean
{
  private String MACAddr;
  private String bssid;
  private String dns;
  private String dns2;
  private String gateway;
  private String ip;
  private int linkspeed;
  private int mRssi;
  private String mask;
  private String ssid;

  public String getBssid()
  {
    return this.bssid;
  }

  public String getDns()
  {
    return this.dns;
  }

  public String getDns2()
  {
    return this.dns2;
  }

  public String getGateway()
  {
    return this.gateway;
  }

  public String getIp()
  {
    return this.ip;
  }

  public int getLinkspeed()
  {
    return this.linkspeed;
  }

  public String getMACAddr()
  {
    return this.MACAddr;
  }

  public String getMask()
  {
    return this.mask;
  }

  public String getSsid()
  {
    return this.ssid;
  }

  public int getmRssi()
  {
    return this.mRssi;
  }

  public void setBssid(String paramString)
  {
    this.bssid = paramString;
  }

  public void setDns(String paramString)
  {
    this.dns = paramString;
  }

  public void setDns2(String paramString)
  {
    this.dns2 = paramString;
  }

  public void setGateway(String paramString)
  {
    this.gateway = paramString;
  }

  public void setIp(String paramString)
  {
    this.ip = paramString;
  }

  public void setLinkspeed(int paramInt)
  {
    this.linkspeed = paramInt;
  }

  public void setMACAddr(String paramString)
  {
    this.MACAddr = paramString;
  }

  public void setMask(String paramString)
  {
    this.mask = paramString;
  }

  public void setSsid(String paramString)
  {
    this.ssid = paramString;
  }

  public void setmRssi(int paramInt)
  {
    this.mRssi = paramInt;
  }
}