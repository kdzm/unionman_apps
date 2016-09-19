package com.unionman.settings.content;

public class EthernetsBean
{
  private String strDns = "";
  private String strDns2 = "";
  private String strGateway = "";
  private String strIp = "";
  public int leaseDuration;
  private String strMask = "";
  public String strServerAddress;

  public String getDns()
  {
    return this.strDns;
  }

  public String getDns2()
  {
    return this.strDns2;
  }

  public String getGateway()
  {
    return this.strGateway;
  }

  public String getIp()
  {
    return this.strIp;
  }

  public int getLeaseDuration()
  {
    return this.leaseDuration;
  }

  public String getMask()
  {
    return this.strMask;
  }

  public String getServerAddress()
  {
    return this.strServerAddress;
  }

  public void setDns(String paramString)
  {
    this.strDns = paramString;
  }

  public void setDns2(String paramString)
  {
    this.strDns2 = paramString;
  }

  public void setGateway(String paramString)
  {
    this.strGateway = paramString;
  }

  public void setIp(String paramString)
  {
    this.strIp = paramString;
  }

  public void setLeaseDuration(int paramInt)
  {
    this.leaseDuration = paramInt;
  }

  public void setMask(String paramString)
  {
    this.strMask = paramString;
  }

  public void setServerAddress(String paramString)
  {
    this.strServerAddress = paramString;
  }
}