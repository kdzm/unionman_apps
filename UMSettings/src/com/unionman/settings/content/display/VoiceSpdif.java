package com.unionman.settings.content;

import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiAoService;

public class VoiceSpdif extends RightWindowBase {

	private RadioButton rb_jiema;
	private RadioButton rb_touchuan;
	private RadioGroup mGroup;
	private HiAoService mAOService;
	private static final String TAG = "com.unionman.settings.content.display--VoiceSpdif--";
	public VoiceSpdif(Context paramContext) {
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
		this.layoutInflater.inflate(R.layout.voice_spdif,this);
		rb_jiema = (RadioButton)findViewById(R.id.spdifjiema_id);
		rb_touchuan =(RadioButton)findViewById(R.id.spdiftouchuan_id);
		
		mGroup = (RadioGroup)findViewById(R.id.spdifgroup_id);
		initdefaultchoice();
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mAOService = new HiAoService();	
				if(checkedId == R.id.spdifjiema_id){
					mAOService.setAudioOutput(2, 2);
					
				}
				else if(checkedId == R.id.spdiftouchuan_id){
					mAOService.setAudioOutput(2, 3);
					}
				
			}
		});
	}

	private void initdefaultchoice() {
		Logger.i(TAG,"initdefaultchoice()--");
		if(mAOService == null){
			mAOService = new HiAoService();
		}
		int srentry = mAOService.getAudioOutput(2);
		Logger.i(TAG, "srentry"+srentry);
		if(srentry == 0){
			
		}else if(srentry == 1){
		
		}
		else if(srentry == 2){
			rb_jiema.requestFocus();
			rb_jiema.setChecked(true);
		}
		else if(srentry == 3){
			rb_touchuan.requestFocus();
			rb_touchuan.setChecked(true);
		}else{mGroup.requestFocus();}
		
	}

}
