package com.um.upgrade.util;

import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ziliang.nong on 14-6-17.
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName()+"----U668";
    private static boolean LOGE = true;

    private FileUtil() {
    }

    public static boolean renameFile(String oldPath, String newPath) {
        boolean result = false;
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        if (newFile.exists() && newFile.isFile()) {
            newFile.delete();
        }

        if (oldFile.exists() && oldFile.isFile()) {
            result = oldFile.renameTo(newFile);
        }

        if (oldFile.exists() && oldFile.isFile()) {
            oldFile.delete();
        }
        return result;
    }

    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }

            file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

    public static boolean fileExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static boolean renameFolder(String oldDirPath, String newDirPath) {
        boolean result = false;
        File oldFolder = new File(oldDirPath);
        File newFolder = new File(newDirPath);
        if (newFolder.exists() && newFolder.isDirectory()) {
            newFolder.delete();
        }

        if (oldFolder.exists() && oldFolder.isDirectory()) {
            result = oldFolder.renameTo(newFolder);
        }

        if (oldFolder.exists() && oldFolder.isDirectory()) {
            oldFolder.delete();
        }
        return result;
    }

    public static File creatDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.exists() && file.isDirectory()) {
                file.delete();
            }
            file.mkdir();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static boolean deleteDir(String dirPath) {
//        try {
//            File file = new File(dirPath);
//            if (file.exists() && file.isDirectory()) {
//                Log.d("deleteDir--U668", "try to delete directory: "+dirPath);
//                return file.delete();
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("deleteDir", "error!!!");
//            return false;
//        }
//    }

    public static boolean dirExist(String folderPath) {
        File file = new File(folderPath);
        if(file.exists() && file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteDir(String dirPath) {
        File file = new File(dirPath);
        return deleteDir(file);
    }

    private static boolean deleteDir(File file) {
        boolean result = false;
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return file.delete();
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteDir(childFiles[i]);
            }
            result = file.delete();
        }
        return result;
    }

    /**
     * 写数据到SD卡中
     * @param filePath
     * @param writeString
     */
    public static void writeFile(String filePath, String writeString){
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            byte [] bytes = writeString.getBytes();

            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取SD卡中的文件
     * @param filePath
     * @return
     */
    public static String readFile(String filePath){
        String result="";
        try {
            FileInputStream fin = new FileInputStream(filePath);

            int length = fin.available();

            byte [] buffer = new byte[length];
            fin.read(buffer);

            result = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从输入流中写到文件
     * @param filePath
     * @param is
     * @return
     */
    public static boolean writeFileFromStream(String filePath, InputStream is) {
        if (LOGE)Log.d(TAG, "writeFileFromStream, filePath: "+filePath+", is: "+is);
        try {
            String SUFFIX = ".tmp";
//            OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filePath+SUFFIX)));
//            int diviIndex = filePath.lastIndexOf("/");
//            String folderPath = filePath.substring(0, diviIndex+1);
//            String fileName = filePath.substring(diviIndex+1);
//            Log.d("writeFileFromStream----U668", "folderPath: "+folderPath+", fileName: "+fileName);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            final int BUFFER_SIZE = 10240;
            int offset = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            if (LOGE) Log.d(TAG, "writeFileFromStream, writing");
            while ((offset = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                if (LOGE) Log.d(TAG, "writeFileFromStream, writing recovery, offet = "+offset);
                os.write(buffer, 0, offset);
            }
            if (LOGE) Log.d(TAG, "writeFileFromStream, write revcovery completed");
            renameFile(filePath + SUFFIX, filePath);
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将文件读到输出流
     * @param filePath
     * @return
     */
    public static InputStream readFileToStream(String filePath) {
        if (LOGE) Log.d(TAG, "writeFileFromStream, filePath: "+filePath);
        InputStream result = null;
        try {
            FileInputStream fin = new FileInputStream(filePath);
            result = fin;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 移动件 
     * @param srcFilePath 	源文件完整路径
     * @param dstDirPath 	目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFilePath, String dstDirPath) {
        if (LOGE) Log.d(TAG, "moveFile, moved file from: "+srcFilePath+" to: "+dstDirPath);
        File srcFile = new File(srcFilePath);
        if(!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(dstDirPath);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(dstDirPath + File.separator + srcFile.getName()));
    }
}
