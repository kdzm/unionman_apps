
package cn.com.unionman.umtvsetting.appmanage;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.appmanage.util.Constant;
import cn.com.unionman.umtvsetting.appmanage.util.Util;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    // sharing of data,application list
    private List<ResolveInfo> mResolveInfos = null;
    private static Context mContext;
    private Handler mhandler;

    // access to shared data
    public List<ResolveInfo> getResolveInfos() {
        return mResolveInfos;
    }

    public void setResolveInfos(List<ResolveInfo> resolveInfos) {
        clearList();
        this.mResolveInfos = resolveInfos;
        if (mResolveInfos.size() != 0){
            mhandler.sendEmptyMessage(Util.UPDATE_VIEW);
        }else{
        	mhandler.sendEmptyMessage(Util.UPDATE_VIEW_WHITHOUT_APPS);
        }
    }

    public void setHandler(Handler handler) {
        this.mhandler = handler;
    }

    /**
     * clear all data
     */
    public void clearList() {
        if (null != mResolveInfos && mResolveInfos.size() > 0) {
            mResolveInfos.clear();
            mResolveInfos = null;
        }
    }

    public void onCreate() {
        super.onCreate();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onCreate");
        }
        MyApplication.mContext = getApplicationContext();
        /*
         * try { startOtherService(); } catch (Exception e) {
         * e.printStackTrace(); }
         */
    }

    @Override
    public void onLowMemory() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onLowMemory");
        }
        super.onLowMemory();
    }

    public static Context getAppContext() {
        return MyApplication.mContext;
    }

}
