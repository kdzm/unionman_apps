package com.um.umgallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.SystemProperties;

import com.um.umgallery.R;
import com.um.umgallery.control.EventController;
import com.um.umgallery.control.ExplorerController;
import com.um.umgallery.control.RotateController;
import com.um.umgallery.control.ScaleController;
import com.um.umgallery.control.SlidingController;
import com.hisilicon.higallery.core.GalleryCore;
import com.um.umgallery.utils.Utils;
import com.um.umgallery.control.Controller;

@SuppressWarnings("deprecation")
public class UMGallery extends Activity implements OnTouchListener {

	public static int VIDEO_LAYER_WIDTH = 3840;
	public static int VIDEO_LAYER_HEIGHT = 2160;

	public static boolean is_2k_GalleryL() {
		return "true".equals(SystemProperties.get("ro.config.2k_galleryl",
				"false"));
	}

	private GalleryCore mGalleryCore;
	// private SurfaceView mSurfaceView;
	private EventController mEventController;
	public LinearLayout mInfoLayout;
	private ExplorerController mExploreController;
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			View lastView = mInfoLayout.getChildAt(1);
			switch (msg.what) {
			case Utils.SHOW_INFO:
				if (lastView != null) {
					mInfoLayout.removeView(lastView);
				}
				View view = (View) msg.obj;
				mInfoLayout.addView(view, 1);
				mHandler.removeMessages(Utils.DISMISS_INFO);
				mHandler.sendEmptyMessageDelayed(Utils.DISMISS_INFO, 3500);
				break;
			case Utils.DISMISS_INFO:
				if (lastView != null) {
					mInfoLayout.removeView(lastView);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hi_gallery);

		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			if (uri.getScheme().equals("file")) {
				// init(uri.toString().substring(7));
				init(uri.getPath());
			}
		} else {
			// finish();
		}
	}

	@Override
	protected void onPause() {
		mGalleryCore.deinit();
		mExploreController.recycle();
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mEventController.stopControl();
		super.onDestroy();
	}

	private void init(String startPath) {
		Gallery gallery = (Gallery) findViewById(R.id.thumbnail_list);
		mGalleryCore = GalleryCore.getGallery(Looper.getMainLooper());
		TextView textView = (TextView) findViewById(R.id.img_info);
		mInfoLayout = (LinearLayout) findViewById(R.id.info_layout);
		mExploreController = new ExplorerController(mGalleryCore, this,
				gallery, startPath, textView, mHandler);
		Controller[] controllers = new Controller[] { mExploreController,
				new ScaleController(mGalleryCore, this, mHandler),
				new RotateController(mGalleryCore, this, mHandler),
				new SlidingController(mGalleryCore, this, mExploreController) };
		String[] controllerNames = getResources().getStringArray(
				R.array.controllers);

		mEventController = new EventController(mGalleryCore, this, controllers,
				controllerNames);
		mEventController.startControl();

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.picture_loading);
		mGalleryCore.setFailBitmap(bitmap);

		// mSurfaceView = (SurfaceView) findViewById(R.id.surface);
		// mSurfaceView.getHolder().addCallback(new Callback2() {
		//
		// @Override
		// public void surfaceDestroyed(SurfaceHolder holder) {
		// mGalleryCore.deinit();
		// }
		//
		// @Override
		// public void surfaceCreated(SurfaceHolder holder) {
		//
		// }
		//
		// @Override
		// public void surfaceChanged(SurfaceHolder holder, int format, int
		// width, int height) {
		// if (width > 0 && height > 0) {
		// mGalleryCore.initWithSurface(holder.getSurface(), width, height);
		// }
		// }
		//
		// @Override
		// public void surfaceRedrawNeeded(SurfaceHolder holder) {
		//
		// }
		// });
		//
		// mSurfaceView.setOnTouchListener(this);
	}

	@Override
	protected void onStart() {
		// mGalleryCore.enablePQ(true);
		if (is_2k_GalleryL()) {
			VIDEO_LAYER_WIDTH = 1920;
			VIDEO_LAYER_HEIGHT = 1080;
		}
		mGalleryCore.init(VIDEO_LAYER_WIDTH, VIDEO_LAYER_HEIGHT);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// mGalleryCore.deinit();
		// mExploreController.recycle();
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("lxc", "onKeyDown " + keyCode);
		return mEventController.onKeyEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mEventController.onMotionEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mEventController.onMotionEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mEventController.dispatchKeyEvent(event);
	}

}
