package com.um.launcher.weather;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import android.content.Context;
import android.util.Log;

public class HttpUtils {
	// 网络连接部分
	private static final String TAG = "HttpUtils";

	public static String getContent(String url, Header[] headers,
			NameValuePair[] pairs ) {

		String content = null;
		HttpResult result = null;
		result = HttpClientHelper.get(url, headers, pairs);
		if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
			try {
				//内存溢出
				content = result.getHtml();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d(TAG, "content= " + content);
		return content;
	}


	public static String getResultRedirecUrl(String url, Header[] headers,
			NameValuePair[] pairs) {
		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			if (headers != null && headers.length > 0) {
				for (Header header : headers) {
					conn.setRequestProperty(header.getName(), header.getValue());
				}
			}
			System.out.println("返回码: " + conn.getResponseCode());
			return conn.getURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return url;
	}

	public static byte[] getBinary(String url, Header[] headers,
			NameValuePair[] pairs) {
		// if(!isNetworkAvailable(context)){
		// return null ;
		// }
		byte[] binary = null;
		HttpResult result = HttpClientHelper.get(url, headers, pairs, null, 0);
		if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
			binary = result.getResponse();
		}
		// Log.d(TAG, "binary= " + binary);
		return binary;
	}

}

