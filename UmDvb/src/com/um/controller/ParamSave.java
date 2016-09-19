package com.um.controller;

import android.content.Context;
import android.util.Log;

import com.um.dvbstack.DVB;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.ProviderProgManage;
import com.um.dvbstack.Tuner;
import com.unionman.dvbstorage.SettingsStorage;
import com.unionman.jazzlib.SystemProperties;
import com.unionman.nativeprovider.NativeContentValue;
import com.unionman.nativeprovider.NativeCursor;
import com.unionman.nativeprovider.NativeProvider;


public class ParamSave {
	private static String PREFS_NAME = "ParamSave";
    private static String MAIN_FREQ_DVBC = "persist.sys.dvb.dvbc.mainfreq";
    private static String MAIN_FREQ_DTMB = "persist.sys.dvb.dtmb.mainfreq";
	private static String PARENT_LOCK = "parent_lock";
	private static String PARENT_PASSWD = "parent_passwd";
	private static int DEFAULT_MAIN_FREQ = 227;
	public static int MAX_MAIN_FREQ = 862;	
	public static int MIN_MAIN_FREQ = 111;
    private final String TAG = "ParamSave --NONG";
	private static int mCurFrontendType = -1;
    public static final String URI_MAIN_FREQ = "com.umdvb.sysdata.mainfreq";
    public static final String VALUE_MAIN_FREQ_TRANS_TYPE = "trans_type";
    public static final String VALUE_MAIN_FREQ_VALUE = "freq_value";
	
    public static final String URI_STOP_MODE = "com.umdvb.sysdata.stopmode";
    public static final String VALUE_STOP_MODE = "stop_mode_value";
    public static final String URI_SOUND_CHANNEL = "com.umdvb.sysdata.soundchannel";
    public static final String VALUE_SOUND_CHANNEL = "sound_channel_value";
	static private int getFrontednType() {
		if (mCurFrontendType == -1) {
			mCurFrontendType = Tuner.GetInstance(DVB.getInstance()).GetType();
		}
		return mCurFrontendType;
	}

    /**
     * 锟斤拷锟斤拷频锟斤拷锟斤拷锟矫碉拷系统锟斤拷锟斤拷锟斤拷
     * @param freq 锟斤拷频锟斤拷锟街�     */
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
     * 锟斤拷系统锟斤拷锟斤拷锟叫伙拷取锟斤拷频锟斤拷
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

	static public void SaveLastProgInfo(Context c,int index,int mode)
	{	
        	ProgList progList = null;
        	switch(mode)
        	{
	        	case ProviderProgManage.TVPROG:
	        		progList = ProviderProgManage.GetInstance(c).getTVProgList();
	        		break;
	        	case ProviderProgManage.RADIOPROG:
	        		progList = ProviderProgManage.GetInstance(c).getRadioProgList();
	        		break;
	        	default:
	        		progList = ProviderProgManage.GetInstance(c).getAllProgList();
	        		break;
        	}
        	

        	String progId = progList.list.get(index).get(ProgManage.PROG_ID);
        	int id = Integer.parseInt(progId);

    		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
            int tunerType = getFrontednType();        /* -s:1  -c:2  -t:3*/
            if (tunerType == 2) {
                ss.putInt("mode-c",mode);
            	if(ProviderProgManage.TVPROG == mode)
            	{
	                ss.putInt("progid-c",id);
	                ss.putInt("pre_progindex-c", ss.getInt("progindex-c",0));
	                ss.putInt("progindex-c",index);
            	}
            	else
            	{
	                ss.putInt("progid-c-radio",id);
	                ss.putInt("pre_progindex-c-radio", ss.getInt("progindex-c-radio",0));
	                ss.putInt("progindex-c-radio",index);

            	}

            }
            else if (tunerType == 3) {
                ss.putInt("mode-t",mode);
                if(ProviderProgManage.TVPROG == mode)
                {
	                ss.putInt("progid-t",id);
	                ss.putInt("pre_progindex-t", ss.getInt("progindex-t",0));
	                ss.putInt("progindex-t",index);
                }
                else
                {
	                ss.putInt("progid-t-radio",id);
	                ss.putInt("pre_progindex-t-radio", ss.getInt("progindex-t-radio",0));
	                ss.putInt("progindex-t-radio",index);

                }

            }
	}
	
	static public int GetLastProgIndex(Context c,int mode)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        int tunerType = getFrontednType();        /* -s:1  -c:2  -t:3*/
        //int mode  = ProviderProgManage.GetInstance(c).GetCurMode();
        int index = -1;
        if (tunerType == 2) {
        	if(ProviderProgManage.TVPROG == mode)
        	{
        		index =  ss.getInt("progindex-c",0);
        	}
        	else
        	{	
        		index =  ss.getInt("progindex-c-radio",0);
        	}	
        }
        else if (tunerType == 3) {
        	if(ProviderProgManage.TVPROG == mode)
        	{
        		index =  ss.getInt("progindex-t",0);
        	}
        	else
        	{	
        		index =  ss.getInt("progindex-t-radio",0);
        	}	
        }
        return index;
	}
	
	static public int GetPreProgIndex(Context c)
	{
		
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        int tunerType = getFrontednType();        /* -s:1  -c:2  -t:3*/
        int mode  = ProviderProgManage.GetInstance(c).GetCurMode();
        int index = -1;
        if (tunerType == 2) {
        	if(ProviderProgManage.TVPROG == mode)
        	{
        		index =  ss.getInt("pre_progindex-c",0);
        	}
        	else
        	{	
        		index =  ss.getInt("pre_progindex-c-radio",0);
        	}	
        }
        else if (tunerType == 3) {
        	if(ProviderProgManage.TVPROG == mode)
        	{
        		index =  ss.getInt("pre_progindex-t",0);
        	}
        	else
        	{	
        		index =  ss.getInt("pre_progindex-t-radio",0);
        	}	
        }
        return index;

	}
	
	static public int GetLastProgMode(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
        int tunerType = ss.getInt("tuner_type",2);        /* -s:1  -c:2  -t:3*/
        if (tunerType == 2) {
		    return ss.getInt("mode-c",0);
        }
        else if (tunerType == 3) {
            return ss.getInt("mode-t",0);
        }
        else {
            return -1;  /*error*/
        }
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
	
	static public void setDTMBProgSyncStatus(Context c, int type)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt("progDTMBSyncStatus",type);
	}
	
	static public void setDVBCProgSyncStatus(Context c, int type)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		ss.putInt("progDVBCSyncStatus",type);
	}
	
	static public int getProgDTMBSyncStatus(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("progDTMBSyncStatus",1);
	}
	
	static public int getProgDVBCSyncStatus(Context c)
	{
		SettingsStorage ss = new SettingsStorage(c.getContentResolver());
		return ss.getInt("progDVBCSyncStatus",1);
	}
}
