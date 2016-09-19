package com.um.launcher.constant;

/**
 * Some constants about db
 * 
 */
public class DbConstants {

    public static final String       DB_NAME                                       = "unionman_launcher.db";
    public static final int          DB_VERSION                                    = 2;

    private static final String      TERMINATOR                                    = ";";

    public static final StringBuffer CREATE_MAIN_PAGE_USER_APP_TABLE_SQL = new StringBuffer();
    public static final String MAIN_PAGE_USER_APP_TABLE_ID = android.provider.BaseColumns._ID;
    public static final String MAIN_PAGE_USER_APP_TABLE_NAME = "main_page_app";
    public static final String MAIN_PAGE_USER_APP_PACKAGE_NAME = "package_name";
    public static final String MAIN_PAGE_USER_APP_INSTALL_TIME = "install_time";

    static {
        /**
         * sql to main page user app table
         **/
        CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.append("CREATE TABLE ").append(MAIN_PAGE_USER_APP_TABLE_NAME);
        CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.append(" (").append(MAIN_PAGE_USER_APP_TABLE_ID)
                .append(" integer primary key autoincrement,");
        CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.append(MAIN_PAGE_USER_APP_PACKAGE_NAME).append(" text,");
        CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.append(MAIN_PAGE_USER_APP_INSTALL_TIME).append(" long)");
        CREATE_MAIN_PAGE_USER_APP_TABLE_SQL.append(TERMINATOR);
    }
}
