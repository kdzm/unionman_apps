package com.um.huanauth.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.um.huanauth.util.Util;

import android.util.Log;

public class HttpClientHelper {
	private static String TAG = "HttpClientHelper";
	private static DefaultHttpClient client = null;
	private static final int SO_TIMEOUT = 30000;
	private static final int CONNECT_TIMEOUT = 5000;

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param pairs
	 * @param cookies
	 * @param soTimeout
	 *            ��ȡ��ʱ <=0 ����ΪĬ��30s
	 * @return
	 */
	public static HttpResult get(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies, int soTimeout) {
		
		client = initHttpClient();
		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				soTimeout);
		HttpGet get = new HttpGet();
		client.setRedirectHandler(new DefaultRedirectHandler());
		try {
			// get �������ò���
			if (pairs != null && pairs.length > 0) {
				StringBuilder sb = new StringBuilder("?");
				for (int i = 0; i < pairs.length; i++) {
					if (i > 0) {
						sb.append("&");
					}
					sb.append(String.format("%s=%s", pairs[i].getName(),
							pairs[i].getValue()));
				}
				url = url + sb.toString();
			}
//			Log.d(TAG, "get url=" + url);
			get.setURI(new URI(url));

			// get ���� ���� ͷ
			if (headers != null && headers.length > 0) {
				get.setHeaders(headers);
			}

			// client ���� cookie
			if (cookies != null && cookies.length > 0) {
				BasicCookieStore cookieStore = new BasicCookieStore();
				cookieStore.addCookies(cookies);
				client.setCookieStore(cookieStore);
			} else {
				client.getCookieStore().clear();
			}
			HttpResponse response = client.execute(get);
			return new HttpResult(response, client.getCookieStore());

		} catch (IOException e) {
			//e.printStackTrace();
		} catch (URISyntaxException e) {
			//e.printStackTrace();
		}catch (Exception e) {
			//e.printStackTrace();
		} finally {
			get.abort();
		}
		return null;
	}

	public static HttpResult get(String url, Header[] headers) {
		return get(url, headers, null, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url, Header[] headers,
			NameValuePair[] pairs) {
		return get(url, headers, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url, NameValuePair[] pairs) {
		return get(url, null, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url) {
		return get(url, null, null, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies, int soTimeout) {
//		Log.d(TAG, " post url=" + url);
		client = initHttpClient();
		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				soTimeout);
		HttpPost post = new HttpPost(url);
		try {
			// post �������ò���
			if (pairs != null && pairs.length > 0) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (NameValuePair nameValuePair : pairs) {
					formParams.add(nameValuePair);
					Log.d(TAG,"nameValuePair:getName:"+nameValuePair.getName()+";getValue:"+nameValuePair.getValue());
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, HTTP.UTF_8);
				post.setEntity(entity);
			}
			
			// get ���� ���� ͷ
			if (headers != null && headers.length > 0) {
				post.setHeaders(headers);
			}
			// client ���� cookie
			if (cookies != null && cookies.length > 0) {
				BasicCookieStore cookieStore = new BasicCookieStore();
				cookieStore.addCookies(cookies);
				client.setCookieStore(cookieStore);
			} else {
				client.getCookieStore().clear();
			}
			HttpResponse response = client.execute(post);
			return new HttpResult(response, client.getCookieStore());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.abort();
		}
		return null;
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs, String stringEntity, Cookie[] cookies, int soTimeout) {
//		Log.d(TAG, " post url=" + url);
		client = initHttpClient();
		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				soTimeout);
		HttpPost post = new HttpPost(url);
		try {
			// post �������ò���
			if (pairs != null && pairs.length > 0) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (NameValuePair nameValuePair : pairs) {
					formParams.add(nameValuePair);
					Log.d(TAG,"nameValuePair:getName:"+nameValuePair.getName()+";getValue:"+nameValuePair.getValue());
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, HTTP.UTF_8);
				post.setEntity(entity);
			}
						
			if (stringEntity != null){
				StringEntity entity = new StringEntity(stringEntity);
				post.setEntity(entity);
			}

			// post ���� ���� ͷ
			if (headers != null && headers.length > 0) {
				post.setHeaders(headers);
			}
			// client ���� cookie
			if (cookies != null && cookies.length > 0) {
				BasicCookieStore cookieStore = new BasicCookieStore();
				cookieStore.addCookies(cookies);
				client.setCookieStore(cookieStore);
			} else {
				client.getCookieStore().clear();
			}
			HttpResponse response = client.execute(post);
			return new HttpResult(response, client.getCookieStore());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.abort();
		}
		return null;
	}
	
	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs) {
		return post(url, headers, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies) {
		return post(url, headers, pairs, cookies, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers) {
		return post(url, headers, null, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, NameValuePair[] pairs) {
		return post(url, null, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers, String stringEntity) {
		return post(url, headers, null, stringEntity, null, SO_TIMEOUT);
	}
	
	/**
	 * ʹ�� http net �� HttpURLConnection ��ȡ�ض���
	 * @param url
	 * @param headers
	 * @param pairs
	 * @return
	 */
	private static String getResultRedirecUrl(String url, Header[] headers,
			NameValuePair[] pairs) {
		HttpURLConnection conn = null ;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			if(headers!=null&&headers.length>0){
				for (Header header : headers) {
					conn.setRequestProperty(header.getName(), header.getValue()) ;
				}
			}
			System.out.println("������: " + conn.getResponseCode());
			return conn.getURL().toString() ;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(conn!=null) 
				conn.disconnect();
		}
		
		
		
		
//		client.setRedirectHandler(new DynamicRedirectHandler());
//		HttpGet get = new HttpGet();
//		try {
//			get.setURI(new URI(url));
//			get.setHeaders(headers);
//			HttpResponse reponse = client.execute(get);
//			Header header = reponse.getLastHeader("Location");
//			if (header == null) {
//				return url;
//			} else {
//				String redirecUrl = header.getValue();
//				return getResultRedirecUrl(redirecUrl, headers, pairs);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}catch (Exception e){
//			e.printStackTrace();
//		}
		return url;
	}

	/**
	 * ����httpClientʵ��
	 * 
	 * @return
	 * @throws Exception
	 */
	public static DefaultHttpClient initHttpClient() {
		if (null == client) {
			HttpParams params = new BasicHttpParams();
			// params.setParameter("http.protocol.cookie-policy",
			// CookiePolicy.BROWSER_COMPATIBILITY);
			// ����һЩ��������
			HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// ��ʱ����
			/* �����ӳ���ȡ���ӵĳ�ʱʱ�� */
			ConnManagerParams.setTimeout(params, 2000);
			/* ���ӳ�ʱ */
			HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
			/* ����ʱ */
			HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
			// �������ǵ�HttpClient֧��HTTP��HTTPS����ģʽ
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			try{
			    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
			    trustStore.load(null, null); 
				SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				schReg.register(new Scheme("https", sf, 443));
			}catch(Exception e){
				e.printStackTrace();
			}

			// ʹ���̰߳�ȫ�����ӹ���������HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			client = new DefaultHttpClient(conMgr, params);
			//client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, true)) ;
		}
		return client;
	}

	/**
	 * Map�������͵Ĳ���תNameValuePair ����
	 * 
	 * @param params
	 * @return
	 */
	public static NameValuePair[] mapToPairs(Map<String, String> params) {
		Set<String> keySet = params.keySet();
		if (keySet == null || keySet.size() == 0) {
			return null;
		}
		String[] keyArray = keySet.toArray(new String[0]);
		NameValuePair[] pairs = new NameValuePair[keyArray.length];
		for (int i = 0; i < keyArray.length; i++) {
			pairs[i] = new BasicNameValuePair(keyArray[i],
					params.get(keyArray[i]));
		}
		return pairs;
	}

	/**
	 * �ֶ��ض���
	 * 
	 * @author shenhui
	 * 
	 */
	static class DynamicRedirectHandler extends DefaultRedirectHandler {
		@Override
		public boolean isRedirectRequested(HttpResponse response,
				HttpContext context) {
			return false;
		}
	}

	public static class SSLSocketFactoryEx extends SSLSocketFactory {      
	      
	    SSLContext sslContext = SSLContext.getInstance("TLS");      
	      
	    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,      
	            KeyStoreException, UnrecoverableKeyException {      
	        
	    	super(truststore);      
	      
	        TrustManager tm = new X509TrustManager(){
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            	return null;
	            }      
	      
	            @Override      
	            public void checkClientTrusted(      
	                java.security.cert.X509Certificate[] chain, String authType)      
	                throws java.security.cert.CertificateException {      
	      
	            }      
	      
	            @Override      
	            public void checkServerTrusted(      
	                    java.security.cert.X509Certificate[] chain, String authType)      
	                    throws java.security.cert.CertificateException {      
	      
	            }      
	        };      
	      
	        sslContext.init(null, new TrustManager[] { tm }, null);      
	    }
	    
	    @Override      
	    public Socket createSocket(Socket socket, String host, int port,      
	            boolean autoClose) throws IOException, UnknownHostException {      
	        return sslContext.getSocketFactory().createSocket(socket, host, port,      
	                autoClose);      
	    }      
	      
	    @Override      
	    public Socket createSocket() throws IOException {      
	        return sslContext.getSocketFactory().createSocket();      
	    }      
	}
}

