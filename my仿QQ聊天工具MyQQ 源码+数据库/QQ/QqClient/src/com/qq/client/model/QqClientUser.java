package com.qq.client.model;

import com.qq.common.*;
public class QqClientUser {
	
	public boolean checkUser(User u)
	{
		return new QqClientConServer().SendLoginfoToServer(u);
	}
	public boolean Sql(User u)
	{
		return new QqClientConServer().SendSqlInfoToServer(u);
	}
	public String[] Sql2(User u)
	{
		return new QqClientConServer().SendSqlInfoToServer2(u);
	}
}
