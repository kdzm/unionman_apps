package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;

public class Tf_email extends AppBaseActivity
{
    private static int focus_index;
    private int email_cn;
    private int email_index;
    private final byte DELETE_SINGLE = 6;
    private final byte DELETE_ALL = 7;
    
    private static int selectedPosition = 0;
    private ListView emailListView;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tf_email);

        try
        {
            get_email_list();
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("Tf_email", "email open");
        SetOnItemSelectListener();
        Log.e("Tf_email", "SetOnKeyListener  before");
        SetOnKeyListener();
        SetOnItemClickListener();

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }

	@Override
    public void onResume()
    {	
    	super.onResume();
    	
        try
        {
            get_email_list();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (emailListView != null && selectedPosition > 0) {
        	emailListView.setSelection(selectedPosition);
		}
    }
    
    public void  get_email_list() throws UnsupportedEncodingException
    {
    	emailListView = (ListView) findViewById(R.id.MyemailView);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        int []buff_len = {204800};
        byte [] buff = new byte[buff_len[0]];
        int offset = 0;
        int i ;
        int j ;
        int k ;
        int readcount = 0;
        String priority_str;
        String readflag_str;
        String theme_str;
        byte[] theme_byte = new byte[256];

        /*Get the buffer*/
        Ca ca = new Ca(DVB.GetInstance());
        int ret = ca.CaGetEmailheads(buff, buff_len);
        Log.i("Tf_email", "ret:"+ret);
        Log.i("Tf_email", "buff_len[0]:"+buff_len[0]);
        if ((0 == ret) && (buff_len[0] != 0))
        {
   			 try {  
   				 	String jsonStr = new String(buff, 0, buff_len[0], "gb2312");
   				  	JSONObject jsonObject = new JSONObject(jsonStr);
					email_cn = jsonObject.getInt("email_count");	   				
   				 	System.out.format("email_count:%d\n", email_cn);
   				  	JSONArray jsonArrayMailInfo = jsonObject.getJSONArray("mail");
   				 	
   		            for (i = 0; i < email_cn; i++)
   		            {
   		                HashMap<String, String> map = new HashMap<String, String>();

   		                /*Email Index*/

   		                int index = i;//(buff[offset + 16]& 0xff)|(((buff[offset + 17])& 0xff)<<8)| (((buff[offset + 18])& 0xff)<<16) | (((buff[offset + 19])& 0xff)<<24) ;

   		                String index_str = String.valueOf(index);

   		                /*Important or not*/
   		                int priority = (jsonArrayMailInfo.getJSONObject(i).getInt("u32Priority")) ;
   		                System.out.format("priority:%d\n", priority);
   		                if (0 == priority)
   		                {
   		                    priority_str = this.getString(R.string.tf_normal_email);
   		                }
   		                else
   		                {
   		                    priority_str = this.getString(R.string.tf_important_email);
   		                }

   		                theme_str = jsonArrayMailInfo.getJSONObject(i).getString("title");
   		                
   		                /*Readflag*/
   		                int readflag = (jsonArrayMailInfo.getJSONObject(i).getInt("bReadFlag")) ;

   		                if (0 == readflag)
   		                {
   		                    readflag_str = this.getString(R.string.tf_readed);
   		                }
   		                else
   		                {
   		                    readflag_str = this.getString(R.string.tf_not_read);
   		                    readcount = readcount + 1;
   		                }

   		                map.put("index", index_str);
   		                map.put("theme", theme_str);
   		                map.put("readflag", readflag_str);
   		                map.put("priority", priority_str);
   		                
   		                map.put("emailid", String.valueOf(i+1));
   		                //map.put("delete","");  //this column is reserved

   		                list.add(map);
   		            }
   				 	
   				    
   				} catch (JSONException ex) {  
   					System.out.println("get JSONObject fail");// 瀵倸鐖舵径鍕倞娴狅絿鐖� 
   				}  
	   		
        }	
        
		TextView read_email = (TextView)this.findViewById(R.id.mailread);
		read_email.setText(String.valueOf(readcount));
        
		TextView max_email = (TextView)this.findViewById(R.id.maxmail_num);
		max_email.setText(String.valueOf(100));
		
		TextView count = (TextView)this.findViewById(R.id.mail_count);
		count.setText(String.valueOf(email_cn));
		
        SimpleAdapter emailAdaptor = new SimpleAdapter(this, list,
                R.layout.tf_email_detailitem,
                new String[] {"emailid","theme","readflag","priority"},
                new int[] { R.id.email_Item1,R.id.email_Item2,R.id.email_Item3,R.id.email_Item4});
        emailListView.setAdapter(emailAdaptor);

    }

    public void SetOnItemClickListener()
    {
        ListView emailListView = (ListView) findViewById(R.id.MyemailView);

        /* Add setOnItemClickListener*/
        emailListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
            {
                // TODO Auto-generated method stub
            	selectedPosition = arg2;
                ListView listView = (ListView)arg0;
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(arg2);
                String email_index = map.get("index");
                String email_title = map.get("theme");
                Intent it = new Intent();
                Log.e("Tf_email", "email SetOnItemClickListener"+email_index);
                
                int index_int = Integer.parseInt(email_index);
                int tf_cur_mail_id;
                tf_cur_mail_id = email_cn -index_int-1;//鏉╂稖顢戞禍鍡樺笓鎼达拷
                String index_str = String.valueOf(tf_cur_mail_id);
                
                it.putExtra("email_index", index_str);   //Send data from one activity to another
                it.putExtra("email_title", email_title);
                it.setClass(Tf_email.this, Tf_email_read.class);
                Tf_email.this.startActivity(it);
            }

        });
    }

    public void SetOnKeyListener()
    {
        final ListView emailListView = (ListView) findViewById(R.id.MyemailView);
        emailListView.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
            	 Log.e("Tf_email", "event.getKeyCode   === "+ keyCode);
                restarAutoFinishTimer();
            	if (event.getAction() == KeyEvent.ACTION_UP) {
            		return false ;
            	}
            	
            	if((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)||(keyCode == KeyEvent.KEYCODE_DPAD_UP))
            	{	
	            	if(emailListView.getSelectedItemId() == 0)
	            	{
	            		emailListView.setSelection(emailListView.getCount()-1);
	            	}	
	            	else if(emailListView.getSelectedItemId() == emailListView.getCount()-1)
	            	{
	            		emailListView.setSelection(0);
	            	}	
            	}
                if (keyCode == KeyEvent.KEYCODE_PROG_RED)
                {
                	
                    Log.e("Tf_email", "email open KEYCODE_F1"+email_index);
                    //SetOnItemSelectListener();  //Get focus index
                    
                    int tf_cur_mail_id;
                    focus_index = email_cn -email_index-1;//鏉╂稖顢戞禍鍡樺笓鎼达拷
                    popDialog(emailListView, DELETE_SINGLE);
                    return true ;
                }
                else if (keyCode == KeyEvent.KEYCODE_PROG_GREEN)
                {
                    Log.e("Tf_email", "email open KEYCODE_F2");
                    popDialog(emailListView, DELETE_ALL);
                    return true;
                }

                return false ;
            }
        });
    }
    
    private void popDialog(ListView lv, final byte type){
    	final PopupWindow popWindow = new PopupWindow(Tf_email.this);
    	View v = LayoutInflater.from(this).inflate(R.layout.del_confirm, null);
    	TextView title = (TextView)v.findViewById(R.id.tvDelTitle);
    	Button btnOk = (Button)v.findViewById(R.id.btnDelPositive);
    	Button btnCancel = (Button)v.findViewById(R.id.btnDelNegative);
    	String str = type==DELETE_SINGLE
    			?getResources().getString(R.string.tf_delete_email_by_index)
    			:getResources().getString(R.string.tf_delete_all_email);
    	title.setText(str);
    	btnOk.setText("OK");
    	btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Ca ca = new Ca(DVB.GetInstance());
				if(type == DELETE_SINGLE)
					ca.CaDeleteEmailByIndex(focus_index);
				else
					ca.CaDeleteAllEmail();
				Toast.makeText(Tf_email.this, Tf_email.this.getResources().getString(R.string.tf_mail_del_success), Toast.LENGTH_LONG).show();
				Tf_email.this.finish();
				Intent it4 = new Intent(Tf_email.this,Tf_email.class);
				startActivity(it4);
			}
		});
    	btnCancel.setText("Cancel");
    	btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
    	
    	popWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.BLACK));
    	
    	popWindow.setWidth(588);
    	popWindow.setHeight(266);
    	//设置触摸对话框外部以及按下返回键时对话框消失。
    	popWindow.setOutsideTouchable(true);
    	
    	popWindow.setFocusable(true);
    	popWindow.setContentView(v);
    	popWindow.showAtLocation(lv, Gravity.CENTER, 0, 0);
    	
    }// popDialog  --  end.

    public void SetOnItemSelectListener()
    {
        ListView emailListView = (ListView) findViewById(R.id.MyemailView);
        emailListView.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                email_index = arg2;
                Log.e("Tf_email", "email Listener"+email_index);
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });
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
