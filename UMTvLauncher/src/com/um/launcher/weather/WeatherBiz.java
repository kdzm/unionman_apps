package com.um.launcher.weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.um.launcher.weather.CityWeatherInfoBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WeatherBiz {

	private static final String dbName = "db_weather.db";
	private static final String dir = "/data/data/com.um.launcher/databases/";

	public static boolean copyWetherData(Context context) {
		boolean ret = true;
		byte[] buf = new byte[30720]; // 30k
		try {
			File dirFile = new File(dir);
			//if(!(dirFile.exists()))
			{
				if(!(dirFile.exists())){
					dirFile.mkdirs();
				}
				
				File file = context.getDatabasePath(dbName);
				if(!(file.exists()))
				{
					InputStream is = context.getAssets().open(dbName);// 得到数据库文件的数据流
					FileOutputStream os = new FileOutputStream(file);// 得到数据库文件的写入流
					int count = -1;
					while ((count = is.read(buf)) != -1) {
						os.write(buf, 0, count);
					}
					os.flush();
					is.close();
					os.close();
				}
			//	Log.i("HomeMainActivity", "file:" + file.toString());
				System.out.println("copy sucess");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			buf = null;
		}
		
		return ret;
	}

	public static final String DEFAULT_CITYCODE = "101010100"; // 默认北京

	/**
	 * 获取默认的城市编号 先取偏好设置，有设置直接返回，如果没有则自动粗略定位，定位失败初始为北京
	 * 
	 * @param context
	 * @return
	 */
	public static String getCityCode(final Context context) {
		if (context == null){
			return DEFAULT_CITYCODE;
		}
		
		SharedPreferences sp = context.getSharedPreferences("settingSPF",
				Context.MODE_PRIVATE);
		String cityCode = sp.getString("weather_city_code", "0");
		if (cityCode.equals("0")) {
			String cityCodeAuto = getRoughlyLocation(context);
			Log.i("HomeMainActivity", "cityCodeAuto:" + cityCodeAuto);
			if (cityCodeAuto != null) {
				return cityCodeAuto;
			} else {
				return DEFAULT_CITYCODE;
			}
		} else {
			return cityCode;
		}
	}

	public static CityWeatherInfoBean getWeatherFromHttp(String cityCode) {
		//String url = "http://www.weather.com.cn/data/cityinfo/" + cityCode
				//+ ".html";
		String url = "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode="+cityCode+"&weatherType=0";
		String json = HttpUtils.getContent(url, null, null);
		if (json != null) {
			Log.i("HomeMainActivity", "json:" + json);
			try {
				CityWeatherInfoBean bean = new CityWeatherInfoBean();
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonInfro = jsonObject.getJSONObject("weatherinfo");
				bean.setCityId(jsonInfro.getString("cityid"));
				bean.setCityName(jsonInfro.getString("city"));
				bean.setfTemp(jsonInfro.getString("temp1"));
				bean.settTemp(jsonInfro.getString("temp2"));
				bean.setDnstr(jsonInfro.getString("img1"));
				bean.setWeatherInfo(jsonInfro.getString("weather1"));
				return bean;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	    Log.i("HomeMainActivity", "json: null");
		return null;
	}

	/**
	 * 获取当前的粗略位置
	 * 
	 * @return
	 */
	public static String getRoughlyLocation(Context context) {
		String location[] = new String[4];
		SQLiteDatabase weatherDb;
		String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
		String json = HttpUtils.getContent(url, null, null);
		if (json == null) {
			return null;
		}
//		System.out.println(json);
		try {
			JSONObject object = new JSONObject(json);
			location[0] = object.getString("country");// 国
			location[1] = object.getString("province");// 省
			location[2] = object.getString("city");// 市
			location[3] = object.getString("district");// 区
			copyWetherData(context);

			// 将获取到的位置转化为城市编码
			weatherDb = SQLiteDatabase.openDatabase(
					context.getDatabasePath("db_weather.db").toString(), null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;// 如果JSON解析出错直接返回空
		} catch (Exception e) {
			e.printStackTrace();
			boolean ret = copyWetherData(context);
			if (ret == false){
				return null;
			}
			weatherDb = SQLiteDatabase.openDatabase(
					context.getDatabasePath("db_weather.db").toString(), null,
					SQLiteDatabase.OPEN_READONLY);
			if (weatherDb != null){
				return null;
			}
		}
		// 查询城市编号
		Cursor cursor = null;
		if (location[3] != null && !location[3].equals("")) {
			cursor = weatherDb.query("citys", new String[] { "city_num" },
					"name=?", new String[] { location[2] + "." + location[3] },
					null, null, null);
		} else {
			cursor = weatherDb.query("citys", new String[] { "city_num" },
					"name=?", new String[] { location[2] }, null, null, null);
		}
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			String citycode = cursor.getString(cursor
					.getColumnIndex("city_num"));
			cursor.close();
			weatherDb.close();
			return citycode;
		} else {
			return null;
		}
	}

}

