package com.unionman.cityfeature;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

import com.unionman.cityfeature.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2014/11/6.
 */
public class FeatureInitService extends Service {
    private final String TAG = "FeatureInitService";
    private final String LIB_FLODER = "libs";
    private final String PACKAGE_FLODER = "/data/data/com.unionman.cityfeature";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "===================onStartCommand: ");
        Log.d(TAG, "Copy file begin");
        copyFiles(); // !!!!”–ANR∑Áœ’
        Log.d(TAG, "Copy file end");
        return super.onStartCommand(intent, flags, startId);
    }

    private void copyFiles() {
        AssetManager assets = getAssets();

        String[] libFileNames = new String[0];
        try {
            libFileNames = assets.list(LIB_FLODER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.deleteFile(PACKAGE_FLODER +  File.separator + LIB_FLODER);
        writeFiles(assets, libFileNames, LIB_FLODER, PACKAGE_FLODER + File.separator + LIB_FLODER);
    }

    private void writeFiles(AssetManager assets, String[] fileNames, String soucer, String target) {
        InputStream inputStream = null;
        for (String fileName : fileNames) {
            try {
                inputStream = assets.open(soucer + File.separator + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.writeFile(target + File.separator + fileName, inputStream);
        }

        FileUtils.changeFolderMod(target, "777");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
