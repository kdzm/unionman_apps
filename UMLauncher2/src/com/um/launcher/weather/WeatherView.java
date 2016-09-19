package com.um.launcher.weather;

import com.um.launcher.weather.WeatherReceiver.WeatherUpdateListener;
import com.um.launcher.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.EthernetDataTracker;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherView extends LinearLayout implements WeatherUpdateListener{
	private final static String tag = "WeatherView";
	private int parceableValue = 1;
	private TextView weatherInfo;
    private TextView weatherCity;
    private ImageView weatherLog1;
    private ImageView weatherLog2;
    private WeatherReceiver weatherReceiver = null;
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private Handler handler = new Handler();
    
    public WeatherView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	mContext = context;
    	
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.weather_view, this);
        weatherInfo = (TextView)this.findViewById(R.id.weather_info);
        weatherCity = (TextView)this.findViewById(R.id.weather_city);
        weatherLog1 = (ImageView)this.findViewById(R.id.weather_log1);
        weatherLog2 = (ImageView)this.findViewById(R.id.weather_log2);
        mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        Log.d(tag, "come to WeatherView with attrs");
        
    }
    
    public WeatherView(Context context) {
        super(context);
        Log.d(tag, "come to WeatherView");
    }
    
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		weatherReceiver = new WeatherReceiver();
		mContext.registerReceiver(weatherReceiver, new IntentFilter(
				WeatherReceiver.RESPONSE_WEATHER));
		
		registerNetBroadcastReceiver();
		
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		handler.removeCallbacks(getWeatherInfoRun);
		
        if (weatherReceiver != null)
        {
        	mContext.unregisterReceiver(weatherReceiver);
        }
        
        if (netReceiver != null){
        	mContext.unregisterReceiver(netReceiver);
        }
	}
	
	@Override
	public void updateWeather(CityWeatherInfoBean bean) {
		
		Log.d(tag, "updateWeather ui");
		
		if (bean == null) {
			Log.d(tag, "updateWeather ui bean is null");
			return;
		}
        
		if (weatherCity != null)
			weatherCity.setText(bean.getCityName());
		if (weatherInfo != null)
			weatherInfo.setText(bean.getWeatherInfo());
		
		int[] ids = StringUtils.myGetWeaResByWeather(bean.getWeatherInfo());
		Log.d(tag, "updateWeather ids[0]:"+ids[0]+";ids[1]:"+ids[1]);
		if (ids[0] != 0) {
			if (weatherLog1 != null){
				weatherLog1.setVisibility(View.VISIBLE);
				weatherLog1.setImageResource(ids[0]);
			}
		} else {
			if (weatherLog1 != null)
				weatherLog1.setVisibility(View.GONE);
		}
		if (ids[1] != 0) {
			if (weatherLog2 != null){
				weatherLog2.setVisibility(View.VISIBLE);
				weatherLog2.setImageResource(ids[1]);
			}
		} else {
			if (weatherLog2 != null)
				weatherLog2.setVisibility(View.GONE);
		}
		String temp = bean.getfTemp() + "~" + bean.gettTemp();
		
		//instance.setCityWeatherBean(bean);
        handler.removeCallbacks(getWeatherInfoRun);
        handler.postDelayed(getWeatherInfoRun, 1000 * 60 * 60 * 12);//����ȡ�ɹ�����12Сʱ��ȡһ��
	}
	
	private void sendWeatherBroadcast(){
		Intent intent = new Intent(WeatherReceiver.RESPONSE_WEATHER);
		mContext.sendBroadcast(intent);
	}
	
    private Runnable getWeatherInfoRun = new Runnable() {
        @Override
        public void run() {
            Log.i(tag, "Runnable send broadcast to get weather!");
            sendWeatherBroadcast();
        }
    };

	public BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null)
            {
	        	if (networkInfo.isAvailable() && networkInfo.isConnected()) {
	        		Log.i(tag, "netReceiver send broadcast to get weather!");
	        		sendWeatherBroadcast();
	        	}
            }
        }
    };
    
    private void registerNetBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(
        		ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        //filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        //filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
        mContext.registerReceiver(netReceiver, filter);
    }
 
}
