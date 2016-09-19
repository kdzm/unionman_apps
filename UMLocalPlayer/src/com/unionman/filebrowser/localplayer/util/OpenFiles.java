package com.unionman.filebrowser.localplayer.util;

import java.io.File;

import com.unionman.filebrowser.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class OpenFiles {
	/**
	 * 打开sdcard上的文件
	 * @param context
	 * @param file 要打开的文件
	 */
	public static void open(Context context, File file) {
		if (file != null && file.isFile()) {
			String fileName = file.toString();
			Intent intent;
			if (checkEndsWithInStringArray(fileName, context.getResources()
					.getStringArray(R.array.fileEndingImage))) {
				intent = OpenFiles.getImageFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingWebText))) {
				intent = OpenFiles.getHtmlFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPackage))) {
				intent = OpenFiles.getApkFileIntent(file);
				context.startActivity(intent);

			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingAudio))) {
				intent = OpenFiles.getAudioFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingVideo))) {
				intent = OpenFiles.getVideoFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingText))) {
				intent = OpenFiles.getTextFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPdf))) {
				intent = OpenFiles.getPdfFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingWord))) {
				intent = OpenFiles.getWordFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingExcel))) {
				intent = OpenFiles.getExcelFileIntent(file);
				context.startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, context
					.getResources().getStringArray(R.array.fileEndingPPT))) {
				intent = OpenFiles.getPPTFileIntent(file);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, context.getString(R.string.can_not_open_file), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, context.getString(R.string.not_file), Toast.LENGTH_SHORT).show();
		}

		if (file != null && file.isFile()) {
		    Intent intent = getFileOpenIntent(context, file);
		    if (intent != null) {
		        context.startActivity(intent);
		    } else {
		        Toast.makeText(context, context.getString(R.string.can_not_open_file), 1).show();
		    }
		} else {
		    Toast.makeText(context, context.getString(R.string.not_file), 1).show();
		}
	}

    public static Intent getFileOpenIntent(Context context, File file) {
        Intent intent = null;
        String fileName = file.toString();
        if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingImage))) {
            intent = OpenFiles.getImageFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingWebText))) {
            intent = OpenFiles.getHtmlFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPackage))) {
            intent = OpenFiles.getApkFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingAudio))) {
            intent = OpenFiles.getAudioFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingVideo))) {
            intent = OpenFiles.getVideoFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingText))) {
            intent = OpenFiles.getTextFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPdf))) {
            intent = OpenFiles.getPdfFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingWord))) {
            intent = OpenFiles.getWordFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingExcel))) {
            intent = OpenFiles.getExcelFileIntent(file);
        } else if (checkEndsWithInStringArray(fileName,
                context.getResources().getStringArray(R.array.fileEndingPPT))) {
            intent = OpenFiles.getPPTFileIntent(file);
        } else {
            // do nothing
        	Toast.makeText(context, context.getString(R.string.can_not_open_file), 1).show();
        }

        return intent;
    }

	private static boolean checkEndsWithInStringArray(String checkItsEnd,String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// android获取一个用于打开HTML文件的intent
	public static Intent getHtmlFileIntent(File file) {
		Uri uri = Uri.parse(file.toString()).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(file.toString()).build();
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "text/html");
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	// android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	// android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "text/plain");
		return intent;
	}

	// android获取一个用于打开音频文件的intent
	public static Intent getAudioFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// android获取一个用于打开视频文件的intent
	public static Intent getVideoFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android获取一个用于打开PPT文件的intent
	public static Intent getPPTFileIntent(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// android获取一个用于打开apk文件的intent
	public static Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}

}