package com.um.huanauth.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AuthProvider extends ContentProvider{
	private static final String TAG = "AuthProvider";
	private static final int AUTH_MYTYPE = 1;
	private static final int AUTH_ITEM_MYTYPE = 2;
	private static final UriMatcher mUriMatcher;
	private Context mContext;
	private DbOpenHelper mDbOpenHelper;
	
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(ContentDefine.AUTHORITY,
				ContentDefine.AuthTable.TABLE_NAME, AUTH_MYTYPE);
		mUriMatcher.addURI(ContentDefine.AUTHORITY,
				ContentDefine.AuthTable.TABLE_NAME + "/#", AUTH_ITEM_MYTYPE);
	}
	
	@Override
	public boolean onCreate() {
		mContext = getContext();
		mDbOpenHelper = new DbOpenHelper(mContext);
		
		return true;
	}
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tableName;
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		
		switch (mUriMatcher.match(uri)){
			case AUTH_MYTYPE:
				tableName = ContentDefine.AuthTable.TABLE_NAME;
				break;
			case AUTH_ITEM_MYTYPE:
				tableName = ContentDefine.AuthTable.TABLE_NAME;
				break;
			default:
				throw new IllegalArgumentException("UnKnow URI:" + uri);
		}
		
		long rowId = db.insert(tableName, null, values);
		if (rowId > 0) {
			Uri nodeUri = ContentUris.withAppendedId(uri, rowId);
			mContext.getContentResolver().notifyChange(nodeUri, null);
			return nodeUri;
		}

		throw new SQLiteException("Fail to insert data:" + uri);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		String tableName;
		int count = 0;
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		
		switch (mUriMatcher.match(uri)){
			case AUTH_MYTYPE:
				tableName = ContentDefine.AuthTable.TABLE_NAME;
				count = db.delete(tableName, where, whereArgs);
				break;
			case AUTH_ITEM_MYTYPE:
				tableName = ContentDefine.AuthTable.TABLE_NAME;
				String id = uri.getPathSegments().get(1);
				count = db.delete(
						tableName,
						ContentDefine.AuthTable.KEY_ROWID
								+ "="
								+ id
								+ (!TextUtils.isEmpty(where) ? " AND(" + where + ')'
										: ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("UnKnow URI:" + uri);
		}
		
		mContext.getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
	@Override
	public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		String tableName;
		int count;
		
		switch (mUriMatcher.match(uri)){
			case AUTH_ITEM_MYTYPE:
				tableName = ContentDefine.AuthTable.TABLE_NAME;
				String id = uri.getPathSegments().get(1);
				count = db.update(
						tableName,
						contentValues,
						ContentDefine.AuthTable.KEY_ROWID
								+ "="
								+ id
								+ (!TextUtils.isEmpty(where) ? " AND(" + where + ')'
										: ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("UnKnow URI:" + uri);
		}
		
		mContext.getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		
		switch (mUriMatcher.match(uri)) {
			case AUTH_MYTYPE:
				return db.query(ContentDefine.AuthTable.TABLE_NAME, projection,
						selection, selectionArgs, null, null, sortOrder);
			case AUTH_ITEM_MYTYPE:
				String rowId = uri.getPathSegments().get(1);
				return db.query(
						ContentDefine.AuthTable.TABLE_NAME,
						projection,
						ContentDefine.AuthTable.KEY_ROWID
								+ "="
								+ rowId
								+ (!TextUtils.isEmpty(selection) ? " AND("
										+ selection + ')' : ""), selectionArgs,
						null, null, sortOrder);
			default:
				throw new IllegalArgumentException("UnKnow URI" + uri);
		}
	}
	
	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case AUTH_MYTYPE:
			return ContentDefine.AuthTable.CONTENT_TYPE;
		case AUTH_ITEM_MYTYPE:
			return ContentDefine.AuthTable.CONTENT_ITME_TYPE;
		default:
			throw new IllegalArgumentException("UnKonw uri" + uri);
		}
	}
	
}
