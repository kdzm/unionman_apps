package com.um.controller;

public class Play_Last_Prog
{
	public int lastProg;
	public int lastProgMode;

	static Play_Last_Prog me;

	public static Play_Last_Prog GetInstance()
	{
		 if(me == null)
		 {
			 me = new Play_Last_Prog();
		 }
		return me;
			
	}

}
