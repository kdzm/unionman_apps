package com.um.util;

import android.annotation.SuppressLint;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
	private static final String DEFAULT_CIPHER_ALGORITHM = "DESede";

	@SuppressLint("TrulyRandom")
	public static String encryptMode(String src, String keyString) {
		try {
			SecretKey deskey = new SecretKeySpec(getKeyBytes(keyString), DEFAULT_CIPHER_ALGORITHM);
			Cipher c = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, deskey);
			return bytes2Hex(c.doFinal(src.getBytes()));
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getKeyBytes(String strKey) {
		byte[] bkey24 = new byte[24];
		byte[] bkey = strKey.getBytes();
		int start = bkey.length;
		int i;
		for (i = 0; i < start; i++) {
			bkey24[i] = bkey[i];
		}
		for (i = start; i < 24; i++) {
			bkey24[i] = '0';
		}
		return bkey24;
	}

	private static String bytes2Hex(byte[] src) {
		final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] res = new char[2 * src.length];
		for (int i = 0, j = 0; i < src.length; i++) {
			res[j++] = hexDigits[src[i] >>> 4 & 0x0F];
			res[j++] = hexDigits[src[i] & 0x0F];
		}
		return new String(res);
	}

}
