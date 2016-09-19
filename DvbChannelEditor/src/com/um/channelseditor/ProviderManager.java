package com.um.channelseditor;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;

public class ProviderManager {
	private final String TAG = ProviderManager.class.getSimpleName()+"----U668";
	private final boolean LOGE = true;

    public final static int ALLPROG      = 0;
	public final static int TVPROG       = 1;
	public final static int RADIOPROG    = 2;
	public final static int FAVPROG      = 3;
	public final static int NVODPROG     = 4;
	public final static int BATPROG      = 5;
	public final static int MAX_LIST_CNT = 6;
	
	public static final int ALL_ID   = 0;
    public static final int TV_ID    = 1;
    public static final int RADIO_ID = 2;
    public static final int DVBC_ID  = 3;
    public static final int DTMB_ID  = 4;
    public static final int FAV_ID   = 5;
	
	private static ProviderManager mInstance;
	private Context mContext;
	private ContentResolver mContentResolver;
    private ProgStorage mProgStorage;
	
//	private List<ProgList> allList;// = new ArrayList<ProgList>(); 
//	private List<ProgList> batList;// = new ArrayList<ProgList>();
//	private List<String>  batListName;// = new ArrayList<String>();
    
	private ProviderManager(){}
	
	private ProviderManager(Context context){
		if (LOGE) Log.v(TAG, "construct a ProviderManager object");
		mContext = context;
		init();
	}
	
	public static ProviderManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (ProviderManager.class) {
				if (mInstance == null) {
					mInstance = new ProviderManager(context);
				}
			}
		}
		return mInstance;
	}
	
	public ChannelInfo[] getChannelInfos(int tunerType) {
		return progInfosToChanInfos(tunerType);
	}
	
	private void init() {
		mContentResolver = mContext.getContentResolver();
		mProgStorage = new ProgStorage(mContentResolver);
	}

	private ChannelInfo[] progInfosToChanInfos(int tunerType) {
		if(LOGE) Log.v(TAG, "progInfosToChanInfos was called");
		ChannelInfo[] channelInfos = null;
		int categoryID = ((tunerType == 2) ? ContentSchema.CategoryTable.DVBC_ID
				: ContentSchema.CategoryTable.DTMB_ID);
		List<ProgInfo> progInfos = mProgStorage.getProgOrderBy(new int[]{categoryID, TV_ID}, ContentSchema.ProgsTable.ORDER,true);
		if (progInfos != null) {
			final int length = progInfos.size();
			if (length > 0) {
				channelInfos = new ChannelInfo[length];
			}
			ProgInfo progInfo;
			if(LOGE) Log.v(TAG, "progInfos size: "+length);
			for (int i = 0; i < length; i++) {
				progInfo = progInfos.get(i);
				if(channelInfos[i] == null) {
					channelInfos[i] = new ChannelInfo();
				}
				channelInfos[i].setChanName(progInfo.getProgName());
				channelInfos[i].setProgID(progInfo.getProgId());
				channelInfos[i].setOrder(progInfo.getOrder());
				channelInfos[i].setHided(progInfo.hiden);
				channelInfos[i].setFav(progInfo.fav);
				channelInfos[i].setValid(progInfo.valid);
			}
		}
		return channelInfos;
	}
	
	public boolean saveChanInfosDB(ChannelInfo chanInfos[]) {
		if (chanInfos == null) {
			return false;
		}
		Log.i("channedit","channel_length"+chanInfos.length);
		try {
			ContentValues[] contentValues = new ContentValues[chanInfos.length];
			for (int i = 0; i < chanInfos.length; i++) {  
				contentValues[i] = new ContentValues();
				contentValues[i].put(ContentSchema.ProgsTable.PROG_ID, chanInfos[i].getProgID());
				contentValues[i].put(ContentSchema.ProgsTable.ORDER, chanInfos[i].getOrder());
				contentValues[i].put(ContentSchema.ProgsTable.FAVORITE, chanInfos[i].getFav()==true?1:0);
				contentValues[i].put(ContentSchema.ProgsTable.HIDE, chanInfos[i].getHided()==true?1:0);
				contentValues[i].put(ContentSchema.ProgsTable.VALID, chanInfos[i].getValid()==true?1:0);
			}
			Log.i("channedit","channel_length"+chanInfos.length);
			mProgStorage.updateProg(contentValues);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
