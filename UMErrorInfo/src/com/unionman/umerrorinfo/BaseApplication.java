package com.unionman.umerrorinfo;

import android.app.Application;
import android.content.Context;
/**
 * @Description 
 * @author weiyi.nong
 * @email weiyi.nong@unionman.com.cn
 * @date 2016-6-21
 */
public class BaseApplication extends Application{
	 private static Context context;
	 
	 @Override
	    public void onCreate() {
	        super.onCreate();
	        context = getApplicationContext();
	    }

	public static Context getContext(){
	    return  context;
	}
}
