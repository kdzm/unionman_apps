package com.um.ui;

import java.util.Date;

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
import com.um.dvbstack.Status;
import com.um.irdca.R;

public class Ird_condition_access extends AppBaseActivity{
	final static String TAG = "Ird_condition_access";
	static int lastKeyCode = 0;
	static long lastKeyClickTime = 0L;
    Status status;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ird_condition_access);
            
        Button button1;
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Ird_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
					
				Intent it1 = new Intent(Ird_condition_access.this,Ird_smc_info.class);
				startActivity(it1);
			}
		});
/*
		Button button2;
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvn_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				Log.v("xing.luo", "before start entitle...");
				Intent it2 = new Intent(Dvn_condition_access.this,Dvn_entitle_info.class);
				startActivity(it2);
				Log.v("xing.luo", "after start entitle...");
			}
		});
        
        Button button3;
        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvn_condition_access.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
			        Log.v("xing.luo", "before start entitle...");

				Intent it3 = new Intent(Dvn_condition_access.this,Dvn_email.class);
				startActivity(it3);
			        Log.v("xing.luo", "after start entitle...");

			}
		}); 
		
        
	Button button4;
        button4= (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			         Log.v("xing.luo", "before get stb info...");

				Intent it4 = new Intent(Dvn_condition_access.this,Stb_version_info.class);
				startActivity(it4);
			        Log.v("xing.luo", "after get stb info...");

			}
		}); 
*/		
        DVB.GetInstance();
        status = Status.getInstance();
        //Status.getInstance().addStatusListener(this);
        //status.attachContext(this);
        Log.v("e.hong", "attach status to service");
	
    }

    @Override
    protected void onDestroy() {
        //status.detachContext();
        super.onDestroy();
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.GetInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Dvn_condition_access", "ret:" +ret);
    	Log.d("Dvn_condition_access", "cardStatus[0]:" +cardStatus[0]);
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
//				Intent it12 = new Intent(Dvn_condition_access.this,DvnHideMenu.class);
//				startActivity(it12);  
			}
        }
		
		lastKeyCode = keyCode;
		lastKeyClickTime = dt.getTime();

		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

}
