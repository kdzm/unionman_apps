package com.um.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.util.Log;

public class UserToken {
	
	private static final String TAG = "com.unionman.gettoken-----UserToken-----";
	private String strUserURL = "http://183.235.21.100:8090/iptvepg/platform/index.jsp";
	
	public void getUserToken(){
		Log.i(TAG, "getUserToken()-------");
		
		try {
			URL encryUrl = new URL(strUserURL );
		//	URL testUrl = new URL("http://www.baidu.com");
		//	URL encryUrl = new URL(null, strEncryURL+strEncryParam, new net.ssl.internal.www.protocol.https.Handler() );
			Log.i(TAG, "EncryToken connect https service----");
			HttpsURLConnection connection = (HttpsURLConnection)encryUrl.openConnection();
		//	HttpsURLConnection connection = (HttpsURLConnection)testUrl.openConnection();
		//	connection.setSSLSocketFactory(context.getSocketFactory());
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setDoInput(true);
			
			Log.i(TAG, "judege if encryToken connect https service success ----");
			StringBuffer encryBuffer = new StringBuffer();
			if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
				Log.i(TAG, "connect service success-----");
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				Log.i(TAG, "get message from service-----");
				String line ;
				StringBuffer sb = new StringBuffer();
				while((line = reader.readLine()) != null){
					
					sb.append(line);
				}
				encryBuffer = sb;
				
			}else{
				Log.i(TAG, "connect service fail-----");
			}
			Log.i(TAG, "http get back is --------- "+encryBuffer.toString());
			Log.i(TAG, "http get responseCode------------  "+connection.getResponseCode());
			Log.i(TAG, "http message is ------------  "+connection.getResponseMessage());
			Log.i(TAG, "http getContentType is------------  "+connection.getContentType());
			Log.i(TAG, "http getContentLength is------------  "+connection.getContentLength());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
