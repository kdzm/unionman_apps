
package com.um.launcher.view.setting;

import java.io.File;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.um.launcher.R;
import com.um.launcher.util.Constant;
import com.um.launcher.view.setting.SystemUpdateDialog.UpdateCallback;

/**
 * Enter the Dialog network upgrade
 *
 * @author wangchuanjian
 */
public class NetUpdate extends LinearLayout implements
        View.OnFocusChangeListener {
    private static final String TAG = "NetUpdate";
    private Handler mHandler;
    private Context mContext;
    // private LogicFactory mLogicFactory;
    // button of cancel
    private Button mSystemCancelBtn;
    // text of netUpdate
    private TextView mNetUpdateText;
    // seekBar of netUpdate
    private SeekBar mNetUpdateSeekBar;
    // DownloadManager object
    private DownloadManager mDownloadManager = null;
    // private DownloadChangeObserver downloadObserver;
    // last download id
    private long mLastDownloadId = 0;

    // file path of server
    private static String serverFilePath = null;
    // file name
    private String fileName = "update.zip";
    private static boolean mCheckSd = false;
    private boolean isDownloadFinished = false;
    private static final long UPDATE_TIME_DELTA = 2000;
    private static final int MsgRefreshProgress = 10001;
    private static final int SpaceIsNotEnough = 10002;
    private static final int SdcardNotExist = 10003;
    private Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MsgRefreshProgress:
                    int progress = msg.arg1;
                    mNetUpdateSeekBar.setProgress(progress);
                    mNetUpdateText.setText("" + progress + "%");
                    break;
                case SpaceIsNotEnough:
                    Toast.makeText(mContext, R.string.sdcard_available_space_not_enough,Toast.LENGTH_SHORT).show();
                break;
                case SdcardNotExist:
                    Toast.makeText(mContext, R.string.sdcard_not_exist, Toast.LENGTH_SHORT).show();
                     break;
            }
        }
    };
    private static final String UpdateSpName = "update_download";
    private static final String UpdateSpDownloadidName = "last_download_id";
    public NetUpdate(Context context, Handler handle) {
        super(context);
        mContext = context;
        mHandler = handle;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.net_update, this);
        mNetUpdateSeekBar = (SeekBar) findViewById(R.id.net_update_seekbar);
        mNetUpdateText = (TextView) findViewById(R.id.net_update_text);
        mSystemCancelBtn = (Button) findViewById(R.id.net_update_cancel_btn);
        mSystemCancelBtn.requestFocus();

        String serviceString = Context.DOWNLOAD_SERVICE;
        mDownloadManager = (DownloadManager) mContext
                .getSystemService(serviceString);
        if(null == serverFilePath)
    stopUpdate(true);
        Log.d(TAG,  " serverFilePath = "+serverFilePath);
        Uri uri = Uri.parse(serverFilePath);
        if (mkdir(Environment.getExternalStorageDirectory().getPath())) {// Environment.DIRECTORY_DOWNLOADS)
            SharedPreferences sp = mContext.getSharedPreferences(UpdateSpName,
                    Context.MODE_WORLD_WRITEABLE);
            mLastDownloadId = sp.getLong(UpdateSpDownloadidName, 0);
            Log.d(TAG, "===LastDownloadId in SP :  " + mLastDownloadId + "=====");
            deleteFile();
            mLastDownloadId = mDownloadManager
                    .enqueue(new DownloadManager.Request(uri)
                            .setAllowedNetworkTypes(
                                    DownloadManager.Request.NETWORK_MOBILE
                                            | DownloadManager.Request.NETWORK_WIFI)
                            .setAllowedOverRoaming(false)
                            .setDestinationInExternalPublicDir("", fileName));// Environment.getExternalStorageDirectory().getPath()
            Log.d(TAG, "===LastDownloadId new Request :  " + mLastDownloadId + "=====");

            mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(MsgRefreshProgress, 0, 0));
            sp.edit().putLong(UpdateSpDownloadidName, mLastDownloadId).commit();
            mUpdateProgressThread.start();
        } else {
            if (Constant.LOG_TAG) {
                Log.v(TAG, "can't find sdcard");
            }
        }

    }
    public static void setserverFilePath(String serverpath)
    {
    serverFilePath = serverpath;
    Log.d(TAG, "serverFilePath:" + serverFilePath);
    }

    public void stopUpdate(boolean success) {
        Log.d(TAG, "-----------------stopUpdate:" + success);
        mIsActive = false;
        mUpdateHandler.removeMessages(MsgRefreshProgress);
        if (!success) {
            deleteFile();
        }
    }

    private UpdateCallback mUpdateCallback = null;

    public void setUpdateCallback(UpdateCallback callback) {
        mUpdateCallback = callback;
    }

    private void deleteFile() {
        synchronized (SystemUpdateDialog.mDownloadLock) {
            File cancelFile = new File(Environment.getExternalStorageDirectory()
                    .getPath(), fileName);
            if (Constant.LOG_TAG) {
                Log.v(TAG, "delete file=:" + cancelFile);
            }
            Log.d(TAG, "===Remove LastDownloadId :  " + mLastDownloadId + "=====");
            mDownloadManager.remove(mLastDownloadId);
            if (cancelFile.exists()) {
                boolean deleteSucce = cancelFile.delete();
                if (Constant.LOG_TAG) {
                    Log.v(TAG, "delete file=:" + fileName + ":" + deleteSucce);
                }
            }
        }

    }

    /**
     * Check the SD space is enough
     *
     * @param c
     * @return
     */
    private boolean checkSDCard(Cursor c) {
        int fileSizeIdx = c.getInt(c
                .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        if (Constant.LOG_TAG) {
            Log.v(TAG, "totoal file:" + fileSizeIdx + ":sdfreesize:"
                    + getSDFreeSize());
        }
        if (fileSizeIdx == -1)
            return false;
        if (fileSizeIdx >= getSDFreeSize()) {
            // available space not enough
            mUpdateHandler.sendEmptyMessage(SpaceIsNotEnough);
            return false;
        }

        return true;
    }

    /**
     * Gets the remaining space
     *
     * @return
     */
    public long getSDFreeSize() {
        // Get the SD card file path
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // To obtain a single data block size (Byte)
        long blockSize = sf.getBlockSize();
        // The amount of free data blocks
        long freeBlocks = sf.getAvailableBlocks();
        // Returns the size of the SD card free
        return freeBlocks * blockSize - 10 * 1024 * 1024; // unit Byte leave 10M
    }

    /**
     * Create folder
     *
     * @param folderName
     * @return
     */
    private boolean mkdir(String folderName) {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // deleteFile();
            File folder = new File(folderName);
            return (folder.exists() && folder.isDirectory()) ? true : folder
                    .mkdirs();
        } else {
            // sd not exist
            mUpdateHandler.sendEmptyMessage(SdcardNotExist);
            return false;
        }

    }

    private boolean mIsActive = true;
    private Thread mUpdateProgressThread = new Thread() {
        private DownloadManager.Query query = null;

        @Override
        public void run() {
            while (mIsActive && !isDownloadFinished) {
                try {
                    Thread.currentThread().sleep(UPDATE_TIME_DELTA);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                SharedPreferences sp = mContext.getSharedPreferences(UpdateSpName,
                        Context.MODE_WORLD_READABLE);
                long lastId = sp.getLong(UpdateSpDownloadidName, mLastDownloadId);
                if (lastId != mLastDownloadId) {
                    Log.d(TAG, "run---Shared-last-download-id:" + lastId + "----mLastDownloadId:"
                            + mLastDownloadId);
                    break;
                }
                double download = 0;
                double size = 0;
                query = new DownloadManager.Query();
                query.setFilterById(mLastDownloadId);
                Cursor mCursor = mDownloadManager.query(query);
                if (mCursor.equals(null))
                    break;
                if (mCursor != null && mCursor.moveToFirst()) {
                    int status = mCursor.getInt(mCursor
                            .getColumnIndex(DownloadManager.COLUMN_STATUS));

                    int reasonIdx = mCursor
                            .getColumnIndex(DownloadManager.COLUMN_REASON);
                    int titleIdx = mCursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                    int fileSizeIdx = mCursor
                            .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    int bytesDLIdx = mCursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    String title = mCursor.getString(titleIdx);
                    int fileSize = mCursor.getInt(fileSizeIdx);
                    int bytesDL = mCursor.getInt(bytesDLIdx);

                    // Translate the pause reason to friendly text.
                    int reason = mCursor.getInt(reasonIdx);
                    StringBuilder sb = new StringBuilder();
                    sb.append(title).append("\n");
                    sb.append("Downloaded ").append(bytesDL).append(" / ")
                            .append(fileSize);

                    // Check whether a space is enough
                    if (!mCheckSd) {
                        mCheckSd = checkSDCard(mCursor);
                    }
                    mCursor.close();

                    // Display the status

                    download = bytesDL;
                    size = fileSize;
                    int progress = (int) (download * 100 / size);
                    if (size <= 0) {
                        progress = 0;
                    }
                    Log.d(TAG, "=============query: " + progress + " ==== status: " + status + "====" + Thread.currentThread().getId() + "==");
                    mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(MsgRefreshProgress,
                            progress, 0));

                    switch (status) {
                        case DownloadManager.STATUS_PAUSED:
                            Log.v(TAG, "STATUS_PAUSED");
                        case DownloadManager.STATUS_PENDING:
                            Log.v(TAG, "STATUS_PENDING");
                        case DownloadManager.STATUS_RUNNING:
                            // Downloading, don't do anything
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            // finish
                            Log.v(TAG, "STATUS_SUCCESSFUL");
                            isDownloadFinished = true;
                            mUpdateHandler.removeMessages(MsgRefreshProgress);
                            // mDownloadManager.remove(mLastDownloadId);

                            Message message = mHandler.obtainMessage();
                            message.what = SystemUpdateDialog.DIALOG_GOTO_NEXT;
                            mHandler.sendMessageDelayed(message, 100);
                            stopUpdate(true);
                            break;
                        case DownloadManager.STATUS_FAILED:
                            Log.v(TAG, "STATUS_FAILED");
                            // Remove downloaded content, re Download
                            mUpdateHandler.removeMessages(MsgRefreshProgress);
                            isDownloadFinished = true;
                            // mDownloadManager.remove(mLastDownloadId);
                            if (mUpdateCallback != null) {
                                mUpdateCallback.onDownloadFailed();
                            }
                            stopUpdate(false);
                            break;
                    }

                }

            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (Constant.LOG_TAG) {
                Log.d(TAG,
                        "dispatchKeyEvent : event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER");
            }
            Message message = mHandler.obtainMessage();
            message.what = SystemUpdateDialog.DIALOG_CLOSE;
            mHandler.sendMessageDelayed(message, 100);
            mUpdateHandler.removeMessages(0);
            deleteFile();
            return true;
        } else {
            return true;
        }
        // Returns the true, does not respond to other key

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

}
