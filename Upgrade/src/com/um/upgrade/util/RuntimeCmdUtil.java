package com.um.upgrade.util;

import android.util.Log;

/**
 * Created by ziliang.nong on 14-6-17.
 */
public class RuntimeCmdUtil {

    public static void execCmd(String cmd) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void chmod(String premission, String filePath) {
        try {
            String cmd = "chmod " + premission + " "+filePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rmFile(String filePath) {
        try {
            String cmd = "rm "+filePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mvFile(String srcFilePath, String dstFilePath) {
        try {
            String cmd = "mv "+srcFilePath+" "+dstFilePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rmDir(String dirPath) {
        try {
            String cmd = "rm -r "+dirPath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upgradeRecovery(String srcFilePath) {
        String recoveryPartPath = "/dev/block/platform/hi_mci.1/by-name/recovery_test";
        try {
            String cmd = "cat "+srcFilePath+" > "+recoveryPartPath;
//            String cmd = "dd if="+srcFilePath+" of="+recoveryPartPath;
            Log.d("upgradeRecovery----U668", "cmd: "+cmd);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (Exception e) {
            Log.e("upgradeRecovery----U668", "error: "+e.getStackTrace());
            e.printStackTrace();
        }
    }

}
