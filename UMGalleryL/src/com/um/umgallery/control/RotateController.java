
package com.um.umgallery.control;

import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hisilicon.higallery.core.GalleryCore;
import com.hisilicon.higallery.core.GalleryCore.Rotation;
import com.um.umgallery.utils.Utils;
import android.util.Log;

public class RotateController implements Controller {
    private GalleryCore mGalleryCore;
    private Context mContext;
    private Rotation mRotation;
    private Handler mInfoHandler;

    static final String TAG = "RotateController";

    public RotateController(GalleryCore galleryCore, Context context, Handler handler) {
        mGalleryCore = galleryCore;
        mContext = context;
        mInfoHandler = handler;
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_UP:
                Log.d(TAG, "HiGalleryL::rotate left::-90::OK");
                rotateLeft();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Log.d(TAG, "HiGalleryL::rotate right::90::OK");
                rotateRight();
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
    	Log.i("ccc" ,"Roate========");
        mRotation = Rotation.ROTATION_0;
        Utils.showInfo(mContext, mInfoHandler, Utils.ROTATE_MODE);
    }

    @Override
    public void stopControl() {
        mRotation = Rotation.ROTATION_0;
        mGalleryCore.reset();
        mInfoHandler.sendEmptyMessage(Utils.DISMISS_INFO);
    }

    public void rotateLeft() {
        // switch(mRotation) {
        // case ROTATION_0:
        // mRotation = Rotation.ROTATION_270;
        // break;
        // case ROTATION_90:
        // mRotation = Rotation.ROTATION_0;
        // break;
        // case ROTATION_180:
        // mRotation = Rotation.ROTATION_90;
        // break;
        // case ROTATION_270:
        // mRotation = Rotation.ROTATION_180;
        // break;
        // }
        mGalleryCore.rotate(Rotation.ROTATION_270);
    }

    public void rotateRight() {
        // switch(mRotation) {
        // case ROTATION_0:
        // mRotation = Rotation.ROTATION_90;
        // break;
        // case ROTATION_90:
        // mRotation = Rotation.ROTATION_180;
        // break;
        // case ROTATION_180:
        // mRotation = Rotation.ROTATION_270;
        // break;
        // case ROTATION_270:
        // mRotation = Rotation.ROTATION_0;
        // break;
        // }
        mGalleryCore.rotate(Rotation.ROTATION_90);
    }

}
