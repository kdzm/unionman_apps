/*
 * 这是与好友聊天的界面
 * 因为客户端要处于读取的状态，因此把它做成一个线程
 */
package com.qq.client.view;

import com.qq.client.tools.*;
import com.qq.client.model.*;
import com.qq.common.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class QqChat extends JFrame implements ActionListener {

	JTextArea jta1,jta2;

	JButton jb;
	JPanel jp;
	JScrollPane jsp1,jsp2;
	JToolBar toolbar;// 工具栏
	String ownerId;
	String friendId;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QqChat qqChat=new QqChat("1","2");
	}
	public QqChat(String ownerId,String friend)
	{
		this.ownerId=ownerId;
		this.friendId=friend;
		this.setLayout(null);
		
		jta1=new JTextArea();
		jta1.setEditable(false);
		jsp1 = new JScrollPane(jta1);
		jsp1.setBounds(0, 35, 400, 275);
		this.add(jsp1);
		
		toolbar = new JToolBar("工具栏");
		toolbar.setBounds(new Rectangle(0, 310, 400, 31));
		this.add(toolbar);
		
		jta2 = new JTextArea();
	
		//快捷键 ctrl+enter
		
		jta2.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent c){
				if(c.getKeyChar()==KeyEvent.VK_ENTER && (c.isControlDown())){
					jb.doClick();
				}
			}
		});

		jsp2 = new JScrollPane(jta2);
		jsp2.setBounds(0, 342, 400, 120);
		this.add(jsp2);
		
		jb=new JButton("发送");
		jb.addActionListener(this);
		jb.setBounds(320,463,70,30);
		this.add(jb);

		
		this.setTitle(ownerId+"正在和"+friend+"聊天");
		this.setIconImage((new ImageIcon("image/头像.GIF").getImage()));
		this.setSize(410,530);
		this.setLocation(200, 150);
		this.setVisible(true);
		
	}
	//写一个方法，让它显示消息
	public void showMessage(Message m)
	{
		String info=m.getSendTime()+"  "+m.getSender()+" 对 "+m.getGetter()+" 说： "+"\n"+m.getConn()+"\r\n";
		this.jta1.append(info);
		jta1.setCaretPosition(jta1.getText().length()); // 使自动下滑
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==jb)
		{
			if(jta2.getText().equals("")){
				JOptionPane.showMessageDialog(this, "输入不能为空","系统提醒",JOptionPane.WARNING_MESSAGE);
			}else{
			//如果用户点击了发送按钮
			Message m=new Message();
			m.setMessType(MessageType.message_comm_mes);
			m.setSender(this.ownerId);
			m.setGetter(this.friendId);
			m.setConn(jta2.getText());
			m.setSendTime(new java.util.Date().toString());
			//发送给服务器
			try {
				ObjectOutputStream oos=new ObjectOutputStream
				(ManageClientConServerThread.getClientConServerThread(ownerId).getS().getOutputStream());
				oos.writeObject(m);
			} catch (Exception b) {
				// TODO: handle exception
				b.printStackTrace();
			}
			String info1=m.getSendTime()+"  "+m.getSender()+" 对 "+m.getGetter()+" 说： "+"\n"+m.getConn()+"\r\n";
			this.jta1.append(info1);
			jta1.setCaretPosition(jta1.getText().length()); // 使自动下滑
			this.jta2.setText("");
		}
	}
	}
}
