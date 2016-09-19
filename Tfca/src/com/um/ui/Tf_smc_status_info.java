package com.um.ui;
//import android.os.SystemProperties;
import java.io.UnsupportedEncodingException;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.Ca.Ca_Rating;
import com.um.dvbstack.Ca.Ca_Version;
import com.um.dvbstack.Ca.Card_No;
import com.um.dvbstack.CaEvent;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
public class Tf_smc_status_info extends AppBaseActivity{
	private TextView cn_txt;// card number text
	private TextView ps_txt;//pair status text	
	
	/*CA version*/
	private TextView cv_txt; //ca version
	private TextView cr_txt; //ca_rating
	private TextView stbid_txt; //stb id
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_smc_status_info);
		
		/*Card Number*/
		Ca ca = new Ca(DVB.GetInstance());
		Card_No card_no = new Card_No();
		ca.CaGetIcNo(0, card_no);
		
		cn_txt = (TextView)findViewById(R.id.textView10);
		String cardno = new String(card_no.cardno);
		cn_txt.setText(cardno);
		
		/*Paired State*/
		ps_txt = (TextView)findViewById(R.id.textView2);
		switch(ca.CaGetPairStatus(1))
		{
			case 0:
				ps_txt.setText(R.string.tf_smc_pair_present_stb);
			break;			
			case 0x80000029:
				ps_txt.setText(R.string.tf_smc_pair_none_stb);
				break;

			case 0x80000028:
				ps_txt.setText(R.string.tf_smc_pair_other_stb);
				break;
				
			default:
				ps_txt.setText(R.string.tf_no_smc_available);
				break;
		}

		/*CA Version*/		
		Ca_Version ca_version = new Ca_Version();
		ca.CaGetVersion(ca_version);		
		cv_txt = (TextView)findViewById(R.id.textView4);	
		String s_version = new String(ca_version.caversion);
		
		byte[] b_version = new byte[10];
		int len = s_version.length();
		if(len > 10)
		{
			byte[] b_version_all = s_version.getBytes();
			for(int i = 0;i < 10; i++)
			{
				b_version[i] = b_version_all[i];
			}
			try {
				s_version = new String(b_version,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		s_version =s_version.toUpperCase();
		cv_txt.setText("(" + s_version + ")");	
		
		/*Watch Level*/
		Ca_Rating ca_rating = new Ca_Rating();
		ca.CaGetRating(ca_rating);
		Log.i("DVBACTIVITY","DVB LOCK FRE:"+String.valueOf(ca_rating.carating));
		cr_txt = (TextView)findViewById(R.id.textView6);
		cr_txt.setText(String.valueOf(ca_rating.carating[0]));
	
		/*Working Time*/
		CaEvent wt = new CaEvent();
		wt = ca.CaparseWorkingTime();
		
		if(null != wt){
			 TextView pStartTime = (TextView)this.findViewById(R.id.textView8);
			 TextView pEndTime = (TextView)this.findViewById(R.id.textView11);
			 
			 Time startTime = wt.CagetStartTime();
			 Time endTime = wt.CagetEndTime();
			
			 String s_hour = convertToTwoDigit(startTime.hour);
			 String s_minute = convertToTwoDigit(startTime.minute);
			 String s_second = convertToTwoDigit(startTime.second);
			 String e_hour = convertToTwoDigit(endTime.hour);
			 String e_minute = convertToTwoDigit(endTime.minute);
			 String e_second = convertToTwoDigit(endTime.second);
			 pStartTime.setText(s_hour + ":" + s_minute + ":" + s_second + "-");
			 pEndTime.setText(e_hour + ":" + e_minute + ":" + e_second);
		}	 
		
		 TextView pAreaCode = (TextView)this.findViewById(R.id.tv_area_code);
		 int [] operid = new int[20];
		 int [] opernum = new int[1];
		 
		 ca.CaGetOperID(operid, opernum);
		 
		 int[] array = new int[18];
		 int ret = ca.CaGetEigenvalue(operid[0], array);
		 
		 if(0 == ret){
			 pAreaCode.setText(String.valueOf(array[0]));
		 }
        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
	
	public static String convertToTwoDigit ( int value)
	{
		String ret = String.valueOf(value);
		int len = ret.length();
		if(1 == len)
		{
			ret = "0"+ret;
		}
		return ret;  	  
	}

}
