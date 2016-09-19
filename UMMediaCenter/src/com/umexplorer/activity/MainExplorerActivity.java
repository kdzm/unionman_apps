package com.umexplorer.activity;

import java.io.File;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.bool;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.umexplorer.R;
import com.umexplorer.common.CommonActivity;
import com.umexplorer.common.ControlListAdapter;
import com.umexplorer.common.ExpandAdapter;
import com.umexplorer.common.FileAdapter;
import com.umexplorer.common.FileAdapter.customerBitmap;
import com.umexplorer.common.FileUtil;
import com.umexplorer.common.FilterType;
import com.umexplorer.common.GroupInfo;
import com.umexplorer.common.MountInfo;
import com.umexplorer.common.MyProDialog;
import com.umexplorer.common.NewCreateDialog;
import com.hisilicon.android.sdkinvoke.HiSdkinvoke;

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

    // file list selected
    // CNcomment:选中的文件列表
    List<String> selectList = null;

    // operation list
    // CNcomment:操作列表
    ListView list;

    // mount list
    // CNcomment:挂载列表
    List<Map<String, Object>> sdlist;

    ArrayList<HashMap<String, Object>> deviceList;

    AlertDialog dialog;

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

    private TextView titleTxt;
    
    protected String FileType;
    
    protected String MountPath;
    
    protected String FileTitle;
    
    protected View[] FilterImgView;
    
    protected AsyncLoadedImage AsyncLoadedImageTask = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String chipVersion = HiSdkinvoke.getChipVersion();
        Log.i(TAG, "chipVersion:" + chipVersion);

        Intent intent=getIntent(); 
		FileType = intent.getStringExtra("FileType");
		//MountPath = intent.getStringExtra("MountPath");
		//FileTitle = intent.getStringExtra("FileTitle");
		
        if (chipVersion.equals("Unknown chip ID"))
        { finish(); }

        FilterType.filterType(MainExplorerActivity.this);
        init();
        selectList = new ArrayList<String>();
        getUSB();
        
    }

    private void init() {
		
    	FilterImgView = new View[] {
    			findViewById(R.id.filterBut),
    			findViewById(R.id.filterButPicture),
    			findViewById(R.id.filterButAudio),
    			findViewById(R.id.filterButVideo),
    			findViewById(R.id.showBut)
    	};

        showBut = (Button) findViewById(R.id.showBut);
        showLayout = (LinearLayout) findViewById(R.id.show_layout);
        // sortBut = (ImageButton) findViewById(R.id.sortBut);
        filterBut = (Button) findViewById(R.id.filterBut);
		filterButPicture = (Button) findViewById(R.id.filterButPicture);
		filterButAudio = (Button) findViewById(R.id.filterButAudio);
		filterButVideo = (Button) findViewById(R.id.filterButVideo);
        intList = new ArrayList<Integer>();
        listFile = new ArrayList<File>();
        gridlayout = R.layout.gridfile_row;
        listlayout = R.layout.file_row;
        listView = (ListView) findViewById(R.id.listView);
        gridView = (GridView) findViewById(R.id.gridView);
        deviceGridLayout = R.layout.gridview_device_row;
        gridViewDevice = (GridView) findViewById(R.id.gridView_device);
        pathTxt = (TextView) findViewById(R.id.file_txt_Path);
        titleTxt = (TextView) findViewById(R.id.file_txt_Title);
        numInfo = (TextView) findViewById(R.id.file_txt_num);
		//titleTxt.setText(FileTitle);
		
		for (int i = 0; i < FilterImgView.length; i++) {
			FilterImgView[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus)
					{
						if (v.equals(FilterImgView[4]))
						{
							showLayout.setBackgroundResource(R.drawable.filter_focused);
						}
						else
						{
							v.setBackgroundResource(R.drawable.filter_focused);
						}
					}
					else
					{
						if ((v.equals(FilterImgView[0])&& filterCount == 0)
							|| (v.equals(FilterImgView[1])&& filterCount == 1)
							|| (v.equals(FilterImgView[2])&& filterCount == 2)
							|| (v.equals(FilterImgView[3])&& filterCount == 3))
						{
							v.setBackgroundResource(R.drawable.filter_selected);
						}
						else if (v.equals(FilterImgView[4]))
						{
							showLayout.setBackgroundResource(R.drawable.filter_bg);
						}
						else
						{
							v.setBackgroundResource(R.drawable.filter_bg);
						}
					}
				}
			});
		}
		
        for (int i = 0; i < filterMethod.length; i++)
        {
        	//Log.i(TAG, "==="+i+"==="+filterMethod[i]);
        	if (filterMethod[i].equals(FileType))
        	{
        		filterCount = i;
        	}
        }
        
        if (filterCount == 0)
		{
        	filterBut.requestFocus();
			filterBut.setBackgroundResource(R.drawable.filter_selected);
		}
        else if (filterCount == 1)
		{
        	filterButPicture.requestFocus();
			filterButPicture.setBackgroundResource(R.drawable.filter_selected);
		}
        else if (filterCount == 2)
		{
        	filterButAudio.requestFocus();
			filterButAudio.setBackgroundResource(R.drawable.filter_selected);
		}
        else if (filterCount == 3)
		{
        	filterButVideo.requestFocus();
			filterButVideo.setBackgroundResource(R.drawable.filter_selected);
		}
        
        getsdList();
        isNetworkFile = false;
    }

    /**
     * Obtain mount list
     * CNcomment:获得挂载列表
     */
    public void getsdList() {
        
    	getMountEquipmentList();
    	
    	SimpleAdapter deviceAdapter = new SimpleAdapter(this, deviceList, deviceGridLayout, 
				new String[] {"ItemImage","ItemText"}, 
				new int[] {R.id.gridview_device_image,R.id.gridview_device_text});
		
    	gridViewDevice.setAdapter(deviceAdapter);
    	gridViewDevice.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> l, View v, int position,
    				long id) {
    			// TODO Auto-generated method stub
    			MountPath = (String)deviceList.get(position).get("mountPath");
    			FileTitle = (String)deviceList.get(position).get("ItemText");
    			mountSdPath = MountPath;
    			titleTxt.setText(FileTitle);
    			arrayFile.clear();
    	        arrayDir.clear();
    			directorys = MountPath;
    			listFile.clear();
    	        clickPos = 0;
    	        myPosition = 0;
    	        geDdirectory(directorys);
    			intList.add(0);
    	        updateList(true);
    		}
		});
        
    	MountPath = (String)deviceList.get(0).get("mountPath");
		FileTitle = (String)deviceList.get(0).get("ItemText");
        mountSdPath = MountPath;
		arrayFile.clear();
        arrayDir.clear();
		directorys = MountPath;
		showCount = 1;
		titleTxt.setText(FileTitle);
		showBut.setText(showMethod[showCount]);
		//listView.setVisibility(View.VISIBLE);
		gridView.setVisibility(View.VISIBLE);
		listFile.clear();
        clickPos = 0;
        myPosition = 0;
        geDdirectory(directorys);
		intList.add(0);
        updateList(true);
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
            if (TmpFileDir.length() > MountPath.length())
            {
            	FileDir = TmpFileDir.substring(MountPath.length(), TmpFileDir.length());
            }
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
                    //getMenu(myPosition, menu_item, list);
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
			filterButPicture.setOnClickListener(clickListener);
			filterButAudio.setOnClickListener(clickListener);
			filterButVideo.setOnClickListener(clickListener);

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            //progress = new ProgressDialog(MainExplorerActivity.this);
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

    int clickPos = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.w(TAG, "keyCode:" + keyCode);

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                super.onKeyDown(KeyEvent.KEYCODE_ENTER, event);
                return true;

            case KeyEvent.KEYCODE_BACK:// KEYCODE_BACK
                keyBack = true;
                String newName = pathTxt.getText().toString();

                // The current directory is the root directory
                // CNcomment:当前目录是根目录
                if (newName.equals("")) {
                    finish();
                }
                else {
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
                        pathTxt.setText("");
                        numInfo.setText("");
                        showBut.setOnClickListener(null);
                        //showBut.setImageResource(showArray[0]);
                        showCount = 0;
                        // sortBut.setOnClickListener(null);
                        // sortBut.setImageResource(sortArray[0]);
                        sortCount = 0;
                        filterBut.setOnClickListener(null);
                        filterBut.setBackgroundResource(R.drawable.filter_bg);
                        filterCount = 0;
                        gridView.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.INVISIBLE);
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
                    }
                }

                return true;

            case KeyEvent.KEYCODE_SEARCH: // search

                return true;
            case KeyEvent.KEYCODE_MENU:
            	View CurrentFocusView = getCurrentFocus();
            	if (CurrentFocusView.equals(gridViewDevice))
            	{
            		switch (filterCount)
            		{
            			case 0:
            				filterBut.requestFocus();
            				break;
            			case 1:
            				filterButPicture.requestFocus();
            				break;
            			case 2:
            				filterButAudio.requestFocus();
            				break;
            			case 3:
            				filterButVideo.requestFocus();
            				break;
            			default:
            				filterBut.requestFocus();
            				break;
            		}
            	}
            	else if (CurrentFocusView.equals(listView)
            				|| CurrentFocusView.equals(gridView))
            	{
            		gridViewDevice.requestFocus();
            	}
            	else
            	{
            		if (listView.getVisibility() == View.VISIBLE)
            		{
            			listView.requestFocus();
            		}
            		else if (gridView.getVisibility() == View.VISIBLE)
            		{
            			gridView.requestFocus();
            		}
            	}
                return true;
            case KeyEvent.KEYCODE_INFO: // info
                FileUtil util = new FileUtil(this);

                if (listFile.size() < myPosition) {
                    return true;
                }

                util.showFileInfo(listFile.get(myPosition));

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
                if(filterBut.hasFocus() 
					|| filterButPicture.hasFocus() 
					|| filterButAudio.hasFocus()
					|| filterButVideo.hasFocus()
					|| showBut.hasFocus()){
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

	private boolean checkRootPathUnmounted() {
        String[] mountType = getResources().getStringArray(R.array.mountType);
        MountInfo info = new MountInfo(this);

        for (int j = 0; j < mountType.length; j++)
		{
            for (int i = 0; i < info.index; i++)
			{
                if ((info.type[i] == j) && (info.path[i].equals(MountPath)))
				{
					return false;
                }
            }
        }

		return true;
    }
	
    private void getMountEquipmentList() {
        String[] mountType = getResources().getStringArray(R.array.mountType);
        MountInfo info = new MountInfo(this);
        GroupInfo group = null;
        deviceList = new ArrayList<HashMap<String, Object>>();
		
        for (int j = 0; j < mountType.length; j++) {
            group = new GroupInfo();
			
            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ItemImage", R.drawable.device_selector);
                        map.put("ItemText", mountType[j]+":"+info.partition[i]);
                        map.put("mountPath", info.path[i]);
                        deviceList.add(map);
                    }
                }
            }
        }
    }

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.i(TAG, action);
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
                else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    FileUtil.showToast(context,
                                       getString(R.string.install_equi));
                	}
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            refushList();
            super.handleMessage(msg);
        }
    };

    private void refushList() {
    	getMountEquipmentList();
    	
    	SimpleAdapter deviceAdapter = new SimpleAdapter(this, deviceList, deviceGridLayout, 
				new String[] {"ItemImage","ItemText"}, 
				new int[] {R.id.gridview_device_image,R.id.gridview_device_text});
		
    	gridViewDevice.setAdapter(deviceAdapter);
        if (checkRootPathUnmounted())
        {
        	MountPath = (String)deviceList.get(0).get("mountPath");
    		FileTitle = (String)deviceList.get(0).get("ItemText");
            mountSdPath = MountPath;
    		arrayFile.clear();
            arrayDir.clear();
    		directorys = MountPath;
    		titleTxt.setText(FileTitle);
    		listFile.clear();
            clickPos = 0;
            myPosition = 0;
            geDdirectory(directorys);
    		intList.add(0);
            updateList(true);
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
