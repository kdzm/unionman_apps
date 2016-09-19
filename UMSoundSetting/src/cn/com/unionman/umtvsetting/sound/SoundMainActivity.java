package cn.com.unionman.umtvsetting.sound;


import cn.com.unionman.umtvsetting.sound.R;
import cn.com.unionman.umtvsetting.sound.util.Constant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
 
public class SoundMainActivity extends Activity {
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				mPicSettingDialog.dismiss();
				break;
			}

        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_main);
		
		createNetDialog();
	}
	
    private SoundSettingDialog mPicSettingDialog;
    private void createNetDialog() {
    	mPicSettingDialog = new SoundSettingDialog(SoundMainActivity.this,handler);
    	mPicSettingDialog.setCanceledOnTouchOutside(false);
    	mPicSettingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	mPicSettingDialog = null;
            	finish();
            }
        });
        Window window = mPicSettingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.height = 900;
//        lp.width = 1540;
        window.setAttributes(lp);
        mPicSettingDialog.show();
    }
}
