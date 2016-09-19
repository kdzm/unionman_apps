package com.um.ui;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.controller.ParamSave;
import com.um.dvbsettings.R;
public class ParentLock extends AppBaseActivity {
	protected static final String TAG = "ParentLock";
	private EditText ed1;
	private EditText ed2;
	private EditText ed3;
    private Button btn_ok;
    private Button btn_canncel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_lock);

        btn_ok = (Button)findViewById(R.id.passwdCommit);
        btn_canncel = (Button)findViewById(R.id.passwdCancel);

    	ed1 = (EditText)findViewById(R.id.passwdEditTextOld);
    	ed2 = (EditText)findViewById(R.id.passwdEditTextNew);
    	ed3 = (EditText)findViewById(R.id.passwdEditTextComfirm);

        /*设置确定键使能文字监听器*/
        ed1.addTextChangedListener(mTextWatcher);
        ed2.addTextChangedListener(mTextWatcher);
        ed3.addTextChangedListener(mTextWatcher);
/*        ed1.setOnFocusChangeListener(pwdCheckFocusChange);
        ed2.setOnFocusChangeListener(pwdCheckFocusChange);
        ed3.setOnFocusChangeListener(pwdCheckFocusChange);
        btn_ok.setOnFocusChangeListener(pwdCheckFocusChange);
        btn_canncel.setOnFocusChangeListener(pwdCheckFocusChange);*/

        Drawable itemBg = getResources().getDrawable(R.drawable.dvbsetting_item_grey);
/*        findViewById(R.id.linearLayout3).setBackground(itemBg);
        findViewById(R.id.linearLayout4).setBackground(itemBg);*/
//        ed1.setNextFocusDownId(R.id.passwdCancel);
        btn_canncel.setNextFocusUpId(R.id.passwdEditTextOld);

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
			public void onClick(View v)
            {
                buttonOKClick();
			}
		});

        btn_canncel.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ParentLock.this.finish();
            }
        });

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }

    /*确定键使能监听器*/
    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            //Don't need do anything for now.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if(ed1.getText().length() == 6)
            {
                String oldPassword = ParamSave.GetParentPasswd(ParentLock.this);
                if(ed1.getText().toString().compareTo(oldPassword) == 0)
                {
                    ed1.setNextFocusDownId(R.id.passwdEditTextNew);
                    btn_ok.setNextFocusUpId(R.id.passwdEditTextComfirm);
                    btn_canncel.setNextFocusUpId(R.id.passwdEditTextComfirm);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if ((null == ed1.getText()) || (null == ed2.getText()) || (null == ed3.getText()))
            {
//                btn_ok.setEnabled(false);
//                btn_ok.setFocusable(false);
            }
            if ((ed1.getText().length() != 6) || (ed2.getText().length() != 6) || (ed3.getText().length() != 6))
            {
//                btn_ok.setEnabled(false);
//                btn_ok.setFocusable(false);
            }
            else
            {
//                btn_ok.setEnabled(true);
//                btn_ok.setFocusable(true);
            }
        }
    };

    private void tips(String s)
    {
    	Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

/*    View.OnFocusChangeListener pwdCheckFocusChange = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            View view = null;
            int id = 0;

            String oldPassword = ParamSave.GetParentPasswd(ParentLock.this);

            switch (v.getId())
            {
                case R.id.passwdEditTextOld:
                    if(false == hasFocus)
                    {
                        if(ed1.getText().toString().compareTo(oldPassword)!= 0)
                        {
                            tips(getResources().getString(R.string.password_incorrect) + "!");
                            ed1.setText("");
                            ed1.setNextFocusDownId(R.id.passwdCancel);
                        }
                        else
                        {
                            Drawable itemBg = getResources().getDrawable(R.drawable.dvbsetting_item_sh);
                            findViewById(R.id.linearLayout3).setBackground(itemBg);
                            findViewById(R.id.linearLayout4).setBackground(itemBg);
                            ed1.setNextFocusDownId(R.id.passwdEditTextNew);
                            btn_ok.setNextFocusUpId(R.id.passwdEditTextComfirm);
                            btn_canncel.setNextFocusUpId(R.id.passwdEditTextComfirm);
                        }
                    }
                    id = R.id.layoutpwd1;
                    break;
                case R.id.passwdEditTextNew:
                    id = R.id.layoutpwdnew;
                    break;
                case R.id.passwdEditTextComfirm:
                    id = R.id.layoutcomfirm;
                    break;
                case R.id.passwdCommit:
                    id = R.id.pwd_sure;

                    if((ed1.getText().toString().compareTo(oldPassword)!= 0)&&(false == hasFocus))
                        findViewById(R.id.passwdCommit).setNextFocusUpId(R.id.passwdEditTextOld);
                    break;
                case R.id.passwdCancel:
                    id = R.id.pwd_cancel;

                    if((ed1.getText().toString().compareTo(oldPassword)!= 0)&&(false == hasFocus))
                        findViewById(R.id.passwdCancel).setNextFocusUpId(R.id.passwdEditTextOld);
                    break;
                default:
                    id = 0;
                    break;
            }

            view = findViewById(id);
            if(null != view)
            {
                int[] location = new  int[2] ;
                if(true == hasFocus)
                {
                    view.getLocationOnScreen(location);
                    ImageView focusFrame = (ImageView)findViewById(R.id.pwd_focus);
                    FocusAnimator focusAnimator = new FocusAnimator();
                    focusAnimator.flyFoucsFrame(focusFrame, view.getWidth(), view.getHeight(), location[0], location[1]);
                }
            }
        }
    };*/
    
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
			if(ed1.isFocused()||ed2.isFocused()||ed3.isFocused()){
				 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);	
			}

    		if(btn_ok.isFocused()){
    			buttonOKClick();
    		}
    		if(btn_canncel.isFocused()){
    			finish();
    		}
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void buttonOKClick() {
		try
		{
		    String oldPassword = ParamSave.GetParentPasswd(ParentLock.this);
		    if((null == ed1.getText()) || (ed1.getText().toString().compareTo(oldPassword)!= 0))
		    {
		        tips(getResources().getString(R.string.password_incorrect) + "!");
		        ed1.setText("");
		        ed1.requestFocus();
		        return;
		    }

		    if((null == ed2.getText()) || (null == ed3.getText())||(ed2.getText().length() != 6))
		    {
		        tips(getResources().getString(R.string.input_new_pwd) + "!");
		        return;
		    }

		    String newPassword = ed3.getText().toString();
		    if(newPassword.compareTo(ed2.getText().toString())!= 0)
		    {
		        tips(getResources().getString(R.string.password_unmatch) + "!");
		        ed2.setText("");
		        ed3.setText("");
		        ed2.requestFocus();
		        return;
		    }

		    ParamSave.SaveParentPasswd(ParentLock.this, ed2.getText().toString());
		    tips(getResources().getString(R.string.modify_ok) + "!");
		}catch (Exception e)
		{
		    Toast.makeText(ParentLock.this,e.getMessage(),Toast.LENGTH_SHORT).show();
		    return;
		}

		ParentLock.this.finish();
	}
}
