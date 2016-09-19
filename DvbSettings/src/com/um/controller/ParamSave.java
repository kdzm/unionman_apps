package com.um.controller;

import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
import com.um.ui.SysSetting;
import com.unionman.dvbstorage.SettingsStorage;
import com.unionman.jazzlib.SystemProperties;
import com.unionman.nativeprovider.NativeContentValue;
import com.unionman.nativeprovider.NativeCursor;
import com.unionman.nativeprovider.NativeProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

public class ParamSave {
	private static final String PREFS_NAME = "ParamSave";
    private static String MAIN_FREQ_DVBC = "persist.sys.dvb.dvbc.mainfreq";
    private static String MAIN_FREQ_DTMB = "persist.sys.dvb.dtmb.mainfreq";
	private static final String PARENT_LOCK = "parent_lock";
	private static final String PARENT_PASSWD_DVBC = "parent_passwd_dvbc";
    private static final String PARENT_PASSWD_DTMB = "parent_passwd_dtmb";
    public static final boolean DEFAULT_PARAM_LOCK = false;
    public static final String DEFAULT_PARENT_PASSWORD = "000000";
    public static final int DEFAULT_MAIN_FREQ = 227;
	public static final int MAX_MAIN_FREQ = 862000;
	public static final int MIN_MAIN_FREQ = 111000;
	
    public static final String URI_MAIN_FREQ = "com.umdvb.sysdata.mainfreq";
    public static final String VALUE_MAIN_FREQ_TRANS_TYPE = "trans_type";
    public static final String VALUE_MAIN_FREQ_VALUE = "freq_value";
    public static final String URI_STOP_MODE = "com.umdvb.sysdata.stopmode";
    public static final String VALUE_STOP_MODE = "stop_mode_value";
    public static final String URI_SOUND_CHANNEL = "com.umdvb.sysdata.soundchannel";
    public static final String VALUE_SOUND_CHANNEL = "sound_channel_value";
    public final static String KEY_MAIN_FREQ = "main_freq";

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
        contentValue.putInt(VALUE_MAIN_FREQ_VALUE, freq / 10);
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
        return nativeCursor.getInt(VALUE_MAIN_FREQ_VALUE, 0) * 10;
    }

	
    static public int GetStopMode()
    {
        NativeProvider nativeProvider = new NativeProvider();
        NativeCursor nativeCursor = nativeProvider.query(URI_STOP_MODE,
                new String[]{VALUE_STOP_MODE}, null, null, null);
		
        return nativeCursor.getInt(VALUE_STOP_MODE, 0);
    }

    static public void SetStopMode(int stopmode)
    {
		NativeProvider nativeProvider = new NativeProvider();
		NativeContentValue contentValue = new NativeContentValue();
		contentValue.putInt(VALUE_STOP_MODE, stopmode);
		nativeProvider.insert(URI_STOP_MODE, contentValue);
    }

	
    static public int GetSoundChannel()
    {
        NativeProvider nativeProvider = new NativeProvider();
        NativeCursor nativeCursor = nativeProvider.query(URI_SOUND_CHANNEL,
                new String[]{VALUE_SOUND_CHANNEL}, null, null, null);
		
        return nativeCursor.getInt(VALUE_SOUND_CHANNEL, 0);
    }

    static public void SetSoundChannel(int soundchannel)
    {
		NativeProvider nativeProvider = new NativeProvider();
		NativeContentValue contentValue = new NativeContentValue();
		contentValue.putInt(VALUE_SOUND_CHANNEL, soundchannel);
		nativeProvider.insert(URI_SOUND_CHANNEL, contentValue);
    }

    /**
     * 重置DvbProvider中的主频点
     * @param context
     */
    public static void resetMainFreq(Context context)
    {
    	Log.i("ParamSave", "resetMainFreq");
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        resetMainFreq(context, tunerType);
    }

    public static void resetAll(Context context) {
    	NativeProvider nativeProvider = new NativeProvider();
    	nativeProvider.delete(null, null, null);
    	//resetMainFreq(context);
        resetParentLock(context);

        resetParentPasswd(context);
    }
    
    public static void resetMainFreq(Context context, int tunerType)
    {
        int freq = SystemProperties.getInt("persist.sys.dvb.main_freq", 227000);
        Log.d("getRemoteSharedPreferencesInt: ", "freq: " + freq);
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            SaveMainFreq(Tuner.UM_TRANS_SYS_TYPE_TER, freq);
        } else {
            SaveMainFreq(Tuner.UM_TRANS_SYS_TYPE_CAB, freq);
        }
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

    public static void resetParentLock(Context c)
    {
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt(PARENT_LOCK,DEFAULT_PARAM_LOCK?1:0);
    }

    static public void SaveParentPasswd(Context c, int tunerType, String pwd)
    {
        SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            ss.putString(PARENT_PASSWD_DTMB, pwd);
        } else {
            ss.putString(PARENT_PASSWD_DVBC, pwd);
        }
    }

    static public String GetParentPasswd(Context c, int tunerType)
    {
        SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        String parentPassword;
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            parentPassword = ss.getString(PARENT_PASSWD_DTMB, DEFAULT_PARENT_PASSWORD);
        } else {
            parentPassword = ss.getString(PARENT_PASSWD_DVBC, DEFAULT_PARENT_PASSWORD);
        }

        return parentPassword;
    }

	static public void SaveParentPasswd(Context c, String pwd)
	{
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        SaveParentPasswd(c, tunerType, pwd);
	}

    public static void resetParentPasswd(Context c, int tunerType)
    {
        SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            ss.putString(PARENT_PASSWD_DTMB,DEFAULT_PARENT_PASSWORD);
        } else {
            ss.putString(PARENT_PASSWD_DVBC,DEFAULT_PARENT_PASSWORD);
        }
    }

    public static void resetParentPasswd(Context c)
    {
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        resetParentPasswd(c, tunerType);
    }
	
	static public String GetParentPasswd(Context c)
	{
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		return GetParentPasswd(c, tunerType);
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

    public static int getRemoteSharedPreferencesInt(Context remoteContex, String preferenceName, String key, int defVal) {
        SharedPreferences settings = remoteContex.getSharedPreferences(preferenceName,
                Context.MODE_WORLD_WRITEABLE | Context.MODE_MULTI_PROCESS);
        return settings.getInt(key, defVal);
    }

}
