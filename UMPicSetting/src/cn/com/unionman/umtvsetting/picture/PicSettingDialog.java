package cn.com.unionman.umtvsetting.picture;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import cn.com.unionman.umtvsetting.picture.R;
import cn.com.unionman.umtvsetting.picture.util.Constant;

public class PicSettingDialog extends Dialog{
	private PicMainSettingLayout mPicMainSettingLayout;
	private Context mContext;
	private Handler picSettingDialogHandler;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.PIC_DIALOG_ITEM_SHOW:	
				mPicMainSettingLayout.setVisibility(View.INVISIBLE);
				break;
			case Constant.PIC_DIALOG_ITEM_DISMISS:
				mPicMainSettingLayout.setVisibility(View.VISIBLE);
				break;
			}

        }
    };

	public PicSettingDialog(Context context , Handler handler) {
		this(context, handler, PicMainSettingLayout.TVPROG);
	}

	public PicSettingDialog(Context context , Handler handler, int dvbMode) {
		super(context, R.style.Dialog_Fullscreen);
		mContext = context;
		picSettingDialogHandler = handler;
		mPicMainSettingLayout  = new PicMainSettingLayout(context, mHandler,handler, dvbMode);
		setContentView(mPicMainSettingLayout);
	}
	@Override
	protected void onStop() {
		mPicMainSettingLayout.onDestroy();
		mPicMainSettingLayout = null;
		super.onStop();
	}

}
