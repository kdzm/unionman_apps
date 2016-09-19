package cn.com.unionman.umtvsetting.picture.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * VGA automatically adjusts the success, failure pop-up box
 *
 * @author wangchuanjian
 *
 */
public class VGAAdjustingDialog extends Dialog {
    // close Dialog
    public final static int DIALOG_CLOSE = 5;
    // ADJUSTTING
    public final static int ADJUSTING = 0;
    // adjust failed
    public final static int ADJUST_FAILED = 1;
    // adjust success
    public final static int ADJUST_SUCCESS = 2;
    private Context mContext;
    // private int mAdjustFlag = -1;

    /**
     * handler of dismiss dialog
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == VGAAdjustingDialog.DIALOG_CLOSE) {
                dismiss();
            }
        }
    };

    public VGAAdjustingDialog(Context context, int flag) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        setContent(flag);
    }

    private void setContent(int flag) {
        // mAdjustFlag = flag;
        switch (flag) {
        case ADJUSTING:
            changeDialogLength(5000);
            setContentView(new Adjusting(mContext, mHandler));
            break;
        case ADJUST_FAILED:
            // Automatic adjusting failure
            changeDialogLength(500);
            setContentView(new AdjustingFailed(mContext, mHandler));
            break;
        case ADJUST_SUCCESS:
            // Automatic adjusting success
            changeDialogLength(500);
            setContentView(new AdjustingSuccess(mContext, mHandler));
            break;

        default:
            break;
        }
    }

    /**
     * change dialog length
     */
    public void changeDialogLength(int timeout) {
        getWindow().getAttributes().width = 600;
        getWindow().getAttributes().height = 400;
        getWindow().setAttributes(getWindow().getAttributes());
        mHandler.removeMessages(DIALOG_CLOSE);
        mHandler.sendEmptyMessageDelayed(DIALOG_CLOSE, timeout);
    }

}
