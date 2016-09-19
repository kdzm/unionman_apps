package com.unionman.settings.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.UMSettings;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;

public class EthNetwork extends RightWindowBase implements View.OnClickListener {
	private static NetworkInfo.State mNetworkState = NetworkInfo.State.UNKNOWN;
	private DhcpInfo mdhcpInfo;
	private int intSelectedId;
	private TextView tv_dhcp_ip;
	private TextView tv_dhcpinfo;
	private TextView tv_pppoe_ip;
	private TextView tv_pppoeinfo;
	private RadioButton rb_dhcp;
	private RadioButton rb_pppoe;
	private RadioButton rb_static;
	private TextView tv_static_ip;
	private TextView tv_static_info;
	LinearLayout lay_pppoe;
	LinearLayout lay_static;
	LinearLayout lay_dhcp;
	private static final String TAG = "com.unionman.settings.content.network--EthNetwork--";


	Handler handler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			String str = ((UMSettings) EthNetwork.this.context.getApplicationContext()).result;
			Logger.i(TAG, "handleMessage msg.what="+ paramAnonymousMessage.what + " result=" + str);
			if (paramAnonymousMessage.what == 0) {
				EthNetwork.this.tv_pppoeinfo.setText(R.string.network_pppoe_msg);
			}
			if (paramAnonymousMessage.what == 1) {
				if (!str.equals("1"))
					Settings.System.putInt(EthNetwork.this.context.getContentResolver(),"ppoe_open", 0);
				if (str.equals("1")) {
					EthNetwork.this.tv_pppoeinfo.setText(R.string.connect_success);
					return;
				}
				if (str.equals("3")) {
					EthNetwork.this.tv_pppoeinfo.setText(R.string.network_state_connecting);
					return;
				}
				if (str.equals("2")) {
					EthNetwork.this.tv_pppoeinfo.setText(R.string.network_state_unconnected);
					return;
				}
				EthNetwork.this.tv_pppoeinfo.setText(R.string.network_state_connecting);
				return;
			}
			if (paramAnonymousMessage.what == 3) {
				EthNetwork.this.tv_pppoeinfo.setText(R.string.internet_break);
				return;
			}
			if (paramAnonymousMessage.what == 4) {
				EthNetwork.this.tv_pppoeinfo.setText(R.string.connect_success);
				return;
			}
			if (paramAnonymousMessage.what == 5) {
				EthNetwork.this.tv_pppoeinfo.setText(context.getText(R.string.network_state_connecting));
				return;
			}
			UMDebug.umdebug_trace();
			EthNetwork.this.tv_pppoeinfo.setText("");
		}
	};
	private ConnectivityManager mConnectivityManager;
	private EthernetManager mEthernetManager;
	private PppoeManager mPppoeManager;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramAnonymousContext,Intent paramAnonymousIntent) {
			String str = paramAnonymousIntent.getAction();
			Logger.i(TAG, "action=" + str);
			if (str == null)
				Logger.i(TAG,"action is null");
			else {
				if (str.equals("android.net.ethernet.ETHERNET_STATE_CHANGE")) {
					switch (paramAnonymousIntent.getIntExtra("ethernet_state",-1)) {
					case 18:
						EthNetwork.this.tv_static_info.setText(paramAnonymousContext.getText(R.string.eth_phy_link_up));
						EthNetwork.this.tv_dhcpinfo.setText(paramAnonymousContext.getText(R.string.eth_phy_link_up));
						EthNetwork.this.tv_pppoeinfo.setText(paramAnonymousContext.getText(R.string.eth_phy_link_up));
						break;
					case 19:
						EthNetwork.this.tv_static_info.setText(paramAnonymousContext.getText(R.string.eth_phy_link_down_check));
						EthNetwork.this.tv_dhcpinfo.setText(paramAnonymousContext.getText(R.string.eth_phy_link_down_check));
						EthNetwork.this.tv_pppoeinfo.setText(paramAnonymousContext.getText(R.string.eth_phy_link_down_check));
						break;
					}
					EthNetwork.this.updateUI();
					Logger.i(TAG,"ETHERNET_STATE_CHANGE");
				} else if (str.equals("android.net.ethernet.STATE_CHANGE")) {
					EthNetwork.this.updateUI();
					Logger.i(TAG,"STATE_CHANGE");
					return;
				} else if (str.equals("ETH_STATE")) {
					int j = paramAnonymousIntent.getIntExtra("dhcp_state", 0);
					Logger.i(TAG,"ethstate=" + j);
					EthNetwork.this.updateUI();
					return;
				} else if (str.equals("PPPOE_STATE_CHANGED")) {
					int i = paramAnonymousIntent.getIntExtra("pppoe_state", -1);
					EthNetwork.this.updateUI();
					Logger.i(TAG,"pppoe state event=" + i);
				}
			}
		}
	};

	private String getSystemScure(String paramString) {

		return Settings.Secure.getString(this.context.getContentResolver(),
				paramString);
	}
	
	public EthNetwork(Context paramContext) {
		super(paramContext);
	}

	private String getIp() {
		Logger.i(TAG,"getIp()--");
		try {
			this.mdhcpInfo = this.mEthernetManager.getDhcpInfo();
			String str = NetworkUtils.intToInetAddress(this.mdhcpInfo.ipAddress).getHostAddress();
			Logger.i(TAG, "getip="+str);
			return str;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	private String getPPPoEIp() {
		Logger.i(TAG,"getPPPoEIp()--");
		try {
			// this.mdhcpInfo = this.mPppoeManager.getDhcpInfo();
			//String str =NetworkUtils.intToInetAddress(this.mdhcpInfo.ipAddress).getHostAddress();
			String str=this.mPppoeManager.getIpaddr("ppp0");
			if(str!=null){
				return str;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	private void initEthNetworkState() {
		Logger.i(TAG,"initEthNetworkState()--");
		if (this.mConnectivityManager != null) {
			NetworkInfo localNetworkInfo = this.mConnectivityManager.getNetworkInfo(9);
			if (localNetworkInfo != null) {
				mNetworkState = localNetworkInfo.getState();
				Logger.i(TAG,"Ethernet network state: " + mNetworkState);
			}
		}
	}

	private void initView() {
		Logger.i(TAG,"initView()--");
		((UMSettings) this.context.getApplicationContext()).mHandler = this.handler;
		this.rb_dhcp.setChecked(false);
		this.rb_static.setChecked(false);
		this.rb_pppoe.setChecked(false);
		this.intSelectedId = Settings.Secure.getInt(this.context.getContentResolver(), "default_eth_mod", 0);
		Logger.i(TAG,"get current mod=" + this.intSelectedId);
		switch (this.intSelectedId) {
		case 0:
			this.rb_dhcp.setChecked(true);
			this.rb_dhcp.requestFocus();
			this.tv_static_ip.setVisibility(8);
			this.tv_pppoe_ip.setVisibility(8);
			this.tv_dhcp_ip.setVisibility(0);
			this.tv_dhcpinfo.setVisibility(0);
			this.tv_static_info.setVisibility(4);
			this.tv_pppoeinfo.setVisibility(4);
			break;
		case 1:
			this.rb_static.setChecked(true);
			this.rb_static.requestFocus();
			this.tv_dhcp_ip.setVisibility(8);
			this.tv_static_ip.setVisibility(0);
			this.tv_dhcpinfo.setVisibility(8);
			this.tv_pppoe_ip.setVisibility(8);
			this.tv_static_info.setVisibility(0);
			this.tv_pppoeinfo.setVisibility(4);
			break;
		case 2:
			this.rb_pppoe.setChecked(true);
			this.rb_pppoe.requestFocus();
			this.tv_dhcp_ip.setVisibility(8);
			this.tv_static_ip.setVisibility(8);
			this.tv_pppoe_ip.setVisibility(0);
			this.tv_dhcpinfo.setVisibility(4);
			this.tv_dhcpinfo.setVisibility(4);
			this.tv_static_info.setVisibility(4);
			this.tv_pppoeinfo.setVisibility(0);
			break;

		}
	}

	private void registReceiver() {
		Logger.i(TAG,"registReceiver()--");
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGE");
		localIntentFilter.addAction("ETH_STATE");
		if (Contants.SYS_SURPORT_PPPOE)
			localIntentFilter.addAction("PPPOE_STATE_CHANGED");
		this.context.registerReceiver(this.mReceiver, localIntentFilter);
	}

	private void unregistReceiver() {
		Logger.i(TAG,"unregistReceiver()--");
		try {
			this.context.unregisterReceiver(this.mReceiver);
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public void initData() {
	}

	public void onClick(View paramView) {
		Logger.i(TAG,"onClick()--");
		if (paramView == this.rb_dhcp) {
			try {
				this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_ETH_DHCP);
			} catch (Exception localException3) {
				localException3.printStackTrace();
				return;
			}
		} else {
			if (paramView == this.rb_static) {
				Logger.i(TAG,"static radio button checked");
				try {
					this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_ETH_STATIC);
					return;
				} catch (Exception localException2) {
					localException2.printStackTrace();
					return;
				}
			} else {
				if (paramView == this.rb_pppoe) {
					Logger.i(TAG,"pppoe radio button checked");
					try {
						this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_ETH_PPPOE);
						return;
					} catch (Exception localException1) {
						localException1.printStackTrace();
					}
				}
			}
		}
	}

	public void onInvisible() {
		unregistReceiver();
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		initEthNetworkState();
		registReceiver();
		updateUI();
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = ConstantList.FRAME_NETWORK_ETH;
		this.levelId = 1002;
		this.mConnectivityManager = ((ConnectivityManager) this.context.getSystemService("connectivity"));
		this.mEthernetManager = ((EthernetManager) this.context.getSystemService("ethernet"));
		if (Contants.SYS_SURPORT_PPPOE)
			this.mPppoeManager = ((PppoeManager) this.context.getSystemService("pppoe"));
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.network_eth, this);
		this.rb_dhcp = ((RadioButton) findViewById(R.id.radio_eth_dhcp));
		this.rb_static = ((RadioButton) findViewById(R.id.radio_eth_static));
		this.rb_pppoe = ((RadioButton) findViewById(R.id.radio_eth_pppoe));
		this.tv_dhcpinfo = ((TextView) findViewById(R.id.eth_dhcp_info));
		this.tv_static_info = ((TextView) findViewById(R.id.eth_static_info));
		this.tv_pppoeinfo = ((TextView) findViewById(R.id.eth_pppoe_info));
		this.lay_pppoe = ((LinearLayout) findViewById(R.id.linearlayout_pppoe));
		this.lay_dhcp = ((LinearLayout) findViewById(R.id.linearlayout_dhcp));
		this.lay_static = ((LinearLayout) findViewById(R.id.linearlayout_static));
		this.tv_dhcp_ip = ((TextView) findViewById(R.id.eth_dhcp_ip));
		this.tv_static_ip = ((TextView) findViewById(R.id.eth_static_ip));
		this.tv_pppoe_ip = ((TextView) findViewById(R.id.eth_pppoe_ip));
		this.rb_dhcp.setOnClickListener(this);
		this.rb_static.setOnClickListener(this);
		this.rb_pppoe.setOnClickListener(this);
		if (!Contants.SYS_SURPORT_ETHERNET) {
			this.lay_dhcp.setVisibility(View.GONE);
			findViewById(R.id.line2).setVisibility(View.GONE);
			this.lay_static.setVisibility(View.GONE);
		}
		if (!Contants.SYS_SURPORT_PPPOE) {
			this.lay_pppoe.setVisibility(View.GONE);
			findViewById(R.id.line1).setVisibility(View.GONE);
		}
		View.OnFocusChangeListener local3 = new View.OnFocusChangeListener() {
			public void onFocusChange(View paramAnonymousView,
					boolean paramAnonymousBoolean) {
				Logger.i(TAG,"focuschange="+ ((View) paramAnonymousView.getParent()).getId());
				if (paramAnonymousBoolean) {
					((View) paramAnonymousView.getParent()).setBackgroundResource(R.drawable.setitem_focus);
					return;
				}
				((View) paramAnonymousView.getParent()).setBackgroundResource(R.color.trans);
			}
		};
		this.rb_dhcp.setOnFocusChangeListener(local3);
		this.rb_pppoe.setOnFocusChangeListener(local3);
		this.rb_static.setOnFocusChangeListener(local3);
	}

	public void updateUI() {
		Logger.i(TAG,"updateUI()--");
		initView();
		initEthNetworkState();
		if (intSelectedId == 0){
				if (mNetworkState == NetworkInfo.State.CONNECTING){
					this.tv_dhcpinfo.setText(this.context.getText(R.string.network_state_connecting_msg));
					this.tv_dhcp_ip.setText("");
					Logger.i(TAG,"manual check networkstate NOW SELECT DHCP=State.CONNECTING");
					return;
				}
				
				if (mNetworkState == NetworkInfo.State.CONNECTED) {
					this.tv_dhcpinfo.setText(this.context.getText(R.string.network_state_connected));
					this.tv_dhcp_ip.setText("IP:" + getIp());
					Logger.i(TAG,"manual check networkstate NOW SELECT DHCP =State.CONNECTED");
					return;
				}
				
				if (mNetworkState == NetworkInfo.State.DISCONNECTED) {
					this.tv_dhcpinfo.setText(this.context.getText(R.string.network_state_unconnected));
					Logger.i(TAG,"manual check networkstate NOW SELECT DHCP =State.UNCONNECTED");
					this.tv_dhcp_ip.setText("");
					return;
				}
				this.tv_dhcpinfo.setText("");
				this.tv_dhcp_ip.setText("");
		}else if(intSelectedId==1){
			if (mNetworkState == NetworkInfo.State.CONNECTED) {
				this.tv_static_info.setText(this.context.getText(R.string.network_state_connected));
				this.tv_static_ip.setText("IP:" + getSystemScure("ethernet_ip"));
				Logger.i(TAG,"manual check networkstate NOW SELECT STATIC =State.CONNECTED");
				return;
			}
			if (mNetworkState == NetworkInfo.State.CONNECTING) {
				this.tv_static_info.setText(this.context.getText(R.string.network_state_connecting_msg));
				this.tv_static_ip.setText("");
				return;
			}
			if (mNetworkState == NetworkInfo.State.DISCONNECTED) {
				this.tv_static_info.setText(this.context.getText(R.string.network_state_unconnected));
				this.tv_static_ip.setText("");
				return;
			}
			this.tv_static_info.setText("");
			this.tv_static_ip.setText("");
		}else if(intSelectedId==2){
			Logger.i(TAG,"PppoeState="+ this.mPppoeManager.getPppoeState() + " ip="+ getPPPoEIp());
			switch (this.mPppoeManager.getPppoeState()) {
			case 1:
				this.tv_pppoeinfo.setText(this.context.getText(R.string.network_state_connected));
				Logger.i(TAG,"pppoe STATIC = State.CONNECTED");
				TextView localTextView = this.tv_pppoe_ip;
				if (getPPPoEIp() != null) {
					localTextView.setText(getPPPoEIp());
				}
				break;
			case 2:
				this.tv_pppoeinfo.setText(this.context.getText(R.string.network_state_unconnected));
				Logger.i(TAG,"manual check networkstate  NOW SELECT STATIC = State.UNCONNECTED");
				this.tv_pppoe_ip.setText("");
				return;
			case 3:
				this.tv_pppoeinfo.setText(this.context.getText(R.string.network_state_connecting));
				this.tv_pppoe_ip.setText("");
				return;
			default:
				this.tv_pppoeinfo.setText("");
				this.tv_pppoe_ip.setText("");
				break;
			}
		}
	}
}
