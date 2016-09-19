package com.unionman.settings.wifi;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.util.Log;

public class WifiTools_API17
{
	public static void connect(WifiManager paramWifiManager, int paramInt)
	{
		paramWifiManager.connect(paramInt, new WifiManager.ActionListener()
		{
			public void onFailure(int paramAnonymousInt)
			{
				Log.i("insertEPG", "insertEPG connect onFailure,arg0=" + paramAnonymousInt);
			}

			public void onSuccess()
			{
				Log.i("insertEPG", "insertEPG connect onSuccess");
			}
		});
	}

	public static void forget(WifiManager paramWifiManager, int paramInt)
	{
		paramWifiManager.forget(paramInt, new WifiManager.ActionListener()
		{
			public void onFailure(int paramAnonymousInt)
			{
				Log.i("insertEPG", "insertEPG forget onFailure,arg0=" + paramAnonymousInt);
			}

			public void onSuccess()
			{
				Log.i("insertEPG", "insertEPG forget onSuccess");
			}
		});
	}
}
