package com.um.music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.um.music.util.FileUtil;
import com.um.music.MediaPlaybackActivity.TmpMediaPlaybackActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MediaPlayMenuActivity extends Activity{
	private static final String TAG = "MediaPlayMenuActivity";
	
    private String[] listMenuItems;
    private ArrayList<HashMap<String, Object>> listMenuName = new ArrayList<HashMap<String, Object>>(); 
	private String[] listMenuItemVals;
	private int[] listMenuItemRightImgs;
	private int[] listMenuItemLeftImgs;

	private String[] listItems;
	
	private String titleName ;
	//private Audio  currAudio;
    private ListView mListView;
    private int[] sound_mode_flag;
    
    private  String[] track_mode; 
    private  String[] modeVal;
    
    private AlertDialog mAlertDialog;
    private SimpleAdapter adapter;
    private Dialog menuDialog    = null;
    private View mView;
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
	
	public static final int DISPEAR_TIME_30s = 30000;
	private static final int ACTIVITY_FINISH = 0;
	private static final int DIALOG_FINISH=1;
	 
	private boolean stopPlay = true;
 
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	IntentFilter listViewSet = new IntentFilter(SOUND_SET_FINISH_ACTION);
        MediaPlayMenuActivity.this.registerReceiver(mListViewReceiver, listViewSet);
    	
    	setContentView(R.layout.menu_view);
    	mView = (LinearLayout) findViewById(R.id.menu_containerlayout);
        mListView = (ListView) findViewById(R.id.menuview_list);

    	listMenuItemRightImgs = new int[]{
  	  			R.drawable.touming,     
    			R.drawable.touming
    			}; 
    	
    	listMenuItemLeftImgs = new int[]{
    			R.drawable.touming,
    			R.drawable.touming
    			}; 
    	listMenuItems = new String[]{
        		getResources().getString(R.string.audioplay_audioinfo),
        		getResources().getString(R.string.audioplay_soundselect)
    	};
	      
    	listMenuItemVals = new String[]{
        	null,null
    	};
    	for (int i=0; i<listMenuItems.length; i++) {
    	    HashMap<String, Object> map = new HashMap<String, Object>(); //锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷娲istview锟斤拷锟斤拷示锟斤拷锟斤拷锟� 
    		map.put("ItemContext", listMenuItems[i]);
    		map.put("ItemVal",listMenuItemVals[i]);
    		map.put("ItemRightImg",listMenuItemRightImgs[i]);
    		map.put("ItemLeftImg",listMenuItemLeftImgs[i]);
    		listMenuName.add(map);
    	}
    	 	  
    	adapter = new SimpleAdapter(this, listMenuName,R.layout.menu_view_option,
    			new String[] {"ItemContext","ItemVal","ItemRightImg","ItemLeftImg"},
                new int[] {R.id.setting_option_item_txt,R.id.setting_option_item_val,R.id.right_arrow_img,R.id.left_arrow_img});  
        mListView.setAdapter(adapter);
        
      /*//  final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        menuDialog.setContentView(mView);
        
        dialogAutoDismiss(menuDialog);*/
		Intent intent=getIntent(); 
		titleName=intent.getStringExtra("titleName"); 

        mListView
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
            	
            	if (mView.getVisibility() == View.VISIBLE) {
              		mView.setVisibility(View.INVISIBLE);
                 } 
            	
            	 if ( position == 0 ) {

    				File MP3FILE = new File(titleName);
    				FileUtil fileutil= new FileUtil(MediaPlayMenuActivity.this);
    				fileutil.showFileInfo(MP3FILE);
    				fileutil.getDialog().setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							if (mView.getVisibility() == View.INVISIBLE) {
		                  		mView.setVisibility(View.VISIBLE);
		                     } 
						}
					});
                 }                    
            	 
            	 if ( position == 1) {
                   
            		 Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
            		 MediaPlayMenuActivity.this.startService(intent_voice);
                  	
                 }
            	
            }     
        });
    	
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	switch(keyCode) {
    		case KeyEvent.KEYCODE_BACK:
    			stopPlay = false;
    			break;
			default:
				break;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	if (stopPlay == true)
    	{
    		((MediaPlaybackActivity)TmpMediaPlaybackActivity.activity).onPause();
    	}
    	stopPlay = true;
    	super.onPause();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
	}

    private void notifyListviewForDataChange() {
	      listMenuItemVals = new String[]{
   		     null,
   	         null
 			}; 
		
	      listMenuName.clear();
		for (int i=0; i<listMenuItems.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>(); 
			map.put("ItemContext", listMenuItems[i]);
			map.put("ItemVal",listMenuItemVals[i]);
			map.put("ItemRightImg",listMenuItemRightImgs[i]);
			map.put("ItemLeftImg",listMenuItemLeftImgs[i]);
			listMenuName.add(map);
		}
		adapter.notifyDataSetChanged();
    }
    
    private BroadcastReceiver mListViewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context content, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (Constants.LOG_TAG) {
                Log.d(TAG, "onReceive--->action:" + action);
            }
          if (action.equals(SOUND_SET_FINISH_ACTION)) {
            	if (mView.getVisibility() == View.INVISIBLE) {
            		mView.setVisibility(View.VISIBLE);
               }
            }
        }
    };
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            delay();
        } else {
            finishHandle.removeMessages(ACTIVITY_FINISH);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * handler of finish activity
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	stopPlay = false;
            if (msg.what == ACTIVITY_FINISH)
            { finish();}
            else if(msg.what == DIALOG_FINISH){
            	mAlertDialog.dismiss();
            }
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay() {
        finishHandle.removeMessages(ACTIVITY_FINISH);
        Message message = new Message();
        message.what = ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
    }
    
    /**
     * set delay time to dismiss dialog
     */
    public void dialogdelay() {
        finishHandle.removeMessages(DIALOG_FINISH);
        Message message = new Message();
        message.what = DIALOG_FINISH;
        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
    }
    
}
