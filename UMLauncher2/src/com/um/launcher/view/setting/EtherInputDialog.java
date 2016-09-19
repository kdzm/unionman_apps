
package com.um.launcher.view.setting;

import android.app.Dialog;
import android.content.Context;
import android.net.pppoe.PppoeManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.um.launcher.R;

/**
 * the dialog of input Ethernet info
 *
 * @author huyq
 */
public class EtherInputDialog extends Dialog implements
        android.view.View.OnClickListener {
    private Context mContext;
    // PppoeManager object
    private PppoeManager mPppoeManager;

    // EditText of account
    private EditText accountEdit;
    // EditText of password
    private EditText passwordEdit;
    // CheckBox of autoConnect
    private CheckBox autoConnectCb;
    // button of positive
    private Button positiveBtn;
    // button of cancel
    private Button cancelBtn;
    // flag of is AutoReconnect
    private Boolean isAutoReconnect = false;

    // private int pppoe_on = 0;

    public EtherInputDialog(Context context, Handler handler) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        mPppoeManager = (PppoeManager) context
                .getSystemService(Context.PPPOE_SERVICE);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ether_input_dialog);
        initView();
    }

    /**
     * The initialization of view
     */
    private void initView() {
        accountEdit = (EditText) findViewById(R.id.ether_account_input);
        passwordEdit = (EditText) findViewById(R.id.ether_pwd_input);
        autoConnectCb = (CheckBox) findViewById(R.id.ether_autoconnect_cb);
        positiveBtn = (Button) findViewById(R.id.ether_ok_btn);
        positiveBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.ether_cancle_btn);
        cancelBtn.setOnClickListener(this);
        autoConnectCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                isAutoReconnect = isChecked;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ether_ok_btn) {
            // because the PPPOE interface, there are problems, not open the
            // code
            // String userName = account.getText().toString();
            // String pwd = password.getText().toString();
            // if (null == userName || userName.isEmpty()) {
            // return;
            // } else {
            // Settings.Secure.putString(mContext.getContentResolver(),
            // Settings.Secure.PPPOE_USER_NAME, userName);
            // }
            // if (null == pwd || pwd.isEmpty()) {
            // return;
            // } else {
            // Settings.Secure.putString(mContext.getContentResolver(),
            // Settings.Secure.PPPOE_USER_PASS, pwd);
            // }
            // ContentResolver cr = mContext.getContentResolver();
            // try {
            // pppoe_on = Settings.System.getInt(cr, Settings.Secure.PPPOE_ON);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // new LoginPppoe().execute();
            // if (isAutoReconnect
            // && PppoeManager.PPPOE_STATE_ENABLED == pppoe_on
            // && !pppoeManager.getPppoeConnectStatus()) {
            // pppoeManager.setPppoeState(PppoeManager.PPPOE_STATE_ENABLED);
            // }
            // pppoeManager.setPppoeAutoReconnect(isAutoReconnect);
        } else if (v.getId() == R.id.ether_cancle_btn) {
            dismiss();
        }

    }

    class LoginPppoe extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (!mPppoeManager.getPppoeConnectStatus()) {
                mPppoeManager.enablePppoe(true);
                System.putInt(mContext.getContentResolver(), "pppoe_enable", 1);
            }// return boolean
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
}
