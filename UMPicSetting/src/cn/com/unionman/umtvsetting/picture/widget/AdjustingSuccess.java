package cn.com.unionman.umtvsetting.picture.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * set Adjusting Success
 *
 * @author wangchuanjian
 *
 */
public class AdjustingSuccess extends LinearLayout implements
        View.OnFocusChangeListener {
    // private static final String TAG = "AdjustingSuccess";
    private Handler mHandler;
    // button of adjustSuccess
    private Button adjustSuccessBtn;

    public AdjustingSuccess(Context context, Handler handle) {
        super(context);
        mHandler = handle;
        LayoutInflater mLinflater = LayoutInflater.from(context);
        mLinflater.inflate(R.layout.adjust_success, this);
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {

    }

}
