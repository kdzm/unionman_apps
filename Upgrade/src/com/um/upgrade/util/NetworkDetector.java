package com.um.upgrade.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkDetector {
    private static NetworkDetector mInstance = null;
	private Context mContext = null;
	
	private NetworkDetector(Context context)
	{
		this.mContext = context;
	}

	public boolean NetwrokCheck()
	{
		ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(cm!=null)
		{
			NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
			if(networkInfo!=null)
			{
				for(int i=0;i<networkInfo.length;i++)
				{
					if(networkInfo[i].getState()==NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

    public static NetworkDetector getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkDetector(context);
        }
        return mInstance;
    }
}
