
package com.um.launcher.view;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;

import com.um.launcher.MyAppActivity;
import com.um.launcher.data.AppAdapter;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;

/**
 * custom GridView
 *
 * @author huyq
 */
public class CustomGridView extends GridView implements ShowAbleInterface {

    private static final String TAG = "CustomeGridView";
    private MyAppActivity mMyAppActivity;
    private Handler mHandler;

    public CustomGridView(Context context, Handler handler) {
        super(context);
        mMyAppActivity = (MyAppActivity) context;
        mHandler = handler;
    }

    @Override
    public void isShow() {
        if (Constant.LOG_TAG) {
            Log.d(TAG,
                    "isShow() "
                            + this
                            + "  "
                            + ((ResolveInfo) ((AppAdapter) getAdapter())
                                    .getItem(0)).toString());
        }
        // invalidate();
        requestFocusFromTouch();
        if (MyAppActivity.isSnapRight) {
            setSelection(0);
        } else {
            setSelection(5);
        }
        mHandler.removeMessages(MyAppActivity.REFRESH_PAGE);
        mHandler.sendEmptyMessageDelayed(MyAppActivity.REFRESH_PAGE, 455);

    }

    @Override
    public View[] getImgViews() {
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mMyAppActivity.isFinished()) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            MyAppActivity.isSnapRight = false;
            if ((getSelectedItemPosition() == 0
                    || getSelectedItemPosition() == 6 || getSelectedItemPosition() == 12)) {
                mMyAppActivity.snapToPreScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            MyAppActivity.isSnapRight = true;
            if ((getSelectedItemPosition() == getCount() - 1
                    || getSelectedItemPosition() == 5 || getSelectedItemPosition() == 11)) {
                mMyAppActivity.snapToNextScreen();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
