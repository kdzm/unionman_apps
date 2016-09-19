package com.um.videoplayer.activity.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;

import com.um.videoplayer.R;
import com.um.videoplayer.component.ToastMedia;
import com.um.videoplayer.model.ModelBDInfo;
import com.um.videoplayer.model.ModelDVDInfo;
import com.um.videoplayer.receiver.ReceiverNetwork;
import com.um.videoplayer.receiver.ReceiverUSB;
import com.um.videoplayer.utility.DialogTool;
import com.um.videoplayer.utility.LogTool;

/**
 * ActivityFrame: business dependent
 */
public class ActivityFrame extends ActivityBase {
    protected ModelBDInfo mModelBDInfo;

    protected ModelDVDInfo mModelDVDInfo;

    protected int mScreenWidth;

    protected int mScreenHeitht;

    protected ToastMedia mToastMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window _Window = getWindow();
        _Window.setFormat(PixelFormat.RGBA_8888);
        // get screen size
        Display _Display = getWindowManager().getDefaultDisplay();
        Point _Point = new Point();
        _Display.getSize(_Point);
        mScreenWidth = _Point.x;
        mScreenHeitht = _Point.y;
        mToastMedia = ToastMedia.getInstance(this);
    }

    public void showToastMedia(int pMsgId) {
        showToastMedia(getString(pMsgId));
    }

    protected void showToastMedia(String pMsg) {
        mToastMedia.setText(pMsg);
        mToastMedia.show();
    }

    protected void hideStatusbar() {
        Intent _Intent = new Intent();
        _Intent.setComponent(new ComponentName("com.android.systemui",
                                               "com.android.systemui.SystemUIService"));
        _Intent.putExtra("starttype", 1);
        startService(_Intent);
    }

    protected void showStatusbar() {
        Intent _Intent = new Intent();
        _Intent.setComponent(new ComponentName("com.android.systemui",
                                               "com.android.systemui.SystemUIService"));
        startService(_Intent);
    }

    protected void pauseMediaScanner() {
        sendBroadcast(new Intent("MEDIA_SCANNER_PAUSE"));
        LogTool.d("");
    }

    protected void continueMediaScanner() {
        sendBroadcast(new Intent("MEDIA_SCANNER_CONTINUE"));
        LogTool.d("");
    }

    /**
     * get default setting dialog
     * @param pTitleId dialog title id
     * @param pView dialog view
     * @return dialog
     */
    protected Dialog getDefaultSettingDialog(int pTitleId, View pView) {
        Dialog _Dialog = new Dialog(this, R.style.styleDialog);
        _Dialog.setTitle(pTitleId);
        _Dialog.setContentView(pView);
        return _Dialog;
    }

    protected void initReceiver(BroadcastReceiver pReceiver) {
        if (pReceiver == null) {
            return;
        }

        IntentFilter _IntentFilter = new IntentFilter();

        if (pReceiver instanceof ReceiverNetwork) {
            _IntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
        else if (pReceiver instanceof ReceiverUSB) {
            _IntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            _IntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        }

        registerReceiver(pReceiver, _IntentFilter);
    }

    protected void destroyReceiver(BroadcastReceiver pReceiver) {
        if (pReceiver == null) {
            return;
        }

        unregisterReceiver(pReceiver);
    }

    protected void showLoadProgressDialog() {
        //modify loading by jly 20140317
        //      showProgressDialog(R.string.dialogTitleLoading, R.string.dialogMessageLoading);
        showProgressDialog();
        DialogTool.disableBackgroundDim(mProgressDialog);
        //      NetStateDialog.createDialog(ActivityFrame.this).show();
        //      DialogTool.disableBackgroundDim(NetStateDialog.createDialog(ActivityFrame.this));
    }

    protected void togglePauseBackgroundMusic(Context pContext) {
        LogTool.d("");
        Intent _Intent = new Intent();
        _Intent.setAction("com.um.music.musicservicecommand.togglepause");
        _Intent.putExtra("command", "togglepause");
        pContext.sendBroadcast(_Intent);
    }

    protected void pauseBackgroundMusic(Context pContext) {
        LogTool.d("");
        Intent _Intent = new Intent();
        _Intent.setAction("com.um.music.musicservicecommand.pause");
        _Intent.putExtra("command", "pause");
        pContext.sendBroadcast(_Intent);
    }

    protected void startActivityWithAnim(Intent pIntent) {
        LogTool.d("");

        if (mModelBDInfo != null) {
            pIntent.putExtra("bd", mModelBDInfo);
        }
        else if (mModelDVDInfo != null) {
            pIntent.putExtra("dvd", mModelDVDInfo);
        }

        startActivity(pIntent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public void finishActivityWithAnim() {
        LogTool.d("");
        finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public boolean isNetworkFile() {
        if (mModelBDInfo == null) {
            return false;
        }

        return mModelBDInfo.isNetworkFile();
    }
}
