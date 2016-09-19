package com.unionman.settings.content;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.unionman.settings.R;
import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;

public class NetworkCheck extends RightWindowBase {
	private Button btnOneKey;
	private final static String TAG = "UMSettings--NetworkCheck";

	public NetworkCheck(Context paramContext) {
		super(paramContext);
	}

	public void initData() {
		Log.i(TAG,"initData");
	}

	public void onInvisible() {
		Log.i(TAG,"onInvisible");
	}

	public void onResume() {
		Log.i(TAG,"onResume");
	}

	public void setId() {
		Log.i(TAG,"setId");
		this.frameId = 13;
		this.levelId = 1001;
	}

	public void setView() {
		Log.i(TAG,"setView");
		this.layoutInflater.inflate(R.layout.network_check, this);
		this.btnOneKey = ((Button) findViewById(R.id.btn_netcheck));
		this.btnOneKey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					layoutManager.showLayout(ConstantList.NET_CHECK);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		Button bt_title_focus = (Button)findViewById(R.id.bt_net_title_for_focus);
		Button bt_bottom_focus = (Button)findViewById(R.id.bt_net_bottom_for_focus);
		OnFocusChangeListener focusListenner = new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				NetworkCheck.this.btnOneKey.requestFocus();
			}
		};
		bt_title_focus.setOnFocusChangeListener(focusListenner);
		bt_bottom_focus.setOnFocusChangeListener(focusListenner);
	}

}
