package com.unionman.settingwizard.ui;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnFocusChangeListener;

import com.unionman.settingwizard.MyService;
import com.unionman.settingwizard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.ethernet.EthernetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.EthernetDataTracker;
import android.widget.Toast;

import com.unionman.settingwizard.network.EthCtl;
import com.unionman.settingwizard.util.BitmapCtl;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.PropertyUtils;

public class NetworkSetupActivity extends Activity implements OnClickListener, EthCtl.StatusCallBack {
    private String TAG = "NetworkSetupActivity";

    private EthernetManager mEthManager = null;
    private IntentFilter mIntentFilter = null;
    private String KEY_ETH_INTERFACE = "eth0";
    private ConnectivityManager mConnectivityManager = null;
    private EthCtl mNetworkCtl = null;
    private ImageView mReflectedView;
    private LinearLayout mContentView;

    private TextView mWiredAuto = null;
    private TextView mWiredAutoSubhead = null;
    private TextView mWiredManual = null;
    private TextView mWiredManualSubhead = null;
    private TextView mWirelessAuto = null;
    private TextView mWirelessAutoSubhead = null;
    private LinearLayout mWiredAutoSetupView;
    private LinearLayout mWiredManulSetupView;
    private LinearLayout mWirelessAutoSetupView;
    private WifiManager mWifiManager = null;
    private boolean bIsWired = false;
    private boolean bIsWifi = false;
    private boolean bNextStepClicked = true;

	private static final int WIRED_NETWORK_OK = 1;
	private static final int WIRED_NETWORK_FAILED = 2;
	private static final int WIFI_NETWORK_OK = 3;
	private static final int WIFI_NETWORK_FAILED = 4;
    
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WIRED_NETWORK_OK:
			case WIRED_NETWORK_FAILED:
			case WIFI_NETWORK_OK:
			case WIFI_NETWORK_FAILED: {
				Intent intent = new Intent();
				intent.setClass(NetworkSetupActivity.this, SetupFinishActivity.class);
				startActivity(intent);
				finish();
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_setup2);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		bIsWired = false;
		bIsWifi = false;
        initView();
        mNetworkCtl = new EthCtl(this);
    }

    @Override
    public void onResume() {
        if (mWifiManager.getConnectionInfo() != null) {
            showWifiSSID();
        }
        mNetworkCtl.startBroadcast(this, this);
        super.onResume();
    }

    private void initView() {
        mWiredAutoSetupView = (LinearLayout) findViewById(R.id.wired_auto);
        mWiredManulSetupView = (LinearLayout) findViewById(R.id.wired_manual);
        mWirelessAutoSetupView = (LinearLayout) findViewById(R.id.wireless_auto);
        mWiredAuto = (TextView) findViewById(R.id.tv_wired_auto_setup);
        mWiredManual = (TextView) findViewById(R.id.tv_wired_manual_setup);
        mWirelessAuto = (TextView) findViewById(R.id.tv_wireless_auto_setup);
        mWiredAutoSubhead = (TextView) findViewById(R.id.tv_wired_auto_setup_subhead);
        mWiredManualSubhead = (TextView) findViewById(R.id.tv_wired_manual_setup_subhead);
        mWirelessAutoSubhead = (TextView) findViewById(R.id.tv_wireless_auto_setup_subhead);

        mWiredAutoSetupView.setOnClickListener(this);
        mWiredManulSetupView.setOnClickListener(this);
        mWirelessAutoSetupView.setOnClickListener(this);
        mWiredAutoSetupView.setOnFocusChangeListener(focusListener);
        mWiredManulSetupView.setOnFocusChangeListener(focusListener);
        mWirelessAutoSetupView.setOnFocusChangeListener(focusListener);

        if (MyService.mEthernetCurrentStatus == EthernetDataTracker.EVENT_PHY_LINK_DOWN) {
            mWiredAutoSubhead.setText(R.string.eth_phy_link_down_check);
        } else {
            mWiredAutoSubhead.setText(R.string.wired_auto_subhead);
        }

        Button nextSetupBtn = (Button) findViewById(R.id.btn_next_step);
        Button lastSetupBn = (Button) findViewById(R.id.btn_last_step);
        nextSetupBtn.requestFocus();
        nextSetupBtn.setOnClickListener(this);
        lastSetupBn.setOnClickListener(this);
    }

    public OnFocusChangeListener focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            
        }

    };

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (mNetworkInfo != null) {
                Log.w("mNetworkInfo.getType():  ", "" + mNetworkInfo.getType());
                Log.w("mNetworkInfo.getTypeName():  ", "" + mNetworkInfo.getTypeName());
                Log.w("mNetworkInfo.getExtraInfo():  ", "" + mNetworkInfo.getExtraInfo());
                Log.w("mNetworkInfo.getSubtypeName():  ", "" + mNetworkInfo.getSubtypeName());
                Log.w("mNetworkInfo.getState():  ", "" + mNetworkInfo.getState());
                Log.w("mNetworkInfo.describeContents():  ", "" + mNetworkInfo.describeContents());
                Log.w("mNetworkInfo.toString():  ", "" + mNetworkInfo.toString());

                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private String getWifiIP() {
        int ipInt = mWifiManager.getConnectionInfo().getIpAddress();
        String ip = NetworkUtils.intToInetAddress(ipInt).toString().split("\\/")[1];
        return ip;
    }

    /*
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_next_step:

                String ipEth = mNetworkCtl.getEthIP();
                String ipWifi = getWifiIP();

                if ((ipEth.equals("") || ipEth.equals("0.0.0.0"))
                        && (ipWifi.equals("") || ipWifi.equals("0.0.0.0"))
                        && MyService.mEthernetCurrentStatus == EthernetDataTracker.EVENT_PHY_LINK_UP) {
                	if (!bIsWired){
                		 wiredAuto();
                	}
                } else {
                	Log.i(TAG,"================start SourceSetupActivity==============");
                	mHandler.removeMessages(WIRED_NETWORK_OK);
                	mHandler.removeMessages(WIRED_NETWORK_FAILED);
                	mHandler.removeMessages(WIFI_NETWORK_OK);
                	mHandler.removeMessages(WIFI_NETWORK_FAILED);
                	intent = new Intent(NetworkSetupActivity.this, SetupFinishActivity.class);
   			    	startActivity(intent);
                    finish();
                }            		
                break;
            case R.id.btn_last_step:
            	intent = new Intent(NetworkSetupActivity.this, UsermodeOrStoremodeActivity.class);
            	startActivity(intent);
                finish();
                break;
            case R.id.wired_auto:
                wiredAuto();
                break;
            case R.id.wired_manual:
                mNetworkCtl.startEth();
                mWifiManager.setWifiEnabled(false);
                intent = new Intent(NetworkSetupActivity.this, WiredNetworkSetup.class);
                startActivity(intent);
                finish();
                break;
            case R.id.wireless_auto:
                //mNetworkCtl.stopEth();
                intent = new Intent(NetworkSetupActivity.this, WirelessNetworkSetup.class);
                startActivity(intent);
                bIsWifi = true;
                finish();
                break;
            default:
                break;
        }
    }

    private void wiredAuto() {
        mNetworkCtl.startEth();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (MyService.mEthernetCurrentStatus == EthernetDataTracker.EVENT_PHY_LINK_DOWN) {
                    mWiredAutoSubhead.setText(R.string.eth_phy_link_down_check);
                } else {
                    //mNetworkCtl.startEth();
                    mWifiManager.setWifiEnabled(false);
                    mWiredAutoSubhead.setText(R.string.auto_get_ip_please_wait);
                    mNetworkCtl.startDHCP(true);
                    bIsWired = true;
                }
            }
        }, 500);
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) { 
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        mNetworkCtl.stopBroadcast(this);
        super.onPause();
    }

    private void showDhcpIP() {
        try {
            mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            String mIP = mConnectivityManager
                    .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                    .getAddresses().toString();
            String[] arrIP = mIP.split("/|\\[|\\]| ");

            if (arrIP.length >= 3) {
                String str = getResources().getString(R.string.aleady_connect);
                mWiredAutoSubhead.setText(str + ": " + arrIP[2]);
                mWiredManualSubhead.setText(R.string.wired_manual_subhead);
                mWirelessAutoSubhead.setText(R.string.wireless_auto_subhead);
            }
        } catch (NullPointerException e) {
            Log.w("NetworkSetupActivity", "can not get IP" + e);
            e.printStackTrace();
        }

    }

    private void showStaticIP() {
        try {
            mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            String mIP = mConnectivityManager
                    .getLinkProperties(ConnectivityManager.TYPE_ETHERNET)
                    .getAddresses().toString();
            String[] arrIP = mIP.split("/|\\[|\\]| ");

            String str = getResources().getString(R.string.aleady_connect);
            mWiredManualSubhead.setText(str + ": " + arrIP[2]);
            mWiredAutoSubhead.setText(R.string.wired_auto_subhead);
            mWirelessAutoSubhead.setText(R.string.wireless_auto_subhead);

            Log.w("NetworkSetupActivity", " get IP  " + arrIP[2]);
        } catch (NullPointerException e) {
            Log.w("NetworkSetupActivity", "can not get IP" + e);
            e.printStackTrace();
        }
    }

    private void showWifiSSID() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null && info.getSSID() != null) {
            String ssid = info.getSSID();
            String str = getResources().getString(R.string.aleady_connect);
            int ipaddr = info.getIpAddress();
            Log.d(TAG, "showWifiSSID ssid = "+ssid+" ipaddr = "+ipaddr);
            if(0 == ipaddr){
            	mWirelessAutoSubhead.setText(R.string.wireless_auto_subhead);
            }else{
            	mWirelessAutoSubhead.setText(str + ": " + ssid.substring(1, ssid.length() - 1));
            }
            mWiredAutoSubhead.setText(R.string.wired_auto_subhead);
            mWiredManualSubhead.setText(R.string.wired_manual_subhead);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, int message) {
        switch (message) {
            case EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED:
                Log.w("NetworkSetupActivity", "EVENT_DHCP_CONNECT_SUCCESSED ");
                showDhcpIP();
                if(bIsWired){
                	mHandler.sendEmptyMessageDelayed(WIRED_NETWORK_OK, 2000);
                }else if(bIsWifi){
                	mHandler.sendEmptyMessageDelayed(WIFI_NETWORK_OK, 2000);
                }
                break;
            case EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED:
                Log.w("NetworkSetupActivity", "EVENT_DHCP_CONNECT_FAILED ");
                //show connect faled
                if(bIsWired){
                	mWiredAutoSubhead.setText(R.string.network_phy_link_failed);
                	mHandler.sendEmptyMessageDelayed(WIRED_NETWORK_FAILED, 2000);
                }else if(bIsWifi){
                	mWirelessAutoSubhead.setText(R.string.network_phy_link_failed);
                	mHandler.sendEmptyMessageDelayed(WIFI_NETWORK_FAILED, 2000);
                }
                break;
            case EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED:
                Log.w("NetworkSetupActivity", "EVENT_DHCP_DISCONNECT_SUCCESSED ");
                break;
            case EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED:
                Log.w("NetworkSetupActivity", "EVENT_DHCP_DISCONNECT_FAILED ");
                //show disconnect failed
                break;
            case EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED:
                Log.w("NetworkSetupActivity", "EVENT_STATIC_CONNECT_SUCCESSED ");
                showStaticIP();
                //show set static ip connect success
                break;
            case EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED:
                Log.w("NetworkSetupActivity", "EVENT_STATIC_CONNECT_FAILED ");
                //show set static ip connect failed
                if(bIsWired){
                	mWiredAutoSubhead.setText(R.string.network_phy_link_failed);
                	mHandler.sendEmptyMessage(WIRED_NETWORK_FAILED);
                }else if(bIsWifi){
                	mWirelessAutoSubhead.setText(R.string.network_phy_link_failed);
                }
                break;
            case EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED:
                Log.w("NetworkSetupActivity", "EVENT_STATIC_DISCONNECT_SUCCESSED ");
                //show set static ip disconnect success
                break;
            case EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED:
                Log.w("NetworkSetupActivity", "EVENT_STATIC_DISCONNECT_FAILED ");
                //show set static ip disconnect failed
                break;
            case EthernetDataTracker.EVENT_PHY_LINK_UP:
                mWiredAutoSubhead.setText(R.string.auto_get_ip_please_wait);
                Log.w("NetworkSetupActivity", "EVENT_PHY_LINK_UP ");
                //show phy link up
                break;
            case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
                mWiredAutoSubhead.setText(R.string.eth_phy_link_down_check);
                bIsWired = false;
                Log.w("NetworkSetupActivity", "EVENT_PHY_LINK_DOWN ");
                //show phy link down
                break;
            default:
                break;
        }
    }
}
