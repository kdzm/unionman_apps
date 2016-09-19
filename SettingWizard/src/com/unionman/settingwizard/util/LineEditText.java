package com.unionman.settingwizard.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {
    private Paint mPaint;

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub  
        mPaint = new Paint();

        mPaint.setStrokeWidth(1.5f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
        mPaint.setColor(color);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画底线  
        canvas.drawLine(0, this.getHeight() - 2, this.getWidth() - 6, this.getHeight() - 2, mPaint);
        //canvas.drawPoint(this.getWidth() - 8, this.getHeight() - 4, mPaint);
    }
}
