package com.unionman.dvbserver;

import com.unionman.jazzlib.SystemProperties;

public class DvbServerManager {
	private static DvbServerManager mInstance = null;
	
	static {
		System.loadLibrary("dvbservermanager_jni");
	}
	
	private DvbServerManager() {
		
	}
	
	synchronized public static DvbServerManager getInstance() {
		if (mInstance == null) {
			mInstance = new DvbServerManager();
		}
		return mInstance;
	}
	
	public void killService() {
		native_killService();
	}
	
	public void restartService() {
		native_killService();
		SystemProperties.set("ctl.start", "dvbserver");
	}
	
	public String getServiceStatus() {
		return SystemProperties.get("init.svc.dvbserver");
	}
	
	private native void native_killService();
}
