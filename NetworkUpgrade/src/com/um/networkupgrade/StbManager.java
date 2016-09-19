package com.um.networkupgrade;

import android.content.Context;
import android.os.SystemProperties;

public class StbManager {

	public static final String TAG = "NetWorkUpgrade---StbManager---";
	
	/**
	 * 获取系统属性值
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getSystemProperties(String name,String defaultValue){
		return SystemProperties.get(name, defaultValue);
	}
	
	/**
	 * 设置系统属性值
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static void setSystemProperties(String name,String value){
		SystemProperties.set(name, value);
	}
}
