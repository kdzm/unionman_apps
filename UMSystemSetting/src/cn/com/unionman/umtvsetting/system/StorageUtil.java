package cn.com.unionman.umtvsetting.system;

import java.io.File;                                                                                                

import android.os.Environment;    
import android.os.StatFs;    
    
public class StorageUtil {

    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
     
        return stat.getAvailableBytes();
    }

    /**
     * 获取手机内部总的存储空间
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getTotalBytes();
    }

    /**
     * 获取SDCARD剩余存储空间
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            return stat.getAvailableBytes();
        } else {
            return ERROR;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            return stat.getTotalBytes();
        } else {
            return ERROR;
        }
    }
}
