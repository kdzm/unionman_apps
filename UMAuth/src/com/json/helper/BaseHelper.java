package com.json.helper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Http请求基类
 * 
 * @author UMAuthHW
 */
public class BaseHelper {
	private Boolean isGet = true;
	private String mUrl = null;
	public Context mContext;
	private DataCompleteListener mDataCompleteListener = null;

	public BaseHelper(String url,Context context) {
		mUrl = url.replaceAll(" ", "%20");
		mContext=context;
		this.isGet = true;
	}

	public BaseHelper(String url, Boolean isGet,Context context) {
		mUrl = url.replaceAll(" ", "%20");
		this.isGet = isGet;
		mContext=context;
	}

	public void setUrl(String url) {
		mUrl = url.replaceAll(" ", "%20");
		JsonHelperLog.d(this, url);
	}

	public void setDataCompleteListener(DataCompleteListener listener) {
		mDataCompleteListener = listener;
		new AsyncTask<String, Integer, HttpEntity>() {

			@Override
			protected HttpEntity doInBackground(String... params) {
				HttpEntity entity = null;
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
				HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
				//HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
				try {
					HttpResponse response;
					String testUri = "http://www.baidu.com";
					String param = "/EDS/jsp/Authentication/Action=Login&UserID=123456&return_type=1";
					
					if (isGet) {
						HttpGet http = new HttpGet(mUrl);
						response = new DefaultHttpClient().execute(http);
					} else {
						
						//mUrl = "http://www.baidu.com/EPG/oauth/v2/token!grant_type=refresh_token&refresh_token=";
						String[] url = mUrl.split("\\?");
						JsonHelperLog.d(this, "mUrl = " + mUrl);
						if (url.length < 2){
							String[] url2 = mUrl.split("\\!");
							if(url2.length < 2){
								return null;
							}
							Log.d("UMAuthHW BaseHelper", "url2[0] = " + url2[0].toString() + " url2[1] = " 
									+ url2[1].toString());
							String data = url2[1].toString();
							HttpPost httpRequest = new HttpPost(url2[0]);
							//HttpEntity reqEntity = new StringEntity(url2[1]);
							//httpRequest.setEntity(reqEntity);
							httpRequest.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
							//httpRequest.setHeader(HTTP.CONTENT_LEN, data.length() + "");
							httpRequest.addHeader("Data", data);
					

							// httpRequest.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
							response = new DefaultHttpClient(httpParams).execute(httpRequest);
							// response = new DefaultHttpClient().execute(httpRequest);
							Log.d("UMAuthHW BaseHelper", "refresh token response code is " 
									+ response.getStatusLine().getStatusCode());
							return null;
						}
						HttpPost httpRequest = new HttpPost(url[0]);
						StringEntity reqEntity = new StringEntity(url[1]);
						Log.d("UMAuthHW BaseHelper", "url[0] = " + url[0].toString() + " url[1] = " 
								+ url[1].toString());
						reqEntity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
						// httpRequest.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
						httpRequest.setEntity(reqEntity);
						response = new DefaultHttpClient(httpParams).execute(httpRequest);
						// response = new DefaultHttpClient().execute(httpRequest);
						Log.d("UMAuthHW BaseHelper", "refresh token response code is " 
								+ response.getStatusLine().getStatusCode());
					}
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						entity = response.getEntity();
						JsonHelperLog.d(this, "HTTP/1.1 200 OK");
						
						Header[] tHeader = response.getHeaders("epgurl");
						if(tHeader != null){
							Log.d("UMAuthHW BaseHelper", "headr = " + tHeader[0].toString());
						}else{
							Log.d("UMAuthHW BaseHelper", "no needle found");
						}
						// result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
					}else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY){
						JsonHelperLog.e(this, "HTTP/1.1 302");
						Header[] tHeader = response.getHeaders("Location");
						if(tHeader != null){
							String newLocation = tHeader[0].toString();
							String a[] = newLocation.split(":"); 
							String newUrl = null;
							if(a != null){
					        	Log.d("UMAuthHW BaseHelper", "a.length = " + a.length);
					        	for(int k = 0; k < a.length; k++){
					        		Log.d("UMAuthHW BaseHelper", "a[" + k + "] = " + a[k]);
					        	}
					        	
					        	if(a.length > 1){
					        		newUrl = a[1];
					        	}
							}else{
								return entity;
							}
							
							Log.d("UMAuthHW BaseHelper", "headr = " + newUrl);
						
							HttpGet tHttp = new HttpGet(newUrl);
							HttpResponse tResponse = new DefaultHttpClient().execute(tHttp);
							
							if(tResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
								JsonHelperLog.d(this, "HTTP/1.1 200 OK");
							}else if(tResponse.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST){
								JsonHelperLog.e(this, "HTTP/1.1 400 Bad Request");
							} 
							entity = tResponse.getEntity();
						}
						
						
					}else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
						entity = response.getEntity();
						JsonHelperLog.e(this, "HTTP/1.1 400 Bad Request");
					}
				} catch (ClientProtocolException e) {
					JsonHelperLog.e(this, "ClientProtocolException");
				} catch (IOException e) {
					JsonHelperLog.e(this, "IOException");
				} catch (Exception e) {
					JsonHelperLog.e(this, "Exception");
				}
				return entity;
			}

			@Override
			protected void onPostExecute(HttpEntity result) {
				super.onPostExecute(result);
				mDataCompleteListener.dataCompleteListener(result);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
		}.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
	}
}
