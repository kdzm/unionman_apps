/*
 * 这是个用户信息类
 */
package com.qq.common;

public class User implements java.io.Serializable{//序列化，可以让一个对象在网络上传输
	
	private String qum;
	private String petname;
	private String password;
	private String sex;
	private String status;
	private String friendId;
	private String operation;
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getFriendId() {
		return friendId;
	}
	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}
	public String getQum() {
		return qum;
	}
	public void setQum(String qum) {
		this.qum = qum;
	}
	public String getPetname() {
		return petname;
	}
	public void setPetname(String petname) {
		this.petname = petname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
