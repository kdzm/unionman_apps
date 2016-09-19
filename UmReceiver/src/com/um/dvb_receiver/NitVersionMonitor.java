package com.um.dvb_receiver;

import com.um.dvbstack.Status;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import com.unionman.jazzlib.*;
import android.util.Log;

public class NitVersionMonitor implements Status.StatusListener {
	private int mLocalNitVer;
	private final static String TAG = "NitVersionMonitor";
	private OnNitChangeListener mLs = null;
	private String PROP_NAME_SYS_NIT_VER = "";
	private String PROP_NAME_LOCAL_NIT_VER = "";
	
	public NitVersionMonitor(OnNitChangeListener ls) {
		mLs = ls;
		mLocalNitVer = -1;
		String fendType = SystemProperties.get("sys.dvb.frontend.type", "-1");
		if (fendType.equals("1")) {
			PROP_NAME_SYS_NIT_VER = "sys.dvb.sat.nit.version";
			PROP_NAME_LOCAL_NIT_VER = "runtime.dvb.sat.nit.version";
		} else if (fendType.equals("2")) {
			PROP_NAME_SYS_NIT_VER = "sys.dvb.dvbc.nit.version";
			PROP_NAME_LOCAL_NIT_VER = "runtime.dvb.dvbc.nit.version";
		} else if (fendType.equals("3")) {
			PROP_NAME_SYS_NIT_VER = "sys.dvb.dtmb.nit.version";
			PROP_NAME_LOCAL_NIT_VER = "runtime.dvb.dtmb.nit.version";
		} else {
			PROP_NAME_SYS_NIT_VER = "sys.dvb.nit.version";
			PROP_NAME_LOCAL_NIT_VER = "runtime.dvb.nit.version";
		}
		Log.v(TAG, "PROP_NAME_SYS_NIT_VER="+PROP_NAME_SYS_NIT_VER);
	}
	
	public interface OnNitChangeListener {
		void onNitChange(int newVer);
	}
	
	public void start() {
		int sysVer = SystemProperties.getInt(PROP_NAME_SYS_NIT_VER, -1);
		mLocalNitVer = SystemProperties.getInt(PROP_NAME_LOCAL_NIT_VER, -1);
		if (mLocalNitVer != sysVer) {
			mLocalNitVer = sysVer;
			SystemProperties.set(PROP_NAME_LOCAL_NIT_VER, Integer.toString(mLocalNitVer));
			if (mLocalNitVer != -1) {
				if (mLs != null) {
					//mLs.onNitChange(mLocalNitVer);
				}
			}
		}
		
		Status.getInstance().addStatusListener(this);
	}
	
	public void stop() {
		Status.getInstance().removeStatusListener(this);
	}
	
	public void reset() {
		SystemProperties.set(PROP_NAME_LOCAL_NIT_VER, "-1");
		mLocalNitVer = -1;
	}
	
	@Override
	public void OnMessage(Message msg) {
		// TODO Auto-generated method stub
		if (msg.what == 7 && msg.arg1 == 7) {
			Log.v(TAG, "new Nit version: " + msg.arg2);
			if (mLocalNitVer != msg.arg2) {
				mLocalNitVer = msg.arg2;
				SystemProperties.set(PROP_NAME_LOCAL_NIT_VER, Integer.toString(mLocalNitVer));
				
				if (mLs != null) {
					/* 去掉NIT节目更新功能 */
					Log.v(TAG, "nit chagned, ready to call onNitChange.");
					//mLs.onNitChange(mLocalNitVer);
				}
			}
		}
	}
}
