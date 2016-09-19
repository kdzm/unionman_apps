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
import com.um.controller.AppBaseActivity;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Tf_pair_info_check extends AppBaseActivity{
	private static final String TAG = "Tf_pair_info_check";
	private TextView paired_text;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_pair_info_check);
		
		Ca ca = new Ca(DVB.GetInstance());
		int []buff_len = {2048};
		byte []buff = new byte[buff_len[0]];
		
		int ret = ca.CaGetPairInfoCheck(buff, buff_len);
		Log.d("Tf_pair_info_check", "ret:" +ret); 
		Log.d("Tf_pair_info_check", "buffLen:" +buff_len[0]); 
		if(buff_len[0] != 0){
			String jsonStr = new String(buff, 0, buff_len[0]);
			showPairInfo(jsonStr);
		}
		else{
			Log.e("Tf_pair_info_check", "CaGetPairInfoCheck,fail,ret:"+ret);
		}

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


	private void showPairInfo(String jsonStr){
		Log.i("Tf_pair_info_check", "jsonStr:"+jsonStr); //杈撳嚭瀛楃涓�
		 
		/*paired information list*/
		ListView pairinfoListView = (ListView) findViewById(R.id.listView1);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		try {  
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			 	int count = jsonObject.getInt("count");
				System.out.format("count:%d\n", count);
				JSONArray jsonArrayStbidInfo = jsonObject.getJSONArray("stbid_list");
		
				for(int i = 0;i < count ;i++)
				{
					String str_index;
					String str_stbid;
					
					int index = i+1;
					HashMap<String, String> map = new HashMap<String, String>();
					str_stbid =  jsonArrayStbidInfo.getJSONObject(i).getString("stbid");
					if(str_stbid.isEmpty())
					{
						str_index = "";
						str_stbid = "";
						map.put("index",str_index);
						map.put("stbid",str_stbid);
						mylist.add(map);
						break;
					}
					else
					{
						str_index = String.valueOf(index);
						map.put("index",str_index);
						map.put("stbid",str_stbid);
						mylist.add(map);
					}
				}
					 	
			    
			} catch (JSONException ex) {  
				System.out.println("get JSONObject fail");// 寮傚父澶勭悊浠ｇ爜  
			}  
			
			SimpleAdapter saImageItems = new SimpleAdapter(this, mylist,
					R.layout.tf_pair_info_check_2item, new String[] { "index", "stbid"
							}, new int[] { R.id.pair_Info_Check_Item1,
							R.id.pair_Info_Check_Item2
							});
			pairinfoListView.setAdapter(saImageItems);
            pairinfoListView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    restarAutoFinishTimer();
                    return false;
                }
            });
			
			/*Pair Information*/
			paired_text = (TextView)findViewById(R.id.textView4);
			Ca ca = new Ca(DVB.GetInstance());
			switch(ca.CaGetPairStatus(1))
			{
				case 0:
					paired_text.setText(R.string.tf_smc_pair_present_stb);
				break;			
				case 0x80000029:
					paired_text.setText(R.string.tf_smc_pair_none_stb);
					break;
					
				case 0x80000028:
					paired_text.setText(R.string.tf_smc_pair_other_stb);
					break;				
				default:
					paired_text.setText(R.string.tf_no_smc_available);
					break;
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
