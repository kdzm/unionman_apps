package com.um.umgallery.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.um.umgallery.R;
import com.hisilicon.higallery.core.BitmapDecodeUtils;

import android.text.Layout;
import android.util.Log;

public class Utils {
	public static final int MIN_DIALOG_DISMISS = 300000;
	public static final int EXPLORE_MODE = 0;
	public static final int ROTATE_MODE = 1;
	public static final int SCALE_MODE = 2;
	public static final int SLIDEIND_MODE = 3;
	public static final int SHOW_INFO = 4;
	public static final int DISMISS_INFO = 5;

	static final String TAG = "Utils";

	public static Runnable mThumRunnable;

	public static void getDetails(final Context context,
			final String imagePath, Handler handler, final TextView textView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Options options = BitmapDecodeUtils.getOptions(imagePath);
				int width = options.outWidth;
				int height = options.outHeight;
				File file = new File(imagePath);
				String name = file.getName();
				String size;
				float length = file.length() / 1024;
				if (length < 1024) {
					BigDecimal bd = new BigDecimal(length);
					length = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					size = length + "KB";
				} else {
					length /= 1024;
					BigDecimal bd = new BigDecimal(length);
					length = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					size = length + "MB";
				}
				String details = context.getString(R.string.details);
				textView.setText(String.format(details, name, width, height,
						size, imagePath));
				textView.setVisibility(View.VISIBLE);
				Log.d(TAG,
						"HiGalleryL::Picture Detail"
								+ String.format(details, name, width, height,
										size, imagePath));
			}
		});
	}

	public void setDetails(final Context context, final String imagePath,
			Handler handler, final TextView fileName, final TextView fileType,
			final TextView fileSize, final TextView filePicsize,
			final TextView fileModifyTime) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Options options = BitmapDecodeUtils.getOptions(imagePath);
				int width = options.outWidth;
				int height = options.outHeight;
				File file = new File(imagePath);
				String name = file.getName();
				String size;
				float length = file.length() / 1024;
				if (length < 1024) {
					BigDecimal bd = new BigDecimal(length);
					length = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					size = length + "KB";
				} else {
					length /= 1024;
					BigDecimal bd = new BigDecimal(length);
					length = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
							.floatValue();
					size = length + "MB";
				}
				SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
				String modify = simple.format(new Date(file.lastModified()));

				fileName.setText(name);
				String end = file
						.getName()
						.substring(file.getName().lastIndexOf(".") + 1,
								file.getName().length()).toLowerCase();
				fileType.setText(end);
				fileSize.setText(size.toString());
				filePicsize.setText(width + "x" + height);
				fileModifyTime.setText(modify);
			}
		});
	}

	public static void showInfo(Context context, Handler handler, int mode) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View mItemLayout;
		
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		switch (mode) {
		case EXPLORE_MODE:
			mItemLayout = inflater.inflate(R.layout.help_info_explore, null);
			mItemLayout.setLayoutParams(params);
			break;
		case ROTATE_MODE:
			mItemLayout = inflater.inflate(R.layout.help_info_rotate, null);
			break;
		case SCALE_MODE:
			mItemLayout = inflater.inflate(R.layout.help_info_scale, null);
			break;
		// case SLIDEIND_MODE:
		// break;
		default:
			mItemLayout = inflater.inflate(R.layout.help_info_explore, null);
			mItemLayout.setLayoutParams(params);
		}
		Message message = new Message();
		message.obj = mItemLayout;
		message.what = SHOW_INFO;
		handler.sendMessage(message);
	}
}
