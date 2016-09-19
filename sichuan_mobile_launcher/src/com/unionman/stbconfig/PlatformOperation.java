package com.unionman.stbconfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.unionman.main.PlatformInfo;

public class PlatformOperation {
	public static final String TAG = "PlatformOperation";

	public static PlatformInfo queryPlatform(Context ctx) {
		Log.i(TAG, "query platform");
		DataBaseHelper dbhelper = new DataBaseHelper(ctx);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(StbconfigMetaData.StartmethodTable.TABLE_NAME);
		queryBuilder.appendWhere(StbconfigMetaData.StartmethodTable._ID
				+ " = 1");
		Cursor cursor = queryBuilder.query(dbhelper.getReadableDatabase(),
				new String[] { StbconfigMetaData.StartmethodTable.isAlways,
						StbconfigMetaData.StartmethodTable.platform }, null,
				null, null, null, null);
		if (cursor.getCount() == 1) {
			Log.i(TAG, "get platform succes");
			cursor.moveToFirst();
			PlatformInfo info = new PlatformInfo();
			if ("true"
					.equals(cursor.getString(cursor
							.getColumnIndex(StbconfigMetaData.StartmethodTable.isAlways)))) {
				info.setAlways(true);
			}
			info.setCurPlatform(cursor.getString(cursor
					.getColumnIndex(StbconfigMetaData.StartmethodTable.platform)));
			dbhelper.close();
			return info;
		} else {
			Log.i(TAG, "get platform fail");
			dbhelper.close();
			return null;
		}
	}

	public static boolean insertPlatform(Context ctx, PlatformInfo platform) {
		Log.i(TAG, "insert platform:" + platform.toString());
		DataBaseHelper dbhelper = new DataBaseHelper(ctx);
		ContentValues values = new ContentValues();
		if (platform.isAlways()) {
			values.put(StbconfigMetaData.StartmethodTable.isAlways, "true");
		} else {
			values.put(StbconfigMetaData.StartmethodTable.isAlways, "false");
		}
		values.put(StbconfigMetaData.StartmethodTable.platform,
				platform.getCurPlatform());
		if (0 >= dbhelper.getWritableDatabase().insert(
				StbconfigMetaData.StartmethodTable.TABLE_NAME, null, values)) {
			Log.i(TAG, "insert success");
			dbhelper.close();
			return true;
		} else {
			Log.i(TAG, "insert fail");
			dbhelper.close();
			return false;
		}
	}

	public static void updatePlatformInfo(Context ctx, PlatformInfo info) {
		Log.i(TAG, "update platform:" + info.toString());
		DataBaseHelper dbhelper = new DataBaseHelper(ctx);
		ContentValues values = new ContentValues();
		if (info.isAlways()) {
			values.put(StbconfigMetaData.StartmethodTable.isAlways, "true");
		} else {
			values.put(StbconfigMetaData.StartmethodTable.isAlways, "false");
		}
		values.put(StbconfigMetaData.StartmethodTable.platform,
				info.getCurPlatform());
		if(1 == dbhelper.getWritableDatabase().update(
				StbconfigMetaData.StartmethodTable.TABLE_NAME, values,
				StbconfigMetaData.StartmethodTable._ID + " =1", null)){
			Log.i(TAG,"update success");
		}else{
			Log.i(TAG,"update fail");
		}
		dbhelper.close();
	}

}
