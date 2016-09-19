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
 * enter AdjustingFailed
 *
 * @author wangchuanjian
 *
 */
public class Adjusting extends LinearLayout implements
        View.OnFocusChangeListener {
    public Adjusting(Context context, Handler handle) {
        super(context);
        LayoutInflater mLinflater = LayoutInflater.from(context);
        mLinflater.inflate(R.layout.adjustting, this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

}
