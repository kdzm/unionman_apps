
package com.unionman.netsetup.view.setting;

import java.util.Iterator;
import java.net.InetAddress;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.EthernetDataTracker;
import android.net.PppoeStateTracker;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.LinkProperties;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.view.WindowManager;

import com.unionman.netsetup.R;
import com.unionman.netsetup.util.Constant;

/**
 * Network wire three level menu
 *
 * @author huyq
 */
public class EtherSetting extends LinearLayout {
    private static final String TAG = "EtherSetting";
    // public final static int MSG_CLEAR = NetStateDialog.MSG_CLEAR;
    // public final static int CONNECTING = NetStateDialog.CONNECTING;
    // public final static int CONNECT_FAILED = NetStateDialog.CONNECT_FAILED;
    // public final static int SETTING_IP = NetStateDialog.SETTING_IP;
    // public final static int SET_IP_FAILED = NetStateDialog.SET_IP_FAILED;
    // public final static int GETTING_IP = NetStateDialog.GETTING_IP;

    private Context mContext;
    // EthernetManager object
    private EthernetManager mEthManager;
    // PppoeManager object
    private PppoeManager mPppoeManager;
    private ConnectivityManager mConnectivityManager;

    // layout of AutoIP
    private LinearLayout mAutoIPLayout;
    // text of AutoIP
    private TextView mAutoIPText;
    // layout of ManualIP
    private LinearLayout mManualIPLayout;
    // text of ManualIP
    private TextView mManualIPText;
    // text of PPPOE
    private TextView mPPPOEText;
    private CheckBox mSwitchCb;
    // text of AutoSelect
    private TextView mAutoSelectText;
    // view of ManualSelect
    private View mManualSelect;

    // Control NetSettingDialog display content
    private NetStateDialog netStateDialog;
    private EtherInputDialog etherInputDialog;
    private EtherShowIPDialog mEtherDialog;
    private EtherSetIPDialog mEtherSetDialog;

    private final IntentFilter mIntentFilter;
    // ethernet current status
    private int ethernet_current_status = -1;
    private int tips_status = -1;
    private int tips_status_new = -1;
    // pppoe current status
    private int pppoe_current_status = -1;
    // flag of is Ethernet On
    private boolean isEthernetOn = false;

    private int mLastMsg = -1;

    public static String DhcpIP = "";
    public static String DhcpGatewary = "";
    public static String DhcpMask = "";
    public static String DhcpDns = "";
    public final int TIPS_TIOME_OUT = 1500;
    public static Handler tipsHandler = new Handler();
    Runnable tipsRunnable = new Runnable() {
        public void run() {
        	if (tips_status == tips_status_new){
        		return;
        	}else{
        		tips_status = tips_status_new;
        	}
        	
        	if(tips_status==EthernetDataTracker.EVENT_PHY_LINK_DOWN)
        	{
                Toast.makeText(mContext, R.string.ethernet_link_down, Toast.LENGTH_SHORT).show();
        	}
        	else if(tips_status == EthernetDataTracker.EVENT_PHY_LINK_UP)
        	{
                Toast.makeText(mContext, R.string.ethernet_link_up, Toast.LENGTH_SHORT).show();	
        	}
            tipsHandler.removeCallbacks(tipsRunnable);
        }
    };

    /**
     * Control NetStateDialog display content
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (netStateDialog == null) {
                netStateDialog = NetStateDialog.createDialog(mContext,
                        mHandler, msg.what);
                netStateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            switch (msg.what) {
                case NetStateDialog.CONNECTING:
                case NetStateDialog.CONNECT_FAILED:
                case NetStateDialog.SETTING_IP:
                case NetStateDialog.SET_IP_FAILED:
                case NetStateDialog.GETTING_IP:
                    netStateDialog.refreshView(msg.what);
                    break;
                case NetStateDialog.CONNECT_SUCCESSED:
                    if (mLastMsg != -1) {
                        netStateDialog.refreshView(msg.what);
                    }
                    break;
                case NetStateDialog.MSG_CLEAR:
                    if (netStateDialog != null) {
                        netStateDialog.dismiss();
                        netStateDialog = null;
                    }
                    break;
                default:
                    break;
            }

            mLastMsg = msg.what;
        };
    };

    public EtherSetting(Context context, Handler handler) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.setting_ether, this);

        mIntentFilter = new IntentFilter(
                EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthSettingsReceiver, mIntentFilter);
        mEthManager = (EthernetManager) context
                .getSystemService(Context.ETHERNET_SERVICE);
        mPppoeManager = (PppoeManager) context
                .getSystemService(Context.PPPOE_SERVICE);
        IBinder b = ServiceManager
                .getService(Context.NETWORKMANAGEMENT_SERVICE);
        INetworkManagementService mNwService = INetworkManagementService.Stub
                .asInterface(b);

        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        initView(parent);
        // According to the status of Ethernet Ethernet switch setting
        if (mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
            mSwitchCb.setChecked(true);
            isEthernetOn = true;
            setViewInvalid(true);
        } else {
            mSwitchCb.setChecked(false);
            isEthernetOn = false;
            setViewInvalid(false);
        }

    }

    /**
     * The initialization of view
     *
     * @param parent
     */
    private void initView(View parent) {
        final ContentResolver cr = mContext.getContentResolver();
        // Ethernet switch
        mSwitchCb = (CheckBox) parent.findViewById(R.id.ether_switch_cb);
        mSwitchCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                etherNetOn(isChecked);
                // int state = mEthManager.getEthernetState();
                //
                // if (state != EthernetManager.ETHERNET_STATE_ENABLED &&
                // isChecked) {
                // if (mEthManager.isEthernetConfigured() != true) {
                //
                // } else {
                // mEthManager.setEthernetEnabled(isChecked);
                // }
                // } else {
                // mEthManager.setEthernetEnabled(isChecked);
                // }
                // mEthManager.enableEthernet(isChecked);
                // //mEthManager.setEthernetEnabled(isChecked);
                // isEthernetOn = isChecked;
                // if (isChecked) {
                // setViewInvalid(isChecked);
                // } else {
                // setViewInvalid(isChecked);
                // }
            }
        });

        // Automatic IP
        mAutoIPLayout = (LinearLayout) parent
                .findViewById(R.id.ether_autoip_lay);
        mAutoSelectText = (TextView) findViewById(R.id.ether_autosel_txt);
        mAutoIPText = (TextView) parent.findViewById(R.id.ether_autoip_txt);
        mAutoIPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pppoe_on = 0;
                try {
                    pppoe_on = Settings.System.getInt(cr,
                            Settings.Secure.PPPOE_ON);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pppoe_on == PppoeManager.PPPOE_STATE_ENABLED) {
                    if (mAutoSelectText.getVisibility() == View.GONE) {
                        // DHCP When not selected
                        ethernet_current_status = EthernetDataTracker.EVENT_PHY_LINK_UP;
                        startDhcp();
                    } else {
                        // DHCP has been selected
                        mEthManager.setEthernetEnabled(false);
                    }
                } else {
                    if (mAutoSelectText.getVisibility() == View.GONE) {
                    	if(ethernet_current_status==EthernetDataTracker.EVENT_PHY_LINK_DOWN)
                    	{
                            Toast.makeText(mContext, R.string.ethernet_link_down, Toast.LENGTH_SHORT).show();
                            return ;
                    	}
                        Message message = mHandler.obtainMessage();
                        message.what = NetStateDialog.GETTING_IP;
                        mHandler.sendMessageDelayed(message, 100);
                        // DHCP When not selected
                        cleanIP();
                        startDhcp();
                        //ethernet_current_status = EthernetDataTracker.EVENT_PHY_LINK_UP;
                    } else {
                        // DHCP has been selected
                        mEtherDialog = new EtherShowIPDialog(mContext,
                                mEthManager);
                        mEtherDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        mEtherDialog.show();
                    }
                }
            }
        });
/*        mAutoIPLayout
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mAutoIPLayout
                                    .setBackgroundResource(R.drawable.launcher_set_focus);
                            mAutoIPText.setTextColor(mContext.getResources()
                                    .getColor(R.color.black));
                        } else {
                            mAutoIPLayout
                                    .setBackgroundResource(R.drawable.button_transparent);
                            mAutoIPText.setTextColor(mContext.getResources()
                                    .getColor(R.color.white));
                        }
                    }
                });*/

        // Manual IP
        mManualIPLayout = (LinearLayout) parent
                .findViewById(R.id.ether_manualip_lay);
        mManualSelect = (TextView) parent
                .findViewById(R.id.ether_manualsel_txt);
        mManualIPText = (TextView) parent.findViewById(R.id.ether_manualip_txt);
        mManualIPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.LOG_TAG) {
                    Log.i(TAG, "onClick--->Ethernet_current_status-->"
                            + ethernet_current_status);
                }
                mEtherSetDialog = new EtherSetIPDialog(mContext, mHandler);
                mEtherSetDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                if (mAutoSelectText.getVisibility() == View.VISIBLE) {
                    mAutoSelectText.setVisibility(View.GONE);
                    mManualSelect.setVisibility(View.VISIBLE);
                }
                mEtherSetDialog.show();
                mEtherSetDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        // TODO Auto-generated method stub
                        if (ethernet_current_status == 0
                                && mAutoSelectText.getVisibility() == View.GONE) {
                            mAutoSelectText.setVisibility(View.VISIBLE);
                            mManualSelect.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
 /*       mManualIPLayout
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mManualIPLayout
                                    .setBackgroundResource(R.drawable.launcher_set_focus);
                            mManualIPText.setTextColor(mContext.getResources()
                                    .getColor(R.color.black));
                        } else {
                            mManualIPLayout
                                    .setBackgroundResource(R.drawable.button_transparent);
                            mManualIPText.setTextColor(mContext.getResources()
                                    .getColor(R.color.white));
                        }
                    }
                });
*/
        // Broadband dial-up
        mPPPOEText = (TextView) parent.findViewById(R.id.ether_pppoe_txt);
        mPPPOEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etherInputDialog = new EtherInputDialog(mContext, mHandler);
                etherInputDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                etherInputDialog.show();
            }
        });
/*        mPPPOEText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPPPOEText
                            .setBackgroundResource(R.drawable.launcher_set_focus);
                    mPPPOEText.setTextColor(mContext.getResources().getColor(
                            R.color.black));
                } else {
                    mPPPOEText
                            .setBackgroundResource(R.drawable.button_transparent);
                    mPPPOEText.setTextColor(mContext.getResources().getColor(
                            R.color.white));
                }
            }
        });*/
    }


	public void EtherSettingRequestFocus()
	{
        if (mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
			if(mAutoIPLayout!=null)
			{
				mAutoIPLayout.requestFocus();
			}
        }else{
			if(mSwitchCb!=null)
			{
				mSwitchCb.requestFocus();
			}
        }

	}
    /**
     * etherNet switch
     *
     * @param isChecked
     */
    public void etherNetOn(boolean isChecked) {
        int state = mEthManager.getEthernetState();

        if (state != EthernetManager.ETHERNET_STATE_ENABLED && isChecked) {
            if (mEthManager.isEthernetConfigured() != true) {

            } else {
                mEthManager.setEthernetEnabled(isChecked);
            }
        } else {
            mEthManager.setEthernetEnabled(isChecked);
        }
        mEthManager.enableEthernet(isChecked);
        isEthernetOn = isChecked;
        if (isChecked) {
            setViewInvalid(isChecked);
        } else {
            setViewInvalid(isChecked);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
/*            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSwitchCb.isFocused() && mSwitchCb.isChecked()) {
                    etherNetOn(false);
                    mSwitchCb.setChecked(false);
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSwitchCb.isFocused() && !mSwitchCb.isChecked()) {
                    etherNetOn(true);
                    mSwitchCb.setChecked(true);
                }
                break;

            default:
                break;*/
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Open automatically obtain IP address
     */
    private void startDhcp() {
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetDefaultConf();
        mEthManager.setEthernetEnabled(true);
    }

    /**
     * set all view invalid
     *
     * @param isEtherOpen
     */
    protected void setViewInvalid(boolean isEtherOpen) {
        if (isEtherOpen) {
            mAutoIPText.setTextColor(mContext.getResources().getColor(R.color.white));
            mAutoIPLayout.setClickable(true);
            mAutoIPLayout.setFocusable(true);
            mAutoIPLayout.setFocusableInTouchMode(true);
            mManualIPText.setTextColor(mContext.getResources().getColor( R.color.white));
            mManualIPLayout.setClickable(true);
            mManualIPLayout.setFocusable(true);
            mManualIPLayout.setFocusableInTouchMode(true);
            mPPPOEText.setClickable(true);
            mPPPOEText.setFocusable(true);
            mPPPOEText.setFocusableInTouchMode(true);
            mPPPOEText.setTextColor(mContext.getResources().getColor( R.color.white));
            mAutoIPLayout.requestFocus();
        } else {
            mAutoIPText.setTextColor(mContext.getResources().getColor( R.color.grey));
            mAutoIPLayout.setClickable(false);
            mAutoIPLayout.setFocusable(false);
            mAutoIPLayout.setFocusableInTouchMode(false);
            mManualIPText.setTextColor(mContext.getResources().getColor( R.color.grey));
            mManualIPLayout.setClickable(false);
            mManualIPLayout.setFocusable(false);
            mManualIPLayout.setFocusableInTouchMode(false);
            mPPPOEText.setClickable(false);
            mPPPOEText.setFocusable(false);
            mPPPOEText.setFocusableInTouchMode(false);
            mPPPOEText.setTextColor(mContext.getResources().getColor( R.color.grey));
			 mSwitchCb.requestFocus();           
        }
    }
    
    public void TipsPostDelay() {
    	tipsHandler.removeCallbacks(tipsRunnable);
    	tipsHandler.postDelayed(tipsRunnable, TIPS_TIOME_OUT);
    }

    /**
     * Broadcast of Ethernet
     */
    private final BroadcastReceiver mEthSettingsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int message = -1;
            int rel = -1;
            ContentResolver resolver = mContext.getContentResolver();
            if (intent.getAction().equals(
                    EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                mAutoIPText.setEnabled(isEthernetOn);
                mManualIPText.setEnabled(isEthernetOn);
                mPPPOEText.setEnabled(isEthernetOn);
                message = intent.getIntExtra(
                        EthernetManager.EXTRA_ETHERNET_STATE, rel);
                if (Constant.LOG_TAG) {
                    Log.i(TAG, "onReceive-->message" + message);
                }
                switch (message) {
                // Dynamic IP connection is successful event
                    case EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED:
                        mAutoSelectText.setVisibility(View.VISIBLE);
                        mManualSelect.setVisibility(View.GONE);

                        Message msg0 = mHandler.obtainMessage();
                        msg0.what = NetStateDialog.CONNECT_SUCCESSED;
                        mHandler.sendMessageDelayed(msg0, 200);

                        Message msg = mHandler.obtainMessage();
                        msg.what = NetStateDialog.MSG_CLEAR;
                        mHandler.sendMessageDelayed(msg, 1000);
                        ethernet_current_status = EthernetDataTracker.EVENT_DHCP_CONNECT_SUCCESSED;
                        break;
                    // Event dynamic IP connection failed
                    case EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED:
                        Message msg1 = mHandler.obtainMessage();
                        msg1.what = NetStateDialog.CONNECT_FAILED;
                        mHandler.sendMessageDelayed(msg1, 100);
                        ethernet_current_status = EthernetDataTracker.EVENT_DHCP_CONNECT_FAILED;
                        break;
                    // Dynamic IP disconnect the successful event
                    case EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED:
                        mAutoSelectText.setVisibility(View.GONE);
                        ethernet_current_status = EthernetDataTracker.EVENT_DHCP_DISCONNECT_SUCCESSED;
                        break;
                    // Event dynamic IP open failed
                    case EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED:
                        ethernet_current_status = EthernetDataTracker.EVENT_DHCP_DISCONNECT_FAILED;
                        break;
                    // Static IP connection is successful event
                    case EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED:
                        if (Constant.LOG_TAG) {
                            Log.i(TAG, "EVENT_STATIC_CONNECT_SUCCESSED");
                        }
                        mManualSelect.setVisibility(View.VISIBLE);
                        mAutoSelectText.setVisibility(View.GONE);

                        Message msgtem = mHandler.obtainMessage();
                        msgtem.what = NetStateDialog.CONNECT_SUCCESSED;
                        mHandler.sendMessageDelayed(msgtem, 200);

                        Message msg2 = mHandler.obtainMessage();
                        msg2.what = NetStateDialog.MSG_CLEAR;
                        mHandler.sendMessageDelayed(msg2, 1000);
                        ethernet_current_status = EthernetDataTracker.EVENT_STATIC_CONNECT_SUCCESSED;
                        break;
                    // Event of static IP connection failed
                    case EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED:
                        ethernet_current_status = EthernetDataTracker.EVENT_STATIC_CONNECT_FAILED;
                        break;
                    // Static IP disconnect the successful event
                    case EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED:
                        mManualSelect.setVisibility(View.GONE);
                        ethernet_current_status = EthernetDataTracker.EVENT_STATIC_DISCONNECT_SUCCESSED;
                        break;
                    // Static IP disconnection failure events
                    case EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED:
                        ethernet_current_status = EthernetDataTracker.EVENT_STATIC_DISCONNECT_FAILED;
                        break;
                    // The cable connections on the event
                    case EthernetDataTracker.EVENT_PHY_LINK_UP:
                        mAutoIPText.setEnabled(true);
                        mManualIPText.setEnabled(true);
                        mPPPOEText.setEnabled(true);
                       // Toast.makeText(mContext, R.string.ethernet_link_up, Toast.LENGTH_LONG).show();
                        ethernet_current_status = EthernetDataTracker.EVENT_PHY_LINK_UP;
                        tips_status_new = ethernet_current_status;
                        TipsPostDelay();
                        break;
                    // Cable disconnect events
                    case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
                        mAutoIPText.setEnabled(false);
                        mManualIPText.setEnabled(false);
                        mPPPOEText.setEnabled(false);
                        cleanIP();
                        mManualSelect.setVisibility(View.GONE);
                        mAutoSelectText.setVisibility(View.GONE);
                        
                        Message msg3 = mHandler.obtainMessage();
                        msg3.what = NetStateDialog.MSG_CLEAR;
                        mHandler.sendMessageDelayed(msg3, 100);
                        
                        //Toast.makeText(mContext, R.string.ethernet_link_down, Toast.LENGTH_SHORT).show();
                        ethernet_current_status = EthernetDataTracker.EVENT_PHY_LINK_DOWN;
                        tips_status_new  = ethernet_current_status;
                        TipsPostDelay();
                        break;
                    default:
                        break;
                }
            } else {
                // PPPOE State change notification
                if (intent.getAction().equals(
                        PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
                    message = intent.getIntExtra(
                            PppoeManager.EXTRA_PPPOE_STATE, rel);
                    switch (message) {
                    // Event broadband connection is successful
                        case PppoeStateTracker.EVENT_PPPOE_CONNECT_SUCCESSED:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_CONNECT_SUCCESSED;
                            break;
                        // Broadband connectivity failure events
                        case PppoeStateTracker.EVENT_PPPOE_CONNECT_FAILED:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_CONNECT_FAILED;
                            break;
                        // Broadband disconnect successful event
                        case PppoeStateTracker.EVENT_PPPOE_DISCONNECT_SUCCESSED:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_DISCONNECT_SUCCESSED;
                            break;
                        // Broadband disconnection failure events
                        case PppoeStateTracker.EVENT_PPPOE_DISCONNECT_FAILED:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_DISCONNECT_FAILED;
                            break;
                        // Broadband connections in the event
                        case PppoeStateTracker.EVENT_PPPOE_CONNECTING:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_CONNECTING;
                            break;
                        // Broadband connection failed, password error event
                        case PppoeStateTracker.EVENT_PPPOE_CONNECT_FAILED_WRONG_PASSWORD:
                            pppoe_current_status = PppoeStateTracker.EVENT_PPPOE_CONNECT_FAILED_WRONG_PASSWORD;
                            break;
                        // Cable connection events
                        case PppoeStateTracker.EVENT_PHY_LINK_UP:
                            pppoe_current_status = PppoeStateTracker.EVENT_PHY_LINK_UP;
                            break;
                        // Cable disconnect events
                        case PppoeStateTracker.EVENT_PHY_LINK_DOWN:
                            pppoe_current_status = PppoeStateTracker.EVENT_PHY_LINK_DOWN;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };

    /**
     * make the child dialog shutdown
     */
    public void dismissChildDialog() {
        if (mEtherDialog != null && mEtherDialog.isShowing()) {
            mEtherDialog.dismiss();
        }
        if (null != mEtherSetDialog && mEtherSetDialog.isShowing()) {
            mEtherSetDialog.dismiss();
        }
        if (null != netStateDialog && netStateDialog.isShowing()) {
            netStateDialog.dismiss();
        }
        if (null != etherInputDialog && etherInputDialog.isShowing()) {
            etherInputDialog.dismiss();
        }
    }

    public void onStop() {
        mContext.unregisterReceiver(mEthSettingsReceiver);
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
    }

    public static void cleanIP() {
        DhcpIP = "";
        DhcpGatewary = "";
        DhcpMask = "";
        DhcpDns = "";
    }
}
