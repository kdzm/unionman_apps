package cn.com.unionman.umtvsetting.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.com.unionman.umtvsetting.sound.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.logic.factory.LogicFactory;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.util.Constant;
import cn.com.unionman.umtvsetting.sound.util.Util;
import cn.com.unionman.umtvsetting.sound.util.UtilDvbPlayerCtrl;
import cn.com.unionman.umtvsetting.sound.widget.CustomSettingView;
import cn.com.unionman.umtvsetting.sound.R;
import cn.com.unionman.umtvsetting.sound.interfaces.SystemSettingInterface;
import android.app.KeyguardManager;
import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.media.AudioManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.hisilicon.android.tvapi.CusAudio;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSoundAdvEftParam;
import com.hisilicon.android.tvapi.constant.EnumSoundEftParam;
import com.hisilicon.android.tvapi.constant.EnumSoundfield;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundSpdif;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;
import android.os.SystemProperties;

public class SoundMainSettingLayout extends LinearLayout implements View.OnFocusChangeListener{
	
	private static String TAG = "SoundMainSettingLayout";
	private Handler mHandler;
	private  Handler soundMainSettingLayoutHandler;
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
    
    private Dialog   pwdDialog;
    private  Dialog   surroundDialog;
    
    // listView of menu options
    private ListView menuOptionsListView;
    // layout of up arrow
    private LinearLayout upArrowLayout;
    // layout of down arrow
    private LinearLayout downArrowLayout;
    // layout of menu item
    private LinearLayout menuItemLay;
    private LinearLayout mDialogLayout;
    // list of name
    // adapter of menuOptionsListView
   // private SettingListAdapter mSettingListAdapter;
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
    private String[] listItemVals;
    private String[] listItems;
    private String[] listItemsHelp;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
    private HashMap<String, Object> map;
    private ListAdapter mListAdapter;
    AudioManager mAudioManager;
    WindowManager mWindowManager;
    private int mCurrentStreamType;
    
    private	 ArrayList<HashMap<String, Object>> surroundListName;
    private	 String[] surroundListItems;
    private  String[] surroundListItemsHelp;
    private  String[]  surroundListItemVals;
    private	int[] surroundListItemRightImgs;
    private	int[] surroundListItemLeftImgs;  
    private  ListView surroundList;
    private CheckBox sw_SurroundOnOff;
    private TextView tv_surround_islistshow;
    private SimpleAdapter  surroundAdapter;
    
    private boolean sws_status;
    private boolean surround_status;
    private boolean dialog_status;
    private boolean treble_status;
    private boolean bass_status;
    private boolean loundness;
    private boolean handMode;
    
    public static final int FLAG_MUTE_VOLUME = 1 << 7;
    public static final int FLAG_SHOW_UI = 1 << 0;
    public static final int ADJUST_SAME = 0;
    
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = mContext.getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            mAlertDialog.dismiss();
        }
    };
    /**
     * 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚瑰Ο鈥崇础
     */
    private  String[] sound_mode;
    /**
     * 闁跨喐鏋婚幏椋庢樊闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷 闁跨喐鏋婚幏鐑芥晸閺傘倖瀚瑰Ο鈥崇础
     */
    private int[] sound_mode_flag;
    private  int selectItemSoundMode =0;
     /**
      * spdif
      */
    private  String[] spdifVal;
    private boolean mIsTrackModeShow = false;
    private boolean mIsTrackSelectShow = false;
    private String[] mTracksList;
    
	public SoundMainSettingLayout(Context context,Handler handler,Handler mainHandler ) {
		super(context);
		mContext = context;
		mHandler = handler;
		soundMainSettingLayoutHandler = mainHandler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_sound, this);
        menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
        mDialogLayout = (LinearLayout) findViewById(R.id.sound_dialog_ly);
        
        mLogicFactory = new LogicFactory(mContext);
        
        int curSourceId =  UmtvManager.getInstance().getSourceManager().getCurSourceId(0);
        
        if (curSourceId == EnumSourceIndex.SOURCE_DVBC
				|| curSourceId == EnumSourceIndex.SOURCE_DTMB) {
        	mIsTrackSelectShow = true;
        }
        
        if (curSourceId != EnumSourceIndex.SOURCE_ATV) {
        	mIsTrackModeShow = true;
        }
        
        addData();

		unMuteIfMute();
        
	}

    /**
     * The main menu to add a menu item icon and text
     */
    private void addData() {	
        initListItemVals();
        initValueInItem();  
    	
    	for (int i=0; i<listItems.length; i++) {
    	    map = new HashMap<String, Object>(); 
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
        
        //闁跨喐鏋婚幏椋庘�闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閿燂拷
          menuOptionsListView.setAdapter(mSchedule);
//         mListAdapter = new ListAdapter(mContext);
//         menuOptionsListView.setAdapter(mListAdapter);
         menuOptionsListView.setOnKeyListener(new OnKeyListener() {			
			@Override
				public boolean onKey(View arg0, int keycode, KeyEvent event) {
			        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
			        	int position = menuOptionsListView.getSelectedItemPosition();
						//闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻弬銈嗗闁跨喓绮搁敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归幁锟�						
			        delayForDialog();

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
			 			Message message = new Message();
				        message.what = Constant.DIALOG_DISMISS_NOW;
				        soundMainSettingLayoutHandler.sendMessage(message);			
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
	
    public void onDestroy() {
        if (mCustomSettingView != null) {
            mCustomSettingView.onDestroy();
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if (pwdDialog != null && pwdDialog.isShowing()) {
        	pwdDialog.dismiss();
        }
        if (surroundDialog != null && surroundDialog.isShowing()) {
        	surroundDialog.dismiss();
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
    private void listviewItemClick(int position) {
    	boolean isRestoreItem = false;
    	boolean isTrackModeItem = false;
    	boolean isTrackSelItem = false;
    	boolean isVoiceSpeaker = false;    	
    	int curSourceId =  UmtvManager.getInstance().getSourceManager().getCurSourceId(0);
    	
    	if (position == 6) {
    		if(curSourceId== EnumSourceIndex.SOURCE_ATV){
    			isVoiceSpeaker= true;
    		}else{
    			isTrackModeItem= true;
    		}
    	} else if (position == 7) {
            if (curSourceId == EnumSourceIndex.SOURCE_DVBC|| curSourceId == EnumSourceIndex.SOURCE_DTMB){
            	isTrackSelItem= true;
            }else if(curSourceId== EnumSourceIndex.SOURCE_ATV){
            	isRestoreItem = true;
            }else{
            	isVoiceSpeaker = true;
            }
    	}else if(position == 8){
    		 if (curSourceId == EnumSourceIndex.SOURCE_DVBC|| curSourceId == EnumSourceIndex.SOURCE_DTMB){
    			 isVoiceSpeaker = true;
    		 }else{
    			 isRestoreItem = true;
    		 }
    	}else if (position == 9) {
    		isRestoreItem = true;
    	}
    	
		  if (isRestoreItem) {	  
		      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
		      View myView = pwdFactory.inflate(R.layout.psw_input_dialog,null);

	    	  pwdDialog = new Dialog(mContext,R.style.NobackDialog);
	    	  pwdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    	  pwdDialog.setContentView(myView);
	    	  pwdDialog.show();
/*	    	  delayForPwdDialog();*/
	    	  
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
/*						 delayForPwdDialog();*/
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
/*		            	 delayForPwdDialog();*/
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
		  }  else if (isTrackModeItem) {
				int tmode = UmtvManager.getInstance().getAudio().getTrackMode();
				int tmodeIndex= Util.getIndexFromArray(tmode, InterfaceValueMaps.audio_track_mode);
				tmodeIndex %= 4;
				ArrayList<String> trackModeStrList = new ArrayList<String>();
				for (int i=0; i<4; i++) {
					trackModeStrList.add(getResources()
							.getString(InterfaceValueMaps.audio_track_mode[i][1]));
				}
				showSignleSelectDisalog(listItems[position], 
							trackModeStrList.toArray(new String[trackModeStrList.size()]),
							tmodeIndex, 
							new OnSingleItemSelected() {
						
								@Override
								public void onSelected(int pos) {
									UmtvManager.getInstance().getAudio()
											.setTrackMode(InterfaceValueMaps.audio_track_mode[pos][0]);
								}
							});
				
		  } else if (isTrackSelItem) {
              if (mTracksList != null) {
            	  int curTrack = UtilDvbPlayerCtrl.getAudioCurTrack();
            	  if (curTrack >= 0 && curTrack < mTracksList.length) {
            		  showSignleSelectDisalog(listItems[position], 
            				mTracksList,
            				curTrack, 
  							new OnSingleItemSelected() {
  						
  								@Override
  								public void onSelected(int pos) {
  									UtilDvbPlayerCtrl.setAudioTrack(pos);
  								}
  							});
            	  }
              }
		  }else if(isVoiceSpeaker){
				boolean speaker = AudioInterface.getAudioManager().isAmplifierMute(); 
				AudioInterface.getAudioManager().enableAmplifierMute(!speaker);
			    notifyListviewForDataChange();
		  } else if(0==position){	
			    int mode = AudioInterface.getAudioManager().getSoundMode(); 
		  	    Log.i(TAG,"ZEMIN mode="+mode);
			    int modeIndex = Util.getIndexFromArray(mode,InterfaceValueMaps.sound_mode);
			    int flag=0;
			    Log.i(TAG,"ZEMIN modeIndex="+modeIndex);
			      for(int i=0;i<sound_mode_flag.length;i++){
			    	  if(mode==sound_mode_flag[i]){
			    		  flag=i;
			    	  }
			      }
			      Log.i(TAG,"ZEMIN flag="+flag); 
			      int [] dialog_item_img = new int[]{
			   			R.color.transparent,  
		 	   			R.color.transparent, 
		 	   			R.color.transparent, 
		 	   			R.color.transparent, 
		 	   			R.color.transparent 
			      };
			      dialog_item_img[flag] = R.drawable.net_select;
			      
		  	   final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_item);
		  	  
		      malertdialog.setTitle(listItems[position]);
		      LayoutInflater factory = LayoutInflater.from(mContext);
		      View myView = factory.inflate(R.layout.dialog_layout,null);
		    final ListView lisview = (ListView) myView.findViewById(R.id.setting_list);
		    final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
			for (int i=0; i<sound_mode.length; i++) {
				HashMap<String, Object> map =  new HashMap<String, Object>(); 
				map.put("ItemContext", sound_mode[i]); 
				map.put("ItemImg", dialog_item_img[i]); 		
				listDialog.add(map);
			}
			final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
					                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
		     lisview.setAdapter(mSimpleAdapter);
			  lisview.setSelection(flag);
		     lisview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int selectItemSoundMode, long arg3) {

					soundModeItemClick(listDialog, mSimpleAdapter, selectItemSoundMode);
				}
			});
		     malertdialog.setView(myView);
		     mAlertDialog = malertdialog.create();
		     mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		     mAlertDialog.show();
		     delay();
		      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
						// TODO Auto-generated method stub
		             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
		            	 int position = lisview.getSelectedItemPosition();	
		            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�							
		            	       delay();
				               switch (keycode) {
								  case KeyEvent.KEYCODE_MENU:
							    		 mAlertDialog.dismiss();		
										break; 
								  case 	KeyEvent.KEY_SOURCEENTER:
									  soundModeItemClick(listDialog, mSimpleAdapter, position);
									  return true;
							}	
		             }
						return false;
					}
				});
		      	
		 }  else if(3==position){  
		    	    int spdif = AudioInterface.getAudioManager().getSPDIFOutput();   //SPDIF闁跨喐鏋婚幏鐑芥晸閿燂拷		    	   
		    	    final int spdifIndex =  Util.getIndexFromArray(spdif, InterfaceValueMaps.SPDIF_output);
		    	    int [] dialog_item_img = new int[]{
		    	    		R.color.transparent,
		    	    		R.color.transparent,
		    	    		R.color.transparent,
		    	    };
		    	    dialog_item_img[spdifIndex]=R.drawable.net_select;
		    	    
		  	     AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_item);
		  	      malertdialog.setTitle(listItems[position]);
		  	      LayoutInflater factory = LayoutInflater.from(mContext);
		  	      View myView = factory.inflate(R.layout.dialog_layout,null);
		  	     final ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
		  	    ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
		 		for (int i=0; i<spdifVal.length; i++) {
		 			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		    		map.put("ItemContext", spdifVal[i]); 
		    		map.put("ItemImg", dialog_item_img[i]); 		
		    		listDialog.add(map);
		    	}
		  		 SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.setting_item_dialog, 
		                  new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
		  		
		        lisview.setAdapter(mSimpleAdapter);
				  lisview.setSelection(spdifIndex);
		       lisview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int selectItemSoundMode, long arg3) {	
					sPDIFItemClick(lisview, selectItemSoundMode);

				}
			});
		       malertdialog.setView(myView);
		       mAlertDialog = malertdialog.create();
		       mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		       mAlertDialog.show();
			   delay();
			      	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
							// TODO Auto-generated method stub
			             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
			            	 int position = lisview.getSelectedItemPosition();	
			            	 //闁跨喎褰ㄧ敮顔藉闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔虹哺閹插瀚归柨鐔告灮閹风兘鏁撻敓锟絪闁跨喐鏋婚幏宄般亼闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚归幁锟�								
			            	       delay();
					               switch (keycode) {
									  case KeyEvent.KEYCODE_MENU:							    	
								    		 mAlertDialog.dismiss();		
											break; 
									  case 	KeyEvent.KEY_SOURCEENTER:
										  sPDIFItemClick(lisview, position);
										  return true;
								}									
			             }
							return false;
						}
					});    
		 }else if(2==position){

		     boolean avl =AudioInterface.getAudioManager().getEffectParameter(          //闁跨喓娈曠拋瑙勫闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗  闁跨喐鏋婚幏锟介柨鐔告灮閹凤拷
		             EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF) == 1 ? true
		             : false;  
			     int avlIndex;
			      if(avl){
			    	  avlIndex =1;
			      }else{
			    	  avlIndex =0;
			      }
			   avlIndex--;
	            if (avlIndex < 0) {
	            	avlIndex = 1;
	            }  
	            AudioInterface.getAudioManager().setEffectParameter(
	                    EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF, avlIndex );
				notifyListviewForDataChange();
		 } 
		 else if(5==position){
			 
		      LayoutInflater surroundFactory = LayoutInflater.from(mContext);
		      View surroundView = surroundFactory.inflate(R.layout.virtual_surround,null);
		      
		      surroundList = (ListView) surroundView.findViewById(R.id.setting_surround_list);
		      
		      tv_surround_islistshow =(TextView) surroundView.findViewById(R.id.tv_surround_islistshow);
		      sw_SurroundOnOff =(CheckBox) surroundView.findViewById(R.id.sw_SurroundOnOff);
			 
			  surroundListItemValInit(); 			  
					
				for (int i=0; i<surroundListItems.length; i++) {
		            HashMap<String, Object> map = new HashMap<String, Object>(); 
		    		map.put("ItemContext", surroundListItems[i]);
		    		map.put("ItemHelptxt", surroundListItemsHelp[i]);
		    		map.put("ItemVal",surroundListItemVals[i]);		
		    		surroundListName.add(map);
		    	}				
		      
				if(sws_status){
				    sw_SurroundOnOff.setChecked(true);
					surroundList.setFocusable(true);					
				       surroundAdapter = new SimpleAdapter(mContext, surroundListName,
                               R.layout.setting_item4, 
                               new String[] {"ItemContext", "ItemHelptxt","ItemVal"},      
                               new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val});		       
					 surroundList.setAdapter(surroundAdapter);
				    surroundList.requestFocus();
				}else{
					sw_SurroundOnOff.setChecked(false);
					surroundList.setFocusable(false);
				       surroundAdapter = new SimpleAdapter(mContext, surroundListName,
                               R.layout.setting_item3, 
                               new String[] {"ItemContext", "ItemHelptxt","ItemVal"},      
                               new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val});		       	
					surroundList.setAdapter(surroundAdapter);
				}
			  
		      sw_SurroundOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						AudioInterface.EnableSWS(true);
					     surroundAdapter = new SimpleAdapter(mContext, surroundListName,
	                               R.layout.setting_item4, 
	                               new String[] {"ItemContext", "ItemHelptxt","ItemVal"},      
	                               new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val});
					    surroundList.setAdapter(surroundAdapter);
						surroundList.setFocusable(true);
					    surroundList.requestFocus();
					}else{
						AudioInterface.EnableSWS(false);
					     surroundAdapter = new SimpleAdapter(mContext, surroundListName,
	                               R.layout.setting_item3, 
	                               new String[] {"ItemContext", "ItemHelptxt","ItemVal"},      
	                               new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val});
					    surroundList.setAdapter(surroundAdapter);
						surroundList.setFocusable(false);
					}
				}
			});		      
		         surroundList.setNextFocusRightId(R.id.setting_surround_list);
		         surroundList.setOnItemClickListener(new OnItemClickListener() {

		             @Override
		             public void onItemClick(AdapterView<?> arg0, View arg1,
		                     int position, long arg3) {
		               
		               surroundListviewItemClick(position);
		 			}
		         	
		 		});
		      
	    	   surroundDialog = new Dialog(mContext,R.style.NobackDialog_backgroundDimEnabled_false);
	    	  surroundDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    	  surroundDialog.setContentView(surroundView);
	    	  surroundDialog.show();
				Message msg = new Message();
		      	msg.what = Constant.SOUND_DIALOG_ITEM_SHOW;
		      	mHandler.sendMessage(msg);
		      	delayForSurroundDialog();
		      	
		      	surroundDialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
					      	msg.what = Constant.SOUND_DIALOG_ITEM_DISMISS;
					      	mHandler.sendMessage(msg); 					
						}
					});
		      	
			      surroundDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {			
			 			@Override
			 				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent event) {
			 			        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
			 			        	int position = surroundList.getSelectedItemPosition();
			 			        	delayForSurroundDialog();
			 			        	
			 					switch (keycode) {
			 					case KeyEvent.KEYCODE_DPAD_LEFT:
			 					case KeyEvent.KEYCODE_DPAD_RIGHT:
			 						if(AudioInterface.getSWS_Status()&&surroundList.hasFocus()){
			 							  surroundListviewItemClick(position);
			 						}
			 					
			 						break;
/*			 			        case KeyEvent.KEYCODE_DPAD_DOWN:
			 			        	if (surroundList.getSelectedItemPosition() ==( surroundList.getCount()-1)) {
			 			        		surroundList.setSelection(0);		        		
			 			        	    }
			 			            break;
			 			        case KeyEvent.KEYCODE_DPAD_UP:
			 			            if (surroundList.getSelectedItemPosition() == 0) {
			 			            	surroundList .setSelection(surroundList.getCount() - 1);
			 			                 }
			 			            break;*/

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
			 			    	 case KeyEvent.KEY_SOURCEENTER:	
							    	  new Thread() {
							    		   public void run() {
							    		    try {
							    		     Instrumentation inst = new Instrumentation();
							    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER );
							    		    } catch (Exception e) {
							    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
							    		    }
							    		   }
							    		  }.start();	
			 			    		 return true;    
			 					}
			 				 }
			 					return false;
			 				}	        
			 		});     
		 }
	/*	 else if(4==position){

				int speaker = AudioInterface.getAudioManager().getSpeakerOutput(); 
				speaker--;
				if(speaker<0){
					speaker=1;
				}
				AudioInterface.getAudioManager().setSpeakerOutput(speaker);
						notifyListviewForDataChange();
		 }*/
		  else {
		  
		      InterfaceLogic logic = mLogicFactory.createLogic(
		              1, position);
		      
		      if (null == logic) {
		          return;
		      }
		      List<WidgetType> list = logic.getWidgetTypeList();
		      if (logic != null && list != null) {
		          mAlertDialog = new AlertDialog.Builder(mContext,
		                  R.style.Translucent_NoTitle).create();
		          mDialogLayout.setVisibility(View.INVISIBLE);
		          mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		          mAlertDialog.show();
		          Window window = mAlertDialog.getWindow();
		          WindowManager.LayoutParams lp = window
		                  .getAttributes();
		          lp.x = 10;
		          lp.y = 150;	                
		          mCustomSettingView = new CustomSettingView(
		                  mAlertDialog, mContext, listItems[position], mLogicFactory
		                          .createLogic(1, position));
		          window.setContentView(mCustomSettingView);
		          
		          if (logic != null && list.size() == 1) {
		              lp.y = 250;
		              
		              String w = SystemProperties.get("ro.sf.lcd_density");
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
		            	mDialogLayout.setVisibility(View.VISIBLE);	
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
		      unMuteIfMute();
		  }
	}

	private void surroundListItemValInit() {
		 sws_status =   AudioInterface.getSWS_Status();
		 surround_status = AudioInterface.getSURROUND_Status();
		 dialog_status = AudioInterface.getDIALOG_Status();
		 treble_status = AudioInterface.getTREBLE_Status();
		 bass_status = AudioInterface.getBASS_Status();
		 loundness = AudioInterface.getLOUDNESS_Status();
		 handMode = AudioInterface.getHANGMODE_Status();
		Log.i(TAG,"sws_status="+sws_status);
		Log.i(TAG,"surround_status="+surround_status);
		Log.i(TAG,"dialog_status="+dialog_status);
		Log.i(TAG,"treble_status="+treble_status);
		Log.i(TAG,"bass_status="+bass_status);
		Log.i(TAG,"loundness="+loundness);
		Log.i(TAG,"handMode="+handMode);
		
		surroundListName = new ArrayList<HashMap<String, Object>>(); 
		  surroundListItems = new String[]{	
					    			getResources().getString(R.string.surround_sound),
					    			getResources().getString(R.string.voice_enhancement),
					    			getResources().getString(R.string.treble_boost),
					    			getResources().getString(R.string.bass_boost),
					    			getResources().getString(R.string.equal_loudness),
					    			getResources().getString(R.string.hang_mode),
			    	};
					
		 surroundListItemsHelp = new String[]{
					    			getResources().getString(R.string.surround_sound_help),						    			
					    			getResources().getString(R.string.voice_enhancement_help),
					    			getResources().getString(R.string.treble_boost_help),
					    			getResources().getString(R.string.bass_boost_help),
					    			getResources().getString(R.string.equal_loudness_help),
					    			getResources().getString(R.string.hang_mode_help),
			    	};
		  surroundListItemVals = new String[]{
				  ( surround_status ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
				  ( dialog_status ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
				  ( treble_status ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
				  ( bass_status ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
				  ( loundness ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
				  ( handMode ? getResources().getString(R.string.on) : getResources().getString(R.string.off) ),
		 			}; 
					
/*		 surroundListItemRightImgs = new int[]{
			 			R.drawable.selector_arrow_right, 
			 			R.drawable.selector_arrow_right, 
			 			R.drawable.selector_arrow_right, 
			 			R.drawable.selector_arrow_right, 
			 			R.drawable.selector_arrow_right, 
			 			R.drawable.selector_arrow_right, 
			 			}; 
				
		surroundListItemLeftImgs = new int[]{
			 			R.drawable.selector_arrow_left, 
			 			R.drawable.selector_arrow_left, 
			 			R.drawable.selector_arrow_left, 
			 			R.drawable.selector_arrow_left, 
			 			R.drawable.selector_arrow_left,
			 			R.drawable.selector_arrow_left,
			 			};*/
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
    
    private void stripListItem() {
    	ArrayList<String> newItemValsList = new ArrayList<String>();
    	ArrayList<Integer> newItemRightImgsList = new ArrayList<Integer>();
    	ArrayList<Integer> newItemLeftImgsList = new ArrayList<Integer>();
    	ArrayList<String> newItemItemsList = new ArrayList<String>();
    	ArrayList<String> newItemsHelpList = new ArrayList<String>();
    	
    	for (int i = 0; i < listItemVals.length; i++) {
    		if (listItemVals[i] != null) {
    			newItemValsList.add(listItemVals[i]);
    			newItemRightImgsList.add(listItemRightImgs[i]);
    			newItemLeftImgsList.add(listItemLeftImgs[i]);
    			newItemItemsList.add(listItems[i]);
    			newItemsHelpList.add(listItemsHelp[i]);
    		}
    	}
    	
    	listItemVals = 	(String[]) newItemValsList.toArray(new String[newItemValsList.size()]);
    	listItemRightImgs = new int[newItemRightImgsList.size()];
    	for (int i=0;i<listItemRightImgs.length;i++) {
    		listItemRightImgs[i] = newItemRightImgsList.get(i);
    	}
    	
    	listItemLeftImgs = new int[newItemLeftImgsList.size()];
    	for (int i=0;i<listItemLeftImgs.length;i++) {
    		listItemLeftImgs[i] = newItemLeftImgsList.get(i);
    	}
    	
    	listItems = (String[]) newItemItemsList.toArray(new String[newItemItemsList.size()]);
    	listItemsHelp = (String[]) newItemsHelpList.toArray(new String[newItemsHelpList.size()]);
    }
   
    /**
     * 闁跨喐鏋婚幏宄邦瀶闁跨喐鏋婚幏绋琲stItem闁跨喎褰ㄧ喊澶嬪閸婏拷
     */
   private void initListItemVals(){
	  	  int mode = AudioInterface.getAudioManager().getSoundMode();  //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚瑰Ο鈥崇础
	      int  modeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.sound_mode);
	     String modeStr="";
	     modeStr = getResources().getString(InterfaceValueMaps.sound_mode[modeIndex][1]);
		      String eq = "" ; //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐕傛嫹
	     
	     int balance = AudioInterface.getAudioManager().getEffectParameter(  //楠炴娊鏁撻弬銈嗗
	             EnumSoundEftParam.E_SOUND_SET_PARAM_BALANCE)-50;
	     Log.i(TAG,"balance="+balance);
	     
	/*     String surround = ""; //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷
	*/      
	     boolean avl =AudioInterface.getAudioManager().getEffectParameter(          //闁跨喓娈曠拋瑙勫闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗  闁跨喐鏋婚幏锟介柨鐔告灮閹凤拷
	             EnumSoundEftParam.E_SOUND_SET_PARAM_AVC_ONOFF) == 1 ? true
	             : false;  
		     int avlIndex;
		      if(avl){
		    	  avlIndex =1;
		      }else{
		    	  avlIndex =0;
		      }
		  String avlStr  =   getResources().getString(InterfaceValueMaps.on_off[avlIndex][1]);   
	     Log.i(TAG,"avlStr="+avlStr);
	     
	/*     int delay = AudioInterface.getAudioManager().getSPDIFOutput(); //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹烽攱妞�
	     int delayIndex =  Util.getIndexFromArray(delay, InterfaceValueMaps.SPDIF_output);
	     String delayStr =   getResources().getString(InterfaceValueMaps.SPDIF_output[delayIndex][1]);   
	     Log.i(TAG,"delayStr="+delayStr);*/
	     
	     
	     int spdif = AudioInterface.getAudioManager().getSPDIFOutput();   //SPDIF闁跨喐鏋婚幏鐑芥晸閿燂拷	     
	     int spdifIndex =  Util.getIndexFromArray(spdif, InterfaceValueMaps.SPDIF_output);
	     String spdifStr =   getResources().getString(InterfaceValueMaps.SPDIF_output[spdifIndex][1]);   
	     Log.i(TAG,"spdif="+spdif);
		 
		 /*     int hangMode = AudioInterface.getAudioManager().getHangMode();  //闁跨喕濡惂鍛婂闁跨喐鏋婚幏閿嬫櫏闁跨喐鏋婚幏锟�	     int hangModeIndex= Util.getIndexFromArray(hangMode, InterfaceValueMaps.hang_mode);
	     String handModeStr = getResources().getString(InterfaceValueMaps.hang_mode[hangModeIndex][1]); 
	     Log.i(TAG,"hangModeIndex="+hangModeIndex+" EnumSoundfield.SNDFIELD_HANG="+EnumSoundfield.SNDFIELD_HANG+" EnumSoundfield.SNDFIELD_DESKTOP="+EnumSoundfield.SNDFIELD_DESKTOP);
	     Log.i(TAG,"hangMode="+hangMode+" handModeStr="+handModeStr);*/
	     
	     
	     String virtualSurround = "";
	     
		String trackModeStr=null;
		if (mIsTrackModeShow) {
			trackModeStr = "";
			int tmode = UmtvManager.getInstance().getAudio().getTrackMode();
			int  tmodeIndex= Util.getIndexFromArray(tmode, InterfaceValueMaps.audio_track_mode);
			trackModeStr = getResources().getString(InterfaceValueMaps.audio_track_mode[tmodeIndex][1]);
		}
		
		String trackSel=null;
		if (mIsTrackSelectShow) {
			
			int trackCount = UtilDvbPlayerCtrl.getAudioTrackCount();
			if (trackCount > 0) {
				mTracksList = new String[trackCount];
				for (int i=0; i<trackCount; i++) {
					mTracksList[i] = getResources().getString(R.string.audio_track)+" "+(i+1);
				}
				
				trackSel = mTracksList[UtilDvbPlayerCtrl.getAudioCurTrack()];
			}
		}
		
	     boolean speaker = AudioInterface.getAudioManager().isAmplifierMute();  //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹凤拷
	     String speakerStr = getResources().getString( R.string.off);
	     if(speaker){
	    	 speakerStr = getResources().getString( R.string.on);
	     }
	     Log.i(TAG,"speaker="+speaker+" speakerStr="+speakerStr);
	     
	    String reset = "";  //闁跨喕顢滈棃鈺傚闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�
		 
		  	 listItemVals = new String[]{
	 			modeStr, 
	 			balance+"", 
	/* 			surround,*/
	 			avlStr,
	/* 			delayStr,*/
	 			spdifStr,
	/* 			handModeStr,*/
	/* 			speakerStr,*/
	 			eq,
	 			virtualSurround,
	 			trackModeStr,
	 			trackSel,
	 			speakerStr,
	 			reset
	 			}; 
		  	 
		  	listItemRightImgs = new int[]{
		 			R.drawable.selector_arrow_right, 
		 			R.drawable.selector_arrow_right, 
		/* 			R.color.transparent,*/
		 			R.drawable.selector_arrow_right,
		/* 			R.drawable.selector_arrow_right,*/
		 			R.drawable.selector_arrow_right,
		 			R.color.transparent,
		/* 			R.drawable.selector_arrow_right,*/
//		 			R.drawable.selector_arrow_right,

		 			R.color.transparent,
		 			R.drawable.selector_arrow_right,
		 			R.drawable.selector_arrow_right,
		 			R.drawable.selector_arrow_right,
		 			R.color.transparent
		 			}; 
		 	
		 	 listItemLeftImgs = new int[]{
		 			R.drawable.selector_arrow_left, 
		 			R.drawable.selector_arrow_left, 
		/* 			R.color.transparent,*/
		 			R.drawable.selector_arrow_left,
		/* 			R.drawable.selector_arrow_left,*/
		 			R.drawable.selector_arrow_left,
		 			R.color.transparent,
		/* 			R.drawable.selector_arrow_left,*/
//		 			R.drawable.selector_arrow_left,
		 			R.color.transparent,
		 			R.drawable.selector_arrow_left,
		 			R.drawable.selector_arrow_left,
		 			R.drawable.selector_arrow_left,
		 			R.color.transparent
		 			}; 
		     
		    	 listItems = new String[]{
		    			getResources().getStringArray(R.array.voice_setting)[0], 
		    			getResources().getStringArray(R.array.voice_setting)[2], 
		/*    			getResources().getString(R.string.setting_voice_surround),*/
		    			getResources().getString(R.string.setting_voice_avl),
		/*			    getResources().getString(R.string.setting_voice_delay),*/
		    			getResources().getStringArray(R.array.voice_setting)[5],
		/*    			getResources().getStringArray(R.array.voice_setting)[6],*/
//		    			getResources().getString(R.string.setting_voice_speaker),
		       			getResources().getStringArray(R.array.voice_setting)[1],
		       			getResources().getString(R.string.virtual_surround),
		       			getResources().getStringArray(R.array.voice_setting)[7],
		       			getResources().getStringArray(R.array.voice_setting)[8],
		       			getResources().getStringArray(R.array.voice_setting)[9],
		    			getResources().getString(R.string.setting_voice_reset)
		    	};
		    	 listItemsHelp = new String[]{
		    			getResources().getString(R.string.Sound_Mode_help),	
		    			getResources().getString(R.string.Balance_help),	
		    			getResources().getString(R.string.setting_voice_avl_help),	
		    			getResources().getString(R.string.SPDIF_help),	
//		    			getResources().getString(R.string.setting_voice_speaker_help),	
		    			getResources().getString(R.string.EQ_help),
		    			getResources().getString(R.string.virtual_surround_help),
		    			getResources().getString(R.string.track_mode_help),
		    			getResources().getString(R.string.track_select_help),
		    			getResources().getString(R.string.voice_speaker_help),
		       			getResources().getString(R.string.setting_voice_reset_help),
		    	};
		    	 
		    	stripListItem();
		 		 		  
   }
   /**
    * 闁跨喐鏋婚幏宄邦瀶闁跨喐鏋婚幏绌昳stview闁跨喐鏋婚幏鐑芥晸閸欘偉顔愰幏鐑芥晸閺傘倖瀚归柅澶愭晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗
    */
   private void initValueInItem(){
	  	 sound_mode = new String[]{
	    			getResources().getString(R.string.sndmode_standard_string) ,
	    			getResources().getString(R.string.sndmode_movie_string),
	    			getResources().getString(R.string.sndmode_music_string),
	    			getResources().getString(R.string.sndmode_dialog_string),
	    			getResources().getString(R.string.sndmode_user_string),
	    	 };
	    	  sound_mode_flag = new int[]{
	     			0,
	     			1,
	     			2,
	     			4,
	     			8,
	     	 };
	     	      
	      spdifVal = new String []{
	     		// getResources().getString(R.string.off),
	     		 getResources().getString(R.string.spdif_pcm_string),
	     		 getResources().getString( R.string.spdif_rawdata_string)
	      };

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
				map.put("ItemHelptxt", listItemsHelp[i]);
				map.put("ItemVal",listItemVals[i]);
				map.put("ItemRightImg",listItemRightImgs[i]);
				map.put("ItemLeftImg",listItemLeftImgs[i]);
				listName.add(map);
			}

			mSchedule.notifyDataSetChanged();
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
    /**
     * handler of finish dialog
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_ITEM_DISMISS_BYTIME:
				 mAlertDialog.dismiss();
				break;
			case Constant.DIALOG_PWD_DISMISS_BYTIME:
				pwdDialog.dismiss();
				break;	
			case Constant.DIALOG_SURROUND_DISMISS_BYTIME:
				surroundDialog.dismiss();
				break;		
			}
            
        };
    };
    
    public void delayForSurroundDialog() {
    	Log.i(TAG,"calling delayForSurroundDialog()");
        finishHandle.removeMessages(Constant.DIALOG_SURROUND_DISMISS_BYTIME);
        Message message = new Message();
        message.what = Constant.DIALOG_SURROUND_DISMISS_BYTIME;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
    
    public void delayForPwdDialog() {
    	Log.i(TAG,"calling delayForPwdDialog()");
        finishHandle.removeMessages(Constant.DIALOG_PWD_DISMISS_BYTIME);
        Message message = new Message();
        message.what = Constant.DIALOG_PWD_DISMISS_BYTIME;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
    /**
     * set delay time to finish activity
     */
    public void delay() {
        finishHandle.removeMessages(Constant.DIALOG_ITEM_DISMISS_BYTIME);
        Message message = new Message();
        message.what = Constant.DIALOG_ITEM_DISMISS_BYTIME;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
    
    @Override
public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i(TAG,"onWindowFocusChanged() hasFocus="+hasFocus);
    if (hasFocus) {
    	delayForDialog();
    } else {
    	soundMainSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
    }
    super.onWindowFocusChanged(hasFocus);
}
public void delayForDialog() {
	soundMainSettingLayoutHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
    Message message = new Message();
    message.what = Constant.DIALOG_DISMISS_BYTIME;
    soundMainSettingLayoutHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
}


public AudioManager getSysAudioManager() {
    if (mAudioManager == null) {
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    return mAudioManager;
}


public void unMuteIfMute() {
	AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	mCurrentStreamType = audioManager.getMasterStreamType();
	 Log.i(TAG,"isStreamMute " + audioManager.isStreamMute(AudioManager.STREAM_MUSIC)+"AudioManager.STREAM_MUSIC is "+AudioManager.STREAM_MUSIC+" getMasterStreamType "+mCurrentStreamType);
    if (audioManager != null) {
    	 Log.i(TAG,"isStreamMute " + audioManager.isStreamMute(mCurrentStreamType));
        if(audioManager.isStreamMute(mCurrentStreamType)){
        	
        	audioManager.setStreamMute(mCurrentStreamType, false);
        	audioManager.adjustStreamVolume(mCurrentStreamType,
        			ADJUST_SAME,
                    (FLAG_SHOW_UI | FLAG_MUTE_VOLUME));
        }
    }
}


private void soundModeItemClick(final ArrayList<HashMap<String, Object>> listDialog,
		final SimpleAdapter mSimpleAdapter, int selectItemSoundMode) {
	AudioInterface.getAudioManager().setSoundMode(sound_mode_flag[selectItemSoundMode]);
	notifyListviewForDataChange();

	int [] item_dialog_item_img = new int[]{
			R.color.transparent,  
			R.color.transparent, 
			R.color.transparent, 
			R.color.transparent, 
			R.color.transparent 
	  };
	  item_dialog_item_img[selectItemSoundMode] = R.drawable.net_select;
	  listDialog.clear();
		for (int i=0; i<sound_mode.length; i++) {
			HashMap<String, Object> map =  new HashMap<String, Object>();
			map.put("ItemContext", sound_mode[i]); 
			map.put("ItemImg", item_dialog_item_img[i]); 		
			listDialog.add(map);
		}
		mSimpleAdapter.notifyDataSetChanged();
	/*
		AudioManager audioManager = (AudioManager)mContext.getSystemService(
                Context.AUDIO_SERVICE);
		mCurrentStreamType = audioManager.getMasterStreamType();
		 Log.i(TAG,"isStreamMute " + audioManager.isStreamMute(AudioManager.STREAM_MUSIC)+"AudioManager.STREAM_MUSIC is "+AudioManager.STREAM_MUSIC+" getMasterStreamType "+mCurrentStreamType);
        if (audioManager != null) {
        	 Log.i(TAG,"isStreamMute " + audioManager.isStreamMute(mCurrentStreamType));
            if(audioManager.isStreamMute(mCurrentStreamType)){
            	
            	audioManager.setStreamMute(mCurrentStreamType, false);
            	audioManager.adjustStreamVolume(mCurrentStreamType,
            			ADJUST_SAME,
                        (FLAG_SHOW_UI | FLAG_MUTE_VOLUME));

            }
        }
        */
		unMuteIfMute();
}

private void sPDIFItemClick(final ListView lisview, int selectItemSoundMode) {
	AudioInterface.getAudioManager().setSPDIFOutput(selectItemSoundMode+1);
	notifyListviewForDataChange();
	
	int [] item_dialog_item_img = new int[]{
			R.color.transparent,
			R.color.transparent,
	};
	item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;
	ArrayList<HashMap<String, Object>> item_listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
	for (int i=0; i<spdifVal.length; i++) {
		HashMap<String, Object> map =  new HashMap<String, Object>(); 
		map.put("ItemContext", spdifVal[i]); 
		map.put("ItemImg", item_dialog_item_img[i]); 		
		item_listDialog.add(map);
	}
	
	 SimpleAdapter itemSimpleAdapter = new SimpleAdapter(mContext, item_listDialog, R.layout.setting_item_dialog, 
	          new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
   lisview.setAdapter(itemSimpleAdapter);
  lisview.setSelection(selectItemSoundMode);
} 
	
	private interface OnSingleItemSelected {
		void onSelected(int pos);
	}
	
	private void showSignleSelectDisalog(String title,final String[] items,
			int initSel, OnSingleItemSelected ls) {

		int[] dialog_item_img = new int[items.length];

		for (int i = 0; i < dialog_item_img.length; i++) {
			dialog_item_img[i] = R.color.transparent;
		}

		dialog_item_img[initSel] = R.drawable.net_select;

		AlertDialog.Builder malertdialog = new AlertDialog.Builder(mContext,
				R.style.Dialog_item);
		malertdialog.setTitle(title);
		LayoutInflater factory = LayoutInflater.from(mContext);
		View myView = factory.inflate(R.layout.dialog_layout, null);
		final ListView lisview = (ListView) myView
				.findViewById(R.id.setting_list);
		final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < items.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemContext", items[i]);
			map.put("ItemImg", dialog_item_img[i]);
			listDialog.add(map);
		}
		final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog,
				R.layout.setting_item_dialog, new String[] { "ItemContext",
						"ItemImg" }, new int[] { R.id.tv_dialog_item,
						R.id.tv_dialog_item_img });

		lisview.setAdapter(mSimpleAdapter);
		lisview.setSelection(initSel);
		final OnSingleItemSelected selectLs = ls;
		lisview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (selectLs != null) {
					selectLs.onSelected(arg2);
				}
				notifyListviewForDataChange();
				
				int [] item_dialog_item_img = new int[]{
						R.color.transparent,  
						R.color.transparent, 
						R.color.transparent, 
						R.color.transparent 
				  };
				  item_dialog_item_img[arg2] = R.drawable.net_select;
				  listDialog.clear();
					for (int i=0; i<items.length; i++) {
						HashMap<String, Object> map =  new HashMap<String, Object>();
						map.put("ItemContext", items[i]); 
						map.put("ItemImg", item_dialog_item_img[i]); 		
						listDialog.add(map);
					}
					mSimpleAdapter.notifyDataSetChanged();				
			}
		});
		malertdialog.setView(myView);
		mAlertDialog = malertdialog.create();
		mAlertDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mAlertDialog.show();
		delay();
		mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keycode,
					KeyEvent arg2) {

				if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
					int position = lisview.getSelectedItemPosition();
					delay();
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
	
    private void surroundListviewItemClick(int position) {
		// TODO Auto-generated method stub
		Log.i(TAG,"calling surroundListviewItemClick()");
        switch (position) {
		case 0:
			if(surround_status){
				AudioInterface.EnableSURROUND(false);
			}else{
				AudioInterface.EnableSURROUND(true);
			}
			break;
		case 1:
			if(dialog_status){
				AudioInterface.EnableDIALOG(false);
			}else{
				AudioInterface.EnableDIALOG(true);
			}
			break;
		case 2:
			if(treble_status){
				AudioInterface.EnableTREBLE(false);
			}else{
				AudioInterface.EnableTREBLE(true);
			}
			break;
		case 3:
			if(bass_status){
				AudioInterface.EnableBASS(false);
			}else{
				AudioInterface.EnableBASS(true);
			}
			break;
		case 4:
			if(loundness){
				AudioInterface.EnableLOUDNESS(false);
			}else{
				AudioInterface.EnableLOUDNESS(true);
			}
			break;
		case 5:
			if(handMode){
				AudioInterface.EnableHANGMODE(false);
			}else{
				AudioInterface.EnableHANGMODE(true);
			}
			break;
		}
        notifySurroundListviewForDataChange(position);
	}
    
	   private void notifySurroundListviewForDataChange(int position) {
		surroundListItemValInit();
		surroundListName.clear();
			for (int i=0; i<surroundListItems.length; i++) {
	            HashMap<String, Object> map = new HashMap<String, Object>(); 
	    		map.put("ItemContext", surroundListItems[i]);
	    		map.put("ItemHelptxt", surroundListItemsHelp[i]);
	    		map.put("ItemVal",surroundListItemVals[i]);  	
	    		Log.i(TAG,"surroundListItemVals["+i+"]="+surroundListItemVals[i]);
	    		surroundListName.add(map);
	    	}
		       surroundAdapter = new SimpleAdapter(mContext, surroundListName,
                       R.layout.setting_item4, 
                       new String[] {"ItemContext", "ItemHelptxt","ItemVal"},      
                       new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2,R.id.setting_menu_item_val});
                surroundList.setAdapter(surroundAdapter);
                surroundList.setSelection(position);
//	        	surroundAdapter.notifyDataSetChanged();
	   }
	   private void isSoundUserMode(){
		   int mode = AudioInterface.getAudioManager().getSoundMode();  
		   int  modeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.sound_mode);
		   if(modeIndex!=6){
			   listName.remove(4);
		   }
	   }
}



