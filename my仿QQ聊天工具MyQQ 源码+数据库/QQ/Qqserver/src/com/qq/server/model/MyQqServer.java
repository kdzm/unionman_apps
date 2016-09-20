/*
 * 这是qq服务器，它在监听，等待某个qq客户端来连接
 */
package com.qq.server.model;
import java.net.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.qq.common.Message;
import com.qq.common.User;
import com.qq.server.view.MyServerFrame;

public class MyQqServer {

	public MyQqServer()
	{
			//在9999端口监听
			System.out.println("我是服务器,在9999监听");
			ServerSocket ss = null;
			try {
				ss=new ServerSocket(9999);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		try{
			//阻塞，等待连接
			while(true)//循环监听
			{
				Socket s=ss.accept();
				
				//接受客户端发来的信息
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				User u=(User)ois.readObject();
				System.out.println("//接受客户端发来的信息"+u.getQum()+" "+u.getPassword());
				Message m=new Message();
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());

		        if(u.getOperation().equals("1"))//数据库的验证登陆
		        {
					if(new QqServerUser().CheckUser(u))
					{
						//返回一个成功登陆的信息包
						m.setMessType("1");
						oos.writeObject(m);
						//这里就单开一个线程。让该线程与该客户端保持通讯
						SerConClientThread scct=new SerConClientThread(s);
						ManageClientThread.addClientThread(u.getQum(),scct);
						//启动与该客户端通信的线程
						scct.start();
						//并通知其他的在线用户
						scct.notifyOther(u.getQum());
					}else{
						m.setMessType("2");
						oos.writeObject(m);
						//关闭连接
					}
		        }else if(u.getOperation().equals("2")){//添加好友
		        	String []pas={u.getQum(),u.getFriendId()};
		        	if(new QqServerUser().SerInsertFriend(pas))
		        	{
		        		m.setMessType("1");
		        		oos.writeObject(m);
		        	}
		        }else if(u.getOperation().equals("3")){//注册信息
		        	String []pas={u.getQum(),u.getPetname(),u.getPassword(),u.getSex()};
		        	if(new QqServerUser().SerInsertpersonalinfo(pas))
		        	{
		        		m.setMessType("1");
		        		oos.writeObject(m);
		        	}
		        }else if(u.getOperation().equals("4")){//删除好友
		        	String []pas={u.getQum(),u.getFriendId()};
		        	if(new QqServerUser().SerDelFriend(pas))
		        	{
		        		m.setMessType("1");
		        		oos.writeObject(m);
		        	}
		        }else if(u.getOperation().equals("5")){//在列表中显示所有的好友
		        	int count=0;
		        	String []pas={u.getQum()};
		        	m.setFriend(new QqServerUser().showFriend(pas));
		        	oos.writeObject(m);
		        	
		        }
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
