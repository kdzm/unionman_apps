package com.um.videoplayer.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//import com.um.videoplayer.activity.activity.ActivityVideo;
import com.um.videoplayer.R;
import com.um.videoplayer.activity.BDActivityNavigation;
import com.um.videoplayer.activity.DVDActivityNavigation;
import com.um.videoplayer.activity.base.ActivityFrame;
import com.um.videoplayer.utility.LogTool;

public class ReceiverUSB extends BroadcastReceiver {
    private Activity mActivity;

    public ReceiverUSB(Activity pActivity) {
        mActivity = pActivity;
    }

    @Override
    public void onReceive(Context pContext, Intent pIntent) {
        final String _Action = pIntent.getAction();

        if (_Action.equals(Intent.ACTION_MEDIA_REMOVED)
            || _Action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            doDeviceRemoved();
        }
    }

    private void doDeviceRemoved() {
        LogTool.d("");

        if ((mActivity instanceof BDActivityNavigation) || (mActivity instanceof DVDActivityNavigation)) {
            if (!((ActivityFrame) mActivity).isNetworkFile()) {
                LogTool.e("device removed");
                Toast.makeText(mActivity, R.string.toastDeviceRemoved, Toast.LENGTH_SHORT).show();
                ((ActivityFrame) mActivity).finishActivityWithAnim();
            }
        }
    }
}
