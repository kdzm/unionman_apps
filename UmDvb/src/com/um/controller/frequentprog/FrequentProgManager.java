package com.um.controller.frequentprog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hjan on 2014/4/18.
 */
public class FrequentProgManager {
    private final String INCREMENT_ENTYR_DUTATION = "UPDATE " + DBHelper.TABLE_FREQUENT_PROG
            + " SET " + DBHelper.KEY_WATCH_DURATION + " =( " + DBHelper.KEY_WATCH_DURATION + " + ?),"
            + DBHelper.KEY_WATCH_TIMES + " =( " + DBHelper.KEY_WATCH_TIMES + " + ?)"
            + " WHERE " + DBHelper.KEY_PROG_ID + " = ?";
    public static int NEED_NOT_SORT = 0;
    public static int SORT_BY_DURATION = 1;
    public static int SORT_BY_TIMES = 2;
    private DBHelper dbOpenHelper;
    // private Context context;

    public FrequentProgManager(Context context) {
        // this.context = context;
        dbOpenHelper = new DBHelper(context);
    }

    public void add(FrequentProg frequentProg) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_PROG_ID, frequentProg.getProgId()); //
        values.put(DBHelper.KEY_PROG_NAME, frequentProg.getProgName()); //
        values.put(DBHelper.KEY_WATCH_DURATION, frequentProg.getWatchDuration()); //
        values.put(DBHelper.KEY_WATCH_TIMES, frequentProg.getWatchTimes());
        values.put(DBHelper.KEY_PROG_MODE, frequentProg.getProgMode());

        if (db != null) {
            db.insert(DBHelper.TABLE_FREQUENT_PROG, null, values);
            db.close(); // Closing database connection
        }
    }

    // Getting single progFrequency
    public FrequentProg get(int prog_id) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        FrequentProg frequentProg = null;
        Cursor cursor;
        if (db != null) {
            cursor = db.query(
                    DBHelper.TABLE_FREQUENT_PROG,
                    new String[]{DBHelper.KEY_PROG_ID,
                            DBHelper.KEY_PROG_NAME,
                            DBHelper.KEY_WATCH_DURATION,
                            DBHelper.KEY_WATCH_TIMES,
                            DBHelper.KEY_PROG_MODE},
                    DBHelper.KEY_PROG_ID + "=?",
                    new String[]{String.valueOf(prog_id)},
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                frequentProg = new FrequentProg(
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PROG_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PROG_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_WATCH_DURATION)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_WATCH_TIMES)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PROG_MODE)));

                cursor.close();
                db.close();
            }
        }

        return frequentProg;
    }

    // Getting All ProgFrequencys
    public ArrayList<FrequentProg> getAll(int sortType, int mode) {
        ArrayList<FrequentProg> list = new ArrayList<FrequentProg>();
        try {
            list.clear();

            String selectQuery;
            String orderStr;
            if (sortType == SORT_BY_DURATION) {
                orderStr = " order by " + DBHelper.KEY_WATCH_DURATION + " desc";
                selectQuery = "SELECT  * FROM " + DBHelper.TABLE_FREQUENT_PROG + orderStr;
            } else if (sortType == SORT_BY_TIMES) {
                orderStr = " order by " + DBHelper.KEY_WATCH_TIMES + " desc";
                selectQuery = "SELECT  * FROM " + DBHelper.TABLE_FREQUENT_PROG + orderStr;
            } else {
                selectQuery = "SELECT  * FROM " + DBHelper.TABLE_FREQUENT_PROG;
            }

            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            Cursor cursor = null;
            if (db != null) {
                cursor = db.rawQuery(selectQuery, null);

                int progMode;
                if (cursor.moveToFirst()) {
                    do {
                        progMode = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PROG_MODE));
                        if (mode == progMode) {
                            FrequentProg frequentProg = new FrequentProg();
                            frequentProg.setProgId(
                                    cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PROG_ID)));
                            frequentProg.setProgName(
                                    cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PROG_NAME)));
                            frequentProg.setWatchDuration(
                                    cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_WATCH_DURATION)));
                            frequentProg.setWatchTimes(
                                    cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_WATCH_TIMES)));
                            frequentProg.setProgMode(progMode);

                            list.add(frequentProg);
                        }
                    } while (cursor.moveToNext());

                    cursor.close();
                    db.close();
                }
            }

            return list;
        } catch (Exception e) {
            Log.e("all_progFrequency", "");
            e.printStackTrace();
        }

        return list;
    }

    // Updating single frequentProg
    public boolean update(int progId, boolean needIncreaseTimes, int duration) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        assert db != null;
        SQLiteStatement localSQLiteStatement = db.compileStatement(INCREMENT_ENTYR_DUTATION);
        assert localSQLiteStatement != null;
        localSQLiteStatement.bindLong(1, duration);
        localSQLiteStatement.bindLong(2, needIncreaseTimes ? 1 : 0);
        localSQLiteStatement.bindLong(3, progId);

        boolean bool;
        try {
            localSQLiteStatement.execute();
            bool = true;
            db.close();

        } catch (SQLiteDatabaseCorruptException dbCorruptException) {
            bool = false;
            dbCorruptException.printStackTrace();
        }

        return bool;
    }

    public void deleteAll(){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db != null) {
            db.delete(DBHelper.TABLE_FREQUENT_PROG,
                    DBHelper.KEY_PROG_ID + " > ?",
                    new String[]{String.valueOf(0)});

            db.close();
        }
    }

    // Deleting single progFrequency
    public void delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db != null) {
            db.delete(DBHelper.TABLE_FREQUENT_PROG,
                    DBHelper.KEY_PROG_ID + " = ?",
                    new String[]{String.valueOf(id)});

            db.close();
        }
    }

    // Getting progFrequencys Count
    public int getCounts() {
        String countQuery = "SELECT  * FROM " + DBHelper.TABLE_FREQUENT_PROG;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        int count = 0;
        if (db != null) {
            Cursor cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
            cursor.close();
        }

        // return count
        return count;
    }

    public boolean isExit(int prog_id) {
        return get(prog_id) != null;
    }

    public void closeDB() {
        dbOpenHelper.close();
    }
}
