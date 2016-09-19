package com.um.ui;

import java.io.FileInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Ca.Card_No;
import com.um.controller.AppBaseActivity;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.widget.TextView;

public class Tf_smc_update_status extends AppBaseActivity{
	private TextView cn_txt;// card number text
	private TextView update_flag_txt;// update flag text
	private TextView date_txt;// card number text
	private TextView time_txt;// update time text
	int update_flag,year,month,day,hour,minute,second;
	String cardno;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_smc_update_status);
		
		
		Ca ca = new Ca(DVB.GetInstance());

		cn_txt = (TextView)findViewById(R.id.textView3);
		update_flag_txt = (TextView)findViewById(R.id.textView8);
		date_txt = (TextView)findViewById(R.id.textView5);
		time_txt = (TextView)findViewById(R.id.textView6);

		int []buff_len = {512};
		byte []buff = new byte [buff_len[0]];

		int ret = ca.CaGetUpdateStatus(buff, buff_len);
		if((0 == ret)&&(0 != buff_len[0]))
		{
			String jsonStr = new String(buff, 0, buff_len[0]);
			if(!jsonStr.isEmpty())
			{
				showUpdateInfo(jsonStr);
			}
		}else
		{
			update_flag_txt.setText("");
			date_txt.setText("");
			time_txt.setText("");
		}

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}	
	
	private String convertToFourDigit ( int value)
	{
		String ret = String.valueOf(value);
		int len = ret.length();
		switch(len)
		{
		case 1:
			ret = "000"+ret;
			break;
		case 2:
			ret = "00"+ret;
			break;
		case 3:
			ret = "0"+ret;
			break;
		default:
			break;  		  
		}
		
		return ret;  	  
	}
    
	private String convertToTwoDigit ( int value)
	{
		String ret = String.valueOf(value);
		int len = ret.length();
		if(1 == len)
		{
			ret = "0"+ret;
		}
		return ret;  	  
	}
	
	private void showUpdateInfo(String jsonStr){
			System.out.println("jsonStr:"+jsonStr); //杈撳嚭瀛楃涓�
			 
			try {  
				JSONObject jsonObject = new JSONObject(jsonStr);
				update_flag = jsonObject.getInt("CAUpdateFlag"); 
				year = jsonObject.getInt("year"); 
				month = jsonObject.getInt("month"); 
				day = jsonObject.getInt("day"); 
				hour = jsonObject.getInt("hour"); 
				minute = jsonObject.getInt("min"); 
				second = jsonObject.getInt("sec"); 
				cardno = jsonObject.getString("CACardSn");
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");// 寮傚父澶勭悊浠ｇ爜  
			}  
			
			String year_str = convertToFourDigit(year);
			String month_str = convertToTwoDigit(month);
			String day_str = convertToTwoDigit(day);
			String hour_str = convertToTwoDigit(hour);
			String minute_str = convertToTwoDigit(minute);
			String second_str = convertToTwoDigit(second);
			
			if(0 == update_flag)
			{
				update_flag_txt.setText("FAILED");
			}
			else
			{
				update_flag_txt.setText("SUCCESS");
			}
			
			String update_date = year_str + "." + month_str + "." + day_str;
			String update_time = hour_str + "." + minute_str + "." + second_str;
			
			date_txt.setText(update_date);
			time_txt.setText(update_time);
		
			cn_txt.setText(cardno);
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
	     case KeyEvent.KEYCODE_MENU:
			finish();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}

