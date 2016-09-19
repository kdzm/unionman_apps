package com.unionman.settingwizard.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.unionman.factory.EntryFactory;
import com.unionman.settingwizard.R;

public class FactoryEntry extends Activity implements View.OnClickListener {
    private final String mPwdDefault = "yoshiba";
    private final String mPwdSuper = "90123";
    private EditText mPassword = null;
    private Button mCancel = null;
    private Button mConfirm = null;
    private CheckBox mShowPwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factory_entry);
        mPassword = (EditText) findViewById(R.id.edit_password);
        mCancel = (Button) findViewById(R.id.cancel);
        mConfirm = (Button) findViewById(R.id.confirm);
        mShowPwd = (CheckBox) findViewById(R.id.show_password);
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mPassword
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                } else {
                    mPassword
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                }
                mPassword.postInvalidate();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                if (!mPassword.getText().toString().equals(mPwdDefault)
                        && !mPassword.getText().toString().equals(mPwdSuper)) {
                    Toast.makeText(this, R.string.entry_pwd_error, 2000).show();
                    break;
                }
                new EntryFactory(FactoryEntry.this).start();
                break;
        }
    }

}
