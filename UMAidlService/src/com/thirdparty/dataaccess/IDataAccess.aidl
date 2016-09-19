package com.thirdparty.dataaccess;
interface IDataAccess
{
	String getSTBData(String dataName, String extData);
	int setSTBData(String dataName, String value, String extData);
}