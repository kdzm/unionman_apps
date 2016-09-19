
package cn.com.unionman.umtvsetting.umsysteminfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.com.unionman.umtvsetting.umsysteminfo.SocketClient;

import cn.com.unionman.umtvsetting.umsysteminfo.R;
import cn.com.unionman.umtvsetting.umsysteminfo.utils.Utils;
import android.os.Environment;
import android.os.StatFs;

/**
 * Enter the local update, detects the update package Dialog, temporarily not
 * used
 *
 * @author wangchuanjian
 */
public class LocalUpdate extends LinearLayout implements
        View.OnFocusChangeListener {
    private static final String TAG = "LocalUpdate";
    private static Context mContext;
    private Handler mHandler;

    // button of OK
    private Button mSystemOKBtn;
    // button of Cancel
    private Button mSystemCancelBtn;

    // update data path
    private static String sPath;
    // private LogicFactory mLogicFactory;
    private static ProgressDialog mProgressDialog = null;
    
    private final static String SDPATH="/storage/emulated/0/recovery2";
    
    private final static String CACHEDIR="/cache2";
    
    private final static String UPDATEFILENAME="/update.zip";
    
    private final static String UPDATE_FROM_NAME="/mnt/sda1/update.zip";
    
    private static File updataZip ;

    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = mContext.getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            Message message = mHandler.obtainMessage();
            message.what = SystemUpdateDialog.DIALOG_CLOSE;
            mHandler.sendMessage(message);
        }
    };

    public LocalUpdate(Context context, Handler handle, String path) {
        super(context);
        mContext = context;
        mHandler = handle;
        sPath = path;
		Log.d(TAG, "sPath:"+sPath);
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.local_update2, this);
        mCountDownTimer.start();
        mSystemOKBtn = (Button) findViewById(R.id.update_btn);
        mSystemCancelBtn = (Button) findViewById(R.id.update_cancel_btn);
        mSystemCancelBtn.requestFocus();
        mSystemOKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // updateSystem("/mnt/sda/sda1/update.zip");
                Message msg = mUpdateHandler.obtainMessage(0);
                mUpdateHandler.sendMessage(msg);
                mCountDownTimer.cancel();

                    Log.d("debug", "mPath:" + sPath);

            }
        });
        mSystemCancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message message = mHandler.obtainMessage();
                message.what = SystemUpdateDialog.DIALOG_CLOSE;
                mHandler.sendMessage(message);
                mCountDownTimer.cancel();
            }
        });
    }

    /**
     * handler of update
     */
	final static Handler mUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				Log.i("localupdate" , "sPath =" + sPath);
				String path = sPath;// "/mnt/sda/sda1" ; //+ "/"+fileName
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setTitle("file_check");
				mProgressDialog.setMessage("file_checking");
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
				mThread = new checkCAUpdateThread(path);
				mThread.start();
				break;
			}
			case 1: {
				int result = msg.arg2;
				 mProgressDialog.dismiss();
				 
				 String updatePath = SDPATH + UPDATEFILENAME;
				 
				if (result == 0) {
					SocketClient socketClient = null;
					socketClient = new SocketClient();
					socketClient.writeMsg("upgrade " + updatePath);
					socketClient.readNetResponseSync();
//					Intent intent = new Intent(
//							"android.intent.action.MASTER_CLEAR");
//					intent.putExtra("mount_point", path);
//					mContext.sendBroadcast(intent);
					
					try {
						RecoverySystem.installPackage(mContext, new File(updatePath));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (result == -1) {
					Toast tmpToast = Toast.makeText(mContext,
							"file_check_failed", Toast.LENGTH_SHORT);
					tmpToast.show();
				}
				break;
			}
			case 2: {

				int result = msg.arg1;
				mProgressDialog.dismiss();
				mProgressDialog = new ProgressDialog(mContext);

				String title = mContext.getResources().getString(R.string.file_copy);
				String content = mContext.getResources().getString(R.string.interrupt_power);
				mProgressDialog.setTitle(title);
				mProgressDialog.setMessage(content);

				mProgressDialog.setTitle(title);
				mProgressDialog.setMessage(content);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
				if (result == 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							Message msg=null;
							msg = mUpdateHandler.obtainMessage(1);
							try {
								int ret = copyFile(sPath+"/update.zip", SDPATH + UPDATEFILENAME);
								
								if (ret == 0) {
									msg.arg2 = 0;
									Log.i("localupdate", "copyFile   success");
								}else {
									Toast.makeText(mContext, R.string.update_interrupt, Toast.LENGTH_SHORT).show();
									return;
								}
								
							} catch (Exception e) {
								Log.i("localupdate" , "Exception  here");
								msg.arg2 = -1;
							}
							Log.i("localupdate", " sendMessage  1 ");
							mUpdateHandler.sendMessage(msg);
						}
					}).start();
				} 
				break;
			}
			}
		}

		private int copyFiles(){
			
			File dir = new File(SDPATH);
			
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			try {
				SocketClient socketClient = null;
				socketClient = new SocketClient();
				socketClient.writeMsg("system busybox cp "+sPath+"/update.zip /storage/emulated/0/recovery2/update.zip");
				socketClient.readNetResponseSync();
			} catch (Exception e) {
				return  -1;
			}
			return 0;
		}
	    
		private int copyFile(String from, String to) {
			FileInputStream in = null;
			FileOutputStream out = null;
			FileChannel inChannel = null;
			FileChannel outChannel = null;
			
			File dir = new File(SDPATH);
			dir.mkdirs();
			updataZip = new File(to);
			if(updataZip.exists()) {
				Log.i("localupdate" , "exists   run here");
				return 0;}
			try {
				in = new FileInputStream(from);
				out = new FileOutputStream(updataZip);
				inChannel = in.getChannel();
				outChannel = out.getChannel();
//				byte[] buffer = new byte[1024 * 1024];
//				ByteBuffer buffer = ByteBuffer.allocate(4096);
//				long i = 0;
//				int length;
//				while ((length = inChannel.read(buffer)) != -1) {
//					Log.i("localupdate" , "i =" + i++);
//					buffer.flip();
//					outChannel.write(buffer);
//					buffer.clear();
//
//				}
				inChannel.transferTo(0 , inChannel.size() , outChannel);
				return 0;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
					inChannel.close();
					out.close();
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return -1;

		}
	};

    private static checkCAUpdateThread mThread = null;

    private static class checkCAUpdateThread extends Thread {
        public String updatePath = null;

        public checkCAUpdateThread(String path) {
            this.updatePath = path;
        }

        public void run() {
            checkCAUpdateFile(updatePath);
        }
    }

    /**
     * check update file
     *
     * @param updatePath
     */
    public static void checkCAUpdateFile(String updatePath) {
        File file = new File(updatePath + "/update.zip");

            Log.v(TAG, "checkCAUpdateFile:" + file);

        SocketClient socketClient = null;
        socketClient = new SocketClient();

            Log.i(TAG, "CaAndroidUpdateFile " + file.getAbsolutePath() + " "
                    + file.getAbsolutePath());

        socketClient.writeMsg("CaAndroidUpdateFile " + file.getAbsolutePath()
                + " " + file.getAbsolutePath());
        int result = socketClient.readNetResponseSync();
        if (result == 0) {

                Log.i(TAG, "CAUpdateFile Check Success!");

            Message msg = mUpdateHandler.obtainMessage(2);
            msg.arg1 = 0;
            msg.obj = updatePath;
            mUpdateHandler.sendMessage(msg);
        } else if (result == -1) {

                Log.i(TAG, "CAUpdateFile Check Failed!");

            Message msg = mUpdateHandler.obtainMessage(2);
            msg.arg1 = -1;
            msg.obj = updatePath;
            mUpdateHandler.sendMessage(msg);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    /**
     * update
     *
     * @param path
     */
    private void updateSystem(String path) {
        SocketClient socketClient = new SocketClient();
        socketClient.writeMsg("upgrade " + path);
        try {
            socketClient.readNetResponseSync();
        } catch (Exception e) {

                Log.d(TAG, e.toString());

        }
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
        intent.putExtra("mount_point", path);
		intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mContext.sendBroadcast(intent);
    }

}
