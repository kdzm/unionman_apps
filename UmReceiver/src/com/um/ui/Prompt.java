package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//import com.um.umsmcupdate.SmcUpdateService;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.System;
import android.renderscript.Int3;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

import com.um.dvbstack.DVB;
import com.um.dvbstack.ReceiverMsgInterface;
import com.um.umreceiver.R;
import android.content.Intent;
import android.content.Context;

public class Prompt {
    private final String TAG = "STATUS";
    private AlertDialog alertDialog;
    private Object altersync;

    private static Context mConetxt;
    private int flag = 0;
    private LinearLayout mPrompt = null;
    //static private Tf_updatebar updatebar;
    
    static final int STATUS_SERVER_DIED = 0;
    static final int STATUS_TUNER = 1;
    static final int STATUS_CA = 4;
    static final int STATUS_PLAY = 3;

    static final int CDCA_Email_IconHide = 0;
    static final int CDCA_Email_New = 1;
    static final int CDCA_Email_SpaceExhaust = 2;

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
	
    public enum subtype {
        UMSG_DVB_NULL,
        /*tuner signal*/
		UMSG_DVB_SIGNAL_CHANGE,
		UMSG_DVB_NO_SIGNAL,
		UMSG_DVB_WEAK_SIGNAL,
		UMSG_DVB_STRONG_SIGNAL,
		/*avplay*/
		UMSG_DVB_AVPLAY_STOP,   /*5*/
		UMSG_DVB_AVPLAY_START,
		/*db*/
		UMSG_DVB_DB_NIT_VERSION_CHANGE, /*7*/
		UMSG_DVB_DB_PROGRAM_NAME_CHANGE,
		UMSG_DVB_DB_PMT_PID_CHANGE,
		UMSG_DVB_DB_PROGRAM_BASIC_INFO_CHANGE,
		UMSG_DVB_DB_PROGRAM_DELETE,
		/*srch*/
		UMSG_DVB_SRCH_PROGRESS,         /*12*/
		UMSG_DVB_SRCH_GET_PROG,
		UMSG_DVB_SRCH_GET_TP,
		/*epg*/
		UMSG_DVB_EPG_PF_EVENTS_UPDATE,
		UMSG_DVB_EPG_SCH_EVENTS_UPDATE,
		/*loader*/
		UMSG_DVB_LOADER_FORCE_UPDATE,
		UMSG_DVB_LOADER_MANUAL_UPDATE,
		UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE,          /*ÏÂÔØÊý¾Ý¼¾¶È param1:0~100*/
		UMSG_DVB_LOADER_UPGRADE_ERR,     //20                   /*ÏÂÔØÊ§°Ü*/
		UMSG_DVB_LOADER_UPGRADE_SUCCESS,                    /*ÏÂÔØ³É¹¦*/
		/*CA*/
		UMSG_DVB_CA_MESSAGE_CANCEL_TYPE,        /*È¡ÏûÌáÊ¾Óï*/
		UMSG_DVB_CA_MESSAGE_BADCARD_TYPE,       /*ÎÞ·¨Ê¶±ð¿¨£¬²»ÄÜÊ¹ÓÃ*/
		UMSG_DVB_CA_MESSAGE_EXPICARD_TYPE,      /*ÖÇÄÜ¿¨ÒÑ¾­¹ýÆÚ£¬Çë¸ü»»ÐÂ¿¨*/
		UMSG_DVB_CA_MESSAGE_INSERTCARD_TYPE,    /*¼ÓÈÅ½ÚÄ¿£¬Çë²åÈëÖÇÄÜ¿¨*/
		UMSG_DVB_CA_MESSAGE_NOOPER_TYPE ,       /*¿¨ÖÐ²»´æÔÚ½ÚÄ¿ÔËÓªÉÌ*/
		UMSG_DVB_CA_MESSAGE_BLACKOUT_TYPE ,     /*Ìõ¼þ½û²¥*/
		UMSG_DVB_CA_MESSAGE_OUTWORKTIME_TYPE ,  /*²»ÔÚ¹¤×÷Ê±¶ÎÄÚ*/
		UMSG_DVB_CA_MESSAGE_WATCHLEVEL_TYPE,    /*½ÚÄ¿¼¶±ð¸ßÓÚÉè¶¨¹Û¿´¼¶±ð*/
		UMSG_DVB_CA_MESSAGE_PAIRING_TYPE ,   //30 /*½ÚÄ¿ÒªÇó»ú¿¨¶ÔÓ¦*/
		UMSG_DVB_CA_MESSAGE_NOENTITLE_TYPE ,    /*Ã»ÓÐÊÚÈ¨*/
		UMSG_DVB_CA_MESSAGE_DECRYPTFAIL_TYPE ,  /*½ÚÄ¿½âÃÜÊ§°Ü*/
		UMSG_DVB_CA_MESSAGE_NOMONEY_TYPE ,      /*¿¨ÄÚ½ð¶î²»×ã*/
		UMSG_DVB_CA_MESSAGE_ERRREGION_TYPE ,    /*ÇøÓò²»ÕýÈ·*/
		UMSG_DVB_CA_MESSAGE_NEEDFEED_TYPE ,     /*×Ó¿¨ÐèÒªºÍÄ¸¿¨¶ÔÓ¦*/
		UMSG_DVB_CA_MESSAGE_ERRCARD_TYPE ,      /*ÖÇÄÜ¿¨Ð£ÑéÊ§°Ü£¬ÇëÁªÏµÔËÓªÉÌ£¬Í¬²½ÖÇÄÜ¿¨*/
		UMSG_DVB_CA_MESSAGE_UPDATE_TYPE ,       /*ÏµÍ³Éý¼¶£¬Çë²»Òª°Î¿¨»òÕß¹Ø»ú*/
		UMSG_DVB_CA_MESSAGE_LOWCARDVER_TYPE ,   /*ÇëÉý¼¶ÖÇÄÜ¿¨*/
		UMSG_DVB_CA_MESSAGE_VIEWLOCK_TYPE,      /*ÇëÎðÆµ·±ÇÐ»»ÆµµÀ*/
		UMSG_DVB_CA_MESSAGE_MAXRESTART_TYPE,//40    /*ÖÇÄÜ¿¨ÒÑÊÜËð*/
		UMSG_DVB_CA_MESSAGE_FREEZE_TYPE,        /*ÖÇÄÜ¿¨ÒÑ¶³½á£¬ÇëÁªÏµÔËÓªÉÌ*/
		UMSG_DVB_CA_MESSAGE_CALLBACK_TYPE ,     /*»Ø´«Ê§°Ü*/
		UMSG_DVB_CA_MESSAGE_STBLOCKED_TYPE ,    /*Çë¹Ø»úÖØÆô»ú¶¥ºÐ*/
		UMSG_DVB_CA_MESSAGE_STBFREEZE_TYPE ,    /*»ú¶¥ºÐ±»¶³½á*/
		UMSG_DVB_CA_MESSAGE_CARDTESTSTART_TYPE, /*Éý¼¶²âÊÔ¿¨²âÊÔÖÐ...*/
		UMSG_DVB_CA_MESSAGE_CARDTESTFAILD_TYPE, /*Éý¼¶²âÊÔ¿¨²âÊÔÊ§°Ü£¬Çë¼ì²é»ú¿¨Í¨Ñ¶Ä£¿é*/
		UMSG_DVB_CA_MESSAGE_CARDTESTSUCC_TYPE , /*Éý¼¶²âÊÔ¿¨²âÊÔ³É¹¦*/
		UMSG_DVB_CA_MESSAGE_NOCALIBOPER_TYPE ,  /*¿¨ÖÐ²»´æÔÚÒÆÖ²¿â¶¨ÖÆÔËÓªÉÌ*/

		/*CAÆäËûÏûÏ¢*/
		UMSG_DVB_TFCA_SHOW_TOP_OSD_MESSAGE ,  /*osdÏÔÊ¾ÏûÏ¢*/
		UMSG_DVB_TFCA_HIDE_TOP_OSD_MESSAGE ,//50  /*osdÒþ²ØÏûÏ¢*/
		UMSG_DVB_CA_FINGER_PRINT ,      /*Ö¸ÎÆÏÔÊ¾ÏûÏ¢*/
		UMSG_DVB_CA_LOCK_SERVICE ,      /*Ç¿ÖÆÇÐÌ¨ÏûÏ¢*/
		UMSG_DVB_CA_UNLOCK_SERVICE ,    /*È¡ÏûÇ¿ÖÆÇÐÌ¨ÏûÏ¢*/
		UMSG_DVB_CA_SCALE_RECEIVEPATCH ,/*ÖÇÄÜ¿¨Éý¼¶Êý¾Ý½ÓÊÕÖÐ*/
		UMSG_DVB_CA_SCALE_PATCHING ,    /*ÖÇÄÜ¿¨Éý¼¶ÖÐ*/
		UMSG_DVB_CA_EMAIL_NOTIFY ,      /*ÏÔÊ¾ÓÊ¼þÍ¼±êÏûÏ¢*/
		UMSG_DVB_CA_ENTITLE_CHANGED ,   /*ÊÚÈ¨¸Ä±äÏûÏ¢*/
		UMSG_DVB_CA_DETITLE_RECEIVED ,  /*½ÓÊÕµ½·´ÊÚÈ¨µÄÏûÏ¢*/
		UMSG_DVB_CA_FEEDING_REQUEST ,   /*Î¹ÑøÌáÊ¾ÏûÏ¢*/
		UMSG_DVB_TFCA_HIDE_IPPVDLG , //60     /*Òþ²Øipp¹ºÂò¿ò*/
		UMSG_DVB_TFCA_START_IPPVDLG ,   /*µ¯³ötf ipp¶Ô»°¿ò*/
		UMSG_DVB_CA_SMC_OUT ,           /*ÖÇÄÜ¿¨°Î³ö*/    
		UMSG_DVB_CA_SMC_IN ,            /*ÖÇÄÜ¿¨²åÈë*/
		UMSG_DVB_CA_UPDATE_WINDOW ,     
		UMSG_DVB_CA_CARD_CHANGED ,      /*ÖÇÄÜ¿¨¸Ä±ä*/
		UMSG_DVB_CA_LOCK_SERVICE_ERROR ,	/*Ç¿ÖÆÇÐÌ¨²ÎÊý´íÎó*/
		UMSG_DVB_CA_SHOW_PREVIEW,       /*¸ß¼¶Ô¤ÀÀ*/
		UMSG_DVB_TFCA_SHOW_BOTTON_OSD_MESSAGE , 
		UMSG_DVB_TFCA_HIDE_BOTTON_OSD_MESSAGE ,    
		/*loader*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_CONTROLHEAD_DATE,    /*Éý¼¶½çÃæ"ÕýÔÚËÑË÷ÏÂÔØcontrolheadÊý¾Ý"*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_PARTINFO_DATE,       /*Éý¼¶½çÃæ"ÕýÔÚËÑË÷ÏÂÔØpartinfoÊý¾Ý"*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_DATAGRAM_DATE,       /*Éý¼¶½çÃæ"ÕýÔÚËÑË÷ÏÂÔØdatagramÊý¾Ý"*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_DATE,                 /*Éý¼¶½çÃæ"ÕýÔÚ¼ìÑéÊý¾Ý"*/
		UMSG_DVB_LOADER_UPGRADE_CONTROLHEAD_IS_RECIEVED,    /*Êý¾ÝcontrolheadÐÅÏ¢ÒÑËÑµ½*/
		UMSG_DVB_LOADER_UPGRADE_PARTION_IS_RECIEVED,        /*Êý¾ÝpartinfoÐÅÏ¢ÒÑËÑµ½*/
		UMSG_DVB_LOADER_UPGRADE_DATAGRAM_IS_RECIEVED,       /*Êý¾ÝdatagramÐÅÏ¢ÒÑËÑµ½*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_CRC_SCHEDULE,         /*Ð£ÑéÊý¾Ý½ø¶È param1:0~100*/
		UMSG_DVB_LOADER_UPGRADE_FACID_ERROR,                /*³§ÉÌid²»Æ¥Åä*/
		UMSG_DVB_LOADER_UPGRADE_HW_ERROR,                   /*Ó²¼þ°æ±¾²»Æ¥Åä*/
		UMSG_DVB_LOADER_UPGRADE_SW_ERROR,                   /*Èí¼þ°æ±¾²»Æ¥Åä*/
		UMSG_DVB_LOADER_UPGRADE_SN_ERROR,                   /*ÐòÁÐºÅ²»Æ¥Åä*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_OK,                   /*Í·²¿Êý¾Ý¼ì²éÕý³£*/
		UMSG_DVB_LOADER_UPGRADE_LOCK_FAILED,                /*Éý¼¶ËøÆµÊ§°Ü*/
		UMSG_DVB_LOADER_UPGRADE_TIME_UP,                    /*ËÑÊý¾Ý³¬Ê±*/
		UMSG_DVB_LOADER_UPGRADE_CRC_ERROR,                  /*crcÐ£Ñé´íÎó*/
		UMSG_DVB_LOADER_UPGRADE_MALLOC_ERROR,               /*ÉêÇëÄÚ´æÊ§°Ü*/
		UMSG_DVB_LOADER_UPGRADE_UNKNOWN_ERROR,              /*Î´Öª´íÎó*/

		/*DVT???*/
	    UMSG_DVB_DVTCA_RATING_TOO_LOW,    //                    0            //????????
	    UMSG_DVB_DVTCA_NOT_IN_WATCH_TIME,    //                  1            //????????????
	    UMSG_DVB_DVTCA_NOT_PAIRED,    //                         2            //??§Ý???
	    UMSG_DVB_DVTCA_IS_PAIRED_OTHER,    //                    3            //IC?????????§Ø??
	    UMSG_DVB_DVTCA_PLEASE_INSERT_CARD,    //                 4            //??ñ
	    UMSG_DVB_DVTCA_NO_ENTITLE,    //                         5            //??§Û??????
	    UMSG_DVB_DVTCA_PRODUCT_RESTRICT,    //                   6            //????????????y??
	    UMSG_DVB_DVTCA_AREA_RESTRICT,    //                      7            //???????????????
	    UMSG_DVB_DVTCA_MOTHER_RESTRICT,    //                    8            //?????????????????????????????????
	    UMSG_DVB_DVTCA_NO_MONEY, //                           9            //???????????????????????
	    UMSG_DVB_DVTCA_IPP_NO_CONFIRM ,    //                     10            //?????IPPV???????IPPV??????/??????????????????
	    UMSG_DVB_DVTCA_IPP_NO_BOOK,    //                        11            //?????IPPV??????????????????????????????
	    UMSG_DVB_DVTCA_IPPT_NO_CONFIRM,    //                    12            //?????IPPT???????IPPT??????/??????????????????
	    UMSG_DVB_DVTCA_IPPT_NO_BOOK ,    //                       13            //?????IPPT??????????????????????????????
	    UMSG_DVB_DVTCA_IPPV_NO_MONEY ,    //                      14            //?????IPPV????????????????????????
	    UMSG_DVB_DVTCA_IPPT_NO_MONEY  ,    //                     15            //?????IPPT????????????????????????
	    UMSG_DVB_DVTCA_DATA_INVALID ,    //                       16            //?????§¹??STB?????¦Ê???????????????
	    UMSG_DVB_DVTCA_SLOT_NOT_FOUND ,    //                     17            //??????
	    UMSG_DVB_DVTCA_SC_NOT_SERVER ,    //                      18            //IC???????????
	    UMSG_DVB_DVTCA_TVS_NOT_FOUND ,    //                      19            //??????????
	    UMSG_DVB_DVTCA_KEY_NOT_FOUND ,    //                      20            //???¦Ä????????????????
	    UMSG_DVB_DVTCA_IPPNEED_CALLBACK ,    //                   21            //????????????IPP??????
	    UMSG_DVB_DVTCA_CANCEL_PROMTMSG ,    //                    22            //?????

	    UMSG_DVB_DVTCA_MSG_HIDEPROMPTMSG ,    //                    50
	    UMSG_DVB_DVTCA_MSG_FINGER_PRINT,    //                     51
	    UMSG_DVB_DVTCA_MSG_LOCK_SERVICE ,    //                     52
	    UMSG_DVB_DVTCA_MSG_UNLOCK_SERVICE ,    //                   53
	    UMSG_DVB_DVTCA_MSG_SHOW_OSD_MESSAGE ,    //                 54
	    UMSG_DVB_DVTCA_MSG_EMAIL_NOTIFY ,    //                     55
	    UMSG_DVB_DVTCA_MSG_START_IPPVDLG ,    //                    56
	    UMSG_DVB_DVTCA_MSG_SMC_OUT ,    //                          57
	    UMSG_DVB_DVTCA_MSG_SMC_IN ,    //                           58
	    UMSG_DVB_DVTCA_MSG_HIDE_URGENT_BROADCAST ,    //            59
	    UMSG_DVB_DVTCA_MSG_SHOW_URGENT_BROADCAST ,    //            60
	    UMSG_DVB_DVTCA_MSG_CARD_CHANGED ,    //                     61
	    UMSG_DVB_DVTCA_MSG_AUTO_FD ,    //                          62
	    UMSG_DVB_DVTCA_MSG_PDSD_VALUE_CHANGE ,    //                63

	    UMSG_DVB_DVTCA_DECRYPT_FAILURE ,
	    UMSG_DVB_DVTCA_MESSAGE_SHOW_PREVIEW,  //????????????
	    UMSG_DVB_DVTCA_MESSAGE_CLOSE_PREVIEW, //???????????
	    /****************/

		UMSG_DVB_DVTCA_START_IPPVDLG,	/*????dvt ipp?????*/
	    UMSG_DVB_DVTCA_HIDE_IPPVDLG,	/*????dvt ipp?????*/
	    UMSG_DVB_DVTCA_SHOW_BOTTON_OSD_MESSAGE,
	    UMSG_DVB_DVTCA_AUTO_FEED_CARD,
	    UMSG_DVB_DVTCA_SMC_IN,
	    UMSG_DVB_DVTCA_SMC_OUT,
	    UMSG_DVB_DVTCA_LOCK_SERVICE ,      /*DVT?????????*/
	    UMSG_DVB_DVTCA_UNLOCK_SERVICE ,    /*DVT????????????*/
    };

   
    private static int MASK_TUNER = 1 << STATUS_TUNER;
    private static int MASK_CA = 1 << STATUS_CA;
    private static int MASK_PLAY = 1 << STATUS_PLAY;

    private static int status_mask = 0;
    private static int STATUS_COUNT = 32;

    private int curResID = 0;

    static final int STRONG_SIGNAL = 4;
    static final int NO_SIGNAL = 2;
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
    static final int HIDE_OSD_MESSAGE = 50;
    static final int FINGER_PRINT = 51;
    static final int LOCK_SERVICE = 52;
    static final int UNLOCK_SERVICE = 53;
    static final int SCALE_RECEIVEPATCH = 54;
    static final int SCALE_PATCHING = 55;
    static final int EMAIL_NOTIFY = 56;
    static final int ENTITLE_CHANGED = 57;
    static final int DETITLE_RECEIVED = 58;
    static final int FEEDING_REQUEST = 59;
    static final int HIDE_IPPVDLG = 60;
    static final int START_IPPVDLG = 61;
    static final int SMC_OUT = 62;
    static final int SMC_IN = 63;
    static final int UPDATE_WINDOW = 64;
    static final int CARD_CHANGED = 65;
    private static Prompt m_instance = new Prompt();
    static int TFCAS_PROGRESS_STATUS = 0;
    static int UpdateFlag = 0;
    static int CDCA_OSD_TOP = 0x01;  /* OSD风格：显示在屏幕上方 */
    static int CDCA_OSD_BOTTOM = 0x02;  /* OSD风格：显示在屏幕下方 */
    private static Prompt mInstance = null;
    
    private List<Integer> saveMessage = new ArrayList<Integer>();

    private Prompt() {
        for (int i = 0; i < STATUS_COUNT; i++) {
            Integer msg = Integer.valueOf(0);
            saveMessage.add(msg);
        }

    }

    synchronized public static Prompt getInstance() {
        if (mInstance == null) {
            mInstance = new Prompt();
        }
        return mInstance;
    }


    private void deinitWindow(Context context) {
        if (context != null && mPrompt != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Activity.WINDOW_SERVICE);
            windowManager.removeView(mPrompt);
            mPrompt = null;
            Log.v("Status", "revome view.");
        }
    }

    private void hidePrompt() {
        if (mPrompt != null) {
            mPrompt.setVisibility(View.GONE);
        }
    }

    public void attachContext(Context c) {
        hidePrompt();
        mConetxt = c;
    }

    public void detachContext() {
        hidePrompt();
        deinitWindow(mConetxt);

        mConetxt = null;
        Log.v("Status", "dettach context");
    }

    private native void startListnerStatus(Object status_this, int dvbHandle, boolean flag);

    public void handleMessage(Message msg) {
        Log.i("Status", "receiver handleMessage! what=" +  msg.what);
        if (msg.what == 0) {
            Log.i("Status", "Dvbstack die.");

            if (mConetxt != null) {
                Intent intent = new Intent("com.unionman.dvb.ACTION_DVB_SERVER_STATUS_CHANGE");
                intent.putExtra("status", 0);
                mConetxt.sendStickyBroadcast(intent);
                Toast.makeText(mConetxt, "UmRecevier服务退出", 3000).show();
                java.lang.System.exit(0);
            }
        }

        if (mConetxt == null) {
            return;
        }

        if ((null == msg.obj) && (msg.what != 3)) {
            doAlertMessage(msg);
            return;
        }


        Log.i("Status", "do data handleMessage!");
        doData(msg);
    }

    private int getMask(Message msg) {
        switch (msg.what) {
            case STATUS_CA:
                return MASK_CA;

            case STATUS_PLAY:
                return MASK_PLAY;

            case STATUS_TUNER:
                return MASK_TUNER;

            default:
                return 0;
        }
    }

    private boolean isClearMsg(Message msg) {
        switch (msg.what) {
            case STATUS_CA:
                return (msg.arg1 == CANCEL_TYPE) ? true : false;

            case STATUS_PLAY:
                return (msg.arg1 == 0) ? true : false;

            case STATUS_TUNER:
                return (msg.arg1 == STRONG_SIGNAL) ? true : false;

            default:
                return false;
        }
    }


    private void saveResID(Message msg) {
        saveMessage.set(msg.what, msg.arg1);
    }

    private void doData(Message msg) {
        byte[] byteinfo = (byte[]) msg.obj;

        String progname;
        Log.i("Status", "22receiver doData!");

        Log.i("Status", "msg.what:" + msg.what);
        Log.i("Status", "msg.arg1:" + msg.arg1);

        switch (msg.what) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                Log.e("Status", "CA Data msg.arg1"+msg.arg1);
                Log.e("Status", "CA Data msg.arg1"+msg.arg1);
                if (msg.arg1 == subtype.UMSG_DVB_CA_SCALE_RECEIVEPATCH.ordinal())
                {
                    int process = byteinfo[3]<<24|byteinfo[2]<<16|byteinfo[1]<<8|byteinfo[0];

                    Log.e("Status", "UMSG_DVB_CA_SCALE_RECEIVEPATCH process"+process);

                    if (process <= 100)
                    {
                        if (TFCAS_PROGRESS_STATUS == 0)
                        {
                            Log.e("Status", "UMSG_DVB_CA_START_PROGRESS_RECEIVEPATCH 0");
                            Intent intent = new Intent();

                            intent.setAction("com.um.umsmcupdate.START_PROGRESS_RECEIVEPATCH");
                            Bundle bundle = new Bundle();
                            bundle.putInt("progress", process);
                            intent.putExtras(bundle);
                            mConetxt.sendBroadcast(intent);

                            TFCAS_PROGRESS_STATUS = 1;
                        }

                        UpdateFlag = 0;
                    }
                    else
                    {
                        Log.e("Status", "UMSG_DVB_CA_STOP_PROGRESS_RECEIVEPATCH 0");
                        Intent intent = new Intent();
                        intent.setAction("com.um.umsmcupdate.STOP_PROGRESS_RECEIVEPATCH");
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", process);
                        intent.putExtras(bundle);
                        mConetxt.sendBroadcast(intent);

                        TFCAS_PROGRESS_STATUS = 0;
                    }

                    if (process == 100)
                    {
                        UpdateFlag = 1;
                        //保存时间，智能卡升级成功
                    }

                } else if (msg.arg1 == subtype.UMSG_DVB_CA_SCALE_PATCHING.ordinal())
                {
                    int process = byteinfo[3]<<24|byteinfo[2]<<16|byteinfo[1]<<8|byteinfo[0];

                    Log.e("Status", "UMSG_DVB_CA_SCALE_RECEIVEPATCH process"+process);

                    if (process <= 100)
                    {
                        if (TFCAS_PROGRESS_STATUS == 0)
                        {
                            Log.e("Status", "UMSG_DVB_CA_START_PROGRESS_PATCHING 0");
                            Intent intent = new Intent();
                            intent.setAction("com.um.umsmcupdate.START_PROGRESS_PATCHING");
                            Bundle bundle = new Bundle();
                            bundle.putInt("progress", process);
                            intent.putExtras(bundle);
                            mConetxt.sendBroadcast(intent);

                            TFCAS_PROGRESS_STATUS = 1;
                        }
                        if(process == 100)
                        {
                            Log.i("TF_updatebar_receiver", "update success");
                            Toast.makeText(mConetxt, mConetxt.getResources().getString(R.string.update_success), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e("Status", "UMSG_DVB_CA_STOP_PROGRESS_PATCHING 0");
                        Intent intent = new Intent();
                        intent.setAction("com.um.umsmcupdate.STOP_PROGRESS_PATCHING");
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", process);
                        intent.putExtras(bundle);
                        mConetxt.sendBroadcast(intent);

                        TFCAS_PROGRESS_STATUS = 0;
                    }
                }
                else if(msg.arg1 == subtype.UMSG_DVB_TFCA_SHOW_TOP_OSD_MESSAGE.ordinal())
                {
                    Log.e("Status", "UMSG_DVB_TFCA_SHOW_TOP_OSD_MESSAGE 0");
                    String srt2 = "";
                    try {
                        srt2 = new String(byteinfo,"GB2312");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("osdString", srt2);  //OSD字符串
                    bundle.putInt("osdParam", -1);  //OSD播放参数
                    bundle.putInt("osdScrollType", 1);

                    Intent intent = new Intent("com.unionman.umosd.START_TFCA_TOP_OSD");
                    intent.putExtras(bundle);
                    mConetxt.sendBroadcast(intent);
                }
                else if(msg.arg1 == subtype.UMSG_DVB_TFCA_SHOW_BOTTON_OSD_MESSAGE.ordinal())
                {
                    Log.e("Status", "UMSG_DVB_CA_SHOW_BOTTON_OSD_MESSAGE 0");
                    String srt2 = "";
                    try {
                        srt2 = new String(byteinfo,"GB2312");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("osdString", srt2);  //OSD字符串
                    bundle.putInt("osdParam", -1);  //OSD播放参数
                    bundle.putInt("osdScrollType", 1);

                    Intent intent = new Intent("com.unionman.umosd.START_TFCA_BOTTON_OSD");
                    intent.putExtras(bundle);
                    mConetxt.sendBroadcast(intent);

                }
                else if(msg.arg1 == subtype.UMSG_DVB_TFCA_HIDE_TOP_OSD_MESSAGE.ordinal())
                {
                    Log.e("Status", "UMSG_DVB_TFCA_HIDE_TOP_OSD_MESSAGE 0");
                    mConetxt.sendBroadcast(new Intent("com.unionman.umosd.STOP_TFCA_TOP_OSD"));
                }
                else if(msg.arg1 == subtype.UMSG_DVB_TFCA_HIDE_BOTTON_OSD_MESSAGE.ordinal())
                {
                    Log.e("Status", "UMSG_DVB_TFCA_HIDE_BOTTON_OSD_MESSAGE 0");
                    mConetxt.sendBroadcast(new Intent("com.unionman.umosd.STOP_TFCA_BOTTON_OSD"));
                }
                else if(msg.arg1 == subtype.UMSG_DVB_CA_EMAIL_NOTIFY.ordinal())
                {
                }

                else if(msg.arg1 == subtype.UMSG_DVB_CA_LOCK_SERVICE.ordinal())
                {

                }
                else if(msg.arg1 == subtype.UMSG_DVB_CA_UNLOCK_SERVICE.ordinal())
                {

                }
                else if(msg.arg1 == subtype.UMSG_DVB_CA_LOCK_SERVICE_ERROR.ordinal())
                {

                }
                else if(msg.arg1 == subtype.UMSG_DVB_CA_FEEDING_REQUEST.ordinal())
                {

                }

                else if(msg.arg1 == subtype.UMSG_DVB_CA_DETITLE_RECEIVED.ordinal())
                {
                }
                else if(msg.arg1 == subtype.UMSG_DVB_CA_FINGER_PRINT.ordinal())
                {
                }
                else if(msg.arg1 == subtype.UMSG_DVB_DVTCA_SHOW_BOTTON_OSD_MESSAGE.ordinal())
                {
                    Log.e("Status", "UMSG_DVB_DVTCA_SHOW_BOTTON_OSD_MESSAGE 0");
                    String srt2 = "";
                    try {
                        srt2 = new String(byteinfo,"GB2312");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("osdString", srt2);  //OSDå­—ç¬¦ä¸²
                    bundle.putInt("osdParam", 1);  //OSDæ’­æ”¾å‚æ•°
                    bundle.putInt("osdScrollType", 2);

                    Intent intent = new Intent("com.unionman.umosd.START_DVTCA_BOTTON_OSD");
                    intent.putExtras(bundle);
                    mConetxt.sendBroadcast(intent);

                }
                break;
            case 5:
                if(msg.arg1 == subtype.UMSG_DVB_LOADER_FORCE_UPDATE.ordinal()) {
                    Log.i(TAG+"--NONG", "com.um.upgrade.CABLE_FORCE_UPGRADE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CABLE_FORCE_UPGRADE"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_MANUAL_UPDATE.ordinal())
                {
                    Log.i(TAG+"--NONG", "com.um.upgrade.CABLE_MANUAL_UPGRADE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CABLE_MANUAL_UPGRADE"));
                }else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE.ordinal())
                {
                    Log.i(TAG+"--NONG", "com.um.upgrade.CABLE_DOWNLOAD_PROGRESS");
                    int schedule = 0;

                    schedule = byteinfo[0];
                    Bundle bundle = new Bundle();
                    bundle.putInt("downloadProgress", schedule);
                    Intent intent = new Intent("com.um.upgrade.CABLE_DOWNLOAD_PROGRESS");
                    intent.putExtras(bundle);
                    mConetxt.sendBroadcast(intent);
                }else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_CHECK_CRC_SCHEDULE.ordinal()){
                    Log.i(TAG+"--NONG", "com.um.upgrade.CABLE_CRC_PROGRESS");
                    Bundle bundle = new Bundle();
                    int schedule = 0;

                    schedule = byteinfo[0];
                    bundle.putInt("crcProgress", schedule);
                    Intent intent = new Intent("com.um.upgrade.CABLE_CRC_PROGRESS");
                    intent.putExtras(bundle);
                    mConetxt.sendBroadcast(intent);
                }else if(msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_ERR.ordinal()){
                    Log.i(TAG+ "--NONG", "com.um.upgrade.CABLE_DOWNLOAD_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CABLE_DOWNLOAD_ERROR"));
                }else if(msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SUCCESS.ordinal()){
                    Log.i(TAG+ "--NONG", "com.um.upgrade.CABLE_DOWNLOAD_SUCCESS");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CABLE_DOWNLOAD_SUCCESS"));
                } else if(msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SEARCH_CONTROLHEAD_DATE.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.SEARCH_CONTROLHEAD_DATE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.SEARCH_CONTROLHEAD_DATE"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SEARCH_PARTINFO_DATE.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.SEARCH_PARTINFO_DATE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.SEARCH_PARTINFO_DATE"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SEARCH_DATAGRAM_DATE.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.SEARCH_DATAGRAM_DATE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.SEARCH_DATAGRAM_DATE"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_CHECK_DATE.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.CHECK_DATE");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CHECK_DATE"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_CONTROLHEAD_IS_RECIEVED.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.CONTROLHEAD_IS_RECIEVED");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CONTROLHEAD_IS_RECIEVED"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_PARTION_IS_RECIEVED.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.PARTION_IS_RECIEVED");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.PARTION_IS_RECIEVED"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_DATAGRAM_IS_RECIEVED.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.DATAGRAM_IS_RECIEVED");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.DATAGRAM_IS_RECIEVED"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_FACID_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.FACID_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.FACID_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_HW_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.HW_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.HW_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SW_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.SW_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.SW_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SN_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.SN_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.SN_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_CHECK_OK.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.CHECK_OK");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CHECK_OK"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_LOCK_FAILED.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.LOCK_FAILED");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.LOCK_FAILED"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_TIME_UP.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.TIME_UP");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.TIME_UP"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_CRC_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.CRC_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.CRC_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_MALLOC_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.MALLOC_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.MALLOC_ERROR"));
                } else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_UNKNOWN_ERROR.ordinal()) {
                    Log.i(TAG + "--NONG", "com.um.upgrade.UNKNOWN_ERROR");
                    mConetxt.sendBroadcast(new Intent("com.um.upgrade.UNKNOWN_ERROR"));
                }
                break;
            default:
                break;
        }
    }
    private void doAlertMessage(Message msg) {
        if (mListener == null) {
            return ;
        }

        if (msg.what == 4) {
            if (msg.arg1 == subtype.UMSG_DVB_CA_SMC_OUT.ordinal()) {
                mListener.receiveCard(false);
            } else if (msg.arg1 == subtype.UMSG_DVB_CA_SMC_IN.ordinal()) {
                mListener.receiveCard(true);
            }
        } else if (msg.what == STATUS_TUNER) {
            if (msg.arg1 == STRONG_SIGNAL) {
                Log.i("UmCaService", "strong_signal");
                mListener.receiveCable(true);
            } else {
                mListener.receiveCable(false);
            }
        }
    }

	static ReceiverMsgInterface mListener = null;

	public static void setReceiveMsgListener(ReceiverMsgInterface listener) {
		mListener = listener;
	}
}
