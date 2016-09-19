package com.um.huanauth.provider;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper{
	
	private final static String DATABASE_PATH = "/atv/external/";
	private final static String DATABASE_NAME = "auth.db";
	private final static int DATABASE_VER = 3;
	
	static {
		File file = new File(DATABASE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}	
	}
	
	public DbOpenHelper(Context context) {
		super(context, DATABASE_PATH + DATABASE_NAME, null, DATABASE_VER);
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(ContentDefine.AuthTable.SQL_CREATE_TABLE);
		arg0.execSQL(ContentDefine.UpgradeTable.SQL_CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		arg0.execSQL(ContentDefine.AuthTable.SQL_DELETE_TABLE);
		arg0.execSQL(ContentDefine.UpgradeTable.SQL_DELETE_TABLE);
		onCreate(arg0);
	}
	
}
