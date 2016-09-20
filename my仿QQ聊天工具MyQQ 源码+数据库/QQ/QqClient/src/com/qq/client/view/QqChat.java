/*
 * �������������Ľ���
 * ��Ϊ�ͻ���Ҫ���ڶ�ȡ��״̬����˰�������һ���߳�
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
	JToolBar toolbar;// ������
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
		
		toolbar = new JToolBar("������");
		toolbar.setBounds(new Rectangle(0, 310, 400, 31));
		this.add(toolbar);
		
		jta2 = new JTextArea();
	
		//��ݼ� ctrl+enter
		
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
		
		jb=new JButton("����");
		jb.addActionListener(this);
		jb.setBounds(320,463,70,30);
		this.add(jb);

		
		this.setTitle(ownerId+"���ں�"+friend+"����");
		this.setIconImage((new ImageIcon("image/ͷ��.GIF").getImage()));
		this.setSize(410,530);
		this.setLocation(200, 150);
		this.setVisible(true);
		
	}
	//дһ��������������ʾ��Ϣ
	public void showMessage(Message m)
	{
		String info=m.getSendTime()+"  "+m.getSender()+" �� "+m.getGetter()+" ˵�� "+"\n"+m.getConn()+"\r\n";
		this.jta1.append(info);
		jta1.setCaretPosition(jta1.getText().length()); // ʹ�Զ��»�
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==jb)
		{
			if(jta2.getText().equals("")){
				JOptionPane.showMessageDialog(this, "���벻��Ϊ��","ϵͳ����",JOptionPane.WARNING_MESSAGE);
			}else{
			//����û�����˷��Ͱ�ť
			Message m=new Message();
			m.setMessType(MessageType.message_comm_mes);
			m.setSender(this.ownerId);
			m.setGetter(this.friendId);
			m.setConn(jta2.getText());
			m.setSendTime(new java.util.Date().toString());
			//���͸�������
			try {
				ObjectOutputStream oos=new ObjectOutputStream
				(ManageClientConServerThread.getClientConServerThread(ownerId).getS().getOutputStream());
				oos.writeObject(m);
			} catch (Exception b) {
				// TODO: handle exception
				b.printStackTrace();
			}
			String info1=m.getSendTime()+"  "+m.getSender()+" �� "+m.getGetter()+" ˵�� "+"\n"+m.getConn()+"\r\n";
			this.jta1.append(info1);
			jta1.setCaretPosition(jta1.getText().length()); // ʹ�Զ��»�
			this.jta2.setText("");
		}
	}
	}
}
