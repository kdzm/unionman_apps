package com.um.customview;

import com.um.customview.PicturePlayDialog.OnDataChangedListener;
import com.um.umgallery.R;
import com.um.umgallery.UMGallery;
import com.um.umgallery.control.Controller;
import com.um.umgallery.control.ExplorerController;
import com.um.umgallery.control.RotateController;
import com.um.umgallery.control.ScaleController;
import com.um.umgallery.control.SlidingController;
import com.um.umgallery.utils.Utils;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UMMenuDialog extends Dialog implements OnItemClickListener {

	private Context mContext;
	private ListView lv;
	private View view;

	Controller[] mController;
	private ExplorerController ec;
	Controller mCurrentController;
	Controller mDefaultController;

	private SlidingController slidingController;
	private SharedPreferences sp;

	RotateController rController;
	ScaleController sController;

	PicturePlayDialog pDialog;
	DetailDialog dialog;
	UMMenuDialog mDialog;

	public UMMenuDialog(Context context) {
		super(context);
	}

	public UMMenuDialog(Context context, int theme, Controller[] cl) {
		super(context, theme);
		this.mContext = context;
		this.mController = cl;
		this.ec = (ExplorerController) cl[0];
		this.mDefaultController = mController[0];

		sp = mContext.getSharedPreferences("sliding_control", 0);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_view);
		initView();
	}

	private void initView() {
		String[] resource = mContext.getResources().getStringArray(
				R.array.picturesetting);
		view = findViewById(R.id.menu_containerlayout);
		lv = (ListView) view.findViewById(R.id.menuview_list);
		lv.setAdapter(new DialogAdapter(resource));

		lv.setOnItemClickListener(this);
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

	public boolean onDialogKeyEvent(KeyEvent event) {
		if (rController != null) {
			return rController.onKeyEvent(event);
		} else if (sController != null) {
			return sController.onKeyEvent(event);
		}
		return false;
	}

	class DialogAdapter extends BaseAdapter {

		String[] data;

		public DialogAdapter(String[] data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int arg0) {
			return data[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contenview, ViewGroup parent) {
			Holder holder = null;
			if (contenview == null) {
				holder = new Holder();
				contenview = LayoutInflater.from(mContext).inflate(
						R.layout.menu_view_option, null);
				holder.key = (TextView) contenview
						.findViewById(R.id.setting_option_item_txt);
				holder.value = (TextView) contenview
						.findViewById(R.id.setting_option_item_val);
				holder.leftimg = (ImageView) contenview
						.findViewById(R.id.left_arrow_img);
				holder.rightimg = (ImageView) contenview
						.findViewById(R.id.right_arrow_img);
				contenview.setTag(holder);
			}
			holder = (Holder) contenview.getTag();
			holder.key.setText(data[position]);
			holder.leftimg.setBackgroundColor(color.transparent);
			holder.rightimg.setBackgroundColor(color.transparent);

			return contenview;
		}

		class Holder {
			TextView key, value;
			ImageView leftimg, rightimg;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {

		if (mCurrentController != mDefaultController) {
			Log.i("ccc", "mDefaultControllerPre====" + mDefaultController);
			setController(mDefaultController);
		}
		switch (position) {
		case 0:// 图片信息
			dialog = new DetailDialog(mContext,
					R.style.dialog, ec);
			dialog.show();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					dialog.dismiss();
				}
			}, Utils.MIN_DIALOG_DISMISS);
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					mDialog=new UMMenuDialog(mContext, R.style.dialog , mController);
					mDialog.show();
				}
			});
			break;
		case 1:// 旋转\
			Log.i("ccc", "setController==mController[2]==" + mController[2]);
			rController = (RotateController) mController[2];
			if (onMKeyChange != null) {
				onMKeyChange.onMKeyChange(rController);
			}
			break;
//		case 2:// 缩放
//			Log.i("ccc", "setController==mController[1]==" + mController[1]);
//			sController = (ScaleController) mController[1];
//			if (onMKeyChange != null) {
//				onMKeyChange.onMKeyChange(sController);
//			}
//			break;
		case 2:// 幻灯片
			pDialog = new PicturePlayDialog(mContext, R.style.dialog);
			pDialog.show();
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					pDialog.dismiss();
				}
			}, Utils.MIN_DIALOG_DISMISS);
			pDialog.setOnDataChangedListener(new OnDataChangedListener() {

				@Override
				public void onDataChangedListener() {
					if (changedListener != null) {
						changedListener.onSlidDataChangedListener();
					}
				}
			});

			pDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					if (mDialog == null) {
						mDialog=new UMMenuDialog(mContext, R.style.dialog , mController);
					}
					mDialog.show();
					if (mContext instanceof UMGallery) {
						Log.i("aaa", "mContext=======");
						UMGallery activity = (UMGallery) mContext;
						Utils.showInfo(mContext, activity.mHandler,
								Utils.EXPLORE_MODE);
					}
				}
			});
			break;
		}
		this.dismiss();
	}

	public void setOnSlidDataChangedListener(OnSlidDataChangedListener listener) {
		this.changedListener = listener;
	}

	public OnSlidDataChangedListener changedListener;

	public interface OnSlidDataChangedListener {
		void onSlidDataChangedListener();
	}

	private void setController(Controller controller) {
		// if (mCurrentController != null) {
		// mCurrentController.stopControl();
		// Log.i("ccc", "mCurrentControllerPre====" + mCurrentController);
		// }
		// mCurrentController = controller;
		// Log.i("ccc", "mCurrentControllerAFTER====" + mCurrentController);
		// mCurrentController.startControl();
	}

	public void setOnMKeyChangeListener(OnMKeyChange onMKeyChange) {
		this.onMKeyChange = onMKeyChange;
	}

	OnMKeyChange onMKeyChange;

	public interface OnMKeyChange {
		void onMKeyChange(Controller controller);
	}

}
