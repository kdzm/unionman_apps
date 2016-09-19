package com.unionman.stbconfig;

import android.content.Context;
import android.util.Log;

import com.unionman.main.PlatformInfo;

public class PlatformUtil {
	private static final String TAG = "PlatformUtil";
	
	public static PlatformInfo getPlatformInfo(Context ctx){
		Log.i(TAG, "get platform info");
		return PlatformOperation.queryPlatform(ctx);
	}
	
	public static PlatformInfo initPlatformInfo(Context ctx){
		Log.i(TAG,"init Platform info");
		PlatformInfo info;
		if((info = PlatformOperation.queryPlatform(ctx)) != null){
			Log.i(TAG,"use the inited platform info");
		}else{
			Log.i(TAG,"init a empty platform info");
			info = new PlatformInfo();
			info.setAlways(false);
			info.setCurPlatform(PlatformInfo.PI_FIBER);
			PlatformOperation.insertPlatform(ctx,info);
		}
		return info;
	}
	
	public static void updatePlatformInfo(Context ctx , PlatformInfo info){
		Log.i(TAG,"update platform info");
		PlatformOperation.updatePlatformInfo(ctx,info);
	}
}
