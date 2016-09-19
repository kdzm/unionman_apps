package com.um.controller;

import android.content.ContentResolver;
import android.util.Log;

import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
import com.unionman.nativeprovider.NativeContentValue;
import com.unionman.nativeprovider.NativeCursor;
import com.unionman.nativeprovider.NativeProvider;

public class ParamSave {
    public static final int DEFAULT_MAIN_FREQ = 227;
	public static final int MAX_MAIN_FREQ = 862;
	public static final int MIN_MAIN_FREQ = 111;
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

    /**
     * 重置DvbProvider中的主频点
     * @param cr
     */
    public static void resetMainFreq(ContentResolver cr)
    {
    	Log.i("ParamSave", "resetMainFreq");
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        resetMainFreq(cr, tunerType);
    }

    public static void resetMainFreq(ContentResolver cr, int tunerType)
    {
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            SaveMainFreq(Tuner.UM_TRANS_SYS_TYPE_TER, DEFAULT_MAIN_FREQ);
        } else {
            SaveMainFreq(Tuner.UM_TRANS_SYS_TYPE_CAB, DEFAULT_MAIN_FREQ);
        }
    }
}
