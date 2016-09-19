package com.unionman.filebrowser.localplayer.util;

import com.unionman.filebrowser.R;
import com.unionman.filebrowser.localplayer.CustomApplication;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {

    public static void showToast(Context ctx, int duration, String text) {
        TextView tv = new TextView(ctx);
        tv.setPadding(15, 8, 15, 8);
        tv.setTextSize(28);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.toast_bg);
        Toast toast = new Toast(ctx);
        toast.setView(tv);
        toast.setDuration(duration);
        toast.show();
    }
    public static void toast(String text) {
        TextView tv = new TextView(CustomApplication.getContext());
        tv.setPadding(15, 8, 15, 8);
        tv.setTextSize(28);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.toast_bg);
        Toast toast = new Toast(CustomApplication.getContext());
        toast.setView(tv);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
