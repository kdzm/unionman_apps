package com.um.ui;

import android.app.Application;

import com.unionman.crashhandler.*;

public class AppExtension extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 异常处理，不需要处理时注释掉这两句即可！
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());
    }
}

