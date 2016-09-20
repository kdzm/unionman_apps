package com.qq.server.model;

import com.qq.common.User;
import com.qq.server.db.SqlHelper;
import java.sql.*;
import javax.swing.*;
public class QqServerUser {
	public boolean SerInsertFriend(String []paras)//增加好友
	{
		boolean b=true;
		SqlHelper mysql=new SqlHelper();
		String sql="insert into friend(myqno,friendqno) values(?,?)";
		if(!mysql.SqlUpdate(sql, paras))
		{
			//提示
			b=false;
		}
		return b;
	}
	
	public boolean SerInsertpersonalinfo(String []paras)//注册用户
	{
		boolean b=true;
		SqlHelper mysql=new SqlHelper();
		String sql="insert into personalinfo(qnum,petname,password,sex,status) values(?,?,?,?,'否')";
		if(!mysql.SqlUpdate(sql, paras))
		{
			//提示
			b=false;
		}
		return b;
	}
	public String[] showFriend(String []paras)//查询某个账号的所有的好友
	{
		
		SqlHelper mysql=new SqlHelper();
		String sql="select friendqno from friend where myqno=?";
		return mysql.FriendQuery(sql, paras);
	}
	
	public boolean SerDelFriend(String []paras)//删除某个用户的好友
	{
		boolean b=true;
		SqlHelper mysql=new SqlHelper();
		String sql="delete from friend where myqno=? and friendqno=?";
		if(!mysql.SqlUpdate(sql, paras))
		{
			//提示
			b=false;
		}
		return b;
	}
	public boolean CheckUser(User u)
	{
		boolean b=false;
		String sql="select qnum,password from personalinfo where qnum=? and password=?";
		String []paras=new String[2];
		paras[0]=u.getQum();
		paras[1]=u.getPassword();
		if(new SqlHelper().SqlQuery(sql,paras))
		{
			b=true;
		}
		return b;
	}
/*
 * 调试代码段
 */
//	public boolean CheckmyUser()
//	{
//		boolean b=false;
//		String sql="select qnum,password from personalinfo where qnum=? and password=?";
//		String []paras=new String[2];
//		paras[0]="10000";
//		paras[1]="10000";
//		if(new SqlHelper().SqlQuery(sql,paras))
//		{
//			b=true;
//		}
//		return b;
//	}
//	public static void main(String[] args){
//		QqServerUser qsu = new QqServerUser();
//		qsu.CheckmyUser();
//	}
//	
}
