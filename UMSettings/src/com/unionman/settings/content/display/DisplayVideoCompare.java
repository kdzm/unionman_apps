package com.unionman.settings.content;

import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.hisilicon.android.HiDisplayManager;
import com.unionman.settings.tools.Logger;


public class DisplayVideoCompare extends RightWindowBase {
	
	private RadioGroup rg_compare;
	private HiDisplayManager display_manager;
	private RadioButton rb_auto;
	private RadioButton rb_compare1;
	private RadioButton rb_compare2;
	private static final String TAG = "com.unionman.settings.content.display--DisplayVideoCompare";

	public DisplayVideoCompare(Context paramContext) {
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
		this.layoutInflater.inflate(R.layout.dispaly_compare, this);
		rg_compare = (RadioGroup)findViewById(R.id.comparegroup_id);
		rb_auto = (RadioButton)findViewById(R.id.compareauto_id);
		rb_compare1 = (RadioButton)findViewById(R.id.compare1_id);
		rb_compare2 = (RadioButton)findViewById(R.id.compare2_id);
		initDefaultChoice();
		rg_compare.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				display_manager = new HiDisplayManager();
				if(checkedId == R.id.compareauto_id){
					 display_manager.setAspectRatio(0);
					  display_manager.SaveParam();
				}
				if(checkedId == R.id.compare1_id){
					 display_manager.setAspectRatio(1);
					  display_manager.SaveParam();
				}
				if(checkedId == R.id.compare2_id){
					 display_manager.setAspectRatio(2);
					  display_manager.SaveParam();
				}
			}
		});
	}

	private void initDefaultChoice() {
		Logger.i(TAG,"initDefaultChoice()--");
		if(display_manager == null){
			display_manager = new HiDisplayManager();
		}
		 int ratio = display_manager.getAspectRatio();
		if(ratio == 0){
			rb_auto.requestFocus();
			rb_auto.setChecked(true);
		}else if(ratio == 1) {
			rb_compare1.requestFocus();
			rb_compare1.setChecked(true);
		}else if(ratio == 2 ){
			rb_compare2.requestFocus();
			rb_compare2.setChecked(true);
		}else {
			rg_compare.requestFocus();
		}
		
	}

}
