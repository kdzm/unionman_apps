package com.um.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.Ca.Ca_Rating;
import com.um.dvbstack.CaEvent;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class Dvt_smc_info extends AppBaseActivity{
	private final String TAG = "Dvt_smc_info";
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
		Ca ca = new Ca(DVB.getInstance());
        int[] cardid = new int[1];
		ca.CaGetCardId(cardid);
		cn_txt = (TextView)findViewById(R.id.textView10);
		cn_txt.setText(String.valueOf(cardid[0]));
				
		/*CA ScCosver*/	
		int [] ScCosver = new int[1];
		ca.CaGetScCosVer(ScCosver);		
		cv_txt = (TextView)findViewById(R.id.textView4);	
		//String s_version = new String(ca_version.caversion);
		cv_txt.setText(String.valueOf(ScCosver[0]));

		/*CA StbCasver*/
		final int strLen = 11;
		byte [] StbCasver = new byte[strLen];
		ca.CaGetStbCasVer(StbCasver, strLen);		
		cs_txt = (TextView)findViewById(R.id.textView8);	
		String s_version = new String(StbCasver);
		cs_txt.setText(String.valueOf(s_version));

		/*CA ManuName*/	
		int [] manuname_len = {30};
		byte [] ManuName = new byte[30];
		ca.CaGetManuName(ManuName, manuname_len);
		String s_manuname = new String(ManuName, 0, manuname_len[0]);
		
		cm_txt = (TextView)findViewById(R.id.textView6);	
		//String s_version = new String(ca_version.caversion);
		cm_txt.setText(s_manuname);		

		/*Area code*/
		TextView pAreaCode = (TextView)this.findViewById(R.id.tv_area_code);
		TextView pAreaFlag = (TextView)this.findViewById(R.id.textView2);
		TextView pAreaTime = (TextView)this.findViewById(R.id.textView22);
		
        int []buff_len = {80};
        byte []buff = new byte[buff_len[0]];
        int area_code = 0;
        int area_flag = 0;
        int area_time = 0;
        int ret = ca.CaGetAreaInfo(buff, buff_len);
        Log.i("CaGetAreaInfo", "ret:"+ret);
        Log.i("CaGetAreaInfo", "buff_len:"+buff_len[0]);
        
		int [] operid = new int[20];
		int [] opernum = new int[1];
		int retOperInfo = 1;
		int retOperID = ca.CaGetOperID(operid, opernum);
        if ((retOperID == 0) && (opernum[0] > 0))
        {
			byte [] opername = new byte[100];
			retOperInfo = ca.CaGetOperatorInfo(operid[0],opername);
			
			if(0 == retOperInfo){
				isValidCard = true;
			}
        }
        
        if(true == isValidCard){
	        if((ret == 0) && (buff_len[0] != 0))
	        {
	        	 String jsonStr = new String(buff, 0, buff_len[0]);
				 System.out.println("jsonStr:"+jsonStr); 
				 
				 try {  
					  	JSONObject jsonObject = new JSONObject(jsonStr);
					  	area_code = jsonObject.getInt("areacode");
					  	area_flag = jsonObject.getInt("areaflag");
					  	area_time = jsonObject.getInt("areatime");
					    
					} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					}  
	        }	
	        
	        pAreaCode.setText(String.valueOf(area_code));
	        
	        switch(area_flag)
			{
				case 0:
					pAreaFlag.setText(R.string.dvt_arelock_unuse);
				break;			
				case 1:
					pAreaFlag.setText(R.string.dvt_arelock_use);
					break;
				default:
					break;
			}
	        
			 String send_time_str = null;
			 Dvt_email_read email_read = new Dvt_email_read();
			 //email_read.calculateSendDate(time);
			 Log.i("Tf_email_read", "send_time"+area_time);
			 try {
				 send_time_str = email_read.calculateSendDate(area_time);
			 } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 
	        pAreaTime.setText(send_time_str);
        }
       
		/*Watch Level*/
		Ca_Rating ca_rating = new Ca_Rating();
		ca.CaGetRating(ca_rating);
		Log.i("Dvt_smc_info","CaGetRating:"+String.valueOf(ca_rating.carating));
		cr_txt = (TextView)findViewById(R.id.textView30);
		cr_txt.setText(String.valueOf(ca_rating.carating[0]));
		/*Working Time*/
		CaEvent wt = new CaEvent();
		wt = ca.CaparseWorkingTime();
		
		if(null != wt){
			 TextView pStartTime = (TextView)this.findViewById(R.id.textView32);
			 TextView pEndTime = (TextView)this.findViewById(R.id.textView33);
			 
			 Time startTime = wt.CagetStartTime();
			 Time endTime = wt.CagetEndTime();
			
			 String s_hour = convertToTwoDigit(startTime.hour);
			 String s_minute = convertToTwoDigit(startTime.minute);
			 String e_hour = convertToTwoDigit(endTime.hour);
			 String e_minute = convertToTwoDigit(endTime.minute);
			 pStartTime.setText(s_hour + ":" + s_minute + "-");
			 pEndTime.setText(e_hour + ":" + e_minute);
		}
/*		
		if(true == isValidCard){
	        int []motherinfo_len = {80};
	        byte []motherinfo = new byte[motherinfo_len[0]];
	        int mother_child_id = 0;
	        int Ismother = 0;
	        int operatorid = 0;
	        int mother_ret = ca.CaGetMotherInfo(operatorid, motherinfo, motherinfo_len);
	        //int mother_ret = 1;
	        Log.i("CaGetMotherInfo", "ret:"+ret);
	        Log.i("CaGetMotherInfo", "buff_len:"+motherinfo_len[0]);
	        if((mother_ret == 0)&&(motherinfo_len[0] != 0))
	        {
	        	 String jsonStr_motherinfo = new String(motherinfo, 0, motherinfo_len[0]);
				 System.out.println("jsonStr:"+jsonStr_motherinfo); 
				 
				 try {  
					  	JSONObject jsonObject_motherinfo = new JSONObject(jsonStr_motherinfo);
					  	mother_child_id = jsonObject_motherinfo.getInt("mother_card_id");
					  	Ismother = jsonObject_motherinfo.getInt("ismother");
					    
					} catch (JSONException ex) {  
						System.out.println("get JSONObject fail");
					}  
			 
				TextView pMotheorchild = (TextView)this.findViewById(R.id.textView26);
				TextView pIs_mother = (TextView)this.findViewById(R.id.textView24);
				
				if ((Ismother == 1) && (mother_child_id != 0) )
				{
					pIs_mother.setText(R.string.dvt_child_card);
					pMotheorchild.setText(String.valueOf(mother_child_id));										
				}
				else if (Ismother == 0)
				{
					pIs_mother.setText(R.string.dvt_parent_card);
					pMotheorchild.setText(String.valueOf(0));	
				}
				else
				{
					Log.i("CaGetMotherInfo", "nothing");
				}
				
	        }
		}
*/		
		/*CA Pinstate*/	
		int [] Pinstate = new int[1];
		ca.CaGetPinState(Pinstate);		
		TextView pinstate = (TextView)findViewById(R.id.textView28);	
		if (1 == Pinstate[0])
		{
			pinstate.setText(R.string.dvt_pin_lock);					
		}
		else
		{			
			pinstate.setText(R.string.dvt_pin_unlock);					
		}

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
	
	@Override
	protected void onPause(){
        super.onPause();
        finish();
        Log.v(TAG, "onPause:finish()");
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
