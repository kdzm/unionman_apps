package com.um.ui;

import com.um.tfca.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.um.controller.AppBaseActivity;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
public class Tf_operator_info extends AppBaseActivity{
	
	private static final String TAG = "Tf_operator_info";
	private String value; 
	private   Button button1;
	private   Button button2;
	private   Button button3;
	private   Button button4;
	private   Button button5;
	private   Button button6;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tf_operator_info);

        /*Get operator ID from Operator information list*/
        Intent intent = getIntent();
        value= intent.getStringExtra("operid");
        //int oper_id = Integer.parseInt(value);
        
        button1 = (Button)findViewById(R.id.operator_btn1);
        button1.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
        	button1Click();
        }
        });
        

        button2 = (Button)findViewById(R.id.operator_btn2);
        button2.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	button2Click();
        }
        });
        

        button3 = (Button)findViewById(R.id.operator_btn3);
        button3.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	button3Click();
        }
        });
        
        button4 = (Button)findViewById(R.id.operator_btn4);
        button4.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	button4Click();
        }
        });
        
        button5 = (Button)findViewById(R.id.operator_btn5);
        button5.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	button5Click();
        }
        }); 
        

        button6 = (Button)findViewById(R.id.operator_btn6);
        button6.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	button6Click();
        }
        }); 
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.GetInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Tf_operator_info", "ret:" +ret);
    	Log.d("Tf_operator_info", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
	     case KeyEvent.KEYCODE_MENU:
			finish();
			break;
		 case KeyEvent.KEY_SOURCEENTER:	
			 Log.i(TAG, "KEY_SOURCEENTER is click");
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
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void button1Click() {
		if(getCardStatus() != true){
			new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();
			return;
		}
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_general_authorized.class);
		Tf_operator_info.this.startActivity(it);
	}

	private void button2Click() {
		if(getCardStatus() != true){
/*				new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();*/
			Util.showConfirmDialog(Tf_operator_info.this,R.string.ca_insert_card);	
			Log.d("Button2", "pop_insert_card");
			return;
		}
		
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_buy_program.class);
		Tf_operator_info.this.startActivity(it);
	}

	private void button3Click() {
		if(getCardStatus() != true){
/*				new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();*/
			Util.showConfirmDialog(Tf_operator_info.this,R.string.ca_insert_card);		
			return;
		}
		
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_walletinfo.class);
		Tf_operator_info.this.startActivity(it);
	}

	private void button4Click() {
		if(getCardStatus() != true){
/*				new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();*/
			Util.showConfirmDialog(Tf_operator_info.this,R.string.ca_insert_card);	
			return;
		}
		
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_negative_authorization_info.class);
		Tf_operator_info.this.startActivity(it);
	}

	private void button5Click() {
		if(getCardStatus() != true){
/*				new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();*/
			Util.showConfirmDialog(Tf_operator_info.this,R.string.ca_insert_card);	
			return;
		}
		
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_tezhengzhi_info.class);
		Tf_operator_info.this.startActivity(it);
	}

	private void button6Click() {
		if(getCardStatus() != true){
/*				new AlertDialog.Builder(Tf_operator_info.this)
			.setMessage(R.string.ca_insert_card)
			.setPositiveButton("ok", null)
			.show();*/
			Util.showConfirmDialog(Tf_operator_info.this,R.string.ca_insert_card);	
			return;
		}
		
		Intent it = new Intent();
		it.putExtra("operids",value);
		it.setClass(Tf_operator_info.this, Tf_feed_card.class);
		Tf_operator_info.this.startActivity(it);
	}
}
