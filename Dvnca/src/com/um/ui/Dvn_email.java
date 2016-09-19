package com.um.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.um.dvnca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;

public class Dvn_email extends AppBaseActivity
{
    private static int focus_index;
    private int email_cn;
    private int email_index;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dvt_email);

        try
        {
            get_email_list();
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("Dvn_email", "email open");
        SetOnItemSelectListener();
        SetOnKeyListener();
        SetOnItemClickListener();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void  get_email_list() throws UnsupportedEncodingException
    {
    	ListView emailListView = (ListView) findViewById(R.id.MyemailView);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        int []buff_len = {204800};
        byte [] buff = new byte[buff_len[0]];
        int offset = 0;
        int i ;
        int j ;
        int k ;
        int readcount = 0;
        String sender_str;
        String readflag_str;
        String theme_str;
        byte[] theme_byte = new byte[256];
        byte[] sender_byte = new byte[258];

        /*Get the buffer*/
        Ca ca = new Ca(DVB.GetInstance());
        int ret = ca.CaGetEmailheads(buff, buff_len);
        Log.i("Dvn_email", "ret:"+ret);
        Log.i("Dvn_email", "buff_len[0]:"+buff_len[0]);
        if ((0 == ret) && (buff_len[0] != 0))
        {
    		 //String jsonStr = new String(buff, 0, buff_len[0]);
//   			 System.out.println("jsonStr:"+jsonStr);
   			 
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

   		                /*Sender*/
  		                sender_str = jsonArrayMailInfo.getJSONObject(i).getString("as8Sender");
                		
   		                /*Email theme*/	   		                
  		                theme_str = jsonArrayMailInfo.getJSONObject(i).getString("title");
 
   		                /*Readflag*/
   		                int readflag = (jsonArrayMailInfo.getJSONObject(i).getInt("bReadFlag")) ;

   		        Log.i("email","readflag:"+readflag);
   		        
                if (1 == readflag)
                {
                    readflag_str = this.getString(R.string.dvt_not_read);
                    readcount = readcount + 1;                	
                }
                else
                {
                    readflag_str = this.getString(R.string.dvt_readed);
                }

   		                map.put("index", index_str);
   		                map.put("theme", theme_str);
   		                map.put("readflag", readflag_str);
   		                map.put("sender", sender_str);
   		                
   		                map.put("emailid", String.valueOf(i+1));
   		                //map.put("delete","");  //this column is reserved

   		                list.add(map);
   		            }
   				 	
   				    
   				} catch (JSONException ex) {  
   					System.out.println("get JSONObject fail");
   				}  
	   		
        }	
        
		TextView read_email = (TextView)this.findViewById(R.id.mailread);
		read_email.setText(String.valueOf(readcount));
        
		TextView max_email = (TextView)this.findViewById(R.id.maxmail_num);
		max_email.setText(String.valueOf(50));
		
		TextView count = (TextView)this.findViewById(R.id.mail_count);
		count.setText(String.valueOf(email_cn));
		
        SimpleAdapter emailAdaptor = new SimpleAdapter(this, list,
                R.layout.dvt_email_detailitem,
                new String[] {"emailid","theme","readflag","sender"},
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
        		if(getCardStatus() != true){
        			new AlertDialog.Builder(Dvn_email.this).setMessage(R.string.ca_insert_card).setPositiveButton("ok", null).show();
        			return;
        		}
        		
                // TODO Auto-generated method stub
                ListView listView = (ListView)arg0;
                
				HashMap<String, String> map = (HashMap<String, String>)listView.getItemAtPosition(arg2);
                String email_index = map.get("index");
                String email_title = map.get("theme");
                Intent it = new Intent();
                Log.e("Dvn_email", "email SetOnItemClickListener"+email_index);
                
                int index_int = Integer.parseInt(email_index);
                int dvn_cur_mail_id;
                dvn_cur_mail_id = email_cn -index_int-1;//杩涜浜嗘帓搴�
                String index_str = String.valueOf(dvn_cur_mail_id);
                
                it.putExtra("email_index", index_str);   //Send data from one activity to another
                it.putExtra("email_title", email_title);
                it.setClass(Dvn_email.this, Dvn_email_read.class);
                Dvn_email.this.startActivity(it);
                //Dvn_email.this.finish();   //In case of refresh this activity,but is not user_friendly
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
                if (keyCode == KeyEvent.KEYCODE_F1)
                {
                  
                     Log.e("Dvn_email", "email open KEYCODE_F1"+email_index);
                    //SetOnItemSelectListener();  //Get focus index
                    
                    focus_index = email_cn -email_index-1;//杩涜浜嗘帓搴�
                    new AlertDialog.Builder(Dvn_email.this)
                    .setMessage(R.string.dvt_delete_email_by_index)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
        					if(getCardStatus() != true){
        						new AlertDialog.Builder(Dvn_email.this).setMessage(R.string.ca_insert_card).setPositiveButton("ok", null).show();
        						return;
        					}
        					
                            Ca ca = new Ca(DVB.GetInstance());
                            int ret = ca.CaDeleteEmailByIndex(focus_index);
                            Log.i("delete mail", "ret:"+ret);
                            if (ret == 0)
                            {
    							Toast.makeText(Dvn_email.this, Dvn_email.this.getResources().getString(R.string.dvt_mail_del_success), Toast.LENGTH_LONG).show();                            	
                                Dvn_email.this.finish();
                				Intent it4 = new Intent(Dvn_email.this,Dvn_email.class);
                				startActivity(it4);
                            }
                            else if (ret == 0x80000039)
                            {
    							Toast.makeText(Dvn_email.this, Dvn_email.this.getResources().getString(R.string.pin_mail_not_delete), Toast.LENGTH_LONG).show();                            	                            	
                            	
                            }
                            else if (ret == 0x80000000)
                            {
    							Toast.makeText(Dvn_email.this, Dvn_email.this.getResources().getString(R.string.dvt_caerr_unknown), Toast.LENGTH_LONG).show();                            	                 	                            	
                            }

                        }
                    }
                                      )
//                                      
//					.setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
//					{
//					    @Override
//					    public void onClick(DialogInterface dialog, int i) {
//					        dialog.dismiss();
//					    }
//					})
                    .show();
                    return true ;
                }
                else if (keyCode == KeyEvent.KEYCODE_F2)
                {
                    Log.e("Dvn_email", "email open KEYCODE_F2");
                    new AlertDialog.Builder(Dvn_email.this)
                    .setMessage(R.string.dvt_delete_all_email)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
        					if(getCardStatus() != true){
        						new AlertDialog.Builder(Dvn_email.this).setMessage(R.string.ca_insert_card).setPositiveButton("ok", null).show();
        						return;
        					}                        	
                            Ca ca = new Ca(DVB.GetInstance());
                            ca.CaDeleteAllEmail();
							Toast.makeText(Dvn_email.this, Dvn_email.this.getResources().getString(R.string.dvt_mail_del_success), Toast.LENGTH_LONG).show();
                            Dvn_email.this.finish();
            				Intent it5 = new Intent(Dvn_email.this,Dvn_email.class);
            				startActivity(it5);
                        }
                    }
                                      )
          
                    .show();
                    return true;
                }

                return false ;
            }
        });
    }

    public void SetOnItemSelectListener()
    {
        ListView emailListView = (ListView) findViewById(R.id.MyemailView);
        emailListView.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                email_index = arg2;
                Log.e("Dvn_email", "email Listener"+email_index);
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });
    }

    private boolean getCardStatus(){
    	Ca ca = new Ca(DVB.GetInstance());
    	boolean []cardStatus = new boolean[1];
    	
    	int ret = ca.CaGetCardStatus(cardStatus);
    	Log.d("Dvn_condition_access", "ret:" +ret);
    	Log.d("Dvn_condition_access", "cardStatus[0]:" +cardStatus[0]);
    	return cardStatus[0];
    }
    
}
