
package com.um.umgallery.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class ScaleThunbView extends View {
    Point mDisplaySize;
    String mBitmapPath;
    Context mContext;
    Bitmap mThumbBitmap;
    Rect mScreenRect;
    Rect mBitmapRect;
    int mWidth;
    int mHeight;
    Paint mPaint;
    WindowManager mWM;
    WindowManager.LayoutParams mParam;

    public ScaleThunbView(Context context, Point displaySize, String path) {
        super(context);
        mContext = context;
        mDisplaySize = displaySize;
        mBitmapPath = path;
        mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        mWM.getDefaultDisplay().getSize(size);
        mWidth = size.x / 4;
        mHeight = size.y / 4;
        mScreenRect = new Rect(0, 0, mWidth, mHeight);
        mBitmapRect = new Rect();
        mThumbBitmap = getScaledBitmap();
        mPaint = new Paint();
        mPaint.setColor(0xff0055aa);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);

        mParam = new WindowManager.LayoutParams();
        mParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParam.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParam.gravity = Gravity.RIGHT | Gravity.BOTTOM;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xff000000);
        if(null != mThumbBitmap)
        {
            canvas.drawBitmap(mThumbBitmap, null, mBitmapRect, null);
        }
        canvas.drawRect(mScreenRect, mPaint);
    }

    Bitmap getScaledBitmap() {
        Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mBitmapPath, options);
        int sampleSize = 1;
        if (options.outWidth > options.outHeight) {
            sampleSize = options.outWidth / mWidth + 1;
        } else {
            sampleSize = options.outHeight / mHeight + 1;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(mBitmapPath, options);
        float w = options.outWidth;
        float h = options.outHeight;
        float scale = Math.min(mWidth / w, mHeight / h);
        w *= scale;
        h *= scale;
        int gapW = (int) ((mWidth - w) / 2);
        int gapH = (int) ((mHeight - h) / 2);
        mBitmapRect.left = gapW;
        mBitmapRect.right = mWidth - gapW;
        mBitmapRect.top = gapH;
        mBitmapRect.bottom = mHeight - gapH;
        return b;
    }

    public void show() {
        mWM.addView(this, mParam);
    }

    public void hide() {
        mWM.removeView(this);
    }
    
    public void setDrawnFrame(Rect frame) {
        boolean outside = frame.width() > mDisplaySize.x || frame.height() > mDisplaySize.y;
        if (outside) {
            float scale = Math.max(frame.width() / (float)mWidth, frame.height() / (float)mHeight);

            mBitmapRect.left = (int) (frame.left / scale);
            mBitmapRect.top = (int) (frame.top / scale);
            mBitmapRect.right = (int) (frame.right / scale);
            mBitmapRect.bottom = (int) (frame.bottom / scale);

            int xOffset = (mWidth - (mBitmapRect.right + mBitmapRect.left)) / 2;
            int yOffset = (mHeight - (mBitmapRect.bottom + mBitmapRect.top)) / 2;
            mBitmapRect.left += xOffset;
            mBitmapRect.right += xOffset;
            mBitmapRect.top += yOffset;
            mBitmapRect.bottom += yOffset;
            mScreenRect.left = xOffset;
            mScreenRect.top = yOffset;
            mScreenRect.right = (int)(mDisplaySize.x / scale + xOffset);
            mScreenRect.bottom = (int)(mDisplaySize.y / scale + yOffset);
        } else {
            mScreenRect.set(0, 0, mWidth, mHeight);
            float scale = mDisplaySize.x / mWidth;
            mBitmapRect.left = (int) (frame.left / scale);
            mBitmapRect.top = (int) (frame.top / scale);
            mBitmapRect.right = (int) (frame.right / scale);
            mBitmapRect.bottom = (int) (frame.bottom / scale);
        }
        invalidate();
    }

}
