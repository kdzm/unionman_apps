package com.um.ui;

import java.util.ArrayList;
import java.util.List;

import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;
import com.um.dvbsearch.R;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MenualSearch extends AppBaseActivity {
	protected static final String TAG = "MenualSearch";
	private Spinner qam_spinner;
	private EditText fre_ed;
	private EditText smbl_ed;
	private Button btn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menual_search);
        
        qam_spinner = (Spinner)findViewById(R.id.menual_search_edit_mod);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
//        	      this, R.array.qam_array, R.layout.simple_spinner_item); 
        List<CharSequence> spinnerVal = new ArrayList<CharSequence>();
        spinnerVal.add("16");
        spinnerVal.add("32");
        spinnerVal.add("64");
        spinnerVal.add("128");
        spinnerVal.add("256");

        ArrayAdapter<CharSequence> adapter =  
        		new ArrayAdapter<CharSequence>(MenualSearch.this, R.layout.simple_spinner_item,spinnerVal ){
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
        qam_spinner.setSelection(2);    
        
        fre_ed = (EditText)findViewById(R.id.menual_search_edit_frq);
        smbl_ed= (EditText)findViewById(R.id.menual_search_edit_sym);
        
        fre_ed.setText("227000");
        smbl_ed.setText("6875");
        
        fre_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            	int freqVal;
                if(hasFocus){//如果组件获得焦点
                }else{

                	if(0 != Integer.parseInt(fre_ed.getText().toString())%10){
                		Toast.makeText(MenualSearch.this, getResources().getString(R.string.lastNumMayNotBeNonzero), 1).show();      	
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
			Toast.makeText(MenualSearch.this, getResources().getString(R.string.inputNotEmpty), 1).show();
			return ;
		}else if(smbl_ed.getText().toString().equals("")){
			smbl_ed.requestFocus();
			Toast.makeText(MenualSearch.this, getResources().getString(R.string.inputNotEmpty), 1).show();
			return ;					
		}
		int fre;
		int symbl;
		int qam;
		int type = 1;
		fre = Integer.parseInt(fre_ed.getText().toString());
		symbl = Integer.parseInt(smbl_ed.getText().toString());
		qam = qam_spinner.getSelectedItemPosition()+1;
		Log.i("MenualSearch",""+fre +symbl +qam);
		
		Intent it = new Intent(MenualSearch.this, Search.class);
		Bundle bundle = new Bundle();                          
		bundle.putInt("type", type);
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
				if(fre_ed.isFocused()||smbl_ed.isFocused()){
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
