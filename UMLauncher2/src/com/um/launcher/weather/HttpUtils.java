package com.um.launcher.weather;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
	// �������Ӳ���
	private static final String TAG = "HttpUtils";

	public static String getContent(String url, Header[] headers,
			NameValuePair[] pairs ) {

		String content = null;
		HttpResult result = null;
		result = HttpClientHelper.get(url, headers, pairs);
		if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
			try {
				//�ڴ����
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
			System.out.println("������: " + conn.getResponseCode());
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

    public static InputStream getInputStream(String url) {
        InputStream inputStream = null;
        HttpURLConnection con = null;
        try {
            URL _url = new URL(url);
            con = (HttpURLConnection) _url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(10 * 1000);
            inputStream = con.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return inputStream;
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

}

