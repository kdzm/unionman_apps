package com.unionman.filebrowser.localplayer;

import com.unionman.filebrowser.R;
import com.unionman.filebrowser.localplayer.adapter.FileAdapter;
import com.unionman.filebrowser.localplayer.util.FileUtil;
import com.unionman.filebrowser.localplayer.util.PreferenceUtil;
import com.unionman.filebrowser.localplayer.util.SocketClient;
import com.unionman.filebrowser.localplayer.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity {

	/**
	 * 列表方式显示数据容器
	 */
	protected ListView listView;

	/**
	 * 平铺方式显示数据容器
	 */
	protected GridView gridView;


	/**
	 * 显示方式数组
	 */
	protected int[] showTypeArray;

	/**
	 * 显示方式按钮
	 */
	protected ImageButton showBtn;

	/**
	 * 当前显示方式
	 */
	protected int currShowType = 0;

	/**
	 * 当前排序方式
	 */
	protected int currSortType = 0;

	/**
	 * 当前过滤方式
	 */
	protected int currfilterType = 0;

	/**
	 * 存储设备本地挂载路径
	 */
	public String mountSdPath = "";

	/**
	 * 更新文件列表
	 */
	protected final static int UPDATE_LIST = 3;

	/**
	 * 路径显示
	 */
	protected TextView pathTxt;

	/**
	 * 加载对话框
	 */
	protected CustomProgressDialog progress;

	/**
	 * 文件列表
	 */
	protected List<File> arrayFile;

	/**
	 * 目录列表
	 */
	protected List<File> arrayDir;

	/**
	 * 文件缩略图平铺资源
	 */
	protected int gridlayout = 0;

	/**
	 * 文件列表列表资源 
	 */
	protected int listlayout = 0;

	/**
	 * 文件读写模式
	 */
	protected final static int SHARE_MODE = MODE_WORLD_READABLE
			+ MODE_WORLD_WRITEABLE;

	/**
	 * 当前文件路径
	 */
	protected String currentFileString = "";
	
	/**
	 * 对象锁
	 */
	public byte[] lock = new byte[0];

	/**
	 * 文件数据适配器
	 */
	protected FileAdapter adapter;

	/**
	 * 文件列表集合
	 */
	protected List<File> listFile;

	/**
	 * SOCKET客户端
	 */
	protected static SocketClient socketClient = null;

	/**
	 * 想打开的文件
	 */
	protected File openFile;


	// last click on the file path
	protected String preCurrentPath = "";

	/**
	 * 是否通过返回刷新界面
	 */
	protected boolean keyBack = true;

	/**
	 * 显示方式提示
	 */
	private String[] showMethod;

	/**
	 * 获取文件列表线程基类 
	 */
	public MyThreadBase thread;

	/**
	 * 更新文件列表
	 * @param flag
	 */
	public abstract void updateList(boolean flag);

	/**
	 * 填充文件列表到数据容器
	 * @param file
	 */
	public abstract void fill(File file);

	/**
	 * 获得消息接收对象
	 * @return
	 */
	public abstract Handler getHandler();

	/**
	 * 获得文件列表
	 * @param path
	 */
	public abstract void getFiles(String path);

	/**
	 * 更新缩略图处理器
	 */
	private Handler handler;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handler=new Handler();
		showTypeArray = new int[] { R.drawable.head_style_list,R.drawable.head_style_grid };


		showMethod = getResources().getStringArray(R.array.show_method);

		arrayFile = new ArrayList<File>();

		arrayDir = new ArrayList<File>();

		listFile = new ArrayList<File>();
	}

	final static String MUSIC_PATH = "music_path";
	/**
	 * 打开文件
	 */
	public void openFile(BaseActivity activity, File file) {
		Intent intent = new Intent();
		String type = "*/*";
		type = FileUtil.getMIMEType(file, activity);
		if (type.equals("audio/*")) {
			/*intent.setClassName("com.unionman.android.music",
					 "com.unionman.android.music.MediaFileListService");
					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(file), type);
						intent.putExtra("currSortType", currSortType);
						activity.startService(intent);*/
		    /*intent.setType(type);
		    intent.setAction(Intent.ACTION_VIEW);
		    intent.setDataAndType(Uri.fromFile(file), type);
			startActivity(intent);*/
                    intent.setClassName("com.um.music", "com.um.music.TransitActivity");
            	    intent.setType(type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(file), type);
                    startActivity(intent);
			return;
		} else if (type.equals("image/*") || type.equals("image/gif")) {
			/*intent.setClassName("com.unionman.android.gallery3d",
					"com.unionman.android.gallery3d.list.ImageFileListService");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			startService(intent);*/
			/*intent.setClassName("com.hisilicon.android.gallery3d",
			          "com.hisilicon.android.gallery3d.list.ImageFileListService");
		      SharedPreferences p = getSharedPreferences("musicPath", SHARE_MODE);
		      String path = p.getString(MUSIC_PATH, "");
		      intent.putExtra("path", path);
		      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		      intent.setDataAndType(Uri.fromFile(file), "image/*");
		      startService(intent);*/
			Log.i("==","===========start com.um.umgallery=============");
            intent.setType(type);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.um.umgallery" ,
                        "com.um.umgallery.UMGallery");
            intent.setDataAndType(Uri.fromFile(file), type);
			startActivity(intent);
			Log.i("==","===========end start com.um.umgallery=============");
			return;
		} /*else if (type.equals("image/gif")) {  
				intent.setClassName("com.unionman.android.gallery3d",
						"com.unionman.android.gallery3d.list.ImageFileListService");   
				Log.d("LocalPlayer ","send intent image/gif");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
 				intent.setDataAndType(Uri.fromFile(file), "image/gif");
                startService(intent);    
                return;            
		}*/
		else if (type.equals("video/*")) {
			/*intent.setClassName("com.unionman.videoplayer",
					"com.unionman.videoplayer.MainActivity");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("path", file.getAbsolutePath());
			activity.startActivity(intent);*/
			/*intent.setClassName("com.hisilicon.android.videoplayer",
					"com.hisilicon.android.videoplayer.activity.MediaFileListService");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), type);
			intent.putExtra("sortCount", 0);
			activity.startService(intent);*/
            		intent.setClassName("com.um.videoplayer",
                     		           "com.um.videoplayer.activity.MediaFileListService");
            		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		intent.setDataAndType(Uri.fromFile(file), type);
	                intent.putExtra("sortCount", 0);
            		activity.startService(intent);
			return;
		} else if (type.equals("apk/*")) {
            String debug=SystemProperties.get("persist.sys.debugenable");
            if(debug!=null&&debug.equals("1")){
            	intent.setAction(android.content.Intent.ACTION_VIEW);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.setAction("android.intent.action.INSTALL_PACKAGE");
    			intent.setDataAndType(Uri.fromFile(file),
    					"application/vnd.android.package-archive");
    			activity.startActivity(intent);
            }else{
            	ToastUtil.showToast(this, Toast.LENGTH_SHORT, "抱歉,暂不支持安装未经许可的应用");
            }
		}else {
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("currSortType", currSortType);
			Log.w("TYPE", " = " + type);
			intent.setDataAndType(Uri.fromFile(file), type);
			activity.startActivity(intent);
		}
		
	}


	/**
	 * 显示方式按钮点击事件 
	 */
	public OnClickListener clickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v.equals(showBtn)) {
				if (currShowType == showTypeArray.length - 1) {
					currShowType = 0;
				} else {
					currShowType++;
				}
				//保存显示方式
				PreferenceUtil.setPrefInt(BaseActivity.this, "currShowType", currShowType);
				showBtn.setImageResource(showTypeArray[currShowType]);
				//提示显示方式
				ToastUtil.showToast(BaseActivity.this,Toast.LENGTH_SHORT,
						showMethod[currShowType]);
				
				if (currShowType == 0) {
					gridView.setVisibility(View.INVISIBLE);
					listView.setVisibility(View.VISIBLE);
				}else if (currShowType == 1) {
					gridView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.INVISIBLE);
				}
			} 
			updateList(true);
		}
		
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// previous
		case KeyEvent.KEYCODE_PAGE_UP:
			if (!pathTxt.getText().toString().equals("")) {
				if (listView.getVisibility() == View.VISIBLE) {
					if (listView.getFirstVisiblePosition() >= 6) {
						listView.setSelection(listView
								.getFirstVisiblePosition() - 6);
					} else {
						listView.setSelection(0);
					}
					return true;
				}
			}

			// next page
		case KeyEvent.KEYCODE_PAGE_DOWN:
			if (!pathTxt.getText().toString().equals("")) {
				if (listView.getVisibility() == View.VISIBLE) {
					listView.setSelection(listView.getLastVisiblePosition());
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * escape path
	 * @param path
	 * @return  
	 */
	public static String tranString(String path) {
		String tranPath = "";
		for (int i = 0; i < path.length(); i++) {
			tranPath += "\\" + path.substring(i, i + 1);
		}

		return tranPath;
	}

	/**
	 * clear files list
	 */
	public void clearList(List<File> fileDir, List<File> file) {

		if (fileDir != null) {
			fileDir.clear();
		}
		if (file != null) {
			file.clear();
		}
	}


	/**
	 * modify the file permissions
	 * @param path
	 */
	public static void chmodFile(String path) {
		socketClient = new SocketClient();
		if (null != socketClient) {
			try {
				socketClient.writeMess("system chmod 777 " + tranString(path));
				socketClient.readNetResponseSync();
			} catch (Exception e) {
			}
		}
	}


	public class MyThreadBase extends Thread {
		private boolean runFlag = false;

		public synchronized void setStopRun(boolean flag) {
			this.runFlag = flag;
		}

		public synchronized boolean getStopRun() {
			return runFlag;
		}

		public void run() {
		}
	}

	/**
	 * whether the thread is running
	 * @param thrd
	 * @return
	 */
	public boolean threadBusy(Thread thrd) {
		if (thrd == null)
			return false;

		if ((thrd.getState() != Thread.State.TERMINATED)
				&& (thrd.getState() != Thread.State.NEW)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * under the program is running state decided whether to open a new thread
	 * @param thrd
	 */
	public void waitThreadToIdle(Thread thrd) {
		while (threadBusy(thrd)) {
			try {
				Log.w("THREAD", "THREAD");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
