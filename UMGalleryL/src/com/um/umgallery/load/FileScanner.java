
package com.um.umgallery.load;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.um.umgallery.control.ExplorerController;

public class FileScanner implements Runnable {
    private List<String> mFileList = new ArrayList<String>();
    private Handler mHandler;
    private String mPath;

    public FileScanner(Handler handler, String path) {
        mHandler = handler;
        mPath = path;
    }

    @Override
    public void run() {
        getPictureList(mPath);
        Message message = new Message();
        if (mFileList.size() >= 0) {
            message.what = ExplorerController.SCAN_FINISH;
            message.obj = mFileList;
            mHandler.sendMessage(message);
        } else {
            mHandler.sendEmptyMessage(ExplorerController.SCAN_FAILED);
        }
        //getPictureList(mPath);
    }

    private List<String> getPictureList(String path) {
        if (path != null && !"".equals(path)) {
            File root = new File(path);
            File[] files = root.listFiles(new MyFileExplore());

            if (files == null || files.length <= 0)
                return mFileList;

            List<String> fileList = new ArrayList<String>();
            List<String> dirList = new ArrayList<String>();

            for (File file : files) {
                if (file.isDirectory()) {
                    //dirList.add(file.getAbsolutePath());
                } else {
                    fileList.add(file.getAbsolutePath());
                    if (fileList.size() % 10 == 0) {
                        mHandler.sendEmptyMessage(ExplorerController.UPDATE_LIST);
                    }
                }
            }

            Collections.sort(fileList, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareToIgnoreCase(rhs);
                }
            });

            mFileList.addAll(fileList);

            for (String file : dirList) {
                getPictureList(file);
            }
        }
        return mFileList;
    }

    class MyFileExplore implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            try {
                if (!pathname.isDirectory()) {
                    String path = pathname.getPath();
                    String type = path.substring(path.lastIndexOf("."));
                    return type.equalsIgnoreCase(".jpg") || type.equalsIgnoreCase(".jpeg")
                            || type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".gif")
                            || type.equalsIgnoreCase(".png");
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
