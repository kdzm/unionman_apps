package com.um.dvbstack;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//import com.um.umsmcupdate.SmcUpdateService;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
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
import android.content.Intent;
import android.content.Context;

public class Status {
    private final String TAG = new String("STATUS");
    private AlertDialog alertDialog;
    private Object altersync;

    private static Context mConetxt;
    private int flag = 0;
    private LinearLayout mPrompt = null;
    static private StatusHandler handler = null;
    //static private Tf_updatebar updatebar;

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
		UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE,          /*������ݼ��� param1:0~100*/
		UMSG_DVB_LOADER_UPGRADE_ERR,     //20                   /*����ʧ��*/
		UMSG_DVB_LOADER_UPGRADE_SUCCESS,                    /*���سɹ�*/
		/*CA*/
		UMSG_DVB_CA_MESSAGE_CANCEL_TYPE,        /*ȡ����ʾ��*/
		UMSG_DVB_CA_MESSAGE_BADCARD_TYPE,       /*�޷�ʶ�𿨣�����ʹ��*/
		UMSG_DVB_CA_MESSAGE_EXPICARD_TYPE,      /*���ܿ��Ѿ����ڣ�����¿�*/
		UMSG_DVB_CA_MESSAGE_INSERTCARD_TYPE,    /*���Ž�Ŀ����������ܿ�*/
		UMSG_DVB_CA_MESSAGE_NOOPER_TYPE ,       /*���в����ڽ�Ŀ��Ӫ��*/
		UMSG_DVB_CA_MESSAGE_BLACKOUT_TYPE ,     /*������*/
		UMSG_DVB_CA_MESSAGE_OUTWORKTIME_TYPE ,  /*���ڹ���ʱ����*/
		UMSG_DVB_CA_MESSAGE_WATCHLEVEL_TYPE,    /*��Ŀ��������趨�ۿ�����*/
		UMSG_DVB_CA_MESSAGE_PAIRING_TYPE ,   //30 /*��ĿҪ����Ӧ*/
		UMSG_DVB_CA_MESSAGE_NOENTITLE_TYPE ,    /*û����Ȩ*/
		UMSG_DVB_CA_MESSAGE_DECRYPTFAIL_TYPE ,  /*��Ŀ����ʧ��*/
		UMSG_DVB_CA_MESSAGE_NOMONEY_TYPE ,      /*���ڽ���*/
		UMSG_DVB_CA_MESSAGE_ERRREGION_TYPE ,    /*������ȷ*/
		UMSG_DVB_CA_MESSAGE_NEEDFEED_TYPE ,     /*�ӿ���Ҫ��ĸ����Ӧ*/
		UMSG_DVB_CA_MESSAGE_ERRCARD_TYPE ,      /*���ܿ�У��ʧ�ܣ�����ϵ��Ӫ�̣�ͬ�����ܿ�*/
		UMSG_DVB_CA_MESSAGE_UPDATE_TYPE ,       /*ϵͳ���벻Ҫ�ο����߹ػ�*/
		UMSG_DVB_CA_MESSAGE_LOWCARDVER_TYPE ,   /*�������ܿ�*/
		UMSG_DVB_CA_MESSAGE_VIEWLOCK_TYPE,      /*����Ƶ���л�Ƶ��*/
		UMSG_DVB_CA_MESSAGE_MAXRESTART_TYPE,//40    /*���ܿ�������*/
		UMSG_DVB_CA_MESSAGE_FREEZE_TYPE,        /*���ܿ��Ѷ��ᣬ����ϵ��Ӫ��*/
		UMSG_DVB_CA_MESSAGE_CALLBACK_TYPE ,     /*�ش�ʧ��*/
		UMSG_DVB_CA_MESSAGE_STBLOCKED_TYPE ,    /*��ػ��������*/
		UMSG_DVB_CA_MESSAGE_STBFREEZE_TYPE ,    /*��б�����*/
		UMSG_DVB_CA_MESSAGE_CARDTESTSTART_TYPE, /*����Կ�������...*/
		UMSG_DVB_CA_MESSAGE_CARDTESTFAILD_TYPE, /*����Կ�����ʧ�ܣ������ͨѶģ��*/
		UMSG_DVB_CA_MESSAGE_CARDTESTSUCC_TYPE , /*����Կ����Գɹ�*/
		UMSG_DVB_CA_MESSAGE_NOCALIBOPER_TYPE ,  /*���в�������ֲ�ⶨ����Ӫ��*/

		/*CA������Ϣ*/
		UMSG_DVB_TFCA_SHOW_TOP_OSD_MESSAGE ,  /*osd��ʾ��Ϣ*/
		UMSG_DVB_TFCA_HIDE_TOP_OSD_MESSAGE ,//50  /*osd������Ϣ*/
		UMSG_DVB_CA_FINGER_PRINT ,      /*ָ����ʾ��Ϣ*/
		UMSG_DVB_CA_LOCK_SERVICE ,      /*ǿ����̨��Ϣ*/
		UMSG_DVB_CA_UNLOCK_SERVICE ,    /*ȡ��ǿ����̨��Ϣ*/
		UMSG_DVB_CA_SCALE_RECEIVEPATCH ,/*���ܿ�����ݽ�����*/
		UMSG_DVB_CA_SCALE_PATCHING ,    /*���ܿ�����*/
		UMSG_DVB_CA_EMAIL_NOTIFY ,      /*��ʾ�ʼ�ͼ����Ϣ*/
		UMSG_DVB_CA_ENTITLE_CHANGED ,   /*��Ȩ�ı���Ϣ*/
		UMSG_DVB_CA_DETITLE_RECEIVED ,  /*���յ�����Ȩ����Ϣ*/
		UMSG_DVB_CA_FEEDING_REQUEST ,   /*ι����ʾ��Ϣ*/
		UMSG_DVB_TFCA_HIDE_IPPVDLG , //60     /*����ipp�����*/
		UMSG_DVB_TFCA_START_IPPVDLG ,   /*����tf ipp�Ի���*/
		UMSG_DVB_CA_SMC_OUT ,           /*���ܿ��γ�*/    
		UMSG_DVB_CA_SMC_IN ,            /*���ܿ�����*/
		UMSG_DVB_CA_UPDATE_WINDOW ,     
		UMSG_DVB_CA_CARD_CHANGED ,      /*���ܿ��ı�*/
		UMSG_DVB_CA_LOCK_SERVICE_ERROR ,	/*ǿ����̨�������*/
		UMSG_DVB_CA_SHOW_PREVIEW,       /*�߼�Ԥ��*/
		UMSG_DVB_TFCA_SHOW_BOTTON_OSD_MESSAGE , 
		UMSG_DVB_TFCA_HIDE_BOTTON_OSD_MESSAGE ,    
		/*loader*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_CONTROLHEAD_DATE,    /*�����"������������controlhead���"*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_PARTINFO_DATE,       /*�����"������������partinfo���"*/
		UMSG_DVB_LOADER_UPGRADE_SEARCH_DATAGRAM_DATE,       /*�����"������������datagram���"*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_DATE,                 /*�����"���ڼ������"*/
		UMSG_DVB_LOADER_UPGRADE_CONTROLHEAD_IS_RECIEVED,    /*���controlhead��Ϣ���ѵ�*/
		UMSG_DVB_LOADER_UPGRADE_PARTION_IS_RECIEVED,        /*���partinfo��Ϣ���ѵ�*/
		UMSG_DVB_LOADER_UPGRADE_DATAGRAM_IS_RECIEVED,       /*���datagram��Ϣ���ѵ�*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_CRC_SCHEDULE,         /*У����ݽ�� param1:0~100*/
		UMSG_DVB_LOADER_UPGRADE_FACID_ERROR,                /*����id��ƥ��*/
		UMSG_DVB_LOADER_UPGRADE_HW_ERROR,                   /*Ӳ���汾��ƥ��*/
		UMSG_DVB_LOADER_UPGRADE_SW_ERROR,                   /*����汾��ƥ��*/
		UMSG_DVB_LOADER_UPGRADE_SN_ERROR,                   /*���кŲ�ƥ��*/
		UMSG_DVB_LOADER_UPGRADE_CHECK_OK,                   /*ͷ����ݼ����*/
		UMSG_DVB_LOADER_UPGRADE_LOCK_FAILED,                /*����Ƶʧ��*/
		UMSG_DVB_LOADER_UPGRADE_TIME_UP,                    /*����ݳ�ʱ*/
		UMSG_DVB_LOADER_UPGRADE_CRC_ERROR,                  /*crcУ�����*/
		UMSG_DVB_LOADER_UPGRADE_MALLOC_ERROR,               /*�����ڴ�ʧ��*/
		UMSG_DVB_LOADER_UPGRADE_UNKNOWN_ERROR,              /*δ֪����*/

		/*DVT???*/
	    UMSG_DVB_DVTCA_RATING_TOO_LOW,    //                    0            //????????
	    UMSG_DVB_DVTCA_NOT_IN_WATCH_TIME,    //                  1            //????????????
	    UMSG_DVB_DVTCA_NOT_PAIRED,    //                         2            //??��???
	    UMSG_DVB_DVTCA_IS_PAIRED_OTHER,    //                    3            //IC?????????��??
	    UMSG_DVB_DVTCA_PLEASE_INSERT_CARD,    //                 4            //??��
	    UMSG_DVB_DVTCA_NO_ENTITLE,    //                         5            //??��??????
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
	    UMSG_DVB_DVTCA_DATA_INVALID ,    //                       16            //?????��??STB?????��???????????????
	    UMSG_DVB_DVTCA_SLOT_NOT_FOUND ,    //                     17            //??????
	    UMSG_DVB_DVTCA_SC_NOT_SERVER ,    //                      18            //IC???????????
	    UMSG_DVB_DVTCA_TVS_NOT_FOUND ,    //                      19            //??????????
	    UMSG_DVB_DVTCA_KEY_NOT_FOUND ,    //                      20            //???��????????????????
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
    private static Status m_instance = new Status();
    static int TFCAS_PROGRESS_STATUS = 0;
    static int UpdateFlag = 0;
    static int CDCA_OSD_TOP = 0x01;  /* OSD风格：显示在屏幕上方 */
    static int CDCA_OSD_BOTTOM = 0x02;  /* OSD风格：显示在屏幕下方 */

    
    private List<Integer> saveMessage = new ArrayList<Integer>();

    private Status() {
        for (int i = 0; i < STATUS_COUNT; i++) {
            Integer msg = Integer.valueOf(0);
            saveMessage.add(msg);
        }
    }

    public void attachContext(Context c) {
        synchronized (m_instance) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            //hidePrompt();
            handler = new StatusHandler(c.getMainLooper());
        }

        mConetxt = c;
        //initWindow(c);
        Log.v("Status", "attach context");
        //DVB.GetInstance().enableStatusListener(true);
        WeakReference<Status> obj = new WeakReference<Status>(this);
        startListnerStatus(obj, DVB.GetInstance().mNativeContext, true);
    }

    public void detachContext() {
        //DVB.GetInstance().enableStatusListener(false);
        WeakReference<Status> obj = new WeakReference<Status>(this);
        startListnerStatus(obj, DVB.GetInstance().mNativeContext, false);
        synchronized (m_instance) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            handler = null;
        }

        mConetxt = null;
        Log.v("Status", "dettach context");
    }

    public static Status GetInstance() {
        return m_instance;
    }

    private static byte[] AllocData(int len) {
        return new byte[len];
    }
    
    private static void SendMessage(Object statusRef, int type, int param, byte[] data, int len) {
        Log.i("Status", "Send message data!");
		Status status = (Status)((WeakReference)statusRef).get();
        if (status == null) {
            return;
        }
		
        synchronized (status.m_instance) {
            if (status.handler != null) {
                Message msg = status.handler.obtainMessage(type, param, len, data);

                status.handler.sendMessage(msg);
                Log.i("Status", "handler Send message data!");
            }
        }
    }

    
    private static void SendMessage(Object statusRef, int type, int param1, int param2) {
		Status status = (Status)((WeakReference)statusRef).get();
        if (status == null) {
            return;
        }
		
        synchronized (status.m_instance) {
            if (status.handler != null) {
                Message msg = status.handler.obtainMessage(type, param1, param2);

                status.handler.sendMessage(msg);
            }
        }
    }

    private native void startListnerStatus(Object status_this, int dvbHandle, boolean flag);

    private class StatusHandler extends Handler {

        StatusHandler(Looper lp) {
            super(lp);
        }

        public void handleMessage(Message msg) {
            Log.i("Status", "receiver handleMessage!");
            
            if (msg.what == 0) {
            	Log.i("Status", "dvbstack is died");
            	System.exit(0);
            	return;
            }
            
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

    }
    
}
