package com.um.ui;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.tfca.R;
import com.um.tfca.R.string;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.ui.Tf_general_authorized;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.um.controller.AppBaseActivity;

public class Tf_buy_program extends AppBaseActivity {
	private String operid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tf_buy_program);
        
        Intent intent = getIntent();
        operid = intent.getStringExtra("operids");
        int operid_int = Integer.parseInt(operid);
		
		int[] buffLen = {40960};
		byte[] buff = new byte [buffLen[0]];
        
		 Ca ca = new Ca(DVB.GetInstance());
		 int ret = ca.CaGetAllIpps(operid_int, buff, buffLen);
		 System.out.printf("Tf_buy_program,ret:%d,buffLen:%d\n", ret, buffLen[0]); 
		 
		 if((ret == 0)&&(0 != buffLen[0]))
		 {
			 String jsonStr = new String(buff, 0, buffLen[0]);
			 showIppsInfo(jsonStr);
		 }else{
			 Log.e("Tf_buy_program","CaGetAllIpps:fail");
		 }
    }
    
    private void showIppsInfo(String jsonStr){
    	System.out.println("jsonStr:"+jsonStr); //杈撳嚭JSON瀛楃涓�
		ListView list = (ListView) findViewById(R.id.MybuyProgramView);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

		try {
		  	JSONObject jsonObject = new JSONObject(jsonStr);
		  	JSONArray jsonArrayippsInfo = jsonObject.getJSONArray("ippsInfo");
		
		 	int count = jsonObject.getInt("count");
		 	System.out.format("count:%d\n", count);
		 	System.out.format("jsonArrayippsInfo.length:%d\n", jsonArrayippsInfo.length());
			for(int i = 0; i < jsonArrayippsInfo.length(); i++)
			{		
				 int slotID = jsonArrayippsInfo.getJSONObject(i).getInt("slotId");
				 long prodID = jsonArrayippsInfo.getJSONObject(i).getLong("prodId");
				 int ippStatus = jsonArrayippsInfo.getJSONObject(i).getInt("ippStatus");
	
				 String ippstatus_str="";
				 if(ippStatus == 3)			 
				 {		 
					 ippstatus_str = getString(R.string.tf_ippv_overview);					 
				 }
				 else if(ippStatus == 1)
				 {
					 ippstatus_str = getString(R.string.tf_book);				 
				 }
				 
				 int ippPrice = jsonArrayippsInfo.getJSONObject(i).getInt("price");
				 int taping = jsonArrayippsInfo.getJSONObject(i).getInt("taping");
				 String tap_str="";
				 if(taping == 1)
				 {
					 tap_str = "Yes";
				 }
				 else
				 {
					 tap_str = "No";
				 }
				
				 int expiredDate = jsonArrayippsInfo.getJSONObject(i).getInt("expiredDate");
				 Tf_general_authorized general_authorized = new Tf_general_authorized();
				 String sttTemp = general_authorized.tf_YMD_calculate(expiredDate);
				 
				 HashMap<String, String> map = new HashMap<String, String>();
					map.put("walletid",String.valueOf(slotID));
					map.put("progid",String.valueOf(prodID));
					map.put("status",ippstatus_str);
					map.put("price",String.valueOf(ippPrice));
					map.put("recored",tap_str);
					map.put("expiredtime",sttTemp);
					mylist.add(map);
			}
			
			SimpleAdapter saImageItems = new SimpleAdapter(this,mylist,
					 R.layout.tf_buy_program_detailitem, new String[] {"walletid","progid"
							, "status","price","recored","expiredtime"},
					 new int[]{R.id.buy_program_Item1,R.id.buy_program_Item2,R.id.buy_program_Item3,R.id.buy_program_Item4,R.id.buy_program_Item5,R.id.buy_program_Item6});
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
