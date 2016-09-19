package com.unionman.dvbporvidertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.dvbstorage.ProgStorageUtil;
import android.content.ContentValues;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		ProgStorage ps = new ProgStorage(getContentResolver());
//		Log.v("EHONG", "prog count=" + ps.getProgCount());
//		ProgStorageUtil psu = new ProgStorageUtil(ps);
//		ArrayList<ProgInfo> ls = psu.getProgsWithBook();
//		if (ls != null) {
//			for (ProgInfo pi : ls) {
//				pi.printInfo();
//			}
//		} 
//		ls = psu.getProgWithType(ProgInfo.SERVICE_TYPE_RADIO);
//		if (ls != null) {
//			for (int i = 0; i < 10 && i < ls.size(); i++) {
//				ProgInfo pi = ls.get(i);
//				pi.printInfo();
//			}
//		}
//		
//		ProgInfo pi = new ProgInfo(100, 200, 300, 2, "浣犲ソ鍟�);
//		ps.addProg(pi, true);
		
		Log.i("porvider","map--size");
		
		ProgStorage ps = new ProgStorage(getContentResolver());
		ps.addCategory(ContentSchema.CategoryTable.DVBC_ID, ContentSchema.CategoryTable.DVBC);
		ps.addCategory(ContentSchema.CategoryTable.FAV_ID, ContentSchema.CategoryTable.FAV);
		ps.addCategory(ContentSchema.CategoryTable.TV_ID, ContentSchema.CategoryTable.TV);
		Map catMap = ps.getCategory();
		Log.i("porvider","map--size"+catMap.size());
		Iterator iter = catMap.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			int cateID = (Integer)entry.getKey();
			String cateName = (String)entry.getValue();
			Log.i("porvider","cateid:"+cateID+"cateName:"+cateName);	
		}
		
		//ps.delCategory(new int[]{ContentSchema.CategoryTable.FAV_ID});
		
		catMap = ps.getCategory();
		Log.i("porvider","map--size"+catMap.size());
		iter = catMap.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			int cateID = (Integer)entry.getKey();
			String cateName = (String)entry.getValue();
			Log.i("porvider","cateid:"+cateID+"cateName:"+cateName);	
		}
		ps.addProg(new ProgInfo(1, 2, 3, 4,"A123"),true,new int[]{ContentSchema.CategoryTable.TV_ID,ContentSchema.CategoryTable.DVBC_ID});
		ps.addProg(new ProgInfo(2, 2, 3, 4,"B123"),true,new int[]{ContentSchema.CategoryTable.TV_ID,ContentSchema.CategoryTable.DVBC_ID});
		ps.addProg(new ProgInfo(3, 2, 3, 4,"C123"),true,new int[]{ContentSchema.CategoryTable.TV_ID,ContentSchema.CategoryTable.DVBC_ID});
		ps.addProg(new ProgInfo(4, 2, 3, 4,"D123"),true,new int[]{ContentSchema.CategoryTable.TV_ID,ContentSchema.CategoryTable.DVBC_ID,ContentSchema.CategoryTable.FAV_ID});
		ps.addProg(new ProgInfo(5, 2, 3, 4,"D123"),true,new int[]{ContentSchema.CategoryTable.FAV_ID,ContentSchema.CategoryTable.FAV_ID});
		
		ContentValues cv  = new ContentValues();
		cv.put(ContentSchema.ProgsTable.PROG_NAME, "AAAA");
		ps.updateProg(5, cv);
		
		ps.getProgInfo(5).printInfo();
		
		int count = ps.getProgCount(new int[]{ContentSchema.CategoryTable.TV_ID});
		
		Log.i("porvider","pg count:"+count);	
		
		ps.DeletProg(3);
		
		count = ps.getProgCount(new int[]{ContentSchema.CategoryTable.TV_ID});
		
		Log.i("porvider","pg count:"+count);
		
		//ps.DeleCategoryProg(new int[]{ContentSchema.CategoryTable.TV_ID});
		
		count = ps.getProgCount(new int[]{ContentSchema.CategoryTable.FAV_ID,ContentSchema.CategoryTable.TV_ID});
		
		Log.i("porvider","pg count:"+count);
		
		ArrayList<ProgInfo> ls = ps.getProg(new int[]{ContentSchema.CategoryTable.TV_ID}, 0);
		
			if (ls != null) {
			for (ProgInfo pi : ls) {
				pi.printInfo();
			}
			}
			
			boolean exist = ps.isCategoryProgExist(ContentSchema.CategoryTable.TV_ID,3);
			
			Log.i("porvider",exist==true?"exist":"not exist");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
