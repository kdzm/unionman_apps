package com.um.umgallery.control;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.um.customview.UMMenuDialog;
import com.um.customview.UMMenuDialog.OnMKeyChange;
import com.um.customview.UMMenuDialog.OnSlidDataChangedListener;
import com.um.umgallery.control.ExplorerController.SlidingShow;
import com.hisilicon.higallery.core.GalleryCore;
import com.hisilicon.higallery.core.GalleryCore.AnimType;
import com.um.umgallery.ui.HorizontalArrayPicker;
import com.um.umgallery.utils.Utils;
import com.um.umgallery.R;

public class EventController implements Controller, SlidingShow {

	GalleryCore mGalleryCore;
	Context mContext;

	Controller[] mControllers;
	String[] mControllerNames;
	Controller mCurrentController;
	Controller mDefaultController;

	private SlidingController slidingController;

	Dialog mMenuDialog;
	// Dialog mQuitDialog;
	BroadcastReceiver mMediaReceiver;

	private SharedPreferences sp;

	private UMMenuDialog dialogview;

	public EventController(GalleryCore calleryCore, Context context,
			Controller[] controllers, String[] controllerNames) {

		mGalleryCore = calleryCore;
		mContext = context;
		mControllers = controllers;
		mDefaultController = mControllers[0];
		mControllerNames = controllerNames;
		((ExplorerController) mControllers[0]).setSliding(this);

		sp = mContext.getSharedPreferences("sliding_control", 0);
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) {
		// switch (event.getKeyCode()) {
		// case KeyEvent.KEYCODE_MENU:
		// toogleMenu();
		// return true;
		// case KeyEvent.KEYCODE_BACK:
		// if (mCurrentController != mDefaultController) {
		// setController(mDefaultController);
		// } else if (mContext instanceof Activity) {
		// Activity activity = (Activity) mContext;
		// activity.finish();
		// }
		//
		// return true;
		// default:
		// return mCurrentController.onKeyEvent(event);
		// }
		return false;
	}

	@Override
	public boolean onMotionEvent(MotionEvent event) {
		return mCurrentController.onMotionEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (dialogview != null) {
			dialogview
					.setOnSlidDataChangedListener(new OnSlidDataChangedListener() {

						@Override
						public void onSlidDataChangedListener() {
							startSlidingShow();
						}
					});
		}
		if (event.getAction() != KeyEvent.ACTION_DOWN)
			return false;

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			startSlidingShow();

			return true;
		case KeyEvent.KEYCODE_MENU:
			// toogleMenu();
			pictureMenu();
			return true;
		case KeyEvent.KEYCODE_BACK:
			if (mCurrentController != mDefaultController) {
				setController(mDefaultController);
			} else if (mContext instanceof Activity) {
				Activity activity = (Activity) mContext;
				activity.finish();
			}
			return true;
		default:
			return mCurrentController.onKeyEvent(event);
		}
	}

	private void pictureMenu() {
		dialogview = new UMMenuDialog(mContext, R.style.dialog, mControllers);
		dialogview.show();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				dialogview.dismiss();
			}
		}, Utils.MIN_DIALOG_DISMISS);
		dialogview.setOnMKeyChangeListener(new OnMKeyChange() {

			@Override
			public void onMKeyChange(Controller controller) {
				setController(controller);
			}
		});
	}

	@Override
	public void startControl() {
		setController(mDefaultController);
		if (mMediaReceiver == null) {
			mMediaReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					File current = new File(mGalleryCore.getCurrentPath());
					if (!current.exists()) {
						// if (mQuitDialog == null) {
						// AlertDialog.Builder builder = new
						// AlertDialog.Builder(mContext);
						// builder.setMessage(R.string.media_unmount_warnning);
						// builder.setPositiveButton(android.R.string.yes, new
						// OnClickListener() {
						//
						// @Override
						// public void onClick(DialogInterface arg0, int arg1) {
						// Activity activity = (Activity) mContext;
						// activity.finish();
						// }
						// });
						// builder.setNegativeButton(android.R.string.cancel,
						// null);
						// mQuitDialog = builder.create();
						// }
						// mQuitDialog.show();
						Toast.makeText(mContext,
								R.string.media_unmount_warnning,
								Toast.LENGTH_LONG).show();
						Activity activity = (Activity) mContext;
						activity.finish();
					}
				}
			};
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		mContext.registerReceiver(mMediaReceiver, filter);
	}

	@Override
	public void stopControl() {
		if (mMediaReceiver != null) {
			mContext.unregisterReceiver(mMediaReceiver);
			mMediaReceiver = null;
		}
	}

	private void setController(Controller controller) {
		if (mCurrentController != null)
			mCurrentController.stopControl();
		mCurrentController = controller;
		mCurrentController.startControl();
	}

	// private void toogleMenu() {
	// ExplorerController c = (ExplorerController) mControllers[0];
	// if (c.mShowingFailed)
	// return;
	// if (mMenuDialog == null) {
	// mMenuDialog = new MenuDialog(mContext);
	// }
	// if (mMenuDialog.isShowing()) {
	// mMenuDialog.dismiss();
	// } else {
	// mMenuDialog.show();
	// }
	// }

	public class MenuDialog extends Dialog implements
			android.view.View.OnClickListener {
		View mSlidingSettings;
		HorizontalArrayPicker mSlidingInterval;
		HorizontalArrayPicker mSlidingAnim;

		public MenuDialog(Context context) {
			super(context);
			setContentView(R.layout.menu_layout);
			setTitle(R.string.menu);
			Button button = (Button) findViewById(R.id.scale);
			button.setOnClickListener(this);

			button = (Button) findViewById(R.id.rotate);
			button.setOnClickListener(this);

			button = (Button) findViewById(R.id.sliding);
			button.setOnClickListener(this);

			button = (Button) findViewById(R.id.sliding_start);
			button.setOnClickListener(this);

			mSlidingInterval = (HorizontalArrayPicker) findViewById(R.id.sliding_interval_picker);
			mSlidingAnim = (HorizontalArrayPicker) findViewById(R.id.sliding_anim_picker);

			Resources res = context.getResources();
			String[] intervalNames = res
					.getStringArray(R.array.sliding_interval_names);
			int[] intervalValues = res.getIntArray(R.array.sliding_intervals);
			mSlidingInterval.setArray(intervalNames, intervalValues);

			String[] animNames = res.getStringArray(R.array.anim_name);
			int[] animValues = res.getIntArray(R.array.anim_values);
			mSlidingAnim.setArray(animNames, animValues);

			mSlidingSettings = findViewById(R.id.sliding_control);

			SharedPreferences sp = mContext.getSharedPreferences(
					"sliding_control", 0);
			int animType = sp.getInt("sliding_animtype", 0);
			int interval = sp.getInt("sliding_interval", 3000);
			mSlidingAnim.setValue(animType);
			mSlidingInterval.setValue(interval);

			int width = (int) context.getResources().getDimension(
					R.dimen.menu_width);
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.width = width;
			params.height = ViewGroup.LayoutParams.MATCH_PARENT;
			params.gravity = Gravity.LEFT | Gravity.TOP;
			Window w = getWindow();
			w.setWindowAnimations(R.style.MenuDialog);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					|| keyCode == KeyEvent.KEYCODE_MENU) {
				event.startTracking();
				return true;
			}

			return false;
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)
					&& event.isTracking() && !event.isCanceled()) {
				onBackPressed();
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.scale:
				setController(mControllers[1]);
				dismiss();
				break;

			case R.id.rotate:
				setController(mControllers[2]);
				dismiss();
				break;

			// case R.id.sliding_start:
			// SlidingController slidingController = (SlidingController)
			// mControllers[3];
			// slidingController.setAnimation(getAnimation(mSlidingAnim
			// .getValue()));
			// slidingController.setInterval(mSlidingInterval.getValue());
			// setController(slidingController);
			// // savePreference();
			// dismiss();
			// break;
			//
			// case R.id.sliding:
			// // toogleSlidingSettings();
			// break;
			}
		}

		// private void savePreference() {
		// int animType = mSlidingAnim.getValue();
		// int interval = mSlidingInterval.getValue();
		//
		// editor.putInt("sliding_animtype", animType);
		// editor.putInt("sliding_interval", interval);
		// editor.commit();
		// }

		// public void toogleSlidingSettings() {
		// if (mSlidingSettings.getVisibility() == View.VISIBLE) {
		// mSlidingSettings.setVisibility(View.GONE);
		// } else {
		// mSlidingSettings.setVisibility(View.VISIBLE);
		// }
		// }

	}

	AnimType getAnimation(int type) {
		switch (type) {
		case 0:
			return AnimType.ANIM_NONE;
		case 1:
			return AnimType.ANIM_SCALE;
		case 2:
			return AnimType.ANIM_SLIDE;
		case 3:
			return AnimType.ANIM_FADE;
		default:
			return AnimType.ANIM_RANDOM;
		}
	}

	@Override
	public void startSlidingShow() {
		slidingController = (SlidingController) mControllers[3];
		sp = mContext.getSharedPreferences("sliding_control", 0);
		int animType = sp.getInt("sliding_animtype", 0);
		int interval = sp.getInt("sliding_interval", 3000);
		AnimType type = getAnimation(animType);
		slidingController.setAnimation(type);
		slidingController.setInterval(interval);
		setController(slidingController);
	}

	@Override
	public void showMenu() {
		// toogleMenu();
	}

}
