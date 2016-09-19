package com.unionman.factorytestassist.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.util.Log;

public class FileLog {
	private static final String TAG = "FileLog";;
	private static final String LOG_FILE = "/data/fac_test_assist.log";
	
	private static BufferedWriter mWriter = null;
	private static FileLog mInstance = null;
	
	private FileLog() {
		
	}
	
	synchronized public static FileLog getInstance() {
		if (mInstance == null) {
			try {
				mWriter = new BufferedWriter(new FileWriter(
						new File(LOG_FILE)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mInstance = new FileLog();
		}
		return mInstance;
	}
	
	private String getCurrentTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		String date =  sDateFormat.format(new java.util.Date());
		return date;
	}
	
	public void print(String tag, String str) {
		Log.v(tag, str);
		if (mWriter != null) {
			try {
				mWriter.write("["+getCurrentTime()+"] <"+tag+">: "+ str + "\n\r");
				mWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
