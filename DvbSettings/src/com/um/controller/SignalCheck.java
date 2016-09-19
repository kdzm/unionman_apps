package com.um.controller;

import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
import com.um.dvbstack.Tuner.TunerInfo;

import android.os.Handler;
import android.util.Log;

public class SignalCheck {
	private long uptimeMillis = 2000;
	
	private Handler handler = new Handler();
	private Runnable r = new Runnable(){

		public void run() {
			DVB dvb = DVB.getInstance();
			Tuner tuner = Tuner.GetInstance(dvb);
			TunerInfo info = new TunerInfo();
			
			if(0==tuner.GetInfo(0, info))
			{
				tl.setParam(info.Quality, info.Ber, info.Strength);				
			}
			else
			{
				tl.setParam(0, new int[]{100,0,0}, 0);
				Log.e("SIGNALCHECK", "GET INFO ERROR!");
			}
			handler.postDelayed(r, uptimeMillis);
		}		
	};

	TunerCheckListener tl;
	public void setListener(TunerCheckListener l)
	{
		tl = l;
	}
	
	public void start()
	{
		handler.postDelayed(r, uptimeMillis);
	}
	
	public void stop()
	{
		handler.removeCallbacks(r);
	}

	public interface TunerCheckListener { 
	    void setParam(int snr, int[] ber, int strength); 
	} 

}
