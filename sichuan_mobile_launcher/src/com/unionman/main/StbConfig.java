package com.unionman.main;

public class StbConfig {
	private String AuthURL;
	private String STBID;
	private String IP;
	private String MAC;
	private String UserID;
	private String UserPassword;
	private String UserToken;
	private String LastchannelNum;
	private String STBType;
	private String SoftwareVersion;
	private String Reserved;
	private String NetworkAcount;
	private String NetworkPassword;

	public StbConfig(String authURL, String sTBID, String iP, String mAC,
			String userID, String userPassword, String userToken,
			String lastchannelNum, String sTBType, String softwareVersion,
			String reserved, String networkAcount, String networkPassword) {
		AuthURL = authURL;
		STBID = sTBID;
		IP = iP;
		MAC = mAC;
		UserID = userID;
		UserPassword = userPassword;
		UserToken = userToken;
		LastchannelNum = lastchannelNum;
		STBType = sTBType;
		SoftwareVersion = softwareVersion;
		Reserved = reserved;
		NetworkAcount = networkAcount;
		NetworkPassword = networkPassword;
	}

	public StbConfig() {
		// TODO Auto-generated constructor stub
	}

	public String getAuthURL() {
		return AuthURL;
	}

	public void setAuthURL(String authURL) {
		AuthURL = authURL;
	}

	public String getSTBID() {
		return STBID;
	}

	public void setSTBID(String sTBID) {
		STBID = sTBID;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getUserPassword() {
		return UserPassword;
	}

	public void setUserPassword(String userPassword) {
		UserPassword = userPassword;
	}

	public String getUserToken() {
		return UserToken;
	}

	public void setUserToken(String userToken) {
		UserToken = userToken;
	}

	public String getLastchannelNum() {
		return LastchannelNum;
	}

	public void setLastchannelNum(String lastchannelNum) {
		LastchannelNum = lastchannelNum;
	}

	public String getSTBType() {
		return STBType;
	}

	public void setSTBType(String sTBType) {
		STBType = sTBType;
	}

	public String getSoftwareVersion() {
		return SoftwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		SoftwareVersion = softwareVersion;
	}

	public String getReserved() {
		return Reserved;
	}

	public void setReserved(String reserved) {
		Reserved = reserved;
	}
	
	public String getNetworkAcount() {
		return NetworkAcount;
	}

	public void setNetworkAcount(String networkAcount) {
		NetworkAcount = networkAcount;
	}
	
	public String getNetworkPassword() {
		return NetworkPassword;
	}

	public void setNetworkPassword(String networkPassword) {
		NetworkPassword = networkPassword;
	}

	@Override
	public String toString() {
		return "stbconfig: AuthURL + [" + AuthURL + "],STBID[" + STBID
				+ "],IP[" + IP + "],MAC[" + MAC + "],UserID[" + UserID
				+ "],UserPassword[" + UserPassword + "],UserToken[" + UserToken
				+ "],LastChannelNum[" + LastchannelNum + "],STBType[" + STBType
				+ "],SoftwareVersion[" + SoftwareVersion + "],Reserved["
				+ Reserved + "],NetworkAcount[" + NetworkAcount
				+ "],NetworkPassword[" + NetworkPassword + "]";
	}

}
