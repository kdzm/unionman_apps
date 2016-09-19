package com.um.usbupgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//import com.hisilicon.android.hisysmanager.HiSysManager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.os.SystemProperties;

public class UsbUpgradeService extends Service {
	private static final String TAG="UsbUpgrade--UsbUpgradeService";
	private String PATH = "/cache/";
	private String mFileFromPath="";
    private TextView mTextView;
    private AlertDialog.Builder localBuilder = null;
	private Dialog localDialog = null;
	private int copyLength=0;
	
	private BroadcastReceiver usbBroadcastReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				Log.v(TAG, "usb disk is unmounted");
				deleteFile(new File(PATH+"/update.bak"));
				if(localDialog!=null){
					if(localDialog.isShowing()){
						handler.sendEmptyMessage(1);
						localDialog.dismiss();
					}
				}
			} 
		}
	};
	private void showProcessDialog(Context context){
		mTextView=new TextView(context);
		localBuilder = new Builder(context);
		localBuilder.setTitle("系统升级");
		localBuilder.setMessage("正在拷贝升级文件，请勿关闭电源和插拔U盘");
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextSize(25);
		localBuilder.setView(mTextView);
		localDialog = localBuilder.create();
		localDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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
	}
	
	 private Handler handler=new Handler(){
	    	public void handleMessage(Message msg){
	    		if (!Thread.currentThread().isInterrupted()){
	    			switch (msg.what){
	    			case 0:
	    				mTextView.setText("即将重启进行升级");
//	    				HiSysManager hisys = new HiSysManager();
//						hisys.upgrade(PATH+"/update.zip");
						SocketClient socketClient = null;
						socketClient = new SocketClient();
						socketClient.writeMsg("upgrade " + PATH+"/update.zip");
						socketClient.readNetResponseSync();
						try{
							Log.i(TAG, "installPackage");
							RecoverySystem.installPackage(getApplicationContext(),new File(PATH+"/update.zip"));
						}catch(Exception e){
							e.printStackTrace();
						}
	    				if(localDialog!=null){
	    					if(localDialog.isShowing()){
	    						localDialog.dismiss();
	    					}
	    				}
	    				break;
	    			case 1:
	    				ToastUtil.showToast(getApplicationContext(), "拷贝失败");
	    				break;
	    			case 2:
	    				int x=copyLength*100/10000;
	    				if(x>100){
	    					x=100;
	    					mTextView.setText(x+"%");
	    				}else{
	    					mTextView.setText(x+"%");
	    				}
	    				break;
	    			case 3:
	    				mTextView.setText("拷贝成功，请等待...");
	    				break;
	    			default:
	    				break;
	    			}
	    		}
	    	}
	    };
	
	private void copyFile(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileOutputStream fos=null;
				FileInputStream fis=null;
				copyLength = 0;
				try{
					fos=new FileOutputStream(PATH+"update.bak");//写文件
					fis=new FileInputStream(mFileFromPath+"/update.zip");//读文件
					byte[] buf=new byte[fis.available()/10000];//根据文件字节流设定缓冲区的大小
					int length=0;
					while((length = fis.read(buf))!=-1){
						fos.write(buf,0,length);
						copyLength += 1;				
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
					}
					handler.sendEmptyMessage(3);
					File file=new File(PATH+"update.bak");
					if(file.exists()){
						boolean result=rename(file, "update.zip");
						if(result){
							handler.sendEmptyMessage(0);
						}
					}else{
						Log.i(TAG, "update.bak unexists");
					}
				}catch(IOException e){
					handler.sendEmptyMessage(1);
					deleteFile(new File(PATH+"update.bak"));
					localDialog.dismiss();
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(fis!=null)
							fis.close();
					}
					catch(IOException e)
					{
						throw new RuntimeException("读取关闭失败");
					}
					try
					{
						if(fos!=null)
							fos.close();
					}
					catch(IOException e)
					{
						throw new RuntimeException("写入关闭失败");
					}
				}
			}
		}).start();
	}
	
	private boolean rename(File file,String name){
		boolean result=false;
		String newPath=file.getParentFile().getAbsoluteFile()+"/"+name;
		result=file.renameTo(new File(newPath));
		return result;
	}
	
	private void deleteFile(File file){
		if(file.exists()){
			file.delete();
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(!SystemProperties.get("persist.sys.usb.upgrade", "false").equals("true")){
			return super.onStartCommand(intent, flags, startId);
		}
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		filter.addDataScheme("file");
		registerReceiver(usbBroadcastReceiver, filter);
		
		if(intent!=null){
			mFileFromPath=intent.getStringExtra("filepath");
		}
		if(mFileFromPath!=null){
			if(!mFileFromPath.equals("")){
				File file=new File(PATH+"update.zip");
				if(file.exists()){
					deleteFile(file);
				}
				showProcessDialog(UsbUpgradeService.this);
				copyFile();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(!SystemProperties.get("persist.sys.usb.upgrade", "false").equals("true")){
			super.onDestroy();
			return ;
		}
		unregisterReceiver(usbBroadcastReceiver);
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
