package com.um.ui;

import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.CaEvent;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Dvt_working_time extends AppBaseActivity {
	private final String TAG = "Dvt_working_time";
	private EditText pin;
	private Button enter_btn;
	private int shour;
	private int sminute;
	private int ehour;
	private int eminute;
	private EditText mStartHour, mStartMinute;
	private EditText mEndHour, mEndMinute;
	public static int HOUR = 0;
	public static int MINIUTE = 1;
	private Context mContext = Dvt_working_time.this;
	
	private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d(TAG, "ret:" +ret);
    	Log.d(TAG, "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_working_time);

		int ret = getWorkingtime();
		if(ret != 0){
			new AlertDialog.Builder(mContext)
			.setMessage(R.string.got_info_fail)
			.setPositiveButton("ok", null)
			.show();
			return;
		}
		
		enter_btn = (Button) findViewById(R.id.button1);
		enter_btn.setText(R.string.ok);
		enter_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				getData();
				if(!checkTimeFormat()){
//					new AlertDialog.Builder(mContext)
//					.setMessage(R.string.dvt_working_time_invalid)
//					.setPositiveButton("ok", null)
//					.show();
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_working_time_invalid), Toast.LENGTH_LONG).show();
					return;
				}

                if(!checkTimeValid()){
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_working_time_err), Toast.LENGTH_LONG).show();
                    return;
                }

				if(!getCardStatus()){
//					new AlertDialog.Builder(mContext)
//					.setMessage(R.string.ca_insert_card)
//					.setPositiveButton("ok", null)
//					.show();
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.ca_insert_card), Toast.LENGTH_LONG).show();
					return;
				}
				
				String str = String.valueOf(pin.getText());
				byte[] temp1 = str.getBytes();	
				Log.i("CA","CA str:"+str);	
				
				if(temp1.length != 8){
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.pin_len_err), Toast.LENGTH_LONG).show();
//					new AlertDialog.Builder(mContext)
//					.setMessage(R.string.pin_len_err)
//					.setPositiveButton("ok", null)
//					.show();
					
					return;
				}
				
				Ca ca = new Ca(DVB.getInstance());
				int ret = ca.CaVerifyPin(temp1,8);
				System.out.printf("ca.CaVerifyPin, ret:0x%x", ret);
				if(0x8000002c == ret){
//					new AlertDialog.Builder(mContext)
//					.setMessage(R.string.dvt_pin_err)
//					.setPositiveButton("ok", null)
//					.show();
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_pin_err), Toast.LENGTH_LONG).show();
					return;
				}else if(0x8000002d == ret){
//					new AlertDialog.Builder(mContext)
//					.setMessage(R.string.pin_is_locked)
//					.setPositiveButton("ok", null)
//					.show();
                    Toast.makeText(mContext, mContext.getResources().getText(R.string.pin_is_locked), Toast.LENGTH_LONG).show();
					return;
				}
				
				changeWorkingTime();	
			}
		});

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
	
	@Override
	protected void onPause(){
        super.onPause();
        finish();
	}
	
	private int getWorkingtime()
	{
		mStartHour = (EditText) this.findViewById(R.id.tv_start_hour);
		mStartMinute = (EditText) this.findViewById(R.id.tv_start_minute);
		mEndHour = (EditText) this.findViewById(R.id.tv_end_hour);
		mEndMinute = (EditText) this.findViewById(R.id.tv_end_minute);

		Ca ca = new Ca(DVB.getInstance());
		/* Set default time */
		CaEvent wt = new CaEvent();
		wt = ca.CaparseWorkingTime();
		if(null != wt){
		
			Time startTime = wt.CagetStartTime();
			Time endTime = wt.CagetEndTime();
			Log.i("CA", "CA start hour: " + startTime.hour);
			Log.i("CA", "CA start minute: " + startTime.minute);
			Log.i("CA", "CA end hour: " + endTime.hour);
			Log.i("CA", "CA end minute: " + endTime.minute);
			
			shour = startTime.hour;
			sminute = startTime.minute;
		
			ehour = endTime.hour;
			eminute = endTime.minute;
		
			mStartHour.setText(shour + "");
			mStartMinute.setText(sminute + "");
		
			mEndHour.setText(ehour + "");
			mEndMinute.setText(eminute + "");
		
			TextWatcher hourWatcher = new UmTextWatcher(mContext, HOUR);
			mStartHour.addTextChangedListener(hourWatcher);
			mEndHour.addTextChangedListener(hourWatcher);
			TextWatcher miniuteWatcher = new UmTextWatcher(mContext, MINIUTE);
			mStartMinute.addTextChangedListener(miniuteWatcher);
			mEndMinute.addTextChangedListener(miniuteWatcher);
		
			/* input PIN */
			pin = (EditText) findViewById(R.id.editText3);
			pin.setText(pin.getText().toString());
			pin.setSelection(pin.getText().toString().length());
			return 0;
		}else{
			return -1;
		}
	}
	
	private void getData()
	{
		//获取开始小时数，默认为0；
		if(!("".equalsIgnoreCase(mStartHour.getText().toString())))
		{
			shour = Integer.valueOf(mStartHour.getText().toString());
		}else
		{
			shour = 0;
		}
		//获取开始分钟数，默认为0;
		if(!("".equalsIgnoreCase(mStartMinute.getText().toString())))
		{
			sminute = Integer.valueOf(mStartMinute.getText().toString());
		}else
		{
			sminute = 0;
		}

		//获取结束小时数，默认为0；
		if(!("".equalsIgnoreCase(mEndHour.getText().toString())))
		{
			ehour = Integer.valueOf(mEndHour.getText().toString());
		}else
		{
			ehour = 0;
		}	
		//获取结束分钟数，默认为0;
		if(!("".equalsIgnoreCase(mEndMinute.getText().toString())))
		{
			eminute = Integer.valueOf(mEndMinute.getText().toString());
		}else
		{
			eminute = 0;
		}		

		Log.i("YINHAOJUN", "shour" + shour + "sminute" + sminute);
		Log.i("YINHAOJUN", "ehour" + ehour + "eminute" + eminute);		
	}

	private boolean checkTimeFormat()
	{
		if((23 < shour)||(59 < sminute))
		{
			return false;
		}
		else if((23 < ehour)||(59 < eminute))
		{
			return false;
		}

		return true;
	}

    private boolean checkTimeValid()
    {
        int startTime = shour * 60 * 60 + sminute * 60;
        int endTime = ehour * 60 * 60 + eminute * 60;
        return startTime < endTime;
    }
	
	private void changeWorkingTime()
	{
		Ca ca = new Ca(DVB.getInstance());
		
		String strHour = "";
		String strMin = "";
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

		stime = strHour.concat(strMin);

		strHour = "";
		strMin = "";
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

		etime = strHour.concat(strMin);

		Log.i("CA", "CA stime: " + stime);
		Log.i("CA", "CA etime: " + etime);
		String verifypinstr = String.valueOf(pin.getText());

		byte[] stimetmp = stime.getBytes();
		byte[] etimetmp = etime.getBytes();
		byte[] verifypintmp = verifypinstr.getBytes();

		if (ca.CaSetWorkTime(stimetmp, etimetmp, verifypintmp, 8) == 0) {
//			new AlertDialog.Builder(mContext)
//			.setMessage(R.string.dvt_working_time_change_ok)
//			.setPositiveButton("ok", null)
//			.show();
            Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_working_time_change_ok), Toast.LENGTH_LONG).show();
		} else {
//			new AlertDialog.Builder(mContext)
//			.setMessage(R.string.dvt_pin_err)
//			.setPositiveButton("ok", null)
//			.show();
            Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_pin_err), Toast.LENGTH_LONG).show();
		}	
	}

}
