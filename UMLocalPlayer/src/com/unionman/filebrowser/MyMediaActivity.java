package com.unionman.filebrowser;

import java.io.File;
import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.filebrowser.R;
import com.unionman.filebrowser.localplayer.BaseActivity;
import com.unionman.filebrowser.localplayer.CustomApplication;
import com.unionman.filebrowser.localplayer.CustomProgressDialog;
import com.unionman.filebrowser.localplayer.BaseActivity.MyThreadBase;
import com.unionman.filebrowser.localplayer.adapter.DeviceAdapter;
import com.unionman.filebrowser.localplayer.adapter.FileAdapter;
import com.unionman.filebrowser.localplayer.adapter.FileAdapter.ViewHolder;
import com.unionman.filebrowser.localplayer.bean.GroupInfo;
import com.unionman.filebrowser.localplayer.util.AnimationEffect;
import com.unionman.filebrowser.localplayer.util.FileUtil;
import com.unionman.filebrowser.localplayer.util.FilterType;
import com.unionman.filebrowser.localplayer.util.MountInfo;
import com.unionman.filebrowser.localplayer.util.OpenFiles;
import com.unionman.filebrowser.localplayer.util.PreferenceUtil;
import com.unionman.filebrowser.localplayer.util.ToastUtil;

public class MyMediaActivity extends BaseActivity implements
		OnFocusChangeListener {
	public final String TAG = "LocalPlayer--MainActivity";

	final static String MOUNT_LABLE = "mountLable";

	final static String MOUNT_TYPE = "mountType";

	final static String MOUNT_PATH = "mountPath";

	final static String MOUNT_NAME = "mountName";

	final static int DELETE_POSITION = 0;
	final static int SEARCH_POSITION = 1;
	final static int ORDER_POSITION = 2;
	final private static int SORT_FILE_OK = 0x11;
	final private static int SEARCH_FILE_OK = 0x12;
	final private static int DELETE_FILE_OK = 0x13;
	final private static int DELETE_FILE_ERRO = 0x14;

	private final static int DELETE_SUCCEED_DIALOG = 0x21;
	private final static int DELETE_ASK_DIALOG = 0x22;

	private AnimationEffect animEffect;
	private LayoutInflater mInflater;

	/**
	 * 挂载设备
	 */
	private LinearLayout ll_mounted_equipment;
	private GridView gvMountedDevice;
	private ImageView ivDeviceIcon;
	private TextView tvDeviceName;
	/**
	 * 版本号
	 */
	private TextView tvVersion;

	private LinearLayout ll_choose_type;
	private ImageButton ibTypeAll;
	private ImageButton ibTypeMoive;
	private ImageButton ibTypeMusic;
	private ImageButton ibTypeImage;
	private ImageView ivCopyAll;
	private ImageView ivCopyMoive;
	private ImageView ivCopyMusic;
	private ImageView ivCopyImage;
	/**
	 * 文件管理功能组件
	 */
	private LinearLayout ll_bottom_menu;
	private GridView gv_bottomMenu;
	private CheckBox lv_item_checkbox;
	private int[] gv_bottomMenu_icon = { R.drawable.delete, R.drawable.search,
			R.drawable.order };
	private String[] gv_bottomMenu_name = new String[3];
	private Boolean isCheckBoxSelected = false;
	private static Context context;
	private int isSelectedNumber = 0;
	private RelativeLayout rl_check_all;
	private CheckBox cb_check_all;

	/**
	 * 设备类型节点集合
	 */
	List<GroupInfo> groupList;
	/**
	 * 设备子节点集合
	 */
	List<Map<String, String>> childList;
	/**
	 * 存放点击位置集合
	 */
	List<Integer> intList;
	int clickPos = 0;
	/**
	 * 文件列表的点击位置
	 */
	private int myPosition = 0;
	FileUtil util;
	int menu_item = 0;
	/**
	 * 操作列表
	 */
	ListView list;

	/**
	 * 选中的文件列表
	 */
	List<String> selectList = null;
	private String directorys = "";
	private String parentPath = "";
	/**
	 * 索引显示
	 */
	TextView numInfo;
	int clickCount = 0;
	/**
	 * 设备列表类别位置
	 */
	int groupPosition = -1;

	/**
	 * 所选存储设备标识
	 */
	private int selectedEquipment = 0;

	List<Map<String, String>> groupmap;

	/**
	 * 提示
	 */
	private TextView tvNotice;

	private BroadcastReceiver usbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			mHandler.removeMessages(0);
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessageDelayed(msg, 1000);
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				ToastUtil.showToast(context, Toast.LENGTH_SHORT,
						getString(R.string.install_equi));
				Log.i(TAG, "install_equi");
			}
			if (action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				ToastUtil.showToast(context, Toast.LENGTH_SHORT,
						getString(R.string.uninstall_equi));
				Log.i(TAG, "uninstall_equi");
			}
		}
	};

	/*** add for zhejiang chinamobile begin **/
	private static final String ACTION_VIDEO = "android.intent.action.localvideo";
	private static final String ACTION_PICTURE = "android.intent.action.localpicture";
	private static final String ACTION_MUSIC = "android.intent.action.localmusic";
	private static final String ACTION_FILE = "android.intent.action.localfile";
	private int intFromWhere = 0;
	private static final int FROM_VIDEO = 1;
	private static final int FROM_MUSIC = 2;
	private static final int FROM_PICTURE = 3;
	private static final int FROM_FILE = 4;
	private TextView tvDevNotice;
	/*** add for zhejiang chinamobile end **/

	private Handler mHandler = new Handler() {
		List<File> updateList;

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				refeshList();
				break;
			case SORT_FILE_OK:
				updateList = (List<File>) msg.obj;
				refresh(updateList);
				break;
			case SEARCH_FILE_OK:
				updateList = (List<File>) msg.obj;
				refresh(updateList);
				break;
			case DELETE_FILE_OK:
				listFile.removeAll((List<File>) msg.obj);
				updateList = listFile;
				refresh(updateList);
				showCheckBox(false);
				ToastUtil.toast(context.getResources().getString(
						R.string.deleted_succeed));
				gv_bottomMenu.setVisibility(View.GONE);
				isSelectedNumber = 0;
				cb_check_all.setChecked(false);
				break;
			case DELETE_FILE_ERRO:
				showCheckBox(false);
				ToastUtil.toast(context.getResources().getString(
						R.string.delete_error));
				gv_bottomMenu.setVisibility(View.GONE);
				isSelectedNumber = 0;
				cb_check_all.setChecked(false);
				
				break;
			default:
				break;
			}

		}

	};

	/**
	 * @Description:refresh data after files sorted or searched.
	 * 
	 */
	private void refresh(List<File> updateList) {
		showCheckBox(false);
		if (updateList.size() == 0 || updateList == null) {
			Toast.makeText(this, R.string.no_file, Toast.LENGTH_SHORT).show();
		} else {
			adapter.list = updateList;
			adapter.initSelectedMapData();
			adapter.notifyDataSetChanged();
			listFile = updateList;
			/*
			 * if (listView.getVisibility() == View.VISIBLE) { adapter = new
			 * FileAdapter(MainActivity.this, listFile, listlayout, handler,
			 * false); listView.setAdapter(adapter); } else if
			 * (gridView.getVisibility() == View.VISIBLE) { adapter = new
			 * FileAdapter(MainActivity.this, listFile, gridlayout, handler,
			 * false); gridView.setAdapter(adapter); }
			 */

		}

	}

	private void refeshList() {
		getMountEquipmentList();
		ll_choose_type.setVisibility(View.INVISIBLE);
		listView.setVisibility(View.INVISIBLE);
		gridView.setVisibility(View.INVISIBLE);
		ll_mounted_equipment.setVisibility(View.VISIBLE);
		showBtn.setVisibility(View.INVISIBLE);
		intList.clear();
		numInfo.setText("");
		pathTxt.setText("");
		updateGridViewNumColumns(childList);
		DeviceAdapter adapter = new DeviceAdapter(childList,
				MyMediaActivity.this);
		gvMountedDevice.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*** add for zhejiang chinamobile begin **/
		Intent mIntent = getIntent();
		if (mIntent != null) {
			String strAction = mIntent.getAction();
			if (strAction != null) {
				if (strAction.equals(ACTION_VIDEO)) {
					intFromWhere = FROM_VIDEO;
				} else if (strAction.equals(ACTION_MUSIC)) {
					intFromWhere = FROM_MUSIC;
				} else if (strAction.equals(ACTION_PICTURE)) {
					intFromWhere = FROM_PICTURE;
				} else if (strAction.equals(ACTION_FILE)) {
					intFromWhere = FROM_FILE;
				} else {
					intFromWhere = 0;
				}
			}
		}
		/*** add for zhejiang chinamobile end **/

		setBottomMenu_nameArr();
		context = CustomApplication.getContext();
		FilterType.filterType(MyMediaActivity.this);
		setContentView(R.layout.main);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		selectList = new ArrayList<String>();
		init();
		registerUSB();
		setBottomMenuGridview();
	}

	/**
	 * @Description:set the gridView Item name array
	 * 
	 */
	private void setBottomMenu_nameArr() {
		gv_bottomMenu_name[DELETE_POSITION] = this.getResources().getString(
				R.string.delete);
		gv_bottomMenu_name[SEARCH_POSITION] = this.getResources().getString(
				R.string.search);
		gv_bottomMenu_name[ORDER_POSITION] = this.getResources().getString(
				R.string.sort);
	}

	/**
	 * @Description:set bottom menu
	 * 
	 */
	private void setBottomMenuGridview() {
		String[] from = { "grid_bottommenu_image", "grid_bottommenu_text" };
		int[] to = { R.id.grid_bottommenu_image, R.id.grid_bottommenu_text };
		List<Map<String, Object>> gv_bottmMenu_List = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < gv_bottomMenu_icon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("grid_bottommenu_image", gv_bottomMenu_icon[i]);
			map.put("grid_bottommenu_text", gv_bottomMenu_name[i]);
			gv_bottmMenu_List.add(map);
		}
		SimpleAdapter gv_bottmMenu_Adapter = new SimpleAdapter(
				getApplicationContext(), gv_bottmMenu_List,
				R.layout.gridview_bottommenu_item, from, to);
		gv_bottomMenu.setAdapter(gv_bottmMenu_Adapter);

		setBottomMenuGridviewListner();
	}

	/**
	 * @Description:Set bottom menu gridView item click listener.
	 * 
	 */
	private void setBottomMenuGridviewListner() {
		gv_bottomMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case DELETE_POSITION:
					// delete
					
					if (adapter.isChecBoxShow) {
						// if the delete button onece clicked ,calculate the
						// checkbox selected size and detete
						if (isSelectedNumber == 0) {
							ToastUtil.toast(context.getResources().getString(
									R.string.nothing_chose));
						} else {
							Map<Integer, Boolean> selectedMap = FileAdapter
									.getIsSelectedMap();
							List<File> deleteFilesList = new ArrayList<File>();
							for (int key : selectedMap.keySet()) {
								if (selectedMap.get(key)) {
									deleteFilesList.add(listFile.get(key));
								}
							}
							showDeleteAskDialog(deleteFilesList);
						}

					} else {
						// if the delete button never clicked ,then show the
						// checkbox in listview or gridview item
						showCheckBox(true);
						gv_bottomMenu.setVisibility(View.GONE);
					}

					break;
				case SEARCH_POSITION:
					// search
					showSearchDialog();
					break;
				case ORDER_POSITION:
					// sort
					showOrderDialog();
					break;

				default:
					break;
				}

			}

		});

	}

	/**
	 * Show a dialog to ask whether delete or not
	 */
	private void showDeleteAskDialog(final List<File> deleteFilesList) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final AlertDialog alertDialog = dialog.create();

		dialog.setTitle(this.getResources().getString(R.string.remind));
		dialog.setMessage(this.getResources().getString(R.string.delete_ask_b)
				+ isSelectedNumber
				+ this.getResources().getString(R.string.delete_ask_e));
		// confirm
		dialog.setPositiveButton(this.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startDeleteThread(deleteFilesList);
						
					}
				});
		// cancle
		dialog.setNegativeButton(
				this.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						
						isSelectedNumber = 0;
						for (int index = 0; index < listFile.size(); index++) {
							FileAdapter.isSelectedMap.put(index, false);
							adapter.notifyDataSetChanged();
						}
					}
				});
		dialog.create();
		dialog.show();
	}

	/**
	 * Start a thread to delete files
	 * 
	 * @param deleteFilesList
	 */
	public void startDeleteThread(final List<File> deleteFilesList) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = mHandler.obtainMessage();
				if (FileUtil.deleteFile(deleteFilesList)) {
					msg.what = DELETE_FILE_OK;
					msg.obj = deleteFilesList;
				} else {
					msg.what = DELETE_FILE_ERRO;
					msg.obj = deleteFilesList;
				}
				mHandler.sendMessage(msg);

			}
		}).start();

	}

	/**
	 * @Description:show the listview or gridview item checkbox image
	 * 
	 */
	private void showCheckBox(Boolean flag) {
		if (flag) {
			adapter.isChecBoxShow=true;
			rl_check_all.setVisibility(View.VISIBLE);
			
		} else {
			adapter.isChecBoxShow=false;
			rl_check_all.setVisibility(View.GONE);

		}
		adapter.list=listFile;
		adapter.notifyDataSetChanged();

	}

	/**
	 * 初始化视图
	 */
	private void init() {

		showBtn = (ImageButton) findViewById(R.id.ib_showstyle);

		showBtn.setVisibility(View.INVISIBLE);
		int count = PreferenceUtil.getPrefInt(this, "currShowType", 0);
		showBtn.setImageResource(showTypeArray[count]);
		currShowType = count;
		intList = new ArrayList<Integer>();
		listFile = new ArrayList<File>();
		gridlayout = R.layout.gridview_item;
		listlayout = R.layout.listview_item;
		listView = (ListView) findViewById(R.id.lv_content);
		gridView = (GridView) findViewById(R.id.gv_content);
		gridView.setSelector(R.drawable.gridview_selected_bg);
		listView.setSelector(R.drawable.listview_selected_bg);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("v" + getLocalInfo());

		tvNotice = (TextView) findViewById(R.id.tv_notice);
		gvMountedDevice = (GridView) findViewById(R.id.gv_mounted_equipment);

		pathTxt = (TextView) findViewById(R.id.tv_path);
		numInfo = (TextView) findViewById(R.id.tv_count);

		ll_mounted_equipment = (LinearLayout) this
				.findViewById(R.id.ll_mounted_equipment);
		ll_mounted_equipment.setVisibility(View.VISIBLE);
		ll_choose_type = (LinearLayout) this.findViewById(R.id.ll_choose_type);
		ibTypeAll = (ImageButton) this.findViewById(R.id.ib_all);
		ibTypeMoive = (ImageButton) this.findViewById(R.id.ib_moive);
		ibTypeMusic = (ImageButton) this.findViewById(R.id.ib_music);
		ibTypeImage = (ImageButton) this.findViewById(R.id.ib_image);

		/*** add for zhejiang chinamobile begin **/
		tvDevNotice = (TextView) findViewById(R.id.tv_dev_notice);
		/*** add for zhejiang chinamobile end **/

		gv_bottomMenu = (GridView) this.findViewById(R.id.gv_bottomMenu);
		rl_check_all = (RelativeLayout) this.findViewById(R.id.rl_check_all);
		cb_check_all = (CheckBox) this.findViewById(R.id.cb_check_all);

		cb_check_all.setOnCheckedChangeListener(ch_check_all_Listener);
		initChooseTypeView();
		getsdList();
	}

	private OnCheckedChangeListener ch_check_all_Listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean check) {

			Map<Integer, Boolean> isSelectedMap = new HashMap<Integer, Boolean>();
			for (int index = 0; index < listFile.size(); index++) {
				isSelectedMap.put(index, check);
			}
	
			adapter.setIsSelected(isSelectedMap);
			adapter.notifyDataSetChanged();
			if (check) {
				isSelectedNumber = isSelectedMap.size();
			} else {
				isSelectedNumber = 0;
			}
			Log.i("nongweiyi", "isSelectedNumber=" + isSelectedNumber
					+ " check=" + check);
		}
	};

	/**
	 * 初始化文件类型选择视图
	 */
	private void initChooseTypeView() {
		// TODO Auto-generated method stub
		animEffect = new AnimationEffect();

		ivCopyAll = (ImageView) findViewById(R.id.iv_copy_all);
		ivCopyMoive = (ImageView) findViewById(R.id.iv_copy_moive);
		ivCopyMusic = (ImageView) findViewById(R.id.iv_copy_music);
		ivCopyImage = (ImageView) findViewById(R.id.iv_copy_image);

		Bitmap bitmap_copy_all = convertDrawable2BitmapSimple(getResources()
				.getDrawable(R.drawable.type_all));
		ivCopyAll.setImageBitmap(createCutReflectedImage(bitmap_copy_all, 0));

		Bitmap bitmap_copy_moive = convertDrawable2BitmapSimple(getResources()
				.getDrawable(R.drawable.type_mov));
		ivCopyMoive
				.setImageBitmap(createCutReflectedImage(bitmap_copy_moive, 0));

		Bitmap bitmap_copy_music = convertDrawable2BitmapSimple(getResources()
				.getDrawable(R.drawable.type_music));
		ivCopyMusic
				.setImageBitmap(createCutReflectedImage(bitmap_copy_music, 0));

		Bitmap bitmap_copy_image = convertDrawable2BitmapSimple(getResources()
				.getDrawable(R.drawable.type_img));
		ivCopyImage
				.setImageBitmap(createCutReflectedImage(bitmap_copy_image, 0));

		ibTypeAll.setOnFocusChangeListener(this);
		ibTypeMoive.setOnFocusChangeListener(this);
		ibTypeMusic.setOnFocusChangeListener(this);
		ibTypeImage.setOnFocusChangeListener(this);
	}

	/**
	 * Obtain mount list 获得挂载列表
	 */
	public void getsdList() {
		getMountEquipmentList();
		updateGridViewNumColumns(childList);
		DeviceAdapter adapter = new DeviceAdapter(childList,
				MyMediaActivity.this);
		gvMountedDevice.setAdapter(adapter);
		gvMountedDevice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long id) {

				selectedEquipment = position;
				ll_choose_type.setVisibility(View.VISIBLE);
				ll_mounted_equipment.setVisibility(View.INVISIBLE);

				/*** add for zhejiang chinamobile begin **/
				switch (intFromWhere) {
				case FROM_VIDEO:
					
					Log.i(TAG, "FROM_VIDEO");
					currfilterType = 1;
					ll_choose_type.setVisibility(View.INVISIBLE);
					showBtn.setVisibility(View.VISIBLE);
					updateByType(position);
					break;
				case FROM_MUSIC:
					
					Log.i(TAG, "FROM_MUSIC");
					currfilterType = 2;
					ll_choose_type.setVisibility(View.INVISIBLE);
					showBtn.setVisibility(View.VISIBLE);
					updateByType(position);
					break;
				case FROM_PICTURE:
					
					Log.i(TAG, "FROM_PICTURE");
					currfilterType = 3;
					ll_choose_type.setVisibility(View.INVISIBLE);
					showBtn.setVisibility(View.VISIBLE);
					updateByType(position);
					break;
				case FROM_FILE:
					
					// TODO Auto-generated method stub
					Log.i(TAG, "ibTypeAll is clicked");

					currfilterType = 0;
					ll_choose_type.setVisibility(View.INVISIBLE);
					showBtn.setVisibility(View.VISIBLE);
					updateByType(position);
					break;
				}
				/*** add for zhejiang chinamobile end **/

				ibTypeAll.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						
						// TODO Auto-generated method stub
						Log.i(TAG, "ibTypeAll is clicked");

						currfilterType = 0;
						ll_choose_type.setVisibility(View.INVISIBLE);
						showBtn.setVisibility(View.VISIBLE);
						updateByType(position);
					}
				});
				ibTypeMoive.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						
						// TODO Auto-generated method stub
						Log.i(TAG, "ibTypeMoive is clicked");

						currfilterType = 1;
						ll_choose_type.setVisibility(View.INVISIBLE);
						showBtn.setVisibility(View.VISIBLE);
						updateByType(position);
					}
				});
				ibTypeMusic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						
						// TODO Auto-generated method stub
						Log.i(TAG, "ibTypeMusic is clicked");

						currfilterType = 2;
						ll_choose_type.setVisibility(View.INVISIBLE);
						showBtn.setVisibility(View.VISIBLE);
						updateByType(position);
					}
				});
				ibTypeImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						
						// TODO Auto-generated method stub
						Log.i(TAG, "ibTypeImage is clicked");

						currfilterType = 3;
						ll_choose_type.setVisibility(View.INVISIBLE);
						showBtn.setVisibility(View.VISIBLE);
						updateByType(position);
					}
				});
				// First time enter,selected all by default
				ibTypeAll.requestFocus();
			}

			private void updateByType(int position) {
				String path = childList.get(position).get(MOUNT_PATH);
				mountSdPath = path;
				arrayFile.clear();
				arrayDir.clear();
				directorys = path;
				int count = PreferenceUtil.getPrefInt(MyMediaActivity.this,
						"currShowType", 0);
				if (count == 0) {
					gridView.setVisibility(View.INVISIBLE);
					listView.setVisibility(View.VISIBLE);
				} else {
					gridView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.INVISIBLE);
				}
				listFile.clear();
				clickPos = 0;
				myPosition = 0;
				geDdirectory(directorys);
				intList.add(position);
				updateList(true);
			}
		});
		gvMountedDevice.setSelector(R.drawable.blank);
		gvMountedDevice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (view == null) {
					view = mInflater.inflate(R.layout.device_item, null);
				}
				ivDeviceIcon = (ImageView) view
						.findViewById(R.id.iv_device_icon);
				tvDeviceName = (TextView) view
						.findViewById(R.id.tv_device_name);
				showOnFocusAnimation(ivDeviceIcon);
				showOnFocusAnimation(tvDeviceName);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * File list sorting 文件列表排序
	 */
	@Override
	public void updateList(boolean flag) {

		if (flag) {
			// for broken into the directory contains many files,click again
			// error
			listFile.clear();
			Log.i(TAG, "updateList");
			showBtn.setOnClickListener(clickListener);

			if (progress != null && progress.isShowing()) {
				progress.dismiss();
			}

			progress = new CustomProgressDialog(MyMediaActivity.this);
			progress.show();

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			waitThreadToIdle(thread);
			thread = new MyThread();
			thread.setStopRun(false);
			progress.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					thread.setStopRun(true);
					if (keyBack) {
						intList.add(clickPos);
					} else {
						clickPos = myPosition;
						currentFileString = preCurrentPath;
						Log.v("\33[32m Main1", "onCancel" + currentFileString
								+ "\33[0m");
						intList.remove(intList.size() - 1);
					}
					ToastUtil.showToast(MyMediaActivity.this,
							Toast.LENGTH_SHORT, getString(R.string.cause_anr));
				}
			});
			thread.start();
		} else {
			adapter.notifyDataSetChanged();
			fill(new File(currentFileString));
		}

	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	private void registerUSB() {
		IntentFilter usbFilter = new IntentFilter();
		usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		usbFilter.addDataScheme("file");
		registerReceiver(usbReceiver, usbFilter);
	}

	protected void onDestroy() {
		unregisterReceiver(usbReceiver);
		super.onDestroy();
	}

	private void getMountEquipmentList() {
		String[] mountType = getResources().getStringArray(R.array.mountType);
		MountInfo info = new MountInfo(this);
		groupList = new ArrayList<GroupInfo>();
		childList = new ArrayList<Map<String, String>>();
		GroupInfo group = null;
		for (int j = 0; j < mountType.length; j++) {
			group = new GroupInfo();
			for (int i = 0; i < info.index; i++) {
				if (info.type[i] == j) {
					if (info.path[i] != null && (info.path[i].contains("/mnt"))) {
						Map<String, String> map = new HashMap<String, String>();
						map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
						map.put(MOUNT_PATH, info.path[i]);
						map.put(MOUNT_LABLE, "");
						map.put(MOUNT_NAME, info.partition[i]);
						childList.add(map);
					}
				}
			}
			if (childList.size() > 0) {
				/*** add for zhejiang chinamobile begin **/
				tvDevNotice.setVisibility(View.INVISIBLE);
				/*** add for zhejiang chinamobile begin **/
				group.setChildList(childList);
				group.setName(mountType[j]);
				groupList.add(group);
				/*** add for zhejiang chinamobile begin **/
			} else {
				tvDevNotice.setVisibility(View.VISIBLE);
			}
			/*** add for zhejiang chinamobile begin **/
		}
	}

	// for broken into the directory contains many files,click again error
	class MyThread extends MyThreadBase {
		public void run() {
			util = new FileUtil(MyMediaActivity.this, currfilterType,
					currentFileString);
			listFile = util.getFiles(currSortType, "local");
			currentFileString = util.currentFilePath;
			handler.sendEmptyMessage(UPDATE_LIST);
		}

	}

	private Handler handler = new Handler() {
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_LIST:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				if (listView.getVisibility() == View.VISIBLE) {
					adapter = new FileAdapter(MyMediaActivity.this, listFile,
							listlayout, handler, false);
					listView.setAdapter(adapter);
					listView.setOnItemSelectedListener(itemSelect);
					listView.setOnItemClickListener(ItemClickListener);
					listView.setOnItemLongClickListener(ItemLongClickListener);
				} else if (gridView.getVisibility() == View.VISIBLE) {
					adapter = new FileAdapter(MyMediaActivity.this, listFile,
							gridlayout, handler, false);
					gridView.setAdapter(adapter);
					gridView.setOnItemClickListener(ItemClickListener);
					gridView.setOnItemSelectedListener(itemSelect);
					gridView.setOnItemLongClickListener(ItemLongClickListener);
				}
				fill(new File(currentFileString));
				if (listFile.size() <= 0) {
					switch (currfilterType) {
					case 0:
						tvNotice.setText("没有找到任何文件");
						break;
					case 1:
						tvNotice.setText("没有找到视频文件");
						break;
					case 2:
						tvNotice.setText("没有找到音频文件");
						break;
					case 3:
						tvNotice.setText("没有找到图片文件");
						break;
					}

				}
				break;
			}
		}
	};
	private OnItemLongClickListener ItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			showOnLongClickDialog(listFile.get(position));
			return true;
		}

	};

	/**
	 * Show a dialog to user to delete or preview chose
	 */
	private void showOnLongClickDialog(final File seletedFile) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		View view = View.inflate(this, R.layout.orderdialog_layout, null);
		final ListView lv_dialog = (ListView) view
				.findViewById(R.id.lv_orderdialog);
		final List<String> list = new ArrayList<String>();
		final String item_delete = this.getResources().getString(
				R.string.delete);
		final String item_preview = this.getResources().getString(
				R.string.preview);
		list.add(item_delete);
		list.add(item_preview);

		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lv_dialog.setAdapter(adapter);

		dialog.setView(view);
		dialog.setTitle(this.getResources().getString(R.string.opration_remind));
		final AlertDialog alert = dialog.create();
		alert.show();

		dialog.setNegativeButton(
				this.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						alert.dismiss();
					}
				});

		lv_dialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectType = list.get(position);
				alert.dismiss();
				deleteOrPreview(selectType, seletedFile);
			}
		});

	}

	/**
	 * delete or preview file accomding to selectType
	 * 
	 * @param selectType
	 */
	private void deleteOrPreview(String selectType, File seletedFile) {

		if (selectType.equals(this.getResources().getString(R.string.delete))) {
			// delete
			List<File> deleteFileList = new ArrayList<File>();
			deleteFileList.add(seletedFile);
			isSelectedNumber = 1;
			showDeleteAskDialog(deleteFileList);

		} else {
			// preview
			showPreviewDialog(seletedFile);
		}

	}

	/**
     * 
     */
	private void showPreviewDialog(File seletedFile) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		View view = View.inflate(this, R.layout.preview_layout, null);
		TextView tv_fileName = (TextView) view.findViewById(R.id.tv_fileName);
		TextView tv_fileSize = (TextView) view.findViewById(R.id.tv_fileSize);
		TextView tv_fileType = (TextView) view.findViewById(R.id.tv_fileType);
		TextView tv_lastTime = (TextView) view.findViewById(R.id.tv_lastTime);
		if (seletedFile.isFile()) {
			tv_fileName.setText(FileUtil.getFileName(seletedFile));
		} else {
			tv_fileName.setText(seletedFile.getName());
		}
		tv_fileSize.setText(FileUtil.fileSizeMsg(seletedFile));
		tv_fileType.setText(FileUtil.getFileType(seletedFile));
		tv_lastTime.setText(FileUtil.getFileLastModified(seletedFile));

		dialog.setView(view);
		dialog.setTitle(this.getResources().getString(R.string.preview));
		final AlertDialog alert = dialog.create();

		dialog.setNegativeButton(
				this.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						alert.dismiss();
					}
				});
		dialog.show();
	}

	/**
	 * 文件项选中事件
	 */
	OnItemSelectedListener itemSelect = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (parent.equals(listView) || parent.equals(gridView)) {
				myPosition = position;
			}
			numInfo.setText((position + 1) + "/" + listFile.size());
		}

		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	/**
	 * 文件项点击事件
	 */
	private OnItemClickListener ItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> l, View v, int position, long id) {

			ViewHolder vHollder = (ViewHolder) v.getTag();
			if (vHollder.iv_item_select.getVisibility() == View.VISIBLE) {
				vHollder.iv_item_select.toggle();
				FileAdapter.getIsSelectedMap().put(position,
						vHollder.iv_item_select.isChecked());
				if (vHollder.iv_item_select.isChecked()) {
					isSelectedNumber++;
				} else {
					isSelectedNumber--;
				}

			} else {
				if (listFile.size() > 0) {
					if (position >= listFile.size()) {
						position = listFile.size() - 1;
					}
					// for chmod the file
					//chmodFile(listFile.get(position).getPath());
					if (listFile.get(position).isDirectory()
							&& listFile.get(position).canRead()) {
						intList.add(position);
						clickPos = 0;
					} else {
						clickPos = position;
						// OpenFiles.open(CustomApplication.getContext(),listFile.get(position));
						// listFile.get(clickPos));
					}
					myPosition = clickPos;
					arrayFile.clear();
					arrayDir.clear();
					// for broken into the directory contains many files,click
					// again
					// error
					preCurrentPath = currentFileString;
					keyBack = false;
					getFiles(listFile.get(position).getPath());
				}
			}

		}

	};

	/**
	 * Obtain a collection of files directory 获得目录下文件集合
	 * 
	 * @param path
	 */
	private void geDdirectory(String path) {
		directorys = path;
		parentPath = path;
		currentFileString = path;
	}

	@Override
	/**
	 * Depending on the file directory path judgment do:
	 * Go to the directory the file: Open the file system application
	 * 根据文件路径判断执行的操作目录:进入目录 文件:系统应用打开文件
	 * @param path
	 */
	public void getFiles(String path) {
		if (path == null)
			return;
		openFile = new File(path);
		if (openFile.exists()) {
			if (openFile.isDirectory()) {
				currentFileString = path;
				updateList(true);
			} else {
				super.openFile(this, openFile);
			}
		} else {
			refeshList();
		}
	};

	@Override
	/**
	 * Populate the list of files to the data container
	 * 将文件列表填充到数据容器中
	 * @param fileroot
	 */
	public void fill(File fileroot) {
		try {
			if (clickPos >= listFile.size()) {
				clickPos = listFile.size() - 1;
			}
			System.out.println("lll:path=" + fileroot.getPath());
			pathTxt.setText(fileroot.getPath());
			numInfo.setText((clickPos + 1) + "/" + listFile.size());
			if (!fileroot.getPath().equals(directorys)) {
				parentPath = fileroot.getParent();
				currentFileString = fileroot.getPath();
			} else {
				currentFileString = directorys;
			}

			if (listFile.size() == 0) {
				numInfo.setText(0 + "/" + 0);
			}

			if (clickPos >= 0) {
				if (listView.getVisibility() == View.VISIBLE) {
					listView.requestFocus();
					listView.setSelection(clickPos);
				} else if (gridView.getVisibility() == View.VISIBLE) {
					gridView.requestFocus();
					gridView.setSelection(clickPos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ////////////////////////测试使用/////////////////////
	public void testPop(View v) {
		Button b = (Button) this.findViewById(R.id.bt);
		if (listView.getVisibility() == View.VISIBLE
				|| gridView.getVisibility() == View.VISIBLE) {
			gv_bottomMenu.setVisibility(View.VISIBLE);
			gv_bottomMenu.requestFocus();
			b.setVisibility(View.GONE);
		}
	}

	// ////////////////////////////////////////////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// menu
		case KeyEvent.KEYCODE_MENU:
			if (listView.getVisibility() == View.VISIBLE
					|| gridView.getVisibility() == View.VISIBLE) {
				gv_bottomMenu.setVisibility(View.VISIBLE);
				gv_bottomMenu.requestFocus();
				return true;
			}

		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			super.onKeyDown(KeyEvent.KEYCODE_ENTER, event);
			return true;

		case KeyEvent.KEYCODE_BACK:
			if (gv_bottomMenu.getVisibility() == View.VISIBLE) {
				gv_bottomMenu.setVisibility(View.GONE);
			} else if (adapter.isChecBoxShow) {
				adapter.isChecBoxShow = false;
				adapter.notifyDataSetChanged();
				rl_check_all.setVisibility(View.GONE);
				cb_check_all.setChecked(false);
			} else {
				keyBack = true;
				tvNotice.setText("");
				String newName = pathTxt.getText().toString();
				// 当前目录是根目录
				if (newName.equals("")) {
					showBtn.setVisibility(View.INVISIBLE);

					if (ll_choose_type.getVisibility() == View.VISIBLE) {
						ll_choose_type.setVisibility(View.INVISIBLE);
						ll_mounted_equipment.setVisibility(View.VISIBLE);
						DeviceAdapter adapter = new DeviceAdapter(childList,
								MyMediaActivity.this);
						gvMountedDevice.setAdapter(adapter);
						System.out.println("lll:selectedEquipment="
								+ selectedEquipment);
						gvMountedDevice.setSelection(selectedEquipment);
					} else {
						SharedPreferences share = getSharedPreferences(
								"OPERATE", SHARE_MODE);
						share.edit().clear().commit();
						Intent cmd = new Intent(
								"com.unionman.android.music.musicservicecommand");
						cmd.putExtra("command", "stop");
						this.sendBroadcast(cmd);
						onBackPressed();
						/*
						 * clickCount++; if (clickCount == 1) {
						 * ToastUtil.showToast(MyMediaActivity.this,
						 * Toast.LENGTH_SHORT, getString(R.string.quit_app)); }
						 * else if (clickCount == 2) { SharedPreferences share =
						 * getSharedPreferences( "OPERATE", SHARE_MODE);
						 * share.edit().clear().commit(); Intent cmd = new
						 * Intent(
						 * "com.unionman.android.music.musicservicecommand");
						 * cmd.putExtra("command", "stop");
						 * this.sendBroadcast(cmd); onBackPressed(); }
						 */
					}
				} else {
					clickCount = 0;
					if (!currentFileString.equals(directorys)) {
						arrayDir.clear();
						arrayFile.clear();
						getFiles(parentPath);

					} else {
						pathTxt.setText("");
						numInfo.setText("");
						showBtn.setOnClickListener(null);
						int count = PreferenceUtil.getPrefInt(this,
								"currShowType", 0);
						showBtn.setImageResource(showTypeArray[count]);
						currShowType = count;
						currSortType = 0;
						// filterBut.setOnClickListener(null);
						// filterBut.setImageResource(filterArray[0]);
						gridView.setVisibility(View.INVISIBLE);
						listView.setVisibility(View.INVISIBLE);
						listFile.clear();
						getsdList();
					}
					// 点击的父目录位置
					int pos = -1;
					if (intList.size() <= 0) {
						groupPosition = 0;
						intList.add(0);
					}

					pos = intList.size() - 1;
					if (pos >= 0) {
						if (listView.getVisibility() == View.VISIBLE) {
							clickPos = intList.get(pos);
							myPosition = clickPos;
							intList.remove(pos);
						} else if (gridView.getVisibility() == View.VISIBLE) {
							clickPos = intList.get(pos);
							myPosition = clickPos;
							intList.remove(pos);
						} else if (intFromWhere == 0) {
							ll_mounted_equipment.setVisibility(View.GONE);
							showBtn.setVisibility(View.GONE);
							System.out.println("llllll:requestFocus");
							ll_choose_type.setVisibility(View.VISIBLE);
							ll_choose_type.requestFocus();
							System.out.println("lll:currfilterType="
									+ currfilterType);
							switch (currfilterType) {
							case 0:
								ibTypeAll.requestFocus();
								break;
							case 1:
								ibTypeMoive.requestFocus();
								break;
							case 2:
								ibTypeMusic.requestFocus();
								break;
							case 3:
								ibTypeImage.requestFocus();
								break;
							}
							/*** add for zhejiang chinamobile begin **/
						} else {
							showBtn.setVisibility(View.INVISIBLE);
							ll_choose_type.setVisibility(View.INVISIBLE);
							ll_mounted_equipment.setVisibility(View.VISIBLE);
							DeviceAdapter adapter = new DeviceAdapter(
									childList, MyMediaActivity.this);
							gvMountedDevice.setAdapter(adapter);
						}
						/*** add for zhejiang chinamobile end **/
					}
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * 转换图片格式
	 * 
	 * @param drawable
	 * @return
	 */
	public Bitmap convertDrawable2BitmapSimple(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	private int reflectImageHeight = 70;

	/**
	 * 倒影效果
	 * 
	 * @param paramBitmap
	 * @param paramInt
	 * @return
	 */
	public Bitmap createCutReflectedImage(Bitmap paramBitmap, int paramInt) {
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Bitmap localBitmap2 = null;
		if (j <= paramInt + reflectImageHeight) {
			localBitmap2 = null;
			Log.i(TAG, "localBitmap2 is null");
		} else {
			Matrix localMatrix = new Matrix();
			localMatrix.preScale(1.0F, -1.0F);
			Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j
					- reflectImageHeight - paramInt, i, reflectImageHeight,
					localMatrix, true);
			localBitmap2 = Bitmap.createBitmap(i, reflectImageHeight,
					Bitmap.Config.ARGB_8888);
			Canvas localCanvas = new Canvas(localBitmap2);
			localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
			LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F,
					0.0F, localBitmap2.getHeight(), -2130706433, 16777215,
					TileMode.CLAMP);
			Paint localPaint = new Paint();
			localPaint.setShader(localLinearGradient);
			localPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight(),
					localPaint);
			Log.i(TAG, "localBitmap2 is not null");
			if (!localBitmap1.isRecycled())
				localBitmap1.recycle();
			System.gc();
		}
		return localBitmap2;
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			switch (view.getId()) {
			case R.id.ib_all:
				showOnFocusAnimation(ibTypeAll);
				break;
			case R.id.ib_moive:
				showOnFocusAnimation(ibTypeMoive);
				break;
			case R.id.ib_music:
				showOnFocusAnimation(ibTypeMusic);
				break;
			case R.id.ib_image:
				showOnFocusAnimation(ibTypeImage);
				break;
			}
		} else {
			switch (view.getId()) {
			case R.id.ib_all:
				showLooseFocusAinimation(ibTypeAll);
				break;
			case R.id.ib_moive:
				showLooseFocusAinimation(ibTypeMoive);
				break;
			case R.id.ib_music:
				showLooseFocusAinimation(ibTypeMusic);
				break;
			case R.id.ib_image:
				showLooseFocusAinimation(ibTypeImage);
				break;
			}

		}
	}

	private void showLooseFocusAinimation(ImageButton ib) {
		animEffect.setAttributs(1.085F, 1.0F, 1.085F, 1.0F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		ib.bringToFront();
		ib.startAnimation(localAnimation);
	}

	private void showOnFocusAnimation(ImageButton ib) {
		animEffect.setAttributs(1.0F, 1.3F, 1.0F, 1.3F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		ib.bringToFront();
		ib.startAnimation(localAnimation);
	}

	private void showOnFocusAnimation(ImageView iv) {
		animEffect.setAttributs(1.01F, 1.0F, 1.01F, 1.0F, 500L);
		Animation localAnimation = this.animEffect.createAnimation();
		iv.bringToFront();
		iv.startAnimation(localAnimation);
	}

	private void showOnFocusAnimation(TextView tv) {
		animEffect.setAttributs(1.01F, 1.0F, 1.01F, 1.0F, 500L);
		Animation localAnimation = this.animEffect.createAnimation();
		tv.bringToFront();
		tv.startAnimation(localAnimation);
	}

	private void updateGridViewNumColumns(List<Map<String, String>> childList) {
		int num = 0;
		int width = 0;
		for (Map<String, String> map : childList) {
			num++;
			width = width + 300;
		}
		gvMountedDevice.setNumColumns(num);

		LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) gvMountedDevice
				.getLayoutParams();
		linearParams2.width = width;
		linearParams2.height = 450;
		gvMountedDevice.setLayoutParams(linearParams2);
	}

	/**
	 * 获取当前版本
	 * 
	 * @param packagename
	 */
	private String getLocalInfo() {
		try {
			PackageInfo packageInfo = getApplicationContext()
					.getPackageManager().getPackageInfo(getPackageName(), 0);
			if (packageInfo == null) {
				return "";
			} else {
				return packageInfo.versionName + "";
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * @Description:Pop the sort selected window dialog.
	 * 
	 */
	private void showOrderDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		View view = View.inflate(this, R.layout.orderdialog_layout, null);
		final ListView lv_orderdialog = (ListView) view
				.findViewById(R.id.lv_orderdialog);
		final List<String> list = new ArrayList<String>();
		final String file_name = this.getResources().getString(
				R.string.file_name);
		final String update_time = this.getResources().getString(
				R.string.update_time);
		final String file_size = this.getResources().getString(
				R.string.file_size);
		list.add(file_name);
		list.add(update_time);
		list.add(file_size);

		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lv_orderdialog.setAdapter(adapter);

		dialog.setView(view);
		final AlertDialog alert = dialog.create();
		alert.show();

		lv_orderdialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectType = list.get(position);
				alert.dismiss();
				startSortThread(selectType);
			}
		});
	}

	/**
	 * @Description:start a thread to sort files.
	 * 
	 */
	private void startSortThread(final String selectType) {
		new Thread(new Runnable() {
			public void run() {
				List<File> sortFileList = FileUtil.sortFile(listFile,
						selectType, MyMediaActivity.this);
				Message msg = handler.obtainMessage();
				msg.what = SORT_FILE_OK;
				msg.obj = sortFileList;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * @Description:start a thread to search files.
	 * 
	 */
	private void startSearchThread(final String keyword) {
		new Thread(new Runnable() {
			public void run() {
				List<File> searchFileList = FileUtil.searchFile(listFile,
						keyword);
				Message msg = handler.obtainMessage();
				msg.what = SEARCH_FILE_OK;
				msg.obj = searchFileList;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * @Description:Pop the search window dialog.
	 * 
	 */
	private void showSearchDialog() {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		View view = View.inflate(this, R.layout.searchdialog_layout, null);
		final EditText et_search_keyword = (EditText) view
				.findViewById(R.id.et_search_keyword);
		dialog.setView(view);
		// confirm
		dialog.setPositiveButton(this.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						String search_keyword = et_search_keyword.getText()
								.toString().trim();
						if (TextUtils.isEmpty(search_keyword)) {
							Toast.makeText(
									MyMediaActivity.this,
									MyMediaActivity.this.getResources()
											.getString(R.string.input_nothing),
									Toast.LENGTH_SHORT).show();
						} else {
							startSearchThread(search_keyword);
						}
					}
				});
		// cancle
		dialog.setNegativeButton(
				this.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		dialog.create();
		dialog.show();
	}
}
