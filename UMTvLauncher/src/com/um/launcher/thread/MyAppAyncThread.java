package com.um.launcher.thread;

import java.util.List;

import com.um.launcher.MyApplication;
import com.um.launcher.util.Util;
import android.content.Context;
import android.content.pm.ResolveInfo;

public class MyAppAyncThread extends Thread{
	
	private Context mContext = null;
	private int mType = 0;
	
	public MyAppAyncThread(Context context) {
		mContext = context;
	}
	
	public void myStart(int type){
		mType = type;
		start();
		
	}
	
	@Override
	public void run() {
		super.run();
        MyApplication application = (MyApplication) mContext
                .getApplicationContext();
        
		List<ResolveInfo> resolveInfos = null;
		if (interrupted()){
			application.setResolveInfos(resolveInfos);
			return;
		}
		
		switch (mType) {
            case Util.ALL_APP:
                resolveInfos = Util.getAllApps(mContext);
                break;
            default:
                break;
        }
		
		if (interrupted()){
			application.setResolveInfos(null);
			return;
		}
		
        application.setResolveInfos(resolveInfos);
	}
	
	
}
