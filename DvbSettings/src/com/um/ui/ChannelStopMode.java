package com.um.ui;

import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.um.dvbsettings.R;



public class ChannelStopMode extends AppBaseActivity {
	private Spinner mode_spinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_stop_mode);
     
        mode_spinner = (Spinner)findViewById(R.id.chnStopMode);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
        	      this, R.array.switch_chn_array, R.layout.simple_spinner_item); 
        adapter.setDropDownViewResource(R.layout.simple_spinner_item);

        mode_spinner.setAdapter(adapter);
		int stopmode;
		stopmode = ParamSave.GetStopMode();
		if((stopmode > 1) || (stopmode <0)){
			return;
		}
		
		mode_spinner.setSelection(stopmode);

		mode_spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                    /*ParamSave.SetStopMode(mode_spinner.getSelectedItemPosition());*/
			}


		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ParamSave.SetStopMode(mode_spinner.getSelectedItemPosition());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		});

	}
}
