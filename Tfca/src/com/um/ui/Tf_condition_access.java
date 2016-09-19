package com.um.ui;

import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Status;
import com.um.tfca.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;
public class Tf_condition_access extends AppBaseActivity implements Status.StatusListener {
    private static final String TAG = "Tf_condition_access";
	private Context mContext = Tf_condition_access.this;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button8;
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
        	finish();
        	return;
        }
        setContentView(R.layout.tf_condition_access);
            
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button1Click();
			}
		});
        
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button2Click();
			}
		});
        
        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button3Click();
			}
		}); 
        
        button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button4Click();
			}
		});
        
        button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button5Click();
			}
		});   
        
        button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
			button6Click();        	    	
        						}
        });
        
        
        button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
        	button8Click();        	    	
        						}
        });
        
//        Button button9;
//        button9 = (Button)findViewById(R.id.button9);
//        button9.setOnClickListener(new View.OnClickListener() {
//        public void onClick(View v) { 
//        	Intent it9 = new Intent(Tf_condition_access.this, Stb_version_info.class);
//        	startActivity(it9);        	    	
//        }
//        });
        
        DVB.GetInstance();
        Status.getInstance().addStatusListener(this);
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
        switch (keyCode) {
		 case KeyEvent.KEYCODE_MENU:
			finish();
			break;
		 case KeyEvent.KEY_SOURCEENTER:
			 Log.i(TAG,"KEY_SOURCEENTER is clicked");
			 if(button1.isFocused()){
				 button1Click();
			 }
			 if(button2.isFocused()){
				 button2Click();
			 }
			 if(button3.isFocused()){
				 button3Click(); 
			 }
			 if(button4.isFocused()){
				 button4Click();
			 }
			 if(button5.isFocused()){
				 button5Click();
			 }
			 if(button6.isFocused()){
				 button6Click();
			 }
			 if(button8.isFocused()){
				 button8Click();
			 }
			 return true;
		}
        return super.onKeyDown(keyCode, event);
    }

    private void restarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
    	if (DVB.isServerAlive()) {
    		Status.getInstance().removeStatusListener(this);
    	}
        super.onDestroy();
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.GetInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Tf_condition_access", "ret:" +ret);
    	Log.d("Tf_condition_access", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }

	@Override
	public void OnMessage(Message msg) {
		// TODO Auto-generated method stub
		Prompt.handleMessage(this, msg);
	}

	private void button1Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
			
		Intent it1 = new Intent(Tf_condition_access.this,Tf_smc_status_info.class);
		startActivity(it1);
	}

	private void button2Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it2 = new Intent(Tf_condition_access.this,Tf_change_pin.class);
		startActivity(it2);
	}

	private void button3Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it3 = new Intent(Tf_condition_access.this,Tf_watch_level.class);
		startActivity(it3);
	}

	private void button4Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it4 = new Intent(Tf_condition_access.this,Tf_working_time.class);
		startActivity(it4);
	}

	private void button5Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it5 = new Intent(Tf_condition_access.this,Tf_pair_info_check.class);
		startActivity(it5);
	}

	private void button6Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it6 = new Intent(Tf_condition_access.this, Tf_operator_list.class);
		startActivity(it6);
	}

	private void button7Click() {
		if(getCardStatus() != true){
			Util.showConfirmDialog(mContext,R.string.ca_insert_card);
			return;
		}
		Intent it7 = new Intent(Tf_condition_access.this, Tf_smc_update_status.class);
		startActivity(it7);
	}

	private void button8Click() {
		Intent it8 = new Intent(Tf_condition_access.this, Tf_email.class);
		startActivity(it8);
	}
}
