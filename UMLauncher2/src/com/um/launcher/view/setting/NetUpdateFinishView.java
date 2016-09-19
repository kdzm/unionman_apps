
package com.um.launcher.view.setting;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
 * The download is complete, whether to upgrade Dialog
 *
 * @author wangchuanjian
 */
public class NetUpdateFinishView extends LinearLayout implements
        View.OnFocusChangeListener {
    private static Context mContext;
    private Handler mHandler;
    // button of OK
    private Button mSystemOKBtn;
    // button of cancel
    private Button mSystemCannelBtn;

    // private LogicFactory mLogicFactory;

    public NetUpdateFinishView(Context context, Handler handle) {
        super(context);
        mContext = context;
        mHandler = handle;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.net_update_finish, this);
        mSystemOKBtn = (Button) findViewById(R.id.update_ok_btn);
        mSystemOKBtn.requestFocus();
        mSystemOKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Restart the computer to complete the upgrade
                Message msg = mUpdateHandler.obtainMessage(0);
                mUpdateHandler.sendMessage(msg);
            }
        });
        mSystemCannelBtn = (Button) findViewById(R.id.update_cancel_btn);
        mSystemCannelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message message = mHandler.obtainMessage();
                message.what = SystemUpdateDialog.DIALOG_CLOSE;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
    }

    private static final String TAG = "Update";
    private static ProgressDialog mProgressBar = null;

    /**
     * handler of update
     */
    final static Handler mUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    String path = Environment.getExternalStorageDirectory()
                            .getPath();// "/mnt/sda/sda1" ; //+ "/"+fileName

                    mProgressBar = new ProgressDialog(mContext);
                    mProgressBar.setTitle("file_check");
                    mProgressBar.setMessage("file_checking");
                    mProgressBar.setCancelable(false);
                    mProgressBar.show();
                    mThread = new checkCAUpdateThread(path);
                    mThread.start();
                    break;
                }
                case 1: {
                    int result = msg.arg1;
                    // String path = (String)msg.obj;
                    String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String path = directory + "/update.zip"; // ""/mnt/sda/sda1/update.zip";
                    mProgressBar.dismiss();
                    if (result == 0) {
                        SocketClient socketClient = null;
                        socketClient = new SocketClient();
                        socketClient.writeMsg("upgrade " + directory);
                        socketClient.readNetResponseSync();
                        Intent intent = new Intent(
                                "android.intent.action.MASTER_CLEAR");
                        intent.putExtra("mount_point", directory);
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

    /**
     * thread of check update
     *
     * @author huyq
     */
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
}
