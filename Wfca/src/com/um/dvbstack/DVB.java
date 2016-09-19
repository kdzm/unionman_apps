package com.um.dvbstack;

import android.util.Log;

import java.lang.ref.WeakReference;

import android.os.IBinder;
import android.util.Log;
import com.unionman.jazzlib.*;

public class DVB {
	private final static String TAG = "DVB";

	private Tuner mTuner;
	private DvbStackSearch mSearch;
	private ProgManage mProgManage;
	public int mNativeContext; // accessed by native methods
	static DVB mInstance = null;

	static {
		System.loadLibrary("wfcajni");
		native_init();

	}
	
	private DVB() {
		
	}
	
	synchronized public static DVB GetInstance() {
		if (mInstance == null) {
			mInstance = new DVB();
			mInstance.create();
		}
		return mInstance;
	}

	public void create() {
		native_setup(new WeakReference<DVB>(this));
		InitSubObjects();
	}
	
	public void release() {
		Status.getInstance().stopListen();
		native_release();
	}

	private void InitSubObjects() {
		mTuner = Tuner.GetInstance(this);
		mSearch = new DvbStackSearch(this);
		// mProgManage = ProgManage.GetInstance();
		Status.getInstance().startListen(this);
	}

	public Tuner GetTunerInstance() {
		return mTuner;
	}

	protected void finalize() {
		Log.d(TAG, "DVB finalize");
		native_finalize();
	}

	public static boolean isServerAlive() {
		IBinder binder = ServiceManager.checkService("dvbstack");
		return (binder != null) && binder.pingBinder();
	}

	private static native final void native_init();

	private native final void native_setup(Object dvb_this);

	private native void native_release();

	private native final void native_finalize();

	private native final void native_enableStatusListener(boolean enable);

}
