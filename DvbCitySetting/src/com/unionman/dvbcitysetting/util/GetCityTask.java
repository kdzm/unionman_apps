package com.unionman.dvbcitysetting.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Administrator on 2014/11/13.
 */
public class GetCityTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private GetCityListener getCityListener;
    public GetCityTask(Context context, GetCityListener getCityListener) {
        this.context = context;
        this.getCityListener = getCityListener;
    }

    public String doInBackground(Void[] paramArrayOfVoid) {
        return HttpGetUncheck("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js");
    }

    public void onPostExecute(String paramString) {
        String str = "";
        if (!StringUtils.isBlank(paramString))
            str = paramString.substring(paramString.indexOf("{"), 1 + paramString.lastIndexOf("}"));
        try {
            JSONObject localJSONObject = new JSONObject(str);

            Log.d("ip_area_by_sina: ", "" + localJSONObject.getString("city"));
            Log.d("ip_area_by_sina: ", "" + localJSONObject.getString("country"));
            Log.d("ip_area_by_sina: ", "" + localJSONObject.getString("province"));

            if (getCityListener != null) {
                getCityListener.onGetDone(localJSONObject.getString("province"),localJSONObject.getString("city"),"");
            }
            PreferencesUtils.putString(context, "my_local_city", localJSONObject.getString("city"));
        } catch (JSONException localJSONException) {
            if (getCityListener != null) {
                getCityListener.onGetDone("", "", "");
            }
            localJSONException.printStackTrace();
        }
    }

    public static String HttpGetUncheck(String paramString) {
        return HttpGet(paramString);
    }

    private static String HttpGet(String paramString) {
        StringBuffer localStringBuffer = new StringBuffer("");
        try {
            HttpURLConnection localHttpURLConnection = (HttpURLConnection) new URL(paramString.replace(" ", "")).openConnection();
            localHttpURLConnection.setConnectTimeout(4000);
            localHttpURLConnection.setReadTimeout(2000);
            localHttpURLConnection.connect();
            InputStream localInputStream = localHttpURLConnection.getInputStream();
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(localInputStream), "utf-8"));
            while (true) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    localInputStream.close();
                    localHttpURLConnection.disconnect();
                    return localStringBuffer.toString();
                }
                localStringBuffer.append(str + "\r\n");
            }
        } catch (SocketTimeoutException localSocketTimeoutException) {
            localSocketTimeoutException.printStackTrace();
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        }
        return "";
    }

    public interface GetCityListener {
        public void onGetDone(String province, String state, String city);
    }
}
