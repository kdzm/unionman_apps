package com.unionman.settingwizard.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BitmapCtl {
    public void setReflectionSync(View sourceView, ImageView destImageView) {
        //        new Handler().postDelayed(new Runnable()
        //        {
        //            @Override
        //            public void run()
        //            {
        ViewGroup sourceImageView = (ViewGroup) sourceView;
        sourceImageView.setDrawingCacheEnabled(true);

        Bitmap mBitmap = sourceImageView.getDrawingCache();

        destImageView.setImageBitmap(createReflectedBitmap(mBitmap));

        //                new Handler().postDelayed(new Runnable()
        //                {
        //                    @Override
        //                    public void run()
        //                    {
        sourceImageView.setDrawingCacheEnabled(false);
        sourceImageView.destroyDrawingCache();
        //                    }
        //                }, 1000);
        //            }
        //        }, 1000);
    }

    public void setReflection(final View sourceView, final ImageView destImageView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewGroup sourceImageView = (ViewGroup) sourceView;
                sourceImageView.setDrawingCacheEnabled(true);
                Bitmap mBitmap = sourceImageView.getDrawingCache();

                destImageView.setImageBitmap(createReflectedBitmap(mBitmap));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sourceImageView.setDrawingCacheEnabled(false);
                        sourceImageView.destroyDrawingCache();
                    }
                }, 1000);
            }
        }, 1000);
    }

    public static Bitmap createReflectedBitmap(Bitmap srcBitmap) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int reflectionWidth = srcWidth;
        int reflectionHeight = 100;

        if (0 == srcWidth || srcHeight == 0) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        try {

            Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 328, srcWidth, reflectionHeight,
                    matrix, false);

            if (null == bitmap) {
                return null;
            }

            Bitmap reflectionBitmap = Bitmap.createBitmap(reflectionWidth, reflectionHeight,
                    Config.ARGB_8888);

            if (null == reflectionBitmap) {
                return null;
            }
            Canvas canvas = new Canvas(reflectionBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            LinearGradient shader = new LinearGradient(0, 0, 0, reflectionHeight, 0x70FFFFFF,
                    0x00FFFFFF, TileMode.MIRROR);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));

            canvas.drawRect(0, 0, reflectionWidth, reflectionHeight, paint);

            return reflectionBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}