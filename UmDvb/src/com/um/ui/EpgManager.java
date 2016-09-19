package com.um.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.um.controller.Player;
import com.um.dvb.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Event;
import com.um.dvbstack.Prog;
import com.um.dvbstack.Prog.Epg_LocalTime;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Status;
import com.unionman.dvbstorage.ProgBookInfo;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class EpgManager {
	private static String TAG = "EpgManager";
	private Activity mContext = null;
	private Player mPlayer = null;
	private ListView mEpgListView = null;
	private static EpgManager mInstance = null;
	private DvbPlayService.DvbServerBinder mService = null;
	private EpgBroadcastReceiver mEpgBroadcastReceiver = null;
	private int mChannelIndex;
	private int mDayOffset;
	private ArrayList<Event> schEvent = new ArrayList<Event>();
    private boolean epgshowflag = false;
	private int mCurDay = 0;
	private boolean mIsSearchingShowed = false;
	private Handler mHandler = null;
	
	private EpgManager(){

	}
	
    synchronized public static EpgManager getInstance(){
		if (mInstance == null){
			mInstance = new EpgManager();
		}
		
		return mInstance;
	}
    
    public boolean init(Activity activity) {
    	Log.v(TAG, "init");
		mContext = activity;
		mPlayer = Player.GetInstance();
		mHandler = new Handler(mContext.getMainLooper());
		mEpgListView = (ListView) activity.findViewById(R.id.epg_info_list_view);
		Log.v(TAG, "mEpgListView="+mEpgListView);
		if (mEpgListView == null) {
			return false;
		}
		
		
		mEpgListView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.v(TAG, "onKey(): keyCode="+keyCode);
				((Dvbplayer_Activity)mContext).handler.removeCallbacks(((Dvbplayer_Activity)mContext).mHideListRunnable);
				((Dvbplayer_Activity)mContext).handler.postDelayed(((Dvbplayer_Activity)mContext).mHideListRunnable, 1000*5);
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_UP:
							if (mEpgListView.getSelectedItemPosition() == 0) {
								return true;
							}
							break;
						case KeyEvent.KEYCODE_DPAD_DOWN:
							if (mEpgListView.getSelectedItemPosition() == mEpgListView.getCount()-1) {
								return true;
							}
							break;
						case KeyEvent.KEYCODE_DPAD_LEFT:
							((Dvbplayer_Activity)mContext).mWeekdayListView.setSelection(mDayOffset);
							((Dvbplayer_Activity)mContext).mWeekdayListView.getSelectedView().setBackgroundResource(android.R.color.transparent);
							((Dvbplayer_Activity)mContext).mWeekdayListView.requestFocus();
							return true;
						case KeyEvent.KEYCODE_DPAD_RIGHT:
							return true;
						case KeyEvent.KEYCODE_DPAD_CENTER:
                        {
                        	/*
							TextView bookText = (TextView) mEpgListView.getSelectedView().findViewById(R.id.epg_state_text_view);
                            int pos = mEpgListView.getSelectedItemPosition();
                            Prog prog = getProg(mChannelIndex);
                            ProgStorage ps = new ProgStorage(((Dvbplayer_Activity)mContext).getContentResolver());
                            ProgBookInfo book = new ProgBookInfo(schEvent.get(pos).getStartTime(), schEvent.get(pos).getEndTime(), schEvent.get(pos).getName());
                            ProgInfo pi = new ProgInfo(prog.getProgId(), prog.getTsId(), prog.getServiceId(), 0, prog.getName());

                            if(ps.getProg(prog.getProgId()) != null)
                                pi = ps.getProg(prog.getProgId());

                            if(pi.bookList == null)
                                pi.bookList = new ArrayList<ProgBookInfo>();

                            if (bookText.getText().equals(mContext.getResources().getString(R.string.book))) {
                                for(int i = 0; i < pi.bookList.size(); i++)
                                {
                                	Log.v("TestEpg", "remove book!!");
                                    if(pi.bookList.get(i).getStartTime() == schEvent.get(pos).getStartTime())
                                        pi.bookList.remove(i);
                                }
								bookText.setText("");
							} else {
                                if((book != null )&&(pi != null))
                                {
                                    Log.v("TestEpg", "add book!!");
                                    pi.bookList.add(book);
                                }

								bookText.setText(mContext.getResources().getString(R.string.book));
								bookText.setTextColor(mContext.getResources().getColor(R.color.red));
							}
                            boolean b = ps.addProg(pi,true);
                            pi.printInfo();*/
							break;
                        } 	
						case KeyEvent.KEYCODE_BACK:
							((Dvbplayer_Activity)mContext).handler.removeCallbacks(((Dvbplayer_Activity)mContext).mHideListRunnable);
							((Dvbplayer_Activity)mContext).handler.post(((Dvbplayer_Activity)mContext).mHideListRunnable);
							return true;
						default:
							break;
					}
				}
				return false;
			}
		});
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.um.dvbstack.UMSG_DVB_EPG_SCH_EVENTS_UPDATE"); 
		mEpgBroadcastReceiver = new EpgBroadcastReceiver();
		mContext.registerReceiver(mEpgBroadcastReceiver, intentFilter);  
		return true;
    }
    
    public ListView getListView() {
    	return mEpgListView;
    }
    
    public void deinit() {
    	Log.v(TAG, "deinit");
    	if (mEpgBroadcastReceiver != null) {
	    	mContext.unregisterReceiver(mEpgBroadcastReceiver);
	    	mEpgBroadcastReceiver = null;
    	}
    	if (mEpgListView != null) {
    		mEpgListView.setAdapter(null);
    	}
    	mEpgListView = null;
    	mContext = null;
    	mPlayer = null;
    	epgshowflag = true;
    	if (mHandler != null) {
    		mHandler.removeCallbacksAndMessages(null);
    	}
    }

    public void setEpgShowFlag(boolean flag){
		Log.i("TestEpg", "setEpgShowFlag flag:" + flag);
        epgshowflag = flag;
    }

    public boolean getEpgShowFlag(){
		Log.i("TestEpg", "getEpgShowFlag flag:" + epgshowflag);
        return epgshowflag;
    }
	
	private Prog getProg(int curChannelIndex) {
		ProgList progList = Player.GetInstance().getCurModeProgList();
		int progIndex = 0;

		if (progList.list.isEmpty() == false){
			progIndex = Integer.valueOf(progList.list.get(curChannelIndex).get(
					ProgManage.PROG_VIEWINDEX));
			return Player.GetInstance().getProg(progIndex);
		} else {
			return new Prog(DVB.getInstance());		
		}
	}
	
	public void displayEpgList(int channelIndex, int dayOffset) {
		if (mEpgListView == null) {
			Log.e(TAG, "mEpgListView is null");
			return;
		}	
		
		Log.i(TAG, "getEventList--channelIndex:" + channelIndex + "\tdayOffset:" + dayOffset);
		Epg_LocalTime epgLocalTime = new Epg_LocalTime();
		Prog prog = getProg(channelIndex);
		prog.prog_get_localtime(epgLocalTime);
		Log.v(TAG, "prog_id:" + prog.getProgId() + ", mjd="+epgLocalTime.mjd);
		schEvent.clear();
		prog.getWeek(schEvent, epgLocalTime.mjd + dayOffset);
		
		mChannelIndex = channelIndex;
		mDayOffset = dayOffset;
		mHandler.removeCallbacksAndMessages(null);
		
		if (!schEvent.isEmpty()){
			ArrayList<HashMap<String, String>> schList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < schEvent.size(); i++)
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("index", "" + (i + 1));
				String strTime = String.format("%02d:%02d~%02d:%02d",schEvent.get(i).getStartTime().hour,
						schEvent.get(i).getStartTime().minute,schEvent.get(i).getEndTime().hour,
						schEvent.get(i).getEndTime().minute);
				map.put("time", strTime);
				map.put("prog", (schEvent.get(i)).getName());
				Log.i(TAG, "time:" + strTime + "\t index:" + (i+1) + "\t prog:" + (schEvent.get(i)).getName());
				schList.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(
					mContext,
					schList,
					R.layout.epg_list_item,
					new String[] { "time", "prog" },
					new int[] { R.id.epg_time_text_view, 
							R.id.epg_prog_text_view });
			mEpgListView.setAdapter(adapter);
		} else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String> (
							mContext, 
							R.layout.epg_searching_tip_item,
							R.id.epg_searching_tip_view);
			adapter.add(mContext.getString(R.string.searching_epg_sch));
			mEpgListView.setAdapter(adapter);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ArrayAdapter<String> adapter = new ArrayAdapter<String> (
							mContext, 
							R.layout.epg_searchfail_tip_item,
							R.id.epg_searching_tip_view);
			adapter.add(mContext.getString(R.string.schedule_not_found));
			mEpgListView.setAdapter(adapter);
				}
			}, 6*1000);
		}
	}
	
	private class EpgBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (epgshowflag) {
				displayEpgList(mChannelIndex, mDayOffset);
			}
		}
	}
}
