package com.um.ca;

import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvb.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.ui.Dvbplayer_Activity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.unionman.jazzlib.*;

public class DvtAutoFeedCardReceiver extends BroadcastReceiver{
	private final String TAG = DvtAutoFeedCardReceiver.class.getSimpleName();
	static LinearLayout FeedCardLayout;
	static TextView autoFeedCardTip;
	private int motherCardId;
	private int isMotherCard;
	private static boolean autoFeedFlag = false;
	private static int readMotherFlag = -1;
	private Context mContext;
	private static byte [] feedData = new byte [1024];
	private static int [] dataLen = new int [1];
	private Handler mHandler = new Handler();
	private Activity mInstance = null;
	public DvtAutoFeedCardReceiver(Activity activity) {
		mInstance = activity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!DVB.isServerAlive()) {
			return;
		}
		mContext = context;
		// TODO Auto-generated method stub
		if (intent.getAction().equals("com.um.dvb.DVTCA_AUTO_FEED_CARD")){
			Log.i(TAG, "com.um.dvb.DVTCA_AUTO_FEED_CARD");
			startFeedCard();
		}else if (intent.getAction().equals("com.um.dvb.STOP_AUTO_FEED_CARD")){
			Log.i(TAG, "com.um.dvb.STOP_AUTO_FEED_CARD");
			stopFeedCard();
		}else if(intent.getAction().equals("com.um.dvb.DVTCA_SMC_IN")){
			Log.i(TAG, "com.um.dvb.DVTCA_SMC_IN");
			Log.i(TAG, "autoFeedFlag:" +autoFeedFlag);
			
			if(true == autoFeedFlag){
				Log.i("ljs autoFeedCardProcess", "readMotherFlag"+readMotherFlag);	
				autoFeedCardProcess();
			}
		}else if(intent.getAction().equals("com.um.dvb.DVTCA_SMC_OUT")){
			Log.i(TAG, "com.um.dvb.DVTCA_SMC_OUT");
		}
	}
	
    private void startFeedCard(){
    	if(null == mInstance ){
    		Log.i(TAG, "notFullScrenplay: return");
    		return;
    	}
    	if(true == autoFeedFlag){
    		Log.i(TAG, "Auto Feeding, return");
    		return;
    	}
    	
		Log.i("ljs startFeedCard", "startFeedCard");

    	Dvbplayer_Activity.setAutoFeedCardFlag(true);
        LinearLayout blankFeedCardLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
        FeedCardLayout = (LinearLayout) LayoutInflater.from(mInstance).inflate(R.layout.dvt_auto_feed_card, null);
        blankFeedCardLayout.removeAllViews();
        blankFeedCardLayout.addView(FeedCardLayout);
        autoFeedCardTip = (TextView) FeedCardLayout.findViewById(R.id.autoFeedCardTip);

    	autoFeedCardTip.setText(R.string.auto_feedcard_insert_mother_card);
        
        autoFeedFlag = true;
        SystemProperties.set("runtime.unionman.disexback", "1");//只响应返回键
    }
    
    private void stopFeedCard(){
    	if(null == mInstance ){
    		Log.i(TAG, "notFullScrenplay: return");
    		return;
    	}
    	Dvbplayer_Activity.setAutoFeedCardFlag(false);
    	LinearLayout blankFeedCardLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
    	blankFeedCardLayout.removeAllViews();
        
    	readMotherFlag = -1;
        autoFeedFlag = false;
        SystemProperties.set("runtime.unionman.disexback", "0");//所有按键恢复正常
        
        Ca ca = new Ca(DVB.getInstance());
        ca.CaRestoreMsgSend();
    }
    
    private void autoFeedCardProcess(){
    	int ret = -1;
    	boolean getCardInfo = false;
        final Ca ca = new Ca(DVB.getInstance());
        
    	if(getCardStatus() != true){
    		Log.d(TAG, "the card is out");
    		return;
    	}
	    	
    	getCardInfo = getMotherCardInfo();//获取子母卡信息
    	Log.i(TAG, "getCardInfo:"+getCardInfo +", readMotherFlag:" +readMotherFlag);
    	
		if(getCardInfo != true){ 	 //读取子母卡信息失败
			if(readMotherFlag == 0){
				Log.i("ljs getMotherCardInfo", "readMotherFlag == 0");
				autoFeedCardTip.setText(R.string.get_mother_card_info_fail);  //获取母卡信息失败，请插入要配对的母卡
			}
            else {
				Log.i("ljs getMotherCardInfo", "feed_fail_insert_child_card");
            	autoFeedCardTip.setText(R.string.feed_fail_insert_child_card);  //配对失败，请插入要配对的子卡
            }
			return;
		}		
		Log.i(TAG, "motherCardId:" +motherCardId +", readMotherFlag:"+readMotherFlag);
		
		if(motherCardId != 0){	 //如果当前是子卡
			if(readMotherFlag == 1) { 	 //成功读取过母卡数据
				Log.i("ljs CaWriteFeeddataToChild", "write_mother_card_info");
				autoFeedCardTip.setText(R.string.write_mother_card_info); //正在写入母卡信息，请稍等
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//把读取到的母卡信息写入到子卡
						Log.i("ljs CaWriteFeeddataToChild", "dataLen:"+dataLen[0]);
					 	System.out.format("ljs CaWriteFeeddataToChild:0x%x, 0x%x, 0x%x, 0x%x\n", feedData[0], feedData[1],feedData[2],feedData[3]);
					 	System.out.format("ljs CaWriteFeeddataToChild:0x%x, 0x%x, 0x%x, 0x%x\n", feedData[4], feedData[5],feedData[6],feedData[7]);
						
						int ret = ca.CaWriteFeeddataToChild(0, feedData, dataLen[0]);
						Log.i("ljs CaWriteFeeddataToChild", "ret:"+ret);

		                if(ret != 0){
		                	autoFeedCardTip.setText(R.string.feed_fail_insert_child_card);    //配对失败，请插入要配对的子卡
						}
						else{
							autoFeedCardTip.setText(R.string.feed_card_success); //恭喜，配对成功，请插入其他要配对的子卡		
						}			
					}
				}, 2000);//延时2s后执行
				
			}
			else {  	 //没有成功读取过母卡数据
				Log.i("ljs CaWriteFeeddataToChild", "get_mother_card_info_fail");
				autoFeedCardTip.setText(R.string.get_mother_card_info_fail);   //获取母卡信息失败，请插入要配对的母卡
			}				
		}	

		else{  	 //当前为母卡
			if(readMotherFlag == 1){      //成功读取过母卡数据
				Log.i("ljs CaReadFeeddataFromParent", "feed_fail_insert_child_card");
				autoFeedCardTip.setText(R.string.feed_fail_insert_child_card);    //配对失败，请插入要配对的子卡
			}
			else{		//没有成功读取过母卡数据
				Log.i("ljs CaReadFeeddataFromParent", "get_mother_card_info");
				autoFeedCardTip.setText(R.string.get_mother_card_info); 	//正在读取母卡信息，请稍等
				
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//读取母卡数据
						int ret = ca.CaReadFeeddataFromParent(0, feedData, dataLen);
						Log.i("ljs CaReadFeeddataFromParent", "ret:"+ret);
						Log.i("ljs CaReadFeeddataFromParent", "dataLen:"+dataLen[0]);	
					 	System.out.format("ljs CaReadFeeddataFromParent:0x%x, 0x%x, 0x%x, 0x%x\n", feedData[0], feedData[1],feedData[2],feedData[3]);
					 	System.out.format("ljs CaReadFeeddataFromParent:0x%x, 0x%x, 0x%x, 0x%x\n", feedData[4], feedData[5],feedData[6],feedData[7]);

			      	    if(ret != 0){
							autoFeedCardTip.setText(R.string.get_mother_card_info_fail); 	  //获取母卡信息失败，请插入要配对的母卡
							readMotherFlag = 0;
						}
						else{
							autoFeedCardTip.setText(R.string.get_mother_card_info_success); //成功获取母卡信息，请插入要配对的子卡
							readMotherFlag = 1;
						}
					}
				}, 2000);//延时2s后执行
			}
		}
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d(TAG, "ret:" +ret);
    	Log.d(TAG, "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
    
	private boolean getMotherCardInfo()
	{
		int i = 0;
		int ret = 0;
        int []motherinfo_len = {80};
        byte []motherinfo = new byte[motherinfo_len[0]];
		
		final Ca ca = new Ca(DVB.getInstance());
		
		for (i = 0; i < 3; i++)
		{
	    	//延时2s
	    	try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    	
	        ret = ca.CaGetMotherInfo(0, motherinfo, motherinfo_len);
			if ((ret == 0)&&(motherinfo_len[0] != 0))
			{
				Log.i(TAG,"CaGetMotherInfo,success");
				String strJson = new String(motherinfo, 0, motherinfo_len[0]);
				if(parseCardInfoJson(strJson) == 0){
					break;
				}else{
					continue;
				}
			}
			else
			{
				Log.i(TAG,"CaGetMotherInfo,fail");	
				continue;							
			}
		}		
		
		if(3 == i){
			return false;
		}
		return true;
	}
	
	private int parseCardInfoJson(String jsonStr){
	   	 int ret = 0;
		 System.out.println("jsonStr:"+jsonStr); //输出字符串
		 
		 try {
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	motherCardId = jsonObject.getInt("mother_card_id");
		  	isMotherCard = jsonObject.getInt("ismother");
		  	Log.i(TAG, "motherCardId: "+motherCardId);
		  	Log.i(TAG, "isMotherCard: "+isMotherCard);
		
		} catch (JSONException ex) {  
			System.out.println("get JSONObject fail");// 异常处理代码  
			ret = -1;
		}
	   	return ret;
	   	
	   }
	
}
