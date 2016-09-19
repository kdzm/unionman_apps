package com.unionman.settings.menumanager;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.tools.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class MenuListManager {
	private int index;
	private FrameLayout contantView;
	private Context ctx;
	private static final String TAG = "com.unionman.settings.menumamager--MenuListManager";
	private boolean isfirst = true;
	private LayoutManager layoutManager;
	private MenuListAdapter mAdatper;
	private List<Map<String, Object>> mData;
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong) {
			Logger.i(TAG, "mListView  on item click  position="+paramAnonymousInt + " mPostion="+ MenuListManager.this.mPosition);
			if (paramAnonymousInt == MenuListManager.this.mPosition) {
				MenuListManager.this.contantView.requestFocus();
				return;
			}
		}
	};
	AdapterView.OnItemSelectedListener mItemSelectListener = new AdapterView.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong) {
			Logger.i(TAG, "mListView  on item selected  position="+paramAnonymousInt + " mPostion="+ MenuListManager.this.mPosition);
			try {
				MenuListManager.this.mPosition = paramAnonymousInt;
				MenuListManager.this.mAdatper.setOkPosition(paramAnonymousInt);
				if (MenuListManager.this.isfirst) {
					MenuListManager.this.isfirst = false;
				} else {
					mAdatper.notifyDataSetChanged();
					MenuListManager.this.layoutManager.clearView();
					MenuListManager.this.layoutManager.showLayout(paramAnonymousInt);
				}
			} catch (SecurityException localSecurityException) {
				Logger.e(TAG,localSecurityException.toString());
				return;
			} catch (IllegalArgumentException localIllegalArgumentException) {
				Logger.e(TAG,localIllegalArgumentException.toString());
				return;
			} catch (NoSuchMethodException localNoSuchMethodException) {
				Logger.e(TAG,localNoSuchMethodException.toString());
				return;
			} catch (InvocationTargetException localInvocationTargetException) {
				localInvocationTargetException.printStackTrace();
				Logger.e(TAG,localInvocationTargetException.toString());
				return;
			}
			
			Logger.i(TAG,"onItemSelected return ");

			return;
		}

		public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {
		}
	};
	
	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
		public void onFocusChange(View paramAnonymousView,
				boolean paramAnonymousBoolean) {
			Logger.i(TAG,"mListView  focus change  hasFocus="+paramAnonymousBoolean + " mPostion="+ MenuListManager.this.mPosition);
			if (paramAnonymousBoolean) {
				MenuListManager.this.index = 1;
				((ListView) paramAnonymousView).setSelection(MenuListManager.this.mPosition);
				return;
			}
			MenuListManager.this.index = 0;
			MenuListManager.this.mAdatper.setOkPosition(MenuListManager.this.mPosition);
			//mAdatper.resetBackground();
			intemchange();
			MenuListManager.this.mAdatper.notifyDataSetChanged();
		}
	};
	private ListView mListView;
	private MenuListData mMenuListData;
	private int mPosition;
	private int top;

	public MenuListManager(View paramView, Context paramContext,LayoutManager paramLayoutManager) {
		this.mListView = ((ListView) paramView);
		this.layoutManager = paramLayoutManager;
		this.ctx = paramContext;
		this.mMenuListData = new MenuListData(paramContext);
		this.mPosition = -1;
	}

	public int getPosition() {
		return this.mPosition;
	}

	public int getCount() {
		return this.mAdatper.getCount();
	}

	public int getTop() {
		return this.top;
	}

	private class onkeylisten implements View.OnKeyListener{

		@Override
		public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
			Logger.i(TAG, "mListView  onkey index=" + MenuListManager.this.index + " mPostion=" + MenuListManager.this.mPosition);
				/*if (MenuListManager.this.index == 1) {
					MenuListManager.this.mAdatper.setOkPosition(-1);
					MenuListManager.this.mAdatper.notifyDataSetChanged();
				}*/

				if (paramAnonymousInt == KeyEvent.KEYCODE_DPAD_DOWN || paramAnonymousInt == KeyEvent.KEYCODE_DPAD_UP) {
					mAdatper.resetBackground();
					intemchange();

				}

				if (paramAnonymousInt == KeyEvent.KEYCODE_DPAD_RIGHT) {
					MenuListManager.this.mAdatper.setOkPosition(MenuListManager.this.mPosition);
					MenuListManager.this.mAdatper.notifyDataSetChanged();
				}


			MenuListManager localMenuListManager = MenuListManager.this;
			localMenuListManager.index = (1 + localMenuListManager.index);
			return false;
		}
	}


	public void intemchange()
	{
		this.mData = this.mMenuListData.getMenuData();
		this.mAdatper = new MenuListAdapter(this.ctx, this.mData);
		this.mListView.setAdapter(this.mAdatper);

	}

	public void initLeftMenu() {
		this.mData = this.mMenuListData.getMenuData();
		this.mAdatper = new MenuListAdapter(this.ctx, this.mData);
		this.mListView.setAdapter(this.mAdatper);
		this.mListView.setOnItemClickListener(this.mItemClickListener);
		this.mListView.setOnItemSelectedListener(this.mItemSelectListener);
//		this.mListView.setOnFocusChangeListener(mFocusChangeListener);
//		this.mListView.setOnKeyListener(new View.OnKeyListener() {
//			public boolean onKey(View paramAnonymousView,
//								 int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
//				Logger.i(TAG, "mListView  onkey index=" + MenuListManager.this.index + " mPostion=" + MenuListManager.this.mPosition);
//				/*if (MenuListManager.this.index == 1) {
//					MenuListManager.this.mAdatper.setOkPosition(-1);
//					MenuListManager.this.mAdatper.notifyDataSetChanged();
//				}*/
//				if (paramAnonymousKeyEvent.getAction() == 0) {
//					if (paramAnonymousInt == KeyEvent.KEYCODE_DPAD_DOWN || paramAnonymousInt == KeyEvent.KEYCODE_DPAD_UP) {
//						mAdatper.resetBackground();
//
//					}
//					if (paramAnonymousInt == KeyEvent.KEYCODE_DPAD_RIGHT) {
//						MenuListManager.this.mAdatper.setOkPosition(MenuListManager.this.mPosition);
//						MenuListManager.this.mAdatper.notifyDataSetChanged();
//					}
//				}
//
//				MenuListManager localMenuListManager = MenuListManager.this;
//				localMenuListManager.index = (1 + localMenuListManager.index);
//				return false;
//			}
//		});
	}

	public void initList(int paramInt1, int paramInt2) {
		this.mPosition = paramInt1;
		this.top = paramInt2;
		this.mData = this.mMenuListData.getMenuData();
		this.mAdatper = new MenuListAdapter(this.ctx, this.mData);
		this.mAdatper.setOkPosition(this.mPosition);
		this.mListView.setAdapter(this.mAdatper);
	}

	public void setContantView(FrameLayout paramFrameLayout) {
		this.contantView = paramFrameLayout;
	}

	public void touchModInit() {
		this.mPosition = 0;
		try {
			this.mAdatper.setOkPosition(this.mPosition);
			this.mAdatper.notifyDataSetChanged();
			this.layoutManager.showLayout(this.mPosition);
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
			Logger.e(TAG,localException.toString());
		}
	}
}