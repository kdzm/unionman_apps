package com.um.launcher.weather;

import com.um.launcher.weather.CityWeatherInfoBean;
import com.um.launcher.weather.HttpWorkTask;
import com.um.launcher.weather.WeatherBiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class WeatherReceiver extends BroadcastReceiver {

	public static final String RESPONSE_WEATHER = "com.um.launcher.weather.responseweather";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		
		if (action.equals(RESPONSE_WEATHER)) {
			//if (context instanceof WeatherUpdateListener) {
			    Log.i("WeatherReceiver", "Receiver to get Weather");
			    WeatherUpdateListener weatherViewListener = (WeatherUpdateListener)context;//(WeatherUpdateListener)intent.getParcelableExtra("WeatherView");
				final WeatherUpdateListener listener = (WeatherUpdateListener) weatherViewListener;
				new HttpWorkTask<CityWeatherInfoBean>(
						new HttpWorkTask.ParseCallBack<CityWeatherInfoBean>() {

							@Override
							public CityWeatherInfoBean onParse() {
								String code = WeatherBiz.getCityCode(context);
						//		Log.i("HomeMainActivity", "cityCode:" + code);
								return WeatherBiz.getWeatherFromHttp(code);
							}
						},
						new HttpWorkTask.PostCallBack<CityWeatherInfoBean>() {

							@Override
							public void onPost(CityWeatherInfoBean result) {
								if(result != null)
								{
						//			Log.i("HomeMainActivity", "city:" + result.getCityName());
					//				Log.i("HomeMainActivity", "weatherInfo:" + result.getWeatherInfo());
								}
							   if (listener != null){
								   listener.updateWeather(result);
							   }
							}
						}).execute();
			//}

		}
	}

	public interface WeatherUpdateListener {
		public void updateWeather(CityWeatherInfoBean bean);
	}
}

