package com.um.cpelistener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.RecoverySystem;

import android.os.SystemProperties;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.Uri;
import android.content.ContentValues;
import android.content.ContentResolver;

import com.jcraft.jsch.ChannelSftp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.um.interfaces.SystemSettingInterface;
//import com.hisilicon.android.hisysmanager.HiSysManager;
import android.widget.Toast;


public class ListenService extends Service {
	private final String		TAG										= "CpeListener--ListenService";

	private Context				mContext								= null;
	private Handler				mHandler								= null;
	private NetworkDetector		mNetwrokDetector						= null;

	private String				mFileURL								= "";
	private String				mUpadteMode								= "";
	private String				mDownloadWay							= "";
	private UpLoadLog			mUpLoadLog								= null;

	private final int			MSG_TYPE_SELECT_UPGRADEMODE				= 0;
	private final int			MSG_TYPE_SHOW_DOWNLOAD_DIALOG			= 1;
	private final int			MSG_TYPE_SHOW_DOWNLOAD_SELECT_DIALOG	= 2;
	private final int			MSG_TYPE_SHOW_UPGRADE_SELECT_DIALG		= 3;
	private final int			MSG_TYPE_DONWLOAD_SUCCESS				= 4;
	private final int			MSG_TYPE_DOWNLOAD_COUNTDOWN				= 5;
	private final int			MSG_TYPE_UPGRADE_COUNTDOWN				= 6;
	private final int			MSG_TYPE_BLACKLIST_ENABLE				= 7;
	private final int			MSG_TYPE_BLACKLIST_DISABLE				= 8;
	private final int 			MSG_TYPE_SHOW_DOWNLOAD_BEGIN			= 9;
	private final int			UPGRADE									= 11;
	private final int			RESET									= 3;
	private final int			SAVE_PARAM								= 1;
	private final int			UPLOAD_PACKAGE							= 13;

	private final String		QUITE_UPGRADE							= "quite";
	private final String		FORCE_UPGRADE							= "force";
	private final String		MANUAL_UPGRADE							= "manual";

	private Thread				mThreadCheck							= null;

	/**
	 * 显示百分比
	 */
	private TextView			mTextView;

	/**
	 * 延时
	 */
	private long				delayTime								= 10000;
	/**
	 * 下载处理器
	 */
	private HttpHandler			httpHandler								= null;

	private AlertDialog.Builder	localBuilder							= null;
	private Dialog				localDialog								= null;

	private Thread				mSocketServerThread;

	private SocketMessage		mSocketMessage;

	private ProgressDialog		mProgressDialog;
	// 接受到cpe发送的数据的类型
	private int					action									= 0;
	private final static String SDPATH="/storage/emulated/0/recovery3";
	private boolean				isDownloading							= false;
	private SystemSettingInterface mSystemSettingInterface;
	//error code
	private final static int ERROR_DOWNLOAD=0;
	private final static int ERROR_CONNECT=1;
	private final static int ERROR_NOSPC=2;
	private final static int ERROR_NOFOUND=3;
	private int tryCount=0;
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i(TAG, "onCreate ...");
		mContext = this;
		mSystemSettingInterface  =new SystemSettingInterface(mContext);
		/* 保证不被杀死 */
		Notification notification = new Notification();
		startForeground(1, notification);
		
		File dir = new File(SDPATH);
		
		if(!dir.exists()){
			dir.mkdirs();
		}
		/* 检查标识文件,如果存在则删除掉升级包和标识文件 */
		File flagfile = new File(DefaultParameter.FLAG_FILE_PATH);
		File updatefile = new File(DefaultParameter.UPDATE_FILE_PATH);
		if (updatefile.exists() && updatefile.length() >= DefaultParameter.MAX_LENGTH) {
			deleteFile(updatefile);
		}
		if (flagfile.exists()) {
			flagfile.delete();
			if (updatefile.exists()) {
				deleteFile(updatefile);
			}
		}

		if (mSocketServerThread == null) {
			try {
				mSocketServerThread = new Thread(new SocketServer());
				mSocketServerThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mSocketMessage = new SocketMessage();

		mNetwrokDetector = new NetworkDetector(mContext);

		/* 错误捕获 */
		// MyCrashHandler handler = MyCrashHandler.getInstance();
		// handler.init(mContext);

		/* 消息处理器 */
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_TYPE_SELECT_UPGRADEMODE:
						Logger.i(TAG, "选择升级模式");
						if (isDownloading) {
							return;
						}
						isDownloading = true;
						selectUpgradeMode();
						break;
					case MSG_TYPE_SHOW_DOWNLOAD_SELECT_DIALOG:
						mTextView = new TextView(ListenService.this);
						localBuilder = new Builder(mContext);
						localBuilder.setTitle("系统升级");
						localBuilder.setMessage("有新版本系统固件安装包可供下载，取消请点击【取消】");
						mTextView.setGravity(Gravity.CENTER);
						mTextView.setTextSize(25);
						localBuilder.setView(mTextView);
						localBuilder.setPositiveButton("下载", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mHandler.removeMessages(MSG_TYPE_DOWNLOAD_COUNTDOWN);
								dialog.dismiss();
								downloadUpradeFile();
							}
						});
						localBuilder.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mHandler.removeMessages(MSG_TYPE_DOWNLOAD_COUNTDOWN);
								dialog.dismiss();
								isDownloading = false;
							}
						});
						localDialog = localBuilder.create();
						localDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						localDialog.show();
						setDialogFontSize(localDialog, 22);
						mTextView.setText("10s");
						mHandler.sendEmptyMessageDelayed(MSG_TYPE_DOWNLOAD_COUNTDOWN, 1000);
						break;
					case MSG_TYPE_DOWNLOAD_COUNTDOWN:
						if (mTextView != null) {
							String sCountDown = mTextView.getText().toString().trim();
							int iCountDown = Integer.parseInt(sCountDown.substring(0, sCountDown.length() - 1));
							if (iCountDown == 0) {
								if (localDialog != null || localDialog.isShowing()) {
									localDialog.dismiss();
								}
								downloadUpradeFile();
							} else {
								iCountDown = iCountDown - 1;
								mTextView.setText(iCountDown + "s");
								mHandler.sendEmptyMessageDelayed(MSG_TYPE_DOWNLOAD_COUNTDOWN, 1000);
							}
						}
						break;
					case MSG_TYPE_SHOW_DOWNLOAD_DIALOG:
						if (mProgressDialog != null && mProgressDialog.isShowing()) {
							mProgressDialog.dismiss();
						}
						mProgressDialog = new ProgressDialog(ListenService.this);
						mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mProgressDialog.setTitle("系统升级");
						mProgressDialog.setMessage("正在下载系统升级文件，请勿关闭电源和网络");
						mProgressDialog.setMax(100);
						mProgressDialog.setCancelable(false);
						mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						mProgressDialog.show();
						setDialogFontSize(mProgressDialog, 22);
						break;
					case MSG_TYPE_SHOW_UPGRADE_SELECT_DIALG:
						mTextView = new TextView(ListenService.this);
						localBuilder = new Builder(mContext);
						localBuilder.setTitle("系统升级");
						localBuilder.setMessage("新版本已下载,是否现在升级?选择立即升级会自动重启进行升级,选择下次升级会在下次开机进行升级");
						mTextView.setGravity(Gravity.CENTER);
						mTextView.setTextSize(25);
						localBuilder.setView(mTextView);
						localBuilder.setPositiveButton("立即升级", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mHandler.removeMessages(MSG_TYPE_UPGRADE_COUNTDOWN);
								dialog.dismiss();
								mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
							}
						});
						localBuilder.setNegativeButton("下次升级", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mHandler.removeMessages(MSG_TYPE_UPGRADE_COUNTDOWN);
								dialog.dismiss();
								File data = new File(DefaultParameter.DATA_FILE_PATH);
								File update = new File(DefaultParameter.UPDATE_FILE_PATH);
								data.renameTo(update);
								//HiSysManager hisys = new HiSysManager();
								//hisys.upgrade(DefaultParameter.UPDATE_FILE_PATH);
								SocketClient sc = new SocketClient();
								sc.writeMess("upgrade " +DefaultParameter.UPDATE_FILE_PATH);
								sc.readNetResponseSync();
								//Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
								//intent.putExtra("mount_point", DefaultParameter.UPDATE_FILE_PATH);
								//intent.putExtra("upgrade_mode", "nexttime");
								//ListenService.this.sendBroadcast(intent);
								try{
								
									RecoverySystem.installPackage(ListenService.this, new File(DefaultParameter.UPDATE_FILE_PATH),false);//need to do
								 }catch (Exception e){
							        e.printStackTrace();
							     }
							}
						});
						localDialog = localBuilder.create();
						localDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						localDialog.setCancelable(false);
						localDialog.show();
						setDialogFontSize(localDialog, 25);
						mTextView.setText("20s");
						mHandler.sendEmptyMessageDelayed(MSG_TYPE_UPGRADE_COUNTDOWN, 1000);
						break;
					case MSG_TYPE_UPGRADE_COUNTDOWN:
						if (mTextView != null) {
							String sCountDown = mTextView.getText().toString().trim();
							int iCountDown = Integer.parseInt(sCountDown.substring(0, sCountDown.length() - 1));
							if (iCountDown == 0) {
								if (localDialog != null || localDialog.isShowing()) {
									localDialog.dismiss();
								}
								mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
							} else {
								iCountDown = iCountDown - 1;
								mTextView.setText(iCountDown + "s");
								mHandler.sendEmptyMessageDelayed(MSG_TYPE_UPGRADE_COUNTDOWN, 1000);
							}
						}
						break;
					case MSG_TYPE_DONWLOAD_SUCCESS:
						Logger.i(TAG, "下载完成，准备升级");
						File data = new File(DefaultParameter.DATA_FILE_PATH);
						File update = new File(DefaultParameter.UPDATE_FILE_PATH);
						data.renameTo(update);
						fileDownloadCompleteHandle(DefaultParameter.UPDATE_FILE_PATH);
						break;

					case MSG_TYPE_BLACKLIST_ENABLE:
						List<String> packages = (List<String>) msg.obj;
						setBlackList(packages);
						break;

					case MSG_TYPE_BLACKLIST_DISABLE:
						packages = new ArrayList<String>();
						setBlackList(packages);
						break;
					case MSG_TYPE_SHOW_DOWNLOAD_BEGIN:
						Toast.makeText(getApplicationContext(), "有新版本开始下载", Toast.LENGTH_LONG).show();
						break;
					default:
						break;
				}
			};
		};

		/**
		 * 黑名单处理机制：开机获取名单，disable掉apk，3秒后enable
		 */
		List<String> packages = getBlackList();
		if (packages != null) {
			for (int i = 0; i < packages.size(); i++) {
				Logger.i(TAG, "stopApp :" + packages.get(i));
				try {
					stopApp(this, packages.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			startHandler.sendEmptyMessageDelayed(0, 10000);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 设置对话框字体大小
	 * 
	 * @param dialog
	 * @param size
	 */
	private void setDialogFontSize(Dialog dialog, int size) {
		Window window = dialog.getWindow();
		View view = window.getDecorView();
		setViewFontSize(view, size);
	}

	/**
	 * 设置视图字体大小
	 * 
	 * @param view
	 * @param size
	 */
	private void setViewFontSize(View view, int size) {
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) view;
			int count = parent.getChildCount();
			for (int i = 0; i < count; i++) {
				setViewFontSize(parent.getChildAt(i), size);
			}
		} else if (view instanceof TextView) {
			TextView textview = (TextView) view;
			textview.setTextSize(size);
		}
	}

	@Override
	public void onDestroy() {
		Logger.i(TAG, "UpgradeService onDestroy");
		mThreadCheck.interrupt();
		mThreadCheck = null;
		super.onDestroy();
	}

	/**
	 * 查询升级
	 */
	private void selectUpgradeMode() {
		if (mUpadteMode.equals(MANUAL_UPGRADE)) {
			mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DOWNLOAD_SELECT_DIALOG);
		} else {
			downloadUpradeFile();
		}
	}

	/**
	 * 流转成字符串
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * 下载完成后进行升级
	 * 
	 * @param fileType
	 * @param filePath
	 */
	private void fileDownloadCompleteHandle(String filePath) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Logger.i(TAG, "upgrade file path: " + DefaultParameter.UPDATE_FILE_PATH);
				//HiSysManager hisys = new HiSysManager();
				//hisys.upgrade(DefaultParameter.UPDATE_FILE_PATH);
				Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
				intent.putExtra("mount_point", DefaultParameter.UPDATE_FILE_PATH);
				intent.putExtra("upgrade_mode", "reboot");
				ListenService.this.sendBroadcast(intent);
				SocketClient sc = new SocketClient();
				sc.writeMess("upgrade " + DefaultParameter.UPDATE_FILE_PATH);
				sc.readNetResponseSync();
				try {
					Logger.i(TAG, "upgrade, install package, update file: " + DefaultParameter.UPDATE_FILE_PATH);
					RecoverySystem.installPackage(ListenService.this, new File(DefaultParameter.UPDATE_FILE_PATH));
				} catch (IOException e) {
					Logger.e(TAG, "upgrade, can't perform master clear/factory reset");
				}
				return;
			}
		}, 3000);

	}

	/**
	 * 下载升级文件
	 */
	private void downloadUpradeFile() {
		Logger.i(TAG, "downloadUpradeFile: " + mFileURL);
		isDownloading = true;
		/* 检查标识文件 */
		File flagfile = new File(DefaultParameter.FLAG_FILE_PATH);
		File updatefile = new File(DefaultParameter.UPDATE_FILE_PATH);
		File datafile = new File(DefaultParameter.DATA_FILE_PATH);
		if (updatefile.exists()) {// &&updatefile.length()>=DefaultParameter.MAX_LENGTH){
			deleteFile(updatefile);
		}

		if (datafile.exists() && datafile.length() >= DefaultParameter.MAX_LENGTH) {
			deleteFile(datafile);
		}

		if (flagfile.exists()) {
			flagfile.delete();
			if (updatefile.exists()) {
				deleteFile(updatefile);
			}
		}

		// if(mUpadteMode.equals(MANUAL_UPGRADE)){
		// mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DOWNLOAD_DIALOG);
		// }else if(mUpadteMode.equals(FORCE_UPGRADE)){
		// mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DOWNLOAD_DIALOG);
		// }

		if (httpHandler != null) {
			Logger.i(TAG, "httpHandler.cancel");
			httpHandler.cancel(true);
		}
		Logger.i(TAG, "升级模式:" + mUpadteMode);
		if (mDownloadWay.equals("breakpoint")) {
			Logger.i(TAG, "准备多线程断点下载");
			breakpointDownload();
		} else {
			Logger.i(TAG, "准备非断点下载");
			download();
		}

	}

	/**
	 * 创建新文件
	 */
	private void createFlagFile() {
		File file = new File(DefaultParameter.FLAG_FILE_PATH);
		if (!file.exists()) {
			try {
				file.createNewFile();
				Logger.i(TAG, "create " + file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		if (file.exists()) {
			Logger.i(TAG, "delete " + file.getAbsolutePath());
			file.delete();
		}
	}

	/**
	 * 多线程断点下载
	 */
	private void breakpointDownload() {
		HttpUtils hu = new HttpUtils();
		httpHandler = hu.download(mFileURL, DefaultParameter.DATA_FILE_PATH, true, new RequestCallBack<File>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Logger.i(TAG, "current=" + current);
				if (mUpadteMode.equals(MANUAL_UPGRADE) || mUpadteMode.equals(FORCE_UPGRADE)) {
					int percent = (int) ((current * 100) / total);
					Logger.i(TAG, "percent=" + percent);
					mProgressDialog.setProgress(percent);
				}
				super.onLoading(total, current, isUploading);
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Logger.i(TAG, "断点下载成功");
				if (mUpadteMode.equals(QUITE_UPGRADE)) {
					mHandler.sendEmptyMessage(MSG_TYPE_SHOW_UPGRADE_SELECT_DIALG);
				} else {
					mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
				Logger.i(TAG, "断点下载失败" + arg1);
				Logger.i(TAG, "remove handler and postdelay");
				if(arg1.contains("HttpHostConnectException")){
					sendErrorBroadcast(ERROR_DOWNLOAD);
				}else if(arg1.contains("UnknownHostException")){
					sendErrorBroadcast(ERROR_CONNECT);
				}else if(arg1.contains("ENOSPC")){
					sendErrorBroadcast(ERROR_NOSPC);
				}else if(arg1.contains("Not Found")){
					sendErrorBroadcast(ERROR_NOFOUND);
				}
				isDownloading = false;
				if(tryCount<3) {
					tryCount += 1;
					mHandler.removeMessages(MSG_TYPE_SELECT_UPGRADEMODE);
					mHandler.sendEmptyMessageDelayed(MSG_TYPE_SELECT_UPGRADEMODE, delayTime);
				}
				if (null != arg1) {
					if (arg1.contains("complete")) {
						File updatefile = new File(DefaultParameter.DATA_FILE_PATH);
						if (updatefile.exists()) {
							deleteFile(updatefile);
						}
					}
				}
			}
		});
	}

	/**
	 * 非断点下载
	 */
	private void download() {
		Logger.i(TAG, "准备非断点下载");
		HttpUtils hu = new HttpUtils();
		hu.download(mFileURL, DefaultParameter.DATA_FILE_PATH, false, new RequestCallBack<File>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Logger.i(TAG, "current=" + current);
				if (mUpadteMode.equals(MANUAL_UPGRADE) || mUpadteMode.equals(FORCE_UPGRADE)) {
					int percent = (int) ((current * 100) / total);
					Logger.i(TAG, "percent=" + percent);
					mProgressDialog.setProgress(percent);
				}
				super.onLoading(total, current, isUploading);
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Logger.i(TAG, "非断点下载成功");
				if (mUpadteMode.equals(QUITE_UPGRADE)) {
					mHandler.sendEmptyMessage(MSG_TYPE_SHOW_UPGRADE_SELECT_DIALG);
				} else {
					mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
				Logger.i(TAG, "非断点下载失败" + arg1);
				if(arg1.contains("HttpHostConnectException")){
					sendErrorBroadcast(ERROR_DOWNLOAD);
				}else if(arg1.contains("UnknownHostException")){
					sendErrorBroadcast(ERROR_CONNECT);
				}else if(arg1.contains("ENOSPC")){
					sendErrorBroadcast(ERROR_NOSPC);
				}else if(arg1.contains("Not Found")){
					sendErrorBroadcast(ERROR_NOFOUND);
				}
				isDownloading = false;
				Logger.i(TAG, "remove handler and postdelay");
				if(tryCount<3) {
					tryCount+=1;
					mHandler.removeMessages(MSG_TYPE_SELECT_UPGRADEMODE);
					mHandler.sendEmptyMessageDelayed(MSG_TYPE_SELECT_UPGRADEMODE, delayTime);
				}
			}
		});
	}

	/**
	 * 封装请求体信息 请求体内容，encode编码格式
	 */
	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * 恢复出厂
	 */
	private void reset() {
		Log.d("yiyonghui","reset---");
		mSystemSettingInterface.restoreDefault();
		//HiSysManager hisys = new HiSysManager();
		//hisys.reset();
		SocketClient sc = new SocketClient();
		sc.writeMess("reset");
		sc.readNetResponseSync();
		this.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
	}

	/**
	 * 重新enable新apk
	 */
	private Handler startHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<String> mPackages = getBlackList();
			for (int i = 0; i < mPackages.size(); i++) {
				Logger.i(TAG, "startApp :" + mPackages.get(i));
				try {
					startApp(ListenService.this, mPackages.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	};

	/**
	 * 设置黑名单
	 * 
	 * @param packages
	 */
	private void setBlackList(List<String> packages) {
		File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/blacklist.xml");
		Logger.i(TAG, getApplicationContext().getFilesDir().getAbsolutePath() + "");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream mOutputStream = new FileOutputStream(file);
			boolean result = XMLUtil.pullXMLCreate(packages, mOutputStream);
			Logger.i(TAG, "result=" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取黑名单
	 * 
	 * @return
	 */
	private List<String> getBlackList() {
		List<String> packages = new ArrayList<String>();
		try {
			File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/blacklist.xml");
			if (file.exists()) {
				InputStream mInputStream = new FileInputStream(file);
				packages = XMLUtil.pullXMLResolve(mInputStream);
				for (int i = 0; i < packages.size(); i++) {
					Logger.i(TAG, "blacklist=" + packages.get(i));
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return packages;
	}

	/**
	 * 停掉apk
	 * 
	 * @param context
	 * @param packageName
	 */
	private void stopApp(Context context, String packageName) {
		final PackageManager pm = context.getPackageManager();
		pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
		Logger.i(TAG, "stopApp");
	}

	/**
	 * 启用apk
	 * 
	 * @param context
	 * @param packageName
	 */
	private void startApp(Context context, String packageName) {
		final PackageManager pm = context.getPackageManager();
		pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
		Logger.i(TAG, "startApp");
	}

	class SocketServer implements Runnable {

		public static final int	SERVERPORT	= 51806;
		public ServerSocket		serverSocket;

		public SocketServer() throws IOException {
			serverSocket = new ServerSocket(SERVERPORT);
		}

		public void run() {
			Log.i(TAG, "ServerSocket start ");
			try {
				while (true) {
					Socket client = serverSocket.accept();
					Log.i(TAG, "ServerSocket accept ");
					try {
						String message = getReceiveMessage(client, Msger_Info.length, 10000);
						//Log.i(TAG, "message=" + message);
						// 升级
						if (action == UPGRADE) {
							String[] upgradeInfos = message.split(";");
							if (upgradeInfos.length > 0) {
								String upgradeMode = upgradeInfos[0].substring(0, 1);
								// if(upgradeMode.equals("0")){
								// mUpadteMode=MANUAL_UPGRADE;
								// }else{
								// mUpadteMode=FORCE_UPGRADE;
								// }
								mFileURL = upgradeInfos[1];
								String mode = "1";// mSocketMessage.getMsg("Device.X_CMCC_OTT.ServiceInfo.SilentUpgrade",
													// 0, "");
								Logger.i(TAG, "mode=" + mode);
								// if(mode.equals("1")){
								mUpadteMode = QUITE_UPGRADE;
								// }
								Logger.i(TAG, "mUpadteMode=" + mUpadteMode);
								Logger.i(TAG, "mFileURL=" + mFileURL);
								if(!isDownloading){
									mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DOWNLOAD_BEGIN);
									tryCount=0;
									downloadUpradeFile();
								}
								// mHandler.sendEmptyMessage(MSG_TYPE_SELECT_UPGRADEMODE);
							}
						}
						// 恢复出厂
						else if (action == RESET) {
							reset();
						}
						// 设置参数
						else if (action == SAVE_PARAM) {
							String[] datas = message.split(";");
							//Log.e("Eniso", "SAVE_PARAM message = " + message);
							if (datas.length > 0) {
								String type = datas[0];
								// ntp服务地址
								if (type.equals("ntpserver1")) {
									String url1 = datas[1];
									Logger.i(TAG, "ntpserver1=" + url1);
									Settings.Secure.putString(ListenService.this.getContentResolver(), "ntp_server",
											url1);
								} else if (type.equals("ntpserver2")) {
									String url2 = datas[1];
									Logger.i(TAG, "ntpserver2=" + url2);
									Settings.Secure.putString(ListenService.this.getContentResolver(), "ntp_server2",
											url2);
								} else if (type.equals("UserID")) {
									String url2 = datas[1];
									//Log.d(TAG, "content://stbconfig/summary UserID = " + url2);
									Log.d(TAG, "content://stbconfig/summary UserID = ****");
									Uri uri = Uri.parse("content://stbconfig/summary");
									ContentResolver contentResolver = ListenService.this.getContentResolver();
									ContentValues values = new ContentValues();
									values.put("UserID", url2);
									if (contentResolver.query(uri, new String[]{"UserID"}, null, null, null).moveToFirst() == false) {
										contentResolver.insert(uri, values);
									} else {
										contentResolver.update(uri, values, null, null);
									}
									contentResolver.notifyChange(uri, null);
								} else if (type.equals("UserPassword")) {
									String url2 = datas[1];
									//Log.d(TAG, "content://stbconfig/summary UserPassword = " + url2);
									Log.d(TAG, "content://stbconfig/summary UserPassword = ****");
									Uri uri = Uri.parse("content://stbconfig/summary");
									ContentResolver contentResolver = ListenService.this.getContentResolver();
									ContentValues values = new ContentValues();
									values.put("UserPassword", url2);
									if (contentResolver.query(uri, new String[]{"UserPassword"}, null, null, null).moveToFirst() == false) {
										contentResolver.insert(uri, values);
									} else {
										contentResolver.update(uri, values, null, null);
									}
									contentResolver.notifyChange(uri, null);
								} // 黑名单
								else if (type.equals("AppAutoRunBlackList")) {
									String flag = mSocketMessage
											.getMsg("Device.X_CMCC_OTT.Extention.AppAutoRunBlackListFlag", 0, "");
									if (flag.equals("true")) {
										List<String> packages = new ArrayList<String>();
										for (int i = 0; i < 32; i++) {
											String pk = mSocketMessage
													.getMsg("Device.X_CMCC_OTT.Extention.AppAutoRunBlockList." + (i + 1)
															+ ".PackageName", 0, "");
											if (!pk.equals("")) {
												packages.add(pk);
												Logger.i(TAG, "black list =" + pk);
											}
										}
										Message msg = mHandler.obtainMessage();
										msg.obj = packages;
										msg.what = MSG_TYPE_BLACKLIST_ENABLE;
										Logger.i(TAG, "MSG_TYPE_BLACKLIST_ENABLE");
										mHandler.sendMessage(msg);
									} else {
										Logger.i(TAG, "MSG_TYPE_BLACKLIST_DISABLE");
										mHandler.sendEmptyMessage(MSG_TYPE_BLACKLIST_DISABLE);
									}
								} else if (type.equals("SysLog")) {
									if (datas.length < 7) {
										Log.e(TAG, "SysLog params too less");
										return;
									}
									//Log.e(TAG, "SysLog need to upload!!!");
									String logServer = datas[1];
									String startTime = null;
									long timeout = 0;
									int logLevel = 0;
									int logType = 0;
									int outType = 0;

									if (logServer == null)
										return;
									try {
										logType = Integer.parseInt(datas[2]);
										startTime = datas[3];
										timeout = Long.parseLong(datas[4]);
										logLevel = Integer.parseInt(datas[5]);
										outType = Integer.parseInt(datas[6]);
									} catch (Exception e) {
										return;
									}

									if (mUpLoadLog == null)
										mUpLoadLog = new UpLoadLog();

									mUpLoadLog
									.setServerAddr(logServer)
									.setStartTime(startTime)
									.setEndTime(timeout)
									.setLogLevel(logLevel)
									.setLogType(logType)
									.setOutPutType(outType);

									if (mUpLoadLog.getState() == UpLoadLog.UPLOAD_SYSLOG_STOPED) {
										mUpLoadLog.sendUDPLog();
									}
								} else if (type.equals("PlayURL")) {
									Log.e("Eniso", "播测诊断！");
									Bundle mBundle = new Bundle();
									mBundle.putString("PlayURL", datas[1]);
									Intent it = new Intent()
											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
											.setClass(ListenService.this, MainActivity.class)
											.putExtras(mBundle);
									startActivity(it);
								}
							}
							// 传网络包
						} else if (action == UPLOAD_PACKAGE) {
							String[] datas = message.split(";");
							String host = "218.207.213.114";
							int port = 37028;
							String username = "HUAWEI_dump";
							String password = "F!-or.*s1T+b";
							String localFilePath = "/data/umcpe/package_tmp.pcap";
							if (datas.length == 5) {
								String url = datas[1];
								String urldatas[] = url.split(":");
								if (urldatas.length == 3) {
									host = urldatas[1].substring(2, urldatas[1].length());
									port = Integer.parseInt(urldatas[2]);
								}
								username = datas[2];
								password = datas[3];
								localFilePath = datas[4];
								Log.i(TAG, "host=" + host + ",port=" + port + ",username=" + username + ",password="
										+ password + ",localFilePath=" + localFilePath);
								SFTPUtil sf = new SFTPUtil();
								ChannelSftp sftp = sf.connect(host, port, username, password, localFilePath);
								sf.upload(sftp);
								Socket socket_client = null;
								try {
									InetAddress addr = InetAddress.getByName("127.0.0.1");
									socket_client = new Socket(addr, 23416);
									SocketMessage.send_message(socket_client, "java_upload", SAVE_PARAM, "6", 1);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (socket_client != null) {
										socket_client.close();
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.i(TAG, "socket err");
					}
				}
			} catch (Exception e) {
				Log.i(TAG, "socket failure===" + e.toString());
			}
		}

		/**
		 * 获取Socket接收到的数据
		 * 
		 * @param socket
		 * @param len
		 * @param timeout
		 * @return
		 */
		public String getReceiveMessage(Socket socket, int len, int timeout) {
			byte[] receive = new byte[len];
			Msger_Info mmInfo = null;
			try {
				socket.setSoTimeout(timeout);
				socket.getInputStream().read(receive);
				Log.i(TAG, new String(receive));
				mmInfo = Msger_Info.getmsger_info(receive);
				if (mmInfo != null) {
					action = mmInfo.type;
					Logger.i(TAG, "action=" + action);
				}
				System.out.println(TAG + "received:name is :" + mmInfo.name + ",msg is :" + mmInfo.msg + ",len is :"
						+ mmInfo.len + ",action=" + mmInfo.type);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mmInfo != null && mmInfo.msg != null) {
				return mmInfo.msg;
			} else {
				return "";
			}
		}
	}

	private void sendErrorBroadcast(int what){
		Intent mIntent=new Intent("android.unionman.action.ERROR_REPORT");
		switch (what){
			case ERROR_DOWNLOAD:
				mIntent.putExtra("code_type","10040");
				Log.i("ERROR_REPORT","code_type 10040");
				break;
			case ERROR_CONNECT:
				mIntent.putExtra("code_type","10043");
				Log.i("ERROR_REPORT", "code_type 10043");
				break;
			case ERROR_NOSPC:
				mIntent.putExtra("code_type","10044");
				Log.i("ERROR_REPORT", "code_type 10044");
				break;
			case ERROR_NOFOUND:
				mIntent.putExtra("code_type","10045");
				Log.i("ERROR_REPORT", "code_type 10045");
				break;
		}
		if(tryCount==0) {
			mContext.sendBroadcast(mIntent);
		}
	}
}
