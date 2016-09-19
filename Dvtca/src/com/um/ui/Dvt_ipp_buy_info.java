package com.um.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import java.io.UnsupportedEncodingException;

public class Dvt_ipp_buy_info extends AppBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dvt_ipp_buy_info);
        		
		int[] buffLen = {40960};
		byte[] buff = new byte [buffLen[0]];
        
		 Ca ca = new Ca(DVB.getInstance());
		 int ret = ca.CaGetViewedIpps(buff, buffLen);
		 System.out.printf("dvt_viewed_ipps,ret:%d,buffLen:%d\n", ret, buffLen[0]); 
		 
		 if((ret == 0)&&(0 != buffLen[0]))
		 {
			 //final String jsonStr = new String(buff, 0, buffLen[0]);
			try {
				 final String jsonStr = new String(buff, 0, buffLen[0], "gb2312");
				 showIppsInfo(jsonStr);
				 SetOnItemSelectListener(jsonStr);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		 }else{
			 Log.e("Dvt_buy_program","CaGetViewedIpps:fail");
		 }

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }
    
	@Override
	protected void onPause(){
        super.onPause();
        finish();
	}
    
    private void showIppsInfo(final String jsonStr){
    	System.out.println("jsonStr:"+jsonStr);
		ListView list = (ListView) findViewById(R.id.MybuyProgramView);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		 Ca ca = new Ca(DVB.getInstance());
		 
		try {
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	JSONArray jsonArrayippsInfo = jsonObject.getJSONArray("viewippInfo");
		
		 	int count = jsonObject.getInt("count");
		 	TextView ippcount = (TextView)findViewById(R.id.dvt_viewipps_num);
		 	ippcount.setText(String.valueOf(count));
		 	
		 	System.out.format("count:%d\n", count);
		 	System.out.format("jsonArrayippsInfo.length:%d\n", jsonArrayippsInfo.length());
		 	
		 	
			for(int i = 0; i < jsonArrayippsInfo.length(); i++)
			{		
				String ProdName = jsonArrayippsInfo.getJSONObject(i).getString("prod_name");		

				int Tvsid = jsonArrayippsInfo.getJSONObject(i).getInt("tvs_id");
	
				byte [] opername = new byte[100];
				ca.CaGetOperatorInfo(Tvsid,opername);
				String operaname_str = "";
				try {
					operaname_str = new String(opername,"gb2312");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
				 	 
				 HashMap<String, String> map = new HashMap<String, String>();
					map.put("prod_name",ProdName);
					map.put("opername",operaname_str);
					mylist.add(map);
			}
				
			SimpleAdapter saImageItems = new SimpleAdapter(this,mylist,
					 R.layout.dvt_ipp_buy_info_item, new String[] {"prod_name","opername"},
					 new int[]{R.id.buy_program_Item1,R.id.buy_program_Item2});
			list.setAdapter(saImageItems);
            list.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    restartAutoFinishTimer();
                    return false;
                }
            });
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");
		}  
	}
    
    public void SetOnItemSelectListener(final String jsonStr)
    {
        ListView emailListView = (ListView) findViewById(R.id.MybuyProgramView);
		 final TextView IppPrice = (TextView)findViewById(R.id.dvt_orderprice);
		 final TextView IppStartime = (TextView)findViewById(R.id.dvt_startime);
		 final TextView IppDuration = (TextView)findViewById(R.id.dvt_duration);
		 final TextView IppOtherinfo = (TextView)findViewById(R.id.dvt_otherinfo);
		 
        emailListView.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
            	int index  = arg2;
                Log.e("Dvt_email", "email Listener"+index);
    			try {
    				 JSONObject jsonObject = new JSONObject(jsonStr);
    			  	 JSONArray jsonArrayippsInfo = jsonObject.getJSONArray("viewippInfo");
					 int StartTime = jsonArrayippsInfo.getJSONObject(index).getInt("start_time");
					 int Duration = jsonArrayippsInfo.getJSONObject(index).getInt("duration");
					 int Price = jsonArrayippsInfo.getJSONObject(index).getInt("booked_price");
					 int Unit = jsonArrayippsInfo.getJSONObject(index).getInt("by_unit");
					 int PriceType = jsonArrayippsInfo.getJSONObject(index).getInt("booked_price_type");
					 int Interval = jsonArrayippsInfo.getJSONObject(index).getInt("booked_interval");
					 String OtherInfo = jsonArrayippsInfo.getJSONObject(index).getString("other_info");
					 
					String priceStr = "";					 
					if (Interval == 0)
					{
						priceStr = dvt_display_yuan(Price);
					}
					else
					{
						priceStr = dvt_display_yuan_min(Price, Interval, Unit);					
					}
					
					 String durationStr="";
					 durationStr = dvt_display_date(Duration);

					Dvt_email_read general_authorized = new Dvt_email_read();
					String start_time_str = "";
					 try {
							start_time_str = general_authorized.calculateSendDate(StartTime);
					 } catch (ParseException e) {
						e.printStackTrace();
					 }	
					 					
					 IppPrice.setText(priceStr);
					 IppStartime.setText(start_time_str);
					 IppDuration.setText(durationStr);
					 IppOtherinfo.setText(OtherInfo);	
					 
    			} catch (JSONException ex) {  
    				System.out.println("get JSONObject fail");
    			}
            }
            
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });

        emailListView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                restartAutoFinishTimer();
                return false;
            }
        });
    }


    public String dvt_display_date(int date)
    {
    	String str = "";
//    	String unicoStr = "";
    	int day = date/(3600*24);
    	int hour = (date%(3600*24))/3600;
    	int min = (date%3600)/60;
    	int sec = date%60;
    	str = str+day+"天"+hour+"小时"+min+"分"+sec+"秒";
    	
    	return str;
//		try {
//			unicoStr = new String(str.getBytes(),"gb2312");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		return unicoStr;	
    		
    }
    
    public String dvt_display_yuan(int fen)
    {
    	String str = "";
//    	String unicoStr = "";
    	
    	if (fen == 0)
    	{
    		str = str+"不回传、不可录像的价格(无)";
    	}
    	else
    	{
    		int yuan = fen/100;
    		int feng = fen%100;
    		str = str+"不回传、不可录像的价格("+yuan+"."+feng+"元)";
    	}
    	
    	return str;
    	
//		try {
////			byte b[] = str.getBytes();
////			str = new String(b);
////			unicoStr = new String(str.getBytes(),"gb2312");
////		} catch (UnsupportedEncodingException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////		
    	
//		byte b[] = str.getBytes();
//		unicoStr = new String(b);
//		return unicoStr;
		
		//return unicoStr;
    }
    
    public static final  int DVTCAS_IPP_BYUNIT_MIN = 0;
    public static final  int DVTCAS_IPP_BYUNIT_HOUR = 1;
    public static final  int DVTCAS_IPP_BYUNIT_DAY = 2;
    public static final  int DVTCAS_IPP_BYUNIT_MON = 3;
    public static final  int DVTCAS_IPP_BYUNIT_YER = 4;
    
    public String dvt_display_yuan_min(int fen, int min, int  uint)
    {
    	String str = "";
//    	String unicoStr = "";
    	
    	if (fen == 0)
    	{
    		str = str+"不回传、不可录像的价格(无)";
    	}
    	else
    	{
    		int yuan = fen/100;
    		int feng = fen%100;
    		str = str+"不回传、不可录像的价格("+yuan+"."+feng+"元/";
    	}
    	
    	if (min != 0)
    	{
    		str = str+min;
    	}
 
    	switch(uint)
    	{
	    	case DVTCAS_IPP_BYUNIT_MIN:
	    		str = str+"分钟)";
	    		break;
	    	case DVTCAS_IPP_BYUNIT_HOUR:
	    		str = str+"小时)";	    		
	    		break;
	    	case DVTCAS_IPP_BYUNIT_DAY:
	    		str = str+"天)";
	    		break;
	    	case DVTCAS_IPP_BYUNIT_MON:
	    		str = str+"月)";
	    		break;
	    	case DVTCAS_IPP_BYUNIT_YER:
	    		str = str+"年)";
	    		break;	    		
	    	default:
	    		str = str+"分钟)";	    		
	    		break;
	    	
    	}
    	
    	return str;
    	
//		try {
//			unicoStr = new String(str.getBytes(),"gb2312");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		return unicoStr;
    }
    
}


