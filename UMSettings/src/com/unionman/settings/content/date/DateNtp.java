package com.unionman.settings.content;

import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.IPUtil;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;
import com.unionman.settings.tools.Logger;
import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DateNtp extends RightWindowBase {
	private EditText mEditText;
	private static final String TAG="com.unionman.settings.content.date--DateNtp--";
	View.OnKeyListener mEditKeyListener = new View.OnKeyListener() {
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt,
				KeyEvent paramAnonymousKeyEvent) {
			if ((paramAnonymousKeyEvent.getAction() == 0)
					&& (paramAnonymousInt == 4))
				if (((EditText) paramAnonymousView).getText().length() > 0)
					;
			while ((paramAnonymousKeyEvent.getAction() != 1)
					|| (paramAnonymousInt != 4)) {
				return false;
			}
			StringUtils.delText((EditText) paramAnonymousView);
			return true;
		}
	};
	private Button btn_save;

	public DateNtp(Context paramContext) {
		super(paramContext);
	}

	public void initData() {
		this.mEditText.setOnKeyListener(this.mEditKeyListener);
	}

	public void onInvisible() {
	}

	public void onResume() {
		String str_ntp = Settings.Secure.getString(this.context.getContentResolver(), "ntp_server");
		/*if (str_ntp == null) {
			String strSerialno = SystemProperties.get("ro.serialno", "");
			if (strSerialno.substring(40, 41).equals("0")) {
				str_ntp = "183.235.3.59";
			}else if (strSerialno.substring(40, 41).equals("1")) {
				str_ntp = "221.181.100.40";
			}
		}*/
		mEditText.setText(str_ntp);
		mEditText.requestFocus();
		mEditText.setSelection(str_ntp.length());
	}

	public void setId() {
		frameId = ConstantList.FRAME_DATE_NTP;
		levelId = 1002;
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		layoutInflater.inflate(R.layout.date_ntp, this);
		btn_save = ((Button) findViewById(R.id.btn_ntp));
		mEditText = ((EditText) findViewById(R.id.date_ntp_server));
		mEditText.requestFocus();
		btn_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				String str_et = mEditText.getText().toString().trim();
				if ((str_et != null) && (!str_et.equals("")) && (IPUtil.checkIP(str_et))) {
					Settings.Secure.putString(DateNtp.this.context.getContentResolver(),"ntp_server", str_et);
					layoutManager.backShowView();
					return;
				}
				ToastUtil.showToast(DateNtp.this.context, "请输入合法ip地址");
			}
		});
	}
}
