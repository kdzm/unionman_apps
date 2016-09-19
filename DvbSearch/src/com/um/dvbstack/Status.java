package com.um.dvbstack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class Status {
	private static final String TAG = "Status";
	
	private StatusHandler handler = new StatusHandler(Looper.getMainLooper());
	private DVB mDVB = null;

	private static Status mInstance = null;

	private List<StatusListener> mStatusListenerList = new ArrayList<StatusListener>();

    static final int STATUS_TUNER	= 1;
    static final int STATUS_CA		= 4;
    static final int STATUS_PLAY	= 3;
	
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
		UMSG_DVB_CA_SHOW_OSD_MESSAGE , 
		UMSG_DVB_CA_HIDE_OSD_MESSAGE ,  
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
    };
    
	private Status() {

	}

	public interface StatusListener {
		void OnMessage(Message msg);
	}

	public boolean addStatusListener(StatusListener ls) {
		if (ls == null)
			return false;

		if (mStatusListenerList.contains(ls)) {
			return false;
		}
		mStatusListenerList.add(ls);
		return true;
	}

	public void removeStatusListener(StatusListener ls) {
		if (ls == null)
			return;

		mStatusListenerList.remove(ls);
	}

	public void startListen(DVB dvb) {
		WeakReference<Status> obj = new WeakReference<Status>(this);
		startListnerStatus(obj, dvb.mNativeContext, true);
		mDVB = dvb;
	}

	public void stopListen() {
		if (mDVB != null) {
			WeakReference<Status> obj = new WeakReference<Status>(this);
			startListnerStatus(obj, mDVB.mNativeContext, false);
		}
		mStatusListenerList.clear();
		Log.v("Status", "dettach context");
	}

	synchronized public static Status getInstance() {
		if (mInstance == null) {
			mInstance = new Status();
		}
		return mInstance;
	}

	private static byte[] AllocData(int len) {
		return new byte[len];
	}

	private static void SendMessage(Object statusRef, int type, int param,
			byte[] data, int len) {
		Log.i("Status", "Send message data!");
		Status status = (Status) ((WeakReference<?>) statusRef).get();
		if (status == null) {
			return;
		}

		synchronized (status) {
			if (status.handler != null) {
				Message msg = status.handler.obtainMessage(type, param, len,
						data);

				status.handler.sendMessage(msg);
				Log.i("Status", "handler Send message data!");
			}
		}
	}

	private static void SendMessage(Object statusRef, int type, int param1,
			int param2) {
		Status status = (Status) ((WeakReference<?>) statusRef).get();
		if (status == null) {
			return;
		}

		synchronized (status) {
			if (status.handler != null) {
				Message msg = status.handler
						.obtainMessage(type, param1, param2);

				status.handler.sendMessage(msg);
			}
		}
	}

	private native void startListnerStatus(Object status_this, int dvbHandle,
			boolean flag);

	private class StatusHandler extends Handler {

		StatusHandler(Looper lp) {
			super(lp);
		}

		public void handleMessage(Message msg) {
			Log.i("Status", "handleMessage!");
			if (msg.what == 0) {
				System.exit(1);
				return;
			}

			for (StatusListener ls : mStatusListenerList) {
				if (ls != null) {
					ls.OnMessage(msg);
				}
			}
		}
	}
}
