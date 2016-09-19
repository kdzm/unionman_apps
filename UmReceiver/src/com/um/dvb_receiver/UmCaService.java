package com.um.dvb_receiver;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.unionman.jazzlib.*;
import android.util.Log;
import android.widget.Toast;

import com.um.dvb_receiver.CaInfoAccessor.EntitleInfo;
import com.um.dvbstack.BaseDate;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Prog;
import com.um.dvbstack.Prog.Epg_LocalTime;
import com.um.dvbstack.ReceiverMsgInterface;
import com.um.dvbstack.Status;
import com.um.ui.Prompt;
import com.um.umreceiver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yhj 1、首先去不断获取TDT时间 2、获取CA卡的状态； 3、获取运营商的信息； 4、获取数据包的信息； 5、判断节目是否过期；
 *         收到卡的状态的广播，则重走步骤1。
 * 
 */
public class UmCaService extends Service implements Status.StatusListener {

	boolean[] cardStatus = new boolean[1];

	int ret;
	int[] buffLen = { 20480 };
	byte[] buff = new byte[buffLen[0]];

	private int year, month, day;
	private List<BaseDate> mOverTimeList = new ArrayList<BaseDate>();
	private BaseDate temp;
	private Ca ca = null;
	private String CA_CARD_ACTION = "com.unionman.card.action";
	private Toast mCaStatusToast = null;
	private Handler mHandler = null;
	private boolean isInsertCard = false;
	private CaInfoAccessor mCaAccessor = null;

	// private boolean isHave50 = false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private BaseDate mSysDate = new BaseDate();
	private boolean sendTdtBroadcast = false;
	private boolean tdtTimeOk = false;

	private Runnable getTdtRun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 获取ttt时间
			mSysDate = new BaseDate();
			Epg_LocalTime dt = getEpgLocalTime();
			Log.i("TTT-xinhua", "dt-year:" + dt.year + "dt-month:"
					+ dt.month + "dt-day:" + dt.day);
			if (dt.year >= 2000) {
				tdtTimeOk = true;
				mSysDate.year = dt.year;
				mSysDate.month = dt.month;
				mSysDate.day = dt.day;
				isInsertCard = false;
				dealWithRightTime();
			} else {
				if (sendTdtBroadcast == false) {
					sendBroad(CHECK_CABLE);
					// 当前日期不对，直接返回
					sendTdtBroadcast = true;
				}
				mHandler.postDelayed(getTdtRun, 5000);// 五秒之后重新获取tdt时间
			}
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.v("xinhua_UmCaService", "onCreate");
		
		if (!DVB.isServerAlive()) {
            Log.d("xinhua_UmCaService", "Server not alive, exit UmCaService");
			stopSelf();
			return;
		}
		
		mCaAccessor = new CaInfoAccessor(getContentResolver());
		DVB.getInstance();
		Status.getInstance().addStatusListener(this);

		String uCaFlag = SystemProperties.get("ro.sys.ca.control", "1");
		if (uCaFlag.equalsIgnoreCase("0")) {
			// forbid to install apk from U-disk
			Log.i("xinhua_CaControl", "CaControl is not open");
			return;
		}

		Log.i("xinhua_CaControl", "CaControl is open");

		mHandler = new Handler();
		mHandler.post(getTdtRun);

		
		Log.i("xinhua_CaControl", "start setReceiveMsgListener");
		Prompt.setReceiveMsgListener(new ReceiverMsgInterface() {

			@Override
			public void receiveOverTime(boolean status) {
			}

			@Override
			public void receiveCard(boolean status) {
				// TODO Auto-generated method stub
				Log.i("xinhua_UmCaService", "card status is changed!tdtTimeOk:"+tdtTimeOk);
				if (tdtTimeOk) {
					isInsertCard = true;
					dealWithRightTime();
				} else {
					if (sendTdtBroadcast == false) {
						// 当前日期不对，直接返回
						sendBroad(CHECK_CABLE);
						sendTdtBroadcast = true;

					}
				}
			}

			@Override
			public void receiveCable(boolean status) // 接收到插入cable线
			{
			}
		});
		
	}
	
	@Override
	public void onDestroy() {
		Log.v("xinhua_UmCaService", "onDestroy");
		if (DVB.isServerAlive()) {
			Status.getInstance().removeStatusListener(this);
			DVB.getInstance().release();
		}
		super.onDestroy();
	}
	
	protected void dealWithRightTime() {

		Log.i("xinhua_UmCaService", "cardStatus[0]:" + cardStatus[0]);
		if (mCaAccessor.getCardStatus()) {
			if (isInsertCard) {
				Log.i("xinhua", "delay 3 seconds to get Info");
				handler.postDelayed(runnable, 3000);
			} else {
				Log.i("xinhua", "3000 no delay to get Info");
				handler.post(runnable);
			}

			// 卡已经被插入
			// dealWithCard();
		} else {
			// 没有可用的卡
				Log.i("xinhua", "no delay to get Info");
			sendBroad(INSERT_CARD);
		}
	}

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			dealWithCard();
		}
	};

	private final int DEFAULT_TIME = 2014;

	/**
	 * 当卡的状态为true
	 */
	protected void dealWithCard() {
		ArrayList<EntitleInfo> entitles = mCaAccessor.getEntitles();
		if (entitles == null) {
			Log.i("xinhua", "CaGetOperID is called error");
			// 运营商信息获取失败
			sendBroad(OPERA_ERROR);
			return;
		}

		String card_type = SystemProperties.get("persist.sys.dvb.cas.type", "DVT");
		Log.v("xinhua", "card_type="+card_type);
		Epg_LocalTime systime = getEpgLocalTime();
		@SuppressWarnings("deprecation")
		long sysUTC = Date.UTC(systime.year-1900, systime.month-1, systime.day,
				systime.hour, systime.min, systime.sec) - 8*3600*1000;
		Date date = new Date(sysUTC);
		Log.v("xinhua", "date="+date.toString());
		
		boolean isAllExpired = true;
		for (EntitleInfo entitle : entitles) {
			Log.v("xinhua", "year:"+entitle.year+"month:"+entitle.month+"day:"+entitle.day); 
			long testUTC = Date.UTC(entitle.year-1900, entitle.month-1, entitle.day,
					entitle.hour, entitle.min, entitle.second) - 8*3600*1000;
			Log.v("xinhua", "productId[" + entitle.productId + "]="
					+ entitle.expiredTime);
			Log.v("xinhua", "entitle.expiredTime:"+entitle.expiredTime+"sysUTC:"+(sysUTC/1000));//.getTime());
			Log.v("xinhua", "entitle.testUTC:"+testUTC+"testUTC:"+(testUTC/1000));//.getTime());
			if (card_type.equalsIgnoreCase("WF")||card_type.equalsIgnoreCase("TF")) {
				if (testUTC > sysUTC) {
					isAllExpired = false;
					}
				}
			else if(card_type.equalsIgnoreCase("DVT")){
				if (entitle.expiredTime > (sysUTC/1000)){
					isAllExpired = false;
				}
			}
			else{
					Log.v("xinhua", "IRD DVN programs is ok , no ca control");
					isAllExpired = false;
			}
		}
		

		if (isAllExpired) {
			Log.v("xinhua", "all programs is expired");
			sendBroad(OVER_TIME);
		} else {
			Log.v("xinhua", "all programs is CA_OK");
			sendBroad(CA_OK);
		}
	}

	private static int CHECK_CABLE = 1;
	private static int OVER_TIME = 2;
	private static int INSERT_CARD = 3;
	private static int OPERA_ERROR = 4;
	private static int CA_OK = 5;
	private static int DATA_ERROR = 6;
	private static int NO_HAVE_50 = 7;

	private void sendBroad(int type) {
		Intent intent = new Intent();
		intent.setAction(CA_CARD_ACTION);
		switch (type) {
		case 1:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.check_cable));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		case 2:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.over_time));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		case 3:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.insert_card));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		case 4:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.opera_fail));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		case 5:
			intent.putExtra("CA_INFO", getResources().getString(R.string.ca_ok));
			break;
		case 6:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.program_data_error));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		case 7:
			intent.putExtra("CA_INFO",
					getResources().getString(R.string.have_no_package));
			intent.putStringArrayListExtra("DEFAULT_PACKAGE_LIST",
					getPackageList());
			break;
		default:
			break;
		}
		Log.i("xinhua-UmCaService", "sendBroadCast");
		sendBroadcast(intent);
	}

	private ArrayList<String> list = null;

	private ArrayList<String> getPackageList() {
		if (list == null) {
			list = getDefaultPkgList();
		}
		return list;
	}

	private ArrayList<String> getDefaultPkgList() {
		list = new ArrayList<String>();

		InputStream inputStream = getResources().openRawResource(
				R.raw.default_package_list);
		JSONObject result = null;
		JSONArray arrayList = null;
		String data = "{\"defaultPkgList\":" + getString(inputStream) + "}";
		try {
			result = new JSONObject(data);
			arrayList = result.getJSONArray("defaultPkgList");
			for (int i = 0, len = arrayList.length(); i < len; i++) {
				JSONObject object = arrayList.getJSONObject(i);
				Log.i("xinhua-UmCaService", object.optString("package"));
				list.add(object.optString("package"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (IOException e) {
		}
		return list;
	}

	private String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 根据cable线获取当前的epg时间
	 * 
	 * @return
	 */
	private Epg_LocalTime getEpgLocalTime() {
		Epg_LocalTime dt = new Epg_LocalTime();
		Prog prog = new Prog(DVB.getInstance());
		prog.prog_get_localtime(dt);
		return dt;
	}

    @Override
    public void OnMessage(Message msg) {
        Prompt.getInstance().handleMessage(msg);
    }
}
