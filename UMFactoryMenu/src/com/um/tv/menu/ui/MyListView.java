package com.um.tv.menu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;

public class MyListView extends ListView {
    private static final String TAG = "MyListView";
    public MyListView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onKeyDown--->keyCode:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mWindowCallback != null) {
                    mWindowCallback.changeValue(-1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mWindowCallback != null) {
                    mWindowCallback.changeValue(1);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onKeyUp--->keyCode:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                gotoUpperLevel();
                break;

            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void gotoUpperLevel() {
        if (mWindowCallback != null) {
            mWindowCallback.gotoUpperLevel();
        }
    }

    private FactoryWindow.WindowCallback mWindowCallback = null;
    public void setWindowCallback(FactoryWindow.WindowCallback callback) {
        mWindowCallback = callback;
    }
}
