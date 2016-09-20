/*
 * 这是客户端和服务器端保持通信的线程
 * 不断地读取服务器发来的信息
 */
package com.qq.client.tools;

import com.qq.client.view.QqChat;
import com.qq.client.view.QqFriendList;
import com.qq.common.*;
import java.io.*;
import java.net.*;
public class ClientConServerThread extends Thread{

	private Socket s;
	
	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	//构造函数
	public ClientConServerThread(Socket s)
	{
		this.s=s;
	}
	
	public void run()
	{
		while(true)
		{
			ObjectInputStream ois=null;
			Message m=null;
			//不停地读取从服务器端发来的消息
			try {
				ois=new ObjectInputStream(s.getInputStream());
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				m = (Message)ois.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(m.getMessType().equals(MessageType.message_comm_mes))//聊天的内容的
			{
					//把从服务器获得的消息显示到聊天界面
					QqChat qqChat=ManageQqChat.getQqChat(m.getGetter()+" "+m.getSender());
					//显示
					qqChat.showMessage(m);
			}else if(m.getMessType().equals(MessageType.message_ret_onLineFriend)){
					String conn=m.getConn();
					String friends[]=conn.split(" ");
					String getter=m.getGetter();
					//修改相应的好友列表
					QqFriendList qqFriendList=ManageQqFriendList.getQqFriendList(getter);
					//更新在线好友
					if(qqFriendList!=null)
					{
						qqFriendList.updateFriend(m);
					}
			}
		}
	}
}
