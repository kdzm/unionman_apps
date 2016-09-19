package com.um.upgrade;

public class DefaultParameter {
    public static final String STB_MACHINE_MODEL = "UT-H518SDS";  //默认机器型号
    public static final String STB_PRODUCT_MODEL = "UT-H518SDS-TFB-";  //默认产品型号
    public static final String STB_VENDOR = "unionman";  //默认厂商
    public static final String STB_DVB_FLAG = "0"; //默认无DVB功能
    public static final String STB_SOFTWARE_VERSION = "01.000";  //
    public static final String STB_HARDWARE_VERSION = "UNKOWN";  //
    public static final String STB_SERIAL_NO = ""; //
    public static final String STB_LOADER_VERSION = "1.0.0.0";  //loader版本号
    public static final String STB_SERVER_URL = "www.umstb.com.cn";  //正式域名，新域名 String configUrl = "http://"+serverUrl+"/upgrade/config/config.xml";
    public static final String UPGRADE_CONFIG = "/upgrade/umtv/config.xml";
    public static final String STB_BACKUP_SERVER_URL = "bgw025052.my3w.com:80";  //临时域名，老域名
    public static final int STB_SHOWED_SERIAL_START = 0; //显示的序列号开始位
    public static final int STB_SHOWED_SERIAL_LEN = 17; //显示的序列号长度
    public static final int STB_SERIAL_LEN = 17; //实际的序列号长度，包括：厂商代码，产品型号，运营商代码，生产年号等
    public static final int STB_MAC_LEN = 12; //MAC地址长度
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
    public static final String KEY_NAME_DOWNLOAD_VERSION = "downloadVersion";
    public static final String KEY_NAME_UPGRADE_MODE = "upgradeMode";
    public static final String KEY_NAME_UPGRADE_FILE_TYPE = "upgradeFileType";
    public static final String KEY_NAME_UPGRADE_SOFTWARE_VERSION = "upgradeSoftWareVersion";
    public static final String KEY_NAME_UPGRADE_DESCRIPTION = "upgradeDescription";
    public static final String KEY_NAME_DEVICE_SOFTWARE_VERSION = "devicesSoftWareVersion";

    public static final String DOWNLOAD_FOLDER_NAME = "/cache";
    public static final String DOWNLOAD_FILE_NAME = "update.zip";

    /* 以下用于Recovery分区升级 */
    public static final String STB_RECOVERY_PARTITION = "/sdcard/recovery/system/bin/recovery/recovery";
    public static final String STB_LODAERDB_PARTITION = "/sdcard/recovery/system/bin/recovery/loaderdb";
    public static final String STB_UNZIP_RECOVERY_DIR = "/sdcard/recovery_tmp/";
    public static final String STB_RECOVERY_IMG_NAME = "recovery.img";
    public static final String STB_LOADERDB_IMG_NAME = "loaderdb.img";
}
