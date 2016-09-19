package cn.com.unionman.umtvsetting.system.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.Refreshable;
import cn.com.unionman.umtvsetting.system.util.Constant;

/**
 * The progressView WidgetView in second menu.
 *
 * @author wang_chuanjian HiSi.ltd <br>
 *
 */
public class ProgressView extends LinearLayout implements Refreshable,
        View.OnKeyListener, View.OnFocusChangeListener {
    private static final String TAG = "ProgressView";
    // seekBar of ProgressView
    private SeekBar progressViewSeekbar;
    // text of option
    private TextView optionTxt;
    // text of menu
    private TextView menuTxt;
    // menu Container
    private CustomSettingView mCustomSettingView;
    // list of WidgetType
    private List<WidgetType> mWidgetTypeList = null;
    private Context mContext;
    // is on focus or not
    private boolean isOnFocus;
    // type of widget
    private WidgetType mWidgetType;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    @SuppressLint("ResourceAsColor")
    public ProgressView(CustomSettingView customSettingView, Context context,
            WidgetType widgetType, List<WidgetType> li) {
        super(context);
        mContext = context;
        this.mCustomSettingView = customSettingView;
        mWidgetTypeList = li;
        mWidgetType = widgetType;
        if (mWidgetType == null
                || mWidgetType.getmAccessProgressInterface() == null) {
            throw new NullPointerException(
                    "mWidgetType is null || mWidgetType.getmAccessProgressInterface() is null");
        }
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.progress_view, this);
        // The initialization of progressview
        initView();
        // Touch, the mouse to select the progress bar, the schedule change
        onTouchSeekbar();

    }

    /**
     * Touch, the mouse to select the progress bar, the schedule change
     */
    public void onTouchSeekbar() {
        progressViewSeekbar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                        progressViewSeekbar.setFocusable(true);
                        progressViewSeekbar.setFocusableInTouchMode(true);
                        progressViewSeekbar.requestFocus();
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int arg1,
                            boolean arg2) {
                        menuTxt.setText("  "
                                + (progressViewSeekbar.getProgress() + mWidgetType
                                        .getOffset()));
                    }
                });
    }

    /**
     * The initialization of progressView
     */
    public void initView() {
        optionTxt = (TextView) findViewById(R.id.progress_menu_txt);
        optionTxt.setText(mWidgetType.getName());
        if (Constant.LOG_TAG) {
            Log.i(TAG, mWidgetType.getName() + "=length="
                    + mWidgetType.getName().length());
        }
        menuTxt = (TextView) findViewById(R.id.progress_number_txt);
        int progress = mWidgetType.getmAccessProgressInterface().getProgress();
        menuTxt.setText("  " + (progress + mWidgetType.getOffset()));
        progressViewSeekbar = (SeekBar) findViewById(R.id.progress_seekbar);
        progressViewSeekbar.setMax(mWidgetType.getMaxProgress());
        progressViewSeekbar.setProgress(progress);
        progressViewSeekbar.setOnFocusChangeListener(this);
        progressViewSeekbar.setOnKeyListener(this);
        findViewById(R.id.progess_layout).setBackgroundResource(
                R.drawable.setting_focus_on);
        findViewById(R.id.progess_layout).setBackgroundResource(
                R.drawable.button_transparent);
        progressViewSeekbar.setFocusable(mWidgetType.isEnable());
    }

    @Override
    public void refreshUI() {
        int progress = mWidgetType.getmAccessProgressInterface().getProgress();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "refreshUI this = " + this + "-------------progress = "
                    + progress);
        }
        progressViewSeekbar.setProgress(progress);
        menuTxt.setText("  " + progress);
    }

    @Override
    public WidgetType getWidgetType() {
        return mWidgetType;
    }

    @Override
    public boolean getIsFocus() {
        // TODO Auto-generated method stub
        return isOnFocus;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
        case R.id.progress_seekbar:
            isOnFocus = hasFocus;
            if (hasFocus) {
                if (mWidgetTypeList.size() > 1) {
                    findViewById(R.id.progess_layout).setBackgroundResource(
                            R.drawable.setting_focus_on);
                } else {
                    findViewById(R.id.progress_seekbar).setBackgroundResource(
                            R.drawable.button_transparent);
                }

                progressViewSeekbar.setProgressDrawable(getResources()
                        .getDrawable(R.drawable.seek_bar_progress));
                optionTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[0]));
                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[0]));
            } else {
                findViewById(R.id.progess_layout).setBackgroundResource(
                        R.drawable.button_transparent);
                progressViewSeekbar.setProgressDrawable(getResources()
                        .getDrawable(R.drawable.seek_bar_progress_focus));
                optionTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
            }
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onFocusChange flag = " + isOnFocus);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
        case R.id.progress_seekbar:
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mCustomSettingView.onKey(v, keyCode, event)) {
                    return true;
                }
                int progress = progressViewSeekbar.getProgress();
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (isOnFocus) {
                        progress--;
                        if (progress >= 0) {
                            progressViewSeekbar.setProgress(progress);
                            mWidgetType.getmAccessProgressInterface()
                                    .setProgress(progress);
                            menuTxt.setText("  "
                                    + (progress + mWidgetType.getOffset()));
                            return true;
                        }
                    }
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (isOnFocus) {
                        progress++;
                        if (progress <= mWidgetType.getMaxProgress()) {
                            progressViewSeekbar.setProgress(progress);
                            mWidgetType.getmAccessProgressInterface()
                                    .setProgress(progress);
                            menuTxt.setText("  "
                                    + (progress + mWidgetType.getOffset()));
                            return true;
                        }
                    }
                case KeyEvent.KEYCODE_BACK:
                    if (Constant.LOG_TAG) {
                        Log.d(TAG, "back !!!");
                    }
                    break;

                default:
                    break;
                }
            }
            if (Constant.LOG_TAG) {
                Log.d(TAG, progressViewSeekbar.getProgress() + "");
            }
            break;
        default:
            break;
        }
        return false;
    }
}
