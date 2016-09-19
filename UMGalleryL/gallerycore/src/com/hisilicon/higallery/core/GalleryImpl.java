
package com.hisilicon.higallery.core;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.Parcel;
import android.util.Log;
import android.view.Surface;

import com.hisilicon.higallery.core.DecodeThread.DecodeListener;

class GalleryImpl extends GalleryCore {

    static {
        System.loadLibrary("gallerycore");
    }

    static final String TAG = "gallerycore";
    static final int ANIM_DURATION = 2000;

    private static final float MAX_SCALE = 8f;
    private static final float MIN_SCALE = 0.125f;
    private float mScaleLevel = 1;

    private int mDisplayWidth;
    private int mDisplayHeight;
    private Rect mShownFrame = new Rect();
    private boolean mInit = false;

    private static final int MOVE_STEP = 30;


    boolean mEnablePQ = false;

    boolean mAnimationEnabled;
    Handler mGLHandler;
    GLThread mGLThread;
    Handler mMainHandler;

    Bitmap mCurrentBmp;
    Bitmap mFailBitmap;
    String mCurrentFile;

    HandlerThread mHandlerThread;
    DecodeHander mDecodeHandler;

    Bitmap mFalureBitmap;
    long lastViewTime;
    long initBeginTime;

    AnimType mAnimType;
    AnimType[] mAnimRandomSeeds;
    long mSlidingInterval;
    boolean mIsSliding = false;
    Sliding mSliding;
    Runnable mSlidingTask = new Runnable() {

        @Override
        public void run() {
            if (mAnimType == AnimType.ANIM_RANDOM) {
                if (mAnimRandomSeeds != null) {
                    int animTypeIndex = new Random().nextInt(mAnimRandomSeeds.length);
                    AnimType a = mAnimRandomSeeds[animTypeIndex];
                    if (a == AnimType.ANIM_RANDOM)
                        a = AnimType.ANIM_NONE;
                    nativeSetAnimationType(a.type, ANIM_DURATION);
                } else {
                    int animType = new Random().nextInt(3) + 1;
                    nativeSetAnimationType(animType, ANIM_DURATION);
                }
            } else {
                nativeSetAnimationType(mAnimType.type, ANIM_DURATION);
            }
            if (mSliding != null)
                mSliding.showNext();
        }
    };

    public GalleryImpl(Looper mainLooper) {
        mGLThread = new GLThread("GLThread");
        mGLThread.start();
        setLooper(mainLooper);
        // if (mHandlerThread == null) {
        // mHandlerThread = new HandlerThread("decode_thread");
        // mHandlerThread.start();
        // }
        // if (mDecodeHandler == null) {
        // mDecodeHandler = new DecodeHander(mHandlerThread.getLooper());
        // }
    }

    public void setLooper(Looper mainLooper) {
        mMainHandler = new Handler(mainLooper) {

            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                case CMD_INIT_COMPLETED:
                case CMD_VIEW_COMPLETED:
                    if (mCallback != null) {
                        mCallback.onReceiveCMD(msg.what, (msg.arg1==1));
                    }
                    if (mCallbackWithUrl != null){
                        Parcel parcel = Parcel.obtain();
                       // parcel.writeInt(msg.arg1);
                        parcel.writeString((String)msg.obj);
                        mCallbackWithUrl.onReceiveCMDWithUrl(msg.what, (msg.arg1==1),parcel);
                        parcel.recycle();
                    }
                    break;
                case CMD_SHOWN_FRAME_CHANGED:
                    if (mCallback != null) {
                        mCallback.onReceiveCMD(msg.what, msg.obj);
                    }
                    if (mCallbackWithUrl != null){
                        Parcel parcel = Parcel.obtain();
                        mCallbackWithUrl.onReceiveCMDWithUrl(msg.what, msg.obj, parcel);
                        parcel.recycle();
                    }
                default:
                    break;
                }
            }
        };
    }

    protected Callback mCallback;
    protected CallbackWithUrl mCallbackWithUrl;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setCallbackWithUrl(CallbackWithUrl callback) {
        mCallbackWithUrl = callback;
    }

    @Override
    public void enablePQ(boolean enable) {
        mEnablePQ = enable;
    }

    @Override
    public void init(int width, int height) {
    	Log.i("gallare" ,"gallare");
        init(width, height, 200);
    }

    @Override
    public void init(int width, int height, int maxUsedMemSize) {
        initBeginTime = System.currentTimeMillis();
        while (mGLHandler == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        mInit = true;

        Message msg = mGLHandler.obtainMessage(GLThread.GL_INIT, width, height);
        msg.obj = maxUsedMemSize * 1024 * 1024;
        mGLHandler.sendMessage(msg);
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("decode_thread");
            mHandlerThread.start();
        }
        if (mDecodeHandler == null) {
            mDecodeHandler = new DecodeHander(mHandlerThread.getLooper());
        }
        mDisplayWidth = width;
        mDisplayHeight = height;
    }

    @Override
    public boolean deinit() {
        mInit = false;
        if (mIsSliding) {
            stopSliding();
        }
        Message msg = mGLHandler.obtainMessage(GLThread.GL_DEINIT);
        mGLHandler.sendMessage(msg);
        if (mDecodeHandler != null) {
            mDecodeHandler.stop();
            mDecodeHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        mCallback = null;
        mCallbackWithUrl = null;
        return true;
    }

    @Override
    public void enableAnimation(boolean enable) {
        mAnimationEnabled = enable;
    }

    @Override
    public void viewImage(final String path) {
        viewImage(path, true);
    }

    @Override
    public void viewImage(final String path, final boolean fullScreen) {
        viewImage(path, fullScreen ? ViewMode.FULLSCREEN_MODE : ViewMode.ORIGINAL_MODE);
    }

    @Override
    public void viewImage(final String path, final ViewMode viewmode) {
        if (!mInit) {
            mMainHandler.sendMessage(mMainHandler.obtainMessage(CMD_VIEW_COMPLETED, 0, 0, path));
            return;
        }
        mCurrentFile = path;
        lastViewTime = System.currentTimeMillis();
        Log.d(TAG, "viewImage " + path);
        DecodeThread thread = new DecodeThread();
        thread.setListener(new DecodeListener() {
            @Override
            public void onStartDecode() {
                long time = SystemClock.currentThreadTimeMillis();
                showBitmap(path, viewmode.mode);//use native decode
/*
                // Bitmap bitmap = BitmapDecodeUtils.getOrigionBitmap(path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                long duration = SystemClock.currentThreadTimeMillis() - time;
                Log.d(TAG, "decode pic duration = " + duration + "ms" + ", succ = "
                        + (bitmap != null));
                if (bitmap == null) {
                    showCompleted(false);
                    if (mFailBitmap != null)
                        showBitmap(mFailBitmap, fullScreen);
                } else {
                    showBitmap(bitmap, fullScreen);
                }
*/
            }
        });
        mDecodeHandler.postDecode(thread);
    }

    @Override
    public void viewImage(final String path, float scale) {
        viewImage(path, true);
    }

    @Override
    public void getImageSize(Point size) {
        // TODO Auto-generated method stub

    }
    
//    public void showBitmap(Bitmap bitmap, int viewmode) {
//        if (mCurrentBmp != null && mCurrentBmp != mFailBitmap)
//            mCurrentBmp.recycle();
//        mCurrentBmp = bitmap;
//        Message msg = mGLHandler.obtainMessage(GLThread.GL_SHOW_BITMAP);
//        msg.obj = bitmap;
//        msg.arg1 = viewmode;
//        mGLHandler.sendMessage(msg);
//    }

    public void showBitmap(final String path, int viewmode) {

        Message msg = mGLHandler.obtainMessage(GLThread.GL_SHOW_BITMAP);
        msg.obj = path;
        msg.arg1 = viewmode;
        mGLHandler.sendMessage(msg);
    }

    @Override
    public boolean zoomIn() {
        if (mScaleLevel >= MAX_SCALE)
            return false;
        mScaleLevel *= 2;
        scale(2f, 2f);
        return true;
    }

    @Override
    public boolean zoomOut() {
        if (mScaleLevel <= MIN_SCALE)
            return false;
        mScaleLevel *= 0.5f;
        scale(0.5f, 0.5f);
        return true;
    }

    public boolean zoom(float scale) {
        if (scale == 1) {
            return false;
        } else if (scale > 1 && mScaleLevel >= MAX_SCALE) {
            return false;
        } else if (scale < 1 && mScaleLevel <= MIN_SCALE) {
            return false;
        }

        mScaleLevel *= scale;
        scale(scale, scale);
        if (mScaleLevel == 1 && scale < 1) {
            reset();
        }
        return true;
    }

    @Override
    public boolean move(Direction r, int step) {
        if (mScaleLevel <= 1)
            return false;
        switch (r) {
            case LEFT:
                if (mShownFrame.left >= 0)
                    return false;
                translate(step, 0);
                break;
            case RIGHT:
                if (mShownFrame.right <= mDisplayWidth)
                    return false;
                translate(-step, 0);
                break;
            case UP:
                if (mShownFrame.top >= 0)
                    return false;
                translate(0, step);
                break;
            case DOWN:
                if (mShownFrame.bottom <= mDisplayHeight)
                    return false;
                translate(0, -step);
                break;
        }
        return true;
    }

    public boolean scale(float scaleX, float scaleY) {
        Message msg = mGLHandler.obtainMessage(GLThread.GL_SCALE);
        Bundle data = msg.getData();
        data.putFloat("scaleX", scaleX);
        data.putFloat("scaleY", scaleY);
        mGLHandler.sendMessage(msg);
        return true;
    }

    public boolean translate(int tX, int tY) {
        Message msg = mGLHandler.obtainMessage(GLThread.GL_TRANSLATE);
        msg.arg1 = tX;
        msg.arg2 = tY;
        mGLHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean rotate(Rotation r) {
        Message msg = mGLHandler.obtainMessage(GLThread.GL_ROTATE);
        // msg.arg1 = r.degree;
        msg.arg1 = 360 - r.degree;
        mGLHandler.sendMessage(msg);
        return true;
    }

    public boolean setAlpha(float a) {
        if (a < 0 || a > 1) {
            Log.e(TAG, "alpha should be 0~1, not " + a);
            return false;
        }

        Message msg = mGLHandler.obtainMessage(GLThread.GL_SET_ALPHA);
        msg.obj = a;
        mGLHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean reset() {
        Message msg = mGLHandler.obtainMessage(GLThread.GL_RESET);
        mGLHandler.sendMessage(msg);

        mScaleLevel = 1;
        return true;
    }

    @Override
    public boolean startSliding(Sliding s, AnimType a, long interval) {
        return startSliding(s, a, null, interval);
    }

    public boolean startSliding(Sliding s, AnimType a, AnimType[] randomSeeds, long interval) {
        mAnimType = a;
        mAnimRandomSeeds = randomSeeds;
        mSlidingInterval = interval;
        mSliding = s;
        mIsSliding = true;
        mMainHandler.postDelayed(mSlidingTask, mSlidingInterval);
        return true;
    }

    @Override
    public boolean stopSliding() {
        mIsSliding = false;
        mMainHandler.removeCallbacks(mSlidingTask);
        nativeSetAnimationType(AnimType.ANIM_NONE.type, ANIM_DURATION);
        return true;
    }

    @Override
    public void setFailBitmap(Bitmap bitmap) {
        mFailBitmap = bitmap;
    }

    @Override
    public boolean decodeSizeEvaluate(String path, int width, int height, int sampleSize ,int usedDecSize) {
        return nativeDecodeSizeEvaluate(path, width, height, sampleSize ,usedDecSize);
    }

    public void initCompleted(boolean result) {
        mMainHandler.sendMessage(mMainHandler.obtainMessage(CMD_INIT_COMPLETED, (result?1:0), 0));
        long duration = System.currentTimeMillis() - initBeginTime;
        Log.d(TAG, "Init timecost = " + duration + "ms, result = " + result);
    }

    public void showCompleted(boolean result) {
        mMainHandler.sendMessage(mMainHandler.obtainMessage(CMD_VIEW_COMPLETED, (result?1:0), 0));
        Runtime.getRuntime().gc();
        long duration = System.currentTimeMillis() - lastViewTime;
        Log.d(TAG, "View timecost = " + duration + "ms, result = " + result);

        if (mIsSliding)
            mMainHandler.postDelayed(mSlidingTask, mSlidingInterval);
    }

    public void showCompleted(boolean result, String url) {
        mMainHandler.sendMessage(mMainHandler.obtainMessage(CMD_VIEW_COMPLETED, (result?1:0), 0, url));
        Runtime.getRuntime().gc();
        long duration = System.currentTimeMillis() - lastViewTime;
        Log.d(TAG, "View timecost = " + duration + "ms, result = " + result);

        if (mIsSliding)
            mMainHandler.postDelayed(mSlidingTask, mSlidingInterval);
    }

    public void shownFrameChanged(int left, int top, int right, int bottom) {
        mShownFrame.set(left, top, right, bottom);
        mMainHandler.sendMessage(mMainHandler.obtainMessage(CMD_SHOWN_FRAME_CHANGED, mShownFrame));
    }

    public String getCurrentPath() {
        return mCurrentFile;
    }

    public void getDisplaySize(Point size) {
        if (size == null)
            size = new Point();
        size.x = mDisplayWidth;
        size.y = mDisplayHeight;
    }

    public Rect getShownFrame() {
        Rect r = new Rect(mShownFrame);
        return r;
    }

    @Override
    public void initWithSurface(Surface surface, int width, int height) {
        Message msg = mGLHandler.obtainMessage(GLThread.GL_INIT_SURFACE, width, height, surface);
        mGLHandler.sendMessage(msg);
    }

    class GLThread extends HandlerThread {
        static final int GL_INIT = 1;
        static final int GL_DEINIT = 2;
        static final int GL_SHOW_BITMAP = 3;
        static final int GL_SCALE = 4;
        static final int GL_TRANSLATE = 5;
        static final int GL_ROTATE = 6;
        static final int GL_SET_ALPHA = 7;
        static final int GL_RESET = 8;

        static final int GL_INIT_SURFACE = 99;

        public GLThread(String name) {
            super(name, Process.THREAD_PRIORITY_DISPLAY);
        }

        @Override
        protected void onLooperPrepared() {
            mGLHandler = new Handler(getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case GL_INIT: {
                            int width = msg.arg1;
                            int height = msg.arg2;
                            int maxUsedMemSizeByte = (Integer)msg.obj;
                            // add enablePQ
                            Log.d(TAG, "init with enablePQ = " + mEnablePQ);
                            nativeInit(width, height, maxUsedMemSizeByte, mEnablePQ);
                            break;
                        }
                        case GL_DEINIT:
                            nativeDeinit();
                            if (mCurrentBmp != null) {
                                mCurrentBmp.recycle();
                            }
                            //quitSafely();
                            break;

                        case GL_SHOW_BITMAP:
                            int viewmode = msg.arg1;
                            // nativeShowBitmap((Bitmap) msg.obj, fullScreen);
                            try {
                                //nativeShowBitmap((Bitmap) msg.obj, fullScreen);
                                nativeShowImage((String) msg.obj, viewmode);
                            } catch (Exception e) {
                                //nativeShowBitmap(mFalureBitmap, viewmode);
                                showCompleted(false, (String) msg.obj);
                            }
                            break;
                        case GL_INIT_SURFACE: {
                            int width = msg.arg1;
                            int height = msg.arg2;
                            Surface surface = (Surface) msg.obj;
                            nativeInitWithSurface(surface, width, height);
                            break;
                        }

                        case GL_SCALE: {
                            Bundle data = msg.getData();
                            float scaleX = data.getFloat("scaleX");
                            float scaleY = data.getFloat("scaleY");
                            nativeScale(scaleX, scaleY);
                            break;
                        }
                        case GL_TRANSLATE: {
                            int tX = msg.arg1;
                            int tY = msg.arg2;
                            nativeTranslate(tX, tY);
                            break;
                        }
                        case GL_ROTATE: {
                            int degree = msg.arg1;
                            nativeRotate(degree);
                            break;
                        }
                        case GL_SET_ALPHA: {
                            float a = (Float) msg.obj;
                            nativeSetAlpha(a);
                            break;
                        }
                        case GL_RESET: {
                            nativeReset();
                            break;
                        }
                    }
                }

            };
        }
    }

    Bitmap loadGifBitmap(String path) {
        Movie movie = Movie.decodeFile(path);
        Bitmap bitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(0xffff0000);
        movie.setTime(movie.duration());
        movie.draw(c, movie.width(), movie.height());
        return bitmap;

    }

    public native void nativeInit(int width, int height, int maxUsedMemSizeByte, boolean enablePQ);

    public native void nativeInitWithSurface(Surface surface, int width, int height);

    public native void nativeDeinit();

    public native void nativeShowImage(String path, int viewmode);

    public native void nativeShowBitmap(Bitmap bitmap, int fullScreen);

    public native void nativeScale(float scaleX, float scaleY);

    public native void nativeTranslate(int tX, int tY);

    public native void nativeRotate(int degree);

    public native void nativeSetAlpha(float a);

    public native void nativeReset();

    public native void nativeSetAnimationType(int animType, int duration);

    public native boolean nativeDecodeSizeEvaluate(String path, int width, int height, int sampleSize ,int usedDecSize);
}
