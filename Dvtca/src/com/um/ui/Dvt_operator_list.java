package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Operator;
import com.um.controller.AppBaseActivity;

public class Dvt_operator_list extends AppBaseActivity{
	public ArrayList<Operator> operatorlist;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_operator_list);
		
		get_operator_list();
		SetOnItemClickListener();
		
		ListView operatorListView = (ListView) findViewById(R.id.operator_list);
		operatorListView.setFocusable(true);
		operatorListView.requestFocus();

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}

    public void  get_operator_list() 
	{
    	ListView operatorListView = (ListView) findViewById(R.id.operator_list);
		Ca ca = new Ca(DVB.getInstance());
		
		int [] operid = new int[20];
		int [] opernum = new int[1];
		int ret;
		ret = ca.CaGetOperID(operid, opernum);
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < opernum[0]; i++)
		{	
			HashMap<String, String> map = new HashMap<String, String>();
			byte [] opername = new byte[100];
			ca.CaGetOperatorInfo(operid[i],opername);
			String operaname_str = "";
			try {
				operaname_str = new String(opername,"gb2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			map.put("id", String.valueOf(operid[i]));
			map.put("name", operaname_str);
			list.add(map);
		}
		SimpleAdapter operatorAdaptor = new SimpleAdapter(this, list,
			R.layout.dvt_operator_item, 
				new String[] { "id","name" }, 
				new int[] { R.id.operator_id,R.id.operator_name });
		operatorListView.setAdapter(operatorAdaptor);
        operatorListView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                restartAutoFinishTimer();
                return false;
            }
        });
	}
    
    public void SetOnItemClickListener()
    {
    	ListView operatorListView = (ListView) findViewById(R.id.operator_list);
		/*Add setOnItemClickListener*/
		operatorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(getCardStatus() != true){
					new AlertDialog.Builder(Dvt_operator_list.this)
					.setMessage(R.string.ca_insert_card)
					.setPositiveButton("ok", null)
					.show();
					return;
				}
				
				// TODO Auto-generated method stub	
				ListView listView = (ListView)arg0;
				HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(arg2);
				String oper_id = map.get("id");
				Log.i("Dvt_operator_list","oper_id:"+oper_id);
				Intent it = new Intent();
				it.putExtra("operids", oper_id);
				it.setClass(Dvt_operator_list.this, Dvt_entitle_info.class);
				Dvt_operator_list.this.startActivity(it);
			}
		});
    }
    
    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.getInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Dvt_operator_list", "ret:" +ret);
    	Log.d("Dvt_operator_list", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
}
