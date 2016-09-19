package com.um.networkupgrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.hisilicon.android.hisysmanager.HiSysManager;

public class UpgradeService extends Service {
	private final String TAG = "NetworkUpgrade--UpgradeService";
	private String mLocalProductModel = StbManager.getSystemProperties("ro.build.product", "");
	private String mLocalVendor = DefaultParameter.STB_VENDOR;
	private String mLocalSoftVer = DefaultParameter.STB_SAFEWARE_DEFAULT_VERSION;
	private String mLocalHardVer = StbManager.getSystemProperties("ro.build.product", "");
	private String mLocalSerial = StbManager.getSystemProperties("ro.serialno", "");
	private String mServerUrl = DefaultParameter.STB_DEFAULT_URL;

	private Context mContext = null;
	private Handler mHandler = null;
	private NetworkDetector mNetwrokDetector = null;
	private List<UpgradeInfo> upgradeInfo;

	private String mFileURL;
	private String mFileType;
	private String mUpadteMode;
	private String mDownloadWay;
	private String mVersion;
	private String mMd5;

	private final int MSG_TYPE_CHECK_UPGRADE = 0;
	private final int MSG_TYPE_SHOW_DIALOG = 1;
	private final int MSG_TYPE_DOWNLOAD = 2;
	private final int MSG_TYPE_DONWLOAD_SUCCESS = 3;
	private final int MSG_TYPE_FORCE_UPGRADE = 4;
	private final int MSG_TYPE_READY_DOWNLOAD=5;

	private final String FORCE_UPGRADE = "force";
	private final String MANUAL_UPGRADE = "manual";

	private Runnable mUpgradeRunable;

	private Thread mThreadCheck = null;
	private final static int ERROR_DOWNLOAD=0;
	private final static int ERROR_CHECK=1;
	private final static int ERROR_CONNECT=2;
	private final static int ERROR_NOSPC=3;
	private final static int ERROR_NOFOUND=4;
	private boolean isSendErrorReport=false;

	/**
	 * 延时10秒
	 */
	private long delayTime=10*1000;
	/**
	 * 最长超时2h
	 */
	private long maxDelay=60*60*1000;
	/**
	 * 最短超时10s
	 */
	private long minDelay=10*1000;
	/**
	 * 请求时间间隔30分钟
	 */
	private long timeStep=30*60*1000;
	
	/**
	 * 显示百分比
	 */
	private TextView mTextView;
	
	/**
	 * 选择对话框是否显示过
	 */
	private boolean isSelectDialogShowed=false;
	
	/**
	 * 选择下载提示框是否显示
	 */
	private boolean isDownloadDialogShow=false;
	
	/**
	 * 是否在下载升级文件
	 * */
	private boolean isDownloading=false;
	
	/**
	 * 是否成功获取基本配置信息
	 * */
	private boolean isGetBaseInfoSuccess=false;
	
	/**
	 * 是否第一次接收到网络改变广播,apk启动默认会接收到一条
	 */
	
	/**
	 * 下载处理器
	 */
	private HttpHandler httpHandler=null;
	
	private AlertDialog.Builder localBuilder = null;
	private Dialog localDialog = null;
	/**
	 * 如果来自升级界面的请求则进行Toast提示
	 */
	private boolean isFromActivity=false;
	
	private int getUpgradeFileCount=0;
	
	private boolean isNetworkConnected=true;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i(TAG, "onCreate ...");
		mContext = this;
		
		/*保证不被杀死*/
		Notification notification=new Notification();
		startForeground(1, notification);
		
		/*检查标识文件*/
		File flagfile=new File(DefaultParameter.FLAG_FILE_PATH);
		File updatefile=new File(DefaultParameter.UPDATE_FILE_PATH);
		if(updatefile.exists()&&updatefile.length()>=DefaultParameter.MAX_LENGTH){
			deleteFile(updatefile);
		}
		if(flagfile.exists()){
			flagfile.delete();
			if(updatefile.exists()){
				deleteFile(updatefile);
			}
		}
		
		/* 获取升级配置文件url,如果没有则使用默认的 */
		mServerUrl = StbManager.getSystemProperties("persist.sys.um.upgrade.url",
				DefaultParameter.STB_DEFAULT_URL);

		/* 本地软件版本 */
		mLocalSoftVer = StbManager.getSystemProperties("ro.build.version.incremental", "");

		mNetwrokDetector = new NetworkDetector(mContext);

		/* 错误捕获 */
		//MyCrashHandler handler = MyCrashHandler.getInstance();
		//handler.init(mContext);


		/* 消息处理器 */
		
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TYPE_CHECK_UPGRADE:
					Logger.i(TAG, "check upgrade");
					checkUpgrade();
					break;
				case MSG_TYPE_SHOW_DIALOG:
					Logger.i(TAG, "isSelectDialogShowed="+isSelectDialogShowed);
					Logger.i(TAG, "remove handler");
					mHandler.removeCallbacks(mUpgradeRunable);
					if(!isSelectDialogShowed){
						localBuilder = new Builder(mContext);
						localBuilder.setTitle(getResources().getString(R.string.dialog_title));
						localBuilder.setMessage(getResources().getString(R.string.dialog_msg));
						localBuilder.setPositiveButton(getResources().getString(R.string.dialog_pb), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								downloadUpradeFile();
							}
						});
						localBuilder.setNegativeButton(getResources().getString(R.string.dialog_nb), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						localDialog = localBuilder.create();
						localDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						localDialog.show();
						isSelectDialogShowed=true;
					}else{
						Logger.i(TAG, "isDownloading:"+isDownloading);
						if(!isDownloading){
							downloadUpradeFile();
						}
					}
					break;
				case MSG_TYPE_DOWNLOAD:
					if(!isDownloadDialogShow){
						Logger.i(TAG,"MSG_TYPE_DOWNLOAD");
						mTextView=new TextView(UpgradeService.this);
						localBuilder = new Builder(mContext);
						localBuilder.setTitle(getResources().getString(R.string.dialog_title));
						localBuilder.setMessage(getResources().getString(R.string.dialog_tips));
						mTextView.setGravity(Gravity.CENTER);
						mTextView.setTextSize(25);
						localBuilder.setView(mTextView);
						localDialog = localBuilder.create();
						localDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						localDialog.setOnKeyListener(new OnKeyListener() {
							
							@Override
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
								if(keyCode==KeyEvent.KEYCODE_BACK){
									return true;
								}
								return false;
							}
						});
						localDialog.show();
						isDownloadDialogShow=true;
					}
					isDownloading=true;
					break;
				case MSG_TYPE_DONWLOAD_SUCCESS:
					sendDownloadEnd();
					Logger.i(TAG, "download success,ready to upgrade");
					File data = new File(DefaultParameter.DATA_FILE_PATH);
					File update = new File(DefaultParameter.UPDATE_FILE_PATH);
					data.renameTo(update);
					String localMd5=Md5Util.getFileMD5(update);
					if(mMd5!=null){
						if(localMd5.equalsIgnoreCase(mMd5)){
							Logger.i(TAG, "md5 is correct,check success");
							createFlagFile();
							localBuilder = new Builder(mContext);
							localBuilder.setTitle(getResources().getString(R.string.system_upgrade));
							localBuilder.setMessage(getResources().getString(R.string.ready_upgrade));
							localDialog = localBuilder.create();
							localDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
							localDialog.show();
							fileDownloadCompleteHandle(mFileType, DefaultParameter.UPDATE_FILE_PATH);
							break;
						}
						Logger.i(TAG, "localMd5="+localMd5+" md5="+mMd5);
					}
					sendErrorBroadcast(ERROR_CHECK);
					Logger.i(TAG, "md5 is not correct");
					deleteFile(update);
					if(delayTime>=maxDelay){
						upgradeInfo=null;
						delayTime=minDelay;
						mHandler.removeCallbacks(mUpgradeRunable);
						mHandler.post(mUpgradeRunable);
					}else{
						mHandler.removeCallbacks(mUpgradeRunable);
						mHandler.postDelayed(mUpgradeRunable, delayTime);
					}
					ToastUtil.showLongToast(mContext, getResources().getString(R.string.check_failed));
					if(mUpadteMode.equals(MANUAL_UPGRADE)){
						localDialog.dismiss();
						resetStatus();
					}else{
						delayTime*=2;
					}
					break;
				case MSG_TYPE_FORCE_UPGRADE:
					downloadUpradeFile();
					break;
				case MSG_TYPE_READY_DOWNLOAD:
					if(null!=mTextView){
						mTextView.setText(getResources().getString(R.string.ready_download));
					}
					break;
				default:
					break;
				}
			};
		};

		mUpgradeRunable = new Runnable() {
			@Override
			public void run() {
				{
					Logger.i(TAG, "start timer");
					mHandler.sendEmptyMessage(MSG_TYPE_CHECK_UPGRADE);
					Logger.i(TAG, "postDelayed="+timeStep);
					Logger.i(TAG, "mUpgradeRunable remove handler and postdelay");
					mHandler.removeCallbacks(mUpgradeRunable);
					mHandler.postDelayed(this, timeStep);  
				}
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.i(TAG, "onStartCommand");
		isSendErrorReport=false;
		if(null!=intent.getStringExtra("from")){
			Logger.i(TAG, "from Activity");
			isFromActivity=true;
			isSelectDialogShowed=false;
			Logger.i(TAG, "onStartCommand remove handler and postdelay");
			mHandler.removeCallbacks(mUpgradeRunable);
			mHandler.postDelayed(mUpgradeRunable, 1000);
		}else{
			Logger.i(TAG, "onStartCommand remove handler and postdelay");
			mHandler.removeCallbacks(mUpgradeRunable);
			mHandler.postDelayed(mUpgradeRunable, 10000);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Logger.i(TAG, "UpgradeService onDestroy");
		mHandler.removeCallbacks(mUpgradeRunable);
		mHandler.removeMessages(MSG_TYPE_CHECK_UPGRADE);
		mThreadCheck.interrupt();
		mThreadCheck = null;
		super.onDestroy();
	}

	/**
	 * 查询升级
	 */
	private void checkUpgrade() {
		if (mThreadCheck != null) {
			mThreadCheck.interrupt();
		}
		mThreadCheck = new Thread(new Runnable() {
			public void run() {
				if(!isGetBaseInfoSuccess){
					getBaseConfig();
				}
				mServerUrl = StbManager.getSystemProperties("persist.sys.um.upgrade.url",
						DefaultParameter.STB_DEFAULT_URL);
				if (upgradeInfo == null||upgradeInfo.size()==0) {
					Logger.i(TAG, "download upgrade config file request");
					upgradeInfo = getUpgradeConfig(mServerUrl);
				} 
				if (upgradeInfo != null&&upgradeInfo.size()!=0) {
					Logger.i(TAG, "upgradeInfo != null");
					getUpgradeFile();
				} else {
					Logger.i(TAG, "no upgrade information is detected");
					if(isFromActivity){
						isFromActivity=false;
						Looper.prepare();
						ToastUtil.showToast(mContext,getResources().getString(R.string.new_version));
						Looper.loop();
					}
				}
			}

		});
		mThreadCheck.start();
	}

	/**
	 * 获取基本配置信息
	 * 
	 * @param stbUrl
	 * @return
	 */
	private void getBaseConfig() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sv", mLocalSoftVer);
			params.put("hw", mLocalHardVer);
			params.put("serialno", mLocalSerial);
			Logger.i(TAG, "download base info request data is "+getRequestData(params, "utf-8").toString());
			byte[] data = getRequestData(params, "utf-8").toString().getBytes();
			List<BaseInfo> baseInfo = null;
			String urlStr = "http://" + mServerUrl + "/UpgradeMonitor/Time.aspx";
			
			Logger.i(TAG, "base info path: " + urlStr);
			HttpURLConnection httpConn=null;
			try {
				URL url = new URL(urlStr);
				URLConnection rulConn = url.openConnection();
				httpConn = (HttpURLConnection) rulConn;
				httpConn.setConnectTimeout(5000);
				httpConn.setReadTimeout(5000);
				httpConn.setDoOutput(true);  
				httpConn.setDoInput(true);  
				httpConn.setRequestMethod("POST"); 
				httpConn.setRequestProperty("Connection", "close"); 
				OutputStream outStream = httpConn.getOutputStream();
		        outStream.write(data);
		        outStream.flush();
		        outStream.close();
				if(httpConn.getResponseCode()==200){
					Logger.i(TAG, "get base info response success "+httpConn.getResponseCode());
					InputStream in = httpConn.getInputStream();
					baseInfo = PullParserXml.parseBasesConfigFile(in);
					in.close();
		        }else{
		        	Logger.i(TAG, "get base info response err "+httpConn.getResponseCode());
		        	if(isFromActivity){
		        		isFromActivity=false;
		        		Looper.prepare();
						ToastUtil.showToast(mContext, getResources().getString(R.string.new_version));
						Looper.loop();
					}
		        }
			} catch (Exception e) {
				e.printStackTrace();
				Logger.i(TAG, "get base info connect err");
				if(isFromActivity){
	        		isFromActivity=false;
	        		Looper.prepare();
					ToastUtil.showToast(mContext, getResources().getString(R.string.connect_exception));
					Looper.loop();
				}
			}finally{
				httpConn.disconnect();
			}

			if(baseInfo!=null){
				isGetBaseInfoSuccess=true;
				timeStep=baseInfo.get(0).getTimeStep()*1000;
				Logger.i(TAG, "time step is "+timeStep);
				Logger.i(TAG, "getBaseConfig remove handler and postdelay");
				mHandler.removeCallbacks(mUpgradeRunable);
				mHandler.postDelayed(mUpgradeRunable, timeStep);
			}else{
				if(isFromActivity){
	        		isFromActivity=false;
	        		Looper.prepare();
					ToastUtil.showToast(mContext, getResources().getString(R.string.new_version));
					Looper.loop();
				}
			}
	}
	
	/**
	 * 获取升级配置信息
	 * 
	 * @param stbUrl
	 * @return
	 */
	private List<UpgradeInfo> getUpgradeConfig(String stbUrl) {
		List<UpgradeInfo> upgradeInfo = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("sv", mLocalSoftVer);
		params.put("hw", mLocalHardVer);
		params.put("serialno", mLocalSerial);
		Logger.i(TAG, "download upgrade info request data is "+getRequestData(params, "utf-8").toString());
		byte[] data = getRequestData(params, "utf-8").toString().getBytes();
		String urlStr = "http://" + stbUrl + "/UpgradeMonitor/Upgrade.aspx";
		//String urlStr ="http://192.168.1.2/taiguo/config.xml";
		
		Logger.i(TAG, "upgrade config path: " + urlStr);
		HttpURLConnection httpConn=null;
		try {
			URL url = new URL(urlStr);
			URLConnection rulConn = url.openConnection();
			httpConn = (HttpURLConnection) rulConn;
			httpConn.setConnectTimeout(5000);
			httpConn.setReadTimeout(5000);
			httpConn.setDoOutput(true);  
			httpConn.setDoInput(true);  
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Connection", "close"); 
			OutputStream outStream = httpConn.getOutputStream();
	        outStream.write(data);
	        outStream.flush();
	        outStream.close();
	        if(httpConn.getResponseCode()==200){
	        	Logger.i(TAG, "get upgrade info response success "+httpConn.getResponseCode());
	        	InputStream in = httpConn.getInputStream();
				upgradeInfo = PullParserXml.getPackage(in);
	        	System.out.println(TAG+"="+inputStream2String(in));
				in.close();
	        }else{
	        	Logger.i(TAG, "get upgrade info response err "+httpConn.getResponseCode());
	        	if(isFromActivity){
	        		isFromActivity=false;
	        		Looper.prepare();
					ToastUtil.showToast(mContext, getResources().getString(R.string.new_version));
					Looper.loop();
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			if(isFromActivity){
				isFromActivity=false;
				Looper.prepare();
				ToastUtil.showToast(mContext, getResources().getString(R.string.connect_exception));
				Looper.loop();
			}
			Logger.i(TAG, "get upgrade info connect err");
		}finally{
			httpConn.disconnect();
		}

		return upgradeInfo;
	}
	
	/**
	 * 流转成字符串
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public String inputStream2String (InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	} 

	/**
	 * 比较升级信息
	 * @param upgradeInfo
	 */
	private void getUpgradeFile() {
		if (upgradeInfo == null||upgradeInfo.size()==0) {
			return;
		}

		for (Iterator<UpgradeInfo> upgradeNode = upgradeInfo.iterator(); upgradeNode
				.hasNext();) {
			Logger.i(TAG, "serverInfo:" + upgradeInfo);
			Logger.i(TAG, "localInfos:[mProductModel:" + mLocalProductModel + 
					" mVersion="+ mLocalSoftVer+" mHardwareVersion="+ mLocalHardVer + "]");
			UpgradeInfo upgradNode = upgradeNode.next();
			if (mLocalProductModel.equals(upgradNode.getProductModel()) /* 匹配产品型号 */
					&& mLocalHardVer.equals(upgradNode.getHardVersion()) /* 匹配硬件型号 */
					/*匹配版本,是否与当前版本相同*/
					&&checkVersion(mLocalSoftVer,upgradNode.getVersion())){
						List<UpgradeInfo.Packet> listPacket = upgradNode
								.getPacketList();
						String stbVer = upgradNode.getVersion();
						for (Iterator<UpgradeInfo.Packet> packetNode = listPacket
								.iterator(); packetNode.hasNext();) {
							UpgradeInfo.Packet pnode = packetNode.next();
							Logger.i(TAG, "update="+pnode.getPacketUpdate());
							Logger.i(TAG, "type="+pnode.getPacketType());
							Logger.i(TAG, "md5="+pnode.getMd5());
							Logger.i(TAG, "mUpadteMode="+upgradNode.getUpdateMode());
							Logger.i(TAG, "mDownloadWay="+upgradNode.getDownloadWay());
							/*判断是否升级*/
							if (pnode.getPacketUrl().endsWith(
									pnode.getPacketType())
									&&pnode.getPacketUpdate().equals("true")) {
								
								mFileURL = pnode.getPacketUrl();
								mFileType = pnode.getPacketType();
								mMd5=pnode.getMd5();
								mUpadteMode = upgradNode.getUpdateMode();
								mVersion = stbVer;
								mDownloadWay = upgradNode.getDownloadWay();
								Looper.prepare();
								if (mUpadteMode.equals(FORCE_UPGRADE)) {
									Logger.i(TAG, "force upgrade,ready download");
									if(isFromActivity){
										ToastUtil.showToast(mContext, getResources().getString(R.string.new_version));
										isFromActivity=false;
										Logger.i(TAG, "FORCE_UPGRADE remove handler and postdelay");
										mHandler.removeCallbacks(mUpgradeRunable);
										mHandler.postDelayed(mUpgradeRunable, timeStep);
									}else{
										mHandler.sendEmptyMessage(MSG_TYPE_FORCE_UPGRADE);
									}
								}
								if (mUpadteMode.equals(MANUAL_UPGRADE)) {
									if(!isNetworkConnected){
										Logger.i(TAG, "network is not connected,manual upgrade,pop tips");
										mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DIALOG);
										isNetworkConnected=true;
									}
									else if(getUpgradeFileCount<=4){
										getUpgradeFileCount+=1;
										Logger.i(TAG, "manual upgrade,pop tips");
										mHandler.sendEmptyMessage(MSG_TYPE_SHOW_DIALOG);
									}else{
										Logger.i(TAG, "getUpgradeFileCount>10 remove handler");
										mHandler.removeCallbacks(mUpgradeRunable);
										ToastUtil.showLongToast(mContext, getResources().getString(R.string.cancel_download));
										if(localDialog.isShowing()){
											localDialog.dismiss();
										}
										getUpgradeFileCount=0;
										isDownloadDialogShow=false;
									}
								}
								Looper.loop();
							}else{
								Logger.i(TAG, "set upgradeInfo==null");
								upgradeInfo=null;
							}
						}
			}else{
				upgradeInfo=null;
				Logger.i(TAG, "set upgradeInfo==null");
				if(isFromActivity){
					isFromActivity=false;
					Looper.prepare();
					ToastUtil.showToast(mContext, getResources().getString(R.string.new_version));
					Looper.loop();
				}
			}
		}
	}

	/**
	 * 下载完成后进行升级
	 * 
	 * @param fileType
	 * @param filePath
	 */
	private void fileDownloadCompleteHandle(String fileType, String filePath) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mFileType.equals("zip")) {
						Logger.i(TAG, "upgrade file path: " + DefaultParameter.UPDATE_FILE_PATH);
						
						/*
						HiSysManager hisys = new HiSysManager();
						hisys.upgrade(DefaultParameter.UPDATE_FILE_PATH);
						*/
						
						SocketClient socketClient = null;
						socketClient = new SocketClient();
						socketClient.writeMsg("upgrade " + DefaultParameter.UPDATE_FILE_PATH);
						socketClient.readNetResponseSync();
						
						try{
							Logger.i(TAG, "installPackage");
							RecoverySystem.installPackage(getApplicationContext(),new File(DefaultParameter.UPDATE_FILE_PATH));
						}catch(Exception e){
							e.printStackTrace();
						}
						return;
				} else {
					Logger.i(TAG, "unkown file type!");
				}
			}
		}, 8000);
		
	}

	
	/**
	 * 检测软件版本
	 * @param localVer
	 * @param remoteVer
	 * @return
	 */
	private boolean checkVersion(String localVer,String remoteVer){
		if (localVer.equals(remoteVer)) {
			Logger.i(TAG, "same version");
			return false;
		}else{
			Logger.i(TAG, "different version");
			return true;
		}
	}
	

	/**
	 * 下载升级文件
	 */
	private void downloadUpradeFile() {
		Logger.i(TAG, "downloadUpradeFile: " + mFileURL);
		/*已经进行升级,暂停检测*/
		Logger.i(TAG, "remove handler");
		mHandler.removeCallbacks(mUpgradeRunable);
		
		/*检查标识文件*/
		File flagfile=new File(DefaultParameter.FLAG_FILE_PATH);
		File updatefile=new File(DefaultParameter.UPDATE_FILE_PATH);
		File datafile=new File(DefaultParameter.DATA_FILE_PATH);
		if(updatefile.exists()&&updatefile.length()>=DefaultParameter.MAX_LENGTH){
			deleteFile(updatefile);
		}
		
		if(datafile.exists()&&datafile.length()>=DefaultParameter.MAX_LENGTH){
			deleteFile(datafile);
		}
		
		if(flagfile.exists()){
			flagfile.delete();
			if(updatefile.exists()){
				deleteFile(updatefile);
			}
		}
		
		/*手动升级,发送选择框,避免重复显示选择框,在下载进度条已经显示的时候不发送此消息*/
		if(mUpadteMode.equals(MANUAL_UPGRADE)){
			if(!isDownloading){
				mHandler.sendEmptyMessage(MSG_TYPE_DOWNLOAD);
			}
			mHandler.sendEmptyMessage(MSG_TYPE_READY_DOWNLOAD);
		}
		
		if(httpHandler!=null){
			Logger.i(TAG, "httpHandler.cancel");
		    httpHandler.cancel(true);
		}
		
		if (mDownloadWay.equals("breakpoint")) {
			Logger.i(TAG, "breakpoint download upgrade file");
			breakpointDownload();
		} else {
			Logger.i(TAG, "download upgrade file");
			download();
		}
		
		sendDownloadBegin();
	}
	
	/**
	 * 发送下载开始线程
	 */
	private void sendDownloadBegin(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("flag", "begin");
		params.put("sv", mLocalSoftVer);
		params.put("hw", mLocalHardVer);
		params.put("serialno", mLocalSerial);
		Logger.i(TAG, "download begin request data is "+getRequestData(params, "utf-8").toString());
		byte[] data = getRequestData(params, "utf-8").toString().getBytes();
		new BeginAndEndThread(data).start();
	}
	
	/**
	 * 发送下载完成请求
	 */
	private void sendDownloadEnd(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("flag", "end");
		params.put("sv", mLocalSoftVer);
		params.put("hw", mLocalHardVer);
		params.put("serialno", mLocalSerial);
		Logger.i(TAG, "download end request data is "+getRequestData(params, "utf-8").toString());
		byte[] data = getRequestData(params, "utf-8").toString().getBytes();
		new BeginAndEndThread(data).start();
	}
	
	/**
	 * 下载开始或结束线程
	 * @author jackie
	 *
	 */
	class BeginAndEndThread extends Thread{
		private byte[] data;
		public  BeginAndEndThread(byte[] data){
			this.data=data;
		}
		@Override
		public void run() {
			HttpURLConnection httpConn=null;
			try {
				Logger.i(TAG, "begin or end");
				URL url = new URL(DefaultParameter.BEGIN_OR_END_DOWNLOAD);
				URLConnection rulConn = url.openConnection();
				httpConn = (HttpURLConnection) rulConn;
				httpConn.setConnectTimeout(5000);
				httpConn.setReadTimeout(5000);
				httpConn.setDoOutput(true);  
				httpConn.setDoInput(true);  
				httpConn.setRequestMethod("POST"); 
				httpConn.setRequestProperty("Connection", "close");
				OutputStream outStream = httpConn.getOutputStream();
		        outStream.write(data);
		        outStream.flush();
		        outStream.close();
		        Logger.i(TAG, "ResponseCode="+httpConn.getResponseCode());
			} catch (Exception e) {
				e.printStackTrace();
				Logger.i(TAG, "download begin or end,connect err");
			}finally{
				httpConn.disconnect();
			}

			super.run();
		}
	}
	
	/**
	 * 创建新文件
	 */
	private void createFlagFile(){
		File file=new File(DefaultParameter.FLAG_FILE_PATH);
		if(!file.exists()){
			try {
				file.createNewFile();
				Logger.i(TAG, "create "+file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	private void deleteFile(File file){
		if(file.exists()){
			Logger.i(TAG, "delete "+file.getAbsolutePath());
			file.delete();
		}
	}
	
	
	/**
	 * 多线程断点下载
	 */
	private void breakpointDownload(){
		HttpUtils hu=new HttpUtils();
		httpHandler=hu.download(mFileURL, DefaultParameter.DATA_FILE_PATH, true, new RequestCallBack<File>() {
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Logger.i(TAG, "current="+current);
	        	if(mUpadteMode.equals(MANUAL_UPGRADE)){
	        		String percent = String.format("%2.1f",
							((float) current / (float) total) * 100.0);
	        		if(percent.equals("100.0")){
	        			mTextView.setText(getResources().getString(R.string.check_md5));
	        		}else{
	        			mTextView.setText(percent+"%");
	        		}
	        	}
				super.onLoading(total, current, isUploading);
			}
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Logger.i(TAG, "breakpoint download success");
				mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
				Logger.i(TAG, "breakpoint download failed "+arg1);
				Logger.i(TAG, "remove handler and postdelay");

				if(delayTime>=maxDelay){
					upgradeInfo=null;
					delayTime=minDelay;
					mHandler.removeCallbacks(mUpgradeRunable);
					mHandler.post(mUpgradeRunable);
				}else{
					mHandler.removeCallbacks(mUpgradeRunable);
					mHandler.postDelayed(mUpgradeRunable, delayTime);
				}
				if(mUpadteMode.equals(MANUAL_UPGRADE)){
					/*if (mNetwrokDetector.NetwrokCheck()) {
						mTextView.setText(getResources().getString(R.string.download_failed));
					} else {
						mTextView.setText(getResources().getString(R.string.network_disconnect));
						isNetworkConnected=false;
					}*/
					if(localDialog.isShowing()){
						localDialog.dismiss();
					}
					isDownloadDialogShow=false;
				}else{
					delayTime*=2;
				}
				isDownloading=false;
				if(null!=arg1){
					if(arg1.contains("complete")){
						File updatefile=new File(DefaultParameter.DATA_FILE_PATH);
						if(updatefile.exists()){
							deleteFile(updatefile);
						}
					}else if(arg1.contains("HttpHostConnectException")){
						sendErrorBroadcast(ERROR_DOWNLOAD);
					}else if(arg1.contains("UnknownHostException")){
						sendErrorBroadcast(ERROR_CONNECT);
					}else if(arg1.contains("ENOSPC")){
						sendErrorBroadcast(ERROR_NOSPC);
					}else if(arg1.contains("Not Found")){
						sendErrorBroadcast(ERROR_NOFOUND);
					}
				}

			}
		});
	}
	
	
	/**
	 * 非断点下载
	 */
	private void download(){
		Logger.i(TAG, "ready download upgrade file");
		HttpUtils hu=new HttpUtils();
		hu.download(mFileURL,  DefaultParameter.DATA_FILE_PATH, false, new RequestCallBack<File>() {
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Logger.i(TAG, "current="+current);
	        	if(mUpadteMode.equals(MANUAL_UPGRADE)){
	        		String percent = String.format("%2.1f",
							((float) current / (float) total) * 100.0);
	        		if(percent.equals("100.0")){
	        			mTextView.setText(getResources().getString(R.string.check_md5));
	        		}else{
	        			mTextView.setText(percent+"%");
	        		}
	        	}
				super.onLoading(total, current, isUploading);
			}
			
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Logger.i(TAG, "download upgrade file success");
				mHandler.sendEmptyMessage(MSG_TYPE_DONWLOAD_SUCCESS);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
				Logger.i(TAG, "download upgrade file failed "+arg1);
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
				if(delayTime>=maxDelay){
					upgradeInfo=null;
					delayTime=minDelay;
					mHandler.removeCallbacks(mUpgradeRunable);
					mHandler.post(mUpgradeRunable);
				}else{
					mHandler.removeCallbacks(mUpgradeRunable);
					mHandler.postDelayed(mUpgradeRunable, delayTime);
				}
				if (mUpadteMode.equals(MANUAL_UPGRADE)){
					/*if (mNetwrokDetector.NetwrokCheck()) {
						mTextView.setText(getResources().getString(R.string.download_failed));
					} else {
						mTextView.setText(getResources().getString(R.string.network_disconnect));
						isNetworkConnected=false;
					}*/
					if(localDialog.isShowing()){
						localDialog.dismiss();
					}
					isDownloadDialogShow=false;
				}else{
					delayTime*=2;
				}
				isDownloading = false;
				
			}
		});
	}
	
	
	/**
     * 封装请求体信息
     * 请求体内容,encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    
    private void resetStatus(){
		isSelectDialogShowed=false;
		isDownloadDialogShow=false;
		isDownloading=false;
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void sendErrorBroadcast(int what){
		Intent mIntent=new Intent("android.unionman.action.ERROR_REPORT");
		switch (what){
			case ERROR_DOWNLOAD:
				mIntent.putExtra("code_type","10040");
				Log.i("ERROR_REPORT","code_type 10040");
				break;
			case ERROR_CHECK:
				mIntent.putExtra("code_type","10041");
				Log.i("ERROR_REPORT", "code_type 10041");
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
		if(!isSendErrorReport) {
				isSendErrorReport=true;
				mContext.sendBroadcast(mIntent);
		}
	}
}
