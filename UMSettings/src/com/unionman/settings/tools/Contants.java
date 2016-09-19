package com.unionman.settings.tools;

import android.os.SystemProperties;


public class Contants
{
  public static final String EPG_LOGIN = "epg.login";
  public static final String EPG_MOBILE_DEVICEID = "epg.mobile.deviceid";
  public static final String EPG_MOBILE_TOKEN = "epg.mobile.token";
  public static final String EPG_MOBILE_USERID = "epg.mobile.userid";
  public static final String EPG_TOKEN = "epg.token";
  public static final String EPG_USERID = "epg.userid";
  public static final String PERSIST_SYS_TIMEZONE = "persist.sys.timezone";
  public static final String RO_BUILD_VERSION_INCREMENTAL = "ro.build.version.incremental";
  public static final String RO_MAC = "ro.mac";
  public static final String RO_MEDIA_TIMESHIFT = "ro.media.timeshift";
  public static final String RO_PRODUCT_MODEL = "ro.product.model";
  public static final String RO_PRODUCT_NAME = "ro.product.name";
  public static final String RO_SERIALNO = "ro.serialno";
  public static final String SYS_SETTINGS_PWD = "setting_pswd";
//  public static final String SYS_SETTINGS_PWDPROTECTED = "sys.settings.pwdprotected";
  public static final String SYS_SETTINGS_SUPPORT = "sys.settings.support";
  public static final String SYS_SETTINGS_SUPPORT_NETWORK_FLAGS = "sys.settings.support.net.flags";
  public static boolean SYS_SURPORT_ETHERNET = false;
  private static final int SYS_SURPORT_ETHERNET_FLAG = 2;
  public static boolean SYS_SURPORT_PPPOE = false;
  private static final int SYS_SURPORT_PPPOE_FLAG = 4;
  public static boolean SYS_SURPORT_WIFI = false;
  private static final int SYS_SURPORT_WIFI_FLAG = 1;
  public static final String SYS_XMPP_PWD = "xmpp_pswd";
  public static final String SYS_XMPP_SERVER = "xmpp_server";
  public static final String SYS_XMPP_USER = "xmpp_user";
  public static final String DEFAULT_PASSWORD = "0000";
  public static final String KEY_DEFAULT_PASSWORD = "settings_password";
  public static final String KEY_PARENT_CONTROL_PASSWORD = "parent_control_password";
  public static final String KEY_PARENT_CONTROL_STATUS = "parent_control_status";
  public static final String KEY_AUTO_CLOSE_TIME = "auto_close_time";
  public static final String KEY_AUTO_CLOSE_ENABLE = "auto_close_enable";

  static
  {
    SYS_SURPORT_ETHERNET = true;
    SYS_SURPORT_PPPOE = true;
    init();
  }

  private static void init()
  {
	  SYS_SURPORT_WIFI =true;
	  SYS_SURPORT_ETHERNET = true;
	  try
	  {
		  Class.forName("android.net.pppoe.PppoeManager");
		  SYS_SURPORT_PPPOE = true;
	  }
	  catch(ClassNotFoundException localClassNotFoundException)
	  {
		  SYS_SURPORT_PPPOE = false;
	  }
	  return ;
  }
}
