package com.um.ui;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Ca.Ca_Rating;
import com.um.controller.AppBaseActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Tf_watch_level extends AppBaseActivity{
	private static final String TAG = "Tf_watch_level";
	private Button enter_btn;
	private Button back_btn;
	private EditText rate;
	private EditText pin;
	
	@Override
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_watch_level);
		
		Ca ca = new Ca(DVB.GetInstance());
		Ca_Rating ca_rating = new Ca_Rating();
		ca.CaGetRating(ca_rating);
		Log.i("Tf_watch_level","watch rating:"+String.valueOf(ca_rating.carating));
		
	    rate = (EditText)findViewById(R.id.editText2);
        rate.requestFocus();
	    rate.setText(String.valueOf(ca_rating.carating[0]));
	    rate.setSelection(0, rate.getText().toString().length());
	    
		pin = (EditText)findViewById(R.id.editText1);
		pin.setText(pin.getText().toString());
		pin.setSelection(pin.getText().toString().length());
		
		/*button*/
		enter_btn = (Button)findViewById(R.id.button1);
		
		enter_btn.setText(R.string.ok);
		
		enter_btn.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View arg0) {
				
				enterBtnClick();
			}
			}
		);

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
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
			 Log.i(TAG, "KEY_SOURCEENTER is click");
				if(rate.isFocused()||pin.isFocused()){
					 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);				 
				 }
			if(enter_btn.isFocused()){
				 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
					enterBtnClick();
			 }

    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void enterBtnClick() {
		Ca ca = new Ca(DVB.GetInstance());
		
		if(String.valueOf(rate.getText()).length() == 0){
/*					new AlertDialog.Builder(Tf_watch_level.this)
			.setMessage(R.string.watch_level_4_18)
			.setPositiveButton("ok", null)
			.show();*/
		    Toast.makeText(Tf_watch_level.this, getResources().getText(R.string.watch_level_4_18), Toast.LENGTH_LONG).show();
//				  Util.showConfirmDialog(Tf_watch_level.this,R.string.watch_level_4_18);
			return;
		}
		
		int rate_value = Integer.parseInt(String.valueOf(rate.getText()));
		Log.i("CA","CA rate_value:"+rate_value);
		if((rate_value < 4) || (rate_value > 18))
		{
/*					new AlertDialog.Builder(Tf_watch_level.this)
			.setMessage(R.string.watch_level_4_18)
			.setPositiveButton("ok", null)
			.show();*/
		    Toast.makeText(Tf_watch_level.this, getResources().getText(R.string.watch_level_4_18), Toast.LENGTH_LONG).show();
//					Util.showConfirmDialog(Tf_watch_level.this,R.string.watch_level_4_18);
			return;
		}	
		
		String str = String.valueOf(pin.getText());
		byte[] temp1 = str.getBytes();	
		Log.i("CA","CA str:"+str);	
		
		if(temp1.length != 6){
/*					new AlertDialog.Builder(Tf_watch_level.this)
			.setMessage(R.string.tf_pin_err)
			.setPositiveButton("ok", null)
			.show();*/
		    Toast.makeText(Tf_watch_level.this, getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
//					Util.showConfirmDialog(Tf_watch_level.this,R.string.tf_pin_err);
			return;
		}

		// TODO Auto-generated method stub
		
		if(ca.CaSetRate(rate_value,temp1,6) != 0)
		{
			Log.i("CA","CA CaSetRate:"+6);	
/*					new AlertDialog.Builder(Tf_watch_level.this)
			.setTitle(R.string.tf_change_watch_level_notice)
			.setMessage(R.string.tf_pin_err)
			.setPositiveButton("ok", null)
			.show();*/
//					Util.showConfirmDialog(Tf_watch_level.this,R.string.tf_pin_err);
		    Toast.makeText(Tf_watch_level.this, getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
			Log.i("CA","CA CaSetRate end:"+6);	
		}
		else
		{
/*					new AlertDialog.Builder(Tf_watch_level.this)
			.setMessage(R.string.tf_change_watch_level_right)
			.setPositiveButton("ok", null)
			.show();*/
		    Toast.makeText(Tf_watch_level.this, getResources().getText(R.string.tf_change_watch_level_right), Toast.LENGTH_LONG).show();
//					Util.showConfirmDialog(Tf_watch_level.this,R.string.tf_change_watch_level_right);
		}
	}
}
