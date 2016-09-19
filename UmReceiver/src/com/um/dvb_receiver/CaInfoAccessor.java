package com.um.dvb_receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class CaInfoAccessor {
	private static final String TAG = "CaInfoAccessor";
	private ContentResolver mCr = null;

	private static final Uri CONTENT_URI = Uri
			.parse("content://com.uninoman.caprovider/grobal");

	public static final String NAME = "name";
	public static final String VALUE = "value";
	private static Date mFromDate = null;

	public CaInfoAccessor(ContentResolver cr) {
		mCr = cr;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			mFromDate = formatter.parse("2000-01-01 00:00:00");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	public String getCaInfo(String name, String args, boolean fromCa) {
		if (name == null) {
			Log.v("xinhua","get getCaInfo null name  :"+name);
			return null;
		}
		Log.v("xinhua","welcome to getCaInfo :");
		Uri uri = Uri.withAppendedPath(CONTENT_URI, getStorageName(name, args)
				+ (fromCa ? "/raw" : ""));

		Cursor cursor = mCr.query(uri, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			String value = cursor.getString(cursor.getColumnIndex(VALUE));
			return value;
		}

		return null;
	}

	public Uri getUriFor(String name, String args) {
		String storageName = getStorageName(name, args);
		if (storageName == null) {
			Log.v(TAG, "invalid CaData Object.");
			return null;
		}

		return Uri.withAppendedPath(CONTENT_URI, storageName);
	}

	private static String getStorageName(String name, String args) {
		if (args == null) {
			return name;
		} else {
			return name + ":" + args;
		}
	}

	public boolean getCardStatus() {
		boolean insert = false;
		String status = getCaInfo("card_status", null, true);
		if (status != null) {
			try {
				JSONObject json = new JSONObject(status);
				insert = json.getBoolean("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return insert;
	}

	public class EntitleInfo {
		public int productId;
		public int expiredTime;
		public int year ;
		public int month ;
		public int day ;
		public int hour ;
		public int min ;
		public int second ;

		public EntitleInfo(int productId, int  expiredTime ,int year,int month,int day,int hour,int min,int second) {
			this.productId = productId;
			this.expiredTime = expiredTime;
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.min = min;
			this.second = second ;
		}
	}

	public ArrayList<EntitleInfo> getEntitles() {
		int operId = -1;
		String operators = getCaInfo("operators", null, false);
		if (operators == null) {
			Log.v("xinhua","get operator fail ors :"+operators);
			return null;
		}
		try {
			JSONObject json = new JSONObject(operators);
			if (json != null) {
				JSONArray arr = json.getJSONArray("value");
				if (arr != null && arr.length() > 0) {
					operId = arr.getInt(0);
					Log.v("xinhua","ca_info entitle.operId :"+operId);
				}
			}
			if (operId != -1) {
				String entitles = getCaInfo("entitles", "{\"operator_id\":"
						+ operId + "}", false);
				if (entitles == null) {
					Log.v("xinhua","ca_info entitl fail :");
					return null;
				}
				JSONObject entitlesJsn = new JSONObject(entitles);
				JSONArray jsonArr = entitlesJsn.getJSONArray("value");

				ArrayList<EntitleInfo> result = new ArrayList<EntitleInfo>();
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject js = jsonArr.getJSONObject(i);
					int productId = js.getInt("product_id");
					int days = js.getInt("end_date");
					int expireTime = js.getInt("end_time");
					int expireTime_date = js.getInt("end_date");
					int year = js.getInt("year");
					int month = js.getInt("month");
					int day = js.getInt("day");
					int hour = js.getInt("hour");
					int min = js.getInt("min");
					int second = js.getInt("second");
					//Date expireTime = new Date();
					//expireTime.setTime(mFromDate.getTime() + (long) days * 24L
					//		* 60 * 60 * 1000);
					EntitleInfo entitle = new EntitleInfo(productId, expireTime,year,month,day,hour,min,second);
					result.add(entitle);
				}
				return result;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
