package com.um.filemanager.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.Button;

public class NewCreateDialog extends AlertDialog {
	private Button ok_button;
	private Button cancel_button;
	public NewCreateDialog(Context context) {
        super(context);
    }
    public NewCreateDialog(Context context,Button ok,Button cancel) {
        super(context);
        ok_button=ok;
        cancel_button=cancel;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(ok_button == null){
    		switch (keyCode) {
            
            case KeyEvent.KEYCODE_DPAD_LEFT:
                getButton(BUTTON_POSITIVE).requestFocus();
            	
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                getButton(BUTTON_NEGATIVE).requestFocus();
            	
                return true;

            default:
                break;
        }
    	}else{
			switch (keyCode) {
			            
			            case KeyEvent.KEYCODE_DPAD_LEFT:
			              
			            	ok_button.requestFocus();
			                return true;
			
			            case KeyEvent.KEYCODE_DPAD_RIGHT:
			            
			            	cancel_button.requestFocus();
			                return true;
			
			            default:
			                break;
			        }
    	}
        

        return super.onKeyDown(keyCode, event);
    }

}
