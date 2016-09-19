package com.um.ui;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.um.dvtca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.controller.AppBaseActivity;

public class Dvt_feed_card  extends AppBaseActivity{
	
	private int ret;
	private byte [] feeddata = new byte [1024];
	private int [] datalen = new int [1];
	private AlertDialog.Builder builder;
	private AlertDialog.Builder ChildBuilder;
	private Context mContext = Dvt_feed_card.this;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_feed_card);
		
		final Ca ca = new Ca(DVB.getInstance());

		builder = new AlertDialog.Builder(Dvt_feed_card.this);
		builder.setMessage(R.string.dvt_insert_parent_card);
		builder.setPositiveButton("cancle", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("Dvt_feed_card","cancle");
				return;
			}
		});
		
		builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				IsMotherOrChild();
				
				ret = ca.CaReadFeeddataFromParent(0,feeddata,datalen);	
				if (ret == 0)
				{
					Log.i("CaReadFeeddataFromParent","ok ========= ok");
					ShowChildPeriod();
				}
				else
				{
					Log.i("CaReadFeeddataFromParent","ok ========= fail");
					
					AlertDialog.Builder parent_fail_builder = new AlertDialog.Builder(Dvt_feed_card.this);
					LinearLayout layout = (LinearLayout) LayoutInflater.from(Dvt_feed_card.this).inflate(R.layout.dvt_feed_card_tip, null);
					parent_fail_builder.setView(layout);
					Button okButton = (Button)layout.findViewById(R.id.ok_btn);
					Button cancleButton = (Button) layout.findViewById(R.id.cancle_btn);
					final TextView text1 = (TextView) layout.findViewById(R.id.feed_text); 
					final AlertDialog parentFailDialog = parent_fail_builder.create();
					text1.setText(R.string.get_mother_card_info_fail);
					parentFailDialog.show();	
					View.OnClickListener onClickListener = new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
							case R.id.ok_btn:
							{
								IsMotherOrChild();
								
								ret = ca.CaReadFeeddataFromParent(0,feeddata,datalen);
								if (ret == 0)
								{
									parentFailDialog.cancel();
									ShowChildPeriod();
								}
								else
								{
									text1.setText(R.string.get_mother_card_info_fail);
								}
							}
								break;
							case R.id.cancle_btn:
								parentFailDialog.cancel();
								break;
							default:
								break;
							}
						}
					};
					okButton.setOnClickListener(onClickListener);
					cancleButton.setOnClickListener(onClickListener);
					
				}			
					
			}

		});
		
		AlertDialog dialog = builder.create();
		dialog.show();

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
	
	@Override
	protected void onPause(){
        super.onPause();
        finish();
	}
	
	private void ShowChildPeriod()
	{
		//Log.i("CaWriteFeeddataToChild","ok =====1111==== ok");						
		final Ca ca = new Ca(DVB.getInstance());
		
		ChildBuilder = new AlertDialog.Builder(Dvt_feed_card.this);
		ChildBuilder.setMessage(R.string.get_mother_card_info_success);
		ChildBuilder.setPositiveButton("cancle", new DialogInterface.OnClickListener() {						
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		ChildBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				IsMotherOrChild();
				ret = ca.CaWriteFeeddataToChild(0,feeddata,datalen[0]);
				
				if (ret == 0)
				{
					AlertDialog.Builder ParentScuccessBuilder = new AlertDialog.Builder(Dvt_feed_card.this);
					LinearLayout layout = (LinearLayout) LayoutInflater.from(Dvt_feed_card.this).inflate(R.layout.dvt_feed_card_tip, null);
					ParentScuccessBuilder.setView(layout);
					Button okButton = (Button)layout.findViewById(R.id.ok_btn);
					Button cancleButton = (Button) layout.findViewById(R.id.cancle_btn);
					final TextView text = (TextView) layout.findViewById(R.id.feed_text);
					final AlertDialog parentFailDialog = ParentScuccessBuilder.create();
					text.setText(R.string.feed_card_success);
					parentFailDialog.show();	
					View.OnClickListener onClickListener = new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
							case R.id.ok_btn:
							{
								IsMotherOrChild();										
								ret = ca.CaWriteFeeddataToChild(0,feeddata,datalen[0]);
								if (ret == 0)
								{
									text.setText(R.string.feed_card_success);
								}
								else
								{
									text.setText(R.string.dvt_feedcard_fail);
								}
							}
								break;
							case R.id.cancle_btn:
								parentFailDialog.cancel();
								break;
							default:
								break;
							}
						}
					};
					
					okButton.setOnClickListener(onClickListener);
					cancleButton.setOnClickListener(onClickListener);
					
				}
				else
				{
					AlertDialog.Builder ParentFailBuilder = new AlertDialog.Builder(Dvt_feed_card.this);
					LinearLayout layout = (LinearLayout) LayoutInflater.from(Dvt_feed_card.this).inflate(R.layout.dvt_feed_card_tip, null);
					ParentFailBuilder.setView(layout);
					Button okButton = (Button)layout.findViewById(R.id.ok_btn);
					Button cancleButton = (Button) layout.findViewById(R.id.cancle_btn);
					final TextView text2 = (TextView) layout.findViewById(R.id.feed_text);
					final AlertDialog parentFailDialog = ParentFailBuilder.create();
					text2.setText(R.string.dvt_feedcard_fail);
					parentFailDialog.show();	
					View.OnClickListener onClickListener = new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
							case R.id.ok_btn:
							{
								IsMotherOrChild();										
								ret = ca.CaWriteFeeddataToChild(0,feeddata,datalen[0]);
								if (ret == 0)
								{
									text2.setText(R.string.dvt_feedcard_ok);
								}
								else
								{
									text2.setText(R.string.dvt_feedcard_fail);
								}
							}
								break;
							case R.id.cancle_btn:
								parentFailDialog.cancel();
								break;
							default:
								break;
							}
						}
					};
					okButton.setOnClickListener(onClickListener);
					cancleButton.setOnClickListener(onClickListener);
					
				}
			}
		});
		
		AlertDialog ChildDialog = ChildBuilder.create();
		ChildDialog.show();			
		
	}
	
	private void IsMotherOrChild()
	{
		int i = 0;
		int ret = 0;
		
		final Ca ca = new Ca(DVB.getInstance());
		
		for (i=0; i<3;i++)
		{
	        int []motherinfo_len = {80};
	        byte []motherinfo = new byte[motherinfo_len[0]];
            
	        try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	        
	        ret = ca.CaGetMotherInfo(0, motherinfo, motherinfo_len);
			if ((ret == 0)&&(motherinfo_len[0] != 0))
			{
				//Log.i("CaGetMotherInfo","success");							
				break;
			}
			else
			{
				//Log.i("CaGetMotherInfo","fail");
				continue;							
			}
		}		
		
		return;
	}
	


	
	
}
