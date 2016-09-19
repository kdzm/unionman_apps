package com.unionman.settings.content;

import android.R.string;
import android.content.Context;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiAoService;

public class VoiceActivity extends RightWindowBase {
	private HiAoService mAOService;
	private CheckRadioButton crb_hdmi;
	private CheckRadioButton crb_spdif;
	private static final String TAG = "com.unionman.settings.content.display--VoiceActivity";
	public VoiceActivity(Context paramContext) {
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
		initHdmiModeOldeChoice();
		initSpdifModeOldeChoice();
		//if(crb_hdmi.isChecked()){crb_hdmi.setChecked(false);}
		//if(crb_spdif.isChecked()){crb_spdif.setChecked(false);}
	}

	@Override
	public void setId() {
		this.levelId = 1001;

	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.voice, this);
		crb_hdmi = (CheckRadioButton) findViewById(R.id.hdmimode_id);
		crb_hdmi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				if(paramCheckRadioButton.isChecked()){
					try {
						VoiceActivity.this.layoutManager.showLayout(ConstantList.FRAME_VOICE_HDMI);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		crb_spdif = (CheckRadioButton) findViewById(R.id.spdifmode_id);
		crb_spdif.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				if(paramCheckRadioButton.isChecked()){
					try {
						VoiceActivity.this.layoutManager.showLayout(ConstantList.FRAME_VOICE_SPDIF);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		
		initHdmiModeOldeChoice();
		initSpdifModeOldeChoice();
		
	}

	private void initSpdifModeOldeChoice() {
		Logger.i(TAG,"initSpdifModeOldeChoice()--");
		if(mAOService == null){
			mAOService = new HiAoService();
		}
		int srentry = mAOService.getAudioOutput(2);
		Logger.i(TAG, "srentry"+srentry);
		if(srentry == 0){
			
		}else if(srentry == 1){
		
		}
		else if(srentry == 2){
			crb_spdif.setText2(this.getResources().getString(R.string.display_sound_hdmi_jiema));
		}
		else if(srentry == 3){
			crb_spdif.setText2(this.getResources().getString(R.string.display_sound_hdmi_touchuan));

		}}
		
	private void initHdmiModeOldeChoice() {
		Logger.i(TAG,"initHdmiModeOldeChoice()--");
		if(mAOService == null){
			mAOService = new HiAoService();
		}
		int srentry = mAOService.getAudioOutput(1);
		Logger.i(TAG, "srentry"+srentry);
		if(srentry == 0){
			crb_hdmi.setText2(this.getResources().getString(R.string.display_sound_hdmi_close));
		}else if(srentry == 1){
			crb_hdmi.setText2(this.getResources().getString(R.string.display_sound_hdmi_auto));
		}
		else if(srentry == 2){
			crb_hdmi.setText2(this.getResources().getString(R.string.display_sound_hdmi_jiema));
		}
		else if(srentry == 3){
			crb_hdmi.setText2(this.getResources().getString(R.string.display_sound_hdmi_touchuan));
		}}
		
	}

