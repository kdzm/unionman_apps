package com.unionman.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.menumanager.MenuListManager;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;
import com.unionman.settings.tools.UMDebug;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;

public class UMSettingsActivity extends Activity {
	private FrameLayout fl_contentView;
	private LinearLayout linearlayout;
	private LinearLayout lay_down;
	private LinearLayout lay_left;
	private LinearLayout lay_up;
	private ListView lv_menu;
	private EditText et_login;
	private TextView tv_error;
	public UMSettingsActivity mInstance;
	private UMSettings mApplicationContext;
	private CustomDialog loginDialog;
	private Handler mHandler = null;
	private LayoutManager mLayoutManager;
	private boolean isFirstCreate = false;
	public static String AccessPWD = "10086";
	private String strPwdInputTitle = "请输入密码";
	public static final int EXIT_DIALOG = 2;
	public static final int RESET_DIALOG = 1;
	MenuListManager menuListManager;
	private static final String TAG = "UMSettingsActivity";

	View.OnFocusChangeListener containtOnFocusChangeListener = new View.OnFocusChangeListener() {
		public void onFocusChange(View paramAnonymousView,
				boolean paramAnonymousBoolean) {
			Logger.i(TAG, "rightlayout.....hasfocus=" + paramAnonymousBoolean);
			Iterator localIterator;
			if (paramAnonymousBoolean) {
				localIterator = ((FrameLayout) paramAnonymousView)
						.getFocusables(0).iterator();
			} else {
				return;
			}
			
			View localView;
			do {
				if (!localIterator.hasNext()) {
					UMSettingsActivity.this.lay_left.requestFocus();
					return;
				}
				localView = (View) localIterator.next();
				Logger.i(TAG,localView.toString()+ "  "+ ((FrameLayout) paramAnonymousView).getFocusables(0).size());
				UMDebug.umdebug_trace();
			} while ((!localView.isFocusable()) || ((localView instanceof FrameLayout)));
			localView.requestFocus();

		}
	};

	View.OnFocusChangeListener layoutOnFocusChangeListener = new View.OnFocusChangeListener() {
		public void onFocusChange(View paramAnonymousView,
				boolean paramAnonymousBoolean) {
			Logger.i(TAG, "lay_left.....hasfocus="+ paramAnonymousBoolean);
			if (paramAnonymousBoolean){
				UMSettingsActivity.this.lv_menu.requestFocus();
			}

		}
	};


	private void findView() {
		Logger.i(TAG, "findView()--");
		this.lay_left = ((LinearLayout) findViewById(R.id.leftMenu_layout));
		this.lay_left.setOnFocusChangeListener(this.layoutOnFocusChangeListener);
		
		this.lv_menu = ((ListView) findViewById(R.id.menulist_id));
		
		this.fl_contentView = ((FrameLayout) findViewById(R.id.rightContent_layout));
		this.fl_contentView.setOnFocusChangeListener(this.containtOnFocusChangeListener);
		
		this.lay_up = ((LinearLayout) findViewById(R.id.listviewup));
		this.lay_up.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View paramAnonymousView,
							boolean paramAnonymousBoolean) {
						if (paramAnonymousBoolean) {
							UMSettingsActivity.this.lv_menu.requestFocus();
							UMSettingsActivity.this.lv_menu.setSelection(menuListManager.getCount() - 1);
						}
					}
		});
		
		this.lay_down = ((LinearLayout) findViewById(R.id.listviewdown));
		this.lay_down.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View paramAnonymousView,
							boolean paramAnonymousBoolean) {
						if (paramAnonymousBoolean) {
							UMSettingsActivity.this.lv_menu.requestFocus();
							UMSettingsActivity.this.lv_menu.setSelection(0);
						}
					}
		});
	}

	private void forceStopPackages(String paramString) {
		Logger.i(TAG, "forceStopPackages()--");
		Logger.i(TAG,"packagename==" + paramString);
		ActivityManager localActivityManager = (ActivityManager) getSystemService("activity");
		try {
			Method localMethod = ActivityManager.class.getDeclaredMethod(
					"forceStopPackage", new Class[] { String.class });
			localMethod.setAccessible(true);
			localMethod.invoke(localActivityManager,
					new Object[] { paramString });
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	private String getVersion() {
		Logger.i(TAG, "getVersion()--");
		try {
			String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			return str;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return "";
	}

	private void initLeftMenu() {
		Logger.i(TAG, "initLeftMenu()--");
		this.menuListManager = new MenuListManager(this.lv_menu, this,this.mLayoutManager);
		this.menuListManager.initLeftMenu();
		this.menuListManager.setContantView(this.fl_contentView);
	}

	private void initRightContent() {
		Logger.i(TAG, "initRightContent()--");
		this.mLayoutManager = LayoutManager.getLayoutManager(this.fl_contentView);
		this.mLayoutManager.setMainActivity(this);
		this.mApplicationContext.setLayoutManager(this.mLayoutManager);
	}

	private void setSystemLanguage(String paramString) {
		Logger.i(TAG, "setSystemLanguage()--");
		try {
			if (paramString.contains("-"))
				;
			Locale localLocale;
			for (Object localObject1 = new Locale(paramString.split("-")[0],
					paramString.split("-")[1]);; localObject1 = localLocale) {
				Class localClass1 = Class
						.forName("android.app.ActivityManagerNative");
				Object localObject2 = localClass1.getDeclaredMethod(
						"getDefault", new Class[0]).invoke(localClass1,
						new Object[0]);
				Configuration localConfiguration = (Configuration) localObject2
						.getClass()
						.getDeclaredMethod("getConfiguration", new Class[0])
						.invoke(localObject2, new Object[0]);
				Class localClass2 = Class.forName("android.content.res.Configuration");
				localClass2.getDeclaredField("locale").set(localConfiguration,
						localObject1);
				localClass2.getDeclaredField("userSetLocale").set(
						localConfiguration, Boolean.valueOf(true));
				localObject2
						.getClass()
						.getDeclaredMethod("updateConfiguration",
								new Class[] { Configuration.class })
						.invoke(localObject2,
								new Object[] { localConfiguration });
				Class localClass3 = Class
						.forName("android.app.backup.BackupManager");
				localClass3.getDeclaredMethod("dataChanged",
						new Class[] { String.class }).invoke(localClass3,
						new Object[] { "com.android.providers.settings" });

				localLocale = new Locale(paramString);
				return;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			Logger.i(TAG, "Exception--"+localException.getMessage());
		}
	}

	private void showInputPwdWindow() {
		Logger.i(TAG, "showInputPwdWindow()--");
		if (!"0".equals(SystemProperties.get("persist.sys.pwdprotected", "0")))
			return;
		loginDialog = new CustomDialog(this, -2, -2,
				R.layout.dialog_inputpassword, R.style.dialog);
		Button btn_yes = (Button) loginDialog
				.findViewById(R.id.yes);
		Button btn_no = (Button) loginDialog
				.findViewById(R.id.no);
		et_login=(EditText)loginDialog.findViewById(R.id.et_login);
		this.tv_error=(TextView)loginDialog.findViewById(R.id.login_error_info);
		btn_yes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				String str = et_login.getText().toString();
				if(str!=null){
					if (!UMSettingsActivity.AccessPWD.equals(str)) {
						tv_error.setVisibility(View.VISIBLE);
					}else{
						loginDialog.cancel();
					}
				}else{
					tv_error.setVisibility(View.VISIBLE);
				}
			}
		});
		btn_no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				Process.killProcess(Process.myPid());
			}
		});
		loginDialog.setCancelable(true);
		loginDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(
					DialogInterface paramAnonymousDialogInterface,
					int paramAnonymousInt,
					KeyEvent paramAnonymousKeyEvent) {
				if (paramAnonymousInt == 4)
					return true;
				return false;
			}
		});
		loginDialog.show();
	}

	public void onConfigurationChanged(Configuration paramConfiguration) {
		Logger.i(TAG, "onConfigurationChanged()--");
		Logger.i(TAG,"paramConfiguration=" + paramConfiguration);
		super.onConfigurationChanged(paramConfiguration);
		int i = this.menuListManager.getPosition();
		int j = this.menuListManager.getTop();
		this.menuListManager.initList(i, j);
		this.mLayoutManager.clearView();
		try {
			this.mLayoutManager.showLayout(1);
			return;
		} catch (Exception localException) {
			Logger.e(TAG,localException.toString());
		}
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.mInstance = this;
		Logger.i(TAG, "=============================onCreate=================================");
		this.isFirstCreate = true;
		//setSystemLanguage("zh-cn");
		setContentView(R.layout.main);
		this.mApplicationContext = ((UMSettings) getApplicationContext());
		
		findView();
		
		initRightContent();
		
		initLeftMenu();
		
		if (SystemProperties.get("debug").equals("1")){
			ToastUtil.showLongToast(this, "当前版本：" + getVersion());
		}
		
		String password = Settings.Secure.getString(getContentResolver(),"setting_pswd");
		if ((password == null) || ("".equals(password))){
			password = AccessPWD;
		}
		AccessPWD = password;
		
		this.strPwdInputTitle = this.mInstance.getResources().getString(R.string.password_input);
		Logger.i(TAG, "strPwdInputTitle=" + this.strPwdInputTitle);
		this.mHandler = new Handler() {
			public void handleMessage(Message paramAnonymousMessage) {
				Logger.i(TAG, "showInputPwdWindow");
				UMSettingsActivity.this.showInputPwdWindow();
			}
		};
	}

	protected void onDestroy() {
		super.onDestroy();
		this.mLayoutManager.clearView();
		this.isFirstCreate = true;
		Logger.i(TAG,"=============================ondestroy=================================");
		Process.killProcess(Process.myPid());
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		Logger.i(TAG,"===============onKeyDown=============" + paramInt);
		if (paramInt == 4) {
			if (this.mLayoutManager.backShowView() == -1) {
				if (this.lv_menu.hasFocus()){
					//show();
					Process.killProcess(Process.myPid());
				}
			} else{
				return true;
			}
			this.lay_left.requestFocus();
			return true;
		}
		return false;
	}

	protected void onPause() {
		Logger.i(TAG,"=============================onPause=================================");
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "=============================onResume=================================");
		Logger.i(TAG, "isFirstCreate=" + isFirstCreate);

		LinearLayout linearlayout = (LinearLayout) findViewById(R.id.aaa);
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		linearlayout.setBackground(wallpaperManager.getDrawable());

		if (this.isFirstCreate) {
			this.menuListManager.touchModInit();
			this.isFirstCreate = false;
			this.lv_menu.setSelection(0);
		}
		
		// check passphrase.
		checkPsw();
	}

	protected void onStart() {
		Logger.i(TAG,"=============================onStart=================================");
		super.onStart();
	}

	protected void onStop() {
		Logger.i(TAG,"=============================onStop=================================");
		super.onStop();
	}

	private void checkPsw(){
		this.mHandler.sendEmptyMessageDelayed(0, 200L);
	}

	private void show() {
		Logger.i(TAG, "show()--");
		final CustomDialog localCustomDialog = new CustomDialog(this, -2, -2,
				R.layout.dialog_quite, R.style.dialog);
		Button btn_yes = (Button) localCustomDialog
				.findViewById(R.id.reset_yes);
		Button btn_no = (Button) localCustomDialog
				.findViewById(R.id.reset_no);
		btn_yes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				Process.killProcess(Process.myPid());
			}
		});
		btn_no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				localCustomDialog.dismiss();
			}
		});
		localCustomDialog.setCancelable(true);
		localCustomDialog.show();
	}
}