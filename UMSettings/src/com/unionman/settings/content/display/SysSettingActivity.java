package com.unionman.settings.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unionman.settings.R;
import com.unionman.settings.UMSettings;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiDisplayManager;
import com.hisilicon.android.tvapi.UmtvManager;
import android.provider.Settings;
import android.util.Log;

public class SysSettingActivity extends RightWindowBase {
    private LinearLayout ll_key_sound;
    private LinearLayout ll_remember_source;
    private LinearLayout ll_software_update;
    private TextView tv_key_sound;
    private TextView tv_remember_source;
    private SharedPreferences sharedata;
    private boolean firstInTag;
	private static final String TAG = "com.unionman.settings.content.display--SysSettingActivity";

	public SysSettingActivity(Context paramContext) {
		super(paramContext);
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
//		initComOlderChorice();
//		initModeOlderChorice();
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
		View mView = this.layoutInflater.inflate(R.layout.sys_setting, this);
		ll_key_sound = (LinearLayout) mView.findViewById(R.id.ll_key_sound);
		ll_remember_source = (LinearLayout) mView.findViewById(R.id.ll_remember_source);
		tv_key_sound = (TextView) mView.findViewById(R.id.tv_key_sound);
		tv_remember_source = (TextView) mView.findViewById(R.id.tv_remember_source);
		
		if(UmtvManager.getInstance().getSystemSetting().isKeypadSoundEnable()){
			tv_key_sound.setText(SysSettingActivity.this.getResources().getString(R.string.on));
		}else{
			tv_key_sound.setText(SysSettingActivity.this.getResources().getString(R.string.off));
		}
		
	    sharedata = context.getSharedPreferences("itemVal",Activity.MODE_WORLD_WRITEABLE | Activity.MODE_WORLD_READABLE| Context.MODE_MULTI_PROCESS);
		int bootSourceState = sharedata.getInt("bootSourceState", 0);
		if(bootSourceState==1){
			tv_remember_source.setText(SysSettingActivity.this.getResources().getString(R.string.on));
		}else{
			tv_remember_source.setText(SysSettingActivity.this.getResources().getString(R.string.off));
		}
		
		ll_key_sound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(UmtvManager.getInstance().getSystemSetting().isKeypadSoundEnable()){
					//off
					UmtvManager.getInstance().getSystemSetting().enableKeypadSound(false);
					Settings.System.putInt(context.getContentResolver(),Settings.System.SOUND_EFFECTS_ENABLED,  0);					
					tv_key_sound.setText(SysSettingActivity.this.getResources().getString(R.string.off));
				}else{
					//on
					UmtvManager.getInstance().getSystemSetting().enableKeypadSound(true);
					Settings.System.putInt(context.getContentResolver(),Settings.System.SOUND_EFFECTS_ENABLED,  1);	
					tv_key_sound.setText(SysSettingActivity.this.getResources().getString(R.string.on));
				}
			}
		});
		
		ll_remember_source.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int bootSourceState = sharedata.getInt("bootSourceState", 0);
				if(bootSourceState==1){
					//off
					 Editor sharedata = context.getSharedPreferences("itemVal", Activity.MODE_WORLD_WRITEABLE|Activity.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS).edit();  
				     sharedata.putInt("bootSourceState",0); 
				     sharedata.commit();  
					tv_remember_source.setText(SysSettingActivity.this.getResources().getString(R.string.off));
				}else{
					//on
					 Editor sharedata = context.getSharedPreferences("itemVal", Activity.MODE_WORLD_WRITEABLE|Activity.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS).edit();  
				     sharedata.putInt("bootSourceState",1); 
				     sharedata.commit();  					
					tv_remember_source.setText(SysSettingActivity.this.getResources().getString(R.string.on));
				}
			}
		});
		
		ll_software_update = (LinearLayout) mView.findViewById(R.id.ll_software_update);
		ll_software_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Intent intent_upgrade = new Intent();
                intent_upgrade.setClassName("cn.com.unionman.umtvsetting.umsysteminfo",
                                "cn.com.unionman.umtvsetting.umsysteminfo.UpgradeMainActivity");
                context.startActivity(intent_upgrade);
			}
		});
/*		crb_compare = (CheckRadioButton) findViewById(R.id.displayvideo_compare_id);
		//crb_compare.requestFocus();
		crb_mode = (CheckRadioButton) findViewById(R.id.displayvideo_mode_id);
		crb_compare.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CheckRadioButton paramCheckRadioButton,
					boolean paramBoolean) {
				try {
					SysSettingActivity.this.layoutManager.showLayout(ConstantList.FRAME_DISPLAY_VIDEO_COMPARE);
				
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
					SysSettingActivity.this.layoutManager.showLayout(ConstantList.FRAME_DISPLAY_VIDEO_MODE);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		initComOlderChorice();
		initModeOlderChorice();*/
		firstInTag = true;
		Button bt_none_for_focus = (Button)mView.findViewById(R.id.bt_none_for_focus);
		Button bt_title_for_focus = (Button)mView.findViewById(R.id.bt_title_for_focus);
		bt_none_for_focus.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				Log.i("yeah" , "onFocusChange-------arg1+="+arg1);
				ll_key_sound.requestFocus();
			}
		});
		bt_title_for_focus.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				Log.i("yeah" , "onFocusChange-------ll_key_sound--arg1="+arg1);
				if (firstInTag && arg1) {
					firstInTag =false;
					ll_key_sound.requestFocus();
				}
				else if (arg1 && !firstInTag) {
					ll_remember_source.requestFocus();
				}
			}
		});
	}

/*	private void initModeOlderChorice() {
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
		
	}*/

/*	private void initComOlderChorice() {
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
		
	}*/

}
