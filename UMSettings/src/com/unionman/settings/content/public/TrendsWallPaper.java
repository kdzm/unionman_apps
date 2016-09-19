package com.unionman.settings.content;

import android.app.WallpaperManager;
import android.content.Context;

import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;

public class TrendsWallPaper extends RightWindowBase {
    private WallpaperManager wallpaperManager;

    public TrendsWallPaper(Context paramContext) {
        super(paramContext);
        wallpaperManager = WallpaperManager.getInstance(context);
    }

    public void initData() {

    }

    public void onInvisible() {
    }

    public void onResume() {
    }

    public void setId() {
        this.frameId = ConstantList.FRAME_TRENDS_WALLPAPER;
        this.levelId = 1002;
    }

    public void setView() {
        this.layoutInflater.inflate(R.layout.wallpaper_trends, this);
    }

}
