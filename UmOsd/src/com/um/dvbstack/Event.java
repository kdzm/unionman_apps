package com.um.dvbstack;


import android.text.format.Time;


public class Event {

	private Time start;
	private Time end;
	private String name;
	
	public Event()
	{

	}
	public Event(Time s,Time e,String n)
	{
		start = s;
		end	  = e;
		name  = n;
	}
	
	public Time getStartTime()
	{
		return start;
		
	}
	
	public Time getEndTime()
	{
		return end;
	}
	
	public String getName()
	{
		return name;
	}

}
