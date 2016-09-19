package com.um.tv.menu.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.um.tv.menu.R;

/**
 * enter AdjustingFailed
 *
 * @author wangchuanjian
 *
 */
public class AdjustingFailed extends LinearLayout implements
        View.OnFocusChangeListener {
    public AdjustingFailed(Context context, Handler handle) {
        super(context);
        LayoutInflater mLinflater = LayoutInflater.from(context);
        mLinflater.inflate(R.layout.adjust_failed, this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

}
