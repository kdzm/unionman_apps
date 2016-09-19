package com.cvte.tv.at.api.tvapi.hisilicon;

import android.util.Log;

public class LogPrint {
    private static boolean info = true;
    private static boolean debug = true;
    private static boolean warning = true;
    public static boolean fatal = true;

    public static void Debug(String msg) {
        if (debug) {
            StackTraceElement line = new Throwable().getStackTrace()[1];
            if (line != null) {
                Log.d("tv-api", "[" + line.getFileName() + ":" + line.getLineNumber() + "]: " + msg);
            }
        }
    }

    public static void Warning(String msg) {
        if (warning) {
            StackTraceElement line = new Throwable().getStackTrace()[1];
            if (line != null) {
                Log.w("tv-api", "[" + line.getFileName() + ":" + line.getLineNumber() + "]: " + msg);
            }
        }
    }

    public static void Fatal(String msg) {
        if (fatal) {
            StackTraceElement line = new Throwable().getStackTrace()[1];
            if (line != null) {
                Log.e("tv-api", "[" + line.getFileName() + ":" + line.getLineNumber() + "]: " + msg);
            }
        }
    }

    public static void Info(String msg) {
        if (info) {
            StackTraceElement line = new Throwable().getStackTrace()[1];
            if (line != null) {
                Log.i("tv-api", "[" + line.getFileName() + ":" + line.getLineNumber() + "]: " + msg);
            }
        }
    }
}
