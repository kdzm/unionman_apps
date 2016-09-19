package com.um.upgrade.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/*
 *    The MIT License (MIT)
 *
 *   Copyright (c) 2014 Danylyk Dmytro
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

public class DownloadProcessButton extends ProcessButton {

    private static final String TAG = "DownloadProcessButton";

    public static final int WORKING_NONE = 0;
    public static final int WORKING_CHECK_CONFIG = 1;
    public static final int WORKING_DOWNLOAD = 2;

    private int status = WORKING_NONE;

    public DownloadProcessButton(Context context) {
        super(context);
    }

    public DownloadProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isLoadingComplete()) {
            onLoadingComplete();
        } else {
            drawProgress(canvas);
        }

        super.onDraw(canvas);
    }

    private void drawProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorWidth = (float) getMeasuredWidth() * scale;

        getProgressDrawable().setBounds(5, 5, (int) indicatorWidth, getMeasuredHeight() - 5);
        getProgressDrawable().draw(canvas);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
