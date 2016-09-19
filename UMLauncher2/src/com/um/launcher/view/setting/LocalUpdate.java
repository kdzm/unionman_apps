
package com.um.launcher.view.setting;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.um.launcher.R;
import com.um.launcher.util.Constant;
import com.um.launcher.util.SocketClient;

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

    public LocalUpdate(Context context, Handler handle, String path) {
        super(context);
        mContext = context;
        mHandler = handle;
        sPath = path;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.local_update, this);
        mSystemOKBtn = (Button) findViewById(R.id.update_btn);
        mSystemCancelBtn = (Button) findViewById(R.id.update_cancel_btn);
        mSystemOKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // updateSystem("/mnt/sda/sda1/update.zip");
                Message msg = mUpdateHandler.obtainMessage(0);
                mUpdateHandler.sendMessage(msg);
                if (Constant.LOG_TAG) {
                    Log.d("debug", "mPath:" + sPath);
                }
            }
        });
        mSystemCancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message message = mHandler.obtainMessage();
                message.what = SystemUpdateDialog.DIALOG_CLOSE;
                mHandler.sendMessageDelayed(message, 100);
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
                    int result = msg.arg1;
                    // String path = (String)msg.obj;
                    String path = sPath;// "/mnt/sda/sda1/update.zip";
                    mProgressDialog.dismiss();
                    if (result == 0) {
                        SocketClient socketClient = null;
                        socketClient = new SocketClient();
                        socketClient.writeMsg("upgrade " + path);
                        socketClient.readNetResponseSync();
                        Intent intent = new Intent(
                                "android.intent.action.MASTER_CLEAR");
                        intent.putExtra("mount_point", path);
                        mContext.sendBroadcast(intent);
                    } else if (result == -1) {
                        Toast tmpToast = Toast.makeText(mContext,
                                "file_check_failed", Toast.LENGTH_SHORT);
                        tmpToast.show();
                    }
                    break;
                }
            }
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
        if (Constant.LOG_TAG) {
            Log.v(TAG, "checkCAUpdateFile:" + file);
        }
        SocketClient socketClient = null;
        socketClient = new SocketClient();
        if (Constant.LOG_TAG) {
            Log.i(TAG, "CaAndroidUpdateFile " + file.getAbsolutePath() + " "
                    + file.getAbsolutePath());
        }
        socketClient.writeMsg("CaAndroidUpdateFile " + file.getAbsolutePath()
                + " " + file.getAbsolutePath());
        int result = socketClient.readNetResponseSync();
        if (result == 0) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "CAUpdateFile Check Success!");
            }
            Message msg = mUpdateHandler.obtainMessage(1);
            msg.arg1 = 0;
            msg.obj = updatePath;
            mUpdateHandler.sendMessage(msg);
        } else if (result == -1) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "CAUpdateFile Check Failed!");
            }
            Message msg = mUpdateHandler.obtainMessage(1);
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
            if (Constant.LOG_TAG) {
                Log.d(TAG, e.toString());
            }
        }
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
        intent.putExtra("mount_point", path);
        mContext.sendBroadcast(intent);
    }

}
