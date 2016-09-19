package com.um.dvbstack;

import java.lang.ref.WeakReference;

import android.util.Log;
import android.os.IBinder;
import com.unionman.jazzlib.*;

public class DVB
{
	private final static String TAG = "DVB";

	private Tuner mTuner;
	private DvbStackSearch mSearch;
	private ProgManage mProgManage;
	public  int mNativeContext; // accessed by native methods
	static DVB m_instance=null;

	static
	{
		System.loadLibrary("smcjni");  
		native_init();

	}

	public static DVB GetInstance()
	{
		if(m_instance == null)
		{
			m_instance = new DVB();		
			m_instance.create();
		}
		return m_instance;
	}

    public void enableStatusListener(boolean enable)
    {
        native_enableStatusListener(enable);
    }
    
	public static boolean IsInstanced()
	{
		if(m_instance == null)
			return false;
		else
			return true;
	}

	public void create()
	{
	    native_setup(new WeakReference<DVB>(this));
		InitSubObjects();
	}
	
	private void InitSubObjects()
	{
		 mTuner  		= Tuner.GetInstance(this);
		 mSearch 		= new DvbStackSearch(this);
		 mProgManage 	= ProgManage.GetInstance();
	}

	public Tuner GetTunerInstance() {			
		return mTuner;
	}
					
	protected void finalize() { 
		Log.d(TAG, "DVB finalize");
		native_finalize(); 
	}

	public void release() {
	    native_release();
	}

	public static boolean isServerAlive() {
		IBinder binder = ServiceManager.checkService("dvbstack");
		return (binder != null) && binder.pingBinder();
	}
	
	private static native final void native_init();
	private native final void native_setup(Object dvb_this);
	private native void native_release();
	private native final void native_finalize();
	private native final void native_enableStatusListener(boolean enable);

}

