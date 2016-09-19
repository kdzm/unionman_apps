package com.unionman.stbconfig;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

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

}
