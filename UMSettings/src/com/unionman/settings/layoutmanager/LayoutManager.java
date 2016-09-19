package com.unionman.settings.layoutmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.Logger;

import com.unionman.settings.content.PowerSaveActivity;

public class LayoutManager {
	private static final String TAG="LayoutManager";
	private static FrameLayout containerView;
	private static LayoutManager layoutmanager;
	private boolean inputIsShow = false;
	public ArrayList<RightWindowBase> mRightlayouts = new ArrayList<RightWindowBase>();
	private Activity mainActivity;
	private Button nowEditButton;
	private TextView nowEditText;
	private RightWindowBase rightLayoutContent = null;
	private Map<String, Object> wifimap;

	public static LayoutManager getLayoutManager(FrameLayout paramFrameLayout) {
		if (layoutmanager == null) {
			layoutmanager = new LayoutManager();
		}
		containerView = paramFrameLayout;
		return layoutmanager;
	}

	public void LayoutFirstShow(RightWindowBase paramRightWindowBase) {
		Logger.i(TAG,"LayoutFirstShow()--");
		this.rightLayoutContent = paramRightWindowBase;
		containerView.removeAllViews();
		containerView.addView(this.rightLayoutContent,this.rightLayoutContent.layoutParam);
		this.rightLayoutContent.firstShow();
	}

	public void LayoutResume(RightWindowBase paramRightWindowBase) {
		Logger.i(TAG,"LayoutResume()--");
		this.rightLayoutContent = paramRightWindowBase;

		containerView.removeAllViews();
		containerView.addView(rightLayoutContent,rightLayoutContent.layoutParam);

		rightLayoutContent.onResume();
		rightLayoutContent.requestFocus();
	}

	public int backShowView() {
		Logger.i(TAG,"backShowView()--");
		int i = -1001 + this.rightLayoutContent.levelId;
		if ((this.mRightlayouts.size() <= 1) || (this.mRightlayouts == null)|| (-1001 + this.rightLayoutContent.levelId == 0)){
			return -1;
		}
		this.rightLayoutContent = ((RightWindowBase) this.mRightlayouts.get(i - 1));
		containerView.removeAllViews();
		containerView.addView(this.rightLayoutContent);
		this.rightLayoutContent.onResume();
		this.rightLayoutContent.requestFocus();
		return 1;
	}
	
	public int backShowViewClear() {
		Logger.i(TAG,"backShowViewClear()--");
		int i = -1001 + this.rightLayoutContent.levelId;
		if ((this.mRightlayouts.size() <= 1) || (this.mRightlayouts == null)|| (-1001 + this.rightLayoutContent.levelId == 0)){
			return -1;
		}
		this.rightLayoutContent = ((RightWindowBase) this.mRightlayouts.get(i - 1));
		containerView.removeAllViews();
		containerView.addView(this.rightLayoutContent);
		this.rightLayoutContent.onResume();
		this.rightLayoutContent.requestFocus();
		mRightlayouts.remove(mRightlayouts.size()-1);
		return 1;
	}

	public void clearView() {
		Logger.i(TAG,"LayoutFirstShow()--");
		this.mRightlayouts.clear();
		containerView.removeAllViews();
	}

	public void getFocus(int paramInt) {
		Logger.i(TAG,"getFocus()--");
		((RightWindowBase) this.mRightlayouts.get(paramInt)).requestFocus();
	}

	public Activity getMainActivity() {
		return this.mainActivity;
	}

	public Button getNowButton() {
		return this.nowEditButton;
	}

	public TextView getNowEdit() {
		return this.nowEditText;
	}

	public Map<String, Object> getWifimap() {
		return this.wifimap;
	}

	public boolean isInputIsShow() {
		return this.inputIsShow;
	}

	public void setInputIsShow(boolean paramBoolean) {
		this.inputIsShow = paramBoolean;
	}

	public void setMainActivity(Activity paramActivity) {
		this.mainActivity = paramActivity;
	}

	public void setNowButton(Button paramButton) {
		this.nowEditButton = paramButton;
	}

	public void setNowEdit(TextView paramTextView) {
		this.nowEditText = paramTextView;
	}

	public void setWifimap(Map<String, Object> paramMap) {
		this.wifimap = paramMap;
	}

	public void showLayout(int paramInt) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		Logger.i(TAG, "showLayout()--");
		Logger.i(TAG, Debug.getCallers(5));
		String rightLayoutContentClassName = null;
		String framelayoutClassName = null;
		Logger.i(TAG, "paramInt=" + paramInt);
		framelayoutClassName = (String) ConstantList.classname.get(Integer.valueOf(paramInt));
		if (this.rightLayoutContent == null) {
			if (framelayoutClassName == null) {
				Logger.i(TAG, "no view");
				Toast.makeText(containerView.getContext(), "椤甸潰涓嶅瓨鍦�", Toast.LENGTH_LONG).show();
				return;
			}
		}
		Logger.i(TAG,"rightLayoutContentClassName=" + rightLayoutContentClassName + ",framelayoutClassName= " + framelayoutClassName);
		while ((rightLayoutContentClassName != null) && ((rightLayoutContentClassName == null) 
				|| (rightLayoutContentClassName.equals(framelayoutClassName)))) {
			rightLayoutContentClassName = this.rightLayoutContent.getClass().getName();
			Logger.i(TAG, "rightLayoutContentClassName="+rightLayoutContentClassName);
			return;
		}
		RightWindowBase localRightWindowBase;
		try {
			Logger.i(TAG, "framelayoutClassName="+framelayoutClassName);
			Constructor localConstructor = Class.forName(framelayoutClassName).getConstructor(
					new Class[] { Context.class });
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = containerView.getContext();
			Log.v(TAG, "framelayoutclassname="+framelayoutClassName);
			if(framelayoutClassName.equals("com.unionman.settings.content.PowerSaveActivity")){
				/*
				 * 节能设置模块无法通过反射方式创建实例，很奇怪。
				 * */
				localRightWindowBase = new PowerSaveActivity(containerView.getContext());
			}else
				localRightWindowBase = (RightWindowBase) localConstructor.newInstance(arrayOfObject);
			
			Logger.i(TAG, "localRightWindowBase=" + localRightWindowBase);
			Logger.i(TAG, "mRightlayouts.size()="+ this.mRightlayouts.size());
			if (this.mRightlayouts.size() <= 0) {
				LayoutFirstShow(localRightWindowBase);
				this.mRightlayouts.add(this.rightLayoutContent);
				return;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			return;
		}
		for (int i = 0; i < this.mRightlayouts.size(); i++) {
			Logger.i(TAG, ""+((RightWindowBase) this.mRightlayouts.get(i)).getClass().getName());
			if (framelayoutClassName.equals(((RightWindowBase) this.mRightlayouts.get(i)).getClass().getName())) {
				this.rightLayoutContent = ((RightWindowBase) this.mRightlayouts.get(i));
				LayoutResume(this.rightLayoutContent);
				this.mRightlayouts.remove(this.rightLayoutContent);
				this.mRightlayouts.add(-1001 + this.rightLayoutContent.levelId, this.rightLayoutContent);
				return;
			}
		}
		this.mRightlayouts.add(-1001 + localRightWindowBase.levelId, localRightWindowBase);
		LayoutFirstShow(localRightWindowBase);
		Logger.i(TAG, "rightLayoutContent.getClass().getName()="+this.rightLayoutContent.getClass().getName());
		return;
	}
}
