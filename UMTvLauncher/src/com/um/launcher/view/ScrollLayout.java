
package com.um.launcher.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.um.launcher.MainActivity;
import com.um.launcher.MyAppActivity;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;

/**
 * @author janey
 */
public class ScrollLayout extends ViewGroup {
    // mCurScreen
    private int mCurScreen = 1;
    // SNAP_VELOCITY
    private static final int SNAP_VELOCITY = 0;

    private static final String TAG = "ScrollLayout";
    //
    private static final int TOUCH_STATE_REST = 0;
    //
    private static final int TOUCH_STATE_SCROLLING = 1;

    private float mLastMotionX;
    //
    private Scroller mScroller;

    private int mTouchSlop;

    private int mTouchState = TOUCH_STATE_REST;
    //
    private VelocityTracker mVelocityTracker;

    private int mWidth;
    //
    private Camera mCamera;
    private Matrix mMatrix;
    //
    private float angle = 0;

    private int duration;

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ScrollLayout(Context context) {
        this(context, null);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context, new AccelerateInterpolator(), true);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    protected void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /*
     *
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            drawScreen(canvas, i, drawingTime);
        }
    }

    public void drawScreen(Canvas canvas, int screen, long drawingTime) {
        //
        final int width = getWidth();
        final int scrollWidth = screen * width;
        final int scrollX = this.getScrollX();
        //
        if (scrollWidth > scrollX + width || scrollWidth + width < scrollX) {
            return;
        }
        final View child = getChildAt(screen);
        final int faceIndex = screen;
        final float currentDegree = getScrollX() * (angle / getMeasuredWidth());
        final float faceDegree = currentDegree - faceIndex * angle;
        if (faceDegree > 90 || faceDegree < -90) {
            return;
        }
        final float centerX = (scrollWidth < scrollX) ? scrollWidth + width
                : scrollWidth;
        final float centerY = getHeight() / 2;
        final Camera camera = mCamera;
        final Matrix matrix = mMatrix;
        canvas.save();
        camera.save();
        camera.rotateY(-faceDegree);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        canvas.concat(matrix);
        drawChild(canvas, child, drawingTime);
        canvas.restore();
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        super.dispatchWindowFocusChanged(hasFocus);
    }
    
    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        super.dispatchWindowVisibilityChanged(visibility);
    }

    public ShowAbleInterface getCurScreen() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "mCurScreen---->" + mCurScreen);
        }
        return (ShowAbleInterface) this.getChildAt(mCurScreen);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(mLastMotionX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;

        }
        return mTouchState != TOUCH_STATE_REST;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth,
                        childView.getMeasuredHeight());
                childLeft += childWidth;
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurScreen * width, 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "event down!");
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                break;

            case MotionEvent.ACTION_MOVE:

                int deltaX = (int) (mLastMotionX - x);

                mLastMotionX = x;

                scrollBy(deltaX, 0);

                break;

            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0
                        && x - mLastMotionX > 10) {
                    // Fling enough to move left
                    if (Constant.LOG_TAG) {
                        Log.d(TAG, "snap left");
                    }
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY
                        && mCurScreen < getChildCount() - 1
                        && mLastMotionX - x > 10) {
                    // Fling enough to move right
                    if (Constant.LOG_TAG) {
                        Log.d(TAG, "snap right");
                    }
                    snapToScreen(mCurScreen + 1);
                } else {
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        return true;

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {

        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }

    private void setMWidth() {
        if (mWidth == 0) {
            mWidth = getWidth();
        }
    }

    private void setNext() {
        int count = this.getChildCount();
        View view = getChildAt(count - 1);
        removeViewAt(count - 1);
        addView(view, 0);
    }

    private void setPre() {
        int count = this.getChildCount();
        View view = getChildAt(0);
        removeViewAt(0);
        addView(view, count - 1);
    }

    public void setToTVScreen(int whichScreen) {
        if (whichScreen == 0) {
            return;
        }
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        scrollTo(whichScreen * mWidth, 0);
        for (int i = 0; i < (5 - whichScreen); i++) {
            setPre();
        }
        getCurScreen().isShow();

    }

    public void setToFirstScreen(int whichScreen) {
        Log.d(TAG, "leon...setToFirstScreen");
        if (whichScreen == 0) {
            return;
        }
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        scrollTo(whichScreen * mWidth, 0);
        for (int i = 0; i < (4 - whichScreen); i++) {
            setPre();
        }
        getCurScreen().isShow();

    }
    
    public void setToMovieScreen(int whichScreen) {
        if (whichScreen == 1) {
            return;
        }
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        scrollTo(whichScreen * mWidth, 0);
        for (int i = 0; i < (6 - whichScreen); i++) {
            setPre();
        }
        getCurScreen().isShow();
    }

    public void setToSettingScreen(int whichScreen) {
        if (whichScreen == 4) {
            return;
        }
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        scrollTo(whichScreen * mWidth, 0);
        for (int i = 0; i < (9 - whichScreen); i++) {
            setPre();
        }
        getCurScreen().isShow();
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */

    public void snapToDestination() {
        setMWidth();
        final int destScreen = (getScrollX() + mWidth / 2) / mWidth;
        snapToScreen(destScreen);

    }

    public void snapToScreen(int whichScreen) {
        MainActivity.isSnapLeftOrRight = true;
        if (getChildCount() == 2 && MyAppActivity.isSnapRight) {
            whichScreen = 2;
        } else {
            whichScreen = Math.max(0,
                    Math.min(whichScreen, getChildCount() - 1));
        }
        setMWidth();
        int scrollX = getScrollX();
        int startWidth = whichScreen * mWidth;

        if (scrollX != startWidth) {

            int delta = 0;
            int startX = 0;

            if (whichScreen > mCurScreen) {
                setPre();
                delta = startWidth - scrollX;
                startX = mWidth - startWidth + scrollX;

            } else if (whichScreen < mCurScreen) {
                setNext();
                delta = -scrollX;
                startX = scrollX + mWidth;
            } else {
                startX = scrollX;
                delta = startWidth - scrollX;

            }
            duration = 450;
            if (Constant.LOG_TAG) {
                Log.e(TAG, "duration = " + duration);
            }
            mScroller.startScroll(startX, 0, delta, 0, duration);
            invalidate(); // Redraw the layout
            getCurScreen().isShow();
        }
    }

    public int getCurrentScreen() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "getCurrentScreen--->" + mCurScreen);
        }
        return mCurScreen;
    }

    public boolean isFinished() {
        return mScroller.isFinished();
    }

    public int getDuration() {
        return duration;
    }
    
    
}
