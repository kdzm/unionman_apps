package cn.com.unionman.umtvsetting.powersave;



import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.com.unionman.umtvsetting.powersave.R;
import cn.com.unionman.umtvsetting.powersave.util.Constant;



public class PowerSettingDialog extends Dialog{
	private PowerMainSettingLayout powerMainSettingLayout;

	public PowerSettingDialog(Context context, Handler handler) {
		super(context, R.style.Dialog_Fullscreen);
		powerMainSettingLayout =new PowerMainSettingLayout(context,handler);
		setContentView(powerMainSettingLayout);
	}

}
