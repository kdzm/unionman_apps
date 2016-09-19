package com.um.ui;

import com.um.dvbsettings.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.um.controller.AppBaseActivity;
public class AudioSetting extends AppBaseActivity {
	private Spinner audio_spinner1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_setting);
        
        audio_spinner1 = (Spinner)findViewById(R.id.audio_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
        	      this, R.array.audio_array, android.R.layout.simple_spinner_item); 
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        audio_spinner1.setAdapter(adapter);
        audio_spinner1.setPrompt("ѡ��");
        audio_spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	
        	public void onItemSelected(AdapterView<?> adapterView,View view,int position,long id){
        		String selected = adapterView.getItemAtPosition(position).toString();
        		System.out.println(selected);
        	}
        	
        	public void onNothingSelected(AdapterView<?> adapterView){
        		System.out.println("nothingSelected");
        	}
        });
    }
}
