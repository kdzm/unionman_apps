package com.unionman.stbconfig;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

public class StbconfigProvider extends ContentProvider {
	private static final String TAG = "StbconfigProvider";
	private static final UriMatcher sUriMatcher;
	private static final int SUMMARY = 1;
	private static final int SUMMARY_ID = 2;
	private static final int STARTMETHOD = 3;
	private static final int STARTMETHOD_ID = 4;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StbconfigMetaData.AUTHORITY, "summary", SUMMARY);
		sUriMatcher
				.addURI(StbconfigMetaData.AUTHORITY, "summary/#", SUMMARY_ID);
		sUriMatcher.addURI(StbconfigMetaData.AUTHORITY, "startmethod",
				STARTMETHOD);
		sUriMatcher.addURI(StbconfigMetaData.AUTHORITY, "startmethod/#",
				STARTMETHOD_ID);
	}
	private static HashMap<String, String> stbconfigProjMap;
	private static HashMap<String, String> startmethodProjMap;
	static {
		stbconfigProjMap = new HashMap<String, String>();
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable._ID,
				StbconfigMetaData.SummaryTable._ID);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.AuthURL,
				StbconfigMetaData.SummaryTable.AuthURL);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.STBID,
				StbconfigMetaData.SummaryTable.STBID);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.IP,
				StbconfigMetaData.SummaryTable.IP);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.MAC,
				StbconfigMetaData.SummaryTable.MAC);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.UserID,
				StbconfigMetaData.SummaryTable.UserID);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.UserPassword,
				StbconfigMetaData.SummaryTable.UserPassword);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.UserToken,
				StbconfigMetaData.SummaryTable.UserToken);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.LastchannelNum,
				StbconfigMetaData.SummaryTable.LastchannelNum);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.STBType,
				StbconfigMetaData.SummaryTable.STBType);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.SoftwareVersion,
				StbconfigMetaData.SummaryTable.SoftwareVersion);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.Reserved,
				StbconfigMetaData.SummaryTable.Reserved);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.NetworkAcount,
				StbconfigMetaData.SummaryTable.NetworkAcount);
		stbconfigProjMap.put(StbconfigMetaData.SummaryTable.NetworkPassword,
				StbconfigMetaData.SummaryTable.NetworkPassword);

		startmethodProjMap = new HashMap<String, String>();
		startmethodProjMap.put(StbconfigMetaData.StartmethodTable._ID,
				StbconfigMetaData.StartmethodTable._ID);
		startmethodProjMap.put(StbconfigMetaData.StartmethodTable.isAlways,
				StbconfigMetaData.StartmethodTable.isAlways);
		startmethodProjMap.put(StbconfigMetaData.StartmethodTable.platform,
				StbconfigMetaData.StartmethodTable.platform);
	}

	private DataBaseHelper mDBHelper;

	/*private static class DataBaseHelper extends SQLiteOpenHelper {

		public DataBaseHelper(Context context) {
			super(context, StbconfigMetaData.DATABASE_NAME, null,
					StbconfigMetaData.DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub
			Log.e("myDataBaseHelper", "create table:"
					+ StbconfigMetaData.SummaryTable.SQL_CREATE_TABLE);
			arg0.execSQL(StbconfigMetaData.SummaryTable.SQL_CREATE_TABLE);
			Log.e("myDataBaseHelper", "create table:"
					+ StbconfigMetaData.StartmethodTable.SQL_CREATE_TABLE);
			arg0.execSQL(StbconfigMetaData.StartmethodTable.SQL_CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			arg0.execSQL("DROP TABLE IF EXISTS "
					+ StbconfigMetaData.SummaryTable.TABLE_NAME);
			arg0.execSQL("DROP TABLE IF EXISTS "
					+ StbconfigMetaData.StartmethodTable.TABLE_NAME);
			onCreate(arg0);
		}

	}*/

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = -1;
		switch (sUriMatcher.match(uri)) {
		case SUMMARY:
			count = db.delete(StbconfigMetaData.SummaryTable.TABLE_NAME, where,
					whereArgs);
			break;
		case SUMMARY_ID:
			String summaryID = uri.getPathSegments().get(1);
			count = db.delete(StbconfigMetaData.SummaryTable.TABLE_NAME,
					StbconfigMetaData.SummaryTable._ID
							+ " = "
							+ summaryID
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArgs);
			break;
		case STARTMETHOD:
			count = db.delete(StbconfigMetaData.StartmethodTable.TABLE_NAME,
					where, whereArgs);
			break;
		case STARTMETHOD_ID:
			String startmethodId = uri.getPathSegments().get(1);
			count = db.delete(StbconfigMetaData.StartmethodTable.TABLE_NAME,
					StbconfigMetaData.StartmethodTable._ID
							+ startmethodId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("unknown uri:" + uri);
		}
		this.getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		switch (sUriMatcher.match(arg0)) {
		case SUMMARY:
		case STARTMETHOD:
			return StbconfigMetaData.CONTENT_TYPE;
		case SUMMARY_ID:
		case STARTMETHOD_ID:
			return StbconfigMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("unknown uri:" + arg0);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			throw new SQLException("Fail to insert to table:null values");
		}

		String tableName = "";
		String nullColumn = "";
		switch (sUriMatcher.match(uri)) {
		case SUMMARY:
			tableName = StbconfigMetaData.SummaryTable.TABLE_NAME;
			nullColumn = StbconfigMetaData.SummaryTable.MAC;
			if (values.containsKey(StbconfigMetaData.SummaryTable.MAC) == false) {
				values.put(StbconfigMetaData.SummaryTable.MAC,
						SystemProperties.get("ro.mac", ""));
			}
			if (values.containsKey(StbconfigMetaData.SummaryTable.STBID) == false) {
				values.put(StbconfigMetaData.SummaryTable.STBID,
						SystemProperties.get("ro.serialno", ""));
			}
			break;
		case STARTMETHOD:
			tableName = StbconfigMetaData.StartmethodTable.TABLE_NAME;
			nullColumn = StbconfigMetaData.StartmethodTable.isAlways;
			break;
		default:
			throw new IllegalArgumentException("unknown uri:" + uri);
		}
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long rowID = db.insert(tableName, nullColumn, initialValues);
		if (rowID > 0) {
			Uri retUri = ContentUris.withAppendedId(uri, rowID);
			return retUri;
		}
		throw new SQLException("Fail to insert to table:" + uri);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mDBHelper = new DataBaseHelper(getContext());
		return (mDBHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String orderby;
		switch (sUriMatcher.match(arg0)) {
		case SUMMARY:
		case SUMMARY_ID:
			queryBuilder.setTables(StbconfigMetaData.SummaryTable.TABLE_NAME);
			if (TextUtils.isEmpty(arg4)) {
				orderby = StbconfigMetaData.SummaryTable.DEFAULT_ORDERBY;
			} else {
				orderby = arg4;
			}
			break;
		case STARTMETHOD:
		case STARTMETHOD_ID:
			queryBuilder
					.setTables(StbconfigMetaData.StartmethodTable.TABLE_NAME);
			if (TextUtils.isEmpty(arg4)) {
				orderby = StbconfigMetaData.StartmethodTable.DEFAULT_ORDERBY;
			} else {
				orderby = arg4;
			}
			break;
		default:
			throw new IllegalArgumentException("unknown uri:" + arg0);
		}

		switch (sUriMatcher.match(arg0)) {
		case SUMMARY:
			queryBuilder.setProjectionMap(stbconfigProjMap);
			break;
		case STARTMETHOD:
			queryBuilder.setProjectionMap(startmethodProjMap);
			break;
		case SUMMARY_ID:
			queryBuilder.setProjectionMap(stbconfigProjMap);
			queryBuilder.appendWhere(StbconfigMetaData.SummaryTable._ID + " = "
					+ arg0.getPathSegments().get(1));
			break;
		case STARTMETHOD_ID:
			queryBuilder.setProjectionMap(startmethodProjMap);
			queryBuilder.appendWhere(StbconfigMetaData.StartmethodTable._ID
					+ " = " + arg0.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("unknown uri:" + arg0);
		}

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Log.i(TAG,"querybuilder start query");
		Cursor cursor = queryBuilder.query(db, arg1, arg2, arg3, null, null,
				orderby);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArg) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = -1;
		switch (sUriMatcher.match(uri)) {
		case SUMMARY:
			count = db.update(StbconfigMetaData.SummaryTable.TABLE_NAME,
					values, where, whereArg);
			break;
		case SUMMARY_ID:
			String summaryId = uri.getPathSegments().get(1);
			count = db.update(StbconfigMetaData.SummaryTable.TABLE_NAME,
					values, StbconfigMetaData.SummaryTable._ID
							+ " = "
							+ summaryId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArg);
			break;
		case STARTMETHOD:
			String startmethodId = uri.getPathSegments().get(1);
			count = db.update(StbconfigMetaData.StartmethodTable.TABLE_NAME,
					values, StbconfigMetaData.StartmethodTable._ID
							+ " = "
							+ startmethodId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArg);
			break;
		default:
			throw new IllegalArgumentException("unknown uri:" + uri);
		}
		this.getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
