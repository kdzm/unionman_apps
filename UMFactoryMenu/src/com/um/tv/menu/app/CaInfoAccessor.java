package com.um.tv.menu.app;

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
			return null;
		}

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
		Log.v(TAG, "getCardStatus status = " + status);
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
	
	public String getCardNO() {
		String cardNO = "";
		String status = getCaInfo("card_no", null, true);
		Log.v(TAG, "getCardNO status = " + status);
		
		if (status != null) {
			try {
				JSONObject json = new JSONObject(status);
				cardNO = json.getString("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return cardNO;
	}

	public boolean checkCardProtocalT0() {
		boolean isT0 = false;
		String status = getCaInfo("card_protocal_T0", null, true);
		Log.v(TAG, "checkCardProtocalT0 status = " + status);
		if (status != null) {
			try {
				JSONObject json = new JSONObject(status);
				isT0 = json.getBoolean("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return isT0;
	}

	public class EntitleInfo {
		public int productId;
		public Date expiredTime;

		public EntitleInfo(int productId, Date expiredTime) {
			this.productId = productId;
			this.expiredTime = expiredTime;
		}
	}

	public ArrayList<EntitleInfo> getEntitles() {
		int operId = -1;
		String operators = getCaInfo("operators", null, false);
		if (operators == null) {
			return null;
		}
		try {
			JSONObject json = new JSONObject(operators);
			if (json != null) {
				JSONArray arr = json.getJSONArray("value");
				if (arr != null && arr.length() > 0) {
					operId = arr.getInt(0);
				}
			}
			if (operId != -1) {
				String entitles = getCaInfo("entitles", "{\"operator_id\":"
						+ operId + "}", false);
				if (entitles == null) {
					return null;
				}
				JSONObject entitlesJsn = new JSONObject(entitles);
				JSONArray jsonArr = entitlesJsn.getJSONArray("value");

				ArrayList<EntitleInfo> result = new ArrayList<EntitleInfo>();
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject js = jsonArr.getJSONObject(i);
					int productId = js.getInt("product_id");
					int days = js.getInt("end_date");
					Date expireTime = new Date();
					expireTime.setTime(mFromDate.getTime() + (long) days * 24L
							* 60 * 60 * 1000);
					EntitleInfo entitle = new EntitleInfo(productId, expireTime);
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
