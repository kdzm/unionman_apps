package com.unionman.main;

public class PlatformInfo {
	public static final String PI_FIBER = "com.fonsview.tvlauncher";
	public static final String PI_HUAWEI = "com.huawei.stb.tm";
	public static final String PI_ZTE = "com.zte.iptvclient.android.launcher_sc";

	private String curPlatform;
	

	public PlatformInfo() {
		this.curPlatform = PI_FIBER;
		this.isAlways = false;
	}

	public String getCurPlatform() {
		return curPlatform;
	}

	public void setCurPlatform(String curPlatform) {
		this.curPlatform = curPlatform;
	}

	private boolean isAlways;

	public boolean isAlways() {
		return isAlways;
	}

	public void setAlways(boolean isAlways) {
		this.isAlways = isAlways;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "platform info : isAlways = [" + isAlways
				+ "],current Platform = [" + this.curPlatform + "]";
	}

}
