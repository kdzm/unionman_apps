package com.um.usbupgrade;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class UsbUpgradeReceiver extends BroadcastReceiver {
	private final String TAG = "UsbUpgrade--UsbUpgradeReceiver";
	private final String UPGRADE_ZIP_FILE = "update.zip";
	private final String UPGRADE_INFO_FILE = "META-INF/com/google/android/update-info";

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		Log.v(TAG, "onReceive() is called. intent action=" + arg1.getAction());
		if(!SystemProperties.get("persist.sys.usb.upgrade", "false").equals("true")){
			return ;
		}
		if (arg1.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
			String path = arg1.getData().getPath();
			Log.v(TAG, "usb disk is mounted, path: " + path);
			if (null != path && CheckUpdateFile(path)) {
				Log.v(TAG, "usb upgrade dictected!. located at: " + path);
				try {
				        PackageManager manager = arg0.getPackageManager();
				        PackageInfo info = manager.getPackageInfo(arg0.getPackageName(), 0);
				        String version = info.versionName;
				        Log.i(TAG, "UsbUpgrade version="+version);
				    } catch (Exception e) {
				        e.printStackTrace();
				}
				startUpgradeService(arg0, path);
			}
		}
	}


	private boolean CheckUpdateFile(String path) {
		File file = new File(path + "/" + UPGRADE_ZIP_FILE);

		Log.d(TAG, "file.path = " + file.getAbsolutePath()
				+ " file.exists() = " + file.exists());

		try {
			if (file.exists() && checkVersion(file)) {
				return true;
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			return false;
		}
		return false;

	}
	
	
	private String getOldVersion() {
		String ver = "0";
		try {
			ver = SystemProperties.get("ro.build.version.incremental");
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			ver = "0";
		}
		
		return ver;
	}
	
	private boolean checkVersion(File zipFile) {
		try {
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));
			ZipEntry entry;
			boolean find_info_file_flag = false;
			while ((entry = zis.getNextEntry()) != null) {
				//Log.v(TAG, "find file:" + entry.getName());
				if (entry.getName().toString().equals(UPGRADE_INFO_FILE)) {
					Log.v(TAG, "find upgrade info file:" + UPGRADE_INFO_FILE);
					find_info_file_flag = true;
					BufferedReader br = new BufferedReader(
							new InputStreamReader(zis));
					Map<String, String> map = new HashMap<String, String>();
					
					String line = br.readLine();
					while(line != null) {
						String[] items = line.split("=");
						if (items.length == 2) {
							map.put(items[0], items[1]);
						}
						line = br.readLine();
					} 
					
					br.close();
				
					String version = map.get("Software-Version");
					if (version == null) {
						break;
					}
					Log.v(TAG, "new version=" + version);
					Log.v(TAG, "old version=" + getOldVersion());
					if (!version.equals(getOldVersion())) {
						zis.close();
						return true;
					} 
					break;
				}
				
			}
			if(!find_info_file_flag)
			    Log.e(TAG, "Can't Find Upgrade Info File!"+UPGRADE_INFO_FILE);
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	private void startUpgradeService(Context context, String mountPoint) {
            Log.i(TAG, "Start service....... mountPoint:"+mountPoint);
            Intent it = new Intent(context, UsbUpgradeService.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("filepath", mountPoint);
            context.startService(it);
	}
}
