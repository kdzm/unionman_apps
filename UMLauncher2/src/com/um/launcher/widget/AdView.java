package com.um.launcher.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.um.launcher.util.LogUtils;

/**
 * Created by hjian on 2015/3/27.
 */
public class AdView extends ReflectImageView implements  FocusedRelativeLayout.ScalePostionInterface, FocusedRelativeLayout.ViewTypeInterface{
    public AdView(Context context) {
        super(context);
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getViewType() {
        return FocusedRelativeLayout.ViewTypeInterface.TYPE_VIDEO;
    }

    @Override
    public Rect getScaledRect(float scaleXValue, float scaleYValue, boolean isScaled) {
        Rect firstRect = new Rect();
        this.getGlobalVisibleRect(firstRect);
        int imgReflectH = 0;
        int padding = 2;
        if (this.getScaleX() == 1.0f && this.getScaleY() == 1.0f && isScaled) {
            int imgW = firstRect.right - firstRect.left;
            int imgH = firstRect.bottom - firstRect.top;

            firstRect.left = (int) (firstRect.left + (1.0 - scaleXValue) * imgW / 2) - padding;
            firstRect.top = (int) (firstRect.top + (1.0 - scaleYValue) * imgH / 2) - padding;
            firstRect.right = (int) (firstRect.left + imgW * scaleXValue) + padding;
            firstRect.bottom = (int) (firstRect.top + imgH * scaleYValue) + padding;

            imgReflectH = (int) (this.getReflectHight() * scaleYValue + reflectionGap * scaleYValue + 0.5);

            firstRect.bottom -= imgReflectH;

            return firstRect;
        }

        imgReflectH = (int) (getReflectHight() * this.getScaleY() + reflectionGap * this.getScaleY() + 0.5);
        firstRect.bottom = firstRect.bottom - imgReflectH + padding;
        firstRect.top = firstRect.top - padding;
        firstRect.left = firstRect.left - padding;
        firstRect.right = firstRect.right + padding;

        LogUtils.d("scaleXValue=" + scaleXValue
                + ",bottom=" + firstRect.bottom
                + ",top=" + firstRect.top
                + ",left" + firstRect.left);

        return firstRect;
    }

    @Override
    public boolean getIfScale() {
        return false;
    }
}
