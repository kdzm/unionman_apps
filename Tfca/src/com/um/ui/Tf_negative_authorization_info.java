package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Tf_negative_authorization_info extends AppBaseActivity{
	private String operid="";
	private int max_num = 5;
	@Override
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_negative_authorization_info);
		
		int id = 0;
		int i = 0;
		int offset = 0;
		int[] buff = new int [200];
		int buff_len = 200;  
		String status = "";
		Intent intent = getIntent();
		operid = intent.getStringExtra("operids");
		int operid_int = Integer.parseInt(operid);
		Ca ca = new Ca(DVB.GetInstance());
		int ret1 = ca.CaGetDetitleChecknum(operid_int, buff, buff_len);
		int ret2 = ca.CaGetDetitleReaded(operid_int);
		Log.i("CA", "CaGetDetitleChecknum :" +ret1);

		ListView list = (ListView) findViewById(R.id.tf_detitle_listView);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		if(ret1 == 0)
		{
			int index = 0;
			for(i=0;i<max_num;i++)
			{
				Log.i("CA", "buff[i] :" +buff[i]);					
				if(buff[i] != 0)
				{
					index++;
					
				}
			}
			Log.i("CA", "index :" +index);			
			for(i=0;i<index;i++)
			{
				int checknum = buff[i];		
				Log.i("CA", "index ret2:"+ret2);
				if(ret2 == 0)
				{
					status = getString(R.string.tf_readed); //Remember to add the string.
				}
				else
				{
					status = getString(R.string.tf_not_read);
				}
				Log.i("CA", "index ");	
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("index",String.valueOf(i+1));
				map.put("status",status);
				map.put("affirmation_code",String.valueOf(checknum));
				mylist.add(map);
			}
			
		}
		
		SimpleAdapter saImageItems = new SimpleAdapter(this, mylist,
				R.layout.tf_negative_authorization_info_3item, new String[] { "index", "status", "affirmation_code"
						}, new int[] { R.id.index,
						R.id.status, R.id.confirmation_code,
						});
		list.setAdapter(saImageItems);
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
	