package com.umexplorer.activity;

import java.util.ArrayList;
import java.util.List;

import java.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.os.RemoteException;
import android.util.Xml;
import android.widget.Toast;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;


import com.umexplorer.interfaces.SourceManagerInterface;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.net.wifi.WifiManager;
import com.umexplorer.R;
import com.umexplorer.R.color;
import com.umexplorer.common.CommonActivity;
import com.umexplorer.common.FileUtil;

/**
 * Page container
 * CNcomment:页面容器
 */
public class SelectFileType extends Activity implements android.view.View.OnClickListener, OnFocusChangeListener {
    
    public View[] imgView;
    public View[] imgFocusView;
	public TextView[] mTextView;

    private IntentFilter mIntenFilter = null;
    private static final String TAG = "SelectFileType";
    private String[] filterMethod;
    private int clickCount = 0;
    private Object mSwitchLock;
    //jly
    /**
     * Page display
     * CNcomment:页面显示
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_new);       
        mSwitchLock = new Object();
		
        filterMethod = getResources().getStringArray(R.array.filter_method);
        
        imgView = new View[] {
                findViewById(R.id.Music_item_icon),
                findViewById(R.id.Video_item_icon),
                findViewById(R.id.Picture_item_icon),
                findViewById(R.id.ALL_item_icon)
        };
        
        imgFocusView = new View[] {
                findViewById(R.id.view_Music_focus),
                findViewById(R.id.view_video_focus),
                findViewById(R.id.view_Picture_focus),
                findViewById(R.id.view_all_focus)
        };
        mTextView =  new TextView[]{
              (TextView)  findViewById(R.id.app_txt_Music),
              (TextView)  findViewById(R.id.app_txt_Video),
              (TextView)  findViewById(R.id.app_txt2),
              (TextView)  findViewById(R.id.app_all_txt)	
        };
        for (int i = 0; i < imgView.length; i++) {
        	Log.i("hehe", "focusNum iiii :"+i);
            imgView[i].setOnClickListener(this);
            
           imgFocusView[i].getBackground().setAlpha(0);
     
           imgView[i].setOnFocusChangeListener(this);
        }
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	switch (keyCode) {
    		case KeyEvent.KEYCODE_BACK:
    			clickCount++;
    			if (clickCount == 1)
    			{
    				FileUtil.showToast(SelectFileType.this, getString(R.string.quit_app));
    				exitHandler.sendEmptyMessageDelayed(0, 2000);
    				return true;
    			}
    			else if (clickCount == 2)
    			{
    				clickCount = 0;
					
					if (FileUtil.getToast() != null) {
						FileUtil.getToast().cancel();
					}
					finish();
    			}
    			break;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private Handler exitHandler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		clickCount = 0;
    	};
    };

	@Override
    protected void onResume() {
        Log.i(TAG, "current num --->");
        super.onResume();
		KillBlackListApps();
		KillNonsystemApps();
		doSelectMediaSource();
    }
	
	private void KillBlackListApps() {
	  File file = new File("/vendor/etc/blacklist.xml");
	  Log.d(TAG, "KillBlackListApps Enter ");
	  if(file.exists() && file.isFile()){
		  try {
			  
			  InputStream xml = new FileInputStream(file);
			  ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
			  XmlPullParser pullParser = Xml.newPullParser();
			  pullParser.setInput(xml, "UTF-8"); //为Pull解释器设置要解析的XML数据		  
			  int event = pullParser.getEventType();
			  
			  while (event != XmlPullParser.END_DOCUMENT) {
				  
				  switch (event) {
				  
				  case XmlPullParser.START_DOCUMENT:
					  break;	
				  case XmlPullParser.START_TAG:   
					  String name = pullParser.getName();
					  if ("listname".equals(name)) {
						  String value = pullParser.getAttributeValue(0);
						  Log.d(TAG, "KillBlackListApps value is "+value);
						  am.forceStopPackage(value);
					  }
					  break;
					  
				  case XmlPullParser.END_TAG:
					  break;
				  }
				  
				  event = pullParser.next();
				  }
			  } catch (FileNotFoundException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: blacklist.xml not found.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  } catch (XmlPullParserException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: parse blacklist.xml failed.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  } catch (IOException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: read blacklist.xml failed.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  }
		  }
	
	  }
	
		
	  private boolean isSystemApplication(PackageManager packageManager, String packageName) {
		  if (packageManager == null || packageName == null || packageName.length() == 0) {
			  return false;
		  }
	
		  try {
			  ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
			  return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
		  } catch (NameNotFoundException e) {
			  e.printStackTrace();
		  }
		  return false;
	  }
	  
	private void KillNonsystemApps(){
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (int i=0; i<processes.size(); i++){
			ActivityManager.RunningAppProcessInfo infor = processes.get(i);
			String[] pkgNameList =	infor.pkgList;; // 鑾峰緱杩愯鍦ㄨ杩涚▼閲岀殑鎵€鏈夊簲鐢ㄧ▼搴忓寘 
			// 杈撳嚭鎵€鏈夊簲鐢ㄧ▼搴忕殑鍖呭悕	
			for (int j = 0; j < pkgNameList.length; j++) {	
				String pkgName = pkgNameList[j];  
				Log.i(TAG, "packageName " + pkgName + " at index " + j);			
				if(!isSystemApplication(this.getPackageManager(), pkgName)){
					Log.i(TAG, "forceStopPackage " + pkgName + " at index " + j);		
					am.forceStopPackage(pkgName);
				} 
			}
		}
	}
	
	private void doSelectMediaSource(){
    	int TvSourceIdx = EnumSourceIndex.SOURCE_ATV;
	    int mCurrentSourceIdx = EnumSourceIndex.SOURCE_ATV;
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doDeleteMediaSource is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx);
    		if(TvSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
				SourceManagerInterface.deselectSource(TvSourceIdx, true);
			}
			
        	if (mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
	    		Log.d(TAG, "doDelectTVSource");
                SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
            }
    	}
	}
	
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus) {		
				v.bringToFront();	
				if(v.equals(imgView[0])){
                    mTextView[0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					imgFocusView[0].getBackground().setAlpha(255);
				}else if(v.equals(imgView[1])){
                    mTextView[1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					imgFocusView[1].getBackground().setAlpha(255);
				}else if(v.equals(imgView[2])){
                    mTextView[2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					imgFocusView[2].getBackground().setAlpha(255);
				}else if(v.equals(imgView[3])){
                    mTextView[3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
					imgFocusView[3].getBackground().setAlpha(255);
				}
				v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(0).start();
	        } else {
	        	if(v.equals(imgView[0])){
	        		mTextView[0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
					imgFocusView[0].getBackground().setAlpha(0);
				}else if(v.equals(imgView[1])){
	        		mTextView[1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
					imgFocusView[1].getBackground().setAlpha(0);
				}else if(v.equals(imgView[2])){
	        		mTextView[2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
					imgFocusView[2].getBackground().setAlpha(0);
				}else if(v.equals(imgView[3])){
	        		mTextView[3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
					imgFocusView[3].getBackground().setAlpha(0);
				}
	        	v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0).start();
	        }
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(SelectFileType.this, MainExplorerActivity.class);
		if (v.equals(imgView[0]))
		{
			intent.putExtra("FileType", filterMethod[2]);
		}
		else if (v.equals(imgView[1]))
		{
			intent.putExtra("FileType", filterMethod[3]);
		}
		else if (v.equals(imgView[2]))
		{
			intent.putExtra("FileType", filterMethod[1]);
		}
		else if (v.equals(imgView[3]))
		{
			intent.putExtra("FileType", filterMethod[0]);
		}
		
		startActivity(intent);
	}
}
