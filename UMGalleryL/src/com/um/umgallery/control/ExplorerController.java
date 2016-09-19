package com.um.umgallery.control;

import java.util.ArrayList;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcel;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.um.umgallery.R;
import com.hisilicon.higallery.core.GalleryCore;
import com.hisilicon.higallery.core.GalleryCore.Callback;
import com.hisilicon.higallery.core.GalleryCore.CallbackWithUrl;
import com.hisilicon.higallery.core.GalleryCore.Sliding;
import com.hisilicon.higallery.core.GalleryCore.ViewMode;
import com.um.umgallery.load.FileScanner;
import com.um.umgallery.utils.Utils;
import com.um.umgallery.GalleryAdapter;

import android.os.SystemProperties;

@SuppressWarnings("deprecation")
public class ExplorerController implements Controller, Sliding {

	public static final int SCAN_FINISH = 0;
	public static final int SCAN_FAILED = 1;
	public static final int DISMISS_THUM_VIEW = 2;
	public static final int DISMISS_DETAIL_VIEW = 3;
	public static final int UPDATE_LIST = 4;

	private GalleryCore mGalleryCore;
	private Gallery mGallery;
	private GalleryAdapter mGalleryAdapter;
	private ProgressDialog mDialog;
	private String mCurrentPicturePath;
	private ArrayList<String> mFilePath;
	private int mFileSize;
	private boolean mThumViewShowing = true;
	private int mCurrentPosition;
	private Context mContext;
	private Callback mCallback;
	private CallbackWithUrl mCallbackWithUrl;
	private TextView mImgInfo;
	private Handler mInfoHandler;
	private boolean mIsProcessing = false;
	private GestureDetector mDetector;
	private ImageView mFailImage;
	boolean mShowingFailed;
	ProgressDialog mLoadingDialog;
	boolean mOnControl;
	Toast mTipToast;
	private Utils utils;

	static final String TAG = "ExplorerController";

	public static boolean is_2k_GalleryL() {
		return "true".equals(SystemProperties.get("ro.config.2k_galleryl",
				"false"));
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			if (mImgInfo.getVisibility() == View.VISIBLE) {
				mHandler.sendEmptyMessageDelayed(DISMISS_DETAIL_VIEW, 3500);
			}
			switch (msg.what) {
			case SCAN_FINISH:
				mDialog.dismiss();
				mFilePath = (ArrayList<String>) msg.obj;
				mFileSize = mFilePath.size();
				initAdapter();
				break;
			case UPDATE_LIST:
				if (mGalleryAdapter != null)
					mGalleryAdapter.notifyDataSetChanged();
				break;
			case SCAN_FAILED:
				mDialog.dismiss();
				break;
			case DISMISS_THUM_VIEW:
				showThumView(false);
				Log.d(TAG, "HiGalleryL::Hide thumber view");
			case DISMISS_DETAIL_VIEW:
				mImgInfo.setVisibility(View.GONE);
				Log.d(TAG, "HiGalleryL::Hide detail view");
				break;
			}
		}
	};

	public ExplorerController(GalleryCore galleryCore, Context context,
			Gallery gallery, String filePath, TextView info, Handler handler) {
		mGalleryCore = galleryCore;
		mGallery = gallery;
		mGallery.setFocusable(false);
		mGallery.setFocusableInTouchMode(false);
		mContext = context;
		mImgInfo = info;
		Activity activity = (Activity) context;
		mFailImage = (ImageView) activity.findViewById(R.id.fail_img);
		mInfoHandler = handler;
		mDetector = new GestureDetector(context, new MyGestureListener());

		mGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mHandler.removeMessages(DISMISS_THUM_VIEW);
				mHandler.sendEmptyMessageDelayed(DISMISS_THUM_VIEW, 3500);
				mCurrentPosition = arg2;
				mCurrentPicturePath = mFilePath.get(mCurrentPosition);
				viewImage(mCurrentPicturePath);
			}
		});
		/*
		 * mCallback = new Callback() {
		 * 
		 * @Override public void onReceiveCMD(int cmd, Object obj) { if (cmd ==
		 * GalleryCore.CMD_INIT_COMPLETED) { boolean result = (Boolean) obj; if
		 * (result && mCurrentPicturePath != null) {
		 * viewImage(mCurrentPicturePath); } } else if (cmd ==
		 * GalleryCore.CMD_VIEW_COMPLETED) { mIsProcessing = false; boolean
		 * result = (Boolean) obj; if (!result) {
		 * mFailImage.setVisibility(View.VISIBLE); Log.d("ExplorerController",
		 * "=========== onReceiveCMD CMD_VIEW_COMPLETED FailImage []"); } else {
		 * mFailImage.setVisibility(View.INVISIBLE); Log.d("ExplorerController",
		 * "=========== onReceiveCMD CMD_VIEW_COMPLETED success []"); }
		 * mShowingFailed = !result; if (mLoadingDialog != null)
		 * mLoadingDialog.dismiss(); } } };
		 */
		mCallbackWithUrl = new CallbackWithUrl() {
			@Override
			public void onReceiveCMDWithUrl(int cmd, Object obj,Parcel parcel) {
				if (cmd == GalleryCore.CMD_INIT_COMPLETED) {
                                         boolean result = (Boolean) obj;
					if (result && mCurrentPicturePath != null) {
						viewImage(mCurrentPicturePath);
					}
				} else if (cmd == GalleryCore.CMD_VIEW_COMPLETED) {
                                    parcel.setDataPosition(0);
                    String url = parcel.readString();
                    boolean result = (Boolean) obj;
					mIsProcessing = false;
					if (!result) {
						mFailImage.setVisibility(View.VISIBLE);
						Log.d(TAG,
								"=========== onReceiveCMDWithUrl CMD_VIEW_COMPLETED FailImage ["
										+ url + "]");
					} else {
						mFailImage.setVisibility(View.INVISIBLE);
						Log.d(TAG,
								"=========== onReceiveCMDWithUrl CMD_VIEW_COMPLETED success ["
										+ url + "]");
					}
					mShowingFailed = !result;
					if (mLoadingDialog != null)
						mLoadingDialog.dismiss();
				}if (cmd == GalleryCore.CMD_SHOWN_FRAME_CHANGED){
                    Log.d(TAG,"=========== CMD_SHOWN_FRAME_CHANGED");
                    Rect rect = (Rect) obj;
                }
			}
		};
		mTipToast = Toast.makeText(context, R.string.already_last,
				Toast.LENGTH_SHORT);
		initView();
		scanFile(filePath);
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) {
		mHandler.removeMessages(DISMISS_THUM_VIEW);
		mHandler.sendEmptyMessageDelayed(DISMISS_THUM_VIEW, 3500);

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_UP:
			if (!mThumViewShowing) {
				showThumView(true);
				Log.d(TAG, "HiGalleryL::Show thumber view");
				mGallery.setSelection(mCurrentPosition);
			} else {
				showThumView(false);
				Log.d(TAG, "HiGalleryL::Hide thumber view");
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
//			// ///////////寰呭垹锟�?//			if (mImgInfo.getVisibility() != View.VISIBLE) {
//				mHandler.removeMessages(DISMISS_DETAIL_VIEW);
//				Utils.getDetails(mContext, mCurrentPicturePath, mHandler,
//						mImgInfo);
//				Log.d(TAG, "HiGalleryL::Show detail view");
//			} else {
//				mImgInfo.setVisibility(View.GONE);
//				Log.d(TAG, "HiGalleryL::Hide detail view");
//			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (event.getAction() != KeyEvent.ACTION_UP) {
				prev();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (event.getAction() != KeyEvent.ACTION_UP) {
				next();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return onKeyEvent(event);
	}

	@Override
	public void startControl() {
		mGalleryCore.setCallback(mCallback);
		mGalleryCore.setCallbackWithUrl(mCallbackWithUrl);
		showThumView(true);
		Log.d(TAG, "HiGalleryL::Show thumber view");
		Utils.showInfo(mContext, mInfoHandler, Utils.EXPLORE_MODE);
		mOnControl = true;
	}

	@Override
	public void stopControl() {
		showThumView(false);
		Log.d(TAG, "HiGalleryL::Hide thumber view");
		mInfoHandler.sendEmptyMessage(Utils.DISMISS_INFO);
		mOnControl = false;
	}

	private void showThumView(boolean show) {
		mThumViewShowing = show;
		if (mThumViewShowing) {
			mGallery.setVisibility(View.VISIBLE);
			mHandler.removeMessages(DISMISS_THUM_VIEW);
			mHandler.sendEmptyMessageDelayed(DISMISS_THUM_VIEW, 3500);
		} else {
			mGallery.setVisibility(View.GONE);
			mThumViewShowing = false;
		}
	}

	private void initView() {
		WindowManager mWM = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Point screenSize = new Point();
		mWM.getDefaultDisplay().getSize(screenSize);
		int screenWidth = screenSize.x;
		int screenHeight = screenSize.y;
		int size = screenWidth < screenHeight ? screenHeight / 8
				: screenWidth / 8;
		mGalleryAdapter = new GalleryAdapter(mContext, null, mGalleryCore,
				mGallery, size);
		if (!is_2k_GalleryL()) {
			mGallery.setAdapter(mGalleryAdapter);
		}
		// mLoadingDialog = new ProgressDialog(mContext,
		// ProgressDialog.STYLE_SPINNER);
		// mLoadingDialog.setCancelable(false);
		// mLoadingDialog.setMessage(mContext.getResources().getString(R.string.loading));
	}

	private void scanFile(String filePath) {
		mCurrentPicturePath = filePath;
		String parentPath = mCurrentPicturePath.substring(0,
				mCurrentPicturePath.lastIndexOf("/"));
		FileScanner scanner = new FileScanner(mHandler, parentPath);
		new Thread(scanner).start();

		mDialog = new ProgressDialog(mContext);
		mDialog.setMessage(mContext.getString(R.string.scanning));
		mDialog.setIndeterminate(false);
		mDialog.setCancelable(false);
		// mDialog.show();
	}

	private void initAdapter() {
		if (mFilePath != null) {
			mCurrentPosition = mFilePath.indexOf(mCurrentPicturePath);
			mGalleryAdapter.updateDate(mFilePath);
			mGallery.setSelection(mCurrentPosition);
		}
	}

	private void next() {
		if (!mIsProcessing && mCurrentPosition + 1 <= mFileSize - 1) {
			mCurrentPosition++;
			mCurrentPicturePath = mFilePath.get(mCurrentPosition);
			// if (!mThumViewShowing) {
			viewImage(mCurrentPicturePath);
			mImgInfo.setVisibility(View.GONE);
			Log.d(TAG, "HiGalleryL::Hide detail view");
			// } else {
			// mGalleryAdapter.updateDate(mThumPicturePath);
			mGallery.setSelection(mCurrentPosition);
			// }
			mIsProcessing = true;
		} else if (mCurrentPosition + 1 == mFileSize) {
			mTipToast.setText(R.string.already_last);
			mTipToast.show();
		}
	}

	private void prev() {
		if (!mIsProcessing && mCurrentPosition - 1 >= 0) {
			mCurrentPosition--;
			mCurrentPicturePath = mFilePath.get(mCurrentPosition);
			// if (!mThumViewShowing) {
			viewImage(mCurrentPicturePath);
			mImgInfo.setVisibility(View.GONE);
			Log.d(TAG, "HiGalleryL::Hide detail view");
			// } else {
			// mGalleryAdapter.updateDate(mThumPicturePath);
			mGallery.setSelection(mCurrentPosition);
			// }
			mIsProcessing = true;
		} else if (mCurrentPosition == 0) {
			mTipToast.setText(R.string.already_first);
			mTipToast.show();
		}
	}

	public void showNext() {
		mGalleryCore.setCallback(mCallback);
		mGalleryCore.setCallbackWithUrl(mCallbackWithUrl);
		if (mCurrentPosition + 1 > mFileSize - 1) {
			mCurrentPosition = -1;
		}
		next();
	}

	public void recycle() {
		mGalleryAdapter.recycle();
	}

	private void viewImage(String path) {
		mGalleryCore.viewImage(path, ViewMode.AUTO_MODE);
		if (mOnControl && mLoadingDialog != null)
			mLoadingDialog.show();
	}

	class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			if (e1.getX() - e2.getX() > 0 && Math.abs(velocityX) > 200) {
				next();
			} else if (e2.getX() - e1.getX() > 0 && Math.abs(velocityX) > 200) {
				prev();
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (!mThumViewShowing) {
				showThumView(true);
				Log.d(TAG, "HiGalleryL::Show thumber view");
				mGallery.setSelection(mCurrentPosition);
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			showThumView(false);
			Log.d(TAG, "HiGalleryL::Hide thumber view");
			mSlidingShow.startSlidingShow();
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			mSlidingShow.showMenu();
		}
	}

	private SlidingShow mSlidingShow;

	public void setSliding(SlidingShow slidingShow) {
		mSlidingShow = slidingShow;
	}

	public interface SlidingShow {
		void startSlidingShow();

		void showMenu();
	}

	//add by  um
	public void setDetailsInfo(TextView fileName, TextView fileType,
			TextView fileSize, TextView filePicsize, TextView fileModifyTime) {
		if (utils == null) {
			utils = new Utils();
		}
		if (mContext != null || mCurrentPicturePath != null || mHandler != null) {

			utils.setDetails(mContext, mCurrentPicturePath, mHandler, fileName,
					fileType, fileSize, filePicsize, fileModifyTime);
		}
	}
}
