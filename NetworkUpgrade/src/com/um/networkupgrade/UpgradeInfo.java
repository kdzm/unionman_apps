package com.um.networkupgrade;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInfo {

	private String mProductModel = null;
	private String mVersion = null;
	private String mHardwareVersion = null;
	private String mUpdateMode = null;
	private String mDownloadWay = null;

	public String getDownloadWay() {
		return mDownloadWay;
	}

	public void setDownloadWay(String mDownloadWay) {
		this.mDownloadWay = mDownloadWay;
	}

	private List<Serial> mSerialList = null;
	private List<Packet> mPacketList = null;

	public UpgradeInfo() {
		mSerialList = new ArrayList<UpgradeInfo.Serial>();
		mPacketList = new ArrayList<UpgradeInfo.Packet>();
	}

	class Serial {
		private String from = null;
		private String to = null;
		private int length = 0;

		public Serial(String from, String to, int len) {
			// TODO Auto-generated constructor stub
			this.from = from;
			this.to = to;
			this.length = len;
		}

		public String getStartSerial() {
			return this.from;
		}

		public void setSerialFrom(String from) {
			this.from = from;

		}

		public String getEndSerial() {
			return this.to;
		}

		public void setSerialTo(String to) {
			this.to = to;

		}

		public int getSerialLength() {
			return this.length;
		}

		public void setSerialLength(int length) {
			this.length = length;
		}

	}

	class Packet {
		private String update = null;
		private String type = null;
		private String url = null;
		private String md5=null;

		public Packet(String update, String type, String url,String md5) {
			this.update = update;
			this.type = type;
			this.url = url;
			this.md5=md5;
		}

		public String getPacketUpdate() {
			return this.update;
		}

		public void setPacketUpdate(String update) {
			this.update = update;

		}

		public String getPacketType() {
			return this.type;
		}

		public void setPacketType(String type) {
			this.type = type;

		}

		public String getPacketUrl() {
			return this.url;
		}

		public void setPacketUrl(String url) {
			this.url = url;
		}
		
		public String getMd5(){
			return this.md5;
		}
		
		public void setMd5(String md5){
			this.md5=md5;
		}

	}

	public String getProductModel() {
		return this.mProductModel;
	}

	public void setProductModel(String productModel) {

		this.mProductModel = productModel;
	}


	public List<Serial> getSerialList() {

		return this.mSerialList;
	}

	public void setSerialList(List<Serial> serialList) {

		this.mSerialList = serialList;
	}

	public void setSerial(String from, String to, int len) {
		this.mSerialList.add(new Serial(from, to, len));
	}

	public List<Packet> getPacketList() {
		return this.mPacketList;
	}

	public void setPacketList(List<Packet> packetList) {
		this.mPacketList = packetList;
	}

	public void setPacket(String update, String type, String url,String md5) {
		this.mPacketList.add(new Packet(url, type, update,md5));
	}

	public void setVersion(String version) {

		this.mVersion = version;
	}

	public String getVersion() {
		return mVersion;
	}

	public String getHardVersion() {

		return this.mHardwareVersion;
	}

	public void setHardVersion(String hardwareVersion) {

		this.mHardwareVersion = hardwareVersion;
	}

	public void setUpdateMode(String updateMode) {

		this.mUpdateMode = updateMode;
	}

	public String getUpdateMode() {
		return mUpdateMode;
	}

	@Override
	public String toString() {
		return "mProductModel=" + mProductModel
				+ " mVersion=" + mVersion +" mHardwareVersion=" + mHardwareVersion;
	}

}
