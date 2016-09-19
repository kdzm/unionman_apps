package com.um.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.um.launcher.constant.DbConstants;

/**
 * db helper
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-10-21
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(DbConstants.CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
