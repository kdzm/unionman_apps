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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.widget.TextView;
import android.util.Log;
import android.net.LinkProperties;
import android.net.LinkAddress;
import java.net.InetAddress;
import java.util.Iterator;

import android.net.RouteInfo;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.content.EthernetsBean;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;

public class NetworkInfoActivity extends RightWindowBase {
	private TextView tv_eth;
	private TextView tv_eth_dns;
	private TextView tv_eth_gateway;
	private TextView tv_eth_ip;
	private TextView tv_eth_mask;
	private TextView tv_wifi;
	private TextView tv_wifi_dns;
	private TextView tv_wifi_gateway;
	private TextView tv_wifi_ip;
	private TextView tv_wifi_mask;
	private PppoeManager mPppoeManager;
	private BroadcastReceiver mReceiver;
	private WifiManager wifiManager;
	private ConnectivityManager mConnectivityManager;
	private EthernetManager mEthernetManager;
	private IntentFilter mFilter;
	private String DhcpIP = "";
	private String DhcpGatewary = "";
	private String DhcpMask = "";
	private String DhcpDns = "";
	private static final String TAG="com.unionman.settings.content.network--NetworkInfoActivity--";

	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			super.handleMessage(paramAnonymousMessage);
			if (paramAnonymousMessage.what == 0)
				NetworkInfoActivity.this.updateUi();
		}
	};


	public NetworkInfoActivity(Context paramContext) {
		super(paramContext);
		if (Contants.SYS_SURPORT_WIFI)
			this.wifiManager = ((WifiManager) paramContext.getSystemService("wifi"));
		if (Contants.SYS_SURPORT_ETHERNET)
			mEthernetManager = ((EthernetManager) paramContext.getSystemService("ethernet"));
		if (Contants.SYS_SURPORT_PPPOE) {
			this.mPppoeManager = ((PppoeManager) paramContext.getSystemService("pppoe"));
		}
		this.mConnectivityManager = ((ConnectivityManager) paramContext.getSystemService("connectivity"));
	}

	
	 private void refreshDhcpIp() {
	        Log.d(TAG, "refreshDhcpIp()");
	        LinkProperties linkProperties = mConnectivityManager
	                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
	        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses().iterator();
	        if (!addrs.hasNext()) {
	            Log.d(TAG, "showDhcpIP:can not get LinkAddress!!");
	            return;
	        }
	        LinkAddress linkAddress = addrs.next();
	        int prefixLength = linkAddress.getNetworkPrefixLength();
	        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
	        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
	        String mNM = Netmask.getHostAddress();
	        Log.d(TAG, "netmask:  " + mNM);
	        if (null != DhcpMask) {
	            DhcpMask = mNM;
	            Log.d(TAG, "DhcpMask:  " + DhcpMask);
	        }

	        try {
	            String mIP = linkAddress.getAddress().getHostAddress();
	            Log.d(TAG, "mIP" + mIP);
	            if (null != DhcpIP) {
	                DhcpIP = mIP;
	            }

	            String mGW = "";
	            for (RouteInfo route : linkProperties.getRoutes()) {
	                if (route.isDefaultRoute()) {
	                    mGW = route.getGateway().getHostAddress();
	                    Log.d(TAG, "Gateway:  " + mGW);
	                    break;
	                }
	            }
	            DhcpGatewary = mGW;
	            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
	            if (!dnses.hasNext()) {
	                Log.d(TAG, "showDhcpIP:empty dns!!");
	            } else {
	                String mDns1 = dnses.next().getHostAddress();
	                Log.d(TAG, "DNS1: " + mDns1);
	                if (null != DhcpDns) {
	                    DhcpDns = mDns1;
	                }
	            }
	        } catch (NullPointerException e) {
	            Log.d(TAG, "can not get IP" + e);
	        }
	        //localDhcpInfo1.ipAddress = DhcpIP;
	        
	    }
	
	private EthernetsBean getStaticNetworkInfo(String paramString) {
		Logger.i(TAG,"getStaticNetworkInfo()--");
		Log.i(TAG,"getStaticNetworkInfo()-paramString=-"+paramString);
		EthernetsBean localEthernetsBean = new EthernetsBean();
		DhcpInfo localDhcpInfo1 = null;
		if (paramString.equals("PPPOE")) {
			try {
				DhcpInfo localDhcpInfo2 = this.mPppoeManager.getDhcpInfo();
				if (null != localDhcpInfo2) {
					localEthernetsBean.setIp("0.0.0.0");
					localEthernetsBean.setGateway("0.0.0.0");
					localEthernetsBean.setMask("0.0.0.0");
					localEthernetsBean.setDns("0.0.0.0");
					localEthernetsBean.setDns2("0.0.0.0");
					return localEthernetsBean;
				} else {
					
					 localEthernetsBean.setIp(this.mPppoeManager.getIpaddr("ppp0"));
					 localEthernetsBean.setMask(this.mPppoeManager.getNetmask("ppp0"));
					 localEthernetsBean.setGateway(this.mPppoeManager.getGateway("ppp0"));
					 localEthernetsBean.setDns("8.8.8.8");
					 
					return localEthernetsBean;
				}
			} catch (Exception localException) {
				localException.printStackTrace();
				return localEthernetsBean;
			}
		} else if (paramString.equals(this.getResources().getString(R.string.ethnetwork_info_static))) {
			try {
				int k = Settings.Secure.getInt(this.context.getContentResolver(),"ethernet_prefixlength");
				if (getSystemScure("ethernet_ip") != null) {
					localEthernetsBean.setIp(getSystemScure("ethernet_ip"));
					localEthernetsBean.setMask(maskToString(k));
					localEthernetsBean.setGateway(getSystemScure("ethernet_iproute"));
					localEthernetsBean.setDns(getSystemScure("ethernet_dns1"));
					localEthernetsBean.setDns2(getSystemScure("ethernet_dns2"));
					return localEthernetsBean;
				}
			} catch (Settings.SettingNotFoundException localSettingNotFoundException) {
				localSettingNotFoundException.printStackTrace();
				return localEthernetsBean;
			}
		} else {
			try {
				localDhcpInfo1 = this.mEthernetManager.getDhcpInfo();
				refreshDhcpIp();
				Log.i(TAG,"getStaticNetworkInfo()-DhcpIP=-"+DhcpIP);
				Log.i(TAG,"getStaticNetworkInfo()-localDhcpInfo1=-"+localDhcpInfo1);
				if (null == DhcpIP) {
					Log.i(TAG,"DhcpIP getStaticNetworkInfo()-localDhcpInfo1=-"+DhcpIP);
					localEthernetsBean.setIp("0.0.0.0");
					localEthernetsBean.setGateway("0.0.0.0");
					localEthernetsBean.setMask("0.0.0.0");
					localEthernetsBean.setDns("0.0.0.0");
					localEthernetsBean.setDns2("0.0.0.0");
				} else {
					//localEthernetsBean.setIp(NetworkUtils.intToInetAddress(localDhcpInfo1.ipAddress).getHostAddress());
					localEthernetsBean.setIp(DhcpIP);
					//String str1 = NetworkUtils.intToInetAddress(localDhcpInfo1.gateway).getHostAddress();
					localEthernetsBean.setGateway(DhcpGatewary);
					//Logger.i(TAG,localDhcpInfo1.netmask+"");
					//int i = NetworkUtils.netmaskIntToPrefixLength(localDhcpInfo1.netmask);
					//InetAddress localInetAddress = NetworkUtils.intToInetAddress(localDhcpInfo1.netmask);
					//String mMask = localInetAddress.getHostAddress();
					//localEthernetsBean.setMask(maskToString(i));
					localEthernetsBean.setMask(DhcpMask);
					//localEthernetsBean.setDns(NetworkUtils.intToInetAddress(localDhcpInfo1.dns1).getHostAddress());
					//localEthernetsBean.setDns2(NetworkUtils.intToInetAddress(localDhcpInfo1.dns2).getHostAddress());
					localEthernetsBean.setDns(DhcpDns);
					//localEthernetsBean.setDns2(NetworkUtils.intToInetAddress(localDhcpInfo1.dns2).getHostAddress());
					
					localEthernetsBean.setServerAddress("");
				}
			} catch (Exception localException) {
				localException.printStackTrace();
				return localEthernetsBean;
			}
		}
		return localEthernetsBean;
	}

	private String getSystemScure(String paramString) {
		return Settings.Secure.getString(this.context.getContentResolver(),paramString);
	}

	private void hideViewByIds(int[] paramArrayOfInt) {
		for (int j = 0; j < paramArrayOfInt.length; j++) {
			findViewById(paramArrayOfInt[j]).setVisibility(8);
		}
	}

	private void registReceiver() {
		Logger.i(TAG,"registReceiver()--");
		this.mReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramAnonymousContext,
					Intent paramAnonymousIntent) {
				String str = paramAnonymousIntent.getAction();
				Log.i(TAG,"registReceiver()--str="+str);
				if ((Contants.SYS_SURPORT_WIFI)&& (str.equals("android.net.wifi.WIFI_STATE_CHANGED"))) {
					NetworkInfoActivity.this.mHandler.sendEmptyMessage(0);
				}
				if ((Contants.SYS_SURPORT_ETHERNET)&& (str.equals("android.net.ethernet.ETHERNET_STATE_CHANGE"))) {
					NetworkInfoActivity.this.mHandler.sendEmptyMessage(0);
					// return;
				}
				if ((Contants.SYS_SURPORT_PPPOE)
						&& (str.equals("PPPOE_STATE_CHANGED"))) {
					NetworkInfoActivity.this.mHandler.sendEmptyMessage(0);
					// return;
				}
				// NetworkInfoActivity.this.mHandler.sendEmptyMessage(0);
			}
		};
		this.mFilter = new IntentFilter();
		if (Contants.SYS_SURPORT_WIFI){
			this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		}
		if (Contants.SYS_SURPORT_ETHERNET){
			this.mFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGE");
		}
		if (Contants.SYS_SURPORT_PPPOE){
			this.mFilter.addAction("PPPOE_STATE_CHANGED");
		}
		this.context.registerReceiver(this.mReceiver, this.mFilter);
	}

	private void setValue(String paramString) {
		Logger.i(TAG,"setValue()--paramString="+paramString);
		EthernetsBean localEthernetsBean = getStaticNetworkInfo(paramString);
		String str1 = localEthernetsBean.getIp();
		String str2 = localEthernetsBean.getMask();
		String str3 = localEthernetsBean.getGateway();
		String str4 = localEthernetsBean.getDns();

		localEthernetsBean.getDns2();
		TextView localTextView1 = this.tv_eth_ip;
		TextView localTextView2 = this.tv_eth_mask;
		TextView localTextView3 = this.tv_eth_gateway;
		TextView localTextView4 = this.tv_eth_dns;
		if ((str1 == null) || (str1.equals(""))) {
			localTextView1.setText("0. 0. 0. 0");
		} else {
			localTextView1.setText(str1);
		}

		if ((str2 == null) || (str2.equals(""))) {
			localTextView2.setText("0. 0. 0. 0");
		} else {
			localTextView2.setText(str2);
		}

		if ((str3 == null) || (str3.equals(""))) {
			localTextView3.setText("0. 0. 0. 0");
		} else {
			localTextView3.setText(str3);
		}

		if ((str4 == null) || (str4.equals(""))) {
			localTextView4.setText("0. 0. 0. 0");
		} else {
			localTextView4.setText(str4);
		}
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

	public String maskToString(int paramInt) {
		Logger.i(TAG,"maskToString()--paramInt="+paramInt);
		String str;
		if (paramInt == 0 || String.valueOf(paramInt).equals("0")) {
			return "0.0.0.0";
		}
		if (paramInt >= 32) {
			str = "255.255.255.255";
			return str;
		}
		int i = paramInt / 8;
		int j = paramInt % 8;
		StringBuffer localStringBuffer = new StringBuffer();
		int k = 0;

		for (int m = 0; m < i; m++) {
			localStringBuffer.append("255.");
			k++;
		}
		for (int m = k; m < 4; m++) {
			if (j > 0) {
				localStringBuffer.append((0xFF & 255 << 8 - j) + ".");
				i++;
			}
		}
		localStringBuffer.append("0.");
		str = localStringBuffer.toString();
		return str.substring(0, -1 + str.length());
	}

	public void onInvisible() {
		unregistReceiver();
	}

	public void onResume() {
		registReceiver();
		updateUi();
	}

	public void setId() {
		this.frameId = ConstantList.FRAME_NETWORK_NETWORKINFO;
		this.levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.network_info, this);
		this.tv_wifi = ((TextView) findViewById(R.id.wifi_info));
		this.tv_eth = ((TextView) findViewById(R.id.eth_info));
		this.tv_wifi_ip = ((TextView) findViewById(R.id.txt_wifi_ip));
		this.tv_wifi_mask = ((TextView) findViewById(R.id.txt_wifi_mask));
		this.tv_wifi_gateway = ((TextView) findViewById(R.id.txt_wifi_gateway));
		this.tv_wifi_dns = ((TextView) findViewById(R.id.txt_wifi_dns));
		this.tv_eth_ip = ((TextView) findViewById(R.id.txt_eth_ip));
		this.tv_eth_mask = ((TextView) findViewById(R.id.txt_eth_mask));
		this.tv_eth_gateway = ((TextView) findViewById(R.id.txt_eth_gateway));
		this.tv_eth_dns = ((TextView) findViewById(R.id.txt_eth_dns));
		if (!Contants.SYS_SURPORT_WIFI) {
			this.tv_wifi.setVisibility(8);
			hideViewByIds(new int[] { R.id.line1, R.id.line2, R.id.line3,R.id.line4, R.id.line5 });
			hideViewByIds(new int[] { R.id.linelayout1, R.id.linelayout2,R.id.linelayout3, R.id.linelayout4, R.id.linelayout5 });
		}
		if (!Contants.SYS_SURPORT_ETHERNET) {
			this.tv_eth.setVisibility(8);
			hideViewByIds(new int[] { R.id.line6, R.id.line7, R.id.line8,R.id.line9, R.id.line10 });
			hideViewByIds(new int[] { R.id.linelayout6, R.id.linelayout7,R.id.linelayout8, R.id.linelayout9, R.id.linelayout10 });
		}
	}

	void updateUi() {
		Logger.i(TAG,"updateUi()--");
		WifiInfo localWifiInfo;
		DhcpInfo localDhcpInfo;
		String str2 = "";
		TextView localTextView = this.tv_wifi;

		if (Contants.SYS_SURPORT_WIFI) {
			try {
				localWifiInfo = this.wifiManager.getConnectionInfo();
				localDhcpInfo = this.wifiManager.getDhcpInfo();
				str2 = localWifiInfo.getSSID();
				Logger.i(TAG,"======updateUi======ssid" + localWifiInfo.getSSID()
						+ "ip=" + localDhcpInfo.ipAddress + "mask="
						+ localDhcpInfo.netmask + "gateway="
						+ localDhcpInfo.gateway + "=dns=" + localDhcpInfo.dns1);
				if ((localWifiInfo == null)
						|| (str2 == null)
						|| (str2.equals(""))
						|| (str2.equals("<unknown ssid>"))
						|| (str2.equals("0x"))
						|| (this.mConnectivityManager.getNetworkInfo(1)
								.getState() != NetworkInfo.State.CONNECTED)) {
					this.tv_wifi_ip.setText("0. 0. 0. 0");
					this.tv_wifi_mask.setText("0. 0. 0. 0");
					this.tv_wifi_gateway.setText("0. 0. 0. 0");
					this.tv_wifi_dns.setText("0. 0. 0. 0");
				} else {
					String str3 = this.getResources().getString(R.string.wifi_have_connected_to) + str2;
					localTextView.setText(str3);
					this.tv_wifi_ip.setText(Formatter
							.formatIpAddress(localWifiInfo.getIpAddress()));
					this.tv_wifi_mask.setText(Formatter
							.formatIpAddress(localDhcpInfo.netmask));
					this.tv_wifi_gateway.setText(Formatter
							.formatIpAddress(localDhcpInfo.gateway));
					this.tv_wifi_dns.setText(Formatter
							.formatIpAddress(localDhcpInfo.dns1));
				}
			} catch (Exception localException) {
				this.tv_wifi_ip.setText("0. 0. 0. 0");
				this.tv_wifi_mask.setText("0. 0. 0. 0");
				this.tv_wifi_gateway.setText("0. 0. 0. 0");
				this.tv_wifi_dns.setText("0. 0. 0. 0");
			}
		}
		if (Contants.SYS_SURPORT_ETHERNET) {
			String str1 = Settings.Secure.getString(
					this.context.getContentResolver(), "default_eth_mod");
			if (str1 == null || str1.equals("")) {
				str1 =this.getResources().getString(R.string.ethnetwork_info);
			} else {
				if (str1.equals("0")) {
					str1 = this.getResources().getString(R.string.ethnetwork_info_Dhcp);
				} else if (str1.equals("2")) {
					str1 = this.getResources().getString(R.string.ethnetwork_info_Pppoe);
				} else if (str1.equals("1")) {
					str1 = this.getResources().getString(R.string.ethnetwork_info_static);
				}
			}
			this.tv_eth.setText(str1);
			setValue(str1);
			return;
		}
	}
}
