package com.unionman.settings.content;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.LinearLayout;

import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;

import java.util.List;

public class WallpaperActivity extends RightWindowBase {
	private LinearLayout lay_viewRoot;
	private static final String TAG = "com.unionman.settings.content.public--WallpaperActivity--";

	public WallpaperActivity(Context paramContext) {
		super(paramContext);
	}

	public void initData() {

	}

	public void onInvisible() {
		Logger.i(TAG, "onInvisible");
	}

	public void onResume() {

	}

	public void setId() {
		Logger.i(TAG, "setId()--");
		this.frameId = 0;
		this.levelId = 1001;
	}

	public void setView() {
		Logger.i(TAG, "setView()--");
		this.layoutInflater.inflate(R.layout.wallpaper, this);
		this.lay_viewRoot = (LinearLayout) findViewById(R.id.aaa);
		populateWallpaperTypes();
	}

	private void populateWallpaperTypes() {
		Logger.i(TAG, "populateWallpaperTypes()--");
		// Search for activities that satisfy the ACTION_SET_WALLPAPER action
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		final PackageManager pm = context.getPackageManager();
		List<ResolveInfo> rList = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		// Add Preference items for each of the matching activities
		for (ResolveInfo info : rList) {
			final Intent prefIntent = new Intent(intent);
			prefIntent.setComponent(new ComponentName(
					info.activityInfo.packageName, info.activityInfo.name));
			CharSequence label = info.loadLabel(pm);
			if (label == null) label = info.activityInfo.packageName;

			LinearLayout view = (LinearLayout) this.layoutInflater.inflate(R.layout.radio_button, null);
			CheckRadioButton button = (CheckRadioButton) view.getChildAt(1);
			button.setText1(label.toString());
			button.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CheckRadioButton paramCheckRadioButton, boolean paramBoolean) {
					context.startActivity(prefIntent);
				}
			});
			if (rList.indexOf(info) == rList.size() - 1) {
				button.setNextFocusDownId(button.getId());
			} else if (rList.indexOf(info) == 0) {
				button.setNextFocusUpId(button.getId());
			}
			this.lay_viewRoot.addView(view);
		}
	}
}
