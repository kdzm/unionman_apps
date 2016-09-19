package com.unionman.settingwizard.ui;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unionman.settingwizard.MyService;
import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
	private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		SharedPreferences preferences = getSharedPreferences("times", Context.MODE_WORLD_READABLE);
        int count = preferences.getInt("times", 0);
		        setContentView(R.layout.activity_main2);
		        copyVstDB();
		        btn = (Button) findViewById(R.id.btn_start);
		        btn.requestFocus();
		        btn.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		                int id = v.getId();
		                switch (id) {
		                    case R.id.btn_start:              
		                    	Intent intent = new Intent(MainActivity.this, UsermodeOrStoremodeActivity.class);
		                        startActivity(intent);
		                        finish();
		                        break;
		                    default:
		                        break;
		                }
		            }
		        });
		
		
		        Intent serviceIntent = new Intent(this, MyService.class);
		        startService(serviceIntent);
		        
		        if(!  isServiceWork(MainActivity.this,"com.android.settings.bluetooth.BlueToothAutoPairService"))	
		          {
			        Log.i(TAG, "start BlueToothAutoPairService");
			        Intent autoPairIntent = new Intent("UM_BlueToothAutoPairService");
			        startService(autoPairIntent);
			        Log.i(TAG, "end BlueToothAutoPairService"); 
		        }
//	        }
    }



    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_VOLUME_UP :
            case KeyEvent.KEYCODE_VOLUME_DOWN :
            	Log.i(TAG,"click keyCode="+keyCode);
            	break;
            default:
            	Log.i(TAG,"click keyCode="+keyCode+" return true");
            	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
       private void copyVstDB() {
               
               SharedPreferences preferences = getSharedPreferences("count",
                               MODE_WORLD_READABLE);
               int count = preferences.getInt("count", 0);
               if (0 == count) {
                       SharedPreferences.Editor editor = preferences.edit();
                       editor.putInt("count", ++count);
                       editor.commit();
                       Log.i("MainActivity -setting", "copy app.db to vst data");
                       new FileCtl().copyFiles(getApplicationContext());
              }
       }
       

      	public boolean isServiceWork(Context mContext, String serviceName) {
      		boolean isWork = false;
      		ActivityManager myAM = (ActivityManager) mContext
      				.getSystemService(Context.ACTIVITY_SERVICE);
      		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
      		if (myList.size() <= 0) {
      			return false;
      		}
      		for (int i = 0; i < myList.size(); i++) {
      			String mName = myList.get(i).service.getClassName().toString();
      			if (mName.equals(serviceName)) {
      				isWork = true;
      				break;
      			}
      		}
      		return isWork;
      	}
}
