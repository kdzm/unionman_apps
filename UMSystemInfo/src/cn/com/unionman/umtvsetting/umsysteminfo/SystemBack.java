
package cn.com.unionman.umtvsetting.umsysteminfo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.com.unionman.umtvsetting.umsysteminfo.SocketClient;

import cn.com.unionman.umtvsetting.umsysteminfo.R;

/**
 * Enter the local update, detects the update package Dialog, temporarily not
 * used
 *
 * @author wangchuanjian
 */
public class SystemBack extends LinearLayout implements
        View.OnFocusChangeListener {
    private Context mContext;
    private Handler mHandler;
    private Button mSystemOKBtn;
    private Button mSystemCancelBtn;


    public SystemBack(Context context, Handler handle) {
        super(context);
        mContext = context;
        mHandler = handle;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.system_back, this);
        mSystemOKBtn = (Button) findViewById(R.id.back_ok_btn);
        mSystemCancelBtn = (Button) findViewById(R.id.back_cancel_btn);
        mSystemOKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SocketClient socketClient = null;
                socketClient = new SocketClient();
                socketClient.writeMsg("reset");
                socketClient.readNetResponseSync();
                mContext.sendBroadcast(new Intent(
                        "android.intent.action.MASTER_CLEAR"));
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }
}
