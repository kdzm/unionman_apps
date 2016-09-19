package com.um.launcher.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;

import com.um.launcher.util.ImageUtils;

public class ReflectImageView extends ImageView implements FocusedRelativeLayout.ScalePostionInterface {
    public static final int SHOW_TOP = 1;
    public static final int SHOW_BOTTOM = 2;
    public static final int SHOW_CENTER = 3;

    private String TAG = "ReflectImageView";
    private int mReflectHight = 0;
    public int reflectionGap = 2;
    public int filmPostion = 0;
    public ImageView imageView = null;
    public GridView gridView = null;
    public String name = "";
    public int txtShowType = 0;
    private String text = "";
    private int textSize = 24;
    public boolean isShow = false;
    private int type = 0;

    public int getReflectHight() {
        return mReflectHight;
    }

    public void setReflectHight(int reflectHight) {
        this.mReflectHight = reflectHight;
    }

    public ReflectImageView(Context context) {
        super(context);
    }

    public ReflectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(1, 2, 2, Color.BLACK);
        paint.setTextSize(textSize);
        int hight = 0;
        int width = 0;
        int textW = (int) paint.measureText(getText());

        if (type == SHOW_BOTTOM) {
            width = (this.getWidth() - textW) / 2;
            hight = this.getHeight() - mReflectHight - textSize;
        } else if (type == SHOW_TOP) {
            width = this.getWidth() - textW - 20;
            hight = 20 + textSize;
        } else {
            width = this.getWidth() - textW - 10;
            hight = (this.getHeight() - mReflectHight + textSize) / 2;
        }
        canvas.drawText(getText(), width, hight, paint);
    }

    public void setText(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    @Override
    public void setImageResource(int resId) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
        int imageViewHight = this.getLayoutParams().height;
        int imageViewWidth = this.getLayoutParams().width;
        if (imageViewHight > originalBitmap.getHeight() || imageViewWidth > originalBitmap.getWidth()) {
            this.setImageBitmap(ImageUtils.getScaleBitmap(originalBitmap, imageViewWidth, imageViewHight));
        } else {
            super.setImageResource(resId);
        }
    }

    public void setImageResource(int resId, int reflectHight) {
        this.mReflectHight = 80;
        Bitmap originalImage = BitmapFactory.decodeResource(getResources(), resId);
        CreateReflectBitmap(originalImage, reflectHight);
    }

    public void setImageBitmap(Bitmap bitmap, int reflectHight) {
        this.mReflectHight = 80;
        CreateReflectBitmap(bitmap, reflectHight);
    }

    private void CreateReflectBitmap(Bitmap originalImage, int reflectHight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, reflectHight, width, (int) (height - reflectHight), matrix,
                false);
        final Bitmap bitmap4Reflection = Bitmap.createBitmap(width, (int) (height + reflectHight), Config.ARGB_8888);
        final Canvas canvasRef = new Canvas(bitmap4Reflection);
        canvasRef.drawBitmap(originalImage, 0, 0, null);
        final Paint deafaultPaint = new Paint();
        deafaultPaint.setColor(Color.TRANSPARENT);
        canvasRef.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        reflectionImage.recycle();
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmap4Reflection.getHeight() + reflectionGap,
                0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvasRef.drawRect(0, height, width, bitmap4Reflection.getHeight() + reflectionGap, paint);

        this.setImageBitmap(bitmap4Reflection);
    }

    @Override
    public Rect getScaledRect(float scaleXValue, float scaleYValue, boolean isScaled) {
        Rect firstRect = new Rect();
        this.getGlobalVisibleRect(firstRect);
        int imgReflectH = 0;
        if (this.getScaleX() == 1.0f && this.getScaleY() == 1.0f && isScaled) {
            int imgW = firstRect.right - firstRect.left;
            int imgH = firstRect.bottom - firstRect.top;

            firstRect.left = (int) (firstRect.left + (1.0 - scaleXValue) * imgW / 2);
            firstRect.top = (int) (firstRect.top + (1.0 - scaleYValue) * imgH / 2);
            firstRect.right = (int) (firstRect.left + imgW * scaleXValue);
            firstRect.bottom = (int) (firstRect.top + imgH * scaleYValue);

            imgReflectH = (int) (mReflectHight * scaleYValue + reflectionGap * scaleYValue + 0.5);

            firstRect.bottom -= imgReflectH;

            return firstRect;
        }

        imgReflectH = (int) (mReflectHight * this.getScaleY() + reflectionGap * this.getScaleY() + 0.5);
        firstRect.left = firstRect.left;
        firstRect.top = firstRect.top;
        firstRect.right = firstRect.right;
        firstRect.bottom = firstRect.bottom - imgReflectH;

        Log.d(TAG, "scaleXValue=" + scaleXValue + ",bottom=" + firstRect.bottom + ",top=" + firstRect.top + ",left" + firstRect.left);

        return firstRect;
    }

    @Override
    public boolean getIfScale() {
        return true;
    }
}
