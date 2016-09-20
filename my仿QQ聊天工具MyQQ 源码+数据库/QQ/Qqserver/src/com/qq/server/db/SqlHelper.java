package com.qq.server.db;

import java.sql.*;
public class SqlHelper {

	//定义需要的对象
	
	String url="jdbc:microsoft:sqlserver://127.0.0.1:1433;databaseName=MyQQ2";
	String user="sa";
	String passwd="123456";
	public boolean SqlUpdate(String sql,String []paras)
	{
		PreparedStatement ps=null;
		Connection ct=null;
		boolean b=true;
		try {
			//1.加载驱动
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			//2.得到连接
			ct=DriverManager.getConnection(url,user,passwd);
		
			ps=ct.prepareStatement(sql);
			//给ps的问号赋值
			for(int i=0;i<paras.length;i++)
			{
				ps.setString(i+1, paras[i]);
			}
			
			//执行操作
			ps.executeUpdate();
			
		} catch (Exception e) {
			b=false;
			e.printStackTrace();
		}finally{
			//关闭资源
			try {
				if(ps!=null)
					ps.close();
				if(ct!=null)
					ct.close();
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return b;
	}
	
	
	public boolean SqlQuery(String sql,String []paras)
	{
		PreparedStatement ps=null;
		Connection ct=null;
		ResultSet rs=null;
		boolean b=true;
	
		try {

	
			//1.加载驱动
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		    //2.得到连接
			ct=DriverManager.getConnection(url,user,passwd);
		    ps=ct.prepareStatement(sql);
			//给ps的问号赋值
			for(int i=0;i<paras.length;i++)
			{
				ps.setString(i+1, paras[i]);
			}
			//执行操作
			rs=ps.executeQuery();
			
			while(rs.next())
			{
				String qnum=rs.getString(1);
				String password=rs.getString(2);;
				System.out.println("QQ号码"+qnum+"密码"+password);	
			}
			rs=ps.executeQuery();
			if(!rs.next())
			{
				b=false;
			}
		} catch (Exception e) {
			b=false;
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			//关闭资源
			try {
				if(rs!=null)
					rs.close();
				if(ps!=null)
					ps.close();
				if(ct!=null)
					ct.close();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return b;
	}
	
	public String[] FriendQuery(String sql,String []paras)
	{
		PreparedStatement ps=null;
		Connection ct=null;
		ResultSet rs=null;
		int count=0;
		String friend[]=new String[50];
		try {
			//1.加载驱动
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			//2.得到连接
			ct=DriverManager.getConnection(url,user,passwd);
		
			ps=ct.prepareStatement(sql);
			//给ps的问号赋值
			for(int i=0;i<paras.length;i++)
			{
				ps.setString(i+1, paras[i]);
			}
			//执行操作
			rs=ps.executeQuery();

			while(rs.next())
			{
        		friend[count++]=rs.getString(1);
				System.out.println("QQ好友:"+friend[count]);	
			};
			
		} catch (Exception e) {
			
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			//关闭资源
			try {
				if(rs!=null)
					rs.close();
				if(ps!=null)
					ps.close();
				if(ct!=null)
					ct.close();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return friend;
	}
	
}
