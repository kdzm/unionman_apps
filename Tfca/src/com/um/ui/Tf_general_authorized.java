package com.um.ui;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.um.controller.AppBaseActivity;
public class Tf_general_authorized extends AppBaseActivity
{
	private String operid="";
	private int year, month,day;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_general_authorized);
		
		Intent intent = getIntent();
		operid = intent.getStringExtra("operids");
		int operid_int = Integer.parseInt(operid)	;	
		Ca ca = new Ca(DVB.GetInstance());
		
		int[] buffLen = {20480};
		byte[] buff = new byte [buffLen[0]];
		
		int ret = ca.CaGetEntitles(operid_int, buff, buffLen);
		System.out.printf("CaGetEntitles, ret:%d,buffLen:%d\n", ret, buffLen[0]); 
		if((0 == ret) && (0 != buffLen[0]))
		{	
			String jsonStr = new String(buff, 0, buffLen[0]);
			showEntitleInfo(jsonStr);
		}
		else{
			System.out.println("CaGetEntitles,fail");
		}
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

	public String tf_YMD_calculate(int iDiffDays)
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

	private void showEntitleInfo(String jsonStr){
		
		ListView list = (ListView) findViewById(R.id.tf_general_listView1);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
		 System.out.println("showEntitleInfo,jsonStr:"+jsonStr); //杈撳嚭瀛楃涓�
		 
		 try {
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  	JSONArray jsonArrayEntitleInfo = jsonObject.getJSONArray("entitleInfo");
			
			 	int count = jsonObject.getInt("productCount");
			 	System.out.format("count:%d\n", count);
			 	System.out.format("jsonArrayEntitleInfo.length:%d\n", jsonArrayEntitleInfo.length());
				for(int i = 0; i < jsonArrayEntitleInfo.length(); i++)
				{		
					String progid = jsonArrayEntitleInfo.getJSONObject(i).getString("productId");
					int e_date = jsonArrayEntitleInfo.getJSONObject(i).getInt("endDate");
					int taping = jsonArrayEntitleInfo.getJSONObject(i).getInt("bTaping");

					String tap="";
					if(taping == 1)
					{
						tap = "YES";
					}
					else
					{
						tap = "NO";
					}
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("progid",progid);
					map.put("taping",tap);
					String temp = tf_YMD_calculate(e_date);
					map.put("overtime",temp);
					mylist.add(map);
				}
			
		        
				SimpleAdapter saImageItems = new SimpleAdapter(this, mylist,
						R.layout.tf_general_authoriz_3info, new String[] { "progid", "taping", "overtime"
								}, new int[] { R.id.TextView_01,
								R.id.TextView_02, R.id.TextView_03,
								});
				list.setAdapter(saImageItems);
			    
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");// 寮傚父澶勭悊浠ｇ爜  
			}
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
}
