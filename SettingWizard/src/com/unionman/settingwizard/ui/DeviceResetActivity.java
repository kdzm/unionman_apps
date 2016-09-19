/*
 * Copyright (c) 2013,广东九联科技股份有限公司软件开发部
 * All rights reserved.
 * 
 * 文件名称：DeviceResetActivity.java
 * 文件标识：
 * 摘 要：简要描述本文件的内容
 * 
 * 当前版本：1.1
 * 作 者：输入作者（或修改者）名字
 * 完成日期：2010年7月20日
 * 
 * 取代版本：1.0
 * 原作者 ：TODO
 * 完成日期：2013-8-1
 */
package com.unionman.settingwizard.ui;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;
import com.unionman.settingwizard.util.SocketClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 一句话描述
 * <p/>
 * 详细描述
 *
 * @author
 * @version 1.0, 2013-8-1
 */
public class DeviceResetActivity extends Activity {
    private EditText mPasswordEditText;
    private CheckBox mPasswordCheckBox;
    private Button mResetOkBtn;
    private Button mResetCancleBtn;
    String str;
    ImageView mReflectedView;
    LinearLayout mContentView;

    /* 
     * 覆盖方法描述
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_reset);

        mPasswordCheckBox = (CheckBox) this.findViewById(R.id.cb_password_show);
        mPasswordEditText = (EditText) this
                .findViewById(R.id.tv_reset_password);
        mResetOkBtn = (Button) this.findViewById(R.id.btn_reset_sure);
        mResetCancleBtn = (Button) this.findViewById(R.id.btn_reset_cancle);

        mPasswordCheckBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        System.out.println(isChecked);
                        if (isChecked) {
                            mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());

                        } else {
                            mPasswordEditText
                                    .setTransformationMethod(PasswordTransformationMethod
                                            .getInstance());
                        }

                    }
                });

        mResetOkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                str = mPasswordEditText.getText().toString();
                if ("90123".equals(str)) {
                    SocketClient socketClient = null;
                    socketClient = new SocketClient();
                    socketClient.writeMess("reset");
                    socketClient.readNetResponseSync();
                    DeviceResetActivity.this.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
                } else {
                    Toast.makeText(
                            DeviceResetActivity.this,
                            getResources().getString(
                                    R.string.password_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mResetCancleBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceResetActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        mContentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(mContentView, mReflectedView);
    }

//    @Override
//    public void onBackPressed() {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent(DeviceResetActivity.this,
//                MainActivity.class);
//        startActivity(intent);
//        finish();
//        super.onBackPressed();
//    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.more_time_to_exit), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
