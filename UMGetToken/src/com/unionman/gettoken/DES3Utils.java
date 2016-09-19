package com.unionman.gettoken;

import java.io.UnsupportedEncodingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Log;

public class DES3Utils {
	
	private static final String TAG = "com.unionman.gettoken-------";
	
	private static final String DEFAULT_CIPHER_ALGORITHM = "DESede";
	
	@SuppressLint("TrulyRandom")
	public static byte[] encryptMode(byte[] src,String keyString) {
		Log.i(TAG, "encryptMode()------");
		try {
			SecretKey deskey = new SecretKeySpec(build3DESKey(keyString), DEFAULT_CIPHER_ALGORITHM);
			Log.i(TAG, "deskey()------"+deskey);
			Cipher c1 = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM); 
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	

	
public static byte[] build3DESKey(String keystr) throws UnsupportedEncodingException{
	Log.i(TAG, "build3DESKey------------ ");
		byte[] key = {(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',
				(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',
				(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0'};
		byte[] temp = keystr.getBytes();
		if(key.length > temp.length){
			System.arraycopy(temp, 0, key, 0, temp.length);			
		}else{
			System.arraycopy(temp, 0, key, 0, key.length);			
		}
		Log.i(TAG, "key2------------ "+key);
		return key;
	}	
	
	
}
