package com.unionman.settings.content;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.view.View;

import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.wifi.WifiEnabler;

public class NetworkActivity extends RightWindowBase {

	private CheckRadioButton crb_eth_set = null;
	private CheckRadioButton crb_wifi_net_set = null;
	private CheckRadioButton crb_wifi_set = null;
	private CheckRadioButton crb_wifi_toggle = null;
	private CheckRadioButton crb_ap_toggle=null;
	private CheckRadioButton crb_ap_set=null;
	
	private WifiManager wifiManager;
	private WifiEnabler wifiEnabler = null;
	private static final String TAG = "NetworkActivity";

	public NetworkActivity(Context paramContext) {
		super(paramContext);
	}

	public void initData() {

	}

	public void onInvisible() {
		Logger.i(TAG, "onInvisible()--");
		if (this.wifiEnabler != null)
			this.wifiEnabler.pause();
	}

	public void onResume() {
		Logger.i(TAG, "onResume()--");
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int apState=wifiManager.getWifiApState();
		if (this.crb_wifi_set == null) {
			this.crb_wifi_set = ((CheckRadioButton) findViewById(R.id.crb_wifi_ap_set));
			this.crb_wifi_toggle = ((CheckRadioButton) findViewById(R.id.crb_wifi_toggle));
		}
		
		if(this.crb_ap_set==null){
			this.crb_ap_set = ((CheckRadioButton) findViewById(R.id.crb_ap_set));
			this.crb_ap_toggle = ((CheckRadioButton) findViewById(R.id.crb_ap_toggle));
		}
		
		if(apState==WifiManager.WIFI_AP_STATE_ENABLED||apState==WifiManager.WIFI_AP_STATE_ENABLING){
			crb_ap_toggle.setChecked(true);
			 WifiConfiguration apConfig =wifiManager.getWifiApConfiguration();
			crb_ap_toggle.setText2(apConfig.SSID);
		}else if(apState==WifiManager.WIFI_AP_STATE_DISABLED||apState==WifiManager.WIFI_AP_STATE_DISABLING){
			crb_ap_toggle.setChecked(false);
		}
		if(NetworkActivity.this.crb_ap_toggle.isChecked()){
			NetworkActivity.this.crb_ap_toggle.setNextFocusDownId(R.id.crb_ap_set);
		}else{
			NetworkActivity.this.crb_ap_toggle.setNextFocusDownId(R.id.crb_ap_toggle);
		}
		crb_ap_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
					if(paramBoolean){
						crb_ap_toggle.setText2("Android_AP");
					}else{
						crb_ap_toggle.setText2("");
					}
				crb_ap_set.setViewState(paramBoolean);
					setWifiApEnabled(paramBoolean);
										
					if(paramBoolean){
						NetworkActivity.this.crb_ap_toggle.setNextFocusDownId(R.id.crb_ap_set);
					}else{
						NetworkActivity.this.crb_ap_toggle.setNextFocusDownId(R.id.crb_ap_toggle);
					}
			}
		});
		
		this.crb_wifi_set.setViewState(this.crb_wifi_toggle.isChecked());
		this.crb_ap_set.setViewState(this.crb_ap_toggle.isChecked());
		this.crb_ap_set.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				try {
					Logger.i(TAG, "show frame_wifi");
					NetworkActivity.this.layoutManager.showLayout(ConstantList.FRAME_WIFI_AP);
					return;
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		});
		
		if (this.wifiEnabler != null) {
			this.wifiEnabler.resume();
		} else {
			this.wifiEnabler = new WifiEnabler(this.context, this.crb_wifi_toggle,this.crb_wifi_set,this.crb_ap_toggle);
			this.wifiEnabler.resume();
		}
	}

	public void setId() {
		Logger.i(TAG, "setId()--");
		this.frameId = 0;
		this.levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG, "setView()--");
		this.layoutInflater.inflate(R.layout.network, this);
		this.crb_wifi_toggle = ((CheckRadioButton) findViewById(R.id.crb_wifi_toggle));
		this.crb_wifi_set = ((CheckRadioButton) findViewById(R.id.crb_wifi_ap_set));
		this.crb_ap_toggle = ((CheckRadioButton) findViewById(R.id.crb_ap_toggle));
		this.crb_ap_set = ((CheckRadioButton) findViewById(R.id.crb_ap_set));
		this.crb_wifi_net_set = ((CheckRadioButton) findViewById(R.id.crb_wifi_net_set));
		this.crb_eth_set = ((CheckRadioButton) findViewById(R.id.crb_eth_set));
		Logger.i(TAG, "Contants.SYS_SURPORT_WIFI="+Contants.SYS_SURPORT_WIFI);
		Logger.i(TAG, "Contants.SYS_SURPORT_ETHERNET="+Contants.SYS_SURPORT_ETHERNET);
		if (!Contants.SYS_SURPORT_WIFI) {
			Logger.i(TAG, "SYS NO SURPORT WIFI");
			this.crb_wifi_toggle.setVisibility(View.GONE);
			this.crb_eth_set.setVisibility(View.GONE);
			this.crb_wifi_set.setVisibility(View.GONE);
			findViewById(R.id.line2).setVisibility(View.GONE);
		}
		if (!Contants.SYS_SURPORT_ETHERNET){
			Logger.i(TAG, "SYS NO SURPORT ETHERNET");
			this.crb_eth_set.setVisibility(View.GONE);
		}

		this.crb_wifi_set.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CheckRadioButton paramAnonymousCheckRadioButton,
							boolean paramAnonymousBoolean) {
						try {
							Logger.i(TAG, "show frame_wifi");
							NetworkActivity.this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_WIFI);
							return;
						} catch (Exception localException) {
							localException.printStackTrace();
						}
					}
				});
		
		this.crb_wifi_net_set.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CheckRadioButton paramAnonymousCheckRadioButton,
							boolean paramAnonymousBoolean) {
						try {
							Logger.i(TAG, "show frame_wifi_set");
							NetworkActivity.this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_WIFI_SET);
							return;
						} catch (Exception localException) {
							localException.printStackTrace();
						}
					}
				});

		this.crb_eth_set.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(
							CheckRadioButton paramAnonymousCheckRadioButton,
							boolean paramAnonymousBoolean) {
						try {
							Logger.i(TAG, "show frame_eth");
							NetworkActivity.this.layoutManager.showLayout(ConstantList.FRAME_NETWORK_ETH);
							return;
						} catch (Exception localException) {
							localException.printStackTrace();
						}
					}
				});
		
		this.wifiEnabler = new WifiEnabler(this.context, this.crb_wifi_toggle,this.crb_wifi_set,this.crb_ap_toggle);
	}
	
	public void setWifiApEnabled(boolean enabled) {
		Logger.i(TAG, "setWifiApEnabled()--");
        if (enabled) { // disable WiFi in any case  
            //wifi���ȵ㲻��ͬʱ�򿪣����Դ��ȵ��ʱ����Ҫ�ر�wifi  
            wifiManager.setWifiEnabled(false);  
        }  
        try {  
        	 WifiConfiguration apConfig =wifiManager.getWifiApConfiguration();
        	 if(apConfig==null){
        		 apConfig=new WifiConfiguration();
        		 //�ȵ������
                 apConfig.SSID = "MyBox";  
                 //�ȵ������  
                 apConfig.preSharedKey="12345678";
                 //�ŵ�
                 apConfig.channel=1;
                 //��ȫ��
                 apConfig.allowedKeyManagement.set(KeyMgmt.NONE);
        	 }
        	 boolean isSuccess=wifiManager.setWifiApEnabled(apConfig, enabled);
        	 if(enabled){
        		 if(isSuccess){
					 crb_ap_toggle.setText2(apConfig.SSID);
        		 }
        	 }else{
        		 if(isSuccess){
					 crb_ap_toggle.setText2("");
        		 }
        	 }
        } catch (Exception e) {  
        	e.printStackTrace();
        }  
    }  
}
