package com.unionman.settingwizard.ui;

import android.os.SystemProperties;

public class DefaultParameter {
	  public static final String STB_SAFEWARE_DEFAULT_VERSION = SystemProperties.get("ro.cursoftware","");
	  public static final String STB_HARDWARE_DEFAULT_VERSION = SystemProperties.get("ro.hardwareversion","");
	  public static final String STB_DEFAULT_URL = "bgw025052.chinaw3.com:80";
	  public static final String STB_VENDOR = "unionman";
      public static final String STB_VENDOR_CODE = "34";
	  public static final String STB_Product_Model = "reviver_alios4.2";
	  public static final int STB_DEFAULT_SERIAL = 10001;
	  public static final int STB_SERIAL_LEN = 17;
	  public static final int STB_MAC_LEN = 12;
}
