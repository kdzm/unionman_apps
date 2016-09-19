package com.um.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.um.controller.AppBaseActivity;
import java.sql.Date;
import java.text.SimpleDateFormat; 

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Status;
import android.os.Handler;

public class Tf_updatebar extends AppBaseActivity{
	private ProgressBar updatebar;
	private TextView title;
	public Handler handler=new Handler();
	public String curdate_str;
	public int update_status = 0;
	static final int SCALE_RECEIVEPATCH = 54;
	static final int SCALE_PATCHING = 55;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.tf_updatebar);
//		Log.i("TF_updatebar_receiver", "tf_updatebar open");
//        Intent intent = getIntent();
//		String progress = intent.getStringExtra("progress");
//		String flag = intent.getStringExtra("flag");
//		int progress_int = Integer.parseInt(progress);
//		int flag_int = Integer.parseInt(flag);
//
//		DVB.GetInstance();
//		Status status = Status.GetInstance();
//		status.attachContext(this); 
//		
//		title = (TextView)findViewById(R.id.textViewUpdating);
//		if(1 == flag_int)          //flag_int = 1  loaddata
//		{
//			title.setText(R.string.tf_ca_scale_receivepatch);
//		}
//		else                       //update
//		{
//			title.setText(R.string.tf_ca_scale_patching);
//		}
//    	
//		//2.启动计时器：
//		//handler.postDelayed(runnable, 20);//每两秒执行一次runnable.
//		
//        onLoadData(progress_int);
    }




    public void onLoadData(int process)
    {	
    	Intent intent = getIntent();
    	String flag = intent.getStringExtra("flag");
    	int flag_int = Integer.parseInt(flag);
		if(process<100)
		{
			updatebar	  = (ProgressBar)findViewById(R.id.updateProgressBar);
			updatebar.setProgress(process);
			TextView progress = (TextView)findViewById(R.id.update_percent);
			progress.setText(process+" %");
		}
		else if((process >= 100)&& (0 == flag_int))
		{
			//3.停止计时器：
			//handler.removeCallbacks(runnable);
			
			updatebar	  = (ProgressBar)findViewById(R.id.updateProgressBar);
			updatebar.setProgress(process);
			TextView progress = (TextView)findViewById(R.id.update_percent);
			progress.setText(process+" %");
			
			SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy.MM.dd.HH.mm.ss");     
			Date   curDate   =   new   Date(System.currentTimeMillis());//Get local time     
			curdate_str   =   formatter.format(curDate); 
			String [] strs = curdate_str.split("[.]");
			byte[] buff = new byte[16];
			
			buff[0] = 1;
			int year_int = Integer.parseInt(strs[0]);
			int month_int = Integer.parseInt(strs[1]);
			int day_int = Integer.parseInt(strs[2]);
			int hour_int = Integer.parseInt(strs[3]);
			int min_int = Integer.parseInt(strs[4]);
			int second_int = Integer.parseInt(strs[5]);
			
			byte[] year_byte = intToByteArray(year_int);
			byte[] month_byte = intToByteArray(month_int);
			byte[] day_byte = intToByteArray(day_int);
			byte[] hour_byte = intToByteArray(hour_int);
			byte[] min_byte = intToByteArray(min_int);
			byte[] second_byte = intToByteArray(second_int);

			buff[8] = year_byte[3];
			buff[9] = year_byte[2];
			buff[10]= month_byte[3];
			buff[11] = day_byte[3];
			buff[13] = hour_byte[3];
			buff[14] = min_byte[3];
			buff[15] = second_byte[3];

			Ca ca = new Ca(DVB.GetInstance());
			ca.CaSetUpdateStatus(buff,16);
		}
		else
		{
			return;
		}
    }
    public static byte[] intToByteArray(int i) 
    {     
    	byte[] result = new byte[4];     
    	result[0] = (byte)((i >> 24) & 0xFF); 
		result[1] = (byte)((i >> 16) & 0xFF);  
		result[2] = (byte)((i >> 8) & 0xFF);   
		result[3] = (byte)(i & 0xFF);  
		return result; 
	}
}
