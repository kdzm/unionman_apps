package cn.com.unionman.umtvsetting.system;

import java.io.File;                                                                                                

import android.os.Environment;    
import android.os.StatFs;    
    
public class StorageUtil {

    private static final int ERROR = -1;

    /**
     * SDCARD�Ƿ��
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * ��ȡ�ֻ��ڲ�ʣ��洢�ռ�
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
     
        return stat.getAvailableBytes();
    }

    /**
     * ��ȡ�ֻ��ڲ��ܵĴ洢�ռ�
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getTotalBytes();
    }

    /**
     * ��ȡSDCARDʣ��洢�ռ�
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
     * ��ȡSDCARD�ܵĴ洢�ռ�
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
