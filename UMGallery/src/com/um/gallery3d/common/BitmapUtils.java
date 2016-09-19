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

package com.um.gallery3d.common;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    private static final int COMPRESS_JPEG_QUALITY = 90;

    public static final int UNCONSTRAINED = -1;

    private static final int SCREEN_WIDTH = 1920;

    private static final int SCREEN_HEIGHT = 1080;

    private static final int TOTAL_COUNT = 10;

    private static final int MIN_CHANGE = 100;

    private BitmapUtils() {
    }

    /*
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage. The function returns a
     * sample size based on the constraints. Both size and minSideLength can be
     * passed in as UNCONSTRAINED, which indicates no care of the corresponding
     * constraint. The functions prefers returning a sample size that generates
     * a smaller bitmap, unless minSideLength = UNCONSTRAINED. Also, the
     * function rounds up the sample size to a power of 2 or multiple of 8
     * because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(int width, int height, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(width, height, minSideLength, maxNumOfPixels);
        return initialSize <= 8 ? Utils.nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
    }

    private static int computeInitialSampleSize(int w, int h, int minSideLength, int maxNumOfPixels) {
        if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED) {
            return 1;
        }

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math
                .sqrt((double) (w * h) / maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            int sampleSize = Math.min(w / minSideLength, h / minSideLength);
            return Math.max(sampleSize, lowerBound);
        }
    }

    // This computes a sample size which makes the longer side at least
    // minSideLength long. If that's not possible, return 1.
    public static int computeSampleSizeLarger(int w, int h, int minSideLength) {
        int initialSize = Math.max(w / minSideLength, h / minSideLength);

        if (initialSize <= 1) {
            return 1;
        }

        return initialSize <= 8 ? Utils.prevPowerOf2(initialSize) : initialSize / 8 * 8;
    }

    // Fin the min x that 1 / x <= scale
    public static int computeSampleSizeLarger(float scale) {
        int initialSize = (int) Math.floor(1f / scale);

        if (initialSize <= 1) {
            return 1;
        }

        return initialSize <= 8 ? Utils.prevPowerOf2(initialSize) : initialSize / 8 * 8;
    }

    // Find the max x that 1 / x >= scale.
    public static int computeSampleSize(float scale) {
        Utils.assertTrue(scale > 0);
        int initialSize = Math.max(1, (int) Math.ceil(1 / scale));
        return initialSize <= 8 ? Utils.nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
    }

    public static Bitmap resizeDownToPixels(Bitmap bitmap, int targetPixels, boolean recycle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) Math.sqrt((double) targetPixels / (width * height));

        if (scale >= 1.0f) {
            return bitmap;
        }

        return resizeBitmapByScale(bitmap, scale, recycle);
    }

    public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);

        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
            return bitmap;
        }

        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (recycle) {
            bitmap.recycle();
        }

        return target;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();

        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        return config;
    }

    public static Bitmap resizeDownBySideLength(Bitmap bitmap, int maxLength, boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min((float) maxLength / srcWidth, (float) maxLength / srcHeight);//图片切换时放大
      
        Log.i(TAG, "resizeDown scale:" + scale);
        if (scale > 1.0f)
            return bitmap;
        return resizeBitmapByScale(bitmap, scale, recycle);
    }

    public static RegionBitmap getPointToPointBitmap(Bitmap bitmap, int x, int y, int screenWidth,
            int screenHeight) {
        RegionBitmap region = new RegionBitmap();
        Bitmap targetBitmap = null;
        boolean result = true;
        int totalHeight = bitmap.getHeight();
        int totalWidth = bitmap.getWidth();
        int targetX = 0;
        int targetY = 0;
        int targetWidth = totalWidth;
        int targetHeight = totalHeight;
        int move = MIN_CHANGE;

        if (totalHeight > screenHeight || totalWidth > screenWidth) {
            if (totalWidth > screenWidth) {
                targetWidth = screenWidth;
                targetX = (totalWidth - screenWidth) / 2;
                if (targetX > MIN_CHANGE * TOTAL_COUNT) {
                    move = targetX / TOTAL_COUNT;
                }
                targetX += (x * move);
                if (targetX < 0) {
                    targetX = 0;
                    result = false;
                }

                if (targetX > totalWidth - screenWidth) {
                    targetX = totalWidth - screenWidth;
                    result = false;
                }
            }

            if (totalHeight > screenHeight) {
                targetHeight = screenHeight;
                targetY = (totalHeight - screenHeight) / 2;
                if (targetY > MIN_CHANGE * TOTAL_COUNT) {
                    move = targetY / TOTAL_COUNT;
                }
                targetY += (y * move);

                if (targetY < 0) {
                    targetY = 0;
                    result = false;
                }

                if (targetY > totalHeight - screenHeight) {
                    targetY = totalHeight - screenHeight;
                    result = false;
                }
            }
            Log.i("RegionDecoder", "getPointToPointBitmap:" + targetX + " " + targetY + " "
                    + targetWidth + " " + targetHeight);
            targetBitmap = Bitmap.createBitmap(bitmap, targetX, targetY, targetWidth, targetHeight);
        } else {
            targetBitmap = bitmap;
        }
        region.set(targetBitmap, result);
        return region;

    }

    public static class RegionBitmap {
        private Bitmap bitmap = null;

        private boolean result = true;

        public RegionBitmap() {
            super();
        }

        public void set(Bitmap bitmap, boolean result) {
            this.bitmap = bitmap;
            this.result = result;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }

    // Resize the bitmap if each side is >= targetSize * 2
    public static Bitmap resizeDownIfTooBig(Bitmap bitmap, int targetSize, boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.max((float) targetSize / srcWidth, (float) targetSize / srcHeight);

        if (scale > 0.5f) {
            return bitmap;
        }

        return resizeBitmapByScale(bitmap, scale, recycle);
    }

    // Crops a square from the center of the original image.
    public static Bitmap cropCenter(Bitmap bitmap, boolean recycle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width == height) {
            return bitmap;
        }

        int size = Math.min(width, height);
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2, (size - height) / 2);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (recycle) {
            bitmap.recycle();
        }

        return target;
    }

    public static Bitmap resizeDownAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int minSide = Math.min(w, h);

        if (w == h && minSide <= size) {
            return bitmap;
        }

        size = Math.min(size, minSide);
        float scale = Math.max((float) size / bitmap.getWidth(), (float) size / bitmap.getHeight());
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (recycle) {
            bitmap.recycle();
        }

        return target;
    }

    public static void recycleSilently(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        try {
            bitmap.recycle();
        } catch (Throwable t) {
            Log.w(TAG, "unable recycle bitmap", t);
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
        if (rotation == 0) {
            return source;
        }

        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);

        if (recycle) {
            source.recycle();
        }

        return bitmap;
    }

    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;

        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();
            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);

                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    if (bitmap != null) {
                        return bitmap;
                    }
                }

                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_JPEG_QUALITY, os);
        return os.toByteArray();
    }

    public static boolean isSupportedByRegionDecoder(String mimeType) {
        if (mimeType == null) {
            return false;
        }

        mimeType = mimeType.toLowerCase();
        return mimeType.startsWith("image/")
                && (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp")
                        && !mimeType.equals("image/tif") && !mimeType.equals("image/tiff")
                        && !mimeType.endsWith("tif") && !mimeType.endsWith("tiff"));
    }

    public static boolean isRotationSupported(String mimeType) {
        if (mimeType == null) {
            return false;
        }

        mimeType = mimeType.toLowerCase();
        return mimeType.equals("image/jpeg");
    }

    public static byte[] compressToBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
