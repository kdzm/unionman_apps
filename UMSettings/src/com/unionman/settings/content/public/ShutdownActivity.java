package com.unionman.settings.content;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.picker.OnPickerClickListener;
import com.unionman.settings.custom.picker.TimePicker;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.content.ShutdownReceiver;
import com.unionman.settings.tools.Contants;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.ToastUtil;

import java.util.Calendar;

public class ShutdownActivity extends RightWindowBase {

	private TimePicker mtimePicker;
	private LinearLayout lay_autoClose;
	private CheckRadioButton crb_autoClose;
	private Button btn_confirm;
	private Button btn_cancel;
	private static final String TAG = "com.unionman.settings.content.public--ShutdownActivity--";

	public ShutdownActivity(Context paramContext) {
		super(paramContext);
	}

	public void initData() {
	}

	public void onInvisible() {
	}

	public void onResume() {
		Logger.i(TAG,"onResume()--");
		if (getAutoCloseEnable(context)) {
			this.crb_autoClose.setChecked(true);
			setAutoCloseView(true);
		} else {
			this.crb_autoClose.setChecked(false);
			setAutoCloseView(false);
		}
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = ConstantList.FRAME_OPERATION_DEVICE;
		this.levelId = 1002;
	}

	private void setAutoCloseView(boolean visible) {
		Logger.i(TAG,"setAutoCloseView()--");
		if (visible) {
			this.lay_autoClose.setVisibility(VISIBLE);

			Calendar curCalendar = Calendar.getInstance();
			curCalendar.setTimeInMillis(System.currentTimeMillis());

			long timeMills = getAutoCloseTime(context);
			Calendar closeCalendar = Calendar.getInstance();
			closeCalendar.setTimeInMillis(timeMills);
			if (closeCalendar.before(curCalendar)) {
				setPickerTime(curCalendar);
			} else {
				setPickerTime(closeCalendar);
			}

		} else {
			this.lay_autoClose.setVisibility(GONE);
		}
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.shutdown, this);
		this.mtimePicker = (TimePicker) findViewById(R.id.time_picker);
		this.crb_autoClose = (CheckRadioButton) findViewById(R.id.switch_auto_close);
		this.lay_autoClose = (LinearLayout) findViewById(R.id.ly_auto_close);
		this.btn_confirm = (Button) findViewById(R.id.btn_confirm);
		this.btn_cancel = (Button) findViewById(R.id.btn_cancle);
		this.crb_autoClose.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CheckRadioButton radioButton, boolean onCheck) {
				setAutoCloseView(onCheck);
				setAutoCloseEnable(context, onCheck);
			}
		});
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_cancle:
						//this.crb_autoClose.requestFocus();
						setAutoCloseEnable(context, false);
						layoutManager.backShowView();
						break;
					case R.id.btn_confirm:
						Calendar curCalendar = Calendar.getInstance();
						curCalendar.setTimeInMillis(System.currentTimeMillis());

						Calendar configCalendar = getConfigCalendar();
						if (configCalendar.before(curCalendar)) {
							ToastUtil.showToast(context, R.string.timer_must_after_current);
						} else {
							setAlarm(configCalendar);
							ToastUtil.showToast(context, R.string.auto_close_device_time_set_success);
							layoutManager.backShowView();
						}
						break;
				}
			}
		};
		OnPickerClickListener onPickerClickListener = new OnPickerClickListener() {
			@Override
			public void onClick(View view) {
				btn_confirm.requestFocus();
				switch (view.getId()) {
					case R.id.time_picker:
						break;
				}
			}
		};
		this.mtimePicker.setOnPickerClickListener(onPickerClickListener);
		this.btn_confirm.setOnClickListener(onClickListener);
		this.btn_cancel.setOnClickListener(onClickListener);
	}

	private void setPickerTime(Calendar calendar) {
		Logger.i(TAG,"setPickerTime()--");
		this.mtimePicker.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	}

	private Calendar getConfigCalendar() {
		Logger.i(TAG,"getConfigCalendar()--");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, this.mtimePicker.getCurrentHour());
		calendar.set(Calendar.MINUTE, this.mtimePicker.getCurrentMinute());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	private void setAlarm(Calendar calendar){
		Logger.i(TAG,"setAlarm()--");

		setAutoCloseTime(context, calendar.getTimeInMillis());
		Logger.i(TAG, "target time" + calendar.get(Calendar.YEAR) + "/"
				+ calendar.get(Calendar.MONTH) + "/"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":00:00");

		// create a receiver, when time is up, it will start this
		// receiver.
		Intent intent = new Intent(context.getApplicationContext(), ShutdownReceiver.class);
		intent.setAction("ShutdownReceiver");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), 0, intent, 0);
		AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	public static void setAutoCloseTime(Context context, long time) {
		Logger.i(TAG,"setAutoCloseTime()--");
		Settings.System.putLong(context.getContentResolver(), Contants.KEY_AUTO_CLOSE_TIME, time);
	}

	public static long getAutoCloseTime(Context context) {
		Logger.i(TAG,"getAutoCloseTime()--");
		return Settings.System.getLong(context.getContentResolver(), Contants.KEY_AUTO_CLOSE_TIME, 0l);
	}

	public static void setAutoCloseEnable(Context context, boolean enable) {
		Logger.i(TAG,"setAutoCloseEnable()--");
		Settings.System.putInt(context.getContentResolver(), Contants.KEY_AUTO_CLOSE_ENABLE, enable ? 1 : 0);
	}

	public static boolean getAutoCloseEnable(Context context) {
		Logger.i(TAG,"getAutoCloseEnable()--");
		int value = Settings.System.getInt(context.getContentResolver(), Contants.KEY_AUTO_CLOSE_ENABLE, 0);
		return value == 1;
	}
}
