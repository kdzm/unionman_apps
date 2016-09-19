package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.irdca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import com.um.controller.AppBaseActivity;

public class Ird_email_read extends AppBaseActivity {
	private String index_str;
	private String email_title_str;
	private Time stime;
	TextView titleTxt;
	TextView sendTimeTxt;
	TextView senderNameTxt;
	TextView emailContentTxt;
	byte[] email_content_byte = new byte[1028];
	byte[] sender_name_byte = new byte[200];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ird_condition_access);
        
		 Intent intent = getIntent();
		 index_str = intent.getStringExtra("email_index");
		 int index_int = Integer.parseInt(index_str);
		 
		 email_title_str = intent.getStringExtra("email_title");
		 
		 int []buff_len = {40960};
		 byte[] buff = new byte[buff_len[0]];
		
		Log.i("read email","mailid:"+index_int);
		 
		 Ca ca = new Ca(DVB.GetInstance());
		 int ret = ca.CaGetEmailContentByIndex(index_int, buff, buff_len);
		 Log.i("Tf_email_read","CaGetEmailContentByIndex,ret:" +ret);
		 Log.i("Tf_email_read","buff_len:" +buff_len[0]);
		 if((0 == ret) && (0 != buff_len[0]))
		 {
			 //String jsonStr = new String(buff, 0, buff_len[0]);
			try {
				 String jsonStr = new String(buff, 0, buff_len[0], "gb2312");
				 showEmailContent(jsonStr);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			 
		 }
		 else
		 {
			new AlertDialog.Builder(Ird_email_read.this).setMessage(R.string.pin_mail_not_read).setPositiveButton("ok", null).show();
		 }
    }
    
	public String calculateSendDate(int time) throws ParseException
    {
    	String sendtime;
        time=time+8*3600;
        String send_date_str = CaculateSysDateToUTC(time / 86400);
    	String send_time_str = CaculateSysDateToHMS(time % 86400);
    	sendtime = send_date_str + " " + send_time_str;
    	return sendtime;
    }
    
	private String CaculateSysDateToUTC(int sysDay) throws ParseException
    {
        int nYear,nMon,nDay,nTemp,nDate = sysDay;
        nYear = 1970;
        do
        {    /*year.*/
            nTemp = (IsLeapYear(nYear))?366:365;
            if(nDate - nTemp < 0)
                break;
            nDate -= nTemp;
            nYear ++;
        }while(true);

        nMon = 1;
        do
        {    /*month.*/
            if (2 == nMon)
            {
                nTemp = (IsLeapYear(nYear))? 29: 28;
            }
            else if(4 == nMon || 6 == nMon || 9 == nMon || 11 == nMon)
                nTemp = 30;
            else
                nTemp = 31;
            if(nDate - nTemp < 0)
                break;
            nDate -= nTemp;
            nMon ++;
        }while(true);
        if(1 > nMon || 12 < nMon)
        {
            return "";
        }

        nDay = 1;
        nDay += nDate; /*day.*/
        if((1 > nDay)||(31 < nDay))
        {
            return "";
        }
        
        String temp_str = nYear + "/" + nMon + "/" + nDay;
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");  
        Date date = format.parse(temp_str);
        String send_date_str=format.format(date); 
        
        return send_date_str;
    }
    
	public boolean  IsLeapYear(int iYear)
	{
		if((iYear %400) == 0)
		{
		    return true;
		}
		else if((iYear % 4) == 0)
		{
		    if((iYear % 100) == 0)
		    {
			return false;
		    }
		    else
		    {
			return true;
		    }
		}
		else 
		{
		    return false;
		}
	}

    
    private String CaculateSysDateToHMS(int second) throws ParseException
    {
    	int hour = 0;
    	int minute = 0;
    	int sec = 0;
        hour = second / 3600;
        minute  = (second % 3600) / 60;
        sec = (second % 3600) % 60;
        String temp_str = hour + ":" + minute + ":" + sec;
        Log.i("time","hour"+hour);
        Log.i("time","minute"+minute);
        Log.i("time","second"+second);
        
//        SimpleDateFormat format=new SimpleDateFormat("hh:mm:ss");   //this is 12-hour zhidu
//        Date date = format.parse(temp_str);
//        String send_time_str=format.format(date); 
//        return send_time_str;
        return temp_str;   // use 24-hour zhidu
    }
    
    private void showEmailContent(String jsonStr){
//		 System.out.println("jsonStr:"+jsonStr); 
		 int i = 0;
		 int j = 0;
		 String sender_name_str;
		 String email_content_str;
		 
		 Ca ca = new Ca(DVB.GetInstance());
		 
		 try {  
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  	String mailContent = jsonObject.getString("content");
			  	String senderStr = jsonObject.getString("Sender_str");
			    int EmailId = jsonObject.getInt("u32ID");
			    int ret = ca.CaReadEmail(EmailId);
			    Log.i("CaReadEmail","EmailId:"+EmailId);			    
			    Log.i("CaReadEmail","ret:"+ret);
		
				 titleTxt = (TextView)findViewById(R.id.textTitle);
				 senderNameTxt =(TextView)findViewById(R.id.textSendername);
				 emailContentTxt = 	(TextView)findViewById(R.id.textEmailContent);	
				 titleTxt.setText(email_title_str);
				 
				 senderNameTxt.setText(senderStr);
				 emailContentTxt.setText(mailContent);
		 
		 } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
}
