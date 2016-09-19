package com.cvte.tv.at;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created by evan on 14-10-8.
 */
public class TvApplication extends Application {

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }
}
