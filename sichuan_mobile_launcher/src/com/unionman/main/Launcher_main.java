package com.unionman.main;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.unionman.sichuan_mobile_launcher.R;
import com.unionman.stbconfig.PlatformUtil;
import com.unionman.stbconfig.StbConfigUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;

public class Launcher_main extends Activity {
	private static final String TAG = "LAUNCHER_MAIN";
	private static PlatformInfo platforminfo;
	private static Handler myHandler;
	//private Timer launch_timer;
	private static final int TIME_OUT_TO_SETTING = 8000;
	private static final String PLAT_PROP = "persist.sys.sc.platform";
	public static final String ZTE_PACKAGE = "com.zte.iptvclient.android.launcher_sc";
	public static final String ZTE_LOGIN = "com.zte.iptvclient.android.launcher.activity.LoginActivity";
	public static final String ZTE_LAUNCHER = "com.zte.iptvclient.android.launcher.activity.LauncherActivity";
	private static EthernetManager mEthManager;
	private boolean isLogin = false;
	private Runnable loopCheck;
	private TextView mTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.launcher_main);
		StbConfigUtil.initStbConfig(this);
		platforminfo = PlatformUtil.initPlatformInfo(this);
		mEthManager = (EthernetManager) getApplicationContext()
				.getSystemService(Context.ETHERNET_SERVICE);
		SystemProperties.set("persist.sys.networkchange", "0");
		
		mTextView=(TextView) findViewById(R.id.tv_message);
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0x101:
					Log.d(TAG, "handler recieve:" + (String) msg.obj);
					mTextView.setText("正在启动客户端，请等待...");
					StartThread st = new StartThread((String) msg.obj);
					st.start();
					break;

				case 0x401:
					Log.d(TAG, "time out and start setting");
					StartThread setting = new StartThread("setting");
					setting.start();
					break;

				case 0x701:
					Log.d(TAG, "ipoe connect");
					Toast.makeText(getApplicationContext(), "网络连接成功",
							Toast.LENGTH_SHORT).show();
					break;
				case 0x801:
					launch(getPlatform());
					break;

				default:
					break;
				}
			}
		};
		
	    loopCheck=new Runnable() {
			
			@Override
			public void run() {
				String runningActivity = "";
				runningActivity = getRunningActivityPackageName();
				Log.i(TAG, "Runnable loopCheck");
				Log.i(TAG, runningActivity);
				if(runningActivity.equals("com.zte.iptvclient.android.launcher.activity.LauncherActivity")){
					Log.i(TAG, "isLogined");
					SystemProperties.set("persist.sys.networkchange", "0");
					isLogin = true;
				}else{
					myHandler.postDelayed(loopCheck, 1000);
				}
				
			}
		};
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		mTextView.setText("正在检测网络，请等待，如果长时间未成功，请按设置键");
		String isStarted=SystemProperties.get("dev.bootcomplete", "");
		if("".equals(isStarted)){
			SystemProperties.set("dev.bootcomplete", "1");
			myHandler.postDelayed(loopCheck,3000);
			Timer launch_timer = new Timer();
			launch_timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = myHandler.obtainMessage(0x801);
					Launcher_main.myHandler.sendMessage(msg);
					Log.d(TAG, "launch timer  is up");
				}
			}, 3000);
		}else{
			myHandler.post(loopCheck);
			launch(getPlatform());
		}
		
		Log.i(TAG, "loopCheck");
		/*
		 * platforminfo = PlatformUtil.getPlatformInfo(this); if
		 * (platforminfo.isAlways()) { launch(platforminfo.getCurPlatform());
		 * return; }
		 */
		// createDialog().show();
	}

	private String getPlatform() {
		String pf = PlatformInfo.PI_ZTE;
		String plat_prop = SystemProperties.get(PLAT_PROP, "");
		if ("1".equals(plat_prop)) {
			pf = PlatformInfo.PI_ZTE;
		} else if ("2".equals(plat_prop)) {
			pf = PlatformInfo.PI_HUAWEI;
		} else if ("3".equals(plat_prop)) {
			pf = PlatformInfo.PI_FIBER;
		} else {
			String serial = SystemProperties.get("ro.serialno", null);
			if (serial != null) {
				int pf_num = Integer.parseInt(serial.substring(12, 13));
				switch (pf_num) {
				case 1:
					pf = PlatformInfo.PI_ZTE;
					break;
				case 2:
					pf = PlatformInfo.PI_HUAWEI;
					break;
				case 3:
					pf = PlatformInfo.PI_FIBER;
					break;
				default:
					pf = PlatformInfo.PI_ZTE;
				}
			}
		}
		Log.d(TAG, "getPlatform:" + pf);
		return pf;
	}

	public Dialog createDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.dialogTitle);
		String[] platforms = new String[3];
		platforms[0] = getResources().getString(R.string.plat_fons);
		platforms[1] = getResources().getString(R.string.plat_huawei);
		platforms[2] = getResources().getString(R.string.plat_zte);

		dialogBuilder.setSingleChoiceItems(platforms, 0, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Launcher_main.platforminfo
							.setCurPlatform(PlatformInfo.PI_FIBER);
					break;
				case 1:
					Launcher_main.platforminfo
							.setCurPlatform(PlatformInfo.PI_HUAWEI);
					break;
				case 2:
					Launcher_main.platforminfo
							.setCurPlatform(PlatformInfo.PI_ZTE);
					break;
				}
			}
		});

		dialogBuilder.setPositiveButton(R.string.dialogAlways,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Launcher_main.platforminfo.setAlways(true);
						PlatformUtil.updatePlatformInfo(Launcher_main.this,
								Launcher_main.platforminfo);
						Log.i(TAG, "insert into db for starting ways");
						Launcher_main.this.launch(Launcher_main.platforminfo
								.getCurPlatform());
					}
				});
		dialogBuilder.setNegativeButton(R.string.dialogOneshot,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Launcher_main.platforminfo.setAlways(false);
						PlatformUtil.updatePlatformInfo(Launcher_main.this,
								Launcher_main.platforminfo);
						Log.i(TAG, "insert into db for starting ways");
						Launcher_main.this.launch(Launcher_main.platforminfo
								.getCurPlatform());
					}
				});
		return dialogBuilder.create();
	}

	public void launch(String curPlatform) {

		/*
		 * Log.i(TAG, "now start app:" + curPlatform); Intent start = new
		 * Intent(); if (curPlatform.equals(PlatformInfo.PI_HUAWEI)) {
		 * start.setClassName("com.huawei.stb.tm",
		 * "com.huawei.stb.tm.ui.activity.LoadingActivity"); } else if
		 * (curPlatform.equals(PlatformInfo.PI_FIBER)) {
		 * start.setClassName(PlatformInfo.PI_FIBER,
		 * "com.fonsview.tvlauncher.MainActivity"); } else if
		 * (curPlatform.equals(PlatformInfo.PI_ZTE)) {
		 * Log.i(TAG,"set class name zte ");
		 * start.setClassName("com.zte.iptvclient.android.launcher_sc",
		 * "com.zte.iptvclient.android.launcher.activity.LoginActivity"); }
		 * Log.i(TAG, start.getPackage() + ":" + start.toString()); if (start !=
		 * null) { //startActivity(start); Log.i(TAG,
		 * "now start platform!!!!!!!!!!!!!!!!!!!!!"); return; }
		 */
		// if(start == null){
		// showNoApk();
		// return;
		// }
		class launchThread extends Thread {
			private String cp = null;

			public launchThread(String curPlatform) {
				cp = curPlatform;
			}

			@Override
			public void run() {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				while (true) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {

					}
					Log.i(TAG, "wait for network!!!!!!!!!!!!!!!!!!!!!");
					if (cm == null) {
						Log.i(TAG, "cm is null!!!!!!!!!!!!!!!!!!!!!");
						cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						continue;
					}
					NetworkInfo ni = null;
					try {
						ni = cm.getActiveNetworkInfo();
					} catch (Exception e) {
						e.printStackTrace();
						ni = null;
					} finally {

					}
					if ((ni != null) && (ni.isConnected())) {
						Log.i(TAG, "network is ok!!!!!!!!!!!!!!!!!!!!!,cp is"
								+ cp);
						Log.i(TAG, "ethernetoooo mode is :" + mEthManager.getEthernetMode());
						Log.i(TAG, "connect type is :" + ni.getType());
						if (!"".equals(SystemProperties.get(
								"persist.sys.dhcp.user", ""))
								&& Launcher_main.mEthManager
										.getEthernetMode()
										.equals(EthernetManager.ETHERNET_CONNECT_MODE_DHCP)
								&& (ni.getType() == (ConnectivityManager.TYPE_ETHERNET))) {
							Message msg = myHandler.obtainMessage(0x701);
							Launcher_main.myHandler.sendMessage(msg);
						}
						Message msg = myHandler.obtainMessage(0x101, cp);
						Launcher_main.myHandler.sendMessage(msg);
						//launch_timer.cancel();
						break;
					}
				}
			}
		}
		;
		launchThread th = new launchThread(curPlatform);
		th.start();
		Log.d(TAG, "launchthread is statt");
		/*launch_timer = new Timer();
		launch_timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = myHandler.obtainMessage(0x401);
				Launcher_main.myHandler.sendMessage(msg);
				Log.d(TAG, "launch timer  is up");
			}
		}, TIME_OUT_TO_SETTING);*/
		Log.d(TAG, "launch timer is started");
	}

	private class StartThread extends Thread {
		private String cp = null;

		public StartThread(String curPlatform) {
			// TODO Auto-generated constructor stub
			cp = curPlatform;
		}

		@Override
		public void run() {
			super.run();
			Log.i(TAG, "now start app:" + cp);
			Intent start = new Intent();
			if (cp.equals(PlatformInfo.PI_HUAWEI)) {
				start.setClassName("com.huawei.stb.tm",
						"com.huawei.stb.tm.ui.activity.LoadingActivity");
			} else if (cp.equals(PlatformInfo.PI_FIBER)) {
				start.setClassName(PlatformInfo.PI_FIBER,
						"com.fonsview.tvlauncher.MainActivity");
			} else if (cp.equals(PlatformInfo.PI_ZTE)) {
				Log.i(TAG, "set class name zte ");
				String isNetworkChange = SystemProperties.get("persist.sys.networkchange", "0");
				if(isLogin && "0".equals(isNetworkChange)){
					/*start.setClassName("com.zte.iptvclient.android.launcher_sc",
							"com.zte.iptvclient.android.launcher.activity.LauncherActivity");*/
					Log.d(TAG, "starting zte launcher");
					try {
						Runtime.getRuntime().exec( "am start -f 0x20000000 " + Launcher_main.ZTE_PACKAGE + "/" + Launcher_main.ZTE_LAUNCHER);
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Log.d(TAG, "now starting zte loggin!");
					ActivityManager activityMgr = (ActivityManager) Launcher_main.this.getSystemService(ACTIVITY_SERVICE );
					activityMgr.killBackgroundProcesses(Launcher_main.ZTE_PACKAGE);
					start.setClassName(Launcher_main.ZTE_PACKAGE,
						Launcher_main.ZTE_LOGIN);
				}
			} else if (cp.equals("setting")) {
				start.setClassName("com.unionman.settings",
						"com.unionman.settings.UMSettingsActivity");
			}
			Log.i(TAG, start.getPackage() + ":" + start.toString());
			start.setAction("android.intent.action.VIEW");
			start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (start != null) {
				startActivity(start);
				Log.i(TAG, "now start platform!!!!!!!!!!!!!!!!!!!!!");
			}

		}
	}
	
	private String getRunningActivityPackageName(){
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE); 
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
	}

	private void showNoApk() {
		Toast.makeText(getApplicationContext(), R.string.warn_noapp, 2000)
				.show();
		createDialog().show();
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
	
}
