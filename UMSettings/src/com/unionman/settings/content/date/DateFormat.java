package com.unionman.settings.content;

import android.content.Context;
import android.widget.ListView;
import java.util.Calendar;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;


import android.provider.Settings;

public class DateFormat extends RightWindowBase {
	int current = -1;
	String[] dateFormats;
	private CheckRadioButton crb_format_1;
	private CheckRadioButton crb_format_2;
	private CheckRadioButton crb_format_3;
	private CheckRadioButton crb_format_local;
	private Calendar mDummyDate;
	private static final String TAG="com.unionman.settings.content.date--DateFormat--";
	private CheckRadioButton.OnCheckedChangeListener mOnCheckedChangeListener = new CheckRadioButton.OnCheckedChangeListener() {
		public void onCheckedChanged(
				CheckRadioButton paramAnonymousCheckRadioButton,
				boolean paramAnonymousBoolean) {
			if (paramAnonymousBoolean) {
				if (paramAnonymousCheckRadioButton == DateFormat.this.crb_format_local) {
					DateFormat.this.crb_format_local.setCheckedState(true);
					DateFormat.this.crb_format_1.setCheckedState(false);
					DateFormat.this.crb_format_2.setCheckedState(false);
					DateFormat.this.crb_format_3.setCheckedState(false);
					Settings.System.putString(DateFormat.this.context.getContentResolver(),"date_format", DateFormat.this.dateFormats[0]);
				} else if (paramAnonymousCheckRadioButton == DateFormat.this.crb_format_1) {
					DateFormat.this.crb_format_local.setCheckedState(false);
					DateFormat.this.crb_format_1.setCheckedState(true);
					DateFormat.this.crb_format_2.setCheckedState(false);
					DateFormat.this.crb_format_3.setCheckedState(false);
					Settings.System.putString(DateFormat.this.context.getContentResolver(),"date_format", DateFormat.this.dateFormats[1]);
				} else if (paramAnonymousCheckRadioButton == DateFormat.this.crb_format_2) {
					DateFormat.this.crb_format_local.setCheckedState(false);
					DateFormat.this.crb_format_2.setCheckedState(true);
					DateFormat.this.crb_format_1.setCheckedState(false);
					DateFormat.this.crb_format_3.setCheckedState(false);
					Settings.System.putString(DateFormat.this.context.getContentResolver(),"date_format", DateFormat.this.dateFormats[2]);
				} else {
					DateFormat.this.crb_format_3.setCheckedState(true);
					DateFormat.this.crb_format_2.setCheckedState(false);
					DateFormat.this.crb_format_1.setCheckedState(false);
					DateFormat.this.crb_format_local.setCheckedState(false);
					Settings.System.putString(DateFormat.this.context.getContentResolver(),"date_format", DateFormat.this.dateFormats[3]);
				}
			}
			DateFormat.this.layoutManager.backShowView();
		}
	};

	public DateFormat(Context paramContext) {
		super(paramContext);
	}

	public void initData() {
		Logger.i(TAG,"initData()--");
		this.crb_format_local.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
		this.crb_format_1.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
		this.crb_format_2.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
		this.crb_format_3.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
		this.mDummyDate = Calendar.getInstance();
		String[] arrayOfString = new String[4];
		for (int i = 0; i < 4; i++) {
			String str = android.text.format.DateFormat
					.getDateFormatForSetting(this.context, this.dateFormats[i])
					.format(this.mDummyDate.getTime());
			if (dateFormats[i].length() == 0) {
				arrayOfString[i] = getResources().getString(R.string.date_date_format_local,new Object[] { str });
			} else {
				arrayOfString[i] = str;
			}
		}
		this.crb_format_local.setText1(arrayOfString[0]);
		this.crb_format_1.setText1(arrayOfString[1]);
		this.crb_format_2.setText1(arrayOfString[2]);
		this.crb_format_3.setText1(arrayOfString[3]);
		return;
	}

	public void onInvisible() {
		this.current = -1;
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		String str = Settings.System.getString(this.context.getContentResolver(), "date_format");
		if ((str == null) || (str.equals("")) || (str.equals("2131099670")))
			str = "null";
		this.current = -1;
		for (int i = 0; i < 4; i++) {
			if (str.equals(this.dateFormats[i])) {
				this.current = i;
				break;
			}
		}
		switch (this.current) {
		case 0:
			this.crb_format_local.setCheckedState(true);
			this.crb_format_local.requestFocus();
			break;
		case 1:
			this.crb_format_1.setCheckedState(true);
			this.crb_format_1.requestFocus();
			break;
		case 2:
			this.crb_format_2.setCheckedState(true);
			this.crb_format_2.requestFocus();
			break;
		case 3:
			this.crb_format_3.setCheckedState(true);
			this.crb_format_3.requestFocus();
			break;
		default:
			this.crb_format_local.setCheckedState(true);
			this.crb_format_local.requestFocus();
			break;
		}
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = ConstantList.FRAME_DATE_FORMAT;
		this.levelId = 1002;
		this.dateFormats = new String[] { "", "MM-dd-yyyy", "dd-MM-yyyy","yyyy-MM-dd" };
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.date_format, this);
		this.crb_format_local = ((CheckRadioButton) findViewById(R.id.crb_date_format_local));
		this.crb_format_1 = ((CheckRadioButton) findViewById(R.id.crb_date_format_1));
		this.crb_format_2 = ((CheckRadioButton) findViewById(R.id.crb_date_format_2));
		this.crb_format_3 = ((CheckRadioButton) findViewById(R.id.crb_date_format_3));
	}
}
