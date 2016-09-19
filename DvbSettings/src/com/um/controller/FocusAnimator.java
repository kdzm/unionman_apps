package com.um.controller;

import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

/**
 * Created by Administrator on 14-5-23.
 */
public class FocusAnimator {
    /**
     * �����ɶ����ƶ������
     * @param frame   �����
     * @param width   �ƶ�Ŀ��Ŀ�
     * @param height  �ƶ�Ŀ��ĸ�
     * @param paramFloat1 �ƶ�Ŀ�����λ�õ�left,ͨ��xx.getLocationOnScreen��ȡ
     * @param paramFloat2 �ƶ�Ŀ�����λ�õ�top,ͨ��xx.getLocationOnScreen��ȡ
     * */
    public void flyFoucsFrame(ImageView frame, int width, int height, float paramFloat1, float paramFloat2) {
        if(frame == null)
            return;

        frame.setVisibility(View.VISIBLE);
        int mWidth = frame.getWidth();
        int mHeight = frame.getHeight();
        if (mWidth == 0 || mHeight == 0) {
            mWidth = 1;
            mHeight = 1;
        }

        ViewPropertyAnimator localViewPropertyAnimator = frame.animate();
        localViewPropertyAnimator.setDuration(150L);
        localViewPropertyAnimator.scaleX((float) (width * 1) / (float) mWidth);
        localViewPropertyAnimator.scaleY((float) (height * 1) / (float) mHeight);
        localViewPropertyAnimator.x(paramFloat1 + (width - mWidth)/2);
        localViewPropertyAnimator.y(paramFloat2 + (height - mHeight)/2);
        localViewPropertyAnimator.start();
    }
}
