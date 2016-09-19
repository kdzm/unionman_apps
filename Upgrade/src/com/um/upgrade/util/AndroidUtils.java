package com.um.upgrade.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.ServiceManager;
import android.net.InterfaceConfiguration;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import android.os.SystemProperties;

public class AndroidUtils {
	private static int sdkVersion;
	static {
		try {
			sdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
		} catch (Exception ex) {
		}
	}

	/** Device support the froyo (Android 2.2) APIs */
	public static boolean isAndroid22() {
		return sdkVersion >= 8;
	}

	/** Device supports the Honeycomb (Android 3.0) APIs */
	public static boolean isAndroid30() {
		return sdkVersion >= 11;
	}

	/**
	 * Test if this device is a Google TV.
	 * 
	 * See 32:00 in "Google I/O 2011: Building Android Apps for Google TV"
	 * http://www.youtube.com/watch?v=CxLL-sR6XfM
	 * 
	 * @return true if google tv
	 */
	public static boolean isGoogleTV(Context context) {
		final PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature("com.google.android.tv");
	}

	public static boolean hasTelephony(Context context) {
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	public static boolean isConnectedToWifi(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifi != null && wifi.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isConnectedToWired(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo ethernet = connec.getNetworkInfo(9);

		if (ethernet != null && ethernet.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isEthernetWired() {
		if (getHwAddress("wlan0").equals("00:00:00:00:00:00")) {
			return true;
		}
		return false;
	}

	public static boolean isConnectedRoaming(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = connec
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mobile != null && mobile.isConnected() && mobile.isRoaming()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getHwAddress(String iface) {
		try {
			IBinder b = ServiceManager
					.getService(Context.NETWORKMANAGEMENT_SERVICE);
			INetworkManagementService mNManager = INetworkManagementService.Stub
					.asInterface(b);
			InterfaceConfiguration config = mNManager.getInterfaceConfig(iface);
			Class<?> clazz_ifconfig = config.getClass();
			;
			String hwAddr;
			if (sdkVersion > 15) {
				Method method_getHW = clazz_ifconfig
						.getMethod("getHardwareAddress");
				hwAddr = (String) method_getHW.invoke(config);
			} else {
				Field field_hwAddr = clazz_ifconfig.getDeclaredField("hwAddr");
				hwAddr = (String) field_hwAddr.get(config);
			}
			return hwAddr;
		} catch (Exception e) {
			e.printStackTrace();
			return "00:00:00:00:00:00";
		}
	}

	public static boolean appExists(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

    public static String getInterfaceIpAddress(String iface){
        String ip;
        try{
            IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
            INetworkManagementService netd = INetworkManagementService.Stub.asInterface(b);
            InterfaceConfiguration config = netd.getInterfaceConfig(iface);
            ip = config.getLinkAddress().getAddress().getHostAddress();
        }catch(Exception e){
            ip = "0.0.0.0";
        }
        return ip;
    }

    public static boolean isPppConnected(){
        return !"0.0.0.0".equals(getInterfaceIpAddress("ppp0"));
    }

    public static boolean isPppActive(){
        String isActive = SystemProperties.get("net.pppoe.active", "false");
        return isActive.equals("true");
    }

}
