package com.umexplorer.activity;

import com.umexplorer.R;

import android.app.Activity;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ServiceManager;
import android.os.storage.IMountService;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.umexplorer.R;
import com.umexplorer.activity.MainExplorerActivity.MyThread;
import com.umexplorer.common.CommonActivity;
import com.umexplorer.common.ControlListAdapter;
import com.umexplorer.common.ExpandAdapter;
import com.umexplorer.common.FileAdapter;
import com.umexplorer.common.FileUtil;
import com.umexplorer.common.FilterType;
import com.umexplorer.common.GroupInfo;
import com.umexplorer.common.MountInfo;
import com.umexplorer.common.NewCreateDialog;

public class SelectDeviceActivity extends Activity {

	private static final String TAG = "SelectDeviceActivity";
		
	protected GridView DevicegridView;
		
	ArrayList<HashMap<String, Object>> ListImageItem;
	
	protected SimpleAdapter adapter;
		
	protected int gridlayout = 0;
	
	protected String FileType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_device);
		
		Intent intent=getIntent(); 
		FileType = intent.getStringExtra("FileType");
		DevicegridView = (GridView) findViewById(R.id.devicegridView);
		gridlayout = R.layout.device_gridview_row;
		GetDeviceList();
		getUSB();
	}

	public void GetDeviceList() {
        getMountEquipmentList();
		DevicegridView.setVisibility(View.VISIBLE);

		adapter = new SimpleAdapter(this, ListImageItem, gridlayout, 
				new String[] {"ItemImage","ItemText"}, 
				new int[] {R.id.device_image_Icon,R.id.device_text});
		
        DevicegridView.setAdapter(adapter);
		
        DevicegridView.setOnItemClickListener(ItemClickListener);
    }
	
	private void getMountEquipmentList() {
        String[] mountType = getResources().getStringArray(R.array.mountType);
        MountInfo info = new MountInfo(this);
        GroupInfo group = null;
		ListImageItem = new ArrayList<HashMap<String, Object>>();
		
		Log.i(TAG, "=========mountType.length========" + mountType.length);
        for (int j = 0; j < mountType.length; j++) {
            group = new GroupInfo();
			
            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        //Map<String, String> map = new HashMap<String, String>();
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ItemImage", R.drawable.device_selector);
                        map.put("ItemText", mountType[j]+":"+info.partition[i]);
                        map.put("mountPath", info.path[i]);
                        ListImageItem.add(map);
                    }
                }
            }
        }
    }
	
	private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, action);
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
		
		DevicegridView.setVisibility(View.VISIBLE);

		adapter = new SimpleAdapter(this, ListImageItem, gridlayout, 
				new String[] {"ItemImage","ItemText"}, 
				new int[] {R.id.device_image_Icon,R.id.device_text});
        DevicegridView.setAdapter(adapter);

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
    
    private OnItemClickListener ItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        	//Log.i(TAG, "=========AdapterView========" + l);
        	//Log.i(TAG, "=========View========" + v);
        	//Log.i(TAG, "=========position========" + position);
        	String path = (String)ListImageItem.get(position).get("mountPath");
        	String title = (String)ListImageItem.get(position).get("ItemText");
        	//Log.i(TAG, "=========path========" + path);
        	
        	Intent intent = new Intent(SelectDeviceActivity.this, MainExplorerActivity.class);
        	intent.putExtra("FileType", FileType);
        	intent.putExtra("MountPath", path);
        	intent.putExtra("FileTitle", title);
        	startActivity(intent);
        	//HashMap<String, Object> item=(HashMap<String, Object>) l.getItemAtPosition(position); 
        	//setTitle((String)item.get("ItemText"));
        }
    };
}
