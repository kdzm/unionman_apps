package com.unionman.settingwizard.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkUtils {
	// 网络连接判断
	// 判断是否有网络
	public static boolean isNetworkAvailable(Context context) {
		return networkAvailable(context);
	}

	// 判断以太网络是否可用
	public static boolean isEthernetDataEnable(Context context) {
		try {
			return ethernetDataEnable(context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 判断wifi网络是否可用
	public static boolean isWifiDataEnable(Context context) {
		try {
			return wifiDataEnable(context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    /**
     * 判断是否有网络连接
     */
    private static boolean networkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            Log.w("", "couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isAvailable() && info[i].isConnected() ) {
                        Log.d("", "network is available");
                        return true;
                    }
                }
            }
        }
        Log.d("", "network not available");
        return false;
    }



    /**
     * 判断以太网络是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    private static boolean ethernetDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isEthernetDataEnable = false;

        isEthernetDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_ETHERNET).isConnectedOrConnecting();

        return isEthernetDataEnable;
    }

    /**
     * 判断wifi 是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    private static boolean wifiDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }

}
