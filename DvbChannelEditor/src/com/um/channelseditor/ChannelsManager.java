package com.um.channelseditor;

import android.content.Context;
import android.util.Log;

public class ChannelsManager {
	private final String TAG = ChannelsManager.class.getSimpleName()+"----U668";
	private final boolean LOGE = true;
	
	private static ChannelsManager mInstance;
	private Context mContext;
	private ProviderManager mProviderManager;
	private ChannelInfo mCachedChanInfos[];
	
	private ChannelsManager (Context context) {
		if(LOGE) Log.v(TAG, "construct a ChannelsManager object");
		mContext = context;
		mProviderManager = ProviderManager.getInstance(mContext);
	}
	
	
	public static ChannelsManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (ChannelsManager.class) {
				if (mInstance == null) {
					mInstance = new ChannelsManager(context);
				}
			}
		}
		return mInstance;
	}
	
	public ChannelInfo[] getChannelInfos(int tunerType) {
		mCachedChanInfos = mProviderManager.getChannelInfos(tunerType);
		return mProviderManager.getChannelInfos(tunerType);
	}
	
	public ChannelInfo getCachedChanInfoByName(String chanName) {
		for (int i = 0; i < mCachedChanInfos.length; i++) {
			if (mCachedChanInfos[i].getChanName().equals(chanName)) {
				return mCachedChanInfos[i];
			}
		}
		return null;
	}
	
	public boolean saveChanInfosDB(ChannelInfo chanInfos[]) {
		if (chanInfos != null) {
			return mProviderManager.saveChanInfosDB(chanInfos);
		}
		return false;
	}
}
