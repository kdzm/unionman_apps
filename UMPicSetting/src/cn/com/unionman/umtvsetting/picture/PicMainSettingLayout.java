package cn.com.unionman.umtvsetting.picture;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.unionman.umtvsetting.picture.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.interfaces.PCSettingInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.logic.factory.LogicFactory;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;
import cn.com.unionman.umtvsetting.picture.widget.CustomSettingView;

import cn.com.unionman.umtvsetting.picture.R;
import cn.com.unionman.umtvsetting.picture.interfaces.SystemSettingInterface;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.unionman.umtvsetting.picture.interfaces.SourceManagerInterface;
import android.os.SystemProperties;

import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.constant.EnumPictureClrtmp;
import com.hisilicon.android.tvapi.constant.EnumColorSystem;


public class PicMainSettingLayout extends LinearLayout implements View.OnFocusChangeListener{

	public final static int ALLPROG = 0;
	public final static int TVPROG  = 1;
	public final static int RADIOPROG = 2;
	public final static int FAVPROG = 3;
	public final static int NVODPROG = 4;
	private static String TAG = "PicMainSettingLayout";
	private Handler mHandler;
	private  Handler picMainSettingLayoutHandler;
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
    private LinearLayout menuItemLay;
    // list of name
    // adapter of menuOptionsListView

    // position of menuOptionsListView
    private int mPositon;
    private AlertDialog mAlertDialog;
    private LogicFactory mLogicFactory = null;
    // widget container
    private CustomSettingView mCustomSettingView;
    private String[] mItems;
	private Button mSystemOKBtn;
	private Button mSystemCancelBtn;
    private ArrayList<HashMap<String, Object>> listName = new ArrayList<HashMap<String, Object>>(); 
    private SimpleAdapter mSchedule;
    private float scrolledX;
    private float scrolledY;
    
    private Dialog   pwdDialog;
    
    private String[] listItems;
    private String[] listItemVals;
    private  String[] listItems_help;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
    /**
     * 閸ラ箖鏁撻弬銈嗗濡�绱�
     */
    private String[] modeVal;
    /**
     * 閼规煡鏁撻弬銈嗗
     */
    private String[] colorTempVal;
    /**
     * 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣浠嬫晸閿燂拷     */
    private String[] aspectVal;
    private String[] aspectValVGA;
    private String[] aspectValVideo;
    /**
     * 闁跨喐鏋婚幏椋庢樊闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷  闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣浠嬫晸閿燂拷     */
    private int [] aspectFlag;
    private int [] aspectFlagVGA;
    private int [] aspectFlagVideo;
    
    /**
     * dnr 閸ラ箖鏁撻弬銈嗗闁跨喐鏋婚幏锟�     */
    private String [] dnrVal;
    /**
     * memc 闁跨喎澹欑拋瑙勫闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�
     */
    private String [] memcVal;
    
    private  PictureInterface mPictureInterface;
    private  AlertDialog mResetAlertDialog;
    private   int COLORTEMP_POSITION=7;
    private   int ASPECT_POSITION=8;
    private   int NR_POSITION=9;
    //private   int MEMC_POSITION=10;
    private   int REPRODUCTIONRATE_POSITION=10;
    /**
     * 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撶憴鎺戝殩閹风兘鏁撻弬銈嗗item闁跨喐鏋婚幏椋庢窗闁跨喐鏋婚幏鐑芥晸閺傘倖瀚圭粈锟�     */
    private Boolean isRepShow =true;
    
    // ADJUSTING
    public final static int ADJUSTING = 0;
    // adjust failed
    public final static int ADJUST_FAILED = 1;
    // adjust success
    public final static int ADJUST_SUCCESS = 2;
    
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = mContext.getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            mAlertDialog.dismiss();
        }
    };
	private int mDvbMode = -1;

    /**
     * handler of finish dialog
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_ITEM_DISMISS_BYTIME:
				if(mAlertDialog.isShowing()){
					 mAlertDialog.dismiss();					
				}
				break;
			case Constant.DIALOG_PWD_DISMISS_BYTIME:
				if(pwdDialog.isShowing()){
					pwdDialog.dismiss();					
				}
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
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
    
    public void delayForPwdDialog() {
    	Log.i(TAG,"calling delayForPwdDialog()");
        finishHandle.removeMessages(Constant.DIALOG_PWD_DISMISS_BYTIME);
        Message message = new Message();
        message.what = Constant.DIALOG_PWD_DISMISS_BYTIME;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }

	public PicMainSettingLayout(Context context, Handler handler, Handler mainHandler) {
		this(context, handler, mainHandler, PicMainSettingLayout.TVPROG);
	}

	public PicMainSettingLayout(Context context, Handler handler, Handler mainHandler, int dvbMode) {
		super(context);
		mContext = context;
		mHandler = handler;
		picMainSettingLayoutHandler = mainHandler;
		mDvbMode = dvbMode;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_pic, this);
        menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
        mLogicFactory = new LogicFactory(mContext);
        mPictureInterface = new PictureInterface(mContext);        
        addData();
        
        setVisibility(View.VISIBLE);
//        int position = 0;
	}

    /**
     * The initialization of ListView
     */
/*    private void initListView() {
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
     initValueInItem();
	 listItemRightImgs = new int[]{
			R.drawable.selector_view_rigth_gred, 
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred, 
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			//R.drawable.selector_view_rigth_gred,
			R.drawable.selector_view_rigth_gred,
			R.drawable.touming,
			R.drawable.touming
			}; 
	
	 listItemLeftImgs = new int[]{
			R.drawable.selector_view_left_gred, 
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred, 
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			//R.drawable.selector_view_left_gred,
			R.drawable.selector_view_left_gred,
			R.drawable.touming,
			R.drawable.touming
			}; 
	
    	
    	Log.i(TAG, "addData" + " " +mContext);
    	 listItems = new String[]{
    			getResources().getString(R.string.picmode_string), 
    			getResources().getString(R.string.picmode_contrast_string),
    			getResources().getString(R.string.picmode_brightness_string), 
    			getResources().getString(R.string.picmode_saturation_string),
    			getResources().getString(R.string.picmode_sharpness_string),
    			getResources().getString(R.string.picmode_backlight_string),
    			getResources().getString(R.string.picmode_hue_string),
    			getResources().getString(R.string.Color_temperature),
    			getResources().getString(R.string.pic_display_mode_string),
      			getResources().getStringArray(R.array.pic_setting)[3],
      			//getResources().getStringArray(R.array.pic_setting)[2],
      			getResources().getString(R.string.reproductionrate),
    			getResources().getString(R.string.VGA_adjust),
    			getResources().getString(R.string.recover_setting)
    			};

    	 listItems_help = new String[]{
     			getResources().getString(R.string.picmode_string_help), 
     			getResources().getString(R.string.picmode_contrast_string_help),
     			getResources().getString(R.string.picmode_brightness_string_help), 
     			getResources().getString(R.string.picmode_saturation_string_help),
     			getResources().getString(R.string.picmode_sharpness_string_help),
     			getResources().getString(R.string.picmode_backlight_string_help),
     			getResources().getString(R.string.picmode_hue_string_help),
     			getResources().getString(R.string.Color_temperature_help),
     			getResources().getString(R.string.pic_display_mode_string_help),
     			getResources().getString(R.string.DNR_help),   			
     			//getResources().getString(R.string.MEMC_help),
       			getResources().getString(R.string.reproductionrate_help),
     			getResources().getString(R.string.VGA_adjust_help),
     			getResources().getString(R.string.recover_setting_help)
     			};
    	
    	for (int i=0; i<listItems.length; i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItems_help[i]);
    		map.put("ItemVal",listItemVals[i]);
    		map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
    		listName.add(map);
    	}

    	//Boolean iUserPicMode=isPicMode_User();
   
    	isVGA_Hue_RepShow(true);
    	
         mSchedule = new SimpleAdapter(mContext, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext", "ItemHelptxt","ItemVal","ItemRightImg","ItemLeftImg"},   
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val,R.id.right_arrow_img,R.id.left_arrow_img});
        //闁跨喐鏋婚幏椋庘�闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閿燂拷
        menuOptionsListView.setAdapter(mSchedule);
        
        menuOptionsListView.setOnKeyListener(new OnKeyListener() {
			
        	
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) {  
					delayForDialog();
					int position = menuOptionsListView.getSelectedItemPosition();
					//闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻弬銈嗗闁跨喓绮搁敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归幁锟�					delayForDialog();
				switch (keycode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					listviewItemClick(position);					
					break;
		        case KeyEvent.KEYCODE_DPAD_DOWN:
		        	if (menuOptionsListView.getSelectedItemPosition() ==( menuOptionsListView.getCount()-1)) {
			            menuOptionsListView.setSelection(0);		        		
		        	    }
		            break;
		        case KeyEvent.KEYCODE_DPAD_UP:
		            if (menuOptionsListView.getSelectedItemPosition() == 0) {
		                menuOptionsListView .setSelection(menuOptionsListView.getCount() - 1);
		                 }
		            break;
		    	 case KeyEvent.KEYCODE_MENU:
		    		 Log.i(TAG,"KEYCODE_MENU is clicked");
		 			Message message = new Message();
			        message.what = Constant.DIALOG_DISMISS_NOW;
			        picMainSettingLayoutHandler.sendMessage(message);			
					break; 
		    	 case KeyEvent.KEY_SOURCEENTER:	
		    		 listviewItemClick(position);		
		    		 return true;
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


	
	private void listviewItemClick(final int position) {
      if ((listName.size()-1)==position) {
    	  
	      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
	      View myView = pwdFactory.inflate(R.layout.psw_input_dialog,null);

    	  pwdDialog = new Dialog(mContext,R.style.NobackDialog);
    	  pwdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    	  pwdDialog.setContentView(myView);
    	  pwdDialog.show();
/*    	  delayForPwdDialog();*/
    	  
		   final Button   pwdSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
		   final Button   pwdSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
		   final EditText edittext =(EditText)   myView.findViewById(R.id.system_back_edittext);
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
	       			  Toast.makeText(mContext,getResources().getString(R.string.password_error) , 1).show();
	       			  edittext.setText("");
	       			  edittext.requestFocus();
	       		  }
	          }

			private void resetFactory(final Dialog pwdDialog) {
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
				        	  SystemSettingInterface.restoreDefault();
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
				  
				  	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
							// TODO Auto-generated method stub
				         if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
				               switch (keycode) {
								  case KeyEvent.KEYCODE_MENU:
							    		 mAlertDialog.dismiss();
							        	  mAlertDialog = null;
							        	  mCountDownTimer.cancel();
										break; 
								  case 	KeyEvent.KEY_SOURCEENTER:
									  if(mSystemOKBtn.isFocused()){
							        	  SystemSettingInterface.restoreDefault();
							        	  doSystemReboot();
							              mAlertDialog.dismiss();
							              mAlertDialog = null;
							              mCountDownTimer.cancel();								  
									  }
									  if(mSystemCancelBtn.isFocused()){
							        	  mAlertDialog.dismiss();
							        	  mAlertDialog = null;
							        	  mCountDownTimer.cancel();
									  }
									  return true;
							}
				         }
							return false;
						}
					});
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
/*				 delayForPwdDialog();*/
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
		  pwdDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
					// TODO Auto-generated method stub
	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
/*	            	 delayForPwdDialog();*/
		               switch (keycode) {
						  case KeyEvent.KEYCODE_MENU:
					    	  new Thread() {
					    		   public void run() {
					    		    try {
					    		     Instrumentation inst = new Instrumentation();
					    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
					    		    } catch (Exception e) {
					    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
					    		    }
					    		   }
					    		  }.start();
								break; 
						  case 	KeyEvent.KEY_SOURCEENTER:
							  if(pwdSystemOKBtn.isFocused()||edittext.isFocused()){
						    	  new Thread() {
						    		   public void run() {
						    		    try {
						    		     Instrumentation inst = new Instrumentation();
						    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
						    		    } catch (Exception e) {
						    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
						    		    }
						    		   }
						    		  }.start();						  
							  }
							  if(pwdSystemCancleBtn.isFocused()){
								  pwdDialog.dismiss();
							  }
							  return true;
					}
	             }
					return false;
				}
			});		  
			   

	  } else if(0==position){
          int mode = PictureInterface.getPictureMode();  //閸ラ箖鏁撻弬銈嗗濡�绱�
          final  int modeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.picture_mode);
	 	    int [] dialog_item_img = new int[]{
		    		R.color.transparent,
		    		R.color.transparent,
		    		R.color.transparent,
		    		R.color.transparent,
		    };
      	   dialog_item_img[modeIndex]=R.drawable.net_select;  
      	     	   
    	      AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
    	      malertdialog.setTitle(((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"));
    	      LayoutInflater factory = LayoutInflater.from(mContext);
    	      View myView = factory.inflate(R.layout.dialog_layout,null);
    	     final ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
    	     final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
   		for (int i=0; i<modeVal.length; i++) {
   			HashMap<String, Object> map =  new HashMap<String, Object>(); 
      		map.put("ItemContext", modeVal[i]); 
      		map.put("ItemImg", dialog_item_img[i]); 		
      		listDialog.add(map);
      	}
   		   final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
                    new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
    		
          lisview.setAdapter(mSimpleAdapter);
		  lisview.setSelection(modeIndex);
  	      malertdialog.setView(myView);
    	   mAlertDialog = malertdialog.create();
    	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    	   mAlertDialog.show();	
    	   delay();
    	   
          lisview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int selectItemSoundMode, long arg3) {	
			    picModeItemClick(listDialog, mSimpleAdapter,selectItemSoundMode);
			  }
			});
           
	      
      	Message msg = new Message();
      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
      	mHandler.sendMessage(msg); 
      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
		      	Message msg = new Message();
		      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
		      	mHandler.sendMessage(msg); 
			}
		});
      	
      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				// TODO Auto-generated method stub
             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
          	     delay();
            	 int position = lisview.getSelectedItemPosition();		
            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�					delay();
	               switch (keycode) {
					  case KeyEvent.KEYCODE_MENU:
				    		 mAlertDialog.dismiss();		
							break; 
					  case 	KeyEvent.KEY_SOURCEENTER:
						  picModeItemClick(listDialog, mSimpleAdapter,position);
						  return true;
				}
             }
				return false;
			}
		});
      	
      }else if(COLORTEMP_POSITION==position){
    	    int colorTemp =	PictureInterface.getColorTemp();       //閼规煡鏁撻弬銈嗗
    	    int colorTempIndex= Util.getIndexFromArray(colorTemp,InterfaceValueMaps.picture_clrtmp); 
	 	    int [] dialog_item_img = new int[]{
		    		R.color.transparent,
		    		R.color.transparent,
		    		R.color.transparent,
		    };
      	   dialog_item_img[colorTempIndex]=R.drawable.net_select;  

      	 AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
      	  malertdialog.setTitle(((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"));
 	      LayoutInflater factory = LayoutInflater.from(mContext);
	      View myView = factory.inflate(R.layout.dialog_layout,null);
	     final ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
	     final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
	   		for (int i=0; i<colorTempVal.length; i++) {
	   			HashMap<String, Object> map =  new HashMap<String, Object>(); 
	      		map.put("ItemContext", colorTempVal[i]); 
	      		map.put("ItemImg", dialog_item_img[i]); 		
	      		listDialog.add(map);
	      	}
	    		 final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
	                    new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});	  
	    	  lisview.setAdapter(mSimpleAdapter);
	   		  lisview.setSelection(colorTempIndex);
	 	      malertdialog.setView(myView);
	 	  	   mAlertDialog = malertdialog.create();
	 	  	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	 	  	   mAlertDialog.show();	
	 	  	   delay();
	 	  	   
		 	  	 lisview.setOnItemClickListener(new OnItemClickListener() {
	
		 			@Override
		 			public void onItemClick(AdapterView<?> arg0, View arg1,
		 					int selectItemSoundMode, long arg3) {	
		 				colorTempItemClick(lisview, selectItemSoundMode);
	
		 			}
		 		});   
		 	  	 
		       	Message msg = new Message();
		      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
		      	mHandler.sendMessage(msg); 
		      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
				      	Message msg = new Message();
				      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
				      	mHandler.sendMessage(msg); 
					}
				});
		      	
		      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
						// TODO Auto-generated method stub
		             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
		  	 	  	     delay();		  	 	  	     
		            	 int position = lisview.getSelectedItemPosition();	
		            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�							delay();
				               switch (keycode) {
								  case KeyEvent.KEYCODE_MENU:
							    		 mAlertDialog.dismiss();		
										break; 
								  case 	KeyEvent.KEY_SOURCEENTER:
									  colorTempItemClick(lisview, position);
									  return true;
							}	
		             }
						return false;
					}
				});    	
      }else if(ASPECT_POSITION==position){
    	  if (mDvbMode == RADIOPROG) {
			  Toast.makeText(getContext(), R.string.not_support_in_radio_mode, Toast.LENGTH_SHORT).show();
			  return;
		  }
    	  
    	  String w = SystemProperties.get("persist.sys.reslutionWidth");
    	  String h = SystemProperties.get("persist.sys.reslutionHight");
    	  
    	  Log.i("hehe", "======w:==========="+w+"===========h:====="+h);
    	  
    	    int aspect = PictureInterface.getAspect();          //display mode  4 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣浠嬫晸閿燂拷 
    	    int aspectIndex= Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
    	    int flag=0;
    	    Log.i(TAG,"ZEMIN aspectIndex="+aspectIndex);
    	    int [] dialog_item_img ;
    	    if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_MEDIA){ 
	    	    	 for(int i=0;i<aspectFlagVideo.length;i++){
	       	    	  if(aspect==aspectFlagVideo[i]){
	       	    		  flag=i;
	       	    	  }
	       	      }
	       	      Log.i(TAG,"ZEMIN flag="+flag); 
	       	      dialog_item_img = new int[]{ 
	       	    		    R.color.transparent, 
	        	   			R.color.transparent, 
	        	   			R.color.transparent, 
	        	   			R.color.transparent,
	       	   			    R.color.transparent,  
	        	   			R.color.transparent 
	       	      };
    	      }else if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_VGA){
    	    		 for(int i=0;i<aspectFlagVGA.length;i++){
   	       	    	  if(aspect==aspectFlagVGA[i]){
   	       	    		  flag=i;
   	       	    	  }
   	       	      }
   	       	      Log.i(TAG,"ZEMIN flag="+flag); 
   	       	      dialog_item_img = new int[]{ 
   	       	   			    R.color.transparent,  
   	        	   			R.color.transparent 
   	       	      };
    	      }else{
    	    	  
	    	    	 for(int i=0;i<aspectFlag.length;i++){
	       	    	  if(aspect==aspectFlag[i]){
	       	    		  flag=i;
	       	    	  }
	       	      }
	       	      Log.i(TAG,"ZEMIN flag="+flag); 
	       	      dialog_item_img = new int[]{ 
	        	   			R.color.transparent, 
	        	   			R.color.transparent, 
	        	   			R.color.transparent,
	       	   			    R.color.transparent,  
	        	   			R.color.transparent 
	       	      };
 	      
    	      }
    	     
    	      dialog_item_img[flag] = R.drawable.net_select;
    	      
         	   final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
     	      malertdialog.setTitle(((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"));
     	      LayoutInflater factory = LayoutInflater.from(mContext);
     	      View myView = factory.inflate(R.layout.dialog_layout,null); 
     	      
	      	    final ListView lisview = (ListView) myView.findViewById(R.id.setting_list);
	      	    ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
	      	    if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_VGA){
	      	    	
		      	  	for (int i=0; i<aspectFlagVGA.length; i++) {
		     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		        		map.put("ItemContext", aspectValVGA[i]); 
		        		map.put("ItemImg", dialog_item_img[i]); 		
		        		listDialog.add(map);
		        	}
	      	  	
	      	    	
	      	    }else if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_MEDIA){
	      	    	for (int i=0; i<aspectFlagVideo.length; i++) {
		     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		        		map.put("ItemContext", aspectValVideo[i]); 
		        		map.put("ItemImg", dialog_item_img[i]); 		
		        		listDialog.add(map);
		        	}	
	      	    }else{
	      	    	for (int i=0; i<aspectFlag.length; i++) {
		     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		        		map.put("ItemContext", aspectVal[i]); 
		        		map.put("ItemImg", dialog_item_img[i]); 		
		        		listDialog.add(map);
		        	}	
	      	    }
	      	    
	      		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
	      				                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	      	     lisview.setAdapter(mSimpleAdapter);
				 lisview.setSelection(flag);
		 	      malertdialog.setView(myView);
		 	  	   mAlertDialog = malertdialog.create();
		 	  	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		 	  	   mAlertDialog.show();	
		 	  	   delay();
		 	  	   
			 	  	 lisview.setOnItemClickListener(new OnItemClickListener() {
			 	  		
				 			@Override
				 			public void onItemClick(AdapterView<?> arg0, View arg1,
				 					int selectItemSoundMode, long arg3) {					 			    
				 			    displayModeItemClick(lisview, selectItemSoundMode);
			
				 			}
				 		});  
			 	  	 
			       	Message msg = new Message();
			      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
			      	mHandler.sendMessage(msg); 
			      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
					      	Message msg = new Message();
					      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
					      	mHandler.sendMessage(msg); 
						}
					});	
			      	
			      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
							// TODO Auto-generated method stub
			             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
			            	 delay();
			            	 int position = lisview.getSelectedItemPosition();
			            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�								delay();
					               switch (keycode) {
									  case KeyEvent.KEYCODE_MENU:								    		 
								    		 mAlertDialog.dismiss();		
											break; 
									  case 	KeyEvent.KEY_SOURCEENTER:
										  displayModeItemClick(lisview, position);
										  return true;
								}		
			             }
							return false;
						}
					});       	
			      	
      }else if(NR_POSITION ==position){
  	    int dnr = PictureInterface.getNR();         //DNR
  	      int [] dialog_item_img = new int[]{
  	   			R.color.transparent,  
   	   			R.color.transparent, 
   	   			R.color.transparent, 
   	   			R.color.transparent,
   	   		    R.color.transparent,
  	      };
  	      dialog_item_img[dnr] = R.drawable.net_select;

       	   final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
   	      malertdialog.setTitle(((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"));
   	      LayoutInflater factory = LayoutInflater.from(mContext);
   	      View myView = factory.inflate(R.layout.dialog_layout,null); 
   	      
	      	    final ListView lisview = (ListView) myView.findViewById(R.id.setting_list);
	      	    ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
	     		for (int i=0; i<dnrVal.length; i++) {
	     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
	        		map.put("ItemContext", dnrVal[i]); 
	        		map.put("ItemImg", dialog_item_img[i]); 		
	        		listDialog.add(map);
	        	}
	      		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
	      				                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	      	     lisview.setAdapter(mSimpleAdapter);
				 lisview.setSelection(dnr);
		 	      malertdialog.setView(myView);
		 	  	   mAlertDialog = malertdialog.create();
		 	  	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		 	  	   mAlertDialog.show();	
		 	  	   delay();
		 	  	   
			 	  	 lisview.setOnItemClickListener(new OnItemClickListener() {
			 	  		
				 			@Override
				 			public void onItemClick(AdapterView<?> arg0, View arg1,
				 					int selectItemSoundMode, long arg3) {				 			    
				 			    dNRItemClick(lisview,selectItemSoundMode);
			
				 			}
				 		});  
			 	  	 
			       	Message msg = new Message();
			      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
			      	mHandler.sendMessage(msg); 
			      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
					      	Message msg = new Message();
					      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
					      	mHandler.sendMessage(msg); 
						}
					});
			      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
							// TODO Auto-generated method stub
			             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
			            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�			            	 int position = lisview.getSelectedItemPosition();	
							 delay();
				               switch (keycode) {
								  case KeyEvent.KEYCODE_MENU:							    	
							    		 mAlertDialog.dismiss();		
										break; 
								  case 	KeyEvent.KEY_SOURCEENTER:
									  dNRItemClick(lisview, position);
									  return true;
							}								 
			             }
							return false;
						}
					});  
			      	
     /* }else if(MEMC_POSITION ==position){

    	    int memc = PictureInterface.getMEMCLevel();         //MEMC
    	      int [] dialog_item_img = new int[]{
    	   			R.color.transparent,  
     	   			R.color.transparent, 
     	   			R.color.transparent, 
     	   			R.color.transparent,
    	   			R.color.transparent,
    	      };
    	      dialog_item_img[memc] = R.drawable.net_select;
         	   final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
     	      malertdialog.setTitle(((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"));
     	      LayoutInflater factory = LayoutInflater.from(mContext);
     	      View myView = factory.inflate(R.layout.dialog_layout,null); 
     	      
  	      	    final ListView lisview = (ListView) myView.findViewById(R.id.setting_list);
  	      	    ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
  	     		for (int i=0; i<memcVal.length; i++) {
  	     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
  	        		map.put("ItemContext", memcVal[i]); 
  	        		map.put("ItemImg", dialog_item_img[i]); 		
  	        		listDialog.add(map);
  	        	}
  	      		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
  	      				                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
  	      	     lisview.setAdapter(mSimpleAdapter);
  				 lisview.setSelection(memc);
  		 	      malertdialog.setView(myView);
  		 	  	   mAlertDialog = malertdialog.create();
  		 	  	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
  		 	  	   mAlertDialog.show();	
  		 	  	   delay();
  		 	  	   
  			 	  	 lisview.setOnItemClickListener(new OnItemClickListener() {
  			 	  		
  				 			@Override
  				 			public void onItemClick(AdapterView<?> arg0, View arg1,
  				 					int selectItemSoundMode, long arg3) {    
  				 			    memcItemClick(lisview, selectItemSoundMode);
  			
  				 			}
  				 		});  
  			 	  	 
  			       	Message msg = new Message();
  			      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
  			      	mHandler.sendMessage(msg); 
  			      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
  						@Override
  						public void onDismiss(DialogInterface arg0) {
  							// TODO Auto-generated method stub
  					      	Message msg = new Message();
  					      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
  					      	mHandler.sendMessage(msg); 
  						}
  					});	
  			      	
  			      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
  						
  						@Override
  						public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
  							// TODO Auto-generated method stub
  			             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
  			            	 delay();
  			            	 int position = lisview.getSelectedItemPosition();	
  			            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�  							delay();
				               switch (keycode) {
								  case KeyEvent.KEYCODE_MENU:							    	
							    		 mAlertDialog.dismiss();		
										break; 
								  case 	KeyEvent.KEY_SOURCEENTER:
									  memcItemClick(lisview, position);
									  return true;
							}	  							
  			             }
  							return false;
  						}
  					});       	*/
        	             	  
      }else if(isRepShow && REPRODUCTIONRATE_POSITION ==position){   

			boolean state = mPictureInterface.isOverscanEnable();  //Reproductionrate 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷

			if(state == true)
				state=false;
			else
				state=true;
	            mPictureInterface.enableOverscan(state);
	            notifyListviewForDataChange();
      }
      
      else {
    	  /*int truepos=position;
      if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_VGA && position == 4){ 
    	truepos=10;
      }*/
      InterfaceLogic logic = mLogicFactory.createLogic(
              0, position);
      
      if (null == logic) {
    	  Log.i(TAG, "can't get logic at position:"+position);
          return;
      }
      List<WidgetType> list = logic.getWidgetTypeList();
      if (logic != null && list != null) {
          mAlertDialog = new AlertDialog.Builder(mContext,
                  R.style.Translucent_NoTitle).create();
          setVisibility(View.INVISIBLE);
          mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
          mAlertDialog.show();
          Window window = mAlertDialog.getWindow();
          WindowManager.LayoutParams lp = window
                  .getAttributes();
          //lp.x = 10;
          //lp.y = 150;
         /* mCustomSettingView = new CustomSettingView(
                  mAlertDialog, mContext, ((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"), mLogicFactory
                          .createLogic(0, position));*/
        
          mCustomSettingView = new CustomSettingView(
                      mAlertDialog, mContext, ((HashMap<String, String>)menuOptionsListView.getAdapter().getItem(position)).get("ItemContext"), mLogicFactory
                              .createLogic(0, position));
          
          window.setContentView(mCustomSettingView);
          
          String w = SystemProperties.get("ro.sf.lcd_density");
         
          //lp.width = 1000;
          
          if(w.equals("240")){
        	  lp.width = 1000;
          }else{
        	  lp.width = 700;
          }
          
          window.setAttributes(lp);
          if (logic != null && list.size() == 1) {
       	  
        	  lp.y = 250;
        	  
              if(w.equals("240")){
            	  lp.height = 150;
              }else{
            	  lp.height = 100;
              }
              window.setGravity(Gravity.NO_GRAVITY);
          }         
          mAlertDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {    
	            notifyListviewForDataChange();       	
             	Message msg = new Message();
             	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
             	mHandler.sendMessage(msg);   
           }
        });
	      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
				
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				// TODO Auto-generated method stub
	       if(arg2.getAction() == KeyEvent.ACTION_DOWN){
		               switch (keycode) {
						  case KeyEvent.KEYCODE_MENU:							    	
					    		 mAlertDialog.dismiss();		
								break; 
					}	
	       }
				return false;
			}
		}); 
      }
	}
	}
    public void onDestroy() {
        if (mCustomSettingView != null) {
            mCustomSettingView.onDestroy();
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if(pwdDialog != null && pwdDialog.isShowing() ){
        	pwdDialog.dismiss();
        }
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onFocusChange(View arg0, boolean arg1) {
    	// TODO Auto-generated method stub
    	
    }
    /**
     * 闁跨喐鏋婚幏宄邦瀶闁跨喐鏋婚幏绋琲stItem闁跨喎褰ㄧ喊澶嬪閸婏拷
     */
   private void initListItemVals(){
       int mode = PictureInterface.getPictureMode();  //閸ラ箖鏁撻弬銈嗗濡�绱�
       int modeIndex= Util.getIndexFromArray(mode,
               InterfaceValueMaps.picture_mode);
      String modeStr="";
      modeStr = getResources().getString(InterfaceValueMaps.picture_mode[modeIndex][1]);
      Log.i(TAG,"mode="+mode+" modeIndex="+modeIndex+" modeStrmodeStr="+modeStr);     

	 int contrast =	PictureInterface.getContrast();        //闁跨喓娈曞В鏃囶啇閹凤拷
    Log.i(TAG,"contrast="+contrast);
    
   int brightness = PictureInterface.getBrightness();      //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�
   Log.i(TAG,"brightness="+brightness);
   
   int saturation = PictureInterface.getSaturation();      //闁跨喐鏋婚幏鐑芥晸闁颁絻顔愰幏锟�   Log.i(TAG,"saturation="+saturation);
   
   int sharpness =	PictureInterface.getSharpness();       //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐕傛嫹
   Log.i(TAG,"sharpness="+sharpness);
   
   int hue = PictureInterface.getHue();             //閼规煡鏁撻弬銈嗗
   Log.i(TAG,"hue="+hue);
   	
   int colorTemp =	PictureInterface.getColorTemp();       //閼规煡鏁撻弬銈嗗
   Log.i(TAG,"colorTemp="+colorTemp);
   int colorTempIndex= Util.getIndexFromArray(colorTemp,
           InterfaceValueMaps.picture_clrtmp);
   String colorTempStr="";
   colorTempStr = getResources().getString(InterfaceValueMaps.picture_clrtmp[colorTempIndex][1]);
	
	   int backlight = 	PictureInterface.getBacklight();       //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�
  Log.i(TAG,"backlight="+backlight);
   	
	/*	  Log.i(TAG," EnumPictureAspect.ASPECT_AUTO="+ EnumPictureAspect.ASPECT_AUTO);
		  Log.i(TAG," EnumPictureAspect.ASPECT_16_9="+ EnumPictureAspect.ASPECT_16_9);
		  Log.i(TAG," EnumPictureAspect.ASPECT_4_3="+ EnumPictureAspect.ASPECT_4_3);
		  Log.i(TAG," EnumPictureAspect.ASPECT_SUBTITLE="+ EnumPictureAspect.ASPECT_SUBTITLE);
		  Log.i(TAG," EnumPictureAspect.ASPECT_CINEMA="+ EnumPictureAspect.ASPECT_CINEMA);
		  Log.i(TAG," EnumPictureAspect.ASPECT_ZOOM="+ EnumPictureAspect.ASPECT_ZOOM);
		  Log.i(TAG," EnumPictureAspect.ASPECT_ZOOM1="+ EnumPictureAspect.ASPECT_ZOOM1);
		  Log.i(TAG," EnumPictureAspect.ASPECT_ZOOM2="+ EnumPictureAspect.ASPECT_ZOOM2);
		  Log.i(TAG," EnumPictureAspect.ASPECT_PANORAMA="+ EnumPictureAspect.ASPECT_PANORAMA);*/
   int aspect = PictureInterface.getAspect();          //display mode  4 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣浠嬫晸閿燂拷 
   int aspectIndex= Util.getIndexFromArray(aspect,
           InterfaceValueMaps.picture_aspect); 
   Log.i(TAG,"aspect="+aspect);
   //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撴鎭掑劵閹风兘鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥懄椤掑﹥瀚圭化顖炴晸閿熶粙鏁撻弬銈嗗闁跨喕鍓兼潏鐐2闁鏁撶粵瀣拷闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归崣鏍э拷娑撴椽鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗閸婇棿璐烝SPECT_16_9
   if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_MEDIA){
	   if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2)){
		   PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
		   aspect = PictureInterface.getAspect(); 
		   aspectIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
		   Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2 or ASPECT_AUTO;set value = ASPECT_16_9; aspect="+aspect);
	   } 
   }else{

	   if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2)||(aspect==EnumPictureAspect.ASPECT_AUTO)){
		   PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
		   aspect = PictureInterface.getAspect(); 
		   aspectIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
		   Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2 or ASPECT_AUTO;set value = ASPECT_16_9; aspect="+aspect);
	   } 
   
   }
 
   String aspectStr = "";
   aspectStr = getResources().getString(InterfaceValueMaps.picture_aspect[aspectIndex][1]);
   Log.i(TAG,"picture_aspect["+aspectIndex+"][1]="+InterfaceValueMaps.picture_aspect[aspectIndex][1]);
   Log.i(TAG,"aspectStr="+aspectStr);
	
	    String vagAdjust = "";
   String nr =  getResources().getString(InterfaceValueMaps.NR[PictureInterface.getNR()][1]);
   Log.i(TAG," PictureInterface.getNR()="+PictureInterface.getNR());
   
   int overscanid =1;
   if(true == mPictureInterface.isOverscanEnable())
   	overscanid = 0;
   //String memc = getResources().getString(InterfaceValueMaps.MEMC_level[PictureInterface.getMEMCLevel()][1]);     
   String reproductionrate = getResources().getString(InterfaceValueMaps.reproductionrate[overscanid][1]);
   String restore = "";
	
	
		 listItemVals = new String[]{
			modeStr, 
			contrast+"",
			brightness+"", 
			saturation+"",
			sharpness+"",
			backlight+"",
			(hue-50)+"",
			colorTempStr,
			aspectStr,
			nr,   
			//memc, 
			reproductionrate, 
			vagAdjust,
			restore
			}; 
		   
   }
   /**
    * 闁跨喐鏋婚幏宄邦瀶闁跨喐鏋婚幏绌昳stview闁跨喐鏋婚幏鐑芥晸閸欘偉顔愰幏鐑芥晸閺傘倖瀚归柅澶愭晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗
    */
   private void initValueInItem(){
       modeVal = new String []{
    		getResources().getString( R.string.picmode_standard_string), 
    		getResources().getString( R.string.picmode_vivid_string),  
    		getResources().getString( R.string.picmode_softness_string),  
    		getResources().getString( R.string.picmode_user_string)
       };
       colorTempVal = new String[]{
       		getResources().getString(R.string.clrtmp_nature_string) ,
       		getResources().getString( R.string.clrtmp_cool_string),
       		getResources().getString( R.string.clrtmp_warm_string) 
       };
       aspectVal = new String[]{
//   				getResources().getString(R.string.aspect_auto_string),
   				getResources().getString(R.string.aspect_16_9_string),
   				getResources().getString(R.string.aspect_4_3_string),
   				getResources().getString(R.string.aspect_subtitle_string),
   				getResources().getString(R.string.aspect_cinema_string),
//   				getResources().getString(R.string.aspect_zoom_string),
//   				getResources().getString(R.string.aspect_zoom1_string),
//   				getResources().getString(R.string.aspect_zoom2_string),
   				getResources().getString(R.string.aspect_panorama_string)
       };
       aspectValVGA = new String[]{
				getResources().getString(R.string.aspect_16_9_string),
				getResources().getString(R.string.aspect_4_3_string)
  };
       aspectValVideo = new String[]{
				getResources().getString(R.string.aspect_auto_string),
				getResources().getString(R.string.aspect_16_9_string),
				getResources().getString(R.string.aspect_4_3_string),
				getResources().getString(R.string.aspect_subtitle_string),
				getResources().getString(R.string.aspect_cinema_string),
				getResources().getString(R.string.aspect_panorama_string)
  };
       aspectFlag = new int[]{
//    		   EnumPictureAspect.ASPECT_AUTO,	       	//	4,
    		   EnumPictureAspect.ASPECT_16_9,			       	//	0,
    		   EnumPictureAspect.ASPECT_4_3,    	//	1,
    		   EnumPictureAspect.ASPECT_SUBTITLE,     	//	5,
    		   EnumPictureAspect.ASPECT_CINEMA,    	//	7,
//    		   EnumPictureAspect.ASPECT_ZOOM,    	//	9,
//    		   EnumPictureAspect.ASPECT_ZOOM1,    	//	10,
//    		   EnumPictureAspect.ASPECT_ZOOM2,    	//	11,
    		   EnumPictureAspect.ASPECT_PANORAMA    	//	8
       };
       aspectFlagVGA = new int[]{
//    		   EnumPictureAspect.ASPECT_AUTO,	       	//	4,
    		   EnumPictureAspect.ASPECT_16_9,			       	//	0,
    		   EnumPictureAspect.ASPECT_4_3,    	//	1,
 //   		   EnumPictureAspect.ASPECT_SUBTITLE,     	//	5,
 //   		   EnumPictureAspect.ASPECT_CINEMA,    	//	7,
//    		   EnumPictureAspect.ASPECT_ZOOM,    	//	9,
//    		   EnumPictureAspect.ASPECT_ZOOM1,    	//	10,
//    		   EnumPictureAspect.ASPECT_ZOOM2,    	//	11,
 //   		   EnumPictureAspect.ASPECT_PANORAMA    	//	8
       };
       
       aspectFlagVideo = new int[]{
    		   EnumPictureAspect.ASPECT_AUTO,	       	//	4,
    		   EnumPictureAspect.ASPECT_16_9,			       	//	0,
    		   EnumPictureAspect.ASPECT_4_3,    	//	1,
    		   EnumPictureAspect.ASPECT_SUBTITLE,     	//	5,
    		   EnumPictureAspect.ASPECT_CINEMA,    	//	7,
    		   EnumPictureAspect.ASPECT_PANORAMA    	//	8
       };
	    dnrVal  = new String[]{
					getResources().getString(R.string.off),
					getResources().getString(R.string.low_string),
					getResources().getString(R.string.middle_string),
					getResources().getString(R.string.high_string),
					getResources().getString(R.string.auto),
	      };
	   /* memcVal  = new String[]{
					getResources().getString(R.string.memc_off),
					getResources().getString(R.string.memc_low),
					getResources().getString(R.string.memc_mid),
					getResources().getString(R.string.memc_high),
					getResources().getString(R.string.memc_auto),
	      };	 */   
   }
    /**
     * 闁跨喐鏋婚幏鐤涪闁跨喐鏋婚幏鐑芥晸閹存帒瀵查弮鍫曟晸閺傘倖瀚归崚鐑芥晸閺傘倖瀚筶istview
     */
		private void notifyListviewForDataChange() {
			initListItemVals();
			listName.clear();
			for (int i=0; i<listItems.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>(); 
				map.put("ItemContext", listItems[i]);
				map.put("ItemHelptxt", listItems_help[i]);
				map.put("ItemVal",listItemVals[i]);
				map.put("ItemRightImg",listItemRightImgs[i]);
				map.put("ItemLeftImg",listItemLeftImgs[i]);
				listName.add(map);
			}
			//Boolean iUserPicMode=isPicMode_User();
			isVGA_Hue_RepShow(true);
			mSchedule.notifyDataSetChanged();
     }
		/**
		 * 闁跨喎褰ㄧ拋瑙勫VGA,閼规煡鏁撻弬銈嗗,闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷闁跨喕顬戦崙銈嗗闁跨喐鏋婚幏椋庛仛
		 */
		private void isVGA_Hue_RepShow( Boolean isUserPicMode) {
			String w = SystemProperties.get("persist.sys.reslutionWidth");
	    	String h = SystemProperties.get("persist.sys.reslutionHight");
			if (SourceManagerInterface.getSelectSourceId() != EnumSourceIndex.SOURCE_VGA){ //闁跨喎褰ㄧ拋瑙勫VGA闁跨喕顬戦崙銈嗗闁跨喐鏋婚幏椋庛仛
			    	listName.remove(listName.size()-2);
			 	}			
			if (!isHueEnable()){ //闁跨喎褰ㄧ拋瑙勫閼规煡鏁撻弬銈嗗闁跨喕顬戦崙銈嗗闁跨喐鏋婚幏椋庛仛
	               /* listName.remove(6);

	                COLORTEMP_POSITION=6;
	                ASPECT_POSITION=7;
	                NR_POSITION=8;
	                MEMC_POSITION=9;
	                REPRODUCTIONRATE_POSITION=10;*/
	                
	                
	            	if(isUserPicMode){
	            		
	            		if((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160"))){
	            			listName.remove(6);
	            			listName.remove(7);
			                COLORTEMP_POSITION=6;
			                ASPECT_POSITION=20;
			                NR_POSITION=7;
			                //MEMC_POSITION=8;
			                REPRODUCTIONRATE_POSITION=8;
	            		}else{
	            			listName.remove(6);

	    	                COLORTEMP_POSITION=6;
	    	                ASPECT_POSITION=7;
	    	                NR_POSITION=8;
	    	                //MEMC_POSITION=9;
	    	                REPRODUCTIONRATE_POSITION=9;
	            		}
					
					}else{
						//COLORTEMP_POSITION=6;
						listName.remove(1);
		                ASPECT_POSITION=1;
		                NR_POSITION=2;
		                //MEMC_POSITION=3;
		                REPRODUCTIONRATE_POSITION=3;
					}
			    }else{
			    	if((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160"))){
            			listName.remove(8);
		                ASPECT_POSITION=20;
		                NR_POSITION=8;
		                //MEMC_POSITION=9;
		                REPRODUCTIONRATE_POSITION=9;
            		}
			    }
			
		
	    	
	    	
	    		
			if ((SourceManagerInterface.getSelectSourceId() != 9 && SourceManagerInterface.getSelectSourceId() != 10 && SourceManagerInterface.getSelectSourceId() != 11) || ((w.equals("4096")||(w.equals("3840")))&&(h.equals("2160")))){ //闁跨喎褰ㄧ拋瑙勫闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撶憴鎺戝殩閹风兘鏁撻弬銈嗗缁�拷
				if (SourceManagerInterface.getSelectSourceId() != EnumSourceIndex.SOURCE_VGA){
		        	listName.remove(listName.size()-2);
		        	isRepShow=false; 
				}else{
					listName.remove(listName.size()-3);
					isRepShow=false; 
				}
		 	}
			
		
		}
		
		   /**
	     * is Hue enable or not
	     *
	     * @return
	     */
	    private boolean isHueEnable() {
	        int mCurSourceIdx = SourceManagerInterface.getSelectSourceId();
	        Log.i(TAG,"mCurSourceIdx="+mCurSourceIdx);
	        int colorsystem = 0;
	        if(mCurSourceIdx == EnumSourceIndex.SOURCE_ATV){
	            colorsystem = ATVChannelInterface.getCurrentColorSystem();
	            Log.d(TAG,"atv current program colorsystem = "+colorsystem);
	            if(colorsystem == EnumColorSystem.CLRSYS_NTSC || colorsystem == EnumColorSystem.CLRSYS_NTSC443
	               ||colorsystem == EnumColorSystem.CLRSYS_NTSC_50){

	                 return true;
	            }
	            else{
	                return false;
	            }

	        }
	        else if(mCurSourceIdx == EnumSourceIndex.SOURCE_CVBS1 || mCurSourceIdx == EnumSourceIndex.SOURCE_CVBS2){

	            colorsystem = PictureInterface.getColorSystem();

	            Log.d(TAG,"av current  colorsystem = "+colorsystem);

	            if(colorsystem == EnumColorSystem.CLRSYS_NTSC || colorsystem == EnumColorSystem.CLRSYS_NTSC443
	               ||colorsystem == EnumColorSystem.CLRSYS_NTSC_50){

	                 return true;
	            }
	            else{
	                return false;
	            }

	        }
	        else{

	            return false;
	        }

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
	    
	    @Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	    	Log.i(TAG,"onWindowFocusChanged() hasFocus="+hasFocus);
	        if (hasFocus) {
	        	delayForDialog();
	        } else {
	        	picMainSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        }
	        super.onWindowFocusChanged(hasFocus);
	    }
	    public  void delayForDialog() {
	    	Log.i(TAG,"delayForDialog() is calling");
	    	picMainSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_DISMISS_BYTIME;
	        picMainSettingLayoutHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
	    }  
	    
	    
		private void picModeItemClick(
				final ArrayList<HashMap<String, Object>> listDialog,
				final SimpleAdapter mSimpleAdapter, int selectItemSoundMode) {
			if(selectItemSoundMode==1){
				PictureInterface.setPictureMode(EnumPictureMode.PICTURE_VIVID);  //EnumPictureMode.PICTURE_VIVID =4
			}else{
				PictureInterface.setPictureMode(selectItemSoundMode);
			}
		    notifyListviewForDataChange();
		   	
   	    int [] item_dialog_item_img = new int[]{
   	    		R.color.transparent,
   	    		R.color.transparent,
   	    		R.color.transparent,
	    		R.color.transparent
   	    };
   	    item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;	      	    
        	 listDialog.clear();
     		for (int i=0; i<modeVal.length; i++) {
     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
        		map.put("ItemContext", modeVal[i]); 
        		map.put("ItemImg", item_dialog_item_img[i]); 		
        		listDialog.add(map);
        	}
     		mSimpleAdapter.notifyDataSetChanged();
		}



		private void colorTempItemClick(final ListView lisview,
				int selectItemSoundMode) {
			int mContrast = 0 ;
			int mSaturation = 0;
			int mSharpness = 0;
			int mBrightness = 0;
			int mHue = 0;
			int mBackLight = 0;
			mContrast = PictureInterface.getContrast();
			mSaturation = PictureInterface.getSaturation();
			mBrightness = PictureInterface.getBrightness();
			mBackLight = PictureInterface.getBacklight();
			if (isHueEnable()) {
			    mHue = PictureInterface.getHue();
			}
			mSharpness = PictureInterface.getSharpness();
			
			PictureInterface
			        .setPictureMode(InterfaceValueMaps.picture_mode[3][0]);
			
			PictureInterface.setColorTemp(selectItemSoundMode);
			
			PictureInterface.setBrightness(mBrightness);
			PictureInterface.setContrast(mContrast);
			PictureInterface.setSaturation(mSaturation);
			PictureInterface.setSharpness(mSharpness);
			PictureInterface.setBacklight(mBackLight);
			if (isHueEnable()) {
			    PictureInterface.setHue(mHue);
			}
							   
			notifyListviewForDataChange();
			
			int [] item_dialog_item_img = new int[]{
					R.color.transparent,
					R.color.transparent,
					R.color.transparent,
			};
			item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
			ArrayList<HashMap<String, Object>> item_listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
			for (int i=0; i<colorTempVal.length; i++) {
				HashMap<String, Object> map =  new HashMap<String, Object>(); 
				map.put("ItemContext", colorTempVal[i]); 
				map.put("ItemImg", item_dialog_item_img[i]); 		
				item_listDialog.add(map);
			}
			
			 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
			           new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	   lisview.setAdapter(itemSimpleAdapter);
	  lisview.setSelection(selectItemSoundMode);
		}



		private void displayModeItemClick(final ListView lisview, int selectItemSoundMode) {
			int [] item_dialog_item_img;
			ArrayList<HashMap<String, Object>> item_listDialog = new ArrayList<HashMap<String, Object>>(); 
			Log.d(TAG,"===yiyonghui==selectItemSoundMode="+selectItemSoundMode );
			if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_MEDIA){
				item_listDialog.clear();
				PictureInterface.setAspect(aspectFlagVideo[selectItemSoundMode],false);
				   notifyListviewForDataChange();
						
						item_dialog_item_img = new int[]{
								R.color.transparent,
								R.color.transparent,
								R.color.transparent,
								R.color.transparent,
								R.color.transparent,
								R.color.transparent
						};
						item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
						    	           		
						for (int i=0; i<aspectValVideo.length; i++) {
							HashMap<String, Object> map =  new HashMap<String, Object>(); 
							map.put("ItemContext", aspectValVideo[i]); 
							map.put("ItemImg", item_dialog_item_img[i]); 		
							item_listDialog.add(map);
						}
			}else if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_VGA){
				PictureInterface.setAspect(aspectFlagVGA[selectItemSoundMode],false);
				   notifyListviewForDataChange();
				   item_listDialog.clear();
						item_dialog_item_img = new int[]{
								R.color.transparent,
								R.color.transparent
						};
						item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
					
						for (int i=0; i<aspectValVGA.length; i++) {
							HashMap<String, Object> map =  new HashMap<String, Object>(); 
							map.put("ItemContext", aspectValVGA[i]); 
							map.put("ItemImg", item_dialog_item_img[i]); 		
							item_listDialog.add(map);
						}
			}else{

				item_listDialog.clear();
				Log.d(TAG,"===yiyonghui==aspectFlag="+aspectFlag[selectItemSoundMode] );
				PictureInterface.setAspect(aspectFlag[selectItemSoundMode],false);
				   notifyListviewForDataChange();
						
						item_dialog_item_img = new int[]{
								R.color.transparent,
								R.color.transparent,
								R.color.transparent,
								R.color.transparent,
								R.color.transparent
						};
						item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
						    	           		
						for (int i=0; i<aspectVal.length; i++) {
							HashMap<String, Object> map =  new HashMap<String, Object>(); 
							map.put("ItemContext", aspectVal[i]); 
							map.put("ItemImg", item_dialog_item_img[i]); 		
							item_listDialog.add(map);
						}
			
				
			}
	
			
			 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
			           new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	   lisview.setAdapter(itemSimpleAdapter);
 	  lisview.setSelection(selectItemSoundMode);
		}

        /**
         * 閸ラ箖鏁撻弬銈嗗闁跨喐鏋婚幏锟絠tem onclick
         * @param lisview
         * @param selectItemSoundMode
         */
		private void dNRItemClick(final ListView lisview, int selectItemSoundMode) {
			PictureInterface.setNR(selectItemSoundMode);
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
			for (int i=0; i<dnrVal.length; i++) {
				HashMap<String, Object> map =  new HashMap<String, Object>(); 
				map.put("ItemContext", dnrVal[i]); 
				map.put("ItemImg", item_dialog_item_img[i]); 		
				item_listDialog.add(map);
			}
			
			 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
			           new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	   lisview.setAdapter(itemSimpleAdapter);
	  lisview.setSelection(selectItemSoundMode);
		}
         /**
          *  闁跨喎澹欑拋瑙勫闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�item onclick
          * @param lisview
          * @param selectItemSoundMode
          */
		private void memcItemClick(final ListView lisview,
				int selectItemSoundMode) {
			PictureInterface.setMEMCLevel(selectItemSoundMode);
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
			for (int i=0; i<memcVal.length; i++) {
				HashMap<String, Object> map =  new HashMap<String, Object>(); 
				map.put("ItemContext", memcVal[i]); 
				map.put("ItemImg", item_dialog_item_img[i]); 		
				item_listDialog.add(map);
			}
			
			 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
			           new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
			   lisview.setAdapter(itemSimpleAdapter);
			  lisview.setSelection(selectItemSoundMode);
		}    
		
		//is usersetting picmode
		private Boolean isPicMode_User() {
			int mode = PictureInterface.getPictureMode();  
		    int modeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.picture_mode);
		    
		    if(modeIndex!=3){
		        listName.remove(1);
		    	listName.remove(1);
		    	listName.remove(1);
		    	listName.remove(1);
		    	listName.remove(1);
		    	listName.remove(2);
	

		    	return false;
		    }else{

		
		    	return true;
		    }
			
			
		}
}
