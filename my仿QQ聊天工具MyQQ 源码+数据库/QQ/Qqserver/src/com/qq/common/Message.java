package com.qq.common;

public class Message implements java.io.Serializable{
	
	private String messType;
	private String sender;
	private String getter;
	private String conn;
	private String sendTime;
	private String friend[];
	public String[] getFriend() {
		return friend;
	}

	public void setFriend(String[] friend) {
		this.friend = friend;
	}

	public String getMessType() {
		return messType;
	}

	public void setMessType(String messType) {
		this.messType = messType;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getGetter() {
		return getter;
	}

	public void setGetter(String getter) {
		this.getter = getter;
	}

	public String getConn() {
		return conn;
	}

	public void setConn(String conn) {
		this.conn = conn;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
}
