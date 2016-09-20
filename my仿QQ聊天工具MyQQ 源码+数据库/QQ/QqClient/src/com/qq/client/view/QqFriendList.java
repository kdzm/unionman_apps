/*
 * 我的好友列表（包括我的同学、黑名单）
 */
package com.qq.client.view;

import com.qq.client.model.QqClientUser;
import com.qq.client.tools.*;
import com.qq.common.Message;
import com.qq.common.User;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
public class QqFriendList extends JFrame implements ActionListener,MouseListener{
	String []friend=new String[50];//该数组保存好友的ID
	int count;//保存好友的数目
	
	//处理第一张卡片（我的好友）
	JLabel jphy_jl;
	JPanel jphy1,jphy2,jphy3,jphy4,jphy5;
	JButton jphy_jb1,jphy_jb2,jphy_jb3,jphy_jb4,jphy_jb5,jphy_jb6;
	JScrollPane jspl;
	String owner;
	
	//处理第二张卡片（我的同学）
	JLabel jpmsr_jl;
	JPanel jpmsr1,jpmsr2,jpmsr3,jpmsr4,jpmsr5;
	JButton jpmsr_jb1,jpmsr_jb2,jpmsr_jb3,jpmsr_jb4,jpmsr_jb5,jpmsr_jb6;
	JScrollPane jspl2;
	JLabel []jbls;
	
	//处理第三张卡片（黑名单）
	JLabel jphmd_jl;
	JPanel jphmd1,jphmd2,jphmd3,jphmd4;
	JButton jphmd_jb1,jphmd_jb2,jphmd_jb3,jphmd_jb4,jphmd_jb5,jphmd_jb6;
	JScrollPane jspl3;
	
	//把整个JFrame设置成CardLayout
	CardLayout cl;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QqFriendList qqFriendList=new QqFriendList("10000");
	}
	
	//查询用户的好友，然后显示出来
	public void showMyFriend(String ownerId)
	{
		QqClientUser qqClientUser=new QqClientUser();
		User u=new User();
		u.setOperation("5");
		u.setQum(ownerId);
		friend=qqClientUser.Sql2(u);
		for(int i=0;i<50;i++)
		{
			//System.out.println(friend[i]);
		}	
		for(int i=0;friend[i]!=null;i++)
			count++;
		//System.out.println("好友的数目为："+count);
	}
	
	//更新在线的好友的情况
	/*public void updateFriend(Message m)
	{
		String onLineFriend[]=m.getConn().split(" ");
		int i=0,j=0;
		for(i=0;i<count;i++)
		{  
			for(j=0;j<onLineFriend.length;j++)
			{
				if(friend[i].equals(onLineFriend[j]))
				{
					jbls[i].setEnabled(true);
					System.out.println("i="+i);
					break;
				}
			}
		}
	}*/
	
	public void updateFriend(Message m)
	{
		String onLineFriend[]=m.getConn().split(" ");
		for(int i=0;i<onLineFriend.length;i++)
		{
		//让第i个标签有效
			jbls[Integer.parseInt(onLineFriend[i])-10000].setEnabled(true);
		}
	}

	
	public QqFriendList(String ownerId)
	{
		this.owner=ownerId;
		
		showMyFriend(owner);
		//处理第一张卡片（显示好友列表）
		jphy_jl=new JLabel(ownerId+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
		jphy_jb1=new JButton("我的好友");
		jphy_jb2=new JButton("我的同学");
		jphy_jb2.addActionListener(this);
		jphy_jb3=new JButton("黑名单");
		jphy_jb3.addActionListener(this);
		jphy_jb4=new JButton("增加好友");
		jphy_jb4.addActionListener(this);
		jphy_jb5=new JButton("删除好友");
		jphy_jb5.addActionListener(this);
		jphy_jb6=new JButton("退出");
		jphy_jb6.addActionListener(this);
		
		jphy1=new JPanel(new BorderLayout());
		//限定只能有50个好友
		jphy2=new JPanel(new GridLayout(50,1,4,4));
		//给jphy2，初始化50个好友
		jbls =new JLabel[50];
		
		
		/*for(int i=0;i<count;i++)
		{
			jbls[i]=new JLabel(friend[i]+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
			jbls[i].setEnabled(false);
			jbls[i].addMouseListener(this);
			jphy2.add(jbls[i]);	
		}*/
		
		for(int i=0;i<jbls.length;i++)
		{
			jbls[i]=new JLabel(i+10000+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
			jbls[i].setEnabled(false);
			if(jbls[i].getText().equals(ownerId))
			{
				jbls[i].setEnabled(true);
			}
			jbls[i].addMouseListener(this);
			jphy2.add(jbls[i]);	
		}
		
	
		
		jphy3=new JPanel(new GridLayout(3,1));
		//把两个按钮加入到jphy3中
		
		
		jphy4=new JPanel(new GridLayout(2,1));
		jphy4.add(jphy_jl);
		jphy4.add(jphy_jb1);
		
		jphy5=new JPanel();
		jphy5.add(jphy_jb4);
		jphy5.add(jphy_jb5);
        jphy5.add(jphy_jb6);
		
		jphy3.add(jphy_jb2);
		jphy3.add(jphy_jb3);
		jphy3.add(jphy5);
		
		jspl=new JScrollPane(jphy2);
		
		//对jphy1初始化
		jphy1.add(jphy4,"North");
		jphy1.add(jspl,"Center");
		jphy1.add(jphy3,"South");
		
		
		
		//处理第二张卡片(显示陌生人)
		jpmsr_jl=new JLabel(ownerId+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
		jpmsr_jb1=new JButton("我的好友");
		jpmsr_jb1.addActionListener(this);
		jpmsr_jb2=new JButton("我的同学");
		jpmsr_jb3=new JButton("黑名单");
		jpmsr_jb3.addActionListener(this);
		jpmsr_jb4=new JButton("增加好友");
		jpmsr_jb4.addActionListener(this);
		jpmsr_jb5=new JButton("删除好友");
		jpmsr_jb5.addActionListener(this);
		jpmsr_jb6=new JButton("退出");
		jpmsr_jb6.addActionListener(this);
		
		
		jpmsr1=new JPanel(new BorderLayout());
		//假定有20个陌生人
		jpmsr2=new JPanel(new GridLayout(20,1,4,4));
		
		//给jpmsr2，初始化20个陌生人
		JLabel []jbls2=new JLabel[20];
		for(int i=0;i<jbls2.length;i++)
		{
			jbls2[i]=new JLabel(i+11000+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
			jbls2[i].setEnabled(false);
			jpmsr2.add(jbls2[i]);	
		}
		
	
		jpmsr3=new JPanel(new GridLayout(3,1));
		//把两个按钮加入到jphy3中
		jpmsr3.add(jpmsr_jl);
		jpmsr3.add(jpmsr_jb1);
		jpmsr3.add(jpmsr_jb2);
		
		jpmsr5=new JPanel();
		jpmsr5.add(jpmsr_jb4);
		jpmsr5.add(jpmsr_jb5);
		jpmsr5.add(jpmsr_jb6);
		
		jpmsr4=new JPanel(new GridLayout(2,1));
		jpmsr4.add(jpmsr_jb3);
		jpmsr4.add(jpmsr5);
		jspl2=new JScrollPane(jpmsr2);
		
		//对jpmsr1初始化
		jpmsr1.add(jpmsr3,"North");
		jpmsr1.add(jspl2,"Center");
		jpmsr1.add(jpmsr4,"South");
		
		
		//处理第三张卡片（显示黑名单）
		jphmd_jl=new JLabel(ownerId+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
		jphmd_jb1=new JButton("我的好友");
		jphmd_jb1.addActionListener(this);
		jphmd_jb2=new JButton("我的同学");
		jphmd_jb2.addActionListener(this);
		jphmd_jb3=new JButton("黑名单");
		jphmd_jb4=new JButton("增加好友");
		jphmd_jb4.addActionListener(this);
		jphmd_jb5=new JButton("删除好友");
		jphmd_jb5.addActionListener(this);
		jphmd_jb6=new JButton("退出");
		jphmd_jb6.addActionListener(this);
		
		jphmd1=new JPanel(new BorderLayout());
		//假定有10个好友
		jphmd2=new JPanel(new GridLayout(10,1,4,4));
		
		//给jphmd2，初始化10个好友
		JLabel []jbls3=new JLabel[10];
		for(int i=0;i<jbls3.length;i++)
		{
			jbls3[i]=new JLabel(i+12000+"",new ImageIcon("image/头像.GIF"),JLabel.LEFT);
			jbls3[i].setEnabled(false);
			jphmd2.add(jbls3[i]);	
		}
		
	
		jphmd3=new JPanel(new GridLayout(4,1));
		jphmd4=new JPanel();
		//把两个按钮加入到jphy3中
		jphmd3.add(jphmd_jl);
		jphmd3.add(jphmd_jb1);
		jphmd3.add(jphmd_jb2);
		jphmd3.add(jphmd_jb3);
		jphmd4.add(jphmd_jb4);
		jphmd4.add(jphmd_jb5);
		jphmd4.add(jphmd_jb6);
		jspl3=new JScrollPane(jphmd2);
		
		//对jphmd1初始化
		jphmd1.add(jphmd3,"North");
		jphmd1.add(jspl3,"Center");
		jphmd1.add(jphmd4,"South");
		
	
		cl=new CardLayout();
		this.setLayout(cl);
		this.add(jphy1,"1");
		this.add(jpmsr1,"2");
		this.add(jphmd1,"3");
		
		//在窗口显示自己的编号
		this.setSize(300,550);
		this.setLocation(900,80);
		this.setTitle("  QQ2013    "+ownerId);
		Image img = Toolkit.getDefaultToolkit().getImage("image/头像.GIF");
		setIconImage(img);
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource()==jphy_jb2 || e.getSource()==jphmd_jb2)//如果点击了我的同学的按钮，就显示第二张卡片
		{
			cl.show(this.getContentPane(),"2");
		}
		else if(e.getSource()==jpmsr_jb1 || e.getSource()==jphmd_jb1)//如果点击了我的好友的按钮，就显示第一张卡片
		{
			cl.show(this.getContentPane(),"1");
		}
		else if(e.getSource()==jphy_jb3 || e.getSource()==jpmsr_jb3)//如果点击了黑名单的按钮，就显示第三张卡片
		{
			cl.show(this.getContentPane(),"3");
		}else if(e.getSource()==jphy_jb4||e.getSource()==jpmsr_jb4||e.getSource()==jphmd_jb4)
		{
			//增加好友
			String friendId = JOptionPane.showInputDialog("请输入要添加好友的QQ号码：");
			QqClientUser qqClientUser=new QqClientUser();
			User u=new User();
			u.setOperation("2");
			u.setQum(owner);
			u.setFriendId(friendId);
			if(qqClientUser.Sql(u))
			{
				JOptionPane.showMessageDialog(this, "添加成功","系统提醒",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(this, "添加失败","系统提醒",JOptionPane.WARNING_MESSAGE);
			}
			
		}else if(e.getSource()==jphy_jb5||e.getSource()==jpmsr_jb5||e.getSource()==jphmd_jb5)
		{
			//删除好友
			String friendId = JOptionPane.showInputDialog("请输入要删除好友的QQ号码：");
			QqClientUser qqClientUser=new QqClientUser();
			User u=new User();
			u.setOperation("2");
			u.setQum(owner);
			u.setFriendId(friendId);
			u.setOperation("4");
			if(qqClientUser.Sql(u))
			{
				JOptionPane.showMessageDialog(this, "删除成功","系统提醒",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(this, "删除失败","系统提醒",JOptionPane.WARNING_MESSAGE);
			}
			
			
		}else if(e.getSource()==jphy_jb6||e.getSource()==jpmsr_jb6||e.getSource()==jphmd_jb6)
		{
			//退出
			//断开与服务器的连接
			System.exit(0);
			
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//响应用户双击的事件，并得到好友的编号
		if(e.getClickCount()==2)//双击
		{
			//得到好友编号
			String friendNo=((JLabel)e.getSource()).getText();
			if(this.owner.equals(friendNo)){
				JOptionPane.showMessageDialog(this, "自己不能和自己聊天","系统提醒",JOptionPane.WARNING_MESSAGE);
			}
			else{
            System.out.println(friendNo);
			QqChat qqChat=new QqChat(this.owner,friendNo);
			//把聊天界面加入管理类中
			ManageQqChat.addQqChat(this.owner+" "+friendNo, qqChat);
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		JLabel jl=(JLabel)e.getSource();
		jl.setForeground(Color.red);
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		JLabel jl=(JLabel)e.getSource();
		jl.setForeground(Color.black);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
