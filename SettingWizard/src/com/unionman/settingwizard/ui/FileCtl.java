package com.unionman.settingwizard.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.unionman.settingwizard.R;
import android.content.Context;
import android.util.Log;

public class FileCtl {
	private String PATH[] = {"/data/data/com.vst.itv52.v1/shared_prefs/","/data/data/com.vst.itv52.v1/databases/"};

	private String FILES[] = { "settingSPF.xml" ,"VST.db3"};
	private int FILES_ID[] = { R.raw.setting, R.raw.vst};
	private int BUF_LEN = 1024 * 100;

	public void copyFiles(Context context) {
		// cd("/data/data/com.vst.itv52.v1");
		for (int i = 0; i < PATH.length; i++) {
			chmod("777", PATH[i]);
			mkdir(PATH[i]);
		}

		for (int i = 0; i < FILES.length; i++) {
			File file = new File(PATH[i] + FILES[i]);
			if (file.exists()) {
				file.delete();
			}

			InputStream is = context.getResources()
					.openRawResource(FILES_ID[i]);
			try {
				FileOutputStream fos = new FileOutputStream(PATH[i] + FILES[i]);

				byte[] buffer = new byte[BUF_LEN];
				int count = 0;

				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				chmod("667", PATH[i] + FILES[i]);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	private void mkdir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void chmod(String permission, String path) {
		Process process;
		int status = -1;
		try {
			String command = "busybox chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(command);
			try {
				status = process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (status == 0)
				Log.i("change mod", "change MODE success");
			else
				Log.i("change mod", "change MODE error");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cd(String path) {
		try {
			String command = "cd " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
