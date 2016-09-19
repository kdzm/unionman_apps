package com.um.cpelistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;

public class UpLoadLog {

	public static final int UPLOAD_SYSLOG_RUNNING = 1000;
	public static final int UPLOAD_SYSLOG_STOPED = 1001;

	private static final String TAG = "UpLoadLog";
	private static final String mMac = SystemProperties.get("ro.mac");
	private static final String mSoftWareVersion = SystemProperties.get("ro.build.version.incremental");
	private static final String mProductModel = SystemProperties.get("ro.product.model");
	private LooperThread mLooperThread;
	private Handler mHandler;
	private DatagramSocket mDatagramSocket;
	private Runnable mSysLogRunnable;
	private boolean mReload = false;
	private String[] mServerAddr;
	private int mLogType = 0;
	private int mLogLevel = 0;
	private int mOutPutType = 0;
	private long mStartTime = 0;
	private long mEndTime = 0;
	private int mState = UPLOAD_SYSLOG_STOPED;

	public UpLoadLog() {
		if (mSysLogRunnable != null)
			return;
		mSysLogRunnable = initRunnable();
	}

	public void sendUDPLog() {
		if (mSysLogRunnable == null)
			mSysLogRunnable = initRunnable();
		if (mLooperThread == null) {
			mLooperThread = new LooperThread();
			mLooperThread.start();
		}
		if (mDatagramSocket == null) {
			try {
				mDatagramSocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
				return;
			}
		}
		mHandler.removeCallbacks(mSysLogRunnable);
		mHandler.post(mSysLogRunnable);
		/*
		 * long delay = mStartTime - new Date().getTime(); if (delay < 0) delay
		 * = 0; mHandler.postDelayed(mSysLogRunnable, delay);
		 */
	}

	/**
	 * @param 日志类型
	 *            [0:不过滤,输出所有类型];16:操作日志;(17:运行日志);19:安全日志;20:用户日志
	 * @param 日志级别
	 *            [0:不过滤,输出所有];3:输出Error;6:输出Info;7:输出Debug
	 * @param msg
	 *            输出的信息
	 * @return
	 */
	private String buildUDPMessage(String msg) {
		String line = null;
		String[] dateArray = null;
		BufferedReader bufferedReader = null;
		if (new Date().getTime() < getStartTime())
			return null;
		StringBuffer sb = new StringBuffer();
		// sb.append("<143>");// <X> ＝ 日志类型×8 ＋ 日志级别 = 17x8 + 7
		sb.append("<" + (getLogType() * 8 + getLogLevel()) + ">");
		try {
			// Sat Jun 4 22:18:49 JST 2016
			Process date = Runtime.getRuntime().exec(new String[] { "date" });
			bufferedReader = new BufferedReader(new InputStreamReader(date.getInputStream()));
			if ((line = bufferedReader.readLine()) != null) {
				dateArray = line.split("\\s+");
			}
			dateArray[2] = (dateArray[2].length() > 1) ? dateArray[2] : ("0" + dateArray[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append(dateArray[1] + " " + dateArray[2] + " " + dateArray[3] + " ");
		sb.append(mMac + " ");
		sb.append(mProductModel + mSoftWareVersion + " ");
		sb.append(msg + "\n");
		// "<143>Jun 03 18:21:26 aa:bb:cc:dd:ee:ff UNT400B7_ZJ 202.173.12.88
		// connect rtsp://202.173.4.88/mov/test.ts timeout\n";
		return sb.toString();
	}

	private void sendToUDPServer(String[] serverAddr, String msg) {
		if (msg == null)
			return;
		InetAddress ip = null;
		int port = 514; // 默认端口514
		try {
			ip = InetAddress.getByName(serverAddr[0]);
			port = Integer.parseInt(serverAddr[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		DatagramPacket p = new DatagramPacket(msg.getBytes(), msg.length(), ip, port);
		try {
			mDatagramSocket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable initRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				Process logcatProcess = null;
				BufferedReader bufferedReader = null;
				String[] logcmd = null;
				String msg = null;
				String line = null;
				int level = 0;
				mHandler.removeCallbacks(this);
				setState(UPLOAD_SYSLOG_RUNNING);
				level = getLogLevel();
				switch (level) {
					case 3: // Error
						logcmd = new String[] { "logcat", "-s", "*:E" };
						break;
					case 6: // Info
						logcmd = new String[] { "logcat", "-s", "*:I" };
						break;
					case 7: // Debug
						logcmd = new String[] { "logcat", "-s", "*:D" };
						break;
					default:
						logcmd = new String[] { "logcat" };
						break;
				}
				Log.d(TAG, "Begin to execute logcat");
				try {
					// Runtime.getRuntime().exec(new String[] { "logcat", "-c" });
					logcatProcess = Runtime.getRuntime().exec(logcmd);
					bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));

					while ((line = bufferedReader.readLine()) != null) {
						if (SystemClock.elapsedRealtime() > getEndTime()) {
							Runtime.getRuntime().exec(new String[] { "logcat", "-c" });
							msg = buildUDPMessage("===>>>  SysLog send finish!  <<<===");
							sendToUDPServer(getServerAddr(), msg);
							mDatagramSocket.close();
							mDatagramSocket = null;
							mReload = false;
							setState(UPLOAD_SYSLOG_STOPED);
							Log.d(TAG, "SysLog send finish!");
							break;
						}
						if (mReload) {
							mReload = false;
							mHandler.postDelayed(this, 300);
							break;
						}
						if((!line.contains("UserID")) || (!line.contains("Password")) ){//去除日志包含账号和密码信息
							msg = buildUDPMessage(line);
							sendToUDPServer(getServerAddr(), msg);
						}
						//Log.d(TAG,"send msg="+msg);
						// Thread.sleep(20);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (logcatProcess != null)
						logcatProcess.destroy();
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Exception e) {};
					}
				}
			}
		};
	}

	public String[] getServerAddr() {
		return mServerAddr;
	}

	public UpLoadLog setServerAddr(String addr) {
		Log.d(TAG, "set SysLog server[ " + addr + " ]");
		mServerAddr = addr.split(":");// 192.168.1.1:5555
		return this;
	}

	public int getState() {
		return mState;
	}

	public UpLoadLog setState(int state) {
		mState = state;
		return this;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public UpLoadLog setStartTime(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
		long starttime = 0;
		try {
			starttime = format.parse(time).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mStartTime = starttime;
		Log.d(TAG, "mStartTime=" + mStartTime + ", mStartTime=" + time + ", Now=" + new Date().getTime());
		return this;
	}

	public long getEndTime() {
		return mEndTime;
	}

	public UpLoadLog setEndTime(long timeout) {
		mEndTime = SystemClock.elapsedRealtime() + timeout * 60 * 1000;
		return this;
	}

	public int getLogType() {
		return mLogType;
	}

	public UpLoadLog setLogType(int type) {
		mLogType = type;
		return this;
	}

	public int getLogLevel() {
		return mLogLevel;
	}

	public UpLoadLog setLogLevel(int level) {
		if (mLogLevel != level) {
			mLogLevel = level;
			if (getState() == UPLOAD_SYSLOG_RUNNING)
				mReload = true;
		}
		return this;
	}

	public int getOutPutType() {
		return mOutPutType;
	}

	public UpLoadLog setOutPutType(int type) {
		mOutPutType = type;
		return this;
	}

	private class LooperThread extends Thread {
		public void run() {
			Looper.prepare();
			mHandler = new MyHandler();
			Looper.loop();
		}
	};

	private static class MyHandler extends Handler {
		public void handleMessage(Message msg) {
		}
	}
}
