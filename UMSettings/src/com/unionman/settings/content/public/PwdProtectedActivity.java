package com.unionman.settings.content;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.unionman.settings.R;
import com.unionman.settings.UMSettingsActivity;
import com.unionman.settings.custom.CheckRadioButton;
//import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;

public class PwdProtectedActivity extends RightWindowBase {

	private Button btn_save;
	private EditText et_pswd;
	private EditText et_pswd2;
	private CheckBox cb_showpsd;
	private CheckRadioButton crb_wifi_toggle;
	private static final String TAG="com.unionman.settings.content.public--PwdProtectedActivity--";

	View.OnKeyListener mEditKeyListener = new View.OnKeyListener() {
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt,
				KeyEvent paramAnonymousKeyEvent) {
			if ((paramAnonymousKeyEvent.getAction() == 0)
					&& (paramAnonymousInt == 4))
				if (((EditText) paramAnonymousView).getText().length() > 0)
					;
			if ((paramAnonymousKeyEvent.getAction() != 1)
					|| (paramAnonymousInt != 4)) {
				return false;
			}
			StringUtils.delText((EditText) paramAnonymousView);
			return true;
		}
	};
	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View paramAnonymousView) {
			String str1 = PwdProtectedActivity.this.et_pswd.getText().toString().trim();
			String str2 = PwdProtectedActivity.this.et_pswd2.getText().toString().trim();
			if ((str1.equals("")) || (str2.equals(""))) {
				ToastUtil.showLongToast(PwdProtectedActivity.this.context, "请检查，密码不能为空");
				return;
			}
			if (!str1.equals(str2)) {
				ToastUtil.showLongToast(PwdProtectedActivity.this.context,"请检查，两次输入的密码不一致");
				return;
			}
			if ((str1 != null) && (str2 != null) && (!str1.equals(""))&& (!str2.equals(""))) {
				Logger.i(TAG, str1+","+str2);
				Settings.Secure.putString(PwdProtectedActivity.this.context.getContentResolver(),"setting_pswd", str1);
				UMSettingsActivity.AccessPWD = str1;
				if (PwdProtectedActivity.this.crb_wifi_toggle.isChecked()) {
					ToastUtil.showLongToast(PwdProtectedActivity.this.context,"保存成功！下次启动设置密码为：" + UMSettingsActivity.AccessPWD);
					return;
				}
				ToastUtil.showLongToast(PwdProtectedActivity.this.context,"保存成功");
				return;
			}
			ToastUtil.showLongToast(PwdProtectedActivity.this.context,"用户名密码不能为空");
		}
	};

	public PwdProtectedActivity(Context paramContext) {
		super(paramContext);
	}

	private void showInputPwd(int paramInt) {
		Logger.i(TAG,"showInputPwd()--");
		findViewById(R.id.line2).setVisibility(paramInt);
		findViewById(R.id.line3).setVisibility(paramInt);
		findViewById(R.id.line4).setVisibility(paramInt);
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		this.btn_save.setOnClickListener(this.mOnClickListener);
		this.et_pswd.setOnKeyListener(this.mEditKeyListener);
		this.et_pswd2.setOnKeyListener(this.mEditKeyListener);
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG, "onResume()--");
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = 7;
		this.levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG, "setView()--");
		this.layoutInflater.inflate(R.layout.password_protected, this);
		this.crb_wifi_toggle = ((CheckRadioButton) findViewById(R.id.crb_wifi_toggle));
		this.et_pswd = ((EditText) findViewById(R.id.edit_iptv_password));
		this.et_pswd2 = ((EditText) findViewById(R.id.edit_iptv_password2));
		this.cb_showpsd = ((CheckBox) findViewById(R.id.checkBox_psd));
	
		this.btn_save = ((Button) findViewById(R.id.button1));
		this.crb_wifi_toggle.setChecked("0".equals(SystemProperties.get("persist.sys.pwdprotected", "0")));
		if (PwdProtectedActivity.this.crb_wifi_toggle.isChecked()){
			Logger.i(TAG, "ischecked");
			showInputPwd(View.VISIBLE);
		}
		this.crb_wifi_toggle.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CheckRadioButton paramAnonymousCheckRadioButton,
							boolean paramAnonymousBoolean) {
						PwdProtectedActivity localPwdProtectedActivity = PwdProtectedActivity.this;
						if (paramAnonymousBoolean) {
							SystemProperties.set("persist.sys.pwdprotected","0");
							ToastUtil.showLongToast(PwdProtectedActivity.this.context,"下次启动设置密码为：" + UMSettingsActivity.AccessPWD);
						} else {
							SystemProperties.set("persist.sys.pwdprotected","1");
						}
						if (!PwdProtectedActivity.this.crb_wifi_toggle.isChecked()){
							Logger.i(TAG, "isUnchecked");
							localPwdProtectedActivity.showInputPwd(8);
						}else{
							Logger.i(TAG, "ischecked");
							localPwdProtectedActivity.showInputPwd(0);
						}
					}
				});
		this.cb_showpsd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(
							CompoundButton paramAnonymousCompoundButton,
							boolean paramAnonymousBoolean) {
						if (paramAnonymousBoolean) {
							PwdProtectedActivity.this.et_pswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
							PwdProtectedActivity.this.et_pswd2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						} else {
							PwdProtectedActivity.this.et_pswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
							PwdProtectedActivity.this.et_pswd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
						}
						PwdProtectedActivity.this.et_pswd.postInvalidate();
						PwdProtectedActivity.this.et_pswd2.postInvalidate();
					}

	/*				@Override
					public void onCheckedChanged(
							CheckRadioButton paramCheckRadioButton,
							boolean paramBoolean) {
						if(paramCheckRadioButton.isChecked()){
							PwdProtectedActivity.this.et_pswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
							PwdProtectedActivity.this.et_pswd2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						}else{
							PwdProtectedActivity.this.et_pswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
							PwdProtectedActivity.this.et_pswd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
						}
						PwdProtectedActivity.this.et_pswd.postInvalidate();
						PwdProtectedActivity.this.et_pswd2.postInvalidate();
					}*/
				});
		while (true) {
			this.et_pswd.setText(UMSettingsActivity.AccessPWD);
			this.et_pswd2.setText(UMSettingsActivity.AccessPWD);
			return;
		}
	}
}
