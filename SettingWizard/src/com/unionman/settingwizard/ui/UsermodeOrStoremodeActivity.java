package com.unionman.settingwizard.ui;


import com.unionman.settingwizard.R;
import com.unionman.settingwizard.ui.ScreenSetupActivity.MyClickListener;
import com.unionman.settingwizard.util.SystemSettingInterface;
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

public class UsermodeOrStoremodeActivity extends Activity {
    private static final String TAG = "UsermodeOrStoremodeActivity";
	private RelativeLayout rl_usermode;
    private RelativeLayout rl_storemode;
 
    private ImageView im_usermode;
    private ImageView im_storemode;     
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.usermode_or_storemode_setup);
		
		rl_usermode = (RelativeLayout) findViewById(R.id.rl_usermode);
		rl_storemode =  (RelativeLayout) findViewById(R.id.rl_storemode);	
		rl_usermode.setOnClickListener(new MyClickListener());
		rl_storemode.setOnClickListener(new MyClickListener());
		
		im_usermode =(ImageView) findViewById(R.id.im_usermode);
		im_storemode =(ImageView) findViewById(R.id.im_storemode);		
		
	       Button NextStepBtn = (Button) findViewById(R.id.btn_next_step);
	        Button LastStepBtn = (Button) findViewById(R.id.btn_last_step);
	        NextStepBtn.requestFocus();
	        NextStepBtn.setOnClickListener(new MyClickListener());
	        LastStepBtn.setOnClickListener(new MyClickListener());
	        
	        int storeMode = (SystemSettingInterface.getStoreMode())?1:0; //卖场模式
	        Log.i(TAG,"storeMode="+storeMode);
	        setImageBackgroundCheck(storeMode);

	}
    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    intent = new Intent(UsermodeOrStoremodeActivity.this, NetworkSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                	intent = new Intent(UsermodeOrStoremodeActivity.this, MainActivity.class);
                	startActivity(intent);
                    finish();
                    break;
                case R.id.rl_usermode:
                	setImageBackgroundCheck(0);
		             SystemSettingInterface.setStoreMode(0);
                    break;
                case R.id.rl_storemode:
                	setImageBackgroundCheck(1);
                	SystemSettingInterface.setStoreMode(1);
                    break;            
            }
        }
    }
    /**
     * 根据mode值设置图片的背景是否显示
     * @param storeMode
     */
    private void setImageBackgroundCheck(int storeMode){
	       switch (storeMode) {
			case 0:   //off
				im_usermode.setBackgroundResource(R.drawable.setting_checked_focused);
				im_storemode.setBackgroundResource(R.color.transparent);
				break;
			case 1:  //on
				im_usermode.setBackgroundResource(R.color.transparent);
				im_storemode.setBackgroundResource(R.drawable.setting_checked_focused);			
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
