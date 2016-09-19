package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.um.dvbsearch.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.DvbStackSearch;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.Status;
import com.um.dvbstack.Tuner;
import com.um.dvbstack.Tuner.TunerInfo;
import com.um.dvbstack.ProgManage;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.dvbstorage.SettingsStorage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;

public class Search extends AppBaseActivity implements Status.StatusListener {
	static final String TAG = "Search";
	private TextView title;
	private DvbStackSearch Srch;
	private ProgressBar pb;
	private ListView tvlistview;
	private ListView radiolistview;
	private ArrayList<HashMap<String, String>> tvlist;
	private ArrayList<HashMap<String, String>> radiolist;
	private static Search m_instance;
	// private RelativeLayout pb_linearLayout;
	private boolean mUpdateFlag = false;
	private int  search_mod;
	private Handler mSearchHandler = new Handler();
	private SyncFlagObserver mSyncFlagObserver = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");
		super.onCreate(savedInstanceState);

		if (!DVB.isServerAlive()) {
			Toast.makeText(this, "DVB Service is not alive.", 3000).show();
			finish();
			return;
		}
		setContentView(R.layout.search);
		m_instance = this;

		DVB.getInstance();
		Status.getInstance().addStatusListener(this);

		title = (TextView) findViewById(R.id.search_process_title);
		pb = (ProgressBar) findViewById(R.id.search_progress_progressBar);
		pb.setProgress(0);
		ProgressBar strengthbar = (ProgressBar) findViewById(R.id.search_signalbar);
		TextView strengthText = (TextView) findViewById(R.id.signal_textView);
		strengthbar.setProgress(0);
		strengthText.setText("0.0dB");

		// pb_linearLayout = (RelativeLayout)
		// findViewById(R.id.pb_tips_linearLayout);
		tvlistview = (ListView) findViewById(R.id.tv_listView);
		radiolistview = (ListView) findViewById(R.id.radio_listView);
		tvlistview.setFocusable(false);
		radiolistview.setFocusable(false);
		tvlist = new ArrayList<HashMap<String, String>>();
		// HashMap<String, String> tvmap = new HashMap<String, String>();
		// tvmap.put("name", "TV" );
		// tvlist.add(tvmap);

		SimpleAdapter tvItems = new SimpleAdapter(this, tvlist,
				R.layout.dvbplayer_list, new String[] { "name" },
				new int[] { R.id.Dvbplayer_TextView_01 });
		tvlistview.setAdapter(tvItems);

		radiolist = new ArrayList<HashMap<String, String>>();
		// HashMap<String, String> radiomap = new HashMap<String, String>();
		// radiomap.put("name", "RADIO" );
		// radiolist.add(radiomap);
		SimpleAdapter radioItems = new SimpleAdapter(this, radiolist,
				R.layout.dvbplayer_list, new String[] { "name" }, new int[]

				{ R.id.Dvbplayer_TextView_01 });
		radiolistview.setAdapter(radioItems);
		
		
		Srch = new DvbStackSearch(DVB.getInstance());
	}
	
	private String getLaunchReason() {
		return getIntent().getStringExtra("launchReason");
	}
	
	private void cleanView() {
		ProgressBar strengthbar = (ProgressBar) findViewById(R.id.search_signalbar);
		TextView strengthText = (TextView) findViewById(R.id.signal_textView);
		strengthbar.setProgress(0);
		strengthText.setText("0.0dB");
		pb.setProgress(0);
		tvlist.clear();
		radiolist.clear();
		((SimpleAdapter) (radiolistview.getAdapter())).notifyDataSetChanged();
		((SimpleAdapter) (tvlistview.getAdapter())).notifyDataSetChanged();
		
		TextView fre_text = (TextView) findViewById(R.id.search_status_text_value);
		fre_text.setText("");
	}
	
	private void resetProg() {
		Log.v(TAG, "PROGRESET: ready to clear provider...");
		setProgSyncFlag(true);
		ProgStorage ps = new ProgStorage(getContentResolver());
		int tunnerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		int categoryID = (tunnerType == Tuner.UM_TRANS_SYS_TYPE_TER ? ContentSchema.CategoryTable.DTMB_ID
				: ContentSchema.CategoryTable.DVBC_ID);
		Log.v(TAG, "categoryID = " + categoryID);
		ps.DeleCategoryProg(new int[] { categoryID });
		Log.v(TAG, "clear provider done.");
		
		Log.v(TAG, "ready to reset prog db...");
		ProgManage.GetInstance().resetDvbProgList();
		ProgManage.GetInstance().refreshProgList();
		setProgSyncFlag(false);
	}
	
	private void startSearch(Intent it) {
		Bundle bundle = it.getExtras();
		if (1 == bundle.getInt("type")) {
			Log.i("Search type",
					"manual!" + bundle.getInt("fre") + bundle.getInt("sym")
							+ bundle.getInt("qam"));
			title.setText(R.string.menul_search);

			Srch.ManualSearch(bundle.getInt("tunertype"),
					bundle.getInt("band"), bundle.getInt("fre"),
					bundle.getInt("sym"), bundle.getInt("qam"));

		} else if (2 == bundle.getInt("type")) {
			Log.i(TAG, "full band search!");
			title.setText(R.string.all_serach);
			resetProg();
			Srch.FullBandSearch(bundle.getInt("tunertype"),
					bundle.getInt("band"), 0, 0);
		} else {
			Log.i(TAG, "auto search. tuner type: " + Tuner.GetInstance(DVB.getInstance()).GetType());
			title.setText(R.string.auto_serach);
			resetProg();
			if (Tuner.GetInstance(DVB.getInstance()).GetType() == Tuner.UM_TRANS_SYS_TYPE_TER) {
				Log.v(TAG, "in dtmb mode, treat Auto-Search as Full-Search");
				Srch.FullBandSearch(bundle.getInt("tunertype"),
						bundle.getInt("band"), 0, 0);
			} else {
				Srch.AutoSearch(bundle.getInt("tunertype"), bundle.getInt("band"),
						bundle.getInt("fre"), bundle.getInt("sym"),
						bundle.getInt("qam"));
			}
		}
		search_mod = bundle.getInt("type");
		
		Log.i("SEARCH search!!", "type:" + bundle.getInt("type"));
		setProgSyncFlag(true);
		registerProgSyncObserver();
	}
	
	private boolean isTVProgEmpty() {
		int progTypeId;
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if (turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) {
			progTypeId = ContentSchema.CategoryTable.DVBC_ID;
		} else {
			progTypeId = ContentSchema.CategoryTable.DTMB_ID;
		}

		ProgStorage progStorage = new ProgStorage(getContentResolver());
		ArrayList<ProgInfo> progList = progStorage.getProgOrderBy(new int[] { progTypeId, ContentSchema.CategoryTable.TV_ID }, 
									null, false);
		if (progList != null && progList.size() > 0) {
			for (ProgInfo pi : progList) {
				if (!pi.hiden && pi.valid) {
					return false;
				}
			}
		}
		return true;
	}
	
	private class SyncFlagObserver extends ContentObserver {

		public SyncFlagObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			if (!DVB.isServerAlive()) {
				finish();
				Log.w(TAG, "onChange: server is not ready.");
                return ;
			}
			
			if (!getProgSyncFlag()) {
				Log.v(TAG, "prog sync is finished!!");
				String launchReason = getLaunchReason();
				Log.v(TAG, "launchReason:" + launchReason);
				boolean backToPlayer = (search_mod != 1)
										|| (launchReason != null && launchReason.equals("noProg"));
	            
				if (backToPlayer && !isTVProgEmpty()) {
					startFullscreenPlay();
				}
				finish();
			}
		}
	}

	private void registerProgSyncObserver() {
		Uri uri = null;
		SettingsStorage ss = new SettingsStorage(getContentResolver());
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if(turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) {
			uri = ss.getUriFor("progDVBCSyncStatus");
		} else {
			uri = ss.getUriFor("progDTMBSyncStatus");
		}
		unregisterSyncObserver();
		mSyncFlagObserver = new SyncFlagObserver(new Handler());	
		getContentResolver().registerContentObserver(uri, false, mSyncFlagObserver);
	}
	
	private void unregisterSyncObserver() {
		if (mSyncFlagObserver != null) {
			getContentResolver().unregisterContentObserver(mSyncFlagObserver);
			mSyncFlagObserver = null;
		}
	}
	
	public void onGetProgram(int type, String name) {

		Log.i(TAG, name + "type:" + type);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", "" + name);
		if ((type == 1) || (type == 25)) {
			tvlist.add(map);

			if (tvlist.size() > 5) {
				tvlistview.setSelection(tvlist.size() - 5);
				// tvlistview.setScrollY((tvlist.size()-5)*46);
			} else {
				tvlistview.setSelection(tvlist.size());
				// tvlistview.setScrollY(0);
			}

			((SimpleAdapter) (tvlistview.getAdapter())).notifyDataSetChanged();

			Log.i(TAG, "tvlist size:" + tvlist.size());
			Log.i(TAG, "add tv");
		} else if (type == 2) {
			radiolist.add(map);
			if (radiolist.size() > 5) {
				radiolistview.setSelection(radiolist.size() - 5);

				// radiolistview.setScrollY((radiolist.size()-5)*46);
			} else {
				// radiolistview.setScrollY(0);
				radiolistview.setSelection(radiolist.size());
			}
			((SimpleAdapter) (radiolistview.getAdapter()))
					.notifyDataSetChanged();
			Log.i(TAG, "add radio");
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy()");
		super.onDestroy();
	}

    private Handler mHandler = new Handler();
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            StringBuffer strbuf = new StringBuffer();
            strbuf.append(getResources().getString(R.string.search_tv_count));
            strbuf.append(tvlist.size());
            strbuf.append(getResources().getString(R.string.search_radio_count));
            strbuf.append(radiolist.size());
            String str = new String(strbuf);
            Log.i(TAG, "" + str);
            Toast.makeText(m_instance, str, Toast.LENGTH_LONG).show();
        }
    };

	public void onUpdateProcess(int process) {
		Log.i("Search", "search process is:" + process);
		if (process >= 0 && process < 101) {
			pb = (ProgressBar) findViewById(R.id.search_progress_progressBar);
			pb.setProgress(process);
			TextView progress = (TextView) findViewById(R.id.progress_textView);
			progress.setText(process + " %");
		}
		if (process >= 101) {
			TextView progress = (TextView) findViewById(R.id.progress_textView);
			pb.setProgress(100);
			progress.setText(100 + " %");
            mHandler.removeCallbacks(mRunable);
            mHandler.postDelayed(mRunable, 500);
//			Log.i(TAG, "SEARCH FINISH!!!");
//			// Srch.StopSearch();
//
//			StringBuffer strbuf = new StringBuffer();
//			strbuf.append(getResources().getString(R.string.search_tv_count));
//			strbuf.append(tvlist.size());
//			strbuf.append(getResources().getString(R.string.search_radio_count));
//			strbuf.append(radiolist.size());
//			String str = new String(strbuf);
//			Log.i("search finish", "" + str);
//			Toast.makeText(m_instance, str, Toast.LENGTH_LONG).show();
		}
		
		DVB dvb = DVB.getInstance();
		Tuner tuner = Tuner.GetInstance(dvb);
		TunerInfo info = new TunerInfo();

		int strength = 0;
		if (0 == tuner.GetInfo(0, info)) {
			strength = info.Strength;
		} else {
			strength = 0;
			Log.e("SIGNALCHECK", "Search GET INFO ERROR!");
		}
		
		
		Log.i("SIGNALCHECK", "strength: " + strength);
		ProgressBar strengthbar = (ProgressBar) findViewById(R.id.search_signalbar);
		TextView strengthText = (TextView) findViewById(R.id.signal_textView);
		strengthbar.setProgress(strength % 101);
		strengthText.setText(strength + ".0dB");
		
		if (process >= 101) {
				// Save last program to 0
				Log.i(TAG, "Save last program to TV 0");
				// ProgramSyncProvider();
				ParamSave.SaveLastProgInfo(Search.this, 0, 0);
				//System.putInt(Search.this.getContentResolver(), "progindex", 0);
				//System.putInt(Search.this.getContentResolver(), "mode", 0);
				startSyncProgram();
		}

	}
	
	void setProgSyncFlag(boolean syncFlag)
	{
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if(turnType==Tuner.UM_TRANS_SYS_TYPE_CAB)
		{
			ParamSave.setDVBCProgSyncStatus(m_instance, syncFlag?0:1);
		}
		else
		{
			ParamSave.setDTMBProgSyncStatus(m_instance, syncFlag?0:1);	
		}
	}
	
	private boolean getProgSyncFlag() {
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if (turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) {
			return ParamSave.getProgDVBCSyncStatus(this) != 1;
		} else {
			return ParamSave.getProgDTMBSyncStatus(this) != 1;
		}
	}

	public void onGetTpInfo(int fre) {
		TextView fre_text = (TextView) findViewById(R.id.search_status_text_value);
		fre_text.setText(fre + " Khz ...");
	}
	
	@Override
	protected void onResume() {
		Log.v(TAG, "onResume()");
		super.onResume();
		Status.getInstance().addStatusListener(this);
		
		cleanView();
		mSearchHandler.removeCallbacksAndMessages(null);
		mSearchHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent it = Search.this.getIntent();
				startSearch(it);
			}
		}, 1*1000);
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "onPause()");
		
		unregisterSyncObserver();
		
		mSearchHandler.removeCallbacksAndMessages(null);
		if (DVB.isServerAlive()) {
			Srch.StopSearch();
			if (getProgSyncFlag()) {
				startSyncProgram();
			}
		}
		
		Status.getInstance().removeStatusListener(this);
		super.onPause();
	}
	
	private void startSyncProgram() {
		Intent it = new Intent("com.unionman.intent.SERVICE_SYNC_PROGRAM");
		it.setPackage(getPackageName());
		startService(it);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Srch.StopSearch();
			if (getProgSyncFlag()) {
				startSyncProgram();
				return true;
			}
			
		case KeyEvent.KEYCODE_SOURCE:
        case KeyEvent.KEY_USB:
        case KeyEvent.KEY_APPLICATION:
		case KeyEvent.KEYCODE_TV:
		case KeyEvent.KEY_SOURCEENTER:	
		    return true;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	private void startFullscreenPlay() {
		Intent intent = new Intent("com.unionman.intent.ACTION_PLAY_DVB");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		Log.v(TAG, "onNewIntent()");
		if (!DVB.isServerAlive()) {
			finish();
			return;
		}
	}
	
	@Override
	public void OnMessage(Message msg) {
		if (msg.what != 2) {
			return;
		}
		byte[] byteinfo = (byte[]) msg.obj;
		if (msg.arg1 == Status.subtype.UMSG_DVB_SRCH_PROGRESS.ordinal()) {

			int process = byteinfo[3] << 24 | byteinfo[2] << 16
					| byteinfo[1] << 8 | byteinfo[0];
			Log.i("Status", "process is:" + process);
			onUpdateProcess(process);
			Log.i("Status", "2 process is:" + process);		
			
		} else if (msg.arg1 == Status.subtype.UMSG_DVB_SRCH_GET_PROG.ordinal()) {
			String progname;
			try {
				progname = new String(byteinfo, 16, 20, "UnicodeBig");
			} catch (UnsupportedEncodingException e1) {
				progname = "";
				e1.printStackTrace();
			}

			int progtype = byteinfo[11] << 24 | byteinfo[10] << 16
					| byteinfo[9] << 8 | byteinfo[8];
			Log.i("Status", "get prog name is:" + progname + progtype);
			onGetProgram(progtype, progname);
			
			DVB dvb = DVB.getInstance();
			Tuner tuner = Tuner.GetInstance(dvb);
			TunerInfo info = new TunerInfo();
			
			int strength = 0;
			if (0 == tuner.GetInfo(0, info)) {
				strength = info.Strength;
			} else {
				strength = 0;
				Log.e("SIGNALCHECK", "Search GET INFO ERROR!");
			}
			
			/**/
			Random random = new Random(System.currentTimeMillis());
			strength = 80 + random.nextInt()%20;
			Log.i("SIGNALCHECK", "22strength: " + strength);
			ProgressBar strengthbar = (ProgressBar) findViewById(R.id.search_signalbar);
			TextView strengthText = (TextView) findViewById(R.id.signal_textView);
			strengthbar.setProgress(strength % 101);
			strengthText.setText(strength + ".0dB");
		} else if (msg.arg1 == Status.subtype.UMSG_DVB_SRCH_GET_TP.ordinal()) {
			String sfre = new String(byteinfo);
			int fre = 0;
			Log.i("Status", "UMSG_DVB_SRCH_GET_TP sfre:" + sfre);
			try {
				fre = Integer.valueOf(sfre);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.i("Status", "get fre:" + fre);
			onGetTpInfo(fre * 10);
		}
		/*else if(msg.arg1 == Status.subtype.UMSG_DVB_SRCH_GET_FREQ_SIGNAL_INFO.ordinal()){
			DVB dvb = DVB.getInstance();
			Tuner tuner = Tuner.GetInstance(dvb);			
			int strength =  byteinfo[11] << 24 | byteinfo[10] << 16
					| byteinfo[9] << 8 | byteinfo[8];
			
			Log.i("SIGNALCHECK", "search locked strength: " + strength);
			ProgressBar strengthbar = (ProgressBar) findViewById(R.id.search_signalbar);
			TextView strengthText = (TextView) findViewById(R.id.signal_textView);
			strengthbar.setProgress(strength % 101);
			strengthText.setText(strength + ".0dB");
		}*/
	}
}
