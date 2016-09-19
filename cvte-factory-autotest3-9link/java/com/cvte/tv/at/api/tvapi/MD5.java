package com.cvte.tv.at.api.tvapi;

import com.cvte.tv.at.util.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by User on 2016/1/25.
 */
public class MD5 {

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    public static String val(byte[] bs, int len) {
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messagedigest.update(bs, 0, len);
        String md5 = bufferToHex(messagedigest.digest(), 0, messagedigest.digest().length);
        Utils.LOG("WriteSecurityKey1: md5:" + md5);
        return md5;
    }
}
