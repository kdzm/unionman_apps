package com.um.ui;

import java.io.UnsupportedEncodingException;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import com.um.wfca.R;
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

public class Wf_smc_info extends AppBaseActivity{
	private TextView sta_txt;// status text
	private TextView cn_txt;//card number text	
	private TextView typ_txt; //type 
	private TextView ver_txt; //version
	private TextView bl_txt; //bulid
	private TextView var_txt; //Variant
	private TextView Pal_txt; //PatchLevel
	/*CA version*/
	private TextView cv_txt; //ca version
	private int Stb_num;
	private String Identif;
	private String Expire_date;
	private String Description;
	private String Provider;
	private String Utc_date;
	
	int cmdType;
	int lparam;
	int rparam;

	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wf_smc_info);
		boolean isValidCard = false;
		/*Card Number*/
		Ca ca = new Ca(DVB.GetInstance());
        int[] cardid = new int[1];
        int []buff_len = {1024};
        byte []buff = new byte[buff_len[0]];
		ca.CaGetCardId(cardid);
		cn_txt = (TextView)findViewById(R.id.textView10);
		cn_txt.setText(String.valueOf(cardid[0]));
		int ret = ca.WfcaCmdProcess(100,buff,buff_len);
        Log.i("WfcaCmdProcess", "xinhua_ret:"+ret);
		if((ret == 0)&&(0 != buff_len[0]))
		{
			String jsonStr = new String(buff, 0, buff_len[0]);
			if(parsesmcInfoJson(jsonStr) == 0){
	        Log.i("WfcaCmdProcess", "31313xinhua_ret:"+ret);

			cn_txt = (TextView)findViewById(R.id.textView4);
			cn_txt.setText(String.valueOf(Stb_num));
			
			typ_txt = (TextView)findViewById(R.id.textView8);
			typ_txt.setText(Identif);
			
			ver_txt = (TextView)findViewById(R.id.textView6);
			ver_txt.setText(Provider);
			
			bl_txt = (TextView)findViewById(R.id.tv_descrition);
			bl_txt.setText(Description);
			
			var_txt = (TextView)findViewById(R.id.textView2);
			var_txt.setText(Expire_date);
			
			Pal_txt = (TextView)findViewById(R.id.textView22);
			Pal_txt.setText(Utc_date);
			
			}
			else{
				Log.i("IrdcaCmdProcess", "parsesmcInfoJson,fail_xinhua");
			}
		}else{
			Log.i("IrdcaCmdProcess", "parsesmcInfoJson,fail_xinhua22");
		}
		
		/*CA ScCosver  �汾*/	
		int [] ScCosver = new int[1];
		ca.CaGetScCosVer(ScCosver);		
		cv_txt = (TextView)findViewById(R.id.textView24);	
		//String s_version = new String(ca_version.caversion);
		cv_txt.setText(String.valueOf(ScCosver[0]));

	
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
		  	Stb_num = jsonObject.getInt("Stb_num");
		  	Identif = jsonObject.getString("Identif");
		  	Description = jsonObject.getString("Description");
		  	Expire_date = jsonObject.getString("Expire_date");
		  	Utc_date = jsonObject.getString("Utc_date");
		  	Provider = jsonObject.getString("Provider");
		} catch (JSONException ex) {  
				System.out.println("get JSONObject fail_xinhua");// 异常处理代码  
				ret = -1;
		}		
    	return ret;
    	
    }
	

}
