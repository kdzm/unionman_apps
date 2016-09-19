package com.unionman.settingwizard.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.pm.IPackageManager;
import android.os.ServiceManager;
import android.content.pm.ParceledListSlice;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

public class PackageUtils {
	private static final String TAG = "PackageUtils";

	public static boolean uninstallPackage(Context context, String packageName) {
		return SystemUtils.shellExecute("pm uninstall " + packageName);
	}
	
	public static boolean installPackage(Context context, String file) {
		return SystemUtils.shellExecute("pm install -r " + file);
	}

	public static boolean forceStopAndWipePackages(Context context) {
		List<PackageInfo> pkgs = getInstalledPackages();
		for (PackageInfo pkg : pkgs) {
			if (!pkg.packageName.equals(context.getPackageName())) {
				Log.v(TAG, "[WARN] stop and wipe package: " + pkg.packageName);
				forceStopPackage(context, pkg.packageName);
				// SystemUtils.shellExecute("pm clear " + pkg.packageName); //
				// ignore it.
			}
		}

		return true;
	}

	private static void forceStopPackage(Context context, String packageName) {
		try {
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			Method method = Class.forName("android.app.ActivityManager")
					.getMethod("forceStopPackage", String.class);
			method.invoke(am, packageName);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static List<PackageInfo> getInstalledPackages() {
		try {
			IPackageManager pm = IPackageManager.Stub
					.asInterface(ServiceManager.getService("package"));
			ParceledListSlice<PackageInfo> slice = pm.getInstalledPackages(0,
					UserHandle.USER_OWNER);
			return slice.getList();
		} catch (RemoteException e) {

		}

		return null;
	}

	public static boolean enableComponet(Context context, ComponentName name,
			boolean enable) {
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(name,
				enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		return true;
	}

	public static boolean setProvisioned(Context context, boolean flag) {
		int newPrvisioned = flag ? 1 : 0;
		int prvisioned = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.DEVICE_PROVISIONED, 0);
		if (prvisioned != newPrvisioned) {
			Settings.Global.putInt(context.getContentResolver(),
					Settings.Global.DEVICE_PROVISIONED, newPrvisioned);
		}

		return true;
	}

	public static boolean enableSettingWizard(Context context, boolean enable) {
		ComponentName name = new ComponentName("com.unionman.settingwizard",
				"com.unionman.settingwizard.ui.ProvisionActivity");
		return enableComponet(context, name, enable);
	}

}
