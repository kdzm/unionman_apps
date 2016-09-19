package cn.com.unionman.umtvsetting.powersave;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import cn.com.unionman.umtvsetting.powersave.R;
import cn.com.unionman.umtvsetting.powersave.util.Constant;
import cn.com.unionman.umtvsetting.powersave.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.powersave.interfaces.SystemSettingInterface;



import android.animation.Animator;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PowerMainSettingLayout extends LinearLayout implements View.OnFocusChangeListener{
	
	private static String TAG = "PowerMainSettingLayout";
	private Handler mHandler;
	private Context mContext;
    private TextView mBrightnessText;
    private TextView mContrastText;
    private TextView mSaturationText;
    private TextView mHueText;
    private TextView mSharpnessText;
    private TextView mBacklightText;
    private TextView mDisplaymodeText;
    private TextView mSeniorText;
    // list of text
    private TextView[] mTextList;
    
    
    
    // listView of menu options
    private ListView menuOptionsListView;
    // layout of up arrow
    private LinearLayout upArrowLayout;
    // layout of down arrow
    private LinearLayout downArrowLayout;
    // layout of menu item
    private LinearLayout mSettingLayout;
    // list of name
    // adapter of menuOptionsListView
    // position of menuOptionsListView
    private int mPositon;
    private AlertDialog mAlertDialog;
    private String[] mItems;
    
    private String[] listItems;
    private String[] listItemVals;
    private  String [] listItemsHelp;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
    private ListAdapter mListAdapter;
    private final SystemSettingInterface mSystemSettingInterface;
    
   private   ArrayList<HashMap<String, Object>> listName = new ArrayList<HashMap<String, Object>>(); 
   private SimpleAdapter mSchedule; 
   public static final String SERVICE_NAME = "cn.com.unionman.umtvsetting.powersave.FxService"; 
   private String powerDisplay ="";
   private Handler powerMainLayoutHandler;
   /**
    * handler of finish dialog item
    */
   private Handler finishHandle = new Handler() {
       public void handleMessage(android.os.Message msg) {
       	switch (msg.what) {
			case Constant.DIALOG_ITEM_DISMISS_BYTIME:
				 mAlertDialog.dismiss();
				break;
			}
           
       };
   };

   /**
    * set delay time to finish activity
    */
   public void delay() {
       finishHandle.removeMessages(Constant.DIALOG_ITEM_DISMISS_BYTIME);
       Message message = new Message();
       message.what = Constant.DIALOG_ITEM_DISMISS_BYTIME;
       finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
   }
   
	public PowerMainSettingLayout(Context context, Handler mainHandler) {
		super(context);
		mContext = context;
		powerMainLayoutHandler  = mainHandler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_power, this);
        menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
        mSettingLayout = (LinearLayout) findViewById(R.id.setting_containerlayout);
        mSystemSettingInterface = new SystemSettingInterface(context);
        addData();

	}

    /**
     * The main menu to add a menu item icon and text
     */
    private void addData() {	
    	
    	initListItemVals();
    	
   	 listItemRightImgs = new int[]{
  			R.drawable.selector_arrow_right_forever, 
  			R.drawable.selector_arrow_right_forever, 
  			R.drawable.selector_arrow_right_forever,
  			R.drawable.selector_arrow_right_forever
  			}; 
  	
  	 listItemLeftImgs = new int[]{
  			R.drawable.selector_arrow_left_forever, 
  			R.drawable.selector_arrow_left_forever, 
  			R.drawable.selector_arrow_left_forever,
			R.drawable.selector_arrow_left_forever
  			}; 
    	Log.i(TAG, "addData" + " " +mContext);
    	 listItems = new String[]{
    			getResources().getStringArray(R.array.power_setting)[0], 
    			getResources().getStringArray(R.array.power_setting)[1],
    			getResources().getStringArray(R.array.power_setting)[2], 
    			getResources().getStringArray(R.array.power_setting)[3]
    			};
    	
    	 listItemsHelp = new String[]{
     			getResources().getStringArray(R.array.power_setting_help)[0], 
     			getResources().getStringArray(R.array.power_setting_help)[1],
     			getResources().getStringArray(R.array.power_setting_help)[2], 
     			getResources().getStringArray(R.array.power_setting_help)[3]
     			};
    	
    	for (int i=0; i<listItems.length; i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItemsHelp[i]);
    		map.put("ItemVal",listItemVals[i]);
    		map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
    		listName.add(map);
    	}
    	
         mSchedule = new SimpleAdapter(mContext, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext", "ItemHelptxt","ItemVal","ItemRightImg","ItemLeftImg"},   
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val,R.id.right_arrow_img,R.id.left_arrow_img});
        //添加并且显示  
         menuOptionsListView.setAdapter(mSchedule);
//        mListAdapter = new ListAdapter(mContext);
//        menuOptionsListView.setAdapter(mListAdapter);  
        menuOptionsListView.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					//有按键操作时发送延时30s消失的消息	      
					delayForDialog();
					Log.i(TAG,"menuOptionsListView onKeyDown");
					int position = menuOptionsListView.getSelectedItemPosition();
					Log.i(TAG, "position "+menuOptionsListView.getSelectedItemPosition()+" key "+event.getKeyCode());
					View  view  = menuOptionsListView.getSelectedView();
					  TextView mTextView = (TextView) view.findViewById(R.id.setting_menu_item_val);	
					  switch (keycode) {
						  case KeyEvent.KEYCODE_DPAD_LEFT:
						  case KeyEvent.KEYCODE_DPAD_RIGHT:

				              listvieItemClick(position);
							  break;
					  }
				}
				return false;
			}
        	
        });
        
        menuOptionsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
              
              listvieItemClick(position);
            }	
		});
    }
	
    public void onDestroy() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.i(TAG, "onKeyDown");
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onFocusChange(View arg0, boolean arg1) {
    	// TODO Auto-generated method stub
    	
    }
    private void listvieItemClick(int position) {
		mPositon = position;
		Log.i(TAG,"listvieItemClick position="+position);
		  if(position == 3){
			  int flag = mSystemSettingInterface.getWaitting();
			    final String [] dialog_item_str = new String[]{
			    	getResources().getString(R.string.off),	
			    	getResources().getString(R.string.onehrs),	
			    	getResources().getString(R.string.twohrs),	
			    	getResources().getString(R.string.threehrs),	
			    	getResources().getString(R.string.fourhrs),	
			    };
		  	    int [] dialog_item_img = new int[]{
			    		R.color.transparent,
			    		R.color.transparent,
			    		R.color.transparent,
			    		R.color.transparent,
			    		R.color.transparent,
			    }; 
		  	   dialog_item_img[flag]=R.drawable.net_select;
		  	   
		  	     AlertDialog.Builder  builder =new AlertDialog.Builder(mContext,R.style.Dialog_item);
		  	      builder.setTitle(listItems[position]);  
		  	      LayoutInflater factory = LayoutInflater.from(mContext);
		  	      View myView = factory.inflate(R.layout.dialog_layout,null);
		  	     final ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
		  	   ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
		 		for (int i=0; i<dialog_item_str.length; i++) {
		 			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		    		map.put("ItemContext", dialog_item_str[i]); 
		    		map.put("ItemImg", dialog_item_img[i]); 		
		    		listDialog.add(map);
		    	}
		  		 SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
		                 new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
		  		
		       lisview.setAdapter(mSimpleAdapter);
				  lisview.setSelection(flag);
		  	      builder.setView(myView); 
		    	   mAlertDialog = builder.create();
		    	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		    	   mAlertDialog.show();	
		    	   delay();
			      
		           lisview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int selectItemSoundMode, long arg3) {
						    mSystemSettingInterface.setWaitting(selectItemSoundMode);
						   	notifyListviewForDataChange();
						    
		        	    int [] item_dialog_item_img = new int[]{
		        	    		R.color.transparent,
		        	    		R.color.transparent,
		        	    		R.color.transparent,
		        	    		R.color.transparent,
		        	    		R.color.transparent,
		        	    };
		        	    item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
			      	    ArrayList<HashMap<String, Object>> item_listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
			     		for (int i=0; i<dialog_item_str.length; i++) {
			     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
			        		map.put("ItemContext", dialog_item_str[i]); 
			        		map.put("ItemImg", item_dialog_item_img[i]); 		
			        		item_listDialog.add(map);
			        	}
		        	    
				      		 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
		                              new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
						   lisview.setAdapter(itemSimpleAdapter);
						  lisview.setSelection(selectItemSoundMode);

						}
					});  
			
		         	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
		    			
		    			@Override
		    			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
		    				// TODO Auto-generated method stub
		                 if(arg2.getAction() == KeyEvent.ACTION_DOWN){
		    						delay();
		                 }
		    				return false;
		    			}
		    		});
		  }else if(position == 0){
				int state = mSystemSettingInterface.getSavingEnergy(); 
				   state--;
		            if (state < 0) {
		            	state = 1;
		            }  
		             mSystemSettingInterface.setSavingEnergy(state);
		             notifyListviewForDataChange();
		  }else if(position == 1){
/*				int displaystate = mSystemSettingInterface.getPowerDisplay(); 
				displaystate--;
		            if (displaystate < 0) {
		            	displaystate = 1;
		            }  
		             mSystemSettingInterface.setPowerDisplay(displaystate);
		             notifyListviewForDataChange();
		             
		     		if(displaystate==0){ //off
						Intent intent = new Intent(mContext, FxService.class);
						mContext.stopService(intent);	
						Log.i(TAG,"stopService FxService");
		    		}else if(displaystate==1){ //on
						Intent intent = new Intent(mContext, FxService.class);
						mContext.startService(intent);	
						Log.i(TAG,"startservice FxService");						
		    		}	*/
			    if(powerDisplay.equals(getResources().getString(R.string.on))){
					Intent intent = new Intent(mContext, FxService.class);
					mContext.stopService(intent);	
					Log.i(TAG,"stopService FxService");
			    }else{
					Intent intent = new Intent(mContext, FxService.class);
					mContext.startService(intent);	
					Log.i(TAG,"startservice FxService");			    	
			    }
			    notifyListviewForDataChange();
		  }else if(position == 2){
				int shutdownstate = mSystemSettingInterface.getAutoShutdonw(); 
				shutdownstate--;
		            if (shutdownstate < 0) {
		            	shutdownstate = 1;
		            }  
		             mSystemSettingInterface.setAutoShutdonw(shutdownstate);
		             notifyListviewForDataChange();
		  }
		  
		  else{
			  ;
		  }
	}
	private class ListAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        public ListAdapter(Context context){
        	mInflater = LayoutInflater.from(context);
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = mInflater.inflate(R.layout.setting_item2, null);
				holder = new ViewHolder();
				holder.item_txt = (TextView) convertView.findViewById(R.id.setting_menu_item_txt);
				holder.item_txt_instruction = (TextView) convertView.findViewById(R.id.setting_menu_item_txt2);
				holder.item_txt_val = (TextView) convertView.findViewById(R.id.setting_menu_item_val);
				holder.right_img = (ImageView) convertView.findViewById(R.id.right_arrow_img);
				holder.left_img = (ImageView) convertView.findViewById(R.id.left_arrow_img);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.item_txt.setText(listItems[position]);
			holder.item_txt_instruction.setText(listItems[position]);
			holder.item_txt_val.setText(listItemVals[position]);
			holder.right_img.setBackgroundResource(listItemRightImgs[position]);
			holder.left_img.setBackgroundResource(listItemLeftImgs[position]);
			return convertView;
		}
    	
    }
    public final class ViewHolder{
    	public TextView item_txt;
    	public TextView item_txt_instruction;
    	public TextView item_txt_val;
    	public ImageView right_img;
    	public ImageView left_img;
    }
    /**
     * 初始化ListItem中的值
     */
   private void initListItemVals(){
	   if(isServiceRunning(SERVICE_NAME)){
		   powerDisplay=getResources().getString(R.string.on);
	   }else{
		   powerDisplay=getResources().getString(R.string.off);
	   }
	     listItemVals = new String[]{
	    		 getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getSavingEnergy()][1]),
//	    		 getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getPowerDisplay()][1]),
	    		 powerDisplay,
	    		 getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getAutoShutdonw()][1]),
	    		 getResources().getString(InterfaceValueMaps.Auto_Standby[mSystemSettingInterface.getWaitting()][1]),
	    	 };
   }
   /**
    * 数据发生变化时，刷新listview
    */
		private void notifyListviewForDataChange() {
			initListItemVals();
			listName.clear();
			for (int i=0; i<listItems.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>(); 
				map.put("ItemContext", listItems[i]);
				map.put("ItemHelptxt", listItemsHelp[i]);
				map.put("ItemVal",listItemVals[i]);
				map.put("ItemRightImg",listItemRightImgs[i]);
				map.put("ItemLeftImg",listItemLeftImgs[i]);
				listName.add(map);
			}
			mSchedule.notifyDataSetChanged();
    }

		 
		/**
		 * 本方法判断Service是否已经运行
		 * @return
		 */
		public  boolean isServiceRunning(String serviceName)
		 {
		  ActivityManager myManager=(ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		  for(int i = 0 ; i<runningService.size();i++)
		  {
		   if(runningService.get(i).service.getClassName().toString().equals(serviceName))
		   {
		    return true;
		   }
		  }
		  return false;
		 }
		
		   @Override
		    public void onWindowFocusChanged(boolean hasFocus) {
		        if (hasFocus) {
		        	delayForDialog();
		        } else {
		        	powerMainLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
		        }
		        super.onWindowFocusChanged(hasFocus);
		    }
		    public void delayForDialog() {
		    	powerMainLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
		        Message message = new Message();
		        message.what = Constant.DIALOG_DISMISS_BYTIME;
		        powerMainLayoutHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
		    }	
}
