package com.um.dvbstack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class Status {
	private final static String TAG = "Irdca-Status";

	private StatusHandler handler = new StatusHandler(Looper.getMainLooper());
	private DVB mDVB = null;
	private static Status mInstance = null;

	private List<StatusListener> mStatusListenerList = new ArrayList<StatusListener>();

	private Status() {

	}

	public interface StatusListener {
		void OnMessage(Message msg);
	}

	public boolean addStatusListener(StatusListener ls) {
		if (ls == null)
			return false;

		if (mStatusListenerList.contains(ls)) {
			return false;
		}
		mStatusListenerList.add(ls);
		return true;
	}

	public void removeStatusListener(StatusListener ls) {
		if (ls == null)
			return;

		mStatusListenerList.remove(ls);
	}

	public void startListen(DVB dvb) {
		WeakReference<Status> obj = new WeakReference<Status>(this);
		startListnerStatus(obj, dvb.mNativeContext, true);
		mDVB = dvb;
	}

	public void stopListen() {
		if (mDVB != null) {
			WeakReference<Status> obj = new WeakReference<Status>(this);
			startListnerStatus(obj, mDVB.mNativeContext, false);
		}
		mStatusListenerList.clear();
		Log.v("Status", "dettach context");
	}

	synchronized public static Status getInstance() {
		if (mInstance == null) {
			mInstance = new Status();
		}
		return mInstance;
	}

	private static byte[] AllocData(int len) {
		return new byte[len];
	}

	private static void SendMessage(Object statusRef, int type, int param,
			byte[] data, int len) {
		Log.i("Status", "Send message data!");
		Status status = (Status) ((WeakReference<?>) statusRef).get();
		if (status == null) {
			return;
		}

		synchronized (status) {
			if (status.handler != null) {
				Message msg = status.handler.obtainMessage(type, param, len,
						data);

				status.handler.sendMessage(msg);
				Log.i("Status", "handler Send message data!");
			}
		}
	}

	private static void SendMessage(Object statusRef, int type, int param1,
			int param2) {
		Status status = (Status) ((WeakReference<?>) statusRef).get();
		if (status == null) {
			return;
		}

		synchronized (status) {
			if (status.handler != null) {
				Message msg = status.handler
						.obtainMessage(type, param1, param2);

				status.handler.sendMessage(msg);
			}
		}
	}

	private native void startListnerStatus(Object status_this, int dvbHandle,
			boolean flag);

	private class StatusHandler extends Handler {

		StatusHandler(Looper lp) {
			super(lp);
		}

		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage!");
			if (msg.what == 0) {
				System.exit(1);
				return;
			}

			for (StatusListener ls : mStatusListenerList) {
				if (ls != null) {
					ls.OnMessage(msg);
				}
			}
		}
	}
}
