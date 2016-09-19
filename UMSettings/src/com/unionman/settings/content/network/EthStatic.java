package com.unionman.settings.content;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.unionman.settings.R;
import com.unionman.settings.custom.IpAddrEditText;
import com.unionman.settings.content.EthernetsBean;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.IPUtil;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;

import java.net.InetAddress;
import java.net.Inet4Address;

public class EthStatic extends RightWindowBase {

	private int intBACK = 1;
	private int intCONNECT = 0;
	private boolean isRightIP = false;
	private Button btn_cancel;
	private Button btn_save;
	private IpAddrEditText et_dns;
	private IpAddrEditText et_dns2;
	private IpAddrEditText et_gateway;
	private IpAddrEditText et_ip;
	private IpAddrEditText et_mask;
	private PppoeManager mPppoeManager;
	private EthernetManager mEthernetManager;
	public static final String ETHERNET_CONNECT_MODE_MANUAL = "manual";
	private static final String TAG="com.unionman.settings.content.network--EthStatic--";

	Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			Logger.i(TAG, "handleMessage what=" + paramAnonymousMessage.what);
			if (paramAnonymousMessage.what == EthStatic.this.intBACK){
				EthStatic.this.layoutManager.backShowView();
			}
		//	while (true) {
		//		super.handleMessage(paramAnonymousMessage);

				if (paramAnonymousMessage.what == EthStatic.this.intCONNECT) {
					EthStatic.this.startStatic();
		//			if (EthStatic.this.isRightIP){
		//				EthStatic.this.mHandler.sendEmptyMessageDelayed(EthStatic.this.intBACK, 1000L);
		//			}
		//		}
				
			}
				return;
		}
	};


	public EthStatic(Context paramContext) {
		super(paramContext);
	}

	private boolean checkIP(String paramString1, String paramString2,
			String paramString3, String paramString4, String paramString5) {
		Logger.i(TAG,"checkIP()--");
		if (!IPUtil.check(paramString1, paramString3, paramString2)) {
			Logger.i(TAG, "IP和网关不在一个网段");
			return false;
		}
		if (!IPUtil.checkHost255(paramString1, paramString3)) {
			Logger.i(TAG, "请检查IP和子网掩码");
			return false;
		}
		return true;
	}

	private EthernetsBean getStaticNetworkInfo() {
		Logger.i(TAG,"getStaticNetworkInfo()--");
		EthernetsBean localEthernetsBean = new EthernetsBean();
		String str1 = getSystemScure("ethernet_ip");
		String str2 = Settings.Secure.getString(
				this.context.getContentResolver(), "ethernet_iproute");
		try {
			if (str1 != null) {
				localEthernetsBean.setIp(str1);
				localEthernetsBean.setGateway(str2);
				int j = Settings.Secure.getInt(this.context.getContentResolver(),"ethernet_prefixlength");
				localEthernetsBean.setMask(maskToString(j));
				localEthernetsBean.setDns(getSystemScure("ethernet_dns1"));
				localEthernetsBean.setDns2(getSystemScure("ethernet_dns2"));
			}
			return localEthernetsBean;
		} catch (Settings.SettingNotFoundException localSettingNotFoundException) {
			localSettingNotFoundException.printStackTrace();
		}
		return null;
	}

	private String getSystemScure(String paramString) {

		return Settings.Secure.getString(this.context.getContentResolver(),
				paramString);
	}

	private void putSystemSecure(String paramString1, String paramString2) {
		Settings.Secure.putString(this.context.getContentResolver(),
				paramString1, paramString2);
	}

	private void setEditPreview() {
		Logger.i(TAG,"setEditPreview()--");
		EthernetsBean localEthernetsBean = getStaticNetworkInfo();
		Logger.i(TAG, "setEditPreview ip="+localEthernetsBean.getIp());
		this.et_ip.setText(localEthernetsBean.getIp());
		this.et_gateway.setText(localEthernetsBean.getGateway());
		this.et_mask.setText(localEthernetsBean.getMask());
		this.et_dns.setText(localEthernetsBean.getDns());
		this.et_dns2.setText(localEthernetsBean.getDns2());
	}

	private void setIP(EthernetsBean paramEthernetsBean) {
		Logger.i(TAG,"setIP()--");
		DhcpInfo dhcpInfo = new DhcpInfo();
		String str1 = paramEthernetsBean.getIp();
		String str2 = paramEthernetsBean.getDns();
		String str3 = paramEthernetsBean.getDns2();
		InetAddress localInetAddress1 = NetworkUtils
				.numericToInetAddress(paramEthernetsBean.getGateway());
		InetAddress localInetAddress2 = NetworkUtils
				.numericToInetAddress(paramEthernetsBean.getMask());
		InetAddress ipaddr = NetworkUtils.numericToInetAddress(str1);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(str2);
        InetAddress idns2 = NetworkUtils.numericToInetAddress(str3);
		Logger.d(TAG,"setIP() ipAddress=" + str1);
		Logger.d(TAG,"setIP() iRoute=" + localInetAddress1);
		Logger.d(TAG,"setIP() iNetmask=" + localInetAddress2);
		Logger.d(TAG,"setIP() DNS1=" + str2);
		Logger.d(TAG,"setIP() DNS2=" + str3);
		try {
			dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address)ipaddr);
            dhcpInfo.gateway = NetworkUtils.inetAddressToInt((Inet4Address)localInetAddress1);
            dhcpInfo.netmask = NetworkUtils.inetAddressToInt((Inet4Address)localInetAddress2);
           dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address)idns1);
           dhcpInfo.dns2 = NetworkUtils.inetAddressToInt((Inet4Address)idns2);
			if(mEthernetManager == null){
				Logger.i(TAG,"mEthernetManager == null");
				this.mEthernetManager = ((EthernetManager) this.context.getSystemService("ethernet"));
			}
			 this.mEthernetManager.setEthernetMode2(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL,
			 dhcpInfo);
			return;
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		}
	}

	private void startStatic() {
		Logger.i(TAG,"startStatic()--");
		this.isRightIP = false;
		String str1 = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_username");
		String str2 = Settings.Secure.getString(this.context.getContentResolver(), "pppoe_pswd");
		int i = Settings.Secure.getInt(this.context.getContentResolver(),"default_eth_mod", 0);
		if ((Contants.SYS_SURPORT_PPPOE) && (i == 2) && (str1 != null)&& (str2 != null)){
			new Thread(new Runnable() {
				public void run() {
					Logger.i(TAG, "DISCONNECT PPPOE");
					//EthStatic.this.mPppoeManager.enablePppoe(false);
					EthStatic.this.mPppoeManager.disconnect(EthStatic.this.mEthernetManager.getInterfaceName());
				}
			}).start();
		}
		String str3 = this.et_ip.getText();
		String str4 = this.et_gateway.getText();
		String str5 = this.et_mask.getText();
		String str6 = this.et_dns.getText();
		String str7 = this.et_dns2.getText();
		if (!IPUtil.isIP(str3)){
			ToastUtil.showToast(this.context, getResources().getString(R.string.alert_network_cfg_ip));
			return;
		}else if (!IPUtil.isIP(str4)) {
			ToastUtil.showToast(this.context, getResources().getString(R.string.alert_network_cfg_gateway));
			return;
		}else if (!IPUtil.checkMask(str5)) {
			ToastUtil.showToast(this.context, getResources().getString(R.string.alert_network_cfg_mask));
			return;
		}else if (!IPUtil.isIP(str6)) {
			ToastUtil.showToast(this.context, getResources().getString(R.string.alert_network_cfg_dns));
			return;
		}else if(!IPUtil.checkSegment(str3, str5, str4)){
			Log.i(TAG, "handleMessage what=" +"888888888888888888888888");
			ToastUtil.showToast(this.context, getResources().getString(R.string.alert_network_cfg_segment));
		    return;
		}
		
		EthernetsBean localEthernetsBean = new EthernetsBean();
		Settings.Secure.putInt(this.context.getContentResolver(),"default_eth_mod", 1);
		this.mEthernetManager.setInterfaceName("eth0");
		this.mEthernetManager.setEthernetEnabled(false);
		
		localEthernetsBean.setIp(str3);
		localEthernetsBean.setGateway(str4);
		localEthernetsBean.setMask(str5);
		localEthernetsBean.setDns(str6);
		localEthernetsBean.setDns2(str7);
		putSystemSecure("ethernet_ip", str3);
		int j = NetworkUtils.netmaskIntToPrefixLength(NetworkUtils.inetAddressToInt(NetworkUtils.numericToInetAddress(str5)));
		Settings.Secure.putInt(this.context.getContentResolver(),"ethernet_prefixlength", j);
		putSystemSecure("ethernet_dns1", str6);
		putSystemSecure("ethernet_dns2", str7);
		putSystemSecure("ethernet_iproute", str4);
		setIP(localEthernetsBean);
		this.mEthernetManager.setEthernetEnabled(true);
		this.mEthernetManager.setWifiDisguise(true);
		this.isRightIP = true;
		ToastUtil.showToast(this.context, "保存成功");
	}

	public void initData() {
	}

	public String maskToString(int paramInt) {
		Logger.i(TAG,"maskToString()--");
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

	// public String maskToString(int paramInt)
	// {
	// Log.d("KJKJKJ","makeToString======enter");
	// String str;
	// if (paramInt >= 32)
	// {
	// str = "255.255.255.255";
	// return str;
	// }
	// int i = paramInt / 8;
	// int j = paramInt % 8;
	// StringBuffer localStringBuffer = new StringBuffer();
	// int k = 0;
	// for (int m = 0; ; m++)
	// {
	// if (m >= 4 - i)
	// {
	// str = localStringBuffer.toString();
	// if (!str.endsWith(".")){
	// Log.d("KJKJKJ","makeToString======returnok");
	// return str.substring(0, -1 + str.length());
	// }
	// localStringBuffer.append("255.");
	// k++;
	// if (k >= i)
	// if (j > 0)
	// {
	// localStringBuffer.append((0xFF & 255 << 8 - j) + ".");
	// i++;
	// };
	// }
	// localStringBuffer.append("0.");
	// }
	//
	// }

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		setEditPreview();
		this.et_ip.requestFocus();
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = ConstantList.FRAME_NETWORK_ETH_STATIC;
		this.levelId = 1003;
		if (Contants.SYS_SURPORT_ETHERNET)
			this.mEthernetManager = ((EthernetManager) this.context.getSystemService("ethernet"));
		if (Contants.SYS_SURPORT_PPPOE)
			this.mPppoeManager = ((PppoeManager) this.context.getSystemService("pppoe"));
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.network_eth_static, this);
		this.et_ip = ((IpAddrEditText) findViewById(R.id.iptxt_eth_static_ip));
		this.et_ip.setCanUp(false);
		this.et_mask = ((IpAddrEditText) findViewById(R.id.iptxt_eth_static_mask));
		this.et_gateway = ((IpAddrEditText) findViewById(R.id.iptxt_eth_static_gateway));
		this.et_dns = ((IpAddrEditText) findViewById(R.id.iptxt_eth_static_dns));
		this.et_dns2 = ((IpAddrEditText) findViewById(R.id.iptxt_eth_static_dns2));
		this.btn_save = ((Button) findViewById(R.id.btn_eth_static_ok));
		this.btn_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				Settings.Secure.putString(EthStatic.this.context.getContentResolver(),"dhcp_option", "0");
				EthStatic.this.mHandler.sendEmptyMessageDelayed(EthStatic.this.intCONNECT, 1000L);
			}
		});
		this.btn_cancel = ((Button) findViewById(R.id.btn_eth_static_cancel));
		this.btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				EthStatic.this.layoutManager.backShowView();
			}
		});
	}
}
