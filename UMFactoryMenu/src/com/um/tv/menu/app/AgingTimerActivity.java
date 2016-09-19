package com.um.tv.menu.app;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.um.tv.menu.R;

public class AgingTimerActivity extends Activity {
	private long mlCount = 0;
	private long mlTimerUnit = 100;
	private TextView tvTime;
	private ImageButton btnStartPause;
	private ImageButton btnStop;
	private Timer timer = null;
	private TimerTask task = null;
	private Handler handler = null;
	private Message msg = null;
	private boolean bIsRunningFlg = false;
	private static final String MYTIMER_TAG = "AGINGTIMER_LOG"; 
	
	// menu item
	private static final int SETTING_TIMER_UNIT_ID = Menu.FIRST;
	private static final int ABOUT_ID = Menu.FIRST + 1;
	private static final int EXIT_ID = Menu.FIRST + 2;
	private static final int SETTING_SECOND_ID = Menu.FIRST + 101;
	private static final int SETTING_100MILLISECOND_ID = Menu.FIRST + 102;
	
	// Setting timer unit flag
	private int settingTimerUnitFlg = SETTING_100MILLISECOND_ID;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agingtimer);
        Log.w(MYTIMER_TAG, "TimerOnCreate");
        tvTime = (TextView)findViewById(R.id.tvTime);
        //btnStartPause = (ImageButton)findViewById(R.id.btnStartPaunse);
        //btnStop = (ImageButton)findViewById(R.id.btnStop);
        
        SharedPreferences sharedPreferences = getSharedPreferences("mytimer_unit", Context.MODE_PRIVATE);
        //getString()�ڶ�������Ϊȱʡֵ�����preference�в����ڸ�key��������ȱʡֵ
        mlTimerUnit = sharedPreferences.getLong("time_unit", 100);
        Log.i(MYTIMER_TAG, "mlTimerUnit = " + mlTimerUnit);
        
        if (1000 == mlTimerUnit) {
        	// second
        	settingTimerUnitFlg = SETTING_SECOND_ID;
			tvTime.setText(R.string.init_time_second);
        } else if (100 == mlTimerUnit) {
			// 100 millisecond
        	settingTimerUnitFlg = SETTING_100MILLISECOND_ID;
			tvTime.setText(R.string.init_time_100millisecond);
        }
        
        // Handle timer message
        handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			// TODO Auto-generated method stub
    			switch(msg.what) {
    			case 1:
    				mlCount++;
    				int totalSec = 0;
    				int yushu = 0;
    				
    				if (SETTING_SECOND_ID == settingTimerUnitFlg) {
						// second
    					totalSec = (int)(mlCount);
					} else if (SETTING_100MILLISECOND_ID == settingTimerUnitFlg) {
						// 100 millisecond
						totalSec = (int)(mlCount / 10);
        				yushu = (int)(mlCount % 10);
					}
    				
    				// Set time display
    				int day = (totalSec / 86400);
    				int hour = (totalSec / 3600);
    				int min = (totalSec / 60);
    				int sec = (totalSec % 60);
    				try{
    					if (SETTING_SECOND_ID == settingTimerUnitFlg) {
    						// second(1000ms)
    						tvTime.setText(String.format("%1$02d:%2$02d:%3$02d:4$02d", day, hour, min, sec));
    					} else if (SETTING_100MILLISECOND_ID == settingTimerUnitFlg) {
    						// 100 millisecond
    						tvTime.setText(String.format("%1$02d:%2$02d:%3$02d:%4$02d:%5$d", day, hour, min, sec, yushu));
    					}
    				} catch(Exception e) {
    					tvTime.setText(day + ":" + hour + ":" + min + ":" + sec + ":" + yushu);
    					e.printStackTrace();
    					Log.e("MyTimer onCreate", "Format string error.");
    				}
    				break;
    				
    			default:
    				break;
    			}
    			
    			super.handleMessage(msg);
    		}
    		
    	};
    	
        //btnStartPause.setOnClickListener(startPauseListener);
        //btnStop.setOnClickListener(stopListener);
    	startClock();
    }
    
    // Start and pause
    View.OnClickListener startPauseListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(MYTIMER_TAG, "Start/Pause is clicked.");
			
			if (null == timer) {
				if (null == task) {
					task = new TimerTask() {
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (null == msg) {
								msg = new Message();
							} else {
								msg = Message.obtain();
							}
							msg.what = 1;
							handler.sendMessage(msg);
						}
						
					};
				}
				
				timer = new Timer(true);
				timer.schedule(task, mlTimerUnit, mlTimerUnit); // set timer duration
			}
			
			// start
			if (!bIsRunningFlg) {
				bIsRunningFlg = true;
				//btnStartPause.setImageResource(R.drawable.pause);
			} else { // pause
				try{
					bIsRunningFlg = false;
					task.cancel();
					task = null;
					timer.cancel(); // Cancel timer
					timer.purge();
					timer = null;
					handler.removeMessages(msg.what);
					//btnStartPause.setImageResource(R.drawable.start);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
    	
    };
    
	public void startClock() {
		// TODO Auto-generated method stub
		Log.i(MYTIMER_TAG, "Start/Pause is clicked.");
		
		if (null == timer) {
			if (null == task) {
				task = new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (null == msg) {
							msg = new Message();
						} else {
							msg = Message.obtain();
						}
						msg.what = 1;
						handler.sendMessage(msg);
					}
					
				};
			}
			
			timer = new Timer(true);
			timer.schedule(task, mlTimerUnit, mlTimerUnit); // set timer duration
		}
		
		// start
		if (!bIsRunningFlg) {
			bIsRunningFlg = true;
			//btnStartPause.setImageResource(R.drawable.pause);
		} else { // pause
			try{
				bIsRunningFlg = false;
				task.cancel();
				task = null;
				timer.cancel(); // Cancel timer
				timer.purge();
				timer = null;
				handler.removeMessages(msg.what);
				//btnStartPause.setImageResource(R.drawable.start);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
    // Stop
    View.OnClickListener stopListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(MYTIMER_TAG, "Stop is clicked.");
			
			if (null != timer) {
				task.cancel();
				task = null;
				timer.cancel(); // Cancel timer
				timer.purge();
				timer = null;
				handler.removeMessages(msg.what);
			}
			
			mlCount = 0;
			bIsRunningFlg = false;
			//btnStartPause.setImageResource(R.drawable.start);
			
			if (SETTING_SECOND_ID == settingTimerUnitFlg) {
				// second
				tvTime.setText(R.string.init_time_second);
			} else if (SETTING_100MILLISECOND_ID == settingTimerUnitFlg) {
				// 100 millisecond
				tvTime.setText(R.string.init_time_100millisecond);
			}
		}
    	
    };
    
    
    // Menu
    @Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		Log.i(MYTIMER_TAG, "Menu is created.");
		
		// Stop timer
		if (null != task) {
			task.cancel();
			task = null;
		}
		if (null != timer) {
			timer.cancel(); // Cancel timer
			timer.purge();
			timer = null;
			handler.removeMessages(msg.what);
		}
		
		bIsRunningFlg = false;
		mlCount = 0;
		//btnStartPause.setImageResource(R.drawable.start);
		
		// �����Ӳ˵������� 
		//SubMenu settingMenu = menu.addSubMenu(0, SETTING_TIMER_UNIT_ID, 0, R.string.menu_setting_timer_unit).setIcon(R.drawable.setting);
		
		// ����Ӧ�����������Ӳ˵�
		// Sub menus do not support item icons, or nested sub menus.
		//settingMenu.add(1, SETTING_SECOND_ID, 0, R.string.menu_setting_second);  
		//settingMenu.add(1, SETTING_100MILLISECOND_ID, 1, R.string.menu_setting_100milisec);  
		
		// About
		//menu.add(0, ABOUT_ID, 1, R.string.menu_about).setIcon(R.drawable.about);
		
		// �˳�
		//menu.add(0, EXIT_ID, 2, R.string.menu_exit).setIcon(R.drawable.exit);
		
		return true;
	}

    // Menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Log.i(MYTIMER_TAG, "Menu item is selected.");
	
		switch(item.getItemId()) {
			case SETTING_TIMER_UNIT_ID:
				break;
			
			case ABOUT_ID:
				// Display about dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.agingtimer_name)
					   .setMessage("What the BIBI was going on")
				       .setCancelable(true)
				       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				break;
				
			case EXIT_ID:
				finish(); // Exit application
				break;
				
			case SETTING_SECOND_ID: // ��(1000ms)
				if (SETTING_SECOND_ID != settingTimerUnitFlg) {
					mlTimerUnit = 1000;
					settingTimerUnitFlg = SETTING_SECOND_ID;
				}
				tvTime.setText(R.string.init_time_second);
				break;
				
			case SETTING_100MILLISECOND_ID: // 100����
				if (SETTING_100MILLISECOND_ID != settingTimerUnitFlg) {
					mlTimerUnit = 100;
					settingTimerUnitFlg = SETTING_100MILLISECOND_ID;
				}
				tvTime.setText(R.string.init_time_100millisecond);
				break;
				
			default:
				Log.i(MYTIMER_TAG, "Other menu item...");
				break;
		}
		
		// Save timer unit
		try{
			SharedPreferences sharedPreferences = getSharedPreferences("mytimer_unit", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();//��ȡ�༭��
			editor.putLong("time_unit", mlTimerUnit);
			editor.commit();//�ύ�޸�
		} catch(Exception e) {
			e.printStackTrace();
			Log.e(MYTIMER_TAG, "save timer unit error.");
		}
		
        
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_MENU == keyCode) {
			super.openOptionsMenu();  // ����������Ϳ��Ե����˵�

			Log.i(MYTIMER_TAG, "Menu key is clicked.");
				
			// Stop timer
			if (null != task) {
				task.cancel();
				task = null;
			}
			if (null != timer) {
				timer.cancel(); // Cancel timer
				timer.purge();
				timer = null;
				handler.removeMessages(msg.what);
			}
			
			bIsRunningFlg = false;
			mlCount = 0;
			//btnStartPause.setImageResource(R.drawable.start);
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

}