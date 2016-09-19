package com.um.ui;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.um.ca.CaShowImgTtxReceiver;
import com.um.ca.DvtAutoFeedCardReceiver;
import com.um.ca.DvtIppvReceiver;
import com.um.ca.TfIppvReceiver;
import com.um.ca.Tf_updatebar;
import com.um.dvb.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
import java.util.List;
import java.util.ArrayList;
import android.view.KeyEvent;
import com.um.controller.AppBaseActivity;


public class Prompt {
	private final String TAG = "Prompt";
	private AlertDialog alertDialog;
	private Object altersync;

	private static Context mConetxt;
	private int flag = 0;
	private LinearLayout mPrompt = null;

	static final int STATUS_TUNER = 1;
	static final int STATUS_CA = 4;
	static final int STATUS_PLAY = 6;
	static final int STATUS_PROG = 7;
	
	public enum subtype {
		UMSG_DVB_NULL,
		/* tuner signal */
		UMSG_DVB_SIGNAL_CHANGE, UMSG_DVB_NO_SIGNAL, UMSG_DVB_WEAK_SIGNAL, UMSG_DVB_STRONG_SIGNAL,
		/* avplay */
		UMSG_DVB_AVPLAY_STOP, UMSG_DVB_AVPLAY_START,
		/* db */
		UMSG_DVB_DB_NIT_VERSION_CHANGE, UMSG_DVB_DB_PROGRAM_NAME_CHANGE, UMSG_DVB_DB_PMT_PID_CHANGE, UMSG_DVB_DB_PROGRAM_BASIC_INFO_CHANGE, UMSG_DVB_DB_PROGRAM_DELETE,
		/* srch */
		UMSG_DVB_SRCH_PROGRESS, UMSG_DVB_SRCH_GET_PROG, UMSG_DVB_SRCH_GET_TP,
		/* epg */
		UMSG_DVB_EPG_PF_EVENTS_UPDATE, UMSG_DVB_EPG_SCH_EVENTS_UPDATE,
		/* loader */
		UMSG_DVB_LOADER_FORCE_UPDATE, UMSG_DVB_LOADER_MANUAL_UPDATE, UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE, UMSG_DVB_LOADER_UPGRADE_ERR, UMSG_DVB_LOADER_UPGRADE_SUCCESS,
		/* CA */
		UMSG_DVB_CA_MESSAGE_CANCEL_TYPE, UMSG_DVB_CA_MESSAGE_BADCARD_TYPE, UMSG_DVB_CA_MESSAGE_EXPICARD_TYPE, UMSG_DVB_CA_MESSAGE_INSERTCARD_TYPE, UMSG_DVB_CA_MESSAGE_NOOPER_TYPE, UMSG_DVB_CA_MESSAGE_BLACKOUT_TYPE, UMSG_DVB_CA_MESSAGE_OUTWORKTIME_TYPE, UMSG_DVB_CA_MESSAGE_WATCHLEVEL_TYPE, UMSG_DVB_CA_MESSAGE_PAIRING_TYPE, UMSG_DVB_CA_MESSAGE_NOENTITLE_TYPE, UMSG_DVB_CA_MESSAGE_DECRYPTFAIL_TYPE, UMSG_DVB_CA_MESSAGE_NOMONEY_TYPE, UMSG_DVB_CA_MESSAGE_ERRREGION_TYPE, UMSG_DVB_CA_MESSAGE_NEEDFEED_TYPE, UMSG_DVB_CA_MESSAGE_ERRCARD_TYPE, UMSG_DVB_CA_MESSAGE_UPDATE_TYPE, UMSG_DVB_CA_MESSAGE_LOWCARDVER_TYPE, UMSG_DVB_CA_MESSAGE_VIEWLOCK_TYPE, UMSG_DVB_CA_MESSAGE_MAXRESTART_TYPE, UMSG_DVB_CA_MESSAGE_FREEZE_TYPE, UMSG_DVB_CA_MESSAGE_CALLBACK_TYPE, UMSG_DVB_CA_MESSAGE_STBLOCKED_TYPE, UMSG_DVB_CA_MESSAGE_STBFREEZE_TYPE, UMSG_DVB_CA_MESSAGE_CARDTESTSTART_TYPE, UMSG_DVB_CA_MESSAGE_CARDTESTFAILD_TYPE, UMSG_DVB_CA_MESSAGE_CARDTESTSUCC_TYPE, UMSG_DVB_CA_MESSAGE_NOCALIBOPER_TYPE,

		/* CA */
		UMSG_DVB_CA_SHOW_TOP_OSD_MESSAGE, UMSG_DVB_CA_HIDE_TOP_OSD_MESSAGE, UMSG_DVB_CA_FINGER_PRINT, UMSG_DVB_CA_LOCK_SERVICE, UMSG_DVB_CA_UNLOCK_SERVICE, UMSG_DVB_CA_SCALE_RECEIVEPATCH, UMSG_DVB_CA_SCALE_PATCHING, UMSG_DVB_CA_EMAIL_NOTIFY, // 56
		UMSG_DVB_CA_ENTITLE_CHANGED, UMSG_DVB_CA_DETITLE_RECEIVED, UMSG_DVB_CA_FEEDING_REQUEST, UMSG_DVB_TFCA_HIDE_IPPVDLG, UMSG_DVB_TFCA_START_IPPVDLG, // 61
		UMSG_DVB_CA_SMC_OUT, UMSG_DVB_CA_SMC_IN, UMSG_DVB_CA_UPDATE_WINDOW, UMSG_DVB_CA_CARD_CHANGED, UMSG_DVB_CA_LOCK_SERVICE_ERROR, UMSG_DVB_CA_SHOW_PREVIEW, UMSG_DVB_CA_SHOW_BOTTON_OSD_MESSAGE, UMSG_DVB_CA_HIDE_BOTTON_OSD_MESSAGE,

		/* loader */
		UMSG_DVB_LOADER_UPGRADE_SEARCH_CONTROLHEAD_DATE, UMSG_DVB_LOADER_UPGRADE_SEARCH_PARTINFO_DATE, UMSG_DVB_LOADER_UPGRADE_SEARCH_DATAGRAM_DATE, UMSG_DVB_LOADER_UPGRADE_CHECK_DATE, UMSG_DVB_LOADER_UPGRADE_CONTROLHEAD_IS_RECIEVED, UMSG_DVB_LOADER_UPGRADE_PARTION_IS_RECIEVED, UMSG_DVB_LOADER_UPGRADE_DATAGRAM_IS_RECIEVED, UMSG_DVB_LOADER_UPGRADE_CHECK_CRC_SCHEDULE, UMSG_DVB_LOADER_UPGRADE_FACID_ERROR, UMSG_DVB_LOADER_UPGRADE_HW_ERROR, UMSG_DVB_LOADER_UPGRADE_SW_ERROR, UMSG_DVB_LOADER_UPGRADE_SN_ERROR, UMSG_DVB_LOADER_UPGRADE_CHECK_OK, UMSG_DVB_LOADER_UPGRADE_LOCK_FAILED, UMSG_DVB_LOADER_UPGRADE_TIME_UP, UMSG_DVB_LOADER_UPGRADE_CRC_ERROR, UMSG_DVB_LOADER_UPGRADE_MALLOC_ERROR, UMSG_DVB_LOADER_UPGRADE_UNKNOWN_ERROR,

		UMSG_DVB_DVTCA_RATING_TOO_LOW, UMSG_DVB_DVTCA_NOT_IN_WATCH_TIME, UMSG_DVB_DVTCA_NOT_PAIRED, UMSG_DVB_DVTCA_IS_PAIRED_OTHER, UMSG_DVB_DVTCA_PLEASE_INSERT_CARD, UMSG_DVB_DVTCA_NO_ENTITLE, UMSG_DVB_DVTCA_PRODUCT_RESTRICT, UMSG_DVB_DVTCA_AREA_RESTRICT, UMSG_DVB_DVTCA_MOTHER_RESTRICT, UMSG_DVB_DVTCA_NO_MONEY, UMSG_DVB_DVTCA_IPP_NO_CONFIRM, UMSG_DVB_DVTCA_IPP_NO_BOOK, UMSG_DVB_DVTCA_IPPT_NO_CONFIRM, UMSG_DVB_DVTCA_IPPT_NO_BOOK, UMSG_DVB_DVTCA_IPPV_NO_MONEY, UMSG_DVB_DVTCA_IPPT_NO_MONEY, UMSG_DVB_DVTCA_DATA_INVALID, UMSG_DVB_DVTCA_SLOT_NOT_FOUND, UMSG_DVB_DVTCA_SC_NOT_SERVER, UMSG_DVB_DVTCA_TVS_NOT_FOUND, UMSG_DVB_DVTCA_KEY_NOT_FOUND, UMSG_DVB_DVTCA_IPPNEED_CALLBACK, UMSG_DVB_DVTCA_CANCEL_PROMTMSG,

		UMSG_DVB_DVTCA_MSG_HIDEPROMPTMSG, UMSG_DVB_DVTCA_MSG_FINGER_PRINT, UMSG_DVB_DVTCA_MSG_LOCK_SERVICE, UMSG_DVB_DVTCA_MSG_UNLOCK_SERVICE, UMSG_DVB_DVTCA_MSG_SHOW_OSD_MESSAGE, UMSG_DVB_DVTCA_MSG_EMAIL_NOTIFY, UMSG_DVB_DVTCA_MSG_START_IPPVDLG, UMSG_DVB_DVTCA_MSG_SMC_OUT, UMSG_DVB_DVTCA_MSG_SMC_IN, UMSG_DVB_DVTCA_MSG_HIDE_URGENT_BROADCAST, UMSG_DVB_DVTCA_MSG_SHOW_URGENT_BROADCAST, UMSG_DVB_DVTCA_MSG_CARD_CHANGED, UMSG_DVB_DVTCA_MSG_AUTO_FD, UMSG_DVB_DVTCA_MSG_PDSD_VALUE_CHANGE,

		UMSG_DVB_DVTCA_DECRYPT_FAILURE, UMSG_DVB_DVTCA_MESSAGE_SHOW_PREVIEW, UMSG_DVB_DVTCA_MESSAGE_CLOSE_PREVIEW, UMSG_DVB_DVTCA_START_IPPVDLG, UMSG_DVB_DVTCA_HIDE_IPPVDLG, UMSG_DVB_DVTCA_SHOW_BOTTON_OSD_MESSAGE, UMSG_DVB_DVTCA_AUTO_FEED_CARD, UMSG_DVB_DVTCA_SMC_IN, UMSG_DVB_DVTCA_SMC_OUT, UMSG_DVB_DVTCA_LOCK_SERVICE, UMSG_DVB_DVTCA_UNLOCK_SERVICE,
		UMSG_DVB_DVNCA_CARD_CHANGED,UMSG_DVB_DVNCA_SHOW_OSD_MESSAGE,UMSG_DVB_DVNCA_CARD_PAIR_START,UMSG_DVB_DVNCA_CARD_PAIR_OK,UMSG_DVB_DVNCA_CARD_PAIR_FAIL,
		UMSG_DVB_DVNCA_CARD_NOT_PAIR,UMSG_DVB_DVNCA_RIGHT_EXPIRE,UMSG_DVB_DVNCA_NO_RIGHT,UMSG_DVB_DVNCA_STB_ON,UMSG_DVB_DVNCA_STB_OFF,UMSG_DVB_DVNCA_ECM_ERROR,UMSG_DVB_DVNCA_CHECK_PASSWORD,
		UMSG_DVB_DVNCA_WRT_USER_INFO,UMSG_DVB_DVNCA_SYS_UPDATING,UMSG_DVB_DVNCA_SET_PASSWORD,UMSG_DVB_DVNCA_AREA_ERROR,UMSG_DVB_DVNCA_STB_LOCKED,
		UMSG_DVB_DVNCA_CARD_SYS_PAIR,UMSG_DVB_DVNCA_BLACKOUT,UMSG_DVB_DVNCA_NOENTITLE,UMSG_DVB_DVNCA_EXPICARD,UMSG_DVB_DVNCA_INSERTCARD,UMSG_DVB_DVNCA_BADCARD,UMSG_DVB_DVNCA_DONOT_TELEVIEW_PPV,UMSG_DVB_DVNCA_APP_LOCKED,UMSG_DVB_DVNCA_MSG_FINGER_PRINT, UMSG_DVNCA_MSG_PAIR_REBOOT,UMSG_DVNCA_MSG_DEPAIR_REBOOT,
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

	static final int AVPLAY_STOP = 5;
	static final int AVPLAY_START = 6;

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
	private List<Integer> saveMessage = new ArrayList<Integer>();
	private static Prompt mInstance = null;
	
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

	private void initWindow(Context context) {
		mPrompt = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.status_prompt, null);
		mPrompt.setVisibility(View.GONE);

		WindowManager windowManager = (WindowManager) context
				.getSystemService(Activity.WINDOW_SERVICE);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCHABLE;
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.x = 0;
		layoutParams.y = 0;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.format = PixelFormat.RGBA_8888;
		windowManager.addView(mPrompt, layoutParams);
	}

	private void deinitWindow(Context context) {
		if (mPrompt != null) {
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Activity.WINDOW_SERVICE);
			windowManager.removeView(mPrompt);
			mPrompt = null;
			Log.v("Status", "revome view.");
		}
	}

	private void showPrompt(int resId) {
		if (mPrompt != null && enablePromptShow) {
			if (resId == R.string.altert_text_no_signal) {
				mPrompt.setVisibility(View.GONE);
			} else {
				TextView text = (TextView) mPrompt
						.findViewById(R.id.tv_prompt_content);
				text.setText(resId);
				mPrompt.setVisibility(View.VISIBLE);
			}
		}
	}

	private void hidePrompt() {
		if (mPrompt != null) {
			mPrompt.setVisibility(View.GONE);
		}
	}

	public void attachContext(Activity c) {
		hidePrompt();
		registerCaReceivers(c);
		mConetxt = c;
		initWindow(c);
		curResID = 0;
		status_mask = 0;
		
		Log.v("Status", "attach context, enablePromptShow="+enablePromptShow);
	}

	public void detachContext() {
		hidePrompt();
		deinitWindow(mConetxt);
		unregisterCaReceivers(mConetxt);
		mConetxt = null;
		Log.v("Status", "dettach context");
	}

    private boolean enablePromptShow = true;
	public void enablePromptShow(boolean enable) {
        enablePromptShow = enable;
		if (enable) {
			if (curResID != 0) {
				showPrompt(curResID);
			}
		} else {
			hidePrompt();
		}
	}

	public void handleMessage(Message msg) {
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
			if (msg.arg1 == SMC_IN && curResID == R.string.tf_insert_card) {
				return true;
			}
			return (msg.arg1 == CANCEL_TYPE) ? true : false;

		case STATUS_PLAY:
			return (msg.arg1 == AVPLAY_START) ? true : false;

		case STATUS_TUNER:
			return (msg.arg1 == STRONG_SIGNAL) ? true : false;

		default:
			return false;
		}
	}

	private int getResID(int status) {
		int msgid = saveMessage.get(status);
		Log.i("Status", "getResID:" + msgid);
		Log.i("Status", "getResID1:" + status);

		switch (status) {
		case STATUS_CA: {
			if (msgid == subtype.UMSG_DVB_DVTCA_RATING_TOO_LOW.ordinal()) {
				return R.string.dvt_rate_too_low;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_NOT_IN_WATCH_TIME
					.ordinal()) {
				return R.string.dvt_not_in_watch_time;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_NOT_PAIRED.ordinal()) {
				return R.string.dvt_not_paired;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IS_PAIRED_OTHER
					.ordinal()) {
				return R.string.dvt_is_paired_other;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_PLEASE_INSERT_CARD
					.ordinal()) {
				return R.string.dvt_insert_card;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_NO_ENTITLE.ordinal()) {
				return R.string.dvt_no_entitle;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_PRODUCT_RESTRICT
					.ordinal()) {
				return R.string.dvt_product_restrict;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_AREA_RESTRICT.ordinal()) {
				return R.string.dvt_area_restrict;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_MOTHER_RESTRICT
					.ordinal()) {
				// return R.string.dvt_mother_restrict;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_NO_MONEY.ordinal()) {
				return R.string.dvt_no_money;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPP_NO_CONFIRM.ordinal()) {
				return R.string.dvt_ippv_no_confirm;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPP_NO_BOOK.ordinal()) {
				return R.string.dvt_ippv_no_book;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPPT_NO_CONFIRM
					.ordinal()) {
				return R.string.dvt_ippt_no_confirm;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPPT_NO_BOOK.ordinal()) {
				return R.string.dvt_ippt_no_book;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPPV_NO_MONEY.ordinal()) {
				return R.string.dvt_ippv_no_money;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPPT_NO_MONEY.ordinal()) {
				return R.string.dvt_ippt_no_money;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_SLOT_NOT_FOUND.ordinal()) {
				return R.string.dvt_slot_not_found;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_SC_NOT_SERVER.ordinal()) {
				return R.string.dvt_sc_not_server;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_TVS_NOT_FOUND.ordinal()) {
				return R.string.dvt_tvs_not_found;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_KEY_NOT_FOUND.ordinal()) {
				return R.string.dvt_key_not_found;
			} else if (msgid == subtype.UMSG_DVB_DVTCA_IPPNEED_CALLBACK
					.ordinal()) {
				return R.string.dvt_ipp_need_callback;
			}
//dvn ÏûÏ¢
			else if (msgid == subtype.UMSG_DVB_DVNCA_CARD_PAIR_START.ordinal())
			{
				return R.string.dvn_card_pair_start;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_CARD_PAIR_OK.ordinal())
			{
				return R.string.dvn_card_pair_ok;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_CARD_PAIR_FAIL.ordinal())
			{
				return R.string.dvn_card_pair_fail;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_CARD_NOT_PAIR.ordinal())
			{
				return R.string.dvn_card_not_pair;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_RIGHT_EXPIRE.ordinal())
			{
				return R.string.dvn_right_expire;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_NO_RIGHT.ordinal())
			{
				return R.string.dvn_no_right;
			}
//					else if (msgid == subtype.UMSG_DVB_DVNCA_STB_ON.ordinal())
//					{
//						return R.string.dvn_stb_on;
//					}
//					else if (msgid == subtype.UMSG_DVB_DVNCA_STB_OFF.ordinal())
//					{
//						return R.string.dvn_stb_off;
//					}
			else if (msgid == subtype.UMSG_DVB_DVNCA_ECM_ERROR.ordinal())
			{
				return R.string.dvn_ecm_error;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_CHECK_PASSWORD.ordinal())
			{
				return R.string.dvn_check_password;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_WRT_USER_INFO.ordinal())
			{
				return R.string.dvn_wrt_user_info;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_SYS_UPDATING.ordinal())
			{
				return R.string.dvn_sys_updating;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_SET_PASSWORD.ordinal())
			{
				return R.string.dvn_set_password;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_AREA_ERROR.ordinal())
			{
				return R.string.dvn_area_error;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_STB_LOCKED.ordinal())
			{
				return R.string.dvn_stb_locked;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_CARD_SYS_PAIR.ordinal())
			{
				return R.string.dvn_card_sys_pair;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_BLACKOUT.ordinal())
			{
				return R.string.dvn_blackout;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_NOENTITLE.ordinal())
			{
				return R.string.dvn_noentitle;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_EXPICARD.ordinal())
			{
				return R.string.dvn_expicard;
			}
			else if (msgid == subtype.UMSG_DVNCA_MSG_PAIR_REBOOT.ordinal())
			{
				return R.string.dvn_reboot;
			}
			else if (msgid == subtype.UMSG_DVNCA_MSG_DEPAIR_REBOOT.ordinal())
			{
				return R.string.dvn_dp_reboot;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_INSERTCARD.ordinal())
			{
				return R.string.dvn_insert_card;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_BADCARD.ordinal())
			{
				return R.string.dvn_bad_card;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_DONOT_TELEVIEW_PPV.ordinal())
			{
				return R.string.dvn_donot_teleview_ppv;
			}
			else if (msgid == subtype.UMSG_DVB_DVNCA_APP_LOCKED.ordinal())
			{
				return R.string.dvn_app_locked;
			}







			switch (msgid) {
			case BADCARD_TYPE:
				return R.string.tf_badcard_type;

			case EXPICARD_TYPE:
				return R.string.tf_expicard_type;

			case INSERTCARD_TYPE:
				return R.string.tf_insert_card;

			case NOOPER_TYPE:
				return R.string.tf_nooper_type;

			case BLACKOUT_TYPE:
				return R.string.tf_blackout_type;

			case OUTWORKTIME_TYPE:
				return R.string.tf_outworktime_type;

			case WATCHLEVEL_TYPE:
				return R.string.tf_watchlevel_type;

			case PAIRING_TYPE:
				return R.string.tf_pairing_type;

			case NOENTITLE_TYPE:
				return R.string.tf_noentitle_type;

			case DECRYPTFAIL_TYPE:
				return R.string.tf_decryptfail_type;

			case NOMONEY_TYPE:
				return R.string.tf_nomoney_type;

			case ERRREGION_TYPE:
				return R.string.tf_erregion_type;

			case NEEDFEED_TYPE:
				return R.string.tf_needfeed_type;

			case ERRCARD_TYPE:
				return R.string.tf_card_error;

			case UPDATE_TYPE:
				return R.string.tf_update_type;

			case LOWCARDVER_TYPE:
				return R.string.tf_lowcardver;

			case VIEWLOCK_TYPE:
				return R.string.tf_viewlock_type;

			case MAXRESTART_TYPE:
				return R.string.tf_maxrestart_type;

			case FREEZE_TYPE:
				return R.string.tf_freeze_type;

			case CALLBACK_TYPE:
				return R.string.tf_callback_type;

			case STBLOCKED_TYPE:
				return R.string.tf_stblocked_type;

			case STBFREEZE_TYPE:
				return R.string.tf_stbfreeze_type;

			case CARDTESTSTART_TYPE:
				return R.string.tf_cardteststart_type;

			case CARDTESTFAILD_TYPE:
				return R.string.tf_cardtestfail_type;

			case CARDTESTSUCC_TYPE:
				return R.string.tf_cardtestsucc;

			case NOCALIBOPER_TYPE:
				return R.string.tf_nocaliboper_type;
			case START_IPPVDLG: {

				break;

			}
			case HIDE_IPPVDLG: {

				break;
			}
			default:
				break;
			}
		}

		case STATUS_PLAY:
			Log.i("Status", "getResID2:" + status);
			return (msgid == AVPLAY_STOP) ? R.string.prog_stop_play : 0;

		case STATUS_TUNER:
			Log.i("Status", "getResID3:" + status);
			return (msgid == STRONG_SIGNAL) ? 0
					: R.string.altert_text_no_signal;

		default:
			Log.i("Status", "getResID4:" + status);
			return 0;
		}
	}

	private void saveResID(Message msg) {
		saveMessage.set(msg.what, msg.arg1);
	}

	private void doData(Message msg) {
		byte[] byteinfo = (byte[]) msg.obj;

		// Tf_updatebar update = new Tf_updatebar();
		String progname;
		Log.i("Status", "doData!");

		Log.i("Status", "msg.what:" + msg.what);
		Log.i("Status", "msg.arg1:" + msg.arg1);
		if (byteinfo != null) {
			Log.i("Status", "byteinfo[0]:" + (byteinfo[0] & 0xff));
			Log.i("Status", "byteinfo[1]:" + (byteinfo[1] & 0xff));
			Log.i("Status", "byteinfo[2]:" + (byteinfo[2] & 0xff));
			Log.i("Status", "byteinfo[3]:" + (byteinfo[3] & 0xff));
		}

		switch (msg.what) {
		case 2:
			if (msg.arg1 == subtype.UMSG_DVB_SRCH_PROGRESS.ordinal()) {
				/*
				 * int process =
				 * byteinfo[3]<<24|byteinfo[2]<<16|byteinfo[1]<<8|byteinfo[0];
				 * Log.i("Status", "process is:"+process);
				 * Search.getSearchInstance().onUpdateProcess(process);
				 * Log.i("Status", "2 process is:"+process);
				 */
			} else if (msg.arg1 == subtype.UMSG_DVB_SRCH_GET_PROG.ordinal()) {
				/*
				 * try { progname = new String(byteinfo, 16, 20, "UnicodeBig");
				 * } catch (UnsupportedEncodingException e1) { progname = "";
				 * e1.printStackTrace(); }
				 * 
				 * int progtype =
				 * byteinfo[11]<<24|byteinfo[10]<<16|byteinfo[9]<<8|byteinfo[8];
				 * Log.i("Status","get prog name is:"+progname +progtype);
				 * Search.getSearchInstance().onGetProgram(progtype, progname);
				 */
			} else if (msg.arg1 == subtype.UMSG_DVB_SRCH_GET_TP.ordinal()) {
				/*
				 * String sfre = new String(byteinfo); int fre = 0;
				 * 
				 * Log.i("Status","UMSG_DVB_SRCH_GET_TP sfre:"+sfre); try { fre
				 * = Integer.valueOf(sfre); } catch (Exception e) {
				 * e.printStackTrace(); }
				 * 
				 * Log.i("Status","get fre:"+fre);
				 * Search.getSearchInstance().onGetTpInfo(fre/100);
				 */
			}
			break;
		case 3:
			if (msg.arg1 == subtype.UMSG_DVB_EPG_PF_EVENTS_UPDATE.ordinal()) {
				Log.v("Status", "UMSG_DVB_EPG_PF_EVENTS_UPDATE");
				Intent intent = new Intent(
						"com.um.dvbstack.UMSG_DVB_EPG_PF_EVENTS_UPDATE");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_EPG_SCH_EVENTS_UPDATE
					.ordinal()) {
				Log.v("Status", "UMSG_DVB_EPG_SCH_EVENTS_UPDATE");
				Intent intent = new Intent(
						"com.um.dvbstack.UMSG_DVB_EPG_SCH_EVENTS_UPDATE");
				mConetxt.sendBroadcast(intent);
			}
			break;
		case 4:

			Log.e("Status", "CA Data msg.arg1" + msg.arg1);
			Log.e("Status", "CA Data msg.arg1" + msg.arg1);

			if (msg.arg1 == subtype.UMSG_DVB_CA_EMAIL_NOTIFY.ordinal()) {
				Intent intent = new Intent();
				intent.setAction("com.um.dvb.CHECK_EMAIL");
				mConetxt.sendBroadcast(intent);
			}

			else if (msg.arg1 == subtype.UMSG_DVB_CA_LOCK_SERVICE.ordinal()) {
				int prog_id = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | ((byteinfo[0] & 0xff));

				Log.v("Status", "UMSG_DVB_CA_LOCK_SERVICE prog_id: " + prog_id);
				Intent intent = new Intent();
				intent.setAction("com.um.umdvb.UMSG_DVB_CA_LOCK_SERVICE");
				Bundle bundle = new Bundle();
				bundle.putInt("progId", prog_id);
				intent.putExtras(bundle);
				mConetxt.sendBroadcast(intent);

			} else if (msg.arg1 == subtype.UMSG_DVB_CA_UNLOCK_SERVICE.ordinal()) {
				Intent intent = new Intent();
				intent.setAction("com.um.umdvb.UMSG_DVB_CA_UNLOCK_SERVICE");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_CA_LOCK_SERVICE_ERROR
					.ordinal()) {
				Toast.makeText(
						mConetxt,
						mConetxt.getResources().getString(
								R.string.tf_lock_service_error),
						Toast.LENGTH_LONG).show();
			} else if (msg.arg1 == subtype.UMSG_DVB_CA_FEEDING_REQUEST
					.ordinal()) {
				int feed_status = byteinfo[3] << 24 | byteinfo[2] << 16
						| byteinfo[1] << 8 | byteinfo[0];
				if (feed_status == 0) {
					Toast.makeText(
							mConetxt,
							mConetxt.getResources().getString(
									R.string.tf_read_parentcard_fail),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							mConetxt,
							mConetxt.getResources().getString(
									R.string.tf_insert_child_card),
							Toast.LENGTH_LONG).show();
				}
			} else if (msg.arg1 == subtype.UMSG_DVB_TFCA_START_IPPVDLG
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_TFCA_START_IPPVDLG");
				Intent intent = new Intent("com.um.dvb.START_TF_IPPV");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_TFCA_HIDE_IPPVDLG.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_TFCA_HIDE_IPPVDLG");
				Intent intent = new Intent("com.um.dvb.STOP_TF_IPPV");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_CA_DETITLE_RECEIVED
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_CA_DETITLE_RECEIVED");

				Intent intent = new Intent();
				intent.setAction("com.um.dvb.CHECK_DETITLE");
				mConetxt.sendBroadcast(intent);

			}

			else if (msg.arg1 == subtype.UMSG_DVB_CA_FINGER_PRINT.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_CA_FINGER_PRINT");

				int finger_id = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);

				SharedPreferences preferences = mConetxt.getSharedPreferences(
						"CA_FINGERID", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putInt("CA_FINGERID", finger_id);
				editor.commit();

				Intent intent = new Intent("com.um.dvb.CHECK_FINGER");
				mConetxt.sendBroadcast(intent);

			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_MSG_FINGER_PRINT
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_CA_FINGER_PRINT");

				int duration = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);
				int finger_id = ((byteinfo[7] & 0xff) << 24)
						| ((byteinfo[6] & 0xff) << 16)
						| ((byteinfo[5] & 0xff) << 8) | (byteinfo[4] & 0xff);
				Log.v("GetEventData", "duration:" + duration);
				Log.v("GetEventData", "finger_id:" + finger_id);

				SharedPreferences preferences = mConetxt.getSharedPreferences(
						"CA_FINGERID", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putInt("CA_FINGERID", finger_id);
				editor.commit();
				editor.putInt("CA_DURATION", duration);
				editor.commit();

				Intent intent = new Intent("com.um.dvb.DVTCHECK_FINGER");
				mConetxt.sendBroadcast(intent);

			} else if (msg.arg1 == subtype.UMSG_DVB_CA_SHOW_PREVIEW.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_CA_SHOW_PREVIEW");

				int previewCode = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);
				Log.v("GetEventData", "previewCode:" + previewCode);

				SharedPreferences preferences = mConetxt.getSharedPreferences(
						"CA_PREVIEWCODE", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putInt("CA_PREVIEWCODE", previewCode);
				editor.commit();

				Intent intent = new Intent();
				intent.setAction("com.um.dvb.CHECK_PREVIEW");
				mConetxt.sendBroadcast(intent);

			}
			/********** DVT CA **********/
			else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_START_IPPVDLG.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_START_IPPVDLG");

				int ecmPid = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);
				Log.v("GetEventData", "ecmPid:" + ecmPid);

				SharedPreferences preferences = mConetxt.getSharedPreferences(
						"DVTCA_ECMPID", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putInt("DVTCA_ECMPID", ecmPid);
				editor.commit();

				Intent intent = new Intent("com.um.dvb.START_DVT_IPPV");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_AUTO_FEED_CARD
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_AUTO_FEED_CARD");

				Intent intent = new Intent("com.um.dvb.DVTCA_AUTO_FEED_CARD");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_SMC_IN.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_SMC_IN");

				Intent intent = new Intent("com.um.dvb.DVTCA_SMC_IN");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_SMC_OUT.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_SMC_OUT");

				Intent intent = new Intent("com.um.dvb.DVTCA_SMC_OUT");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_CA_SHOW_PREVIEW.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_CA_SHOW_PREVIEW");

				int previewCode = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);
				Log.v("GetEventData", "previewCode:" + previewCode);

				SharedPreferences preferences = mConetxt.getSharedPreferences(
						"CA_PREVIEWCODE", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putInt("CA_PREVIEWCODE", previewCode);
				editor.commit();

				Intent intent = new Intent();
				intent.setAction("com.um.dvb.CHECK_PREVIEW");
				mConetxt.sendBroadcast(intent);

			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_MESSAGE_SHOW_PREVIEW
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_MESSAGE_SHOW_PREVIEW");

				Intent intent = new Intent();
				intent.setAction("com.um.dvb.DVTCHECK_PREVIEW");
				mConetxt.sendBroadcast(intent);

			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_MESSAGE_CLOSE_PREVIEW
					.ordinal()) {
				Log.v("GetEventData", "UMSG_DVB_DVTCA_MESSAGE_CLOSE_PREVIEW");
				Intent intent = new Intent();
				intent.setAction("com.um.dvb.DVTCLOSE_PREVIEW");
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_LOCK_SERVICE
					.ordinal()) {
				int prog_id = ((byteinfo[3] & 0xff) << 24)
						| ((byteinfo[2] & 0xff) << 16)
						| ((byteinfo[1] & 0xff) << 8) | (byteinfo[0] & 0xff);
				int duration = ((byteinfo[7] & 0xff) << 24)
						| ((byteinfo[6] & 0xff) << 16)
						| ((byteinfo[5] & 0xff) << 8) | (byteinfo[4] & 0xff);
				Log.v("Status", "UMSG_DVB_DVTCA_LOCK_SERVICE prog_id:"
						+ prog_id);
				Log.v("Status", "UMSG_DVB_DVTCA_LOCK_SERVICE duration:"
						+ duration);
				Intent intent = new Intent();
				intent.setAction("com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE");
				Bundle bundle = new Bundle();
				bundle.putInt("progId", prog_id);
				bundle.putInt("duration", duration);
				intent.putExtras(bundle);
				mConetxt.sendBroadcast(intent);
			} else if (msg.arg1 == subtype.UMSG_DVB_DVTCA_UNLOCK_SERVICE
					.ordinal()) {
				Intent intent = new Intent();
				intent.setAction("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
				mConetxt.sendBroadcast(intent);

			}
			break;
		case 5:
			if (msg.arg1 == subtype.UMSG_DVB_LOADER_FORCE_UPDATE.ordinal()
					|| msg.arg1 == subtype.UMSG_DVB_LOADER_MANUAL_UPDATE
							.ordinal()) {
				/*
				 * Log.v(TAG,"UMSG_DVB_LOADER_FORCE_UPDATE......"); Intent it =
				 * new Intent(mConetxt,UpgradeActivity.class);
				 * mConetxt.startActivity(it);
				 */
			} else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE
					.ordinal()) {
				/*
				 * Log.v(TAG,"UMSG_DVB_LOADER_UPGRADE_DOWNLOAD_SCHEDULE......");
				 * String sprogress = new String(byteinfo); int progress = 0;
				 * try{ progress = Integer.valueOf(sprogress); } catch
				 * (Exception e) { e.printStackTrace(); }
				 * if(UpgradeActivity.getInstance()!=null) {
				 * UpgradeActivity.getInstance().setUpgradeProgress(progress); }
				 */
			} else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_ERR
					.ordinal()) {
				/*
				 * Log.v(TAG,"UMSG_DVB_LOADER_UPGRADE_ERR......");
				 * if(UpgradeActivity.getInstance()!=null) {
				 * UpgradeActivity.getInstance().upgradeFail();
				 * 
				 * }
				 */
			} else if (msg.arg1 == subtype.UMSG_DVB_LOADER_UPGRADE_SUCCESS
					.ordinal()) {
				/*
				 * Log.v(TAG,"UMSG_DVB_LOADER_UPGRADE_SUCCESS......");
				 * if(UpgradeActivity.getInstance()!=null) {
				 * UpgradeActivity.getInstance().upgradeSucess();
				 * 
				 * }
				 */
			}
			break;
		default:
			break;
		}
	}

	private void doAlertMessage(Message msg) {
		int mask = getMask(msg);
		boolean clear = isClearMsg(msg);
		Log.i(TAG, "doAlertMessage()" + msg.what + "," + msg.arg1);
		if (clear) {
			status_mask &= ~mask;
		} else {
			status_mask |= mask;
			saveResID(msg);
		}

		int i = 0;

		for (i = 0; i < STATUS_COUNT; i++) {
			if ((status_mask & (1 << i)) != 0) {
				int resId = getResID(i);
				Log.i(TAG, "resId="+resId+ ",curResID="+curResID);
				if (resId != 0 && curResID != resId) {
					curResID = resId;
					showPrompt(resId);
				}

				return;
			}
		}

		curResID = 0;
		hidePrompt();
	}
	private CaShowImgTtxReceiver mCaShowImgTtxReceiver = null;
	private DvtAutoFeedCardReceiver mDvtAutoFeedCardReceiver = null;
	private DvtIppvReceiver mDvtIppvReceiver = null;
	private Tf_updatebar mTf_updatebar = null;
	private TfIppvReceiver mTfIppvReceiver = null;
	void registerCaReceivers(Activity activity) {
		IntentFilter intentFilter = null;
		mCaShowImgTtxReceiver = new CaShowImgTtxReceiver(activity);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.dvb.CHECK_EMAIL");
		intentFilter.addAction("com.um.dvb.SHOW_EMAIL_OR_NOT");
		intentFilter.addAction("com.um.dvb.CHECK_DETITLE");
		intentFilter.addAction("com.um.dvb.CHECK_PREVIEW");
		intentFilter.addAction("com.um.dvb.CHECK_FINGER");
		intentFilter.addAction("com.um.dvb.DVTCHECK_FINGER");
		intentFilter.addAction("com.um.dvb.DVTCHECK_PREVIEW");
		intentFilter.addAction("com.um.dvb.DVTCLOSE_PREVIEW");
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE");
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
		activity.registerReceiver(mCaShowImgTtxReceiver, intentFilter);
		mDvtAutoFeedCardReceiver = new DvtAutoFeedCardReceiver(activity);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.dvb.DVTCA_AUTO_FEED_CARD");
		intentFilter.addAction("com.um.dvb.STOP_AUTO_FEED_CARD");
		intentFilter.addAction("com.um.dvb.DVTCA_SMC_IN");
		intentFilter.addAction("com.um.dvb.DVTCA_SMC_OUT");
		activity.registerReceiver(mDvtAutoFeedCardReceiver, intentFilter);
		mDvtIppvReceiver = new DvtIppvReceiver(activity);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.dvb.START_DVT_IPPV");
		intentFilter.addAction("com.um.dvb.STOP_IPPV");
		activity.registerReceiver(mDvtIppvReceiver, intentFilter);
		mTf_updatebar = new Tf_updatebar(activity);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_CA_START_PROGRESS_RECEIVEPATCH");
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_CA_STOP_PROGRESS_RECEIVEPATCH");
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_CA_START_PROGRESS_PATCHING");
		intentFilter.addAction("com.um.umdvb.UMSG_DVB_CA_STOP_PROGRESS_PATCHING");
		activity.registerReceiver(mTf_updatebar, intentFilter);
		mTfIppvReceiver = new TfIppvReceiver(activity);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.dvb.START_TF_IPPV");
		intentFilter.addAction("com.um.dvb.STOP_TF_IPPV");
		intentFilter.addAction("com.um.dvb.STOP_IPPV");
		activity.registerReceiver(mTfIppvReceiver, intentFilter);
	}
	void unregisterCaReceivers(Context context) {
		if (mCaShowImgTtxReceiver != null) {
			context.unregisterReceiver(mCaShowImgTtxReceiver);
			mCaShowImgTtxReceiver = null;
		}
		if (mDvtAutoFeedCardReceiver != null) {
			context.unregisterReceiver(mDvtAutoFeedCardReceiver);
			mDvtAutoFeedCardReceiver = null;
		}
		if (mDvtIppvReceiver != null) {
			context.unregisterReceiver(mDvtIppvReceiver);
			mDvtIppvReceiver = null;
		}
		if (mTf_updatebar != null) {
			context.unregisterReceiver(mTf_updatebar);
			mTf_updatebar = null;
		}
		if (mTfIppvReceiver != null) {
			context.unregisterReceiver(mTfIppvReceiver);
			mTfIppvReceiver = null;
		}
	}

	public void sendShoweMailOrNot(){
		Intent intent = new Intent();
		intent.setAction("com.um.dvb.SHOW_EMAIL_OR_NOT");
		if (mConetxt == null) {
			return;
		}
		mConetxt.sendBroadcast(intent);
	}
}
