package com.um.launcher.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.um.launcher.constant.DbConstants;
import com.um.launcher.data.MainPageAppInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainPageAppManager {
    public static final int CUSTOM_APP_COUNT = 5;
    private DbHelper dbOpenHelper;
    // private Context context;

    public MainPageAppManager(Context context) {
        // this.context = context;
        dbOpenHelper = new DbHelper(context);
    }

    public void add(List<MainPageAppInfo> apps) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        assert database != null;
        database.beginTransaction();    //开始事务
        try {
            MainPageAppInfo appInfo;
            for (int i = 0, size = apps.size(); i < size; i++) {
                appInfo = apps.get(i);
                add(appInfo);
            }
            database.setTransactionSuccessful();    //设置事务成功完成
        } finally {
            database.endTransaction();    //结束事务
        }
    }

    public void add(MainPageAppInfo appInfo) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        assert database != null;
        database.beginTransaction();
        database.execSQL("insert into "
                        + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME
                        + "("
                        + DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME + ","
                        + DbConstants.MAIN_PAGE_USER_APP_INSTALL_TIME + ")"
                        + "values(?,?)",
                new Object[]{appInfo.getPackageName(), appInfo.getInstallTime()});
        // database.close();可以不关闭数据库，他里面会缓存一个数据库对象，如果以后还要用就直接用这个缓存的数据库对象。但通过
        // context.openOrCreateDatabase(arg0, arg1, arg2)打开的数据库必须得关闭
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void update(MainPageAppInfo appInfo) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        assert database != null;
        database.execSQL("update "
                        + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME
                        + " set "
                        + DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME + "=?,"
                        + DbConstants.MAIN_PAGE_USER_APP_INSTALL_TIME + "=?"
                        + " where "
                        + DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME + "=?",
                new Object[]{appInfo.getPackageName(), appInfo.getInstallTime(), appInfo.getPackageName()});
    }

    public MainPageAppInfo find(String packageName) {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        assert database != null;
        Cursor cursor = database.rawQuery("select * from "
                        + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME
                        + " where"
                        + " personid" + "=?",
                new String[]{packageName});
        if (cursor.moveToNext()) {
            return new MainPageAppInfo(
                    cursor.getLong(cursor.getColumnIndex(DbConstants.MAIN_PAGE_USER_APP_INSTALL_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME)));
        }
        return null;
    }

    /**
     */
    public void delete(String... packageNames) {
        if (packageNames.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (String packageName : packageNames) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
            assert database != null;
            database.execSQL("delete from "
                    + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME
                    + " where "
                    + DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME + " in(" + sb.toString() + ")", packageNames);
        }
    }

    public ArrayList<MainPageAppInfo> getNew() {
        ArrayList<MainPageAppInfo> list = new ArrayList<MainPageAppInfo>();

        return list;
    }

    // Getting All Contacts
    public ArrayList<MainPageAppInfo> getAll() {
        ArrayList<MainPageAppInfo> list = new ArrayList<MainPageAppInfo>();
        try {
            list.clear();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME;

            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    MainPageAppInfo appInfo = new MainPageAppInfo();
                    appInfo.setPackageName(
                            cursor.getString(cursor.getColumnIndex(DbConstants.MAIN_PAGE_USER_APP_PACKAGE_NAME)));
                    appInfo.setInstallTime(
                            cursor.getLong(cursor.getColumnIndex(DbConstants.MAIN_PAGE_USER_APP_INSTALL_TIME)));
                    list.add(appInfo);
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("all_contact", "" + e);
        }
        return list;
    }

    /**
     * 获取数据库中记录的数量
     * @return 记录总数
     */
    public long getCount() {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        assert database != null;
        Cursor cursor = database.rawQuery("select count(*) from "
                + DbConstants.MAIN_PAGE_USER_APP_TABLE_NAME, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    public final static Comparator<MainPageAppInfo> MAIN_PAGE_APP_COMPARATOR =
            new Comparator<MainPageAppInfo>() {
                public int compare(MainPageAppInfo info1, MainPageAppInfo info2) {
                    long val1 = info1.getInstallTime();
                    long val2 = info2.getInstallTime();
                    return (val1 < val2 ? 1 : (val1 == val2 ? 0 : -1));
                }
            };

}
