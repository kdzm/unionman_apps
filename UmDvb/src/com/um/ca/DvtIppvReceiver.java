package com.um.ca;

import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvb.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.ui.Dvbplayer_Activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DvtIppvReceiver extends BroadcastReceiver{

	LinearLayout ippvLayout;
	LinearLayout ipptPeriodLayout;
	private TextView tvPriceType;
	private EditText ipptPeriod;
	private EditText ed_pin;
	private Button btn_buy_ipp;
	private TextView tvProgemName;
	private TextView tvippType;
	private TextView tvStartTime;
	private TextView tvDuration;
	private TextView tvOrderPrice;
	
	private Context mContext;
	private byte[] verifypintmp;
	private int priceType;
	private String progName;
	private int programType;
	private long startTime;
	private long ippDuration;
	private int orderPrice;
	private int curTppTapPrice;
	private int curTppNoTapPrice;
	private int curCppTapPrice;
	private int curCppNoTapPrice;
	private int curInterval;
	private int byUnit;
	private int ipptPeriodNum = 0;
	private static int ippEcmPid;
	private final String TAG = "DvtIppvReceiver";
	
	private final int DVTCAS_IPP_BYUNIT_MIN = 0;
	private final int DVTCAS_IPP_BYUNIT_HOUR = 1;
	private final int DVTCAS_IPP_BYUNIT_DAY = 2;
	private final int DVTCAS_IPP_BYUNIT_MON = 3;    
	private final int DVTCAS_IPP_BYUNIT_YER = 4;
	
	private final int CAS_TPP_TAP_PRICE = 0;          /*不回传可录像*/
	private final int CAS_TPP_NOTAP_PRICE = 1;        /*不回传不可录像*/
	private final int CAS_CPP_TAP_PRICE = 2;          /*回传可录像*/
	private final int CAS_CPP_NOTAP_PRICE = 3;        /*回传不可录像*/
	private Activity mInstance = null;
	public DvtIppvReceiver(Activity activity) {
		mInstance = activity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!DVB.isServerAlive()) {
			return;
		}
		// TODO Auto-generated method stub
		if (intent.getAction().equals("com.um.dvb.START_DVT_IPPV")){
			Log.i("IppvReceiver", "com.um.dvb.START_DVT_IPPV");
			mContext = context;
			startIppv();
		}
		else if (intent.getAction().equals("com.um.dvb.STOP_IPPV")){
			Log.i("IppvReceiver", "com.um.dvb.STOP_IPPV");
			mContext = context;
			hideIppv();
		}
	}
	
    private void startIppv()
    {
    	if(null == mInstance ){
    		Log.i("startIppv", "notFullScrenplay: return");
    		return;
    	}
    	Dvbplayer_Activity.mDisVolAdjust = true;
    	Dvbplayer_Activity.setCloseIppFlag(false);
    	Dvbplayer_Activity.setIppOpenFlag(true);
    	
        LinearLayout blankIppvLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
        ippvLayout = (LinearLayout) LayoutInflater.from(mInstance).inflate(R.layout.dvt_ipp_pop, null);
        blankIppvLayout.removeAllViews();
        blankIppvLayout.addView(ippvLayout);
        
        EditText et_input_period = (EditText) ippvLayout.findViewById(R.id.et_input_period);
        et_input_period.setFocusable(true);
        et_input_period.requestFocus();
        
		final Ca ca = new Ca(DVB.getInstance());
		int[] buffLen = {1024};
		byte[] buff = new byte [buffLen[0]];
		
		int ret = ca.CaGeIpppopInfo(buff, buffLen);
		System.out.printf("CaGeIpppopInfo,ret:%d,buffLen:%d\n", ret, buffLen[0]); 
		 
		if((ret == 0)&&(0 != buffLen[0]))
		{
			String jsonStr = new String(buff, 0, buffLen[0]);
			if(parseIppInfoJson(jsonStr) == 0){
				showIppInfo();
			}
			else{
				Log.i("CaGeIpppopInfo", "parseIppInfoJson,fail");
			}
		}else{
			Log.i("CaGeIpppopInfo", "CaGeIpppopInfo,fail");
		}
		
		SharedPreferences stbPreferences = mContext.getSharedPreferences("DVTCA_ECMPID",Context.MODE_WORLD_READABLE);
		ippEcmPid = stbPreferences.getInt("DVTCA_ECMPID", 0);
		Log.i(TAG, "DVTCA_ECMPID,ippEcmPid: "+ippEcmPid);
		/* =========================Buy Button============================== */
		btn_buy_ipp = (Button) ippvLayout.findViewById(R.id.btn_dvt_buy_ipp);
		btn_buy_ipp.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				buyDvtIppProgram();
			}
		});
		
    }
    
    private int parseIppInfoJson(String jsonStr){
    	 int ret = 0;

		 System.out.println("jsonStr:"+jsonStr); //输出字符串
	 
		 try {
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	priceType = jsonObject.getInt("bookedPriceType");
		  	progName = jsonObject.getString("serviceName");
		  	programType = jsonObject.getInt("ippStatus");
		  	startTime = jsonObject.getLong("startTime");
		  	ippDuration = jsonObject.getLong("duration");
			curTppTapPrice = jsonObject.getInt("curTppTapPrice");
			curTppNoTapPrice = jsonObject.getInt("curTppNoTapPrice");
			curCppTapPrice = jsonObject.getInt("curCppTapPrice");
			curCppNoTapPrice = jsonObject.getInt("curCppNoTapPrice");
			curInterval = jsonObject.getInt("curInterval");
			byUnit = jsonObject.getInt("byUnit");
		  	
		  	System.out.printf("priceType:%d, progName:%s, programType:%d, startTime:%d, ippDuration:%d\n",
		  			priceType, progName, programType, startTime, ippDuration);
		  	System.out.printf("curTppTapPrice:%d, curTppNoTapPrice:%d, curCppTapPrice:%d, curCppNoTapPrice:%d\n",
		  			curTppTapPrice, curTppNoTapPrice, curCppTapPrice, curCppNoTapPrice);
		  	System.out.printf("curInterval:%d, byUnit:%d\n",curInterval, byUnit);

		} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");// 异常处理代码  
				ret = -1;
		}		
    	return ret;
    	
    }
    
    private void showIppInfo(){
    	findView();
    	/*======================Price Type============================*/
		switch(priceType)
		{
			case CAS_TPP_TAP_PRICE:
				tvPriceType.setText(R.string.tpp_tap_price);
				orderPrice = curTppTapPrice;
				break;
			case CAS_TPP_NOTAP_PRICE:
				tvPriceType.setText(R.string.tpp_notap_price);
				orderPrice = curTppNoTapPrice;
				break;
			case CAS_CPP_TAP_PRICE:
				tvPriceType.setText(R.string.cpp_tap_price);
				orderPrice = curCppTapPrice;
				break;
			case CAS_CPP_NOTAP_PRICE:
				tvPriceType.setText(R.string.cpp_notap_price);
				orderPrice = curCppNoTapPrice;
				break;
			default:
				tvPriceType.setText(R.string.tpp_notap_price);
				orderPrice = curTppNoTapPrice;
				break;
		}
		
		/*======================Prog Name===========================*/
		tvProgemName.setText(progName);
		
		/*======================Program Type===========================*/
		String strProgType = (0x8 ==  programType)? "IPPV":"IPPT";
		tvippType.setText(strProgType);
		
		/*======================Start Time===========================*/
		StringBuffer temp = this.dvtYmdCalculate(startTime);
		tvStartTime.setText(temp);
		
		/*======================Ipp Duration===========================*/
		StringBuffer strTime = timeToDHMS(ippDuration);
		tvDuration.setText(strTime);
		
		/*======================OrderPrice===========================*/
		StringBuffer strPrice;
		if(0x8 ==  programType){
			strPrice = toIppvPriceStr(priceType, orderPrice);
		}else{
		    strPrice = toIpptPriceStr(priceType, orderPrice, curInterval, byUnit);
		}	
		
		tvOrderPrice.setText(strPrice);
		
    	/*=========================Ippt Period=========================*/
		if(0x8 ==  programType){
			Log.i(TAG, "ipptPeriodLayout.removeAllViews()");
			
			ipptPeriodLayout.removeAllViews();
		}
		
    	/*=========================Enter PIN Value=========================*/
		ed_pin.setText(ed_pin.getText().toString());
		ed_pin.setSelection(ed_pin.getText().toString().length());
		String verifypinstr = String.valueOf(ed_pin.getText());
		verifypintmp = verifypinstr.getBytes();
    }
    
    private void buyDvtIppProgram()
    {
    	final Ca ca = new Ca(DVB.getInstance());
		if(getCardStatus() != true){
			Toast.makeText(mContext, mContext.getResources().getText(R.string.ca_insert_card), Toast.LENGTH_LONG).show();
			return;
		 }
		 
		 if(0x8 !=  programType){
			 String strPeriod = String.valueOf(ipptPeriod.getText());
			 if(strPeriod.isEmpty()){
				 Toast.makeText(mContext, mContext.getResources().getText(R.string.invalid_period), Toast.LENGTH_LONG).show();
				 return;
			 }
			 ipptPeriodNum = Integer.parseInt(strPeriod);
			 
			 if(0 == ipptPeriodNum){
				 Toast.makeText(mContext, mContext.getResources().getText(R.string.invalid_period), Toast.LENGTH_LONG).show();
				 return;
			 }
		 }
		 
		 if(0 == orderPrice){
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.invalid_price), Toast.LENGTH_LONG).show();
			 return;
		 }
		 
		 String str = String.valueOf(ed_pin.getText());
		 byte[] temp1 = str.getBytes();
		
		 if(temp1.length != 8){
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.pin_length_error), Toast.LENGTH_LONG).show();
			 return;
		 }
		 
		 if(ca.CaVerifyPin(temp1,8) != 0){
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_pin_err), Toast.LENGTH_LONG).show();
			 Log.i("CA","CA CaVerifyPin fail");	
			 return;
		 }
		 
		 sureOrCancleToBuy();
		 
	}
    
    private void sureOrCancleToBuy(){
		
		final Ca ca = new Ca(DVB.getInstance());
		
		if(null == mInstance ){
    		Log.i(TAG, "sureOrCancleToBuy,notFullScrenplay: return");
    		return;
    	}
		AlertDialog.Builder buyIppBuilder = new AlertDialog.Builder(mInstance);
		LinearLayout layout = (LinearLayout) LayoutInflater.from(mInstance).inflate(R.layout.dvt_buy_ipp_tip, null);
		buyIppBuilder.setView(layout);
		
		Button okButton = (Button)layout.findViewById(R.id.ok_btn);
		Button cancleButton = (Button) layout.findViewById(R.id.cancle_btn);
		final AlertDialog buyIppDialog = buyIppBuilder.create();
		buyIppDialog.show();
		
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.ok_btn:{
						verifyToBuyIppProg();
						buyIppDialog.hide();
					}
						break;
					case R.id.cancle_btn:{	
						buyIppDialog.hide();
						hideIppv();
					}	
						break;
					default:
						break;
					}
			}
		};
		
		okButton.setOnClickListener(onClickListener);
		cancleButton.setOnClickListener(onClickListener);
		
	}
    
    private void verifyToBuyIppProg(){
    	final Ca ca = new Ca(DVB.getInstance());
    	Parcel ippParam = caWriteIppinfo();    			
		int book_ret = ca.CaBookIpp(ippParam);

		 Log.i(TAG, "book_ret:" +book_ret);
		 
		 if (book_ret == 0)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_order_success), Toast.LENGTH_LONG).show();
				hideIppv();
			}
			else if (book_ret == 0x8000002e)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_lack_money), Toast.LENGTH_LONG).show();
			}
			else if (book_ret == 0x8000002f)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_need_verypin), Toast.LENGTH_LONG).show();

			}
			else if (book_ret == 0x80000031)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_slot_invalide), Toast.LENGTH_LONG).show();
			}
			else if (book_ret == 0x80000032)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_product_expired), Toast.LENGTH_LONG).show();									
			}
			else if (book_ret == 0x80000036)
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_ipp_price_error), Toast.LENGTH_LONG).show();									
			}
			else
			{
				Toast.makeText(mContext, mContext.getResources().getText(R.string.dvt_caerr_unknown), Toast.LENGTH_LONG).show();									
			}
    }
    
    private void hideIppv()
    {
    	if(null == mInstance ){
    		Log.i("hideIppv", "notFullScrenplay: return");
    		return;
    	}
    	Dvbplayer_Activity.mDisVolAdjust = false;
    	Dvbplayer_Activity.setCloseIppFlag(true);
    	Dvbplayer_Activity.setIppOpenFlag(false);
    	LinearLayout blankIppvLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
        blankIppvLayout.removeAllViews();
        
        final Ca ca = new Ca(DVB.getInstance());
        Log.i(TAG,"BookIppsOver,ippEcmPid: "+ippEcmPid);
        ca.CaInquireBookIppsOver(ippEcmPid);
    }

	private boolean  IsLeapYear(int iYear)
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

	private StringBuffer dvtYmdCalculate(long dayTime)	{
		int nTemp;
		int year = 1970;
		int month;
		int day;
		int hour;
		int min;
		int second;
		int tempDays = (int)(dayTime + 8*3600)/(3600*24);//to days
		int tempSeconds = (int)(dayTime + 8*3600)%(3600*24);//to seconds
		StringBuffer strDayTime = new StringBuffer();
		
	    do { 
	     /*year.*/
	        nTemp = (IsLeapYear(year) == true)?366:365;
	        if(tempDays - nTemp < 0)
	            break;
	        tempDays -= nTemp;
	        year ++;
	    }while(true);

	    month = 1;
	    do {  
	     /*month.*/
	        if (2 == month)  {
	            nTemp = (IsLeapYear(year))? 29: 28;
	        }
	        else if(4 == month || 6 == month || 9 == month || 11 == month)
	            nTemp = 30;
	        else
	            nTemp = 31;
	        if(tempDays - nTemp < 0)
	            break;
	        tempDays -= nTemp;
	        month ++;
	    }while(true);
	    
	    if(1 > month || 12 < month)  {
	        return null;
	    }

	    day = 1;
	    day += tempDays; /*day.*/
	    if(1 > day || 31 < day) {
	        return null;
	    }
		
	    hour = tempSeconds / 3600;
	    min = (tempSeconds % 3600) / 60;
	    second = (tempSeconds % 3600) % 60;
	    
	    if((hour > 24) || (min > 59) || (second > 59)){
	    	return null;
	    }
	    	
	    strDayTime.append(year);
	    strDayTime.append("/");
	    strDayTime.append(month);
	    strDayTime.append("/");
	    strDayTime.append(day);
	    strDayTime.append(" ");
	    strDayTime.append(hour);
	    strDayTime.append(":");
	    strDayTime.append(month);
	    strDayTime.append(":");
	    strDayTime.append(day);
	    
	    return strDayTime;
	    
	}
	
	private StringBuffer timeToDHMS(long time)
	{
	   int day;
	   int hour;
	   int min;
	   int second;
	   
	   day = (int) (time/(3600*24));
	   hour = (int) (time%(3600*24)/3600);
	   min = (int) (time%3600/60);
	   second = (int) (time%60);
	   
	   StringBuffer strTime = new StringBuffer();
	   strTime.append(day);
	   strTime.append(mContext.getResources().getString(R.string.str_day));
	   
	   strTime.append(hour);
	   strTime.append(mContext.getResources().getString(R.string.str_hour));
	   
	   strTime.append(min);
	   strTime.append(mContext.getResources().getString(R.string.str_min));
	   
	   strTime.append(second);
	   strTime.append(mContext.getResources().getString(R.string.str_second));
	   
	   return strTime;
	}

	private StringBuffer toIppvPriceStr(int priceType,  int orderPrice){
		StringBuffer strPrice = new StringBuffer();
		String strTemp = new String();
		
		switch(priceType){
			case CAS_TPP_TAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.tpp_tap_price);
				break;
			case CAS_TPP_NOTAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.tpp_notap_price);
				break;
			case CAS_CPP_TAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.cpp_tap_price);
				break;
			case CAS_CPP_NOTAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.cpp_notap_price);
				break;
			default:
				strTemp = mContext.getResources().getString(R.string.tpp_notap_price);
				break;
		}
		strPrice.append(strTemp);
		
		if(0 == orderPrice){
			strPrice.append("(");
			strPrice.append(mContext.getResources().getString(R.string.str_none));
			strPrice.append(")");
		}else{
			strPrice.append("(");
			strPrice.append(orderPrice/100);
			strPrice.append(".");
			strPrice.append(orderPrice%100);
			strPrice.append(mContext.getResources().getString(R.string.str_yuan));
			strPrice.append(")");
		}
		
		return strPrice;
	}
	
	private StringBuffer toIpptPriceStr(int priceType,  int orderPrice, int curInterval, int byUnit){
		StringBuffer strPrice = new StringBuffer();
		String strTemp = new String();
		String strByUnit;
		
		switch(priceType){
			case CAS_TPP_TAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.tpp_tap_price);
				break;
			case CAS_TPP_NOTAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.tpp_notap_price);
				break;
			case CAS_CPP_TAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.cpp_tap_price);
				break;
			case CAS_CPP_NOTAP_PRICE:
				strTemp = mContext.getResources().getString(R.string.cpp_notap_price);
				break;
			default:
				strTemp = mContext.getResources().getString(R.string.tpp_notap_price);
				break;
		}
		strPrice.append(strTemp);
		
		if(0 == orderPrice){
			strPrice.append("(");
			strPrice.append(mContext.getResources().getString(R.string.str_none));
			strPrice.append(")");
		}else{
			strPrice.append("(");
			strPrice.append(orderPrice/100);
			strPrice.append(".");
			strPrice.append(orderPrice%100);
			strPrice.append(mContext.getResources().getString(R.string.str_yuan));
			strPrice.append("/");
		}
		
		if(0 != curInterval){
			strPrice.append(curInterval);
		}
		
		switch(byUnit)
		{
			case DVTCAS_IPP_BYUNIT_MIN:
				strByUnit = mContext.getResources().getString(R.string.str_min);
				break;
			case DVTCAS_IPP_BYUNIT_HOUR:
				strByUnit = mContext.getResources().getString(R.string.str_hour);
				break;
			case DVTCAS_IPP_BYUNIT_DAY:
				strByUnit = mContext.getResources().getString(R.string.str_day);
				break;
			case DVTCAS_IPP_BYUNIT_MON: 	
				strByUnit = mContext.getResources().getString(R.string.str_month);
				break;
			case DVTCAS_IPP_BYUNIT_YER:
				strByUnit = mContext.getResources().getString(R.string.str_year);
				break;
			default:
				strByUnit = mContext.getResources().getString(R.string.str_min);
				break;
			
		}
		strPrice.append(strByUnit);
		strPrice.append(")");
		
		return strPrice;
	}
	
	private void findView(){
		ipptPeriodLayout = (LinearLayout) ippvLayout.findViewById(R.id.layoutPeriod);
		tvPriceType = (TextView) ippvLayout.findViewById(R.id.tvPriceType);
		ipptPeriod = (EditText) ippvLayout.findViewById(R.id.et_input_period);
		ed_pin = (EditText) ippvLayout.findViewById(R.id.et_input_pin);
		btn_buy_ipp = (Button) ippvLayout.findViewById(R.id.btn_dvt_buy_ipp);
		tvProgemName = (TextView) ippvLayout.findViewById(R.id.dvt_program_name);
		tvippType = (TextView) ippvLayout.findViewById(R.id.prog_type);
		tvStartTime = (TextView) ippvLayout.findViewById(R.id.dvt_ipp_startime);
		tvDuration = (TextView) ippvLayout.findViewById(R.id.dvt_ipp_duration);
		tvOrderPrice = (TextView) ippvLayout.findViewById(R.id.dvt_ipp_order_price);
	}
	
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d(TAG, "ret:" +ret);
    	Log.d(TAG, "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
	
    private Parcel caWriteIppinfo()
    {    	
		int operator_id = 0;
		int prod_id = 0;
		int slot_id = 0;
		byte product_name[] = new byte[21];
		int start_time = 0;
		int end_time = 0;
		int duration = 0;
		byte serviceName[] = new byte[21];
		int cur_tpp_tap_price = 0;
		int cur_tpp_notap_price = 0;
		int cur_cpp_tap_price = 0;
		int cur_cpp_notap_price = 0;
		int booked_price = 0;
		int booked_price_type = 0;
		int booked_interval = 0;
		int cur_interval = 0;
		int ipp_status = 0;    
		int ipp_type = 0;
		int taping = 0;
		int price = 0;
		int expired_date = 0;
		int price_code = 0;
		int ecm_pid = 0;
		int buy_program = 0;
		int by_unit = 0;
		int ippt_period = 0;
		int pinLen = 0;
		byte pin [] = new byte[8];
		
		booked_price_type = priceType;
	  	ipp_status = programType;
	  	start_time = (int)startTime;
	  	duration = (int)ippDuration;
	  	cur_tpp_tap_price = curTppTapPrice;
	  	cur_tpp_notap_price = curTppNoTapPrice;
	  	cur_cpp_tap_price = curCppTapPrice;
	  	cur_cpp_notap_price = curCppNoTapPrice;
	  	booked_interval = curInterval;
	  	by_unit = byUnit;
	  	ippt_period = ipptPeriodNum;
	  	ecm_pid = ippEcmPid;
	  	
	  	System.out.printf("booked_price_type:%d, ipp_status:%d, start_time:%d, duration:%d\n",
	  			booked_price_type, ipp_status, start_time, duration);
	  	System.out.printf("cur_tpp_tap_price:%d, cur_tpp_notap_price:%d, cur_cpp_tap_price:%d, cur_cpp_notap_price:%d\n",
	  			cur_tpp_tap_price, cur_tpp_notap_price, cur_cpp_tap_price, cur_cpp_notap_price);
	  	System.out.printf("booked_interval:%d, by_unit:%d, ippt_period:%d\n",booked_interval, by_unit, ippt_period);
			
    	Parcel ippParam = Parcel.obtain();		
    	ippParam.writeInterfaceToken("android.Dvbstack.ICa");
		ippParam.writeInt(operator_id);
		ippParam.writeInt(prod_id);
		ippParam.writeInt(slot_id);
		ippParam.writeByteArray(product_name);
		ippParam.writeInt(start_time);
		ippParam.writeInt(end_time);
		ippParam.writeInt(duration);
		ippParam.writeByteArray(serviceName);		
		ippParam.writeInt(cur_tpp_tap_price);
		ippParam.writeInt(cur_tpp_notap_price);
		ippParam.writeInt(cur_cpp_tap_price);
		ippParam.writeInt(cur_cpp_notap_price);
		ippParam.writeInt(booked_price_type);
		ippParam.writeInt(booked_interval);
		ippParam.writeInt(cur_interval);
		ippParam.writeInt(ipp_status);
		ippParam.writeInt(ipp_type);
		ippParam.writeInt(taping);
		ippParam.writeInt(price);
		ippParam.writeInt(expired_date);
		ippParam.writeInt(price_code);
		ippParam.writeInt(ecm_pid);
		ippParam.writeInt(buy_program);
		ippParam.writeInt(by_unit);
		ippParam.writeInt(ippt_period);
		ippParam.writeInt(pinLen);
		ippParam.writeByteArray(pin);		
		
    	return ippParam;
    	
    }
    
    
}
