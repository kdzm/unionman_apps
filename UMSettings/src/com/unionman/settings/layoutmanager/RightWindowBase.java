package com.unionman.settings.layoutmanager;

import com.unionman.settings.UMSettings;
import com.unionman.settings.tools.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public abstract class RightWindowBase extends FrameLayout {
	public Context context;
	public boolean enable;
	public int frameId;
	public LayoutInflater layoutInflater = LayoutInflater.from(getContext());
	public LayoutManager layoutManager;
	public FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(-1, -1);
	public int levelId;
	public static final String TAG="com.unionman.settings.layoutmamager--RightWindowBase--";

	public RightWindowBase(Context paramContext) {
		super(paramContext);
		this.layoutManager = ((UMSettings) paramContext.getApplicationContext()).getLayoutManager();
		this.enable = true;
		this.context = paramContext;
		setId();
		setView();
	}

	public void firstShow() {
		Logger.i(TAG, "firstShow()--");
		initData();
		onResume();
	}

	public int getFrameId() {
		return this.frameId;
	}

	public abstract void initData();

	public abstract void onInvisible();

	public abstract void onResume();

	protected void onVisibilityChanged(View paramView, int paramInt) {
		super.onVisibilityChanged(paramView, paramInt);
	}

	public abstract void setId();

	public abstract void setView();
}