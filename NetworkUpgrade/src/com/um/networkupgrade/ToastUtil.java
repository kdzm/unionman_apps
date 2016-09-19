package com.um.networkupgrade;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {


    public static void showToast(Context ctx, int resID) {
        showToast(ctx, Toast.LENGTH_SHORT, resID);
    }

    public static void showToast(Context ctx, String text) {
        showToast(ctx, Toast.LENGTH_SHORT, text);
    }

    public static void showLongToast(Context ctx, int resID) {
        showToast(ctx, Toast.LENGTH_LONG, resID);
    }


    public static void showLongToast(Context ctx, String text) {
        showToast(ctx, Toast.LENGTH_LONG, text);
    }


    public static void showToast(Context ctx, int duration, int resID) {
        showToast(ctx, duration, ctx.getString(resID));
    }


    public static void showToast(Context ctx, int duration, String text) {
        TextView tv = new TextView(ctx);
        tv.setPadding(15, 10, 15, 10);
        tv.setTextSize(28);
        tv.setText(text);
        tv.setBackgroundResource(R.drawable.toast);
        Toast toast = new Toast(ctx);
        toast.setView(tv);
        toast.setDuration(duration);
        toast.show();
    }

    /**
     * 在UI线程运行弹出
     */
    public static void showToastOnUiThread(final Activity ctx, final String text) {
        if (ctx != null) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(ctx, text);
                }
            });
        }
    }
}
