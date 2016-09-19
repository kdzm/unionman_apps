package cn.com.unionman.umtvsetting.umsysteminfo;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class LocalUpgradeActivity extends Activity {
    private final static String MOUNT_LABLE = "mountLable";
    private final static String MOUNT_TYPE = "mountType";
    private final static String MOUNT_PATH = "mountPath";
    private final static String MOUNT_NAME = "mountName";
    private final static String MOUNT_PARTITION = "partition";
    private final static String FILE_NAME = "/update.zip";
	private static final String TAG = "LocalUpgradeActivity";
    
    private ListView local_upgrade_list;
    private ArrayList<HashMap<String, String>> listName;
    private SimpleAdapter mSimpleAdapter;
    private SystemUpdateDialog mSystemUpdateDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.local_upgrade);
        local_upgrade_list = (ListView) findViewById(R.id.local_upgrade_list);
        listName = getMountEquipmentList();
        mSimpleAdapter = new SimpleAdapter(LocalUpgradeActivity.this, listName, R.layout.listview_item,
        		         new String[]{MOUNT_NAME}, new int[]{R.id.item_tv});
        local_upgrade_list.setAdapter(mSimpleAdapter);
        local_upgrade_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
                Log.i(TAG,"onItemClick position="+position);
                if (isFileExist(listName.get(position).get(MOUNT_PATH),
                        FILE_NAME)) {
                    createDialog(350, 400,
                            SystemUpdateDialog.LOCAL_UPDATE_HAVE,
                            (listName.get(position).get(MOUNT_PATH)));
                } else {
                    // If the letter does not upgrade package
                    createDialog(350, 400,
                            SystemUpdateDialog.LOCAL_UPDATE_NONE,
                            null);
                }
			}
		});
        
        local_upgrade_list.setOnKeyListener(new  android.view.View.OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN) { 
					Log.i(TAG,"local_upgrade_list calling delay()");
					delay();
				}
				return false;
			}
		});
	}

    private ArrayList<HashMap<String, String>> getMountEquipmentList() {
        String[] mountType = getResources().getStringArray(R.array.mount_type);
        MountInfo info = new MountInfo(this);
        ArrayList<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>();

        for (int j = 0; j < mountType.length; j++) {
            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
                        map.put(MOUNT_PATH, info.path[i]);
                        map.put(MOUNT_LABLE, "");
                        map.put(MOUNT_NAME, mountType[j]+":"+info.partition[i]);
                        map.put(MOUNT_PARTITION, info.partition[i]);
                        deviceList.add(map);
                    }
                }
            }
        }
//
//        Collections.sort(deviceList, DEVICE_COMPARATOR);
//        for(HashMap<String, String> map : deviceList) {
//            if (map.get(MOUNT_PATH).contains("sdcard")) {
//                deviceList.remove(map);
//                deviceList.add(map);
//            }
//        }
        return deviceList;
    }

    private Comparator<HashMap<String, String>> DEVICE_COMPARATOR = new Comparator<HashMap<String, String>>() {
        @Override
        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
            String s1 = lhs.get(MOUNT_PARTITION);
            String s2 = rhs.get(MOUNT_PARTITION);
            return -(s1.compareTo(s2));
        }
    };

    /**
     * To determine whether a file exists
     *
     * @param path
     * @param fileName
     * @return
     */
    public boolean isFileExist(String path, String fileName) {
        Log.d(TAG, "path=" + path + "fileName" + fileName);
        File file = new File(path + fileName);
        Log.d(TAG, "file=" + file);
        if (!file.exists()) {
            return false;
        }
        return true;
    }
    
    /**
     * create a dialog
     *
     * @param height
     * @param width
     * @param save
     * @param path
     */
    public void createDialog(int height, int width, int save, String path) {
        mSystemUpdateDialog = new SystemUpdateDialog(LocalUpgradeActivity.this, save, path);
        mSystemUpdateDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        Window window = mSystemUpdateDialog.getWindow();
/*        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = height;
        lp.width = width;
        window.setAttributes(lp);*/
        mSystemUpdateDialog.show();

    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onWindowFocusChanged hasFocus=" + hasFocus);
		if (hasFocus) {
			delay();
		} else {
			finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
		}
		super.onWindowFocusChanged(hasFocus);

	}

	/**
	 * handler of finish activity
	 */
	private Handler finishHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.ACTIVITY_FINISH)
				finish();
		};
	};

	/**
	 * set delay time to finish activity
	 */
	public void delay() {
		Log.i(TAG, "delay() is calling");
		finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
		Message message = new Message();
		message.what = Constant.ACTIVITY_FINISH;
		finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
	}
}
