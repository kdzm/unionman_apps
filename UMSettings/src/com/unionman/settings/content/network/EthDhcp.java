package com.unionman.settings.content;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.ToastUtil;
import java.lang.reflect.Method;
import android.os.SystemProperties;

public class EthDhcp extends RightWindowBase {
	private int BACK = 1;
	private int CONNECT = 0;
//	private Button btn_cancel;
	private Button btn_save;
	private EditText et_pass;
	private TextView tv_passLable;
	private String strPass;
	private String strUser;
	private CheckBox cb_useOption;
	private EditText et_user;
	private TextView tv_userLable;
	private PppoeManager mPppoeManager;
	private static final String TAG = "com.unionman.settings.content.network--EthDhcp--";

	CompoundButton.OnCheckedChangeListener mChecklistener = new CompoundButton.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton,boolean paramAnonymousBoolean) {
			EthDhcp.this.et_user.setEnabled(paramAnonymousBoolean);
			EthDhcp.this.et_pass.setEnabled(paramAnonymousBoolean);
			if (paramAnonymousBoolean) {
				Settings.Secure.putString(EthDhcp.this.context.getContentResolver(),"dhcp_option", "1");
				Settings.Secure.putString(EthDhcp.this.context.getContentResolver(),"dhcp_ipver", "4");
				EthDhcp.this.cb_useOption.setNextFocusUpId(R.id.et_eth_dhcp_pswd);
				EthDhcp.this.et_user.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.white));
				EthDhcp.this.tv_userLable.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.white));
				EthDhcp.this.et_pass.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.white));
				EthDhcp.this.tv_passLable.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.white));
				return;
			}
			Settings.Secure.putString(EthDhcp.this.context.getContentResolver(), "dhcp_option","0");
			EthDhcp.this.cb_useOption.setNextFocusUpId(R.id.eth_dhcp_option60);
			EthDhcp.this.et_user.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.gray));
			EthDhcp.this.tv_userLable.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.gray));
			EthDhcp.this.et_pass.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.gray));
			EthDhcp.this.tv_passLable.setTextColor(EthDhcp.this.context.getResources().getColor(R.color.gray));
		}
	};
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
	private EthernetManager mEthernetManager;
	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			Logger.i(TAG, "handleMessage what=" + paramAnonymousMessage.what);
			if (paramAnonymousMessage.what == EthDhcp.this.BACK) {
				EthDhcp.this.layoutManager.backShowView();
			} else {
				super.handleMessage(paramAnonymousMessage);

				if (paramAnonymousMessage.what == EthDhcp.this.CONNECT) {
					EthDhcp.this.startDhcp();
				}
				return;
			}
		}
	};


	public EthDhcp(Context paramContext) {
		super(paramContext);
		this.mEthernetManager = ((EthernetManager) paramContext.getSystemService("ethernet"));
		if (Contants.SYS_SURPORT_PPPOE) {
			this.mPppoeManager = ((PppoeManager) paramContext.getSystemService("pppoe"));
		}
	}

	private void setEditPreview() {
		Logger.i(TAG, "setEditPreview()--");
		this.et_user.setText(this.strUser);
		this.et_pass.setText(this.strPass);
	}

	private void startDhcp() {
		Logger.i(TAG, "startDhcp--");
		String username = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_username");
		String password = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_pswd");
		if (this.cb_useOption.isChecked()) {
			Settings.Secure.putString(this.context.getContentResolver(),"dhcp_user", this.et_user.getText().toString());
			Settings.Secure.putString(this.context.getContentResolver(),"dhcp_pswd", this.et_pass.getText().toString());
		}
		int i = Settings.Secure.getInt(this.context.getContentResolver(),"default_eth_mod", 0);
		Logger.i(TAG, "username=" + username + " password=" + password);
		if ((Contants.SYS_SURPORT_PPPOE) && (i == 2) && (username != null)&& (password != null)) {
			Logger.i(TAG, "DISCONNECT PPPOE");
			/*hehe*/
			 this.mPppoeManager.disconnect(this.mEthernetManager.getInterfaceName());
			//this.mPppoeManager.enablePppoe(false);
		}
		Settings.Secure.putInt(this.context.getContentResolver(),"default_eth_mod", 0);
		this.mEthernetManager.setInterfaceName("eth0");
	    this.mEthernetManager.setEthernetMode2("dhcp", new DhcpInfo());
		this.mEthernetManager.setEthernetEnabled(false);
		this.mEthernetManager.setEthernetEnabled(true);
	}

	void getEthermethod() {
		Logger.i(TAG,"getEthermethod()--");
		Method[] arrayOfMethod = this.mEthernetManager.getClass().getDeclaredMethods();
		int i = arrayOfMethod.length;
		for (int j = 0;; j++) {
			if (j >= i)
				return;
			Method localMethod = arrayOfMethod[j];
		}
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		this.cb_useOption.setOnCheckedChangeListener(this.mChecklistener);
		this.et_user.setOnKeyListener(this.mEditKeyListener);
		this.et_pass.setOnKeyListener(this.mEditKeyListener);
		cb_useOption.setVisibility(View.GONE);
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		this.strUser = Settings.Secure.getString(this.context.getContentResolver(), "dhcp_user");
		this.strPass = Settings.Secure.getString(this.context.getContentResolver(), "dhcp_pswd");

		String strSerialno = SystemProperties.get("ro.serialno", "");
		String strTmpMac=SystemProperties.get("ro.mac", "");
		String strMacs[]=strTmpMac.split(":");
		StringBuffer sbMac=new StringBuffer();
		for(int i=0;i<strMacs.length;i++){
			sbMac.append(strMacs[i]);
		}
		String strMac=sbMac.toString();
		Logger.i(TAG,"strMac is  =============="+strMac);
		Logger.i(TAG,"this.strUser is  =============="+this.strUser);
		
		if(this.strUser==null||!this.strUser.equals(strMac) ){
			Logger.i(TAG,"dhcp_user is error  ========================");
			this.strUser = strMac;
			this.strPass = "GdMCC68@OTV";
			Settings.Secure.putString(this.context.getContentResolver(),"dhcp_user", this.et_user.getText().toString());
			Settings.Secure.putString(this.context.getContentResolver(),"dhcp_pswd", this.et_pass.getText().toString());
		}

		setEditPreview();
		String str = Settings.Secure.getString(this.context.getContentResolver(), "dhcp_option");
		if ((str == null) || (str.equals("0"))) {
			this.cb_useOption.setChecked(false);
			this.et_user.setEnabled(false);
			this.et_pass.setEnabled(false);
			Settings.Secure.putString(this.context.getContentResolver(),"dhcp_option", "0");
			this.cb_useOption.setNextFocusUpId(R.id.eth_dhcp_option60);
			this.et_user.setTextColor(this.context.getResources().getColor(R.color.gray));
			this.tv_userLable.setTextColor(this.context.getResources().getColor(R.color.gray));
			this.et_pass.setTextColor(this.context.getResources().getColor(R.color.gray));
			this.tv_passLable.setTextColor(this.context.getResources().getColor(R.color.gray));
		}
		while (true) {
			UMDebug.umdebug_trace();
			this.cb_useOption.requestFocus();

			if (str.equals("1")) {
				this.cb_useOption.setChecked(true);
				this.et_user.setEnabled(true);
				this.et_pass.setEnabled(true);
				Settings.Secure.putString(this.context.getContentResolver(),"dhcp_option", "1");
				this.cb_useOption.setNextFocusUpId(R.id.et_eth_dhcp_pswd);
				this.et_user.setTextColor(this.context.getResources().getColor(R.color.white));
				this.tv_userLable.setTextColor(this.context.getResources().getColor(R.color.white));
				this.et_pass.setTextColor(this.context.getResources().getColor(R.color.white));
				this.tv_passLable.setTextColor(this.context.getResources().getColor(R.color.white));
				if (this.strUser == null)
					this.strUser = "";
				if (this.strPass == null)
					this.strPass = "";
				this.et_user.setText(this.strUser);
				this.et_pass.setText(this.strPass);
			}
			return;
		}
	}

	public void setId() {
		this.frameId = ConstantList.FRAME_NETWORK_ETH_DHCP;
		this.levelId = 1003;
		Logger.i(TAG, "setId--");
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.network_eth_dhcp, this);
		this.et_user = ((EditText) findViewById(R.id.et_eth_dhcp_user));
		this.et_pass = ((EditText) findViewById(R.id.et_eth_dhcp_pswd));
		this.cb_useOption = ((CheckBox) findViewById(R.id.eth_dhcp_option60));
		this.tv_userLable = ((TextView) findViewById(R.id.et_eth_dhcp_user_lable));
		this.tv_passLable = ((TextView) findViewById(R.id.et_eth_dhcp_pass_lable));
		this.btn_save = ((Button) findViewById(R.id.btn_eth_dhcp_ok));
		this.btn_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
		//		ToastUtil.showLongToast(EthDhcp.this.mContext, "保存成功，网络配置中请稍候");
				ToastUtil.showLongToast(EthDhcp.this.mContext, "网络配置中请稍候...");
				EthDhcp.this.mHandler.sendEmptyMessageDelayed(EthDhcp.this.CONNECT, 1000L);
				EthDhcp.this.mHandler.sendEmptyMessageDelayed(EthDhcp.this.BACK, 2000L);
			}
		});
	//	this.btn_cancel = ((Button) findViewById(R.id.btn_eth_dhcp_cancel));
//		this.btn_cancel.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View paramAnonymousView) {
//				EthDhcp.this.layoutManager.backShowView();
//			}
//		});
		this.btn_save.requestFocus();
	}
}
