package com.um.upgrade.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 解压Zip文件工具类
 * Created by ziliang.nong on 14-8-15.
 */
public class ZipUtil {
    private final static String TAG = ZipUtil.class.getSimpleName()+"----U668";
    private final static boolean LOGE = true;

    private ZipUtil() {
    }

    /**
     * 解压Zip文件到文件夹
     * @param srcZipFilePath
     * @param dstFolderPath
     * @throws Exception
     */
    public static void unZipFolder(String srcZipFilePath, String dstFolderPath) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(srcZipFilePath));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(dstFolderPath + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(dstFolderPath + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     * @throws Exception
     */
    public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
        if (LOGE) Log.d(TAG, "unziping..., zipFile path: "+zipFile.getAbsolutePath());
        ZipFile zfile=new ZipFile(zipFile);
        Enumeration zList=zfile.entries();
        ZipEntry ze=null;
        byte[] buf=new byte[1024];
        while(zList.hasMoreElements()){
            ze=(ZipEntry)zList.nextElement();
            if(ze.isDirectory()){
                if (LOGE) Log.d(TAG, "directory, unziping: " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = "+dirstr);
                File f=new File(dirstr);
                f.mkdir();
                continue;
            }
            String name = ze.getName();
            if (LOGE) Log.d(TAG, "file, unziping: "+name);
            OutputStream os;
            //debug
//            if (!(name.contains("recovery")||name.contains("loaderdb"))) {
//                continue;
//            }
            if (name.contains("/")) {
                os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, name)));
            } else {
                os = new BufferedOutputStream(new FileOutputStream(new File(folderPath+name)));
            }
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        if (LOGE) Log.d(TAG, "finish");
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir 指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName){
        String[] dirs=absFileName.split("/");
        File ret=new File(baseDir);
        String substr = null;
        if(dirs.length>1){
            for (int i = 0; i < dirs.length-1;i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret=new File(ret, substr);

            }
            Log.d("upZipFile", "1ret = "+ret);
            if(!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length-1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret=new File(ret, substr);
            Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        return ret;
    }
}
