package com.um.ui;

import java.util.ArrayList;
import java.util.List;

import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.um.dvbsettings.R;

import com.hisilicon.android.tvapi.Audio;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.Picture;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;


public class SoundChannelSetting extends AppBaseActivity {
	protected static final String TAG = "SoundChannelSetting";
	private Spinner mode_spinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soundchannel_setting);
     
        mode_spinner = (Spinner)findViewById(R.id.soundChannelSetting);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
//        	      this, R.array.sound_chn_array, R.layout.simple_spinner_item); 
        List<CharSequence> spinnerVal = new ArrayList<CharSequence>();
        spinnerVal.add(getResources().getString(R.string.sound_chn_array_item0));
        spinnerVal.add(getResources().getString(R.string.sound_chn_array_item1));
        spinnerVal.add(getResources().getString(R.string.sound_chn_array_item2));
        spinnerVal.add(getResources().getString(R.string.sound_chn_array_item3));
        ArrayAdapter<CharSequence> adapter =  
        		new ArrayAdapter<CharSequence>(SoundChannelSetting.this, R.layout.simple_spinner_item,spinnerVal ){
        	   @Override
               public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
        		   parent.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						// TODO Auto-generated method stub
						   if(arg2.getAction() == KeyEvent.ACTION_DOWN){
							   Log.i(TAG,"keycode="+arg1);
							   switch (arg1) {
							     case KeyEvent.KEYCODE_MENU:
							    	 Log.i(TAG, "parent KEYCODE_MENU is click");
							    	  new Thread() {
							    		   public void run() {
							    		    try {
							    		     Instrumentation inst = new Instrumentation();
							    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
							    		    } catch (Exception e) {
							    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
							    		    }
							    		   }
							    		  }.start();
									break;
								 case KeyEvent.KEY_SOURCEENTER:	
									 Log.i(TAG, "parent KEY_SOURCEENTER is click");
							    	  new Thread() {
							    		   public void run() {
							    		    try {
							    		     Instrumentation inst = new Instrumentation();
							    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
							    		    } catch (Exception e) {
							    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
							    		    }
							    		   }
							    		  }.start();
						    		 return true;
								}
						   }
						return false;
					}
				});
                   return super.getDropDownView(position, convertView, parent);
               }
       	
        };
        
        adapter.setDropDownViewResource(R.layout.simple_spinner_item);

        mode_spinner.setAdapter(adapter);
		int soundchannel;
		soundchannel = ParamSave.GetSoundChannel();
		soundchannel = soundchannel > 3 ? 3 : soundchannel;
		
		mode_spinner.setSelection(soundchannel);

		mode_spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                    /*ParamSave.SetStopMode(mode_spinner.getSelectedItemPosition());*/
			}


		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ParamSave.SetSoundChannel(mode_spinner.getSelectedItemPosition());
			int soundChannel = EnumSoundTrack.TRACK_STEREO;
			switch(mode_spinner.getSelectedItemPosition())
			{
				case 0:
					soundChannel = EnumSoundTrack.TRACK_STEREO;
					break;
				case 1:
					soundChannel = EnumSoundTrack.TRACK_DOUBLE_MONO;
					break;
				case 2:
					soundChannel = EnumSoundTrack.TRACK_DOUBLE_LEFT;
					break;
				case 3:
					soundChannel = EnumSoundTrack.TRACK_DOUBLE_RIGHT;
					break;
				default:
					break;
			}
			UmtvManager.getInstance().getAudio().setTrackMode(soundChannel);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		});

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
	     case KeyEvent.KEYCODE_MENU:
			finish();
			break;
		 case KeyEvent.KEY_SOURCEENTER:	
             //mode_spinner click event has not to deal!
	    	  new Thread() {
	    		   public void run() {
	    		    try {
	    		     Instrumentation inst = new Instrumentation();
	    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
	    		    } catch (Exception e) {
	    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
	    		    }
	    		   }
	    		  }.start();			 
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
