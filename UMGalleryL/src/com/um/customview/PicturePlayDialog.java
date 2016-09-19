package com.um.customview;

import com.um.umgallery.R;
import com.um.umgallery.ui.HorizontalArrayPicker;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicturePlayDialog extends Dialog {

	private Context mContext;
	private TextView tvInterl, tvAnim;
	private HorizontalArrayPicker hInterval, hAnim;
	private RelativeLayout rlInteral, rlAnim;

	private boolean isFirst = true;
	private SharedPreferences sp;

	public PicturePlayDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_pictureplay);
		initView();
	}

	private void initView() {
		rlInteral = (RelativeLayout) findViewById(R.id.rl_interal);
		rlAnim = (RelativeLayout) findViewById(R.id.rl_anim);
		if (isFirst) {
			rlInteral.setSelected(true);
			isFirst = false;
		}

		Resources res = mContext.getResources();

		String[] array = res.getStringArray(R.array.slide);
		tvInterl = (TextView) findViewById(R.id.setting_option_item_interl);// 时间间隔
		tvAnim = (TextView) findViewById(R.id.setting_option_item_anim);// 动画

		tvInterl.setText(array[0]);
		tvAnim.setText(array[1]);

		hInterval = (HorizontalArrayPicker) findViewById(R.id.sliding_interval_picker);
		hAnim = (HorizontalArrayPicker) findViewById(R.id.sliding_anim_picker);

		initInteralANDAnim(res);

	}

	private void initInteralANDAnim(Resources res) {
		String[] intervalNames = res
				.getStringArray(R.array.sliding_interval_names);
		int[] intervalValues = res.getIntArray(R.array.sliding_intervals);
		hInterval.setArray(intervalNames, intervalValues);

		String[] animNames = res.getStringArray(R.array.anim_name);
		int[] animValues = res.getIntArray(R.array.anim_values);
		hAnim.setArray(animNames, animValues);

		sp = mContext.getSharedPreferences("sliding_control", 0);
		int animType = sp.getInt("sliding_animtype", 0);
		int interval = sp.getInt("sliding_interval", 3000);
		hAnim.setValue(animType);
		hInterval.setValue(interval);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			rlInteral.setSelected(false);
			rlAnim.setSelected(true);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			rlAnim.setSelected(false);
			rlInteral.setSelected(true);
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			event.startTracking();
			return true;

		}
		return false;
	}

	private void savePreference() {
		int animType = hAnim.getValue();
		int interval = hInterval.getValue();

		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("sliding_animtype", animType);
		editor.putInt("sliding_interval", interval);
		editor.commit();
//		if (listener != null) {
//			listener.onDataChangedListener();
//		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			rlAnim.setSelected(true);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			rlInteral.setSelected(true);
		} else if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)
				&& event.isTracking() && !event.isCanceled()) {
			savePreference();
			onBackPressed();
			this.dismiss();
			return true;
		}
		return false;

	}

	public void setOnDataChangedListener(OnDataChangedListener listener) {
		this.listener = listener;
	}

	OnDataChangedListener listener;

	interface OnDataChangedListener {
		void onDataChangedListener();
	}

}
