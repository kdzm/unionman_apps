package cn.com.unionman.umtvsetting.system;

import java.io.IOException;
import java.util.Calendar;

import cn.com.unionman.umtvsetting.system.util.Constant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.com.unionman.umtvsetting.system.widget.DatePicker;
import cn.com.unionman.umtvsetting.system.widget.TimePicker;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.WindowManager;

public class TimeSettingDialogLayout extends LinearLayout{
	
	private static String TAG = "TimeSettingDialogLayout";
	private static final int ON = 1;
	private static final int OFF = 0;

    private static final int SHOW_DATAPICK = 0;   
    private static final int DATE_DIALOG_ID = 1;    
    private static final int SHOW_TIMEPICK = 2;  
    private static final int TIME_DIALOG_ID = 3; 
    private int mAutoTimeStatus = OFF;
    private int mYear;    
    private int mMonth;  
    private int mDay;   
    private int mHour;  
    private int mMinute;
    private int mSecond;
	private Button timeBn ;
	private Button dateBn;
	private TextView timeTxt;
	private TextView dateTxt;
	private Context mContext;
    private Calendar mCalendar ;    
	private TextView mAutoUpdateTxt;
	private CheckBox mAutoUpdateTxtCb;
    private LinearLayout ll_checkbox;
    private LinearLayout ll_setdate;
    private LinearLayout ll_settime;
	
	private AlertDialog mDateDialog;
	private AlertDialog mTimeDialog;
	private Handler timeSettingDialogHandler;
	public TimeSettingDialogLayout(Context context,Handler handler) {
		super(context);
		mContext = context;
		timeSettingDialogHandler=handler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.time_setting, this);
	    
		initializeViews();
		
		mCalendar = Calendar.getInstance();  
        mYear = mCalendar.get(Calendar.YEAR);    
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);  
          
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);  
        mMinute = mCalendar.get(Calendar.MINUTE);
        mSecond = mCalendar.get(Calendar.SECOND);
        Log.i(TAG, ""+mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute + ":" + mSecond);
        setDateTime();   
        setTimeOfDay(); 
	}
    /** 
     * 初始化控件和UI视图 
     */  
    private void initializeViews(){  
    	dateTxt = (TextView) findViewById(R.id.showdate);    
    	dateBn = (Button) findViewById(R.id.pickdate);   
    	timeTxt = (TextView)findViewById(R.id.showtime);  
        timeBn = (Button)findViewById(R.id.picktime);  
          
        mAutoUpdateTxt = (TextView) findViewById(R.id.autoupdate);
        mAutoUpdateTxtCb = (CheckBox) findViewById(R.id.cb);
        
        ll_checkbox = (LinearLayout) findViewById(R.id.ll_checkbox);
        ll_setdate = (LinearLayout) findViewById(R.id.ll_setdate);
        ll_settime = (LinearLayout) findViewById(R.id.ll_settime);
        
        ll_checkbox.setOnKeyListener(new OnKeyListener() {
					
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN){
					Log.i(TAG,"ll_checkbox onKeydown");
					//有按键操作时发送延时30s消失的消息
					delayForDialog();	
				}
				return false;
			}
		}); 
        
        ll_setdate.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN){
					Log.i(TAG,"ll_setdate onKeydown");
					//有按键操作时发送延时30s消失的消息
					delayForDialog();	
				}
				return false;
			}
		});      
        
        ll_settime.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN){
					Log.i(TAG,"ll_settime onKeydown");
					//有按键操作时发送延时30s消失的消息
					delayForDialog();	
				}
				return false;
			}
		});      

        ll_checkbox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {  
 
	            } 
        	
        });
        
        ll_setdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {  
	               Message msg = new Message();   
	               if (ll_setdate.equals((LinearLayout) v)) {    
	                  msg.what = SHOW_DATAPICK;  
	                  Log.i(TAG, "Onclick date.");
	               }    
	               dateandtimeHandler.sendMessage(msg);   
	            } 
        	
        });
        
        ll_settime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {  
	               Message msg = new Message();   
	               if (ll_settime.equals((LinearLayout) v)) {    
	                  msg.what = SHOW_TIMEPICK;    
	                  Log.i(TAG, "Onclick time.");
	               }    
	               dateandtimeHandler.sendMessage(msg);   
	            } 
        	
        });
        

        
        dateBn.setOnClickListener(new View.OnClickListener() {  
              
            @Override  
            public void onClick(View v) {  
               Message msg = new Message();   
               if (dateBn.equals((Button) v)) {    
                  msg.what = SHOW_DATAPICK;  
                  Log.i(TAG, "Onclick date.");
               }    
               dateandtimeHandler.sendMessage(msg);   
            }  
        });  
       
          
        timeBn.setOnClickListener(new View.OnClickListener() {  
              
            @Override  
            public void onClick(View v) {  
               Message msg = new Message();   
               if (timeBn.equals((Button) v)) {    
                  msg.what = SHOW_TIMEPICK;    
                  Log.i(TAG, "Onclick time.");
               }    
               dateandtimeHandler.sendMessage(msg);   
            }  
        });  
        
        
        mAutoTimeStatus = Settings.Global.getInt(
        		mContext.getContentResolver(), Settings.Global.AUTO_TIME, OFF);
        if(mAutoTimeStatus==OFF){
			ll_setdate.setFocusable(true);
			ll_settime.setFocusable(true);
			dateTxt.setTextColor(getResources().getColor(R.color.white));
			dateBn.setTextColor(getResources().getColor(R.color.black));
			timeTxt.setTextColor(getResources().getColor(R.color.white));
			timeBn.setTextColor(getResources().getColor(R.color.black));
        }else{
			ll_setdate.setFocusable(false);
			ll_settime.setFocusable(false);
			dateBn.setFocusable(false);
			timeBn.setFocusable(false);
			dateTxt.setTextColor(getResources().getColor(R.color.grey));
			dateBn.setTextColor(getResources().getColor(R.color.grey));
			timeTxt.setTextColor(getResources().getColor(R.color.grey));
			timeBn.setTextColor(getResources().getColor(R.color.grey));
        }

        mAutoUpdateTxtCb.setChecked(mAutoTimeStatus==ON);
        
        
        mAutoUpdateTxtCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				Settings.Global.putInt(mContext.getContentResolver(),
						Settings.Global.AUTO_TIME, mAutoTimeStatus == OFF ? ON
								: OFF);
			}
		});
        
        ll_checkbox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 mAutoTimeStatus = Settings.Global.getInt(
			        		mContext.getContentResolver(), Settings.Global.AUTO_TIME, OFF);
				if(mAutoTimeStatus== OFF){
					mAutoUpdateTxtCb.setChecked(true);
					ll_setdate.setFocusable(false);
					ll_settime.setFocusable(false);
					dateBn.setFocusable(false);
					timeBn.setFocusable(false);		
					dateTxt.setTextColor(getResources().getColor(R.color.grey));
					dateBn.setTextColor(getResources().getColor(R.color.grey));
					timeTxt.setTextColor(getResources().getColor(R.color.grey));
					timeBn.setTextColor(getResources().getColor(R.color.grey));
				}else{
					mAutoUpdateTxtCb.setChecked(false);
					ll_setdate.setFocusable(true);
					ll_settime.setFocusable(true);	
					dateTxt.setTextColor(getResources().getColor(R.color.white));
					dateBn.setTextColor(getResources().getColor(R.color.black));
					timeTxt.setTextColor(getResources().getColor(R.color.white));
					timeBn.setTextColor(getResources().getColor(R.color.black));
				}
				Settings.Global.putInt(mContext.getContentResolver(),
						Settings.Global.AUTO_TIME, mAutoTimeStatus == OFF ? ON
								: OFF);
			}
        	
        });
    }  
  
    /** 
     * 设置日期 
     */  
    private void setDateTime(){  

         
       mYear = mCalendar.get(Calendar.YEAR);    
       mMonth = mCalendar.get(Calendar.MONTH);
       mDay = mCalendar.get(Calendar.DAY_OF_MONTH);   
    
       updateDateDisplay();   
    }  
      
    /** 
     * 更新日期显示 
     */  
    private void updateDateDisplay(){  
    	dateTxt.setText(new StringBuilder().append(mYear).append("-")  
               .append((mMonth) < 10 ? "0" + (mMonth + 1) : mMonth + 1).append("-")
               .append((mDay < 10) ? "0" + mDay : mDay));   
    }  

	public void setDate(DatePicker picker) {
        mYear = picker.getCurrentYear();
        mMonth = picker.getCurrentMonth() - 1;
        mDay = picker.getCurrentDay();
        updateDateDisplay();

        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        Log.i(TAG,"onDateSet"+mYear+"-"+mMonth+"-"+mDay);

        long when = mCalendar.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);

        if(now - when > 1000)
			try {
				throw new IOException("failed to set Date.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

    /** 
     * 设置时间 
     */  
    private void setTimeOfDay(){  
       mHour = mCalendar.get(Calendar.HOUR_OF_DAY);  
       mMinute = mCalendar.get(Calendar.MINUTE);  
       updateTimeDisplay();  
    }  
      
    /** 
     * 更新时间显示 
     */  
    private void updateTimeDisplay(){  
    	timeTxt.setText(new StringBuilder().append(mHour).append(":")  
               .append((mMinute < 10) ? "0" + mMinute : mMinute));   
    }

    public void setTime(TimePicker view) {
        mHour = view.getTime()[0];
        mMinute = view.getTime()[1];
        mSecond = view.getTime()[2];

        updateTimeDisplay();

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, mSecond);
        mCalendar.set(Calendar.MILLISECOND, 0); // 设为 0

        Log.i(TAG,"onTimeSet"+mHour+":"+mMinute);

        long when = mCalendar.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);

        if(now - when > 1000)
            try {
                throw new IOException("failed to set Date.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case Constant.DATAPICK_DISMISS:
				mDateDialog.dismiss();
				break;
			case Constant.TIMEPICK_DISMISS:
				mTimeDialog.dismiss();
				break;	
			}
            
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay(int msgWhat) {
        finishHandle.removeMessages(msgWhat);
        Message message = new Message();
        message.what = msgWhat;
        finishHandle.sendMessageDelayed(message, Constant.DELAY_TIME_30S);
    }   
    
    /**  
     * 处理日期和时间控件的Handler  
     */    
    private  Handler dateandtimeHandler = new Handler() {  
    
       @Override    
       public void handleMessage(Message msg) {    
           switch (msg.what) {    
           case SHOW_DATAPICK:    
        	   Log.i(TAG, "show date.");
               showDateSetDialog();
//        	   mDateDialog = new DatePickerDialog(mContext, mDateSetListener, mYear, mMonth,
//                       mDay);
//        	   mDateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        	   mDateDialog.show();
//        	   delay(Constant.DATAPICK_DISMISS);
//        	   mDateDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
//
//				@Override
//				public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
//		             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
//		            	 //有按键操作时发送30s消失的延时消息
//							delay(Constant.DATAPICK_DISMISS);
//		             }
//					return false;
//				}
//			});
               break;   
           case SHOW_TIMEPICK:  
        	   Log.i(TAG, "show time.");
               showTimeSetDialog();
//        	   mTimeDialog = new TimePickerDialog(mContext, mTimeSetListener, mHour, mMinute, true);
//        	   mTimeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//               mTimeDialog.show();
//        	   delay(Constant.TIMEPICK_DISMISS);
//        	   mTimeDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
//
//				@Override
//				public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
//		             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
//		            	 //有按键操作时发送30s消失的延时消息
//							delay(Constant.TIMEPICK_DISMISS);
//		             }
//					return false;
//				}
//			});
        	   break;  
           }    
       }    
    
    };

    private void showTimeSetDialog() {
        AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
        LayoutInflater factory = LayoutInflater.from(mContext);
        View myView = factory.inflate(R.layout.timeset,null);
        final TimePicker picker = (TimePicker) myView.findViewById(R.id.time_set);
        picker.setTime(mHour, mMinute, mSecond);
        final Button okButton = (Button) myView.findViewById(R.id.user_back_ok);
        final Button cancelButton = (Button) myView.findViewById(R.id.user_back_cancel);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(picker);
                mTimeDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeDialog.dismiss();
            }
        });
        mTimeDialog = builder.create();
        mTimeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mTimeDialog.show();
        delay(Constant.TIMEPICK_DISMISS);
        mTimeDialog.getWindow().setContentView(myView);

        mTimeDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if(arg2.getAction() == KeyEvent.ACTION_DOWN){
                    //有按键操作时发送30s消失的延时消息
                    delay(Constant.TIMEPICK_DISMISS);
                    switch (arg1) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            if (mTimeDialog.getCurrentFocus().getId() != R.id.user_back_ok && mTimeDialog.getCurrentFocus().getId() != R.id.user_back_cancel) {
                                okButton.requestFocus();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void showDateSetDialog() {
        AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
        LayoutInflater factory = LayoutInflater.from(mContext);
        View myView = factory.inflate(R.layout.dataset,null);
        final DatePicker picker = (DatePicker) myView.findViewById(R.id.data_set);
        picker.setDate(mYear, mMonth + 1, mDay);
        final Button okButton = (Button) myView.findViewById(R.id.user_back_ok);
        final Button cancelButton = (Button) myView.findViewById(R.id.user_back_cancel);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(picker);
                mDateDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateDialog.dismiss();
            }
        });
        mDateDialog = builder.create();
        mDateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDateDialog.show();
        delay(Constant.DATAPICK_DISMISS);
        mDateDialog.getWindow().setContentView(myView);

        mDateDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if(arg2.getAction() == KeyEvent.ACTION_DOWN){
                    //有按键操作时发送30s消失的延时消息
                    delay(Constant.DATAPICK_DISMISS);
                    switch (arg1) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            if (mDateDialog.getCurrentFocus().getId() != R.id.user_back_ok && mDateDialog.getCurrentFocus().getId() != R.id.user_back_cancel) {
                                okButton.requestFocus();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }
	
	   @Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	        if (hasFocus) {
	        	delayForDialog();
	        } else {
	        	timeSettingDialogHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        }
	        super.onWindowFocusChanged(hasFocus);
	    }
	    public void delayForDialog() {
	    	Log.i(TAG,"calling delayForDialog()");
	    	timeSettingDialogHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_DISMISS_BYTIME;
	        timeSettingDialogHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
	    }
	    
}
