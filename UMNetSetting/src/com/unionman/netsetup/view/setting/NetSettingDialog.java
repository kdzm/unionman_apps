
package com.unionman.netsetup.view.setting;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.zip.Inflater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ethernet.EthernetManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.unionman.netsetup.NetTestActivity;
import com.unionman.netsetup.R;
import com.unionman.netsetup.logic.factory.InterfaceLogic;
import com.unionman.netsetup.logic.factory.LogicFactory;
import com.unionman.netsetup.util.Constant;

/**
 * Network settings window contents according to the change of mFlag dialog
 *
 * @author huyq
 */
public class NetSettingDialog extends Dialog {

	private static String TAG = "NetSettingDialog";
    private Context mContext;
    private int mFlag;
    private LogicFactory mLogicFactory = null;
    private InterfaceLogic mInterfaceLogic;
    private Dialog netTestDialog;
    public static final int NET_STATE = 11;
    public static final int SYSTEM_LOCAL = 12;

    public final static int FLAG_NET = 1;
    public final static int FLAG_ETHER = 2;
    public final static int FLAG_WIFI = 3;
    public final static int FLAG_STATE = 4;
    public final static int FLAG_TEST = 5;
    public final static int FLAG_AP = 14;
    public final static int SYSTEM_UPDATE = 6;
    public final static int SYSTEM_LOCAL_UPDATE = 7;
    public final static int NET_NO_UPDATE = 8;
    public final static int SELECT_UPDATE_VERSION= 9;
    public final static int TEST_GW_SUCCESS = 10;
    public final static int TEST_GW_FAIL = 11;
    public final static int TEST_DNS_SUCCESS = 12;
    public final static int TEST_DNS_FAIL = 13;
    // dialogs in the ui
    private EtherSetting mEtherSetting = null;
    private WifiSetting mWifiSetting = null;;
    private CustomSettingView mCustomSettingView;
    private CreateAPView mCreateAPView;
/*    private SystemUpdateView mSystemUpDateView;
    private SelectUpdateVersion selectupdateversion;*/
    // Control NetSettingDialog display content
    private View neTestView;
	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	if (msg.what==FLAG_NET) {

        	} else if (msg.what==TEST_GW_FAIL) {
        		//netTestDialog.dismiss();	
				Toast	 toast = Toast.makeText(mContext,
						mContext.getResources().getString(R.string.test_gw_fail), Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				  // netTestDialog.dismiss();
        	} else if (msg.what==TEST_DNS_FAIL) {

				Toast	 toast = Toast.makeText(mContext,
						mContext.getResources().getString(R.string.test_dns_fail), Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
				   
			
        	} else if (msg.what==TEST_DNS_SUCCESS) {
        		//netTestDialog.dismiss();
        		
				Toast	 toast = Toast.makeText(mContext,
						mContext.getResources().getString(R.string.test_net_success), Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();    		
        	}
        	else {
        		setContent(msg.what, 0);
        	}
        }
    };

    public EtherSetting getEtherSettingDialog() {
        return mEtherSetting;
    }

    public NetSettingDialog(Context context, int flag) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        mFlag = flag;
        mLogicFactory = new LogicFactory(mContext);
        setContent(flag, 0);
        
    	LayoutInflater inflater=LayoutInflater.from(mContext);
    	neTestView=inflater.inflate(R.layout.setting_nettest, null);
    }

    // Focus returned from the three level menu,
    // set the focus of two level menu NetSetting
    private void setContent(int flag, int focus) {
        mFlag = flag;
        switch (flag) {
            case FLAG_NET:
                // netsetting
            	if(mEtherSetting!=null){
            		Animation enterAnimation = AnimationUtils.loadAnimation(mContext,
                            R.anim.dia_fade_out);
            		mEtherSetting.setAnimation(enterAnimation);
                    mEtherSetting.startAnimation(enterAnimation);
            	}
            	
            	if(mWifiSetting!=null){
            		Animation enterAnimation = AnimationUtils.loadAnimation(mContext,
                            R.anim.dia_fade_out);
            		mWifiSetting.setAnimation(enterAnimation);
            		mWifiSetting.startAnimation(enterAnimation);
            	}
            	
            	if(mCustomSettingView!=null){
            		Animation enterAnimation = AnimationUtils.loadAnimation(mContext,
                            R.anim.dia_fade_out);
            		mCustomSettingView.setAnimation(enterAnimation);
            		mCustomSettingView.startAnimation(enterAnimation);
            	}
            	
            	if(mCreateAPView!=null){
            		Animation enterAnimation = AnimationUtils.loadAnimation(mContext,
                            R.anim.dia_fade_out);
            		mCreateAPView.setAnimation(enterAnimation);
            		mCreateAPView.startAnimation(enterAnimation);
            	}
            	
            	NetSetting netSettingNet=new NetSetting(mContext, mHandler, focus);
                setContentView(netSettingNet);
                netSettingNet.setAnimation(AnimationUtils.loadAnimation(mContext,
                		R.anim.dia_fade_in));
                break;
            case FLAG_ETHER:
                // EtherSetting
            	if(mEtherSetting==null)
            	{
	                mEtherSetting = new EtherSetting(mContext, mHandler);
            	}
            	else
            	{
            		mEtherSetting.EtherSettingRequestFocus();	
            	}
                setContentView(mEtherSetting);
                mEtherSetting.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.dia_fade_in));
                break;
            case FLAG_WIFI:
                // WifiSetting
            	if(mWifiSetting==null)
            	{
	                mWifiSetting = new WifiSetting(mContext, mHandler);
            	}
            	else
            	{
            		mWifiSetting.WifiSettingRequestFocus();	
            	}
                setContentView(mWifiSetting);
                mWifiSetting.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.dia_fade_in));
                break;
            case FLAG_STATE:
                // net state
            	
            	mCustomSettingView=new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.net_state_setting),
                        mLogicFactory.createLogic(NET_STATE));
                setContentView(mCustomSettingView);
                mCustomSettingView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.dia_fade_in));
                break;
            case FLAG_TEST:
            	NetTest();

            	break;
           /* case SYSTEM_UPDATE:
                // System UpDate
                mSystemUpDateView = new SystemUpdateView(mContext, mHandler, focus);
                setContentView(mSystemUpDateView);
                break;*/
            
           /* case SYSTEM_LOCAL_UPDATE:
                // Local update
                setContentView(new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.system_update_local),
                        mLogicFactory.createLogic(SYSTEM_LOCAL)));

                mInterfaceLogic = mLogicFactory.createLogic(SYSTEM_LOCAL);
                mCustomSettingView = new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.system_update_local),
                        mInterfaceLogic);
                setContentView(mCustomSettingView);
                break;*/
           /* case SELECT_UPDATE_VERSION:
                selectupdateversion = new SelectUpdateVersion(mContext);
                 setContentView(selectupdateversion);
                break;*/
            case FLAG_AP:
            	// 20160627,add.
            	mCreateAPView = new CreateAPView(mContext, this);
            	setContentView(mCreateAPView);
            	mCreateAPView.setAnimation(AnimationUtils.loadAnimation(mContext,
            			R.anim.dia_fade_in));
            	break;
            default:
                break;
        }
        
       
    }

    @Override
    public void onBackPressed() {
        switch (mFlag) {
            case FLAG_NET:
                super.onBackPressed();
                return;
            case FLAG_ETHER:
            	
            	
                setContent(FLAG_NET, 0);
                return;
            case FLAG_WIFI:
            	
                setContent(FLAG_NET, 1);
                return;
            case FLAG_STATE:
                setContent(FLAG_NET, 2);
                return;
            case FLAG_AP:
                setContent(FLAG_NET, 4);
                return;
            case FLAG_TEST:
                setContent(FLAG_NET, 3);
            case SYSTEM_UPDATE:
                super.onBackPressed();
                return;
            case SYSTEM_LOCAL_UPDATE:
                setContent(SYSTEM_UPDATE, 0);
                return;
            case SELECT_UPDATE_VERSION:
                setContent(SYSTEM_UPDATE, 0);
                 return;
            default:
                break;
        }
        super.onBackPressed();
    }

    @Override
    public void dismiss() {
        if (null != mEtherSetting) {
            mEtherSetting.dismissChildDialog();
        }
        if (null != mWifiSetting) {
            mWifiSetting.dismissChildDialog();
        }
       /* if (null != selectupdateversion) {
            selectupdateversion.dismissChildDialog();
        }*/
        if (null != mInterfaceLogic) {
            mInterfaceLogic.dismissDialog();
        }
        super.dismiss();
    }

    @Override
    protected void onStop() {
        if (mEtherSetting != null) {
            mEtherSetting.onStop();
            mEtherSetting = null;
        }
		
		if (mWifiSetting != null) {
            mWifiSetting.onStop();
            mWifiSetting = null;
        }
        super.onStop();
    }
    
    private class NetTestThread extends Thread {
        public void run() {  
        	Message msg = new Message();
        	if (GWTest()) {
        		msg.what = TEST_GW_SUCCESS;
        		if (DNSTest()) {
        			msg.what = TEST_DNS_SUCCESS;
//        			//锟斤拷锟皆成癸拷
        		} else {
//        			//锟斤拷夭锟斤拷猿晒锟斤拷锟紻NS锟斤拷锟斤拷失锟斤拷
        			msg.what = TEST_DNS_FAIL;
        		}
        	} else {
    	    	   msg.what = TEST_GW_FAIL;
        	}
        	
        	mHandler.sendMessage(msg);
        }  
    }
    
    private void NetTest(){
    
	    Intent intent=new Intent();
	    intent.setClass(mContext,NetTestActivity.class);
	    mContext.startActivity(intent);   
	   /* final IndexThread thread = new IndexThread();
	    thread.start();*/

	   // new NetTestThread().start();
    }
    
    private boolean GWTest(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(isEtherNetEnabled()?ConnectivityManager.TYPE_ETHERNET:ConnectivityManager.TYPE_WIFI);
        String GATEWAY = null;
        for (RouteInfo route : linkProperties.getRoutes()) {
            if (route.isDefaultRoute()) {
            	GATEWAY = route.getGateway().getHostAddress();
                break;
            }
        }
        if (GATEWAY!=null&&!"".equals(GATEWAY)) {
        	Log.i(TAG, "ping gateway:"+GATEWAY);
        	return pingHost(GATEWAY);
        } else {
        	Log.e(TAG,"can't get gate way");
        	return false;
        }
    }
    
    private  boolean DNSTest(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectivityManager.getActiveNetworkInfo();
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(isEtherNetEnabled()?ConnectivityManager.TYPE_ETHERNET:ConnectivityManager.TYPE_WIFI);
    	Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
        if (!dnses.hasNext()) {
        	return false;
        } else {
            String DNS = dnses.next().getHostAddress();
            if (null != DNS) {
            	Log.i(TAG,"ping DNS:"+DNS);
            	if (pingHost(DNS)) {
            		return true;
            	} else {
            		if (!dnses.hasNext()) return false;
            		DNS = dnses.next().getHostAddress();
            		Log.i(TAG,"ping DNS again:"+DNS);
            		return pingHost(DNS);
            	}
            }
        }
        
        return false;
    }
    
    /** 
    * wifi锟角凤拷锟� 
    */ 
    public  boolean isEtherNetEnabled() { 
    	ConnectivityManager mConnMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
    	NetworkInfo mEtherNet = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    	return (mEtherNet!=null)&&(mEtherNet.isAvailable())&&(mEtherNet.isConnected());
    }
    
    private static boolean pingHost(String str){ 
    	boolean resault=false; 
        try { 
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 10 " +str); 
            int status = p.waitFor(); 
            if (status == 0) { 
                resault=true; 
            }    
            else 
            { 
                resault=false; 
            } 
            } catch (IOException e) { 
            } catch (InterruptedException e) { 
            } 
         
        return resault; 
    } 
    
    
}
