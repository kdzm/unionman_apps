package com.um.atv.widget;
import com.um.atv.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 频道列表编辑dialog，可以移动，删除选中项
 *
 */
public class ChannelEditDialog extends Dialog implements
        android.view.View.OnClickListener {
    private Context mContext;
    private Button positiveBtn;
    private Button negativeBtn;

    // 是否移动所选频道
    private TextView promptTxt;
    // 对话框标志 false=move true=delete
    private boolean isMoveFlag = false;
    private ChannelEditListener mChannelEditListener;
    public ChannelEditDialog(Context context) {
        super(context);
        mContext = context;
    }
    /**
     *
     * @param context
     * @param theme
     * @param flag
     *            false表示移动对话框 true表示删除对话框
     */
    public ChannelEditDialog(Context context, int theme, boolean flag) {
        super(context, theme);
        // , R.style.Translucent_NoTitle
        mContext = context;
        isMoveFlag = flag;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_dialog);// channel_edit_dialog
        this.setCancelable(false);
        positiveBtn = (Button) findViewById(R.id.ok_btn);
        negativeBtn = (Button) findViewById(R.id.cancel_btn);
        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
        promptTxt = (TextView) findViewById(R.id.prompt_txt);
        if (isMoveFlag) {
            promptTxt.setText(mContext.getString(R.string.delet_channel_message));
        } else {
            promptTxt.setText(mContext.getString(R.string.move_channel_message));
        }
    }
    @Override
    public void onClick(View v) {
        if (mChannelEditListener == null) {
            return;
        }
        if (v.getId() == R.id.ok_btn) {
            mChannelEditListener.positiveClick();
        } else if (v.getId() == R.id.cancel_btn) {
            mChannelEditListener.negativeClick();
        }
    }
    public ChannelEditListener getChannelEditListener() {
        return mChannelEditListener;
    }
    public void setChannelEditListener(ChannelEditListener channelEditListener) {
        this.mChannelEditListener = channelEditListener;
    }
}