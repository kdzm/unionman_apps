package com.um.ui;

import java.io.FileInputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;
import com.um.ui.Tf_general_authorized;

public class Tf_feed_card  extends AppBaseActivity{
	
	private static final String TAG = "Tf_feed_card";
	private String operid="";
	private int pbIsChild;
	private int pu32DelayTime;
	private int pu32LastFeedTime;
	private int  iscanfeed;
	private String parent_card_sn = "";
	private int operid_int;
	
	private TextView card_type_txt;
	private TextView feed_period_txt;
	private TextView last_feed_time_txt;
	private TextView parent_card_no_txt;
	private Button button1;

	public void onCreate(Bundle savedInstanceState)
	{
		Log.i("Tf_feed_card","onCreate run");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tf_feed_card);
		Intent intent = getIntent();
		operid = intent.getStringExtra("operids");
		operid_int = Integer.parseInt(operid);
		
		findTextView();
		showFeedInfo();
       
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				button1Click();							
				
			}
		});
	}
	
	private void findTextView(){
		card_type_txt = (TextView)findViewById(R.id.cardtype);
		feed_period_txt = (TextView)findViewById(R.id.feedperiod);
		last_feed_time_txt = (TextView)findViewById(R.id.lastfeedtime);
		parent_card_no_txt = (TextView)findViewById(R.id.parentcardno);
	}
	
	private void showFeedInfo(){
		
		final Ca ca = new Ca(DVB.GetInstance());
		
		int ret = getChildCardStatus();
		
		if(ret == 0)
		{
			if(pbIsChild == 0)
			{
				card_type_txt.setText(R.string.tf_parent_card);
				feed_period_txt.setText("");
				last_feed_time_txt.setText("");
				parent_card_no_txt.setText("");
			}
			else
			{
				card_type_txt.setText(R.string.tf_child_card);
				feed_period_txt.setText(String.valueOf(pu32DelayTime));
				
				String ymd_str="";
				String hour = "";
				String minute = "";
				String sec = "";
				String last_feed_time_str="";
				
				Tf_general_authorized general_authorized = new Tf_general_authorized();
				ymd_str = general_authorized.tf_YMD_calculate(pu32LastFeedTime>>16);
				
				hour = String.valueOf((pu32LastFeedTime>>11)&0x1f);
				minute = String.valueOf((pu32LastFeedTime>>5)&0x3f);
				sec = String.valueOf((pu32LastFeedTime&0x1f)*2);
				last_feed_time_str = ymd_str + " " + " " + " " + hour + ":" + minute + ":" + sec + "" ;
				last_feed_time_txt.setText(last_feed_time_str);
				
				parent_card_no_txt.setText(parent_card_sn);	

			}		
		}
	}

	private int getChildCardStatus(){
		final Ca ca = new Ca(DVB.GetInstance());
		
		int []buff_len = {512};
		byte []buff = new byte[buff_len[0]];
		
		int ret = ca.CaGetOperatorChildStatus(operid_int, buff, buff_len);
		Log.i("Tf_feed_card","getChildCardStatus ret:"+ret);
		Log.i("Tf_feed_card","buff_len:"+buff_len[0]);
		
		if((0 == ret) && (0 != buff_len[0])){
			String jsonStr = new String(buff, 0, buff_len[0]);
			Log.i("Tf_feed_card", "jsonStr:" +jsonStr);
		 try {  
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			 	pu32DelayTime = jsonObject.getInt("delay_time");
			 	pu32LastFeedTime = jsonObject.getInt("last_feed_time");
			 	pbIsChild = jsonObject.getInt("IsChild");
			 	iscanfeed = jsonObject.getInt("iscanfeed");
			 	parent_card_sn = jsonObject.getString("parent_card_num");
			 	System.out.format("pu32DelayTime:%d\n", pu32DelayTime);
			 	System.out.format("pu32LastFeedTime:%d\n", pu32LastFeedTime);
			 	System.out.format("pbIsChild:%d\n", pbIsChild);
			 	System.out.format("iscanfeed:%d\n", iscanfeed);
			 	System.out.format("parent_card_sn:%s\n", parent_card_sn); 
			} catch (JSONException ex) {  
				ret = -1;
				System.out.println("get JSONObject fail");// 异常处理代码  
			}
		}else{
			return -1;
		}
		
		return ret;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
	     case KeyEvent.KEYCODE_MENU:
			finish();
			break;
		 case KeyEvent.KEY_SOURCEENTER:	
			 Log.i(TAG, "KEY_SOURCEENTER is click");
			if(button1.isFocused()){
				 Log.i(TAG, "isFocused KEY_SOURCEENTER is click");
				 button1Click();
			 }

    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void button1Click() {
		Log.i("Tf_feed_card","DVB CA click,pbIsChild:" +pbIsChild);
		 /*pbIsChild : 0 is parent card, !0 is child card*/
		
		final Ca ca = new Ca(DVB.GetInstance());
		
		if(pbIsChild != 0)
		{
			Log.i("Tf_feed_card","DVB CA click,1iscanfeed:" +iscanfeed);
			if(iscanfeed == 0)
			{
				/*There is no need to feed child card,Inform before the feed-time*/
/*						new AlertDialog.Builder(Tf_feed_card.this)
				.setMessage(R.string.tf_feedtime_not_arrive)
				.setPositiveButton("ok" ,null)
				.show();*/
				Util.showConfirmDialog(Tf_feed_card.this,R.string.tf_feedtime_not_arrive);			
				return;
			}	
			/*Child card in the STB,inform insert child card*/
			Log.i("Tf_feed_card","DVB CA click enter:"+pbIsChild);	
			new AlertDialog.Builder(Tf_feed_card.this)
			.setMessage(R.string.tf_insert_parent_card)
			.setPositiveButton("ok" ,
			new DialogInterface.OnClickListener() {									
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
						/*Read from parent card*/
						final byte [] feeddata = new byte [1024];
						final int [] datalen = new int [1];

						int ret = ca.CaReadFeeddataFromParent(operid_int, feeddata, datalen);	
						if(ret == 0)
						{
							/*Read from parent card successfully*/
							/*Please insert child card*/
							new AlertDialog.Builder(Tf_feed_card.this)
							.setMessage(R.string.tf_insert_child_card)
							.setPositiveButton("ok" ,
								new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
											/*Read parent and child card state again*/
										
										int ret = getChildCardStatus();
										if(ret == 0)
										{
											 /*pbIsChild : 0 is parent card, !0 is child card*/
											 /*iscanfeed: 0 not need to feed,1 need to feed */
											Log.i("Tf_feed_card","Tf_feed_card"+pbIsChild);
											Log.i("Tf_feed_card","iscanfeed"+iscanfeed);
											if((pbIsChild != 0) && (iscanfeed == 0))
											{
												/*There is no need to feed child card,Inform before the feed-time*/
												new AlertDialog.Builder(Tf_feed_card.this)
												.setMessage(R.string.tf_feedtime_not_arrive)
												.setPositiveButton("ok" ,null)
												.show();
												return;
											}
											Log.i("Tf_feed_card","CaWriteFeeddataToChild");
											/*Child card need feed,write child card date*/
											ret = ca.CaWriteFeeddataToChild(operid_int,feeddata,datalen[0]);
											if(ret == 0)
											{
												/*Write parent card date successfully*/
												new AlertDialog.Builder(Tf_feed_card.this)
												.setMessage(R.string.tf_feedcard_ok)
												.setPositiveButton("ok" ,null)
												.show();
												
												showFeedInfo();
												return;
											}
											else if(ret == 0x11)
											{
												/*Before the feed-time,this return value need to be affirmed*/
												new AlertDialog.Builder(Tf_feed_card.this)
												.setMessage(R.string.tf_feedtime_not_arrive)
												.setPositiveButton("ok" ,null)
												.show();
												return;
											}
											else
											{
												/*Before the feed-time,this return value need to be affirmed*/
												new AlertDialog.Builder(Tf_feed_card.this)
												.setMessage(R.string.tf_feedcard_fail)
												.setPositiveButton("ok" ,null)
												.show();
												return;
											}
										}
									  }
									}
								)
							.show();
						}
						else
						{
							new AlertDialog.Builder(Tf_feed_card.this)
							.setMessage(R.string.tf_read_parentcard_fail)
							.setPositiveButton("ok" ,null)
							.show();
						}
					}
				}
			)
			.show();
		}
		else
		{
			/*Parent card,read from parent card*/
			final byte [] feeddata = new byte [1024];
			final int [] datalen = new int [1];
			int ret = ca.CaReadFeeddataFromParent(operid_int, feeddata, datalen);
			if(ret == 0)
			{
				/*Read from parent card successfully*/
				/*Please enter child card*/
				new AlertDialog.Builder(Tf_feed_card.this)
				.setMessage(R.string.tf_insert_child_card)
				.setPositiveButton("ok" ,
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
								/*Read parent card state again*/
							int ret = getChildCardStatus();
							if(ret == 0)
							{
								 /*pbIsChild : 0 is parent card, !0 is child card*/
								 /*iscanfeed: 0 not need to feed,1 need to feed */
								if((pbIsChild != 0) && (iscanfeed == 0))
								{
									/*There is no need to feed child card,Inform before the feed-time*/
									new AlertDialog.Builder(Tf_feed_card.this)
									.setMessage(R.string.tf_feedtime_not_arrive)
									.setPositiveButton("ok" ,null)
									.show();
									return;
								}
								
								/*Child card need feed,write child card date*/
								ret = ca.CaWriteFeeddataToChild(operid_int,feeddata,datalen[0]);
								if(ret == 0)
								{
									/*Write parent card date successfully*/
									new AlertDialog.Builder(Tf_feed_card.this)
									.setMessage(R.string.tf_feedcard_ok)
									.setPositiveButton("ok" ,null)
									.show();
									
									showFeedInfo();
									return;
								}
								else if(ret == 0x11)
								{
									/*Before the feed-time,this return value need to be affirmed*/
									new AlertDialog.Builder(Tf_feed_card.this)
									.setMessage(R.string.tf_feedtime_not_arrive)
									.setPositiveButton("ok" ,null)
									.show();
									return;
								}
								else
								{
									/*Before the feed-time,this return value need to be affirmed*/
									new AlertDialog.Builder(Tf_feed_card.this)
									.setMessage(R.string.tf_feedcard_fail)
									.setPositiveButton("ok" ,null)
									.show();
									return;
								}
							}
						  }
						}
					)
				.show();
			}
			else
			{
/*						new AlertDialog.Builder(Tf_feed_card.this)
				.setMessage(R.string.tf_read_parentcard_fail)
				.setPositiveButton("ok" ,null)
				.show();*/
				Util.showConfirmDialog(Tf_feed_card.this,R.string.tf_read_parentcard_fail);	
				return;
			}				
		}
	}	
}
