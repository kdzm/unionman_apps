package cn.com.unionman.umtvsetting.umsettingmenu;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;

public class MainDialog extends Dialog {

	public MainDialog(Context context,Handler handler) {
		super(context, R.style.Dialog_Fullscreen);
		// TODO Auto-generated constructor stub
		setContentView(new MainLayout(context,handler));
	}

}
