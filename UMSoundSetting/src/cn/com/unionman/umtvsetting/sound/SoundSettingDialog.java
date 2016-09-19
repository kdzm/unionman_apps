package cn.com.unionman.umtvsetting.sound;



import cn.com.unionman.umtvsetting.sound.util.Constant;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;


public class SoundSettingDialog extends Dialog{
	private Context mContext;
	private SoundMainSettingLayout mSoundMainSettingLayout = null;
	private Handler soundSettingDialogHandler;
	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.SOUND_DIALOG_ITEM_SHOW:	
				mSoundMainSettingLayout.setVisibility(View.INVISIBLE);
				break;
			case Constant.SOUND_DIALOG_ITEM_DISMISS:
				mSoundMainSettingLayout.setVisibility(View.VISIBLE);
				break;
        }
      }
    };
	public SoundSettingDialog(Context context,Handler handler) {
		super(context, R.style.Dialog_Fullscreen);
		mContext = context;
		soundSettingDialogHandler = handler;
		mSoundMainSettingLayout = new SoundMainSettingLayout(context,mHandler,handler);
		setContentView(mSoundMainSettingLayout);
	}

	@Override
	protected void onStop() {
		mSoundMainSettingLayout.onDestroy();
		mSoundMainSettingLayout=null;
		super.onStop();
	}

}
