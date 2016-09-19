package com.um.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

public class UmTextWatcher implements TextWatcher{

	private Context mContext = null;
	private int mType = -1;
	public static int HOUR = 0;
	public static int MINIUTE = 1;
	public static int SECOND = 2;
	
	public UmTextWatcher(Context context, int type)
	{
		mContext = context;
		mType = type;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		String str = s.toString();
		switch(mType)
		{
		case 0:
			dealWithRange(str,23);
			break;
		case 1:
			dealWithRange(str,59);
			break;
		case 2:
			dealWithRange(str,59);
			break;
			default:
				break;
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	private void dealWithRange(String str , int type)
	{
		
		if(!("".equalsIgnoreCase(str)))
		{
			int value = Integer.valueOf(str);			
			if(value > type)
			{
				Toast.makeText(mContext, "请输入0"+"-"+type+"之间的数！！", Toast.LENGTH_SHORT).show();
			}	
			Log.i("YINHAOJUN", "value:" + value);
		}
	}
	
	
}
