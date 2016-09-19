package cn.com.unionman.umtvsetting.umsysteminfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class LicenseActivity extends Activity {
	private static final String TAG = "LicenseActivity";
	private TextView license_txt;
	private ScrollView  scrollView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license_info);
		license_txt = (TextView) findViewById(R.id.license_txt);
        InputStream inputStream = null;
		try {
			inputStream = getAssets().open("license.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (inputStream!=null) {
	        String string = getString(inputStream);  
	        if (string!=null)
	        	license_txt.setText(string); 
	        
	        try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        scrollView1.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) { 
					Log.i(TAG,"scrollView1 calling delay()");
					delay();
				}
				return false;
			}
		});
	}

 
		private String getString(InputStream inputStream) {
			InputStreamReader inputStreamReader = null;   
			try {     
				inputStreamReader = new InputStreamReader(inputStream, "gbk");   
				} catch (UnsupportedEncodingException e1) { 
					e1.printStackTrace();   }    
			BufferedReader reader = new BufferedReader(inputStreamReader);   
			StringBuffer sb = new StringBuffer("");   
			String line;   
			try {
				while ((line = reader.readLine()) != null) {    
					sb.append(line);   
					sb.append("\n");    
					}    
				} catch (IOException e) {    
					e.printStackTrace();   
					}
			if (inputStreamReader != null){
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return sb.toString();  
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
