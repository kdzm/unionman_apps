/*
 * 管理好友、黑名单..界面类
 */
package com.qq.client.tools;

import java.util.*;
import java.io.*;
import com.qq.client.view.*;
public class ManageQqFriendList {
	
	private static HashMap hm=new HashMap<String,QqFriendList>();
	
	public static void addQqFriendList(String qqid,QqFriendList qqFriendList)
	{
		hm.put(qqid,qqFriendList);
	}
	//返回好友列表
	public static QqFriendList getQqFriendList(String qqId)
	{
		return (QqFriendList)hm.get(qqId);
	}
}
