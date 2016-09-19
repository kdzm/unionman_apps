package cn.com.unionman.umtvsetting.umsysteminfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.um.huanauth.data.HuanClientAuth;
import android.os.SystemProperties;
import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;
import com.hisilicon.android.tvapi.UmtvManager;

public class SysInfoActivity extends Activity {
	private static final String TAG = "SysInfoActivity";
	private TextView tv_software_version_val;
	private TextView tv_build_time_val;
	private TextView tv_serial_number_val;
	private TextView tv_project_id_val;
	private TextView mDeviceType;
	private TextView mDeviceId;
	private TextView mDnum;
	private TextView mDidtoken;
	private TextView mHuanId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_info);
		tv_software_version_val = (TextView) findViewById(R.id.tv_software_version_val);
		tv_project_id_val = (TextView) findViewById(R.id.tv_project_info_id_val);
		tv_build_time_val = (TextView) findViewById(R.id.tv_build_time_val);
		tv_serial_number_val = (TextView) findViewById(R.id.tv_serial_number_val);
		mDeviceType = (TextView) findViewById(R.id.tv_devicemodel_val);
		mDeviceId = (TextView) findViewById(R.id.tv_deviceid_val);
		mDnum = (TextView) findViewById(R.id.tv_dnum_val);
		mDidtoken = (TextView) findViewById(R.id.tv_didtoken_val);
		mHuanId = (TextView) findViewById(R.id.tv_huanid_val);
		tv_software_version_val.setText(Build.UMTVSWVER);  
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date buildTime = new Date(Build.TIME);
		String buildTimeStr = formatter.format(buildTime); 
		tv_build_time_val.setText(buildTimeStr);
		String sn = CusFactoryImpl.getInstance().getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_SERIAL_NO);
		Log.v(TAG, "sn="+sn);
		tv_serial_number_val.setText(sn);
		/*CusFactory mFactory = UmtvManager.getInstance().getFactory();
		String projectId = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);*/
		String projectId = CusFactoryImpl.getInstance().getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);
		if (projectId == null){
			projectId = "";
		}
		
		tv_project_id_val.setText(projectId);
		initData();
	}
	
	private void initData(){
		String deviceType = "";
		String deviceId = "";
		String dnum = "";
		String didtoken = "";
		String huanId = "";
		
		HuanClientAuth huanClientAuth = new HuanClientAuth(this);
		deviceType = huanClientAuth.getDevicemode();
		deviceId = huanClientAuth.getDeviceid();
		dnum = huanClientAuth.getDnum();
		didtoken = huanClientAuth.getDidtoken();
		huanId = huanClientAuth.getHuanid();
		
		mDeviceType.setText(deviceType);
		mDeviceId.setText(deviceId);
		mDnum.setText(dnum);
		mDidtoken.setText(didtoken);
		mHuanId.setText(huanId);
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
