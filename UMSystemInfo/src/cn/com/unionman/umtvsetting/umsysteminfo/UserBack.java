
package cn.com.unionman.umtvsetting.umsysteminfo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.com.unionman.umtvsetting.umsysteminfo.SystemSettingInterface;

import cn.com.unionman.umtvsetting.umsysteminfo.R;

/**
 * Enter the local update, detects the update package Dialog, temporarily not
 * used
 *
 * @author wangchuanjian
 */
public class UserBack extends LinearLayout implements
        View.OnFocusChangeListener {
    // private Context mContext;
    private Handler mHandler;
    // button of OK
    private Button mSystemOKBtn;
    // button of cancel
    private Button mSystemCancelBtn;

    // private LogicFactory mLogicFactory;

    public UserBack(Context context, Handler handle) {
        super(context);
        mContext = context;
        mHandler = handle;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.user_back, this);
        mSystemOKBtn = (Button) findViewById(R.id.user_back_ok);
        mSystemCancelBtn = (Button) findViewById(R.id.user_back_cancel);
        mSystemOKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SystemSettingInterface.restoreDefault();
                Message message = mHandler.obtainMessage();
                message.what = SystemUpdateDialog.DIALOG_CLOSE;
                mHandler.sendMessageDelayed(message, 100);
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
