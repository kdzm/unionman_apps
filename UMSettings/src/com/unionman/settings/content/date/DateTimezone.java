package com.unionman.settings.content;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;

public class DateTimezone extends RightWindowBase {
	private static final String TAG = "com.unionman.settings.content.date--DateTimezone";
	private SimpleAdapter mAlphabeticalAdapter;
	private ListView lv_ListZone;
	private SimpleAdapter mTimezoneSortedAdapter;

	public DateTimezone(Context paramContext) {
		super(paramContext);
	}

	private String getLocaleLanguage() {
		Logger.i(TAG,"getLocaleLanguage()--");
		Locale localLocale = Locale.getDefault();
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = localLocale.getLanguage();
		arrayOfObject[1] = localLocale.getCountry();
		return String.format("%s-%s", arrayOfObject);
	}

	private List<HashMap> getZones() {
		Logger.i(TAG,"getZones()--");
		ArrayList localArrayList = new ArrayList();
		long l = Calendar.getInstance().getTimeInMillis();
		try {
			XmlResourceParser localXmlResourceParser = loadXMLByLocaleLanguage();
			while (localXmlResourceParser.next() != 2) {
				UMDebug.umdebug_trace();
				localXmlResourceParser.next();
			}
			while (true) {
				UMDebug.umdebug_trace();
				if (localXmlResourceParser.getEventType() == 3) {
					localXmlResourceParser.close();
					return localArrayList;
				}
				while (localXmlResourceParser.getEventType() != 2) {
					UMDebug.umdebug_trace();
					if (localXmlResourceParser.getEventType() == 1)
						break;
					localXmlResourceParser.next();
				}
				if (localXmlResourceParser.getName().equals("timezone"))
					addItem(localArrayList,
							localXmlResourceParser.getAttributeValue(0),
							localXmlResourceParser.nextText(), l);
				if (localXmlResourceParser.getEventType() != 3)
					break;
				localXmlResourceParser.next();
				UMDebug.umdebug_trace();
			}
		} catch (XmlPullParserException localXmlPullParserException) {
			XmlResourceParser localXmlResourceParser;
			Logger.i(TAG, "localXmlPullParserException");
			return localArrayList;
		} catch (IOException localIOException) {
			Logger.i(TAG, "localIOException");
		}
		return localArrayList;
	}

	private XmlResourceParser loadXMLByLocaleLanguage() {
		Logger.i(TAG,"loadXMLByLocaleLanguage()--");
		String str = getLocaleLanguage();
		if (str.equals("zh-CN"))
			return this.context.getResources().getXml(2130968578);
		if (str.equals("en-US"))
			return this.context.getResources().getXml(2130968577);
		return this.context.getResources().getXml(2130968578);
	}

	private void setSorting(boolean paramBoolean) {
		Logger.i(TAG,"setSorting()--");
		ListView localListView = this.lv_ListZone;
		SimpleAdapter localSimpleAdapter;
		if (paramBoolean) {
			localSimpleAdapter = mTimezoneSortedAdapter;
		} else {
			localSimpleAdapter = mAlphabeticalAdapter;
		}
		// for (SimpleAdapter localSimpleAdapter = this.mTimezoneSortedAdapter;
		// ; localSimpleAdapter = this.mAlphabeticalAdapter)
		// {
		// localListView.setAdapter(localSimpleAdapter);
		// return;
		// }
		localListView.setAdapter(localSimpleAdapter);
	}

	protected void addItem(List<HashMap> paramList, String paramString1,
			String paramString2, long paramLong) {
		Logger.i(TAG,"addItem()--");
		HashMap localHashMap = new HashMap();
		localHashMap.put("id", paramString1);
		localHashMap.put("name", paramString2);
		int i = TimeZone.getTimeZone(paramString1).getOffset(paramLong);
		int j = Math.abs(i);
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("GMT");
		if (i < 0)
			localStringBuilder.append('-');
		while (true) {
			localStringBuilder.append(j / 3600000);
			localStringBuilder.append(':');
			int k = j / 60000 % 60;
			if (k < 10) {
				localStringBuilder.append('0');
			}
			localStringBuilder.append(k);
			localHashMap.put("gmt", localStringBuilder.toString());
			localHashMap.put("offset", Integer.valueOf(i));
			paramList.add(localHashMap);

			localStringBuilder.append('+');
			return;
		}
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		String[] arrayOfString = { "name", "gmt" };
		int[] arrayOfInt = { 16908308, 16908309 };
		MyComparator localMyComparator = new MyComparator("offset");
		List localList = getZones();
		Collections.sort(localList, localMyComparator);
		this.mTimezoneSortedAdapter = new SimpleAdapter(this.context,localList, 2130903091, arrayOfString, arrayOfInt);
		ArrayList localArrayList = new ArrayList(localList);
		localMyComparator.setSortingKey("gmt");
		Collections.sort(localArrayList, localMyComparator);
		this.mAlphabeticalAdapter = new SimpleAdapter(this.context,localArrayList, 2130903091, arrayOfString, arrayOfInt);
		String str = Calendar.getInstance().getTimeZone().getID();
		for (int i = 0;; i++) {
			if (i >= localArrayList.size()) {
				setSorting(false);
				this.lv_ListZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(
									AdapterView<?> paramAnonymousAdapterView,
									View paramAnonymousView,
									int paramAnonymousInt,
									long paramAnonymousLong) {
								Map localMap = (Map) paramAnonymousAdapterView.getItemAtPosition(paramAnonymousInt);
								((AlarmManager) DateTimezone.this.context.getSystemService("alarm")).setTimeZone((String) localMap.get("id"));
								Settings.Secure.putInt(DateTimezone.this.context.getContentResolver(),"timezone", paramAnonymousInt);
								DateTimezone.this.layoutManager.backShowView();
							}
						});
				return;
			}
			if (((HashMap) localArrayList.get(i)).toString().contains(str)) {
				Settings.Secure.putInt(this.context.getContentResolver(),"timezone", i);
			}
		}
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		this.lv_ListZone.requestFocus();
		try {
			int i = Settings.Secure.getInt(this.context.getContentResolver(),"timezone");
			this.lv_ListZone.setSelection(i);
			return;
		} catch (Settings.SettingNotFoundException localSettingNotFoundException) {
			localSettingNotFoundException.printStackTrace();
		}
	}

	public void setId() {
		this.frameId = ConstantList.FRAME_DATE_TIMEZONE;
		this.levelId = 1002;
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.date_timezone, this);
		this.lv_ListZone = ((ListView) findViewById(R.id.date_timezone_list));
	}

	private static class MyComparator implements Comparator<HashMap> {
		private String mSortingKey;

		public MyComparator(String paramString) {
			this.mSortingKey = paramString;
		}

		private boolean isComparable(Object paramObject) {
			return (paramObject != null)&& ((paramObject instanceof Comparable));
		}

		public int compare(HashMap paramHashMap1, HashMap paramHashMap2) {
			Logger.i(TAG,"compare()--");
			Object localObject1 = paramHashMap1.get(this.mSortingKey);
			Object localObject2 = paramHashMap2.get(this.mSortingKey);
			if (!isComparable(localObject1)) {
				if (isComparable(localObject2))
					return 1;
				return 0;
			}
			if (!isComparable(localObject2))
				return -1;
			return ((Comparable) localObject1).compareTo(localObject2);
		}

		public void setSortingKey(String paramString) {
			this.mSortingKey = paramString;
		}
	}
}
