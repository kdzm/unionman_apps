package com.um.customview;

import com.um.umgallery.R;
import com.um.umgallery.control.ExplorerController;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class DetailDialog extends Dialog {

	private View view;
	private ExplorerController ec;

	public DetailDialog(Context context) {
		super(context);
	}

	public DetailDialog(Context context, int theme, ExplorerController ec) {
		super(context, theme);
		this.ec = ec;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_back);
		initView();
	}

	private void initView() {
		view = findViewById(R.id.dailogbg);
		TextView fileName = (TextView) view.findViewById(R.id.file_name);
		TextView fileType = (TextView) view.findViewById(R.id.file_type);
		TextView fileSize = (TextView) view.findViewById(R.id.file_size);
		TextView filePicsize = (TextView) view.findViewById(R.id.file_picsize);
		TextView fileModifyTime = (TextView) view
				.findViewById(R.id.file_modify);
		ec.setDetailsInfo(fileName, fileType, fileSize, filePicsize,
				fileModifyTime);
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
	
}
