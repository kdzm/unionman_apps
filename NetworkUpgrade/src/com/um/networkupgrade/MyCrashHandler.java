package com.um.networkupgrade;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

public class MyCrashHandler implements UncaughtExceptionHandler {

	private static MyCrashHandler instance; 
	private static String TAG = "NetworkUpgrade--MyCrashHandler";

	private MyCrashHandler() {
	}

	public synchronized static MyCrashHandler getInstance() { 
		if (instance == null) {
			instance = new MyCrashHandler();
		}
		return instance;
	}

	public void init(Context ctx) { 
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) { 
		String threadName = thread.getName();
		Logger.i(TAG, "uncaughtException threadNamethreadName=" + threadName);
	}
}