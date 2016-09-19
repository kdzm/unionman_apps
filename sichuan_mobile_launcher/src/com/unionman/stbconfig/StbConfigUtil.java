package com.unionman.stbconfig;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;

import com.unionman.main.StbConfig;

public class StbConfigUtil {
	private static final String TAG = "StbConfigUtil";
	private static final String PROP_MAC = "ro.mac";
	private static final String PROP_STBID = "ro.serialno";
	private static final String PROP_SOFTVER = "ro.build.version.incremental";
	private static final String PROP_STBTYPE = "ro.product.name";

	public static StbConfig getStbConfig(Context ctx) {
		Log.i(TAG, "get stb config");
		return StbConfigOperation.queryStbConfig(ctx);
	}

	public static StbConfig initStbConfig(Context ctx) {
		Log.i(TAG, "init stb config");
		if (StbConfigOperation.queryStbConfig(ctx,
				StbconfigMetaData.SummaryTable.STBID) != null) {
			Log.i(TAG, "retrive inited stb config");
			return getStbConfig(ctx);
		}
		StbConfig stbconfig = new StbConfig();
		stbconfig.setMAC(SystemProperties.get(PROP_MAC));
		stbconfig.setSTBID(SystemProperties.get(PROP_STBID));
		stbconfig.setSoftwareVersion(SystemProperties.get(PROP_SOFTVER));
		stbconfig.setSTBType(SystemProperties.get(PROP_STBTYPE));
		StbConfigOperation.insertStbConfig(ctx,stbconfig);
		Log.i(TAG,"insert stbconfig :" + stbconfig.toString());
		return stbconfig;
	}
	
	public static void updateStbConfig(Context ctx,StbConfig stbconfig){
		Log.i(TAG,"update stb config:" + stbconfig.toString());
		StbConfigOperation.updateStbCofnig(ctx,stbconfig);
	}
}
