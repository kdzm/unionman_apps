package cn.com.unionman.umtvsetting.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Intent;

import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.SocketClient;
import cn.com.unionman.umtvsetting.system.interfaces.SystemSettingInterface;

import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.widget.CustomSettingView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FactoryResetDialogLayout extends LinearLayout{
	private static final String TAG = "FactoryResetDialogLayout";
	private Context mContext ;
    // listView of menu options
	private Button mSystemOKBtn;
	private Button mSystemCancelBtn;
    private ListView menuOptionsListView;
    private int mPositon;
    private AlertDialog mAlertDialog;
    private Dialog   pwdDialog;
	private Handler factoryResetDialogHandler;   
    private SystemSettingInterface mSystemSettingInterface;
    private ArrayList<HashMap<String, String>> listName = new ArrayList<HashMap<String, String>>();
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = mContext.getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            mAlertDialog.dismiss();
        }
    };

	public FactoryResetDialogLayout(Context context, SystemSettingInterface systemSettingInterface,Handler handler) {
		super(context);
		mContext = context;
		factoryResetDialogHandler=handler;
		mSystemSettingInterface = systemSettingInterface;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_reset, this);	 
	    menuOptionsListView = (ListView) findViewById(R.id.setting_menuoptions_list);
	    menuOptionsListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN){
					//有按键操作时发送延时30s消失的消息
					delayForDialog();	
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
    	
    	String[] listItems = new String[]{
    			getResources().getStringArray(R.array.factory_reset)[0],
    			getResources().getStringArray(R.array.factory_reset)[1], 
    			};
    	String[] listItemsHelp = new String[]{
    			getResources().getStringArray(R.array.factory_reset_help)[0],
    			getResources().getStringArray(R.array.factory_reset_help)[1], 
    			};
    	
    	for (int i=0; i<listItems.length; i++) {
    		HashMap<String, String> map = new HashMap<String, String>(); 
    		map.put("ItemContext", listItems[i]);
    		map.put("ItemHelptxt", listItemsHelp[i]);
    		listName.add(map);
    	}
    	
        SimpleAdapter mSchedule = new SimpleAdapter(mContext, listName,
                                                    R.layout.setting_item2, 
                                                    new String[] {"ItemContext","ItemHelptxt"},   
                                                    new int[] {R.id.setting_menu_item_txt,R.id.setting_menu_item_txt2});
        //锟斤拷硬锟斤拷锟斤拷锟绞� 
        menuOptionsListView.setAdapter(mSchedule);
        
        menuOptionsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
              mPositon = position;
              
              if (mPositon==0) {
            	  
        	      LayoutInflater pwdFactory = LayoutInflater.from(mContext);
        	      View myView = pwdFactory.inflate(R.layout.psw_input_dialog,null);

            	  pwdDialog = new Dialog(mContext,R.style.NobackDialog);
            	  pwdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            	  pwdDialog.setContentView(myView);
            	  pwdDialog.show();
            	  delayForPwdDialog();
            	  
        		   final Button   pwdSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
        		   final Button   pwdSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
        		   final EditText edittext =(EditText)   myView.findViewById(R.id.system_back_edittext);
         		  pwdSystemOKBtn.setOnClickListener(new OnClickListener() {
         		    	
        	          @Override
        	          public void onClick(View arg0) {
                           String inputNum = edittext.getText().toString();
                           String deafaultPwd = Constant.DeafaultPwd;
                           String realPwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.RestorePwd); 
        	       			if(realPwd!=null){
        	       				realPwd =  deafaultPwd;
        	    			}
        	       		  Log.i(TAG,"inputNum="+inputNum+" deafaultPwd="+deafaultPwd+" realPwd="+realPwd);
        	       		  if(inputNum.equals(realPwd)){
        	       			 resetFactory(pwdDialog, pwdSystemOKBtn);	       			  
        	       		  }else{
        	       			  Toast.makeText(mContext,getResources().getString(R.string.password_error) , 1).show();
        	       			  edittext.setText("");
        	       		  }
        	          }

        			private void resetFactory(final Dialog pwdDialog,
        					final Button pwdSystemOKBtn) {
        				pwdDialog.dismiss();
        					  // reset
        				  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
        				  LayoutInflater factory = LayoutInflater.from(mContext);
        				  View myView = factory.inflate(R.layout.user_back,null);
        				mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
        				mSystemCancelBtn = (Button) myView.findViewById(R.id.user_back_cancel);
        				     mSystemCancelBtn.setFocusable(true);
        				     mSystemCancelBtn.requestFocus();
        				      pwdSystemOKBtn.setOnClickListener(new OnClickListener() {
            		
        				          @Override
        				          public void onClick(View arg0) {
                		        	  mSystemSettingInterface.restoreDefault();
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
        		  pwdDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
        				
        				@Override
        				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
        					// TODO Auto-generated method stub
        	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
        	            	 delayForPwdDialog();
        	             }
        					return false;
        				}
        			});		  
        			   

        	  }	 else {
            	 // 锟街革拷系统锟斤拷锟�

        		  // reset
            	  AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
            	//  builder.setTitle(getResources().getString(R.string.warming));
        	      LayoutInflater factory = LayoutInflater.from(mContext);
        	      View myView = factory.inflate(R.layout.system_back,null);
        		      mSystemOKBtn = (Button) myView.findViewById(R.id.back_ok_btn);
        		      mSystemCancelBtn = (Button) myView.findViewById(R.id.back_cancel_btn);
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
                              mContext.sendBroadcast(new Intent(
                                      "android.intent.action.MASTER_CLEAR"));
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
			}
        	
		});
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
	        if (hasFocus) {
	        	delayForDialog();
	        } else {
	        	factoryResetDialogHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        }
	        super.onWindowFocusChanged(hasFocus);
	    }
	    public void delayForDialog() {
	    	Log.i(TAG,"delayForDialog()");
	    	factoryResetDialogHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_DISMISS_BYTIME;
	        factoryResetDialogHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
	    }   
	    public void delayForPwdDialog() {
	    	Log.i(TAG,"calling delayForPwdDialog()");
	        finishHandle.removeMessages(Constant.DIALOG_PWD_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_PWD_DISMISS_BYTIME;
	        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
	    }
	    
	    /**
	     * handler of finish dialog
	     */
	    private Handler finishHandle = new Handler() {
	        public void handleMessage(android.os.Message msg) {
	        	switch (msg.what) {
				case Constant.DIALOG_PWD_DISMISS_BYTIME:
					pwdDialog.dismiss();
					break;	
				}
	            
	        };
	    };
}
