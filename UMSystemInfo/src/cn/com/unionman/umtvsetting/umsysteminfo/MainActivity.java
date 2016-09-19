package cn.com.unionman.umtvsetting.umsysteminfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private LinearLayout system_info_ll;
	private LinearLayout legal_informatics_ll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		system_info_ll  =(LinearLayout) this.findViewById(R.id.system_info_ll);
		legal_informatics_ll  =(LinearLayout) this.findViewById(R.id.legal_informatics_ll);
		
		system_info_ll.setOnClickListener(this);
		legal_informatics_ll.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		Intent intent  = new Intent ();
		switch (view.getId()) {
		case R.id.system_info_ll:
			Log.i(TAG,"system_info_ll clicked");	
			intent.setClassName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.SysInfoActivity");
			startActivity(intent);
			break;
		case R.id.legal_informatics_ll:
			Log.i(TAG,"legal_informatics_ll clicked");	
			intent.setClassName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.LicenseActivity");
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG,"onKeyDown+++++++");
		delay();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
        if (hasFocus) {
            delay();
        } else {
            finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        }		
		super.onWindowFocusChanged(hasFocus);
		
	}
    /**
 * handler of finish activity
 */
private Handler finishHandle = new Handler() {
    public void handleMessage(android.os.Message msg) {
        if (msg.what == Constant.ACTIVITY_FINISH)
            finish();
    };
};

    /**
 * set delay time to finish activity
 */
public void delay() {
	Log.i(TAG,"delay() is calling");
    finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
    Message message = new Message();
    message.what = Constant.ACTIVITY_FINISH;
    finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
}

	
}
