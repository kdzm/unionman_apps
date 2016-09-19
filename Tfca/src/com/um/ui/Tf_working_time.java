package com.um.ui;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.CaEvent;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Tf_working_time extends AppBaseActivity {

	private static final String TAG = "Tf_working_time";
	private EditText pin;
	private Button enter_btn;
	private int shour;
	private int sminute;
	private int sSecond;
	private int ehour;
	private int eminute;
	private int eSecond;
	private EditText mStartHour, mStartMinute, mStartSecond;
	private EditText mEndHour, mEndMinute, mEndSecond;
	public static int HOUR = 0;
	public static int MINIUTE = 1;
	public static int SECOND = 2;
	private Context mContext = Tf_working_time.this;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_working_time);

		getWorkingtime();
		
		enter_btn = (Button) findViewById(R.id.button1);
		enter_btn.setText(R.string.ok);
		enter_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				enterBtnClick();	
			}
		});

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
	
	private void getWorkingtime()
	{
		mStartHour = (EditText) this.findViewById(R.id.tv_start_hour);
		mStartMinute = (EditText) this.findViewById(R.id.tv_start_minute);
		mStartSecond = (EditText) this.findViewById(R.id.tv_start_second);
		mEndHour = (EditText) this.findViewById(R.id.tv_end_hour);
		mEndMinute = (EditText) this.findViewById(R.id.tv_end_minute);
		mEndSecond = (EditText) this.findViewById(R.id.tv_end_second);

        mStartHour.requestFocus();

		Ca ca = new Ca(DVB.GetInstance());
		/* Set default time */
		CaEvent wt = new CaEvent();
		wt = ca.CaparseWorkingTime();
		if(null != wt){
		
			Time startTime = wt.CagetStartTime();
			Time endTime = wt.CagetEndTime();
			Log.i("CA", "CA start hour: " + startTime.hour);
			Log.i("CA", "CA start minute: " + startTime.minute);
			Log.i("CA", "CA start second: " + startTime.second);
			Log.i("CA", "CA end hour: " + endTime.hour);
			Log.i("CA", "CA end minute: " + endTime.minute);
			Log.i("CA", "CA end second: " + endTime.second);
			
			
			shour = startTime.hour;
			sminute = startTime.minute;
			sSecond = startTime.second;
		
			ehour = endTime.hour;
			eminute = endTime.minute;
			eSecond = endTime.second;
		
			mStartHour.setText(shour + "");
			mStartMinute.setText(sminute + "");
			mStartSecond.setText(sSecond + "");
		
			mEndHour.setText(ehour + "");
			mEndMinute.setText(eminute + "");
			mEndSecond.setText(eSecond + "");
		
			TextWatcher hourWatcher = new UmTextWatcher(mContext, HOUR);
            mStartHour.setSelection(0, mStartHour.getText().toString().length());
			mStartHour.addTextChangedListener(hourWatcher);
			mEndHour.addTextChangedListener(hourWatcher);
			TextWatcher miniuteWatcher = new UmTextWatcher(mContext, MINIUTE);
			mStartMinute.addTextChangedListener(miniuteWatcher);
			mEndMinute.addTextChangedListener(miniuteWatcher);
			TextWatcher secondWatcher = new UmTextWatcher(mContext, SECOND);
			mStartSecond.addTextChangedListener(secondWatcher);
			mEndSecond.addTextChangedListener(secondWatcher);
		
			/* input PIN */
			pin = (EditText) findViewById(R.id.editText3);
			pin.setText(pin.getText().toString());
			pin.setSelection(pin.getText().toString().length());
		}
	}
	
	private void getData()
	{
		//鑾峰彇寮�灏忔椂鏁帮紝榛樿涓�锛�
		if(!("".equalsIgnoreCase(mStartHour.getText().toString())))
		{
			shour = Integer.valueOf(mStartHour.getText().toString());
		}else
		{
			shour = 0;
		}
		//鑾峰彇寮�鍒嗛挓鏁帮紝榛樿涓�;
		if(!("".equalsIgnoreCase(mStartMinute.getText().toString())))
		{
			sminute = Integer.valueOf(mStartMinute.getText().toString());
		}else
		{
			sminute = 0;
		}
		//鑾峰彇寮�绉掓暟锛岄粯璁や负0锛�
		if(!("".equalsIgnoreCase(mStartSecond.getText().toString())))
		{
			sSecond = Integer.valueOf(mStartSecond.getText().toString());
		}else
		{
			sSecond = 0;
		}
		//鑾峰彇缁撴潫灏忔椂鏁帮紝榛樿涓�锛�
		if(!("".equalsIgnoreCase(mEndHour.getText().toString())))
		{
			ehour = Integer.valueOf(mEndHour.getText().toString());
		}else
		{
			ehour = 0;
		}	
		//鑾峰彇缁撴潫鍒嗛挓鏁帮紝榛樿涓�;
		if(!("".equalsIgnoreCase(mEndMinute.getText().toString())))
		{
			eminute = Integer.valueOf(mEndMinute.getText().toString());
		}else
		{
			eminute = 0;
		}		
		//鑾峰彇缁撴潫绉掓暟锛岄粯璁や负0;
		if(!("".equalsIgnoreCase(mEndSecond.getText().toString())))
		{
			eSecond = Integer.valueOf(mEndSecond.getText().toString());
		}else
		{
			eSecond = 0;
		}
		Log.i("YINHAOJUN", "shour" + shour + "sminute" + sminute + "sSecond" + sSecond);
		Log.i("YINHAOJUN", "ehour" + ehour + "eminute" + eminute + "eSecond" + eSecond);		
	}

	private boolean checkTimeFormat()
	{
		if((23 < shour)||(59 < sminute)||(59 < sSecond))
		{
			return false;
		}
		else if((23 < ehour)||(59 < eminute)||(59 < eSecond))
		{
			return false;
		}
		
		return true;
	}

    private boolean checkTimeValid()
    {
        int startTime = shour * 60 * 60 + sminute * 60 + sSecond;
        int endTime = ehour * 60 * 60 + eminute * 60 + eSecond;
        return startTime < endTime;
    }
	
	private void changeWorkingTime()
	{
		Ca ca = new Ca(DVB.GetInstance());
		
		String strHour = "";
		String strMin = "";
		String strSec = "";
		String stime = "";
		
		if (shour < 10)
		{
			strHour = String.valueOf(0).concat(String.valueOf(shour));
		}
		else
		{
			strHour = String.valueOf(shour);
		}
		
		if (sminute < 10)
		{
			strMin = String.valueOf(0).concat(String.valueOf(sminute));
		}
		else
		{
			strMin = String.valueOf(sminute);
		}
		
		if (sSecond < 10)
		{
			strSec = String.valueOf(0).concat(String.valueOf(sSecond));
		}
		else
		{
			strSec = String.valueOf(sSecond);
		}

		stime = strHour.concat(strMin).concat(strSec);

		strHour = "";
		strMin = "";
		strSec = "";
		String etime = "";
		
		if (ehour < 10)
		{
			strHour = String.valueOf(0).concat(String.valueOf(ehour));
		}
		else
		{
			strHour = String.valueOf(ehour);
		}
		
		if (eminute < 10)
		{
			strMin = String.valueOf(0).concat(String.valueOf(eminute));
		}
		else
		{
			strMin = String.valueOf(eminute);
		}
		
		if (eSecond < 10)
		{
			strSec = String.valueOf(0).concat(String.valueOf(eSecond));
		}
		else
		{
			strSec = String.valueOf(eSecond);
		}

		etime = strHour.concat(strMin).concat(strSec);

		Log.i("CA", "CA stime: " + stime);
		Log.i("CA", "CA etime: " + etime);
		String verifypinstr = String.valueOf(pin.getText());

		byte[] stimetmp = stime.getBytes();
		byte[] etimetmp = etime.getBytes();
		byte[] verifypintmp = verifypinstr.getBytes();

		if (ca.CaSetWorkTime(stimetmp, etimetmp, verifypintmp, 6) == 0) {
			Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_working_time_change_ok), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
		}	
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
				if(pin.isFocused()||mStartHour.isFocused()||mStartMinute.isFocused()||mStartSecond.isFocused()||mEndHour.isFocused()||mEndMinute.isFocused()||mEndSecond.isFocused()){
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
		// TODO Auto-generated method stub
		
		getData();
		if(checkTimeFormat())
		{
		    if (checkTimeValid())
		    {
		        changeWorkingTime();
		    }
		    else
		    {
		        Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_working_time_err), Toast.LENGTH_LONG).show();
		    }
		}
		else
		{
			Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_working_time_invalid), Toast.LENGTH_LONG).show();
		}
	}
}
