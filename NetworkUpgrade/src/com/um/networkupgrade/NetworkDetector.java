package com.um.networkupgrade;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkDetector {
	
	private Context mContext = null;
	
	public NetworkDetector(Context context) {
		this.mContext = context;
	}
	
	public boolean NetwrokCheck() {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (cm != null) {
			NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
			if (networkInfo != null) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		
		return false;
		
	}
	
}
