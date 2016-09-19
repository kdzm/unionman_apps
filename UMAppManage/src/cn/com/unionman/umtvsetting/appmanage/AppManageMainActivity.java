package cn.com.unionman.umtvsetting.appmanage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.SystemProperties;

import cn.com.unionman.umtvsetting.appmanage.util.Constant;
 
public class AppManageMainActivity extends Activity {
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case Constant.DIALOG_DISMISS_BYTIME:
				if(appManageDialog!=null){
				   appManageDialog.dismiss();
				}
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
	
    private AppManageSettingDialog appManageDialog;
    private void createNetDialog() {
    	appManageDialog = new AppManageSettingDialog(AppManageMainActivity.this,handler);
    	appManageDialog.setCanceledOnTouchOutside(false);
    	appManageDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
//                setViewVisibility(true);
            	appManageDialog = null;
                moveTaskToBack(true);
            	finish();
            }
        });
        Window window = appManageDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        String density =SystemProperties.get("ro.sf.lcd_density");        
        if(density.equals("240")){  
        	lp.height = dip2px(this,700); //900;
            lp.width = dip2px(this,1200);//1540;
        }else{  
            lp.height = dip2px(this,600); //900;
            lp.width = dip2px(this,1050);//1540;
        }
        
        window.setAttributes(lp);
        appManageDialog.show();
    }

	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
}
