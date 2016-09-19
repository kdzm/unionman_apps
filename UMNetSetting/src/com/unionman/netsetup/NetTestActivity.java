package com.unionman.netsetup;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NetTestActivity extends Activity{
    /** Called when the activity is first created. */
	private static String TAG = "NetTestActivity";
    public final static int TEST_GW_SUCCESS = 10;
    public final static int TEST_GW_FAIL = 11;
    public final static int TEST_DNS_SUCCESS = 12;
    public final static int TEST_DNS_FAIL = 13;
        
	private int count = 10;
	private int[] imgIDs = {R.id.point1,R.id.point2,R.id.point3,R.id.point4,R.id.point5,R.id.point6,R.id.point7,R.id.point8,R.id.point9,R.id.point10};
	private int[] imgIDsLeft = {R.id.point1,R.id.point2,R.id.point3,R.id.point4,R.id.point5};
	private int[] imgIDsRight = {R.id.point6,R.id.point7,R.id.point8,R.id.point9,R.id.point10};
	
	ImageView imgLeft;
	ImageView imgRight;
	
	ImageView imgTV;
	ImageView imgRouter;
	ImageView imgNet;
	TextView texTip;
	private int INDEX_SELECTED = 0;
	private final int EDIT_TYPE_SELECTED = 1;     //选中的   
	private final int EDIT_TYPE_NO_SELECTED = 2;  //未选中的
	private IndexThread thread = new IndexThread();
	private WifiManager mWifiManager;
	private EthernetManager mEthManager;
	private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.setting_nettest);
        imgLeft =(ImageView)findViewById(R.id.netLeftError);
        imgRight =(ImageView)findViewById(R.id.netRightError);
        imgRouter=(ImageView)findViewById(R.id.router);
        imgNet=(ImageView)findViewById(R.id.net);
        texTip=(TextView)findViewById(R.id.netest_tip);
        imgTV=(ImageView)findViewById(R.id.tv);
        
        mContext=NetTestActivity.this;
        
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        
        if( mWifiManager.isWifiEnabled() || (mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED)){
        	imgTV.setBackgroundResource(R.drawable.netest_tv_enable);
        	thread.start();
            new NetTestThread().start(); 
        } else{
        	texTip.setText(R.string.test_line_fail);
        }
       
        
        
        
    }
    public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("Test","---"+ msg.arg1);
			switch(msg.what)
			{
			case EDIT_TYPE_SELECTED:
				((ImageView)findViewById(msg.arg1)).setBackgroundResource(R.drawable.netest_line_enable);
				break;
			case EDIT_TYPE_NO_SELECTED:
				((ImageView)findViewById(msg.arg1)).setBackgroundResource(R.drawable.netest_line_dis);
				break;
			}
		}
    };
    
    class IndexThread extends Thread
    {
    	boolean flag = true;
    	@Override
	     public void run()
	     {
    		Message msg;
    		while(flag)
    		{
    			for(int i= 0 ; i < count ; i++)
    			{
    				Log.i("Test","---"+ count);
    				msg = new Message();
    				msg.what = EDIT_TYPE_SELECTED;
    				msg.arg1 = imgIDs[i];
    				myHandler.sendMessage(msg);
    			
    				msg = new Message();
    				if(i==0)
    				{
    					msg.what = EDIT_TYPE_NO_SELECTED;
    					msg.arg1 = imgIDs[count-1];
    					myHandler.sendMessage(msg);
    					
    				}
    				else
    				{
    					msg.what = EDIT_TYPE_NO_SELECTED;
    					msg.arg1 = imgIDs[i-1];
    					myHandler.sendMessage(msg);

    				}
    				SystemClock.sleep(500);
    			}
    		}
    		
	     }
    }
    
    private class NetTestThread extends Thread {
        public void run() {  
        	Message msg = new Message();
        	if (GWTest()) {

        		msg.what = TEST_GW_SUCCESS;
        		if (DNSTest()) {
     
        			msg.what = TEST_DNS_SUCCESS;
        		} else {
        	
        			msg.what = TEST_DNS_FAIL;
        		}
        	} else {
    	    	   msg.what = TEST_GW_FAIL;
        	}
        	
        	mHandler.sendMessage(msg);
        }  
    }
    
    private boolean GWTest(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
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
        ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
    
    public  boolean isEtherNetEnabled() { 
    	ConnectivityManager mConnMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
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
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           if (msg.what==TEST_GW_FAIL) {

				thread.flag=false;
				   
				new Handler().postDelayed(new Runnable(){
	                @Override
		                public void run(){
		                	for(int id : imgIDs)
						        ((ImageView)findViewById(id)).setBackgroundResource(R.drawable.netest_line_dis);
		  
		                	imgLeft.setBackgroundResource(R.drawable.netest_error);
		                	//Toast.makeText(NetTestActivity.this,R.string.test_gw_fail, Toast.LENGTH_LONG).show();
		                	texTip.setText(R.string.test_gw_fail);
		                }
					}, 5000); 
				
				
        	} else if (msg.what==TEST_DNS_FAIL) {

				/*Toast	 toast = Toast.makeText(NetTestActivity.this,R.string.test_dns_fail, Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();*/
	
				   thread.flag=false;
				   
					new Handler().postDelayed(new Runnable(){
		                @Override
			                public void run(){
		                		for(int id : imgIDsLeft)
		                			((ImageView)findViewById(id)).setBackgroundResource(R.drawable.netest_line_enable);
		                		
			                	for(int id : imgIDsRight)
							        ((ImageView)findViewById(id)).setBackgroundResource(R.drawable.netest_line_dis);
			                	
			                	imgRouter.setBackgroundResource(R.drawable.netest_router_enable);
			                	imgRight.setBackgroundResource(R.drawable.netest_error);
			                	//Toast.makeText(NetTestActivity.this,R.string.test_dns_fail, Toast.LENGTH_LONG).show();
			                	texTip.setText(R.string.test_dns_fail);
			                }
						}, 5000);  
					
			
        	} else if (msg.what==TEST_DNS_SUCCESS) {

        		thread.flag=false;
				   
				new Handler().postDelayed(new Runnable(){
	                @Override
		                public void run(){
		                	for(int id : imgIDs)
						        ((ImageView)findViewById(id)).setBackgroundResource(R.drawable.netest_line_enable);
		                	
		                	imgRouter.setBackgroundResource(R.drawable.netest_router_enable);
		                	imgNet.setBackgroundResource(R.drawable.netest_net_enable);
		                	//Toast.makeText(NetTestActivity.this,R.string.test_net_success, Toast.LENGTH_LONG).show();
		                	texTip.setText(R.string.test_net_success);
		                }
					}, 5000); 
				   

	
        	}
        	else{
        		
        	}
        }
    };
}
