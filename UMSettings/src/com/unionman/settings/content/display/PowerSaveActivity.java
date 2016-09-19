package com.unionman.settings.content;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.Window;
import android.view.View.OnFocusChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.content.display.SystemSettingInterface;
import com.unionman.settings.content.SetTimeToSleep;
import com.unionman.settings.content.display.InterfaceValueMaps;

public class PowerSaveActivity extends RightWindowBase implements OnItemClickListener, OnFocusChangeListener {
	private static final String TAG = "PowerSaveActivity";
//	public static final String POWER_SHOW_SERVICE_NAME = 
//			"cn.com.unionman.umtvsetting.powersave.FxService"; tag:zs
	
	private final int LEFT_ARROW_ID = R.drawable.left_arrow;
	private final int RIGHT_ARROW_ID = R.drawable.arrow;
	private final byte FROM_LEFT_SIDE = 20;
	private final byte FROM_HEAD = 21;
	private final byte FROM_NAIL = 22;
	private final byte RESET_VAL = 23;
	private final byte FROM_DOWN = 24;
	private int curSelectedPos;
	private byte focusFrom = FROM_LEFT_SIDE;
	
	private View containerView;
	private ListView lvPwrSaving;
//	private String powerDisplay;  20160701,暂时拿掉动态功耗演示功能。tag:zs
	private String[] listItemVals;
	private String[] listItemName;
	private ArrayList<HashMap<String, Object>> listName;
	private SimpleAdapter mSimpleAdapter;
	private AlertDialog mAlertDialog;
	private View viewBottom;
	private TextView tvTitle;
	
	private SystemSettingInterface mSystemSettingInterface;
	
	public PowerSaveActivity(Context paramContext) {
		super(paramContext);
	}

	@Override
	public void initData() {
	}

	@Override
	public void onInvisible() {
	}

	@Override
	public void onResume() {
		notifyDatasetChanged();
	}

	@Override
	public void setId() {
		this.levelId = 1001;
		mSystemSettingInterface = new SystemSettingInterface(context);
	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.display_pwr_save, this);
		lvPwrSaving = (ListView)findViewById(R.id.lvPwrSave);
		viewBottom = (View)findViewById(R.id.view2);
		tvTitle = (TextView)findViewById(R.id.tvTitle2);
		initListItemVals();
		
		//设置焦点监听。
		viewBottom.setOnFocusChangeListener(this);
		tvTitle.setOnFocusChangeListener(this);
		lvPwrSaving.setOnFocusChangeListener(this);
		lvPwrSaving.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
					focusFrom = FROM_LEFT_SIDE;
				}
				return false;
			}
		});
	}
	
    /**
     * 初始化ListItem中的值
     */
	private void initListItemVals() {
//		if (isServiceRunning(POWER_SHOW_SERVICE_NAME)) {tag:zs
//			powerDisplay = getResources().getString(R.string.on);
//		} else {
//			powerDisplay = getResources().getString(R.string.off);
//		}
		//节能设置中当前的值。
		listItemVals = new String[] {
				getResources().getString(InterfaceValueMaps.
						on_off[mSystemSettingInterface.getSavingEnergy()][1]),
//				powerDisplay, tag:zs
				getResources().getString(InterfaceValueMaps.
						on_off[mSystemSettingInterface.getAutoShutdonw()][1]),
				getResources().getString(InterfaceValueMaps.
						Auto_Standby[mSystemSettingInterface.getWaitting()][1]), 
		};
		//节能设置项的名称。
		listItemName = new String[] { 
				getResources().getStringArray(R.array.power_setting)[0],
//				getResources().getStringArray(R.array.power_setting)[1],tag:zs
				getResources().getStringArray(R.array.power_setting)[2],
				getResources().getStringArray(R.array.power_setting)[3] 
		};
		//开始赋值。
		HashMap<String, Object> map = null;
		listName = new ArrayList<HashMap<String, Object>>();
		for(byte idx = 0; idx < listItemName.length; idx++){
			map = new HashMap<String, Object>(); 
    		map.put("ItemContext", listItemName[idx]);
    		map.put("ItemVal",listItemVals[idx]);
    		map.put("ItemRightImg",RIGHT_ARROW_ID);
    		map.put("ItemLeftImg",LEFT_ARROW_ID);
    		listName.add(map);
		}
		listName.remove(2);
		///准备适配器。
		mSimpleAdapter = new SimpleAdapter(context, listName,
                R.layout.power_save_lv_content_layout, 
                new String[] {
                		"ItemContext", 
                		"ItemVal",
                		"ItemRightImg",
                		"ItemLeftImg"
                },   
                new int[] {
                		R.id.setting_menu_item_txt,
                		R.id.setting_menu_item_val,
                		R.id.right_arrow_img,
                		R.id.left_arrow_img
                }
		);
		//设置适配器上去。
		lvPwrSaving.setAdapter(mSimpleAdapter);
		//给ListView设置相应的监听器。
		lvPwrSaving.setOnItemClickListener(this);
	}
	
//	private  boolean isServiceRunning(String serviceName){ tag:zs
//	  ActivityManager myManager=(ActivityManager)context
//			  .getSystemService(Context.ACTIVITY_SERVICE);
//	  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) 
//			  myManager.getRunningServices(30);
//	  for(int i = 0 ; i<runningService.size();i++){
//		   if(runningService.get(i).service.getClassName().toString()
//				   .equals(serviceName)){
//			   return true;
//		   }
//	  }
//	  return false;
//	}
	
	private void switchState(int pos, String actionEvent){
		Log.d(TAG, "switching state by "+actionEvent+".Item:"+pos);
		curSelectedPos = pos;
		String result = getResources().getString(R.string.on);
		switch(pos){
			case 0:{ //动态功能设置
				int state = mSystemSettingInterface.getSavingEnergy(); 
				   state--;
		            if (state < 0) {
		            	state = 1;
		            }  
		        mSystemSettingInterface.setSavingEnergy(state);
		        notifyDatasetChanged();
			}break;
//			case 1:{tag:zs
//				if(powerDisplay.equals(getResources().getString(R.string.on))){
//					Intent intent = new Intent();
//					intent.setClassName("cn.com.unionman.umtvsetting.powersave", 
//							"cn.com.unionman.umtvsetting.powersave.FxService");
//					context.stopService(intent);	
//					Log.i(TAG,"stopService FxService");
//			    }else{
//					Intent intent = new Intent();
//					intent.setClassName("cn.com.unionman.umtvsetting.powersave", 
//							"cn.com.unionman.umtvsetting.powersave.FxService");
//					context.startService(intent);	
//					Log.i(TAG,"startservice FxService");			    	
//			    }
//				notifyDatasetChanged();
//			}break;
			case 1: {
				int shutdownstate = mSystemSettingInterface.getAutoShutdonw();
				shutdownstate--;
				if (shutdownstate < 0) {
					shutdownstate = 1;
				}
				mSystemSettingInterface.setAutoShutdonw(shutdownstate);
				notifyDatasetChanged();
			}break;
			case 2:{
				pwrSaveAutoSleep();
			}break;
			default:
				Log.e(TAG, "error.");
		}// switch  --  end.
	}
	
	private void notifyDatasetChanged(){
		initListItemVals();
		//刷新列表当前选中的项。
		lvPwrSaving.setSelection(curSelectedPos);
	}
	
	private void pwrSaveAutoSleep(){
		SetTimeToSleep.mSystemSettingInterface = this.mSystemSettingInterface;
		try {
			PowerSaveActivity.this.layoutManager
			.showLayout(ConstantList.SET_TIME_TO_SLEEP);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		switchState(pos, "click");
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId()){
			case R.id.lvPwrSave:
				lvFocus(hasFocus);
				break;
			case R.id.view2:
				viewBottomFocus(hasFocus);
				break;
			case R.id.tvTitle2:
				tvTitle2Focus(hasFocus);
				break;
		}// switch  --  end.
	}

	private void lvFocus(boolean hasFocus) {
		Log.d(TAG, "lvFocus,"+hasFocus+" from:"+focusFrom);
		if(hasFocus){
			lvPwrSaving.setSelector(R.drawable.setitem_focus);
			if(focusFrom == FROM_HEAD){
				lvPwrSaving.setSelection(1);
			}else if(FROM_NAIL == focusFrom){
				lvPwrSaving.setSelection(0);
			}else {
				lvPwrSaving.setSelection(0);
			}
			
			focusFrom = RESET_VAL;
		}else{
			lvPwrSaving.setSelector(R.drawable.tui_numberpicker_trans);
		}
	}

	private void viewBottomFocus(boolean hasFocus) {
		Log.d(TAG, "viewBottomFocus,"+hasFocus);
		focusFrom = FROM_NAIL;
		lvPwrSaving.requestFocus();
	}

	private void tvTitle2Focus(boolean hasFocus) {
		Log.d(TAG, "tvTitle2Focus,"+hasFocus);
		//If this focus is from the left-side view,then,let the ListView select 0.
		if(focusFrom == RESET_VAL)
			focusFrom = FROM_HEAD;
		lvPwrSaving.requestFocus();
	}

}
