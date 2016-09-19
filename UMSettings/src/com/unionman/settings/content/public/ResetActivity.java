package com.unionman.settings.content;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.unionman.settings.util.SocketClient;

import com.unionman.settings.interfaces.SystemSettingInterface;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;
//import com.hisilicon.android.hisysmanager.HiSysManager;

public class ResetActivity extends RightWindowBase {
	private Button btn_Reset;
	private TextView tv_errorInfo;
	private EditText et_Password;
	private final static String TAG = "com.unionman.settings.content.public--ResetActivity--";
	private final SystemSettingInterface mSystemSettingInterface  =new SystemSettingInterface(context);
	// Service Name
	static final String SOCKET_NAME = "configserver";

	// The end of the command whether to perform
	static boolean isRunning = false;

	// Socket
	static LocalSocket mLocalSocket = null;

	// Socket address
	static LocalSocketAddress mLocalSocketAddress;

	// Input stream
	static InputStream mInputStream;

	// Output stream
	static OutputStream mOutputStream;

	// Data output stream
	static DataOutputStream mDataOutputStream;

	// Command return value
	static String strRecData = null;


	View.OnKeyListener mEditKeyListener = new View.OnKeyListener() {
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt,KeyEvent paramAnonymousKeyEvent) {
			if ((paramAnonymousKeyEvent.getAction() == 0)&& (paramAnonymousInt == 4))
				if (((EditText) paramAnonymousView).getText().length() > 0)
					;
			if ((paramAnonymousKeyEvent.getAction() != 1)|| (paramAnonymousInt != 4)) {
				return false;
			}
			StringUtils.delText((EditText) paramAnonymousView);
			return true;
		}
	};
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View paramAnonymousView) {
			if (ResetActivity.this.checkRestPsw()) {
				ResetActivity.this.showCleanDataConfirmDialog();
				return;
			}
			ResetActivity.this.tv_errorInfo.setVisibility(0);
			ResetActivity.this.et_Password.requestFocus();
			ResetActivity.this.et_Password.setSelection(ResetActivity.this.et_Password.getText().length());
		}
	};
	TextWatcher mTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable paramAnonymousEditable) {
			ResetActivity.this.tv_errorInfo.setVisibility(4);
		}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
		}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
		}
	};

	public ResetActivity(Context paramContext) {
		super(paramContext);
	}

	private boolean checkRestPsw() {
		Logger.i(TAG, "checkRestPsw()--");
		String str = Settings.Secure.getString(this.context.getContentResolver(), "reset_pswd");
		if ((str == null) || (str.equals(""))){
			str = "10086";
		}
		return str.equals(this.et_Password.getText().toString());
	}

	private static boolean deleteDir(File paramFile) {
		Logger.i(TAG, "deleteDir()--");
		String[] arrayOfString;
		if (paramFile.isDirectory())
			arrayOfString = paramFile.list();
		else
			return false;
		for (int i = 0;; i++) {
			if (i >= arrayOfString.length)
				return paramFile.delete();
			if (!deleteDir(new File(paramFile, arrayOfString[i])))
				return false;
		}
	}

	private void showCleanDataConfirmDialog() {
		Logger.i(TAG, "showCleanDataConfirmDialog()--");
		final CustomDialog localCustomDialog = new CustomDialog(this.context,554, -2, R.layout.dialog_reset, R.style.dialog);
		Button localButton1 = (Button) localCustomDialog.findViewById(R.id.reset_yes);
		Button localButton2 = (Button) localCustomDialog.findViewById(R.id.reset_no);
		localButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				((AlarmManager) ResetActivity.this.context.getSystemService("alarm")).setTimeZone("Asia/Shanghai");

				Logger.i(TAG, "!!!rebootWipeUserData  SocketClient mess reset!!!");
				//writeMess("reset");
				try {
//					Thread thr = new Thread("Reboot") {
//						@Override
//						public void run() {
//							try {
//								RecoverySystem.rebootWipeUserData(ResetActivity.this.context);
//								Logger.i(TAG,"Still running after clear?!");
//							} catch (Exception e) {
//								Logger.i(TAG,"Can't perform clear/factory reset" + e);
//							}
//						}
//					};
//					thr.start();
//					return;
					//HiSysManager hisys = new HiSysManager();
					//hisys.reset();
					mSystemSettingInterface.restoreDefault();
					SocketClient socketClient = null;
                    socketClient = new SocketClient();
                    socketClient.writeMsg("reset");
                    socketClient.readNetResponseSync();
					context.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
				} catch (Exception localException) {
					Logger.i(TAG, localException.toString());
				}
			}
		});
		localButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				localCustomDialog.dismiss();
			}
		});
		localCustomDialog.setCancelable(true);
		localCustomDialog.show();
	}

	public void initData() {
		Logger.i(TAG, "initData()--");
		this.et_Password.addTextChangedListener(this.mTextWatcher);
		this.btn_Reset.setOnClickListener(this.mOnClickListener);
		this.et_Password.setOnKeyListener(this.mEditKeyListener);
	}

	public void onInvisible() {
	}

	public void onResume() {
	}

	public void setId() {
		this.frameId = 6;
		this.levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG, "setView()--");
		this.layoutInflater.inflate(R.layout.reset, this);
		this.btn_Reset = ((Button) findViewById(R.id.reset_ott));
		this.et_Password = ((EditText) findViewById(R.id.reset_psw));
		this.tv_errorInfo = ((TextView) findViewById(R.id.reset_error_info));
	}



	private static byte[] intToBytes2(int n) {
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i--) {
			b[i] = (byte) (n >> (i * 8));
		}
		return b;
	}

	private static void writeMess(String mess) {
		Logger.i(TAG, "writeMess()--");
		try {
			mLocalSocket = new LocalSocket();
			mLocalSocketAddress = new LocalSocketAddress(SOCKET_NAME,
					LocalSocketAddress.Namespace.RESERVED);
			mLocalSocket.connect(mLocalSocketAddress);
			mInputStream = mLocalSocket.getInputStream();
			mOutputStream = mLocalSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mDataOutputStream = new DataOutputStream(mOutputStream);
			int strLen = mess.getBytes().length;
			byte[] sendLen = intToBytes2(strLen);
			byte[] allLen = new byte[mess.getBytes().length + 4];

			byte[] srcLen = mess.getBytes();

			for (int i = 0; i < (mess.getBytes().length + 4); i++) {
				if (i < 4) {
					allLen[i] = sendLen[i];
					System.out.println(i);
				} else {
					System.out.println("=" + i);
					allLen[i] = srcLen[i - 4];
				}
			}
			mDataOutputStream.write(allLen);
			mDataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			InputStream m_Rece = mLocalSocket.getInputStream();
			byte[] data;
			int receiveLen = 0;
			while (isRunning) {
				receiveLen = m_Rece.available();
				data = new byte[receiveLen];
				if (receiveLen != 0) {
					m_Rece.read(data);
					strRecData = new String(data);
					// success
					if (strRecData.contains("execute ok")) {
						isRunning = false;
					}
					// fail
					else if (strRecData.contains("failed execute")) {
						isRunning = false;
					}
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			m_Rece.close();
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		Logger.i(TAG, "close()--");
		try {
			mDataOutputStream.close();
			mInputStream.close();
			mOutputStream.close();
			mLocalSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
