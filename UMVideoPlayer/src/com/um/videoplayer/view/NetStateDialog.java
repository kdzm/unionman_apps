package com.um.videoplayer.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import com.um.videoplayer.R;


/** 网络设置状态提示dialog 窗口内容4种：获取IP中,设置IP中，请设置正确IP，正在连接中，连接失败 */
public class NetStateDialog extends Dialog {

    private static NetStateDialog netStateDialog = null;


    public NetStateDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.net_state);
        // initView();
    }

    /**
     * 创建对话框
     */
    public static NetStateDialog createDialog(Context context) {
        netStateDialog = new NetStateDialog(context);
        return netStateDialog;
    }

    /* public void initView() {
         tvState = (TextView) findViewById(R.id.net_state);
         progressImage = (ImageView) findViewById(R.id.settingconnecting);
     }*/


    public void onWindowFocusChanged(boolean hasFocus) {
        if (netStateDialog == null) {
            return;
        }

        ImageView imageView = (ImageView) netStateDialog
                              .findViewById(R.id.settingconnecting);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                                              .getBackground();
        animationDrawable.start();
    }





}
