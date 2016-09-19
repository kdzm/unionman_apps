package com.um.controller;

public class Play_Full_Screen
{
	private static boolean mainMemuFlag;
	private static  Play_Full_Screen  mainMenuRecall;
	
	public static Play_Full_Screen GetInstance()
	{
		if(mainMenuRecall == null)
		{
			mainMenuRecall = new Play_Full_Screen();
			mainMemuFlag = false;
		}
		
		return mainMenuRecall;
	}
	
	public void setMainMemuFlag(boolean recallFlag)
	{
		mainMemuFlag = recallFlag;
	}
	public boolean getMainMemuFlag()
	{
		return mainMemuFlag;
	}
}
