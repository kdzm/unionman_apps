package com.um.ui;

import java.util.Date;

import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Status;
import com.um.dvtca.R;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;

public class Dvt_condition_access extends AppBaseActivity {
	final static String TAG = "Dvt_condition_access";
	static int lastKeyCode = 0;
	static long lastKeyClickTime = 0L;
    Status status;
    private final CountDownTimer countDownTimer = new CountDownTimer(1000 * 30, 1000) {
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!DVB.isServerAlive()) {
        	Toast.makeText(this, "DVB服务已停止，不能进入该应用！", 3000).show();
        	//finish();
        	//return;
        }
        setContentView(R.layout.dvt_condition_access);
            
        Button button1;
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
					
				Intent it1 = new Intent(Dvt_condition_access.this,Dvt_operator_list.class);
				startActivity(it1);
			}
		});
        
        Button button2;
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				Intent it2 = new Intent(Dvt_condition_access.this,Dvt_smc_info.class);
				startActivity(it2);
			}
		});
        
        Button button3;
        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				Intent it3 = new Intent(Dvt_condition_access.this,Dvt_change_pin.class);
				startActivity(it3);
			}
		}); 
        
        Button button4;
        button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				Intent it4 = new Intent(Dvt_condition_access.this,Dvt_watch_level.class);
				startActivity(it4);
			}
		});
        
        Button button5;
        button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				Intent it5 = new Intent(Dvt_condition_access.this,Dvt_working_time.class);
				startActivity(it5);
			}
		});   
/*        
        Button button6;
        button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
			if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_condition_access.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	Intent it6 = new Intent(Dvt_condition_access.this, Dvt_feed_card.class);
        	startActivity(it6);        	    	
        						}
        });
        
        Button button7;
        button7 = (Button)findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
			if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_condition_access.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	Intent it7 = new Intent(Dvt_condition_access.this, Dvt_walletinfo.class);
        	startActivity(it7);        	    	
        						}
        });

        Button button8;
        button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
			if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_condition_access.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	Intent it8 = new Intent(Dvt_condition_access.this, Dvt_small_pin.class );//Dvt_ipp_order_info.class
        	startActivity(it8);        	    	
        						}
        });
        
        Button button9;
        button9 = (Button)findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
			if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_condition_access.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	Intent it9 = new Intent(Dvt_condition_access.this, Dvt_ipp_buy_info.class);
        	startActivity(it9);        	    	
        						}
        });
*/        
        Button button8;
		Button button10;
        button8 = (Button)findViewById(R.id.button10);
        button8.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
        	Intent it10 = new Intent(Dvt_condition_access.this, Dvt_email.class);
        	startActivity(it10);        	    	
        						}
        });
        
//        Button button11;
//        button9 = (Button)findViewById(R.id.button11);
//        button9.setOnClickListener(new View.OnClickListener() {
//        public void onClick(View v) { 
//        	Intent it11 = new Intent(Dvt_condition_access.this, Stb_version_info.class);
//        	startActivity(it11);        	    	
//        						}
//        });

        DVB.getInstance();

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        restarTimer();
        return super.onKeyDown(keyCode, event);
    }

    private void restarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Dvt_condition_access", "ret:" +ret);
    	Log.d("Dvt_condition_access", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Date dt= new Date();
		
		Log.i(TAG, "onKeyUp,curKeyCode:" +keyCode);
		Log.i(TAG, "onKeyUp,lastKeyCode:" +lastKeyCode);
		Log.i(TAG, "onKeyUp,lastKeyClickTime:" +lastKeyClickTime);
		
		if ((keyCode == KeyEvent.KEYCODE_2)&&(lastKeyCode == KeyEvent.KEYCODE_1))
        {
			Log.i(TAG, "onKeyUp,dt.getTime():" +dt.getTime());
			if((dt.getTime() - lastKeyClickTime) <= 2000){
				Intent it12 = new Intent(Dvt_condition_access.this,DvtHideMenu.class);
				startActivity(it12);  
			}
        }
		
		lastKeyCode = keyCode;
		lastKeyClickTime = dt.getTime();

		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}
}
