package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.um.tfca.R;
import com.um.ui.Tf_updatebar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Prompt
{
	private final String TAG = new String("Tfca-Status");

    static final int STATUS_TUNER	= 1;
    static final int STATUS_CA		= 4;
    static final int STATUS_PLAY	= 3;

	static final int CDCA_Email_IconHide = 0; 
	static final int CDCA_Email_New =  1; 
	static final int CDCA_Email_SpaceExhaust = 2; 
	
	public enum subtype
    {
        UMSG_DVB_NULL,
        /*tuner signal*/
        UMSG_DVB_SIGNAL_CHANGE,
        UMSG_DVB_NO_SIGNAL,
        UMSG_DVB_WEAK_SIGNAL,
        UMSG_DVB_STRONG_SIGNAL,
        /*avplay*/
        UMSG_DVB_AVPLAY_STOP,
        UMSG_DVB_AVPLAY_START,
        /*db*/
        UMSG_DVB_DB_NIT_VERSION_CHANGE,
        UMSG_DVB_DB_PROGRAM_NAME_CHANGE,
        UMSG_DVB_DB_PMT_PID_CHANGE,
        UMSG_DVB_DB_PROGRAM_BASIC_INFO_CHANGE,
        UMSG_DVB_DB_PROGRAM_DELETE,
        /*srch*/
        UMSG_DVB_SRCH_PROGRESS,
        UMSG_DVB_SRCH_GET_PROG,
        UMSG_DVB_SRCH_GET_TP,
        							/*UMSG_DVB_SRCH_GET_FREQ_SIGNAL_INFO,*/
        /*epg*/
        UMSG_DVB_EPG_PF_EVENTS_UPDATE,
        UMSG_DVB_EPG_SCH_EVENTS_UPDATE,
        /*loader*/
        UMSG_DVB_LOADER_FORCE_UPDATE,
        UMSG_DVB_LOADER_MANUAL_UPDATE,
        UMSG_DVB_LOADER_UPGRADE_PROGRESS,
        UMSG_DVB_LOADER_UPGRADE_ERR,
        UMSG_DVB_LOADER_UPGRADE_SUCCESS,
        /*CA*/
        UMSG_DVB_CA_MESSAGE_CANCEL_TYPE,        
        UMSG_DVB_CA_MESSAGE_BADCARD_TYPE,      
        UMSG_DVB_CA_MESSAGE_EXPICARD_TYPE ,   
        UMSG_DVB_CA_MESSAGE_INSERTCARD_TYPE ,    
        UMSG_DVB_CA_MESSAGE_NOOPER_TYPE ,     
        UMSG_DVB_CA_MESSAGE_BLACKOUT_TYPE ,    
        UMSG_DVB_CA_MESSAGE_OUTWORKTIME_TYPE ,    
        UMSG_DVB_CA_MESSAGE_WATCHLEVEL_TYPE,     
        UMSG_DVB_CA_MESSAGE_PAIRING_TYPE ,     
        UMSG_DVB_CA_MESSAGE_NOENTITLE_TYPE ,   
        UMSG_DVB_CA_MESSAGE_DECRYPTFAIL_TYPE ,     
        UMSG_DVB_CA_MESSAGE_NOMONEY_TYPE ,    
        UMSG_DVB_CA_MESSAGE_ERRREGION_TYPE ,    
        UMSG_DVB_CA_MESSAGE_NEEDFEED_TYPE ,    
        UMSG_DVB_CA_MESSAGE_ERRCARD_TYPE ,      
        UMSG_DVB_CA_MESSAGE_UPDATE_TYPE ,     
        UMSG_DVB_CA_MESSAGE_LOWCARDVER_TYPE ,   
        UMSG_DVB_CA_MESSAGE_VIEWLOCK_TYPE,     
        UMSG_DVB_CA_MESSAGE_MAXRESTART_TYPE,  
        UMSG_DVB_CA_MESSAGE_FREEZE_TYPE,   
        UMSG_DVB_CA_MESSAGE_CALLBACK_TYPE ,  
        UMSG_DVB_CA_MESSAGE_STBLOCKED_TYPE ,   
        UMSG_DVB_CA_MESSAGE_STBFREEZE_TYPE , 
        UMSG_DVB_CA_MESSAGE_CARDTESTSTART_TYPE ,
        UMSG_DVB_CA_MESSAGE_CARDTESTFAILD_TYPE,
        UMSG_DVB_CA_MESSAGE_CARDTESTSUCC_TYPE ,
        UMSG_DVB_CA_MESSAGE_NOCALIBOPER_TYPE ,   
        
		/*CA*/
		UMSG_DVB_CA_SHOW_TOP_OSD_MESSAGE , 
		UMSG_DVB_CA_HIDE_TOP_OSD_MESSAGE ,  
		UMSG_DVB_CA_FINGER_PRINT ,     
		UMSG_DVB_CA_LOCK_SERVICE ,      
		UMSG_DVB_CA_UNLOCK_SERVICE ,   
		UMSG_DVB_CA_SCALE_RECEIVEPATCH ,
		UMSG_DVB_CA_SCALE_PATCHING ,    
		UMSG_DVB_CA_EMAIL_NOTIFY ,     
		UMSG_DVB_CA_ENTITLE_CHANGED ,   
		UMSG_DVB_CA_DETITLE_RECEIVED , 
		UMSG_DVB_CA_FEEDING_REQUEST ,   
		UMSG_DVB_CA_HIDE_IPPVDLG ,     
		UMSG_DVB_CA_START_IPPVDLG ,   //61
		UMSG_DVB_CA_SMC_OUT ,            
		UMSG_DVB_CA_SMC_IN ,          
		UMSG_DVB_CA_UPDATE_WINDOW ,     
		UMSG_DVB_CA_CARD_CHANGED ,  
		UMSG_DVB_CA_SHOW_BOTTON_OSD_MESSAGE , 
		UMSG_DVB_CA_HIDE_BOTTON_OSD_MESSAGE ,  
    };
    private static int MASK_TUNER 	= 1<<STATUS_TUNER; 
    private static int MASK_CA		= 1<<STATUS_CA;
    private static int MASK_PLAY	= 1<<STATUS_PLAY;

    private static int status_mask = 0;
    private static int STATUS_COUNT = 32;

    private int curResID = 0;

    static final int STRONG_SIGNAL 	= 4;
    static final int NO_SIGNAL		= 2;

    static final int CANCEL_TYPE = 22;
    static final int BADCARD_TYPE = 23;
    static final int EXPICARD_TYPE = 24;
    static final int INSERTCARD_TYPE = 25;
    static final int NOOPER_TYPE = 26;
    static final int BLACKOUT_TYPE = 27;
    static final int OUTWORKTIME_TYPE = 28;
    static final int WATCHLEVEL_TYPE = 29;
    static final int PAIRING_TYPE = 30;
    static final int NOENTITLE_TYPE = 31;
    static final int DECRYPTFAIL_TYPE = 32;
    static final int NOMONEY_TYPE = 33;
    static final int ERRREGION_TYPE = 34;
    static final int NEEDFEED_TYPE = 35;
    static final int ERRCARD_TYPE = 36;
    static final int UPDATE_TYPE = 37;
    static final int LOWCARDVER_TYPE = 38;
    static final int VIEWLOCK_TYPE = 39;
    static final int MAXRESTART_TYPE = 40;
    static final int FREEZE_TYPE = 41;
    static final int CALLBACK_TYPE = 42;
    static final int STBLOCKED_TYPE = 43;
    static final int STBFREEZE_TYPE = 44;
    static final int CARDTESTSTART_TYPE = 45;
    static final int CARDTESTFAILD_TYPE = 46;
    static final int CARDTESTSUCC_TYPE = 47;
    static final int NOCALIBOPER_TYPE = 48;

	static final int SHOW_OSD_MESSAGE = 49;
	static final int HIDE_OSD_MESSAGE =50;
	static final int FINGER_PRINT =51;     
	static final int LOCK_SERVICE =52;  
	static final int UNLOCK_SERVICE = 53;  
	static final int SCALE_RECEIVEPATCH =54;
	static final int SCALE_PATCHING =55;  
	static final int EMAIL_NOTIFY =56;
	static final int ENTITLE_CHANGED =57; 
	static final int DETITLE_RECEIVED =58; 
	static final int FEEDING_REQUEST =59;
	static final int HIDE_IPPVDLG =60;
	static final int START_IPPVDLG =61; 
	static final int SMC_OUT =62; 
	static final int SMC_IN =63;      
	static final int UPDATE_WINDOW =64;     
	static final int CARD_CHANGED =65; 

	public static void handleMessage(Context context, Message msg) {
		byte[] byteinfo = (byte[]) msg.obj;

		switch (msg.what) {
		case 4:
			if (msg.arg1 == subtype.UMSG_DVB_CA_EMAIL_NOTIFY.ordinal()) {
				int flag = byteinfo[3] << 24 | byteinfo[2] << 16
						| byteinfo[1] << 8 | byteinfo[0];
				if (CDCA_Email_New == flag) {

					Toast showEmailImageToast = new Toast(context);
					ImageView imageView = new ImageView(context);
					imageView.setImageResource(R.drawable.notread);
					showEmailImageToast.setView(imageView);
					showEmailImageToast.setGravity(Gravity.CENTER, 0, 0);
					showEmailImageToast.setDuration(11111);
					showEmailImageToast.show();
				} else if (CDCA_Email_SpaceExhaust == flag) {

					Toast showEmailImageToast = new Toast(context);
					ImageView imageView = new ImageView(context);
					imageView.setImageResource(R.drawable.alreadyread);
					showEmailImageToast.setView(imageView);
					showEmailImageToast.setGravity(Gravity.RIGHT, 0, 0);
					showEmailImageToast.setDuration(1111);
					showEmailImageToast.show();
				}
			}
		default:
			break;
		}
	}
}
 
      
