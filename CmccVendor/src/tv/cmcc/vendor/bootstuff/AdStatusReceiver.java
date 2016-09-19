package tv.cmcc.vendor.bootstuff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hjian on 2015/7/13.
 */
public class AdStatusReceiver extends BroadcastReceiver{
    private static final String TAG = "AdStatusReceiver";
    private static final String ACTION_DRUMBEATINGCHANGE = "android.intent.action.DRUMBEATINGCHANGE";
    private static final String ACTION_DRUMBEATINGRECIEVE = "android.intent.action.DRUMBEATINGRECIEVE";


    private static final String DOWNLOAD_ID = "DOWNLOAD_ID";
    private static final String VERSION_ID = "VERSION_ID";

    private static final String  STATUS_ID = "STATUS_ID";
    private static final String  RECIEVE_ID = "RECIEVE_ID";




    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "action: " + intent.getAction());
        if (intent.getAction().equals(ACTION_DRUMBEATINGCHANGE)) {
            int downloadId = intent.getIntExtra(DOWNLOAD_ID, 1);
            long versionId = intent.getIntExtra(VERSION_ID, 1);
            boolean ret = UpdateUtil.updateBootStuff(context, downloadId, versionId);
            sentStatusResult(context, downloadId, ret);
        }
    }

    private void sentStatusResult(Context context, int downloadId, boolean statusId) {
        Intent intent = new Intent(ACTION_DRUMBEATINGRECIEVE);
        intent.putExtra(RECIEVE_ID, downloadId);
        intent.putExtra(STATUS_ID, statusId);
        context.sendBroadcast(intent);
    }
}
