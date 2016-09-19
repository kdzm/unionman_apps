package com.um.umpwdlock;


import com.um.umpwdlock.util.Constant;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PwdLockMainActivity extends Activity implements OnClickListener {

	private static final String TAG = "PwdLockMainActivity";
	private EditText edit_pwd_old;
	private EditText edit_pwd_new;
	private EditText edit_pwd_new_comfirm;
    private Button btn_reset_pwd;
    private Button btn_cancel;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_lock);
		btn_reset_pwd = (Button) findViewById(R.id.btn_reset_pwd);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
    	edit_pwd_old = (EditText)findViewById(R.id.passwdEditTextOld);
    	edit_pwd_new = (EditText)findViewById(R.id.passwdEditTextNew);
    	edit_pwd_new_comfirm = (EditText)findViewById(R.id.passwdEditTextComfirm);
		
		
		btn_reset_pwd.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		String deafaultPwd = Settings.Secure.getString(getContentResolver(), Constant.UMDefaultPwd);
		String superPwd = Settings.Secure.getString(getContentResolver(), Constant.UMSuperPwd);
		
		Log.i(TAG,"onCreate() deafaultPwd="+deafaultPwd+" superPwd="+superPwd);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pwd_lock_main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_reset_pwd:
			buttonOKClick();
			break;
		case R.id.btn_cancel:
            finish();
			break;
		}
	}

	private void buttonOKClick() {
		try
		{
			String savePwd = Settings.Secure.getString(getContentResolver(), Constant.RestorePwd);
			String pwd_input_old =edit_pwd_old.getText().toString();
		    String pwd_new = edit_pwd_new.getText().toString();
		    String pwd_new_comfirm = edit_pwd_new_comfirm.getText().toString();
		    
            String defaultPwd =Settings.Secure.getString(getContentResolver(), Constant.UMDefaultPwd);
            if(defaultPwd==null){
           	 defaultPwd = Constant.UMDefaultPwdValue;
           	 Log.i(TAG," defaultPwd==null; set defaultPwd ="+Constant.UMDefaultPwdValue);
            }
          String superPwd = Settings.Secure.getString(getContentResolver(), Constant.UMSuperPwd);
           if(superPwd==null){
       	     superPwd = Constant.UMSuperPwdValue;
           	 Log.i(TAG," superPwd==null; set superPwd ="+ Constant.UMSuperPwdValue);    
           } 
		    
			if(savePwd==null){
				savePwd = defaultPwd;
				 Log.i(TAG,"savePwd is null,set savePwt to defaultPwd");
			}
			 Log.i(TAG,"savePwd="+savePwd);
			 
			 //start;  if pwd_input_old does not equals SuperPwd , deal with illegal state for it 
		    if(pwd_input_old.equals(superPwd)){
                 Log.i(TAG,"input DeafaultPwd="+pwd_input_old);
		    }		    
		    else if((null == pwd_input_old) || (pwd_input_old.compareTo(savePwd)!= 0) )
		    {
		        tips(getResources().getString(R.string.password_incorrect) + "!");
		        edit_pwd_old.setText("");
		        edit_pwd_old.requestFocus();
		        return;
		    }
		    //end;  if pwd_input_old does not equals SuperPwd , deal with illegal state for it 
		    
		    if((null == pwd_new) || (null == pwd_new_comfirm)||(pwd_new_comfirm.length() != 6))
		    {
		        tips(getResources().getString(R.string.input_new_pwd) + "!");
		        edit_pwd_new.setText("");
		        edit_pwd_new_comfirm.setText("");
		        return;
		    }


		    if(pwd_new_comfirm.compareTo(pwd_new)!= 0)
		    {
		        tips(getResources().getString(R.string.password_unmatch) + "!");
		        edit_pwd_new.setText("");
		        edit_pwd_new_comfirm.setText("");
		        edit_pwd_new.requestFocus();
		        return;
		    }
		    
		    Settings.Secure.putString(getContentResolver(), Constant.RestorePwd, pwd_new_comfirm);
		    tips(getResources().getString(R.string.modify_ok) + "!");
		    Log.i(TAG,"newPassword="+pwd_new_comfirm);
		}catch (Exception e)
		{
		    Toast.makeText(PwdLockMainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
		    return;
		}

	}
	
    private void tips(String s)
    {
    	Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
