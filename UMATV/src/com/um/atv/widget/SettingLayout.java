package com.um.atv.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.hisilicon.android.tvapi.constant.Enum3DConstant;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.um.atv.R;
import com.um.atv.ATVMainActivity;
import com.um.atv.SettingActivity;
import com.um.atv.adapter.SettingListAdapter;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.AudioInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.interfaces.SourceManagerInterface;
import com.um.atv.interfaces.Video3DInterface;
/*import com.um.atv.logic.TvChannelAttr;
import com.um.atv.logic.TvProgram;*/
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.logic.factory.LogicFactory;
import com.um.atv.model.WidgetType;
import com.um.atv.util.Constant;
import com.um.atv.util.Util;

/**
 * when you press the menu button,SettingActivity will show SettingLayout will
 * be included in SettingActivity
 *
 * @author wangchuanjian
 *
 */
public class SettingLayout extends LinearLayout {
	
	private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
	
	private static final int DIALOG_FINISH = 1;
	
	private SimpleAdapter mSchedule;

	private String[] listItems;
	private String[] listItemVals;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
    private String[] listItemHelps;

    private String[] modeVal;
    
    private static final String TAG = "SettingLayout";
    private AlertDialog mAlertDialog;
    // public static boolean isChannelEnable = true;
    private Context mContext;
    // listView of menu options
    private ListView menuOptionsListView;
    // adapter of menuOptionsListView
    private LinearLayout menuBgLinearLayout;
    // widget container
    private CustomSettingView mCustomSettingView;
    // position of menuOptionsListView
    private int mFocusPosition = 0;
    // index of focus
    private int mFocusIndex = 0;
    
    private Handler finishHandle;

    private LogicFactory mLogicFactory = null;
    private ArrayList<HashMap<String, Object>> listName = 
			new ArrayList<HashMap<String, Object>>();

    public int getFocusIndex() {
        return mFocusIndex;
    }

    /**
     * Construction method
     *
     * @param context
     * @param attrs
     */
    public SettingLayout(Context context, AttributeSet attrs , Handler handler) {
        super(context, attrs);
        this.mContext = context;
        finishHandle = handler;
        mLogicFactory = new LogicFactory(mContext);
        
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.setting_view, this);
        menuOptionsListView = (ListView) findViewById(R.id.menuoptions_list);
        menuBgLinearLayout = (LinearLayout) findViewById(R.id.setting_containerlayout_bg);
        IntentFilter listViewSet = new IntentFilter(PIC_SET_FINISH_ACTION);
        listViewSet.addAction(SOUND_SET_FINISH_ACTION);
        mContext.registerReceiver(mListViewReceiver, listViewSet);
        mFocusPosition = 0;
        UpdateListView();
        initListView();
        
    }

    public SettingLayout(Context context) {
        super(context);
    }

    
    private BroadcastReceiver mListViewReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context content, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onReceive--->action:" + action);
            }
            if (action.equals(PIC_SET_FINISH_ACTION)) {
                /*
                 * if(mNoSignalLayout.getVisibility() == View.VISIBLE){
                 * mNoSignalLayout.snapBackground(false); }
                 */
            	if (menuBgLinearLayout.getVisibility() == View.INVISIBLE) {
            		menuBgLinearLayout.setVisibility(View.VISIBLE);
               }
            	
                if (menuOptionsListView.getVisibility() == View.INVISIBLE) {
                	menuOptionsListView.setVisibility(View.VISIBLE);
               }
            } else if (action.equals(SOUND_SET_FINISH_ACTION)) {
            	if (menuBgLinearLayout.getVisibility() == View.INVISIBLE) {
            		menuBgLinearLayout.setVisibility(View.VISIBLE);
               }
            	
                if (menuOptionsListView.getVisibility() == View.INVISIBLE) {
                	menuOptionsListView.setVisibility(View.VISIBLE);
               }
            }
        }
    };
    private void initListItemVals()
    {
    	//int changeModeTemp = 0;                                //��̨ģʽ
        //if ( ATVChannelInterface.isChangeModeEnable() == true ){
        	//changeModeTemp = 1;
        //}   
        //int changeModeTempIndex= Util.getIndexFromArray(changeModeTemp,
                //InterfaceValueMaps.change_mode_enable);
        //String changeModeTempStr="";
        //changeModeTempStr = getResources().getString(InterfaceValueMaps.change_mode_enable[changeModeTempIndex][1]);
        int colorSysTemp =	ATVChannelInterface.getColorSystem();       //��ɫ��ʽ
        int colorSysTempIndex= Util.getIndexFromArray(colorSysTemp,
                InterfaceValueMaps.color_system);
        String colorSysTempStr="";
        colorSysTempStr = getResources().getString(InterfaceValueMaps.color_system[colorSysTempIndex][1]);
        	
        int soundSysTemp =	ATVChannelInterface.getAudioSystem();       //������ʽ
        switch (soundSysTemp)
        {
        	case EnumAtvAudsys.AUDSYS_DK:
        	case EnumAtvAudsys.AUDSYS_DK1_A2:
        	case EnumAtvAudsys.AUDSYS_DK2_A2:
        	case EnumAtvAudsys.AUDSYS_DK3_A2:
        	case EnumAtvAudsys.AUDSYS_DK_NICAM:
        		soundSysTemp = EnumAtvAudsys.AUDSYS_DK;
        		break;
        	case EnumAtvAudsys.AUDSYS_BG:
        	case EnumAtvAudsys.AUDSYS_BG_A2:
        	case EnumAtvAudsys.AUDSYS_BG_NICAM:
        		soundSysTemp = EnumAtvAudsys.AUDSYS_BG;
        		break;
        	case EnumAtvAudsys.AUDSYS_M:
        	case EnumAtvAudsys.AUDSYS_M_BTSC:
        	case EnumAtvAudsys.AUDSYS_M_A2:
        	case EnumAtvAudsys.AUDSYS_M_EIA_J:
        		soundSysTemp = EnumAtvAudsys.AUDSYS_M;
        		break;
        	default:
        		break;
        }
        int soundSysTempIndex= Util.getIndexFromArray(soundSysTemp,
                InterfaceValueMaps.audio_system);
        String soundSysTempStr="";
        soundSysTempStr = getResources().getString(InterfaceValueMaps.audio_system[soundSysTempIndex][1]);
      
        	
        //////////////////////////////////////////////////////////////////////
        String autoSearchItem="";
        String manualSearchItem="";    
        String fineTuneItem="";
        String channelEditItem="";
        String voiceItem="";
        String pictrueItem=""; 
        listItemVals = new String[]{
        		autoSearchItem, 
                manualSearchItem,     
                fineTuneItem,
                channelEditItem,
                //skipStr,
                voiceItem,
                pictrueItem ,
                /*changeModeTempStr,*/
                colorSysTempStr,
                soundSysTempStr
    			}; 
    }
    private void UpdateListView() {
    	
    	
    	initListItemVals();
        
    	modeVal = new String []{
        		getResources().getString( R.string.audsys_DK_string), 
        		getResources().getString( R.string.audsys_I_string),  
        		getResources().getString( R.string.audsys_BG_string),  
        		getResources().getString( R.string.audsys_M_string)
           };
    	
        listItemRightImgs = new int[]{
        		R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			/*R.drawable.selector_view_right_gred,*/
    			R.drawable.selector_view_right_gred,
    			R.drawable.selector_view_right_gred		
    			}; 
    	
    	listItemLeftImgs = new int[]{
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			R.drawable.touming,
    			/*R.drawable.selector_view_left_gred,*/
    			R.drawable.selector_view_left_gred,
    			R.drawable.selector_view_left_gred
    			}; 
        listItems = new String[]{
        		getResources().getStringArray(R.array.channel_setting)[0],
        		getResources().getStringArray(R.array.channel_setting)[1],
    			getResources().getStringArray(R.array.channel_setting)[2],
    			getResources().getStringArray(R.array.channel_setting)[3],
    			getResources().getStringArray(R.array.channel_setting)[4],
    			getResources().getStringArray(R.array.channel_setting)[5],
    			/*getResources().getStringArray(R.array.channel_setting)[6],*/
    			getResources().getStringArray(R.array.channel_setting)[7],
    			getResources().getStringArray(R.array.channel_setting)[8]
    	};
        listItemHelps = new String[]{
        		getResources().getStringArray(R.array.channel_setting_help)[0],
        		getResources().getStringArray(R.array.channel_setting_help)[1],
    			getResources().getStringArray(R.array.channel_setting_help)[2],
    			getResources().getStringArray(R.array.channel_setting_help)[3],
    			getResources().getStringArray(R.array.channel_setting_help)[4],
    			getResources().getStringArray(R.array.channel_setting_help)[5],
    			/*getResources().getStringArray(R.array.channel_setting_help)[6],*/
    			getResources().getStringArray(R.array.channel_setting_help)[7],
    			getResources().getStringArray(R.array.channel_setting_help)[8]
    	};
    	
    	
    	for (int i=0; i<listItems.length; i++) {
    	    HashMap<String, Object> map = new HashMap<String, Object>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItemHelps[i]);
    		map.put("ItemVal",listItemVals[i]);
    		map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
    		listName.add(map);
    	}
        mSchedule = new SimpleAdapter(mContext, listName,
									                R.layout.menu_item_option, 
									                new String[] {"ItemContext", "ItemHelptxt","ItemVal","ItemRightImg","ItemLeftImg"},      
									                new int[] {R.id.setting_option_item_txt,R.id.setting_option_item_txt2,R.id.setting_option_item_val,R.id.right_arrow_img,R.id.left_arrow_img});
        menuOptionsListView.setSelection(mFocusPosition);
        menuOptionsListView.setAdapter(mSchedule);
        
    }
    
    /**
     * The initialization of ListView
     */
    private void initListView() {

        menuOptionsListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int position, long arg3) {
                    	listviewItemClick(position);
                    }      
                });
        menuOptionsListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
	
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
					delayForDialog();
					int position = menuOptionsListView.getSelectedItemPosition();
					switch (arg1) {
				       case KeyEvent.KEYCODE_DPAD_RIGHT:
					   case KeyEvent.KEYCODE_DPAD_LEFT:
					           	if(position==6||position==7||position==8){
				            	listviewItemClick(position);	
				        	}

				        	break;
					    case KeyEvent.KEYCODE_DPAD_DOWN:
				            if (Constant.LOG_TAG) {
				                Log.d(TAG, "KEYCODE_DPAD_DOWN=" + KeyEvent.KEYCODE_DPAD_DOWN);
				            }
				            
				            if (menuOptionsListView.getSelectedItemPosition() == (menuOptionsListView.getCount()-1)){
				            	menuOptionsListView.setSelection(0);
				            }
				            
				            break;
				        case KeyEvent.KEYCODE_DPAD_UP:
				            if (menuOptionsListView.getSelectedItemPosition() == 0) {
				                menuOptionsListView
				                        .setSelection(menuOptionsListView.getCount() - 1);
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
    }


    public void dismiss() {
        this.mAlertDialog.dismiss();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_DPAD_LEFT:
            break;
        default:
            break;
        }
        return super.onKeyUp(keyCode, event);
    }


    public void onDestroy() {
        if (mCustomSettingView != null) {
            mCustomSettingView.onDestroy();
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mContext.unregisterReceiver(mListViewReceiver);
    }
    
    private void notifyListviewDataSetChanged(){
    	initListItemVals();
		listName.clear();
		for (int i=0; i<listItems.length; i++) {
    	    HashMap<String, Object> map = new HashMap<String, Object>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemVal",listItemVals[i]);
    		map.put("ItemHelptxt", listItemHelps[i]);
    		map.put("ItemRightImg",listItemRightImgs[i]);
    		map.put("ItemLeftImg",listItemLeftImgs[i]);
    		listName.add(map);
    	}
		mSchedule.notifyDataSetChanged();
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DIALOG_FINISH:
            	mAlertDialog.dismiss();
                break;
            default:
                break;
            }
        }
    };
    public void delay() {
        mHandler.removeMessages(DIALOG_FINISH);
        mHandler.sendEmptyMessageDelayed(DIALOG_FINISH, Constant.DISPEAR_TIME_30s);
        }

	private void listviewItemClick(int position) {
		mFocusPosition = position;
		if (position == 4)
		{
			if (menuBgLinearLayout.getVisibility() == View.VISIBLE) {
				menuBgLinearLayout.setVisibility(View.INVISIBLE);
		  }
			  
		    if (menuOptionsListView.getVisibility() == View.VISIBLE) {
		    	menuOptionsListView.setVisibility(View.INVISIBLE);
		   }
			Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
			mContext.startService(intent_voice);
		}                    
		else if (position == 5)
		{
			if (menuBgLinearLayout.getVisibility() == View.VISIBLE) {
				menuBgLinearLayout.setVisibility(View.INVISIBLE);
		  }
			  
		    if (menuOptionsListView.getVisibility() == View.VISIBLE) {
		    	menuOptionsListView.setVisibility(View.INVISIBLE);
		   }
		    
			Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
			mContext.startService(intent_pic);
		}/*else if(position == 6){
			ATVChannelInterface.enableChangeMode(!ATVChannelInterface.isChangeModeEnable());
			notifyListviewDataSetChanged();
		}*/else if(position == 6){
			
			int t=Util.getIndexFromArray(ATVChannelInterface.getCurrentColorSystem(),InterfaceValueMaps.color_system);
			 ATVChannelInterface
		                 .setColorSystem(InterfaceValueMaps.color_system[t==0?1:0][0]);

			notifyListviewDataSetChanged();
		}else if(position == 7){
			   Log.i(TAG,"zemin  position="+position);
			   int m = ATVChannelInterface.getCurrentAudioSystem();
		       switch (m)
		       {
		       	case EnumAtvAudsys.AUDSYS_DK:
		       	case EnumAtvAudsys.AUDSYS_DK1_A2:
		       	case EnumAtvAudsys.AUDSYS_DK2_A2:
		       	case EnumAtvAudsys.AUDSYS_DK3_A2:
		       	case EnumAtvAudsys.AUDSYS_DK_NICAM:
		       		m = EnumAtvAudsys.AUDSYS_DK;
		       		break;
		       	case EnumAtvAudsys.AUDSYS_BG:
		       	case EnumAtvAudsys.AUDSYS_BG_A2:
		       	case EnumAtvAudsys.AUDSYS_BG_NICAM:
		       		m = EnumAtvAudsys.AUDSYS_BG;
		       		break;
		       	case EnumAtvAudsys.AUDSYS_M:
		       	case EnumAtvAudsys.AUDSYS_M_BTSC:
		       	case EnumAtvAudsys.AUDSYS_M_A2:
		       	case EnumAtvAudsys.AUDSYS_M_EIA_J:
		       		m = EnumAtvAudsys.AUDSYS_M;
		       		break;
		       	default:
		       		break;
		       }

		       final  int modeIndex= Util.getIndexFromArray(m,InterfaceValueMaps.audio_system);;
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
			   
		      Log.i(TAG,"zemin  malertdialog.create().show()");
		    lisview.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int keycode, KeyEvent arg2) {
					// TODO Auto-generated method stub
					
		             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
		            	 int position = lisview.getSelectedItemPosition();		
		            	 //�а������ʱ����30s��ʧ����ʱ��Ϣ
							delay();
			               switch (keycode) {
							  case KeyEvent.KEYCODE_MENU:
						    		 mAlertDialog.dismiss();		
									break; 
							  case 	KeyEvent.KEY_SOURCEENTER:
								  audsysItemClick(listDialog, mSimpleAdapter,position);
								  return true;
						}
		             }
					
					return false;
				}
			});
		  
		      lisview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int selectItemAudioSystem, long arg3) {					
					audsysItemClick(listDialog, mSimpleAdapter,selectItemAudioSystem);
				}
			});
		}
		else{
		InterfaceLogic logic = mLogicFactory.createLogic(
		        mFocusIndex, position);
		if (null == logic) {
		    return;
		}                        
		List<WidgetType> list = logic.getWidgetTypeList();
		if (logic != null && list != null) {
		    mAlertDialog = new AlertDialog.Builder(mContext,
		            R.style.Translucent_NoTitle).create();
		    mAlertDialog.show();
		    mAlertDialog
		            .setOnCancelListener(new OnCancelListener() {

		                @Override
		                public void onCancel(
		                        DialogInterface dialog) {
		                }
		            });

		    mAlertDialog
		            .setOnDismissListener(new DialogInterface.OnDismissListener() {

		                @Override
		                public void onDismiss(
		                        DialogInterface dialog) {
		                	UpdateListView();
		                	setVisibility(View.VISIBLE);
		                	
		                }
		            });

		    Window window = mAlertDialog.getWindow();
		    WindowManager.LayoutParams lp = window
		            .getAttributes();
		    //lp.x = 10;
		    lp.y = 350;
		    Log.d(TAG, "++++++++position"+position+":"+mFocusIndex);
		    mCustomSettingView = new CustomSettingView(
		            mAlertDialog, mContext, listItems[position], mLogicFactory
		                    .createLogic(mFocusIndex, position));
		    window.setContentView(mCustomSettingView);
		    setVisibility(View.INVISIBLE);
		    if (logic != null && list.size() == 1) {
		        lp.y = 350;
		        //lp.height = 150;
		        window.setGravity(Gravity.NO_GRAVITY);
		    }
		}
               }
	}

	private void audsysItemClick(
			final ArrayList<HashMap<String, Object>> listDialog,
			final SimpleAdapter mSimpleAdapter, int selectItemAudioSystem) {
		ATVChannelInterface.setAudioSystem(InterfaceValueMaps.audio_system[selectItemAudioSystem][0]);
		notifyListviewDataSetChanged();
		
 	    int [] item_dialog_item_img = new int[]{
			R.color.transparent,
			R.color.transparent,
			R.color.transparent,
			R.color.transparent
 	    };
 	    item_dialog_item_img[selectItemAudioSystem]=R.drawable.net_select;	      	    
		 listDialog.clear();
		for (int i=0; i<modeVal.length; i++) {
			HashMap<String, Object> map =  new HashMap<String, Object>(); 
			map.put("ItemContext", modeVal[i]); 
			map.put("ItemImg", item_dialog_item_img[i]); 		
			listDialog.add(map);
		}
		mSimpleAdapter.notifyDataSetChanged();
	}
    
    public  void delayForDialog() {
    	Log.i(TAG,"SettingActivity delayForDialog() is calling");
        finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        Message message = new Message();
        message.what = Constant.ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
    }  
      
}
