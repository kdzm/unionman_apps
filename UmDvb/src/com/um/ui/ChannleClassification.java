package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class ChannleClassification
{
	private final String TAG = ChannleClassification.class.getSimpleName()+"--NONG";
	private String classificationName;
	private int selectedChanIndex = 0;
	private List<String> chanNameList;
	private List<String> indexInAllList;
	
	public ChannleClassification(String className) 
	{
		this.classificationName = className;
		chanNameList = new ArrayList<String>();
		indexInAllList = new ArrayList<String>();
	}
	
	public void addChan(String chanName, int indexInAll) 
	{
		this.chanNameList.add(chanName);
		this.indexInAllList.add(String.valueOf(indexInAll));
	}
	
	public void clear() {
		chanNameList.clear();
		indexInAllList.clear();
	}
	
	public String getClassificationName()
	{
		return this.classificationName;
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
		int position=1;
		for (String chanName : this.chanNameList)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", chanName);
			map.put("position",String.format("%03d", position) );
			chanList.add(map);
			position++;
		}
		return chanList;
	}
	
	public void setSelectedChanIndex(int index){
		selectedChanIndex = index;
	}

	public void setSelectedChanIndexInAll(int index){
		selectedChanIndex = 0;
		for (int i=0; i<indexInAllList.size(); i++) 
		{
			String indexInAll = indexInAllList.get(i);
			if (indexInAll.equals(String.valueOf(index)))
			{
				selectedChanIndex = i;
				break;
			}
		}
	}
	
	public int getSelectedChanIndexInAll(){
		return Integer.parseInt(this.indexInAllList.get(selectedChanIndex));
	}
	
	public int getSelectedChanIndex (){
		return selectedChanIndex;
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