package com.unionman.settings.content;

import android.content.Context;

import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;

public class StaticWallPaper extends RightWindowBase {
    public StaticWallPaper(Context paramContext) {
        super(paramContext);
    }

    public void initData() {

    }

    public void onInvisible() {
    }

    public void onResume() {
    }

    public void setId() {
        this.frameId = ConstantList.FRAME_STATIC_WALLPAPER;
        this.levelId = 1002;
    }

    public void setView() {
        this.layoutInflater.inflate(R.layout.wallpaper_static, this);
    }

}
