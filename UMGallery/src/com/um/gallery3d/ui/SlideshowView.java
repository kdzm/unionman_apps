/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.um.gallery3d.ui;

import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;

import com.um.gallery3d.R;
import com.um.gallery3d.anim.CanvasAnimation;
import com.um.gallery3d.anim.FloatAnimation;
import com.um.gallery3d.app.GalleryActivity;

public class SlideshowView extends GLView {
    @SuppressWarnings("unused")
    private static final String TAG = "SlideshowView";

    private static final int SLIDESHOW_DURATION = 360;

    private static final int TRANSITION_DURATION = 1000;

    private static final float SCALE_SPEED = 0.20f;

    private static final float MOVE_SPEED = SCALE_SPEED;

    private static final float DEFAULT_TEXT_SIZE = 40;

    private static final int MSG_SHOW_LOADING = 0;

    private static final int LOADING = 0;

    private static final int LOADED = 1;

    private int mCurrentRotation;

    private BitmapTexture mCurrentTexture;

    private SlideshowAnimation mCurrentAnimation;

    private ProgressSpinner mLoadingSpinner;

    private StringTexture mLoadingText;

    private int mPrevRotation;

    private BitmapTexture mPrevTexture;

    private SlideshowAnimation mPrevAnimation;

    private GalleryActivity mActivity;

    private final FloatAnimation mTransitionAnimation = new FloatAnimation(0, 1,
            TRANSITION_DURATION);

    private Random mRandom = new Random();

    private SynchronizedHandler mHandler;

    private int loadingState = LOADING;

    public SlideshowView(GalleryActivity activity) {
        mActivity = activity;
        SharedPreferences share = activity.getAndroidContext().getSharedPreferences("Setting",
                Context.MODE_PRIVATE);
        int slidemode = share.getInt("slidemode", 0);
        animaState = AnimationMode.values()[slidemode];
        Context context = activity.getAndroidContext();
        mLoadingSpinner = new ProgressSpinner(context);
        mLoadingText = StringTexture.newInstance(context.getString(R.string.loading),
                DEFAULT_TEXT_SIZE, Color.WHITE);

        mHandler = new SynchronizedHandler(activity.getGLRoot()) {
            @TargetApi(Build.VERSION_CODES.FROYO)
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW_LOADING: {
                        loadingState = LOADING;
                        mLoadingSpinner.startAnimation();
                        invalidate();
                        break;
                    }
                    default:
                        throw new AssertionError(message.what);
                }
            }
        };
    }

    public void showProgress() {
        mHandler.sendEmptyMessage(MSG_SHOW_LOADING);
    }

    public void dismissProgress() {
        loadingState = LOADED;
    }

    private enum AnimationMode {
        TRANSITION, ALPHA,
    }

    private AnimationMode animaState = AnimationMode.ALPHA;

    public void next(Bitmap bitmap, int rotation) {
        if (mPrevTexture != null) {
            mPrevTexture.getBitmap().recycle();
            mPrevTexture.recycle();
        }

        mPrevTexture = mCurrentTexture;
        mPrevAnimation = mCurrentAnimation;
        mPrevRotation = mCurrentRotation;
        mCurrentRotation = rotation;
        mCurrentTexture = new BitmapTexture(bitmap);

        if (((rotation / 90) & 0x01) == 0) {
            mCurrentAnimation = new SlideshowAnimation(mCurrentTexture.getWidth(),
                    mCurrentTexture.getHeight(), mRandom);
        } else {
            mCurrentAnimation = new SlideshowAnimation(mCurrentTexture.getHeight(),
                    mCurrentTexture.getWidth(), mRandom);
        }

        if (mPrevAnimation != null) {
            mPrevAnimation.setState(0);
            mPrevAnimation.start();
        }

        mCurrentAnimation.start();
        invalidate();
    }

    public void release() {
        if (mPrevTexture != null) {
            mPrevTexture.recycle();
            mPrevTexture = null;
        }

        if (mCurrentTexture != null) {
            mCurrentTexture.recycle();
            mCurrentTexture = null;
        }
    }

    @Override
    protected boolean onKeyDown(GLRootView glRootView, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ((Activity) mActivity).onBackPressed();
        }

        return true;
    }

    @Override
    protected synchronized void render(GLCanvas canvas) {
        long currentTimeMillis = canvas.currentAnimationTimeMillis();
        boolean requestRender = mTransitionAnimation.calculate(currentTimeMillis);
        GL11 gl = canvas.getGLInstance();
        gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        if (mPrevTexture != null) {
            requestRender |= mPrevAnimation.calculate(currentTimeMillis);
            canvas.save(GLCanvas.SAVE_FLAG_ALPHA | GLCanvas.SAVE_FLAG_MATRIX);
            mPrevAnimation.setState(0);
            mPrevAnimation.apply(canvas);
            canvas.rotate(mPrevRotation, 0, 0, 1);
            mPrevTexture.draw(canvas, -mPrevTexture.getWidth() / 2, -mPrevTexture.getHeight() / 2);
            canvas.restore();
        }

        if (mCurrentTexture != null) {
            requestRender |= mCurrentAnimation.calculate(currentTimeMillis);
            canvas.save(GLCanvas.SAVE_FLAG_ALPHA | GLCanvas.SAVE_FLAG_MATRIX);
            mCurrentAnimation.setState(1);
            mCurrentAnimation.apply(canvas);
            canvas.rotate(mCurrentRotation, 0, 0, 1);
            mCurrentTexture.draw(canvas, -mCurrentTexture.getWidth() / 2,
                    -mCurrentTexture.getHeight() / 2);
            canvas.restore();
        }

        if (loadingState == LOADING) {
            int w = getWidth();
            int h = getHeight();
            int x = w / 2;
            int y = h / 2;
            int s = Math.min(w, h) / 6;
            StringTexture m = mLoadingText;
            ProgressSpinner r = mLoadingSpinner;
            r.draw(canvas, x - r.getWidth() / 2, y - r.getHeight() / 2);
            m.draw(canvas, x - m.getWidth() / 2, y + s / 2 + 5);
            invalidate();
        }

        if (requestRender) {
            invalidate();
        }

        gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private class SlideshowAnimation extends CanvasAnimation {
        private final int mWidth;

        private final int mHeight;

        private float mProgress;

        private int state = 0;

        public SlideshowAnimation(int width, int height, Random random) {
            mWidth = width;
            mHeight = height;
            setDuration(SLIDESHOW_DURATION);
        }

        @Override
        public void apply(GLCanvas canvas) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            float initScale = Math.min(2f,
                    Math.min((float) viewWidth / mWidth, (float) viewHeight / mHeight));

            float centerX = viewWidth / 2;
            float centerY = viewHeight / 2;
            float alpha = 1;
            if (animaState == AnimationMode.TRANSITION) {
                float a = (float) 0.5;
                float b = (float) 1.5;
                if (state == 0) {
                    centerX = viewWidth * (a - mProgress);
                } else {
                    centerX = viewWidth * (b - mProgress);
                }
                centerY = viewHeight / 2;
            } else if (animaState == AnimationMode.ALPHA) {
                if (state == 0) {
                    alpha = 1f - mProgress;
                } else {
                    alpha = mProgress;
                }

            }
            canvas.setAlpha(alpha);
            canvas.translate(centerX, centerY, 0);
            canvas.scale(initScale, initScale, 0);
        }

        @Override
        public int getCanvasSaveFlags() {
            return GLCanvas.SAVE_FLAG_MATRIX;
        }

        @Override
        protected void onCalculate(float progress) {
            mProgress = progress;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

    }
}
