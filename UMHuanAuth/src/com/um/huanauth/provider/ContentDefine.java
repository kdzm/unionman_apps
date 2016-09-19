package com.um.huanauth.provider;

import android.net.Uri;

public class ContentDefine {
	public static final String AUTHORITY = "com.uninoman.huan.auth.provider";
	
	public static class AuthTable {
		public static final String TABLE_NAME = "authInfo";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.huan.auth";
		public static final String CONTENT_ITME_TYPE = "vnd.android.cursor.item/vnd.huan.auth";
		
		public static final String KEY_ROWID = "_id";
		public static final String KEY_DEVICEID = "deviceid";
		public static final String KEY_DNUM = "dnum";
		public static final String KEY_DEVICEMODE = "devicemode";
		public static final String KEY_ACTIVEKEY = "activekey";
		public static final String KEY_DIDTOKEN = "didtoken";
		public static final String KEY_TOKEN = "token";
		public static final String KEY_HUANID = "huanid";
		public static final String KEY_LICENSETYPE = "licensetype";
		public static final String KEY_LICENSEDATA = "licensedata";
		public static final String KEY_ACTIVEFLAG = "activeflag";
		public static final String KEY_DEVICEACTIVEENABLE = "deviceactiveenable";
		
		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ROWID + " integer PRIMARY KEY autoincrement,"
                + KEY_DEVICEID + " TEXT,"
                + KEY_DNUM + " TEXT,"
                + KEY_DEVICEMODE + " TEXT,"
                + KEY_ACTIVEKEY + " TEXT,"
                + KEY_DIDTOKEN + " TEXT,"
                + KEY_TOKEN + " TEXT,"
                + KEY_HUANID + " TEXT,"
                + KEY_LICENSETYPE + " TEXT,"
                + KEY_LICENSEDATA + " TEXT,"
                + KEY_ACTIVEFLAG + " TEXT,"
                + KEY_DEVICEACTIVEENABLE + " TEXT"
                + ");";
		
		public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME +";";
	}
	
	public static class UpgradeTable{
		public static final String TABLE_NAME = "upgradeInfo";
		
		public static final String STAGE_DOWNLOAD = "download";
		public static final String STAGE_UPDATE = "update";
		public static final String STAGE_NORMAL = "normal";
		
		public static final String KEY_ROWID = "_id";
		public static final String KEY_CALLID = "callid";
		public static final String KEY_VERSION = "version";    
		public static final String KEY_STAGE = "stage";
		public static final String KEY_DOWNLOADID = "downloadid";
		public static final String KEY_FILEURL = "fileurl";
		public static final String KEY_FILESIZE = "filesize";
		public static final String KEY_FILEMD5 = "filemd5";
		public static final String KEY_FILEVERSION = "fileversion";
		public static final String KEY_FILESTOAGEURL = "filestoageurl";
		public static final String KEY_FILEUUID = "fileuuid";
		public static final String KEY_RESERVER1 = "reserver1";
		public static final String KEY_RESERVER2 = "reserver2";
		
		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ROWID + " integer PRIMARY KEY autoincrement,"
                + KEY_CALLID + " TEXT,"
                + KEY_VERSION + " TEXT,"
                + KEY_STAGE + " TEXT,"
                + KEY_DOWNLOADID + " TEXT,"
                + KEY_FILEURL + " TEXT,"
                + KEY_FILESIZE + " TEXT,"
                + KEY_FILEMD5 + " TEXT,"
                + KEY_FILEVERSION + " TEXT,"
                + KEY_FILESTOAGEURL + " TEXT,"
                + KEY_FILEUUID + " TEXT,"
                + KEY_RESERVER1 + " TEXT,"
                + KEY_RESERVER2 + " TEXT"
                + ");";
		
		public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME +";";
	}
}
