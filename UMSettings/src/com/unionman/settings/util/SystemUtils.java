package com.unionman.settings.util;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;


public class SystemUtils {
	private final static String TAG = "SystemUtil";
	private final static UMSocketClient mConfigServerSocket = new UMSocketClient();
	
	public static boolean rebootForInitSystem(Context context) {
		boolean result = false;
		Log.v(TAG, "rebootForInitSystem()");
		UMSocketClient socketClietn = new UMSocketClient(); 
		socketClietn.writeMess("reset");
		result = socketClietn.readNetResponseSync();
		if (result) {
			context.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
		}
        return result;
	}
	
	public static boolean removeDirAndFile(String path) {
		Log.v(TAG, "removeDirAndFile: " + path);
		return shellExecute("busybox rm -rf " + path);
	}
	
	
	public static boolean chmod(String path, String mode) {
		Log.v(TAG, "chmod: " + path + ", " + mode);
		return shellExecute("chmod " + mode + " " + path);
	}
	
	public static boolean shellExecute(String cmd) {
		UMSocketClient socketClietn = new UMSocketClient(); 
		socketClietn.writeMess("system " + cmd);
		boolean result = socketClietn.readNetResponseSync();
		if (!result) {
			Log.e(TAG, "shellExecute("+cmd+") failed.");
		}
		return result;
	}
	
	public static void suspendSystem(Context context) {
		//sendKeyEvent(KeyEvent.KEYCODE_POWER);
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		powerManager.goToSleep(SystemClock.uptimeMillis());
	}
	
	public static void setBcbForBootRecovery(boolean bootRecovey) {
		/*
		if (bootRecovey) {
			mConfigServerSocket.writeMess("init_system");
		} else {
			mConfigServerSocket.writeMess("clear_bcb");
		}
		mConfigServerSocket.readNetResponseSync();
		*/
	}
	
	public static void enableKeyDispatch(boolean enable) {
		
	}
	
	private static void injectKeyEvent(KeyEvent event) {
        Log.i(TAG, "injectKeyEvent: " + event);
        InputManager.getInstance().injectInputEvent(event,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
	
	public static void sendKeyEvent(int keyCode) {
        long now = SystemClock.uptimeMillis();
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
    }
	
	public static void sync() {
		shellExecute("sync");
	}
}
