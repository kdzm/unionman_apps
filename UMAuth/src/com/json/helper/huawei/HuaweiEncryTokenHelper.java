package com.json.helper.huawei;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

public class HuaweiEncryTokenHelper extends BaseHelper implements DataCompleteListener {

	private ParserCompleteListener mListener = null;

	public HuaweiEncryTokenHelper(String url,Context context) {
		super(url,context);
	}

	/** 解析返回值 */
	@Override
	public void dataCompleteListener(final HttpEntity entity) {
		new AsyncTask<String, Integer, HuaweiEncryToken>() {
			@Override
			protected HuaweiEncryToken doInBackground(String... params) {
				HuaweiEncryToken info = null;
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
			protected void onPostExecute(HuaweiEncryToken result) {
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
	private HuaweiEncryToken parserData(String content) throws JSONException {
		HuaweiEncryToken temp = new HuaweiEncryToken();
		// content = "{\"EncryToken\": \"D6D0B9FAB5E7D0C5C9CFBAA3D4BAD6C6\"}";
		JsonHelperLog.d(this, "content = " + content);

		if (content == null) {
			JsonHelperLog.e(this, "EncryToken error with network");
			return null;
		}

		JSONObject root = new JSONObject(content);
		temp.encryToken = root.optString("EncryToken");
		JsonHelperLog.d("lwn","temp.encryToken:" + temp.encryToken);

		if (temp.encryToken == null || temp.encryToken.equals("")) {
			JsonHelperLog.d("lwn","temp.encryToken:" + temp.encryToken);
			JsonHelperLog.e(this, "HuaweiEncryToken is null or empty");
			return null;
		}

		return temp;
	}
}
