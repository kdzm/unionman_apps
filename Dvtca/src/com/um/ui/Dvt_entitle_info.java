package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;

import android.app.AlertDialog;

public class Dvt_entitle_info extends AppBaseActivity
{
	private String operid="";
	private int year, month,day;
	private Context mContext = Dvt_entitle_info.this;
	private AlertDialog myDialog;
	private final String TAG = Dvt_entitle_info.class.getSimpleName();
	private String jsonStr;
	private String operanameStr = "";
	private final int gotEntitleInfoOk = 0;
	private final int gotEntitleInfoFail = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_entitle_info);
		
		handler.removeCallbacks(showDialog);
		handler.post(showDialog);
		
		new Thread(getEntitleInfo).start();
	}
	
	private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == gotEntitleInfoOk){
                Log.i(TAG, "gotEntitleInfoOk");
                handler.removeCallbacks(hideDialog);
                handler.post(hideDialog);
				showEntitleInfo(jsonStr);
            }else if(what == gotEntitleInfoFail){
            	Log.i(TAG, "gotEntitleInfoFail");
                handler.removeCallbacks(hideDialog);
                handler.post(hideDialog);

                new AlertDialog.Builder(mContext)
    			.setMessage(R.string.got_info_fail)
    			.setPositiveButton("ok", null)
    			.show();
            }
        }
    };
	
	/*显示Dialog的Runnable*/	
	Runnable showDialog = new Runnable(){
		@Override
		public void run() {
			Log.i(TAG,"showDialog");
			myDialog = new AlertDialog.Builder(mContext)
			.setMessage(R.string.waiting_to_get_card_info)
			.show();
		}
	};
	
	/*隐藏Dialog的Runnable*/	
	Runnable hideDialog = new Runnable(){
		@Override
		public void run() {
			Log.i(TAG,"hideDialog");
			myDialog.hide();
		}
	};
	
	/*获取信息的Runnable*/	
	Runnable getEntitleInfo = new Runnable(){
		@Override
		public void run() {
			Intent intent = getIntent();
			operid = intent.getStringExtra("operids");
			int operid_int = Integer.parseInt(operid);
			Log.i("Dvt_entitle_info","operid_int:"+operid_int);
			
			Ca ca = new Ca(DVB.getInstance());

			byte [] opername = new byte[100];
			ca.CaGetOperatorInfo(operid_int,opername);
			
			try {
				operanameStr = new String(opername,"gb2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				handler.sendEmptyMessage(gotEntitleInfoFail);
			}
			
			int[] buffLen = {51200};
			byte[] buff = new byte [buffLen[0]];
			
			int ret = ca.CaGetEntitles(operid_int, buff, buffLen);
			System.out.printf("CaGetEntitles, ret:%d,buffLen:%d\n", ret, buffLen[0]); 
			if((0 == ret) && (0 != buffLen[0]))
			{	
				try {
						jsonStr = new String(buff, 0, buffLen[0], "gb2312");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally{
					Log.i(TAG, "handler.sendEmptyMessage(1)");
					handler.sendEmptyMessage(gotEntitleInfoOk);
				}
			}
			else{
				System.out.println("CaGetEntitles,fail");
				handler.sendEmptyMessage(gotEntitleInfoFail);
			}
		}
	};
	
	@Override
	protected void onPause(){
        super.onPause();
        finish();
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

	public String dvt_YMD_calculate(int iDiffDays)
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
//	      Log.i("CA","CA year:"+year);  
//	      Log.i("CA","CA month:"+month); 
//	      Log.i("CA","CA day:"+day); 
	      
	      String stryear = String.valueOf(year);
	      String strmonth = String.valueOf(month);
	      String strday = String.valueOf(day);
	      dateresult = stryear + "/" +strmonth + "/" +strday;
          
	      return dateresult;
	}

	private void showEntitleInfo(String jsonStr){
		
		TextView operator_name = (TextView)findViewById(R.id.program_name);
		operator_name.setText(operanameStr);
		
		ListView list = (ListView) findViewById(R.id.dvt_general_listView1);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
//		 System.out.println("showEntitleInfo,jsonStr:"+jsonStr);
        byte[] productName = new byte[50];
        
		 try {
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  	JSONArray jsonArrayEntitleInfo = jsonObject.getJSONArray("entitleInfo");
			
			 	int count = jsonObject.getInt("productCount");
				TextView entitiles_num = (TextView)findViewById(R.id.entitles_num);
				entitiles_num.setText(String.valueOf(count));
				
			 	System.out.format("count:%d\n", count);
			 	System.out.format("jsonArrayEntitleInfo.length:%d\n", jsonArrayEntitleInfo.length());
				Dvt_email_read email_read = new Dvt_email_read();
			 	
				for(int i = 0; i < jsonArrayEntitleInfo.length(); i++)
				{		
					
					String operaname_str = jsonArrayEntitleInfo.getJSONObject(i).getString("productName");
		                
					int entitletime = jsonArrayEntitleInfo.getJSONObject(i).getInt("entitleTime");
					int startime = jsonArrayEntitleInfo.getJSONObject(i).getInt("startTime");
					int endtime = jsonArrayEntitleInfo.getJSONObject(i).getInt("endTime");
					String entitle_time_str = "";
					String start_time_str = "";
					String end_time_str = "";

					 try {
							entitle_time_str = email_read.calculateSendDate(entitletime);
							start_time_str = email_read.calculateSendDate(startime);
							end_time_str = email_read.calculateSendDate(endtime);							
					 } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("productName",operaname_str);
					map.put("entitleTime",entitle_time_str);
					map.put("startTime",start_time_str);
					map.put("endTime",end_time_str);
					
					mylist.add(map);
				}
		        
				SimpleAdapter saImageItems = new SimpleAdapter(this, mylist,
						R.layout.dvt_entitle_info_item, new String[] { "productName", "entitleTime", "startTime", "endTime",
								}, new int[] { R.id.TextView_01,
								R.id.TextView_02, R.id.TextView_03, R.id.TextView_04,
								});
				list.setAdapter(saImageItems);
			    
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");
			}
	}
}
