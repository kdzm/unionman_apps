package com.um.tv.menu;

import android.app.Application;
import android.content.Context;

public class UMFactoryMenu extends Application {
	
	private static Context mContext = UMFactoryMenu.getContext();
	public UMFactoryMenu(){
		mContext = this;
	}
	
	public static Context getContext(){
		return mContext;
	}
	
}
