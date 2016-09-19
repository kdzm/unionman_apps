package com.um.dvbstack;

import java.io.UnsupportedEncodingException;

public class Operator {
	public  int mNativeContext; // accessed by native methods
	public Operator(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;			
	}
	
	private int operatorid;
	private String operatorname;
	
	public int getoperattorid()
	{
		return operatorid;
	}
	
	public String getName()
	{
		return operatorname;
	}
	
	public void setID(int id)
	{
		try {
			operatorid = id;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void setName(byte[] name)
	{
		try {
			operatorname = new String(name, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}