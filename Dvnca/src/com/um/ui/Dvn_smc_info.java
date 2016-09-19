package com.um.ui;

import java.io.UnsupportedEncodingException;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import com.um.dvnca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.Ca.Ca_Rating;
import com.um.dvbstack.Ca.Ca_Version;
import com.um.dvbstack.Ca.Card_No;
import com.um.dvbstack.CaEvent;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class Dvn_smc_info extends AppBaseActivity{
	private TextView cn_txt;// card number text
	private TextView ps_txt;//pair status text	
	
	/*CA version*/
	private TextView cv_txt; //ca version
	private TextView cr_txt; //ca_rating
	private TextView cs_txt; //ca_stbcasver
	private TextView cm_txt; //ca_manuname
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_smc_info);
		boolean isValidCard = false;
		/*Card Number*/
		Ca ca = new Ca(DVB.GetInstance());
        int[] cardid = new int[1];
		ca.CaGetCardId(cardid);
		cn_txt = (TextView)findViewById(R.id.textView10);
		cn_txt.setText(String.valueOf(cardid[0]));
			
		/*Stb Atr*/
		int [] atr_len = {50};
	        byte[] stbatr = new byte[50];
		ca.CaGetStbAtr(stbatr, atr_len);

		String jsonAtr = new String(stbatr, 0, atr_len[0]);
		System.out.println("jsonStr:"+jsonAtr); 

		try { 
			JSONObject jsonObject = new JSONObject(jsonAtr);
			String card_num = jsonObject.getString("StbAtr");

			cn_txt = (TextView)findViewById(R.id.textView4);
			cn_txt.setText(card_num);
			} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					} 

		/*AcountNo*/
		int [] AcountNo_len = {50};
	        byte[] AcountNo = new byte[50];
		ca.CaGetAccountno(AcountNo, AcountNo_len);

		String jsonAcountNo = new String(AcountNo, 0, AcountNo_len[0]);
		System.out.println("jsonStr:"+jsonAcountNo); 

		try { 
			JSONObject jsonObject = new JSONObject(jsonAcountNo);
			String card_num = jsonObject.getString("AccountNo");

			cn_txt = (TextView)findViewById(R.id.textView8);
			cn_txt.setText(card_num);
			} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					} 

		/*Money*/
		int [] Money_len = {50};
	        byte[] Money = new byte[50];
		ca.CaGetMoney(Money, Money_len);

		String jsonMoneyNo = new String(Money, 0, Money_len[0]);
		System.out.println("jsonStr:"+jsonMoneyNo); 

		try { 
			JSONObject jsonObject = new JSONObject(jsonMoneyNo);
			String card_num = jsonObject.getString("Money");

			cn_txt = (TextView)findViewById(R.id.textView6);
			cn_txt.setText(card_num);
			} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					} 	

		/*Area code*/
		TextView pAreaCode = (TextView)this.findViewById(R.id.tv_area_code);
		
	        int []buff_len = {80};
	        byte []buff = new byte[buff_len[0]];
	        int area_code = 0;
	        int area_flag = 0;
	        int area_time = 0;
	        int ret = ca.CaGetAreaInfo(buff, buff_len);
	        Log.i("CaGetAreaInfo", "ret:"+ret);
	        Log.i("CaGetAreaInfo", "buff_len:"+buff_len[0]);
	        


	        if((ret == 0) && (buff_len[0] != 0))
	        {
	        	 String jsonareaStr = new String(buff, 0, buff_len[0]);
				 System.out.println("jsonStr:"+jsonareaStr); 
				 
				 try {  
					  	JSONObject jsonObject = new JSONObject(jsonareaStr);
					  	area_code = jsonObject.getInt("areacode");
					    
					} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					}  

        	}
	        pAreaCode.setText(String.valueOf(area_code));
			
		/*CA StbCasver*/
		final int strLen = 11;
		byte [] StbCasver = new byte[strLen];
		ret = ca.CaGetStbCasVer(StbCasver, strLen);
	    Log.i("CaGetStbCasVer", "ret:"+ret);
		cs_txt = (TextView)findViewById(R.id.textView2);	
		//String s_version = new String(ca_version.caversion);
		cs_txt.setText(String.valueOf(StbCasver[0]));	
		
		/*PAIR*/
			
		int [] Pair_len = {50};
	        byte[] Pair = new byte[50];
		ret = ca.CaGetdvnpair(Pair, Pair_len);
	    Log.i("CaGetdvnpair", "ret:"+ret);
		String jsonPairNo = new String(Pair, 0, Pair_len[0]);
		System.out.println("jsonStr:"+jsonPairNo); 

		try { 
			JSONObject jsonObject = new JSONObject(jsonPairNo);
			String card_num = jsonObject.getString("PairStatus");

			cn_txt = (TextView)findViewById(R.id.textView22);
			cn_txt.setText(card_num);
			} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					} 
	
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
