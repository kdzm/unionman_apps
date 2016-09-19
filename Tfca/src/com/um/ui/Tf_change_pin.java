package com.um.ui;

import java.util.Arrays;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.um.controller.AppBaseActivity;
public class Tf_change_pin extends AppBaseActivity{
	private static final String TAG = "Tf_change_pin";
	private Button enter_btn;
	private Button back_btn;
	private EditText present_pin;
	private EditText new_pin;
	private EditText pin_verify;
	
	@Override
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_change_pin);

		
		/*Pin EditText*/
		present_pin = (EditText)findViewById(R.id.editText1);
		present_pin.setText(present_pin.getText().toString());
		present_pin.setSelection(present_pin.getText().toString().length());
        present_pin.requestFocus();
		
		new_pin = (EditText)findViewById(R.id.editText2);
		new_pin.setText(new_pin.getText().toString());
		new_pin.setSelection(new_pin.getText().toString().length());
		
		pin_verify = (EditText)findViewById(R.id.editText3);
		pin_verify.setText(pin_verify.getText().toString());
		pin_verify.setSelection(pin_verify.getText().toString().length());
		
		/*affirm button*/
		enter_btn = (Button)findViewById(R.id.button1);		
		
		enter_btn.setText(R.string.confirm_ok);		
		
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
	    	 Log.i(TAG,"KEYCODE_MENU is clicked");
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
				if(present_pin.isFocused()||new_pin.isFocused()||pin_verify.isFocused()){
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
		// TODO Auto-generated method stub
		
		String newpinstr = String.valueOf(new_pin.getText());
		String oldpinstr = String.valueOf(present_pin.getText());
		String verifypinstr = String.valueOf(pin_verify.getText());
		
		byte[] newpintmp = newpinstr.getBytes();	
		byte[] oldpintmp = oldpinstr.getBytes();
		byte[] verifypintmp = verifypinstr.getBytes();
		
		 if((newpintmp.length != 6)||(oldpintmp.length != 6)||(verifypintmp.length != 6))
		 {
//					 new AlertDialog.Builder(Tf_change_pin.this).setMessage(R.string.tf_pin_err).setPositiveButton("ok", null).show();	
//					 Util.showConfirmDialog(Tf_change_pin.this,R.string.tf_pin_err);
		     Toast.makeText(Tf_change_pin.this, getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
			 return;
		 }
		
		boolean same_flag = Arrays.equals(newpintmp, verifypintmp);	

		if(same_flag == false)//0x8000000d
		{
			Log.i("CA","CA new pin dismatch");	
//					new AlertDialog.Builder(Tf_change_pin.this).setMessage(R.string.tf_pin_not_paired).setPositiveButton("ok", null).show();
//					 Util.showConfirmDialog(Tf_change_pin.this,R.string.tf_pin_not_paired);
		    Toast.makeText(Tf_change_pin.this, getResources().getText(R.string.tf_pin_not_paired), Toast.LENGTH_LONG).show();
		    return;
		}
		else
		{
			Log.i("CA","CA new pin match");	
			if(ca.CaChangePin(newpintmp,oldpintmp,6)==0)
			{
				Log.i("CA","CA pin change success");	
//						new AlertDialog.Builder(Tf_change_pin.this).setMessage(R.string.tf_change_pin_correctly).setPositiveButton("ok", null).show();
//						 Util.showConfirmDialog(Tf_change_pin.this,R.string.tf_change_pin_correctly);
		        Toast.makeText(Tf_change_pin.this, getResources().getText(R.string.tf_change_pin_correctly), Toast.LENGTH_LONG).show();
			}
			else
			{
				Log.i("CA","CA old pin dismatch");	
//						new AlertDialog.Builder(Tf_change_pin.this).setMessage(R.string.tf_pin_err).setPositiveButton("ok", null).show();
//						 Util.showConfirmDialog(Tf_change_pin.this,R.string.tf_pin_err);
		        Toast.makeText(Tf_change_pin.this, getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
			}
		}
	}
	
}

