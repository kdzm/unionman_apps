package cn.com.unionman.umtvsetting.appmanage;



import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.com.unionman.umtvsetting.appmanage.R;
import cn.com.unionman.umtvsetting.appmanage.util.Constant;


public class AppManageSettingDialog extends Dialog{

	
	public AppManageSettingDialog(Context context, Handler handler) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(new AppManageMainSettingLayout(context,handler));
	}

}
