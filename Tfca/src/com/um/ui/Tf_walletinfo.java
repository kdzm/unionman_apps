package com.um.ui;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.um.controller.AppBaseActivity;

public class Tf_walletinfo extends AppBaseActivity {
		private String operid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		 setContentView(R.layout.walletinfo);
		 
		 Intent intent = getIntent();
		 operid = intent.getStringExtra("operids");
		 int operid_int = Integer.parseInt(operid)	;

		 Ca ca = new Ca(DVB.GetInstance());
		 int []buff_len = {2048};
		 byte []buff = new byte[buff_len[0]];
		 int ret = ca.CaGetWallets(operid_int, buff, buff_len);
		 System.out.printf("CaGetWallets,ret:%d,buff_len:%d\n", ret, buff_len[0]); 
		 if((0 == ret) && (0 != buff_len[0]))
		 { 
			 String jsonStr = new String(buff, 0, buff_len[0]);
			 showWalletInfo(jsonStr);
		 }else{
			 System.out.printf("ca.CaGetWallets:fail\n");
		 }
    }

    private void showWalletInfo(String jsonStr){
    	 ListView list = (ListView) findViewById(R.id.mywalletlist);
		 ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

		 System.out.println("jsonStr:"+jsonStr); //杈撳嚭瀛楃涓�
		 
		 try {
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  	JSONArray jsonArrayWalletInfo = jsonObject.getJSONArray("walletsInfo");
			
			 	int count = jsonObject.getInt("count");
			 	System.out.format("count:%d\n", count);
			 	System.out.format("jsonArrayEntitleInfo.length:%d\n", jsonArrayWalletInfo.length());
				for(int i = 0; i < jsonArrayWalletInfo.length(); i++)
				{		
					HashMap<String, String> map = new HashMap<String, String>();
					
					int u8SlotId = jsonArrayWalletInfo.getJSONObject(i).getInt("walletId");
					long u32Balance = jsonArrayWalletInfo.getJSONObject(i).getLong("balance");
					long u32cost =jsonArrayWalletInfo.getJSONObject(i).getLong("cost");
					long u32credit = u32Balance + u32cost;
					
					map.put("slotid", String.valueOf(u8SlotId));				 
					map.put("credit",String.valueOf(u32credit));
					map.put("cost",String.valueOf(u32cost));
					mylist.add(map);
				}
			
				 SimpleAdapter saImageItems = new SimpleAdapter(this,mylist,
						 R.layout.walletinfo_detailitem, new String[] {"slotid","credit"
								, "cost"},
						 new int[]{R.id.walletItem1,R.id.walletItem2,R.id.walletItem3});
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