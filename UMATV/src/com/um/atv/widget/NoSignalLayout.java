package com.um.atv.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.um.atv.R;
import com.um.atv.logic.SignalLogic.SignalListener;
import com.um.atv.util.Constant;

/**
 * if has no Signal,NoSignalLayout will show
 *
 * @author wangchuanjian
 *
 */
public class NoSignalLayout extends RelativeLayout implements SignalListener {
    private static final String TAG = "NoSignalLayout";
    // no signal gone
    private final int NOSIGNAL_GONE = 1;
    // no signal visible
    private final int NOSIGNAL_VISIBLE = 2;
    // Switch the background
    private final int SHOW_NOSIGNAL_BG = 3;
    // Background on search time
    private final int SHOW_TIME = 3000;
    // private Context mContext;
    // imageView of background
    private ImageView mBackgroundView = null;
    private Handler mHandler;
    // array of no signal picture
    private int mResId[] = new int[] { R.drawable.nosig_bg1,
            R.drawable.nosig_bg2, R.drawable.nosig_bg3, R.drawable.nosig_bg4,
            R.drawable.nosig_bg5, R.drawable.nosig_bg6, R.drawable.nosig_bg7 };
    // id of which picture show
    private int mShowId = 0;
    private View mContentView = null;

    public NoSignalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mContentView = mLinflater.inflate(R.layout.no_signal, this);
        mBackgroundView = (ImageView) mContentView
                .findViewById(R.id.signal_bg_img);
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case NOSIGNAL_GONE:
                    setVisibility(View.GONE);
                    break;

                case NOSIGNAL_VISIBLE:
                    setVisibility(View.VISIBLE);
                    break;
                case SHOW_NOSIGNAL_BG:
                    // contentView.setBackgroundResource(resId[showId]);
                    mBackgroundView.setImageResource(mResId[mShowId]);
                    mShowId++;
                    if (mShowId == mResId.length) {
                        mShowId = 0;
                    }
                    mHandler.sendEmptyMessageDelayed(SHOW_NOSIGNAL_BG,
                            SHOW_TIME);
                    break;
                default:
                    break;
                }
                super.handleMessage(msg);
            }
        };
    }

    /**
     * send or remove message to handler for show no signal background
     *
     * @param showFlag
     *            Whether to start polling play background
     */
    public void snapBackground(boolean showFlag) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "snapBackground ===showFlag:" + showFlag);
        }
        if (showFlag) {
            setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(SHOW_NOSIGNAL_BG);
        } else {
            setVisibility(View.GONE);
            mHandler.removeMessages(SHOW_NOSIGNAL_BG);
        }
    }

    @Override
    public void onNoSignal(boolean isHaveSignal) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onNoSignal = " + isHaveSignal);
        }
        if (isHaveSignal) {
            mHandler.sendEmptyMessage(NOSIGNAL_GONE);
        } else {
            mHandler.sendEmptyMessage(NOSIGNAL_VISIBLE);
        }
    }

    @Override
    public void SignalChange(String str) {
        // TODO Auto-generated method stub
    }
}
