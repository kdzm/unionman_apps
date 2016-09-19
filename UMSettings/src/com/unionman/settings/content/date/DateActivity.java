package com.unionman.settings.content;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;

public class DateActivity extends RightWindowBase {
	private static final String TAG="com.unionman.settings.content.date--DateActivity--";
	private static final String HOURS_12 = "12";
	private static final String HOURS_24 = "24";
	private CheckRadioButton crb_date_format;
	private CheckRadioButton crb_hour12_24;
	private CheckRadioButton crb_ntp;
	private CheckRadioButton crb_timezone;
	private boolean is24;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			super.handleMessage(paramAnonymousMessage);
			DateActivity.this.mHandler.postDelayed(DateActivity.this.runnable, 1000L);
			if (is24) {
				SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
				String str1 = localSimpleDateFormat.format(new Date());
				DateActivity.this.crb_hour12_24.setText2(str1);
			} else {
				SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
				String str2 = localSimpleDateFormat.format(new Date());
				DateActivity.this.crb_hour12_24.setText2(str2);
			}
		}
	};

	Runnable runnable = new Runnable() {
		public void run() {
			DateActivity.this.mHandler.sendEmptyMessage(0);
		}
	};

	public DateActivity(Context paramContext) {
		super(paramContext);
	}

	private char[] formatOffset(int paramInt) {
		int i = paramInt / 1000 / 60;
		char[] arrayOfChar = new char[9];
		arrayOfChar[0] = 'G';
		arrayOfChar[1] = 'M';
		arrayOfChar[2] = 'T';
		if (i < 0) {
			arrayOfChar[3] = '-';
			i = -i;
		} else {
			arrayOfChar[3] = '+';
		}
		while (true) {
			int j = i / 60;
			int k = i % 60;
			arrayOfChar[4] = ((char) (48 + j / 10));
			arrayOfChar[5] = ((char) (48 + j % 10));
			arrayOfChar[6] = ':';
			arrayOfChar[7] = ((char) (48 + k / 10));
			arrayOfChar[8] = ((char) (48 + k % 10));
			Logger.d(TAG,"arrayOfChar[3]" + arrayOfChar[3] + "arrayOfChar[4]=="
					+ arrayOfChar[4] + "arrayOfChar[5]==" + arrayOfChar[5]
					+ "arrayOfChar[6]==" + arrayOfChar[6] + "arrayOfChar[7]=="
					+ arrayOfChar[7] + "arrayOfChar[8]==" + arrayOfChar[8]);
			return arrayOfChar;

		}
	}

	private boolean getAutoState() {
		Logger.i(TAG,"getAutoState()--");
		try {
			int intAutoState = Settings.System.getInt(this.context.getContentResolver(),
					"auto_time");
			return intAutoState > 0;
		} catch (Settings.SettingNotFoundException localSettingNotFoundException) {
		}
		return true;
	}

	private String getDateFormat() {
		Logger.i(TAG,"getDateFormat()--");
		String strDateFormat = Settings.System.getString(this.context.getContentResolver(), "date_format");
		if ((strDateFormat == null) || (strDateFormat.equals("")) || (strDateFormat.equals("2131099670"))) {
			strDateFormat = "";
		}
		return android.text.format.DateFormat.getDateFormatForSetting(
				this.context, strDateFormat).format(Calendar.getInstance().getTime());
	}

	private String getNtpServer() {
		Logger.i(TAG,"getNtpServer()--");
		String strNtpServer = Settings.Secure.getString(
				this.context.getContentResolver(), "ntp_server");
				Logger.i(TAG,"strNtpServer --"+strNtpServer);
		/*if (strNtpServer == null) {
			String strSerialno = SystemProperties.get("ro.serialno", "");
			if (strSerialno.substring(40, 41).equals("0")) {
				strNtpServer = "183.235.3.59";
			}else if (strSerialno.substring(40, 41).equals("1")) {
				strNtpServer = "221.181.100.40";
			}
		}*/
		return strNtpServer;
	}

	private String getTimeZoneText() {
		Logger.i(TAG,"getTimeZoneText()--");
		TimeZone localTimeZone = Calendar.getInstance().getTimeZone();
		boolean bool = localTimeZone.inDaylightTime(new Date());
		StringBuilder localStringBuilder = new StringBuilder();
		int i = localTimeZone.getRawOffset();
		if (bool) {
			int j = localTimeZone.getDSTSavings();
			localStringBuilder.append(formatOffset(j + i)).append(",").append(localTimeZone.getDisplayName(bool, 1));
		} else {
			int j = 0;
			localStringBuilder.append(formatOffset(j + i)).append(",").append(localTimeZone.getDisplayName(bool, 1));
		}
		return localStringBuilder.toString();
	}

	private boolean is24Hour() {
		Logger.i(TAG,"is24Hour()--");
		String string = Settings.System.getString(this.context.getContentResolver(), "time_12_24");
		is24 = android.text.format.DateFormat.is24HourFormat(this.context);
		return is24;
	}

	public void initData() {

	}

	public void onInvisible() {
		mHandler.removeCallbacks(runnable);
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		this.crb_timezone.setText2(getTimeZoneText());
		this.crb_hour12_24.setCheckedState(is24Hour());
		this.crb_date_format.setText2(getDateFormat());
		this.crb_ntp.setText2(getNtpServer());
		mHandler.sendEmptyMessage(0);
	}

	public void setId() {
		frameId = 1;
		levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		layoutInflater.inflate(2130903048, this);
		this.crb_timezone = ((CheckRadioButton) findViewById(R.id.crb_date_timezone));
		this.crb_timezone.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
			public void onCheckedChanged(
					CheckRadioButton paramAnonymousCheckRadioButton,
					boolean paramAnonymousBoolean) {
				try {
					DateActivity.this.layoutManager.showLayout(ConstantList.FRAME_DATE_TIMEZONE);
					return;
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		});
		this.crb_hour12_24 = ((CheckRadioButton) findViewById(R.id.crb_date_timeformat));
		this.crb_hour12_24.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton,boolean paramAnonymousBoolean) {
						ContentResolver localContentResolver = DateActivity.this.context.getContentResolver();
						String str = paramAnonymousBoolean ? HOURS_24 : HOURS_12;
						Settings.System.putString(localContentResolver,"time_12_24", str);
						is24 = paramAnonymousBoolean;
					}
				});
		this.crb_date_format = ((CheckRadioButton) findViewById(R.id.crb_date_dateformat));
		this.crb_date_format.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton,boolean paramAnonymousBoolean) {
						try {
							layoutManager.showLayout(ConstantList.FRAME_DATE_FORMAT);
							return;
						} catch (Exception localException) {
							localException.printStackTrace();
						}
					}
				});
		this.crb_ntp = ((CheckRadioButton) findViewById(R.id.crb_date_ntpset));
		this.crb_ntp.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton,boolean paramAnonymousBoolean) {
				try {
					layoutManager.showLayout(ConstantList.FRAME_DATE_NTP);
					return;
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		});
	}
}
