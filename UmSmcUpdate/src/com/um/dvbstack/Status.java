package com.um.dvbstack;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


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
        UMSG_DVB_CA_MESSAGE_EXPICARD_TYPE,
        UMSG_DVB_CA_MESSAGE_INSERTCARD_TYPE,
        UMSG_DVB_CA_MESSAGE_NOOPER_TYPE,
        UMSG_DVB_CA_MESSAGE_BLACKOUT_TYPE,
        UMSG_DVB_CA_MESSAGE_OUTWORKTIME_TYPE,
        UMSG_DVB_CA_MESSAGE_WATCHLEVEL_TYPE,
        UMSG_DVB_CA_MESSAGE_PAIRING_TYPE,
        UMSG_DVB_CA_MESSAGE_NOENTITLE_TYPE,
        UMSG_DVB_CA_MESSAGE_DECRYPTFAIL_TYPE,
        UMSG_DVB_CA_MESSAGE_NOMONEY_TYPE,
        UMSG_DVB_CA_MESSAGE_ERRREGION_TYPE,
        UMSG_DVB_CA_MESSAGE_NEEDFEED_TYPE,
        UMSG_DVB_CA_MESSAGE_ERRCARD_TYPE,
        UMSG_DVB_CA_MESSAGE_UPDATE_TYPE,
        UMSG_DVB_CA_MESSAGE_LOWCARDVER_TYPE,
        UMSG_DVB_CA_MESSAGE_VIEWLOCK_TYPE,
        UMSG_DVB_CA_MESSAGE_MAXRESTART_TYPE,
        UMSG_DVB_CA_MESSAGE_FREEZE_TYPE,
        UMSG_DVB_CA_MESSAGE_CALLBACK_TYPE,
        UMSG_DVB_CA_MESSAGE_STBLOCKED_TYPE,
        UMSG_DVB_CA_MESSAGE_STBFREEZE_TYPE,
        UMSG_DVB_CA_MESSAGE_CARDTESTSTART_TYPE,
        UMSG_DVB_CA_MESSAGE_CARDTESTFAILD_TYPE,
        UMSG_DVB_CA_MESSAGE_CARDTESTSUCC_TYPE,
        UMSG_DVB_CA_MESSAGE_NOCALIBOPER_TYPE,

        /*CA*/
        UMSG_DVB_CA_SHOW_TOP_OSD_MESSAGE,
        UMSG_DVB_CA_HIDE_TOP_OSD_MESSAGE,
        UMSG_DVB_CA_FINGER_PRINT,
        UMSG_DVB_CA_LOCK_SERVICE,
        UMSG_DVB_CA_UNLOCK_SERVICE,
        UMSG_DVB_CA_SCALE_RECEIVEPATCH,
        UMSG_DVB_CA_SCALE_PATCHING,  
        UMSG_DVB_CA_EMAIL_NOTIFY,   //56
        UMSG_DVB_CA_ENTITLE_CHANGED,
        UMSG_DVB_CA_DETITLE_RECEIVED,
        UMSG_DVB_CA_FEEDING_REQUEST,
        UMSG_DVB_CA_HIDE_IPPVDLG,
        UMSG_DVB_CA_START_IPPVDLG,   //61
        UMSG_DVB_CA_SMC_OUT,
        UMSG_DVB_CA_SMC_IN,
        UMSG_DVB_CA_UPDATE_WINDOW,
        UMSG_DVB_CA_CARD_CHANGED,
    	UMSG_DVB_CA_LOCK_SERVICE_ERROR,
    	UMSG_DVB_CA_SHOW_PREVIEW,
        UMSG_DVB_CA_SHOW_BOTTON_OSD_MESSAGE,
        UMSG_DVB_CA_HIDE_BOTTON_OSD_MESSAGE,
    }

    ;
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
            	Log.i("Status", "dvb die, exit UmSmcUpdate");
            	System.exit(1);
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
