package com.um.huanauth.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		if (result != null){
			Log.d(TAG,"result.getStatuCode():"+result.getStatuCode());
		}
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

	public static String getContentWithPostMethor(String url, Header[] headers,
			NameValuePair[] pairs ) {

		String content = null;
		HttpResult result = null;
		try{
			result = HttpClientHelper.post(url, headers, pairs);
			if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
				try {
					//内存溢出
					content = result.getHtml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		Log.d(TAG, "content= " + content);
		return content;
	}
	
	public static String getContentWithPostMethor(String url, Header[] headers,
			String stringEntity) {
		
		String content = null;
		HttpResult result = null;
		try{
			result = HttpClientHelper.post(url, headers, stringEntity);
			if (result != null){
				Log.d(TAG,"result.getStatuCode():"+result.getStatuCode());
			}
			
			if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
				try {
					//内存溢出
					content = result.getHtml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		Log.d(TAG, "content= " + content);
		return content;
	}
	
	public static String getContentWithPostMethor(String url, Header[] headers) {

		String content = null;
		HttpResult result = null;
		try{
			result = HttpClientHelper.post(url, headers);
			if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
				try {
					//内存溢出
					content = result.getHtml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
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

	public static byte[] getBinaryWithMethor(String url, Header[] headers,
			NameValuePair[] pairs) {
		// if(!isNetworkAvailable(context)){
		// return null ;
		// }
		byte[] binary = null;
		HttpResult result = HttpClientHelper.post(url, headers, pairs, null, 0);
		if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
			binary = result.getResponse();
		}
		// Log.d(TAG, "binary= " + binary);
		return binary;
	}

	 static public void getPic(){
	    	Log.d(TAG,"getPic");
	    	
	    	//String json = HttpUtils.getContent("http://lexueinterface.huan.tv/syncbusiness_getJiuLianData.ws", null, null);
	    	String json = HttpUtils.getContent("http://118.194.161.40:8080/gameservice/gameV3Portal/getPostersData?posterType=unionman", null, null);
	    	Log.d(TAG,"getPic json:"+json);
			String imageDataUrl = null;
			if (json != null){
				try{
					JSONObject jsonObject = new JSONObject(json);
					JSONArray jsonArray = jsonObject.getJSONArray("result");
					int i = 0, length = jsonArray.length();
					Log.d(TAG,"getPic length:"+length);
					for (i = 0; i < length; i++){
						JSONObject jsonPicObject = jsonArray.getJSONObject(i);
						imageDataUrl = jsonPicObject.getString("imageDataUrl");
					}
					Log.d(TAG,"jsonObject.toString:"+jsonObject.toString());
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
			
			if (imageDataUrl != null){
				byte b[] = HttpUtils.getBinary(imageDataUrl, null, null);
				File file = new File("/data/huan/","333.jpg");
				
				try{
					FileOutputStream stream = new FileOutputStream(file);
					stream.write(b);
					stream.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
	    }
		
		static public void getPic1(){
	    	Log.d(TAG,"getPic1");
	    	
			BasicHeader basicHeader1 = new BasicHeader("Content-Type", "application/json");
			Header[] header = {basicHeader1};
	    	String json = HttpUtils.getContentWithPostMethor("http://118.194.161.122/service/appstore/huanwang/getJiuLianRemdApps?clienttype=UNION-CN-HISI80-UW8000-2D&topx=3", header);
	    	//String json = HttpUtils.getContent("http://118.194.161.40:8080/gameservice/gameV3Portal/getPostersData?posterType=unionman", null, null);
	    	Log.d(TAG,"getPic1 json:"+json);
	    	String name = "";
			String imageDataUrl = null;
			String appkey = "";
			String type = "";
			String code = "";
			String message = "";
			
			if (json != null){
				try{
					JSONObject jsonObject = new JSONObject(json);
					JSONArray jsonArray = jsonObject.getJSONArray("result");
					int i = 0, length = jsonArray.length();
					Log.d(TAG,"getPic length:"+length);
					for (i = 0; i < length; i++){
						JSONObject jsonPicObject = jsonArray.getJSONObject(i);
						name = jsonPicObject.getString("name");
						imageDataUrl = jsonPicObject.getString("imageDataUrl");
						appkey = jsonPicObject.getString("appkey");
						type = jsonPicObject.getString("type");
					}
					
					code = jsonObject.getString("code");
					message = jsonObject.getString("message");
					
					Log.d(TAG,"jsonObject.toString:"+jsonObject.toString());
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
			
			if (imageDataUrl != null){
				byte b[] = HttpUtils.getBinary(imageDataUrl, null, null);
				File file = new File("/data/huan/","444.jpg");
				
				try{
					FileOutputStream stream = new FileOutputStream(file);
					stream.write(b);
					stream.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
	    }
		
}

