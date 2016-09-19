
package cn.com.unionman.umtvsetting.umsysteminfo;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.com.unionman.umtvsetting.umsysteminfo.R;

/**
 * Enter the local update
 *
 * @author wangchuanjian
 */
public class LocalUpdateNone extends LinearLayout implements
        View.OnFocusChangeListener {
    private Handler mHandler;
    // private Context mContext;
    // button of confirm
    private Button confirmBtn;
    
    private Context mContext;
    
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = mContext.getString(R.string.ok) + "(" + millisUntilFinished/1000 + "s)";
            confirmBtn.setText(str);
        }

        public void onFinish() {
            Message message = mHandler.obtainMessage();
            message.what = SystemUpdateDialog.DIALOG_CLOSE;
            mHandler.sendMessage(message);
        }
    };

    // private LogicFactory mLogicFactory;

    public LocalUpdateNone(Context context, Handler handle) {
        super(context);
        mContext = context;
        mHandler = handle;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.local_update_none2, this);
        mCountDownTimer.start();
        
        confirmBtn = (Button) findViewById(R.id.no_update_btn);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message message = mHandler.obtainMessage();
                message.what = SystemUpdateDialog.DIALOG_CLOSE;
                mHandler.sendMessage(message);
                mCountDownTimer.cancel();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

}
