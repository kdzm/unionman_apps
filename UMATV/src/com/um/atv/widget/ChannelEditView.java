package com.um.atv.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.atv.R;
import com.um.atv.ATVMainActivity;
import com.um.atv.util.Constant;

/**
 * Show broadcast channel, support for editing channel
 *
 */
public class ChannelEditView extends RelativeLayout {

    private static final String TAG = "ChannelEditView";
    private ATVMainActivity mContext;
    // text of channel number
    private TextView channelNumTxt;
    // private int[][] mSourceModel;
    // private int[][] mColorSystem = InterfaceValueMaps.color_system;
    // private int[][] mAudioSystem = InterfaceValueMaps.audio_system;
    // number of current channel
    private int mCurrentChannel;
    // private long mLastSelectChannelTime;
    // is changing channel or not
    private boolean isChangingChannel;

    public ChannelEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (ATVMainActivity) context;
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.channel_edit_view, this);
        channelNumTxt = (TextView) findViewById(R.id.channel_num_txt);
        mCurrentChannel = 0;
        isChangingChannel = false;
    }

    public ChannelEditView(Context context) {
        super(context);
    }

    /**
     * When can slide left Icon focus is not in the picture when
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onKeyDown keycode = " + keyCode + "; event = " + event);
        }

        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
            if (mCurrentChannel < 99) {
                mCurrentChannel++;
            } else {
                mCurrentChannel = 0;
            }
            channelNumTxt.setText(String.valueOf(mCurrentChannel));
            mContext.RefreshSignalShow(false);
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            if (mCurrentChannel > 0) {
                mCurrentChannel--;
            } else {
                mCurrentChannel = 99;
            }
            channelNumTxt.setText(String.valueOf(mCurrentChannel));
            mContext.RefreshSignalShow(false);
            break;
        case KeyEvent.KEYCODE_0:
        case KeyEvent.KEYCODE_1:
        case KeyEvent.KEYCODE_2:
        case KeyEvent.KEYCODE_3:
        case KeyEvent.KEYCODE_4:
        case KeyEvent.KEYCODE_5:
        case KeyEvent.KEYCODE_6:
        case KeyEvent.KEYCODE_7:
        case KeyEvent.KEYCODE_8:
        case KeyEvent.KEYCODE_9:

            if (isChangingChannel) {
                int tmpChannel = mCurrentChannel * 10 + keyCode
                        - KeyEvent.KEYCODE_0;
                if (tmpChannel > 99) {
                    isChangingChannel = false;
                    tmpChannel = 0;
                }
                mCurrentChannel = tmpChannel;
            } else {
                // mLastSelectChannelTime = event.getEventTime();
                mCurrentChannel = keyCode - KeyEvent.KEYCODE_0;
                isChangingChannel = true;
            }
            channelNumTxt.setText(String.valueOf(mCurrentChannel));
            mContext.RefreshSignalShow(false);
            mTimeHandler.sendEmptyMessageDelayed(0, 1000);
            break;
        default:
            break;
        }

        return super.onKeyDown(keyCode, event);
    }

    Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isChangingChannel = false;
            super.handleMessage(msg);
        }
    };

}
