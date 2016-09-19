package com.unionman.dvbcitysetting.util;

import com.unionman.nativeprovider.NativeContentValue;
import com.unionman.nativeprovider.NativeCursor;
import com.unionman.nativeprovider.NativeProvider;

/**
 * Created by hjian on 2015/2/11.
 */
public class DvbUtils {
    public final static int UM_TRANS_SYS_TYPE_SAT = 1;
    public final static int UM_TRANS_SYS_TYPE_CAB = 2;
    public final static int UM_TRANS_SYS_TYPE_TER = 3;
    public static final String URI_MAIN_FREQ = "com.umdvb.sysdata.mainfreq";
    public static final String VALUE_MAIN_FREQ_TRANS_TYPE = "trans_type";
    public static final String VALUE_MAIN_FREQ_VALUE = "freq_value";

    public static void setMainFreq(int tunerType, int freq) {
        NativeProvider nativeProvider = new NativeProvider();
        NativeContentValue contentValue = new NativeContentValue();
        contentValue.putInt(VALUE_MAIN_FREQ_TRANS_TYPE, tunerType);
        contentValue.putInt(VALUE_MAIN_FREQ_VALUE, freq / 10);
        nativeProvider.insert(URI_MAIN_FREQ, contentValue);
    }
}
