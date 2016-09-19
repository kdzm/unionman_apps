package com.json.helper.huawei;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.json.helper.BaseHelper;
import com.json.helper.DataCompleteListener;
import com.json.helper.JsonHelperLog;
import com.json.helper.ParserCompleteListener;

import android.content.Context;
import android.os.AsyncTask;

public class HuaweiEpgUrlHelper extends BaseHelper implements DataCompleteListener {

	private ParserCompleteListener mListener = null;

	public HuaweiEpgUrlHelper(String url,Context context) {
		super(url,context);
	}

	/** 解析返回值 */
	@Override
	public void dataCompleteListener(final HttpEntity entity) {
		new AsyncTask<String, Integer, HuaweiEpgUrl>() {
			@Override
			protected HuaweiEpgUrl doInBackground(String... params) {
				HuaweiEpgUrl info = null;
				if (entity != null) {
					try {
						String content = EntityUtils.toString(entity, HTTP.UTF_8);
						info = parserData(content);
					} catch (ParseException e) {
						JsonHelperLog.e(this, "ParseException");
						info = null;
					} catch (IOException e) {
						JsonHelperLog.e(this, "IOException");
						info = null;
					} catch (JSONException e) {
						JsonHelperLog.e(this, "JSONException");
						info = null;
					}
				}
				return info;
			}

			@Override
			protected void onPostExecute(HuaweiEpgUrl result) {
				super.onPostExecute(result);
				if (null != mListener) {
					mListener.parserComplete(result);
				}
			}
		}.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
	}

	public void setParserCompleteListener(ParserCompleteListener listener) {
		mListener = listener;
		setDataCompleteListener(this);
	}

	/**
	 * 解析下载到的内容
	 * 
	 * @param content
	 *            String 下载到的内容
	 * @return
	 * @throws JSONException
	 */
	private HuaweiEpgUrl parserData(String content) throws JSONException {
		HuaweiEpgUrl temp = new HuaweiEpgUrl();
		// content = "{\"epgurl\":\"http://27.31.208.36:33200/EPG/jsp/AuthenticationURL?Action=Login\"}";
		JsonHelperLog.d(this, "content = " + content);

		if (content == null) {
			JsonHelperLog.e(this, "epgurl error with network");
			return null;
		}

		JSONObject root = new JSONObject(content);
		temp.epgurl = root.optString("epgurl");

		if (temp.epgurl == null || temp.epgurl.equals("")) {
			JsonHelperLog.e(this, "epgurl is null or empty");
			return null;
		}

		String regex = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})(\\:{0,1})(\\d{0,5})";
		final Matcher mt = Pattern.compile(regex).matcher(temp.epgurl);
		if (!mt.find())
			return null;
		temp.ip = mt.group();

		return temp;
	}
}
