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
import com.um.util.ErrorReport;

import android.content.Context;
import android.os.AsyncTask;

public class HuaweiAccessTokenHelper extends BaseHelper implements DataCompleteListener {

	private ParserCompleteListener mListener = null;

	public HuaweiAccessTokenHelper(String url,Context context) {
		super(url,context);
	}

	/** 解析返回值 */
	@Override
	public void dataCompleteListener(final HttpEntity entity) {
		new AsyncTask<String, Integer, HuaweiAccessToken>() {
			@Override
			protected HuaweiAccessToken doInBackground(String... params) {
				HuaweiAccessToken info = null;
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
			protected void onPostExecute(HuaweiAccessToken result) {
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
	private HuaweiAccessToken parserData(String content) throws JSONException {
		HuaweiAccessToken temp = new HuaweiAccessToken();
		JsonHelperLog.d(this, "content = " + content);

		if (content == null) {
			JsonHelperLog.e(this, "access token error with network");
			return null;
		}

		JSONObject root = new JSONObject(content);
		temp.access_token = root.optString("access_token");
		temp.expires_in = root.optInt("expires_in");
		temp.refresh_token = root.optString("refresh_token");
		temp.heartbit_interval=root.optInt("heartbit_interval");
		temp.platform_type = root.optString("platform_type");

		if (temp.access_token == null || temp.access_token.equals("")) {
			temp.error = root.optString("error");
			temp.error_description = root.optString("error_description");
			if (temp.error == null) {
				JsonHelperLog.e(this, "access token is null or empty");
			}
			JsonHelperLog.e(this, "AccessToken error: " + temp.error + ", reson = " + temp.error_description);
			if(mContext!=null){
				ErrorReport.sendErrorReport(mContext, temp.error);
			}
			return null;
		}

		return temp;
	}
}
