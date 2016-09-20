/*
 * 功能：是服务器和某个客户端的通信线程
 */

package com.qq.server.model;

import java.util.*;
import java.net.*;
import java.io.*;
import com.qq.common.*;
public class SerConClientThread extends Thread{
	
	Socket s;
	
	public SerConClientThread(Socket s)
	{
		//把服务器与该客户端的连接赋给s
		this.s=s;
	}
	
	//让该线程去通知其他的用户
	
	public void notifyOther(String iam)
	{
		//得到所有的在线的人
		HashMap hm=ManageClientThread.hm;
		Iterator it=hm.keySet().iterator();
		
		while(it.hasNext())
		{
			Message m=new Message();
			m.setConn(iam);
			m.setMessType(MessageType.message_ret_onLineFriend);
			
			//取出在线人的Id
			String onLineUserId=it.next().toString();
			try {
				ObjectOutputStream oos=new ObjectOutputStream(ManageClientThread.getClientThread(onLineUserId).s.getOutputStream());
				m.setGetter(onLineUserId);
				oos.writeObject(m);
			
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void run()
	{
		while(true)
		{
			//这里该线程就可以接收客户端的信息
			try {
				
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				Message m=(Message)ois.readObject();
				
				
				//对从客户端取得的消息进行类型判断，让后做相应的处理
				if(m.getMessType().equals(MessageType.message_comm_mes))
				{
					//取得接收人的通信线程
					SerConClientThread sc=ManageClientThread.getClientThread(m.getGetter());
					ObjectOutputStream oos=new ObjectOutputStream(sc.s.getOutputStream());
					oos.writeObject(m);
				}else if(m.getMessType().equals(MessageType.message_get_onLineFriend))
				{
					//把在服务器的好友给该客户端返回
					String res=ManageClientThread.getAllOnLineUserid();
					Message m2=new Message();
					m2.setMessType(MessageType.message_ret_onLineFriend);
					m2.setConn(res);
					m2.setGetter(m.getSender());
					ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(m2);	
				}
			} catch (Exception e) {
				// TODO: handle exception
				
			}
		}
	}
}
