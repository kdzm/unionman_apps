package com.um.ca;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.dvb.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.Ca.Ca_DetitleIcon;
import com.um.dvbstack.Ca.Ca_EmailIcon;
import com.um.dvbstack.DVB;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.unionman.jazzlib.*;

public class CaShowImgTtxReceiver  extends BroadcastReceiver{
	static final int CDCA_Email_IconHide = 0;
    static final int CDCA_Email_New = 1;
    static final int CDCA_Email_SpaceExhaust = 2;
    static final int DVTCA_Email_No_Room = 3;
    
	static final int CAS_DETITLE_ALL_READED = 0;        /*所有反授权确认码已经被读，隐藏图标*/
	static final int CAS_DETITLE_RECEIVED = 1;        /*收到新的反授权码，显示反授权码图标*/
	static final int  CAS_DETITLE_SPACE_SMALL = 2;    /*反授权码空间不足，改变图标状态提示用户*/
	static final int CAS_DETITLE_IGNORE = 3;        /*收到重复的反授权码，可忽略，不做处理*/
    
	/*tf preview start*/ 
	static final int  CURTAIN_CANCLE = 0;		   /*取消窗帘显示*/
	static final int  CURTAIN_OK = 1;    		   /*窗帘节目正常解密*/
	static final int  CURTAIN_TOTTIME_ERROR = 2;	/*窗帘节目禁止解密：已经达到总观看时长*/
	static final int  CURTAIN_WATCHTIME_ERROR = 3;  /*窗帘节目禁止解密：已经达到WatchTime限制*/
	static final int  CURTAIN_TOTCNT_ERROR = 4; 	 /*窗帘节目禁止解密：已经达到总允许观看次数*/
	static final int  CURTAIN_ROOM_ERROR = 5;	    /*窗帘节目禁止解密：窗帘节目记录空间不足*/
	static final int  CURTAIN_PARAM_ERROR = 6;	    /*窗帘节目禁止解密：节目参数错误*/
	static final int  CURTAIN_TIME_ERROR = 7; 		 /*窗帘节目禁止解密：数据错误*/
	
	private UmImageView mail_icon;
	private UmImageView mail_icon_full;
	private UmImageView deTitle_icon;
	private UmImageView preview_img;
	private UmTextView show_finger;
	private TextView urgencyBroadcast;
	private static Context mConetxt;
	private static int mailFlag = 0;
	private static int fingerShowTick = 0;
	private Handler mHandler = new Handler();
	private Activity mInstance = null;
	public CaShowImgTtxReceiver(Activity activity) {
		mInstance = activity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!DVB.isServerAlive()) {
			return;
		}
		// TODO Auto-generated method stub
		mConetxt = context;
		
		if (null == intent){
			Log.i("CaShowImgTtxReceiver", "intent=null");
			return;
		}
		
		if (null == mInstance){
			Log.i("CaShowImgTtxReceiver", "Dvbplayer_Activity.mInstance=null");
			return;
		}
			
		/*邮件图标*/
		if (intent.getAction().equals("com.um.dvb.CHECK_EMAIL")){
			
			mail_icon = (UmImageView)mInstance.findViewById(R.id.img_email);
			mail_icon_full = (UmImageView)mInstance.findViewById(R.id.img_email_full);
			int mailStatus = getEmailStatus();
			Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,mailStatus:" +mailStatus);
			switch (mailStatus)
			{
				case CDCA_Email_IconHide:
					if (mailFlag == 1)
					{
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,hideView: 1111");
						mailFlag = 0;
						mail_icon_full.hideView();
					}
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,hideView:");
					mail_icon.hideView();
					break;
					
				case CDCA_Email_New:
					if (mailFlag == 1)
					{
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,showView:1111");
						mailFlag = 0;
						mail_icon_full.hideView();
					}
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,showView:");

					mail_icon.showView();
					break;	
					
				case CDCA_Email_SpaceExhaust:
					if (mailFlag == 1)
					{
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,startFlashView:1111");
						mailFlag = 0;
						mail_icon_full.hideView();
					}
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,startFlashView:");

					mail_icon.startFlashView();
					break;
				case DVTCA_Email_No_Room:
					mail_icon.hideView();
					mailFlag = 1;
					mail_icon_full.showView();
					Log.i("onReceive", "com.um.dvb.CHECK_EMAIL,mail_icon_full:");
					break;
				default:
					break;
			}
		}
		//add by unionman
			
			if (intent.getAction().equals("com.um.dvb.SHOW_EMAIL_OR_NOT")) {
				Log.i("onReceive", "com.um.dvb.SHOW_EMAIL_OR_NOT");
				int i = 0;
				try {
					i = adjustNotReadCount();
				} catch (UnsupportedEncodingException e) {

				}
				mail_icon = (UmImageView)mInstance.findViewById(R.id.img_email);
				if (i == 0) {
					Log.i("onReceive", "com.um.dvb.SHOW_EMAIL_OR_NOT,hideView:");
					mail_icon.hideView();
				}else {
					mail_icon.showView();
				}
			}
		
			
		//add by end
		
		/*反授权图标*/
		 if (intent.getAction().equals("com.um.dvb.CHECK_DETITLE")){
			
			deTitle_icon = (UmImageView)mInstance.findViewById(R.id.img_detitle);
			int detitleStatus = getDetitleStatus();
			Log.i("onReceive", "com.um.dvb.CHECK_DETITLE,detitleStatus:" +detitleStatus);
			
			switch(detitleStatus){
				case CAS_DETITLE_ALL_READED:
					deTitle_icon.hideView();
					break;
				
				case CAS_DETITLE_RECEIVED:
					deTitle_icon.showView();
					break;
				
				case CAS_DETITLE_SPACE_SMALL:
					deTitle_icon.startFlashView();
					break;
				
				case CAS_DETITLE_IGNORE:
					break;	
				
				default:
					break;
			}
		}
			
		/*指纹*/
		else if (intent.getAction().equals("com.um.dvb.CHECK_FINGER")){
			
			show_finger = (UmTextView)mInstance.findViewById(R.id.txt_finger);
			int fingerId = getCAFingerId();
			Log.i("onReceive", "com.um.dvb.CHECK_FINGER:" +fingerId);
			if(0 != fingerId){
				show_finger.setText(String.valueOf(fingerId));
				show_finger.setVisibility(View.VISIBLE);
				show_finger.moveToRand(1);
			}
			else{
				show_finger.setVisibility(View.INVISIBLE);
				show_finger.endMoveToRand();
			}		
		}
		else if (intent.getAction().equals("com.um.dvb.DVTCHECK_FINGER")){
			show_finger = (UmTextView)mInstance.findViewById(R.id.txt_finger);
			int fingerId = getCAFingerId();
			int duration = getCAFingerDuration();
			Log.i("onReceive", "com.um.dvb.DVTCHECK_FINGER,fingerId:" +fingerId);
			Log.i("onReceive", "com.um.dvb.DVTCHECK_FINGER,duration:" +duration);
			if(0 != fingerId){
				show_finger.setText(String.valueOf(fingerId));
				show_finger.setBackgroundColor(android.graphics.Color.LTGRAY);
				show_finger.setTextColor(android.graphics.Color.BLACK);
				show_finger.setVisibility(View.VISIBLE);
				show_finger.moveToRand(2);
				
				fingerShowTick = (int)System.currentTimeMillis()/1000 + duration ;
				Log.i("onReceive", "fingerShowTick:" +fingerShowTick);
				SystemProperties.set("runtime.um.dvtFingerShowTick",  Integer.toString(fingerShowTick));
			
				mHandler.removeCallbacks(hideFingerRunnable);
				mHandler.postDelayed(hideFingerRunnable, duration*1000);//显示duration秒后清除指纹
			
			}
			else{
				show_finger.setVisibility(View.INVISIBLE);
				show_finger.endMoveToRand();
			}		
		}
		
		/*免费预览*/
		else if (intent.getAction().equals("com.um.dvb.CHECK_PREVIEW")){
			
			preview_img = (UmImageView)mInstance.findViewById(R.id.img_preview);
			int previewCode = getPreviewCode();
			Log.i("onReceive", "com.um.dvb.CHECK_PREVIEW,previewCode:" +previewCode);
			
			switch(previewCode)
			{
				case CURTAIN_CANCLE:
					preview_img.hideView();
					break;

				case CURTAIN_OK:
					preview_img.setImageResource(R.drawable.preview_entitle);
					preview_img.showView();
					break;

				case CURTAIN_TOTTIME_ERROR:
				case CURTAIN_WATCHTIME_ERROR:	
				case CURTAIN_TOTCNT_ERROR:
				case CURTAIN_ROOM_ERROR:
				case CURTAIN_PARAM_ERROR:
				case CURTAIN_TIME_ERROR:
					preview_img.setImageResource(R.drawable.preview_detitle);
					preview_img.showView();
					break;
					
				default:
					break;
			}
		}
		else if (intent.getAction().equals("com.um.dvb.DVTCHECK_PREVIEW")){
			
			preview_img = (UmImageView)mInstance.findViewById(R.id.img_dvt_preview);
			Log.i("onReceive", "com.um.dvb.DVTCHECK_PREVIEW");
			preview_img.setImageResource(R.drawable.ca_dvt_preview);
			preview_img.showView();
		}
		else if (intent.getAction().equals("com.um.dvb.DVTCLOSE_PREVIEW")){
			Log.i("onReceive", "com.um.dvb.DVTCLOSE_PREVIEW");
			preview_img = (UmImageView)mInstance.findViewById(R.id.img_dvt_preview);
			preview_img.hideView();
		}
		else if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE")){
			Log.i("onReceive", "com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE");
			urgencyBroadcast = (TextView)mInstance.findViewById(R.id.tv_urgency_broadcast);
			urgencyBroadcast.setVisibility(View.VISIBLE);
		}
		else if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE")){
			Log.i("onReceive", "com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
			urgencyBroadcast = (TextView)mInstance.findViewById(R.id.tv_urgency_broadcast);
			urgencyBroadcast.setVisibility(View.INVISIBLE);
		} 
		
	}
	
	public int adjustNotReadCount() throws UnsupportedEncodingException {
		int email_cn = 0;
		int[] buff_len = { 204800 };
		byte[] buff = new byte[buff_len[0]];
		int i;
		int readcount = 0;

		/* Get the buffer */
		Ca ca = new Ca(DVB.getInstance());
		int ret = ca.CaGetEmailheads(buff, buff_len);
		Log.i("onReceive", "adjustNotReadCount ---ret=" + ret +"   buff_len[0]==" + buff_len[0]);
		if ((0 == ret) && (buff_len[0] != 0)) {
			try {
				String jsonStr = new String(buff, 0, buff_len[0], "gb2312");
				JSONObject jsonObject = new JSONObject(jsonStr);
				email_cn = jsonObject.getInt("email_count");
				JSONArray jsonArrayMailInfo = jsonObject.getJSONArray("mail");
				
				for (i = 0; i < email_cn; i++) {

					/* Readflag */
					Log.i("onReceive", "adjustNotReadCount ---readflag");
					int readflag = (jsonArrayMailInfo.getJSONObject(i)
							.getInt("bReadFlag"));

					if (0 != readflag) {
						readcount = readcount + 1;
					}

				}

			} catch (JSONException ex) {
				System.out.println("get JSONObject fail");// 瀵倸鐖舵径鍕倞娴狅絿鐖�
			}
		}
		Log.i("onReceive", "adjustNotReadCount ---readcount== " + readcount);
		return readcount;
	}

	/*隐藏指纹的Runnable*/	
	Runnable hideFingerRunnable = new Runnable(){
		@Override
		public void run() {
			
			int fingerShowTick = Integer.parseInt(SystemProperties.get("runtime.um.dvtFingerShowTick",  "0"));
		    
			Log.i("CaShowImgTtxReceiver","fingerShowTick:" +fingerShowTick);
			Log.i("CaShowImgTtxReceiver","System.currentTimeMillis()/1000+1:" +(System.currentTimeMillis()/1000+1));
			if(fingerShowTick > (System.currentTimeMillis()/1000 + 1)){//连续来多个指纹时，只关闭最后一个即可
				Log.i("CaShowImgTtxReceiver","a new finger is showing, can not hide");
				return;
			}
			
			Log.i("CaShowImgTtxReceiver", "hideFingerRunnable");
			show_finger.setVisibility(View.INVISIBLE);
			show_finger.endMoveToRand();
			
			SystemProperties.set("runtime.um.dvtFingerShowTick",  Integer.toString(0));
		}
	};
	
	private int getCAFingerId() {
		SharedPreferences stbPreferences = mConetxt.getSharedPreferences("CA_FINGERID",Context.MODE_WORLD_READABLE);
	    int fingerId = stbPreferences.getInt("CA_FINGERID", 0);
		return fingerId;
	}
	
	private int getCAFingerDuration() {
		SharedPreferences stbPreferences = mConetxt.getSharedPreferences("CA_FINGERID",Context.MODE_WORLD_READABLE);
	    int duration = stbPreferences.getInt("CA_DURATION", 0);
		return duration;
	}
	
	private int getEmailStatus() {
		Ca ca = new Ca(DVB.getInstance());
		Ca_EmailIcon ca_emailIcon = new Ca_EmailIcon();
		
		ca.CaGetEmailIcon(ca_emailIcon);
		Log.i("checkCaIconStatus","CaGetEmailIcon:" +ca_emailIcon.caEmailIcon[0]);

		return ca_emailIcon.caEmailIcon[0];
	}
	
	private int getDetitleStatus() {
		Ca ca = new Ca(DVB.getInstance());
		Ca_DetitleIcon ca_detitleIcon = new Ca_DetitleIcon();
		
		ca.CaGetDetitleIcon(ca_detitleIcon);
		Log.i("checkCaIconStatus","CaGetDetitleIcon:" +ca_detitleIcon.caDetitleIcon[0]);

		return ca_detitleIcon.caDetitleIcon[0];
	}
	
	private int getPreviewCode() {
		SharedPreferences stbPreferences = mConetxt.getSharedPreferences("CA_PREVIEWCODE",Context.MODE_WORLD_READABLE);
	    int previewStatus = stbPreferences.getInt("CA_PREVIEWCODE", 0);
		return previewStatus;
	}
}

class UmImageView extends ImageView {

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0)
            {
                if (isShown())
                {
                    hideView();
                }else
                {
                    showView();
                }
                handler.sendEmptyMessageDelayed(0,1000);
            }
        }
    };

    public UmImageView(Context context) {
        super(context);
    }

    public UmImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UmImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 显示图标
     */
    public void showView() {
    	Log.i("CaShowImg", "create Email logo");
    	endFlashView();
    	bringToFront();
        setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏图标
     */
    public void hideView() {
    	endFlashView();
    	setVisibility(View.GONE);
    }

    /**
     * 开始闪烁图标
     */
    public void startFlashView() {
        endFlashView();
        showView();
        handler.sendEmptyMessageDelayed(0,1000);
    }

    /**
     * 结束闪烁图标
     */
    public void endFlashView()
    {
        handler.removeMessages(0);
    }

}

class UmTextView extends TextView {

    private Random randomX = new Random(1280);
    private Random randomY = new Random(720);
	static int catype = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 1) {
	                moveToRand(catype);
            }
        }
    };

    public UmTextView(Context context) {
        super(context);
    }

    public UmTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UmTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 随机显示指纹
     */
    public void moveToRand(int type) {
        endMoveToRand();
		if (type == 1)
			{
			catype = 1;
	        makeTranslateAnimation();
		}
		else if (type == 2)
			{
			catype = 2;
			dvtMakeTranslateAnimation();
		}
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    /**
     * 隐藏指纹
     */
    public void endMoveToRand() {
        handler.removeMessages(1);
    }

    public void makeTranslateAnimation() {
        int width = getWidth();
        int height = getHeight();

        int desX = randomX.nextInt(1280 - width);
        int desY = randomY.nextInt(720 - height);
        Log.i("UmTextView" , "desX:" + desX + "desY:" + desY + "width:" + width + "height:" + height);
        flyWhiteBorder(0,0,desX,desY);
    }

    public void dvtMakeTranslateAnimation() {
        int width = getWidth();
        int height = getHeight();

		int desX = randomX.nextInt(1280 - width);
        int desY = 72;
        Log.i("UmTextView" , "desX:" + desX + "desY:" + desY + "width:" + width + "height:" + height);
        flyWhiteBorder(0,0,desX,desY);
    }

    private void flyWhiteBorder(int paramInt1, int paramInt2,
                                float paramFloat1, float paramFloat2) {
            ViewPropertyAnimator localViewPropertyAnimator = this
                    .animate();
            localViewPropertyAnimator.setDuration(150L);
            localViewPropertyAnimator.x(paramFloat1);
            localViewPropertyAnimator.y(paramFloat2);
            localViewPropertyAnimator.start();
    }
}

