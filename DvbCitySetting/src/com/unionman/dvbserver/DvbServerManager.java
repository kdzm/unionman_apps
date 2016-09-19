package com.unionman.dvbserver;

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
	
	private native void native_killService();
}
