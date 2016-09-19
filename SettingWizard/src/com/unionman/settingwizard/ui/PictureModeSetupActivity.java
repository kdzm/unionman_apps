package com.unionman.settingwizard.ui;


import com.unionman.settingwizard.R;
import com.unionman.settingwizard.ui.ScreenSetupActivity.MyClickListener;
import com.unionman.settingwizard.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;

public class PictureModeSetupActivity extends Activity {
    private static final String TAG = "PictureModeSetupActivity";
	private RelativeLayout rl_picmode_standard;
    private RelativeLayout rl_picmode_dynamic;
    private RelativeLayout rl_picmode_softness;
    private RelativeLayout rl_picmode_user;   
    private ImageView im_standard;
    private ImageView im_dynamic;
    private ImageView im_softness; 
    private ImageView im_user;     
    public static int picture_mode[][] = {
        { EnumPictureMode.PICMODE_STANDARD,
                R.string.picmode_standard_string },
        { EnumPictureMode.PICMODE_DYNAMIC, R.string.picmode_dynamic_string },
        { EnumPictureMode.PICMODE_SOFTNESS,
                R.string.picmode_softness_string },
        { EnumPictureMode.PICMODE_USER, R.string.picmode_user_string } };
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_mode_setup);
		
		rl_picmode_standard = (RelativeLayout) findViewById(R.id.rl_picmode_standard);
		rl_picmode_dynamic =  (RelativeLayout) findViewById(R.id.rl_picmode_dynamic);
		rl_picmode_softness = (RelativeLayout) findViewById(R.id.rl_picmode_softness);
		rl_picmode_user = (RelativeLayout) findViewById(R.id.rl_picmode_user);		
		rl_picmode_standard.setOnClickListener(new MyClickListener());
		rl_picmode_dynamic.setOnClickListener(new MyClickListener());
		rl_picmode_softness.setOnClickListener(new MyClickListener());	
		rl_picmode_user.setOnClickListener(new MyClickListener());	
		
		im_standard =(ImageView) findViewById(R.id.im_standard);
		im_dynamic =(ImageView) findViewById(R.id.im_dynamic);
		im_softness =(ImageView) findViewById(R.id.im_softness);
		im_user =(ImageView) findViewById(R.id.im_user);		
		
	       Button NextStepBtn = (Button) findViewById(R.id.btn_next_step);
	        Button LastStepBtn = (Button) findViewById(R.id.btn_last_step);
	        NextStepBtn.requestFocus();
	        NextStepBtn.setOnClickListener(new MyClickListener());
	        LastStepBtn.setOnClickListener(new MyClickListener());
	        
	        int mode = UmtvManager.getInstance().getPicture().getPictureMode(); //图像模式
	        Log.i(TAG,"mode="+mode);
	        Log.i(TAG,"PICMODE_STANDARD="+EnumPictureMode.PICMODE_STANDARD); 
	        Log.i(TAG,"PICMODE_DYNAMIC="+EnumPictureMode.PICMODE_DYNAMIC);
	        Log.i(TAG,"PICMODE_SOFTNESS="+EnumPictureMode.PICMODE_SOFTNESS); 	
	        Log.i(TAG,"PICMODE_USER="+EnumPictureMode.PICMODE_USER); 
	        setImageBackgroundCheck(mode);

	}
    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    intent = new Intent(PictureModeSetupActivity.this, NetworkSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(PictureModeSetupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.rl_picmode_standard:
                	setImageBackgroundCheck(EnumPictureMode.PICMODE_STANDARD);
                	UmtvManager.getInstance().getPicture().setPictureMode(EnumPictureMode.PICMODE_STANDARD);
                    break;
                case R.id.rl_picmode_dynamic:
                	setImageBackgroundCheck(EnumPictureMode.PICMODE_DYNAMIC);
                	UmtvManager.getInstance().getPicture().setPictureMode(EnumPictureMode.PICMODE_DYNAMIC);
                    break;     
                case R.id.rl_picmode_softness:
                	setImageBackgroundCheck(EnumPictureMode.PICMODE_SOFTNESS);
                	UmtvManager.getInstance().getPicture().setPictureMode(EnumPictureMode.PICMODE_SOFTNESS);
                    break;  
                case R.id.rl_picmode_user:
                	setImageBackgroundCheck(EnumPictureMode.PICMODE_USER);
                	UmtvManager.getInstance().getPicture().setPictureMode(EnumPictureMode.PICMODE_USER);
                    break;          
            }
        }
    }
    /**
     * 根据mode值设置图片的背景是否显示
     * @param mode
     */
    private void setImageBackgroundCheck(int mode){
	       switch (mode) {
			case EnumPictureMode.PICMODE_STANDARD:
				im_standard.setBackgroundResource(R.drawable.setting_checked_focused);
				im_dynamic.setBackgroundResource(R.color.transparent);
				im_softness.setBackgroundResource(R.color.transparent);
				im_user.setBackgroundResource(R.color.transparent);
				break;
			case EnumPictureMode.PICMODE_DYNAMIC:
				im_standard.setBackgroundResource(R.color.transparent);
				im_dynamic.setBackgroundResource(R.drawable.setting_checked_focused);
				im_softness.setBackgroundResource(R.color.transparent);
				im_user.setBackgroundResource(R.color.transparent);				
				break;
			case EnumPictureMode.PICMODE_SOFTNESS:
				im_standard.setBackgroundResource(R.color.transparent);
				im_dynamic.setBackgroundResource(R.color.transparent);
				im_softness.setBackgroundResource(R.drawable.setting_checked_focused);
				im_user.setBackgroundResource(R.color.transparent);				
				break;
			case EnumPictureMode.PICMODE_USER:
				im_standard.setBackgroundResource(R.color.transparent);
				im_dynamic.setBackgroundResource(R.color.transparent);
				im_softness.setBackgroundResource(R.color.transparent);
				im_user.setBackgroundResource(R.drawable.setting_checked_focused);				
				break;	
		}
    }
    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {       
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
