package cn.com.unionman.umtvsetting.system.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.unionman.umtvsetting.system.R;

public class IpAddrEdit extends LinearLayout   {
    private static final String TAG = "IpAddrEdit";
    private EditText mEtIpaddr1 = null;
    private EditText mEtIpaddr2 = null;
    private EditText mEtIpaddr3 = null;
    private EditText mEtIpaddr4 = null;
    private TextView mTitle;

    public IpAddrEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(
                R.layout.pub_ipaddr_edit, this, true);

        mTitle = (TextView) view.findViewById(R.id.textView1);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.IpAddrEdit);

        String text = typeArray.getString(R.styleable.IpAddrEdit_text);
        if (text != null) {
            mTitle.setText(text);
        }

        mEtIpaddr1 = (LineEditText) view.findViewById(R.id.et_ipaddr1);
        mEtIpaddr2 = (LineEditText) view.findViewById(R.id.et_ipaddr2);
        mEtIpaddr3 = (LineEditText) view.findViewById(R.id.et_ipaddr3);
        mEtIpaddr4 = (LineEditText) view.findViewById(R.id.et_ipaddr4); 
        
        mEtIpaddr1.setInputType(InputType.TYPE_NULL);
        mEtIpaddr2.setInputType(InputType.TYPE_NULL);
        mEtIpaddr3.setInputType(InputType.TYPE_NULL);
        mEtIpaddr4.setInputType(InputType.TYPE_NULL);
        
        mEtIpaddr1.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {
		        		switch (keycode) {
		        		case KeyEvent.KEYCODE_DPAD_CENTER:
		        			mEtIpaddr1.setInputType(InputType.TYPE_CLASS_NUMBER);
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_LEFT:
		        			mEtIpaddr4.requestFocus();
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_RIGHT:
		        			mEtIpaddr2.requestFocus();
		        			break;
		        		}
				 }
				return false;
			}
		});
        mEtIpaddr2.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {
		        		switch (keycode) {
		        		case KeyEvent.KEYCODE_DPAD_CENTER:
		        			mEtIpaddr2.setInputType(InputType.TYPE_CLASS_NUMBER);
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_LEFT:
		        			mEtIpaddr1.requestFocus();
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_RIGHT:
		        			mEtIpaddr3.requestFocus();
		        			break;
		        		}
				 }
				return false;
			}
		});
        mEtIpaddr3.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {
		        		switch (keycode) {
		        		case KeyEvent.KEYCODE_DPAD_CENTER:
		        			mEtIpaddr3.setInputType(InputType.TYPE_CLASS_NUMBER);
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_LEFT:
		        			mEtIpaddr2.requestFocus();
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_RIGHT:
		        			mEtIpaddr4.requestFocus();
		        			break;
		        		}
				 }
				return false;
			}
		});
        mEtIpaddr4.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {
		        		switch (keycode) {
		        		case KeyEvent.KEYCODE_DPAD_CENTER:
		        			mEtIpaddr4.setInputType(InputType.TYPE_CLASS_NUMBER);
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_LEFT:
		        			mEtIpaddr3.requestFocus();
		        			break;
		        		case KeyEvent.KEYCODE_DPAD_RIGHT:
		        			mEtIpaddr1.requestFocus();
		        			break;
		        		}
				 }
				return false;
			}
		});
        
        mEtIpaddr1.addTextChangedListener(watcher);
        mEtIpaddr2.addTextChangedListener(watcher);
        mEtIpaddr3.addTextChangedListener(watcher);
        mEtIpaddr4.addTextChangedListener(watcher);
        mEtIpaddr1.setOnFocusChangeListener(mOnFocusChangeListener);
        mEtIpaddr2.setOnFocusChangeListener(mOnFocusChangeListener);
        mEtIpaddr3.setOnFocusChangeListener(mOnFocusChangeListener);
        mEtIpaddr4.setOnFocusChangeListener(mOnFocusChangeListener);
        mEtIpaddr1.requestFocus();
        setTextInternal("0.0.0.0");

        typeArray.recycle();
    }

    private EditText getFocusedEditText() {
        if (mEtIpaddr1.isFocused()) {
            return mEtIpaddr1;
        }

        if (mEtIpaddr2.isFocused()) {
            return mEtIpaddr2;
        }

        if (mEtIpaddr3.isFocused()) {
            return mEtIpaddr3;
        }

        if (mEtIpaddr4.isFocused()) {
            return mEtIpaddr4;
        }

        return null;
    }

    private EditText getNextFocusEditText(EditText cur) {
        if (cur == mEtIpaddr1)
            return mEtIpaddr2;
        else if (cur == mEtIpaddr2)
            return mEtIpaddr3;
        else if (cur == mEtIpaddr3)
            return mEtIpaddr4;
        else if (cur == mEtIpaddr4)
            return mEtIpaddr1;
        else
            return null;
    }

    private void autoChangeFocus() {
        EditText nextEt = null;
        EditText curEt = getFocusedEditText();

        if (curEt == null) {
            return;
        }

        String str = curEt.getText().toString();
        if (str.length() >= 3) {
            if (Integer.parseInt(str) > 255) {
                curEt.setText("255");
            }

            nextEt = getNextFocusEditText(curEt);
            nextEt.requestFocus();
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
            autoChangeFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

    };

    private void setTextInternal(String ipaddr) {
        int ipaddrInt[] = new int[4];
        String[] arrayOfString = ipaddr.split("\\.");
        if (arrayOfString.length != 4) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            try {
                ipaddrInt[i] = Integer.parseInt(arrayOfString[i]);
                if (ipaddrInt[i] > 255) {
                    return;
                }
            } catch (Exception localException) {
                return;
            }
        }
        mEtIpaddr1.setText(String.valueOf(ipaddrInt[0]));
        mEtIpaddr2.setText(String.valueOf(ipaddrInt[1]));
        mEtIpaddr3.setText(String.valueOf(ipaddrInt[2]));
        mEtIpaddr4.setText(String.valueOf(ipaddrInt[3]));


    }

    public void setText(String ipaddr) {
        if (ipaddr == null || ipaddr.equals("")) {
            setTextInternal("0.0.0.0");
            return;
        }

        setTextInternal(ipaddr);
    }

    private String getNumber(EditText et) {
        String result = "0";
        try {
            int i = Integer.parseInt(et.getText().toString());
            result = i + "";
        } catch (Exception localException) {
            result = "0";
        }

        return result;
    }

    public String getText() {
        StringBuffer buf = new StringBuffer(32);
        buf.append(getNumber(mEtIpaddr1)).append(".")
                .append(getNumber(mEtIpaddr2)).append(".")
                .append(getNumber(mEtIpaddr3)).append(".")
                .append(getNumber(mEtIpaddr4));

        return buf.toString();
    }

    private boolean isEditTextFocused() {
        return mEtIpaddr1.isFocused() || mEtIpaddr2.isFocused() || mEtIpaddr3.isFocused() || mEtIpaddr4.isFocused();
    }

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (isEditTextFocused()) {
                IpAddrEdit.this.setBackgroundResource(R.drawable.button_sel_blue);
            } else {
                IpAddrEdit.this.setBackgroundResource(R.drawable.button_normal_black);
            }

            if (hasFocus) {
                mTitle.setTextColor(Color.WHITE);
                mEtIpaddr1.setTextColor(Color.WHITE);
                mEtIpaddr2.setTextColor(Color.WHITE);
                mEtIpaddr3.setTextColor(Color.WHITE);
                mEtIpaddr4.setTextColor(Color.WHITE);
            } else {
                mTitle.setTextColor(Color.WHITE);
                mEtIpaddr1.setTextColor(Color.WHITE);
                mEtIpaddr2.setTextColor(Color.WHITE);
                mEtIpaddr3.setTextColor(Color.WHITE);
                mEtIpaddr4.setTextColor(Color.WHITE);
            }
        }
    };
}
