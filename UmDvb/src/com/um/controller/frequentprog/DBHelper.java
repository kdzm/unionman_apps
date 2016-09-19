package com.um.controller.frequentprog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hjan on 2014/4/18.
 */
public class DBHelper extends SQLiteOpenHelper {
    
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "um_dvb.db";
    public static final String TABLE_FREQUENT_PROG = "prog_frequency";

    public static final String KEY_PROG_ID = "prog_id";
    public static final String KEY_PROG_NAME = "prog_name";
    public static final String KEY_WATCH_DURATION = "watch_duration";
    public static final String KEY_WATCH_TIMES = "watch_times";
    public static final String KEY_PROG_MODE = "prog_mode";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_FREQUENT_PROG + "("
                + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PROG_ID + " INTEGER,"
                + KEY_PROG_NAME + " VARCHAR,"
                + KEY_WATCH_DURATION + " INTEGER,"
                + KEY_WATCH_TIMES + " INTEGER,"
                + KEY_PROG_MODE + " INTEGER" + ")";
        db.execSQL(sql);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FREQUENT_PROG);

        // Create tables again
        onCreate(db);
    }
}
