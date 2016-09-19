package com.um.channelseditor;

public class ChannelInfo {
	private String mChanName;
	private int mProgID;
	private long mOrder;
	private boolean mHided = false;
	private boolean mFav = false;
	private boolean mLocked = false;
	private boolean mValid = true;
	
	public ChannelInfo(String chanName, int progID, long order, boolean hided, boolean fav, boolean locked ,boolean valid) {
		super();
		mChanName = chanName;
		mProgID = progID;
		mOrder = order;
		mHided = hided;
		mFav = fav;
		mLocked = locked;
		mValid = valid;
	}

	public ChannelInfo(String chanName, int progID, long order) {
		super();
		mChanName = chanName;
		mProgID = progID;
		mOrder = order;
	}

	public ChannelInfo() {
		super();
	}
	
	public void setChanName(String chanName) {
		mChanName = chanName;
	}
	
	public String getChanName() {
		return mChanName;
	}
	
	public void setProgID(int progID) {
		mProgID = progID;
	}
	
	public int getProgID() {
		return mProgID;
	}
	
	public void setOrder(long order) {
		mOrder = order;
	}
	
	public long getOrder() {
		return mOrder;
	}
	
	public boolean getValid() {
		return mValid;
	}
	
	public void setHided(boolean hided) {
		mHided = hided;
	}
	
	public boolean getHided() {
		return mHided;
	}
	
	public void setFav(boolean fav) {
		mFav = fav;
	}
	
	public boolean getFav() {
		return mFav;
	}
	
	public void setLocked(boolean locked) {
		mLocked = locked;
	}
	
	public boolean getLocked() {
		return mLocked;
	}
	
	public void setValid(boolean valid) {
		mValid = valid;
	}
}
