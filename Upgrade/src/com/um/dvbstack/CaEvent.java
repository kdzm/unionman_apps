package com.um.dvbstack;

import android.text.format.Time;

	public class CaEvent {
	private Time start;
	private Time end;
	final int CAPWDLEN = 8;
	private byte[] capwd= new byte[CAPWDLEN];
	private int pwdlen;
	
	public CaEvent()
	{

	}
	
	public void CaEventSetWortime(Time s,Time e,byte[] pwd, int len)
	{
		start = s;
		end	  = e;
		System.arraycopy(pwd, 0, capwd, 0, CAPWDLEN);
		pwdlen = len;
		
	}
	
	public Time CagetStartTime()
	{
		return start;
		
	}
	
	public Time CagetEndTime()
	{
		return end;
	}
	
	public byte[] CagetPwd()
	{
		return capwd;
	}
    
	public int CagetPwdLen()
	{
		return pwdlen;
	}
    	
	

}
