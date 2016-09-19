package com.um.controller;

import java.util.ArrayList;
import java.util.HashMap;

public class AppList
{
	public ArrayList<HashMap<String,Object>> list;
	
	private static AppList mAppList;
	public  static AppList GetInstance()
	{
		if (mAppList == null)
		{
			
			mAppList = new AppList();
		}
		return mAppList;
		
	}
}
