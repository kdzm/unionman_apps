package com.um.ui;

import com.um.tfca.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Util {
/**
 * 鏄剧ず纭瀵硅瘽妗� * @param context 
 * @param textId 鎻愰啋淇℃伅璧勬簮id
 */
 public static void showConfirmDialog(Context context,int textId){
 	  AlertDialog.Builder builder =new AlertDialog.Builder(context);
      LayoutInflater factory = LayoutInflater.from(context);
      View myView = factory.inflate(R.layout.notify_dialog,null);
      TextView textView = (TextView) myView.findViewById(R.id.system_back_text);
      textView.setText(textId);
	   final Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
      final AlertDialog   mAlertDialog = builder.create();
      mAlertDialog.show();	
      mAlertDialog.getWindow().setContentView(myView);
	  mSystemOKBtn.setOnClickListener(new OnClickListener() {

	          @Override
	          public void onClick(View arg0) {
	        	  mAlertDialog.dismiss();
	          }
	      });	
    	mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				// TODO Auto-generated method stub
           if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
	               switch (keycode) {
					  case KeyEvent.KEYCODE_MENU:
				    		 mAlertDialog.dismiss();
							break; 
					  case 	KeyEvent.KEY_SOURCEENTER:
						  if(mSystemOKBtn.isFocused()){
				              mAlertDialog.dismiss();						  
						  }
						  return true;
				}
           }
				return false;
			}
		});

 }

}
