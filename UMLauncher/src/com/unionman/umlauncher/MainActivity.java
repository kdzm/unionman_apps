package com.unionman.umlauncher;

import android.view.KeyEvent;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;

/**
 * 
 * Create by hjan on 2015-07-06
 */
public class MainActivity extends Activity {
    private final String TAG = "Eniso---";
    private RelativeLayout linearLayout;
    private Handler handler;
    private boolean firstCheck = true;
    private Runnable netCheck;
    private String mAction = ""; 
    private String pkgName = "";
    private String serviceName = "";
    private String mCpeAction = "com.um.cpelistener";
    private TextView tvCheck;
    private int checkCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
        setContentView(R.layout.activity_main);
        handler = new Handler();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_netconfig: //
                        Log.d(TAG, "bt_netconfig");
                        startPackage("com.unionman.settings");
                        break;
                    case R.id.bt_recheck:
                        Log.d(TAG, "bt_recheck"); //
                        boolean connect = NetWorkUtils.isNetworkAvailable(MainActivity.this);
                        if (connect) {
                            //linearLayout.setVisibility(View.INVISIBLE);
                            startAccessApp();
                        }else{
                            Toast.makeText(MainActivity.this,R.string.no_connection_tile,Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };

        findViewById(R.id.bt_netconfig).setOnClickListener(onClickListener);
        findViewById(R.id.bt_recheck).setOnClickListener(onClickListener);
        findViewById(R.id.bt_recheck).setFocusable(true);
        findViewById(R.id.bt_recheck).setFocusableInTouchMode(true);
        findViewById(R.id.bt_recheck).requestFocus();
        findViewById(R.id.bt_recheck).requestFocusFromTouch();
        linearLayout = (RelativeLayout) findViewById(R.id.content);
        tvCheck=(TextView)findViewById(R.id.tv_check);
        netCheck = new Runnable() {
            @Override
            public void run() {
                boolean connect = NetWorkUtils.isNetworkAvailable(MainActivity.this);
                if (connect) {
                	checkCount=0;
                	try{
                		Log.i(TAG, "wait 2 second");
                		Thread.sleep(2000);
                	}catch(Exception e){
                		e.printStackTrace();
                	}
                    String defStr = "Hi3798MV100";
                    String sn = SystemProperties.get("ro.serialno", "0");
                    String stbType = SystemProperties.get("ro.product.device", "UNT400B");
                    if(defStr.equals("Hi3798MV100")){
                        stbType = "UNT400B";
                    }
                    Log.d(TAG, "stbType = " + stbType);
                    String a[] = sn.split(stbType);
                    String d = null;
                    if(a != null){
                        Log.d(TAG, "a.length = " + a.length);
                        for(int k = 0; k < a.length; k++){
                            Log.d(TAG, "a[" + k + "] = " + a[k]);
                        }
                        if(a.length >=2 ){
                            if(a[1].length() >= 3){
                                d = a[1].substring(1, 2);
                            }
                        }
                    }
                    if(d != null){
                        if(d.equals("0")){

                            Log.d(TAG, "action.GET.TOKEN.HW");
                            mAction = "action.GET.TOKEN.HW";
                            pkgName = "com.um.auth";
                            serviceName = "com.um.auth.MainService";
                        }else if(d.equals("1")){

                            Log.d(TAG, "action.GET.TOKEN.ZTE");
                            mAction = "action.GET.TOKEN.ZTE";
                            pkgName = "com.unionman.gettoken";
                            serviceName = "com.unionman.gettoken.GetTokenService";
                        }else {
                            mAction = "action.what.the";
                            pkgName = "com.um.auth";
                            serviceName = "com.um.auth.MainService";
                        }
                    }else{
                        mAction = "action.what.the";
                        pkgName = "com.um.auth";
                        serviceName = "com.um.auth.MainService";
                    }

                    startAccessApp();
                } else {
	                checkCount+=1;
	                if(checkCount>=30){
	                	tvCheck.setVisibility(View.INVISIBLE);
		                linearLayout.setVisibility(View.VISIBLE);
	                }
			    	handler.postDelayed(netCheck, 1000);
                }
                firstCheck = false;
            }
        };

        handler.post(netCheck);

    }

    private void startPackage(String pkg) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkg);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

 @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!firstCheck) {
//            RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
//            WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
//            layout.setBackground(wallpaperManager.getDrawable());
            boolean connect = NetWorkUtils.isNetworkAvailable(MainActivity.this);
            if (connect) {
               //linearLayout.setVisibility(View.INVISIBLE);
               startAccessApp();
            } else {
               tvCheck.setVisibility(View.INVISIBLE);
               linearLayout.setVisibility(View.VISIBLE);
            }
            
        }
        Intent intent = new Intent(); 
        ComponentName componentName2 = new ComponentName("com.um.cpelistener", "com.um.cpelistener.ListenService");
        intent.setComponent(componentName2);
        startService(intent);
    }

    private String getLauncherPackageName() {
        Intent localIntent = new Intent(Intent.ACTION_MAIN);
        localIntent.addCategory(Intent.CATEGORY_HOME);
        return getPackageManager().resolveActivity(localIntent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
    }
    
    private void startAccessApp(){
    	Intent intent = new Intent(); 
        ComponentName componentName2 = new ComponentName(pkgName, serviceName);
        intent.setComponent(componentName2);
        startService(intent);
        finish();
    }
    

}
