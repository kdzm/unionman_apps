package com.um.dvb_receiver;

import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
import com.unionman.dvbstorage.SettingsStorage;
import com.unionman.jazzlib.SystemProperties;
import com.unionman.nativeprovider.NativeContentValue;
import com.unionman.nativeprovider.NativeCursor;
import com.unionman.nativeprovider.NativeProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
public class ParamSave {
	private static String PREFS_NAME = "ParamSave";
    private static String MAIN_FREQ_DVBC = "persist.sys.dvb.dvbc.mainfreq";
    private static String MAIN_FREQ_DTMB = "persist.sys.dvb.dtmb.mainfreq";
	private static String PARENT_LOCK = "parent_lock";
	private static String PARENT_PASSWD = "parent_passwd";
	private static int DEFAULT_MAIN_FREQ = 227;
	public static int MAX_MAIN_FREQ = 862;	
	public static int MIN_MAIN_FREQ = 111;
    public static final String URI_MAIN_FREQ = "com.umdvb.sysdata.mainfreq";
    public static final String VALUE_MAIN_FREQ_TRANS_TYPE = "trans_type";
    public static final String VALUE_MAIN_FREQ_VALUE = "freq_value";

    /**
     * 将主频掉设置到系统属性中
     * @param freq 主频点的值
     */
    static public void SaveMainFreq(int freq)
    {
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        SaveMainFreq(tunerType, freq);
    }

    static public void SaveMainFreq(int tunerType, int freq) {
        NativeProvider nativeProvider = new NativeProvider();
        NativeContentValue contentValue = new NativeContentValue();
        contentValue.putInt(VALUE_MAIN_FREQ_TRANS_TYPE, tunerType);
        contentValue.putInt(VALUE_MAIN_FREQ_VALUE, freq * 100);
        nativeProvider.insert(URI_MAIN_FREQ, contentValue);
    }

    /**
     * 从系统属性中获取主频点
     * @return
     */
    static public int GetMainFreq()
    {
        Log.i("ParamSave", "GetMainFreq");
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        return GetMainFreq(tunerType);
    }

    static public int GetMainFreq(int tunerType)
    {
        NativeProvider nativeProvider = new NativeProvider();
        NativeCursor nativeCursor = nativeProvider.query(URI_MAIN_FREQ,
                new String[]{VALUE_MAIN_FREQ_TRANS_TYPE, VALUE_MAIN_FREQ_VALUE}, null, null, null);

        int type = nativeCursor.getInt(VALUE_MAIN_FREQ_TRANS_TYPE, 0);
        return nativeCursor.getInt(VALUE_MAIN_FREQ_VALUE, 0) / 100;
    }

	static public void SaveParentLock(Context c, boolean lock)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt(PARENT_LOCK,lock?1:0);
	}
	
	static public boolean GetParentLock(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		int parentLock = ss.getInt(PARENT_LOCK,0);
		return (parentLock==1) ? true : false;
	}

	static public void SaveLastProgInfo(Context c,int progid,int mode)
	{	
    		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
    		ss.putInt("progindex",progid);
    		ss.putInt("mode",mode);
	}
	
	static public int GetLastProgIndex(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("progindex",0);
	}
	
	static public int GetLastProgMode(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("mode",0);
	}
	
	static public void setProgType(Context c, int type)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt("progTransType",type);
	}
	
	static public int getProgType(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("progTransType",0);
	}
	
	static public void setProgSyncStatus(Context c, int type)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt("progSyncStatus",type);
	}
	
	static public int getProgSyncStatus(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("progSyncStatus",1);
	}
	
}
