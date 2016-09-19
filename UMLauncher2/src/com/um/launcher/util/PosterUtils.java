package com.um.launcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.um.launcher.MyApplication;
import com.um.launcher.data.PosterInfo;
import com.um.launcher.weather.HttpUtils;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjian on 2015/3/31.
 */
public class PosterUtils {
    private OnGetImageListener mOnGetImageListener = null;

    static public List<PosterInfo> getPosterInfo(String baseUrl){
        LogUtils.d("getPic1");
        List<PosterInfo> posterInfos = new ArrayList<PosterInfo>();
        BasicHeader basicHeader1 = new BasicHeader("Content-Type", "application/json");
        Header[] header = {basicHeader1};
        String json = HttpUtils.getContentWithPostMethor(baseUrl, header);
        // String json = HttpUtils.getContentWithPostMethor("http://118.194.161.122/service/appstore/huanwang/getJiuLianRemdApps?clienttype=UNION-CN-HISI80-UW8000-2D&topx=3", header);
        // String json = HttpUtils.getContent("http://118.194.161.40:8080/gameservice/gameV3Portal/getPostersData?posterType=unionman", null, null);
        LogUtils.d("baseUrl json: "+ baseUrl);
        LogUtils.d("getPic1 json: "+ json);
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
                LogUtils.d("getPic length:"+length);
                for (i = 0; i < length; i++){
                    JSONObject jsonPicObject = jsonArray.getJSONObject(i);
                    name = jsonPicObject.getString("name");
                    imageDataUrl = jsonPicObject.getString("imageDataUrl");
                    appkey = jsonPicObject.getString("appkey");
                    type = jsonPicObject.getString("type");
                    posterInfos.add(new PosterInfo(imageDataUrl, appkey, type, name));
                }

                code = jsonObject.getString("code");
                message = jsonObject.getString("message");

                LogUtils.d("jsonObject.toString: "+jsonObject.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        return posterInfos;
    }

    public static List<PosterInfo> getAppPosterInfo(String baseUrl) {
        List<PosterInfo> posterInfos = new ArrayList<PosterInfo>();
        String json = HttpUtils.getContent(baseUrl, null, null);
        String imageDataUrl = null;

        LogUtils.d("baseUrl json: "+ baseUrl);
        LogUtils.d("getPic1 json: "+ json);
        if (json != null){
            try{
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                int i = 0, length = jsonArray.length();
                LogUtils.d("getPic length:"+length);
                for (i = 0; i < length; i++){
                    JSONObject jsonPicObject = jsonArray.getJSONObject(i);
                    imageDataUrl = jsonPicObject.getString("imageDataUrl");
                    posterInfos.add(new PosterInfo(imageDataUrl, "", "", ""));
                }
                LogUtils.d("jsonObject.toString:"+jsonObject.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        return posterInfos;
    }

    public void getPic(String url){
        LogUtils.d("getPic");

        String json = HttpUtils.getContent(url, null, null);
        //String json = HttpUtils.getContent("http://118.194.161.40:8080/gameservice/gameV3Portal/getPostersData?posterType=unionman", null, null);
        LogUtils.d("getPic json:" + json);
        String imageDataUrl = null;
        if (json != null){
            try{
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                int i = 0, length = jsonArray.length();
                LogUtils.d("getPic length:"+length);
                for (i = 0; i < length; i++){
                    JSONObject jsonPicObject = jsonArray.getJSONObject(i);
                    imageDataUrl = jsonPicObject.getString("imageDataUrl");
                }
                LogUtils.d("jsonObject.toString:"+jsonObject.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        if (imageDataUrl != null){
            byte b[] = HttpUtils.getBinary(imageDataUrl, null, null);
            String cachePath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();
            File file = new File(cachePath, hashKeyForDisk(url));

            try{
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(b);
                stream.close();
            }catch(IOException e){
                e.printStackTrace();
            }

        }
    }

    private Bitmap getBitmap (String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            getPic(url);
            bitmap = getBitmapFromCache(url);
        }
        return bitmap;
    }

    private Bitmap getBitmapFromCache(String url) {
        String path = MyApplication.getAppContext().getCacheDir().getAbsolutePath() + hashKeyForDisk(url);
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = null;
        if (in != null) {
            bitmap = BitmapFactory.decodeStream(in);
        }
        return bitmap;
    }

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void loadImage(String url, OnGetImageListener onGetImageListener) {
        mOnGetImageListener = onGetImageListener;
        new GetImageTask().execute(url);
    }

    class GetImageTask extends AsyncTask<String, Integer, Bitmap> {
        String mUrl = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) {
                if (mOnGetImageListener != null) {
                    mOnGetImageListener.onGetFaile(mUrl);
                }
            } else {
                if (mOnGetImageListener != null) {
                    mOnGetImageListener.onGetSuccess(bitmap, mUrl);
                }
            }
        }
    }

    public void setOnGetImageListener(OnGetImageListener onGetImageListener) {
        mOnGetImageListener = onGetImageListener;
    }

    public interface OnGetImageListener{
        public void onGetSuccess(Bitmap bitmap, String url);
        public void onGetFaile(String url);
    }
}
