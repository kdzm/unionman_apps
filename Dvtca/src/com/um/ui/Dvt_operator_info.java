package com.um.ui;

import com.um.dvtca.R;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.um.controller.AppBaseActivity;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

public class Dvt_operator_info extends AppBaseActivity{
	
	private String value; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dvt_operator_info);

        /*Get operator ID from Operator information list*/
        Intent intent = getIntent();
        value= intent.getStringExtra("operid");
        //int oper_id = Integer.parseInt(value);
        Button button1;
        button1 = (Button)findViewById(R.id.operator_btn1);
        button1.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
        	if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_operator_info.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	Intent it = new Intent();
        	it.putExtra("operids",value);
        	it.setClass(Dvt_operator_info.this, Dvt_entitle_info.class);
        	Dvt_operator_info.this.startActivity(it);
        }
        });
        
        Button button2;
        button2 = (Button)findViewById(R.id.operator_btn2);
        button2.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_operator_info.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	
        	Intent it = new Intent();
        	it.putExtra("operids",value);
        	it.setClass(Dvt_operator_info.this, Dvt_ipp_buy_info.class);
        	Dvt_operator_info.this.startActivity(it);
        }
        });
        
        Button button3;
        button3 = (Button)findViewById(R.id.operator_btn3);
        button3.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_operator_info.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	
        	Intent it = new Intent();
        	it.putExtra("operids",value);
        	it.setClass(Dvt_operator_info.this, Dvt_walletinfo.class);
        	Dvt_operator_info.this.startActivity(it);
        }
        });
        
        Button button6;
        button6 = (Button)findViewById(R.id.operator_btn6);
        button6.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	if(getCardStatus() != true){
				new AlertDialog.Builder(Dvt_operator_info.this)
				.setMessage(R.string.ca_insert_card)
				.setPositiveButton("ok", null)
				.show();
				return;
			}
        	
        	Intent it = new Intent();
        	it.putExtra("operids",value);
        	it.setClass(Dvt_operator_info.this, Dvt_feed_card.class);
        	Dvt_operator_info.this.startActivity(it);
        }
        }); 
    }
    
	@Override
	protected void onPause(){
        super.onPause();
        finish();
	}
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Dvt_operator_info", "ret:" +ret);
    	Log.d("Dvt_operator_info", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
}
