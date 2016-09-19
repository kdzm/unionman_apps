package com.unionman.settings.content;

import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.content.display.SystemSettingInterface;
import com.unionman.settings.tools.Logger;


public class SetTimeToSleep extends RightWindowBase {
	
	private RadioGroup rg_pwr;
	private RadioButton rbShutDown;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private RadioButton rb4;
	private static final String TAG = "com.unionman.settings.content.display--SetTimeToSleep";

	public static String currentTime;
	private int level;
	public static SystemSettingInterface mSystemSettingInterface;
	
	
	public SetTimeToSleep(Context paramContext) {
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
		this.layoutInflater.inflate(R.layout.dispaly_pwr_saving_layout, this);
		rg_pwr = (RadioGroup)findViewById(R.id.comparegroup_id);
		rbShutDown = (RadioButton)findViewById(R.id.rbShutDown);
		rb1 = (RadioButton)findViewById(R.id.rb1h);
		rb2 = (RadioButton)findViewById(R.id.rb2h);
		rb3 = (RadioButton)findViewById(R.id.rb3h);
		rb4 = (RadioButton)findViewById(R.id.rb4h);
		initDefaultChoice();
		rg_pwr.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.rbShutDown){
					mSystemSettingInterface.setWaitting(0);
				}else
				if(checkedId == R.id.rb1h){
					mSystemSettingInterface.setWaitting(1);
				}else
				if(checkedId == R.id.rb2h){
					mSystemSettingInterface.setWaitting(2);
				}else
				if(checkedId == R.id.rb3h){
					mSystemSettingInterface.setWaitting(3);
				}else
				if(checkedId == R.id.rb4h){
					mSystemSettingInterface.setWaitting(4);
				}
				
			}
		});
	}

	private void initDefaultChoice() {
		Logger.i(TAG, "initDefaultChoice()--");
		switch (mSystemSettingInterface.getWaitting()) {
		case 0:
			rbShutDown.requestFocus();
			rbShutDown.setChecked(true);
			break;
		case 1:
			rb1.requestFocus();
			rb1.setChecked(true);
			break;
		case 2:
			rb2.requestFocus();
			rb2.setChecked(true);
			break;
		case 3:
			rb3.requestFocus();
			rb3.setChecked(true);
			break;
		case 4:
			rb4.requestFocus();
			rb4.setChecked(true);
			break;
		}
	
	}

}
