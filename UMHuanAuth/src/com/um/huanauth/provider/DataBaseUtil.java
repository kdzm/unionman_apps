package com.um.huanauth.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseUtil {
	private static final String TAG = "DataBaseUtil";
	private Context mContext;
	private DbOpenHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public DataBaseUtil(Context context) {
		mContext = context;
	}
	
	public DataBaseUtil open() throws SQLException{
		mDbHelper = new DbOpenHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        
        return this;
	}
	
	public void close(){
		mDb.close();
	}
	
	public long createAuthInfo(ContentValues initialValues){
		
		return mDb.insert(ContentDefine.AuthTable.TABLE_NAME, null, initialValues);
	}
	
	public int deleteAuthInfo(int rowId){
		return mDb.delete(ContentDefine.AuthTable.TABLE_NAME, ContentDefine.AuthTable.KEY_ROWID+"="+rowId, null);
	}
	
	public int updateAuthInfo(int rowId, ContentValues newValues){
		return mDb.update(ContentDefine.AuthTable.TABLE_NAME, newValues, ContentDefine.AuthTable.KEY_ROWID+"="+rowId, null);
	}
	
	public Cursor fetchAuthInfo(long rowId) throws SQLException{
		Cursor cursor = mDb.query(true, ContentDefine.AuthTable.TABLE_NAME, new String []{ContentDefine.AuthTable.KEY_DEVICEID,
				ContentDefine.AuthTable.KEY_DNUM,
				ContentDefine.AuthTable.KEY_DEVICEMODE,
				ContentDefine.AuthTable.KEY_ACTIVEKEY,
				ContentDefine.AuthTable.KEY_DIDTOKEN,
				ContentDefine.AuthTable.KEY_TOKEN,
				ContentDefine.AuthTable.KEY_HUANID,
				ContentDefine.AuthTable.KEY_LICENSETYPE,
				ContentDefine.AuthTable.KEY_LICENSEDATA,
				ContentDefine.AuthTable.KEY_ACTIVEFLAG,
				ContentDefine.AuthTable.KEY_DEVICEACTIVEENABLE
				}, 
				ContentDefine.AuthTable.KEY_ROWID+"="+rowId, null, null, null, null, null);
		
		if (cursor != null){
			cursor.moveToFirst();
		}
		
		return cursor;
	}
	
	public long createUpgradeInfo(ContentValues initialValues){
		
		return mDb.insert(ContentDefine.UpgradeTable.TABLE_NAME, null, initialValues);
	}
	
	public int deleteUpgradeInfo(int rowId){
		return mDb.delete(ContentDefine.UpgradeTable.TABLE_NAME, ContentDefine.UpgradeTable.KEY_ROWID+"="+rowId, null);
	}
	
	public int updateUpgradeInfo(int rowId, ContentValues newValues){
		return mDb.update(ContentDefine.UpgradeTable.TABLE_NAME, newValues, ContentDefine.UpgradeTable.KEY_ROWID+"="+rowId, null);
	}
	
	public Cursor fetchUpgradeInfo(long rowId) throws SQLException{
		Cursor cursor = mDb.query(true, ContentDefine.UpgradeTable.TABLE_NAME, new String []{ContentDefine.UpgradeTable.KEY_CALLID,
				ContentDefine.UpgradeTable.KEY_VERSION,
				ContentDefine.UpgradeTable.KEY_STAGE,
				ContentDefine.UpgradeTable.KEY_DOWNLOADID,
				ContentDefine.UpgradeTable.KEY_FILEURL,
				ContentDefine.UpgradeTable.KEY_FILESIZE,
				ContentDefine.UpgradeTable.KEY_FILEMD5,
				ContentDefine.UpgradeTable.KEY_FILEVERSION,
				ContentDefine.UpgradeTable.KEY_FILESTOAGEURL,
				ContentDefine.UpgradeTable.KEY_FILEUUID
				}, 
				ContentDefine.AuthTable.KEY_ROWID+"="+rowId, null, null, null, null, null);
		
		if (cursor != null){
			cursor.moveToFirst();
		}
		
		return cursor;
	}
	
}
