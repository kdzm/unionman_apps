package com.unionman.settings.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiAoService;

public class VoiceHdmi extends RightWindowBase {
	
	private HiAoService mAOService;
	private RadioGroup rg_hdmi;
	private RadioButton rb_touchuan;
	private RadioButton rb_close;
	private RadioButton rb_auto;
	private RadioButton rb_jiema;
	private static final String TAG = "com.unionman.settings.content.display--VoiceHdmi--";
	SharedPreferences sharedPreferences;
	private Context mcontex;
	public VoiceHdmi(Context paramContext) {
	
		super(paramContext);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvisible() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId() {
		this.levelId = 1002;

	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.voice_hdmi, this);
		rg_hdmi = (RadioGroup) findViewById(R.id.hdmigroup_id);
		rb_touchuan = (RadioButton) findViewById(R.id.hdmitouchuan_id);
		rb_close = (RadioButton) findViewById(R.id.hdmiclose_id);
		rb_auto = (RadioButton) findViewById(R.id.hdmiauto_id);
		rb_jiema =(RadioButton) findViewById(R.id.hdmijiema_id);
		//rg_hdmi.requestFocus();
		
	//	sharedPreferences = PreferenceManager
	//			.getDefaultSharedPreferences(mcontex);
		intdefaulthdmiVoice();
		rg_hdmi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mAOService = new HiAoService();				
				if(checkedId == R.id.hdmiclose_id){
					Logger.i(TAG, "R.id.hdmiclose_id");
					mAOService.setAudioOutput(1, 0);
				}
				if(checkedId == R.id.hdmiauto_id){
					mAOService.setAudioOutput(1, 1);
				}
				if(checkedId == R.id.hdmijiema_id){
					mAOService.setAudioOutput(1, 2);
					rb_touchuan.setEnabled(false);
				}else{
					rb_touchuan.setEnabled(true);
				}
				if(checkedId == R.id.hdmitouchuan_id){
					mAOService.setAudioOutput(1, 3);
				}
			
			}
		});
	}

	private void intdefaulthdmiVoice() {
		Logger.i(TAG,"intdefaulthdmiVoice()--");
		if(mAOService == null){
			mAOService = new HiAoService();
		}
		int srentry = mAOService.getAudioOutput(1);
		Logger.i(TAG, "srentry"+srentry);
		if(srentry == 0){
			rb_close.requestFocus();
			rb_close.setChecked(true);
		}else if(srentry == 1){
			rb_auto.requestFocus();
			rb_auto.setChecked(true);
		}
		else if(srentry == 2){
			rb_jiema.requestFocus();
			rb_jiema.setChecked(true);
		}
		else if(srentry == 3){
			rb_touchuan.requestFocus();
			rb_touchuan.setChecked(true);
		}else{rg_hdmi.requestFocus();}
		
		
		
	}

}
