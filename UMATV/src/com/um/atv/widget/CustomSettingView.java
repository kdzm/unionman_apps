package com.um.atv.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.um.atv.R;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;
import com.um.atv.model.WidgetType.Refreshable;
import com.um.atv.util.Constant;

/**
 * Container second level menu required
 *
 * @author wangchuanjian
 *
 */
public class CustomSettingView extends LinearLayout {
    private static final String TAG = "CustomSettingView";
    public static final int DIALOG_CLOSE = 0;
    // public static final int FADE_CHANNEL_OUT = 0x1009;
    // private InterfaceLogic mInterfaceLogic;
    private AlertDialog mAlertDialog;
    private Context mContext;
    // list of WidgetType
    private List<WidgetType> mWidgetTypeList;
    // is show dialog or not
    private boolean isShowDialog = false;
    // list of Refreshable
    private List<Refreshable> mViewList;

    public boolean isShowDialog() {
        return isShowDialog;
    }

    private Handler mRefreshHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Constant.SETTING_UI_REFRESH_VIEWS:
                List<String> list = (List<String>) msg.obj;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "mRefreshHandler list size = " + list.size());
                }
                refreshUI(list);
                break;
            case Constant.EXIT_MUNE:
                mAlertDialog.dismiss();
                ((Activity)mContext).finish();
                break;
            default:
                break;
            }
        }
    };

    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    /**
     * handler of dismiss dialog
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DIALOG_CLOSE:
                Log.d("Constant.DISPEAR_TIME", Constant.DISPEAR_TIME + "");
                mAlertDialog.dismiss();
                break;
            default:
                break;
            }
        }
    };

    public CustomSettingView(AlertDialog dlg, Context context, String title,
            InterfaceLogic interfaceLogic) {
        super(context);
        mContext = context;
        // this.mInterfaceLogic = interfaceLogic;
        this.mAlertDialog = dlg;
        interfaceLogic.setHandler(mRefreshHandler);
        this.mWidgetTypeList = interfaceLogic.getWidgetTypeList();
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        if (title != null && !"".equals(title)) {
            if (mWidgetTypeList.size() > 1) {
                addView(new TitleView(mContext, title), new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }
        }
        if (Constant.LOG_TAG) {
            Log.d(TAG, mWidgetTypeList.toString());
        }
        mViewList = new ArrayList<Refreshable>();
        for (WidgetType mWidgetType : mWidgetTypeList) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "enter");
            }
            if (mWidgetType.getType() == WidgetType.TYPE_SELECTOR) {
                SelectorView SelectorView = new SelectorView(this, mContext,
                        mWidgetType, mWidgetTypeList);
                addView(SelectorView, new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                mViewList.add(SelectorView);
            }
        }
       
        setBackgroundResource(R.drawable.channel_setting_bg);
        setLayoutParams(new LayoutParams(
        		android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 100));

        delay();
    }

    public List<WidgetType> getList() {
        return mWidgetTypeList;
    }

    public void setList(List<WidgetType> list) {
        this.mWidgetTypeList = list;
    }

    public void sendDispearMsg() {
        mHandler.sendEmptyMessageDelayed(0, Constant.DISPEAR_TIME);
    }

    public void removeMsg() {
        mHandler.removeMessages(0);
    }

    /**
     * send a message to handler with a delay time
     */
    public void delay() {
        mHandler.removeMessages(DIALOG_CLOSE);
        mHandler.sendEmptyMessageDelayed(DIALOG_CLOSE, Constant.DISPEAR_TIME_30s);
    }

    /**
     * refresh UI
     *
     * @param list
     */
    public void refreshUI(List<String> list) {
        if (mViewList != null && mViewList.size() > 0) {
            for (int i = 0; i < mViewList.size(); i++) {
                mViewList.get(i).refreshUI();
            }
        }
        /*
         * if (list != null && list.size() > 0) { for (int i = 0; i <
         * list.size(); i++) { String name = list.get(i); Refreshable
         * mRefreshable = getViewByName(name); if (Constant.LOG_TAG) { if
         * (mRefreshable != null) Log.d(TAG, "refreshUI " + i + " = " +
         * mRefreshable.getDiaType().getName()); } if (mRefreshable != null) {
         * mRefreshable.refreshUI(); }
         *
         * } }
         */
    }

    /**
     * get Refreshable by name
     *
     * @param name
     * @return
     */
    private Refreshable getViewByName(String name) {
        if (name == null) {
            return null;
        }
        if (mViewList != null && mViewList.size() > 0) {
            for (int i = 0; i < mViewList.size(); i++) {
                if (mViewList.get(i).getWidgetType().getName().equals(name)) {
                    return mViewList.get(i);
                }
            }
        }
        return null;
    }

    /**
     * Subclasses can call this method
     *
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        delay();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mViewList.get(0).getIsFocus()) {
                    ((View) mViewList.get(mViewList.size() - 1)).requestFocus();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mViewList.get(mViewList.size() - 1).getIsFocus()) {
                    ((View) mViewList.get(0)).requestFocus();
                    return true;
                }
                break;
            default:
                break;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        delay();
        return super.dispatchKeyEvent(event);
    }

    public void onDestroy() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
