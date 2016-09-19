package cn.com.unionman.umtvsetting.appmanage;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.com.unionman.umtvsetting.appmanage.R;
import cn.com.unionman.umtvsetting.appmanage.util.Constant;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.SimpleAdapter;


public class AppManageMainSettingLayout extends LinearLayout implements View.OnFocusChangeListener{
	
	private static String TAG = "AppManageMainSettingLayout";
	private Context mContext;
    private Handler appManageHandler;
    // listView of menu options
    private ListView menuOptionsListView;
    
    ArrayList<HashMap<String, String>> listName = new ArrayList<HashMap<String, String>>(); 
    
	public AppManageMainSettingLayout(Context context, Handler mHandler) {
		super(context);
		mContext = context;
		appManageHandler = mHandler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_appmanage, this);
        menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
	        menuOptionsListView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					if (arg2.getAction() == KeyEvent.ACTION_DOWN) {  
						//有按键操作时发送延时30s消失的消息	      
						delay();
						Log.i(TAG,"menuOptionsListView onKeyDown");
					}
					return false;
				}
			});
        addData();
	}

    /**
     * The main menu to add a menu item icon and text
     */
    private void addData() {	
    	
    	Log.i(TAG, "addData" + " " +mContext);
    	String[] listItems = new String[]{
    			getResources().getStringArray(R.array.system_appman)[1],
    		//	getResources().getStringArray(R.array.system_appman)[2], 
    			getResources().getStringArray(R.array.system_appman)[3]
    			};
    	String[] listItemsHelp = new String[]{
    			getResources().getStringArray(R.array.system_appman_help)[1],
    		//	getResources().getStringArray(R.array.system_appman_help)[2], 
    			getResources().getStringArray(R.array.system_appman_help)[3]
    			};
    	
    	for (int i=0; i<listItems.length; i++) {
    		HashMap<String, String> map = new HashMap<String, String>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItemsHelp[i]);
    		listName.add(map);
    	}
    	
        SimpleAdapter mSchedule = new SimpleAdapter(mContext, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext", "ItemHelptxt"},   
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2});
        //添加并且显示  
        menuOptionsListView.setAdapter(mSchedule);
        
        menuOptionsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3){
	              if (position == 0)
	              {
	            	  Intent itent = new Intent();
		              itent.setClassName(mContext, "cn.com.unionman.umtvsetting.appmanage.AppManageSortActivity");
		              mContext.startActivity(itent); 
/*	              }else if (position == 1){
	            	  Intent itent = new Intent();
		              itent.setClassName(mContext, "cn.com.unionman.umtvsetting.appmanage.AppManageUpdateActivity");
		              mContext.startActivity(itent); */
	              }else if (position == 1){
	            	  Intent itent = new Intent();
		              itent.setClassName(mContext, "cn.com.unionman.umtvsetting.appmanage.AppManageRemoveActivity");
		              mContext.startActivity(itent); 
	              }
			}
        	
		});
    }
    
    @Override
    public void onFocusChange(View arg0, boolean arg1) {
    	// TODO Auto-generated method stub
    	
    }
    
	   @Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	        if (hasFocus) {
	        	delay();
	        } else {
	        	appManageHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        }
	        super.onWindowFocusChanged(hasFocus);
	    }
	    public void delay() {
	    	appManageHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_DISMISS_BYTIME;
	        appManageHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
	    }
}
