package com.unionman.settings.content;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;
import com.unionman.settings.tools.Logger;
import android.os.SystemProperties;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class UpgradeActivity extends RightWindowBase {
	private static final String TAG = "com.unionman.settings.content.public--UpgradeActivity--";

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,Intent paramAnonymousIntent) {
			Logger.i(TAG,"BroadcastReceiver()--onReceive()--");
			while (true) {
				UMDebug.umdebug_trace();
				try {
					int i = -1;
					if (paramAnonymousIntent.hasExtra("upgradeInfoType")) {
						i = paramAnonymousIntent.getIntExtra("upgradeInfoType",-1);
						if (i == -1)
							break;
						if (UpgradeActivity.this.mStatus != i) {
							UpgradeActivity.this.check.setClickable(true);
							UpgradeActivity.this.mStatus = i;
						}
					}
					switch (i) {
					default:
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						if (!paramAnonymousIntent.hasExtra("upgradeInfoPercent"))
							return;
						int j = paramAnonymousIntent.getIntExtra("upgradeInfoPercent", 0);
						if (j == UpgradeActivity.this.download_percent)
							return;
						UpgradeActivity.this.download_percent = j;
						Message localMessage = new Message();
						localMessage.arg1 = UpgradeActivity.this.download_percent;
						localMessage.what = 0;
						UpgradeActivity.this.myHandler.sendMessage(localMessage);
						if (!paramAnonymousIntent.hasExtra("status"))
							continue;
						i = paramAnonymousIntent.getIntExtra("status", -1);
						break;
					case 0:
						ToastUtil.showToast(paramAnonymousContext, "版本检测中");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						continue;
					case 1:
						ToastUtil.showToast(paramAnonymousContext, "无法连接升级服务器");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 2:
						ToastUtil.showToast(paramAnonymousContext, "已经是最新版本了，无需升级");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 3:
						ToastUtil.showToast(paramAnonymousContext, "参数错误");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 4:
						ToastUtil.showToast(paramAnonymousContext, "服务器错误");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 5:
						ToastUtil.showToast(paramAnonymousContext, "服务器中的升级文件不存在");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 6:
						ToastUtil.showToast(paramAnonymousContext, "升级文件检验完成");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 7:
						ToastUtil.showToast(paramAnonymousContext, "正常开始下载,断点续传");
						UpgradeActivity.this.check.setText(R.string.upgrade_stop);
						break;
					case 8:
						ToastUtil.showToast(paramAnonymousContext, "下载中");
						UpgradeActivity.this.check.setText(R.string.upgrade_stop);
						break;
					case 9:
						ToastUtil.showToast(paramAnonymousContext, "存储空间不足");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 10:
						ToastUtil.showToast(paramAnonymousContext, "下载异常停止");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 11:
						ToastUtil.showToast(paramAnonymousContext, "升级包与所属顶盒设备类型不匹配");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 12:
						ToastUtil.showToast(paramAnonymousContext, "下载已停止");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 13:
						ToastUtil.showToast(paramAnonymousContext, "升级包验证失败");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 14:
						ToastUtil.showToast(paramAnonymousContext, "创建或重命名文件失败");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					case 15:
						ToastUtil.showToast(paramAnonymousContext, "正在检验已下载的升级包");
						UpgradeActivity.this.check.setText(R.string.upgrade_start);
						break;
					}
				} catch (Exception localException) {
					localException.printStackTrace();
					return;
				}
			}
		}
	};
	private Button check;
	private int download_percent;
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
	private int mStatus = -1;
	Handler myHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			super.handleMessage(paramAnonymousMessage);
			switch (paramAnonymousMessage.what) {
			default:
				return;
			case 0:
				int i = paramAnonymousMessage.arg1;
				UpgradeActivity.this.progressBar.setProgress(i);
				UpgradeActivity.this.upgradePercent.setText(i + "%");
				if (i == -1) {
					UpgradeActivity.this.progressBar.setVisibility(4);
					UpgradeActivity.this.upgradePercent.setVisibility(4);
					return;
				}
				UpgradeActivity.this.progressBar.setVisibility(0);
				UpgradeActivity.this.upgradePercent.setVisibility(0);
				return;
			case 1:
			}
			UpgradeActivity.this.check.setClickable(true);
		}
	};
	private ProgressBar progressBar;
	private TextView upgradePercent;
	private EditText url;

	public UpgradeActivity(Context paramContext) {
		super(paramContext);
	}

	private void registerBroadCast() {
		IntentFilter localIntentFilter = new IntentFilter(
				"com.unionman.action.UPGRADE_INFO");
		this.context.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
	}

	private void unregisterBroadCast() {
		this.context.unregisterReceiver(this.mBroadcastReceiver);
	}

	public void initData() {
		this.url.setOnKeyListener(this.mEditKeyListener);
		this.check.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				UpgradeActivity.this.check.setClickable(false);
				UpgradeActivity.this.myHandler.sendEmptyMessageDelayed(1, 1000L);
				if (UpgradeActivity.this.mStatus == 8) {
					Intent localIntent1 = new Intent();
					localIntent1
							.setAction("com.unionman.action.STOP_UPGRADE_ACTION");
					UpgradeActivity.this.context.sendBroadcast(localIntent1);
					return;
				}
				String str = UpgradeActivity.this.url.getText().toString().trim();
				if ((str == null) || (str.equals(""))) {
					ToastUtil.showToast(UpgradeActivity.this.context, "升级地址不能为空");
					return;
				}
				// Settings.Secure.putString(UpgradeActivity.this.context.getContentResolver(),
				// "upgrade_url", str);
				// try{
				//
				// Context otherAppsContext =
				// UpgradeActivity.this.context.createPackageContext("com.um.networkupgrad",
				// Context.CONTEXT_IGNORE_SECURITY);
				// SharedPreferences configxml =
				// otherAppsContext.getSharedPreferences("URL",
				// Context.MODE_WORLD_WRITEABLE);
				// String mUrl = configxml.getString("URL", "null");
				// Log.i("yanjiwei","升级检测  获取SharedPreferences mUrl = "+mUrl);
				// configxml.edit().putString("URL", str);
				// boolean a = configxml.edit().commit();
				// if(a)
				// {
				// Log.i("yanjiwei","commit 成功 ");
				// }
				// else
				// {
				// Log.i("yanjiwei","commit 失败 ");
				// }
				//
				// mUrl = configxml.getString("URL", "null");
				// Log.i("yanjiwei","升级检测  2222 获取SharedPreferences mUrl = "+mUrl);
				// Log.i("yanjiwei","升级检测  提交SharedPreferences 中！！！！");
				// }catch(NameNotFoundException e)
				// {
				// Log.i("yanjiwei","升级检测  提交SharedPreferences 失败");
				// e.printStackTrace();
				// }

				SystemProperties.set("persist.sys.um.upgrade.url", str);

				Intent intent = new Intent();
				ComponentName componentName = new ComponentName("com.um.networkupgrade", "com.um.networkupgrade.NetworkUpgradeMainActivity");
				intent.setComponent(componentName);
				context.startActivity(intent);

//				Intent localIntent2 = new Intent();
//				localIntent2.setAction("com.um.networkupgrad.BroadCast");
//				localIntent2.putExtra("URL", str);
//
//				UpgradeActivity.this.context.sendBroadcast(localIntent2);
//				ToastUtil.showToast(UpgradeActivity.this.context, "版本检测中");
			}
		});
	}

	public void onInvisible() {
		unregisterBroadCast();
	}

	public void onResume() {
		this.check.setClickable(true);
		String str = SystemProperties.get("persist.sys.um.upgrade.url","gd.umstb.com.cn");
		if (str != null)
			this.url.setText(str);
		registerBroadCast();
	}

	public void setId() {
		this.frameId = 3;
		this.levelId = 1001;
	}

	public void setView() {
		this.layoutInflater.inflate(R.layout.upgrade, this);
		this.check = ((Button) findViewById(R.id.crb_upgrade_now));
		this.url = ((EditText) findViewById(R.id.edit_upgrade_url));
		String str = SystemProperties.get("persist.sys.um.upgrade.url","gd.umstb.com.cn");
		if (str != null)
			this.url.setText(str);
		this.progressBar = ((ProgressBar) findViewById(R.id.progressbarhttp));
		this.progressBar.setEnabled(false);
		this.progressBar.setMax(100);
		this.upgradePercent = ((TextView) findViewById(R.id.upgrade_percent));
		this.upgradePercent.setText("");
	}
}