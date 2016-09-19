package com.um.ui;

import com.um.controller.AppBaseActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.um.dvbsettings.R;
import android.util.Log;

public class ProgramInfo extends AppBaseActivity {
    private TextView frequency_txt_val;
    private TextView symbol_txt_val; 
    private TextView modulation_txt_val;
    private TextView bandwidth_txt_val;
    private TextView symbol_txt; 
    private TextView modulation_txt;
    private TextView bandwidth_txt;
    private TextView signal_strenth_txt;
    private SeekBar signal_sbar;
	
    private TextView program_num_txt_val;  
    private TextView program_name_txt_val;
    private TextView sound_pid_txt_val; 
    private TextView video_pid_txt_val;
    private TextView video_codec_txt_val;
    private TextView audio_codec_txt_val;
    private TextView video_resolution_txt_val;
	
    private TextView signal_strenth_txt_val;
    private TextView signal_snr_txt_val;
    private TextView signal_ber_txt_val;
	
    private Bundle playInfo = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.program_info);
        frequency_txt_val =(TextView) findViewById(R.id.frequency_txt_val);
        symbol_txt_val =(TextView) findViewById(R.id.symbol_txt_val);
        modulation_txt_val =(TextView) findViewById(R.id.modulation_txt_val);
        bandwidth_txt_val = (TextView) findViewById(R.id.bandwidth_txt_val);
 
        symbol_txt =(TextView) findViewById(R.id.symbol_txt);
        modulation_txt =(TextView) findViewById(R.id.modulation_txt);
        bandwidth_txt = (TextView) findViewById(R.id.bandwidth_txt);
        
        program_num_txt_val =(TextView) findViewById(R.id.program_num_txt_val);
        program_name_txt_val =(TextView) findViewById(R.id.program_name_txt_val);
        sound_pid_txt_val =(TextView) findViewById(R.id.sound_pid_txt_val);
        video_pid_txt_val =(TextView) findViewById(R.id.video_pid_txt_val);
        video_codec_txt_val =(TextView) findViewById(R.id.video_codec_txt_val);
        audio_codec_txt_val =(TextView) findViewById(R.id.audio_codec_txt_val);
        video_resolution_txt_val =(TextView) findViewById(R.id.video_resolution_txt_val);
		
        signal_strenth_txt =(TextView) findViewById(R.id.signal_strenth_txt);
        signal_strenth_txt_val =(TextView) findViewById(R.id.signal_strenth_txt_val);
        signal_sbar=(SeekBar) findViewById(R.id.signal_seekbar);
        signal_sbar.setMax(100);

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}

    @Override
    protected void onResume() {
        super.onResume();
        playInfo = getIntent().getExtras();
        if(playInfo!=null)
        {
        	int fre = playInfo.getInt("freq", 0)*10;
        	String val = String.valueOf(fre)+" Khz";
        	frequency_txt_val.setText(val);
        	
        	String fendType = playInfo.getString("fend_type");
        	if (fendType != null && fendType.equals("DTMB")) {
        		modulation_txt_val.setVisibility(View.GONE);
        		symbol_txt_val.setVisibility(View.GONE);
        		bandwidth_txt_val.setVisibility(View.VISIBLE);
        		modulation_txt_val.setVisibility(View.GONE);
        		symbol_txt.setVisibility(View.GONE);
        		bandwidth_txt.setVisibility(View.VISIBLE);
        		modulation_txt.setVisibility(View.GONE);
				
        		signal_strenth_txt.setVisibility(View.VISIBLE);
        		signal_strenth_txt_val.setVisibility(View.VISIBLE);
				
        		symbol_txt_val.setVisibility(View.GONE);
        		bandwidth_txt_val.setVisibility(View.VISIBLE);       		
        		int bandwidth = playInfo.getInt("bandwidth", 0);
        		bandwidth_txt_val.setText(bandwidth + "MHz");
        	} else {
        		symbol_txt_val.setVisibility(View.VISIBLE);
	        	modulation_txt_val.setVisibility(View.VISIBLE);
	        	bandwidth_txt_val.setVisibility(View.GONE);
        		symbol_txt.setVisibility(View.VISIBLE);
	        	modulation_txt.setVisibility(View.VISIBLE);
	        	bandwidth_txt.setVisibility(View.GONE);
				
        		signal_strenth_txt.setVisibility(View.VISIBLE);
        		signal_strenth_txt_val.setVisibility(View.VISIBLE);
				
	        	int symbl = playInfo.getInt("symb", 0);
	        	val = String.valueOf(symbl)+" Kpbs";
	        	symbol_txt_val.setText(val);
	        	
	        	int qam = playInfo.getInt("qam", 0);
	        	switch(qam)
	        	{
		        	case 1:val = "16 QAM";break;
		        	case 2:val = "32 QAM";break;
		        	case 3:val = "64 QAM";break;
		        	case 4:val = "128 QAM";break;
		        	case 5:val = "256 QAM";break;
		        		default:
		        			val = "未知";
		        			break;
	        	}
	        	modulation_txt_val.setText(val);

        	}
        	
        	program_name_txt_val.setText(playInfo.getString("progname"));
        	
        	int progid = playInfo.getInt("progid");
        	val = String.valueOf(progid);		
        	program_num_txt_val.setText(val);
        	
        	int vpid = playInfo.getInt("vpid",0x1fff);
        	val = String.valueOf(vpid);
        	video_pid_txt_val.setText(val);
        	
        	int apid = playInfo.getInt("apid", 0x1fff);
        	val = String.valueOf(apid);
        	sound_pid_txt_val.setText(val);
			
        	int signalstrenth = playInfo.getInt("signalstrenth", 0);
			signalstrenth = signalstrenth%101;
			
        	String acodec = playInfo.getString("adec_type", "");
        	String vcodec = playInfo.getString("vdec_type", "");
        	int vidWidth = playInfo.getInt("vid_width", 0);
        	int vidHeight = playInfo.getInt("vid_height", 0);

			val = String.valueOf(signalstrenth);
			signal_strenth_txt_val.setText(val+"%");
			signal_sbar.setProgress(signalstrenth);
			
        	audio_codec_txt_val.setText(acodec);
        	video_codec_txt_val.setText(vcodec);
        	video_resolution_txt_val.setText(vidWidth + "*" + vidHeight);
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
