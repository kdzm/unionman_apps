package com.unionman.settings.content;

import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;

import com.hisilicon.android.HiDisplayManager;

public class DisplayVideoMode extends RightWindowBase {
	private RadioButton rb_mode1;
	private RadioButton rb_mode2;
	private RadioGroup rg_mode;
	private HiDisplayManager displayManager;
	private static final String TAG = "com.unionman.settings.content.display--DisplayVideoMode";
	public DisplayVideoMode(Context paramContext) {
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
		this.layoutInflater.inflate(R.layout.display_mode, this);
		rb_mode1 = (RadioButton)findViewById(R.id.mode1_id);
		rb_mode2 = (RadioButton)findViewById(R.id.mode2_id);
		rg_mode = (RadioGroup)findViewById(R.id.modegroup_id);
		initDefaultMode();
		rg_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(displayManager == null){
					displayManager = new HiDisplayManager();
				}
				
				if(checkedId == R.id.mode1_id){
					displayManager.setAspectCvrs(0);
					displayManager.SaveParam();
				}
				if(checkedId == R.id.mode2_id){
					displayManager.setAspectCvrs(1);
					displayManager.SaveParam();
				}
				
			}
		});
	}

	private void initDefaultMode() {
		Logger.i(TAG,"initDefaultMode()--");
		displayManager = new HiDisplayManager();
		int cvrs = displayManager.getAspectCvrs();
		Logger.i(TAG,"cvrs"+cvrs);
		if(cvrs == 0){
			rb_mode1.requestFocus();
			rb_mode1.setChecked(true);
		}
		if(cvrs == 1){
			rb_mode2.requestFocus();
			rb_mode2.setChecked(true);
		}
		
	}

}
