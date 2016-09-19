package com.um.launcher.util;
import java.util.ArrayList;

import android.view.Surface;
import android.graphics.Rect;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceView;
import com.unionman.dvbplayer.DvbPlayer;
import com.hisilicon.android.tvapi.vo.RectInfo;

import android.net.Uri;
import android.os.Handler;

import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.SettingsStorage;
import com.unionman.dvbstorage.ProgStorage;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class UtilDtv {
	private static final String TAG = "UtilDtv";
	private static UtilDtv mInstance = null;
	private DvbPlayer mDvbPlayer = null;
	private SurfaceView mSurfaceView = null;
	private Context mContext = null;
	private static BroadcastReceiver mDvbPlayerReceiver = null;
	private Handler mHandlerSetWinRect = new Handler();
	private SyncFlagObserver mSyncFlagObserver = null;
	private boolean mRegisterResolverFlag = false;
	private boolean mNeedToPlay = false;
	private UtilDtv() {

	}

	synchronized public static UtilDtv getInstance() {
		if (mInstance == null) {
			mInstance = new UtilDtv();
		}
		return mInstance;
	}
	
	private int getFirstValidProgId(ProgStorage ps) {
		int type = isCurrentDtmbMode() ? ContentSchema.CategoryTable.DTMB_ID
				: ContentSchema.CategoryTable.DVBC_ID;
		ArrayList<ProgInfo> progInfos = ps.getProgOrderBy(new int[] {
				type, ContentSchema.CategoryTable.TV_ID },
				ContentSchema.ProgsTable.ORDER, true);
		if (progInfos == null) {
			return -1;
		}
		
		for (ProgInfo prog : progInfos) {
			if (prog != null && !prog.hiden && prog.valid) {
				return prog.getProgId();
			}
		}
		return -1;
	}
	
	private String getLastPlayUrl(Context context) {
		int lastProgId = 0;
		int selSourceId = SourceManagerInterface.getCurSourceId();
		SettingsStorage ss = new SettingsStorage(context.getContentResolver());
		if (selSourceId == EnumSourceIndex.SOURCE_DVBC) {
			lastProgId = ss.getInt("progid-c", 0);
		} else {
			lastProgId =  ss.getInt("progid-t", 0);
		}
		ProgStorage progStorage = new ProgStorage(context.getContentResolver());
		ProgInfo lastProgInfo = progStorage.getProgInfo(lastProgId);
		if (lastProgInfo == null || lastProgInfo.hiden || !lastProgInfo.valid) {
			lastProgId = getFirstValidProgId(progStorage);
			if (lastProgId == -1) {
				Log.v(TAG, "getFirstValidProgId() failed.");
				return null;
			} else {
				Log.v(TAG, "getFirstValidProgId: " + lastProgId);
				if (selSourceId == EnumSourceIndex.SOURCE_DVBC) {
					ss.putInt("progid-c", lastProgId);
					ss.putInt("progindex-c", 0);
				} else {
					ss.putInt("progid-t", lastProgId);
					ss.putInt("progindex-t", 0);
				}
			}
		}
		
		String url = "dvb://" + lastProgId;
		Log.i(TAG, "DVB paly url: " + url);
		return url;
	}

	public void startPlay(Context context, SurfaceView sv) {
		Log.v(TAG, "startPlay()");
		if (context == null || sv == null) {
			Log.e(TAG, "param error.");
			return;
		}

		stopPlay();
		
		mNeedToPlay = true;
		mSurfaceView = sv;
		mContext = context;
		
		mDvbPlayerReceiver = new DvbPlayerReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.unionman.dvb.ACTION_DVBPLAYER_SERVER_READY");
		context.registerReceiver(mDvbPlayerReceiver, intentFilter);

		if (getSyncProgFlag(mContext)) {
			Log.v(TAG, "check Prog is not sync");
			registerResolver(mContext);
		}
		
		if (!DvbPlayer.isServerAlive()) {
			Log.e(TAG, "dvbplay server not alive");
			mDvbPlayer = null;
			return;
		}

		startPlayInner();
	}
	
	
	private boolean isAppInstalled(Context context,String packagename)
	{
		PackageInfo packageInfo;        
			try {
				packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
			}catch (NameNotFoundException e) {
				packageInfo = null;
			e.printStackTrace();
			}
		if(packageInfo ==null){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean isCurrentDtmbMode() {
		int selSourceId = SourceManagerInterface.getCurSourceId();
		return selSourceId == EnumSourceIndex.SOURCE_DTMB;
	}
	
	public boolean isDTVProgEmpty(Context cxt) {
		
		int progTypeId;
		
		
		if(isAppInstalled(cxt,"com.unionman.dvbprovider")==false)
		{
			Log.v(TAG, "com.unionman.dvbprovider not install yet !");
			return true;
		}
		
		if (isCurrentDtmbMode()) {
			progTypeId = ContentSchema.CategoryTable.DTMB_ID;
		} else {
			progTypeId = ContentSchema.CategoryTable.DVBC_ID;
		}

		int modeTypeId = ContentSchema.CategoryTable.TV_ID;

		ProgStorage progStorage = new ProgStorage(cxt.getContentResolver());
		int count = progStorage
				.getProgCount(new int[] { progTypeId, modeTypeId });
		return count <= 0;
	}

	public void stopPlay() {
		Log.v(TAG, "stopPlay()");
		releasePlayer();
		
		if (mContext != null) {
			unregisterResolver(mContext);
		}
		
		if (mContext != null && mDvbPlayerReceiver != null) {
			mContext.unregisterReceiver(mDvbPlayerReceiver);
		}
		mDvbPlayerReceiver = null;

		mSurfaceView = null;
		mContext = null;
		mNeedToPlay = false;
	}

	private void releasePlayer() {
		mHandlerSetWinRect.removeCallbacksAndMessages(null);
		if (mDvbPlayer != null) {
			mDvbPlayer.stop();
			mDvbPlayer.reset();
			mDvbPlayer.release();
			mDvbPlayer = null;
		}
	}
	
	private class SyncFlagObserver extends ContentObserver {

		public SyncFlagObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			if (mContext != null && !getSyncProgFlag(mContext)) {
				if (!DvbPlayer.isServerAlive()) {
	                Log.w(TAG, "onChange: server is not ready.");
	                return ;
	            }
	            Log.v(TAG, "prog sync is finished: ready to play dvb");
	            if (mNeedToPlay && mDvbPlayer == null) {
	            	startPlayInner();
	            }
			}
		}
	}
	
	private void registerResolver(Context context) {
		SettingsStorage ss = new SettingsStorage(context.getContentResolver());
		Uri uri = null;
		if (isCurrentDtmbMode()) {
			uri = ss.getUriFor("progDTMBSyncStatus");
		} else {
			uri = ss.getUriFor("progDVBCSyncStatus");
		}
		
		unregisterResolver(context);
		
		mSyncFlagObserver = new SyncFlagObserver(new Handler());
		context.getContentResolver().registerContentObserver(uri, false, mSyncFlagObserver);
	}
	
	private void unregisterResolver(Context context) {
		if (mSyncFlagObserver != null) {
			context.getContentResolver().unregisterContentObserver(mSyncFlagObserver);
			mSyncFlagObserver = null;
		}
	}
	
	private boolean getSyncProgFlag(Context context) {
		SettingsStorage ss = new SettingsStorage(context.getContentResolver());
		if (isCurrentDtmbMode()) {
			return ss.getInt("progDTMBSyncStatus", 1) != 1;
		} else {
			return ss.getInt("progDVBCSyncStatus", 1) != 1;
		}
	}
	
	private void startPlayInner() {
		if (mSurfaceView == null || mContext == null) {
			Log.e(TAG, "mSurfaceView or mContext is null");
			return;
		}

		String url = getLastPlayUrl(mContext);
		Log.e(TAG, "url:" + url);
		if (url == null) {
			Log.e(TAG, "url is null");
			return;
		}
		
		if (mDvbPlayer == null) {
			Log.v(TAG, "new DvbPlayer");
			mDvbPlayer = new DvbPlayer();
			mDvbPlayer.setOnErrorListener(mOnPlayErrorListener);
		}

		mDvbPlayer.reset();
		
		mDvbPlayer.setDataSource(url);
		mDvbPlayer.setDisplay(mSurfaceView.getHolder());
		mDvbPlayer.prepare();
		mDvbPlayer.start();
		mHandlerSetWinRect.removeCallbacksAndMessages(null);
		mHandlerSetWinRect.postDelayed(new Runnable() {
			public void run() {
				Surface surface = mSurfaceView.getHolder().getSurface();
				Rect videoRect = surface.getVideoRect();
				RectInfo rect = new RectInfo();
				/*
                rect.setX(videoRect.left);
                rect.setY(videoRect.top);
                rect.setW(videoRect.right - videoRect.left);
                rect.setH(videoRect.bottom - videoRect.top);
                */
                rect.setX(120);
                rect.setY(235);
                rect.setW(720);
                rect.setH(405);
                SourceManagerInterface.setWindowRect(rect, 0);
			}
		}, 150);
	}
	
	private DvbPlayer.OnErrorListener mOnPlayErrorListener =  new DvbPlayer.OnErrorListener() {
        @Override
        public void onError(DvbPlayer dvbPlayer, int what, int extra) {
            if (what == DvbPlayer.MSG_ERROR_SERVER_DIE) {
                Log.w(TAG, "ready to release DvbPlayer for death reason");
                if (mDvbPlayer != null) {
	                mDvbPlayer.release();
	                mDvbPlayer = null;
                }
            }
        }
    };
    
	private class DvbPlayerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            Log.v(TAG, "receive: " + action);
            if (action != null && action.equals("com.unionman.dvb.ACTION_DVBPLAYER_SERVER_READY") ) {
                if (!DvbPlayer.isServerAlive()) {
                    Log.w(TAG, "server is not ready.");
                    return ;
                }
                Log.v(TAG, "ready to play dvb");
                if (mNeedToPlay && mDvbPlayer == null) {
                	startPlayInner();
                }
            }
		}
	}

}
