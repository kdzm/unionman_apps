package com.json.helper;

import android.util.Log;

/**
 * JsonHelper日志类
 * 
 * @author Eniso
 */
public class JsonHelperLog {
	private static String TAG = "UMAuthHW";
	private static boolean DEBUG = true;
	private static int DEBUGLEVEL = 4;

	public static void v(Object obj, String msg) {
		if (!DEBUG || DEBUGLEVEL < 4)
			return;
		Log.v(TAG, obj.getClass().getName() + " @ [ " + msg + " ]");
	}

	public static void i(Object obj, String msg) {
		if (!DEBUG || DEBUGLEVEL < 3)
			return;
		Log.i(TAG, obj.getClass().getName() + " @ [ " + msg + " ]");
	}

	public static void d(Object obj, String msg) {
		if (!DEBUG || DEBUGLEVEL < 2)
			return;
		Log.d(TAG, obj.getClass().getName() + " @ [ " + msg + " ]");
	}

	public static void w(Object obj, String msg) {
		if (!DEBUG || DEBUGLEVEL < 1)
			return;
		Log.w(TAG, obj.getClass().getName() + " @ [ " + msg + " ]");
	}

	public static void e(Object obj, String msg) {
		if (!DEBUG || DEBUGLEVEL < 0)
			return;
		Log.e(TAG, obj.getClass().getName() + " @ [ " + msg + " ]");
	}
}
