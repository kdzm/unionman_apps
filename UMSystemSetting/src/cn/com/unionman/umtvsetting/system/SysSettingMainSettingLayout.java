package cn.com.unionman.umtvsetting.system;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.SystemSettingInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.logic.factory.LogicFactory;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.IpAddrEdit;
import cn.com.unionman.umtvsetting.system.util.SocketClient;
import cn.com.unionman.umtvsetting.system.widget.CustomSettingView;
import com.hisilicon.android.tvapi.UmtvManager;

import cn.com.unionman.umtvsetting.system.util.SystemUtils;
import android.os.SystemProperties;


public class SysSettingMainSettingLayout extends LinearLayout implements View.OnFocusChangeListener{
	
	private static String TAG = "SysSettingMainSettingLayout";
	private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
	private Handler sysSettingLayoutHandler;
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
    private LinearLayout mSysSettingLay;
    // list of name
    // adapter of menuOptionsListView
    //private SettingListAdapter mSettingListAdapter;
    // position of menuOptionsListView
    private int mPositon;
    private AlertDialog mAlertDialog;
    private LogicFactory mLogicFactory = null;
    // widget container
    private CustomSettingView mCustomSettingView;
    private String[] mItems;
    private String[] listItems;
    private String[] listItemVals;
    private  String[] listItemsHelp;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
    private ListAdapter mListAdapter;
    private SimpleAdapter mSchedule ;
    private Dialog   pwdDialog;
	private Button mSystemOKBtn;
	private Button mSystemCancelBtn;
	private EditText edittext;
	private boolean isBluetoothEnable = false;
	private int passwork_lock_position =9;
	private int storageInfo_position =10;
	private int resetUserMode_position =11;
	private int resetFactory_position =12;
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
        	 String str = mContext.getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            mAlertDialog.dismiss();
        }
    };
    
    private CountDownTimer mPwdCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
        	 String str = mContext.getString(R.string.ok) + "(" + millisUntilFinished/1000 + "s)";
        	 mSystemOKBtn.setText(str);
        }

        public void onFinish() {
        	  mAlertDialog.dismiss();
			  pwdDialog.show();
        	  edittext.setText("");
        	  edittext.requestFocus();
/*        	  delayForPwdDialog();*/
        }
    };
    
    private final SystemSettingInterface mSystemSettingInterface;
    
    private ArrayList<HashMap<String, Object>> listName = new ArrayList<HashMap<String, Object>>(); 

    private  FactoryResetDialogLayout mFactoryResetDialogLayout;
    
    private StorageInfoLayout mStorageInfoLayout;
    
    private TimeSettingDialogLayout mTimeSettingDialogLayout;
    
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				mAlertDialog.dismiss();
				mAlertDialog = null;
				break;
			case Constant.DIALOG_PWD_DISMISS_BYTIME:
				pwdDialog.dismiss();
				pwdDialog = null;
				break;	
			}

        }
	};
	public SysSettingMainSettingLayout(Context context, Handler mainHandler, String action) {
		super(context);
        this.action = action;
		mContext = context;
		BluetoothAdapter  bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		isBluetoothEnable =bluetoothAdapter.isEnabled();
		Log.i(TAG,"===isBluetoothEnable="+isBluetoothEnable);
		if(!isBluetoothEnable){
			passwork_lock_position = 8;
			storageInfo_position = 9;
			resetUserMode_position = 10;
			resetFactory_position = 11;
		}
		
		sysSettingLayoutHandler = mainHandler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_system, this);
        menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
        mSysSettingLay = (LinearLayout)findViewById(R.id.setting_containerlayout);
        mLogicFactory = new LogicFactory(mContext);
         mSystemSettingInterface =new SystemSettingInterface(context);
        addData();
        
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction(RESET_MONITORS);
    	mContext.registerReceiver(systemEventMonitorsReceiver, intentFilter);

        
	}

    private String action = "";
    public SysSettingMainSettingLayout(Context context, Handler mainHandler) {
        this(context, mainHandler, null);
    }

    /**
     * The initialization of ListView
     */
   /* private void initListView() {
        mSettingListAdapter = new SettingListAdapter(mContext, this);
        mItems = mContext.getResources().getStringArray(R.array.senior_setting);
        mSettingListAdapter.setList(mItems);
        menuOptionsListView.setAdapter(mSettingListAdapter);
        
    }*/
    
    /**
     * The main menu to add a menu item icon and text
     */
    private void addData() {	
    	initListItemVals();
    	 listItemRightImgs = new int[]{
 	 			R.drawable.selector_arrow_right,
 	 			R.drawable.selector_arrow_right,
// 	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
	 			R.drawable.selector_arrow_right,
//    	 			R.color.transparent,
	 		    	R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			}; 
    	 	
    	 	 listItemLeftImgs = new int[]{
     	 			R.drawable.selector_arrow_left,
       	 			R.drawable.selector_arrow_left,
//    	 			R.drawable.selector_arrow_left,
    	 			R.drawable.selector_arrow_left,
       	 			R.drawable.selector_arrow_left,
    	 			R.drawable.selector_arrow_left,
       	 			R.drawable.selector_arrow_left,
    	 			R.drawable.selector_arrow_left,
    	 			R.drawable.selector_arrow_left,
//    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			R.color.transparent,
    	 			}; 
    	
    	Log.i(TAG, "zemin addData" + " " +mContext);
    	 listItems = new String[]{
    			getResources().getStringArray(R.array.system_setting)[5],
    			getResources().getStringArray(R.array.system_setting)[6], 
//    			getResources().getStringArray(R.array.system_setting)[7],
    			getResources().getStringArray(R.array.system_setting)[9],
    			getResources().getStringArray(R.array.system_setting)[10],
    			getResources().getStringArray(R.array.system_setting)[4],
    			getResources().getStringArray(R.array.system_setting)[3],
//    			getResources().getStringArray(R.array.system_setting)[1],
    			getResources().getStringArray(R.array.system_setting)[7],
    			getResources().getStringArray(R.array.system_setting)[15],
    			getResources().getStringArray(R.array.system_setting)[11],
    			getResources().getStringArray(R.array.system_setting)[14],
    			getResources().getStringArray(R.array.system_setting)[12],
    			getResources().getStringArray(R.array.factory_reset)[0],
    			getResources().getStringArray(R.array.factory_reset)[1], 
    			getResources().getString(R.string.ntp_address)
    			};
    	  listItemsHelp = new String[]{
     			getResources().getStringArray(R.array.system_setting_help)[5],
     			getResources().getStringArray(R.array.system_setting_help)[6], 
//     			getResources().getStringArray(R.array.system_setting_help)[7],
     			getResources().getStringArray(R.array.system_setting_help)[9],
     			getResources().getStringArray(R.array.system_setting_help)[10],
     			getResources().getStringArray(R.array.system_setting_help)[4],
     			getResources().getStringArray(R.array.system_setting_help)[3],
//     			getResources().getStringArray(R.array.system_setting_help)[1],
     			getResources().getStringArray(R.array.system_setting_help)[7],
    			getResources().getStringArray(R.array.system_setting_help)[15],
     			getResources().getStringArray(R.array.system_setting_help)[11],     			
     			getResources().getStringArray(R.array.system_setting_help)[14],
     			getResources().getStringArray(R.array.system_setting_help)[12],
    			getResources().getStringArray(R.array.factory_reset_help)[0],
    			getResources().getStringArray(R.array.factory_reset_help)[1], 
    			getResources().getString(R.string.ntp_address_help)
     			};
    	 
    	
    	for (int i=0; i<listItems.length; i++) {
    		if(isBluetoothEnable){
        		HashMap<String, Object> map = new HashMap<String, Object>(); 
        		map.put("ItemContext", listItems[i]);
        		map.put("ItemHelptxt", listItemsHelp[i]);
        		map.put("ItemVal",listItemVals[i]);
        		map.put("ItemRightImg",listItemRightImgs[i]);
        		map.put("ItemLeftImg",listItemLeftImgs[i]);  
        		listName.add(map);    			
    		}else{
    			if(i!=8){
            		HashMap<String, Object> map = new HashMap<String, Object>(); 
            		map.put("ItemContext", listItems[i]);
            		map.put("ItemHelptxt", listItemsHelp[i]);
            		map.put("ItemVal",listItemVals[i]);
            		map.put("ItemRightImg",listItemRightImgs[i]);
            		map.put("ItemLeftImg",listItemLeftImgs[i]);  
            		listName.add(map);      				
    			}
    		}
    	}
         mSchedule = new SimpleAdapter(mContext, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext", "ItemHelptxt","ItemVal","ItemRightImg","ItemLeftImg"},      
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val,R.id.right_arrow_img,R.id.left_arrow_img});
        //濮樻挾鐦幏銏㈠皸閻愬妾瑰鎾存倕閸燁亣瀵橀梽瀣殬韫囨瑨澧喊宀冨箺閸椼垽鍨鹃幘浣叉晵韫囧鎷峰鎾诲灳婵傘儺鐏愰敍顫珘閿燂拷
         menuOptionsListView.setAdapter(mSchedule);
//        mListAdapter = new ListAdapter(mContext);
//        menuOptionsListView.setAdapter(mListAdapter);
//    	Log.i(TAG, "zemin setAdapter" + " " +mListAdapter);

        if (action != null && action.equals(Constant.ACTION_SECURITY_SETTINGS)) {
            menuOptionsListView.setSelection(3);
        }
    	 menuOptionsListView.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					//閼煎懓浼�挊娑欑毘闂勫洭鍨炬慨鎴欏剬椤曠儤鎷嬮埞鎾呮嫹妞规潙宓傞柍銉уΤ閻倠鍙夊厞鐠佽鎻掔棄閿涳籍鑼劦閿熻棄绻栭梽鍥灳濮ｎ厸鏁掑浣稿Ψ韫囨瑨妫濇繛鍕瘯妞规挳妾板鎾跺�闂勫洨灏楅挊娑氼暠韫囨瑩妾遍柍銉嫹0s濮樻挾鐦柍銉︹棨閿熷�浼滈懠顐ヤ粶闁炽儲鈼ら埞鐐村厞椤鍎敓钘夊床閼捐姤鎮呯捄顖濆瘶濞屻倝顣懠鍌炩攼闂勶拷				
					delayForDialog();
					Log.i(TAG,"menuOptionsListView onKeyDown");
					
					int position = menuOptionsListView.getSelectedItemPosition();

					  switch (keycode) {
					  case KeyEvent.KEYCODE_DPAD_LEFT:
					  case KeyEvent.KEYCODE_DPAD_RIGHT:				
							   listviewItemClick(position);
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
              listviewItemClick(position);
			}
        	
		});
    }
	
    public void onDestroy() {
        if (mCustomSettingView != null) {
            mCustomSettingView.onDestroy();
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        

    }
    
    public void unregesterevent(){
        mContext.unregisterReceiver(systemEventMonitorsReceiver);

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
    private void listviewItemClick(int position) {
		mPositon = position;
    if (position==passwork_lock_position) {
			  // time setup			   
/*		      mAlertDialog = new AlertDialog.Builder(mContext).create();
		      mSysSettingLay.setVisibility(View.INVISIBLE);
		      mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		      mAlertDialog.show();
		      Window window = mAlertDialog.getWindow();
		      WindowManager.LayoutParams lp = window
		              .getAttributes();
                  lp.x = 10;
		      lp.y = 150;
		        lp.height = 900;
		        lp.width = 1540; 
		        window.setAttributes(lp);
		        mTimeSettingDialogLayout = new TimeSettingDialogLayout(mContext,handler);
		      window.setContentView(mTimeSettingDialogLayout);
		      mAlertDialog.setOnDismissListener(new OnDismissListener() {
		          @Override
		          public void onDismiss(DialogInterface arg0) {
		        	  mSysSettingLay.setVisibility(View.VISIBLE);	
		          }
		      }); */
			  
			// User Password Setting  
				Intent intent = new Intent();
				intent.setClassName("com.um.umpwdlock", "com.um.umpwdlock.PwdLockMainActivity");
				mContext.startActivity(intent);			  
		   
		  }	
/*		  else if (position == 6) {
			  //韫囨瑨浼滈挊娑樼箹闁炽儲顏╃搾浣靛剬韫囧鎷峰鎾剁槵闁炽儲鍣撮閿嬬叞閿燂拷				 Builder builder =  new AlertDialog.Builder(mContext);
				 builder.setTitle(getResources().getString(R.string.choose_input_method));
	   	         LayoutInflater factory = LayoutInflater.from(mContext);
	   	         View myView = factory.inflate(R.layout.dialog_layout,null);
	   	        ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
	   	     List<Integer> dialog_item_img = new ArrayList<Integer>();
	   	     List<String> dialog_item_txt = new ArrayList<String>();
	   	     List<String> dialog_item_key = new ArrayList<String>();
	   	        
			  InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			  final List<InputMethodInfo>   mInputMethodProperties = imm.getInputMethodList();	 
			  for(int m=0;m<mInputMethodProperties.size();m++){
				    InputMethodInfo property = mInputMethodProperties.get(m);
				    String prefKey = property.getId();
			        CharSequence label = property.loadLabel(mContext.getPackageManager());
			        Log.i(TAG,"m="+m+" prefKey="+prefKey+" label="+label);
			        dialog_item_img.add(R.color.transparent);
			        dialog_item_txt.add(label+"");
			        dialog_item_key.add(prefKey);
			  }
			   Integer[] arr_dialog_item_img =  (Integer[]) dialog_item_img.toArray(new Integer[dialog_item_img.size()]);
			  final String [] arr_dialog_item_txt = (String[]) dialog_item_txt.toArray(new String[dialog_item_txt.size()]);
			  final String [] arr_dialog_item_key = (String[]) dialog_item_key.toArray(new String[dialog_item_key.size()]);
		      String curInputMethodId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		      int select_position = 0;
		      for(int n=0;n<arr_dialog_item_key.length;n++){
		    	  if(curInputMethodId.equals(arr_dialog_item_key[n])){
		    		  select_position = n;
		    	  }
		      }
		      arr_dialog_item_img[select_position]= R.drawable.net_select;
		      
			  final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 
		   		for (int i=0; i<arr_dialog_item_txt.length; i++) {
		   			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		      		map.put("ItemContext", arr_dialog_item_txt[i]); 
		      		map.put("ItemImg", arr_dialog_item_img[i]); 		
		      		listDialog.add(map);
		      	}
		   		final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
	                    new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});	    		
	        lisview.setAdapter(mSimpleAdapter);
   	        builder.setView(myView);
       	   mAlertDialog = builder.create();
       	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
       	   mAlertDialog.show();	
           delay();
       	   
           lisview.setOnItemClickListener(new OnItemClickListener() {

   			@Override
   			public void onItemClick(AdapterView<?> arg0, View arg1,
   					int selectItemSoundMode, long arg3) {
   				Log.i(TAG,"inputmethod item onclick selectItemSoundMode="+selectItemSoundMode);
			      Settings.Secure.putString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, arr_dialog_item_key[selectItemSoundMode]);
			    Log.i(TAG,"curInputMethodId = "+Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));  
   			    notifyListviewForDataChange();
			      List<Integer> item_dialog_item_img = new ArrayList<Integer>();
   			   	for(int p=0;p<mInputMethodProperties.size();p++){
   			   	  item_dialog_item_img.add(R.color.transparent);
   			   	}
   			    Integer[] arr_item_dialog_item_img =  (Integer[]) item_dialog_item_img.toArray(new Integer[item_dialog_item_img.size()]);
   			    arr_item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;	      	    
               	 listDialog.clear();
   	     		for (int i=0; i<arr_dialog_item_txt.length; i++) {
   	     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
   	        		map.put("ItemContext", arr_dialog_item_txt[i]); 
   	        		map.put("ItemImg", arr_item_dialog_item_img[i]); 		
   	        		listDialog.add(map);
   	        	}
   	     		mSimpleAdapter.notifyDataSetChanged();
   	     	  Log.i(TAG,"inputmethod item onclick end");
   			}
   		});
           
	       	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
					// TODO Auto-generated method stub
	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
	            	 //閼煎懓浼�挊娑欑毘闂勫洭鍨炬慨鎴欏剬椤曠儤鎷嬮埞鎾呮嫹妞规潙宓傞柍銉уΤ閻倠鍙夊厞鐠佽鎻掔棄閿涳籍鑼劦閿熻棄绻栭梽鍥灳濮ｎ厸鏁掑浣稿Ψ韫囨瑨妫濇繛鍕瘯妞规挳妾�0s濮樻挾鐦柍銉︹棨閿熷�浼滈懠顐ヤ粶闁炽儲鈼ら埞鐐村厞椤鍎敓鍊熷Е韫囨瑥宕查崹鍕瘶閼鳖偉灏冮悮顐ユ绾板矁骞楅幖鍌濈熅閼煎懏鑿犳０鍛板瘯妞规挳妾�
	            	 delay();

	             }
					return false;
				}
			});


		  }*/
		  else if (position == storageInfo_position) {
			  //闁煎彞绮欓崺锟筋敄閺傘倖瀚归煫鍥ㄧ懅閻︻剙顭囬悙瀵告瘶闁艰鲸绋栨禒锟�
		      mAlertDialog = new AlertDialog.Builder(mContext).create();
		      mSysSettingLay.setVisibility(View.INVISIBLE);
		      mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		      mAlertDialog.show();
		      Window window = mAlertDialog.getWindow();
		      WindowManager.LayoutParams lp = window
		              .getAttributes();
		      lp.width = getWidth();
		      lp.height = getHeight();
		      window.setAttributes(lp);
		       mStorageInfoLayout = new StorageInfoLayout(mContext,handler);
		      window.setContentView(mStorageInfoLayout);
		      mStorageInfoLayout.registerUSBroadcastReceiver();

		      mAlertDialog.setOnDismissListener(new OnDismissListener() {
		          @Override
		          public void onDismiss(DialogInterface arg0) {
		        	  mStorageInfoLayout.unregisterUSBroadcastReceiver();
		        	
		        	  mSysSettingLay.setVisibility(View.VISIBLE);	
		          }
		      }); 
		  } else if (position == resetUserMode_position) {
       	  
    	      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
    	      View myView = pwdFactory.inflate(R.layout.psw_input_dialog,null);

        	  pwdDialog = new Dialog(mContext,R.style.NobackDialog);
        	  pwdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        	  pwdDialog.setContentView(myView);
        	  pwdDialog.show();
/*        	  delayForPwdDialog();*/
        	  
    		   final Button   pwdSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    		   final Button   pwdSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
    		    edittext =(EditText)   myView.findViewById(R.id.system_back_edittext);
     		  pwdSystemOKBtn.setOnClickListener(new OnClickListener() {
     		    	
    	          @Override
    	          public void onClick(View arg0) {
                       String inputNum = edittext.getText().toString();
                       String defaultPwd =Settings.Secure.getString(mContext.getContentResolver(), Constant.UMDefaultPwd);
                         if(defaultPwd==null){
                        	 defaultPwd = Constant.UMDefaultPwdValue;
                        	 Log.i(TAG," defaultPwd==null; set defaultPwd ="+Constant.UMDefaultPwdValue);
                         }
                       String superPwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.UMSuperPwd);
                        if(superPwd==null){
                    	     superPwd = Constant.UMSuperPwdValue;
                        	 Log.i(TAG," superPwd==null; set superPwd ="+ Constant.UMSuperPwdValue);    
                        }                     
                       String restorePwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.RestorePwd); 
    	       			if(restorePwd==null){
    	       				restorePwd =  defaultPwd;
    	    			}
    	       		  Log.i(TAG,"inputNum="+inputNum+" defaultPwd="+defaultPwd+" restorePwd="+restorePwd);
    	       		  if(inputNum.equals(restorePwd)||inputNum.equals(superPwd)){
    	       			 resetUserModeFactory(pwdDialog);	       			  
    	       		  }else{
    	    				pwdDialog.dismiss();    	    					
    	    				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
    	    				  LayoutInflater factory = LayoutInflater.from(mContext);
    	    				  View myView = factory.inflate(R.layout.user_confirm,null);
    	    				  
    	    				  mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    	    				  mSystemOKBtn.setFocusable(true);
    	    				  mSystemOKBtn.requestFocus();
    	    				     
    	    				  mSystemOKBtn.setOnClickListener(new OnClickListener() {
    	    				          @Override
    	    				          public void onClick(View arg0) {
    	    				        	  mAlertDialog.dismiss();
    	    				        	  mAlertDialog = null;
    	    				        	  mPwdCountDownTimer.cancel();
    	    				        	  pwdDialog.show();
    	    				        	  edittext.setText("");
    	    				        	  edittext.requestFocus();
/*    	    				        	  delayForPwdDialog();*/
    	    				          }
    	    				      });
    	    				  mAlertDialog = builder.create();
    	    				  mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    	    				  mAlertDialog.show();	
    	    				  mAlertDialog.getWindow().setContentView(myView);
    	    				  mPwdCountDownTimer.start();
    	    			   	       			  
    	       		  }
    	          }

    			private void resetUserModeFactory(final Dialog pwdDialog) {
    				pwdDialog.dismiss();
    					  // reset
    				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
    				  LayoutInflater factory = LayoutInflater.from(mContext);
    				  View myView = factory.inflate(R.layout.user_back,null);
    				mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    				mSystemCancelBtn = (Button) myView.findViewById(R.id.user_back_cancel);
    				     mSystemCancelBtn.setFocusable(true);
    				     mSystemCancelBtn.requestFocus();
    				     mSystemOKBtn.setOnClickListener(new OnClickListener() {
        		
    				          @Override
    				          public void onClick(View arg0) {
    				        	  
    				        	  mSystemSettingInterface.setScreensaverState(0);
            		        	  mSystemSettingInterface.restoreDefault(3);
            		       	      Settings.Secure.putString(mContext.getContentResolver(), "RestorePwd", "000000");            		        	  
            		        	  
            		        	  //savePower
            		        	  Context useContext = null;
            		        	  try {
								    useContext = mContext.createPackageContext("cn.com.unionman.umtvsetting.powersave", Context.CONTEXT_IGNORE_SECURITY);
								} catch (NameNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
									SharedPreferences prefs =	useContext.getSharedPreferences("PoweritemVal",  Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS);
									Editor editor = prefs.edit();
									editor.putInt("autoShutdonw",1);
									editor.putInt("waitting",0); 
									editor.commit();  										
									UmtvManager.getInstance().getPicture().enableDynamicBL(true);
									
									//Remove data
									SystemUtils.removeDirAndFile("/data/misc/wifi");
									
          		        	      doSystemReboot();
    				              mAlertDialog.dismiss();
    				              mAlertDialog = null;
    				              mCountDownTimer.cancel();
    				          }
    				      });
    				       mSystemCancelBtn.setOnClickListener(new OnClickListener() {
    				          @Override
    				          public void onClick(View arg0) {
    				        	  mAlertDialog.dismiss();
    				        	  mAlertDialog = null;
    				        	  mCountDownTimer.cancel();
    				          }
    				      });
    				  mAlertDialog = builder.create();
    				  mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    				  mAlertDialog.show();	
    				  mAlertDialog.getWindow().setContentView(myView);
    				  mCountDownTimer.start();
    				 
    			}
    	      });
    		  pwdSystemCancleBtn.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				  pwdDialog.dismiss();
    			}
    		});
    		  edittext.addTextChangedListener(new TextWatcher() {
    				
    				@Override
    				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    					// TODO Auto-generated method stub
    					Log.i(TAG,"onTextChanged()");
/*    					 delayForPwdDialog();*/
    				}
    				
    				@Override
    				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
    						int arg3) {
    					// TODO Auto-generated method stub
    					
    				}
    				
    				@Override
    				public void afterTextChanged(Editable arg0) {
    					// TODO Auto-generated method stub
    					
    				}
    			});
    		  
/*    		  pwdDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
    				
    				@Override
    				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
    					// TODO Auto-generated method stub
    	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
    	            	 delayForPwdDialog();
    	             }
    					return false;
    				}
    			});		  */
    			       	  					      
		  }else if(position == resetFactory_position){
        	  
    	      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
    	      View myView = pwdFactory.inflate(R.layout.psw_input_dialog,null);

        	  pwdDialog = new Dialog(mContext,R.style.NobackDialog);
        	  pwdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        	  pwdDialog.setContentView(myView);
        	  pwdDialog.show();
/*        	  delayForPwdDialog();*/
        	  
    		   final Button   pwdSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    		   final Button   pwdSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
    		    edittext =(EditText)   myView.findViewById(R.id.system_back_edittext);
     		  pwdSystemOKBtn.setOnClickListener(new OnClickListener() {
     		    	
    	          @Override
    	          public void onClick(View arg0) {
                      String inputNum = edittext.getText().toString();
                      String defaultPwd =Settings.Secure.getString(mContext.getContentResolver(), Constant.UMDefaultPwd);
                        if(defaultPwd==null){
                       	 defaultPwd = Constant.UMDefaultPwdValue;
                       	 Log.i(TAG," defaultPwd==null; set defaultPwd ="+Constant.UMDefaultPwdValue);
                        }
                      String superPwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.UMSuperPwd);
                       if(superPwd==null){
                   	     superPwd = Constant.UMSuperPwdValue;
                       	 Log.i(TAG," superPwd==null; set superPwd ="+ Constant.UMSuperPwdValue);    
                       }                     
                      String restorePwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.RestorePwd); 
   	       			if(restorePwd==null){
   	       				restorePwd =  defaultPwd;
   	    			}
   	       		  Log.i(TAG,"inputNum="+inputNum+" defaultPwd="+defaultPwd+" restorePwd="+restorePwd);
   	       		  if(inputNum.equals(restorePwd)||inputNum.equals(superPwd)){
    	       			 resetFactory(pwdDialog);	       			  
    	       		  }else{
  	    				pwdDialog.dismiss();    	    					
	    				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
	    				  LayoutInflater factory = LayoutInflater.from(mContext);
	    				  View myView = factory.inflate(R.layout.user_confirm,null);
	    				  
	    				  mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
	    				  mSystemOKBtn.setFocusable(true);
	    				  mSystemOKBtn.requestFocus();
	    				     
	    				  mSystemOKBtn.setOnClickListener(new OnClickListener() {
	    				          @Override
	    				          public void onClick(View arg0) {
	    				        	  mAlertDialog.dismiss();
	    				        	  mAlertDialog = null;
	    				        	  mPwdCountDownTimer.cancel();
	    				        	  pwdDialog.show();
	    				        	  edittext.setText("");
	    				        	  edittext.requestFocus();
/*	    				        	  delayForPwdDialog();*/
	    				          }
	    				      });
	    				  mAlertDialog = builder.create();
	    				  mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    				  mAlertDialog.show();	
	    				  mAlertDialog.getWindow().setContentView(myView);
	    				  mPwdCountDownTimer.start();	    			   	       			  
	       		  }
    	          }

    			private void resetFactory(final Dialog pwdDialog) {
    				pwdDialog.dismiss();
    					  // reset
    				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
    				  LayoutInflater factory = LayoutInflater.from(mContext);
    				  View myView = factory.inflate(R.layout.user_back,null);
    				  TextView  system_back_text = (TextView) myView.findViewById(R.id.system_back_text);
    				  system_back_text.setText(getResources().getString(R.string.system_back_textview));
    				mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    				mSystemCancelBtn = (Button) myView.findViewById(R.id.user_back_cancel);
    				     mSystemCancelBtn.setFocusable(true);
    				     mSystemCancelBtn.requestFocus();
    				     mSystemOKBtn.setOnClickListener(new OnClickListener() {
        		
    				          @Override
    				          public void onClick(View arg0) {
            		        	  mSystemSettingInterface.restoreDefault();
                                  SocketClient socketClient = null;
                                  socketClient = new SocketClient();
                                  socketClient.writeMsg("reset");
                                  socketClient.readNetResponseSync();
                                  Intent mIntent = new Intent("android.intent.action.MASTER_CLEAR");
                                  mIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND); 
                                  mContext.sendBroadcast(mIntent);                                  
            		              mAlertDialog.dismiss();
            		              mAlertDialog = null;
                                  mCountDownTimer.cancel();
    				          }
    				      });
    				       mSystemCancelBtn.setOnClickListener(new OnClickListener() {
    				          @Override
    				          public void onClick(View arg0) {
    				        	  mAlertDialog.dismiss();
    				        	  mAlertDialog = null;
    				        	  mCountDownTimer.cancel();
    				          }
    				      });
    				  mAlertDialog = builder.create();
    				  mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    				  mAlertDialog.show();	
    				  mAlertDialog.getWindow().setContentView(myView);
    				  mCountDownTimer.start();
    				 
    			}
    	      });
    		  pwdSystemCancleBtn.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				  pwdDialog.dismiss();
    			}
    		});
    		  edittext.addTextChangedListener(new TextWatcher() {
    				
    				@Override
    				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    					// TODO Auto-generated method stub
    					Log.i(TAG,"onTextChanged()");
/*    					 delayForPwdDialog();*/
    				}
    				
    				@Override
    				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
    						int arg3) {
    					// TODO Auto-generated method stub
    					
    				}
    				
    				@Override
    				public void afterTextChanged(Editable arg0) {
    					// TODO Auto-generated method stub
    					
    				}
    			});
/*    		  pwdDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
    				
    				@Override
    				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
    					// TODO Auto-generated method stub
    	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
    	            	 delayForPwdDialog();
    	             }
    					return false;
    				}
    			});	*/	  
    			       	  							  
		  }else  if((listName.size()-1)==position){
				 Log.i(TAG,"====================NTP======================");
					String str_ntp = Settings.Secure.getString(mContext.getContentResolver(), "ntp_server");
					if (str_ntp == null) {
						String strSerialno = SystemProperties.get("ro.serialno", "");
						if (strSerialno.substring(40, 41).equals("0")) {
							str_ntp = "183.235.3.59";
						}else if (strSerialno.substring(40, 41).equals("1")) {
							str_ntp = "221.181.100.40";
						}
					}
			      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
			      View myView = pwdFactory.inflate(R.layout.ntp_input_dialog,null);	
			      final IpAddrEdit ntp_edittext = (IpAddrEdit) myView.findViewById(R.id.ipet_ipaddr);
			      ntp_edittext.setText(str_ntp);
			      final Dialog ntpDialog = new Dialog(mContext,R.style.NobackDialog);
		    	  ntpDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		    	  ntpDialog.setContentView(myView);
		    	  ntpDialog.show();
			      Button mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
			      mSystemOKBtn.requestFocus();	
			      Button mSystemCancelBtn = (Button) myView.findViewById(R.id.user_back_cancle);
				     mSystemOKBtn.setOnClickListener(new OnClickListener() {
			        		
				          @Override
				          public void onClick(View arg0) {				        	 
								String str_et = ntp_edittext.getText().toString().trim();
								 Log.i(TAG,"============mSystemOKBtn===============str_et="+str_et);
				
									Settings.Secure.putString(mContext.getContentResolver(),"ntp_server", str_et);
						        	ntpDialog.dismiss();
						        	notifyListviewForDataChange();
				    				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
				    				  LayoutInflater factory = LayoutInflater.from(mContext);
				    				  View myView = factory.inflate(R.layout.ntp_set_success,null);				    				  
				    				  Button mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
				    				  mSystemOKBtn.setFocusable(true);
				    				  mSystemOKBtn.requestFocus();	
				    				  final AlertDialog mAlertDialog = builder.create();
				    				  mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				    				  mAlertDialog.show();	
				    				  mAlertDialog.getWindow().setContentView(myView);
				    				     mSystemOKBtn.setOnClickListener(new OnClickListener() {
				    			        		
				    				          @Override
				    				          public void onClick(View arg0) {                              
				            		              mAlertDialog.dismiss();
				    				          }
				    				      });
						        	
	        	  
				          }
				      });
				       mSystemCancelBtn.setOnClickListener(new OnClickListener() {
				          @Override
				          public void onClick(View arg0) {
				        	  ntpDialog.dismiss();
				          }
				      });		    	  
			 }
		  
		  else if(position == 0){
				int state =   mSystemSettingInterface.getKeySoundState();
				Log.i("1000", "state--pre====="+state);
				   state--;
		            if (state < 0) {
		            	state = 1;
		            }  
		            Log.i("1000", "state--after====="+state);
		             mSystemSettingInterface.setKeySoundState(state);
		             notifyListviewForDataChange();

		  }else if(position == 1){
				int ledState =   mSystemSettingInterface.getStandbyLedState();
				 ledState--;
		            if (ledState < 0) {
		            	ledState = 1;
		            }  
		             mSystemSettingInterface.setStandbyLedState(ledState);
		             notifyListviewForDataChange();
		  }
/*		  else if(position == 2){
		             mSystemSettingInterface.setDemoModeState(mSystemSettingInterface.getDemoModeState());
		             notifyListviewForDataChange();		    
		  }*/
		  else if(position == 2){
				int usbState =   mSystemSettingInterface.getUsbState();
				usbState--;
		            if (usbState < 0) {
		            	usbState = 1;
		            }  
		             mSystemSettingInterface.setUsbState(usbState);
		             notifyListviewForDataChange();		    
		  }else if(position == 3){
				int secureState =   mSystemSettingInterface.getSecureState();
				secureState--;
		            if (secureState < 0) {
		            	secureState = 1;
		            }  
		             mSystemSettingInterface.setSecureState(secureState);
		             notifyListviewForDataChange();			    
		  }else if(position == 8){
/*				int bluetoothState =   mSystemSettingInterface.getBluetoothState();
				bluetoothState--;
		            if (bluetoothState < 0) {
		            	bluetoothState = 1;
		            }  
		             mSystemSettingInterface.setBluetoothState(bluetoothState);
		             notifyListviewForDataChange();	*/	
	             mContext.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));  
		  }else if(position == 4){
				int saverState =   mSystemSettingInterface.getScreensaverState();
				saverState--;
		            if (saverState < 0) {
		            	saverState = 1;
		            }  
		             mSystemSettingInterface.setScreensaverState(saverState);
		             notifyListviewForDataChange();	    
		  }else if(position == 5){
				int sleepOnState =   mSystemSettingInterface.getSleeponState();
				  final String [] dialog_item_val = new String[]{
						getResources().getString(R.string.off),
						getResources().getString(R.string.tenmins),
						getResources().getString(R.string.twenmins),
						getResources().getString(R.string.tirmins),
						getResources().getString(R.string.sixmins),
						getResources().getString(R.string.ninemins),
						getResources().getString(R.string.ohtmins),
						getResources().getString(R.string.ohemins),
						getResources().getString(R.string.thfmins),
				  };
		 	      int [] dialog_item_img = new int[]{
		  	   			R.color.transparent,  
		   	   			R.color.transparent, 
		   	   			R.color.transparent, 
		   	   			R.color.transparent,
		  	   			R.color.transparent, 
		   	   			R.color.transparent, 
		   	   			R.color.transparent, 
		   	   			R.color.transparent,
		  	   			R.color.transparent,   
		  	      };
		  	      dialog_item_img[sleepOnState] = R.drawable.net_select;
		  	    final AlertDialog.Builder  builder =new AlertDialog.Builder(mContext,R.style.Dialog_item);
		      builder.setTitle(listItems[position]);
		      LayoutInflater factory = LayoutInflater.from(mContext);
		      View myView = factory.inflate(R.layout.dialog_layout,null);
		  	    final ListView lisview = (ListView) myView.findViewById(R.id.setting_list);
		  	    ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
		 		for (int i=0; i<dialog_item_val.length; i++) {
		 			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		    		map.put("ItemContext", dialog_item_val[i]); 
		    		map.put("ItemImg", dialog_item_img[i]); 		
		    		listDialog.add(map);
		    	}
		  		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
				                          new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
				lisview.setAdapter(mSimpleAdapter);
				lisview.setSelection(sleepOnState);
				builder.setView(myView);
			    mAlertDialog = builder.create();
				mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				mAlertDialog.show();
				delay();
				
		 	  	 lisview.setOnItemClickListener(new OnItemClickListener() {
			 	  		
			 			@Override
			 			public void onItemClick(AdapterView<?> arg0, View arg1,
			 					int selectItemSoundMode, long arg3) {				 			    
			 			   mSystemSettingInterface.setSleeponState(selectItemSoundMode);
			 			   notifyListviewForDataChange();
			 			   	
			        	    int [] item_dialog_item_img = new int[]{
			        	    		R.color.transparent,
			        	    		R.color.transparent,
			        	    		R.color.transparent,
			        	    		R.color.transparent,
			        	    		R.color.transparent,
					   	   			R.color.transparent, 
					   	   			R.color.transparent, 
					   	   			R.color.transparent,
					  	   			R.color.transparent,   
			        	    };
			        	    item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
			 	      	    ArrayList<HashMap<String, Object>> item_listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
			 	     		for (int i=0; i<dialog_item_val.length; i++) {
			 	     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
			 	        		map.put("ItemContext", dialog_item_val[i]); 
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
		            	 //閼煎懓浼�挊娑欑毘闂勫洭鍨炬慨鎴欏剬椤曠儤鎷嬮埞鎾呮嫹妞规潙宓傞柍銉уΤ閻倠鍙夊厞鐠佽鎻掔棄閿涳籍鑼劦閿熻棄绻栭梽鍥灳濮ｎ厸鏁掑浣稿Ψ韫囨瑨妫濇繛鍕瘯妞规挳妾�0s濮樻挾鐦柍銉︹棨閿熷�浼滈懠顐ヤ粶闁炽儲鈼ら埞鐐村厞椤鍎敓鍊熷Е韫囨瑥宕查崹鍕瘶閼鳖偉灏冮悮顐ユ绾板矁骞楅幖鍌濈熅閼煎懏鑿犳０鍛板瘯妞规挳妾�
		            	 delay();

		             }
						return false;
					}
				});
		  }else if(position ==6){
				int storeMode =   (mSystemSettingInterface.getStoreMode())?1:0;
				storeMode--;
		            if (storeMode < 0) {
		            	storeMode = 1;
		            }  
		             mSystemSettingInterface.setStoreMode(storeMode);
		             notifyListviewForDataChange();
		             
		             if(storeMode == 0){  //off
		            	 if( isServiceWork(mContext,"com.um.storemodeservice.StoreModeService")){
		            		 Intent storeModeService = new Intent("UM_StoreModeService");
		            		 mContext.stopService(storeModeService) ;
		            	 }
		             }else{   //on
			             if(!  isServiceWork(mContext,"com.um.storemodeservice.StoreModeService"))	
			             {
			     	        Log.i(TAG, "start StoreModeService");
			     	        Intent storeModeService = new Intent("UM_StoreModeService");
			     	       mContext.startService(storeModeService);
			           }
		             }

		  }else if(position ==7){
				int bootSourceState =   mSystemSettingInterface.getBootSourceState();
				bootSourceState--;
		            if (bootSourceState < 0) {
		            	bootSourceState = 1;
		            }  
		             mSystemSettingInterface.setBootSourceState(bootSourceState);
		             notifyListviewForDataChange();		
		  }
		  else {
			  //閼煎懓浠归懕顐㈢箹閼规瑧顣遍懢鍊熸閸椼垼骞楅柍銉︻仼娴间究鍎敓鑺ユ倕閼句粙鍨鹃幘顖ゆ嫹     閼句粙鍨惧▎宀嬫嫹閸楊垶鍨惧熬閳灈鎳囬敓鍊熸閼句粙鍨惧▍顑挎�閵夘澁鎷烽幖鍌濆箺闁炽儲鎸撮敓锟�			  
			  InterfaceLogic logic = mLogicFactory.createLogic(
		              3, position);
		      
		      if (null == logic) {
		    	  Log.i(TAG, "can't get logic at position:"+position);
		          return;
		      }
		      List<WidgetType> list = logic.getWidgetTypeList();
		      if (logic != null && list != null) {
		          mAlertDialog = new AlertDialog.Builder(mContext).create();
		          mSysSettingLay.setVisibility(View.INVISIBLE);
		          mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		          mAlertDialog.show();
		          Window window = mAlertDialog.getWindow();
		          WindowManager.LayoutParams lp = window
		                  .getAttributes();
		          lp.x = 10;
		          lp.y = 150;
		          mCustomSettingView = new CustomSettingView(
		                  mAlertDialog, mContext, listItems[position], mLogicFactory
		                          .createLogic(3, position));
		          window.setContentView(mCustomSettingView);
		          
		          if (logic != null && list.size() == 1) {
		              lp.y = 250;
		              lp.height = 150;
		              window.setGravity(Gravity.NO_GRAVITY);
		          }
		          mAlertDialog.setOnDismissListener(new OnDismissListener() {
		            @Override
		            public void onDismiss(DialogInterface arg0) {
		            	mSysSettingLay.setVisibility(View.VISIBLE);	
		            }
		        }); 
		     }
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
			holder.item_txt_instruction.setText(listItemsHelp[position]);
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
     * 閼煎懓浠归柍銉︻仾灏炬惔鎿勬嫹閸楊垵浼滈懕顒冨瘶閼毖囧灳閹句絼鍨尨閸斿”stItem韫囨瑩婀堕柍銉︾矮椤旂櫢鎷�韫囨瑥绨柍銉︹棨閳圭偛绂戦悩鍡╁毆閺岊垽鎷�     */
   private void initListItemVals(){
	   	 List<String> dialog_item_txt = new ArrayList<String>();
   	     List<String> dialog_item_key = new ArrayList<String>();
		  InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		   List<InputMethodInfo>   mInputMethodProperties = imm.getInputMethodList();	 
		  for(int m=0;m<mInputMethodProperties.size();m++){
			    InputMethodInfo property = mInputMethodProperties.get(m);
			    String prefKey = property.getId();
		        CharSequence label = property.loadLabel(mContext.getPackageManager());				
		        dialog_item_txt.add(label+"");
		        dialog_item_key.add(prefKey);
		  }	
		  String [] arr_dialog_item_txt = (String[]) dialog_item_txt.toArray(new String[dialog_item_txt.size()]);
		  String [] arr_dialog_item_key = (String[]) dialog_item_key.toArray(new String[dialog_item_key.size()]); 			  
				
		 String curInputMethodId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
	      String  method_name = "";
	      for(int n=0;n<arr_dialog_item_key.length;n++){
	    	  if(curInputMethodId.equals(arr_dialog_item_key[n])){
	    		  method_name = arr_dialog_item_txt[n];
	    	  }
	      }
	   
	    Log.d("1000" , "mSystemSettingInterface.getKeySoundState()======="+mSystemSettingInterface.getKeySoundState());
		String str_ntp = Settings.Secure.getString(mContext.getContentResolver(), "ntp_server");
		if (str_ntp == null) {
			String strSerialno = SystemProperties.get("ro.serialno", "");
			if (strSerialno.substring(40, 41).equals("0")) {
				str_ntp = "183.235.3.59";
			}else if (strSerialno.substring(40, 41).equals("1")) {
				str_ntp = "221.181.100.40";
			}
		}
		listItemVals = new String[] { 
				getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getKeySoundState()][1]),
				getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getStandbyLedState()][1]),		
				getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getUsbState()][1]),
				getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getSecureState()][1]),
				getResources().getString(InterfaceValueMaps.screen_saver[mSystemSettingInterface.getScreensaverState()][1]),
				getResources().getString(InterfaceValueMaps.sleep_on[mSystemSettingInterface.getSleeponState()][1]),
				getResources().getString(InterfaceValueMaps.on_off[(mSystemSettingInterface.getStoreMode())?1:0][1]),
				getResources().getString(InterfaceValueMaps.on_off[mSystemSettingInterface.getBootSourceState()][1]),				
//				"",
//				method_name,
				"", 
				"", 
				"",
				"",
				"",
				str_ntp,
    	};
   }
   /**
    * 閼煎懓浼�懕娑滃箺闁炽儱鍙撻崰鈧剬瀵拌渹鎬ラ埞鐐茬М閸斿绻栭挊锟借箛娆撳灳閳垛晪鎷烽懠鍛颁还闁斥斁鎸屽熬椤﹤銈介妷鍌ゅ毆鐟佷讲鏁掑鐘冲础椤鍟撴慨鎴亷缂佺増绶烽埞鐐茬М閿熷�瀵橀柍銉︾妇閿熻棄绻栨ウ銉ㄥ⒖listview
    */
		private void notifyListviewForDataChange() {
			initListItemVals();
			listName.clear();
			for (int i=0; i<listItems.length; i++) {
				if(isBluetoothEnable){
					HashMap<String, Object> map = new HashMap<String, Object>(); 
					map.put("ItemContext", listItems[i]);
					map.put("ItemHelptxt", listItemsHelp[i]);
					map.put("ItemVal",listItemVals[i]);
					map.put("ItemRightImg",listItemRightImgs[i]);
					map.put("ItemLeftImg",listItemLeftImgs[i]);
					listName.add(map);
				}else{
					if(i!=8){
						HashMap<String, Object> map = new HashMap<String, Object>(); 
						map.put("ItemContext", listItems[i]);
						map.put("ItemHelptxt", listItemsHelp[i]);
						map.put("ItemVal",listItemVals[i]);
						map.put("ItemRightImg",listItemRightImgs[i]);
						map.put("ItemLeftImg",listItemLeftImgs[i]);
						listName.add(map);						
					}
				}

			}
			mSchedule.notifyDataSetChanged();
    }
		
		   @Override
		    public void onWindowFocusChanged(boolean hasFocus) {
		        if (hasFocus) {
		       
		        	delayForDialog();
		        } else {
		        	
		        	sysSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
		        }
		        super.onWindowFocusChanged(hasFocus);
		    }
		   /**
		    *  dismiss dialog after 30s  in SysSettingMainActivity   
		    */
		    public void delayForDialog() {
		    	Log.i(TAG,"calling delayForDialog()");
		    	sysSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
		        Message message = new Message();
		        message.what = Constant.DIALOG_DISMISS_BYTIME;
		        sysSettingLayoutHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
		    }	
		    /**
		     *  dismiss dialog after 30s  in SysSettingMainSettingLayout  
		     */
		    public void delay() {
		    	Log.i(TAG,"calling delay()");
		handler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
        Message message = new Message();
        message.what = Constant.DIALOG_DISMISS_BYTIME;
        handler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
    } 	   
		    /**
		     * dismiss Pwddialog after 30s  in SysSettingMainSettingLayout
		     */
		    public void delayForPwdDialog() {
		    	Log.i(TAG,"calling delayForPwdDialog()");
		    	handler.removeMessages(Constant.DIALOG_PWD_DISMISS_BYTIME);
		        Message message = new Message();
		        message.what = Constant.DIALOG_PWD_DISMISS_BYTIME;
		        handler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
		    }
		    private void doSystemReboot() {
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_REBOOT);
		        intent.putExtra("nowait", 1);
		        intent.putExtra("interval", 1);
		        intent.putExtra("startTime", 1);
		        intent.putExtra("window", 0);
		        mContext.sendBroadcast(intent);
		        
		    }
		    
		    private BroadcastReceiver systemEventMonitorsReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent intent) {
					if (intent.getAction().equals(RESET_MONITORS)) {
						mSystemSettingInterface.setSleeponState(0);
						notifyListviewForDataChange();
					}  
				}
			};
			
			
		    /**
		   	 * 閸掋倖鏌囬弻鎰嚋閺堝秴濮熼弰顖氭儊濮濓絽婀潻鎰攽閻ㄥ嫭鏌熷▔锟�		   	 * 
		   	 * @param mContext
		   	 * @param serviceName
		   	 *            閺勵垰瀵橀崥锟介張宥呭閻ㄥ嫮琚崥宥忕礄娓氬顩ч敍姝痚t.loonggg.testbackstage.TestService閿涳拷
		   	 * @return true娴狅綀銆冨锝呮躬鏉╂劘顢戦敍瀹朼lse娴狅綀銆冮張宥呭濞屸剝婀佸锝呮躬鏉╂劘顢�
		   	 */
		   	public boolean isServiceWork(Context mContext, String serviceName) {
		   		boolean isWork = false;
		   		ActivityManager myAM = (ActivityManager) mContext
		   				.getSystemService(Context.ACTIVITY_SERVICE);
		   		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
		   		if (myList.size() <= 0) {
		   			return false;
		   		}
		   		for (int i = 0; i < myList.size(); i++) {
		   			String mName = myList.get(i).service.getClassName().toString();
		   			if (mName.equals(serviceName)) {
		   				isWork = true;
		   				break;
		   			}
		   		}
		   		return isWork;
		   	}
		   	
		   	
		   	
}
