package com.cvte.tv.at.api;

import java.io.File;
import java.io.IOException;

/**
 * Created by User on 2016/3/8.
 */
public class FileFlag {

    public static boolean Delete(String file) {
        File fp = new File(file);
        return fp.delete();
    }

    public static boolean Exist(String file) {
        File fp = new File(file);
        return fp.exists();
    }

    public static boolean Create(String path) {
        try {
            return (new File(path).createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
