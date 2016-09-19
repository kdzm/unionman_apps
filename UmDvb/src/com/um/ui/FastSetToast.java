package com.um.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.um.dvb.R;

public class FastSetToast {
	private Context mCx = null;
	private  AlertDialog mFastSetInfoDialog;
	private String mFastSetItem = null;
	private Handler mHandler = new Handler();
	private long mDuration = 3*1000;
	
	public FastSetToast(Context cx) {
		mCx = cx;
		Builder builder = new AlertDialog.Builder(mCx,
				R.style.Dialog_backgroundDimEnabled_false);
		
		mFastSetInfoDialog = builder.create();
	}
	
	public void dismiss() {
		if (mFastSetInfoDialog.isShowing()) {
			mFastSetInfoDialog.dismiss();
		}
		mHandler.removeCallbacksAndMessages(null);
	}
	
	public boolean isShowing(String item) {
		return mFastSetInfoDialog.isShowing() 
				&& mFastSetItem != null
				&& mFastSetItem.equals(item);
	}
	
	public void show(String item, int id) {
		String str = mCx.getResources().getString(id);
		show(item, str);
	}

	public void setDuration(long ms) {
		mDuration = ms;
	}
	
	public void show(String item, String str) {
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mFastSetInfoDialog.dismiss();
				mFastSetItem = null;
			}
		}, mDuration);
		
		if (mFastSetInfoDialog.isShowing() 
				&& mFastSetItem != null 
				&& mFastSetItem.equals(item)) {
			TextView txtInfo =  (TextView) mFastSetInfoDialog.findViewById(R.id.menu_btn);
			txtInfo.setText(str);
			return;
		}
		
		mFastSetInfoDialog.dismiss();
		mFastSetItem = item;
		
		Window window = mFastSetInfoDialog.getWindow();
		
		View view = LayoutInflater.from(mCx).inflate(
				R.layout.selector_view_dialog, null);
		TextView txtInfo = (TextView) view.findViewById(R.id.menu_btn);

		txtInfo.setText(str);
		
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.y = 350;
		window.setAttributes(layoutParams);

		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

		mFastSetInfoDialog.show();
		window.setContentView(view);		   
	}
	
	
}
