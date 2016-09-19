package com.um.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import com.um.controller.AppBaseActivity;
import com.um.tfca.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

public class Tf_tezhengzhi_info extends AppBaseActivity {
	private String operid;
	private TextView ac_txt;
	private TextView bid_txt;
	private TextView eigenvalue_txt1;
	private TextView eigenvalue_txt2;
	private TextView eigenvalue_txt3;
	private TextView eigenvalue_txt4;
	private TextView eigenvalue_txt5;
	private TextView eigenvalue_txt6;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tf_tezhengzhi_info);
        
        Intent intent = getIntent();
		operid = intent.getStringExtra("operids");
		int operid_int = Integer.parseInt(operid);
		Ca ca = new Ca(DVB.GetInstance());
        int[] array = new int[18];
		int ret = ca.CaGetEigenvalue(operid_int,array);
		if(ret == 0)
		{
			ac_txt = (TextView)findViewById(R.id.textView2);
			bid_txt = (TextView)findViewById(R.id.textView4);
			eigenvalue_txt1 = (TextView)findViewById(R.id.textView6);
			eigenvalue_txt2 = (TextView)findViewById(R.id.textView7);
			eigenvalue_txt3 = (TextView)findViewById(R.id.textView8);
			eigenvalue_txt4 = (TextView)findViewById(R.id.textView11);
			eigenvalue_txt5 = (TextView)findViewById(R.id.textView9);
			eigenvalue_txt6 = (TextView)findViewById(R.id.textView10);
			
            String str_ac_txt ="0x"+convertDecimalToBinary(array[0]);
            String str_bid_txt ="0x"+convertDecimalToBinary(array[1]);
            String str_eigenvalue_txt1 ="0x"+convertDecimalToBinary(array[4]);
            String str_eigenvalue_txt2 ="0x"+convertDecimalToBinary(array[5]);
            String str_eigenvalue_txt3 ="0x"+convertDecimalToBinary(array[6]);
            String str_eigenvalue_txt4 ="0x"+convertDecimalToBinary(array[7]);
            String str_eigenvalue_txt5 ="0x"+convertDecimalToBinary(array[8]);
            String str_eigenvalue_txt6 ="0x"+convertDecimalToBinary(array[9]);
            
			ac_txt.setText(str_ac_txt);
			bid_txt.setText(str_bid_txt);
			eigenvalue_txt1.setText(str_eigenvalue_txt1);
			eigenvalue_txt2.setText(str_eigenvalue_txt2);
			eigenvalue_txt3.setText(str_eigenvalue_txt3);
			eigenvalue_txt4.setText(str_eigenvalue_txt4);
			eigenvalue_txt5.setText(str_eigenvalue_txt5);
			eigenvalue_txt6.setText(str_eigenvalue_txt6);
		}   
    }
    
    public static String convertDecimalToBinary ( int value){
    	  String ret = Integer.toHexString(value);
    	  int len = ret.length();
    	  switch(len)
    	  {
    	  case 1:
    		  ret = "0000000"+ret;
    		  break;
    	  case 2:
    		  ret = "000000"+ret;
    		  break;
    	  case 3:
    		  ret = "00000"+ret;
    		  break;
    	  case 4:
    		  ret = "0000"+ret;
    		  break;    	  
    	  case 5:
    		  ret = "000"+ret;
    		  break;
    	  case 6:
    		  ret ="00"+ret;
    		  break;
    	  case 7:
    		  ret = "0"+ret;
    		  break;
    	  default:
    		  break;  		  
    	  }
    	  return ret;  	  
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
