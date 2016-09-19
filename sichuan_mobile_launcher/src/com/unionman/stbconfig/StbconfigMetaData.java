package com.unionman.stbconfig;

import android.net.Uri;
import android.provider.BaseColumns;

public final class StbconfigMetaData {
	public static final String AUTHORITY = "stbconfig";
	public static final String DATABASE_NAME = "stbconfig.db";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.unionman.stbconfig";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.unionman.stbconfig";
	public static final int DATABASE_VERSION = 1;

	public static final class SummaryTable implements BaseColumns {
		public static final String TABLE_NAME = "summary";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);

		public static final String AuthURL = "AuthURL";
		public static final String STBID = "STBID";
		public static final String IP = "IP";
		public static final String MAC = "MAC";
		public static final String UserID = "UserID";
		public static final String UserPassword = "UserPassword";
		public static final String UserToken = "UserToken";
		public static final String LastchannelNum = "LastchannelNum";
		public static final String STBType = "STBType";
		public static final String SoftwareVersion = "SoftwareVersion";
		public static final String Reserved = "Reserved";
		public static final String NetworkAcount = "NetworkAcount";
		public static final String NetworkPassword = "NetworkPassword";

		public static final String DEFAULT_ORDERBY = "";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE "
				+ TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + AuthURL + " TEXT,"
				+ STBID + " TEXT," + IP + " TEXT," + MAC + " TEXT," + UserID
				+ " TEXT," + UserPassword + " TEXT," + UserToken + " TEXT,"
				+ LastchannelNum + " TEXT," + STBType + " TEXT,"
				+ SoftwareVersion + " TEXT," + Reserved + " TEXT,"
				+ NetworkAcount + " TEXT," + NetworkPassword + " TEXT"
				+ ");";
	}

	public static final class StartmethodTable implements BaseColumns {
		public static final String TABLE_NAME = "startmethod";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String platform = "platform";
		public static final String isAlways = "isAlways";
		public static final String DEFAULT_ORDERBY = "";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE "
				+ TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + isAlways + " TEXT,"
				+ platform + " TEXT);";
	}
}
