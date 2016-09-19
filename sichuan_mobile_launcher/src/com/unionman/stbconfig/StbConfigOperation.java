package com.unionman.stbconfig;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.unionman.main.StbConfig;

public class StbConfigOperation {
	private static final String TAG = "StbConfigOperation";

	public static StbConfig queryStbConfig(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Cursor cursor = cr.query(StbconfigMetaData.SummaryTable.CONTENT_URI,
				null, null, null, null);
		if (cursor == null) {
			Log.i(TAG, "query stbconfig null");
			return null;
		}
		StbConfig stbconfig = new StbConfig();
		while (cursor.moveToNext()) {
			String[] arrStr = cursor.getColumnNames();
			for (int i = 0; i < arrStr.length; i++) {
				String str = arrStr[i];
				if ("AuthURL".equalsIgnoreCase(str)) {
					stbconfig.setAuthURL(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("STBID".equalsIgnoreCase(str)) {
					stbconfig.setSTBID(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("IP".equalsIgnoreCase(str)) {
					stbconfig
							.setIP(cursor.getString(cursor.getColumnIndex(str)));
				} else if ("MAC".equalsIgnoreCase(str)) {
					stbconfig.setMAC(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("UserID".equalsIgnoreCase(str)) {
					stbconfig.setUserID(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("UserPassword".equalsIgnoreCase(str)) {
					stbconfig.setUserPassword(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("UserToken".equalsIgnoreCase(str)) {
					stbconfig.setUserToken(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("LastchannelNum".equalsIgnoreCase(str)) {
					stbconfig.setLastchannelNum(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("STBType".equalsIgnoreCase(str)) {
					stbconfig.setSTBType(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("SoftwareVersion".equalsIgnoreCase(str)) {
					stbconfig.setSoftwareVersion(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("Reserved".equalsIgnoreCase(str)) {
					stbconfig.setReserved(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("NetworkAcount".equalsIgnoreCase(str)) {
					stbconfig.setNetworkAcount(cursor.getString(cursor
							.getColumnIndex(str)));
				} else if ("NetworkPassword".equalsIgnoreCase(str)) {
					stbconfig.setNetworkPassword(cursor.getString(cursor
							.getColumnIndex(str)));
				}

			}
		}
		Log.i(TAG, "query stbconfig :" + stbconfig.toString());
		cursor.close();
		return stbconfig;
	}

	public static String queryStbConfig(Context ctx, String param) {
		Log.i(TAG, "query stb config with " + param);
		ContentResolver cr = ctx.getContentResolver();
		Log.i(TAG,"content uri:" + StbconfigMetaData.SummaryTable.CONTENT_URI);
		Cursor cursor = cr.query(StbconfigMetaData.SummaryTable.CONTENT_URI,
				null, null, null, null);
		if(cursor == null){
			Log.i(TAG,"query stb config null with" + param);
			return null;
		}
		String[] arrString = cursor.getColumnNames();
		while(cursor.moveToNext()){
			for(String str : arrString){
				if(str.equalsIgnoreCase(param)){
					String retStr =  cursor.getString(cursor.getColumnIndex(param));
					cursor.close();
					return retStr;
				}
			}
		}
		cursor.close();
		return null;
	}

	public static boolean insertStbConfig(Context ctx, StbConfig stbconfig) {
		Log.i(TAG,"insert stbconfig :" + stbconfig.toString());
		ContentValues values = new ContentValues();
		values.put("AuthURL", stbconfig.getAuthURL());
		values.put("STBID", stbconfig.getSTBID());
		values.put("IP", stbconfig.getIP());
		values.put("MAC", stbconfig.getMAC());
		values.put("UserID", stbconfig.getUserID());
		values.put("UserPassword", stbconfig.getUserPassword());
		values.put("UserToken", stbconfig.getUserToken());
		values.put("LastchannelNum", stbconfig.getLastchannelNum());
		values.put("STBType", stbconfig.getSTBType());
		values.put("SoftwareVersion", stbconfig.getSoftwareVersion());
		values.put("Reserved", stbconfig.getReserved());
		values.put("NetworkAcount", stbconfig.getNetworkAcount());
		values.put("NetworkPassword", stbconfig.getNetworkPassword());
		
		ContentResolver cr = ctx.getContentResolver();
		if(cr.insert(StbconfigMetaData.SummaryTable.CONTENT_URI, values) != null){
			Log.i(TAG,"insert stbconfig success");
			return true;
		}
		Log.i(TAG,"insert stbconfig failed");
		return false;
	}

	public static boolean updateStbCofnig(Context ctx, StbConfig stbconfig) {
		Log.i(TAG,"update stbconfig :" + stbconfig.toString());
		ContentValues values = new ContentValues();
		values.put("AuthURL", stbconfig.getAuthURL());
		values.put("STBID", stbconfig.getSTBID());
		values.put("IP", stbconfig.getIP());
		values.put("MAC", stbconfig.getMAC());
		values.put("UserID", stbconfig.getUserID());
		values.put("UserPassword", stbconfig.getUserPassword());
		values.put("UserToken", stbconfig.getUserToken());
		values.put("LastchannelNum", stbconfig.getLastchannelNum());
		values.put("STBType", stbconfig.getSTBType());
		values.put("SoftwareVersion", stbconfig.getSoftwareVersion());
		values.put("Reserved", stbconfig.getReserved());
		values.put("NetworkAcount", stbconfig.getNetworkAcount());
		values.put("NetworkPassword", stbconfig.getNetworkPassword());
		
		ContentResolver cr = ctx.getContentResolver();
		if(cr.update(StbconfigMetaData.SummaryTable.CONTENT_URI, values, null, null) > 0){
			Log.i(TAG,"update stbconfig success");
			return true;
		}
		Log.i(TAG,"update stbconfig failed");
		return false;
	}

}
