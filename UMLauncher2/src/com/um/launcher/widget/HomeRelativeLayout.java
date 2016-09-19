package com.um.launcher.widget;

import android.content.Context;
import android.util.AttributeSet;

public class HomeRelativeLayout extends FocusedRelativeLayout {

    public HomeRelativeLayout(Context context) {
        super(context);
        init();
    }

    public HomeRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHorizontalMode(HORIZONTAL_SINGEL);
    }
}
