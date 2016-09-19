package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.um.controller.ParamSave;
import com.um.dvb.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.ProviderProgManage;
import com.um.dvbstack.Tuner;
import com.um.util.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;

public class DvbSettingPopupWindow extends PopupWindow {
	private final String TAG = "DvbSettingPopupWindow";

	private Context context;
	
	private SimpleAdapter ListItemAdapter;
	
	private ArrayList<HashMap<String, Object>> ListItemData;
	
	private ListView SettingListView;
	
	private View localView;
	
	private Handler mHandler;
	
    private Bundle playInfo=null;

	private boolean mDismissOnItemClick = true;
    private boolean mShowOnEPGDismiss = false;
	private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
	private String[] titles;
	private String[] instructs;
	private int[] listItemRightImgs;
	private int[] listItemLeftImgs;
	private String[] listItemVals;
	
    private BroadcastReceiver mListViewReceiver = new BroadcastReceiver() {

    	
        @Override
        public void onReceive(Context content, Intent intent) {

            String action = intent.getAction();
            if (action.equals(PIC_SET_FINISH_ACTION)) {

            	if (localView.getVisibility() == View.INVISIBLE) {
            		localView.setVisibility(View.VISIBLE);
                    statrCountDownTimer();
               }

            } else if (action.equals(SOUND_SET_FINISH_ACTION)) {
            	
                if (localView.getVisibility() == View.INVISIBLE) {
                	localView.setVisibility(View.VISIBLE);
                    statrCountDownTimer();
               }
            }
        }
    };

    private final CountDownTimer countDownTimer = new CountDownTimer(1000 * 30, 1000) {
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            dismiss();
        }
    };
	
	public DvbSettingPopupWindow(Context paramContext,Handler handler) {
		// TODO Auto-generated constructor stub
		
		this.context = paramContext;
		mHandler =handler;
		setFocusable(true);
		
		setWindowLayoutMode(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		 localView = ((LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.dvb_setting_layout, null);
			
        IntentFilter listViewSet = new IntentFilter(PIC_SET_FINISH_ACTION);
        listViewSet.addAction(SOUND_SET_FINISH_ACTION);
        context.registerReceiver(mListViewReceiver, listViewSet);
		setContentView(localView);
		
		
		SettingListView = (ListView) localView.findViewById(R.id.dvb_setting_list);
		
		SettingListView.setOnKeyListener(new OnKeyListener() {
				
			@Override
			public boolean onKey(View view, int keycode, KeyEvent event) {
                restarCountDownTimer();
				if (event.getAction() == KeyEvent.ACTION_DOWN) {  
					int position = SettingListView.getSelectedItemPosition();
					switch (keycode) {
					case KeyEvent.KEYCODE_MENU:
					 case KeyEvent.KEYCODE_BACK:
                         dismiss();
//						 Message msg = new Message();
//						 msg.what = Constant.DvbSettingPopupWindow_DISMISS;
//						 mHandler.sendMessage(msg);
				     break;
					 case KeyEvent.KEYCODE_DPAD_DOWN:
						 if(SettingListView.getSelectedItemPosition() ==SettingListView.getCount() - 1){
							 SettingListView.setSelection(0); 
							 Log.i(TAG,"KEYCODE_DPAD_DOWN");
						 }
                     break;
			        case KeyEvent.KEYCODE_DPAD_UP:
			            if (SettingListView.getSelectedItemPosition() == 0) {
			            	SettingListView
			                        .setSelection(SettingListView.getCount() - 1);
			            }
			            break;	 
			        case KeyEvent.KEYCODE_DPAD_RIGHT:
			 	    case KeyEvent.KEYCODE_DPAD_LEFT:
			 	           	if(position==6){
			             	listviewItemClick(position);	
			         	}
			         	break;   
			    	 case KeyEvent.KEY_SOURCEENTER:	
			    		 listviewItemClick(position);		
			    		 return true;  	
					}
				}
				return false;
			}
		});

		ListItemData = new ArrayList<HashMap<String, Object>>();

        int tunnerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        if (tunnerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            titles = this.context.getResources().getStringArray(R.array.dvb_setting_dtmb);
            instructs = this.context.getResources().getStringArray(R.array.dvb_setting_instruct_dtmb);
        } else {
            titles = this.context.getResources().getStringArray(R.array.dvb_setting_dvbc);
            instructs = this.context.getResources().getStringArray(R.array.dvb_setting_instruct_dvbc);
        }
        
        listItemRightImgs = new int[]{
        		R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			//R.drawable.selector_view_right_gred,
    			R.drawable.touming,
    			R.drawable.touming		
    			}; 
    	
    	listItemLeftImgs = new int[]{
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			//R.drawable.selector_view_left_gred,
    			R.drawable.touming,
    			R.drawable.touming
    			}; 
    	
    	String autoSearchItem="";
        String manualSearchItem="";    
        String fineTuneItem="";
        String channelEditItem="";
        String voiceItem="";
        String pictrueItem=""; 
        String changeModeTempStr = "";
        String colorSysTempStr = "";
        String soundSysTempStr = "";
        
        if(0 == ParamSave.GetStopMode())
        	changeModeTempStr = this.context.getResources().getString(R.string.black_frame);
        else
        	changeModeTempStr = this.context.getResources().getString(R.string.frozen_frame);
        
        listItemVals = new String[]{
        		autoSearchItem, 
                manualSearchItem,     
                fineTuneItem,
                channelEditItem,
                //skipStr,
                voiceItem,
                pictrueItem ,
                //changeModeTempStr,
                colorSysTempStr,
                soundSysTempStr
    			}; 
    	
		for (int i=0; i<titles.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ListItemTitle", titles[i]);
            map.put("ListItemText", instructs[i]);
            map.put("ItemVal",listItemVals[i]);
            map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
			ListItemData.add(map);
		}
		ListItemAdapter = new SimpleAdapter(this.context, ListItemData, 
								R.layout.dvb_list_item, 
								new String[] {"ListItemTitle", "ListItemText", "ItemVal", "ItemRightImg","ItemLeftImg"},
								new int[] {R.id.ListItemTitle, R.id.ListItemText, R.id.setting_option_item_val, R.id.right_arrow_img, R.id.left_arrow_img});
		SettingListView.setAdapter(ListItemAdapter);

		SettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				listviewItemClick(position);
			}
		});
	}
	public void setPlayInfo(Bundle info)
	{
		playInfo = info;
	}

    public boolean getDismissOnItemClick() {
        return mDismissOnItemClick;
    }

    public boolean isShowOnEPGDismiss() {
        return mShowOnEPGDismiss;
    }

    public void setShowOnEPGDismiss(boolean mShowOnEPGDismiss) {
        this.mShowOnEPGDismiss = mShowOnEPGDismiss;
    }

    public void restarCountDownTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    public void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    public void statrCountDownTimer() {
        countDownTimer.start();
    }
    
    private void notifyListviewDataSetChanged(){
    	ListItemData.clear();
    	
    	if(0 == ParamSave.GetStopMode())
    		listItemVals[6] = this.context.getResources().getString(R.string.black_frame);
        else
        	listItemVals[6] = this.context.getResources().getString(R.string.frozen_frame);
    	
		for (int i=0; i<8; i++) {
    	    HashMap<String, Object> map = new HashMap<String, Object>(); 
            map.put("ListItemTitle", titles[i]);
            map.put("ListItemText", instructs[i]);
            map.put("ItemVal",listItemVals[i]);
            map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
    		ListItemData.add(map);
    	}
		ListItemAdapter.notifyDataSetChanged();
    }
    
    @Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		super.showAtLocation(parent, gravity, x, y);
        countDownTimer.start();
	}

    @Override
    public void dismiss() {
        mDismissOnItemClick = true;
        countDownTimer.cancel();
        super.dismiss();
    }
    
    public void unregisterReceiver(){
    	context.unregisterReceiver(mListViewReceiver);
    }
    
	private void listviewItemClick(int position) {
		// TODO Auto-generated method stub
		Intent intent;
		int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		int fre = ParamSave.GetMainFreq();
		int symbl = 6875;
		int qam = 3;
		int type = 0;
		int band = 8;
		Bundle bundle;
		if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
			// Skip Full-Scan item.
			if (position >= 2) {
				position++;
			}
		}
		switch (position)
		{
			case 0:
		        type = 0;
				intent = new Intent();
				bundle = new Bundle();
		        bundle.putInt("type", type);
		        bundle.putInt("tunertype", tunerType);
		        bundle.putInt("band", band);
		        bundle.putInt("fre", fre);
		        bundle.putInt("sym", symbl);
		        bundle.putInt("qam", qam);
		        intent.putExtras(bundle);
		    	intent.setClassName("com.um.dvbsearch", "com.um.ui.Search");
		    	context.startActivity(intent);
				break;
			case 1:
				if(tunerType==3){
					Intent intent1 = new Intent();
					intent1.setClassName("com.um.dvbsearch", "com.um.ui.MenualSearchWireless");
					context.startActivity(intent1);  								
				}else if(tunerType==2){
			          Intent  intent2 = new Intent();
					  intent2.setClassName("com.um.dvbsearch", "com.um.ui.MenualSearch");
					  context.startActivity(intent2);    								
				}

				break;
			case 2:
		        type = 2;
				intent = new Intent();
				bundle = new Bundle();
		        bundle.putInt("type", type);
		        bundle.putInt("tunertype", tunerType);
		        bundle.putInt("band", band);
		        bundle.putInt("fre", fre);
		        bundle.putInt("sym", symbl);
		        bundle.putInt("qam", qam);
		        intent.putExtras(bundle);
		    	intent.setClassName("com.um.dvbsearch", "com.um.ui.Search");
		    	context.startActivity(intent);
				break;
			case 3:
				intent = new Intent();
				bundle = new Bundle();
				bundle.putInt("tunertype", tunerType);
				intent.putExtras(bundle);
				intent.setAction("com.um.action.CHANNELS_EDITOR");
				context.startActivity(intent);
				break;
			case 4:
				Message msg2 = new Message();
				msg2.what = Constant.DvbSettingPopupWindow_DISMISS_AND_SHOW_EPG;
				mHandler.sendMessage(msg2);
		        mShowOnEPGDismiss = true;
				break;
			case 5:				
				if(localView.getVisibility()==View.VISIBLE){
					localView.setVisibility(View.INVISIBLE);
		            stopCountDownTimer();
				}			
				Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
				intent_pic.putExtra("dvb_cur_mode", ProviderProgManage.GetInstance(context).GetCurMode());
				context.startService(intent_pic);							
				break;
			/*
			case 6:
				ParamSave.SetStopMode(ParamSave.GetStopMode() != 0 ? 0 : 1);
				notifyListviewDataSetChanged();
				break;
				*/
			case 6:				
				if(localView.getVisibility()==View.VISIBLE){
					localView.setVisibility(View.INVISIBLE);
		            stopCountDownTimer();
				}
				Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
				context.startService(intent_voice);	
				break;
			case 7:
				intent = new Intent();
		    	intent.setClassName("com.um.dvbsettings", "com.um.ui.SysSetting");
		    	intent.putExtras(playInfo);
		    	context.startActivity(intent);
		    	
				break;
		}
		mDismissOnItemClick = false;
	}
}
