package com.um.ui;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.tfca.R;
import com.unionman.jazzlib.*;
//import android.os.SystemProperties;

public class Stb_version_info extends AppBaseActivity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stb_version_info);
		
		Ca ca = new Ca(DVB.GetInstance());
		
		 /*STDID*/
		String s_serialno = null;
		
		s_serialno = SystemProperties.get("ro.serialno","");

        String serialno = "0";
		char[] buf=new char[7];
        if (s_serialno.length() >= 17) {
            s_serialno.getChars(10, 17, buf, 0);//只用到流水号，和送给CA库的保持一致
            serialno = charsToHexString(buf);
        }
		Log.i("Stb_version_info", "serialno:" +serialno);
		
		String s_platformID = null;
		int platformID = ca.CaGetPlatformID();
		s_platformID = Integer.toHexString(platformID);
		Log.i("Stb_version_info", "s_platformID:" +s_platformID);
		
		StringBuffer s_stbID = new StringBuffer();
		s_stbID.append("0x");
		s_stbID.append(s_platformID);
		s_stbID.append(serialno);
		Log.i("Stb_version_info", "s_stbID:" +s_stbID);

		TextView stbid_txt = (TextView)findViewById(R.id.tv_stbid);
		stbid_txt.setText(s_stbID);
		
		
		String u32HardwareVersion =null;
		String u32CurSoftware =null;
		u32HardwareVersion = SystemProperties.get("ro.hardwareversion","");
		u32CurSoftware = SystemProperties.get("ro.umtv.sw.version","");
		
		TextView hardver = (TextView)this.findViewById(R.id.tv_hardware_ver);
		hardver.setText(String.valueOf(u32HardwareVersion));

		TextView softver = (TextView)this.findViewById(R.id.tv_software_ver);
		softver.setText(String.valueOf(u32CurSoftware));
		
		Log.i("Stb_version_info","DVB u32HardwareVersion:"+u32HardwareVersion);
		Log.i("Stb_version_info","DVB u32CurSoftware:"+u32CurSoftware);
		TextView loaderver = (TextView)this.findViewById(R.id.tv_loader_ver);
		loaderver.setText("1.0.0");

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}

	private String charsToHexString(char [] chars){
		
		if(chars == null){
			return "";
		}
        String str = String.valueOf(chars);
		int num = Integer.parseInt(str);
		String hexStr = convertDecimalToBinary(num);
		return hexStr;
	}
	
	private String convertDecimalToBinary (int value){
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
	
}	