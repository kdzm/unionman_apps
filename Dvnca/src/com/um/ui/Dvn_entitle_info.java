package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvnca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

public class Dvn_entitle_info extends AppBaseActivity
{
	private String operid="";
	private int year, month,day;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_entitle_info);
		
		Log.i("Dvn_entitle_info","start");
		
		//int operid_int = 0;
		Ca ca = new Ca(DVB.GetInstance());

		int[] buffLen = {51200};
		byte[] buff = new byte [buffLen[0]];

		int ret = 0 ;//ca.CaGetDvnEntitles(buff, buffLen);
		System.out.printf("CaGetDvnEntitles, ret:%d,buffLen:%d\n", ret, buffLen[0]); 
		if((0 == ret) && (0 != buffLen[0]))
		{	
			final  String jsonStr = new String(buff, 0, buffLen[0]);
			showEntitleInfo(jsonStr);
//			try {
//				 final String jsonStr = new String(buff, 0, buffLen[0], "gb2312");
//				 showEntitleInfo(jsonStr);
//			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}			
		}
		else{
			System.out.println("CaGetDvnEntitles,fail");
		}
	}
	
	private void showEntitleInfo(String jsonStr){
		
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
				Dvn_email_read email_read = new Dvn_email_read();
				
				for(int i = 0; i < jsonArrayEntitleInfo.length(); i++)
				{		
					
					String operaname_str = jsonArrayEntitleInfo.getJSONObject(i).getString("type");
		                
//					try {
//						operaname_str = new String(productName,"gb2312");
//					} catch (UnsupportedEncodingException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}					                
					
					//int entitletime = jsonArrayEntitleInfo.getJSONObject(i).getInt("entitleTime");
					String startime = jsonArrayEntitleInfo.getJSONObject(i).getString("startTime");
					String endtime = jsonArrayEntitleInfo.getJSONObject(i).getString("endTime");

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("type",operaname_str);

					map.put("startTime",startime);
					map.put("endTime",endtime);
					
					mylist.add(map);
				}
			
		        
				SimpleAdapter saImageItems = new SimpleAdapter(this, mylist,
						R.layout.dvt_entitle_info_item, new String[] { "type", "startTime", "endTime",
								}, new int[] { R.id.TextView_01,
								R.id.TextView_02, R.id.TextView_03,
								});
				list.setAdapter(saImageItems);
			    
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");
			}
	}
	
}
