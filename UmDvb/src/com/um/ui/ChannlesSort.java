package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class ChannlesSort
{
	private final String TAG = ChannlesSort.class.getSimpleName()+"--NONG";
	private String sortName;
	private List<String> chanNameList;
	private List<String> indexInAllList;
	
	public ChannlesSort(String sortName) 
	{
		this.sortName = sortName;
		chanNameList = new ArrayList<String>();
		indexInAllList = new ArrayList<String>();
	}
	
	public void addChan(String chanName, int indexInAll) 
	{
		this.chanNameList.add(chanName);
		this.indexInAllList.add(String.valueOf(indexInAll));
	}
	
	public String getSortName()
	{
		return this.sortName;
	}
	
	public int getChanIndexInAll(int index) 
	{
		return Integer.parseInt(this.indexInAllList.get(index));
	}
	
	public String getChanName(int index) 
	{
		return chanNameList.get(index);
	}
	
	public ArrayList<HashMap<String, String>> getChanList()
	{
		ArrayList<HashMap<String, String>> chanList = new ArrayList<HashMap<String,String>>();
		for (String chanName : this.chanNameList)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", chanName);
			chanList.add(map);
		}
		return chanList;
	}
	
	
	/*
	 * 测试用的方法，将分类里面包含的节目打印出来
	 */
	public void showChannels()
	{
		for (int i = 0; i<this.chanNameList.size(); i++)
		{
			Log.i(TAG, "类中存在的节目 "+i+"  "+this.chanNameList.get(i));
		}
	}
}