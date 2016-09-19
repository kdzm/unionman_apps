package com.portplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.portplayer.R;
import com.portplayer.util.Constant;



/**
 * when you press the menu button,SettingActivity will show
 *
 * @author wangchuanjian
 *
 */
public class SettingActivity extends Activity {
    // message of finish activity
    private static final int ACTIVITY_FINISH = 0;
    private String TAG = "SettingActivity";
    private RelativeLayout rl_main;
    private ListView settingListView = null;
    private ArrayList<HashMap<String, String>> listName = new ArrayList<HashMap<String, String>>();
	private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
    private BroadcastReceiver mListViewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context content, Intent intent) {

            String action = intent.getAction();
            if (action.equals(PIC_SET_FINISH_ACTION)) {

            	if (rl_main.getVisibility() == View.INVISIBLE) {
            		rl_main.setVisibility(View.VISIBLE);
               }

            } else if (action.equals(SOUND_SET_FINISH_ACTION)) {
            	
                if (rl_main.getVisibility() == View.INVISIBLE) {
                	rl_main.setVisibility(View.VISIBLE);
               }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter listViewSet = new IntentFilter(PIC_SET_FINISH_ACTION);
        listViewSet.addAction(SOUND_SET_FINISH_ACTION);
        registerReceiver(mListViewReceiver, listViewSet);
        setContentView(R.layout.activity_setting);
        initView();
    }

    /**
     * The initialization of all views
     */
    private void initView() {
    	settingListView = (ListView)findViewById(R.id.setting_list);	
    	rl_main = (RelativeLayout) findViewById(R.id.rl_main);
    	addData();
    }
    
    /**
     * The main menu to add a menu item icon and text
     */
    private void addData() {	
    	
    	String[] listItems = new String[]{
    			getResources().getStringArray(R.array.menu_items)[0],
    			getResources().getStringArray(R.array.menu_items)[1], 
    			};
    	String[] listItemsHelp = new String[]{
    			getResources().getStringArray(R.array.menu_items_help)[0],
    			getResources().getStringArray(R.array.menu_items_help)[1], 
    			};
    	   	
    	for (int i=0; i<listItems.length; i++) {
    		HashMap<String, String> map = new HashMap<String, String>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItemsHelp[i]);
    		listName.add(map);
    	}
    	
        SimpleAdapter mSettingAdapter = new SimpleAdapter(this, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext", "ItemHelptxt"},   
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2});
        //ÃÌº”≤¢«“œ‘ æ  
        settingListView.setAdapter(mSettingAdapter);
        settingListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3){
	              listviewOnItemClick(position);	
			}
        	
		});
        
        settingListView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent arg2) {				
				if (arg2.getAction() == KeyEvent.ACTION_DOWN){
					int position = settingListView.getSelectedItemPosition();
					delay();
					switch (keycode) {
					 case KeyEvent.KEYCODE_DPAD_DOWN:
						 if(settingListView.getSelectedItemPosition() ==settingListView.getCount() - 1){
							 settingListView.setSelection(0); 
						 }
	                 break;
			        case KeyEvent.KEYCODE_DPAD_UP:
			            if (settingListView.getSelectedItemPosition() == 0) {
			            	settingListView.setSelection(settingListView.getCount() - 1);
			            }
			            break;	 
		            case KeyEvent.KEY_SOURCEENTER:
		            	Log.i(TAG,"KEY_SOURCEENTER is clicked");
		            	 listviewOnItemClick(position);
		            	 return true;     
					}					
				}
				return false;
			}
		});
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        delay();
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
        case KeyEvent.KEYCODE_BACK:
        case KeyEvent.KEYCODE_TVSETUP:
        	doExit();
            break;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

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
            if (msg.what == ACTIVITY_FINISH)
            	doExit();
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay() {
        finishHandle.removeMessages(ACTIVITY_FINISH);
        Message message = new Message();
        message.what = ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mListViewReceiver);
    }
    
    private void doExit(){
    	finish();
    }

	private void listviewOnItemClick(int position) {
		if (position == 0)
		  {
			Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
			startService(intent_pic);
		  }else if (position == 1){
			Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
			startService(intent_voice);	
		  }
			if(rl_main.getVisibility()==View.VISIBLE){
				rl_main.setVisibility(View.INVISIBLE);
			}
	}
}
