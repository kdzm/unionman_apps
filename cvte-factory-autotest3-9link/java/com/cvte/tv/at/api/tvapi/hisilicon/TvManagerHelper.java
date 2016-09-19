package com.cvte.tv.at.api.tvapi.hisilicon;

import android.content.Context;

import com.hisilicon.android.tvapi.HitvManager;
import com.hisilicon.android.tvapi.SourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

/**
 * Created by zhoujun1868@cvte.com on 15-10-12.
 */
public class TvManagerHelper {

    private static TvManagerHelper instance;
    private SourceManager mSourceManager = null;
    private static final String TV_LIST_NAME = "TV";
    private static final String RADIO_LIST_NAME = "Radio";
    private static int mSignalStatus = 0;
    private Context mContext;

    public TvManagerHelper(Context context) {
        LogPrint.Debug("==>> TvManagerHelper => context... ");
        mContext = context;
        if (mSourceManager == null) {
            mSourceManager = HitvManager.getInstance().getSourceManager();
        }
    }

    public static TvManagerHelper getInstance(Context context) {
        if (instance == null) {      // In case of DTV is null.
            LogPrint.Debug("===>> instance or mDTV is null, Need to reinit...");
            instance = new TvManagerHelper(context);
        }
        return instance;
    }


    private int getValidateValue(int nValue) {
        LogPrint.Info("==>>> nValue = " + nValue);
        if (nValue < 0) {
            return 0;
        }
        if (nValue > 100) {
            return 100;
        }
        return nValue;
    }


    public int getCurrentSource() {
        return mSourceManager.getCurSourceId(0);      //0 means Main Window; 1 means Sub Window.
    }

    public int getSignalStatus() {
        final int curSrc = getCurrentSource();
        LogPrint.Debug("==>>> curSrc = " + curSrc);
        if (curSrc == EnumSourceIndex.SOURCE_DTMB) {
            return mSignalStatus;
        } else {
            // In DTV. can't use this method to update signal. By zhoujun1868@20151016
            mSignalStatus = mSourceManager.getSignalStatus();
        }
        return mSignalStatus;
    }

}
