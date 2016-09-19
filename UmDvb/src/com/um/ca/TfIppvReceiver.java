package com.um.ca;


import org.json.JSONException;
import org.json.JSONObject;

import com.um.dvb.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.ui.Dvbplayer_Activity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TfIppvReceiver extends BroadcastReceiver{

	private static boolean isIppOpen = false;
	LinearLayout ippvLayout;
	private Button btn_tf_cancel;
	private EditText pin;
	private TextView ipp_status;
	private TextView tvs_id;
	private TextView slot_id;
	private TextView product_id;
	private TextView expired_date;
	private TextView charge_type;
	private TextView cur_no_tappping_price;
	private TextView cur_tapping_price;
	private Button btn_tf_view;
	private Button btn_tf_tape;
	private int ippv_price;
	private int priceCode;

	private Context mContext;
	private byte[] verifypintmp;
	private int ippStatus;
	private int operatorId;
	private int slotId;
	private long productId;
	private int curTapPrice;
	private int curNoTapPrice;
	private int ecmPid;
	private int expiredDate;
	private int year;
	private int  month;
	private int day;
	private final String TAG = "TfIppvReceiver";
	private final int ASCII_0 = 48;
	private Activity mInstance = null;
	
	public TfIppvReceiver(Activity activity) {
		mInstance = activity;
	}
    private void startIppv()
    {
    	if(null == mInstance ){
    		Log.i("startIppv", "notFullScrenplay: return");
    		return;
    	}
    	Dvbplayer_Activity.mDisVolAdjust = true;
    	Dvbplayer_Activity.setCloseIppFlag(true);
    	Dvbplayer_Activity.setIppOpenFlag(true);
    	
        LinearLayout blankIppvLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
        ippvLayout = (LinearLayout) LayoutInflater.from(mInstance).inflate(R.layout.tf_ipp_pop, null);
        blankIppvLayout.removeAllViews();
        blankIppvLayout.addView(ippvLayout);
        
        EditText et_input_pin = (EditText) ippvLayout.findViewById(R.id.et_input_pin);
        et_input_pin.setFocusable(true);
        et_input_pin.requestFocus();
        
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
		
		/* =========================Buy Button(View)============================== */
		btn_tf_view = (Button) ippvLayout.findViewById(R.id.btn_tf_view);
		btn_tf_view.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				ippv_price = curNoTapPrice;
				priceCode = 0;
				tf_buy_ippv();
			}
		});
		
		/* =========================Buy Button(Tape)============================== */
		btn_tf_tape = (Button) ippvLayout.findViewById(R.id.btn_tf_tape);
		btn_tf_tape.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				ippv_price = curTapPrice;
				priceCode = 1;
				tf_buy_ippv();
			}
		});
		
       
        /*=========================Cancel Button=========================*/
		btn_tf_cancel = (Button) ippvLayout.findViewById(R.id.btn_tf_cancel);
        btn_tf_cancel.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View arg0) {
				
				String str = String.valueOf(pin.getText());
				byte[] temp2 = str.getBytes();
				
				final Ca ca = new Ca(DVB.getInstance());
				int unbook_ret = 0;
				Parcel ippParam = caWriteIppinfoCancelBuy();    			
				int book_ret = ca.CaBookIpp(ippParam);
				hideIppv();
			}
			}
		);	
    }
    
    private int parseIppInfoJson(String jsonStr){
    	 int ret = 0;
    	
		 System.out.println("jsonStr:"+jsonStr); //输出字符串
	 
		 try {
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	ippStatus = jsonObject.getInt("ippStatus");
		  	operatorId = jsonObject.getInt("operatorId");
		  	slotId = jsonObject.getInt("slotId");
		  	productId = jsonObject.getLong("prodId");
		  	curTapPrice = jsonObject.getInt("curTppTapPrice");
		  	curNoTapPrice = jsonObject.getInt("curTppNoTapPrice");
		  	ecmPid = jsonObject.getInt("ecmPid");
		  	expiredDate = jsonObject.getInt("expiredDate");
		} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");// 异常处理代码  
				ret = -1;
		}		
    	return ret;
    	
    }
    
    private void showIppInfo(){
    	/*=========================Enter PIN Value=========================*/
		pin = (EditText)ippvLayout.findViewById(R.id.et_input_pin);
		pin.setText(pin.getText().toString());
		pin.setSelection(pin.getText().toString().length());
		String verifypinstr = String.valueOf(pin.getText());
		verifypintmp = verifypinstr.getBytes();
		
		/*=========================ipp status=========================*/
		ipp_status = (TextView)ippvLayout.findViewById(R.id.tv_ipp_type);
		ipp_status.setGravity(Gravity.CENTER);

		Log.i("ipp type:", "ippStatus:" +ippStatus);
		switch(ippStatus)
		{
			case 0:
				ipp_status.setText(R.string.tf_ippv_free_view);
				break;
			case 1:
				ipp_status.setText(R.string.tf_ippv_pay_view);
				break;
			case 2:
				ipp_status.setText(R.string.tf_ippt_payview);
				break;
			default:
				break;
		}
		
		/*=========================TvsID=========================*/
		tvs_id = (TextView)ippvLayout.findViewById(R.id.textView4);
		tvs_id.setText(String.valueOf(operatorId));
		
		/*=========================slot ID=========================*/
		slot_id = (TextView)ippvLayout.findViewById(R.id.textView1);
		slot_id.setText(String.valueOf(slotId));
		
		/*=========================product ID=========================*/
		product_id = (TextView)ippvLayout.findViewById(R.id.textView6);
		product_id.setText(String.valueOf(productId));
		
		/*======================Current tapping price=========================*/
		cur_tapping_price = (TextView)ippvLayout.findViewById(R.id.textView3);
		cur_tapping_price.setText(String.valueOf(curTapPrice));
		
		/*=========================Current no tappiing price===================*/
		cur_no_tappping_price = (TextView)ippvLayout.findViewById(R.id.textView8);
		cur_no_tappping_price.setText(String.valueOf(curNoTapPrice));
			
		/*=========================expired date=========================*/
		charge_type = (TextView)ippvLayout.findViewById(R.id.chargeintervaltextView);
		expired_date = (TextView)ippvLayout.findViewById(R.id.textView5);
		
		if(2 == (ippStatus & 0xff)) //IPPT
		{
			charge_type.setText(R.string.tf_ippv_free_view);
			expired_date.setText(String.valueOf(expiredDate));
		}
		else  //IPPV
		{
			charge_type.setText(R.string.expired_time);
			String temp = this.tf_YMD_calculate(expiredDate);
			expired_date.setText(String.valueOf(temp));
		}
		isIppOpen = true;
    }
    
    private void tf_buy_ippv()
    {
		 
		 String str = String.valueOf(pin.getText());
		 byte[] temp1 = str.getBytes();
		
		 if(temp1.length != 6)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
			 return;
		 }	 
		 Log.i("tf_buy_ippv", "ippv_price:" +ippv_price);
		
		 final Ca ca = new Ca(DVB.getInstance());
//		 int book_ret = 0;
//		 book_ret= ca.CaBookIpp(priceCode, ippv_price, true, ecmPid, temp1, 6);
		 Parcel ippParam = caWriteIppinfoToBuy();    			
		 int book_ret = ca.CaBookIpp(ippParam);
		
		 Log.i("tf_buy_ippv", "book_ret:" +book_ret);
		 
		 if( 0 == book_ret)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_buy_success), Toast.LENGTH_LONG).show();
			 hideIppv();
		 }
		 else if((0x80000012 == book_ret) || (book_ret == 0x80000021))
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_nosmc), Toast.LENGTH_LONG).show();
		 }
		 else if(book_ret == 0x80000026)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_no_room), Toast.LENGTH_LONG).show();
		 }
		 else if( 0x80000027 == book_ret)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_prog_status_invalid), Toast.LENGTH_LONG).show();
		 }
		 else if(0x8000000b == book_ret)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_data_not_fount_ippv), Toast.LENGTH_LONG).show();
		 }
		 else if( 0x8000000d == book_ret)
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_pin_err), Toast.LENGTH_LONG).show();
		 }
		 else
		 {
			 Toast.makeText(mContext, mContext.getResources().getText(R.string.tf_caerr_unknown), Toast.LENGTH_LONG).show();
		 }
	}

    private void hideIppv()
    {
    	if(null == mInstance ){
    		Log.i("hideIppv", "notFullScrenplay: return");
    		return;
    	}
    	Dvbplayer_Activity.mDisVolAdjust = false;
    	Dvbplayer_Activity.setIppOpenFlag(false);
    	LinearLayout blankIppvLayout = (LinearLayout) mInstance.findViewById(R.id.ippv_blank_layout);
        blankIppvLayout.removeAllViews();
        isIppOpen = false;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (!DVB.isServerAlive()) {
			return;
		}
		
		Log.i(TAG, "isIppOpen:"+isIppOpen);
		// TODO Auto-generated method stub
		if (intent.getAction().equals("com.um.dvb.START_TF_IPPV")){
			Log.i(TAG, "com.um.dvb.START_TF_IPPV");
			mContext = context;
			startIppv();
		}
		else if (intent.getAction().equals("com.um.dvb.STOP_TF_IPPV")){
			Log.i("IppvReceiver", "com.um.dvb.STOP_TF_IPPV");
			mContext = context;
			hideIppv();
		}else if ((true == isIppOpen)&&(intent.getAction().equals("com.um.dvb.STOP_IPPV"))){
			Log.i(TAG, "com.um.dvb.STOP_IPPV");
			mContext = context;
			hideIppv();
		}
		
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

	private String tf_YMD_calculate(int iDiffDays)
	{
		int offset;
	    int total, i;
	    byte [] month_day={29,31,28,31,30,31,30,31,31,30,31,30,31};
	    String dateresult = "";
	    year = 2000;/*start from 2000/01/01. 0 means 2000/01/01, 1-2000/01/02....*/
	    month = 1;
	    day = 1;

	    offset = iDiffDays;

	    total = (IsLeapYear(year))?366:365;
	      while(offset>=total)     /*Add years*/
	      {
	          year++;
	          offset-=total;
	          total = (IsLeapYear(year))?366:365;
	      }

	     i= (IsLeapYear(year)&&(month==2))?0:month;
	      while(offset>=month_day[i])         /*add months*/
	      {
	          month++;
	          if(month>12)
	          {
	              year++;
	              month=1;
	          }
	          offset-=month_day[i];
	         i= (IsLeapYear(year)&&(month==2))?0:month;
	      }

	      day+=offset;
	      while(day>month_day[i])             /*add days*/
	      {
	          day-=month_day[i];
	          month++;
	          if(month>12)
	          {
	              year++;
	              month=1;
	          }
	      }
	      Log.i("CA","CA year:"+year);  
	      Log.i("CA","CA month:"+month); 
	      Log.i("CA","CA day:"+day); 
	      
	      String stryear = String.valueOf(year);
	      String strmonth = String.valueOf(month);
	      String strday = String.valueOf(day);
	      dateresult = stryear + "/" +strmonth + "/" +strday;
          
	      return dateresult;
	}
	
	private Parcel caWriteIppinfoToBuy()
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
		int i = 0;
		byte pwd[] = new byte[6];

		price_code = priceCode;
		price = ippv_price;
	  	ecm_pid = ecmPid;
	  	buy_program = 1;
	  	
	  	String str = String.valueOf(pin.getText());
		pwd = str.getBytes();
		pinLen = pwd.length;
	  	for(i = 0; i < pinLen; i++){
	  		pwd[i] -= ASCII_0;
	  	}
		
	  	System.out.printf("price_code:%d, price:%d, ecm_pid:0x%x\n", price_code, price, ecm_pid);

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
    	ippParam.writeInt(booked_price);
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
    	ippParam.writeByteArray(pwd);	
    	
    	return ippParam;
    	
    }
	
	private Parcel caWriteIppinfoCancelBuy()
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
		byte pwd[] = new byte[6];
		
		ecm_pid = ecmPid;

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
    	ippParam.writeInt(booked_price);
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
    	ippParam.writeByteArray(pwd);	
    	
    	return ippParam;
    	
    }
	
	
}
