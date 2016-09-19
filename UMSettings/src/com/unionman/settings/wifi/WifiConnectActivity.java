package com.unionman.settings.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.format.Formatter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Iterator;
import java.util.List;
import com.unionman.settings.R;
import com.unionman.settings.UMSettings;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.DLBLog;
import com.unionman.settings.tools.StringUtils;

public class WifiConnectActivity extends Activity
{
      final static int PASSWORD_NO = 0;
      final static int PASSWORD_RIGHT = 1;
      final static int PASSWORD_WRONG = 2;
      
	private AccessPoint ap;
	View.OnClickListener cacelClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			UMDebug.d("WifiConnectActivity", "cacelClickListener");
		  	WifiConnectActivity.this.finish();
		}
	};
	private Button connCancel;
	private Button connDisconect;
	private Button connForget;
	View.OnClickListener connectClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			UMDebug.d("WifiConnectActivity", "connect button click...  save=" + WifiConnectActivity.this.saved 
				+ "======ssid========" + WifiConnectActivity.this.ap.ssid);
			if (WifiConnectActivity.this.ap.ssid != null)
			{
				if ((!connected) && (saved))
				{
					UMDebug.d("WifiConnectActivity", "connected=" + WifiConnectActivity.this.connected +", saved="+ WifiConnectActivity.this.saved);
					mSWifiManager.connectWifi(WifiConnectActivity.this.ap.ssid);
					WifiConnectActivity.this.finish();
					return ;
				}
			int passwordstate=PASSWORD_NO;//没有密码的默认值

					try
        				{
					      if (!saved)
				            {
//      					    mSWifiManager.setSSID(ap.ssid);
        						UMDebug.d("WifiConnectActivity", "haspassword="+ WifiConnectActivity.this.haspassword);
        						if (haspassword)
        						{
        							String str7 = password.getText().toString();
        							UMDebug.d("WifiConnectActivity", "password:"+str7);   							
        							if (WifiConnectActivity.this.checkPassword(str7)){
        								mSWifiManager.setPassword(WifiConnectActivity.this.password.getText().toString());
                                        UMDebug.d("WifiConnectActivity", "Password right!");
                                                   passwordstate = PASSWORD_RIGHT;
                                                 }else{
                                                   passwordstate = PASSWORD_WRONG;
                                                 }
        						}
        						
						}
						if(passwordstate != PASSWORD_WRONG){
							UMDebug.d("WifiConnectActivity", "setSSID========="+mSWifiManager.setSSID(ap.getSsid())+"ap.getSsid()=="+ap.getSsid()+ap.ssid);
							
        						 if (mSWifiManager.setSSID(ap.getSsid()))
        						{   
        							 String str1 = "dhcp";
        							 int i = -1;
        							 try
        							 {
        							  i = Settings.Secure.getInt(WifiConnectActivity.this.getContentResolver(), "default_wifi_mod");
        							  UMDebug.d("WifiConnectActivity", "i========="+i);
        							 }catch (SettingNotFoundException e){
        								str1 = "dhcp";
        							 }
        							if (i == 1)
        							{
        							  str1 = "static";
        							}
        							boolean bool = mSWifiManager.setMode(str1);
        							log.d("connect button click... setNetmod= " + str1);
        							if (bool)
        							{
        								WifiNetworkBean localWifiNetworkBean = new WifiNetworkBean();
        								String str2 = Settings.Secure.getString(WifiConnectActivity.this.getContentResolver(), "wifi_static_ip");
        								String str3 = Settings.Secure.getString(WifiConnectActivity.this.getContentResolver(), "wifi_static_gateway");
        								String str4 = Settings.Secure.getString(WifiConnectActivity.this.getContentResolver(), "wifi_static_mask");
        								String str5 = Settings.Secure.getString(WifiConnectActivity.this.getContentResolver(), "wifi_static_dns");
        								String str6 = Settings.Secure.getString(WifiConnectActivity.this.getContentResolver(), "wifi_static_dns2");
        								log.d(" str2= " + str2+" str3= " + str3+" str4= " + str4+" str5= " + str5+" str6= " + str6);
        								if (str2 == null)
        									str2 = "0.0.0.0";
        								localWifiNetworkBean.setIp(str2);
        								if (str4 == null)
        									str4 = "0.0.0.0";
        								localWifiNetworkBean.setMask(str4);
        								if (str3 == null)
        									str3 = "0.0.0.0";
        								localWifiNetworkBean.setGateway(str3);
        								if (str5 == null)
        									str5 = "0.0.0.0";
        								localWifiNetworkBean.setDns(str5);
        								if (str6 == null)
        									str6 = "0.0.0.0";
        								localWifiNetworkBean.setDns2(str6);
        								mSWifiManager.setNetWork(localWifiNetworkBean);
        								log.d("connect button click... setNetwork ");
        							}
        							
        							List<WifiConfiguration> localList = wifiManager.getConfiguredNetworks();
        							if (localList != null)
        							{
        								Iterator<WifiConfiguration> localIterator = localList.iterator();
        								if (localIterator.hasNext())
        								{
        									WifiConfiguration localWifiConfiguration = (WifiConfiguration)localIterator.next();
        									if ((localWifiConfiguration.SSID != null) && (AccessPoint.removeDoubleQuotes(localWifiConfiguration.SSID).equals(ap.getSsid())))
        									{   
        										log.d("connect button click..localWifiConfiguration.SSID====" + localWifiConfiguration.SSID+ "AccessPoint.removeDoubleQuotes(localWifiConfiguration.SSID)====" + AccessPoint.removeDoubleQuotes(localWifiConfiguration.SSID));
        										log.d("connect button click... ap.getSsid()==="+ap.getSsid());
        										log.d("connect button click... localWifiConfiguration.networkId======"+localWifiConfiguration.networkId);
        										wifiManager.removeNetwork(localWifiConfiguration.networkId);
//        									    wifiManager.saveConfiguration();
        									}
        								}
        							}
        							log.d("connect button click..setNetwork" + mSWifiManager + " ap:" + ap.ssid);
        							mSWifiManager.connectWifi(ap.ssid);
        							log.d("connect button click... will to connect");
        							WifiConnectActivity.this.finish();
        						}
        						return;
						}
				}
				catch (Exception localException)
				{
						mSWifiManager.forgetNetwork(ap.ssid);
						log.d("=========Exception===========" + localException);
						localException.printStackTrace();
					WifiConnectActivity.this.finish();
						return ;
					}
				
			}
			WifiConnectActivity.this.finish();
		}
	};
	private boolean connected;
	View.OnClickListener disconnectClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			UMDebug.d("WifiConnectActivity", "disconnectClickListener");
			mSWifiManager.disconnect();
			WifiConnectActivity.this.finish();
		}
	};
	View.OnClickListener forgetClickListener = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			UMDebug.d("WifiConnectActivity", "forgetClickListener");
			mSWifiManager.forgetNetwork(WifiConnectActivity.this.ap.ssid);
			WifiConnectActivity.this.finish();
		}
	};
	private boolean haspassword;
	private TextView ip;
	private LinearLayout layout_connect_btn;
	private LinearLayout layout_connect_ip;
	private LinearLayout layout_connect_status;
	private LinearLayout layout_nosave_btn;
	private LinearLayout layout_psd;
	private LinearLayout layout_saved_btn;
	UMLogger log = UMLogger.getLogger(getClass());
	View.OnKeyListener mEditKeyListener = new View.OnKeyListener()
	{
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
		{
			UMDebug.d("WifiConnectActivity", "mEditKeyListener");
			if ((paramAnonymousKeyEvent.getAction() == 0) && (paramAnonymousInt == 4))
			{
				return ((EditText)paramAnonymousView).getText().length() > 0;
			}
			if ((paramAnonymousKeyEvent.getAction() == 1) && (paramAnonymousInt == 4))
			{
				if (((EditText)paramAnonymousView).getText().length() <= 0)
				{
					return false;
				}
				StringUtils.delText((EditText)paramAnonymousView);
				return true;
			}
			return false;
		}
	};
	private UMWifiManager mSWifiManager;
	private Button newCancel;
	private Button newConnect;
	private EditText password;
	private TextView safe;
	private Button saveCancel;
	private Button saveConnect;
	private Button saveForget;
	private boolean saved;
	private CheckBox showPsd;
	CompoundButton.OnCheckedChangeListener showPswChecklistener = new CompoundButton.OnCheckedChangeListener()
	{
		public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
		{
			UMDebug.d("WifiConnectActivity", "onCheckedChanged");
			if (paramAnonymousBoolean)
			{
				password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			}
			else
			{
				password.postInvalidate();
				password.setTransformationMethod(PasswordTransformationMethod.getInstance());
				return;
			}
		}
	};
	private TextView ssid;
	private TextView status;
	private TextView strengh;
	private WifiManager wifiManager;

	private boolean checkPassword(String paramString)
	{
		if(paramString == null || paramString.equals(""))
		{   
			if(this.ap.getSecurity() == 0)
			{
				Toast.makeText(this, getResources().getString(2131296523), 3000).show();
				return false;
			}
		}
		int i =  paramString.length();
		log.d("WifiConnectActivity    checkPassword==="+paramString+"i==="+i);
		if ((i == 5) || (i == 10) || (i == 13) || (i == 26) || (i == 16) || (i == 32))
		{	
			//Toast.makeText(this, "密码正确", 3000).show();
			
			return true;
		} 
		else  if ((i >= 8) && (i <= 64))
		{
			//Toast.makeText(this, "密码正确", 3000).show();
			
			return true;
		} 
		else
		{
			Toast.makeText(this, "密码错误", 3000).show();			
			return false;
		}
	}

	private void setView()
	{
		UMDebug.d("WifiConnectActivity", "connected=" + this.connected + ",saved=" + this.saved);
		if ((connected) && (saved))
		{
			UMDebug.d("WifiConnectActivity111", "setView connected=true, saved=true");
			layout_connect_btn = ((LinearLayout)findViewById(2131099916));
			layout_connect_btn.setVisibility(0);
			layout_connect_ip = ((LinearLayout)findViewById(2131099908));
			layout_connect_ip.setVisibility(View.VISIBLE);
			ip = ((TextView)findViewById(2131099909));
			WifiInfo localWifiInfo = ap.getInfo();
			if (localWifiInfo != null)
			{
				int i = localWifiInfo.getIpAddress();
				if (i != 0)
					ip.setText(Formatter.formatIpAddress(i));
				DLBLog.d("address=" + i + ",ip=" + Formatter.formatIpAddress(i));
			}
			layout_connect_status = ((LinearLayout)findViewById(2131099906));
			layout_connect_status.setVisibility(0);
			status = ((TextView)findViewById(2131099907));
			NetworkInfo.DetailedState localDetailedState = ap.getState();
			if (localDetailedState != null)
				status.setText(get(this, localDetailedState));
			connDisconect = ((Button)findViewById(2131099917));
			connDisconect.setOnClickListener(this.disconnectClickListener);
			connForget = ((Button)findViewById(2131099918));
			connForget.setOnClickListener(this.forgetClickListener);
			connCancel = ((Button)findViewById(2131099919));
			connCancel.setOnClickListener(this.cacelClickListener);
			
		}
		else if ((!connected) && (saved))
		{
			UMDebug.d("WifiConnectActivity", "setView connected=false, saved=true");
			layout_saved_btn = ((LinearLayout)findViewById(2131099920));
			layout_saved_btn.setVisibility(0);
			saveConnect = ((Button)findViewById(2131099921));
			saveConnect.setOnClickListener(connectClickListener);
			saveForget = ((Button)findViewById(2131099922));
			saveForget.setOnClickListener(forgetClickListener);
			saveCancel = ((Button)findViewById(2131099923));
			saveCancel.setOnClickListener(cacelClickListener);
		}else
		{
			if (!saved)
			{
				UMDebug.d("WifiConnectActivity", "saved=false");
				this.layout_psd = ((LinearLayout)findViewById(2131099910));
				this.layout_nosave_btn = ((LinearLayout)findViewById(2131099913));
				this.layout_nosave_btn.setVisibility(0);
				this.newConnect = ((Button)findViewById(2131099914));
				this.newConnect.setOnClickListener(this.connectClickListener);
				this.newCancel = ((Button)findViewById(2131099915));
				this.newCancel.setOnClickListener(this.cacelClickListener);
				if (this.haspassword)
				{
					UMDebug.d("WifiConnectActivity", "saved=false haspassword=true");
					this.layout_psd.setVisibility(0);
					this.password = ((EditText)findViewById(2131099911));
					this.password.setOnKeyListener(this.mEditKeyListener);
					this.showPsd = ((CheckBox)findViewById(2131099912));
					this.showPsd.setOnCheckedChangeListener(this.showPswChecklistener);
				}
				else
				{
					this.layout_psd.setVisibility(4);
					UMDebug.d("WifiConnectActivity", "saved=true haspassword=false");
					this.newCancel.requestFocus();
				}
			}
			else
			{
				UMDebug.d("WifiConnectActivity", "saved=true");
				return;
			}
		}
		
		
	}

	public String get(Context paramContext, NetworkInfo.DetailedState paramDetailedState)
	{
		return get(paramContext, null, paramDetailedState);
	}

	public String get(Context paramContext, String paramString, NetworkInfo.DetailedState paramDetailedState)
	{
//		Resources localResources = paramContext.getResources();
//		if (paramString == null)
//		{
//			String[] arrayOfString;
//			int j;
//			for (int i = 2131034112; ; i = 2131034113)
//			{
//				arrayOfString = localResources.getStringArray(i);
//				j = paramDetailedState.ordinal();
//				if ((j < arrayOfString.length) && (arrayOfString[j].length() != 0))
//					break;
//				return null;
//			}
		String[] formats = paramContext.getResources().getStringArray((ssid == null)
                ? 2131034112:2131034113);//R.array.wifi_status : R.array.wifi_status_with_ssid);
        int index = paramDetailedState.ordinal();

        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
       return String.format(formats[index], ssid);
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		mSWifiManager = UMWifiManager.getInstance(this);
		wifiManager = ((WifiManager)getSystemService("wifi"));
		setContentView(R.layout.wifi_connect);
		ap = ((UMSettings)getApplicationContext()).ap;
		UMDebug.d("WifiConnectActivity", "ap="+this.ap);
		if (ap == null)
		{
			finish();
			return ;
		}
		UMDebug.d("WifiConnectActivity", "networkId ="+this.ap.networkId );
		boolean bool1 = ap.networkId != -1 ? true : false;		
		UMDebug.umdebug_trace();
		saved = bool1;
		String localObject = "";
		try
		{
			NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getNetworkInfo(1);
			localNetworkInfo.getState();
			if (localNetworkInfo.isConnected())
			{
				localObject =  wifiManager.getConnectionInfo().getSSID();
			}
			else
			{
				localObject = "";
			}
			UMDebug.d("WifiConnectActivity", "ap current=" + (String)localObject + ",ap.ssid=" + this.ap.ssid+",localObject="+localObject);
			if ((!ap.ssid.equals(localObject)) && (!this.ap.ssid.equals(AccessPoint.removeDoubleQuotes((String)localObject))))
			{
				connected = false;
			}
			UMDebug.d("WifiConnectActivity", "security = "+this.ap.security);
			if (ap.security != 0)
			{
				haspassword = true;
			}
			else
			{
				haspassword = false;
			}
			this.ssid = ((TextView)findViewById(R.id.wifi_ssid));
			this.ssid.setText(ap.ssid);
			this.safe = ((TextView)findViewById(R.id.wifi_safe));
			String[] arrayOfString1 = getResources().getStringArray(2131034114);
			UMDebug.d("WifiConnectActivity", "arrayOfString1[this.ap.security]= "+arrayOfString1[this.ap.security]);
			this.safe.setText(arrayOfString1[this.ap.security]);
			this.strengh = ((TextView)findViewById(2131099905));
			int j = ap.getLevel();
			UMDebug.d("WifiConnectActivity", "j = "+j);
			if (j != -1)
			{
				String[] arrayOfString2 = getResources().getStringArray(2131034115);
				UMDebug.d("WifiConnectActivity", "arrayOfString2[j] = "+arrayOfString2[j]);
				strengh.setText(arrayOfString2[j]);
			}
			setView();
			return;
		}	
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		UMDebug.umdebug_trace();
	}

	protected void onDestroy()
	{
		super.onDestroy();
		((UMSettings)getApplicationContext()).ap = null;
	}

	protected void onResume()
	{
		super.onResume();
	}
}
