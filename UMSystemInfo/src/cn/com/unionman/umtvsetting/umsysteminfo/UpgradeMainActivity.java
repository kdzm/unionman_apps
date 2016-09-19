package cn.com.unionman.umtvsetting.umsysteminfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class UpgradeMainActivity extends Activity implements OnClickListener {
    private static final String TAG = "UpgradeMainActivity";
	private LinearLayout update_local_ll;
    private LinearLayout update_net_ll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_upgrade);
		update_local_ll = (LinearLayout) this.findViewById(R.id.update_local_ll);
		update_net_ll = (LinearLayout) this.findViewById(R.id.update_net_ll);
		
		update_local_ll.setOnClickListener(this);
		update_net_ll.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.update_local_ll:
			Log.i(TAG,"update_local_ll clicked");
			intent.setClassName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.LocalUpgradeActivity");
			startActivity(intent);
			break;
		case R.id.update_net_ll:
			Log.i(TAG,"update_net_ll clicked");
			//intent.setClassName("com.um.upgrade","com.um.upgrade.NetworkUpgradeMainActivity");
            //intent.setClassName("cn.com.unionman.umtvsetting.umsysteminfo","cn.com.unionman.umtvsetting.umsysteminfo.NetUpgradeActivity");
			intent.setAction("com.um.huanauth.NETUPGRADE.ACTION");
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onKeyDown+++++++");
		delay();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
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
