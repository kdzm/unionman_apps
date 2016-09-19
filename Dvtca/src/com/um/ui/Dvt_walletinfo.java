package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.um.controller.AppBaseActivity;

public class Dvt_walletinfo extends AppBaseActivity {
    private String operid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		 setContentView(R.layout.dvt_walletinfo);
		 
		 Ca ca = new Ca(DVB.getInstance());
		int [] operid = new int[20];
		int [] opernum = new int[1];
		int ret_operator;
		
		//Log.i("CaGetOperID", "start");
		ret_operator = ca.CaGetOperID(operid, opernum);	
		Log.i("CaGetOperID", "ret_operator:"+ret_operator);
		int walletNum = 0;
		
		for (int i = 0; i < opernum[0]; i++)
		{	
			byte [] opername = new byte[100];
			ca.CaGetOperatorInfo(operid[i],opername);
			String operaname_str = "";
			try {
				operaname_str = new String(opername,"gb2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
						
			 int []buff_len = {2048};
			 byte []buff = new byte[buff_len[0]];
			 int ret = ca.CaGetWallets(operid[i], buff, buff_len);
			 System.out.printf("CaGetWallets,ret:%d,buff_len:%d\n", ret, buff_len[0]); 
			 if((0 == ret) && (0 != buff_len[0]))
			 { 
				 walletNum++;
				 String jsonStr = new String(buff, 0, buff_len[0]);
				 showWalletInfo(operaname_str, jsonStr);
			 }else{
				 System.out.printf("ca.CaGetWallets:fail\n");
			 }			
			
			
		}	 
		
		if (walletNum == 0)
		{
			new AlertDialog.Builder(Dvt_walletinfo.this).setMessage(R.string.dvt_ipp_slot_invalide).setPositiveButton("ok", null).show();			
		}

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }
    
    private void showWalletInfo(String operatorname_Str, String jsonStr){
    	 ListView list = (ListView) findViewById(R.id.mywalletlist);
		 ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

		 System.out.println("jsonStr:"+jsonStr);
		 
		 try {
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  	JSONArray jsonArrayWalletInfo = jsonObject.getJSONArray("walletsInfo");
			
			 	int count = jsonObject.getInt("count");
			 	System.out.format("count:%d\n", count);
			 	System.out.format("jsonArrayEntitleInfo.length:%d\n", jsonArrayWalletInfo.length());
				for(int i = 0; i < jsonArrayWalletInfo.length(); i++)
				{		
					HashMap<String, String> map = new HashMap<String, String>();
					
					//int u8SlotId = jsonArrayWalletInfo.getJSONObject(i).getInt("walletId");
					long u32Balance = jsonArrayWalletInfo.getJSONObject(i).getLong("balance");
					long u32cost =jsonArrayWalletInfo.getJSONObject(i).getLong("cost");
					//long u32credit = u32Balance + u32cost;
					
					map.put("slotid", operatorname_Str);
					//map.put("slotid", String.valueOf(u8SlotId));				 
					map.put("cost",String.valueOf(u32cost));
					map.put("credit",String.valueOf(u32Balance));
					mylist.add(map);
				}
			
				 SimpleAdapter saImageItems = new SimpleAdapter(this,mylist,
						 R.layout.dvt_walletinfo_item, new String[] {"slotid","cost"
								, "credit"},
						 new int[]{R.id.walletItem1,R.id.walletItem2,R.id.walletItem3});
						 list.setAdapter(saImageItems);
			    
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");
			}

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                restartAutoFinishTimer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
	}

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}