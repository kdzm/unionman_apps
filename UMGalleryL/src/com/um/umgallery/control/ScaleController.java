
package com.um.umgallery.control;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hisilicon.higallery.core.GalleryCore;
import com.hisilicon.higallery.core.GalleryCore.Callback;
import com.hisilicon.higallery.core.GalleryCore.Direction;
import com.um.umgallery.ui.ScaleThunbView;
import com.um.umgallery.utils.Utils;

public class ScaleController implements Controller, Callback {

    private static final float SCALE_MULTIPLE = 2;
    private static final int MOVE_STEP = 120;

    private GalleryCore mGalleryCore;
    private Context mContext;
    private Handler mInfoHandler;
    private ScaleThunbView mScaleThunbView;

    static final String TAG = "ScaleController";

    public ScaleController(GalleryCore galleryCore, Context context, Handler handler) {
        mGalleryCore = galleryCore;
        mContext = context;
        mInfoHandler = handler;
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        boolean ret;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_ZOOM_IN:
                ret = mGalleryCore.zoom(SCALE_MULTIPLE);
                if(ret) {
                    Log.d(TAG, "HiGalleryL::zoomIn 2 times");
                } else {
                    Log.d(TAG, "HiGalleryL::already zoomIn to the largest");
                }
                return true;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_ZOOM_OUT:
                ret = mGalleryCore.zoom(1 / SCALE_MULTIPLE);
                if(ret) {
                    Log.d(TAG, "HiGalleryL::zoomOut 1/2 times");
                } else {
                    Log.d(TAG, "HiGalleryL::already zoomOut to the smallest");
                }
            case KeyEvent.KEYCODE_DPAD_LEFT:
                ret = mGalleryCore.move(Direction.LEFT, MOVE_STEP);
                if(ret) {
                    Log.d(TAG, "HiGalleryL::move to left");
                } else {
                    Log.d(TAG, "HiGalleryL::already move to the most left");
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                ret = mGalleryCore.move(Direction.RIGHT, MOVE_STEP);
                if(ret) {
                     Log.d(TAG, "HiGalleryL::move to right");
                } else {
                     Log.d(TAG, "HiGalleryL::already move to the most right");
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                ret = mGalleryCore.move(Direction.UP, MOVE_STEP);
                if(ret) {
                     Log.d(TAG, "HiGalleryL::move to up");
                } else {
                     Log.d(TAG, "HiGalleryL::already move to the most up");
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                ret = mGalleryCore.move(Direction.DOWN, MOVE_STEP);
                if(ret) {
                      Log.d(TAG, "HiGalleryL::move to down");
                } else {
                     Log.d(TAG, "HiGalleryL::already move to the most down");
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onMotionEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    @Override
    public void startControl() {
        // TODO Auto-generated method stub
        Utils.showInfo(mContext, mInfoHandler, Utils.SCALE_MODE);
        mGalleryCore.setCallback(this);
        Point size = new Point();
        mGalleryCore.getDisplaySize(size);
        mScaleThunbView = new ScaleThunbView(mContext, size, mGalleryCore.getCurrentPath());
        mScaleThunbView.setDrawnFrame(mGalleryCore.getShownFrame());
        mScaleThunbView.show();
    }

    @Override
    public void stopControl() {
        mGalleryCore.reset();
        mInfoHandler.sendEmptyMessage(Utils.DISMISS_INFO);
        mScaleThunbView.hide();
        mScaleThunbView = null;
    }

    @Override
    public void onReceiveCMD(int cmd, Object obj) {
        if (cmd == GalleryCore.CMD_SHOWN_FRAME_CHANGED) {
            Rect rect = (Rect) obj;
            if (mScaleThunbView != null)
                mScaleThunbView.setDrawnFrame(rect);
        }
    }

}
