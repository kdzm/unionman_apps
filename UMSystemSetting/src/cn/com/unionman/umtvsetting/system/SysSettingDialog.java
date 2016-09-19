package cn.com.unionman.umtvsetting.system;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

public class SysSettingDialog extends Dialog{

	private SysSettingMainSettingLayout mSysSettingMainSettingLayout;
	public SysSettingDialog(Context context, Handler handler) {
		this(context, handler, null);
	}

    public SysSettingDialog(Context context, Handler handler, String action) {
        super(context, R.style.Dialog_Fullscreen);
        mSysSettingMainSettingLayout   = new SysSettingMainSettingLayout(context,handler, action);
        setContentView(mSysSettingMainSettingLayout);
    }
    
    
    public void unregester(){
    	mSysSettingMainSettingLayout.unregesterevent();
    }

}
