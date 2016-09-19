package com.um.launcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.um.launcher.R;
import com.um.launcher.util.LogUtils;
import com.um.launcher.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class FocusedRelativeLayout extends RelativeLayout
        implements FocusedBasePositionManager.PositionInterface {
    public static final String TAG = "FocusedRelativeLayout";
    public static final int HORIZONTAL_SINGEL = 1;
    public static final int HORIZONTAL_FULL = 2;
    private static final int SCROLL_DURATION = 100;
    private long KEY_INTERVEL = 20L;
    private long mKeyTime = 0L;
    public int mIndex = -1;
    private boolean mOutsieScroll = false;
    private boolean mInit = false;
    private HotScroller mScroller;
    private int mScreenWidth;
    private int mViewRight = 20;
    private int mViewLeft = 0;
    private int mStartX;
    private long mScrollTime = 0L;
    private int mHorizontalMode = -1;
    private OnChildFocusChangeListener mOnChildFocusChangeListener = null;
    private EdgeListener mOnEdgeListener = null;
    private FocusItemSelectedListener mOnItemSelectedListener = null;
    FocusedLayoutPositionManager mPositionManager;
    private OnScrollListener mScrollerListener = null;
    private int mLastScrollState = 0;
    private Map<View, NodeInfo> mNodeMap = new HashMap();
    boolean isKeyDown = false;
    private SourceChangeListener mSourceChangeListener = null;
    private DirectionKeyListener mDirectionKeyListener = null;

    public void setManualPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.mPositionManager.setManualPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void setFrameRate(int paramInt) {
        this.mPositionManager.setFrameRate(paramInt);
    }

    public void setFrameRate(int paramInt1, int paramInt2) {
        this.mPositionManager.setFrameRate(paramInt1, paramInt2);
    }

    public void setScaleMode(int paramInt) {
        this.mPositionManager.setScaleMode(paramInt);
    }

    public void setScale(boolean paramBoolean) {
        this.mPositionManager.setScale(paramBoolean);
    }

    public void setFocusResId(int paramInt) {
        this.mPositionManager.setFocusResId(paramInt);
    }

    public void setFocusShadowResId(int paramInt) {
        this.mPositionManager.setFocusShadowResId(paramInt);
    }

    public void setItemScaleValue(float paramFloat1, float paramFloat2) {
        this.mPositionManager.setItemScaleValue(paramFloat1, paramFloat2);
    }

    public void setScrollerListener(OnScrollListener paramOnScrollListener) {
        this.mScrollerListener = paramOnScrollListener;
    }

    public void setOnItemSelectedListener(FocusItemSelectedListener paramFocusItemSelectedListener) {
        this.mOnItemSelectedListener = paramFocusItemSelectedListener;
    }

    private void performItemSelect(View paramView, boolean paramBoolean) {
        if (this.mOnItemSelectedListener != null)
            this.mOnItemSelectedListener.onItemSelected(paramView, paramBoolean, this);
    }

    public void setItemScaleFixedX(int paramInt) {
        this.mPositionManager.setItemScaleFixedX(paramInt);
    }

    public void setItemScaleFixedY(int paramInt) {
        this.mPositionManager.setItemScaleFixedY(paramInt);
    }

    public void setFocusMode(int paramInt) {
        this.mPositionManager.setFocusMode(paramInt);
    }

    public void setFocusViewId(int id) {
        for (int i = 0, size = getChildCount(); i < size; i ++) {
            if (getChildAt(i).getId() == id) {
                changeFocus(i);
            }
        }
    }

    public void setHorizontalMode(int paramInt) {
        this.mHorizontalMode = paramInt;
    }

    private void setInit(boolean paramBoolean) {
        synchronized (this) {
            this.mInit = paramBoolean;
        }
    }

    private boolean isInit() {
        synchronized (this) {
            return this.mInit;
        }
    }

    public void setViewRight(int paramInt) {
        this.mViewRight = paramInt;
    }

    public void setViewLeft(int paramInt) {
        this.mViewLeft = paramInt;
    }

    public void setOutsideSroll(boolean paramBoolean) {
        LogUtils.d("setOutsideSroll scroll = " + paramBoolean + ", this = " + this);
        //this.mScrollTime = System.currentTimeMillis();
        this.mScrollTime = TimeUtils.getUptimeMillis();
        this.mOutsieScroll = paramBoolean;
    }

    public FocusedRelativeLayout(Context paramContext) {
        super(paramContext);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(paramContext, new DecelerateInterpolator());
        this.mScreenWidth = paramContext.getResources().getDisplayMetrics().widthPixels;
        this.mPositionManager = new FocusedLayoutPositionManager(paramContext, this);
    }

    public FocusedRelativeLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(paramContext, new DecelerateInterpolator());
        this.mScreenWidth = paramContext.getResources().getDisplayMetrics().widthPixels;
        this.mPositionManager = new FocusedLayoutPositionManager(paramContext, this);
    }

    public FocusedRelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(paramContext, new DecelerateInterpolator());
        this.mScreenWidth = paramContext.getResources().getDisplayMetrics().widthPixels;
        this.mPositionManager = new FocusedLayoutPositionManager(paramContext, this);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int order = this.mIndex;
        if (order < 0)
            return i;
        if (i < order)
            return i;
        if (i >= order)
            return childCount - 1 - i + order;
        return i;
    }

    private synchronized void init() {
        if ((hasFocus()) && (!this.mOutsieScroll) && (!isInit())) {
            int[] arrayOfInt = new int[2];
            int i = 65536;
            for (int j = 0; j < getChildCount(); j++) {
                View localView = getChildAt(j);
                if (!this.mNodeMap.containsKey(localView)) {
                    NodeInfo localNodeInfo = new NodeInfo();
                    localNodeInfo.index = j;
                    this.mNodeMap.put(localView, localNodeInfo);
                }
                localView.getLocationOnScreen(arrayOfInt);
                if (arrayOfInt[0] < i)
                    i = arrayOfInt[0];
            }
            this.mStartX = i;
            LogUtils.d("init mStartX = " + this.mStartX);
            setInit(true);
        }
    }

    public void release() {
        this.mNodeMap.clear();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        LogUtils.d("dispatchDraw");
        super.dispatchDraw(canvas);
        if (VISIBLE == getVisibility())
            this.mPositionManager.drawFrame(canvas);
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        LogUtils.d("onFocusChanged this = " + this + ", mScreenWidth = " + this.mScreenWidth + ", mIndex = " + this.mIndex + ", gainFocus = " + gainFocus + ", child count = " + getChildCount());
        synchronized (this) {
            //this.mKeyTime = System.currentTimeMillis();
        	this.mKeyTime = TimeUtils.getUptimeMillis();
        }
        this.mPositionManager.setFocus(gainFocus);
        this.mPositionManager.setTransAnimation(false);
        this.mPositionManager.setNeedDraw(true);
        this.mPositionManager.setState(1);
        if (!gainFocus) {
            this.mPositionManager.drawFrame(null);
            this.mPositionManager.setFocusDrawableVisible(false, true);
            invalidate();
        } else {
            if (-1 == this.mIndex) {
                this.mIndex = 0;
                this.mPositionManager.setSelectedView(getSelectedView());
            }
            View selectedView =getSelectedView();
            if ((selectedView instanceof ScalePostionInterface))
            {
                ScalePostionInterface localScalePostionInterface = (ScalePostionInterface)selectedView;
                this.mPositionManager.setScaleCurrentView(localScalePostionInterface.getIfScale());
            }
            this.mPositionManager.setLastSelectedView(null);
            invalidate();
        }
    }

    public void getFocusedRect(Rect paramRect) {
        View localView = getSelectedView();
        if (localView != null) {
            localView.getFocusedRect(paramRect);
            offsetDescendantRectToMyCoords(localView, paramRect);
            LogUtils.d("getFocusedRect r = " + paramRect);
            return;
        }
        super.getFocusedRect(paramRect);
    }

    public View getSelectedView() {
        int i = this.mIndex;
        View localView = getChildAt(i);
        return localView;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        LogUtils.d("onKeyUp: " + keyCode + "isCenter: " + (KeyEvent.KEYCODE_DPAD_CENTER == keyCode));
        if (((KeyEvent.KEYCODE_ENTER == keyCode) || (KeyEvent.KEYCODE_DPAD_CENTER == keyCode))
                && (this.isKeyDown)
                && (getSelectedView() != null))
            getSelectedView().performClick();
        this.isKeyDown = false;
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        LogUtils.d("onKeyUp: " + keyCode);
        if (keyEvent.getRepeatCount() == 0)
            this.isKeyDown = true;
        synchronized (this) {
           // if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (System.currentTimeMillis() - this.mScrollTime < SCROLL_DURATION) || (!this.mScroller.isFinished())) {
        	 if ((TimeUtils.getUptimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (this.mPositionManager.getState() == 1) || (TimeUtils.getUptimeMillis() - this.mScrollTime < SCROLL_DURATION) || (!this.mScroller.isFinished())) {   
        	LogUtils.d("onKeyDown mAnimationTime = " + this.mKeyTime + " -- current time = " + System.currentTimeMillis());
                return true;
            }
            //this.mKeyTime = System.currentTimeMillis();
        	 this.mKeyTime = TimeUtils.getUptimeMillis();
        }
        if (!isInit()) {
            init();
            return true;
        }
        View selectedView = getSelectedView();
        NodeInfo localNodeInfo1 = (NodeInfo) this.mNodeMap.get(selectedView);
        View nextView = null;
        int i = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
//                if (localNodeInfo1.fromLeft != null && localNodeInfo1.fromLeft.getVisibility() == VISIBLE)
//                    nextView = localNodeInfo1.fromLeft;
//                else
                    nextView = ((View)selectedView).focusSearch(FOCUS_LEFT);
                i = FOCUS_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                if (localNodeInfo1.fromRight != null && localNodeInfo1.fromRight.getVisibility() == VISIBLE)
//                    nextView = localNodeInfo1.fromRight;
//                else
                    nextView = ((View)selectedView).focusSearch(FOCUS_RIGHT);
                i = FOCUS_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
//                if (localNodeInfo1.fromDown != null && localNodeInfo1.fromDown.getVisibility() == VISIBLE)
//                    nextView = localNodeInfo1.fromDown;
//                else {
                    nextView = ((View) selectedView).focusSearch(FOCUS_DOWN);
//                    if (nextView != null && (this.mNodeMap.get(nextView).index < this.mNodeMap.get(selectedView).index)) {
//                        nextView = selectedView;
//                    }
//                }
                i = FOCUS_DOWN;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
//                if (localNodeInfo1.fromUp != null && localNodeInfo1.fromUp.getVisibility() == VISIBLE)
//                    nextView = localNodeInfo1.fromUp;
//                else {
                    nextView = ((View) selectedView).focusSearch(FOCUS_UP);
//                }
                i = FOCUS_UP;

                break;
            default:
                return super.onKeyDown(keyCode, keyEvent);
        }
        LogUtils.d("onKeyDown v = " + nextView);
        if ((nextView != null) && (this.mNodeMap.containsKey(nextView))) {
            NodeInfo localNodeInfo2 = (NodeInfo) this.mNodeMap.get(nextView);
            this.mIndex = localNodeInfo2.index;
            if (selectedView!=null)
            {
                ((View) selectedView).setSelected(false);
                performItemSelect((View)selectedView,false);
                OnFocusChangeListener localObject2 = ((View)selectedView).getOnFocusChangeListener();
                if (localObject2 != null)
                    ((OnFocusChangeListener) localObject2).onFocusChange((View)selectedView,false);
            }
            View selectedView2 = getSelectedView();
            if (selectedView2 != null) {
                ((View) selectedView2).setSelected(true);
                performItemSelect((View) selectedView2, true);
                OnFocusChangeListener localOnFocusChangeListener = ((View) selectedView2).getOnFocusChangeListener();
                if (localOnFocusChangeListener != null)
                    localOnFocusChangeListener.onFocusChange((View) selectedView2, true);
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    localNodeInfo2.fromRight = ((View)selectedView);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    localNodeInfo2.fromLeft = ((View)selectedView);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (selectedView != null) {
                        localNodeInfo2.fromUp = (selectedView.equals(nextView) ? null : selectedView);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    localNodeInfo2.fromDown = ((View)selectedView);
            }
            boolean bool = true;
            this.mPositionManager.setSelectedView(selectedView2);
            if ((selectedView2 instanceof ScalePostionInterface)) {
                ScalePostionInterface localScalePostionInterface = (ScalePostionInterface) selectedView2;
                bool = localScalePostionInterface.getIfScale();
                this.mPositionManager.setScaleCurrentView(bool);
            }

            ((NodeInfo) this.mNodeMap.get(getSelectedView())).lastView = selectedView;
//            this.mPositionManager.setSelectedView(getSelectedView());
//            this.mPositionManager.computeScaleXY();
//            this.mPositionManager.setScaleCurrentView(bool);
            horizontalScroll();
            this.mPositionManager.setTransAnimation(true);
            this.mPositionManager.setNeedDraw(true);
            this.mPositionManager.setState(1);
            invalidate();
        } else {
            LogUtils.w("onKeyDown select view is null");
            if (mOnEdgeListener != null) {
                mOnEdgeListener.onEdge(i);
            }
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            return super.onKeyDown(keyCode, keyEvent);
        }
        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
        dispactDirectionKey(keyCode, keyEvent);
        return true;
    }

    public void changeFocus(int i) {
        mIndex = i;
//        getSelectedView().requestFocus();
//        requestFocus();
        this.mPositionManager.setSelectedView(getSelectedView());
        invalidate();
    }

    private void dispactDirectionKey(int keyCode, KeyEvent event) {
        if (mDirectionKeyListener != null) {
            mDirectionKeyListener.onDirectionKeyDown(keyCode, event);
        }
    }

    public void setOnDirectionKeyListener(DirectionKeyListener directionKeyListener) {
        this.mDirectionKeyListener = directionKeyListener;
    }

    public void setOnEdgeListener(EdgeListener onEdgeListener) {
        this.mOnEdgeListener = onEdgeListener;
    }

    private void horizontalScroll() {
        if (HORIZONTAL_SINGEL == this.mHorizontalMode)
            scrollSingel();
        else if (HORIZONTAL_FULL == this.mHorizontalMode)
            scrollFull();
    }

    void scrollFull() {
        int[] arrayOfInt = new int[2];
        getSelectedView().getLocationOnScreen(arrayOfInt);
        int left = arrayOfInt[0];
        int right = arrayOfInt[0] + getSelectedView().getWidth();
        LogUtils.d("scrollFull left = " + left + ", right = " + right + ", scaleX = " + this.mPositionManager.getItemScaleXValue());
        int width = getSelectedView().getWidth();
        left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * width / 2.0D);
        right = (int) (left + width * this.mPositionManager.getItemScaleXValue());
        LogUtils.d("scrollFull scaled left = " + left + ", scaled right = " + right);
        getLocationOnScreen(arrayOfInt);
        int m;
        int n;
        if ((right - this.mScreenWidth > 3) && (!this.mOutsieScroll)) {
            m = left - this.mStartX - this.mViewLeft;
            LogUtils.d("scrollFull to right dx = " + m + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
            if (m + this.mScroller.getFinalX() > arrayOfInt[0] + getWidth())
                m = arrayOfInt[0] + getWidth() - this.mScroller.getFinalX();
            n = m * 100 / 300;
            smoothScrollBy(m, n);
            return;
        }
        LogUtils.d("scroll conrtainer left = " + this.mStartX);
        if ((this.mStartX - left > 3) && (!this.mOutsieScroll)) {
            m = right - this.mScreenWidth;
            LogUtils.d("scrollFull to left dx = " + m + ", mStartX = " + this.mStartX + ", currX = " + this.mScroller.getCurrX() + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
            if (this.mScroller.getCurrX() < Math.abs(m))
                m = -this.mScroller.getCurrX();
            n = -m * 100 / 300;
            smoothScrollBy(m, n);
        }
    }

    void scrollSingel() {
        int[] arrayOfInt = new int[2];
        View seletedView = getSelectedView();
        seletedView.getLocationOnScreen(arrayOfInt);
        int left = arrayOfInt[0];
        int right = arrayOfInt[0] + seletedView.getWidth();
        LogUtils.d("scrollSingel left = " + left + ", right = " + right + ", scaleX = " + this.mPositionManager.getItemScaleXValue());
        int k = seletedView.getWidth();
        left = (int) (left + (1.0D - this.mPositionManager.getItemScaleXValue()) * k / 2.0D);
        right = (int) (left + k * this.mPositionManager.getItemScaleXValue());
        LogUtils.d("scrollSingel left = " + arrayOfInt[0] + ", right = " + right);
        int m;

        NodeInfo nodeInfo = mNodeMap.get(getSelectedView());
        if (nodeInfo != null) {
            View view = nodeInfo.lastView;
            if (((ViewTypeInterface)view).getViewType() == ViewTypeInterface.TYPE_POSTER
                    && ((ViewTypeInterface)getSelectedView()).getViewType() == ViewTypeInterface.TYPE_APP) {
                m = (int) getResources().getDimension(R.dimen.video_layout_width);

                LogUtils.d("scrollSingel to right dx = " + m + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
                smoothScrollBy(m, 100);

                if (mSourceChangeListener != null) {
                    mSourceChangeListener.onSourceChange(SourceChangeListener.SOURCE_RIGHT);
                }
                return;
            } else if (((ViewTypeInterface)view).getViewType() == ViewTypeInterface.TYPE_APP
                    && ((ViewTypeInterface)getSelectedView()).getViewType() == ViewTypeInterface.TYPE_POSTER) {
                m = right - this.mScreenWidth;
                LogUtils.d("scrollFull to left dx = " + m + ", mStartX = " + this.mStartX + ", currX = " + this.mScroller.getCurrX() + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
                if (this.mScroller.getCurrX() < Math.abs(m))
                    m = -this.mScroller.getCurrX();
                int n = -m * 100 / 300;
                smoothScrollBy(m, n);
                if (mSourceChangeListener != null) {
                    mSourceChangeListener.onSourceChange(SourceChangeListener.SOURCE_LEFT);
                }
                return;
            }
        }
        if ((right >= this.mScreenWidth) && (!this.mOutsieScroll)) {
            m = right - this.mScreenWidth + this.mViewRight;
            LogUtils.d("scrollSingel to right dx = " + m + ", mStartX = " + this.mStartX + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);
            smoothScrollBy(m, 100);
            return;
        }
        getLocationOnScreen(arrayOfInt);
        LogUtils.d("scrollSingel conrtainer left = " + this.mStartX);
        if ((left < this.mStartX) && (!this.mOutsieScroll)) {
            m = left - this.mStartX;
            LogUtils.d("scrollSingel to left dx = " + m + ", mStartX = " + this.mStartX + ", currX = " + this.mScroller.getCurrX() + ", mScreenWidth = " + this.mScreenWidth + ", left = " + left);

            if (this.mScroller.getCurrX() > Math.abs(m))
                smoothScrollBy(m, 100);
            else
                smoothScrollBy(-this.mScroller.getCurrX(), 100);
        }
    }

    private boolean containView(View paramView) {
        Rect localRect1 = new Rect();
        Rect localRect2 = new Rect();
        getGlobalVisibleRect(localRect1);
        paramView.getGlobalVisibleRect(localRect2);
        return (localRect1.left <= localRect2.left) && (localRect1.right >= localRect2.right) && (localRect1.top <= localRect2.top) && (localRect1.bottom >= localRect2.bottom);
    }

    public void smoothScrollTo(int paramInt1, int paramInt2) {
        int i = paramInt1 - this.mScroller.getFinalX();
        smoothScrollBy(i, paramInt2);
    }

    public void smoothScrollBy(int paramInt1, int paramInt2) {
        LogUtils.w("smoothScrollBy dx = " + paramInt1);
        this.mScroller.startScroll(this.mScroller.getFinalX(), this.mScroller.getFinalY(), paramInt1, this.mScroller.getFinalY(), paramInt2);
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
        invalidate();
    }

    public void setOnSourceChangeListener(SourceChangeListener sourceChangeListener) {
        mSourceChangeListener = sourceChangeListener;
    }

    void reportScrollStateChange(int paramInt) {
        if ((paramInt != this.mLastScrollState) && (this.mScrollerListener != null)) {
            this.mLastScrollState = paramInt;
            this.mScrollerListener.onScrollStateChanged(this, paramInt);
        }
    }

    private boolean checkFocusPosition() {
        if ((null == this.mPositionManager.getCurrentRect()) || (!hasFocus()))
            return false;
        Rect localRect = this.mPositionManager.getDstRectAfterScale(true);
        LogUtils.d("checkFocusPosition this.mPositionManager.getCurrentRect() = " + this.mPositionManager.getCurrentRect() + ", this.mPositionManager.getDstRectAfterScale(true) = " + this.mPositionManager.getDstRectAfterScale(true));
        return (Math.abs(localRect.left - this.mPositionManager.getCurrentRect().left) > 5) || (Math.abs(localRect.right - this.mPositionManager.getCurrentRect().right) > 5) || (Math.abs(localRect.top - this.mPositionManager.getCurrentRect().top) > 5) || (Math.abs(localRect.bottom - this.mPositionManager.getCurrentRect().bottom) > 5);
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            LogUtils.d("computeScroll mScroller.getCurrX() = " + this.mScroller.getCurrX());
        }
        if (this.mScroller.isFinished())
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
        super.computeScroll();
    }

    public void setOnChildFocusChangeListener(OnChildFocusChangeListener onChildFocusChangeListener) {
        this.mOnChildFocusChangeListener = onChildFocusChangeListener;
    }

    public void initState(View view) {
        this.mPositionManager.setSelectedView(view);
        this.mPositionManager.setTransAnimation(true);
        this.mPositionManager.setNeedDraw(true);
        this.mPositionManager.setState(FocusedBasePositionManager.STATE_DRAWING);
    }

    public void initState() {
        initState(getSelectedView());
    }

    public static abstract interface OnChildFocusChangeListener {
        public abstract void onChildFocusChange(View lastView, View focusView);
    }

    public static abstract interface OnScrollListener {
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static final int SCROLL_STATE_FLING = 2;

        public abstract void onScrollStateChanged(RelativeLayout paramRelativeLayout, int paramInt);

        public abstract void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3);
    }

    class FocusedLayoutPositionManager extends FocusedBasePositionManager {
        public FocusedLayoutPositionManager(Context paramView, View arg3) {
            super(paramView, arg3);
        }

        public Rect getDstRectBeforeScale(boolean paramBoolean) {
            View localView = getSelectedView();
            if (null == localView)
                return null;
            Rect localRect1 = new Rect();
            Rect localRect2 = new Rect();
            if ((localView instanceof ScalePostionInterface)) {
                ScalePostionInterface localScalePostionInterface = (ScalePostionInterface) localView;
                if (localScalePostionInterface.getIfScale())
                    localRect1 = localScalePostionInterface.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), true);
                else
                    localRect1 = localScalePostionInterface.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), false);
            } else {
                localView.getGlobalVisibleRect(localRect1);
                int i = localRect1.right - localRect1.left;
                int j = localRect1.bottom - localRect1.top;
                if (!paramBoolean) {
                    localRect1.left = ((int) (localRect1.left + (1.0D - getItemScaleXValue()) * i / 2.0D));
                    localRect1.top = ((int) (localRect1.top + (1.0D - getItemScaleYValue()) * j / 2.0D));
                    localRect1.right = ((int) (localRect1.left + i * getItemScaleXValue()));
                    localRect1.bottom = ((int) (localRect1.top + j * getItemScaleYValue()));
                }
            }
            LogUtils.d("getImageRect imgRect = " + localRect1);
            FocusedRelativeLayout.this.getGlobalVisibleRect(localRect2);
            localRect1.left -= localRect2.left;
            localRect1.right -= localRect2.left;
            localRect1.top -= localRect2.top;
            localRect1.bottom -= localRect2.top;
            localRect1.left += FocusedRelativeLayout.this.mScroller.getCurrX();
            localRect1.right += FocusedRelativeLayout.this.mScroller.getCurrX();
            localRect1.top -= getSelectedPaddingTop();
            localRect1.left -= getSelectedPaddingLeft();
            localRect1.right += getSelectedPaddingRight();
            localRect1.bottom += getSelectedPaddingBottom();
            localRect1.left += getManualPaddingLeft();
            localRect1.right += getManualPaddingRight();
            localRect1.top += getManualPaddingTop();
            localRect1.bottom += getManualPaddingBottom();
            return localRect1;
        }

        public Rect getDstRectAfterScale(boolean paramBoolean) {
            View localView = getSelectedView();
            if (null == localView)
                return null;
            Rect localRect1 = new Rect();
            Rect localRect2 = new Rect();
            if ((localView instanceof ScalePostionInterface)) {
                ScalePostionInterface localScalePostionInterface = (ScalePostionInterface) localView;
                localRect1 = localScalePostionInterface.getScaledRect(getItemScaleXValue(), getItemScaleYValue(), false);
            } else {
                localView.getGlobalVisibleRect(localRect1);
            }
            LogUtils.d("getImageRect imgRect = " + localRect1);
            FocusedRelativeLayout.this.getGlobalVisibleRect(localRect2);
            localRect1.left -= localRect2.left;
            localRect1.right -= localRect2.left;
            localRect1.top -= localRect2.top;
            localRect1.bottom -= localRect2.top;
            localRect1.left += FocusedRelativeLayout.this.mScroller.getCurrX();
            localRect1.right += FocusedRelativeLayout.this.mScroller.getCurrX();
            if ((paramBoolean) && (isLastFrame())) {
                localRect1.top -= getSelectedShadowPaddingTop();
                localRect1.left -= getSelectedShadowPaddingLeft();
                localRect1.right += getSelectedShadowPaddingRight();
                localRect1.bottom += getSelectedShadowPaddingBottom();
            } else {
                localRect1.top -= getSelectedPaddingTop();
                localRect1.left -= getSelectedPaddingLeft();
                localRect1.right += getSelectedPaddingRight();
                localRect1.bottom += getSelectedPaddingBottom();
            }
            localRect1.left += getManualPaddingLeft();
            localRect1.right += getManualPaddingRight();
            localRect1.top += getManualPaddingTop();
            localRect1.bottom += getManualPaddingBottom();
            return localRect1;
        }

        public void drawChild(Canvas paramCanvas) {
        }
    }

    class NodeInfo {
        public int index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;
        public View lastView;

        NodeInfo() {
        }
    }

    public static abstract interface ViewTypeInterface {
        public static final int TYPE_NONE = 0;
        public static final int TYPE_VIDEO = 1;
        public static final int TYPE_POSTER = 2;
        public static final int TYPE_APP = 3;
        public abstract int getViewType();
    }

    public static abstract interface ScalePostionInterface {
        public abstract Rect getScaledRect(float paramFloat1, float paramFloat2, boolean paramBoolean);

        public abstract boolean getIfScale();
    }

    class HotScroller extends Scroller {
        public HotScroller(Context paramInterpolator, Interpolator paramBoolean, boolean arg4) {
            super(paramInterpolator, paramBoolean, arg4);
        }

        public HotScroller(Context paramInterpolator, Interpolator arg3) {
            super(paramInterpolator, arg3);
        }

        public HotScroller(Context arg2) {
            super(arg2, new AccelerateDecelerateInterpolator());
        }

        public boolean computeScrollOffset() {
            boolean bool1 = isFinished();
            boolean bool2 = FocusedRelativeLayout.this.checkFocusPosition();
            LogUtils.d("computeScrollOffset isFinished = " + bool1 + ", mOutsieScroll = " + FocusedRelativeLayout.this.mOutsieScroll + ", needInvalidate = " + bool2 + ", this = " + this);
            if ((FocusedRelativeLayout.this.mOutsieScroll) || (!bool1) || (bool2))
                FocusedRelativeLayout.this.invalidate();
            FocusedRelativeLayout.this.init();
            return super.computeScrollOffset();
        }
    }

    public static abstract interface FocusItemSelectedListener {
        public abstract void onItemSelected(View paramView, boolean paramBoolean, ViewGroup paramViewGroup);
    }

    public static abstract interface EdgeListener {
        public abstract void onEdge(int direction);
    }

    public static abstract interface DirectionKeyListener {
        public abstract void onDirectionKeyDown(int keyCode, KeyEvent keyEvent);
    }

    public static abstract interface SourceChangeListener {
        public static final int SOURCE_LEFT = 1;
        public static final int SOURCE_RIGHT = 2;
        public abstract void onSourceChange(int source);
    }
}