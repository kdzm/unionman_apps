package com.unionman.settings.tools;

import android.util.Log;
import java.lang.Exception;
public class UMDebug
{
	static public void umdebug_trace()
	{
		try
		{
			int i= 1;
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
		}
	}
	static public void d(String tag, String str)
	{
		Log.d(tag, str);
	}
	static public void w(String tag, String str)
	{
		Log.w(tag, str);
	}
	static public void e(String tag, String str)
	{
		Log.e(tag, str);
	}
}

