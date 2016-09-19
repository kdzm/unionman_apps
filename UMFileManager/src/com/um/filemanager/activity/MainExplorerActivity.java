package com.um.filemanager.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.filemanager.R;
import com.hisilicon.android.sdkinvoke.HiSdkinvoke;
import com.um.filemanager.common.CommonActivity;
import com.um.filemanager.common.ControlListAdapter;
import com.um.filemanager.common.ExpandAdapter;
import com.um.filemanager.common.FileAdapter;
import com.um.filemanager.common.FileAdapter.customerBitmap;
import com.um.filemanager.common.FileUtil;
import com.um.filemanager.common.MyProDialog;
import com.um.filemanager.common.FilterType;
import com.um.filemanager.common.GroupInfo;
import com.um.filemanager.common.MountInfo;
import com.um.filemanager.common.NewCreateDialog;


/**
 * The local file browsing
 * CNcomment:本地文件浏览
 */
public class MainExplorerActivity extends CommonActivity {

    private static final String TAG = "MainExplorerActivity";

    private String parentPath = "";

    // Operation File list
    // CNcomment:操作文件列表
    private List<File> fileArray = null;

    private String directorys = "/sdcard";

    // The list of files click position
    // CNcomment:文件列表的点击位置
    private int myPosition = 0;

    int Num = 0;

    int tempLength = 0;
    boolean isKeyMenuEnable = false;
    
    // file list selected
    // CNcomment:选中的文件列表
    List<String> selectList = null;

    // operation list
    // CNcomment:操作列表
    ListView list;

    // mount list
    // CNcomment:挂载列表
    List<Map<String, Object>> sdlist;

    int clickCount = 0;

    AlertDialog dialog ;

    // store the Click position set
    // CNcomment:存放点击位置集合
    List<Integer> intList;

    // Need to sort the file list
    // CNcomment:需要排序的文件列表
    File[] sortFile;

    final static String MOUNT_LABLE = "mountLable";

    final static String MOUNT_TYPE = "mountType";

    final static String MOUNT_PATH = "mountPath";

    final static String MOUNT_NAME = "mountName";

    final static String MUSIC_PATH = "music_path";

    int menu_item = 0;

    ExpandableListView expandableListView;

    // Device type node-set
    // CNcomment:设备类型节点集合
    List<GroupInfo> groupList;

    // sub notes set of equipment
    // CNcomment:设备子节点集合
    List<Map<String, String>> childList;

    // Device List Category Location
    // CNcomment:设备列表类别位置
    int groupPosition = -1;

    // Index Display
    // CNcomment:索引显示
    TextView numInfo;;

    // Intent transfer from VP
    // CNcomment:Intent从VP传输
    boolean subFlag = false;

    FileUtil util;
    //jly 20140313 modify the focus of gridview
    private int gPosition = 0;
    //jly
    //jly
    //private RelativeLayout tabRelativeLayout=(RelativeLayout)findViewById(R.id.tab_title);
    //jly
    private String isoParentPath = new String();

    protected AsyncLoadedImage AsyncLoadedImageTask = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String chipVersion = HiSdkinvoke.getChipVersion();
        Log.i(TAG, "chipVersion:" + chipVersion);

        if (chipVersion.equals("Unknown chip ID"))
        { finish(); }

        FilterType.filterType(MainExplorerActivity.this);
        init();
        selectList = new ArrayList<String>();
        getUSB();
    }

    private void init() {
        showBut = (ImageButton) findViewById(R.id.showBut);
        // sortBut = (ImageButton) findViewById(R.id.sortBut);
        filterBut = (ImageButton) findViewById(R.id.filterBut);
        intList = new ArrayList<Integer>();
        listFile = new ArrayList<File>();
        gridlayout = R.layout.gridfile_row;
        listlayout = R.layout.file_row;
        listView = (ListView) findViewById(R.id.listView);
        gridView = (GridView) findViewById(R.id.gridView);
        pathTxt = (TextView) findViewById(R.id.pathTxt);
        numInfo = (TextView) findViewById(R.id.ptxt);
        bottomView =(View)findViewById(R.id.main_bottom);
        expandableListView = (ExpandableListView) findViewById(R.id.expandlistView);
        pathTxt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (!pathTxt.getText().toString().equals("所有存储设备")) {
					filterBut.setVisibility(View.VISIBLE);
					showBut.setVisibility(View.VISIBLE);
		        	bottomView.setVisibility(View.VISIBLE);
		        	isKeyMenuEnable = true;
		        }
		        else {
		        	filterBut.setVisibility(View.INVISIBLE);
		        	showBut.setVisibility(View.INVISIBLE);
		            bottomView.setVisibility(View.INVISIBLE);
		            isKeyMenuEnable = false;
		        }
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        getsdList();
        isNetworkFile = false;
    }

    /**
     * Obtain mount list
     * CNcomment:获得挂载列表
     */
    public void getsdList() {
        getMountEquipmentList();
        ExpandAdapter adapter = new ExpandAdapter(this, groupList);
        expandableListView.setAdapter(adapter);

        for (int i = 0; i < groupList.size(); i++) {
            expandableListView.expandGroup(i);
        }

        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
                String path = groupList.get(groupPosition).getChildList()
                              .get(childPosition).get(MOUNT_PATH);
                mountSdPath = path;
                MainExplorerActivity.this.groupPosition = groupPosition;
                arrayFile.clear();
                arrayDir.clear();
                directorys = path;
                expandableListView.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                listFile.clear();
                clickPos = 0;
                myPosition = 0;
                // FileAdapter adapter = new FileAdapter(
                // MainExplorerActivity.this, li, listlayout);
                // listView.setAdapter(adapter);
                geDdirectory(directorys);
                intList.add(childPosition);
                updateList(true);
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //jly 20140316
        menu.add(Menu.NONE, MENU_TAB, 0, getString(R.string.hide_tab));
        SubMenu operatFile = menu.addSubMenu(Menu.NONE, Menu.NONE, 0,getString(R.string.operation));
        //getMenuInflater().inflate( R.menu.options_menu , operatFile); 
        operatFile.add(Menu.NONE, MENU_COPY, 0, getString(R.string.copy));
        operatFile.add(Menu.NONE, MENU_CUT, 0, getString(R.string.cut));
        operatFile.add(Menu.NONE, MENU_PASTE, 0, getString(R.string.paste));
        operatFile.add(Menu.NONE, MENU_DELETE, 0, getString(R.string.delete));
        operatFile.add(Menu.NONE, MENU_RENAME, 0,getString(R.string.str_rename));
        menu.add(Menu.NONE, MENU_ADD, 0, getString(R.string.str_new));
        menu.add(Menu.NONE, MENU_SEARCH, 0, getString(R.string.search));
        return true;
    };

    public boolean onPrepareOptionsMenu(Menu menu) {
        //jly 20140317
        /*  if (TabBarExample.getWidget().isShown()) {*/
        if (TabBarExample.getMain_tv().isShown() && TabBarExample.getMain_iv().isShown()) {
            menu.getItem(0).setTitle(R.string.hide_tab);
        }
        else {
            menu.getItem(0).setTitle(R.string.show_tab);
        }

        SharedPreferences share = getSharedPreferences("OPERATE", SHARE_MODE);
        int num = share.getInt("NUM", 0);
        
        if (!OPERATER_ENABLE) {
            menu.getItem(1).setVisible(false);
        }

        if (!pathTxt.getText().toString().equals("所有存储设备")) {
            // menu.getItem(4).setEnabled(true);
            // menu.getItem(5).setEnabled(true);
            if (listFile.size() == 0) {
                menu.getItem(1).getSubMenu().getItem(0).setEnabled(false);
                menu.getItem(1).getSubMenu().getItem(1).setEnabled(false);
                menu.getItem(1).getSubMenu().getItem(3).setEnabled(false);
                menu.getItem(1).getSubMenu().getItem(4).setEnabled(false);

                //               menu.getItem(3).getSubMenu().getItem(5).setEnabled(false);
                if (num == 0) {
                    menu.getItem(1).setEnabled(false);
                }
                else {
                    menu.getItem(1).setEnabled(true);
                    menu.getItem(1).getSubMenu().getItem(2).setEnabled(true);
                }

                menu.getItem(2).setEnabled(true);
                menu.getItem(3).setEnabled(false);
            }
            else {
                menu.getItem(1).setEnabled(true);
                menu.getItem(1).getSubMenu().getItem(0).setEnabled(true);
                menu.getItem(1).getSubMenu().getItem(1).setEnabled(true);
                menu.getItem(1).getSubMenu().getItem(3).setEnabled(true);
                menu.getItem(1).getSubMenu().getItem(4).setEnabled(true);

                // if (arrayFile.size() == 0) {
                // menu.getItem(3).getSubMenu().getItem(5).setEnabled(false);
                // } else {
                // menu.getItem(3).getSubMenu().getItem(5).setEnabled(true);
                // }
                if (num == 0) {
                    menu.getItem(1).getSubMenu().getItem(2).setEnabled(false);
                }
                else {
                    menu.getItem(1).getSubMenu().getItem(2).setEnabled(true);
                }

                menu.getItem(1).setEnabled(true);
                menu.getItem(2).setEnabled(true);
                menu.getItem(3).setEnabled(true);
            }
        }
        else {
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setEnabled(false);
            menu.getItem(3).setEnabled(false);
            // menu.getItem(4).setEnabled(false);
            // menu.getItem(5).setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // invisiable();
        switch (item.getItemId()) {
            case MENU_ADD:
                FileUtil util = new FileUtil(this);
                util.createNewDir(currentFileString);
                Log.i(TAG, "currentFileString========"+currentFileString);
                break;

            case MENU_SEARCH:
                searchFileDialog();
                break;

            case MENU_CUT:
                managerF(myPosition, MENU_CUT);
                break;

            case MENU_PASTE:
                managerF(myPosition, MENU_PASTE);
                break;

            case MENU_DELETE:
                managerF(myPosition, MENU_DELETE);
                break;

            case MENU_RENAME:
                managerF(myPosition, MENU_RENAME);
                break;

            case MENU_COPY:
                managerF(myPosition, MENU_COPY);
                break;

            case MENU_HELP:
                FileMenu.setHelpFlag(1);
                FileMenu.filterType(MainExplorerActivity.this, MENU_HELP, null);
                break;
        }

        return true;
    }

    private OnItemClickListener ItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            if (listFile.size() > 0) {
                if (position >= listFile.size()) {
                    position = listFile.size() - 1;
                }

                // for chmod the file
                chmodFile(listFile.get(position).getPath());

                if (listFile.get(position).isDirectory()
                && listFile.get(position).canRead()) {
                    intList.add(position);
                    clickPos = 0;
                }
                else {
                    clickPos = position;
                }

                myPosition = clickPos;
                arrayFile.clear();
                arrayDir.clear();
                // for broken into the directory contains many files,click again
                // error
                preCurrentPath = currentFileString;
                keyBack = false;
                getFiles(listFile.get(position).getPath());
            }
        }
    };

    private OnItemClickListener deleListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            ControlListAdapter adapter = (ControlListAdapter) list.getAdapter();
            CheckedTextView check = (CheckedTextView) v
                                    .findViewById(R.id.check);
            String path = adapter.getList().get(position).getPath();

            if (check.isChecked()) {
                selectList.add(path);
            }
            else {
                selectList.remove(path);
            }
        }
    };

    /**
     * Depending on the file directory path judgment do:
     * Go to the directory the file: Open the file system application
     * CNcomment:根据文件路径判断执行的操作 目录:进入目录 文件:系统应用打开文件
     * @param path
     */

    public void getFiles(String path) {
        if (path == null)
        { return; }

        openFile = new File(path);

        if (openFile.exists()) {
            if (openFile.isDirectory()) {
                if (mIsSupportBD) {
                    if (FileUtil.getMIMEType(openFile, this).equals("video/bd")) {
                        preCurrentPath = "";
                        // currentFileString = path;
                        intList.remove(intList.size() - 1);
                        Intent intent = new Intent();
                        intent.setClassName(
                            "com.um.videoplayer",
                            "com.um.videoplayer.activity.MediaFileListService");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse(path), "video/bd");
                        intent.putExtra("sortCount", sortCount);
                        intent.putExtra("isNetworkFile", isNetworkFile);
                        startService(intent);
                        return;
                    }
                    else if (FileUtil.getMIMEType(openFile, this).equals("video/dvd")) {
                        preCurrentPath = "";
                        // currentFileString = path;
                        intList.remove(intList.size() - 1);
                        Intent intent = new Intent();
                        intent.setClassName(
                            "com.um.videoplayer",
                            "com.um.videoplayer.activity.MediaFileListService");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse(path), "video/dvd");
                        intent.putExtra("sortCount", sortCount);
                        intent.putExtra("isNetworkFile", isNetworkFile);
                        startService(intent);
                        return;
                    }
                }

                currentFileString = path;
                updateList(true);
            }
            else {
                super.openFile(this, openFile);
            }
        }
        else {
            refushList();
        }
    };

    /**
     * Populate the list of files to the data container
     * CNcomment:将文件列表填充到数据容器中
     * @param files
     * @param fileroot
     */
    public void fill(File fileroot) {
        try {
            // li = adapter.getFiles();
            if (clickPos >= listFile.size()) {
                clickPos = listFile.size() - 1;
            }
            String FileDir = "";
            String TmpFileDir = "";
            TmpFileDir = fileroot.getPath().toString();
            Log.i(TAG, "=TmpFileDir========================"+TmpFileDir+"===");
            if (TmpFileDir.length() > mountSdPath.length())
            {
            	FileDir = TmpFileDir.substring(mountSdPath.length(), TmpFileDir.length());
            }
            Log.i(TAG, "=FileDir========================"+FileDir+"===");
            pathTxt.setText(FileDir);
            numInfo.setText((clickPos + 1) + "/" + listFile.size());

            if (!fileroot.getPath().equals(directorys)) {
                parentPath = fileroot.getParent();
                currentFileString = fileroot.getPath();
            }
            else {
                currentFileString = directorys;
            }

            if (listFile.size() == 0) {
                numInfo.setText(0 + "/" + 0);
            }

            /*
             * if ((listFile.size() == 0) && (showBut.findFocus() == null) &&
             * (filterBut.findFocus() == null)) { sortBut.requestFocus(); }
             */

            if (clickPos >= 0) {
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.requestFocus();
                    listView.setSelection(clickPos);
                }
                else if (gridView.getVisibility() == View.VISIBLE) {
                    gridView.requestFocus();
                    gridView.setSelection(clickPos);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * File Operations Management
     * CNcomment:管理文件操作
     * @param position
     * @param item
     */
    private void managerF(final int position, final int item) {
        // for while first delete more than one file then cause exception
        // if(position == listFile.size()){
        if (position >= listFile.size()) {
            if (listFile.size() != 0) {
                myPosition = listFile.size() - 1;
            }
            else {
                myPosition = 0;
            }
        }

        menu_item = item;

        if (item == MENU_PASTE) {
            getMenu(myPosition, item, list);
        }
        else if (item == MENU_RENAME) {
            LayoutInflater inflater = LayoutInflater
                                      .from(MainExplorerActivity.this);
            View view = inflater.inflate(R.layout.dialog_list_bg,
                                         null);
            Button ok_button=(Button)view.findViewById(R.id.user_back_ok);
            Button cancel_button=(Button)view.findViewById(R.id.user_back_cancel);
            TextView text=(TextView)view.findViewById(R.id.operate_text);
            text.setText(R.string.str_rename);

            dialog = new NewCreateDialog(MainExplorerActivity.this,ok_button,cancel_button);
            /*
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                             getString(R.string.ok), imageButClick);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                             getString(R.string.cancel), imageButClick);*/
            ok_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

	                if (selectList.size() > 0) {
	                    getMenu(myPosition, menu_item, list);
	                    dialog.cancel();
	                }
	                else {
	                    FileUtil.showToast(MainExplorerActivity.this,
	                                       MainExplorerActivity.this
	                                       .getString(R.string.select_file));
	                }
	            
				}
			});
            cancel_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
            dialog.show();
            dialog.getWindow().setContentView(view);
            
            /*dialog = FileUtil.setDialogParams(dialog,
                                              MainExplorerActivity.this);*/
            /*dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextAppearance(MainExplorerActivity.this,
                               android.R.style.TextAppearance_Large_Inverse);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .requestFocus();*/
            /*dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextAppearance(MainExplorerActivity.this,
                               android.R.style.TextAppearance_Large_Inverse);*/
            list = (ListView) dialog
                   .findViewById(R.id.lvSambaServer);
            selectList.clear();
            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setAdapter(new ControlListAdapter(MainExplorerActivity.this,
                                                   listFile));
            list.setItemChecked(myPosition, true);
            list.setSelection(myPosition);
            selectList.add(listFile.get(myPosition).getPath());
            list.setOnItemClickListener(deleListener);
            /*dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .requestFocus();*/
            //ok_button.requestFocus();
        }
        else {
            LayoutInflater inflater = LayoutInflater
                                      .from(MainExplorerActivity.this);
            /*View view = inflater.inflate(R.layout.samba_server_list_dlg_layout,
                                         null);*/
            View view = inflater.inflate(R.layout.dialog_list_bg,
                    null);
            /*dialog = new NewCreateDialog(MainExplorerActivity.this);
            dialog.setView(view);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                             getString(R.string.ok), imageButClick);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                             getString(R.string.cancel), imageButClick);*/

			Button ok_button=(Button)view.findViewById(R.id.user_back_ok);
			Button cancel_button=(Button)view.findViewById(R.id.user_back_cancel);
			TextView text=(TextView)view.findViewById(R.id.operate_text);
            
		      ok_button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
		
		              if (selectList.size() > 0) {
		                  getMenu(myPosition, menu_item, list);
		                  dialog.cancel();
		              }
		              else {
		                  FileUtil.showToast(MainExplorerActivity.this,
		                                     MainExplorerActivity.this
		                                     .getString(R.string.select_file));
		              }
		          
					}
				});
		      cancel_button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
			if(item == MENU_COPY){
				text.setText(R.string.copy);
			}
			else if(item == MENU_CUT){
				text.setText(R.string.cut);
			}else {
				text.setText(R.string.delete);
			}
			
			dialog = new NewCreateDialog(MainExplorerActivity.this,ok_button,cancel_button);
            dialog.show();
            dialog.getWindow().setContentView(view);
            list = (ListView) view.findViewById(R.id.lvSambaServer);
          /*  dialog = FileUtil
                     .setDialogParams(dialog, MainExplorerActivity.this);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextAppearance(MainExplorerActivity.this,
                               android.R.style.TextAppearance_Large_Inverse);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextAppearance(MainExplorerActivity.this,
                               android.R.style.TextAppearance_Large_Inverse);*/
            // Make a list of multiple choice mode
            // CNcomment:让列表为多选模式
            list.setItemsCanFocus(false);
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            selectList.clear();
            list.setAdapter(new ControlListAdapter(MainExplorerActivity.this,
                                                   listFile));
            list.setItemChecked(myPosition, true);
            list.setSelection(myPosition);
            // list.setItemChecked(myPosition, true);
            // list.setSelection(myPosition);
            selectList.add(listFile.get(myPosition).getPath());
            list.setOnItemClickListener(deleListener);
            list.clearFocus();
            //dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
        }
    }

    /**
     * operation menu
     * CNcomment:操作菜单
     * @param position
     *            CNcomment:目标文件位置
     * @param item
     *            CNcomment:操作
     * @param list
     *            CNcomment:数据容器
     */
    private void getMenu(final int position, final int item, final ListView list) {
        int selectionRowID = (int) position;
        File file = null;
        File myFile = null;
        myFile = new File(currentFileString);
        FileMenu menu = new FileMenu();
        SharedPreferences sp = getSharedPreferences("OPERATE", SHARE_MODE);

        if (item == MENU_RENAME) {
            fileArray = new ArrayList<File>();

            //file = new File(currentFileString + "/"
            //+ listFile.get(selectionRowID).getName());
            for (int i = 0; i < selectList.size(); i++) {
                file = new File(selectList.get(i));
                fileArray.add(file);
            }

            fileArray.add(file);
            menu.getTaskMenuDialog(MainExplorerActivity.this, myFile, fileArray, sp,
                                   item, 0);
        }
        else if (item == MENU_PASTE) {
            fileArray = new ArrayList<File>();
            menu.getTaskMenuDialog(MainExplorerActivity.this, myFile,
                                   fileArray, sp, item, 1);
        }
        else {
            fileArray = new ArrayList<File>();

            for (int i = 0; i < selectList.size(); i++) {
                file = new File(selectList.get(i));
                fileArray.add(file);
            }

            menu.getTaskMenuDialog(MainExplorerActivity.this, myFile,
                                   fileArray, sp, item, 1);
        }
    }

    private Handler handler = new Handler() {

        public synchronized void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_RESULT:
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    synchronized (lock) {
                        if (arrayFile.size() == 0 && arrayDir.size() == 0) {
                            FileUtil.showToast(MainExplorerActivity.this,
                                               getString(R.string.no_search_file));
                            return;
                        }
                        else {
                            updateList(true);
                        }
                    }

                    break;

                case UPDATE_LIST:
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    if (listView.getVisibility() == View.VISIBLE) {
                        adapter = new FileAdapter(MainExplorerActivity.this,
                                                  listFile, listlayout);
                        listView.setAdapter(adapter);
                        listView.setOnItemSelectedListener(itemSelect);
                        listView.setOnItemClickListener(ItemClickListener);
                    }
                    else if (gridView.getVisibility() == View.VISIBLE) {
                        adapter = new FileAdapter(MainExplorerActivity.this,
                                                  listFile, gridlayout);
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(ItemClickListener);
                        gridView.setOnItemSelectedListener(itemSelect);
                    }
                    fill(new File(currentFileString));
                    
                    Object[] Item = null;
                    if (listFile.size() > 0)
                    {
                    	Item = listFile.toArray().clone();
                    }
                    AsyncLoadedImageTask = new AsyncLoadedImage();
                    AsyncLoadedImageTask.execute(Item);
                    break;

                case ISO_MOUNT_SUCCESS:
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    intList.add(myPosition);
                    getFiles(mISOLoopPath);
                    break;

                case ISO_MOUNT_FAILD:
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }

                    FileUtil.showToast(MainExplorerActivity.this,
                                       getString(R.string.new_mout_fail));
                    break;

                case CHMOD_FILE:
                    getMenu(myPosition, menu_item, list);
                    break;
                case APK_EXIT:
                	clickCount = 0;
                	break;
            }
        }
    };

    OnItemSelectedListener itemSelect = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
        int position, long id) {
            // invisiable();
            if (parent.equals(listView) || parent.equals(gridView)) {
                myPosition = position;
                gPosition = position;
            }

            numInfo.setText((position + 1) + "/" + listFile.size());
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * File list sorting
     * CNcomment:文件列表排序
     * @param sort
     *            CNcomment:排序方式
     */
    public void updateList(boolean flag) {
        if (flag) {
            // for broken into the directory contains many files,click again
            // error
        	cancellAsyncTask();
            listFile.clear();
            // sortBut.setOnClickListener(clickListener);
            showBut.setOnClickListener(clickListener);
            filterBut.setOnClickListener(clickListener);

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            progress = new MyProDialog(MainExplorerActivity.this);
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
                    }
                    else {
                        clickPos = myPosition;
                        currentFileString = preCurrentPath;
                        Log.v("\33[32m Main1", "onCancel" + currentFileString
                              + "\33[0m");
                        intList.remove(intList.size() - 1);
                    }

                    FileUtil.showToast(MainExplorerActivity.this,
                                       getString(R.string.cause_anr));
                }
            });
            thread.start();
        }
        else {
            adapter.notifyDataSetChanged();
            fill(new File(currentFileString));
        }
    }

    /**
     * Obtain a collection of files directory
     * CNcomment:获得目录下文件集合
     * @param path
     */
    private void geDdirectory(String path) {
        directorys = path;
        parentPath = path;
        currentFileString = path;
    }

    DialogInterface.OnClickListener imageButClick = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                if (selectList.size() > 0) {
                    getMenu(myPosition, menu_item, list);
                    dialog.cancel();
                }
                else {
                    FileUtil.showToast(MainExplorerActivity.this,
                                       MainExplorerActivity.this
                                       .getString(R.string.select_file));
                }
            }
            else {
                dialog.cancel();
            }
        }
    };

    int clickPos = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.w(" = ", " = " + keyCode);

        switch (keyCode) {
        case  KeyEvent.KEYCODE_MENU:
        	Log.i(TAG,"KEYCODE_MENU isKeyMenuEnable="+isKeyMenuEnable);
        	if(!isKeyMenuEnable){
        		return true;
        	}
        break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                super.onKeyDown(KeyEvent.KEYCODE_ENTER, event);
                return true;

            case KeyEvent.KEYCODE_BACK:// KEYCODE_BACK
                keyBack = true;
                String newName = pathTxt.getText().toString();

                // The current directory is the root directory
                // CNcomment:当前目录是根目录
                if (newName.equals("所有存储设备")
                		&& (listView.getVisibility() != View.VISIBLE)
                		&& (gridView.getVisibility() != View.VISIBLE)) {
                    clickCount++;

                    if (clickCount == 1) {
                        // for not choice the srt file then quit the FileM
                        // FileUtil.showToast(MainExplorerActivity.this,
                        // getString(R.string.quit_app));
                        if (getIntent().getBooleanExtra("subFlag", false)) {
                            Intent intent = new Intent();
                            intent.setClassName("com.huawei.activity",
                                                "com.huawei.activity.VideoActivity");
                            intent.putExtra("path", "");
                            intent.putExtra("pathFlag", false);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            FileUtil.showToast(MainExplorerActivity.this,
                                               getString(R.string.quit_app));
                            handler.sendEmptyMessageDelayed(APK_EXIT, 2000);
                        }
                    }
                    else if (clickCount == 2) {
                        // Empty the contents of the clipboard
                        // CNcomment:清空剪贴板内容
                        SharedPreferences share = getSharedPreferences("OPERATE",
                                                                       SHARE_MODE);
                        share.edit().clear().commit();

                        if (FileUtil.getToast() != null) {
                            FileUtil.getToast().cancel();
                        }

                        onBackPressed();
                    }
                }
                else {
                    clickCount = 0;

                    if (!currentFileString.equals(directorys)) {
                        arrayDir.clear();
                        arrayFile.clear();

                        if (newName.equals(ISO_PATH)) {
                            getFiles(prevPath);
                        }
                        else {
                            getFiles(parentPath);
                        }
                    }
                    else {
                        pathTxt.setText("所有存储设备");
                        numInfo.setText("");
                        showBut.setOnClickListener(null);
                        showBut.setImageResource(showArray[0]);
                        showCount = 0;
                        // sortBut.setOnClickListener(null);
                        // sortBut.setImageResource(sortArray[0]);
                        sortCount = 0;
                        filterBut.setOnClickListener(null);
                        filterBut.setImageResource(filterArray[0]);
                        filterCount = 0;
                        gridView.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        expandableListView.setVisibility(View.VISIBLE);
                        listFile.clear();
                        getsdList();
                    }

                    // Click the location of the parent directory
                    // CNcomment:点击的父目录位置
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
                        }
                        else if (gridView.getVisibility() == View.VISIBLE) {
                            clickPos = intList.get(pos);
                            myPosition = clickPos;
                            intList.remove(pos);
                        }
                        else if (expandableListView.getVisibility() == View.VISIBLE) {
                            expandableListView.requestFocus();

                            if (groupPosition < groupList.size()) {
                                if (intList.get(pos) >= groupList
                                    .get(groupPosition).getChildList().size()) {
                                    expandableListView.setSelectedChild(
                                        groupPosition, 0, true);
                                }
                                else {
                                    expandableListView.setSelectedChild(
                                        groupPosition, intList.get(pos), true);
                                }
                            }
                            else {
                                groupPosition = 0;
                                expandableListView.setSelectedChild(groupPosition,
                                                                    intList.get(pos), true);
                            }
                        }
                    }
                }

                return true;

            case KeyEvent.KEYCODE_SEARCH: // search
                if (expandableListView.getVisibility() == View.INVISIBLE) {
                    searchFileDialog();
                }

                return true;

            case KeyEvent.KEYCODE_INFO: // info
                if (expandableListView.getVisibility() == View.INVISIBLE) {
                    FileUtil util = new FileUtil(this);

                    if (listFile.size() < myPosition) {
                        return true;
                    }

                    util.showFileInfo(listFile.get(myPosition));
                }

                return true;

            case KeyEvent.KEYCODE_PAGE_UP:
                super.onKeyDown(keyCode, event);
                break;

            case KeyEvent.KEYCODE_PAGE_DOWN:
                super.onKeyDown(keyCode, event);
                break;

                //jly 20140313 modify the focus of gridview
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if ((gPosition != (gridView.getCount()-1))
                		&& (gPosition%7 == 6))
                {
                    gridView.setSelection(gPosition + 1);
                }

                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if ((gPosition != 0) && (gPosition%7 == 0))
                {
                    gridView.setSelection(gPosition - 1);
                }

                break;
           case KeyEvent.KEYCODE_DPAD_UP:
                if(filterBut.hasFocus() || showBut.hasFocus()){
                     return true;
                } else {
                     return super.onKeyDown(keyCode, event);
                }
        }

        //jly
        return false;
    }

    public String getCurrentFileString() {
        return currentFileString;
    }

    protected void onResume() {
        super.onResume();

        // for grally3D delete the file, flush the data
        File file = new File(currentFileString);
        if (!currentFileString.equals("") && preCurrentPath.equals(currentFileString) && !file.isDirectory()) {
            updateList(true);
        }
    }

    public ListView getListView() {
        return listView;
    }

    private void getMountEquipmentList() {
        String[] mountType = getResources().getStringArray(R.array.mountType);
        MountInfo info = new MountInfo(this);
        groupList = new ArrayList<GroupInfo>();
        childList = new ArrayList<Map<String, String>>();
        GroupInfo group = null;

        for (int j = 0; j < mountType.length; j++) {
            group = new GroupInfo();
            childList = new ArrayList<Map<String, String>>();

            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        Map<String, String> map = new HashMap<String, String>();
                        // map.put(MOUNT_DEV, info.dev[i]);
                        map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
                        map.put(MOUNT_PATH, info.path[i]);
                        // map.put(MOUNT_LABLE, info.label[i]);
                        map.put(MOUNT_LABLE, "");
                        map.put(MOUNT_NAME, info.partition[i]);
                        childList.add(map);
                    }
                }
            }

            if (childList.size() > 0) {
                group.setChildList(childList);
                group.setName(mountType[j]);
                groupList.add(group);
            }
        }
    }

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_REMOVED)
            || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                mHandler.removeMessages(0);
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessageDelayed(msg, 1000);

                if (action.equals(Intent.ACTION_MEDIA_REMOVED)
                        || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                        FileUtil.showToast(context,
                              getString(R.string.uninstall_equi));
                }
                else if (action.equals(Intent.ACTION_MEDIA_MOUNTED))
                    FileUtil.showToast(context,
                              getString(R.string.install_equi));
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // if (pathTxt.getText().toString().equals("")) {
            refushList();
            // }
            super.handleMessage(msg);
        }
    };

    private void refushList() {
        getMountEquipmentList();
        expandableListView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        intList.clear();
        numInfo.setText("");
        pathTxt.setText("所有存储设备");
        
		filterBut.setVisibility(View.INVISIBLE);
		showBut.setVisibility(View.INVISIBLE);
        bottomView.setVisibility(View.INVISIBLE);
        
        ExpandAdapter adapter = new ExpandAdapter(MainExplorerActivity.this,
                                                  groupList);
        expandableListView.setAdapter(adapter);

        for (int i = 0; i < groupList.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void getUSB() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
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

    // for broken into the directory contains many files,click again error
    class MyThread extends MyThreadBase {
        public void run() {
            if (getFlag()) {
                setFlag(false);

                synchronized (lock) {
                    util = new FileUtil(MainExplorerActivity.this, filterCount,
                                        arrayDir, arrayFile, currentFileString);
                }
            }
            else {
                util = new FileUtil(MainExplorerActivity.this, filterCount,
                                    currentFileString);
            }

            if (currentFileString.startsWith(ISO_PATH)) {
                listFile = util.getFiles(sortCount, "net");
            }
            else {
                listFile = util.getFiles(sortCount, "local");
            }

            if (getStopRun()) {
                if (keyBack) {
                    if (pathTxt.getText().toString().equals(ISO_PATH)) {
                        currentFileString = util.currentFilePath;
                    }
                }
            }
            else {
                currentFileString = util.currentFilePath;
                handler.sendEmptyMessage(UPDATE_LIST);
            }
        }

        /**
         * Blu-ray ISO file filter, for maximum video file
         * CNcomment:过滤蓝光ISO文件，获取最大视频文件
         */
        public File getMaxFile(List<File> listFile) {
            int temp = 0;

            for (int i = 0; i < listFile.size(); i++) {
                if (listFile.get(temp).length() <= listFile.get(i).length())
                { temp = i; }
            }

            return listFile.get(temp);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void operateSearch(boolean b) {
        if (b) {
            for (int i = 0; i < fileArray.size(); i++) {
                listFile.remove(fileArray.get(i));
            }
        }
    }

    protected void onStop() {
        super.onStop();
        super.cancleToast();
    }

    public TextView getPathTxt() {
        return pathTxt;
    }

    private static IMountService getMountService() {
        IBinder service = ServiceManager.getService("mount");

        if (service != null) {
            return IMountService.Stub.asInterface(service);
        }
        else {
            Log.e(TAG, "Can't get mount service");
        }

        return null;
    }
    
    /** 
     * 根据指定的图像路径和大小来获取缩略图 
     * 此方法有两点好处： 
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
     *        用这个工具生成的图像不会被拉伸。 
     * @param imagePath 图像的路径 
     * @param width 指定输出图像的期望宽度 
     * @param height 指定输出图像的期望高度 
     * @return 生成的缩略图 
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
    	Bitmap bitmap = null;
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	// 获取这个图片的宽和高，注意此处的bitmap为null  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // 设为 false  
        // 计算缩放比  
        int ThumbnailHeight = 0;
        int ThumbnailWidth = 0;
        int SampleSize = 0;
        
        if (options.outWidth > options.outHeight)
        {
        	SampleSize = (options.outWidth/width);
        	ThumbnailWidth = width;
        	ThumbnailHeight = ((options.outHeight*width)/options.outWidth);
        }
        else
        {
        	SampleSize = (options.outHeight/height);
        	ThumbnailWidth = ((options.outWidth*height)/options.outHeight);
        	ThumbnailHeight = height;
        }
        
        if (SampleSize < 1)
        {
        	SampleSize = 1;
        }
        
        options.inSampleSize = SampleSize;  
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, ThumbnailWidth, ThumbnailHeight,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;
    }
    
    /** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        int ThumbnailHeight = 0;
        int ThumbnailWidth = 0;
        
        if (bitmap.getWidth() > bitmap.getHeight())
        {
        	ThumbnailWidth = width;
        	ThumbnailHeight = ((bitmap.getHeight()*width)/bitmap.getWidth());
        }
        else
        {
        	ThumbnailWidth = ((bitmap.getWidth()*height)/bitmap.getHeight());
        	ThumbnailHeight = height;
        }
        
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, ThumbnailWidth, ThumbnailHeight,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }
    
    class AsyncLoadedImage extends AsyncTask<Object, customerBitmap, Object> {
    	@Override
        protected Object doInBackground(Object... params) {
			if (params == null)
			{
				return null;
			}
			
			Object[] Item = params;
			
            for (int i = 0; i < Item.length; i++)
            {
            	if (isCancelled())
            	{
            		return null;
            	}

                String f_type = FileUtil.getMIMEType(Item[i].toString(), MainExplorerActivity.this);
                if ("image/*".equals(f_type) || "video/*".equals(f_type))
                {
                    try {
                    	customerBitmap bitmap = new customerBitmap();
                    	if ("image/*".equals(f_type))
                    	{
                    		bitmap.imageBitmap = getImageThumbnail(Item[i].toString(), 150, 150);
                    	}
                    	else if ("video/*".equals(f_type))
                    	{
                    		bitmap.imageBitmap = getVideoThumbnail(Item[i].toString(), 150, 150, 
                    				MediaStore.Images.Thumbnails.MINI_KIND);
                    	}
                        bitmap.fileName = ((File)Item[i]).getName();
                        
                        if (bitmap.imageBitmap != null) {
                            publishProgress(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    	
    	@Override
    	protected void onCancelled() {
    		// TODO Auto-generated method stub
    	}
    	
    	@Override
    	protected void onProgressUpdate(customerBitmap... values) {
    		// TODO Auto-generated method stub
    		Map<String, customerBitmap> map = 
        			new HashMap<String, customerBitmap>();
        	map.put("imageBitmap", values[0]);
        	adapter.addThumbnail(map);
    		adapter.imageChanged(values[0]);
    		if (listView.getVisibility() == View.VISIBLE) {
    			listView.invalidate();
    		}
    		else if (gridView.getVisibility() == View.VISIBLE) {
    			gridView.invalidate();
    		}
    	}
    	
    	
    	@Override
    	protected void onPostExecute(Object result) {
    		// TODO Auto-generated method stub
    	}
    }
    
    private void cancellAsyncTask() {
	    if (AsyncLoadedImageTask != null && 
				!AsyncLoadedImageTask.getStatus().toString().equals("FINISHED"))
		{
	    	AsyncLoadedImageTask.cancel(true);
		}
    }

}
