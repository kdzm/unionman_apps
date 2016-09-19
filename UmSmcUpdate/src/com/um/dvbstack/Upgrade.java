package com.um.dvbstack;

public class Upgrade {



	private final static String TAG = "UPGRADE";
	public  int mNativeContext; // accessed by native methods

	public Upgrade(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;
	}	
	
	public final native int UpgradeStart();
	public final native int UpgradeProcess(int type,int bupgrade,int freq,int symbol,int qam, int pid);

}
