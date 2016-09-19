package com.um.ui;

import java.io.UnsupportedEncodingException;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import com.um.irdca.R;
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

public class Ird_smc_info extends AppBaseActivity{
	private TextView sta_txt;// status text
	private TextView cn_txt;//card number text	
	private TextView typ_txt; //type 
	private TextView ver_txt; //version
	private TextView bl_txt; //bulid
	private TextView var_txt; //Variant
	private TextView Pal_txt; //PatchLevel
	private TextView oid_txt; //OwnerId
	private TextView nat_txt; //Nationality
	private int eSource;
	private int wStatus;
	private int eSeverity;
	private String au8SerialNo;
	private String u16SmartcardType;
	private int Version;
	private int u8Build;
	private String u8Variant;
	private String u16OwnerId;
	private String au8Nationality;
	private String u16PatchLevel;

	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ird_smc_info);
		boolean isValidCard = false;
        int []buff_len = {1024};
        byte []buff = new byte[buff_len[0]];
		/*Card Number*/
		Ca ca = new Ca(DVB.GetInstance());
	    Log.i("IrdcaCmdProcess", "xinhua_ret:");
		int ret = ca.IrdcaCmdProcess(buff,buff_len);
		//int ret = ca.CaCmdProcess(buff,buff_len);
        Log.i("IrdcaCmdProcess", "xinhua_ret:"+ret);

		if((ret == 0)&&(0 != buff_len[0]))
		{
			String jsonStr = new String(buff, 0, buff_len[0]);
			if(parsesmcInfoJson(jsonStr) == 0){
        Log.i("IrdcaCmdProcess", "31313xinhua_ret:"+ret);
		/*
        System.out.println("Irdca_cmd_process eSource:\n"+eSource );
        System.out.println("Irdca_cmd_process u16SmartcardType:\n"+u16SmartcardType);
        System.out.println("Irdca_cmd_process au8SerialNo\n",au8SerialNo);
        System.out.println("Irdca_cmd_process u16PatchLevel:%x\n"+u16PatchLevel);
        System.out.println("Irdca_cmd_process u8Build:\n"+u8Build);
        System.out.println("Irdca_cmd_process u8Variant\n",u8Variant);
       	System.out.println("Irdca_cmd_process u16PatchLevel:\n"+u16OwnerId);
        System.out.println("Irdca_cmd_process Version%\n",Version);
        System.out.println("Irdca_cmd_process au8Nationality:\n"+au8Nationality);
		*/
			//sta_txt = (TextView)findViewById(R.id.textView10);
			//sta_txt.setText(au8SerialNo);

			cn_txt = (TextView)findViewById(R.id.textView4);
			cn_txt.setText(au8SerialNo+"0");
			
			typ_txt = (TextView)findViewById(R.id.textView8);
			typ_txt.setText(u16SmartcardType);
			
			ver_txt = (TextView)findViewById(R.id.textView6);
			ver_txt.setText(String.valueOf(Version));
			
			bl_txt = (TextView)findViewById(R.id.tv_build);
			bl_txt.setText(String.valueOf(u8Build));
			
			var_txt = (TextView)findViewById(R.id.textView2);
			var_txt.setText(u8Variant);
			
			Pal_txt = (TextView)findViewById(R.id.textView22);
			Pal_txt.setText(u16PatchLevel);
			
			oid_txt = (TextView)findViewById(R.id.textView24);
			oid_txt.setText(u16OwnerId);
			
			nat_txt = (TextView)findViewById(R.id.textView26);
			nat_txt.setText(au8Nationality);

				//showsmcInfo();
			}
			else{
				Log.i("IrdcaCmdProcess", "parsesmcInfoJson,fail_xinhua");
			}
		}else{
			Log.i("IrdcaCmdProcess", "parsesmcInfoJson,fail_xinhua22");
		}

/*		
        int[] cardid = new int[1];
		ca.CaGetCardId(cardid);
		cn_txt = (TextView)findViewById(R.id.textView10);
		cn_txt.setText(String.valueOf(cardid[0]));
			
		Stb Atr
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
*/
		/*AcountNo
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
*/
		/*Money
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
*/
		/*Area code
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
		*/	
		/*CA StbCasver
		final int strLen = 11;
		byte [] StbCasver = new byte[strLen];
		ret = ca.CaGetStbCasVer(StbCasver, strLen);
	    Log.i("CaGetStbCasVer", "ret:"+ret);
		cs_txt = (TextView)findViewById(R.id.textView2);	
		//String s_version = new String(ca_version.caversion);
		cs_txt.setText(String.valueOf(StbCasver[0]));	
*/		
		/*PAIR
			
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
*/	
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



    
    private int parsesmcInfoJson(String jsonStr){
    	 int ret = 0;
    	
		 System.out.println("xinhua_jsonStr:"+jsonStr); //输出字符串
	 
		 try {			
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	eSource = jsonObject.getInt("eSource");
		  	wStatus = jsonObject.getInt("wStatus");
		  	eSeverity = jsonObject.getInt("eSeverity");
		  	au8SerialNo = jsonObject.getString("au8SerialNo");
		  	u16SmartcardType = jsonObject.getString("u16SmartcardType");
		  	Version = jsonObject.getInt("Version");
		  	u8Build = jsonObject.getInt("u8Build");
		  	u8Variant = jsonObject.getString("u8Variant");
			u16PatchLevel = jsonObject.getString("u16PatchLevel");
			u16OwnerId = jsonObject.getString("u16OwnerId");
			au8Nationality = jsonObject.getString("au8Nationality");
		} catch (JSONException ex) {  
				System.out.println("get JSONObject fail_xinhua");// 异常处理代码  
				ret = -1;
		}		
    	return ret;
    	
    }


}
