package com.unionman.gettoken;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class Network {
	public static String GetIpAddress(String dev) {
		String ipaddress;
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface intf = netInterfaces.nextElement();
				if (intf.getName().equals(dev)) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::")) {// ignore ipv6
								Log.i("UMAuthZTE", dev + " [" + ipaddress + "]");
								return ipaddress;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0.0.0.0";
	}

	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		NetworkInfo[] nis = cm.getAllNetworkInfo();
		if (nis == null)
			return false;
		for (NetworkInfo ni : nis) {
			Log.v("UMAuthZTE", "Network Name:" + ni.getTypeName() + " State:" + ni.getState());
			if (ni.getState() == NetworkInfo.State.CONNECTED)
				return true;
		}
		return false;
	}
}
