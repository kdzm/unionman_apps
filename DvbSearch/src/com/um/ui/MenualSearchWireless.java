package com.um.ui;

import java.util.ArrayList;
import java.util.List;

import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;
import com.um.dvbsearch.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MenualSearchWireless extends AppBaseActivity {
	protected static final String TAG = "MenualSearchWireless";
	private Spinner qam_spinner;
	private EditText fre_ed;
	private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menual_search_wireless);
        
        qam_spinner = (Spinner)findViewById(R.id.menual_search_edit_mod);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
//        	      this, R.array.daikuan_array, R.layout.simple_spinner_item); 
        List<CharSequence> spinnerVal = new ArrayList<CharSequence>();
        spinnerVal.add("6M");
        spinnerVal.add("8M");
        ArrayAdapter<CharSequence> adapter =  
        		new ArrayAdapter<CharSequence>(MenualSearchWireless.this, R.layout.simple_spinner_item,spinnerVal ){
        	   @Override
               public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
        		   parent.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						// TODO Auto-generated method stub
						   if(arg2.getAction() == KeyEvent.ACTION_DOWN){
							   Log.i(TAG,"keycode="+arg1);
							   switch (arg1) {
							     case KeyEvent.KEYCODE_MENU:
							    	 Log.i(TAG, "parent KEYCODE_MENU is click");
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
									 Log.i(TAG, "parent KEY_SOURCEENTER is click");
							    	  new Thread() {
							    		   public void run() {
							    		    try {
							    		     Instrumentation inst = new Instrumentation();
							    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
							    		    } catch (Exception e) {
							    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
							    		    }
							    		   }
							    		  }.start();
						    		 return true;
								}
						   }
						return false;
					}
				});
                   return super.getDropDownView(position, convertView, parent);
               }
       	
        };

        adapter.setDropDownViewResource(R.layout.simple_spinner_item);
        qam_spinner.setAdapter(adapter);
        qam_spinner.setSelection(1);         
  
        
        fre_ed = (EditText)findViewById(R.id.menual_search_edit_frq);
 
        
        fre_ed.setText("52500");

        fre_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            	int freqVal;
                if(hasFocus){//锟斤拷锟斤拷锟斤拷锟斤拷媒锟斤拷锟�
                }else{
	                	if(0 != Integer.parseInt(fre_ed.getText().toString())%10){
	                		Toast.makeText(MenualSearchWireless.this, getResources().getString(R.string.lastNumMayNotBeNonzero), 1).show();      	
		                	freqVal = Integer.parseInt(fre_ed.getText().toString());
		                	freqVal = freqVal/10*10;
		                	fre_ed.setText(Integer.toString(freqVal));
	                	}

                }
            }
        });
        
        btn = (Button)findViewById(R.id.menual_search_button);
        
        btn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				btnClick();
			
			}
		});
        
    }
	private void btnClick() {
		if(fre_ed.getText().toString().equals("")){
			fre_ed.requestFocus();
			Toast.makeText(MenualSearchWireless.this, getResources().getString(R.string.inputNotEmpty), 1).show();
			return ;
		}

		int fre = 506;
		int symbl = 6875;
		int qam = 3;
		int type = 1;
		int band = 8;
		int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		int val = Integer.parseInt(fre_ed.getText().toString());
		fre = Integer.parseInt(fre_ed.getText().toString());
		band = Integer.parseInt(qam_spinner.getSelectedItem().toString().substring(0, 1));
		Log.i(TAG,"fre="+fre+" band="+band);
		Intent it = new Intent(MenualSearchWireless.this, Search.class);
		Bundle bundle = new Bundle();                          
		bundle.putInt("type", type);
		bundle.putInt("tunertype", tunerType);
		bundle.putInt("band", band);
		bundle.putInt("fre", fre);
		bundle.putInt("sym", symbl);
		bundle.putInt("qam", qam);
		it.putExtras(bundle);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			it.putExtras(extras); 
		}
		startActivity(it);
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
				if(fre_ed.isFocused()){
					 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);				 
				 }
    		if(btn.isFocused()){
    			btnClick();
    		}
    		if(qam_spinner.isFocused()){
    	    	  new Thread() {
  	    		   public void run() {
  	    		    try {
  	    		     Instrumentation inst = new Instrumentation();
  	    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
  	    		    } catch (Exception e) {
  	    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
  	    		    }
  	    		   }
  	    		  }.start();   			
      		}
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
