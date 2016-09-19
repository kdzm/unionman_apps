package com.um.channelseditor;


import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelsEditorActivity extends Activity implements OnClickListener {
	private final String TAG = ChannelsEditorActivity.class.getSimpleName()+"----U668";
	private final boolean LOGE = true;
	
	private final String STB_CHAN_INFO_PATH = "/data/dvb/dvb.db";
	private final String UDISK_CHAN_INFO_PATH = "/mnt/sda/sda1/dvb.db";
	private final String mDvbDataNames[] = {"umdb.dat", "umdb_sysdata.dat", "umdb_sysdata.dat-bak"};
	
	private ChannelsManager mChanManager;
	private ListView mChanListView;
	private ChannelAdapter mChanAdapter;
	private int mSortPosition = -1;
	private boolean mSorting = false;
	private boolean mChanged = false;
	private long[] mChanOrders;
	private int[] mChanNums;
	private String[] mChanNames;
	private ChannelInfo[] mChanInfos;
	private Map<String, ChannelInfo> mChanInfoMap;
	private int mTunerType;
	
	private Context mContext;
    private final CountDownTimer countDownTimer = new CountDownTimer(1000 * 60, 1000) {
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            finish();
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channels_editor_main);
		mContext = this;
		Intent it = this.getIntent();
		Bundle bundle = it.getExtras();
		mTunerType = bundle.getInt("tunertype");
		initViews();
		initChanInfos();
		Log.i("channeledit","tunertype="+mTunerType);
	}

    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    private void restarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        restarTimer();
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				break;
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_MENU:
	    		 Log.i(TAG,"KEYCODE_BACK or KEYCODE_MENU  is clicked");
				if (mChanged) {
					return showExitDialog();
				}else{
					finish();
				}
				break;
			case KeyEvent.KEY_SOURCEENTER:
				Log.i(TAG,"KEY_SOURCEENTER is clicked");
				listviewItemClick(mChanListView.getSelectedItemPosition());
	    	return true;												
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View view) {
		boolean result[] = new boolean[4];
		switch (view.getId()) {
			default:
				break;						
		}
		for (int i = 0; i < result.length; i++) {
			if (!result[i]) {
				Toast.makeText(mContext, R.string.chan_copy_fail, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		Toast.makeText(mContext, R.string.chan_copy_success, Toast.LENGTH_SHORT).show();
	}
	
	private void initViews() {
		mChanListView = (ListView) findViewById(R.id.chan_editor_list_view);
	}
	
	private void initChanInfos() {
		if (!CopyUtils.fileExists(STB_CHAN_INFO_PATH)) {
			return;
		}
		mChanManager = ChannelsManager.getInstance(mContext);
		mChanInfos = mChanManager.getChannelInfos(mTunerType);
		if (mChanInfos != null) {
			int length = mChanInfos.length;
			mChanOrders = new long[length];
			mChanNums = new int[length];
			mChanNames = new String[length];
			mChanInfoMap = new HashMap<String, ChannelInfo>();
			for(int i = 0; i < length; i++) {
				mChanOrders[i] = mChanInfos[i].getOrder();
				mChanNums[i] = i+1;
				mChanNames[i] = mChanInfos[i].getChanName();
				mChanInfoMap.put(mChanNames[i], mChanInfos[i]);
				Log.v(TAG, "Channel Number: "+mChanNums[i]);
				Log.v(TAG, "Channel Name: "+mChanNames[i]);
			}
			if (mChanListView == null) {
				Log.e(TAG, "mChanListView == null");
			}
			mChanAdapter = new ChannelAdapter(mContext, mChanNums, mChanNames, mChanInfoMap);
			mChanListView.setAdapter(mChanAdapter);
			OnItemClickListener onItemClickListener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//listviewItemClick(position);
				}
			};
			mChanListView.setOnItemClickListener(onItemClickListener);
			
			OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if (mSorting) {
						int tmpChanNum = mChanNums[mSortPosition];
						String tmpChanName = new String(mChanNames[mSortPosition]);
						mChanNums[mSortPosition] = mChanNums[position];
						mChanNames[mSortPosition] = mChanNames[position];
						mChanNums[position] = tmpChanNum;
						mChanNames[position] = tmpChanName;
						mChanAdapter.refreshData(mChanNums, mChanNames, position);
						mSortPosition = position;
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			};
			mChanListView.setOnItemSelectedListener(onItemSelectedListener);
			
			OnKeyListener onKeyListener = new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					// TODO Auto-generated method stub
					restarTimer();
					int keyAction = arg2.getAction();
					if(keyAction==KeyEvent.ACTION_UP)
						return false;
					int position = mChanListView.getSelectedItemPosition();
					ChannelInfo chanInfo;
						
					switch(arg2.getKeyCode()){
					case KeyEvent.KEYCODE_PROG_RED:
						chanInfo = mChanInfoMap.get(mChanNames[position]);
						chanInfo.setFav(chanInfo.getFav()==true?false:true);
						if(mSorting==true)
						{
							mSorting = false;
							mSortPosition = -1;	
						}
						mChanged = true;
						mChanAdapter.refreshData(-1);
						break;
						
					case KeyEvent.KEYCODE_F1:/*fake GREEN*/
						listviewItemClick(position);
						break;
						
					case KeyEvent.KEYCODE_F2:/*fake YELLOW*/
						/*
						chanInfo = mChanInfoMap.get(mChanNames[position]);
						chanInfo.setValid(chanInfo.getValid()==true?false:true);
						if(mSorting==true)
						{
							mSorting = false;
							mSortPosition = -1;	
						}
						mChanged = true;
						mChanAdapter.refreshData(-1);
						*/
						break;
						
					case KeyEvent.KEYCODE_PROG_BLUE:/*fake BLUE*/
						chanInfo = mChanInfoMap.get(mChanNames[position]);
						chanInfo.setHided(chanInfo.getHided()==true?false:true);
						mChanged = true;
						if(mSorting==true)
						{
							mSorting = false;
							mSortPosition = -1;	
						}
						mChanAdapter.refreshData(-1);
						break;
					default:
						break;
					}
					
					return false;
				}
			};
			
			mChanListView.setOnKeyListener(onKeyListener);
		}
	}
	
	
	
	private void saveDbAndExit(long delayMillis) {
		if (mChanInfos == null) {
			return;
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				int length = mChanOrders.length;
				ChannelInfo chaInfo = null;
				for (int i = 0; i < length; i++) {
//					chaInfo = mChanManager.getCachedChanInfoByName(mChanNames[i]);
					chaInfo = mChanInfoMap.get(mChanNames[i]);
					if (chaInfo.getOrder() != mChanOrders[i]) {
						chaInfo.setOrder(mChanOrders[i]);
					}
					mChanInfos[i] = chaInfo;
				}
				Log.i("channedit","chaninfo"+chaInfo);
				if (chaInfo != null) {
					if (length > 0) {
						mChanManager.saveChanInfosDB(mChanInfos);
					}
				}
				finish();
			}
		}, delayMillis);
	}
	
	private boolean showExitDialog() {
  	  AlertDialog.Builder builder =new AlertDialog.Builder(ChannelsEditorActivity.this);
	      LayoutInflater factory = LayoutInflater.from(ChannelsEditorActivity.this);
	      View myView = factory.inflate(R.layout.channel_edit_dialog,null);
	      TextView textView = (TextView) myView.findViewById(R.id.system_back_text);
	      textView.setText(R.string.save_chan_edit_yes_no);
		   final Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
		   final Button   mSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
	      final AlertDialog   mAlertDialog = builder.create();
	      mAlertDialog.show();
	      mAlertDialog.getWindow().setContentView(myView);
		  mSystemOKBtn.setOnClickListener(new OnClickListener() {
	
		          @Override
		          public void onClick(View arg0) {
						Toast.makeText(mContext, R.string.saving_chan_edit_data, Toast.LENGTH_LONG).show();
						saveDbAndExit(100);
		          }
		      });
		  mSystemCancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		return true;
	}
	
	private void listviewItemClick(int position) {
		int sortingItemId = mChanAdapter.getSortItemId();
		if (mChanAdapter.getSortItemId() == position) {
			mChanAdapter.refreshCancelSort();
			mSorting = false;
			mSortPosition = -1;
		} else if (sortingItemId == -1) {
			mChanAdapter.refreshData(position);
			mSorting = true;
			mChanged = true;
			mSortPosition = position;
			mChanNums = mChanAdapter.getChanNums();
			mChanNames = mChanAdapter.getChanNames();
		}
	}

}
