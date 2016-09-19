package com.um.ui;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.controller.ParamSave;
import com.um.dvbsettings.R;

public class Freset extends AppBaseActivity  {
	protected static final String TAG = "Freset";
	private   Button nextButton;
	private   EditText edit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_main_freq);
             
        nextButton = (Button)findViewById(R.id.button_set_freq);
        nextButton.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {       
        		
        	       buttonClick();
        	}		
        });
        edit = (EditText) this.findViewById(R.id.editTextSetMainFreqValue);
        edit.setText(String.valueOf(ParamSave.GetMainFreq()));
/*        edit.setOnFocusChangeListener(mFocusChangeListener);
        nextButton.setOnFocusChangeListener(mFocusChangeListener);*/

/*        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);*/
    }

 /*   View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            if(true == hasFocus)
            {
                int with = 0;
                int hight = 0;
                int[] location = new  int[2] ;
                ImageView focusFrame = (ImageView)findViewById(R.id.mainfre_focus);

                if(v.getId() == R.id.editTextSetMainFreqValue)
                {
                    LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutfreq);
                    with = layout.getWidth();
                    hight = layout.getHeight();
                    layout.getLocationOnScreen(location);
                }
                else if(v.getId() == R.id.button_set_freq)
                {
                    with = v.getWidth();
                    hight = v.getHeight();
                    v.getLocationOnScreen(location);
                }
                FocusAnimator focusAnimator = new FocusAnimator();
                focusAnimator.flyFoucsFrame(focusFrame, with, hight, location[0], location[1]);
            }
        }
    };*/
    

    private void SuccessDialog()
    {
  	  AlertDialog.Builder builder =new AlertDialog.Builder(Freset.this);
	      LayoutInflater factory = LayoutInflater.from(Freset.this);
	      View myView = factory.inflate(R.layout.notify_dialog,null);
	      TextView textView = (TextView) myView.findViewById(R.id.system_back_text);
	      textView.setText(R.string.save_success);
		   Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
	      final AlertDialog   mAlertDialog = builder.create();
	      mAlertDialog.show();
	      mAlertDialog.getWindow().setContentView(myView);
		  mSystemOKBtn.setOnClickListener(new OnClickListener() {
	
		          @Override
		          public void onClick(View arg0) {
		        	  mAlertDialog.dismiss();
						Freset.this.finish();
		          }
		      });
		  mAlertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				 if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
					 switch (keycode) {
					 case KeyEvent.KEYCODE_MENU:
			        	  mAlertDialog.dismiss();
						  Freset.this.finish();
						 break;
					 case KeyEvent.KEY_SOURCEENTER:	
			        	  mAlertDialog.dismiss();
						  Freset.this.finish();	
			    		 return true;	 
					 }
				 }
				return false;
			}
		});
 
/*    	AlertDialog builder = new AlertDialog.Builder(Freset.this).create();
    	
    	builder.setTitle(R.string.note); //����title
    	CharSequence message = getText(R.string.save_success);
    	builder.setMessage(message);
    	builder.getWindow();
    
    	CharSequence messageok = getText(R.string.ok);
    	builder.setButton(messageok, new DialogInterface.OnClickListener() {
			
//			@Override
			public void onClick(DialogInterface dialog, int which) {
				Freset.this.finish();
			}
		});

    	Window dialogWindow = builder.getWindow();
    	WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    	//builder.setView(DialogView);
    	builder.show();
    	dialogWindow.setGravity(Gravity.CENTER);
    	
    	lp.width = 400;
    	dialogWindow.setAttributes(lp); */  	
    }
	protected void FailDialog() {
	  	  AlertDialog.Builder builder =new AlertDialog.Builder(Freset.this);
	      LayoutInflater factory = LayoutInflater.from(Freset.this);
	      View myView = factory.inflate(R.layout.notify_dialog,null);
	      TextView textView = (TextView) myView.findViewById(R.id.system_back_text);
	      textView.setText(R.string.data_illegal);
		   Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
	      final AlertDialog   mAlertDialog = builder.create();
	      mAlertDialog.show();	
	      mAlertDialog.getWindow().setContentView(myView);
		  mSystemOKBtn.setOnClickListener(new OnClickListener() {
				
	          @Override
	          public void onClick(View arg0) {
	        	    mAlertDialog.dismiss();
					EditText text = (EditText) findViewById(R.id.editTextSetMainFreqValue);
					text.setText("");
					text.requestFocus();
	          }
	      });
		  mAlertDialog.setOnKeyListener(new OnKeyListener() {
				
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				 if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
					 switch (keycode) {
					 case KeyEvent.KEYCODE_MENU:
			        	    mAlertDialog.dismiss();
							EditText text = (EditText) findViewById(R.id.editTextSetMainFreqValue);
							text.setText("");
							text.requestFocus();
						 break;
					 case KeyEvent.KEY_SOURCEENTER:	
			        	    mAlertDialog.dismiss();
							EditText text2 = (EditText) findViewById(R.id.editTextSetMainFreqValue);
							text2.setText("");
							text2.requestFocus();
			    		 return true;	 
					 }
				 }
				return false;
			}
		}); 
   
/*    	AlertDialog builder = new AlertDialog.Builder(Freset.this).create();
    	
    	builder.setTitle(R.string.note); //����title
    	CharSequence message = getText(R.string.data_illegal);
    	builder.setMessage(message);
    	builder.getWindow();
    
    	CharSequence messageok = getText(R.string.ok);
    	builder.setButton(messageok, new DialogInterface.OnClickListener() {
			
//			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				EditText text = (EditText) findViewById(R.id.editTextSetMainFreqValue);
				text.setText("");
				text.requestFocus();
			}
		});

    	Window dialogWindow = builder.getWindow();
    	WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    	//builder.setView(DialogView);
    	builder.show();
    	dialogWindow.setGravity(Gravity.CENTER);
    	
    	lp.width = 400;
    	dialogWindow.setAttributes(lp);*/
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
			if(edit.isFocused()){
				 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);				 
			 }
    		if(nextButton.isFocused()){
    			   buttonClick();
    		}
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void buttonClick() {
		EditText et = (EditText) findViewById(R.id.editTextSetMainFreqValue);
		   et.setInputType(InputType.TYPE_CLASS_NUMBER);

		   String s = et.getText().toString();
		   
		   int fre = 0;
		   if("".equals(s.trim()))
			   ;
		   else
			   fre = Integer.parseInt(s);
		   
		   if((fre<ParamSave.MIN_MAIN_FREQ)||(fre>ParamSave.MAX_MAIN_FREQ))
		   {      			   
			   FailDialog();      			   
		   }
		   else
		   {
			   ParamSave.SaveMainFreq(fre);
			   SuccessDialog();
		   }
	}

	
}
