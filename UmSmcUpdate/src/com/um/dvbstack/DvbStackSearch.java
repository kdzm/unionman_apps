package com.um.dvbstack;

public class DvbStackSearch {


	private final static String TAG = "DVBSTACK";
	public  int mNativeContext; // accessed by native methods
	

	
	public DvbStackSearch(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;
	}	
	

    public final native int AutoSearch(int type, int band, int Freq, int SymbolRate, int QamType);


    public final native int ManualSearch(int type, int band, int Freq, int SymbolRate, int QamType);


    public final native int FullBandSearch(int type, int band, int SymbolRate, int QamType);

	public final native int StopSearch();
	

}
