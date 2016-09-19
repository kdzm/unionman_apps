package com.hisilicon.higallery.core;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import android.os.SystemProperties;

public class BitmapDecodeUtils {
	public static final String TAG = "BitmapDecodeUtils";
	public static final int WIDTH_2K = 1920;
	public static final int HEIGHT_2K = 1080;
	public static final int WIDTH_4K = 3840;
	public static final int HEIGTH_4K = 2160;

	public static int MAX_WIDTH = WIDTH_4K;
	public static int MAX_HEIHGT = HEIGTH_4K;

	public static boolean is_2k_GalleryL() {
		return "true".equals(SystemProperties.get("ro.config.2k_galleryl",
				"false"));
	}

	public static Options getOptions(String imagePath) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		return options;
	}

	public static Bitmap getOrigionBitmap(String imagePath) {
		Options options = getOptions(imagePath);

		int width = options.outWidth;
		int height = options.outHeight;
		if (width <= 0 || height <= 0) {
			return null;
		}

		if (is_2k_GalleryL()) {
			MAX_WIDTH = WIDTH_2K;
			MAX_HEIHGT = HEIGHT_2K;
		}

		int imageSide = Math.max(width, height);
		int maxSideLenght = Math.max(MAX_WIDTH, MAX_HEIHGT);
		int simpleSize = 1;
		while (imageSide > maxSideLenght) {
			imageSide >>>= 1;
			simpleSize <<= 1;
		}
		options.inSampleSize = simpleSize;
		return loadBitmap(imagePath, options);
	}

	public static Bitmap getThumBitmap(GalleryCore gallery, String imagePath,
			int width, int height) {
		Options options = getOptions(imagePath);
		int simpleSize = 1;
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		//限制大图片解码  原4000*4000  改10000*6000  以下图片可解码
		if (imageWidth * imageHeight > 10000 * 6000) {
			return null;
		}
		if (imageWidth <= 0 || imageHeight <= 0) {
			return null;
		}
		if (imageWidth > width || imageHeight > height) {
			int widthRatio = imageWidth / width;
			int heightRatio = imageHeight / height;
			simpleSize = widthRatio > heightRatio ? heightRatio : widthRatio;
		}
		options.inSampleSize = simpleSize;
		// if (!gallery.decodeSizeEvaluate(imagePath, imageWidth, imageHeight,
		// simpleSize, 0)) {
		// return null;
		// }
		/*
		 * int size = 0; boolean res = nativeDecodeSizeEvaluate(imagePath,
		 * imageWidth, imageHeight, sampleSize, size); if(!res) { return null; }
		 */
		return loadBitmap(imagePath, options);
	}

	public static Bitmap getThumBitmap(Resources res, int resId, int width,
			int height) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inJustDecodeBounds = false;
		int simpleSize = 1;

		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		if (imageWidth <= 0 || imageHeight <= 0) {
			return null;
		}
		if (imageWidth > width || imageHeight > height) {
			int widthRatio = imageWidth / width;
			int heightRatio = imageHeight / height;
			simpleSize = widthRatio > heightRatio ? heightRatio : widthRatio;
		}
		options.inSampleSize = simpleSize;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(res, resId, options);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, "decode resource OOM " + resId, e);
		}
		return bitmap;
	}

	public static Bitmap loadBitmap(String path, Options options) {
		Bitmap bitmap = null;
		if (null != path && !"".equals(path)) {
			int simpleSize = options.inSampleSize;
			while (true) {
				try {
					options.inSampleSize = simpleSize;
					bitmap = BitmapFactory.decodeFile(path, options);
					break;
				} catch (OutOfMemoryError e) {
					simpleSize *= 2;
					Log.d(TAG, "decode bitmap OOM for" + path, e);
				}
			}
		}
		return bitmap;
	}
}
