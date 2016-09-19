package com.um.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.um.launcher.data.MainPageAppInfo;
import com.um.launcher.db.MainPageAppManager;
import com.um.launcher.util.LogUtils;
import com.um.launcher.util.PackageUtils;
import com.um.launcher.util.TimeUtils;
import com.um.launcher.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by hjian on 2015/3/28.
 */
public class PackageReceiver extends BroadcastReceiver {
    public static final String USER_PACKAGE_ADDED = "unionman.user.action.PACKAGE_ADDED";
    public static final String USER_PACKAGE_REMOVED = "unionman.user.action.PACKAGE_REMOVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getDataString().substring(8);
            LogUtils.i("add packageName: " + packageName);
            if (!PackageUtils.isSystemApplication(context, packageName) && alowShow(context, packageName)) {
                MainPageAppManager manager = new MainPageAppManager(context);
                ArrayList<MainPageAppInfo> appInfos = manager.getAll();
                if (appInfos.size() >= MainPageAppManager.CUSTOM_APP_COUNT) {
                    Collections.sort(appInfos, MainPageAppManager.MAIN_PAGE_APP_COMPARATOR);
                    manager.delete(appInfos.get(appInfos.size() - 1).getPackageName());
                }

                manager.add(new MainPageAppInfo(TimeUtils.getCurrentTimeInLong(), packageName));

                LogUtils.d("packageName: " + context.getPackageName());
                if (PackageUtils.isTopActivity(context, context.getPackageName(), ".MainActivity")) {
                    context.sendBroadcast(new Intent(USER_PACKAGE_ADDED));
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getDataString().substring(8);
            LogUtils.i("remove packageName: " + packageName);
            MainPageAppManager manager = new MainPageAppManager(context);
            manager.delete(packageName);
            LogUtils.d("packageName: " + context.getPackageName());
            if (PackageUtils.isTopActivity(context, context.getPackageName(), ".MainActivity")) {
                context.sendBroadcast(new Intent(USER_PACKAGE_REMOVED));
            }
        }
    }

    private boolean alowShow(Context context, String packageName) {
        HashMap<String, Boolean> map = Util.filterAppParse(context);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        return intent != null && !map.containsKey(packageName);
    }

}
