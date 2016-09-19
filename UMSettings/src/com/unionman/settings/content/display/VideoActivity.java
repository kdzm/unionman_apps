package com.unionman.settings.content;

import android.content.Context;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiDisplayManager;

public class VideoActivity extends RightWindowBase {
	private HiDisplayManager display_manager;
	private CheckRadioButton crb_compare;
	private CheckRadioButton crb_mode;
	private static final String TAG = "com.unionman.settings.content.display--VideoActivity";

	public VideoActivity(Context paramContext) {
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
		initComOlderChorice();
		initModeOlderChorice();
		//if(crb_compare.isChecked()){crb_compare.setChecked(false);}
		//if(crb_mode.isChecked()){crb_mode.setChecked(false);}

	}

	@Override
	public void setId() {
		this.levelId = 1001;

	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.display_video, this);
		crb_compare = (CheckRadioButton) findViewById(R.id.displayvideo_compare_id);
		//crb_compare.requestFocus();
		crb_mode = (CheckRadioButton) findViewById(R.id.displayvideo_mode_id);
		crb_compare.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				try {
					VideoActivity.this.layoutManager.showLayout(ConstantList.FRAME_DISPLAY_VIDEO_COMPARE);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		crb_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				
				try {
					VideoActivity.this.layoutManager.showLayout(ConstantList.FRAME_DISPLAY_VIDEO_MODE);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		initComOlderChorice();
		initModeOlderChorice();
	}

	private void initModeOlderChorice() {
		Logger.i(TAG,"initModeOlderChorice()--");
		if(display_manager == null){
			display_manager = new HiDisplayManager();
		}
		int cvrs = display_manager.getAspectCvrs();
		Logger.i(TAG,"cvrs"+cvrs);
		if(cvrs == 0){
			crb_mode.setText2(this.getResources().getString(R.string.display_video_change1));
		}
		if(cvrs == 1){
			crb_mode.setText2(this.getResources().getString(R.string.display_video_change2));
		}
		
	}

	private void initComOlderChorice() {
		Logger.i(TAG,"initComOlderChorice()--");
		if(display_manager == null){
			display_manager = new HiDisplayManager();
		}
		 int ratio = display_manager.getAspectRatio();
		if(ratio == 0){
			crb_compare.setText2(this.getResources().getString(R.string.display_video_compare_auto));

		}else if(ratio == 1) {
			crb_compare.setText2("4:3");

		}else if(ratio == 2 ){
			crb_compare.setText2("16:9");

		}
		
	}

}
